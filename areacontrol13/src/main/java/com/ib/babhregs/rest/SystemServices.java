package com.ib.babhregs.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ib.babhregs.system.SystemData;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.JSonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

/**
 * Някакви системни услуги
 * Класификации,Настройки и т.н.
 */
@Path("/system")
public class SystemServices {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemServices.class);
    @Inject
    private SecurityContext securityContext;

    @Inject
    SystemData sd;
    /**
     * @param codeClassif
     * @return
     */
    @GET
    @Path("/getClassif")
    @Operation(tags = "Experimental",summary = "Some KG Summary",description = "some KG description")
    @Produces({ MediaType.APPLICATION_JSON})
    public String getSystemClassif(@QueryParam("codeClassif") Integer codeClassif){
        List<SystemClassif> sysClassification;
        String result;
        long correlationId=new Date().getTime();
        try {
            sysClassification = sd.getSysClassification(codeClassif, new Date(), 1);
            result = JSonUtils.object2json(sysClassification);
        } catch (DbErrorException e) {
            LOGGER.error("DbErrorException: corelationId={}, error:{}",correlationId,e.getMessage(),e);
            throw new IBRestException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),correlationId,"Грешка при извличане на данни от БД",e);
        } catch (JsonProcessingException e) {
            LOGGER.error("DbErrorException: corelationId={}, error:{}",correlationId,e.getMessage(),e);
            throw new IBRestException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),correlationId,"Грешка при извличане на данни от БД",e);
        }
        return result;
    }

    @GET
    @Path("/getEkatte")
    @Operation(tags = "Experimental",summary = "Some KG Summary",description = "some KG description")
    @Produces({MediaType.APPLICATION_JSON})
    public String getEkatte(@QueryParam("codeParent") Integer codeParent){
        List<SystemClassif> sysClassification;
        String result;
        long correlationId=new Date().getTime();
        try {
            sysClassification = sd.getChildrenOnNextLevel(15,codeParent,new Date(),1);

            result = JSonUtils.object2json(sysClassification,new String[]{"code","tekst"});
        } catch (DbErrorException e) {
            LOGGER.error("DbErrorException: corelationId={}, error:{}",correlationId,e.getMessage(),e);
            throw new IBRestException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),correlationId,"Грешка при извличане на данни от БД",e);
        } catch (JsonProcessingException e) {
            LOGGER.error("DbErrorException: corelationId={}, error:{}",correlationId,e.getMessage(),e);
            throw new IBRestException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),correlationId,"Грешка при извличане на данни от БД",e);
        }
        return result;
    }
}
