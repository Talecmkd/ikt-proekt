package finki.ikt.iktproekt.quiz.model.dto;

import finki.ikt.iktproekt.document.model.dto.DocumentDto;

import finki.ikt.iktproekt.question.model.Question;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuizDto {

    private Long id;

    private String title;

    private DocumentDto document;

    private List<Question> questions;
}
