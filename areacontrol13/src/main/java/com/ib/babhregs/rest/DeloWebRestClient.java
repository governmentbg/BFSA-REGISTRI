package com.ib.babhregs.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.ib.babhregs.rest.common.DocWS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.filters.LogClientRequestFilter;
import com.ib.babhregs.utils.JSonUtils;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.RestClientException;

/** Клас, предоставящ рест услуги от деловодната система на БАБХ
 *  
 * @author vassil
 *
 */
public class DeloWebRestClient {
	
		private static final Logger LOGGER = LoggerFactory.getLogger(DeloWebRestClient.class);
	
		SystemData sd = null;
	
		public DeloWebRestClient(SystemData sd) {
			this.sd = sd;
		}
		
		
	 
	    /** Метод връщаш всички новополучени заявления
	     * @return Списък документи от тип DocWS
	     * @throws RestClientException - грешка при извикване на рест услуга
	     */
	    @SuppressWarnings("unchecked")
		public ArrayList<DocWS> getNewDocuments(Integer systemId) throws RestClientException{
	        
	    	LOGGER.debug("Start getNewDocuments...");
	        
	    	try {
	    		
//	    		String wsUrl = sd.getSettingsValue("deloweb.wsUrl");
//	    		if (wsUrl == null || wsUrl.trim().isEmpty()) {
//	    			throw new RestClientException("'deloweb.wsUrl' is not defined in SYTEM_OPTIONS");
//	    		}
	    		String wsUrl = "http://10.29.3.245:8080/BabhWS/";
	    		
	    		
				Client client = ClientBuilder.newClient();
				if(LOGGER.isDebugEnabled()) {
					client.register(new LogClientRequestFilter());
				}
				WebTarget webTarget = client.target(wsUrl).path("api").path("deloWeb").path("getNewDocuments");
				webTarget =webTarget.queryParam("systemId", systemId);
				
				Invocation.Builder invocationBuilder =  webTarget.request();
				Response response = invocationBuilder.get();
				
				LOGGER.debug("Response Status: "+response.getStatus());
				LOGGER.debug("Responce StatusInfo: "+response.getStatusInfo());
				//LOGGER.debug(response.readEntity(String.class));
				
				
				if (response == null || response.getStatus() != 200) {
					LOGGER.error("Error when executing rest service getNewDocuments!  " + response.getStatus() + ": " + response.getStatusInfo() );
					throw new RestClientException("Error when executing rest service getNewDocuments!  " + response.getStatus() + ": " + response.getStatusInfo());
				}
				
				String jsonString = response.readEntity(String.class);
				
				LOGGER.debug(jsonString);
				
//				 ArrayList<DocWS> result = (ArrayList<DocWS>) JSonUtils.json2Object(jsonString, List.class);
				 ArrayList<DocWS> result = (ArrayList<DocWS>) JSonUtils.json2Object(jsonString, new TypeReference<List<DocWS>>() {});
				 
				if (result != null) {
					LOGGER.debug("End getNewDocuments. Number of rows: " + result.size()  );
				}else {
					LOGGER.debug("End getNewDocuments. Number of rows: NULL"  );
				}
				return result;
				
			} catch (Exception e) {
				LOGGER.error("Unexpected error when executing method getNewDocuments!", e);
				throw new RestClientException("Unexpected error when executing method getNewDocuments!", e);
			}
	        
	    }
	    
	    
	    
