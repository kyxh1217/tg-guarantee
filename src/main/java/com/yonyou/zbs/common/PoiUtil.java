package com.yonyou.zbs.common;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PoiUtil {
    private final static DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    private final static DecimalFormat df = new DecimalFormat("0.0000");

    public static List<Map<String, String>> read(MultipartFile file) throws Exception {
        try {

            //最终返回数据
            List<Map<String, String>> list = new ArrayList<>();
            if (file == null) {
                return list;
            }
            InputStream is = file.getInputStream();
            String name = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
            Workbook workbook = WorkbookFactory.create(is);
            //得到一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            //获得数据的总行数
            int totalRowNum = sheet.getLastRowNum();
            //获得总列数
            for (int i = 0; i <= totalRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                //获得第i行对象
                Map<String, String> map = readRow(row);
                if (map != null) {
                    list.add(map);
                }
            }
            return list;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private static Map<String, String> readRow(Row row) {
        Map<String, String> map = new HashMap<>();
        //获得第i行对象
        //如果一行里的所有单元格都为空则不放进list里面
        Cell cell;
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
            cell = row.getCell(i);
            if (cell != null) {
                map.put("field" + i, getXCellVal(cell));
            }
        }//for
        if (map.keySet().isEmpty()) {
            return null;
        }
        return map;
    }

    /**
     * @param cell
     * @return String
     * 获取单元格中的值
     */

    private static String getXCellVal(Cell cell) {
        String val;
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    val = fmt.format(cell.getDateCellValue()); //日期型
                } else {
                    val = df.format(cell.getNumericCellValue()); //数字型
                    // 去掉多余的0，如最后一位是.则去掉
                    val = val.replaceAll("0+?$", "").replaceAll("[.]$", "");
                }
                break;
            case STRING: //文本类型
            case BLANK: //空白
                val = cell.getStringCellValue();
                break;
            case BOOLEAN: //布尔型
                val = String.valueOf(cell.getBooleanCellValue());
                break;
            case ERROR: //错误
                val = "";
                break;
            case FORMULA: //公式
                try {
                    val = String.valueOf(cell.getStringCellValue());
                } catch (IllegalStateException e) {
                    val = String.valueOf(cell.getNumericCellValue());
                }
                break;
            default:
                val = cell.getRichStringCellValue() == null ? null : cell.getRichStringCellValue().toString();
        }
        return val;
    }
}
