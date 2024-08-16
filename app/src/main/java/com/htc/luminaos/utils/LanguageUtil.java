package com.htc.luminaos.utils;

import java.util.Locale;

public class LanguageUtil {

    public static String getCurrentLanguage() {
        // 获取当前的Locale对象
        Locale locale = Locale.getDefault();

        // 获取当前系统语言
        String language = locale.getLanguage();

        // 获取当前系统的国家/地区代码
        String country = locale.getCountry();

        return language + "-" + country;  // 返回语言和国家代码的组合
    }
}
