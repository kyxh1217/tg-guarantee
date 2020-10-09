package com.yonyou.guarantee.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.guarantee.constants.DbType;
import com.yonyou.guarantee.dao.ZbsDAO;
import com.yonyou.guarantee.pdf.PdfUtils;
import com.yonyou.guarantee.service.TgZbsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TgZbsServiceImpl implements TgZbsService {
    @Resource
    private ZbsDAO tgBaseDAO;
    @Resource
    private PdfUtils pdfUtils;
    private final static Logger logger = LoggerFactory.getLogger(TgZbsServiceImpl.class);

    @Override
    public List<Map<String, Object>> getCustomerList(String custName, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY ID) n, ID FROM Customer";
        if (!StringUtils.isEmpty(custName)) {
            innerSQL = innerSQL + " where  cCusName like '%" + custName + "%'";
        }
        String sql = "SELECT w2.n,w1.ID,w1.cCusCode,rtrim(w1.cCusName) cCusName,rtrim(w1.cCusAbbName) cCusAbbName " +
                " FROM Customer w1,(" + innerSQL + ") w2 " +
                " WHERE w1.ID = w2.ID AND w2.n >  " + (currPage - 1) * pageSize + "  ORDER BY w1.ID";
        return tgBaseDAO.executeQueryList(sql, new Object[]{}, DbType.DB_ZBS);
    }

    @Override
    public Integer getCustomerCount(String custName) {
        String sql = "SELECT count(1) count FROM Customer w1";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(custName)) {
            params.add("%" + custName + "%");
            sql = sql + " WHERE w1.cCusName like ? ";
        }
        List<Map<String, Object>> list = tgBaseDAO.executeQueryList(sql, params.toArray(), DbType.DB_ZBS);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return (Integer) list.get(0).get("count");
    }

    @Override
    public List<Map<String, Object>> getStandardList() {
        // return tgBaseDAO.executeQueryList("select t.id,rtrim(t.cStand) cStand from Standard t where ISNULL(cStand,'')<>'' ORDER BY t.id", null, DbType.DB_ZBS);
        return tgBaseDAO.executeQueryList("select t.id,rtrim(t.cBaseName) cStand from baseinfo t where t.iType=0 ORDER BY t.ID", null, DbType.DB_ZBS);
    }

    @Override
    public List<Map<String, Object>> getSteelList() {
        return tgBaseDAO.executeQueryList("SELECT t.ID,rtrim(t.cStellGrade) cStellGrade FROM Steel t ORDER BY t.ID desc", null, DbType.DB_ZBS);
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
        return tgBaseDAO.executeQueryList(sql, params.toArray(), DbType.DB_ZBS);
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
        Map<String, Object> map = tgBaseDAO.executeQueryMap(sql, params.toArray(), DbType.DB_ZBS);
        if (map == null) {
            return 0;
        }
        return (Integer) map.get("count");
    }

    @Override
    public Map<String, Object> getTemHistory(String cMFNo, String cStellGrade, String cCusName, String iSteelType) {
        // 1.先从新表中查询记录
        String colSql = "t.ID,t.iType,t.cCusName,t.cCertificateNO,t.cStellGrade,t.cMFNo,t.cHTLot,t.cSpec,t.iSteelType," +
                " t.cSteelType,t.cMemo,SUBSTRING(CONVERT(VARCHAR(10),t.dDate,120),1,10) dDate,t.dPiece,t.dWeight,t.cCondition,t.cTStandard,t.cSpecifica," +
                " t.cCertalPoro,t.cPatternSegre,t.cDecarb,t.cAnnealed,t.cQuenched,t.cPreHardened,t.cEuectic,t.cSizeToler," +
                " t.cSurFace,t.cUltrasonic,t.cPlane,t.cSide,t.cMicrostru,t.cSegregation,t.cThickness,t.cWithd,t.cStraightness," +
                " t.cCarbide,t.cTensile,t.cShrinkage,t.cMacStructure,t.cGrainSize,t.cContacts,RTRIM(t.A_T) A_T,RTRIM(t.A_H) A_H," +
                " RTRIM(t.B_T) B_T,RTRIM(t.B_H) B_H,RTRIM(t.C_H) C_H,RTRIM(t.C_T) C_T," +
                " RTRIM(t.D_H) D_H,RTRIM(t.D_T) D_T,t.QRCode,t.cSampleNo,t.cOperator,t.cSign,t.bupload";
        String whereSql = " where t.iSteelType=? and t.cMFNo=?";
        List<String> paramList = new ArrayList<>();
        paramList.add(iSteelType);
        paramList.add(cMFNo);
        if (!StringUtils.isEmpty(cCusName)) {
            whereSql += " and t.cCusName=?";
            paramList.add(cCusName);
        }
        if (!StringUtils.isEmpty(cStellGrade)) {
            whereSql += " and t.cStellGrade=?";
            paramList.add(cStellGrade);
        }
        whereSql += " ORDER BY t.ID desc";
        Map<String, Object> map = tgBaseDAO.executeQueryMap("SELECT TOP 1 " + colSql + " FROM NccSteelTem t" + whereSql,
                paramList.toArray(), DbType.DB_ZBS);
        Map<String, Object> temMap = null;
        List<Map<String, Object>> nurbsList;
        // 2.如果新表中有数据，则查询相关历史数据
        if (map != null) {
            temMap = map;
            nurbsList = tgBaseDAO.executeQueryList("select t.cElem,t.dValues from " +
                    "NccSteelTemNurbs t where t.temId=?", new Object[]{map.get("ID")}, DbType.DB_ZBS);
        } else {
            // 3.从旧表中查询记录
            map = tgBaseDAO.executeQueryMap("SELECT TOP 1 t.cSign+t.cCertificateNO as temId," + colSql + " FROM SteelTem t " + whereSql,
                    paramList.toArray(), DbType.DB_ZBS);
            // 4.如果旧表中有数据则查询相关历史数据
            if (map != null) {
                temMap = map;
                nurbsList = tgBaseDAO.executeQueryList("select  RTRIM(t.cElem) cElem,RTRIM(t.dValues) dValues from " +
                        "SteelTemNurbs t where t.cCertificateNO=?", new Object[]{map.get("temId")}, DbType.DB_ZBS);
            } else {
                // 5.最终从相关数据表中查询数据
                nurbsList = tgBaseDAO.executeQueryList("select  RTRIM(t.cElem) cElem,RTRIM(t.dValues) dValues from" +
                        " SteelTemNurbs t where t.cCertificateNO=?", new Object[]{cMFNo}, DbType.DB_ZBS);
            }
        }
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("tem", temMap);
        retMap.put("nurbs", nurbsList);
        return retMap;
    }

    @Override
    public List<Map<String, Object>> getTemByType(String iSteelType, String custName, String startDate, String endDate, String searchText, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY ID DESC) n,ID FROM NccSteelTem WHERE iSteelType=?";
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
        String sql = "SELECT t1.ID,t1.cCusName,t1.cCertificateNO,SUBSTRING(CONVERT(VARCHAR(10),t1.dDate,120),1,10) dDate,t1.billDate,t1.cMFNo,t1.cStellGrade " +
                " FROM NccSteelTem t1,(" + innerSQL + ") t2 " +
                " WHERE t2.n > ? and t1.ID=t2.ID ORDER BY t1.ID desc";
        params.add((currPage - 1) * pageSize);
        return tgBaseDAO.executeQueryList(sql, params.toArray(), DbType.DB_ZBS);
    }

    @Override
    public Integer getTemByTypeCount(String iSteelType, String custName, String startDate, String endDate, String searchText) {
        String sql = "SELECT count(1) count FROM NccSteelTem WHERE  iSteelType=?";
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
        Map<String, Object> map = tgBaseDAO.executeQueryMap(sql, params.toArray(), DbType.DB_ZBS);
        if (map == null) {
            return 0;
        }
        return (Integer) map.get("count");
    }

    @Override
    public Map<String, Object> getTemById(String id) {
        Map<String, Object> retMap = new HashMap<>();
        String sql = "SELECT t.ID,t.iType,t.cCusName,t.cCertificateNO,t.cStellGrade,t.cMFNo,t.cHTLot,t.cSpec,t.iSteelType," +
                " t.cSteelType,t.cMemo,SUBSTRING(CONVERT(VARCHAR(10),t.dDate,120),1,10) dDate,t.dPiece,t.dWeight,t.cCondition,t.cTStandard,t.cSpecifica," +
                " t.cCertalPoro,t.cPatternSegre,t.cDecarb,t.cAnnealed,t.cQuenched,t.cPreHardened,t.cEuectic,t.cSizeToler," +
                " t.cSurFace,t.cUltrasonic,t.cPlane,t.cSide,t.cMicrostru,t.cSegregation,t.cThickness,t.cWithd,t.cStraightness," +
                " t.cCarbide,t.cTensile,t.cShrinkage,t.cMacStructure,t.cGrainSize,t.cContacts,RTRIM(t.A_T) A_T,RTRIM(t.A_H) A_H," +
                " RTRIM(t.B_T) B_T,RTRIM(t.B_H) B_H,RTRIM(t.C_H) C_H,RTRIM(t.C_T) C_T," +
                " RTRIM(t.D_H) D_H,RTRIM(t.D_T) D_T,t.QRCode,t.cSampleNo,t.cOperator,t.cSign,t.bupload,t.billDate" +
                " FROM NccSteelTem t where t.ID=?";
        Map<String, Object> map = tgBaseDAO.executeQueryMap(sql, new Object[]{id}, DbType.DB_ZBS);
        List<Map<String, Object>> nurbsList = null;
        if (map != null) {
            nurbsList = tgBaseDAO.executeQueryList("select t.cElem,t.dValues from " +
                    "NccSteelTemNurbs t where t.temId=?", new Object[]{map.get("ID")}, DbType.DB_ZBS);
        }
        retMap.put("nurbs", nurbsList);
        retMap.put("tem", map);
        return retMap;
    }

    @Override
    public boolean delTemById(String id) {
        try {
            tgBaseDAO.executeUpdate("delete FROM NccSteelTem  where ID=?", new Object[]{id}, DbType.DB_ZBS);
            tgBaseDAO.executeUpdate("delete from NccSteelTemNurbs where temId=?", new Object[]{id}, DbType.DB_ZBS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getNextTemNum() {
        //Map<String, Object> map = tgBaseDAO.executeQueryMap("SELECT NEXT VALUE FOR temSeq", null, DbType.DB_ZBS);
        Map<String, Object> map = tgBaseDAO.executeQueryMap("Declare @NewSeqVal int;Exec @NewSeqVal =  P_GetNewSeqVal_SeqT_0101001;" +
                "select @NewSeqVal  as mySeq;", null, DbType.DB_ZBS);
        Integer seq = (Integer) map.get("mySeq");
        return String.format("%08d", seq);
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
        if (!StringUtils.isEmpty(cMFNo)) {
            innerSQL = innerSQL + " AND  e.ganghao like ?";
            params.add("%" + cStellGrade + "%");
        }
        String sql = "SELECT *  FROM (" + innerSQL + ") t1 WHERE t1.n > ? ORDER BY t1.n";
        params.add((currPage - 1) * pageSize);
        return tgBaseDAO.executeQueryList(sql, params.toArray(), DbType.DB_OA);
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
        Map<String, Object> map = tgBaseDAO.executeQueryMap(sql, params.toArray(), DbType.DB_OA);
        if (map == null) {
            return 0;
        }
        return (Integer) map.get("count");
    }

    @Override
    @Transactional
    public int temSave(String temJson, String nurbsJson, String userName) {
        JSONObject tem = JSONObject.parseObject(temJson);
        String id = tem.getString("ID");
        if (StringUtils.isEmpty(id)) {
            return insertTem(temJson, nurbsJson, userName);
        } else {
            return updateTem(temJson, nurbsJson);
        }

    }

    @Override
    public List<Map<String, Object>> getElementLimits(String cMFNo) {
        return tgBaseDAO.executeQueryList("select RTRIM(t.cElem) cElem,RTRIM(t.dLLimit) dLLimit,RTRIM(t.dSLimit) dSLimit from Elemental t " +
                "where t.cStellNo=? ORDER BY iNo", new Object[]{cMFNo}, DbType.DB_ZBS);
    }

    @Override
    public List<Map<String, Object>> getBatchList(String searchText, String steelGrade, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY ID DESC) n,ID,cMFNo,cStellGrade FROM SteelTem WHERE iType='0' and cStellGrade=?";
        List<Object> params = new ArrayList<>();
        params.add(steelGrade);
        if (!StringUtils.isEmpty(searchText)) {
            innerSQL = innerSQL + " AND cMFNo like ?";
            params.add("%" + searchText + "%");
        }
        String sql = "SELECT t1.ID,t1.cMFNo,t1.cStellGrade " +
                " FROM SteelTem t1,(" + innerSQL + ") t2 " +
                " WHERE t2.n > ? and t1.ID=t2.ID ORDER BY t1.ID DESC";
        params.add((currPage - 1) * pageSize);
        return tgBaseDAO.executeQueryList(sql, params.toArray(), DbType.DB_ZBS);
    }

    @Override
    public Integer getBatchListCount(String searchText, String steelGrade) {
        String sql = "SELECT count(1) count FROM SteelTem WHERE iType='0' and cStellGrade=?";
        List<Object> params = new ArrayList<>();
        params.add(steelGrade);
        if (!StringUtils.isEmpty(searchText)) {
            sql = sql + " AND cMFNo like ?";
            params.add("%" + searchText + "%");
        }
        Map<String, Object> map = tgBaseDAO.executeQueryMap(sql, params.toArray(), DbType.DB_ZBS);
        if (map == null) {
            return 0;
        }
        return (Integer) map.get("count");
    }

    @Override
    @Transactional
    public int multiSave(String headJson, String bodyJson, String refJson, String userName) {
        JSONObject head = JSONObject.parseObject(headJson);
        String id = head.getString("ID");
        if (StringUtils.isEmpty(id)) {
            return insertBatch(headJson, bodyJson, refJson, userName);
        } else {
            return updateBatch(headJson, bodyJson, refJson);
        }
    }

    @Override
    public List<Map<String, Object>> getMultiByType(String iSteelType, String custName, String startDate, String endDate, String searchText, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY ID DESC) n,ID FROM NccMILLTest WHERE iSteelType=?";
        List<Object> params = new ArrayList<>();
        params.add(iSteelType);
        if (!StringUtils.isEmpty(custName)) {
            innerSQL = innerSQL + " AND cCustomer= ?";
            params.add(custName);
        }
        if (!StringUtils.isEmpty(startDate)) {
            innerSQL = innerSQL + " AND dDate>=?";
            params.add(startDate + " 00:00:00");
        }
        if (!StringUtils.isEmpty(endDate)) {
            innerSQL = innerSQL + " AND dDate<=?";
            params.add(endDate + " 23:59:59");
        }
        if (!StringUtils.isEmpty(searchText)) {
            innerSQL = innerSQL + " AND (cStellGrade like ? or cCertificateNo like ?)";
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }
        String sql = "SELECT t1.ID,t1.cCertificateNo,t1.cStellGrade,t1.cCustomer,SUBSTRING(CONVERT(VARCHAR(10),t1.dDate,120),1,10) dDate " +
                " FROM NccMILLTest t1,(" + innerSQL + ") t2 " +
                " WHERE t2.n > ? and t1.ID=t2.ID ORDER BY t1.ID DESC";
        params.add((currPage - 1) * pageSize);
        return tgBaseDAO.executeQueryList(sql, params.toArray(), DbType.DB_ZBS);
    }

    @Override
    public Integer getMultiByTypeCount(String iSteelType, String custName, String startDate, String endDate, String searchText) {
        String sql = "SELECT count(1) count FROM NccMILLTest WHERE iSteelType=?";
        List<Object> params = new ArrayList<>();
        params.add(iSteelType);
        if (!StringUtils.isEmpty(custName)) {
            sql = sql + " AND cCustomer= ?";
            params.add(custName);
        }
        if (!StringUtils.isEmpty(startDate)) {
            sql = sql + " AND dDate>=?";
            params.add(startDate + " 00:00:00");
        }
        if (!StringUtils.isEmpty(endDate)) {
            sql = sql + " AND dDate<=?";
            params.add(endDate + " 23:59:59");
        }
        if (!StringUtils.isEmpty(searchText)) {
            sql = sql + " AND (cStellGrade like ? or cCertificateNo like ?)";
            params.add("%" + searchText + "%");
            params.add("%" + searchText + "%");
        }
        Map<String, Object> map = tgBaseDAO.executeQueryMap(sql, params.toArray(), DbType.DB_ZBS);
        if (map == null) {
            return 0;
        }
        return (Integer) map.get("count");
    }

    @Override
    public Map<String, Object> getMultiById(String id) {
        Map<String, Object> headMap = tgBaseDAO.executeQueryMap("SELECT t.ID,t.cCertificateNo,t.cSign,t.cCustomer," +
                "SUBSTRING(CONVERT(VARCHAR(10),t.dDate,120),1,10) dDate,t.cStellGrade,t.cStellGradeW,t.cContractNO," +
                "t.cCERTIFICATE,t.cPROCESS,t.cDEFORMATION,t.cGSIZE,t.cDELIVERY,t.cULTRASONIC,t.cDECARBURIZATION,t.cQUALITY," +
                "t.iSteelType,t.cNOTES1,t.cNOTES2,t.cSPECIFICATION from NccMILLTest t where t.ID=?", new Object[]{id}, DbType.DB_ZBS);
        List<Map<String, Object>> batchList = tgBaseDAO.executeQueryList("SELECT t.i_id,t.millId,t.cCertificateNo," +
                "t.cHEATNO,t.cSIZES,t.cPCS,t.dWeight,t.cFields1,t.cFields2,t.cFields3,t.cFields4,t.cFields5,t.cFields6," +
                "t.cFields7,t.cFields8,t.cFields9,t.cFields10,t.cFields11,t.cANNEALLING,t.cHardness,t.A_T,t.A_H,t.B_H," +
                "t.B_T,t.C_H,t.C_T,t.D_H,t.C_T,t.D_H,t.D_T,t.cPorosity,t.cSegregation,t.cDistribution,t.cSize,t.cMICROSTRUCTURE," +
                "t.cMICROHOMOGENITY,t.iSteelType FROM NccMILLTestDetail t where t.millId=?", new Object[]{id}, DbType.DB_ZBS);
        Map<String, Object> refMap = tgBaseDAO.executeQueryMap("SELECT TOP 1 t.ID,t.millId,t.C,t.Si,t.Mn,t.P,t.S,t.W,t.Mo," +
                "t.Cr,t.V,t.Cu,t.Ni  FROM NccElemental t where t.millId=? order by t.ID desc", new Object[]{id}, DbType.DB_ZBS);
        Map<String, Object> map = new HashMap<>();
        map.put("head", headMap);
        map.put("ref", refMap);
        map.put("batchList", batchList);
        return map;
    }

    @Override
    public Map<String, Object> getBathHistory(String cMFNo, String cStellGrade, String cCusName, String iSteelType) {
        Map<String, Object> retMap = new HashMap<>();
        Map<String, Object> dataMap = tgBaseDAO.executeQueryMap("select top 1 t2.millId,t2.cCertificateNo,t2.cHEATNO," +
                " t2.cSIZES,t2.cPCS,t2.dWeight,t2.cFields1,t2.cFields2,t2.cFields3,t2.cFields4,t2.cFields5,t2.cFields6," +
                " t2.cFields7,t2.cFields8,t2.cFields9,t2.cFields10,t2.cFields11,t2.cANNEALLING,t2.cHardness,t2.A_T,t2.A_H," +
                " t2.B_H,t2.B_T,t2.C_H,t2.C_T,t2.D_H,t2.C_T,t2.D_H,t2.D_T,t2.cPorosity,t2.cSegregation,t2.cDistribution," +
                " t2.cSize,t2.cMICROSTRUCTURE,t2.cMICROHOMOGENITY,t2.iSteelType from NccMILLTest t1,NccMILLTestDetail t2 " +
                " where t1.ID=t2.millId and t1.cCustomer=? and t1.iSteelType=?" +
                " and t1.cStellGrade=? and t2.cHEATNO=? ORDER BY t1.ID desc", new Object[]{cCusName, iSteelType, cStellGrade, cMFNo}, DbType.DB_ZBS);
        if (dataMap == null) {
            List<Map<String, Object>> nurbsList = tgBaseDAO.executeQueryList("select  RTRIM(t.cElem) cElem,RTRIM(t.dValues) dValues from" +
                    " SteelTemNurbs t where t.cCertificateNO=?", new Object[]{cMFNo}, DbType.DB_ZBS);
            retMap.put("nurbsList", nurbsList);
        }
        retMap.put("dataMap", dataMap);
        return retMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String genSinglePdf(String id) throws IOException {
        Map<String, Object> map = this.getTemById(id);
        List<Map<String, Object>> nurbsList = (List<Map<String, Object>>) map.get("nurbs");
        Map<String, Object> tem = (Map<String, Object>) map.get("tem");
        if (nurbsList != null) {
            nurbsList.forEach(item -> tem.put((String) item.get("cElem"), item.get("dValues")));
        }
        String iSteelType = String.valueOf(tem.get("iSteelType"));
        tem.put("dPiece", tem.get("dPiece"));
        tem.put("dWeight",  tem.get("dWeight"));
        String certPrefix;
        switch (iSteelType) {
            case "1":
                certPrefix = "TGY";
                break;
            case "2":
                certPrefix = "TGB";
                break;
            case "3":
                certPrefix = "TGG";
                break;
            default:
                certPrefix = "";
        }
        return pdfUtils.genSinglePdf(tem, certPrefix);
    }

    private void saveCustomer(String cCusName) {
        try {
            Map<String, Object> map = tgBaseDAO.executeQueryMap("select ID FROM Customer where cCusName=?", new Object[]{cCusName}, DbType.DB_ZBS);
            if (map == null) {
                tgBaseDAO.executeUpdate("insert into Customer (cCusCode,cCusName,cCusAbbName) VALUES(?,?,?)", new Object[]{
                        "NCC" + this.getNextTemNum(), cCusName, cCusName
                }, DbType.DB_ZBS);
            }
        } catch (Exception e) {
            logger.error("保存客户信息错误:" + e.getMessage());
        }

    }

    private String doubleToString(Double d) {
        if (null == d || d == 0) {
            return "";
        } else {
            String dStr = String.valueOf(d);
            if (dStr.endsWith(".0")) {
                dStr = dStr.substring(0, dStr.length() - 2);
            }
            return dStr;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String genMultiPdf(String id) throws IOException {
        Map<String, Object> map = this.getMultiById(id);
        Map<String, Object> head = (Map<String, Object>) map.get("head");
        Map<String, Object> ref = (Map<String, Object>) map.get("ref");
        List<Map<String, Object>> batchList = (List<Map<String, Object>>) map.get("batchList");
        return pdfUtils.genMultiPdf(head, ref, batchList);
    }

    @Override
    public Map<String, Object> getUserByName(String userName) {
        return tgBaseDAO.executeQueryMap("SELECT t.cUserID,t.cUserName,t.nccPassword FROM  US_User t where t.cUserID=?",
                new Object[]{userName}, DbType.DB_ZBS);
    }

    private int insertTem(String temJson, String nurbsJson, String userName) {
        JSONObject tem = JSONObject.parseObject(temJson);
        Set<String> keySet = tem.keySet();
        List<String> keyList = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();
        List<String> holderList = new ArrayList<>();
        for (String key : keySet) {
            if (key.equalsIgnoreCase("ID")) {
                continue;
            }
            if (key.equalsIgnoreCase("cOperator")) {
                valueList.add(userName);
            } else {
                valueList.add(tem.get(key));
            }
            keyList.add(key);
            holderList.add("?");
        }
        String insertSql = "insert into NccSteelTem (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")";
        Integer id = tgBaseDAO.insert(insertSql, valueList.toArray(), DbType.DB_ZBS);
        JSONObject nurbs = JSONObject.parseObject(nurbsJson);
        keySet = nurbs.keySet();
        keySet.forEach(key -> tgBaseDAO.insert("insert into NccSteelTemNurbs (temId,cElem,dValues) values (?,?,?)",
                new Object[]{id, key, nurbs.get(key)}, DbType.DB_ZBS));
        this.saveCustomer(tem.getString("cCusName"));
        return id;
    }

    private int insertBatch(String headForm, String bodyJson, String refJson, String userName) {
        JSONObject head = JSONObject.parseObject(headForm);
        Set<String> keySet = head.keySet();
        List<String> keyList = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();
        List<String> holderList = new ArrayList<>();
        String cCertificateNO = this.getNextTemNum();
        for (String key : keySet) {
            if (key.equalsIgnoreCase("ID") || key.equalsIgnoreCase("dDate")) {
                continue;
            }
            if (key.equalsIgnoreCase("cCertificateNo")) {
                valueList.add(cCertificateNO);
            } else if (key.equalsIgnoreCase("cOperator")) {
                valueList.add(userName);
            } else {
                valueList.add(head.get(key));
            }
            keyList.add(key);
            holderList.add("?");
        }
        Integer millId = tgBaseDAO.insert("insert into NccMILLTest (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")", valueList.toArray(), DbType.DB_ZBS);
        List<JSONObject> bodyArray = JSONArray.parseArray(bodyJson, JSONObject.class);
        for (JSONObject body : bodyArray) {
            keySet = body.keySet();
            keyList = new ArrayList<>();
            valueList = new ArrayList<>();
            holderList = new ArrayList<>();
            for (String key : keySet) {
                if (key.equalsIgnoreCase("ID") || key.equalsIgnoreCase("i_id")) {
                    continue;
                }
                if (key.equalsIgnoreCase("millId")) {
                    valueList.add(millId);
                } else {
                    valueList.add(body.get(key));
                }
                keyList.add(key);
                holderList.add("?");
            }
            tgBaseDAO.insert("insert into NccMILLTestDetail (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")",
                    valueList.toArray(), DbType.DB_ZBS);
        }
        if (!StringUtils.isEmpty(refJson)) {
            JSONObject ref = JSONObject.parseObject(refJson);
            keySet = ref.keySet();
            keyList = new ArrayList<>();
            valueList = new ArrayList<>();
            holderList = new ArrayList<>();
            for (String key : keySet) {
                if (key.equalsIgnoreCase("ID")) {
                    continue;
                }
                valueList.add(ref.get(key));
                keyList.add(key);
                holderList.add("?");
            }
            valueList.add(millId);
            keyList.add("millId");
            holderList.add("?");
            tgBaseDAO.insert("insert into NccElemental (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")",
                    valueList.toArray(), DbType.DB_ZBS);
        }
        return 0;
    }

    private int updateBatch(String headForm, String bodyJson, String refJson) {
        JSONObject head = JSONObject.parseObject(headForm);
        String millId = head.getString("ID");
        Set<String> keySet = head.keySet();
        List<String> keyList = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();
        for (String key : keySet) {
            if (key.equalsIgnoreCase("ID") || key.equalsIgnoreCase("dDate") || key.equalsIgnoreCase("cCertificateNo")) {
                continue;
            }
            valueList.add(head.get(key));
            keyList.add(key);
        }
        String updateSql = "update NccMILLTest set " + String.join("=?,", keyList) + "=? where ID=?";
        valueList.add(millId);
        tgBaseDAO.executeUpdate(updateSql, valueList.toArray(), DbType.DB_ZBS);
        tgBaseDAO.executeUpdate("delete from NccMILLTestDetail where millId=?", new Object[]{millId}, DbType.DB_ZBS);
        tgBaseDAO.executeUpdate("delete from NccElemental where millId=?", new Object[]{millId}, DbType.DB_ZBS);
        List<String> holderList;
        List<JSONObject> bodyArray = JSONArray.parseArray(bodyJson, JSONObject.class);
        for (JSONObject body : bodyArray) {
            keySet = body.keySet();
            keyList = new ArrayList<>();
            valueList = new ArrayList<>();
            holderList = new ArrayList<>();
            for (String key : keySet) {
                if (key.equalsIgnoreCase("i_id")) {
                    continue;
                }
                if (key.equalsIgnoreCase("millId")) {
                    valueList.add(millId);
                } else {
                    valueList.add(body.get(key));
                }
                keyList.add(key);
                holderList.add("?");
            }
            tgBaseDAO.insert("insert into NccMILLTestDetail (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")",
                    valueList.toArray(), DbType.DB_ZBS);
        }
        if (!StringUtils.isEmpty(refJson)) {
            JSONObject ref = JSONObject.parseObject(refJson);
            keySet = ref.keySet();
            keyList = new ArrayList<>();
            valueList = new ArrayList<>();
            holderList = new ArrayList<>();
            for (String key : keySet) {
                if (key.equalsIgnoreCase("ID") || key.equalsIgnoreCase("millId")) {
                    continue;
                }
                valueList.add(ref.get(key));
                keyList.add(key);
                holderList.add("?");
            }
            valueList.add(millId);
            keyList.add("millId");
            holderList.add("?");
            tgBaseDAO.insert("insert into NccElemental (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")",
                    valueList.toArray(), DbType.DB_ZBS);
        }
        return 0;
    }

    private int updateTem(String temJson, String nurbsJson) {
        JSONObject tem = JSONObject.parseObject(temJson);
        String id = tem.getString("ID");
        Set<String> keySet = tem.keySet();
        List<String> keyList = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();
        for (String key : keySet) {
            if (key.equalsIgnoreCase("ID") || key.equalsIgnoreCase("cCertificateNO") || key.equalsIgnoreCase("dDate")) {
                continue;
            }
            keyList.add(key);
            valueList.add(tem.get(key));
        }
        String updateSql = "update NccSteelTem set " + String.join("=?,", keyList) + "=? where ID=?";
        valueList.add(tem.getString("ID"));
        tgBaseDAO.executeUpdate(updateSql, valueList.toArray(), DbType.DB_ZBS);
        tgBaseDAO.executeUpdate("delete from NccSteelTemNurbs where temId=?", new Object[]{id}, DbType.DB_ZBS);
        JSONObject nurbs = JSONObject.parseObject(nurbsJson);
        keySet = JSONObject.parseObject(nurbsJson).keySet();
        keySet.forEach(key -> tgBaseDAO.insert("insert into NccSteelTemNurbs (temId,cElem,dValues) values (?,?,?)",
                new Object[]{id, key, nurbs.get(key)}, DbType.DB_ZBS));
        return Integer.parseInt(id);
    }

    private Integer getTemIdByCertificateNO(String cCertificateNO) {
        Map<String, Object> map = tgBaseDAO.executeQueryMap("select ID from NccsteelTem where cCertificateNO=?",
                new Object[]{cCertificateNO}, DbType.DB_ZBS);
        if (map == null) {
            return 0;
        }
        return (Integer) map.get("ID");
    }


    public static void main(String[] args) {
        System.out.println(new DecimalFormat("#.#######").format(0000.00));
    }
}
