package jay.chd123.problem.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jay.chd123.global.customException.BusinessException;
import jay.chd123.global.entity.BasePageReturn;
import jay.chd123.global.entity.BaseViewParam;
import jay.chd123.global.entity.Result;
import jay.chd123.problem.entity.db.Problem;
import jay.chd123.problem.entity.db.ProblemCase;
import jay.chd123.problem.entity.db.ProblemTag;
import jay.chd123.problem.entity.reqParam.ReqProblemList;
import jay.chd123.problem.service.ProblemService;
import jay.chd123.problem.service.servieImpl.CaseServiceImpl;
import jay.chd123.problem.service.servieImpl.ProblemTagServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game/problem")
public class ProblemController {
    private final ProblemService problemService;
    private final CaseServiceImpl caseService;
    private final ProblemTagServiceImpl tagService;

    public ProblemController(ProblemService problemService, CaseServiceImpl caseService, ProblemTagServiceImpl tagService) {
        this.problemService = problemService;
        this.caseService = caseService;
        this.tagService = tagService;
    }

    @GetMapping()
    public Result<String> getProblems(@Param("problemId") String problemId){
        Problem problem = problemService.getOne(new QueryWrapper<Problem>().eq("id", problemId));
        if(problem == null){
            return Result.error("题目不存在");
        }
        return Result.success(problem.getDescription());
    }

    @PostMapping("/list")
    public Result<BasePageReturn<Problem.ProblemItem>> getProblemList(@RequestBody ReqProblemList req) {
        int page = req.getCurrentPage();
        int pageSize = req.getPageSize();
        List<String> tags = req.getTags();
        BasePageReturn<Problem.ProblemItem> pages = problemService.getProblemPage(page, pageSize, tags);
        return Result.success(pages);
    }

    @PostMapping("/detail")
    public Result<Problem> getProblem(@RequestBody BaseViewParam req) {
        int id = req.getId();
        String code = req.getCode();
        Problem problem = problemService.getProblemDetail(id,code);
        return Result.success(problem);
    }

    @PostMapping("/create")
    public Result<Map<String,Object>> addProblem(@RequestBody Problem problem) {
        if(problem.getInputs().size() != problem.getOutputs().size()){
            throw new BusinessException(-1,"用例数量不一致");
        }
        Integer id = problemService.createProblem(problem);
        HashMap<String,Object> result = new HashMap<>();
        result.put("id",id);
        return Result.success(result);
    }

    @PostMapping("/delete")
    public Result<String> deleteProblem(@RequestBody BaseViewParam req) {
        Integer id = req.getId();
        if(id == null){
            throw new BusinessException(-1,"缺少参数");
        }
        problemService.removeById(id);
        QueryWrapper<ProblemTag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq("sId", id);
        tagService.remove(tagQueryWrapper);
        QueryWrapper<ProblemCase> caseQueryWrapper = new QueryWrapper<>();
        caseQueryWrapper.eq("sId", id);
        caseService.remove(caseQueryWrapper);
        return Result.success("delete success");
    }

    @PostMapping("/update")
    public Result<Integer> updateProblem(@RequestBody Problem problem) {
        if(problem.getId() == null){
            throw new BusinessException(-1,"缺少参数");
        }
        Integer sid = problemService.updateProblem(problem);
        return Result.success(sid);
    }
}
