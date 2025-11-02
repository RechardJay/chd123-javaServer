package jay.chd123.learn.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jay.chd123.global.entity.Result;
import jay.chd123.learn.entity.db.Album;
import jay.chd123.learn.entity.db.Note;
import jay.chd123.learn.entity.dto.AlbumDto;
import jay.chd123.learn.entity.vo.AlbumItem;
import jay.chd123.learn.service.AlbumServiceImpl;
import jay.chd123.sheet.entity.db.Sheet;
import jay.chd123.sheet.service.SheetServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/album")
public class AlbumController {
    private final AlbumServiceImpl albumService;
    private final SheetServiceImpl sheetService;

    public AlbumController(AlbumServiceImpl albumService, SheetServiceImpl sheetService) {
        this.albumService = albumService;
        this.sheetService = sheetService;
    }
    @PostMapping("/create")
    public Result<Object> createAlbum(@RequestBody AlbumDto albumDto) {
        Album album = new Album();
        List<Long> noteIds = albumDto.getNoteIds();
        BeanUtil.copyProperties(albumDto, album);
        boolean save = albumService.save(album);
        if(save && noteIds!=null && !noteIds.isEmpty()){
            albumService.addNotesToAlbum(noteIds, album.getId());
        }
        return Result.success(album.getId());
    }
    @PostMapping("/list")
    public Result<List<AlbumItem>> getAlbumList(@RequestBody Map<String,Object> map) {
        Long userId = Long.valueOf(map.get("userId").toString());
        List<Album> albumList = albumService.list(new QueryWrapper<Album>().eq("creatorId", userId));
        List<AlbumItem> result = new ArrayList<>();
        for(Album album : albumList){
            AlbumItem albumItem = BeanUtil.copyProperties(album, AlbumItem.class);
            Long albumId = album.getId();
            List<Note> notes = albumService.getNotesFromAlbum(albumId);
            List<AlbumItem.NoteItem> noteItems = new ArrayList<>();
            int seq = 0;
            for(Note note : notes){
                AlbumItem.NoteItem noteItem = BeanUtil.copyProperties(note, AlbumItem.NoteItem.class);
                noteItem.setSeq(++seq);
                noteItems.add(noteItem);
            }
            Sheet sheet = sheetService.getById(albumItem.getSheetId());
            if(sheet!=null){
                albumItem.setSheetTitle(sheet.getTitle());
            }
            albumItem.setNoteCount(seq);
            albumItem.setNotes(noteItems);
            result.add(albumItem);
        }
        return Result.success(result);
    }
}
