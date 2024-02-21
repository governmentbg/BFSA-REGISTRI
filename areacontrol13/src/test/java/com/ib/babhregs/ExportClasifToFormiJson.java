package com.ib.babhregs;

import java.util.Date;
import java.util.List;

import com.ib.babhregs.system.SystemData;
import com.ib.system.ActiveUser;
import com.ib.system.db.dao.SystemClassifOpisDAO;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.db.dto.SystemClassifOpis;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.FileUtils;
import com.ib.system.utils.SysClassifUtils;

public class ExportClasifToFormiJson {

	public static void main(String[] args) {
		
		SystemData sd = new SystemData();
		
		String tempDir= "D:\\_Заяления\\ВЛП";
		
		String json = "";
		
		Integer classifId = 708  ;
		
		try {
			
			SystemClassifOpis opisVod = new SystemClassifOpisDAO(ActiveUser.DEFAULT).findById(classifId); //JPA.getUtil().getEntityManager().find(SystemClassifOpis.class, list.getCodeClassifVod());
			System.out.println(opisVod.getTranslations().get(0).getTekst());
			String nameClassif = opisVod.getTranslations().get(0).getTekst();
			
			nameClassif = nameClassif.replace("/", "_");
			String fullPathName = tempDir + "\\"+classifId + "_" + nameClassif+".txt";
			
			//List<SystemClassif> classif = sd.getSysClassification(539, new Date(), 1);
			List<SystemClassif> classif = sd.getSysClassification(classifId, new Date(), 1);
			
			
			SysClassifUtils.doSortClassifPrev(classif);			
			
			json += "values = [";
			for ( SystemClassif item : classif) {
				json += "\r\n\t{value: '"+ item.getCode()+"', label: '"+item.getTekst()+"'},";
			}
			if (json.endsWith(",")) {
				json = json.substring(0, json.length()-1);
			}
			json += "\r\n]";
			
			
			System.out.println(json);
			FileUtils.writeBytesToFile(fullPathName, json.getBytes());
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	
		
	}

}
