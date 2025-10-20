package jay.chd123.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {
    // 密钥（生产环境需配置在配置文件，避免硬编码）
    @Value("${jwt.secret}")
    private String secret;

    // Token 过期时间（单位：秒，默认 24 小时）
    @Getter
    @Value("${jwt.expireSeconds:86400}")
    private long expireSeconds;

    /**
     * 生成 Token（基于用户 ID）
     */
    public String generateToken(Long userId) {
        // 过期时间 = 当前时间 + 过期秒数
        Date expireTime = new Date(System.currentTimeMillis() + expireSeconds * 1000);

        // 使用 Hutool 的 JWT 工具构建 Token
        return JWT.create()
                .setPayload("userId", userId) // 存储用户唯一标识（避免敏感信息）
                        .setIssuedAt(new Date()) // 签发时间
                .setExpiresAt(expireTime) // 过期时间
                .setSigner(JWTSignerUtil.hs256(secret.getBytes())) // 签名算法（HS256）
                .sign(); // 生成 Token 字符串
    }

    /**
     * 验证 Token 有效性（是否过期、签名是否正确）
     */
    public boolean validateToken(String token) {
        try {
            JWT jwt = JWT.of(token);
            // 验证签名和过期时间
            JWTValidator validator = JWTValidator.of(jwt);
            validator.validateAlgorithm(JWTSignerUtil.hs256(secret.getBytes())); // 验证签名算法
            validator.validateDate(); // 验证是否过期
            return true;
        } catch (Exception e) {
            // 验证失败（签名错误、已过期等）
            return false;
        }
    }

    /**
     * 从 Token 中解析用户 ID
     */
    public Long getUserIdFromToken(String token) {
        JWT jwt = JWT.of(token);
        // 1. 获取原始载荷值（可能是 Integer、String 或 Long）
        Object userIdObj = jwt.getPayload("userId");
        if (userIdObj == null) {
            throw new RuntimeException("Token 中未包含 userId");
        }

        // 2. 手动转换为 Long
        if (userIdObj instanceof Number) {
            // 若为数字类型（Integer、Long 等），直接转 Long
            return ((Number) userIdObj).longValue();
        } else if (userIdObj instanceof String) {
            // 若为字符串，尝试解析为 Long
            return Long.parseLong((String) userIdObj);
        } else {
            throw new RuntimeException("userId 类型不支持：" + userIdObj.getClass().getName());
        }
    }

    /**
     * 获取 Token 过期时间（秒）
     */
    public long getExpireSeconds() {
        return expireSeconds;
    }
}
