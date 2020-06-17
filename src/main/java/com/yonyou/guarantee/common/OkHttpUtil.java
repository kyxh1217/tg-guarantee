package com.yonyou.guarantee.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class OkHttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);

    private final static String XML_HTTP_REQUEST = "XMLHttpRequest";
    private final static String X_REQUESTED_WITH = "X-Requested-With";


    public static void respJsonToClient(HttpServletResponse response, String msg) {
        PrintWriter pw = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=UTF-8");
            pw = response.getWriter();
            pw.write(msg);
            pw.flush();
        } catch (IOException e) {
            logger.error(String.format("写JSON错误：%s,内容：%s", e.getMessage(), msg));
        } finally {
            if (null != pw) {
                try {
                    pw.close();
                } catch (Exception e) {
                    logger.error(String.format("写JSON错误：%s,内容：%s", e.getMessage(), msg));
                }
            }
        }
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return null != request && XML_HTTP_REQUEST.equalsIgnoreCase(request.getHeader(X_REQUESTED_WITH));
    }
}
