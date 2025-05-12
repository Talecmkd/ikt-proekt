package finki.ikt.iktproekt.Service;


import finki.ikt.iktproekt.model.Quiz;
import finki.ikt.iktproekt.model.dto.QuizSubmissionResult;
import java.util.*;
import java.util.Optional;

public interface QuizService {
    List<Quiz> findAll();
    Optional<Quiz> findById(Long id);
    Quiz create(Quiz quiz);
    Quiz update(Long id, Quiz quiz);
    void delete(Long id);
    QuizSubmissionResult submitQuiz(Long quizId, Map<Long, String> userAnswers);
}
