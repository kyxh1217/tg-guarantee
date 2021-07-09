package com.yonyou.zbs.controller;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.zbs.annotation.PassToken;
import com.yonyou.zbs.common.JWTTokenUtil;
import com.yonyou.zbs.service.BizService;
import com.yonyou.zbs.vo.PageRespVO;
import com.yonyou.zbs.vo.RestResultVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/zbs")
public class BizController {
    @Resource
    private BizService bizService;

    @PassToken
    @PostMapping(value = "/login")
    public Object login(String userName, String password) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return RestResultVO.error("用户名或密码不能为空");
        }
        Map<String, Object> map = bizService.getUserByName(userName);
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
        List<Map<String, Object>> dataList = bizService.getCustomerList(custName, currPage, pageSize);
        int total = bizService.getCustomerCount(custName);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @GetMapping(value = "/std/list")
    public Object getStandardList() {
        return RestResultVO.success(bizService.getStandardList());
    }

    @GetMapping(value = "/steel/list")
    public Object getSteelList() {
        return RestResultVO.success(bizService.getSteelList());
    }

    @GetMapping(value = "/furnace/list")
    public Object getFurnaceList(String searchText, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = bizService.getFurnaceList(searchText, currPage, pageSize);
        int total = bizService.getFurnaceCount(searchText);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @GetMapping(value = "/next_cert")
    public Object getNextCertNo(String certType) {
        return RestResultVO.success(bizService.getNextCertNo(certType));
    }

    @GetMapping(value = "/qt/list")
    public Object getQtList(String cMFNo, String cStellGrade, Integer currPage, Integer pageSize) {
        currPage = null == currPage ? 1 : currPage;
        pageSize = null == pageSize ? 10 : pageSize;
        List<Map<String, Object>> dataList = bizService.getQtList(cMFNo, cStellGrade, currPage, pageSize);
        int total = bizService.getQtListCount(cMFNo, cStellGrade);
        PageRespVO pageRespVO = new PageRespVO.Builder().total(total).addList(dataList).create();
        return RestResultVO.success(pageRespVO);
    }

    @GetMapping(value = "/elem/limits")
    public Object getElementLimits(String cMFNo) {
        return RestResultVO.success(bizService.getElementLimits(cMFNo));
    }
}
