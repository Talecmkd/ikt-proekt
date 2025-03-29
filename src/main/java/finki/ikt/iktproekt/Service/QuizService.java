package finki.ikt.iktproekt.Service;


import finki.ikt.iktproekt.model.Quiz;

import java.util.List;
import java.util.Optional;

public interface QuizService {
    List<Quiz> findAll();
    Optional<Quiz> findById(Long id);
    Quiz create(Quiz quiz);
    Quiz update(Long id, Quiz quiz);
    void delete(Long id);
}
