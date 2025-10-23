package jay.chd123.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserVO {
    private String code;
    private String email;
    private String name;
    private String bio;
    private String token;
    private long expireTime; // 过期秒数
    public UserVO(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.bio = user.getBio();
        this.code = user.getCode();
    }
}
