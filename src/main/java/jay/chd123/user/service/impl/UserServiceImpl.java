package jay.chd123.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.user.entity.User;
import jay.chd123.user.mapper.UserMapper;
import jay.chd123.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
