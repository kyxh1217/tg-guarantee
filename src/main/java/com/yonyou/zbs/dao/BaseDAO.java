package com.yonyou.zbs.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class BaseDAO {
    private static final Logger logger = LoggerFactory.getLogger(BaseDAO.class);
    private final DecimalFormat formatter = new DecimalFormat("#.#########");

    /**
     * 查询数据List
     */
    public List<Map<String, Object>> executeQueryList(String sql, Object... params) {
        return getJdbcTemplate().query(sql, new RowMapper<Map<String, Object>>() {
            public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                Map<String, Object> mapOfColumnValues = this.createColumnMap(columnCount);

                for (int i = 1; i <= columnCount; ++i) {
                    String column = JdbcUtils.lookupColumnName(rsmd, i);
                    mapOfColumnValues.putIfAbsent(this.getColumnKey(column), this.getColumnValue(rs, i));
                }
                return mapOfColumnValues;
            }

            @SuppressWarnings("all")
            protected Map<String, Object> createColumnMap(int columnCount) {
                return new LinkedCaseInsensitiveMap(columnCount);
            }

            protected String getColumnKey(String columnName) {
                return columnName;
            }

            @Nullable
            protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
                if (rs.getObject(index) instanceof Double) {
                    return formatter.format(rs.getDouble(index));
                }
                return JdbcUtils.getResultSetValue(rs, index);
            }
        }, params);
    }

    /**
     * 查询数据List
     */
    public Map<String, Object> executeQueryMap(String sql, Object... params) {
        List<Map<String, Object>> list = executeQueryList(sql, params);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public <T> T queryForObject(String sql, Class<T> clz) {
        return getJdbcTemplate().queryForObject(sql, clz);
    }

    /**
     * 查询数据List
     */
    public Integer executeUpdate(String sql, Object... params) {
        return getJdbcTemplate().update(sql, new ArgumentPreparedStatementSetter(params));
    }

    public Integer insert(String sql, Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(params);
            pss.setValues(ps);
            return ps;
        };
        getJdbcTemplate().update(preparedStatementCreator, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    public abstract JdbcTemplate getJdbcTemplate();
}
