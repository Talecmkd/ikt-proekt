package finki.ikt.iktproekt.question.service;

import java.util.List;

import finki.ikt.iktproekt.question.model.Question;

public interface QuestionGenerationService {
    List<Question> generateQuestionsFromPdf(Long quizId);
}