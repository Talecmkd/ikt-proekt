package finki.ikt.iktproekt.Service.impl;

import finki.ikt.iktproekt.Service.QuestionService;
import finki.ikt.iktproekt.model.Question;
import finki.ikt.iktproekt.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl  implements QuestionService {

    private  final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    @Override
    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    @Override
    public Question create(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question update(Long id, Question question) {
        if (questionRepository.existsById(id)) {
            return questionRepository.save(question);
        }
        throw new RuntimeException("Question not found");
    }
    @Override
    public void delete(Long id) {
            questionRepository.deleteById(id);
    }
}
