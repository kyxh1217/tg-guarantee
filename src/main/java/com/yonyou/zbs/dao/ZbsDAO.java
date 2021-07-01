package com.yonyou.zbs.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class ZbsDAO extends BaseDAO {
    @Resource(name = "zbsJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
