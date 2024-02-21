package com.ib.babhregs.beans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.ib.babhregs.db.dao.PublicRegisterDAO;
import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;

@Named
@ViewScoped
public class RegsWholesalersVLPBean  extends IndexUIbean   {

	/**
	 * Регистър на одобрените обекти за търговия на фуражни добавки
	 */
	private static final long serialVersionUID = 5882367028502811466L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegsWholesalersVLPBean.class);

	private LazyDataModelSQL2Array regsList;
	PublicRegisterDAO publicRegisterDAO = new PublicRegisterDAO();
 	
	@PostConstruct
	void initData() {


		try {
			regsList=new LazyDataModelSQL2Array(publicRegisterDAO.getRegisterAsList(23),"date_licenz desc");

		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на списък! ", e);
		}
	}
	
	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др.
	 */
	public void postProcessXLS(Object document) {
		
		String title = getMessageResourceString(LABELS, "regsWholesalersVLP.title");		  
    	new CustomExpPreProcess().postProcessXLS(document, title, null, null, null);		
     
	}
	

	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката
	 */
	public void preProcessPDF(Object document)  {
		try{
			
			String title = getMessageResourceString(LABELS, "regsWholesalersVLP.title");		
			new CustomExpPreProcess().preProcessPDF(document, title,  null, null, null);		
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(),e);			
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);			
		} 
	}
	
	

	/**
	 * за експорт в pdf 
	 * @return
	 */
	public PDFOptions pdfOptions() {
		PDFOptions pdfOpt = new CustomExpPreProcess().pdfOptions(null, null, null);
        return pdfOpt;
	}
		
	public LazyDataModelSQL2Array getRegsList() {
		return regsList;
	}

	public void setRegsList(LazyDataModelSQL2Array regsList) {
		this.regsList = regsList;
	}
		
}