package jay.chd123.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jay.chd123.common.entity.MyFile;
import jay.chd123.common.mapper.MyFileMapper;
import org.springframework.stereotype.Service;

@Service
public class MyFileServiceImpl extends ServiceImpl<MyFileMapper, MyFile>{

    /**
     * 获取用户头像
     */
    public byte[] getUserAvatar(Long userId) {
        String key = "user_avatar_" + userId;
        QueryWrapper<MyFile> fileKey = new QueryWrapper<MyFile>().eq("fileKey", key);
        MyFile file = getOne(fileKey);
        return file.getContent();
    }
    public boolean updateUserAvatar(Long userId, MyFile avatarFile) {
        String key = "user_avatar_" + userId;
        return false;
    }
}
