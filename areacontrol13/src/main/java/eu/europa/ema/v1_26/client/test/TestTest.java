package eu.europa.ema.v1_26.client.test;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import eu.europa.ema.v1_26.client.eaf.services.EafTermVo;
import eu.europa.ema.v1_26.client.eaf.services.EutctService;



public class TestTest {
	
	

	public static void main(String[] args) {
		
		
		EutctService ss = new EutctService();
		
		List<EafTermVo> result = ss.getEutctPortTypePort().getAdministrationDevices(null);
		for (EafTermVo item : result) {
			System.out.println(item.getValue() + "\t" + item.getName() );
		}

	}

}
