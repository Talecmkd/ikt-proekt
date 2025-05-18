package finki.ikt.iktproekt.results.service.impl;

import finki.ikt.iktproekt.exception.NotFoundEntityException;
import finki.ikt.iktproekt.question.model.Question;
import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.results.model.UserQuizResults;
import finki.ikt.iktproekt.user.model.User;

import finki.ikt.iktproekt.question.repository.QuestionRepository;
import finki.ikt.iktproekt.results.repository.UserQuizResultsRepository;

import finki.ikt.iktproekt.results.service.IUserQuizResultsService;
import finki.ikt.iktproekt.user.service.UserService;
import finki.ikt.iktproekt.quiz.service.QuizService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserQuizResultsService implements IUserQuizResultsService {

    private final UserQuizResultsRepository userQuizResultsRepository;

    private final QuestionRepository questionRepository;

    private final UserService userService;

    private final QuizService quizService;

    @Override
    public UserQuizResults submitQuiz(Long quizId, Map<Long, String> userAnswers, User user, long timeTakenMillis) {
        Quiz quiz = quizService.findById(quizId);

        List<Question> questions = questionRepository.findAllByQuiz(quiz);
        int totalQuestions = questions.size();
        int correctCount = 0;
        List<String> feedback = new ArrayList<>();

        for (Question question : questions) {
            String correctAnswer = question.getCorrectAnswer().trim();
            String userAnswer = userAnswers.getOrDefault(question.getId(), "").trim();

            if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                correctCount++;
                feedback.add("Q" + question.getId() + ": Correct");
            } else {
                feedback.add("Q" + question.getId() + ": Incorrect (Correct: " + correctAnswer + ")");
            }
        }

        double scorePercentage = ((double) correctCount / totalQuestions) * 100;

        UserQuizResults results = new UserQuizResults(user, quiz, scorePercentage, LocalDateTime.now(), feedback, timeTakenMillis);
        return userQuizResultsRepository.save(results);
    }

    @Override
    public UserQuizResults findResultById(Long resultId) {
        return userQuizResultsRepository.findById(resultId)
                .orElseThrow(() -> new NotFoundEntityException(UserQuizResults.class));
    }

    @Override
    public List<UserQuizResults> getResFromAllUsers() {
       return userQuizResultsRepository.findAll();
    }

    @Override
    public List<UserQuizResults> getResultsForUserAndQuiz() {
        User user = userService.getCurrentLoggedInUser();

        return userQuizResultsRepository.findAllByUser(user);
    }
}
