package com.ib.babhregs.migr.oez;

import java.util.ArrayList;

import javax.persistence.Query;

import com.ib.system.db.JPA;
import com.ib.system.utils.SearchUtils;

public class CheckMissingOez {

	public static void main(String[] args) {
		
		ArrayList<Object> allNoms = (ArrayList<Object>) JPA.getUtil().getEntityManager().createNativeQuery("SELECT distinct nom_oez FROM reg0_errors").getResultList();
		
		int cnt  = allNoms.size();
		
		System.out.println("Уникални номера за проверка: " + cnt);
		
		int cntFound = 0;
		int cntNotFound = 0;
		int cur = 0;
		for (Object o : allNoms) {
			
			cur++;
			
			String nom = SearchUtils.asString(o).replace("\r", "").replace("\n", "");
			
			Query q = JPA.getUtil("vetisReal").getEntityManager().createNativeQuery("select id from JIV_OBEKT where REG_NOMER = :nom or REG_NOMER_STAR = :nom");
			q.setParameter("nom", nom);
			ArrayList<Object> ids = (ArrayList<Object>)q.getResultList();
			
			if (ids.size() > 0) {
				cntFound++;
				//System.out.println(cur + " - found " + nom);
			}else {
				cntNotFound++;
				//System.out.println(cur + " - not found "  + cntNotFound + "\t|" + o + "|");
				System.out.println(nom);
			}
			
					
		}
		
		
	}

}
