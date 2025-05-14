package finki.ikt.iktproekt.results.service;

import finki.ikt.iktproekt.results.model.UserQuizResults;

import java.util.List;

public interface IUserQuizResultsService {
    List<UserQuizResults> getResFromAllUsers();
    List<UserQuizResults> getResultsForUserAndQuiz(Long userId, Long quizId);
}
