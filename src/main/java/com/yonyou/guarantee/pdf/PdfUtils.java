package com.yonyou.guarantee.pdf;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.barcodes.qrcode.EncodeHintType;
import com.itextpdf.barcodes.qrcode.ErrorCorrectionLevel;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@PropertySource(value = "classpath:config/setting.properties")
public class PdfUtils {
    @Value("${tg.pdf.path}")
    private String pdfPath;
    @Value("${tg.pdf.url}")
    private String pdfUrl;
    private final static int PDF_PAGE_SIZE = 9;
    private final static String[] NURB_KEYS = new String[]{"C", "Si", "Mn", "P", "S", "W", "Mo", "Cr", "V", "Cu", "Ni", "Co", "Al", "Pb", "Sn"};
    private final static String[] ADDITIONAL_KEYS = new String[]{"Co", "Al", "Pb", "Sn", "Ti", "B", "Nb"};

    public String genSinglePdf(Map<String, Object> map, String certPrefix) throws IOException {
        PdfReader pdfReader = new PdfReader(Objects.requireNonNull(PdfUtils.class.getClassLoader().getResourceAsStream("pdf/" + certPrefix + ".pdf")));

        //String pdfName = System.currentTimeMillis() + ".pdf";
        String pdfName = certPrefix + map.get("cCertificateNO") + ".pdf";
        File pdfFile = new File(pdfPath + "/" + pdfName);
        if (pdfFile.exists() && !pdfFile.canWrite()) {
            throw new IOException("服务器上的文件不可写：" + pdfName);
        }
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(pdfFile));
        //1、创建pdf文件
        PdfDocument pdf = new PdfDocument(pdfReader, pdfWriter);
        //2、创建中文字体
        PdfFont f2 = PdfFontFactory.createFont(PdfUtils.class.getResource("/font").getPath() + "/SimSun.ttf", PdfEncodings.IDENTITY_H, true);
        pdf.addFont(f2);

        //3、获取pdf模板中的域值信息
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
        Map<String, PdfFormField> fieldMap = form.getFormFields();
        String cCertificateNO = "";
        int eCount = 0;
        for (String key : ADDITIONAL_KEYS) {
            if (eCount >= 4) break;
            String v = (String) map.get(key);
            if (!StringUtils.isEmpty(v) && !"0".equals(v)) {
                map.put("label_e" + eCount, key);
                map.put("e" + eCount, v);
                eCount++;
            } else {
                map.remove(key);
            }
        }
        for (String key : map.keySet()) {
            String value = map.get(key) == null ? null : map.get(key).toString();
            PdfFormField formField = fieldMap.get(key);
            if (formField == null || StringUtils.isEmpty(value)) {
                continue;
            }
            if (key.equalsIgnoreCase("cCertificateNO")) {
                cCertificateNO = certPrefix + value;
                value = cCertificateNO;
            }
            //4、判断文本域是否超出宽度   且文本域是单行
          /*  if (!compareWidth(f2, formField, value) && !formField.isMultiline()) {
                System.out.println(MessageFormat.format("value width was out of text width fieldName:{0}", key));
            }*/

            //5、填充信息
            formField.setFont(f2);
            formField.setFontAndSize(f2, 10f);
            formField.setValue(value);
        }

