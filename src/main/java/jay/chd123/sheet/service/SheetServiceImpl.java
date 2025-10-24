package jay.chd123.sheet.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.problem.entity.db.Problem;
import jay.chd123.problem.service.servieImpl.ProblemServiceImpl;
import jay.chd123.sheet.entity.db.Sheet;
import jay.chd123.sheet.entity.db.SheetProblem;
import jay.chd123.sheet.mapper.SheetMapper;
import jay.chd123.sheet.mapper.SheetProblemMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SheetServiceImpl extends ServiceImpl<SheetMapper, Sheet> {
    private final SheetProblemMapper sheetProblemMapper;
    private final ProblemServiceImpl problemService;


    public SheetServiceImpl(SheetProblemMapper sheetProblemMapper, ProblemServiceImpl problemServiceImpl) {
        this.sheetProblemMapper = sheetProblemMapper;
        this.problemService = problemServiceImpl;
    }
    public List<Problem> getProblemsOfSheet(Long sheetId){
        List<SheetProblem> sheetProblemList = sheetProblemMapper.selectList(new QueryWrapper<SheetProblem>().eq("sheetId", sheetId));
        if(sheetProblemList.isEmpty()){
            return new ArrayList<>();
        }
        List<Long> problemIds = sheetProblemList.stream().map(SheetProblem::getProblemId).collect(Collectors.toList());
        List<Problem> problems = problemService.listByIds(problemIds);
        return problems;
    }
}
