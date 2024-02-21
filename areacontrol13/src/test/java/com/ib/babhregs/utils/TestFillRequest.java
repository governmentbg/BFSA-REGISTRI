package com.ib.babhregs.utils;

import java.io.File;

import com.ib.system.utils.FileUtils;
import com.ib.system.utils.JAXBHelper;

import bg.egov.eforms.ServiceRequest;

public class TestFillRequest {

	public static void main(String[] args) {
		
		try {
			
			
			byte[] bytes = FileUtils.getBytesFromFile(new File("d:\\result.xml"));
			
			String xml = new String(bytes);
				
			ServiceRequest sr = JAXBHelper.xmlToObject(ServiceRequest.class, xml);
			
			System.out.println(sr.getApplicant().getPerson().getPersonalData().getIdentifier().getIdentifier());
			
			System.out.println(sr.getSpecificContent());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
