package finki.ikt.iktproekt.controller;

import finki.ikt.iktproekt.Service.QuizService;
import finki.ikt.iktproekt.model.Quiz;
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

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
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
}
