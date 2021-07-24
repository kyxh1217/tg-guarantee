package com.yonyou.zbs.util;

import com.alibaba.fastjson.JSON;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.barcodes.qrcode.EncodeHintType;
import com.itextpdf.barcodes.qrcode.ErrorCorrectionLevel;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yonyou.zbs.util.CommonTools.findIndex;
import static com.yonyou.zbs.util.CommonTools.getConstValue;

public class MultipleBatchPdfUtils {
    private final static Logger logger = LoggerFactory.getLogger(MultipleBatchPdfUtils.class);
    private final static int PDF_PAGE_SIZE = 10;
    private static final URL RESOURCE_PATH = MultipleBatchPdfUtils.class.getResource("/static");

    public static String genMultiPdf(Map<String, Object> head, Map<String, Object> ref, List<Map<String, Object>> batchList) throws Exception {
        String iSteelType = String.valueOf(head.get("iSteelType"));
        if (CollectionUtils.isEmpty(batchList) || batchList.size() <= PDF_PAGE_SIZE) {
            return doMultiPdf(head, ref, batchList, iSteelType);
        }
        int size = batchList.size();
        int count = size / PDF_PAGE_SIZE;
        if (size % PDF_PAGE_SIZE > 0) count++;
        List<String> pdfList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int endIndex = (i + 1) * PDF_PAGE_SIZE;
            if (endIndex > size) endIndex = size;
            List<Map<String, Object>> subList = batchList.subList(i * PDF_PAGE_SIZE, endIndex);
            pdfList.add(doMultiPdf(head, ref, subList, iSteelType));
        }
        return mergePdfFiles(pdfList);
    }

    public static String doMultiPdf(Map<String, Object> head, Map<String, Object> refs, List<Map<String, Object>> batchList, String iSteelType) throws Exception {
        if (CollectionUtils.isEmpty(head)) {
            throw new Exception("表头数据不能为空");
        }
        long curr = System.currentTimeMillis();
        String certPrefix = (String) getConstValue(iSteelType, "ABBR");
        InputStream templatePdfIs = null;// 模板的InputStream
        Document document = null;
        try {
            templatePdfIs = MultipleBatchPdfUtils.class.getClassLoader().getResourceAsStream("pdf/071911.pdf");
            if (templatePdfIs == null) {
                throw new Exception("模板文件不存在");
            }
            //String pdfName = certPrefix + head.get("cCertificateNo") + ".pdf";
            String pdfName = certPrefix + System.currentTimeMillis() + ".pdf";
            //1、创建pdf文件
            PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(ConfigUtils.getPdfPath() + "/" + pdfName));
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(templatePdfIs), pdfWriter);
            //2、创建字体信息并添加到pdf中
            assert RESOURCE_PATH != null;
            PdfFont arial = PdfFontFactory.createFont(RESOURCE_PATH.getPath() + "/font/arial.ttf", PdfEncodings.IDENTITY_H);
            pdfDocument.addFont(arial);
            PdfFont arialbd = PdfFontFactory.createFont(RESOURCE_PATH.getPath() + "/font/arialbd.ttf", PdfEncodings.IDENTITY_H);
            pdfDocument.addFont(arialbd);
            // 创建document，A4纸尺寸
            document = new Document(pdfDocument, PageSize.A4.rotate());
            //3、获取pdf模板中的域值信息
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true);
            Map<String, PdfFormField> fieldMap = form.getFormFields();
            // 处理公用信息
            String cCertificateNo = doFillHeader(document, fieldMap, arial, arialbd, head, iSteelType);
            // 处理元素表头
            doFillElements(fieldMap, arial, iSteelType);
            // 处理参考数据
            doFillRefs(fieldMap, arial, refs, iSteelType);
            doFillBatch(fieldMap, arial, batchList, iSteelType);
            //设置文本不可编辑
            form.flattenFields();
            // 二维码
            doQrcodeImage(document, pdfDocument, cCertificateNo);
            // 质检章
            doStampImage(document);

            return ConfigUtils.getPdfUrl() + pdfName;
        } finally {
            if (templatePdfIs != null) {
                try {
                    templatePdfIs.close();
                } catch (Exception ignored) {

                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (Exception ignored) {

                }
            }
            long end = System.currentTimeMillis();
            logger.debug("创建pdf开始于：" + curr + ",结束于：" + end + "，耗时：" + (end - curr) + "ms");
        }

    }


    /**
     * 填充header、footer等公共区域
     */
    private static String doFillHeader(Document document, Map<String, PdfFormField> fieldMap, PdfFont arial, PdfFont arialbd,
                                       Map<String, Object> head, String iSteelType) throws Exception {
        String certPrefix = (String) getConstValue(iSteelType, "ABBR");
        String[] headerFields = (String[]) getConstValue(iSteelType, "HEADER_FIELDS");
        String cCertificateNo = null;
        // 开始填充表头、页脚的区域
        for (String key : headerFields) {
            Object obj = head.get(key);
            if (obj == null) continue;
            PdfFormField formField = fieldMap.get(key);
            if (formField == null) continue;
            String value = obj.toString();
            if (key.equalsIgnoreCase("cCertificateNo")) {
                cCertificateNo = (value = certPrefix + value);
            }
            if (key.equalsIgnoreCase("cGroupName")) {
                //formField.setFontAndSize(arialbd, 12f);
                doNameImage(document, value);
            } else if (key.equalsIgnoreCase("enGroupName")) {
                formField.setFontAndSize(arialbd, 6f);
            } else {
                formField.setFontAndSize(arial, 6f);
            }
            formField.setValue(value);
        }
        return cCertificateNo;
    }

    /**
     * 填充表头里的元素
     */
    private static void doFillElements(Map<String, PdfFormField> fieldMap, PdfFont arial, String iSteelType) throws Exception {
        String[] elementFields = (String[]) getConstValue(iSteelType, "ELEMENTS_FIELDS");
        assert elementFields != null;
        for (int i = 0; i < elementFields.length; i++) {
            PdfFormField formField = fieldMap.get("e" + i);
            if (formField == null) continue;
            formField.setValue(elementFields[i], arial, 6f);
        }
    }

    /**
     * 填充参考值
     */
    private static void doFillRefs(Map<String, PdfFormField> fieldMap, PdfFont arial, Map<String, Object> refs, String iSteelType) throws Exception {
        if (CollectionUtils.isEmpty(refs)) return;
        String[] refFields = (String[]) getConstValue(iSteelType, "REF_FIELDS");
        String[] elementFields = (String[]) getConstValue(iSteelType, "ELEMENTS_FIELDS");
        for (String refKey : refFields) {
            Object obj = refs.get(refKey);
            if (obj == null) continue;
            // 判断师是否为元素，如果是元素，就是e0、e1...,否则为原始值
            int elementIndex = findIndex(elementFields, refKey);
            String formFieldName = elementIndex > -1 ? ("e" + elementIndex) : refKey;
            String value = obj.toString();
            PdfFormField formField = fieldMap.get(formFieldName + (value.length() > 8 ? "_ref" : "_sref"));
            if (formField == null) continue;
            //formField.setFontSizeAutoScale();
            formField.setValue(value, arial, 6f);
        }
    }

    /**
     * 填充批次信息
     */
    private static void doFillBatch(Map<String, PdfFormField> fieldMap, PdfFont arial, List<Map<String, Object>> batchList, String iSteelType) throws Exception {
        if (CollectionUtils.isEmpty(batchList)) return;
        String[] bodyFields = (String[]) getConstValue(iSteelType, "BODY_FIELDS");
        for (int i = 0; i < batchList.size(); i++) {
            Map<String, Object> map = batchList.get(i);
            for (String key : bodyFields) {
                Object obj = map.get(key);
                if (obj == null) continue;
                PdfFormField formField = fieldMap.get(key + "_" + i);
                if (formField == null) continue;
                formField.setValue(obj.toString(), arial, 6f);
            }
        }
    }

    private static void doNameImage(Document document, String value) throws Exception {
        // 厂名是中文，老外看到的是乱码，所以要改成图片
        // 1.先用buffed image画出图片。2.将图片转换成byte数组。 3.名称是否清晰由字体大小，图片长和宽决定
        // 生成的图片2.93k，不会占用太多内存
        BufferedImage bufferedNameImage = createZhImage(value, "Songti-SC-Bold", 90f, 900, 150);
        byte[] nameImageBytes = imageToBytes(bufferedNameImage);
        Image nameImage = new Image(ImageDataFactory.create(nameImageBytes));
        nameImage.setHeight(30);
        nameImage.setWidth(180);
        nameImage.setFixedPosition(326, 558);
        document.add(nameImage);
    }

    private static void doQrcodeImage(Document document, PdfDocument pdfDocument, String cCertificateNO) {
        // 二维码
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BarcodeQRCode qrcode = new BarcodeQRCode("http://www.tggj.cn/tg/?id=" + cCertificateNO, hints);
        Image qrImage = new Image(qrcode.createFormXObject(pdfDocument));
        qrImage.setHeight(100);
        qrImage.setWidth(100);
        //image.scaleToFit(50, 50);
        qrImage.setFixedPosition(712, 108);
        document.add(qrImage);
    }

    private static void doStampImage(Document document) throws IOException {
        assert RESOURCE_PATH != null;
        try (FileInputStream stampFis = new FileInputStream(RESOURCE_PATH.getPath() + "/img/tggj.png")) {
            Image stampImage = new Image(ImageDataFactory.create(IOUtils.toByteArray(stampFis)));
            stampImage.setHeight(90);
            stampImage.setWidth(100);
            //image.scaleToFit(50, 50);
            stampImage.setFixedPosition(712, 25);
            document.add(stampImage);
        }
    }

    private static String mergePdfFiles(List<String> files) throws IOException {
        String newfile = System.currentTimeMillis() + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(ConfigUtils.getPdfPath() + "/" + newfile));
        PdfMerger merger = new PdfMerger(pdf);
        files.forEach(name -> {
            name = name.substring(name.lastIndexOf("/") + 1);
            try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(ConfigUtils.getPdfPath() + "/" + name))) {
                merger.merge(pdfDocument, 1, pdfDocument.getNumberOfPages());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pdf.close();
        return ConfigUtils.getPdfUrl() + newfile;
    }

    public static BufferedImage createZhImage(String text, String fontName, float fontSize, int width, int height) throws Exception {
        String fontPath = "static/font/" + fontName + ".ttf";
        try (InputStream is = MultipleBatchPdfUtils.class.getClassLoader().getResourceAsStream(fontPath)) {
            if (is == null) {
                throw new Exception("字体[" + fontName + "]不存在");
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);//返回一个指定字体类型和输入数据的font
            font = font.deriveFont(fontSize);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            Graphics g = image.getGraphics();
            g.setClip(0, 0, width, height);
            g.setColor(Color.white);
            g.fillRect(0, 0, width, height);// 先用黑色填充整张图片,也就是背景
            g.setColor(Color.BLACK);// 在换成黑色
            g.setFont(font);// 设置画笔字体
            // 用于获得垂直居中y
            Rectangle clip = g.getClipBounds();
            FontMetrics fm = g.getFontMetrics(font);
            int ascent = fm.getAscent();
            int descent = fm.getDescent();
            int y = (clip.height - (ascent + descent)) / 2 + ascent;
            g.drawString(text, 4, y);// 画出字符串
            g.dispose();
            return image;
        }
    }

    private static byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //ImageIO.write(image, "png", new FileOutputStream("d:/text.png"));// 输出png图片
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }


    public static void main(String[] args) throws Exception {
        Map<String, Object> head = new HashMap<>();
        head.put("cCertificateNo", "88888888");
        head.put("cCustomer", "Test Customer No.88888888");
        head.put("dDate", "2021-07-22");
        head.put("cStellGrade", "M88888888");
        head.put("cContractNO", "88488848");
        head.put("cSPECIFICATION", "SPECIFICATION ENC 8888888");
        head.put("cStellDesc", "This is the test description");
        head.put("iSteelType", "1");
        head.put("cGroupName", "江苏天工工具有限公司");
        head.put("enGroupName", "JIANGSU TIANGONG TOOLS COMPANY LIMITED");
        head.put("enAddress", "Danbei Houxiang Town Danyang City Jiangsu Province");
        head.put("cNOTES1", "MILL TEST CERTIFICATE: ACC.TO EN 10204/3.1\n\n" +
                "MELTING PROCESS: E.A.F plus LF plus VACUUM TREATMENT + ESR\n\n" +
                "GRADE OF DEFORMATION:MIN.4-FOLD FULLY FORMED\n\n" +
                "MATERIAL IS RADIATION AND MERCURY FREE\n\n" +
                "GRAIN SIZE : 12 AND FINER");
        head.put("cNOTES2", "MILL TEST CERTIFICATE: ACC.TO EN 10204/3.1\n\n" +
                "MELTING PROCESS: E.A.F plus LF plus VACUUM TREATMENT + ESR\n\n" +
                "GRADE OF DEFORMATION:MIN.4-FOLD FULLY FORMED\n\n" +
                "MATERIAL IS RADIATION AND MERCURY FREE\n\n" +
                "GRAIN SIZE : 12 AND FINER");
        List<Map<String, Object>> batchList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> batchMap = new HashMap<>();
            batchMap.put("cHEATNO", "123456789" + i);
            batchMap.put("cSIZES", "100" + i);
            batchMap.put("Condition", "Condition" + i);
            batchMap.put("cPCS", 10000 + i);
            batchMap.put("dWeight", 1230.5f + i);
            batchMap.put("cHardness", 20 + i);
            batchMap.put("cGrainSize", 10 + i);
            batchMap.put("cFields1", i + "0.10" + i);
            batchMap.put("cFields2", i + "0.10" + i);
            batchMap.put("cFields3", i + "0.10" + i);
            batchMap.put("cFields4", i + "0.10" + i);
            batchMap.put("cFields5", i + "0.10" + i);
            batchMap.put("cFields6", i + "0.10" + i);
            batchMap.put("cFields7", i + "0.10" + i);
            batchMap.put("cFields8", i + "0.10" + i);
            batchMap.put("cFields9", i + "0.10" + i);
            batchMap.put("cFields10", i + "0.10" + i);
            batchMap.put("cFields11", i + "0.10" + i);
            batchMap.put("cFields12", i + "0.10" + i);
            batchMap.put("cFields13", i + "0.10" + i);
            batchMap.put("cFields14", i + "0.10" + i);
            batchMap.put("cFields15", i + "0.10" + i);
            batchMap.put("A_H", i + "1." + i);
            batchMap.put("A_T", i + "1." + i);
            batchMap.put("B_H", i + "1." + i);
            batchMap.put("B_T", i + "1." + i);
            batchMap.put("C_H", i + "1." + i);
            batchMap.put("C_T", i + "1." + i);
            batchMap.put("D_H", i + "1." + i);
            batchMap.put("D_T", i + "1." + i);
            batchList.add(batchMap);
        }
        Map<String, Object> refs = new HashMap<>();
        refs.put("cHardness", "<=120");
        refs.put("cGrainSize", "6 OR FINER");
        refs.put("C", "0.1-0.20");
        refs.put("Si", "0.110-0.210");
        refs.put("Mn", "0.110-0.220");
        refs.put("P", "0.110-0.230");
        refs.put("S", "0.110-0.240");
        refs.put("Cr", "0.110-0.250");
        refs.put("Mo", "0.110-0.260");
        refs.put("V", "0.110-0.270");
        refs.put("W", "0.110-0.280");
        refs.put("Co", "0.1110-0.290");
        refs.put("Cu", "0.1110-0.3010");
        refs.put("Ni", "0.1110-0.3110");
        refs.put("H2", "0.10-0.320");
        refs.put("O2", "0.10-0.330");
        refs.put("N2", "0.10-0.340");
        refs.put("A_H", "1.0");
        refs.put("A_T", "1.5");
        refs.put("B_H", "1.0");
        refs.put("B_T", "1.5");
        refs.put("C_H", "1.0");
        refs.put("C_T", "1.5");
        refs.put("D_H", "1.0");
        refs.put("D_T", "1.5");
        doMultiPdf(head, refs, batchList, "1");
        // createZhImage("江苏天工工具有限公司", "msyhbd", 18f, 200, 30);
        System.out.println(JSON.toJSONString(head));
        System.out.println(JSON.toJSONString(refs));
        System.out.println(JSON.toJSONString(batchList));
    }
}
