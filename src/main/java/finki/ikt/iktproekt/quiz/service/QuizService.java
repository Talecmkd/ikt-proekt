package finki.ikt.iktproekt.quiz.service;

import finki.ikt.iktproekt.quiz.model.dto.QuizSubmissionResult;
import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.quiz.model.dto.QuizDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface QuizService {
    List<Quiz> findAll();

    Quiz findById(Long id);

    Quiz create(String title, MultipartFile file);

    Quiz update(Long id, Quiz quiz);

    void delete(Long id);

    QuizDto mapQuizToDto(Quiz quiz);

    QuizSubmissionResult submitQuiz(Long quizId, Map<Long, String> userAnswers);
}
