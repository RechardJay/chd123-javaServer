package jay.chd123.judge;

import jay.chd123.judge.entity.CodeRunRequest;
import jay.chd123.judge.entity.JudgeRequest;
import jay.chd123.judge.entity.JudgeResult;
import jay.chd123.judge.lang.CppJudger;
import jay.chd123.judge.lang.JavaJudger;
import jay.chd123.problem.entity.db.ProblemCase;
import jay.chd123.problem.service.servieImpl.CaseServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class JudgeService {
    final CaseServiceImpl problemCaseService;
    private final ApplicationContext applicationContext;

    public JudgeService(CaseServiceImpl problemCaseService, ApplicationContext applicationContext) {
        this.problemCaseService = problemCaseService;
        this.applicationContext = applicationContext;
    }

    /**
     * 完整的判题方法
     */
    public JudgeResult judgeCode(JudgeRequest request) {
        String language = request.getLanguage();
        Judger judger = getJudger(language);
        JudgeResult result = new JudgeResult();
        List<JudgeResult.CaseResult> caseResults = new ArrayList<>();
        try {
            // 1. 在容器内创建代码文件
            judger.createCodeInContainer(request.getCode());
            // 2. 编译代码
            Judger.CompileResult compileResult = judger.compileCode();
            if (!compileResult.isSuccess()) {
                // 编译错误，直接返回
                result.setSuccess(false);
                result.setErrorMsg(compileResult.getErrorOutput());
                result.setErrorType("COMPILE_ERROR");
                return result;
            }

            // 3. 执行所有测试用例
            List<ProblemCase> caseList = problemCaseService.getAllByProblemId(request.getProblemId());
            for (ProblemCase testCase : caseList) {
                JudgeResult.CaseResult caseResult = judger.executeCase(testCase);
                caseResults.add(caseResult);

                // 如果某个用例执行失败，记录错误信息并停止后续测试
                if (caseResult.getActualOutput() == null || !caseResult.isPassed()) {
                    result.setSuccess(false);
                    result.setErrorMsg("用例执行失败: " + caseResult.getCaseMessage());
                    result.setErrorType("OUTPUT_ERROR");
                    result.setResults(caseResults);
                    return result;
                }
            }

            // 4. 所有用例都通过
            result.setSuccess(true);
            result.setMessage("所有测试用例通过");
            result.setResults(caseResults);

        } catch (Exception e) {
            // 其他系统错误
            result.setSuccess(false);
            result.setErrorMsg("系统错误: " + e.getMessage());
            result.setErrorType("SYSTEM_ERROR");
        }

        return result;
    }

    /**
     * 运行代码，接收用户指定输入，返回结果
     */
    public String runCode(CodeRunRequest request) {
        String language = request.getLanguage();
        Judger judger = getJudger(language);
        judger.createCodeInContainer(request.getCode());
        Judger.CompileResult compileResult = judger.compileCode();
        if (!compileResult.isSuccess()) {
            return compileResult.getErrorOutput();
        }
        String output;
        try {
            output = judger.runCodeTheInput(request.getInput());
        } catch (TimeoutException e) {
            return "执行超时";
        }
        return output;

    }
    /**
     * @param language 语言
     * @return 对应的判题流程
     */
    private Judger getJudger(String language) {
        switch (language) {
            case "cpp":
                return applicationContext.getBean(CppJudger.class);
            case "java":
                return applicationContext.getBean(JavaJudger.class);
            default:
                throw new IllegalArgumentException("暂不支持的语言: " + language);
        }
    }
}