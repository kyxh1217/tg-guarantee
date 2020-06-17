package com.yonyou.guarantee.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class SysConfigUtil {

    private static Logger logger = LoggerFactory.getLogger(SysConfigUtil.class);
    private static int jwtExpire;
    private static String jwtSecret;
    private final static Properties properties;

    static {
        properties = new Properties();
        try {
            InputStreamReader in = new InputStreamReader(
                    Objects.requireNonNull(SysConfigUtil.class.getClassLoader().getResourceAsStream("config/setting.properties")),
                    StandardCharsets.UTF_8);
            properties.load(in);
            jwtExpire = Integer.parseInt(properties.getProperty("jwt.expire", "kui-jwt"));
            jwtSecret = properties.getProperty("jwt.secret", "");
        } catch (IOException e) {
            logger.error("读取config文件错误:" + e.getMessage());
        }
    }


    public static int getJwtExpire() {
        return jwtExpire;
    }

    public static String getJwtSecret() {
        return jwtSecret;
    }

}
