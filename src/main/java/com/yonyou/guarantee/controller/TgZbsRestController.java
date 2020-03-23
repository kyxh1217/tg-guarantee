package com.yonyou.guarantee.controller;

import com.yonyou.guarantee.service.TgZbsService;
import com.yonyou.guarantee.vo.PageRespVO;
import com.yonyou.guarantee.vo.RestResultVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/zbs")
public class TgZbsRestController {

    @Resource
    private TgZbsService tgZbsService;

    @RequestMapping(value = "/cust/list", method = RequestMethod.GET)
    public Object getCustomerList(String custName, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = tgZbsService.getCustomerList(custName, currPage, pageSize);
        int total = tgZbsService.getCustomerCount(custName);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @RequestMapping(value = "/std/list", method = RequestMethod.GET)
    public Object getStandardList() {
        return RestResultVO.success(tgZbsService.getStandardList());
    }

    @RequestMapping(value = "/steel/list", method = RequestMethod.GET)
    public Object getSteelList() {
        return RestResultVO.success(tgZbsService.getSteelList());
    }

}
