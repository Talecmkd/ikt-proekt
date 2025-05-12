package finki.ikt.iktproekt.quiz.service;


import finki.ikt.iktproekt.quiz.model.Quiz;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface QuizService {
    List<Quiz> findAll();

    Optional<Quiz> findById(Long id);

    Quiz create(String title, MultipartFile file);

    Quiz update(Long id, Quiz quiz);

    void delete(Long id);
}
