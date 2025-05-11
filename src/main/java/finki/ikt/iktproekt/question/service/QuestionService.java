package finki.ikt.iktproekt.question.service;


import finki.ikt.iktproekt.question.model.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionService {
    List<Question> findAll();
    Optional<Question> findById(Long id);
    Question create(Question question);
    Question update(Long id, Question question);
    void delete(Long id);
}
