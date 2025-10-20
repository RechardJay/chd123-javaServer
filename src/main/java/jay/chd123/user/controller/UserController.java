package jay.chd123.user.controller;

import cn.hutool.core.lang.ObjectId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jay.chd123.global.entity.Result;
import jay.chd123.user.entity.User;
import jay.chd123.user.entity.UserSignUpDTO;
import jay.chd123.user.entity.UserVO;
import jay.chd123.user.service.UserService;
import jay.chd123.util.JWTUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public UserController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping("/singup")
    public Result<UserVO> singUp(@RequestBody UserSignUpDTO userDto){
        String email = userDto.getEmail();
        long exist = userService.count(new QueryWrapper<User>().eq("email", email));
        if(exist > 0){
            return Result.error("邮箱已经存在");
        }
        //
        User user = new User();
        user.setEmail(email);
        user.setName(userDto.getName());
        user.setPassword(userDto.getPassword());
        user.setCode(ObjectId.next());
        userService.save(user);
        // 4. 生成 JWT Token（使用用户 ID 作为标识）
        String token = jwtUtil.generateToken(user.getId());

        // 5. 构建返回数据（脱敏，不含敏感信息）
        UserVO resultVO = new UserVO();
        resultVO.setCode(user.getCode());
        resultVO.setEmail((email));
        resultVO.setName(user.getName());
        resultVO.setToken(token);
        resultVO.setExpireTime(jwtUtil.getExpireSeconds()); // Token 过期秒数

        Result<UserVO> success = Result.success(resultVO);
        success.setMsg("注册成功");
        return success;
    }
    @PostMapping("login")
    public Result<UserVO> login(@RequestBody UserSignUpDTO userDTO){
        String email = userDTO.getEmail();
        String password = userDTO.getPassword();
        User user = userService.getOne(new QueryWrapper<User>().eq("email", email));
        if(user != null){
            if(password.equals(user.getPassword())){
                // 生成 JWT Token（使用用户 ID 作为标识）
                String token = jwtUtil.generateToken(user.getId());

                // 构建返回数据（脱敏，不含敏感信息）
                UserVO resultVO = new UserVO();
                resultVO.setCode(user.getCode());
                resultVO.setEmail((email));
                resultVO.setName(user.getName());
                resultVO.setToken(token);
                resultVO.setExpireTime(jwtUtil.getExpireSeconds()); // Token 过期秒数

                Result<UserVO> success = Result.success(resultVO);
                success.setMsg("登录成功");
            }
            return Result.error("账号或者密码错误");
        }
        return Result.error("账号不存");
    }
}
