package finki.ikt.iktproekt.results.repository;

import finki.ikt.iktproekt.results.model.UserQuizResults;
import finki.ikt.iktproekt.user.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuizResultsRepository extends JpaRepository<UserQuizResults, Long> {
  List<UserQuizResults> findAllByUser(User user);
}
