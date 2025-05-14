package finki.ikt.iktproekt.results.service.impl;

import finki.ikt.iktproekt.results.model.UserQuizResults;
import finki.ikt.iktproekt.results.repository.UserQuizResultsRepository;
import finki.ikt.iktproekt.results.service.IUserQuizResultsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQuizResultsService implements IUserQuizResultsService {
    private  final UserQuizResultsRepository quizResultsRepository;

    public UserQuizResultsService(UserQuizResultsRepository quizResultsRepository) {
        this.quizResultsRepository = quizResultsRepository;
    }

    @Override
    public List<UserQuizResults> getResFromAllUsers() {
       return quizResultsRepository.findAll();
    }
    @Override
    public List<UserQuizResults> getResultsForUserAndQuiz(Long userId, Long quizId) {
        return quizResultsRepository.findByUser_IdAndQuiz_Id(userId, quizId);
    }
}
