package com.ib.babhregs.beansPublic.registers;

import com.ib.babhregs.db.dao.PublicRegisterDAO;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Named
@ViewScoped
public class Register72 extends IndexUIbean   {

	/**
	 * Регистър на търговците на животни
	 */
	private static final long serialVersionUID = 1574880756078348004L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Register72.class);

	private LazyDataModelSQL2Array regsList;

 	
	@PostConstruct
	void initData() {

		try {
			regsList=new LazyDataModelSQL2Array( new PublicRegisterDAO().getRegisterAsList(72),"date_licenz desc");

		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на списък! ", e);
		}
	}
	
	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др.
	 */
	public void postProcessXLS(Object document) {
		
		String title = getMessageResourceString(LABELS, "regsSellersAnim.title");		  
    	new CustomExpPreProcess().postProcessXLS(document, title, null, null, null);		
     
	}
	

	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката
	 */
	public void preProcessPDF(Object document)  {
		try{
			
			String title = getMessageResourceString(LABELS, "regsSellersAnim.title");		
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