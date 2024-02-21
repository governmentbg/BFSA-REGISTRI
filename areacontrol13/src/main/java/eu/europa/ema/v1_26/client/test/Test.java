package eu.europa.ema.v1_26.client.test;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import eu.europa.ema.v1_26.client.eaf.services.EafTermVo;
import eu.europa.ema.v1_26.client.eaf.services.EutctService;



public class Test {
	
	

	public static void main(String[] args) {
		
		String letters = "QWERTYUIOPASDFGHJKLZXCVBNM";
		
		//System.out.println("start");
		EutctService ss = new EutctService();
//		List<EafTermVo> countries = ss.getEutctPortTypePort().getCountries();
//		System.out.println(countries.size());
//		for (Iterator iterator = countries.iterator(); iterator.hasNext();) {
//			EafTermVo eafTermVo = (EafTermVo) iterator.next();
//			System.out.println("Name:"+eafTermVo.getName()+ ", Value:"+eafTermVo.getValue());
//		}
		
		TreeSet<String> vals = new TreeSet<String>();
		
		for (int i = 0; i < letters.length(); i++) {
			List<EafTermVo> all = ss.getEutctPortTypePort().searchSubstances(letters.substring(i,i+1), null);
			for (Iterator iterator = all.iterator(); iterator.hasNext();) {
				EafTermVo eafTermVo = (EafTermVo) iterator.next();
				vals.add(eafTermVo.getValue() + "\t"+eafTermVo.getName());
				//System.out.println(eafTermVo.getName());
			}
		}
		
		Iterator<String> it = vals.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		
		//System.out.println("end");

	}

}
