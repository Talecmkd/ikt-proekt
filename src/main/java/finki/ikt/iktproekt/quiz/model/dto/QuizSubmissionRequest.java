package finki.ikt.iktproekt.quiz.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class QuizSubmissionRequest {

    private Map<Long, String> answers;

    private long timeTakenMillis;
}
