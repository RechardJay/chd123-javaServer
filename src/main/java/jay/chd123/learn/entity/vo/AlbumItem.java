package jay.chd123.learn.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlbumItem {
    private Long id;
    private String title;
    private String description;
    private Integer noteCount;
    private Integer sheetId;
    private String sheetTitle;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<NoteItem> notes;

    @Data
    public static class NoteItem{
        private Long id;
        private String title;
        private String content;
        private Integer seq;
        private String type;
        private LocalDateTime updateTime;
    }
}
