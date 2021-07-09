package com.yonyou.zbs.service.impl;

import com.yonyou.zbs.dao.OaDAO;
import com.yonyou.zbs.dao.ZbsDAO;
import com.yonyou.zbs.service.BizService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BizServiceImpl implements BizService {
    private final static Logger logger = LoggerFactory.getLogger(BizServiceImpl.class);
    @Resource
    private ZbsDAO zbsDAO;
    @Resource
    private OaDAO oaDAO;

    @Override
    public List<Map<String, Object>> getCustomerList(String custName, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY ID) n, ID FROM Customer";
        if (!StringUtils.isEmpty(custName)) {
            innerSQL = innerSQL + " where  cCusName like '%" + custName + "%'";
        }
        String sql = "SELECT w2.n,w1.ID,w1.cCusCode,rtrim(w1.cCusName) cCusName,rtrim(w1.cCusAbbName) cCusAbbName " +
                " FROM Customer w1,(" + innerSQL + ") w2 " +
                " WHERE w1.ID = w2.ID AND w2.n >  " + (currPage - 1) * pageSize + "  ORDER BY w1.ID";
        return zbsDAO.executeQueryList(sql);
    }

    @Override
    public Integer getCustomerCount(String custName) {
        String sql = "SELECT count(1) count FROM Customer w1";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(custName)) {
            params.add("%" + custName + "%");
            sql = sql + " WHERE w1.cCusName like ? ";
        }
        List<Map<String, Object>> list = zbsDAO.executeQueryList(sql, params.toArray());
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return (Integer) list.get(0).get("count");
    }

    @Override
    public List<Map<String, Object>> getStandardList() {
        // return tgBaseDAO.executeQueryList("select t.id,rtrim(t.cStand) cStand from Standard t where ISNULL(cStand,'')<>'' ORDER BY t.id", null, DbType.DB_ZBS);
        return zbsDAO.executeQueryList("select t.id,rtrim(t.cBaseName) cStand from baseinfo t where t.iType=0 ORDER BY t.ID");
    }

    @Override
    public List<Map<String, Object>> getSteelList() {
        return zbsDAO.executeQueryList("SELECT t.ID,rtrim(t.cStellGrade) cStellGrade FROM Steel t ORDER BY t.ID desc");
    }

    @Override
    public List<Map<String, Object>> getFurnaceList(String searchText, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY t1.cMFNo,t1.cStellGrade) n,t1.cMFNo,t1.cStellGrade " +
                " FROM (SELECT cMFNo, cStellGrade FROM SteelTem WHERE iType = '0' UNION SELECT cMFNo, cStellGrade FROM NccSteelTem WHERE iType = '0') t1 where 1=1 ";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(searchText)) {
            innerSQL = innerSQL + " AND (t1.cMFNo like ? or t1.cStellGrade like ?)";
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }
        String sql = "SELECT t2.n,t2.cStellGrade,t2.cMFNo  FROM (" + innerSQL + ") t2 WHERE t2.n > ? ORDER BY t2.n DESC";
        params.add((currPage - 1) * pageSize);
        return zbsDAO.executeQueryList(sql, params.toArray());
    }

    @Override
    public Integer getFurnaceCount(String searchText) {
        String innerSQL = "SELECT t1.cMFNo,t1.cStellGrade " +
                " FROM (SELECT cMFNo, cStellGrade FROM SteelTem WHERE iType = '0' UNION SELECT cMFNo, cStellGrade FROM NccSteelTem WHERE iType = '0') t1 where 1=1";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(searchText)) {
            innerSQL = innerSQL + " AND (t1.cMFNo like ? or t1.cStellGrade like ?)";
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }
        String sql = "SELECT count(1) count FROM (" + innerSQL + ") t2";
        Map<String, Object> map = zbsDAO.executeQueryMap(sql, params.toArray());
        if (map == null) {
            return 0;
        }
        return (Integer) map.get("count");
    }

    @Override
    public synchronized String getNextCertNo(String certType) {
        String sql = "SELECT ISNULL(max(cCertificateNO), 0)+1 FROM ";
        if ("M".equalsIgnoreCase(certType)) {
            sql = sql + "NccMILLTest";
        } else {
            sql = sql + "NccSteelTem";
        }
        return String.format("%08d", zbsDAO.queryForObject(sql, Integer.class));
    }

    @Override
    public List<Map<String, Object>> getQtList(String cMFNo, String cStellGrade, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY a.requestid DESC) n,a.requestid, d.status, dep.departmentname, HR.LASTNAME wuhuaName, isnull( HR1.lastname, '' ) qtName, lphn AS cMFNo, " +
                "e.ganghao cStellGrade, gg AS cSpec, zxss AS cCertalPoro, dxpx AS cPatternSegre, at AS A_T, ah AS A_H, bt AS B_T, " +
                " bh AS B_H, ct AS C_T, ch AS C_H, dt AS D_T, dh AS D_H, ttc AS cDecarb, thyd AS cAnnealed, gjthbjyd AS cEuectic,  " +
                " thwkld AS cCarbide, xwzz AS cMicrostru, xwpx AS cSegregation, cc AS cSizeToler, hdpc AS cThickness, kdpc AS cWithd,  " +
                " jld AS cGrainSize, csbts AS cUltrasonic, lhjyrq AS lhjyrq, cjzjrq AS qtDate, bmzl AS cSurFace, chhhyd AS cQuenched,  " +
                " pp  FROM formtable_main_72 A LEFT JOIN hrmresource HR ON A.lhjyy= HR.ID LEFT JOIN hrmresource HR1 " +
                " ON a.cjjyy= HR1.ID LEFT JOIN hrmresource HR2 ON a.cjgjyy = HR1.ID LEFT JOIN hrmdepartment dep ON a.sjbm= dep.id LEFT JOIN workflow_requestbase d ON a.requestid = d.requestid LEFT JOIN uf_gh e ON a.xgh= e.id " +
                " WHERE d.currentnodetype <> 0 ";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(cMFNo)) {
            innerSQL = innerSQL + " AND lphn like ? ";
            params.add("%" + cMFNo + "%");
        }
        if (!StringUtils.isEmpty(cStellGrade)) {
            innerSQL = innerSQL + " AND  e.ganghao like ?";
            params.add("%" + cStellGrade + "%");
        }
        String sql = "SELECT *  FROM (" + innerSQL + ") t1 WHERE t1.n > ? ORDER BY t1.n";
        params.add((currPage - 1) * pageSize);
        return oaDAO.executeQueryList(sql, params.toArray());
    }

    @Override
    public Integer getQtListCount(String cMFNo, String cStellGrade) {
        String sql = "SELECT count(1) count FROM formtable_main_72 A LEFT JOIN hrmresource HR ON A.lhjyy= HR.ID LEFT JOIN hrmresource HR1 " +
                " ON a.cjjyy= HR1.ID LEFT JOIN hrmresource HR2 ON a.cjgjyy = HR1.ID LEFT JOIN hrmdepartment dep ON a.sjbm= dep.id" +
                " LEFT JOIN workflow_requestbase d ON a.requestid = d.requestid LEFT JOIN uf_gh e ON a.xgh= e.id " +
                " WHERE d.currentnodetype <> 0 ";

        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(cMFNo)) {
            sql = sql + " AND lphn like ? ";
            params.add("%" + cMFNo + "%");
        }
        if (!StringUtils.isEmpty(cMFNo)) {
            sql = sql + " AND  e.ganghao like ?";
            params.add("%" + cStellGrade + "%");
        }
        Map<String, Object> map = oaDAO.executeQueryMap(sql, params.toArray());
        if (map == null) {
            return 0;
        }
        return (Integer) map.get("count");
    }


    @Override
    public List<Map<String, Object>> getElementLimits(String cMFNo) {
        return zbsDAO.executeQueryList("select RTRIM(t.cElem) cElem,RTRIM(t.dLLimit) dLLimit,RTRIM(t.dSLimit) dSLimit from Elemental t " +
                "where t.cStellNo=? ORDER BY iNo", cMFNo);
    }

    @Override
    public Map<String, Object> getUserByName(String userName) {
        return zbsDAO.executeQueryMap("SELECT t.cUserID,t.cUserName,t.nccPassword FROM  US_User t where t.cUserID=?",
                userName);
    }
}