package com.yonyou.guarantee.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt token工具类
 *
 * @author 朽木
 * @since 2018/10/18
 */
public class JWTTokenUtil {


    /**
     * 获取token
     *
     * @param userName 登录用户id
     * @return String
     */
    public static String getToken(String userName) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, SysConfigUtil.getJwtExpire());
        Long expireTime = now.getTime().getTime();

        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        return JWT.create().withHeader(map)                            // header
                .withClaim("userName", userName)
                .withClaim("expireTime", expireTime)
                .sign(Algorithm.HMAC256(SysConfigUtil.getJwtSecret()));                      // signature
    }

    public static boolean isTokenExpired(Long expireTime) {
        Date expireDate = new Date(expireTime);
        return expireDate.before(new Date());
    }

    /**
     * 解密Token,返回null则token校验失败
     *
     * @param token 要解密的token
     * @return Map
     */
    public static Map<String, Claim> verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SysConfigUtil.getJwtSecret())).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt == null ? null : jwt.getClaims();
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String token = getToken("666");
        System.out.println(token);
        Map<String, Claim> map = verifyToken(token);
    }
}