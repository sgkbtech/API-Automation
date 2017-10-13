package com.ge.current.em.automation.apm.e2e;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

public class ExcelUtils {

	public static String[][] getExcelData(String fileName, String sheetName) {

		try {
			FileInputStream file = new FileInputStream(new File(fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheet(sheetName);
			int noofRows = sheet.getPhysicalNumberOfRows();
			int noofCols = sheet.getRow(0).getPhysicalNumberOfCells();
			String[][] excelContent = new String[noofRows][noofCols];
			Iterator<Row> rowIterator = sheet.iterator();

			for (int i = 0; i < noofRows; i++) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				for (int c = 0; c < noofCols; c++) {
					Cell cell = cellIterator.next();

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:

						SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
						excelContent[i][c] = sdf.format(cell.getDateCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						excelContent[i][c] = cell.getStringCellValue();
						break;
					}
					System.out.println("row: " + i + "col : " + c + "content:" + excelContent[i][c]);

				}

			}
			file.close();
			return excelContent;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider(name = "Assetdetails")
	public static Object[][] assetdata() {
		Object[][] arrayObject = getExcelData("E://Assetdetails.xlsx", "Sheet1");
		return arrayObject;
	}

	public   void  writeIntoExcel(String fileName, Map<String, Object> energyusage) throws IOException {
		// WritableWorkbook wworkbook = Workbook.createWorkbook(new
		// File("D:\\"+fileName+".xls"));
		// WritableSheet wsheet = wworkbook.createSheet("First Sheet", 0);
		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet sheet = workbook.createSheet("First Sheet");

		int rownum = 0;
		for (Entry<String, Object> entry : energyusage.entrySet()) {
			/*
			 * Label lbl1 = new Label(0, i, entry.getKey().toString()); Label
			 * lbl2 = new Label(1, i, entry.getValue().toString()); i++;
			 * wsheet.addCell(lbl1); wsheet.addCell(lbl2);
			 */
			Row row = sheet.createRow(rownum++);
			Cell cell = row.createCell(0);
			cell.setCellValue(entry.getKey().toString());
			Cell cell1 = row.createCell(1);
			cell1.setCellValue(entry.getValue().toString());
		}
		try {
			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(new File("D:\\" + fileName + ".xlsx"));
			workbook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  static void  writeAssertDetailsIntoExcel(String fileName, Map<String, Object> assertDetail,int rowCnt) throws IOException, EncryptedDocumentException, InvalidFormatException {
		
		 //InputStream inp = new FileInputStream(new File("D:\\" + fileName + ".xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet sheet = workbook.createSheet("First Sheet");

		
		for(int rowNum = 0; rowNum < rowCnt; rowNum++){
		    Row row = sheet.createRow(rowNum);
		    Cell cell = row.createCell(0);
		    cell.setCellValue(assertDetail.get("Asset"+rowNum).toString());
		    
		    Cell cell1 = row.createCell(1);
		    cell1.setCellValue(assertDetail.get("SiteId"+rowNum).toString());
		    
		    Cell cell2 = row.createCell(2);
		    cell2.setCellValue(assertDetail.get("Sitename"+rowNum).toString());
		    
		    
		    Cell cell3 = row.createCell(3);
		    cell3.setCellValue(assertDetail.get("Insdate"+rowNum).toString());
		    	    
		}
		    try {
				FileOutputStream out = new FileOutputStream(new File("D:\\" + fileName + ".xlsx"));
				workbook.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
