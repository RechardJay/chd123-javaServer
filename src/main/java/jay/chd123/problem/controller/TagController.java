package jay.chd123.problem.controller;

import jay.chd123.global.entity.Result;
import jay.chd123.problem.entity.db.Tag;
import jay.chd123.problem.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game/tag")
public class TagController {
    private final TagService tagService;

    public TagController(TagService problemTagService) {
        this.tagService = problemTagService;
    }

    @GetMapping
    public Result<List<String>> getAllTags() {
        List<Tag> tags = tagService.list();
        List<String> tagList = tags.stream().map(i -> i.getName()).collect(Collectors.toList());
        return Result.success(tagList);
    }
}
