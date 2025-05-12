package finki.ikt.iktproekt.quiz.model.dto;

import finki.ikt.iktproekt.document.model.dto.DocumentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuizDto {

    private Long id;

    private String title;

    private DocumentDto document;
}
