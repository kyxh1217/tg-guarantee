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
    List<Map<String, Object>> getChemicalsByFurnace(String furnaceNum);

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
}
