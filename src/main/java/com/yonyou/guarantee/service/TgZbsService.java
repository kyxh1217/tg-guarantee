package com.yonyou.guarantee.service;

import java.util.List;
import java.util.Map;

public interface TgZbsService {

    /**
     * 查询客户列表
     *
     * @param custName 客户名称
     * @param currPage 当前页码
     * @param pageSize pageSize
     * @return list
     */
    List<Map<String, Object>> getCustomerList(String custName, int currPage, int pageSize);

    /**
     * 查询客户数据
     *
     * @param custName 客户名称
     * @return int
     */
    Integer getCustomerCount(String custName);

    /**
     * 查询所有的技术标准
     *
     * @return list
     */
    List<Map<String, Object>> getStandardList();

    /**
     * 获取钢号
     *
     * @return list
     */
    List<Map<String, Object>> getSteelList();

    /**
     * 获取熔炼炉号
     *
     * @return list
     */
    List<Map<String, Object>> getFurnaceList(String searchText, int currPage, int pageSize);

    /**
     * 查询数量
     *
     * @param searchText searchText
     * @return int
     */
    Integer getFurnaceCount(String searchText);

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
     * 获取质保书编号
     *
     * @return next tem num
     */
    String getNextTemNum();

    /**
     * 查询客户列表
     *
     * @param searchText 客户名称
     * @param currPage   当前页码
     * @param pageSize   pageSize
     * @return list
     */
    List<Map<String, Object>> getQtList(String searchText, int currPage, int pageSize);

    /**
     * 查询客户数据
     *
     * @param searchText 客户名称
     * @return int
     */
    Integer getQtListCount(String searchText);

    /**
     * 保存质保书
     *
     * @param temJson   temJson
     * @param nurbsJosn nurbsJosn
     * @return int
     */
    int temSave(String temJson, String nurbsJosn);


    /**
     * 查询客户列表
     *
     * @param cMFNo 客户名称
     * @return list
     */
    List<Map<String, Object>> getElementLimits(String cMFNo);

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
     * 保存质保书
     *
     * @param headJson headJson
     * @param bodyJson bodyJson
     * @param refJson  refJson
     * @return int
     */
    int batchSave(String headJson, String bodyJson, String refJson);

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
    List<Map<String, Object>> getMultiByType(String iSteelType, String custName, String startDate, String endDate, String searchText, int currPage, int pageSize);

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
    Integer getMultiByTypeCount(String iSteelType, String custName, String startDate, String endDate, String searchText);

    /**
     * getTemById
     *
     * @param id id
     * @return map
     */
    Map<String, Object> getMultiById(String id);
}
