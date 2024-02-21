package com.ib.babhregs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ib.babhregs.system.SystemData;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;

public class GenerateActiveSubsTranskode {

	public static void main(String[] args) throws DbErrorException {

		XSSFWorkbook workbook = null;
		
		FileInputStream fis;
		try {
			File myFile = new File("D:\\classif\\eu.xlsx");
			fis = new FileInputStream(myFile);
			workbook = new XSSFWorkbook (fis);
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		XSSFSheet sheet = workbook.getSheetAt(1);
		Integer codeClassif = 578;
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		SystemData sd = new SystemData();
		
		List<SystemClassif> classif = sd.getSysClassification(codeClassif, new Date(), 2);
		
		for (SystemClassif item : classif) {
			
			String codeExt = null;
			for (int curRow = 1; curRow <= sheet.getLastRowNum(); curRow++ ) {
			    Row row = sheet.getRow(curRow);
			   
			    String tekst = row.getCell(1).getStringCellValue();
			    //System.out.println(item.getTekst() + " <<<<<<<>>>>>>>>>> "  + tekst);
			    if (tekst.equalsIgnoreCase(item.getTekst())){
			    	 codeExt = row.getCell(0).getStringCellValue();
			    	 codeExt = codeExt.substring(0, codeExt.indexOf("|"));
			    	 break;
			    }			    
			}
			System.out.println(item.getDopInfo() + " -----> " + codeExt);
			
			
			
		}
		
		
		
		
		
		

	}

}
