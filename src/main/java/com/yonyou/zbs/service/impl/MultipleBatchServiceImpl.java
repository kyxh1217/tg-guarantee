package com.yonyou.zbs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.zbs.common.PdfUtil;
import com.yonyou.zbs.common.PoiUtil;
import com.yonyou.zbs.consts.ZbsConsts;
import com.yonyou.zbs.dao.ZbsDAO;
import com.yonyou.zbs.service.BizService;
import com.yonyou.zbs.service.MultipleBatchService;
import com.yonyou.zbs.util.PdfFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MultipleBatchServiceImpl implements MultipleBatchService {
    private final static Logger logger = LoggerFactory.getLogger(MultipleBatchServiceImpl.class);
    @Resource
    private ZbsDAO zbsDAO;
    @Resource
    private PdfUtil pdfUtil;
    @Resource
    private BizService bizService;

    @Override
    public List<Map<String, Object>> getBatchList(String searchText, String steelGrade, int currPage, int pageSize) {
        String innerSQL = "SELECT TOP " + (currPage * pageSize) + " row_number() OVER (ORDER BY t3.cStellGrade,t3.cHEATNO) n, t3.cStellGrade,t3.cHEATNO from (" +
                " SELECT t1.cStellGrade,t2.cHEATNO FROM MILLTest t1,MILLTestDetail t2 WHERE t1.cCertificateNo=t2.cCertificateNo AND isnull(t2.cHEATNO,'')<>'' " +
                " UNION " +
                " SELECT t1.cStellGrade,t2.cHEATNO FROM NccMILLTest t1,NccMILLTestDetail t2 WHERE t1.cCertificateNo=t2.cCertificateNo AND isnull(t2.cHEATNO,'')<>''" +
                ") as t3 where 1=1 ";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(steelGrade)) {
            innerSQL += " AND t3.cStellGrade=? ";
            params.add(steelGrade);
        }
        if (!StringUtils.isEmpty(searchText)) {
            if (!StringUtils.isEmpty(steelGrade)) {
                innerSQL = innerSQL + " AND t3.cHEATNO like ? ";
            } else {
                innerSQL = innerSQL + " AND (t3.cHEATNO like ? or t3.cStellGrade like ?)";
                params.add("%" + searchText + "%");
            }
            params.add("%" + searchText + "%");
        }
        String sql = "SELECT t4.n,t4.cStellGrade,t4.cHEATNO  FROM (" + innerSQL + ") t4 WHERE t4.n > ? ORDER BY t4.n DESC";
        params.add((currPage - 1) * pageSize);
        return zbsDAO.executeQueryList(sql, params.toArray());
    }

    @Override
    public Integer getBatchListCount(String searchText, String steelGrade) {
        String innerSQL = "SELECT t3.cStellGrade,t3.cHEATNO from (" +
                " SELECT t1.cStellGrade,t2.cHEATNO FROM MILLTest t1,MILLTestDetail t2 WHERE t1.cCertificateNo=t2.cCertificateNo AND isnull(t2.cHEATNO,'')<>'' " +
                " UNION " +
                " SELECT t1.cStellGrade,t2.cHEATNO FROM NccMILLTest t1,NccMILLTestDetail t2 WHERE t1.cCertificateNo=t2.cCertificateNo AND isnull(t2.cHEATNO,'')<>''" +
                ") as t3 where 1=1 ";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(steelGrade)) {
            innerSQL += " AND t3.cStellGrade=? ";
            params.add(steelGrade);
        }
        if (!StringUtils.isEmpty(searchText)) {
            if (!StringUtils.isEmpty(steelGrade)) {
                innerSQL = innerSQL + " AND t3.cHEATNO like ? ";
            } else {
                innerSQL = innerSQL + " AND (t3.cHEATNO like ? or t3.cStellGrade like ?)";
                params.add("%" + searchText + "%");
            }
            params.add("%" + searchText + "%");
        }
        String sql = "SELECT count(1) count FROM (" + innerSQL + ") t4";
        Map<String, Object> map = zbsDAO.executeQueryMap(sql, params.toArray());
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
        return zbsDAO.executeQueryList(sql, params.toArray());
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
        Map<String, Object> map = zbsDAO.executeQueryMap(sql, params.toArray());
        if (map == null) {
            return 0;
        }
        return (Integer) map.get("count");
    }

    @Override
    public Map<String, Object> getMultiById(String id) {
        Map<String, Object> headMap = zbsDAO.executeQueryMap("SELECT t.ID,t.cCertificateNo,t.cSign,t.cCustomer," +
                "SUBSTRING(CONVERT(VARCHAR(10),t.dDate,120),1,10) dDate,t.cStellGrade,t.cStellGradeW,t.cContractNO," +
                "t.cCERTIFICATE,t.cPROCESS,t.cDEFORMATION,t.cGSIZE,t.cDELIVERY,t.cULTRASONIC,t.cDECARBURIZATION,t.cQUALITY," +
                "t.iSteelType,t.cNOTES1,t.cNOTES2,t.cSPECIFICATION from NccMILLTest t where t.ID=?", id);
        List<Map<String, Object>> batchList = zbsDAO.executeQueryList("SELECT t.i_id,t.millId,t.cCertificateNo," +
                "t.cHEATNO,t.cSIZES,t.cPCS,t.dWeight,t.cFields1,t.cFields2,t.cFields3,t.cFields4,t.cFields5,t.cFields6," +
                "t.cFields7,t.cFields8,t.cFields9,t.cFields10,t.cFields11,t.cANNEALLING,t.cHardness,t.A_T,t.A_H,t.B_H," +
                "t.B_T,t.C_H,t.C_T,t.D_H,t.C_T,t.D_H,t.D_T,t.cPorosity,t.cSegregation,t.cDistribution,t.cSize,t.cMICROSTRUCTURE," +
                "t.cMICROHOMOGENITY,t.iSteelType FROM NccMILLTestDetail t where t.millId=?", id);
        Map<String, Object> refMap = zbsDAO.executeQueryMap("SELECT TOP 1 t.ID,t.millId,t.C,t.Si,t.Mn,t.P,t.S,t.W,t.Mo," +
                "t.Cr,t.V,t.Cu,t.Ni  FROM NccElemental t where t.millId=? order by t.ID desc", id);
        Map<String, Object> map = new HashMap<>();
        map.put("head", headMap);
        map.put("ref", refMap);
        map.put("batchList", batchList);
        return map;
    }

    @Override
    public Map<String, Object> getBathHistory(String cMFNo, String cStellGrade, String cCusName, String iSteelType) {
        Map<String, Object> retMap = new HashMap<>();
        Map<String, Object> dataMap = this.getHistoryMap(cMFNo, cStellGrade, cCusName, iSteelType);
        if (dataMap == null) {
            List<Map<String, Object>> nurbsList = zbsDAO.executeQueryList("select  RTRIM(t.cElem) cElem,RTRIM(t.dValues) dValues from" +
                    " SteelTemNurbs t where t.cCertificateNO=?", cMFNo);
            if (!CollectionUtils.isEmpty(nurbsList)) {
                retMap.put("nurbsList", nurbsList);
            }
        }
        retMap.put("dataMap", dataMap);
        return retMap;
    }

    private Map<String, Object> getHistoryMap(String cMFNo, String cStellGrade, String cCusName, String iSteelType) {
        String querySql = "select top 1 t2.cHEATNO," +
                " t2.cSIZES,t2.cPCS,t2.dWeight,t2.cFields1,t2.cFields2,t2.cFields3,t2.cFields4,t2.cFields5,t2.cFields6," +
                " t2.cFields7,t2.cFields8,t2.cFields9,t2.cFields10,t2.cFields11,rtrim(t2.cANNEALLING) cANNEALLING," +
                " rtrim(t2.cHardness) cHardness,rtrim(t2.A_T) A_T,rtrim(t2.A_H) A_H,rtrim(t2.B_H) B_H,rtrim(t2.B_T) B_T," +
                " rtrim(t2.C_H) C_H,rtrim(t2.C_T) C_T,rtrim(t2.D_H) D_H,rtrim(t2.C_T) C_T,rtrim(t2.D_H) D_H," +
                " rtrim(t2.D_T) D_T,rtrim(t2.cPorosity) cPorosity,rtrim(t2.cSegregation) cSegregation," +
                " rtrim(t2.cDistribution) cDistribution,rtrim( t2.cSize) cSize,rtrim(t2.cMICROSTRUCTURE) cMICROSTRUCTURE," +
                " rtrim(t2.cMICROHOMOGENITY) cMICROHOMOGENITY from ";
        String whereSQL = "";
        List<Object> params = new ArrayList<>();
        if (!StringUtils.isEmpty(iSteelType)) {
            whereSQL += " and t1.iSteelType=? ";
            params.add(iSteelType);
        }
        if (!StringUtils.isEmpty(cCusName)) {
            whereSQL += " and t1.cCustomer=? ";
            params.add(cCusName);
        }
        if (!StringUtils.isEmpty(cStellGrade)) {
            whereSQL += " and t1.cStellGrade=? ";
            params.add(cStellGrade);
        }
        if (!StringUtils.isEmpty(cMFNo)) {
            whereSQL += " and t2.cHEATNO=? ";
            params.add(cMFNo);
        }
        whereSQL += " ORDER BY t1.ID desc";
        Map<String, Object> dataMap = zbsDAO.executeQueryMap(querySql + " NccMILLTest t1,NccMILLTestDetail t2  where t1.ID=t2.millId  " + whereSQL, params.toArray());
        if (dataMap == null) {
            dataMap = zbsDAO.executeQueryMap(querySql + " MILLTest t1,MILLTestDetail t2 where t1.cCertificateNo=t2.cCertificateNo  " + whereSQL, params.toArray());
        }
        return dataMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String genMultiPdf(String id) throws IOException {
        Map<String, Object> map = this.getMultiById(id);
        Map<String, Object> head = (Map<String, Object>) map.get("head");
        Map<String, Object> ref = (Map<String, Object>) map.get("ref");
        List<Map<String, Object>> batchList = (List<Map<String, Object>>) map.get("batchList");
        return pdfUtil.genMultiPdf(head, ref, batchList);
    }

    @Override
    public String genPdfFreemarkerM(String id) throws Exception {
        PdfFile.template("zbs_m.ftl", this.getMultiById(id), "d:/pdf/m_" + id + ".pdf");
        return null;
    }

    @Override
    public List<Map<String, Object>> batchImport(MultipartFile file, String cStellGrade) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception("文件不能为空");
        }
        List<Map<String, String>> batchList = PoiUtil.read(file);
        if (CollectionUtils.isEmpty(batchList) || batchList.size() <= 3) {
            throw new Exception("excel中没有数据");
        }
        List<Map<String, Object>> retList = new ArrayList<>();
        Map<String, Map<String, Object>> hisCache = new HashMap<>();
        for (int i = 3; i < batchList.size(); i++) {
            Map<String, String> batchMap = batchList.get(i);
            String cHeatNo = batchMap.get("field0");
            Map<String, Object> hisMap;
            if (hisCache.containsKey(cHeatNo)) {
                hisMap = hisCache.get(cHeatNo);
            } else {
                hisMap = this.getHistoryMap(cHeatNo, cStellGrade, null, null);
                if (hisMap == null) {
                    List<Map<String, Object>> list = bizService.getQtList(cHeatNo, cStellGrade, 1, 1);
                    if (!CollectionUtils.isEmpty(list)) {
                        hisMap = new HashMap<>();
                        Map<String, Object> qtMap = list.get(0);
                        for (String s : ZbsConsts.M_NONMETALLIC) {
                            hisMap.put(s, qtMap.get(s));
                        }
                        hisMap.put("cPorosity", qtMap.get("cCertalPoro"));
                        hisMap.put("cSegregation", qtMap.get("cPatternSegre"));
                        hisMap.put("cANNEALLING", qtMap.get("cAnnealed"));
                        hisMap.put("cDistribution", qtMap.get("cQuenched"));
                        hisMap.put("cSize", qtMap.get("cSegregation"));
                    }
                }
            }
            hisCache.put(cHeatNo, hisMap);
            Map<String, Object> resultMap = new HashMap<>();
            if (CollectionUtils.isEmpty(hisMap)) {
                resultMap.put("cHEATNO", batchMap.get("field0"));
            } else {
                for (String key : hisMap.keySet()) {
                    resultMap.put(key, hisMap.get(key));
                }
            }
            resultMap.put("cSIZES", batchMap.get("field1"));
            resultMap.put("cPCS", batchMap.get("field2"));
            resultMap.put("dWeight", batchMap.get("field3"));
            retList.add(resultMap);
        }
        return retList;
    }

    private int insertBatch(String headForm, String bodyJson, String refJson, String userName) {
        JSONObject head = JSONObject.parseObject(headForm);
        List<String> keyList = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();
        List<String> holderList = new ArrayList<>();
        for (String key : ZbsConsts.M_HEAD_COLUMNS) {
            if (key.equalsIgnoreCase("ID") || key.equalsIgnoreCase("dDate")) {
                continue;
            }
            if (key.equalsIgnoreCase("cOperator")) {
                valueList.add(userName);
            } else {
                valueList.add(head.get(key));
            }
            keyList.add(key);
            holderList.add("?");
        }
        Integer millId = zbsDAO.insert("insert into NccMILLTest (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")", valueList.toArray());
        List<JSONObject> bodyArray = JSONArray.parseArray(bodyJson, JSONObject.class);
        for (JSONObject body : bodyArray) {
            keyList = new ArrayList<>();
            valueList = new ArrayList<>();
            holderList = new ArrayList<>();
            for (String key : ZbsConsts.M_BODY_COLUMNS) {
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
            zbsDAO.insert("insert into NccMILLTestDetail (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")",
                    valueList.toArray());
        }
        if (!StringUtils.isEmpty(refJson)) {
            JSONObject ref = JSONObject.parseObject(refJson);
            keyList = new ArrayList<>();
            valueList = new ArrayList<>();
            holderList = new ArrayList<>();
            for (String key : ZbsConsts.M_REF_COLUMNS) {
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
            zbsDAO.insert("insert into NccElemental (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")",
                    valueList.toArray());
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
        zbsDAO.executeUpdate(updateSql, valueList.toArray());
        zbsDAO.executeUpdate("delete from NccMILLTestDetail where millId=?", millId);
        zbsDAO.executeUpdate("delete from NccElemental where millId=?", millId);
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
            zbsDAO.insert("insert into NccMILLTestDetail (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")",
                    valueList.toArray());
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
            zbsDAO.insert("insert into NccElemental (" + String.join(",", keyList) + ") values (" + String.join(",", holderList) + ")",
                    valueList.toArray());
        }
        return 0;
    }

}
