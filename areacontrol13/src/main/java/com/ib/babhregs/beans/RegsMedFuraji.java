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


/**
 * Регистър на одобрените обекти за производство с медикаментозни фуражи idReg=79
 * Регистър на одобрените обекти за търговия с медикаментозни фуражи idReg=80
 * 
 * Регистър на одобрените обекти  медикаментозни фуражи 34
 * @author yoni
 */
@Named
@ViewScoped
public class RegsMedFuraji   extends IndexUIbean   {

	private static final long serialVersionUID = 7522772888733924048L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegsMedFuraji.class);

	private LazyDataModelSQL2Array regsList;
	PublicRegisterDAO publicRegisterDAO = new PublicRegisterDAO();
//	private String title="";
//	private String pageTitle="";
//	private String [] titles= {"regsMedFuraji1.title","regsMedFuraji2.title"};
//	private boolean showLin=false;
// 	
	@PostConstruct
	void initData() {

		try {
			regsList=new LazyDataModelSQL2Array(publicRegisterDAO.getRegisterAsList(34),null);
//			String par=JSFUtils.getRequestParameter("idReg");
//			System.out.println("idReg: "+par);
			//if(!par.isEmpty() ) {
				//Integer idReg=Integer.valueOf(par);
				//setTitle(titles[idReg-79]);
				//setPageTitle(getMessageResourceString(LABELS, getTitle()));	
				//showLin= (idReg==79);
				
		//	}
				

		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на списък! ", e);
		}
	}
	
	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др.
	 */
	public void postProcessXLS(Object document) {
			  
    	new CustomExpPreProcess().postProcessXLS(document, "regsMedFuraji.title", null, null, null);		
     
	}
	

	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката
	 */
	public void preProcessPDF(Object document)  {
		try{	
			new CustomExpPreProcess().preProcessPDF(document, "regsMedFuraji.title",  null, null, null);		
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

//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	public String getPageTitle() {
//		return pageTitle;
//	}
//
//	public void setPageTitle(String pageTitle) {
//		this.pageTitle = pageTitle;
//	}
//
//	public boolean isShowLin() {
//		return showLin;
//	}
//
//	public void setShowLin(boolean showLin) {
//		this.showLin = showLin;
//	}
		
}