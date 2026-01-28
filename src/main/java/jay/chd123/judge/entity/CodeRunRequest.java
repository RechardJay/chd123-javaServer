package jay.chd123.judge.entity;

import lombok.Data;

@Data
public class CodeRunRequest {
    private String code;
    private String language;
    private String input;
}
