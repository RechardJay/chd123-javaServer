package jay.chd123.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jay.chd123.common.entity.MyFile;
import jay.chd123.user.entity.User;

public interface UserService extends IService<User> {
    byte[] getUserAvatar(Long userId);
    boolean updateUserAvatar(Long userId, MyFile avatarFile);
}
