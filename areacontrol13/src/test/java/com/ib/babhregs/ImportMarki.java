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

public class ImportMarki {

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
			
			XSSFSheet sheet = workbook.getSheetAt(0);
			
			for (int curRow = 0; curRow <= sheet.getLastRowNum(); curRow++ ) {
			    Row row = sheet.getRow(curRow);
			    String name = row.getCell(0).getStringCellValue();
			    
			  
			    
			    if (SearchUtils.isEmpty(name)) {
			    	System.out.println("************************************** BUM");
			    	continue;
			    }
			    
			  
			    
			    
			    Integer code =  curRow +1;
			    
			    
			    SystemClassif item = new SystemClassif();
			    item.setCode(code);
			    item.setCodeClassif(583);			    
			    item.setCodeParent(0);
			    item.setCodePrev(curRow);
			    item.setDateOt(dat);
			    item.setDateReg(new Date());
			    item.setDopInfo(null);
			    item.setLevelNumber(1);
			    
			    Multilang langBG = new Multilang();
			    langBG.setTekst(name);
			    langBG.setLang(Constants.CODE_DEFAULT_LANG);
			    item.getTranslations().add(langBG);
				
				JPA.getUtil().begin();
				sdao.doSimpleSave(item);
				JPA.getUtil().commit();
				
			    System.out.println( code + ". " +  name);
			    
			    
			}
			
			
			
		} catch (Exception e) {
			JPA.getUtil().rollback();
			e.printStackTrace();
		}
		
		
		System.out.println("---------------END-------------------");

	}

}
