package com.ib.babhregs.utils;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import com.ib.babhregs.system.SystemData;
import com.ib.system.quartz.BaseJobResult;

public class TestWSUtils {

	
	@Test
	public void testIT() {
		
		try {
			BaseJobResult result = new DeloWebUtils().proccessZaiavlenia(new SystemData());
			
			
			System.out.println("Status: " + result.getStatus()+ "\r\n");
			System.out.println("Comment : \r\n" + result.getComment());
			System.out.println();			
			System.out.println("Description: \r\n" + result.getDescription());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testEmails() {
		
		try {
			
			ArrayList<String> mails = new ArrayList<String>();
			mails.add("mamun@abv.bg");
			
			ArrayList<Object> result = new DeloWebUtils().findUsersIdsByEmail(mails);
			
			System.out.println(result.size() + "  ---> " + result);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
	}
	
	
}
