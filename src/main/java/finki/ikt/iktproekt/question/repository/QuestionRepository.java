package finki.ikt.iktproekt.question.repository;

import finki.ikt.iktproekt.question.model.Question;
import finki.ikt.iktproekt.quiz.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuestionText(String text);

    void deleteAllByQuiz(Quiz quiz);

    List<Question> findAllByQuiz(Quiz quiz);
}
