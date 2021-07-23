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
        now.add(Calendar.MINUTE, SettingsUtils.getJwtExpire());

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(now.getTime())
                .signWith(SignatureAlgorithm.HS256, SettingsUtils.getJwtSecret())
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
                    .setSigningKey(SettingsUtils.getJwtSecret())
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 480);
        String encodeRules = "www.tggj.cn";
        String content = "1234567890";
        String enstr = SymmetricUtils.AESEncode(encodeRules, content);
        System.out.println(enstr);
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject("zhangsan")
                .setIssuedAt(new Date())
                .setExpiration(now.getTime())
                .signWith(SignatureAlgorithm.HS256, enstr)
                .compact();
        System.out.println(token);
        Claims claims = Jwts.parser()
                .setSigningKey(enstr)
                .parseClaimsJws(token).getBody();
        System.out.println(claims);
    }
}