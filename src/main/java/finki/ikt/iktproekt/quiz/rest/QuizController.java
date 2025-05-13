package finki.ikt.iktproekt.quiz.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import finki.ikt.iktproekt.quiz.model.dto.QuizSubmissionResult;
import finki.ikt.iktproekt.question.model.Question;
import finki.ikt.iktproekt.question.service.QuestionService;
import finki.ikt.iktproekt.quiz.model.Quiz;

import finki.ikt.iktproekt.quiz.model.dto.QuizDto;
import finki.ikt.iktproekt.quiz.service.QuizService;

import finki.ikt.iktproekt.results.model.UserQuizResults;
import finki.ikt.iktproekt.user.model.User;
import finki.ikt.iktproekt.user.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    private final ObjectMapper objectMapper;

    private final QuestionService questionService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Quiz> createQuiz(@RequestParam String title,
                                           @RequestParam MultipartFile file) {
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

    @GetMapping("/{id}/export/json")
    public ResponseEntity<byte[]> exportQuizAsJson(@PathVariable Long id) throws IOException {
        Quiz quiz = quizService.findById(id);

        Map<String, Object> quizData = new HashMap<>();
        quizData.put("title", quiz.getTitle());

        List<Question> questions = questionService.findAllByQuizId(id);
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
    public ResponseEntity<UserQuizResults> submitQuiz(@PathVariable Long id, @RequestBody Map<Long, String> userAnswers) {
        User user = userService.getCurrentLoggedInUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        long startTime = System.currentTimeMillis();
        UserQuizResults result = quizService.submitQuiz(id, userAnswers, user, System.currentTimeMillis() - startTime);
        return ResponseEntity.ok(result);
    }
}
