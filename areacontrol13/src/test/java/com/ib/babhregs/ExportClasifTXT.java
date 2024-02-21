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

public class ExportClasifTXT {

	public static void main(String[] args) {
		
		SystemData sd = new SystemData();
		
		
		
		Integer classifId = 672;
		
		try {
			
			
			List<SystemClassif> classif = sd.getSysClassification(classifId, new Date(), 1);
			
			
			SysClassifUtils.doSortClassifPrev(classif);			
			
			for (SystemClassif item : classif) {
				for (int i = 1; i < item.getLevelNumber(); i++) {
					System.out.print("\t");
				}
				//System.out.println(item.getTekst() + "(код: " + item.getCode() + ")" );
				System.out.println(item.getTekst() );
			}
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	
		
	}

}
