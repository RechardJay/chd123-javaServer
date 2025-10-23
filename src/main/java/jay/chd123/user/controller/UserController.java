package jay.chd123.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.ObjectId;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jay.chd123.global.entity.Result;
import jay.chd123.user.entity.User;
import jay.chd123.user.entity.UserSignUpDTO;
import jay.chd123.user.entity.UserVO;
import jay.chd123.user.service.UserService;
import jay.chd123.util.JWTUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public UserController(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public Result<UserVO> singUp(@RequestBody UserSignUpDTO userDto) {
        String email = userDto.getEmail();
        long exist = userService.count(new QueryWrapper<User>().eq("email", email));
        if (exist > 0) {
            return Result.error("邮箱已经注册");
        }
        //
        User user = new User();
        user.setEmail(email);
        user.setName(userDto.getName());
        user.setPassword(userDto.getPassword());
        user.setCode(ObjectId.next());
        userService.save(user);
        // 4. 生成 JWT Token（使用用户 ID 作为标识）
        String token = jwtUtil.generateToken(user.getId(), user.getName());

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
    public Result<UserVO> login(@RequestBody UserSignUpDTO userDTO) {
        String email = userDTO.getEmail();
        String password = userDTO.getPassword();
        User user = userService.getOne(new QueryWrapper<User>().eq("email", email));
        if (user != null) {
            if (password.equals(user.getPassword())) {
                // 生成 JWT Token（使用用户 ID 作为标识）
                String token = jwtUtil.generateToken(user.getId(), user.getName());

                // 构建返回数据（脱敏，不含敏感信息）
                UserVO resultVO = new UserVO();
                resultVO.setCode(user.getCode());
                resultVO.setEmail((email));
                resultVO.setName(user.getName());
                resultVO.setToken(token);
                resultVO.setExpireTime(jwtUtil.getExpireSeconds()); // Token 过期秒数

                Result<UserVO> success = Result.success(resultVO);
                success.setMsg("登录成功");
                return success;
            }
            return Result.error("账号或者密码错误");
        }
        return Result.error("账号不存");
    }

    @GetMapping("/info/{id}")
    public Result<UserVO> getUserInfo(@PathVariable("id") int userId) {
        User user = userService.getById(userId);
        UserVO vo = new UserVO(user);
        return Result.success(vo);
    }

    //修该用户账号
    @PostMapping("/info/code/modify")
    public Result<Object> updateUserCode(@RequestBody Integer id, @RequestBody String code) {
        if (code.length() > 11 || code.length() < 3) {
            return Result.error("长度错误，1-10位数字或者字母");
        }
        if (!code.matches("^[a-zA-Z0-9]+$")) {
            return Result.error("格式错误,只能包含数字和字母");
        }
        long count = userService.count(new QueryWrapper<User>().eq("code", code));
        if (count > 0) {
            return Result.error("账号名已经存在");
        }
        boolean b = userService.update(null, new UpdateWrapper<User>().eq("id", id).set("code", code));
        if (b) {
            return Result.success(b);
        }
        return Result.error("未知错误，失败");
    }

    //更新用户基本信息
    @PostMapping("/info/update")
    public Result<Object> updateUserInfo(@RequestBody Map<String, Object> map) {
        User user = BeanUtil.copyProperties(map, User.class);
        boolean updated = userService.update(user, new UpdateWrapper<User>().eq("id", user.getId()));
        if (updated) {
            return Result.success(updated);
        }
        return Result.error("失败");
    }
}
