package com.yonyou.guarantee.vo;


import java.util.List;
import java.util.Map;

public class PageRespVO {
    private int total;
    private List<Map<String, Object>> list;

    public PageRespVO total(Integer total) {
        this.total = total;
        return this;
    }

    public PageRespVO addList(List<Map<String, Object>> list) {
        this.list = list;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }
}
