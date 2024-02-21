package com.ib.babhregs.rest;


import com.fasterxml.jackson.core.type.TypeReference;
//import com.indexbg.system.utils.JSonUtils;

import com.ib.babhregs.utils.JSonUtils;
import com.ib.system.utils.HTTPUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;


public class TestSimpleRestclient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSimpleRestclient.class);


    @Test
    public void testOne(){

//        String SRV_TARGET="https://integr-test.egov.bg:5050";
        String SRV_TARGET="https://integr-test.egov.bg:5050/token?grant_type=client_credentials&client_id=test_BABH_client_BULSI&scope=/ep*-";
        String PATH="/token";
        LOGGER.debug("SRV_TARGET={}",SRV_TARGET);
        LOGGER.debug("PATH={}",PATH);
//        HTTPUtils.disableSslVerification();
        SimpleRestClient instance = SimpleRestClient.getInstance();
        WebTarget webTarget = instance.getClient().target(SRV_TARGET).path(PATH);
//         webTarget = webTarget.
//                 queryParam("grant_type", "client_credentials").
//                 queryParam("client_id","test_BABH_client_BULSI").
//                 queryParam("scope","/ep*-");

        Response response = webTarget.request().post(null);
        LOGGER.info("response.status: {}", response.getStatus());
        if (response.getStatus() == 200) {
            String resJson = response.readEntity(String.class);
            LOGGER.info(resJson);
        }else {

            if (response.getStatus()!=500){
                // Имаме някаква гршка и не е хвърлена от нашата услуга
                LOGGER.error("Cast service error code: {}", response.getStatus());

                fail(""+response.getStatus());
            } else {
                //Проверяваме дали е подадено грешка от сървъра
                //Ако има ентити в респонса- значи сме ние
                Map<String,String> errorAsMap=null;

                //Правим опит да го докараме до нашия мап с грешките, ако изгърми - значи не е наше
                try {
                    String errorAsString = response.readEntity(String.class);
                    errorAsMap = (Map<String, String>) JSonUtils.json2Object(errorAsString, Map.class);
                    String ls =System.lineSeparator();
                    LOGGER.error("Response with error:!!!corelationID={}, "+ls+" customMessage={}, "+ls+" exception={}",errorAsMap.get("corelationId"),errorAsMap.get("customMessage"),errorAsMap.get("exception"));

                } catch (Exception e) {
                    LOGGER.error("Error:"+response.getStatus(),response.getStatusInfo().toString(),e);

                    fail(e.getMessage());
                }
            }
        }
    }



    @Test
    public void testGo() {
        List<Object[]> cachesList=null;
        String SRV_TARGET="http://10.29.3.242:8080/PNR2";

        String PATH="/rest/content/getCacheChanges";
        LOGGER.debug("SRV_TARGET={}",SRV_TARGET);
        LOGGER.debug("PATH={}",PATH);
        if (SRV_TARGET==null || SRV_TARGET.trim().isEmpty()) {
            LOGGER.error("Path to server is empty!!!!");
        }
        try {
            LOGGER.debug("Get cache changes from server");
            SimpleRestClient instance = SimpleRestClient.getInstance();
            WebTarget webTarget = instance.getClient().target(SRV_TARGET).path(PATH);

            Response response = webTarget.request().get();
            LOGGER.info("response.status: {}", response.getStatus());
            if (response.getStatus() == 200) {
                String resJson = response.readEntity(String.class);
                cachesList = (List<Object[]>) JSonUtils.json2Object(resJson, new TypeReference<ArrayList<Object[]>>() {});
                LOGGER.info("Found (size): {} cahces ", cachesList.size());
            }else {

                if (response.getStatus()!=500){
                    // Имаме някаква гршка и не е хвърлена от нашата услуга
                    LOGGER.error("Cast service error code: {}", response.getStatus());

                    fail(""+response.getStatus());
                } else {
                    //Проверяваме дали е подадено грешка от сървъра
                    //Ако има ентити в респонса- значи сме ние
                    Map<String,String> errorAsMap=null;

                    //Правим опит да го докараме до нашия мап с грешките, ако изгърми - значи не е наше
                    try {
                        String errorAsString = response.readEntity(String.class);
                        errorAsMap = (Map<String, String>) JSonUtils.json2Object(errorAsString, Map.class);
                        String ls =System.lineSeparator();
                        LOGGER.error("Response with error:!!!corelationID={}, "+ls+" customMessage={}, "+ls+" exception={}",errorAsMap.get("corelationId"),errorAsMap.get("customMessage"),errorAsMap.get("exception"));

                    } catch (Exception e) {
                        LOGGER.error("Error:"+response.getStatus(),response.getStatusInfo().toString(),e);

                        fail(e.getMessage());
                    }
                }
            }




        } catch (Exception e) {
            LOGGER.error("Error",e);
            fail(e.getMessage());
        }
        System.out.println(cachesList.size());
        for (Iterator iterator = cachesList.iterator(); iterator.hasNext();) {
            Object[] objects = (Object[]) iterator.next();

            System.out.println(objects[0]+":"+objects[1]);

        }

    }

}

