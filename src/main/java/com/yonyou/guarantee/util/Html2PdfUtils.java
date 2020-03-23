package com.yonyou.guarantee.util;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.attach.impl.layout.HtmlPageBreak;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.font.FontProvider;

import java.io.IOException;
import java.util.List;

public class Html2PdfUtils {
    /**
     * 字体所在目录
     */
    private static final String FONT_RESOURCE_DIR = "/font";

    /**
     * @param htmlContent html文本
     * @param dest        目的文件路径，如 /xxx/xxx.pdf
     * @throws IOException IO异常
     */
    public static void createPdf(String htmlContent, String dest) throws IOException {
        ConverterProperties props = new ConverterProperties();
        props.setCharset("UFT-8");
        FontProvider fp = new FontProvider();
        fp.addStandardPdfFonts();
        // .ttf 字体所在目录
        String fonts = Html2PdfUtils.class.getResource(FONT_RESOURCE_DIR).getPath();
        fp.addDirectory(fonts);
        props.setFontProvider(fp);
        // html中使用的图片等资源目录（图片也可以直接用url或者base64格式而不放到资源里）
        // props.setBaseUri(resources);
        List<IElement> elements = HtmlConverter.convertToElements(htmlContent, props);
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf, PageSize.A4.rotate(), false);
        for (IElement element : elements) {
            // 分页符
            if (element instanceof HtmlPageBreak) {
                document.add((HtmlPageBreak) element);
                //普通块级元素
            } else {
                document.add((IBlockElement) element);
            }
        }
        document.close();
    }

    public static void main(String[] args) throws IOException {
        createPdf("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "    <style>\n" +
                "      body {\n" +
                "            font-size: 14px;\n" +
                "            font-family: \"SimSun\", serif;\n" +
                "        }\n" +
                "\n" +
                "        .table {\n" +
                "            background-color: #ffffff;\n" +
                "            width: 1040px;\n" +
                "            border-collapse: collapse;\n" +
                "            border: 1px solid #000000;\n" +
                "            border-spacing: 0;\n" +
                "        }\n" +
                "\n" +
                "        tr {\n" +
                "        }\n" +
                "\n" +
                "        td {\n" +
                "            border-collapse: collapse;\n" +
                "            border: 1px solid #000000;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div><h1>模具钢(圆钢)产品质量证明书</h1></div>\n" +
                "<div>\n" +
                "    <table class=\"table\">\n" +
                "        <tr>\n" +
                "            <td>钢 号<br/>Steel Grade</td>\n" +
                "            <td>熔 炼 炉 号<br/>Melting Furnace</td>\n" +
                "            <td>热 处 理 炉 号<br/>Heat Treatment Lot</td>\n" +
                "            <td>规格（mm）<br/>Size</td>\n" +
                "            <td>件数<br/>Piece</td>\n" +
                "            <td>重量（kg）<br/>Weight</td>\n" +
                "            <td>交货状态<br/>Condition Of<br/>Delivery</td>\n" +
                "            <td>技术标准<br/>Technology Standard</td>\n" +
                "        </tr>\n" +
                "\n" +
                "    </table>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>", "d:/1.pdf");
    }
}
