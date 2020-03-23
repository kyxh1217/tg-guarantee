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
}
