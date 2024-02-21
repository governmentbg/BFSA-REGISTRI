package com.ib.babhregs.beans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.PublicRegisterDAO;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;

@Named
@ViewScoped
public class RegsOrgsCourcesBean   extends IndexUIbean   {

	/**
	 * Регистър на фирмите и центровете, предоставящи обучение в сферата на животновъдството
	 * 132 Регистър на фирмите и центровете, предоставящи обучение в сферата на здравеопазването и хуманното отношение към животните
	 * 133 Регистър на фирмите и центровете, предоставящи обучение за вземане на проби от мляко
	 */
	private static final long serialVersionUID = 4295598888565992716L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegsOrgsCourcesBean.class);

	private LazyDataModelSQL2Array regsList;
	PublicRegisterDAO publicRegisterDAO = new PublicRegisterDAO();
	private String title="";
	private String pageTitle="";
	private String [] titles= {"regsOrgsCources.title1","regsOrgsCources.title2"};

 	
	@PostConstruct
	void initData() {

		try {
			
			String par=JSFUtils.getRequestParameter("idReg");
			System.out.println("idReg: "+par);
			if(!par.isEmpty() ) {
				Integer idReg=Integer.valueOf(par);
				setTitle(titles[idReg-132]);
				setPageTitle(getMessageResourceString(LABELS, getTitle()));	
				regsList=new LazyDataModelSQL2Array(publicRegisterDAO.getRegisterAsList(idReg),null);
			}
				

		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на списък! ", e);
		}
	}
	
	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др.
	 */
	public void postProcessXLS(Object document) {
			  
    	new CustomExpPreProcess().postProcessXLS(document, title, null, null, null);		
     
	}
	

	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката
	 */
	public void preProcessPDF(Object document)  {
		try{	
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
	
	public Date getToday() {
    	return new Date();
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
		
}