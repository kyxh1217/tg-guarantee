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
        return tgBaseDAO.executeQuery("select t.id,rtrim(t.cStand) cStand from Standard t where ISNULL(cStand,'')<>'' ORDER BY t.id", null, DbType.DB_ZBS);
    }

    @Override
    public List<Map<String, Object>> getSteelList() {
        return tgBaseDAO.executeQuery("SELECT t.ID,rtrim(t.cStellGrade) cStellGrade FROM Steel t ORDER BY t.ID desc", null, DbType.DB_ZBS);
    }

    @Override
    public List<Map<String, Object>> getFurnaceList(String furnaceNum, String startDate, String endDate, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " t1.cCertificateNO, t1.cdate, row_number() OVER (ORDER BY cdate DESC) n  FROM (" +
                " SELECT DISTINCT (t.cCertificateNO) cCertificateNO, SUBSTRING (CONVERT(CHAR(20), MAX (t.cdate),120),1,19) " +
                " AS cdate FROM SteelTemNurbs t  WHERE NULLIF (t.cdate, '') <> ''";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(startDate)) {
            innerSQL = innerSQL + " AND t.cdate>= ?";
            params.add(startDate + " 00:00:00");
        }
        if (!StringUtils.isEmpty(endDate)) {
            innerSQL = innerSQL + " AND t.cdate<= ?";
            params.add(endDate + " 23:59:59");
        }
        if (!StringUtils.isEmpty(furnaceNum)) {
            innerSQL = innerSQL + " and t.cCertificateNO like ?";
            params.add("%" + furnaceNum + "%");
        }
        innerSQL += " GROUP BY t.cCertificateNO) t1";
        String sql = "SELECT t2.cCertificateNO,t2.cdate " +
                " FROM (" + innerSQL + ") t2 " +
                " WHERE t2.n >  ?";
        params.add((currPage - 1) * pageSize);
        return tgBaseDAO.executeQuery(sql, params.toArray(), DbType.DB_ZBS);
    }

    @Override
    public Integer getFurnaceCount(String furnaceNum, String startDate, String endDate) {
        String sql = "SELECT count(1) count FROM (" +
                " SELECT DISTINCT (t.cCertificateNO) cCertificateNO, SUBSTRING (CONVERT(CHAR(20), MAX (t.cdate),120),1,19) " +
                " AS cdate FROM SteelTemNurbs t  WHERE NULLIF (t.cdate, '') <> ''";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(startDate)) {
            sql = sql + " AND t.cdate>= ?";
            params.add(startDate + " 00:00:00");
        }
        if (!StringUtils.isEmpty(endDate)) {
            sql = sql + " AND t.cdate<= ?";
            params.add(endDate + " 23:59:59");
        }
        if (!StringUtils.isEmpty(furnaceNum)) {
            sql = sql + " and t.cCertificateNO like ?";
            params.add("%" + furnaceNum + "%");
        }
        sql += " GROUP BY t.cCertificateNO) t1";
        List<Map<String, Object>> list = tgBaseDAO.executeQuery(sql, params.toArray(), DbType.DB_ZBS);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return (Integer) list.get(0).get("count");
    }

    @Override
    public List<Map<String, Object>> getChemicalsByFurnace(String furnaceNum) {
        return tgBaseDAO.executeQuery("select  RTRIM(t.cElem) cElem,RTRIM(t.dValues) dValues from SteelTemNurbs t where t.cCertificateNO=?", new Object[]{furnaceNum}, DbType.DB_ZBS);
    }

}
