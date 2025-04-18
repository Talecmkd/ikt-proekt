package finki.ikt.iktproekt.Service.impl;

import finki.ikt.iktproekt.Service.QuizService;
import finki.ikt.iktproekt.model.Quiz;
import finki.ikt.iktproekt.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;

    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }

    @Override
    public Optional<Quiz> findById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    public Quiz create(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz update(Long id, Quiz quiz) {
        if (quizRepository.existsById(id)) {
            return quizRepository.save(quiz);
        }
        throw new RuntimeException("Quiz not found");
    }

    @Override
    public void delete(Long id) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (quiz.getDocument() != null && quiz.getDocument().getFilePath() != null) {
            File file = new File(quiz.getDocument().getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }

        quizRepository.deleteById(id);
    }
}
