package finki.ikt.iktproekt.controller;

import finki.ikt.iktproekt.Service.DocumentService;
import finki.ikt.iktproekt.Service.QuizService;
import finki.ikt.iktproekt.Service.UserService;
import finki.ikt.iktproekt.model.Document;
import finki.ikt.iktproekt.model.Quiz;
import finki.ikt.iktproekt.model.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;
    private final UserService userService;
    private final QuizService quizService;

    public DocumentController(DocumentService documentService, UserService userService, QuizService quizService) {
        this.documentService = documentService;
        this.userService = userService;
        this.quizService = quizService;
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
        String filePath = "uploads/" + file.getOriginalFilename();
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            file.transferTo(path);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File saving failed");
        }

        // Extracting text from PDF using PDFBox
        String extractedText = "";
        try (PDDocument document = PDDocument.load(new File(filePath))){
            PDFTextStripper stripper = new PDFTextStripper();
            extractedText = stripper.getText(document);
//            System.out.println(extractedText);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading PDF content");
        }

        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Document document = new Document(null, user, filePath, false, null);
        documentService.create(document);

        /* TODO:
            Don't like setting the properties one by one
            Maybe we should create a constructor that takes only the properties below
            This is the only way for now, should come back to this
        */
        Quiz quiz = new Quiz();
        quiz.setTitle("Quiz for " + file.getOriginalFilename());
        quiz.setUser(user);
        quiz.setDocument(document);
        quiz.setPdfText(extractedText);
        quizService.create(quiz);

        return ResponseEntity.ok("File uploaded and quiz created successfully");
    }
}
