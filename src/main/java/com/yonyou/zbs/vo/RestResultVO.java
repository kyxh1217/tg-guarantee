package com.yonyou.zbs.vo;

import com.yonyou.zbs.consts.ReturnCode;

public class RestResultVO<T> {
    private String code;
    private T data;
    private String msg;

    public static <T> RestResultVO<T> success(T data) {
        RestResultVO<T> restResultVO = new RestResultVO<>();
        restResultVO.setCode(ReturnCode.SUCCESS.getCode());
        restResultVO.setData(data);
        return restResultVO;
    }

    public static RestResultVO error(String msg) {
        RestResultVO restResultVO = new RestResultVO<>();
        restResultVO.setCode(ReturnCode.ERR0R.getCode());
        restResultVO.setMsg(msg);
        return restResultVO;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
