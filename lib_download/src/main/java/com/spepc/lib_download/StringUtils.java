package com.spepc.lib_download;

public class StringUtils  {

    /**
     * 如果s为空 或者为null，返回true; 如果s非空非null，返回false
     */
    public static Boolean isNullOrEmpty(String s) {
        return null == s || s.isEmpty();

    }

    public static Boolean isNotEmpty(String s) {
        return null != s && !s.isEmpty();
    }

    /**
     * 如果空返回空字符串
     */
    public static String parsString(String s) {
        if (isNotEmpty(s)) {
            return s;
        } else {
            return "";
        }
    }
}
