package jay.chd123.learn.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.learn.entity.db.Album;
import jay.chd123.learn.entity.db.AlbumNoteRelation;
import jay.chd123.learn.entity.db.Note;
import jay.chd123.learn.mapper.AlbumMapper;
import jay.chd123.learn.mapper.AlbumNotesMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> {
    private final AlbumNotesMapper albumNotesMapper;
    private final NoteServiceImpl noteService;

    public AlbumServiceImpl(AlbumNotesMapper albumNotesMapper, NoteServiceImpl noteService) {
        this.albumNotesMapper = albumNotesMapper;
        this.noteService = noteService;
    }
    public void addNotesToAlbum(List<Long> noteIds,Long albumId) {
        int order = 1;
        for (Long noteId : noteIds) {
            AlbumNoteRelation relation = new AlbumNoteRelation();
            relation.setAlbumId(albumId);
            relation.setNoteId(noteId);
            relation.setSeq(order++);
            albumNotesMapper.insert(relation);
        }
    }
    public List<Note> getNotesFromAlbum(Long albumId) {
        List<AlbumNoteRelation> albumNoteRelations = albumNotesMapper.selectList(new QueryWrapper<AlbumNoteRelation>().eq("albumId", albumId).orderByAsc("seq"));
        List<Note> notes = new ArrayList<>();
        albumNoteRelations.stream().map(AlbumNoteRelation::getNoteId).forEach(noteId->{
            Note note = noteService.getById(noteId);
            notes.add(note);
        });
        return notes;
    }
}
