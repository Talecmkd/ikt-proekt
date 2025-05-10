package finki.ikt.iktproekt.controller;

import finki.ikt.iktproekt.service.QuestionGenerationService;
import finki.ikt.iktproekt.model.Question;
import finki.ikt.iktproekt.model.Quiz;
import finki.ikt.iktproekt.repository.QuizRepository;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionGenerationService questionGenerationService;
    private final QuizRepository quizRepository;

    public QuestionController(QuestionGenerationService questionGenerationService,
                              QuizRepository quizRepository) {
        this.questionGenerationService = questionGenerationService;
        this.quizRepository = quizRepository;
    }

    @PostMapping("/generate-from-pdf")
    public List<Question> generateQuestionsFromPdf(
            @RequestParam String filePath,
            @RequestParam Long quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        try {
            return questionGenerationService.generateQuestionsFromPdf(filePath, quiz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process PDF file", e);
        }
    }
}