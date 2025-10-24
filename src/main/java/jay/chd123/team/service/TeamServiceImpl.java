package jay.chd123.team.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.team.entity.db.Team;
import jay.chd123.team.mapper.TeamMapper;
import jay.chd123.team.mapper.TeamMemberMapper;
import org.springframework.stereotype.Service;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> {
    private final TeamMemberMapper memberMapper;

    public TeamServiceImpl(TeamMemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }
}
