package jay.chd123.learn.controller;

import cn.hutool.core.bean.BeanUtil;
import jay.chd123.global.entity.Result;
import jay.chd123.learn.entity.db.Album;
import jay.chd123.learn.entity.dto.AlbumDto;
import jay.chd123.learn.service.AlbumServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/album")
public class AlbumController {
    private final AlbumServiceImpl albumService;

    public AlbumController(AlbumServiceImpl albumService) {
        this.albumService = albumService;
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
}
