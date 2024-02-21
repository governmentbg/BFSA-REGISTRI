package com.ib.babhregs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

public class SimpleTest {

	

    public static void main(String[] args) {
        
    	String s = "2024-02-01T22:00:00.000Z";
    	
    	try {
    		SimpleDateFormat xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			System.out.println(xmlDateFormat.parse(s));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	

        
        
        
    }


	
		
		
		
	
	

}


