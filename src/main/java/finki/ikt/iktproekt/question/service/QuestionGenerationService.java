package finki.ikt.iktproekt.question.service;

import java.io.IOException;
import java.util.List;

import finki.ikt.iktproekt.question.model.Question;
import finki.ikt.iktproekt.quiz.model.Quiz;

public interface QuestionGenerationService {
    List<Question> generateQuestionsFromPdf(String filePath, Quiz quiz) throws IOException;
}