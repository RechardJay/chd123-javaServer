package jay.chd123.judge;

import jay.chd123.global.entity.Result;
import jay.chd123.judge.entity.CodeRunRequest;
import jay.chd123.judge.entity.JudgeRequest;
import jay.chd123.judge.entity.JudgeResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
public class Controller {
    private final JudgeService judgeService;

    public Controller(JudgeService judgeService) {
        this.judgeService = judgeService;
    }
    @PostMapping("/judge")
    public Result<JudgeResult> judge(@RequestBody JudgeRequest request) {
        JudgeResult judgeResult = judgeService.judgeCode(request);
        return Result.success(judgeResult);
    }
    @PostMapping("/run")
    public Result<String> runCode(@RequestBody CodeRunRequest request){
        String result = judgeService.runCode(request);
        return Result.success(result);
    }
}
