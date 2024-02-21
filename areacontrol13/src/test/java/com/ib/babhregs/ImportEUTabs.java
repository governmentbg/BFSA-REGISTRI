package com.ib.babhregs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ib.indexui.system.Constants;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.SystemClassifDAO;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.utils.Multilang;
import com.ib.system.utils.SearchUtils;

public class ImportEUTabs {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		
		
		try {
			
			
			SystemClassifDAO sdao = new SystemClassifDAO(ActiveUser.DEFAULT);
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			Date dat = sdf.parse("01.01.1900 00:00:00");
			
			
			
			
			
			
			
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
			XSSFSheet sheet = workbook.getSheetAt(8);
			Integer codeClassif = 693;
			//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			
			for (int curRow = 1; curRow <= sheet.getLastRowNum(); curRow++ ) {
			    Row row = sheet.getRow(curRow);
			    String codeExt = row.getCell(0).getStringCellValue();
			    String tekst = row.getCell(1).getStringCellValue();
			    
			    codeExt = codeExt.substring(0, codeExt.indexOf("|"));
			    
			  
			    
			    if (SearchUtils.isEmpty(tekst)) {
			    	System.out.println("************************************** BUM");
			    	continue;
			    }
			    
			  
			    
			    
			    Integer code =  curRow;
			    
			    
			    SystemClassif item = new SystemClassif();
			    item.setCode(code);
			    item.setCodeClassif(codeClassif);			    
			    item.setCodeParent(0);
			    item.setCodePrev(curRow-1);
			    item.setDateOt(dat);
			    item.setDateReg(new Date());
			    item.setDopInfo(null);
			    item.setLevelNumber(1);
			    item.setCodeExt(codeExt);
			    
			    Multilang langBG = new Multilang();
			    langBG.setTekst(tekst);
			    langBG.setLang(Constants.CODE_DEFAULT_LANG);
			    item.getTranslations().add(langBG);
			    
//			    Multilang langEN = new Multilang();
//			    langEN.setTekst(tekst);
//			    langEN.setLang(2);
//			    item.getTranslations().add(langEN);
				
				JPA.getUtil().begin();
				sdao.doSimpleSave(item);
				JPA.getUtil().commit();
				
			    System.out.println( code + ". " +  tekst + "  ---> " + codeExt);
			    
			    
			}
			
			
			
		} catch (Exception e) {
			JPA.getUtil().rollback();
			e.printStackTrace();
		}
		
		
		System.out.println("---------------END-------------------");

	}

}
