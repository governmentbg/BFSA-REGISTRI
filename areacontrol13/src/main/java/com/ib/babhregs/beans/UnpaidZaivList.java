package com.ib.babhregs.beans;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.search.DocSearch;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.utils.DateUtils;

@Named
@ViewScoped
public class UnpaidZaivList extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5312366408793351018L;
	private static final Logger LOGGER = LoggerFactory.getLogger(UnpaidZaivList.class);
	private DocSearch docSearch = new DocSearch();
	private Integer periodR; // период на регистрация на документа
	private LazyDataModelSQL2Array docsList;
	private Object[] unpaidDoc = new Object[14];
	private BigDecimal paidAmount;
	private Date payDate;
	private String txtCorresp;
	private String dialogHeader = "Плащане на дължима такса за заявление";
	private boolean willEdit = false;
	
	private List<SystemClassif> docVidList = new ArrayList<>();
	
	@PostConstruct
	public void init() {
		
		docVidList = new ArrayList<>();
		
	}
	
	public void actionSearch() {
		try {
			docSearch.buildQueryUnpaidDocs(getUserData(UserData.class));
			docsList = new LazyDataModelSQL2Array(docSearch, "a4 desc");
		} catch(Exception e) {
			LOGGER.error("Грешка при зареждане на dokumentи за неплатени такси за заявления!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене на документи за неплатени такси за заявления!", e.getMessage());			
		}
	}
	
	public void actionClear() {
		docsList = null;
		docSearch = new DocSearch();
		docVidList = new ArrayList<>();
		periodR = null;
		txtCorresp = null;
		dialogHeader = "Плащане на дължима такса за заявление";
		willEdit = false;
	}

	public void changePeriodR() {

		if (this.periodR != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.periodR);
			docSearch.setDocDateFrom(di[0]);
			docSearch.setDocDateTo(di[1]);
		} else {
			docSearch.setDocDateFrom(null);
			docSearch.setDocDateTo(null);
		}
	}

	public void changeDate() {
		this.setPeriodR(null);
	}
	
	public void openModal(Object[] rowDoc) {
		payDate = null;
		rowDoc[4] = DateUtils.printDateFull((Date)rowDoc[4]);
		setUnpaidDoc(rowDoc);
		if(unpaidDoc[9] == null) {
			setPaidAmount(BigDecimal.valueOf(0));
			unpaidDoc[9] = BigDecimal.valueOf(0);
		}else
			setPaidAmount((BigDecimal)unpaidDoc[9]);
		dialogHeader = "Плащане на дължима такса за заявление";
		willEdit = false;
	}
	public void openModalEditTax(Object[] rowDoc) {
		payDate = (Date)rowDoc[13];
		rowDoc[4] = DateUtils.printDateFull((Date)rowDoc[4]);
		setUnpaidDoc(rowDoc);
		if(unpaidDoc[9] == null) {
			setPaidAmount(BigDecimal.valueOf(0));
			unpaidDoc[9] = BigDecimal.valueOf(0);
		}else
			setPaidAmount((BigDecimal)unpaidDoc[14]);
		dialogHeader = "Редакция на такса за заявление";
		willEdit = true;
	}
	
	public void actionPayTax() {
		
		boolean willSave = true;
		if(getPaidAmount() == null ) {
			willSave = false;
			if(willEdit)
				JSFUtils.addMessage("formUnpaidZaivList:paidAmountEdit", FacesMessage.SEVERITY_ERROR, "Моля, въведете Платена сума по-голяма от 0!");
			else
				JSFUtils.addMessage("formUnpaidZaivList:paidAmount", FacesMessage.SEVERITY_ERROR, "Моля, въведете Платена сума по-голяма от 0!");
		}else {
//			if(paidAmount > unpaidDoc[0])
		}
		if(getPayDate() == null) {
			willSave = false;
			if(willEdit) 
				JSFUtils.addMessage("formUnpaidZaivList:datePlashtaneEdit", FacesMessage.SEVERITY_ERROR, "Моля, въведете Дата на плащане!");
			else
				JSFUtils.addMessage("formUnpaidZaivList:datePlashtane", FacesMessage.SEVERITY_ERROR, "Моля, въведете Дата на плащане!");
		}
		if(willEdit) {
			if(unpaidDoc[9] == null || unpaidDoc[9].equals("")) {
				willSave = false;
				JSFUtils.addMessage("formUnpaidZaivList:owedAmountEdit", FacesMessage.SEVERITY_ERROR, "Моля, въведете Такса!");
			}
		}
		if(!willSave)
			return;
			
		try {
			JPA.getUtil().runInTransaction(() -> {
				if(willEdit)
					new DocDAO(getUserData()).updateEditTax(unpaidDoc, paidAmount, payDate);
				else
					new DocDAO(getUserData()).updatePaidTax(unpaidDoc, paidAmount, payDate);
			});
			actionSearch();
			payDate = null;
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,IndexUIbean.getMessageResourceString("ui_beanMessages", SUCCESSAVEMSG));
			if(willEdit)
				PrimeFaces.current().executeScript("PF('mEditZ').hide();");
			else
				PrimeFaces.current().executeScript("PF('mPayZ').hide();");
		} catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
			if(willEdit)
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при Редакция на такса за заявление!", e.getMessage());
			else
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при плащане на такса за заявление!", e.getMessage());
		}
	}
	
	public void closeModalMail() {
		PrimeFaces.current().executeScript("PF('eMail').hide();");
	}

	/**************************************************
	 * ЕКСПОРТИ
	 ***********************************************************/
	public void postProcessXLS(Object document) {
		try {

			String title = getMessageResourceString(LABELS, "babZaiavList.reportTitle");
			new CustomExpPreProcess().postProcessXLS(document, title, null, null, null);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(beanMessages, "general.exception"), e.getMessage());

		}
	}

	public void preProcessPDF(Object document) {
		try {

			String title = getMessageResourceString(LABELS, "babZaiavList.reportTitle");
			new CustomExpPreProcess().preProcessPDF(document, title, null, null, null);

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

	public DocSearch getDocSearch() {
		return docSearch;
	}

	public void setDocSearch(DocSearch docSearch) {
		this.docSearch = docSearch;
	}

	public Integer getPeriodR() {
		return periodR;
	}

	public void setPeriodR(Integer periodR) {
		this.periodR = periodR;
	}

	public LazyDataModelSQL2Array getDocsList() {
		return docsList;
	}

	public void setDocsList(LazyDataModelSQL2Array docsList) {
		this.docsList = docsList;
	}

	public Object[] getUnpaidDoc() {
		return unpaidDoc;
	}

	public void setUnpaidDoc(Object[] unpaidDoc) {
		this.unpaidDoc = unpaidDoc;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getTxtCorresp() {
		return txtCorresp;
	}

	public void setTxtCorresp(String txtCorresp) {
		this.txtCorresp = txtCorresp;
	}

	public String getDialogHeader() {
		return dialogHeader;
	}

	public void setDialogHeader(String dialogHeader) {
		this.dialogHeader = dialogHeader;
	}

	public boolean isWillEdit() {
		return willEdit;
	}

	public void setWillEdit(boolean willEdit) {
		this.willEdit = willEdit;
	}

	public List<SystemClassif> getDocVidList() {
		return docVidList;
	}

	public void setDocVidList(List<SystemClassif> docVidList) {
		this.docVidList = docVidList;
	}
	
}
