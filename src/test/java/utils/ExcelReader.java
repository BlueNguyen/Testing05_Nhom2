package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelReader {

    // 📘 Đọc 1 ô (rowIndex, colIndex)
    public static String getCellData(String filePath, String sheetName, int rowIndex, int colIndex) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null)
                throw new RuntimeException("❌ Sheet '" + sheetName + "' not found");

            Row row = sheet.getRow(rowIndex);
            if (row == null) return "";

            Cell cell = row.getCell(colIndex);
            if (cell == null) return "";

            DataFormatter formatter = new DataFormatter();
            return formatter.formatCellValue(cell).trim();

        } catch (IOException e) {
            throw new RuntimeException("❌ Error reading Excel file: " + e.getMessage(), e);
        }
    }

    // 📗 Đọc toàn bộ sheet → trả về Object[][] (dùng cho DataProvider)
    public static Object[][] getData(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null)
                throw new RuntimeException("❌ Sheet '" + sheetName + "' not found");

            int rowCount = sheet.getPhysicalNumberOfRows();
            int colCount = sheet.getRow(0).getPhysicalNumberOfCells();

            // Bỏ hàng tiêu đề, bắt đầu từ hàng 1
            Object[][] data = new Object[rowCount - 1][colCount];

            DataFormatter formatter = new DataFormatter();

            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    Cell cell = (row != null) ? row.getCell(j) : null;
                    data[i - 1][j] = (cell != null) ? formatter.formatCellValue(cell).trim() : "";
                }
            }

            return data;

        } catch (IOException e) {
            throw new RuntimeException("❌ Error reading Excel file: " + e.getMessage(), e);
        }
    }
}
