package jay.chd123.learn.entity.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Album {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private Long creatorId;
    private Long sheetId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
