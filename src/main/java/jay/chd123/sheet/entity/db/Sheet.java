package jay.chd123.sheet.entity.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Sheet {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long creatorId;
    private Long sourceId;
    private LocalDateTime createTime;
}
