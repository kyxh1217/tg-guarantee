package com.yonyou.guarantee.dao;

import com.yonyou.guarantee.constants.DbType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
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


@Component
public class ZbsDAO {
    private static final Logger logger = LoggerFactory.getLogger(ZbsDAO.class);

    private final JdbcTemplate zbsJdbcTemplate;
    private final JdbcTemplate oaJdbcTemplate;
    private final DecimalFormat formatter = new DecimalFormat("#.#########");

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
    public List<Map<String, Object>> executeQueryList(String sql, Object[] params, DbType dbType) {
        return Objects.requireNonNull(getJdbcTemplate(dbType)).query(sql, params, new RowMapper<Map<String, Object>>() {
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
        });
    }

    /**
     * 查询数据List
     *
     * @param sql
     * @param params
     * @param dbType
     * @return
     */
    public Map<String, Object> executeQueryMap(String sql, Object[] params, DbType dbType) {
        List<Map<String, Object>> list = executeQueryList(sql, params, dbType);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 查询数据List
     *
     * @param sql
     * @param params
     * @param dbType
     * @return
     */
    public Integer executeUpdate(String sql, Object[] params, DbType dbType) {
        return Objects.requireNonNull(getJdbcTemplate(dbType)).update(sql, new ArgumentPreparedStatementSetter(params));
    }

    public Integer insert(String sql, Object[] params, DbType dbType) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatementSetter pss = new ArgumentPreparedStatementSetter(params);
            pss.setValues(ps);
            return ps;
        };
        Objects.requireNonNull(getJdbcTemplate(dbType)).update(preparedStatementCreator, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
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
