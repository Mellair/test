package com.mxy.regi.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用的返回结果类型，服务端响应的数据都会封装成此类对象
 * @param <T>
 */
@Data
public class JsonResult<T> {
    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    /**
     * 响应成功
     * @param object
     * @param <T>
     * @return
     */

    public static <T> JsonResult<T> success(T object) {
        JsonResult<T> r = new JsonResult<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    /**
     * 响应失败
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> JsonResult<T> error(String msg) {
        JsonResult r = new JsonResult();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public JsonResult<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
