package finki.ikt.iktproekt.document.repository;

import finki.ikt.iktproekt.document.model.Document;
import finki.ikt.iktproekt.quiz.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document findDocumentByQuiz(Quiz quiz);
}
