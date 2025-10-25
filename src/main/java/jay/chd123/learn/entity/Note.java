package jay.chd123.learn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Note {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private Long creatorId;

    /**
     * @see TYPE
     */
    private String type;
    private Long sourceId;
    private String meta;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    public static enum TYPE{
        original,
        fork,
        problem
    }
}
