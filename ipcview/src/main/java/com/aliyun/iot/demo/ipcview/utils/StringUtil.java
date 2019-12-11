package com.aliyun.iot.demo.ipcview.utils;

import java.util.UUID;

/**
 *
 * ！！！注意！！！
 * 注意本示例代码主要用于演示部分视频业务接口以及对应的效果
 * 代码中涉及的交互，UI以及代码框架请自行设计，示例代码仅供参考，稳定性请客户自行保证。
 * @author azad
 */
public class StringUtil {

    public static boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isIntegerNumber(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        return str.matches("-?[0-9]+");
    }

    public static boolean isIntegerPositiveNumber(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        return str.matches("[0-9]+");
    }

    public static boolean isIntegerNumberInRange(String str, int min, int max) {
        if (isIntegerNumber(str)) {
            if (min >= max) {
                return false;
            }
            int tmp = Integer.parseInt(str);
            if (min <= tmp && tmp <= max) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static String getUniqueRandomString(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
