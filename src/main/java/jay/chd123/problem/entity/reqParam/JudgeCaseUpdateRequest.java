package jay.chd123.problem.entity.reqParam;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class JudgeCaseUpdateRequest implements Serializable {
    private Integer id;
    private String code;
    private List<Case> caseList;
    @Data
    public static class Case{
        private String input;
        private String output;
    }
}
