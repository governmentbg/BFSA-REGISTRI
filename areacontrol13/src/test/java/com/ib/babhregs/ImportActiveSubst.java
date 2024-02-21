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

public class ImportActiveSubst {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		
		
		try {
			
			SystemClassifDAO sdao = new SystemClassifDAO(ActiveUser.DEFAULT);
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			Date dat = sdf.parse("01.01.1900 00:00:00");
			
			XSSFWorkbook workbook = null;
			
			FileInputStream fis;
			try {
				File myFile = new File("D:\\classif\\big.xlsx");
				fis = new FileInputStream(myFile);
				workbook = new XSSFWorkbook (fis);
			} catch (FileNotFoundException e) {			
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			XSSFSheet sheet = workbook.getSheetAt(1);
			
			for (int curRow = 0; curRow <= sheet.getLastRowNum(); curRow++ ) {
			    Row row = sheet.getRow(curRow);
			    String eng = row.getCell(0).getStringCellValue();
			    String latin = row.getCell(1).getStringCellValue();
			    String bul = row.getCell(2).getStringCellValue();
			    String kod = null;
			    
			    if (row.getCell(3).getCellType() == CellType.STRING) {
			    	kod = row.getCell(3).getStringCellValue();
			    }else {			    
			    	kod = ""  + row.getCell(3).getNumericCellValue();
			    	kod = kod.replace(".0", "");
				    while (kod.length() < 4){
				    	kod = "0" + kod;
				    }
			    }
			    
			    
			    
			    Integer code =  curRow +1;
			    
			    
			    SystemClassif item = new SystemClassif();
			    item.setCode(code);
			    item.setCodeClassif(578);
			    item.setCodeExt(kod);
			    item.setCodeParent(0);
			    item.setCodePrev(curRow);
			    item.setDateOt(dat);
			    item.setDateReg(new Date());
			    item.setDopInfo(latin);
			    item.setLevelNumber(1);
			    
			    Multilang langBG = new Multilang();
			    langBG.setTekst(bul);
			    langBG.setLang(Constants.CODE_DEFAULT_LANG);
			    item.getTranslations().add(langBG);
				
				Multilang langEn = new Multilang();
				langEn.setTekst(eng);
				langEn.setLang(Constants.CODE_LANG_EN);
				item.getTranslations().add(langEn);
			    
				JPA.getUtil().begin();
				//sdao.doSimpleSave(item);
				JPA.getUtil().commit();
				
			    System.out.println( code + ". " +  eng  + "\t\t\t" + latin);
			    
			    
			}
			
			
			
		} catch (Exception e) {
			JPA.getUtil().rollback();
			e.printStackTrace();
		}
		
		
		System.out.println("---------------END-------------------");

	}

}
