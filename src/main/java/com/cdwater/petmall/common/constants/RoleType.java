package com.cdwater.petmall.common.constants;

import java.util.Objects;

/**
 * 角色类型
 */
public class RoleType {
    //管理员
    public static final Integer ADMIN = 0;

    //宠物店
    public static final Integer PETSHOP = 1;

    //普通用户
    public static final Integer USER = 2;

    public static Boolean isAdmin(Integer role) {
        return Objects.equals(role, ADMIN);
    }

    public static Boolean isPetShop(Integer role) {
        return Objects.equals(role, PETSHOP);
    }

    public static Boolean isUser(Integer role) {
        return Objects.equals(role, USER);
    }
}
