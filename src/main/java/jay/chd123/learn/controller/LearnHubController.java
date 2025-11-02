package jay.chd123.learn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jay.chd123.global.entity.Result;
import jay.chd123.learn.entity.db.Note;
import jay.chd123.learn.service.NoteServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/learnhub")
public class LearnHubController {

    private final NoteServiceImpl noteService;

    public LearnHubController(NoteServiceImpl noteServiceImpl) {
        this.noteService = noteServiceImpl;
    }
    //用户的学习中心的基础数据
    @GetMapping("/stats")
    public Result<Map<String, Long>> getBaseStats(@RequestParam Long userId) {
        long count = noteService.count(new QueryWrapper<Note>().eq("creatorId", userId));
        Map<String, Long> result = new HashMap<>();
        result.put("notes", count);
        result.put("albums", 3L);
        result.put("follow", 3L);
        return Result.success(result);
    }
}
