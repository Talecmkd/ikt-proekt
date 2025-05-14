package finki.ikt.iktproekt.results;

import finki.ikt.iktproekt.results.model.UserQuizResults;
import finki.ikt.iktproekt.results.service.impl.UserQuizResultsService;
import finki.ikt.iktproekt.user.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller

public class ResultsController {
    private  final UserQuizResultsService userQuizResultsService;

    public ResultsController(UserQuizResultsService userQuizResultsService) {
        this.userQuizResultsService = userQuizResultsService;
    }
    @GetMapping("/all/results")
    public ResponseEntity<List<UserQuizResults>> getAllQuizzes() {
        return ResponseEntity.ok(userQuizResultsService.getResFromAllUsers());
    }
    @GetMapping("/{quizId}/results")
    public ResponseEntity<List<UserQuizResults>> getResultsForUserAndQuiz(@PathVariable Long quizId,
                                                                          @AuthenticationPrincipal User user) {
        List<UserQuizResults> results = userQuizResultsService.getResultsForUserAndQuiz(user.getId(), quizId);
        return ResponseEntity.ok(results);
    }

}
