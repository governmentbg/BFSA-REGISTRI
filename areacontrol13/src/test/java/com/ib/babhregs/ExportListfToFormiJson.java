package com.ib.babhregs;

import java.util.Date;
import java.util.List;

import com.ib.babhregs.system.SystemData;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.SystemClassifOpisDAO;
import com.ib.system.db.dto.SyslogicListOpisEntity;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.db.dto.SystemClassifOpis;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.FileUtils;
import com.ib.system.utils.SysClassifUtils;
import com.itextpdf.io.util.FileUtil;

public class ExportListfToFormiJson {

	public static void main(String[] args) {
		
		try {
		
			SystemData sd = new SystemData();
			
			
			String tempDir= "D:\\classif\\temp";
			String nameClassifVod = "";
			String nameClassifPod = "";
			
			List<SystemClassif> classifV = null;
			
			
			Integer listId = 52;
			
			SyslogicListOpisEntity list = JPA.getUtil().getEntityManager().find(SyslogicListOpisEntity.class, listId);
			
			if (list.getCodeClassifVod() != null) {
				System.out.println(list.getCodeClassifVod());
				SystemClassifOpis opisVod = new SystemClassifOpisDAO(ActiveUser.DEFAULT).findById(list.getCodeClassifVod()); //JPA.getUtil().getEntityManager().find(SystemClassifOpis.class, list.getCodeClassifVod());
				System.out.println(opisVod.getTranslations().get(0).getTekst());
				nameClassifVod = opisVod.getTranslations().get(0).getTekst();
				classifV = sd.getSysClassification(list.getCodeClassifVod(), new Date(), 1);
			}
			
			if (list.getCodeClassifPod() != null) {
				System.out.println(list.getCodeClassifPod());
				SystemClassifOpis opisPod = new SystemClassifOpisDAO(ActiveUser.DEFAULT).findById(list.getCodeClassifPod()); //JPA.getUtil().getEntityManager().find(SystemClassifOpis.class, list.getCodeClassifVod());
				System.out.println(opisPod.getTranslations().get(0).getTekst());
				nameClassifPod = opisPod.getTranslations().get(0).getTekst();
			}
			System.out.println();
			System.out.println();
			System.out.println();
		    String filename = "";
			if (classifV != null ) {
				for (SystemClassif itemV : classifV) {
					filename = nameClassifPod + " за " +nameClassifVod + " с код " + itemV.getCode() + ".txt";
					System.out.println(filename);
					
					List<SystemClassif> classif = sd.getClassifByListVod(listId, itemV.getCode(), 1, new Date());
					
					String json = "values = [";
					for ( SystemClassif item : classif) {
						json += "\r\n\t{value: '"+ item.getCode()+"', label: '"+item.getTekst()+"'},";
					}
					if (json.endsWith(",")) {
						json = json.substring(0, json.length()-1);
					}
					json += "\r\n]";
					
					System.out.println(json);
					FileUtils.writeBytesToFile(tempDir+"\\"+filename, json.getBytes());
					
				}
			}
		
		
		
		//sd.getClassifByListVod(listId, listId, listId, null);
		
		
		
			
			//sd.getClassifByListVod(null, null, null, null)
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		
	
		
	}

}
