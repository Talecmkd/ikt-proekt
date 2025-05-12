package finki.ikt.iktproekt.quiz.service.impl;

import finki.ikt.iktproekt.document.model.Document;
import finki.ikt.iktproekt.document.model.dto.DocumentDto;
import finki.ikt.iktproekt.model.dto.QuizSubmissionResult;
import finki.ikt.iktproekt.question.model.Question;
import finki.ikt.iktproekt.quiz.model.dto.QuizDto;
import finki.ikt.iktproekt.user.model.User;
import finki.ikt.iktproekt.quiz.model.Quiz;

import finki.ikt.iktproekt.exception.NotFoundEntityException;

import finki.ikt.iktproekt.document.service.DocumentService;
import finki.ikt.iktproekt.quiz.service.QuizService;
import finki.ikt.iktproekt.user.service.UserService;
import finki.ikt.iktproekt.question.service.QuestionService;

import finki.ikt.iktproekt.quiz.repository.QuizRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    private final DocumentService documentService;

    private final UserService userService;

    private final QuestionService questionService;

    @Override
    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }

    @Override
    public Quiz findById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(Quiz.class));
    }

    @Override
    public Quiz create(String title, MultipartFile file) {

        User user = userService.getCurrentLoggedInUser();

        Document document = documentService.validateAndSaveFile(file, user);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setUser(user);

        document.setQuiz(quiz);

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

//        if (quiz.getDocument() != null && quiz.getDocument().getFilePath() != null) {
//            File file = new File(quiz.getDocument().getFilePath());
//            if (file.exists()) {
//                file.delete();
//            }
//        }

        quizRepository.deleteById(id);
    }

    @Override
    public QuizDto mapQuizToDto(Quiz quiz) {
        Document document = documentService.findDocumentByQuiz(quiz);
        DocumentDto documentDto = documentService.mapDocumentToDto(document);

        QuizDto quizDto = new QuizDto();
        quizDto.setId(quiz.getId());
        quizDto.setDocument(documentDto);
        quizDto.setTitle(quiz.getTitle());

        return quizDto;
    }

    @Override
    public QuizSubmissionResult submitQuiz(Long quizId, Map<Long, String> userAnswers) {
        List<Question> questions = questionService.findAllByQuizId(quizId);

        int totalQuestions = questions.size();
        int correctCount = 0;
        List<String> feedback = new ArrayList<>();

        for (Question question : questions) {
            String correctAnswer = question.getCorrectAnswer();
            String userAnswer = userAnswers.get(question.getId());

            if (userAnswer != null && userAnswer.equalsIgnoreCase(correctAnswer)) {
                correctCount++;
                feedback.add("Q" + question.getId() + ": Correct");
            } else {
                feedback.add("Q" + question.getId() + ": Incorrect (Correct: " + correctAnswer + ")");
            }
        }

        double scorePercentage = ((double) correctCount / totalQuestions) * 100;
        return new QuizSubmissionResult(scorePercentage, feedback);
    }
}
