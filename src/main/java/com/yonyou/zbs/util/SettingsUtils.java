package com.yonyou.zbs.util;

import java.io.IOException;
import java.util.Properties;

public class SettingsUtils {
    private static int jwtExpire;

    private static String jwtSecret;

    private static String pdfPath;

    private static String pdfUrl;

    static {
        try {
            Properties properties = new Properties();
            properties.load(SettingsUtils.class.getClassLoader().getResourceAsStream("config/setting.properties"));
            jwtExpire = Integer.parseInt(properties.getProperty("jwt.expire", "480"));
            jwtSecret = properties.getProperty("jwt.secret", "www.tggj.cn");
            pdfPath = properties.getProperty("tg.pdf.path", "d:/pdf");
            pdfUrl = properties.getProperty("tg.pdf.url", "http://127.0.0.1");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int getJwtExpire() {
        return jwtExpire;
    }

    public static String getJwtSecret() {
        return jwtSecret;
    }

    public static String getPdfPath() {
        return pdfPath;
    }

    public static String getPdfUrl() {
        return pdfUrl;
    }
}
