package jay.chd123.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("file")
public class MyFile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String fileKey;
    private String fileName;
    private String fileType;
    private Integer fileSize;
    private byte[] content;
    private LocalDateTime createTime;
}
