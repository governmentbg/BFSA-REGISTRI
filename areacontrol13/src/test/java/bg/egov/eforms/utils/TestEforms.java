package bg.egov.eforms.utils;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.ib.babhregs.system.SystemData;

public class TestEforms {
	
	@Test
	public void testConvert() {
		
		try {
			Integer docId =  86888; //86888; 86885
			EgovContainer eCon = new EFormUtils().convertEformToVpisvane(docId, new SystemData() );
			
			System.out.println("--------------------------------------------");
		} catch (Exception e) {			
			e.printStackTrace();
			fail();
		}
		
	}
	

}
