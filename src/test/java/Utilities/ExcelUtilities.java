package Utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtilities {
    
    private FileInputStream fis;
    private FileOutputStream fos;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private XSSFRow row;
    private XSSFCell cell;
    private String filePath;
    
    // Constructor
    public ExcelUtilities(String filePath) {
        this.filePath = filePath;
    }
    
    // Get row count
    public int getRowCount(String sheetName) {
        int rowCount = 0;
        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            rowCount = sheet.getLastRowNum() + 1;
            workbook.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rowCount;
    }
    
    // Get column count
    public int getColumnCount(String sheetName) {
        int colCount = 0;
        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            row = sheet.getRow(0);
            colCount = row.getLastCellNum();
            workbook.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return colCount;
    }
    
    // Get cell data by row and column index
    public String getCellData(String sheetName, int rowNum, int colNum) {
        String cellData = "";
        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            row = sheet.getRow(rowNum);
            
            if (row == null) {
                workbook.close();
                fis.close();
                return "";
            }
            
            cell = row.getCell(colNum);
            
            if (cell == null) {
                workbook.close();
                fis.close();
                return "";
            }
            
            CellType cellType = cell.getCellType();
            
            switch (cellType) {
                case STRING:
                    cellData = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellData = cell.getDateCellValue().toString();
                    } else {
                        cellData = String.valueOf((long) cell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN:
                    cellData = String.valueOf(cell.getBooleanCellValue());
                    break;
                case BLANK:
                    cellData = "";
                    break;
                default:
                    cellData = "";
            }
            
            workbook.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cellData;
    }
    
    // Get cell data by column name
    public String getCellData(String sheetName, String columnName, int rowNum) {
        int colNum = -1;
        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            row = sheet.getRow(0);
            
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().equalsIgnoreCase(columnName)) {
                    colNum = i;
                    break;
                }
            }
            workbook.close();
            fis.close();
            
            if (colNum == -1) {
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getCellData(sheetName, rowNum, colNum);
    }
    
    // Get all sheet data as 2D array
    public Object[][] getSheetData(String sheetName) {
        Object[][] data = null;
        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            
            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(0).getLastCellNum();
            
            data = new Object[rowCount][colCount];
            
            for (int i = 1; i <= rowCount; i++) {
                row = sheet.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    if (row == null) {
                        data[i - 1][j] = "";
                        continue;
                    }
                    cell = row.getCell(j);
                    if (cell == null) {
                        data[i - 1][j] = "";
                        continue;
                    }
                    
                    CellType cellType = cell.getCellType();
                    
                    switch (cellType) {
                        case STRING:
                            data[i - 1][j] = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            data[i - 1][j] = String.valueOf((long) cell.getNumericCellValue());
                            break;
                        case BOOLEAN:
                            data[i - 1][j] = String.valueOf(cell.getBooleanCellValue());
                            break;
                        default:
                            data[i - 1][j] = "";
                    }
                }
            }
            workbook.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    // Set cell data
    public boolean setCellData(String sheetName, int rowNum, int colNum, String value) {
        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            
            row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            
            cell = row.getCell(colNum);
            if (cell == null) {
                cell = row.createCell(colNum);
            }
            
            cell.setCellValue(value);
            
            fis.close();
            fos = new FileOutputStream(filePath);
            workbook.write(fos);
            fos.close();
            workbook.close();
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}