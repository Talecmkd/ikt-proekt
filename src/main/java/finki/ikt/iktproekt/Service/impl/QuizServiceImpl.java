package finki.ikt.iktproekt.Service.impl;

import finki.ikt.iktproekt.Service.QuizService;
import finki.ikt.iktproekt.model.Question;
import finki.ikt.iktproekt.model.Quiz;
import finki.ikt.iktproekt.model.dto.QuizSubmissionResult;
import finki.ikt.iktproekt.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Override
    public QuizSubmissionResult submitQuiz(Long quizId, Map<Long, String> userAnswers) {
        Quiz quiz = this.quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        int totalQuestions = quiz.getQuestions().size();
        int correctCount = 0;
        List<String> feedback = new ArrayList<>();

        for (Question question : quiz.getQuestions()) {
            String correctAnswer = question.getCorrectAnswer();
            String userAnswer = userAnswers.get(question.getQuestion_id());

            if (userAnswer != null && userAnswer.equalsIgnoreCase(correctAnswer)) {
                correctCount++;
                feedback.add("Q" + question.getQuestion_id() + ": Correct");
            } else {
                feedback.add("Q" + question.getQuestion_id() + ": Incorrect (Correct: " + correctAnswer + ")");
            }
        }

        double scorePercentage = ((double) correctCount / totalQuestions) * 100;
        return new QuizSubmissionResult(scorePercentage, feedback);
    }
}
