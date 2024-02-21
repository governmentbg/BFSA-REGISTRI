package eu.europa.ema.v1_26;

import eu.europa.ema.v1_26.client.eaf.services.EafTermVo;
import eu.europa.ema.v1_26.client.eaf.services.EutctPortType;
import eu.europa.ema.v1_26.client.eaf.services.EutctService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class TestVassi {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestVassi.class);

//	@Test
	public void testFirst() {
		
		
		String letters = "QWERTYUIOPASDFGHJKLZXCVBNM";
		
		ArrayList<String> comb = new ArrayList<String>();
		EutctService ss = new EutctService();
		EutctPortType port = ss.getEutctPortTypePort();
		
		for (int i = 0; i < letters.length(); i ++) {
			for (int j = 0; j < letters.length(); j ++) {
				for (int k = 0; k < letters.length(); k ++) {
					for (int l = 0; l < letters.length(); l ++) {
						String temp = letters.substring(i,i+1) + letters.substring(j,j+1) + letters.substring(k,k+1)  + letters.substring(l,l+1);
						//System.out.println("****** " + temp);
						LOGGER.info("****** " + temp);
						List<EafTermVo> result = port.searchSubstances(temp, null);
						for (EafTermVo tek : result) {
							if (tek.getName() != null && ! tek.getName().trim().isEmpty()) {
								//System.out.println(tek.getName());
								LOGGER.info(tek.getName());
							}
						}
						
					}	
				}	
			}
		}
		
		
		
		
		
		
//		EutctService ss = new EutctService();
//		List<EafTermVo> result = ss.getEutctPortTypePort().searchSubstances("Pyrantel pamoate", null);
//		for (EafTermVo tek : result) {
//			System.out.println(tek.getName());
//		}
		
		
		//System.out.println(result.size());
		
//		TreeSet<String> vals = new TreeSet<String>();
//		
//		for (int i = 0; i < letters.length(); i++) {
//			List<EafTermVo> all = ss.getEutctPortTypePort().searchSubstances(letters.substring(i,i+1), null);
//			for (Iterator iterator = all.iterator(); iterator.hasNext();) {
//				EafTermVo eafTermVo = (EafTermVo) iterator.next();
//				vals.add(eafTermVo.getValue() + "\t"+eafTermVo.getName());
//				//System.out.println(eafTermVo.getName());
//			}
//		}
//		
//		Iterator<String> it = vals.iterator();
//		while (it.hasNext()) {
//			System.out.println(it.next());
//		}
		
		

	}

}
