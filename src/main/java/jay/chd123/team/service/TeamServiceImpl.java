package jay.chd123.team.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.team.entity.db.Team;
import jay.chd123.team.entity.db.TeamMember;
import jay.chd123.team.mapper.TeamMapper;
import jay.chd123.team.mapper.TeamMemberMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> {
    private final TeamMemberMapper memberMapper;

    public TeamServiceImpl(TeamMemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    public boolean joinTeam(Long teamId, Long userId) {
        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(teamId);
        teamMember.setUserId(userId);
        int i = memberMapper.insert(teamMember);
        return i == 1;
    }

    public List<Team> getJoinedTeams(Long userId) {
        //当前用户所有所有团队，包括创建和加入
        List<TeamMember> teamMemberRelation = memberMapper.selectList(new QueryWrapper<TeamMember>().eq("userId", userId));
        List<Long> teamIds = teamMemberRelation.stream().map(TeamMember::getTeamId).collect(Collectors.toList());
        //没有团队，返回空列表
        if (teamIds.isEmpty()) {
            return new ArrayList<>();
        }
        //从团队列表中移除自己创建的
        List<Team> list = list(new QueryWrapper<Team>().in("id", teamIds).ne("creatorId", userId));
        return list;
    }
}
