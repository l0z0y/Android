package com.customizedemo.mylibrary.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecodeUtil {

    private static final Pattern UNICODE = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");


    /**
     * 中文转Unicode
     * 其他英文字母或特殊符号也可进行Unicode编码
     *
     * @param cn
     * @return
     */
    public static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        StringBuilder returnStr = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            returnStr.append("\\u").append(Integer.toString(chars[i], 16));
        }
        return returnStr.toString();
    }


    /**
     * Unicode转 汉字字符串
     *
     * @param str
     * @return
     */
    public static String unicodeToCN(String str) {
        Matcher matcher = UNICODE.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }
}
