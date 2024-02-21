package bg.egov.eforms.utils;

import java.util.Date;
import java.util.List;

import com.ib.babhregs.system.SystemData;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.SysClassifUtils;

public class TestClassif {

	public static void main(String[] args) {
SystemData sd = new SystemData();
		
		
		String json = "";
		
		try {
			//List<SystemClassif> classif = sd.getSysClassification(539, new Date(), 1);
			List<SystemClassif> classif = sd.getSysClassification(508, new Date(), 1);
			
			
			SysClassifUtils.doSortClassifPrev(classif);			
			
			for ( SystemClassif item : classif) {
				for (int i = 1; i < item.getLevelNumber(); i ++) {
					System.out.print("\t");
				}
				System.out.println(item.getTekst() + " код: " + item.getCode());
			}
			
			
			
			
			
		} catch (DbErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnexpectedResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
	}
}
