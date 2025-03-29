package finki.ikt.iktproekt.Service;


import finki.ikt.iktproekt.model.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionService {
    List<Question> findAll();
    Optional<Question> findById(Long id);
    Question create(Question question);
    Question update(Long id, Question question);
    void delete(Long id);
}
