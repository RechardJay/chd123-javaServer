package jay.chd123.learn.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class AlbumDto {
    private Long id;
    private String title;
    private String description;
    private Long creatorId;
    private Long sheetId;
    private List<Long> noteIds;
}
