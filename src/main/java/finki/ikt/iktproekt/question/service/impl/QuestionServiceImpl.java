package finki.ikt.iktproekt.question.service.impl;

import finki.ikt.iktproekt.question.model.Question;
import finki.ikt.iktproekt.question.repository.QuestionRepository;
import finki.ikt.iktproekt.question.service.QuestionService;
import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.quiz.repository.QuizRepository;
import finki.ikt.iktproekt.quiz.service.QuizService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.Objects;
import java.util.ArrayList;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private  final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
//    private final QuizService quizService;

    @Override
    public List<Question> findAllByQuizId(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).get();

        return questionRepository.findAllByQuiz(quiz);
    }

    @Override
    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    @Override
    public Question create(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question update(Long id, Question question) {
        if (questionRepository.existsById(id)) {
            return questionRepository.save(question);
        }
        throw new RuntimeException("Question not found");
    }
    @Override
    public void delete(Long id) {
            questionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void saveUserEditedQuestions(Long quizId, List<Question> questions) {
        Quiz quiz = quizRepository.findById(quizId).get();

        List<Question> existingQuestions = questionRepository.findAllByQuiz(quiz);

        Map<Long, Question> existingById = existingQuestions.stream()
                .filter(q -> q.getId() != null)
                .collect(Collectors.toMap(Question::getId, q -> q));

        Set<Long> incomingIds = questions.stream()
                .map(Question::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Question> toDelete = existingQuestions.stream()
                .filter(q -> q.getId() != null && !incomingIds.contains(q.getId()))
                .toList();

        questionRepository.deleteAll(toDelete);

        List<Question> toSave = new ArrayList<>();

        for (Question incoming : questions) {
            incoming.setQuiz(quiz);

            if (incoming.getId() != null && existingById.containsKey(incoming.getId())) {
                Question existing = existingById.get(incoming.getId());

                existing.setQuestionText(incoming.getQuestionText());
                existing.setQuestionType(incoming.getQuestionType());
                existing.setAnswers(incoming.getAnswers());
                existing.setCorrectAnswer(incoming.getCorrectAnswer());

                toSave.add(existing);
            } else {
                toSave.add(incoming);
            }
        }

        questionRepository.saveAll(toSave);
    }
}
