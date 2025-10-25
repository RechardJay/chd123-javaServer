package jay.chd123.learn.entity.vo;

import jay.chd123.learn.entity.Note;
import lombok.Data;

import java.util.List;

@Data
public class NoteDetail extends Note {
    private String creatorName;
    private String problemTitle;
    private List<Long> fileList;
}
