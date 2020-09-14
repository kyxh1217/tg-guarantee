package com.yonyou.guarantee.configs;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yonyou.guarantee.annotation.PassToken;
import com.yonyou.guarantee.common.JWTTokenUtil;
import com.yonyou.guarantee.common.OkHttpUtil;
import com.yonyou.guarantee.constants.ReturnCode;
import com.yonyou.guarantee.vo.RestResultVO;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;


public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) {

        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        String token = httpServletRequest.getHeader("Authorization");// 从 http 请求头中取出 token
        // 执行认证
        if (token == null) {
            tokenNotPassed(httpServletResponse, "无token，请重新登录");
            return false;
        }
        // 获取 token 中的 user id
        try {
            DecodedJWT decodeToken = JWT.decode(token);
            Claim expireTime = decodeToken.getClaim("expireTime");
            if (null == expireTime || JWTTokenUtil.isTokenExpired(expireTime.asLong())) {
                tokenNotPassed(httpServletResponse, "Token过期了，请重新登录");
                return false;
            }
            Claim userName = decodeToken.getClaim("userName");
            httpServletRequest.setAttribute("userName", userName.asString());
        } catch (JWTDecodeException j) {
            OkHttpUtil.respJsonToClient(httpServletResponse, JSON.toJSONString(RestResultVO.error("Token验证失败")));
            return false;
        }
        // 验证 token
        Map<String, Claim> map = JWTTokenUtil.verifyToken(token);
        if (null == map) {
            tokenNotPassed(httpServletResponse, "非法的token，请重新登录");
            return false;
        }
        return true;
    }

    private void tokenNotPassed(HttpServletResponse httpServletResponse, String s) {
        RestResultVO<String> vo = new RestResultVO<>();
        vo.setCode(ReturnCode.TOKEN_INVALID.getCode());
        vo.setMsg(s);
        OkHttpUtil.respJsonToClient(httpServletResponse, JSON.toJSONString(vo));
    }
}
