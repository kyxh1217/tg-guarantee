package com.yonyou.guarantee.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:config/setting.properties")
public class SettingsUtil {
    private static int jwtExpire;

    private static String jwtSecret;

    @Value("${jwt.expire}")
    public void setJwtExpire(int val) {
        jwtExpire = val;
    }

    @Value("${jwt.secret}")
    public void setJwtSecret(String val) {
        jwtSecret = val;
    }

    public static int getJwtExpire() {
        return jwtExpire;
    }

    public static String getJwtSecret() {
        return jwtSecret;
    }

}
