package jay.chd123.judge;

import jay.chd123.judge.entity.JudgeRequest;
import jay.chd123.judge.entity.JudgeResult;
import jay.chd123.problem.entity.db.ProblemCase;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class JudgeService {

    private static final String CONTAINER_NAME = "judge-universal";
    private static final long TIME_LIMIT_MS = 5000; // 5秒超时

    /**
     * 完整的判题方法
     */
    public JudgeResult judgeCode(JudgeRequest request) {
        JudgeResult result = new JudgeResult();
        List<JudgeResult.CaseResult> caseResults = new ArrayList<>();

        try {
            // 1. 在容器内创建代码文件
            createCodeInContainer(request.getCode());

            // 2. 编译代码
            CompileResult compileResult = compileCode();
            if (!compileResult.success) {
                // 编译错误，直接返回
                result.setSuccess(false);
                result.setErrorMsg(compileResult.getErrorOutput());
                result.setErrorType("COMPILE_ERROR");
                return result;
            }

            // 3. 执行所有测试用例
            List<ProblemCase> caseList = new ArrayList<>();
            //测试
            caseList.add(ProblemCase.builder()
                    .input("2 3")
                    .output("5")
                    .build());
            for (ProblemCase testCase : caseList) {
                JudgeResult.CaseResult caseResult = executeTestCase(testCase);
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

        } catch (TimeoutException e) {
            // 超时异常
            result.setSuccess(false);
            result.setErrorMsg("执行超时");
            result.setErrorType("TIMEOUT");

        } catch (Exception e) {
            // 其他系统错误
            result.setSuccess(false);
            result.setErrorMsg("系统错误: " + e.getMessage());
            result.setErrorType("SYSTEM_ERROR");
        }

        return result;
    }

    /**
     * 编译代码
     */
    private CompileResult compileCode() throws Exception {
        String[] cmd = {
                "docker", "exec", CONTAINER_NAME,
                "sh", "-c", "g++ -o /tmp/solution /tmp/solution.cpp"
        };

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String output = readProcessOutput(process);
        int exitCode = process.waitFor();   // 现在就会是 0

        CompileResult result = new CompileResult();
        result.setSuccess(exitCode == 0);
        result.setErrorOutput(exitCode == 0 ? null : output);
        return result;
    }

    /**
     * 执行单个测试用例
     */
    private JudgeResult.CaseResult executeTestCase(ProblemCase testCase) {

        JudgeResult.CaseResult caseResult = new JudgeResult.CaseResult();
        caseResult.setInput(testCase.getInput());
        caseResult.setExpectedOutput(testCase.getOutput());

        long startTime = System.currentTimeMillis();

        try {
            // 构建执行命令：docker exec -i 容器 sh -c "执行程序"
            String[] cmd = {
                    "docker", "exec", "-i", CONTAINER_NAME,
                    "sh", "-c", "/tmp/solution"
            };

            // 执行命令（带超时）
            Process process = Runtime.getRuntime().exec(cmd);
            // 将输入数据写入进程的标准输入
            try (OutputStream os = process.getOutputStream()) {
                os.write(testCase.getInput().getBytes(StandardCharsets.UTF_8));
                os.flush();  // 重要：刷新缓冲区
            }
            boolean finished = process.waitFor(TIME_LIMIT_MS, TimeUnit.MILLISECONDS);

            if (!finished) {
                process.destroy();
                caseResult.setCaseMessage("执行超时");
                caseResult.setPassed(false);
                return caseResult;
            }

            // 读取输出
            String actualOutput = readProcessOutput(process).trim();
            long endTime = System.currentTimeMillis();

            caseResult.setActualOutput(actualOutput);
            caseResult.setExecutionTime(endTime - startTime);
            caseResult.setPassed(actualOutput.equals(testCase.getOutput()));

            if (caseResult.isPassed()) {
                caseResult.setCaseMessage("通过，用时 " + caseResult.getExecutionTime() + "ms");
            } else {
                caseResult.setCaseMessage("输出不匹配");
            }

        } catch (Exception e) {
            caseResult.setCaseMessage("执行错误: " + e.getMessage());
            caseResult.setPassed(false);
        }

        return caseResult;
    }

    /**
     * 在容器内创建代码文件
     */
    private void createCodeInContainer(String code) throws Exception {

        String[] cmd = {
                "docker", "exec", "-i", CONTAINER_NAME,
                "sh", "-c", "cat > /tmp/solution.cpp"
        };
        Process process = new ProcessBuilder(cmd).start();
        try (OutputStream os = process.getOutputStream()) {
            os.write(code.getBytes());
            os.flush();  // 重要：刷新缓冲区
        }
//        checkProcessResult(process);
    }

    /**
     * 辅助方法：读取进程输出
     */
    private String readProcessOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        return output.toString();
    }

    /**
     * 编译结果封装
     */
    @Data
    private static class CompileResult {
        private boolean success;
        private String errorOutput;
    }
}