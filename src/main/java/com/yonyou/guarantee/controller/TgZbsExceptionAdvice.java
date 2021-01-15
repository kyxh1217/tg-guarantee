package com.yonyou.guarantee.controller;

import com.yonyou.guarantee.vo.RestResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class TgZbsExceptionAdvice {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e) {
        return RestResultVO.error(e.getMessage());
    }
}
