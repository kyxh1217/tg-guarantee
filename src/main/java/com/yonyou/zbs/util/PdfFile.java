package com.yonyou.zbs.util;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.barcodes.qrcode.EncodeHintType;
import com.itextpdf.barcodes.qrcode.ErrorCorrectionLevel;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 生成PDF文件
 */
public class PdfFile {
    private final static String RESOURCE_PATH = Objects.requireNonNull(PdfFile.class.getResource("/static")).getPath();

    public static void downloadPdf(HttpServletResponse response, Map<String, Object> data, String templateName) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("path", RESOURCE_PATH);
        params.put("applicant", data);
        String fileName = new Date().getTime() + ".pdf";
        String pdfFile = "/pdf/" + fileName;
        template(templateName + ".html", params, pdfFile);
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            byte[] buff = new byte[1024];
            os = response.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(pdfFile));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (Exception e1) {
            //e1.getMessage()+"系统找不到指定的文件";
            throw new Exception("下载文件错误：" + e1.getMessage());
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    /**
     * 填充html模板
     *
     * @param templateFile 模板文件名
     * @param args         模板参数
     * @param pdfFile      生成文件路径
     */
    public static void template(String templateFile, Map<String, Object> args, String pdfFile) throws Exception {
        FileOutputStream output = null;
        try {
            // 读取模板文件,填充模板参数
            args.put("path", RESOURCE_PATH);
            String html = compileTemplate(templateFile, args);
            // 设置字体以及字符编码
            ConverterProperties props = new ConverterProperties();
            FontProvider fontProvider = new FontProvider();
            // PdfFont sysFont = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            //PdfFont sysFont = PdfFontFactory.createFont(RESOURCE_PATH + "/font/SimSun.ttf", PdfEncodings.IDENTITY_H);
            PdfFont sysFont = PdfFontFactory.createFont(RESOURCE_PATH + "/font/arial.ttf", PdfEncodings.IDENTITY_H);
            fontProvider.addFont(sysFont.getFontProgram());
            fontProvider.addStandardPdfFonts();
            // fontProvider.addFont("template/simsun.ttc");
            // fontProvider.addFont("template/STHeitibd.ttf");
            props.setFontProvider(fontProvider);
            props.setCharset("utf-8");
           /* PdfDocument pdfDoc = new PdfDocument(new PdfWriter(pdfFile));
            Document document = new Document(pdfDoc, PageSize.A4.rotate(), false);
            List<IElement> elements = HtmlConverter.convertToElements(html, props);
            for (IElement element : elements) {
                // 分页符
                if (element instanceof HtmlPageBreak) {
                    document.add((HtmlPageBreak) element);
                    //普通块级元素
                } else {
                    document.add((IBlockElement) element);
                }
            }
            document.setMargins(18f, 18f, 18f, 18f);
            document.getRenderer().close();
            document.close();*/
            // 转换为PDF文档
            output = new FileOutputStream(pdfFile);
            PdfDocument pdf = new PdfDocument(new PdfWriter(output));
            pdf.setDefaultPageSize(PageSize.A4);
            Document document = HtmlConverter.convertToDocument(html, pdf, props);
            //document.setMargins(10, 10, 10, 10);
            document.setMargins(18f, 18f, 18f, 18f);
            document.getRenderer().close();
            document.close();
        } catch (Exception e) {
            throw new Exception("创建pdf错误：" + e.getMessage());
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private static String compileTemplate(String templateFile, Map<String, Object> args) throws Exception {
        StringWriter sw = null;
        try {
            Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_30);
            freemarkerCfg.setTemplateLoader(new ClassTemplateLoader(PdfFile.class, "/templates/"));
            Template template = freemarkerCfg.getTemplate(templateFile);
            sw = new StringWriter();
            if (args != null && args.size() > 0)
                template.process(args, sw);
            return sw.toString();
        } catch (Exception e) {
            throw new Exception("编译模板错误:" + e.getMessage());
        } finally {
            if (sw != null) {
                sw.close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BarcodeQRCode qrcode = new BarcodeQRCode("url:http://www.tggj.cn/tg/?id=00008888", hints);
        Image qrcodeImg = qrcode.createAwtImage(Color.BLACK, Color.WHITE);
        BufferedImage img = new BufferedImage(qrcodeImg.getWidth(null), qrcodeImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = img.createGraphics();
        g.drawImage(qrcodeImg, 0, 0, Color.WHITE, null);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        byte[] bytes = baos.toByteArray();
        Base64.Encoder encoder = Base64.getEncoder();
        String qrBase64 = encoder.encodeToString(bytes);
        Map<String, Object> head = JSONObject.parseObject("{\"cPROCESS\":\"\",\"cDELIVERY\":\"\",\"cULTRASONIC\":\"\",\"cDECARBURIZATION\":\"\",\"cCertificateNo\":\"00007439\",\"cQUALITY\":\"\",\"cSPECIFICATION\":\"\",\"cContractNO\":\"\",\"cNOTES1\":\"MILL TEST CERTIFICATE: ACC.TO EN 10204/3.1<br/>MELTING PROCESS: E.A.F plus LF plus VACUUM TREATMENT + ESR<br/>GRADE OF DEFORMATION:MIN.4-FOLD FULLY FORMED<br/>MATERIAL IS RADIATION AND MERCURY FREE<br/>GRAIN SIZE : 12 AND FINER\",\"cCERTIFICATE\":\"\",\"cStellGrade\":\"1.2083\",\"cCustomer\":\"Test MILL Custoner No0001\",\"dDate\":\"2021-06-28\",\"cNOTES2\":\"MILL TEST CERTIFICATE: ACC.TO EN 10204/3.1<br/>MELTING PROCESS: E.A.F plus LF plus VACUUM TREATMENT + ESR<br/>GRADE OF DEFORMATION:MIN.4-FOLD FULLY FORMED<br/>MATERIAL IS RADIATION AND MERCURY FREE<br/>GRAIN SIZE : 12 AND FINER\",\"cSign\":\"    \",\"cGSIZE\":\"\",\"ID\":1014,\"iSteelType\":1,\"cStellGradeW\":\"\",\"cDEFORMATION\":\"\"}", Map.class);
        Map<String, Object> bath = JSONObject.parseObject("{\"i_id\":14,\"cFields3\":\"0.33\",\"cHardness\":\"64\",\"cFields2\":\"0.37\",\"cFields1\":\"1.46\",\"cHEATNO\":\"6614092359B\",\"cFields9\":\"\",\"A_H\":\"0.5\",\"B_H\":\"1.0\",\"cFields8\":\"0.76\",\"cSIZES\":\"123\",\"cFields7\":\"11.61\",\"D_H\":\"0.5\",\"cFields6\":\"0.76\",\"C_H\":\"1.0\",\"cFields5\":\"0.005\",\"cFields4\":\"0.025\",\"cPCS\":456,\"cSegregation\":\"1\",\"A_T\":\"1.0\",\"B_T\":\"0.15\",\"D_T\":\"1.0\",\"dWeight\":\"789\",\"C_T\":\"1.0\",\"cPorosity\":\"1\",\"cMICROSTRUCTURE\":\"\",\"iSteelType\":0,\"cSize\":\"10\",\"cFields10\":\"\",\"cCertificateNo\":\"\",\"millId\":1014,\"cFields11\":\"\",\"cDistribution\":\"2B\",\"cMICROHOMOGENITY\":\"\",\"cANNEALLING\":\"237\"}", Map.class);
        List<Map<String, Object>> batchList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            batchList.add(bath);
        }
        Map<String,Object> ref = JSONObject.parseObject("{\"Mn\":\"0.4-0.6\",\"Mo\":\"0-0.2\",\"C\":\"0.38-0.45\",\"millId\":1014,\"Cr\":\"12.5-12.8\",\"P\":\"0-0.025\",\"Cu\":\"0-0.2\",\"S\":\"0-0.03\",\"Si\":\"0.4-0.6\",\"V\":\"0-0.2\",\"W\":\"0-0.2\",\"ID\":5,\"Ni\":\"0-0.3\"}",Map.class);
        Map map = new HashMap();
        map.put("qrBase64", qrBase64);
        map.put("head", head);
        map.put("ref", ref);
        map.put("batchList", batchList);
        System.out.println(qrBase64);
        template("zbs_m.ftl", map, "d:/pdf/" + new Date().getTime() + ".pdf");
    }

}
