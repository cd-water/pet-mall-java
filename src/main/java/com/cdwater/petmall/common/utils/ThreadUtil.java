package com.cdwater.petmall.common.utils;

import java.util.Objects;

/**
 * ThreadLocal工具类
 */
public class ThreadUtil {

    public static ThreadLocal<Integer> idThreadLocal = new ThreadLocal<>();
    public static ThreadLocal<Integer> roleThreadLocal = new ThreadLocal<>();

    public static void setId(Integer id) {
        idThreadLocal.set(id);
    }

    public static Integer getId() {
        return idThreadLocal.get();
    }

    public static void removeId() {
        idThreadLocal.remove();
    }

    public static void setRole(Integer role) {
        roleThreadLocal.set(role);
    }

    public static Integer getRole() {
        return roleThreadLocal.get();
    }

    public static void removeRole() {
        roleThreadLocal.remove();
    }

    public static Boolean hasPermission(Integer id, Integer role) {
        return Objects.equals(id, ThreadUtil.getId()) && Objects.equals(role, ThreadUtil.getRole());
    }
}
