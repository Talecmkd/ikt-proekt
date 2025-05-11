package finki.ikt.iktproekt.quiz.rest;

import finki.ikt.iktproekt.document.service.DocumentService;
import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.quiz.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.List;


@Controller
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final DocumentService documentService;

    public QuizController(QuizService quizService, DocumentService documentService) {
        this.quizService = quizService;
        this.documentService = documentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createQuiz(@Valid @RequestBody Quiz quiz) {
        Quiz createdQuiz = quizService.create(quiz);
        return ResponseEntity.created(URI.create("/api/quizzes/" + createdQuiz.getId()))
                .body(createdQuiz.getId());
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
}
