package jay.chd123.problem.controller;

import jay.chd123.global.entity.Result;
import jay.chd123.problem.entity.db.ProblemCase;
import jay.chd123.problem.service.servieImpl.CaseServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game/case")
public class CaseController {
    private final CaseServiceImpl caseService;

    public CaseController(CaseServiceImpl caseService) {
        this.caseService = caseService;
    }
    @PostMapping("/listByProblemId")
    public Result<List<ProblemCase>> getCaseByProblemId(@RequestBody Map<String,Integer> req){
        Integer problemId = req.get("problemId");
        if(problemId == null){
            return Result.error("参数错误");
        }
        List<ProblemCase> cases = caseService.getJudgesByPromptId(problemId);
        return Result.success(cases);
    }
    @PostMapping("/create")
    public Result<Object> createCase(@RequestBody ProblemCase problemCase){
        if(problemCase.getSId() == null){
            return Result.fail("SID不能为空");
        }
        problemCase.setType(ProblemCase.TYPE.JUDGE.name());
        boolean success = caseService.save(problemCase);
        if(success){
            return Result.success(problemCase.getId());
        }else {
            return Result.error("创建失败");
        }
    }
    @PostMapping("/update")
    public Result<String> updateCase(@RequestBody ProblemCase problemCase){
        Integer id = problemCase.getId();
        if(id == null){
            return Result.error("参数错误");
        }
        ProblemCase one = caseService.getById(problemCase.getId());
        one.setInput(problemCase.getInput());
        one.setOutput(problemCase.getOutput());
        caseService.updateById(one);
        return Result.success("更新成功");
    }
    @PostMapping("/delete")
    public  Result<String> deleteCase(@RequestBody List<Integer> ids){
        if(ids == null){
            return Result.error("参数错误");
        }
        caseService.removeBatchByIds(ids);
        return Result.success("删除成功");
    }
}
