package finki.ikt.iktproekt.quiz.rest;

import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.quiz.model.dto.QuizDto;

import finki.ikt.iktproekt.quiz.service.QuizService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@Controller
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/create")
    public ResponseEntity<Quiz> createQuiz(@RequestParam String title,
                                           @RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(quizService.create(title, file));
    }

    @GetMapping("/{id}")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<QuizDto> getQuizById(@PathVariable Long id) {
        Quiz quiz = quizService.findById(id);
        return ResponseEntity.ok(quizService.mapQuizToDto(quiz));
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

    @GetMapping("/all/user")
    public ResponseEntity<List<QuizDto>> getAllQuizzesByUser() {
        return ResponseEntity.ok(quizService.getAllQuizzesByUser());
    }
}
