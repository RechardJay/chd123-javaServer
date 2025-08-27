package jay.chd123.problem.service.servieImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.problem.entity.db.ProblemCase;
import jay.chd123.problem.entity.reqParam.JudgeCaseUpdateRequest;
import jay.chd123.problem.mapper.CaseMapper;
import jay.chd123.problem.service.CaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CaseServiceImpl extends ServiceImpl<CaseMapper, ProblemCase> implements CaseService {
    /**
     * 获取所有用例
     * @param id 题目id
     * @return 所有测试用例List
     */
    public List<ProblemCase> getAllByProblemId(int id) {
        QueryWrapper<ProblemCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sId", id);
        List<ProblemCase> caseList = baseMapper.selectList(queryWrapper);
        return caseList;
    }
    public List<ProblemCase> getExamplesByPromptId(int id) {
        QueryWrapper<ProblemCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sId", id);
        queryWrapper.eq("type",ProblemCase.TYPE.EXAMPLE.name());
        List<ProblemCase> caseList = baseMapper.selectList(queryWrapper);
        return caseList;
    }
    public List<ProblemCase> getJudgesByPromptId(int id) {
        QueryWrapper<ProblemCase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sId", id).eq("type",ProblemCase.TYPE.JUDGE.name());
        List<ProblemCase> caseList = baseMapper.selectList(queryWrapper);
        return caseList;
    }
    @Transactional
    public boolean updateJudges(JudgeCaseUpdateRequest request){
        int sId = request.getId();
        String sCode = request.getCode();
        QueryWrapper<ProblemCase> deleteQueryWrapper = new QueryWrapper<>();
        deleteQueryWrapper.eq("sId",sId).eq("type",ProblemCase.TYPE.JUDGE.name());
        baseMapper.delete(deleteQueryWrapper);
        QueryWrapper<ProblemCase> updateQueryWrapper = new QueryWrapper<>();
        List<ProblemCase> newCaseList = new ArrayList<>();
        for (JudgeCaseUpdateRequest.Case aCase : request.getCaseList()) {
            ProblemCase newCase = ProblemCase.builder()
                    .sId(sId)
                    .sCode(sCode)
                    .input(aCase.getInput())
                    .output(aCase.getOutput())
                    .type(ProblemCase.TYPE.JUDGE.name())
                    .build();
            newCaseList.add(newCase);
        }
        boolean b = saveBatch(newCaseList);
        return b;
    }

}
