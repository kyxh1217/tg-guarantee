package com.yonyou.zbs.service;

import java.util.List;
import java.util.Map;

public interface BizService {
    /**
     * 查询客户列表
     */
    List<Map<String, Object>> getCustomerList(String custName, int currPage, int pageSize);

    /**
     * 查询客户数据
     */
    Integer getCustomerCount(String custName);

    /**
     * 查询所有的技术标准
     */
    List<Map<String, Object>> getStandardList();

    /**
     * 获取钢号
     */
    List<Map<String, Object>> getSteelList();

    /**
     * 获取熔炼炉号
     */
    List<Map<String, Object>> getFurnaceList(String searchText, int currPage, int pageSize);

    /**
     * 查询数量
     */
    Integer getFurnaceCount(String searchText);

    /**
     * 获取质保书编号
     */
    String getNextCertNo(String certType);

    /**
     * 查询客户列表
     */
    List<Map<String, Object>> getQtList(String cMFNo, String cStellGrade, int currPage, int pageSize);

    /**
     * 查询客户数据
     */
    Integer getQtListCount(String cMFNo, String cStellGrade);

    /**
     * 查询客户列表
     *
     * @param cMFNo 客户名称
     * @return list
     */
    List<Map<String, Object>> getElementLimits(String cMFNo);

    Map<String, Object> getUserByName(String userName);
}
