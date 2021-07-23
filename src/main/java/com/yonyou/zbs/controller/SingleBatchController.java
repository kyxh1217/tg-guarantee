package com.yonyou.zbs.controller;

import com.yonyou.zbs.annotation.PassToken;
import com.yonyou.zbs.service.SingleBatchService;
import com.yonyou.zbs.vo.PageRespVO;
import com.yonyou.zbs.vo.RestResultVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/zbs/tem")
public class SingleBatchController {

    @Resource
    private SingleBatchService singleBatchService;

    @GetMapping(value = "/history")
    public Object getChemicals(String cMFNo, String cStellGrade, String cCusName, String iSteelType) {
        return RestResultVO.success(singleBatchService.getTemHistory(cMFNo, cStellGrade, cCusName, iSteelType));
    }

    @GetMapping(value = "/list")
    public Object getTemByType(String iSteelType, String custName, String startDate, String endDate, String searchText, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = singleBatchService.getTemByType(iSteelType, custName, startDate, endDate, searchText, currPage, pageSize);
        int total = singleBatchService.getTemByTypeCount(iSteelType, custName, startDate, endDate, searchText);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @GetMapping(value = "/id")
    public Object getTemById(String id) {
        return RestResultVO.success(singleBatchService.getTemById(id));
    }

    @GetMapping(value = "/del")
    public Object delTemById(String id) {
        return RestResultVO.success(singleBatchService.delTemById(id));
    }

    @PostMapping(value = "/save")
    public Object temSave(String temJson, String nurbsJson, HttpServletRequest request) {
        String userName = (String) request.getAttribute("userName");
        int id = singleBatchService.temSave(temJson, nurbsJson, userName);
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return RestResultVO.success(map);
    }

    @GetMapping(value = "/gen")
    public Object genSinglePdf(String id) throws IOException {
        return RestResultVO.success(singleBatchService.genSinglePdf(id));
    }

    @GetMapping(value = "/view")
    public Object viewSingle(String id) throws Exception {
        return RestResultVO.success(singleBatchService.viewSinglePdf(id));
    }
}
