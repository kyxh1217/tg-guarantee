package com.yonyou.guarantee.service.impl;

import com.yonyou.guarantee.constants.DbType;
import com.yonyou.guarantee.dao.ZbsDAO;
import com.yonyou.guarantee.service.TgZbsService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TgZbsServiceImpl implements TgZbsService {
    @Resource
    private ZbsDAO tgBaseDAO;

    @Override
    public List<Map<String, Object>> getCustomerList(String custName, int currPage, int pageSize) {

        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY ID) n, ID FROM Customer";
        if (!StringUtils.isEmpty(custName)) {
            innerSQL = innerSQL + " where  cCusName like '%" + custName + "%'";
        }
        String sql = "SELECT w2.n,w1.ID,w1.cCusCode,rtrim(w1.cCusName) cCusName,rtrim(w1.cCusAbbName) cCusAbbName " +
                " FROM Customer w1,(" + innerSQL + ") w2 " +
                " WHERE w1.ID = w2.ID AND w2.n >  " + (currPage - 1) * pageSize + "  ORDER BY w1.ID";
        return tgBaseDAO.executeQuery(sql, new Object[]{}, DbType.DB_ZBS);
    }

    @Override
    public Integer getCustomerCount(String custName) {
        String sql = "SELECT count(1) count FROM Customer w1";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(custName)) {
            params.add("%" + custName + "%");
            sql = sql + " WHERE w1.cCusName like ? ";
        }
        List<Map<String, Object>> list = tgBaseDAO.executeQuery(sql, params.toArray(), DbType.DB_ZBS);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return (Integer) list.get(0).get("count");
    }

    @Override
    public List<Map<String, Object>> getStandardList() {
        return tgBaseDAO.executeQuery("select t.id,rtrim(t.cStand) cStand from Standard t ORDER BY t.id", null, DbType.DB_ZBS);
    }

    @Override
    public List<Map<String, Object>> getSteelList() {
        return tgBaseDAO.executeQuery("SELECT t.ID,rtrim(t.cStellGrade) cStellGrade FROM Steel t ORDER BY t.cStellGrade", null, DbType.DB_ZBS);
    }
}
