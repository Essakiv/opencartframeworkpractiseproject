package Utilities;

import org.testng.annotations.DataProvider;

public class DataProviderClass {
    
    private static final String TEST_DATA_PATH = System.getProperty("user.dir") + "/Testdata/Book1.xlsx";
    private static final String SHEET_NAME = "Sheet1";
    
    @DataProvider(name = "LoginData")
    public static Object[][] getLoginData() {
        ExcelUtilities excel = new ExcelUtilities(TEST_DATA_PATH);
        return excel.getSheetData(SHEET_NAME);
    }
    
    @DataProvider(name = "LoginCredentials")
    public static Object[][] getLoginCredentials() {
        ExcelUtilities excel = new ExcelUtilities(TEST_DATA_PATH);
        int rowCount = excel.getRowCount(SHEET_NAME);
        int colCount = excel.getColumnCount(SHEET_NAME);
        
        Object[][] data = new Object[rowCount - 1][colCount];
        
        for (int i = 1; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i - 1][j] = excel.getCellData(SHEET_NAME, i, j);
            }
        }
        return data;
    }
}