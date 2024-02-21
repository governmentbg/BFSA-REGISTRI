package com.ib.babhregs.migr.bfsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import com.ib.system.db.JPA;
import com.ib.system.utils.SearchUtils;

public class TestUploadedData {

	public static void main(String[] args) {
		
		HashMap<String, Integer> aMap = createAnimalsMap();
		HashMap<String, Integer> pMap = createPrilMap();
		HashMap<String, Integer> oMap = createOpakovkaMap();
		HashMap<String, Integer> fMap = createFormsMap();
		
		
		TreeSet<String> unique = new TreeSet<String>();
		try {
			ArrayList<Object> data = (ArrayList<Object>) JPA.getUtil().getEntityManager().createNativeQuery("select distinct upper(temp_vmp.nachin_pril) from temp_vmp").getResultList();
			for (Object o : data) {
				String tek = SearchUtils.asString(o);
				
				tek = tek.replace(";", ",");
				String[] splited = tek.split(",");
				
				if (splited.length == 1) {
					splited = tek.split("/");
				}
				
				for (String s : splited) {
					unique.add(s.trim());
					//System.out.println("   --> " + s);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String s: unique) {
			Integer code = pMap.get(s);
			if (code == null)
			System.out.println(s);
		}

	}

	
	public static HashMap<String, Integer> createAnimalsMap(){
		
		return createMap("temp_precode_animals");
		
	}
	
	public static HashMap<String, Integer> createFormsMap(){
		
		return createMap("temp_precode_formi");
		
	}


	public static HashMap<String, Integer> createOpakovkaMap(){
	
		return createMap("temp_precode_opakovka");
		
	}
	
	public static HashMap<String, Integer> createPrilMap(){
		
		return createMap("temp_precode_pril");
		
	}
		
		
	public static HashMap<String, Integer> createMap(String table){
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		ArrayList<Object[]> data = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery(" select tekst, iisr_code from "+table+" where iisr_code is not null").getResultList();
		for (Object[] row : data) {
			map.put(SearchUtils.asString(row[0]), SearchUtils.asInteger(row[1]));
		}
		
		return map;
	}
	
	
}

	


