package jay.chd123.sheet.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jay.chd123.global.entity.Result;
import jay.chd123.problem.entity.db.Problem;
import jay.chd123.sheet.entity.db.Sheet;
import jay.chd123.sheet.service.SheetServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sheet")
public class SheetController {
    private final SheetServiceImpl sheetService;

    public SheetController(SheetServiceImpl sheetService) {
        this.sheetService = sheetService;
    }

    /**
     * 获取用户创建/拥有的题单
     */
    @GetMapping("/user/{id}")
    public Result<List<Sheet>> getUserSheet(@PathVariable Long id) {
        List<Sheet> sheetList = sheetService.list(new QueryWrapper<Sheet>().eq("creatorId", id));
        return Result.success(sheetList);
    }
    //创建题单
    @PostMapping("/create")
    public Result<Object> createSheet(@RequestBody Map<Sheet,Object> map) {
        Long userId = Long.valueOf(map.get("userId").toString());
        String title = map.get("title").toString();
        long count = sheetService.count(new QueryWrapper<Sheet>().eq("id", userId).eq("title", title));
        if (count > 0) {
            return Result.fail("您已经创建同名题单");
        }
        String description = map.get("description").toString();
        List<String> tags = new ArrayList<>();
        List<String> list = (List<String>) map.get("tags");
        if (list != null) {
            tags.addAll(list);
        }
        Sheet sheet = Sheet.builder()
                .title(title)
                .creatorId(userId)
                .description(description)
                .tag(StrUtil.join(",",tags))
                .build();
        boolean saved = sheetService.save(sheet);
        if (saved) {
            return Result.success(sheet.getId());
        }
        return Result.fail("创建失败");
    }
    //删除题单
    @PostMapping("/delete")
    public Result<Object> deleteSheet(@RequestBody Map<Sheet,Object> map) {
        Long userId = Long.valueOf(map.get("userId").toString());
        Long sheetId = Long.valueOf(map.get("sheetId").toString());
        boolean remove = sheetService.remove(new QueryWrapper<Sheet>().eq("creatorId", userId).eq("id", sheetId));
        return Result.success(remove);
    }
    //更新题单
    @PostMapping("/update")
    public Result<Object> updateSheet(@RequestBody Sheet sheet) {
        sheetService.count(new QueryWrapper<Sheet>().eq("id", sheet.getId()).eq("creatorId", sheet.getCreatorId()));
        boolean updated = sheetService.updateById(sheet);
        return Result.success(updated);
    }
    @PostMapping("/problems/{id}")
    public Result<List<Problem>> getProblemsOfSheet(@PathVariable Long id) {
        List<Problem> problems = sheetService.getProblemsOfSheet(id);
        return Result.success(problems);
    }
}
