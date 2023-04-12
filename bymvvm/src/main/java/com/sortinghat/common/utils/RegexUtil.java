package com.sortinghat.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wzy on 2021/4/8
 */
public class RegexUtil {

    /**
     * 字符串是否含有空格Pattern
     */
    private static final Pattern STRING_SPACE_PATTERN = Pattern.compile("[\\s]+");

    /**
     * 判断字符串是否含有空格
     */
    public static boolean isContainBlankSpace(String str) {
        Matcher matcher = STRING_SPACE_PATTERN.matcher(str);
        boolean flag = false;
        while (matcher.find()) {
            flag = true;
        }
        return flag;
    }
}
