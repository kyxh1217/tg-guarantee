package com.yonyou.zbs.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Calendar;
import java.util.Date;

public class JWTTokenUtils {

    /**
     * 获取token
     *
     * @param userName 登录用户id
     * @return String
     */
    public static String getToken(String userName) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, ConfigUtils.getJwtExpire());

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(now.getTime())
                .signWith(SignatureAlgorithm.HS256, ConfigUtils.getJwtSecret())
                .compact();
    }

    public static boolean isTokenExpired(Date expireDate) {
        return expireDate.before(new Date());
    }

    /**
     * 解密Token,返回null则token校验失败
     *
     * @param token 要解密的token
     * @return Map
     */
    public static Claims verifyToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(ConfigUtils.getJwtSecret())
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }
}