        //6、设置文本不可编辑
        form.flattenFields();
        // Image image = new Image(ImageDataFactory.create(IOUtils.toByteArray(new FileInputStream("d:/mytest/qrcode.png"))));
        Document doc = new Document(pdf, PageSize.A4.rotate());

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BarcodeQRCode qrcode = new BarcodeQRCode("url:http://www.tggj.cn/tg/?id=" + cCertificateNO, hints);
        Image image = new Image(qrcode.createFormXObject(pdf));
        image.setHeight(110);
        image.setWidth(110);
        //image.scaleToFit(50, 50);
        image.setFixedPosition(670, 450);
        doc.add(image);
        doc.close();
        pdfWriter.flush();
        pdfWriter.close();
        pdfReader.close();
        pdf.close();
        return pdfUrl + pdfName;
    }

    public String genMultiPdf(Map<String, Object> head, Map<String, Object> ref, List<Map<String, Object>> batchList) throws IOException {
        String iSteelType = String.valueOf(head.get("iSteelType"));
        String certPrefix = "1".equals(iSteelType) ? "TGMY" : "TGMB";
        if (CollectionUtils.isEmpty(batchList) || batchList.size() <= PDF_PAGE_SIZE) {
            return doMultiPdf(head, ref, batchList, certPrefix);
        }
        int size = batchList.size();
        int count = size / PDF_PAGE_SIZE;
        if (size % PDF_PAGE_SIZE > 0) count++;
        List<String> pdfList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int endIndex = (i + 1) * PDF_PAGE_SIZE;
            if (endIndex > size) endIndex = size;
            List<Map<String, Object>> subList = batchList.subList(i * PDF_PAGE_SIZE, endIndex);
            pdfList.add(doMultiPdf(head, ref, subList, certPrefix));
        }
        return this.mergePdfFiles(pdfList);
    }

    private String doMultiPdf(Map<String, Object> head, Map<String, Object> ref, List<Map<String, Object>> batchList, String certPrefix) throws IOException {
        PdfReader pdfReader = new PdfReader(Objects.requireNonNull(PdfUtils.class.getClassLoader().getResourceAsStream("pdf/" + certPrefix + ".pdf")));
        String pdfName = System.currentTimeMillis() + ".pdf";
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(pdfPath + "/" + pdfName));
        //1、创建pdf文件
        PdfDocument pdf = new PdfDocument(pdfReader, pdfWriter);
        //2、创建中文字体
        //PdfFont f2 = null;
        // f2 = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H", true);
        PdfFont f2 = PdfFontFactory.createFont(PdfUtils.class.getResource("/font").getPath() + "/SimSun.ttf", PdfEncodings.IDENTITY_H, true);
        // PdfFontFactory.createFont("ADOBESONGSTD-LIGHT.OTF", PdfEncodings.IDENTITY_H,true);
        pdf.addFont(f2);

        //3、获取pdf模板中的域值信息
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
        Map<String, PdfFormField> fieldMap = form.getFormFields();
        String cCertificateNO = "";
        for (String key : head.keySet()) {
            String value = head.get(key) == null ? null : head.get(key).toString();
            PdfFormField formField = fieldMap.get(key);
            if (formField == null || StringUtils.isEmpty(value)) {
                continue;
            }
            if (key.equalsIgnoreCase("cCertificateNO")) {
                cCertificateNO = certPrefix + value;
                value = cCertificateNO;
            }
            if (key.equalsIgnoreCase("cNOTES1") || key.equalsIgnoreCase("cNOTES2")) {
                value = value.replaceAll("\\n{2,}", "\n");
            }
            formField.setFont(f2);
            formField.setFontAndSize(f2, 8f);
            formField.setValue(value);
        }
        if (ref != null) {
            for (String nurbKey : NURB_KEYS) {
                String value = ref.get(nurbKey) == null ? null : ref.get(nurbKey).toString();
                PdfFormField formField = fieldMap.get("ref" + nurbKey);
                if (formField == null || StringUtils.isEmpty(value)) {
                    continue;
                }
                formField.setFont(f2);
                formField.setFontAndSize(f2, 6f);
                formField.setValue(value);
            }
        }
        if (!CollectionUtils.isEmpty(batchList)) {
            for (int i = 0; i < batchList.size(); i++) {
                Map<String, Object> map = batchList.get(i);
                for (String key : map.keySet()) {
                    String value = map.get(key) == null ? null : map.get(key).toString();
                    PdfFormField formField = fieldMap.get(key + i);
                    if (formField == null || StringUtils.isEmpty(value)) {
                        continue;
                    }
                    formField.setFont(f2);
                    formField.setFontAndSize(f2, 8f);
                    formField.setValue(value);
                }
            }
        }
        //6、设置文本不可编辑
        form.flattenFields();
        // Image image = new Image(ImageDataFactory.create(IOUtils.toByteArray(new FileInputStream("d:/mytest/qrcode.png"))));
        Document doc = new Document(pdf, PageSize.A4.rotate());

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BarcodeQRCode qrcode = new BarcodeQRCode("url:http://www.tggj.cn/tg/?id=" + cCertificateNO, hints);
        Image image = new Image(qrcode.createFormXObject(pdf));
        image.setHeight(100);
        image.setWidth(100);
        //image.scaleToFit(50, 50);
        image.setFixedPosition(715, 81);
        doc.add(image);
        doc.close();
        pdfWriter.flush();
        pdfWriter.close();
        pdfReader.close();
        pdf.close();
        return pdfUrl + pdfName;
    }

    public String mergePdfFiles(List<String> files) throws IOException {
        String newfile = System.currentTimeMillis() + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(pdfPath + "/" + newfile));
        PdfMerger merger = new PdfMerger(pdf);
        files.forEach(name -> {
            name = name.substring(name.lastIndexOf("/") + 1);
            try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(pdfPath + "/" + name))) {
                merger.merge(pdfDocument, 1, pdfDocument.getNumberOfPages());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pdf.close();
        return pdfUrl + newfile;
    }

    /**
     * 1、获取文本框的宽度   注意要减去左右的padding值   值为：PdfFormField.X_OFFSET 2、获取字符串宽度注意字体   需使用中文字体
     *
     * @param pdfFont   字体
     * @param formField 文本域
     * @param value     文本值
     * @return textWidth >= valueWidth return true else false
     */
    public static boolean compareWidth(PdfFont pdfFont, PdfFormField formField, String value) {
        //获取当前文本字体大小
        float fontSize = getFontSize(formField);
        PdfArray position = formField.getWidgets().get(0).getRectangle();
        float width = (float) (position.getAsNumber(2).getValue() - position.getAsNumber(0).getValue())
                - PdfFormField.X_OFFSET * 2;

        //获取当前文本值的宽度
        float strWidth = pdfFont.getWidth(value, fontSize);
        return width >= strWidth;
    }

    /**
     * 获取adobe中设置的字体大小
     *
     * @param formField
     * @return
     */
    private static float getFontSize(PdfFormField formField) {
        String defaultAppearance = formField.getDefaultAppearance().toString();
        String[] daTable = defaultAppearance.split(" ");
        return Float.parseFloat(daTable[PdfFormField.DA_SIZE]);

    }
}
