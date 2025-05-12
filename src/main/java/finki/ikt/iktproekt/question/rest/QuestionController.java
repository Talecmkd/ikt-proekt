package finki.ikt.iktproekt.question.rest;

import finki.ikt.iktproekt.question.service.QuestionGenerationService;

import finki.ikt.iktproekt.question.model.Question;

import finki.ikt.iktproekt.question.service.QuestionService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionGenerationService questionGenerationService;

    private final QuestionService questionService;

    @PostMapping("/generate/{quizId}")
    public ResponseEntity<List<Question>> generateQuestionsFromPdf(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionGenerationService.generateQuestionsFromPdf(quizId));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<List<Question>> getQuestionsByQuiz(@PathVariable Long quizId) {
        List<Question> questions = questionService.findAllByQuizId(quizId);
        return ResponseEntity.ok(questions);
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<Void> saveQuestions(@PathVariable Long quizId,
                                              @RequestBody List<Question> questions) {
        questionService.saveUserEditedQuestions(quizId, questions);
        return ResponseEntity.ok().build();
    }
}