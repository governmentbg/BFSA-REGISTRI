package com.ib.babhregs.migr.vmp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;

import com.ib.system.db.JPA;
import com.ib.system.utils.SearchUtils;

public class UpdatePHForms {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
			
	
		try {
			
			HashMap<String, String> items = new HashMap<String, String>(); 
			
			ArrayList<Object[]> teksts =  (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery("select id, forma from temp_vmp").getResultList();
			
			
			
			
			
			
			XSSFWorkbook workbook = null;
			
			FileInputStream fis;
			try {
				File myFile = new File("D:\\_VLP\\Copy of eu 1 (002).xlsx");
				fis = new FileInputStream(myFile);
				workbook = new XSSFWorkbook (fis);
			} catch (FileNotFoundException e) {			
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			XSSFSheet sheet = workbook.getSheetAt(8);
			
			for (int curRow = 1; curRow <= sheet.getLastRowNum(); curRow++ ) {
			    Row row = sheet.getRow(curRow);
			    String code = row.getCell(0).getStringCellValue();
			    code = code.substring(0,code.indexOf("|"));
			    //System.out.println("*************" + row.getCell(0).getStringCellValue() + "   ****************** " + row.getLastCellNum() );
			    String bgName = null;
			    if (row.getCell(2) != null) {
			    	bgName = row.getCell(2).getStringCellValue();
			    }else {
			    	//System.out.println("******************************************************");
			    }
			    
			    if (! SearchUtils.isEmpty(bgName)) {
				    String bgNameClear = bgName.toUpperCase().trim().replace(",", "").replace("-", "").replace(" ", "");
				    items.put(bgNameClear, code);
				    
			    }
			    
			    //System.out.println(code + "\t" + bgName);
			}
			
			
			TreeSet<String> nfSet = new TreeSet<String>(); 
			HashMap<String, String> fMap = new HashMap<String, String>(); 
		   
		    for (Object[] row : teksts) {
		    	
		    	Object o = row[1];
		    	
		    	String tekst = SearchUtils.asString(o).toUpperCase().trim().replace(",", "").replace("-", "").replace(" ", "").replace("ТАБЛЕТКИ", "ТАБЛЕТКА");
		    	
		    	
		    	
		    	String code = items.get(tekst);
		    	
		    	if (code == null) {
		    		//System.out.println("not found: " + o);
		    		nfSet.add(SearchUtils.asString(o));
		    		fMap.put(SearchUtils.asString(o).trim().toUpperCase(), null);
		    	}else {
		    		fMap.put(SearchUtils.asString(o).trim().toUpperCase(), code);
		    	}
		    	
		    	
		    	
		    	
		    }
			
			
			    
			    
			
			
			for (String s : nfSet) {
				//System.out.println(s);
				
			}
			
			
			JPA.getUtil().begin();
			
			Iterator<Entry<String, String>> it = fMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				System.out.println(entry.getKey());
				JPA.getUtil().getEntityManager().createNativeQuery("INSERT INTO temp_precode_formi (tekst, code) VALUES (:tekst, :code)")
					.setParameter("tekst", entry.getKey())
					.setParameter("code", new TypedParameterValue(StandardBasicTypes.STRING, entry.getValue()))
					.executeUpdate();
			}
			JPA.getUtil().commit();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JPA.getUtil().rollback();
		}finally {
			JPA.getUtil().closeConnection();
		}
		
	
	}

}
