package com.yonyou.guarantee.dao;

import com.yonyou.guarantee.constants.DbType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Component
public class ZbsDAO {
    private static final Logger logger = LoggerFactory.getLogger(ZbsDAO.class);

    private final JdbcTemplate zbsJdbcTemplate;
    private final JdbcTemplate oaJdbcTemplate;

    public ZbsDAO(@Qualifier("zbsJdbcTemplate") JdbcTemplate zbsJdbcTemplate, @Qualifier("oaJdbcTemplate") JdbcTemplate oaJdbcTemplate) {
        this.zbsJdbcTemplate = zbsJdbcTemplate;
        this.oaJdbcTemplate = oaJdbcTemplate;
    }

    /**
     * 查询数据List
     *
     * @param sql
     * @param params
     * @param dbType
     * @return
     */
    public List<Map<String, Object>> executeQuery(String sql, Object[] params, DbType dbType) {
        return Objects.requireNonNull(getJdbcTemplate(dbType)).queryForList(sql, params);
    }

    private JdbcTemplate getJdbcTemplate(DbType dbType) {
        if (dbType.equals(DbType.DB_ZBS)) {
            return zbsJdbcTemplate;
        } else if (dbType.equals(DbType.DB_OA)) {
            return oaJdbcTemplate;
        }
        return null;
    }
}
