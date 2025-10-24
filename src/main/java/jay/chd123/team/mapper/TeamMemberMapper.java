package jay.chd123.team.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jay.chd123.team.entity.db.TeamMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {
}
