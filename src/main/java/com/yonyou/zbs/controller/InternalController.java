package com.yonyou.zbs.controller;

import com.yonyou.zbs.service.SingleBatchService;
import com.yonyou.zbs.util.Html2PdfUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/internal/")
public class InternalController {

    private static final String PDF_PATH = "d:/pdfdist";
    @Resource
    private SingleBatchService tgZbsService;

    @RequestMapping("/round")
    public String steelRound() {
        return "internal/round";
    }

    @RequestMapping("/flat")
    public String steelFlat() {
        return "internal/flat";
    }

    @RequestMapping("/pdf")
    public String pdf() {
        return "pdf/pdf";
    }

    @RequestMapping(value = "/createPdf", method = RequestMethod.POST)
    @ResponseBody
    public Object createPdf(String htmlContent) throws IOException {
        String pdfName = PDF_PATH + "/" + UUID.randomUUID().toString() + ".pdf";
        Html2PdfUtils.createPdf(htmlContent, pdfName);
        Map<String, Object> map = new HashMap<>();
        map.put(pdfName, pdfName);
        return map;
    }
}
