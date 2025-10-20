package jay.chd123.user.entity;

import lombok.Data;

@Data
public class UserVO {
    private String code;
    private String email;
    private String name;
    private String token;
    private long expireTime; // 过期秒数
}
