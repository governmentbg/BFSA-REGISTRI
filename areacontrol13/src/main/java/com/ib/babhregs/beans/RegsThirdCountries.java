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

 *  Списък на предприятия, одобрени за износ на царевица за Китай idReg=37
 *  Списък на предприятия, одобрени за износ на люцерна за Китай idReg=38 
 *  Списък на предприятия, одобрени за износ на комбинирани фуражи за Китай idReg=39
 *  Списък на обектите за производства на фуражни добавки, одобрени за износ за Китай idReg=40
 *  Списък на предприятия за преработка, одобрени за износ на слънчогледов шрот за Китай idReg=41
 *  Списък на предприятия за преработка, одобрени за износ на български сух зърнен спиртоварен остатък с извлеци за Китай idReg=42
 *  Регистър на операторите с издаден ветеринарен сертификат за износ на фуражи за трети държави по чл. 53з, ал. 1 от ЗФ idReg=126
 * @author yoni
 */
@Named
@ViewScoped
public class RegsThirdCountries   extends IndexUIbean   {

	private static final long serialVersionUID = -1912235627470078986L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegsThirdCountries.class);

	private LazyDataModelSQL2Array regsList;
	PublicRegisterDAO publicRegisterDAO = new PublicRegisterDAO();
	private String title="";
	private String pageTitle="";
	private String [] titles= {"regsThirdCountries1.title","regsThirdCountries2.title","regsThirdCountries3.title","regsThirdCountries4.title","regsThirdCountries5.title","regsThirdCountries6.title","regsThirdCountries7.title"};

 	
	@PostConstruct
	void initData() {

		try {
			String par=JSFUtils.getRequestParameter("idReg");
			System.out.println("idReg: "+par);
			if(!par.isEmpty() ) {
				Integer idReg=Integer.valueOf(par);
				setTitle(idReg!=126?titles[idReg-37]:titles[6]);
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