package com.ib.babhregs.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.InvalidParameterException;
import com.ib.system.quartz.BaseJobResult;

public class MVUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MVUtils.class);
	
	@SuppressWarnings("unchecked")
	public static BaseJobResult doViewRefresh() throws InvalidParameterException {

		LOGGER.debug("Starting doViewRefresh");
		
		int brOk = 0;
		int brGreshni = 0;
		int brAll = 0;
		
		ArrayList<String> descrRows = new ArrayList<String>();
		
		BaseJobResult jobResult = new BaseJobResult();
		jobResult.setStatus(BabhConstants.CODE_ZNACHENIE_JOB_STATUS_EMPTY);
		jobResult.setComment(null);
			
		List<Object> viewList = JPA.getUtil().getEntityManager().createNativeQuery("SELECT oid\\:\\:regclass\\:\\:text FROM   pg_class WHERE  relkind = 'm'").getResultList();
		brAll = viewList.size();
		LOGGER.debug("Number of views to refresh: " + brAll);
		
		for (Object view : viewList) {
			try {
				JPA.getUtil().begin();
				LOGGER.debug("Processing " + view);
				JPA.getUtil().getEntityManager().createNativeQuery("REFRESH MATERIALIZED VIEW CONCURRENTLY " + view).executeUpdate();
				JPA.getUtil().commit();
				brOk++;
			} catch (Exception e) {
				brGreshni++;
				descrRows.add(view + ": " + e.getMessage());
				JPA.getUtil().rollback();	
			}	
		}
	
		JPA.getUtil().closeConnection();
		
		
		if  (brGreshni > 0) {
			jobResult.setStatus(BabhConstants.CODE_ZNACHENIE_JOB_STATUS_WARN);			
		}else {
			if (brAll > 0) {
				jobResult.setStatus(BabhConstants.CODE_ZNACHENIE_JOB_STATUS_OK);
			}else {
				jobResult.setStatus(BabhConstants.CODE_ZNACHENIE_JOB_STATUS_EMPTY);
			}
			
		}
		
		String comment = "";
		
		comment += "Общо обработени: " + brAll + "\r\n";
		comment += "Брой обработени с грешка : " + brGreshni + "\r\n";
		comment += "Общо обработени без грешка: " + brOk + "\r\n";
		
		jobResult.setComment(comment);
		

		String desc = "";
		for (String row : descrRows) {
			desc += "***** " + row + "\r\n";
		}
		
		jobResult.setDescription(desc);
		
		LOGGER.debug("Comment:\t"  + jobResult.getComment());	
		LOGGER.debug("Description:\t"  + jobResult.getDescription());
		
		LOGGER.debug("End doViewRefresh");
		return jobResult;
		
			
		
	}

}
