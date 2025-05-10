package finki.ikt.iktproekt.controller;

import finki.ikt.iktproekt.service.DocumentService;
import finki.ikt.iktproekt.service.QuestionGenerationService;
import finki.ikt.iktproekt.service.QuizService;
import finki.ikt.iktproekt.service.UserService;
import finki.ikt.iktproekt.model.Document;
import finki.ikt.iktproekt.model.Question;
import finki.ikt.iktproekt.model.Quiz;
import finki.ikt.iktproekt.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;
    private final UserService userService;
    private final QuizService quizService;
    private final QuestionGenerationService questionGenerationService;
    private final String uploadDir = "uploads/";

    public DocumentController(DocumentService documentService, UserService userService, QuizService quizService, QuestionGenerationService questionGenerationService) {
        this.documentService = documentService;
        this.userService = userService;
        this.quizService = quizService;
        this.questionGenerationService = questionGenerationService;

        // Ensure upload directory exists
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") Long userId) {
        // Check if the file is PDF
        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body("Wrong file type. File must be PDF.");
        }

        // File size validation
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File size must be less than 10MB");
        }

        // Saving the file locally
            String filePath = uploadDir + System.currentTimeMillis() + "_" + file.getOriginalFilename();        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            file.transferTo(path);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File saving failed");
        }

        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Create document
            Document document = new Document();
            document.setFilePath(filePath);
            document.setUser(user);
            document = documentService.create(document);

            // Create quiz
            Quiz quiz = new Quiz();
            quiz.setTitle("Quiz for " + file.getOriginalFilename());
            quiz.setUser(user);
//            quiz.setDocument(document);
            quizService.create(quiz);
            
            // Generate questions from PDF
            List<Question> questions = questionGenerationService.generateQuestionsFromPdf(filePath, quiz);
//            quiz.setQuestions(questions);
            
            quizService.create(quiz);
            
            return ResponseEntity.ok("File uploaded and quiz with " + questions.size() + " questions created");
            
        } catch (Exception e) {
            // Clean up file if processing failed
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException ex) {
                // Log cleanup failure
            }
            return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
        }
    }
}
