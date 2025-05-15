package finki.ikt.iktproekt.results.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.itextpdf.text.DocumentException;

import finki.ikt.iktproekt.question.model.Question;
import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.results.model.UserQuizResults;
import finki.ikt.iktproekt.user.model.User;
import finki.ikt.iktproekt.quiz.model.dto.QuizSubmissionRequest;

import finki.ikt.iktproekt.results.service.IUserQuizResultsService;
import finki.ikt.iktproekt.quiz.service.QuizService;
import finki.ikt.iktproekt.user.service.UserService;
import finki.ikt.iktproekt.question.service.QuestionService;
import finki.ikt.iktproekt.pdfexport.service.PdfExportService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class UserQuizResultsController {

    private final IUserQuizResultsService userQuizResultsService;

    private final ObjectMapper objectMapper;

    private final QuestionService questionService;

    private final QuizService quizService;

    private final UserService userService;

    private final PdfExportService pdfExportService;

    @PostMapping("/{id}/submit")
    public ResponseEntity<UserQuizResults> submitQuiz(@PathVariable Long id,
                                                      @RequestBody QuizSubmissionRequest submission) {
        User user = userService.getCurrentLoggedInUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        UserQuizResults result = userQuizResultsService.submitQuiz(id, submission.getAnswers(), user, submission.getTimeTakenMillis());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{quizId}/export/json")
    public ResponseEntity<byte[]> exportQuizAsJson(@PathVariable Long quizId) throws IOException {
        Quiz quiz = quizService.findById(quizId);

        Map<String, Object> quizData = new HashMap<>();
        quizData.put("title", quiz.getTitle());

        List<Question> questions = questionService.findAllByQuizId(quizId);
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

    @GetMapping("/{quizId}/export/pdf")
    public ResponseEntity<byte[]> exportQuizToPdf(@PathVariable Long quizId) throws DocumentException {
        byte[] pdfBytes = pdfExportService.exportQuizToPdf(quizId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "quiz_" + quizId + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserQuizResults>> getAllQuizzes() {
        return ResponseEntity.ok(userQuizResultsService.getResFromAllUsers());
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<UserQuizResults>> getResultsForUserAndQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(userQuizResultsService.getResultsForUserAndQuiz(quizId));
    }

    @GetMapping("{resultId}")
    public ResponseEntity<UserQuizResults> getResultById(@PathVariable Long resultId) {
        return ResponseEntity.ok(userQuizResultsService.findResultById(resultId));
    }
}
