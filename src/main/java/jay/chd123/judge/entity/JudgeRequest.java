package jay.chd123.judge.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class JudgeRequest implements Serializable {
    /**
     * @see ProgramLanguage
     */
    private String language;
    private String code;
    private Integer problemId;
    public enum ProgramLanguage{
        c,
        cpp,
        python,
        nodeJS,
        java,
    }
}
