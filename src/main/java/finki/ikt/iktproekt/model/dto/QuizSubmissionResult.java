package finki.ikt.iktproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class QuizSubmissionResult {

    private double scorePercentage;

    private List<String> feedback;

}
