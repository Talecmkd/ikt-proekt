package finki.ikt.iktproekt.question.rest;

import finki.ikt.iktproekt.question.service.QuestionGenerationService;

import finki.ikt.iktproekt.question.model.Question;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionGenerationService questionGenerationService;

    @PostMapping("/generate/{quizId}")
    public ResponseEntity<List<Question>> generateQuestionsFromPdf(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionGenerationService.generateQuestionsFromPdf(quizId));
    }
}