package com.yonyou.zbs.controller;

import com.yonyou.zbs.annotation.PassToken;
import com.yonyou.zbs.service.SingleBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("/rest/zbs")
public class FileController {
    @Resource
    private SingleBatchService singleBatchService;

    @GetMapping(value = "/tem/download")
    @PassToken
    public ResponseEntity<byte[]> download(HttpServletRequest request, String id) throws Exception {
        return singleBatchService.download(id);
    }
}
