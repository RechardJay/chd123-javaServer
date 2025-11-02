package jay.chd123.learn.entity.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("album_notes")
public class AlbumNoteRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long albumId;
    private Long noteId;
    private Integer seq;
    private LocalDateTime addTime;
}
