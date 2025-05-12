package finki.ikt.iktproekt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import finki.ikt.iktproekt.Service.DocumentService;
import finki.ikt.iktproekt.Service.QuizService;
import finki.ikt.iktproekt.model.Document;
import finki.ikt.iktproekt.model.Question;
import finki.ikt.iktproekt.model.Quiz;
import finki.ikt.iktproekt.model.dto.QuizSubmissionResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Controller
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final DocumentService documentService;
    private final ObjectMapper objectMapper;

    public QuizController(QuizService quizService, DocumentService documentService, ObjectMapper objectMapper) {
        this.quizService = quizService;
        this.documentService = documentService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createQuiz(@Valid @RequestBody Quiz quiz) {
        Quiz createdQuiz = quizService.create(quiz);
        return ResponseEntity.created(URI.create("/api/quizzes/" + createdQuiz.getQuiz_id()))
                .body(createdQuiz.getQuiz_id());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        Optional<Quiz> quiz = quizService.findById(id);
        return quiz.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/export/json")
    public ResponseEntity<byte[]> exportQuizAsJson(@PathVariable Long id) throws IOException {
        Quiz quiz = this.quizService.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));

        Map<String, Object> quizData = new HashMap<>();
        quizData.put("title", quiz.getTitle());

        List<Question> questions = quiz.getQuestions();
        quizData.put("questions", questions);

        String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(quizData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", quiz.getTitle().replaceAll("\\\s+", "_") + ".json");
        headers.setContentLength(jsonContent.getBytes(StandardCharsets.UTF_8).length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(jsonContent.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<QuizSubmissionResult> submitQuiz(@PathVariable Long id, @RequestBody Map<Long, String> userAnswers) {
        QuizSubmissionResult result = quizService.submitQuiz(id, userAnswers);
        return ResponseEntity.ok(result);
    }
}
