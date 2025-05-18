package finki.ikt.iktproekt.results.service;

import finki.ikt.iktproekt.results.model.UserQuizResults;
import finki.ikt.iktproekt.user.model.User;

import java.util.List;
import java.util.Map;

public interface IUserQuizResultsService {

    List<UserQuizResults> getResFromAllUsers();

    List<UserQuizResults> getResultsForUserAndQuiz();

    UserQuizResults submitQuiz(Long quizId, Map<Long, String> userAnswers, User user, long timeTakenMillis);

    UserQuizResults findResultById(Long resultId);
}
