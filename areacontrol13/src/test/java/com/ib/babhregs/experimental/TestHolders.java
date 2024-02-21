package com.ib.babhregs.experimental;

import static org.junit.Assert.fail;

import org.junit.Test;

public class TestHolders {
	
	@Test
	public void TestAllHolders() {
		
		GlobalHolder holder = new GlobalHolder(); //TODO Да се вземе от апликейшън-а
		
		
		try {
			
			CheckNewData cd = new CheckNewData();
			
			//Зареждаме СЕОС и ССЕВ
			cd.fillEgovData(holder);
			
			RegistratureDocHolder regHolder = holder.getRegInfo(1);
			
			System.out.println("EDelivery = " + regHolder.getCounterEDelivery());
			System.out.println("SEOS = " + regHolder.getCounterSEOS());
			//System.out.println("Mail = " + regHolder.getCounterEmails());
			System.out.println("Nasoch = " + regHolder.getCounterNasochvane());
			System.out.println("Official = " + regHolder.getCounterOfficial());
			System.out.println("Dvig = " + regHolder.getCounterOtherReg());
			
			
		}catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}