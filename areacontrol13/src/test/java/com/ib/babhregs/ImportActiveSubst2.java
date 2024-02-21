package com.ib.babhregs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellType;
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

public class ImportActiveSubst2 {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		
		
		try {
			
			
			SystemClassifDAO sdao = new SystemClassifDAO(ActiveUser.DEFAULT);
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			Date dat = sdf.parse("01.01.1900 00:00:00");
			
			
			
			SystemClassif item1 = new SystemClassif();
			item1.setCode(2000);
			item1.setCodeClassif(579);			    
			item1.setCodeParent(0);
			item1.setCodePrev(1000);
			item1.setDateOt(dat);
			item1.setDateReg(new Date());			
			item1.setLevelNumber(1);
		    
		    Multilang langBG1 = new Multilang();
		    langBG1.setTekst("Рискови вещества");
		    langBG1.setLang(Constants.CODE_DEFAULT_LANG);
		    item1.getTranslations().add(langBG1);
			
			JPA.getUtil().begin();
			sdao.doSimpleSave(item1);
			JPA.getUtil().commit();
			
			
			
			
			
			
			XSSFWorkbook workbook = null;
			
			FileInputStream fis;
			try {
				File myFile = new File("D:\\classif\\marki.xlsx");
				fis = new FileInputStream(myFile);
				workbook = new XSSFWorkbook (fis);
			} catch (FileNotFoundException e) {			
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			XSSFSheet sheet = workbook.getSheetAt(2);
			
			for (int curRow = 0; curRow <= sheet.getLastRowNum(); curRow++ ) {
			    Row row = sheet.getRow(curRow);
			    String name1 = row.getCell(0).getStringCellValue();
			    String name2 = row.getCell(1).getStringCellValue();
			    String dopInfo = row.getCell(2).getStringCellValue();
			    
			    
//			    if (row.getCell(3).getCellType() == CellType.STRING) {
//			    	kod = row.getCell(3).getStringCellValue();
//			    }else {			    
//			    	kod = ""  + row.getCell(3).getNumericCellValue();
//			    	kod = kod.replace(".0", "");
//				    while (kod.length() < 4){
//				    	kod = "0" + kod;
//				    }
//			    }
			    
			    if (SearchUtils.isEmpty(name1)) {
			    	System.out.println("************************************** BUM");
			    	break;
			    }
			    
			    String name = name1;
			    if (name1.trim().equals("-")) {
			    	name = name2;
			    }
			    
			    if (name.trim().equals("-")) {
			    	System.out.println("************************************** BUmBUMBUMMMMMMM2");
			    	break;
			    }
			    
			    
			    Integer code =  curRow +1;
			    
			    
			    SystemClassif item = new SystemClassif();
			    item.setCode(code);
			    item.setCodeClassif(579);			    
			    item.setCodeParent(item1.getCode());
			    item.setCodePrev(curRow);
			    item.setDateOt(dat);
			    item.setDateReg(new Date());
			    item.setDopInfo(dopInfo);
			    item.setLevelNumber(2);
			    
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
