package com.ib.babhregs.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.utils.MVUtils;
import com.ib.system.quartz.BaseJobResult;

/**
 * Процес, който трябва да обнови всички Materialized Views
 * @author krasig
 */
public class RefreMViewJob implements Job {
        private static final Logger LOGGER = LoggerFactory.getLogger(RefreMViewJob.class);
        
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("==== Start RefreshMViewJob");

        try {
        	
            BaseJobResult result = MVUtils.doViewRefresh(); 
            context.setResult(result);

        } catch (Exception e) {
            LOGGER.error("Error refreshing MV !", e);
            JobExecutionException ex = new JobExecutionException(e);
            ex.setRefireImmediately(false);
            throw ex;
        }

        LOGGER.info("==== End RefreshMViewJob");
    }
}
