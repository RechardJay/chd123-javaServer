package jay.chd123.judge.lang;

import jay.chd123.judge.Judger;
import jay.chd123.judge.entity.JudgeResult;
import jay.chd123.problem.entity.db.ProblemCase;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class CppJudger implements Judger {
    @Override
    public String createCodeInContainer(String code) {
        String[] cmd = {
                "docker", "exec", "-i", CONTAINER_NAME,
                "sh", "-c", "cat > /tmp/solution.cpp"
        };
        try {
            Process process = new ProcessBuilder(cmd).start();
            try (OutputStream os = process.getOutputStream()) {
                os.write(code.getBytes());
                os.flush();  // 重要：刷新缓冲区
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "solution.cpp";
    }

    /**
     * 编译代码
     */
    @Override

    public Judger.CompileResult compileCode() {
        String[] cmd = {
                "docker", "exec", CONTAINER_NAME,
                "sh", "-c", "g++ -o /tmp/solution /tmp/solution.cpp"
        };

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process;
        try {
            process = pb.start();
            String output = readProcessOutput(process);
            int exitCode = process.waitFor();   // 现在就会是 0

            Judger.CompileResult result = new Judger.CompileResult();
            result.setSuccess(exitCode == 0);
            result.setErrorOutput(exitCode == 0 ? null : output);
            return result;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 执行单个测试用例
     */
    @Override
    public JudgeResult.CaseResult executeCase(ProblemCase testCase) {

        JudgeResult.CaseResult caseResult = new JudgeResult.CaseResult();
        caseResult.setInput(testCase.getInput());
        caseResult.setExpectedOutput(testCase.getOutput());

        long startTime = System.currentTimeMillis();

        try {
            String actualOutput = runCodeTheInput(testCase.getInput());
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

    @Override
    public String runCodeTheInput(String input) throws TimeoutException {
        // 构建执行命令：docker exec -i 容器 sh -c "执行程序"
        String[] cmd = {
                "docker", "exec", "-i", CONTAINER_NAME,
                "sh", "-c", "/tmp/solution"
        };
        try {
            // 执行命令（带超时）
            Process process = Runtime.getRuntime().exec(cmd);
            // 将输入数据写入进程的标准输入
            try (OutputStream os = process.getOutputStream()) {
                os.write(input.getBytes(StandardCharsets.UTF_8));
                os.flush();  // 重要：刷新缓冲区
            }
            boolean finished = process.waitFor(TIME_LIMIT_MS, TimeUnit.MILLISECONDS);

            if (!finished) {
                process.destroy();
                throw new TimeoutException("执行超时");
            }

            // 读取输出
            String actualOutput = readProcessOutput(process).trim();
            return actualOutput;
        }catch (IOException | InterruptedException e){
            throw new RuntimeException(e);
        }

    }

}
