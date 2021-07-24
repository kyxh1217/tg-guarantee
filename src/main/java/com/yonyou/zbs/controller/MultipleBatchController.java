package com.yonyou.zbs.controller;

import com.yonyou.zbs.service.MultipleBatchService;
import com.yonyou.zbs.vo.PageRespVO;
import com.yonyou.zbs.vo.RestResultVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/zbs/multi")
public class MultipleBatchController {
    @Resource
    private MultipleBatchService multipleBatchService;

    @GetMapping(value = "/batch_list")
    public Object getBatchList(String searchText, String steelGrade, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = multipleBatchService.getBatchList(searchText, steelGrade, currPage, pageSize);
        int total = multipleBatchService.getBatchListCount(searchText, steelGrade);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @GetMapping(value = "/id")
    public Object getMultiById(String id) {
        return RestResultVO.success(multipleBatchService.getMultiById(id));
    }

    @PostMapping(value = "/del")
    public Object delMultiById(String id) {
        multipleBatchService.delMultiById(id);
        return RestResultVO.success("");
    }

    @GetMapping(value = "/list")
    public Object getMultiByType(String iSteelType, String custName, String startDate, String endDate, String searchText, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = multipleBatchService.getMultiByType(iSteelType, custName, startDate, endDate, searchText, currPage, pageSize);
        int total = multipleBatchService.getMultiByTypeCount(iSteelType, custName, startDate, endDate, searchText);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @PostMapping(value = "/save")
    public Object batchSave(String headJson, String bodyJson, String refJson, HttpServletRequest request) {
        String userName = (String) request.getAttribute("userName");
        return RestResultVO.success(multipleBatchService.multiSave(headJson, bodyJson, refJson, userName));
    }

    @GetMapping(value = "/batch_history")
    public Object batchHistory(String cMFNo, String cStellGrade, String cCusName, String iSteelType) {
        return RestResultVO.success(multipleBatchService.getBathHistory(cMFNo, cStellGrade, cCusName, iSteelType));
    }

    @GetMapping(value = "/gen_free")
    public Object genPdfFreemarkerM(String id) throws Exception {
        return RestResultVO.success(multipleBatchService.genPdfFreemarkerM(id));
    }

    @PostMapping(value = "/batch_import")
    public Object importBatch(@RequestParam(name = "file1") MultipartFile file, String cStellGrade) {
        try {
            return RestResultVO.success(multipleBatchService.batchImport(file, cStellGrade));
        } catch (Exception e) {
            return RestResultVO.error("导入excel错误：" + e.getMessage());
        }

    }

    @GetMapping(value = "/gen")
    public Object genMultiPdf(String id) throws Exception {
        return RestResultVO.success(multipleBatchService.genMultiPdf(id));
    }
}
