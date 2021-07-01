package com.yonyou.zbs.consts;

public enum DbType {
    DB_LOCAL("local"),
    DB_ZBS("zbs"),
    DB_OA("OA");

    private String name;

    DbType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
