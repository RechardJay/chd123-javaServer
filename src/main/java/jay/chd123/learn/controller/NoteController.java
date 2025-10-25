package jay.chd123.learn.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jay.chd123.global.customException.BusinessException;
import jay.chd123.global.entity.Result;
import jay.chd123.learn.entity.Note;
import jay.chd123.learn.entity.vo.NoteDetail;
import jay.chd123.learn.service.NoteServiceImpl;
import jay.chd123.problem.entity.db.Problem;
import jay.chd123.problem.service.servieImpl.ProblemServiceImpl;
import jay.chd123.user.entity.User;
import jay.chd123.user.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/note")
public class NoteController {
    private final NoteServiceImpl noteService;
    private final UserServiceImpl userService;
    private final ProblemServiceImpl problemService;


    public NoteController(NoteServiceImpl noteService, UserServiceImpl userServiceImpl, ProblemServiceImpl problemServiceImpl) {
        this.noteService = noteService;
        this.userService = userServiceImpl;
        this.problemService = problemServiceImpl;
    }
    @PostMapping("/create")
    public Result<Object> createNote(@RequestBody Note note) {
        noteService.save(note);
        return Result.success(note.getId());
    }
    @PostMapping("/list")
    public Result<List<?>> getUserNotes(@RequestBody Map<String,Object> map) {
        Object objectId = map.get("userId");
        if(objectId == null) {
            throw new BusinessException("错误");
        }
        Long userId = Long.valueOf(objectId.toString());
        List<Note> noteList = noteService.list(new QueryWrapper<Note>().eq("creatorId", userId));
        return Result.success(noteList);
    }
    @PostMapping("/detail")
    public Result<NoteDetail> getNoteDetail(@RequestBody Map<String,Object> map) {
        Long noteId = Long.valueOf(map.get("noteId").toString());
        Note note = noteService.getById(noteId);
        NoteDetail noteDetail = BeanUtil.copyProperties(note, NoteDetail.class);
        User user = userService.getById(note.getCreatorId());
        noteDetail.setCreatorName(user.getName());
        if(Note.TYPE.problem.toString().equals(note.getType())) {
            Problem problem = problemService.getById(note.getSourceId());
            noteDetail.setProblemTitle(problem.getTitle());
        }
        return Result.success(noteDetail);
    }
}
