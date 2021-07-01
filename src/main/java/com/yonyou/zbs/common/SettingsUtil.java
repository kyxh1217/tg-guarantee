package com.yonyou.zbs.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:config/setting.properties")
public class SettingsUtil {
    private static int jwtExpire;

    private static String jwtSecret;

    public static int getJwtExpire() {
        return jwtExpire;
    }

    @Value("${jwt.expire}")
    public void setJwtExpire(int val) {
        jwtExpire = val;
    }

    public static String getJwtSecret() {
        return jwtSecret;
    }

    @Value("${jwt.secret}")
    public void setJwtSecret(String val) {
        jwtSecret = val;
    }

}
