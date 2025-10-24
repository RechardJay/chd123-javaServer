package jay.chd123.global.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：0成功，其它数字为失败

    private String msg; //提示信息

    private T data; //数据

    private Map<String,Object> map = new HashMap<>(); //动态数据

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.msg = "成功";
        result.data = object;
        result.code = 0;
        return result;
    }
    public static Result<Object> fail(String msg) {
        Result<Object> result = new Result<>();
        result.msg = msg;
        result.data = "客户端格式/数据/语法错误";
        result.code = 400;
        return result;
    }
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = -1;
        return result;
    }
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = code;
        return result;
    }

    public Result<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }


}
