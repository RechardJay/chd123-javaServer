package jay.chd123.judge;

import jay.chd123.judge.entity.JudgeRequest;
import jay.chd123.judge.entity.JudgeResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/game")
public class Controller {
    private final JudgeService judgeService;

    public Controller(JudgeService judgeService) {
        this.judgeService = judgeService;
    }
    @PostMapping("/judge")
    public JudgeResult judge(@RequestBody JudgeRequest judge) {
        System.out.println(judge);
        judgeService.notify();
        return null;
    }
}
