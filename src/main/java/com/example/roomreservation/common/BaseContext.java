package com.example.roomreservation.common;

import java.util.Map;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Map<String, Integer>> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     *
     * @param map
     */
    public static void setCurrent(Map<String, Integer> map) {
        threadLocal.set(map);
    }

    /**
     * 获取值
     *
     * @return
     */
    public static Map<String, Integer> getCurrent() {
        return threadLocal.get();
    }

    public static void removeCurrent() {
        threadLocal.remove();
    }
}