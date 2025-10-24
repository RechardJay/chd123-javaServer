package jay.chd123.user.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jay.chd123.common.entity.MyFile;
import jay.chd123.common.service.MyFileServiceImpl;
import jay.chd123.global.entity.Result;
import jay.chd123.user.entity.User;
import jay.chd123.user.entity.UserSignUpDTO;
import jay.chd123.user.entity.UserVO;
import jay.chd123.user.service.UserService;
import jay.chd123.util.JWTUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final MyFileServiceImpl myFileServiceImpl;

    public UserController(UserService userService, JWTUtil jwtUtil, MyFileServiceImpl myFileServiceImpl) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.myFileServiceImpl = myFileServiceImpl;
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
        user.setCode(RandomUtil.randomString(10)); //随机十位code
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
    public Result<Object> updateUserCode(@RequestBody Map<String, Object> map) {
        String code = map.get("code").toString();
        Long id = Long.valueOf(map.get("id").toString());
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

    //刷新用户token
    @PostMapping("/token/update")
    public Result<Object> updateUserToken(@RequestBody  Map<String, Object> map) {
        Long id = Long.valueOf(map.get("id").toString());
        String name = map.get("name").toString();
        String token = jwtUtil.generateToken(id, name);
        return Result.success(token);
    }
    //上传用户头像
    @PostMapping("/avatar/upload")
    public Result<Object> uploadAvatar(@RequestParam("file") MultipartFile file,@RequestParam("userId") Long userId) {
        // 1. 校验用户ID（必须存在，防止未登录上传）
        if (userId == null) {
            return Result.fail("用户未登录");
        }

        // 2. 校验文件（类型、大小）
        if (file.isEmpty()) {
            return Result.fail("请选择文件");
        }
        String contentType = file.getContentType();
        if (!(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            return Result.fail("仅支持JPG、PNG格式");
        }
        if (file.getSize() > 2 * 1024 * 1024) {  // 2MB
            return Result.fail("文件大小不能超过2MB");
        }

        try {
            // 3. 生成文件实体类
            String filename = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileKey = String.format("user_avatar_%s", userId);
            String fileType = file.getContentType();
            Integer fileSize =Integer.parseInt(String.valueOf(file.getSize()));
            MyFile avatarFile = MyFile.builder()
                    .fileKey(fileKey)
                    .fileName(filename)
                    .fileType(fileType)
                    .content(file.getBytes())
                    .fileSize(fileSize)
                    .build();
            // 4. 保存文件到数据库
            MyFile existFile = myFileServiceImpl.getOne(new QueryWrapper<MyFile>().eq("fileKey", fileKey));
            if (existFile == null) {
                avatarFile.setId(existFile.getId());
            }
            myFileServiceImpl.saveOrUpdate(avatarFile);
            return Result.success("上传成功");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败"+e.getMessage());
        }
    }
    //获取用户头像
    @GetMapping("/avatar/{id}")
    public void getAvatarByUserId(@PathVariable String id, HttpServletResponse response) {
        // 1. 从数据库查询该用户的头像
        String fileKey = String.format("user_avatar_%s", id);
        QueryWrapper<MyFile> queryWrapper = new QueryWrapper<MyFile>().eq("fileKey", fileKey);
        MyFile file = myFileServiceImpl.getOne(queryWrapper);
        response.setContentType(file.getFileType());
        response.setContentLength(file.getFileSize());
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(file.getContent());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
