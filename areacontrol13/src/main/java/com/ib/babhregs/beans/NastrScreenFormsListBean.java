package com.ib.babhregs.beans;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.search.NastrScreenFormSearch;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;

/**
 * 15.05.23Г. НОВ ФИЛТЪР ЗА ТЪРСЕНЕ НА НАСТРОЙКИ НА ИНФОРМАЦИОННИ ОБЕКТИ
 * ОПИСАНИЕТО НА МОДУЛА СЕ НАМИРА В src/docs -> Модул за въвеждане на настройки
 * на ИИСР.docx
 * 
 * @author s.arnaudova
 */

@Named
@ViewScoped
public class NastrScreenFormsListBean extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1619166637398413177L;
	private static final Logger LOGGER = LoggerFactory.getLogger(NastrScreenFormsListBean.class);

	private Date decodeDate;
	
	private NastrScreenFormSearch formsSearch;
	private LazyDataModelSQL2Array formsList;

	@PostConstruct
	private void init() {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("nastrScreenFormsListBean!!!!!!");
		}

		this.decodeDate = new Date();
		this.formsSearch = new NastrScreenFormSearch();
	}
	
	public void actionSearch() {
		try {

			this.formsSearch.buildQueryFormsList(getUserData(), getSystemData());
			formsList = new LazyDataModelSQL2Array(this.formsSearch, "a0 desc");

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при търсене на екранни форми! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(beanMessages, "general.exception"), e.getMessage());
		}
	}
	
	public void actionClear() {
		formsSearch = new NastrScreenFormSearch();
		formsList = null;
	}

	public String actionGotoNew() {
		return "nastrScreenForm.jsf?faces-redirect=true";
	}
	
	public String actionGotoEdit(String idObj) {
		return "nastrScreenForm.xhtml?faces-redirect=true&idObj=" + idObj;
	}
	

	/***************************************
	 * ЕКСПОРТИ
	 **************************************************/
	public void postProcessXLS(Object document) {
		try {

			new CustomExpPreProcess().postProcessXLS(document, "Настройки на информационни обекти", null, null, null);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(beanMessages, "general.exception"), e.getMessage());

		}
	}

	public void preProcessPDF(Object document) {
		try {

			new CustomExpPreProcess().preProcessPDF(document, "Настройки на информационни обекти", null, null, null);

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(beanMessages, "general.exception"), e.getMessage());
		}
	}

	public PDFOptions pdfOptions() {
		PDFOptions pdfOpt = new CustomExpPreProcess().pdfOptions(null, null, null);
        return pdfOpt;
	}

	/***************************************
	 * GET/SET
	 **************************************************/
	public Date getDecodeDate() {
		return decodeDate;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate;
	}

	public NastrScreenFormSearch getFormsSearch() {
		return formsSearch;
	}

	public void setFormsSearch(NastrScreenFormSearch formsSearch) {
		this.formsSearch = formsSearch;
	}

	public LazyDataModelSQL2Array getFormsList() {
		return formsList;
	}

	public void setFormsList(LazyDataModelSQL2Array formsList) {
		this.formsList = formsList;
	}

}
