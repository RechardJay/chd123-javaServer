package jay.chd123.judge.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class JudgeResult implements Serializable {


    /**
     * @see TYPE
     */
    private String type;
    private String errMsg;
    private Item errItem;
    public enum TYPE{
        YES,
        NO
    }
    @Data
    public static class Item{
        private String input;
        private String output;
    }
}
