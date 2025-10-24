package jay.chd123.team.entity.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Team {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private Long creatorId;
    private String intro;
    private Integer count;
    private LocalDateTime createTime;
}
