package com.yonyou.guarantee.vo;


import java.util.List;
import java.util.Map;

public class PageRespVO {
    private int total;
    private List<Map<String, Object>> list;

    public static class Builder {
        private int total;
        private List<Map<String, Object>> list;

        public Builder total(Integer total) {
            this.total = total;
            return this;
        }

        public Builder addList(List<Map<String, Object>> list) {
            this.list = list;
            return this;
        }

        public PageRespVO create() {
            return new PageRespVO(this);
        }
    }

    private PageRespVO(Builder builder) {
        this.list = builder.list;
        this.total = builder.total;
    }

    public int getTotal() {
        return total;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

}
