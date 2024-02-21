package com.ib.babhregs;

import java.io.ByteArrayInputStream;
import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ib.system.utils.FileUtils;
import com.ib.system.utils.JAXBHelper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;

import bg.egov.eforms.ServiceRequest;

public class JsonEFormiTest {
    
	

	public static void main(String[] args) {
        
		
		try {
			
//			0006-000121
//			Първоначално заявление за предоставяне на електронна административна услуга
			
			
			//String SRC = "d:\\911-1da8d1a4-28ca-40b2-8dd6-84c7e1f277b8-ZVLN.pdf";
			//String SRC = "d:\\502-ac3b5ae9-11f8-49d8-b696-4250fbdeec02-ZVLN.pdf";
			//String SRC = "d:\\1589-b4f491b9-5847-41d9-82d4-96448bf5fce9-ZVLN.pdf";
			//String SRC = "d:\\hospital.pdf";
			String SRC = "C:\\Users\\vassil\\Downloads\\1589-bacd9475-a0fa-4028-88d0-9858aeb63a8b-ZVLN.pdf";
			
			
			
			
			
			byte[] bytes = FileUtils.getBytesFromFile(new File(SRC)); 
			
			
			//PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC));		
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes)));
			
			
			//Метаданни за pdf-a
			PdfDocumentInfo info = pdfDoc.getDocumentInfo();
			
			//Къстом добавени данни :
			String xml = info.getMoreInfo("application.json_xml");
			
			
			
			System.out.println(xml);
			
			ServiceRequest sr = JAXBHelper.xmlToObject(ServiceRequest.class, xml);
			
			//System.out.println(sr.getApplicant().getPerson().getIdentifier().getIdentifier());
			
			System.out.println(sr.getSpecificContent());
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        		
                
                
        
        
       
        
    }
}


