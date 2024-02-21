package com.ib.babhregs.quartz;

import javax.servlet.ServletContext;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.utils.DeloWebUtils;
import com.ib.system.SysConstants;
import com.ib.system.quartz.BaseJobResult;

/**
 * Процес, който трябва да извика метод, който да извлече документи от ДелоВеб
 * @author krasig
 */
public class GetFromDeloWebJob implements Job {
        private static final Logger LOGGER = LoggerFactory.getLogger(GetFromDeloWebJob.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("==== Start GetFromDeloWebJob");
        //Първо опитваме да вземем системдата-та от контекста
        SystemData systemData=null;
        try {
            ServletContext servletContext = (ServletContext) context.getScheduler().getContext().get("servletContext");
            if (servletContext == null) {
                LOGGER.info("*** servletcontext is null **********");
                return;
            }
            systemData = (SystemData) servletContext.getAttribute("systemData"); // testData;
        } catch (Exception e) {

        }finally {
            //Ако не стане - правим чистонова
            if (null==systemData){
                LOGGER.info("*** SystemData is null. Will generate new!!!");
                systemData=new SystemData();
            }
        }

        try {
            BaseJobResult result = new DeloWebUtils().proccessZaiavlenia(systemData);
            

            context.setResult(result);

        } catch (Exception e) {
            LOGGER.error("Error getting data from DeloWeb !", e);
            JobExecutionException ex = new JobExecutionException(e);
            ex.setRefireImmediately(false);
            throw ex;
        }

        //End ТЕМП

        LOGGER.info("==== End GetFromDeloWebJob");
    }
}
