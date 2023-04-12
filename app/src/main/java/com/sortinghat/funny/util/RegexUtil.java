package com.sortinghat.funny.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wzy on 2021/7/21
 */
public class RegexUtil {

    /**
     * 判断是否包含特殊字符
     */
    public static boolean isHasSpecailString(String str) {
        String regEx = "[～@＠#￥%％&×－=＝＋·_+~*÷   ，`。、；！‘’【】《》？：“”「」|（）……——   ,./;' \\ <>?:\"{}!()…—    ^$]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
