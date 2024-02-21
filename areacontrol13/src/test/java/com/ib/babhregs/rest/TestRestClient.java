package com.ib.babhregs.rest;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import com.ib.babhregs.rest.common.DocWS;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.rest.common.DocWS;
import com.ib.babhregs.system.SystemData;
import com.ib.system.exceptions.RestClientException;


public class TestRestClient {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(TestRestClient.class);

   
   

    @Test
    public void testGetNewDocsRestService(){
        LOGGER.info("Start testGetNewDocsRestService...");
        
        
        
        try {
        	
        	DeloWebRestClient client = new DeloWebRestClient(new SystemData());
        	
			ArrayList<DocWS> result = client.getNewDocuments(1);
			if (result != null ) {
				System.out.println("Брой върнати документи: " + result.size());
			}
			
//			if (result.size() > 0) {
//				client.markDocAsFinished(result.get(0).getId());
//			}
			
			
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
        
        
    }
    
    
    @Test
    public void testGetDocInfoRestService(){
        LOGGER.info("Start testGetDocInfoRestService...");
        
        try {
        	DocWS result = new DeloWebRestClient(new SystemData()).getDocumentInfo(80195);
        	
        } catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
       
    }


}
