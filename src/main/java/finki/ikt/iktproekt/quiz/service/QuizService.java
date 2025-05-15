package finki.ikt.iktproekt.quiz.service;

import finki.ikt.iktproekt.quiz.model.Quiz;
import finki.ikt.iktproekt.quiz.model.dto.QuizDto;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

public interface QuizService {
    List<Quiz> findAll();

    Quiz findById(Long id);

    Quiz create(String title, MultipartFile file) throws IOException;

    Quiz update(Long id, Quiz quiz);

    void delete(Long id);

    QuizDto mapQuizToDto(Quiz quiz);

    List<QuizDto> getAllQuizzesByUser();
}
