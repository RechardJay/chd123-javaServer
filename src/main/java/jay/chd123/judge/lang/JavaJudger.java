package jay.chd123.judge.lang;

import jay.chd123.judge.Judger;
import jay.chd123.judge.entity.JudgeResult;
import jay.chd123.problem.entity.db.ProblemCase;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class JavaJudger implements Judger {
    @Override
    public String createCodeInContainer(String code) {
        // For Java, the public class name must match the file name.
        String[] cmd = {
                "docker", "exec", "-i", CONTAINER_NAME,
                "sh", "-c", "cat > /tmp/Solution.java"
        };
        try {
            Process process = new ProcessBuilder(cmd).start();
            try (OutputStream os = process.getOutputStream()) {
                os.write(code.getBytes());
                os.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Solution.java";
    }

    @Override
    public CompileResult compileCode() {
        String[] cmd = {
                "docker", "exec", CONTAINER_NAME,
                "sh", "-c", "javac /tmp/Main.java"
        };

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        try {
            Process process = pb.start();
            String output = readProcessOutput(process);
            int exitCode = process.waitFor();

            CompileResult result = new CompileResult();
            result.setSuccess(exitCode == 0);
            result.setErrorOutput(exitCode == 0 ? null : output);
            return result;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JudgeResult.CaseResult executeCase(ProblemCase testCase) {
        JudgeResult.CaseResult caseResult = new JudgeResult.CaseResult();
        caseResult.setInput(testCase.getInput());
        caseResult.setExpectedOutput(testCase.getOutput());

        long startTime = System.currentTimeMillis();

        try {
            // Command to run the compiled Java code
            String[] cmd = {
                    "docker", "exec", "-i", CONTAINER_NAME,
                    "sh", "-c", "java -cp /tmp Main"
            };

            Process process = Runtime.getRuntime().exec(cmd);
            try (OutputStream os = process.getOutputStream()) {
                os.write(testCase.getInput().getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            boolean finished = process.waitFor(TIME_LIMIT_MS, TimeUnit.MILLISECONDS);

            if (!finished) {
                process.destroy();
                caseResult.setCaseMessage("执行超时");
                caseResult.setPassed(false);
                return caseResult;
            }

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
}
