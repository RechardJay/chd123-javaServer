package jay.chd123.judge.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JudgeResult implements Serializable {

    private boolean success;          // 整体是否成功
    private String message;           // 总体消息
    private List<CaseResult> results; // 每个测试用例的详细结果
    private String errorMsg;          // 错误信息（如果有）
    private String errorType;         // 错误类型：COMPILE_ERROR, TIMEOUT, RUNTIME_ERROR

    @Data
    public static class CaseResult {
        private String input;          // 输入数据
        private String expectedOutput; // 预期输出
        private String actualOutput;   // 实际输出
        private Long executionTime;    // 执行用时（ms）
        private boolean passed;        // 是否通过
        private String caseMessage;    // 本用例的消息
    }
}

