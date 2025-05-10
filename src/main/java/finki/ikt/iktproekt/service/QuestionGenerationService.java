package finki.ikt.iktproekt.service;

import java.io.IOException;
import java.util.List;

import finki.ikt.iktproekt.model.Question;
import finki.ikt.iktproekt.model.Quiz;

public interface QuestionGenerationService {
    List<Question> generateQuestionsFromPdf(String filePath, Quiz quiz) throws IOException;
}