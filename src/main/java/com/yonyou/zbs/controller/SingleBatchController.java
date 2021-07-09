package com.yonyou.zbs.controller;

import com.yonyou.zbs.service.SingleBatchService;
import com.yonyou.zbs.vo.PageRespVO;
import com.yonyou.zbs.vo.RestResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/zbs")
public class SingleBatchController {

    @Resource
    private SingleBatchService tgZbsService;

    @GetMapping(value = "/tem/his")
    public Object getChemicals(String cMFNo, String cStellGrade, String cCusName, String iSteelType) {
        return RestResultVO.success(tgZbsService.getTemHistory(cMFNo, cStellGrade, cCusName, iSteelType));
    }

    @GetMapping(value = "/tem/list")
    public Object getTemByType(String iSteelType, String custName, String startDate, String endDate, String searchText, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = tgZbsService.getTemByType(iSteelType, custName, startDate, endDate, searchText, currPage, pageSize);
        int total = tgZbsService.getTemByTypeCount(iSteelType, custName, startDate, endDate, searchText);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @GetMapping(value = "/tem/id")
    public Object getTemById(String id) {
        return RestResultVO.success(tgZbsService.getTemById(id));
    }

    @GetMapping(value = "/tem/del")
    public Object delTemById(String id) {
        return RestResultVO.success(tgZbsService.delTemById(id));
    }


    @GetMapping(value = "/tem/seq")
    public Object getNextTemNum() {
        return RestResultVO.success(tgZbsService.getNextTemNum());
    }

    @PostMapping(value = "/tem/save")
    public Object temSave(String temJson, String nurbsJson, HttpServletRequest request) {
        String userName = (String) request.getAttribute("userName");
        return RestResultVO.success(tgZbsService.temSave(temJson, nurbsJson, userName));
    }

    @GetMapping(value = "/pdf/gen/single")
    public Object genSinglePdf(String id) throws IOException {
        return RestResultVO.success(tgZbsService.genSinglePdf(id));
    }

}
