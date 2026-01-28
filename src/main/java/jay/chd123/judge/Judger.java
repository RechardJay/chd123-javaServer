package jay.chd123.judge;

import jay.chd123.judge.entity.JudgeResult;
import jay.chd123.problem.entity.db.ProblemCase;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public interface Judger {
    String CONTAINER_NAME = "judge-universal";
    long TIME_LIMIT_MS = 5000; // 5秒超时
    /**
     * 在容器内创建代码文件
     */
    String createCodeInContainer(String code);
    CompileResult compileCode();
    JudgeResult.CaseResult executeCase(ProblemCase testCase);
    /**
     * 辅助方法：读取进程输出
     */
    default String readProcessOutput(Process process) throws IOException {
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
    class CompileResult {
        private boolean success;
        private String errorOutput;
    }
}