	    /** Метод за извличане на документ по id
	     * @param docId - системен идентификатор на документ (id)
	     * @return Пълни данни за документа + кореспондент + файлове
	     * @throws RestClientException - грешка при извикване на рест услуга
	     */
	    public DocWS getDocumentInfo(Integer docId) throws RestClientException{
	        
	    	LOGGER.debug("Start getDocumentInfo with docId= " + docId);
	        
	    	try {
	    		
	    		String wsUrl = sd.getSettingsValue("deloweb.wsUrl");
	    		if (wsUrl == null || wsUrl.trim().isEmpty()) {
	    			throw new RestClientException("'deloweb.wsUrl' is not defined in SYSTEM_OPTIONS");
	    		}
	    		
	    		Client client = ClientBuilder.newClient();
				if(LOGGER.isDebugEnabled()) {
					client.register(new LogClientRequestFilter());
				}
	            WebTarget webTarget = client.target(wsUrl).path("api").path("deloWeb").path("getDocumentInfo"); //.path("docId=80195");
				webTarget =webTarget.queryParam("docId", docId);
	            
	            System.out.println(webTarget.getUri());
	            
	            Invocation.Builder invocationBuilder =  webTarget.request();
	            
	           
	            
	            Response response = invocationBuilder.get();

				
	            LOGGER.debug("Response Status: "+response.getStatus());
				LOGGER.debug("Responce StatusInfo: "+response.getStatusInfo());
				//LOGGER.debug(response.readEntity(String.class));
				
				
				if (response == null || response.getStatus() != 200) {
					//LOGGER.error(response.readEntity(String.class));
					LOGGER.error("Error when executing rest service getDocumentInfo!  " + response.getStatus() + ": " + response.getStatusInfo() );
					throw new RestClientException("Error when executing rest service getDocumentInfo!  " + response.getStatus() + ": " + response.readEntity(String.class) );
				}
				
				String jsonString = response.readEntity(String.class);
				
				
				DocWS result = (DocWS) JSonUtils.json2Object(jsonString, DocWS.class);
				
				LOGGER.debug("End getDocumentInfo...");
				return result;
				
			} catch (Exception e) {
				LOGGER.error("Unexpected error when executing method getDocumentInfo!", e);
				throw new RestClientException("Unexpected error when executing method getDocumentInfo!", e);
			}
	        
	    }
	    
	    
	    /** Метод за маркиране на документ като обработен
	     * @param docId - системен идентификатор на документ (id)
	     * @return брой променени записи
	     * @throws BaseException 
	     */
	    public Integer markDocAsFinished(Integer docId) throws BaseException{
	        
	    	LOGGER.debug("Start markDocAsFinished with docId= " + docId);
	    	Integer updateCount = 0;
	    	try {
	    		
	    		String wsUrl = sd.getSettingsValue("deloweb.wsUrl");
	    		if (wsUrl == null || wsUrl.trim().isEmpty()) {
	    			throw new RestClientException("'deloweb.wsUrl' is not defined in SYTEM_OPTIONS");
	    		}
	    		
	    		Client client = ClientBuilder.newClient();
				if(LOGGER.isDebugEnabled()) {
					client.register(new LogClientRequestFilter());
				}
	            WebTarget webTarget = client.target(wsUrl).path("api").path("deloWeb").path("markDocAsFinished"); //.path("docId=80195");
				webTarget =webTarget.queryParam("docId", docId);
	            
	            System.out.println(webTarget.getUri());
	            
	            Invocation.Builder invocationBuilder =  webTarget.request();
	            
	           
	            
	            Response response = invocationBuilder.get();
	            
				
	            LOGGER.debug("Response Status: "+response.getStatus());
				LOGGER.debug("Responce StatusInfo: "+response.getStatusInfo());
				//LOGGER.debug(response.readEntity(String.class));
				
				
				if (response == null || response.getStatus() != 200) {
					
					String exception = response.readEntity(String.class);					
					//LOGGER.error(response.readEntity(String.class));
					LOGGER.error("Error when executing rest service markDocAsFinished! " + exception );
					throw new RestClientException("Error when executing rest service markDocAsFinished!  " + exception );
				}
				
				updateCount = response.readEntity(Integer.class);
				if (updateCount == 0) {
//					throw new RestClientException("Документът не е маркиран като приключен");
				}
				
	    	} catch (RestClientException e) {
	    		throw e;
			} catch (Exception e) {
				LOGGER.error("Unexpected error when executing method markDocAsFinished!", e);
				throw new BaseException("Unexpected error when executing method markDocAsFinished!", e);
			}
	        
	    	LOGGER.debug("End markDocAsFinished. updateCount = " + updateCount);
	    	return updateCount;
	    }
	    
	    

}
