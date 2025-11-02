package jay.chd123.learn.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.learn.entity.db.Album;
import jay.chd123.learn.entity.db.AlbumNoteRelation;
import jay.chd123.learn.mapper.AlbumMapper;
import jay.chd123.learn.mapper.AlbumNotesMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> {
    private final AlbumNotesMapper albumNotesMapper;

    public AlbumServiceImpl(AlbumNotesMapper albumNotesMapper) {
        this.albumNotesMapper = albumNotesMapper;
    }
    public void addNotesToAlbum(List<Long> noteIds,Long albumId) {
        int order = 0;
        for (Long noteId : noteIds) {
            AlbumNoteRelation relation = new AlbumNoteRelation();
            relation.setAlbumId(albumId);
            relation.setNoteId(noteId);
            relation.setSeq(order++);
            albumNotesMapper.insert(relation);
        }
    }
}
