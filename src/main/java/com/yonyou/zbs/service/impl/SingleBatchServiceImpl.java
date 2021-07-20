package com.yonyou.zbs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.zbs.common.PdfUtil;
import com.yonyou.zbs.consts.ZbsConsts;
import com.yonyou.zbs.dao.ZbsDAO;
import com.yonyou.zbs.service.BizService;
import com.yonyou.zbs.service.SingleBatchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SingleBatchServiceImpl implements SingleBatchService {
    private final static Logger logger = LoggerFactory.getLogger(SingleBatchServiceImpl.class);
    @Resource
    private ZbsDAO zbsDAO;
    @Resource
    private PdfUtil pdfUtil;
    @Resource
    private BizService bizService;

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
        Map<String, Object> map = zbsDAO.executeQueryMap("SELECT TOP 1 " + colSql + " FROM NccSteelTem t" + whereSql,
                paramList.toArray());
        Map<String, Object> temMap = null;
        List<Map<String, Object>> nurbsList;
        // 2.如果新表中有数据，则查询相关历史数据
        if (map != null) {
            temMap = map;
            nurbsList = zbsDAO.executeQueryList("select t.cElem,t.dValues from " +
                    "NccSteelTemNurbs t where t.temId=?", map.get("ID"));
        } else {
            // 3.从旧表中查询记录
            map = zbsDAO.executeQueryMap("SELECT TOP 1 t.cSign+t.cCertificateNO as temId," + colSql + " FROM SteelTem t " + whereSql,
                    paramList.toArray());
            // 4.如果旧表中有数据则查询相关历史数据
            if (map != null) {
                temMap = map;
                nurbsList = zbsDAO.executeQueryList("select  RTRIM(t.cElem) cElem,RTRIM(t.dValues) dValues from " +
                        "SteelTemNurbs t where t.cCertificateNO=?", map.get("temId"));
            } else {
                // 5.最终从相关数据表中查询数据
                nurbsList = zbsDAO.executeQueryList("select  RTRIM(t.cElem) cElem,RTRIM(t.dValues) dValues from" +
                        " SteelTemNurbs t where t.cCertificateNO=?", cMFNo);
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
        return zbsDAO.executeQueryList(sql, params.toArray());
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
        Map<String, Object> map = zbsDAO.executeQueryMap(sql, params.toArray());
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
        Map<String, Object> map = zbsDAO.executeQueryMap(sql, id);
        List<Map<String, Object>> nurbsList = null;
        if (map != null) {
            nurbsList = zbsDAO.executeQueryList("select t.cElem,t.dValues from NccSteelTemNurbs t where t.temId=?", map.get("ID"));
        }
        retMap.put("nurbs", nurbsList);
        retMap.put("tem", map);
        return retMap;
    }

    @Override
    public boolean delTemById(String id) {
        try {
            zbsDAO.executeUpdate("delete FROM NccSteelTem  where ID=?", id);
            zbsDAO.executeUpdate("delete from NccSteelTemNurbs where temId=?", id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getNextTemNum() {
        //Map<String, Object> map = tgBaseDAO.executeQueryMap("SELECT NEXT VALUE FOR temSeq", null, DbType.DB_ZBS);
        Map<String, Object> map = zbsDAO.executeQueryMap("Declare @NewSeqVal int;Exec @NewSeqVal =  P_GetNewSeqVal_SeqT_0101001;" +
                "select @NewSeqVal  as mySeq;");
        Integer seq = (Integer) map.get("mySeq");
        return String.format("%08d", seq);
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
    @SuppressWarnings("unchecked")
    public String genSinglePdf(String id) throws IOException {
        Map<String, Object> map = this.getTemById(id);
        List<Map<String, Object>> nurbsList = (List<Map<String, Object>>) map.get("nurbs");
        Map<String, Object> tem = (Map<String, Object>) map.get("tem");
        if (nurbsList != null) {
            nurbsList.forEach(item -> tem.put((String) item.get("cElem"), item.get("dValues")));
        }
        String iSteelType = String.valueOf(tem.get("iSteelType"));
        String dPiece = (String) tem.get("dPiece");
        if (StringUtils.isEmpty(dPiece) || Double.parseDouble(dPiece) <= 0) {
            dPiece = "";
        }
        String dWeight = (String) tem.get("dWeight");
        if (StringUtils.isEmpty(dWeight) || Double.parseDouble(dWeight) <= 0) {
            dWeight = "";
        }
        tem.put("dPiece", dPiece);
        tem.put("dWeight", dWeight);
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
        return pdfUtil.genSinglePdf(tem, certPrefix);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String viewSinglePdf(String id) {
        String sql = "SELECT t.ID,t.iSteelType,t.cCertificateNO FROM NccSteelTem t where t.ID=?";
        Map<String, Object> map = zbsDAO.executeQueryMap(sql, id);
        String iSteelType = String.valueOf(map.get("iSteelType"));
        String cCertificateNO = (String) map.get("cCertificateNO");
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
        return pdfUtil.getPdfUrl() + "/" + certPrefix + cCertificateNO + ".pdf";
    }


    private void saveCustomer(String cCusName) {
        try {
            Map<String, Object> map = zbsDAO.executeQueryMap("select ID FROM Customer where cCusName=?", cCusName);
            if (map == null) {
                zbsDAO.executeUpdate("insert into Customer (cCusCode,cCusName,cCusAbbName) VALUES(?,?,?)",
                        "NCC" + this.getNextTemNum(), cCusName, cCusName);
            }
        } catch (Exception e) {
            logger.error("保存客户信息错误:" + e.getMessage());
        }

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
            } else if (key.equalsIgnoreCase("cCertificateNo")) {
                valueList.add(bizService.getNextCertNo(ZbsConsts.ZBS_TYPE_M));
            } else {
                valueList.add(tem.get(key));
            }
            keyList.add(key);
            holderList.add("?");
        }
        String insertSql = "insert into NccSteelTem (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")";
        Integer id = zbsDAO.insert(insertSql, valueList.toArray());
        JSONObject nurbs = JSONObject.parseObject(nurbsJson);
        keySet = nurbs.keySet();
        keySet.forEach(key -> zbsDAO.insert("insert into NccSteelTemNurbs (temId,cElem,dValues) values (?,?,?)",
                id, key, nurbs.get(key)));
        this.saveCustomer(tem.getString("cCusName"));
        return id;
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
        valueList.add(id);
        zbsDAO.executeUpdate(updateSql, valueList.toArray());
        zbsDAO.executeUpdate("delete from NccSteelTemNurbs where temId=?", id);
        JSONObject nurbs = JSONObject.parseObject(nurbsJson);
        keySet = JSONObject.parseObject(nurbsJson).keySet();
        keySet.forEach(key -> zbsDAO.insert("insert into NccSteelTemNurbs (temId,cElem,dValues) values (?,?,?)",
                id, key, nurbs.get(key)));
        return Integer.parseInt(id);
    }

    private Integer getTemIdByCertificateNO(String cCertificateNO) {
        Map<String, Object> map = zbsDAO.executeQueryMap("select ID from NccsteelTem where cCertificateNO=?",
                cCertificateNO);
        if (map == null) {
            return 0;
        }
        return (Integer) map.get("ID");
    }
}
