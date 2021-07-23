package com.yonyou.zbs.service;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SingleBatchService {


    /**
     * 获取熔炼炉的光学信息
     *
     * @return list
     */
    Map<String, Object> getTemHistory(String cMFNo, String cStellGrade, String cCusName, String iSteelType);

    /**
     * 查询质保书列表
     *
     * @param iSteelType iSteelType
     * @param custName   custName
     * @param startDate  startDate
     * @param endDate    endDate
     * @param searchText searchText
     * @return List
     */
    List<Map<String, Object>> getTemByType(String iSteelType, String custName, String startDate, String endDate, String searchText, int currPage, int pageSize);

    /**
     * getTemByTypeCount
     *
     * @param iSteelType iSteelType
     * @param custName   custName
     * @param startDate  startDate
     * @param endDate    endDate
     * @param searchText searchText
     * @return List
     */
    Integer getTemByTypeCount(String iSteelType, String custName, String startDate, String endDate, String searchText);

    /**
     * getTemById
     *
     * @param id id
     * @return map
     */
    Map<String, Object> getTemById(String id);

    /**
     * getTemById
     *
     * @param id id
     * @return map
     */
    boolean delTemById(String id);


    /**
     * 保存质保书
     *
     * @param temJson   temJson
     * @param nurbsJson nurbsJson
     * @return int
     */
    int temSave(String temJson, String nurbsJson, String userName);


    String genSinglePdf(String id) throws IOException;

    String viewSinglePdf(String id) throws Exception;

    ResponseEntity<byte[]> download(String id) throws Exception;

}
