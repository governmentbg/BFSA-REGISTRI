package com.ib.babhregs.experimental;

import org.junit.Test;

import com.ib.babhregs.utils.MVUtils;
import com.ib.system.exceptions.InvalidParameterException;
import com.ib.system.quartz.BaseJobResult;

public class TestRefreshView {
	
	
	
	@Test	
	public void doTest() throws InvalidParameterException {

		try {
			BaseJobResult jobResult = MVUtils.doViewRefresh();
			
			System.out.println("Comment:\t"  + jobResult.getComment());	
			System.out.println("Description:\t"  + jobResult.getDescription());
			
		} catch (InvalidParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		System.out.println("Done!");
	}
	
	
	
	
}
