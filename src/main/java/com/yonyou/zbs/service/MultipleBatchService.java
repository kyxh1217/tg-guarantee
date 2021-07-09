package com.yonyou.zbs.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MultipleBatchService {
    /**
     * 获取熔炼炉号
     *
     * @return list
     */
    List<Map<String, Object>> getBatchList(String searchText, String steelGrade, int currPage, int pageSize);

    /**
     * getBatchListCount
     *
     * @param searchText searchText
     * @param steelGrade steelGrade
     * @return int
     */
    Integer getBatchListCount(String searchText, String steelGrade);

    /**
     * getTemById
     */
    Map<String, Object> getMultiById(String id);

    /**
     * 保存质保书
     */
    int multiSave(String headJson, String bodyJson, String refJson, String userName);

    /**
     * 查询质保书列表
     */
    List<Map<String, Object>> getMultiByType(String iSteelType, String custName, String startDate, String endDate, String searchText, int currPage, int pageSize);

    /**
     * getTemByTypeCount
     */
    Integer getMultiByTypeCount(String iSteelType, String custName, String startDate, String endDate, String searchText);

    /**
     * 获取熔炼炉的光学信息
     *
     * @return list
     */
    Map<String, Object> getBathHistory(String cMFNo, String cStellGrade, String cCusName, String iSteelType);

    String genMultiPdf(String id) throws IOException;

    /**
     * 通过freemarker生成模板，然后itext生成pdf
     */
    String genPdfFreemarkerM(String id) throws Exception;

    List<Map<String, Object>> batchImport(MultipartFile file, String cStellGrade) throws Exception;
}
