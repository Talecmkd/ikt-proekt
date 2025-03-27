package finki.ikt.iktproekt;

import finki.ikt.iktproekt.model.Document;
import finki.ikt.iktproekt.model.Question;
import finki.ikt.iktproekt.model.Quiz;
import finki.ikt.iktproekt.model.User;
import finki.ikt.iktproekt.repository.DocumentRepository;
import finki.ikt.iktproekt.repository.QuestionRepository;
import finki.ikt.iktproekt.repository.QuizRepository;
import finki.ikt.iktproekt.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.time.LocalDateTime;

import static finki.ikt.iktproekt.model.enumeration.QuestionType.TRUE_FALSE;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public DatabaseSeeder(UserRepository userRepository, DocumentRepository documentRepository,
                          QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("securepassword"); // TODO: make this hashed
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        User savedUser = userRepository.findById(user.getUser_id()).orElseThrow();
        System.out.println("Saved User: " + savedUser.getName());

        Document document = new Document();
        document.setUser(savedUser);
        document.setFilePath("uploads/sample.pdf");
        document.setProcessed(false);
        document.setUploadedAt(LocalDateTime.now());
        documentRepository.save(document);

        Quiz quiz = new Quiz();
        quiz.setTitle("Sample Quiz");
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUser(savedUser);
        quiz.setDocument(document);
        quizRepository.save(quiz);

        Question question = new Question();
        question.setQuestionText("What is Java?");
        question.setAnswers(List.of("Programming Language", "Car Brand", "Country"));
        question.setCorrectAnswer("Programming Language");
        question.setQuestionType(TRUE_FALSE);
        question.setCreatedAt(LocalDateTime.now());
        question.setQuiz(quiz);
        questionRepository.save(question);

        Quiz savedQuiz = quizRepository.findById(quiz.getQuiz_id()).orElseThrow();
        System.out.println("Saved Quiz: " + savedQuiz.getTitle());

        Question savedQuestion = questionRepository.findById(question.getQuestion_id()).orElseThrow();
        System.out.println("Saved Question: " + savedQuestion.getQuestionText());
    }
}
