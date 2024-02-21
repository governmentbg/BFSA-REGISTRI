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
 * Регистър на издадените становища на земеделски производители, кандидатстващи по подмерки 4.1, 4.2, 5.1, 6.1, 6.3 от ПРСР в сектор „Фуражи“
 * Регистър на издадените становища за прилагане на подмярка 4.1 от ПРСР type=1 idRed=127
 * Регистър на издадените становища за прилагане на подмярка 4.2 от ПРСР type=2 idRed=128
 * Регистър на издадените становища за прилагане на подмярка 5.1 от ПРСР (animals) type=5 idRed=129
 * Регистър на издадените становища за прилагане на подмярка 6.1 от ПРСР type=3 idRed=130
 * Регистър на издадените становища за прилагане на подмярка 6.3 от ПРСР type=4 idRed= 131
 * @author yoni
 */
@Named
@ViewScoped
public class RegsProizvoditeliMiarka   extends IndexUIbean   {

	private static final long serialVersionUID = 6424513594020621108L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegsProizvoditeliMiarka.class);

	private LazyDataModelSQL2Array regsList;
	PublicRegisterDAO publicRegisterDAO = new PublicRegisterDAO();
	private String title="";
	private String pageTitle="";
	private String [] titles= {"regsProizvoditeliMiarka41.title","regsProizvoditeliMiarka42.title", "regsProizvoditeliMiarka51.title","regsProizvoditeliMiarka61.title", "regsProizvoditeliMiarka63.title" };
	private boolean showAnimals=false;
 	
	@PostConstruct
	void initData() {

		try {
			String par=JSFUtils.getRequestParameter("idReg");
			System.out.println("idReg: "+par);
			if(!par.isEmpty() ) {
				Integer idReg=Integer.valueOf(par);
				setTitle(titles[idReg-127]);
				setPageTitle(getMessageResourceString(LABELS, getTitle()));	
				showAnimals= (idReg==129);
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

	public boolean isShowAnimals() {
		return showAnimals;
	}

	public void setShowAnimals(boolean showAnimals) {
		this.showAnimals = showAnimals;
	}
		
}