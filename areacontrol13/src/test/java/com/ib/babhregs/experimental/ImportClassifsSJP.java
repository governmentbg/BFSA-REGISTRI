package com.ib.babhregs.experimental;

import java.io.File;

import com.ib.indexui.system.Constants;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.SystemClassifDAO;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.FileUtils;
import com.ib.system.utils.Multilang;

public class ImportClassifsSJP {

	public static void main(String[] args) {  
		
		
		SystemClassifDAO sdao = new SystemClassifDAO(ActiveUser.DEFAULT);
		int codeClassif = 531;
		int maxCode = 186;
			
        try {
            File file = new File("D:\\classif\\СЖП.txt");
            
            byte[] bytes = FileUtils.getBytesFromFile(file);
          
            String txt = new String(bytes);
            
            String[] spl = txt.split("\r\n");
            
            
            int codePrev = 0;
            
    		JPA.getUtil().begin();
            
            for (String tek : spl) {
            	
            	SystemClassif itemS = new SystemClassif();
            	
                itemS.setLevelNumber(2);
                itemS.setTekst(tek);
                itemS.setCodePrev(codePrev);
                itemS.setCodeParent(105);
                itemS.setDateOt(DateUtils.systemMinDate());
                itemS.setCode(maxCode);
                
                Multilang lang = new Multilang();
				lang.setTekst(itemS.getTekst());
				lang.setLang(Constants.CODE_DEFAULT_LANG);
				itemS.getTranslations().add(lang);
				itemS.setCodeClassif(codeClassif);
        
				sdao.doSimpleSave(itemS);
				
				codePrev = itemS.getCode();
				maxCode++;
				
            }
				
			JPA.getUtil().commit();
			
		} catch (Exception e) {
			JPA.getUtil().rollback();
			e.printStackTrace();
		}
     
        
        
        
		
	}
	

}
