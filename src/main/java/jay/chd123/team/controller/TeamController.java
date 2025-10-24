package jay.chd123.team.controller;

import cn.hutool.core.lang.ObjectId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jay.chd123.global.entity.Result;
import jay.chd123.team.entity.db.Team;
import jay.chd123.team.service.TeamServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/team")
public class TeamController {
    private final TeamServiceImpl teamService;

    public TeamController(TeamServiceImpl teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/create")
    @Transactional
    public Result<Object> createTeam(@RequestBody Team team) {
        long count = teamService.count(new QueryWrapper<Team>().eq("name", team.getName()).eq("creatorId", team.getCreatorId()));
        if (count > 0) {
            return Result.fail("您已经创建同名团队");
        }
        team.setCode(ObjectId.next());
        team.setCount(1);
        boolean save = teamService.save(team);
        if (!save) {
            return Result.fail("创建失败");
        }
        Long teamId = team.getId();
        boolean join = teamService.joinTeam(teamId, team.getCreatorId());
        if (!join) {
            return Result.fail("无法加入团队");
        }
        return Result.success(teamId);
    }
    @PostMapping("/join")
    public Result<Object> joinTeam(@RequestBody Map<String,Object> map) {
        String code = map.get("code").toString();
        Team team = teamService.getOne(new QueryWrapper<Team>().eq("code", code));
        if (team == null) {
            return Result.fail("团队不存在");
        }
        Long teamId = team.getId();
        Long userId = Long.valueOf(map.get("userId").toString());
        teamService.joinTeam(teamId, userId);
        return Result.success(teamId);
    }
    @PostMapping("/user/list")
    public Result<Object> userList(@RequestBody Map<String,Object> map) {
        Long userId = Long.valueOf(map.get("userId").toString());
        Map<String, List<Team>> result = new HashMap<>();
        List<Team> teamCreated = teamService.list(new QueryWrapper<Team>().eq("creatorId", userId));
        result.put("teamCreated", teamCreated);
        List<Team> joinedTeams = teamService.getJoinedTeams(userId);
        result.put("teamJoined", joinedTeams);
        return Result.success(result);
    }
}
