package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelReader {

    public static String getCellData(String filePath, String sheetName, int rowNum, int colNum) {
        String cellData = "";
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);
            if (row == null) return "";

            Cell cell = row.getCell(colNum);
            if (cell == null) return "";

            switch (cell.getCellType()) {
                case STRING:
                    cellData = cell.getStringCellValue();
                    break;

                case NUMERIC:
                    double num = cell.getNumericCellValue();
                    // Nếu là số nguyên thì bỏ .0
                    if (num == (long) num) {
                        cellData = String.valueOf((long) num);
                    } else {
                        cellData = String.valueOf(num);
                    }
                    break;

                case BOOLEAN:
                    cellData = String.valueOf(cell.getBooleanCellValue());
                    break;

                case FORMULA:
                    cellData = cell.getCellFormula();
                    break;

                default:
                    cellData = "";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cellData.trim();
    }
}
