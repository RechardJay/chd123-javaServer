package jay.chd123.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jay.chd123.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
