package com.yonyou.zbs.controller;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.zbs.annotation.PassToken;
import com.yonyou.zbs.common.JWTTokenUtil;
import com.yonyou.zbs.common.PoiUtil;
import com.yonyou.zbs.service.TgZbsService;
import com.yonyou.zbs.vo.PageRespVO;
import com.yonyou.zbs.vo.RestResultVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/zbs")
public class TgZbsRestController {

    @Resource
    private TgZbsService tgZbsService;

    @PassToken
    @PostMapping(value = "/login")
    public Object login(String userName, String password) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return RestResultVO.error("用户名或密码不能为空");
        }
        Map<String, Object> map = tgZbsService.getUserByName(userName);
        if (map == null) {
            return RestResultVO.error("用户不存在");
        }
        String nccPassword = (String) map.get("nccPassword");
        if (!password.equals(nccPassword)) {
            return RestResultVO.error("密码错误");
        }
        JSONObject json = new JSONObject();
        json.put("cUserName", map.get("cUserName"));
        json.put("token", JWTTokenUtil.getToken(userName));
        return RestResultVO.success(json);
    }

    @GetMapping(value = "/cust/list")
    public Object getCustomerList(String custName, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = tgZbsService.getCustomerList(custName, currPage, pageSize);
        int total = tgZbsService.getCustomerCount(custName);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @GetMapping(value = "/std/list")
    public Object getStandardList() {
        return RestResultVO.success(tgZbsService.getStandardList());
    }

    @GetMapping(value = "/steel/list")
    public Object getSteelList() {
        return RestResultVO.success(tgZbsService.getSteelList());
    }

    @GetMapping(value = "/furnace/list")
    public Object getFurnaceList(String searchText, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = tgZbsService.getFurnaceList(searchText, currPage, pageSize);
        int total = tgZbsService.getFurnaceCount(searchText);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @GetMapping(value = "/batch/list")
    public Object getBatchList(String searchText, String steelGrade, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = tgZbsService.getBatchList(searchText, steelGrade, currPage, pageSize);
        int total = tgZbsService.getBatchListCount(searchText, steelGrade);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

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

    @GetMapping(value = "/multi/list")
    public Object getMultiByType(String iSteelType, String custName, String startDate, String endDate, String searchText, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = tgZbsService.getMultiByType(iSteelType, custName, startDate, endDate, searchText, currPage, pageSize);
        int total = tgZbsService.getMultiByTypeCount(iSteelType, custName, startDate, endDate, searchText);
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

    @GetMapping(value = "/multi/id")
    public Object getMultiById(String id) {
        return RestResultVO.success(tgZbsService.getMultiById(id));
    }

    @GetMapping(value = "/tem/seq")
    public Object getNextTemNum() {
        return RestResultVO.success(tgZbsService.getNextTemNum());
    }

    @GetMapping(value = "/next_cert")
    public Object getNextCertNo(String certType) {
        return RestResultVO.success(tgZbsService.getNextCertNo(certType));
    }

    @GetMapping(value = "/qt/list")
    public Object getQtList(String cMFNo, String cStellGrade, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = tgZbsService.getQtList(cMFNo, cStellGrade, currPage, pageSize);
        int total = tgZbsService.getQtListCount(cMFNo, cStellGrade);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @PostMapping(value = "/tem/save")
    public Object temSave(String temJson, String nurbsJson, HttpServletRequest request) {
        String userName = (String) request.getAttribute("userName");
        return RestResultVO.success(tgZbsService.temSave(temJson, nurbsJson, userName));
    }

    @PostMapping(value = "/multi/save")
    public Object batchSave(String headJson, String bodyJson, String refJson, HttpServletRequest request) {
        String userName = (String) request.getAttribute("userName");
        return RestResultVO.success(tgZbsService.multiSave(headJson, bodyJson, refJson, userName));
    }

    @GetMapping(value = "/elem/limits")
    public Object getQtList(String cMFNo) {
        return RestResultVO.success(tgZbsService.getElementLimits(cMFNo));
    }


    @GetMapping(value = "/batch/his")
    public Object batchHistory(String cMFNo, String cStellGrade, String cCusName, String iSteelType) {
        return RestResultVO.success(tgZbsService.getBathHistory(cMFNo, cStellGrade, cCusName, iSteelType));
    }

    @GetMapping(value = "/pdf/gen/single")
    public Object genSinglePdf(String id) throws IOException {
        return RestResultVO.success(tgZbsService.genSinglePdf(id));
    }

    @GetMapping(value = "/pdf/gen/multi")
    public Object genMultiPdf(String id) throws IOException {
        return RestResultVO.success(tgZbsService.genMultiPdf(id));
    }
    @GetMapping(value = "/pdf/gen_m")
    public Object genPdfFreemarkerM(String id) throws Exception {
        return RestResultVO.success(tgZbsService.genPdfFreemarkerM(id));
    }

    @PostMapping(value = "/batch/import")
    public Object importBatch(@RequestParam(name = "file1") MultipartFile file) throws Exception {
        PoiUtil.read(file);
        return RestResultVO.success("haha");
    }


}
