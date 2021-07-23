package com.yonyou.zbs.util;

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
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SingleBatchPdfUtils {
    private final static String[] ADDITIONAL_KEYS = new String[]{"Co", "Al", "Pb", "Sn", "Ti", "B", "Nb"};
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

    public static String genSinglePdf(Map<String, Object> map, String certPrefix) throws IOException {
        PdfReader pdfReader = new PdfReader(Objects.requireNonNull(SingleBatchPdfUtils.class.getClassLoader().getResourceAsStream("pdf/" + certPrefix + ".pdf")));

        //String pdfName = System.currentTimeMillis() + ".pdf";
        String pdfName = certPrefix + map.get("cCertificateNO") + ".pdf";
        File pdfFile = new File(SettingsUtils.getPdfPath() + "/" + pdfName);
        if (pdfFile.exists() && !pdfFile.canWrite()) {
            throw new IOException("服务器上的文件不可写：" + pdfName);
        }
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(pdfFile));
        //1、创建pdf文件
        PdfDocument pdf = new PdfDocument(pdfReader, pdfWriter);
        //2、创建中文字体
        URL fontPath = Objects.requireNonNull(SingleBatchPdfUtils.class.getResource("/static/font"));
        PdfFont f2 = PdfFontFactory.createFont(fontPath.getPath() + "/SimSun.ttf", PdfEncodings.IDENTITY_H);
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
        BarcodeQRCode qrcode = new BarcodeQRCode("http://www.tggj.cn/tg/?id=" + cCertificateNO, hints);
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
        return SettingsUtils.getPdfUrl() + pdfName;
    }
}
