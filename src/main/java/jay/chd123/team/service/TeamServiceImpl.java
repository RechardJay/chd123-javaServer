package jay.chd123.team.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.team.entity.db.Team;
import jay.chd123.team.entity.db.TeamMember;
import jay.chd123.team.mapper.TeamMapper;
import jay.chd123.team.mapper.TeamMemberMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> {
    private final TeamMemberMapper memberMapper;

    public TeamServiceImpl(TeamMemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }
    {}

    public boolean joinTeam(Long teamId, Long userId) {
        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(teamId);
        teamMember.setUserId(userId);
        int i = memberMapper.insert(teamMember);
        return i == 1;
    }

    public List<Team> getJoinedTeams(Long userId){
        List<TeamMember> teamMemberRelation = memberMapper.selectList(new QueryWrapper<TeamMember>().eq("userId", userId));
        List<Long> teamIds = teamMemberRelation.stream().map(TeamMember::getTeamId).collect(Collectors.toList());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if(teamIds.size() > 0){
            queryWrapper.in("teamId", teamIds);
        }
        List<Team> list = list(queryWrapper.ne("creatorId", userId));
        return list;
    }
}
