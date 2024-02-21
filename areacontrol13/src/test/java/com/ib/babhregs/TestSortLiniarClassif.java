package com.ib.babhregs;

import java.util.Date;
import java.util.List;

import com.ib.babhregs.system.SystemData;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SysClassifUtils;

public class TestSortLiniarClassif {

	public static void main(String[] args) {
	
		try {
			
			int codeClassif = 677;
			
			SystemData sd = new SystemData();
			List<SystemClassif> classif = sd.getSysClassification(codeClassif,new Date(),1);
			
			SysClassifUtils.doSortClassifTekst(classif);
			
			//JPA.getUtil().begin();
			int prev = 0;
			for (SystemClassif item :  classif) {
				
				//item = JPA.getUtil().getEntityManager().merge(item);
				
				//System.out.println(item.getTekst());
				item.setCodePrev(prev);
				
				System.out.println( "update SYSTEM_CLASSIF set CODE_PREV = " + prev + " where code = " + item.getCode() + " and code_classif= " + codeClassif + " and date_do is null;");
				
				
				//JPA.getUtil().getEntityManager().persist(item);
				prev = item.getCode();
			}
//			JPA.getUtil().commit();
//			JPA.getUtil().closeConnection();
//			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
