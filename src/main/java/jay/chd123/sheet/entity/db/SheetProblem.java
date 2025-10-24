package jay.chd123.sheet.entity.db;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sheet_problems")
public class SheetProblem {
    private Long id;
    private Long sheetId;
    private Long problemId;
    private LocalDateTime createTime;
}
