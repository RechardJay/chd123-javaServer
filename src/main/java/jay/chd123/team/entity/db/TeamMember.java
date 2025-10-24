package jay.chd123.team.entity.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("team_members")
public class TeamMember {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teamId;
    private Long userId;
    private LocalDateTime createdTime;
}
