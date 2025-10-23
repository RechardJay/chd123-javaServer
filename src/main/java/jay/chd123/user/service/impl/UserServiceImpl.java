package jay.chd123.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.common.entity.MyFile;
import jay.chd123.common.service.MyFileServiceImpl;
import jay.chd123.user.entity.User;
import jay.chd123.user.mapper.UserMapper;
import jay.chd123.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final MyFileServiceImpl myFileServiceImpl;

    public UserServiceImpl(MyFileServiceImpl myFileServiceImpl) {
        this.myFileServiceImpl = myFileServiceImpl;
    }

    @Override
    public byte[] getUserAvatar(Long userId) {
        byte[] avatar = myFileServiceImpl.getUserAvatar(userId);
        return avatar;
    }

    @Override
    public boolean updateUserAvatar(Long userId, MyFile avatarFile) {
        myFileServiceImpl.updateUserAvatar(userId,avatarFile);
        return false;
    }

}
