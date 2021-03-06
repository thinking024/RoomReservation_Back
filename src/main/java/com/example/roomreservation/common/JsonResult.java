package com.example.roomreservation.common;

import lombok.Data;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
public class JsonResult<T> {
    private Integer code;
    private String msg;
    private T data;

    private JsonResult() {
    }

    public static <T> JsonResult<T> success() {
        JsonResult<T> r = new JsonResult<T>();
        r.data = null;
        r.code = 100;
        r.msg = "success";
        return r;
    }

    public static <T> JsonResult<T> success(T object) {
        JsonResult<T> r = new JsonResult<T>();
        r.data = object;
        r.code = 100;
        r.msg = "success";
        return r;
    }

    public static <T> JsonResult<T> error(Integer code, String msg) {
        JsonResult r = new JsonResult();
        r.msg = msg;
        r.code = code;
        return r;
    }

    public static <T> JsonResult<T> error(String msg) {
        JsonResult r = new JsonResult();
        r.msg = msg;
        r.code = 0;
        return r;
    }
}