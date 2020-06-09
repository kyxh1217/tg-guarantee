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
    public List<Map<String, Object>> getFurnaceList(String searchText, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY ID DESC) n,ID,cMFNo,cStellGrade FROM SteelTem WHERE iType='0' ";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(searchText)) {
            innerSQL = innerSQL + " AND (cMFNo like ? or cStellGrade like ?)";
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }
        String sql = "SELECT t1.ID,t1.cMFNo,t1.cStellGrade " +
                " FROM SteelTem t1,(" + innerSQL + ") t2 " +
                " WHERE t2.n > ? and t1.ID=t2.ID ORDER BY t1.ID";
        params.add((currPage - 1) * pageSize);
        return tgBaseDAO.executeQuery(sql, params.toArray(), DbType.DB_ZBS);
    }

    @Override
    public Integer getFurnaceCount(String searchText) {
        String sql = "SELECT count(1) count FROM SteelTem WHERE iType='0'";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(searchText)) {
            sql = sql + " AND (cMFNo like ? or cStellGrade like ?)";
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }
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

    @Override
    public List<Map<String, Object>> getTemByType(String iSteelType, String custName, String startDate, String endDate, String searchText, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY ID DESC) n,ID FROM SteelTem WHERE iType='2' and iSteelType=?";
        List<Object> params = new ArrayList<>();
        params.add(iSteelType);
        if (!StringUtils.isEmpty(searchText)) {
            innerSQL = innerSQL + " AND (cMFNo like ? or cStellGrade like ? or cCertificateNO like ?)";
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }
        if (!StringUtils.isEmpty(startDate)) {
            innerSQL = innerSQL + " AND dDate>=?";
            params.add(startDate + " 00:00:00");
        }
        if (!StringUtils.isEmpty(endDate)) {
            innerSQL = innerSQL + " AND dDate<=?";
            params.add(endDate + " 23:59:59");
        }
        if (!StringUtils.isEmpty(custName)) {
            innerSQL = innerSQL + " AND cCusName=?";
            params.add(custName);
        }
        String sql = "SELECT t1.ID,t1.cCusName,t1.cCertificateNO,SUBSTRING(CONVERT(VARCHAR(10),t1.dDate,120),1,10) dDate,t1.cMFNo,t1.cStellGrade " +
                " FROM SteelTem t1,(" + innerSQL + ") t2 " +
                " WHERE t2.n > ? and t1.ID=t2.ID ORDER BY t1.ID";
        params.add((currPage - 1) * pageSize);
        return tgBaseDAO.executeQuery(sql, params.toArray(), DbType.DB_ZBS);
    }

    @Override
    public Integer getTemByTypeCount(String iSteelType, String custName, String startDate, String endDate, String searchText) {
        String sql = "SELECT count(1) count FROM SteelTem WHERE iType='2' and iSteelType=?";
        List<Object> params = new ArrayList<>();
        params.add(iSteelType);
        if (!StringUtils.isEmpty(searchText)) {
            sql = sql + " AND (cMFNo like ? or cStellGrade like ? or cCertificateNO like ?)";
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }
        if (!StringUtils.isEmpty(startDate)) {
            sql = sql + " AND dDate>=?";
            params.add(startDate + " 00:00:00");
        }
        if (!StringUtils.isEmpty(endDate)) {
            sql = sql + " AND dDate<=?";
            params.add(endDate + " 23:59:59");
        }
        if (!StringUtils.isEmpty(custName)) {
            sql = sql + " AND cCusName=?";
            params.add(custName);
        }
        List<Map<String, Object>> list = tgBaseDAO.executeQuery(sql, params.toArray(), DbType.DB_ZBS);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return (Integer) list.get(0).get("count");
    }

}
