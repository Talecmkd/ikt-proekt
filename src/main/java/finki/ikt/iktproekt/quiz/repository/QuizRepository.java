package finki.ikt.iktproekt.quiz.repository;

import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.user.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz,Long> {
    List<Quiz> findAllByUser(User user);
}
