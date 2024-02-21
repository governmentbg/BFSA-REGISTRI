package com.ib.babhregs.migr.bfsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.hibernate.boot.model.source.spi.JoinedSubclassEntitySource;

import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;

public class TestSubstancesDataEU {
	
	
	private static HashMap<String, String> parsed = new HashMap<String, String> ();
	
	private static int cntFound = 0;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
	
		
		
		
		TreeSet<String> allEuSubs = new TreeSet<String>();
		
		
		try {
			ArrayList<Object[]> data = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery("select code, subs, naims from temp_vmp_eu ").getResultList();
			ArrayList<Object[]> allVmps = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery("select id, naim from temp_vmp ").getResultList();
			
			int cntBig = 0;
			
			for (Object[] vmp : allVmps) {
				
				Integer id = SearchUtils.asInteger(vmp[0]);
				String naims = SearchUtils.asString(vmp[1]).toUpperCase().trim();
				
				
				
				String[] splitted = naims.split("/");
				if (splitted.length > 2) {
					splitted = naims.split(" / ");
				}
				
				
				//System.out.println(splitted.length);
				
				if (splitted.length < 3) {
					
					String name1 = splitted[0];
					String name2 = null;
					if (splitted.length == 2) {
						name2 = splitted[1];
					}
					
					boolean found = false;
					for (Object[] vmpEU : data) {
						String[] splittedEu = SearchUtils.asString(vmpEU[2]).split("\\|");
						String nameEU1 = splittedEu[0];
						String nameEU2 = null;
						if (splittedEu.length == 2) {
							nameEU2 = splittedEu[1];
						}
						
						if ( compare(name1, nameEU1 ) || compare(name1, nameEU2) || compare(name2, nameEU1 ) || compare(name2, nameEU2 )) {
							found = true;
							break;
						}
						
						
						
					}
					
					if (found) {
						cntFound++;
					}else {
						System.out.println(naims);
						
					}
					
				}else {
					cntBig ++;
					//System.out.println(naims);
				}
				
				
				
				
//				String[] subsArray = tekSubstances.split("\\|");
//				if (! SearchUtils.isEmpty(subsArray[0])) {
//					allEuSubs.add(subsArray[0].trim().toUpperCase());
//				}
			}
				
				
			
			
			System.out.println("Разпознати " + cntFound + " от " + allVmps.size() );
			System.out.println("Неразпознати с / " + cntBig + " от " + allVmps.size() );
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	@SuppressWarnings("unchecked")
	public static void main2(String[] args) {
		
	
		
		
		
		TreeSet<String> allEuSubs = new TreeSet<String>();
		
		
		try {
			ArrayList<Object[]> data = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery("select code, subs, naims from temp_vmp_eu").getResultList();
			ArrayList<Object[]> substances = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery("select identifier, name from substances order by status").getResultList();
			
			
			
			for (Object[] o : data) {
				
				String code = SearchUtils.asString(o[0]);
				String tekSubstances = SearchUtils.asString(o[1]).toUpperCase().trim();
				String naims = SearchUtils.asString(o[2]).toUpperCase().trim();
				
				String[] subsArray = tekSubstances.split("\\|");
				if (! SearchUtils.isEmpty(subsArray[0])) {
					allEuSubs.add(subsArray[0].trim().toUpperCase());
				}
			}
				
				
			for (String s : allEuSubs) {							
				
				String foundName = "";
				for (Object[] sub : substances) {
					String origName = SearchUtils.asString(sub[1]).trim().toUpperCase();
					
					if (origName.equals(s.trim().toUpperCase())) {
						cntFound++;
						foundName = origName;
						break;
					}
				}
				System.out.println(s + "\t" + foundName);
				
			}
			
			System.out.println("Разпознати " + cntFound + " от " + allEuSubs.size() );
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	
    public static boolean compare(String str1, String str2) {
    
    	if (str2 == null || str1 == null) {
    		return false;
    	}
    	
    	str1 = str1.toUpperCase().trim().replace(" ", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "").replace("+", "").replace("–", "").replace("-", "");
    	str2 = str2.toUpperCase().trim().replace(" ", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "").replace("+", "").replace("–", "").replace("-", "");
    	
    	return (str1.equalsIgnoreCase(str2));
    }
	
    
	
}

	


