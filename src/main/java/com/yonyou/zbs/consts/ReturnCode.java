package com.yonyou.zbs.consts;

public enum ReturnCode {
    SUCCESS("1"),
    TOKEN_INVALID("-1"),
    ERR0R("0");

    private String code;

    ReturnCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
