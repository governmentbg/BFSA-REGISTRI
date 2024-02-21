package com.ib.babhregs.rest;


import com.ib.babhregs.db.dao.DocDAO;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Experimental rest for Vue.js adventures
 */
@Path("/publicregs")
public class PublicRegisters {
private static final Logger LOGGER = LoggerFactory.getLogger(PublicRegisters.class);

    /**    Registyr 72,73

     * @param vid 7/8
     * @return
     */
    @GET
    @Path("/identificators/{vid}")
    @Produces({ MediaType.APPLICATION_JSON})
    @Operation(tags = "Experimental",summary = "Some KG Summary",description = "some KG description")
    public String registerIdentifictors(@PathParam("vid") Long vid){
        LOGGER.debug("registerIdentifictors: vid={}",vid);
        String result="{no resuult}";
        String SQL = "" +
                "SELECT " +
                "    CAST ( json_agg( jsonb_build_object( 'id_vpisvane',v12.id_vpisvane, 'oblast_text',  " +
                "    v12.oblast_text, 'obshtina_text',v12.obshtina_text, 'addr_text',v12.addr_text, " +
                "    'licenziant_name',v12.licenziant_name, 'licenziant_egn',v12.licenziant_egn, 'nomer_licenz',  " +
                "    v12.nomer_licenz, 'date_licenz',v12.date_licenz, 'identif',v12.json_identif ) ) AS TEXT) " +
                "FROM " +
                "    v_register_12 v12 " +
                "WHERE " +
                "    edjv_vid=:vid";
        long correlationId=new Date().getTime();

        try {

            JPA jpa = JPA.getUtil();
            Object resultRaw = JPA.getUtil().getEntityManager().createNativeQuery(SQL).setParameter("vid", vid).getSingleResult();

            if (resultRaw!=null){
                result=resultRaw.toString();
            }


        } catch (Exception e) {

            LOGGER.error("DbErrorException: corelationId={}, error:{}",correlationId,e.getMessage(),e);
            throw new IBRestException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),correlationId,"Грешка при извличане на данни от БД",e);
        }finally {

        }
        if (result==null){
            LOGGER.debug("Result is NULL");
        }
        return result;
    }

    @GET
    @Path("/getPaymentInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(tags = "Experimental",summary = "Информация за плащания на документи",description = "some KG description")
    public String getPeymentInfo(){
        long correlationId=new Date().getTime();
        String docPayment;
        DocDAO dao=new DocDAO((ActiveUser.DEFAULT));
        try {
             docPayment = dao.findDocPayment();
            System.out.println("---------------------------------");
            System.out.println(docPayment);
        }catch (Exception e){
            LOGGER.error("DbErrorException: corelationId={}, error:{}",correlationId,e.getMessage(),e);
            throw new IBRestException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),correlationId,"Грешка при извличане на данни от БД",e);


        }finally {
            JPA.getUtil().closeConnection();
        }
        return docPayment;
    }

    //@PUT
    @POST
    @Path("/updatePayment")
    @Operation(tags = "Experimental",summary = "Промяна данни за плащане",description = "some KG description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Invalid parameter supplied"),
            @ApiResponse(responseCode = "200", description = "All is Ok"),
            @ApiResponse(responseCode = "500", description = "Some kind of internal error", content =
                    { @Content(mediaType = "application/json", schema =
                    @Schema(implementation = IBRestException.class)) })
    })
    public Response updatePayment(
            @Parameter(allowEmptyValue = false,required = true,description = "ID на документа за който е плащането",name ="docId" )
                @QueryParam("docId") Integer docId,
            @Parameter(allowEmptyValue = false,required = true,description = "Сумата на плащането",name ="payValue" )
                @QueryParam("payValue") Integer payValue,
            @Parameter(allowEmptyValue = false,required = true,description = "Потребителя извършил плащането",name ="userId" )
                @QueryParam("userId") Integer userId){
        LOGGER.debug("updatePayment(docId:{}, payValue:{},, userId:{} - start",docId,payValue,userId);

        if (docId==null || payValue==null || payValue<=0 || userId==null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Some parameter/s is/are missing.")
                    .build();
        }
        long correlationId=new Date().getTime();
        try{
            JPA.getUtil().runInTransaction(() -> {
                Object[] arg = new Object[10];
                arg[0]=docId;
               new DocDAO(ActiveUser.of(userId)).updateEditTax(arg, BigDecimal.valueOf(payValue), new Date());

            });
            return Response.ok().status(Response.Status.OK).build();
        }catch (Exception e){
            LOGGER.error("DbErrorException: corelationId={}, error:{}",correlationId,e.getMessage(),e);
            throw new IBRestException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),correlationId,"Грешка при извличане на данни от БД",e);
        }




//        LOGGER.debug("updatePayment - end");
//        return Response.ok().build();
    }

}
