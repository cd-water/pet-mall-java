package com.cdwater.petmall.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 正则工具类
 */
public class RegexUtil {
    //正则表达式
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{5,20}$";//账号
    public static final String PASSWORD_REGEX = "^[A-Za-z0-9!@#$%^&*()_+\\-=\\[\\]{},.<>/?;:'\"|\\\\]{8,32}$";//密码
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";//手机号
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";//邮箱
    public static final String VERIFY_CODE_REGEX = "^\\d{6}$";//验证码

    /**
     * 校验手机号格式是否有效
     */
    public static boolean phoneNotValid(String phone) {
        return !isMatch(phone, PHONE_REGEX);
    }

    /**
     * 校验邮箱格式是否有效
     */
    public static boolean emailNotValid(String email) {
        return isMatch(email, EMAIL_REGEX);
    }

    /**
     * 校验验证码格式是否有效
     */
    public static boolean verifyCodeNotValid(String code) {
        return !isMatch(code, VERIFY_CODE_REGEX);
    }

    /**
     * 校验账号格式是否有效
     */
    public static boolean usernameNotValid(String username) {
        return !isMatch(username, USERNAME_REGEX);
    }

    /**
     * 校验密码格式是否有效
     */
    public static boolean passwordNotValid(String password) {
        return !isMatch(password, PASSWORD_REGEX);
    }

    /**
     * 检查密码复杂度（至少包含三种字符类型）
     */
    public static boolean passwordNotComplex(String password) {
        if (StringUtils.isBlank(password)) return true;

        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if ("!@#$%^&*()_+-=[]{},.<>/?;:'\"|\\".indexOf(c) >= 0) {
                hasSpecial = true;
            }

            // 提前退出检查
            if (hasLower && hasUpper && hasDigit && hasSpecial) {
                break;
            }
        }

        int typeCount = (hasLower ? 1 : 0) + (hasUpper ? 1 : 0)
                + (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);

        return typeCount < 3;
    }

    /**
     * 执行正则匹配
     */
    private static boolean isMatch(String str, String regex) {
        if (StringUtils.isBlank(str)) return false;
        return str.matches(regex);
    }
}