package com.yonyou.guarantee.constants;

public enum DbType {
    DB_LOCAL("local"),
    DB_ZBS("zbs"),
    DB_OA("OA");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    DbType(String name) {
        this.name = name;
    }

}
