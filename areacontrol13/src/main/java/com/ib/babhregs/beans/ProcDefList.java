package com.ib.babhregs.beans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.ProcDefDAO;
import com.ib.babhregs.db.dto.ProcDef;
import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.db.SelectMetadata;
import com.ib.system.exceptions.BaseException;

@Named
@ViewScoped
public class ProcDefList extends IndexUIbean   {

	/**
	 * Списък с дефиниции на процедури
	 * 
	 */
	private static final long serialVersionUID = -4029018534201157809L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcDefList.class);
	
	private Integer idReg;
	private Integer nomerProc;
	private String nameProc;
	private Integer status;	
	
	private LazyDataModelSQL2Array defProcList;	
	
	private Date decodeDate;	
	private ProcDef newProc;
	
	/** 
	 * 
	 * 
	 **/
	@PostConstruct
	public void initData() {
		
		LOGGER.debug("PostConstruct!!!");
		
		this.newProc = new ProcDef();
		this.decodeDate = new Date();
		this.idReg = Integer.valueOf(BabhConstants.CODE_REGISTRATURA_BABH);
	}
	
	/** 
	 * Списък с дефиниции на процедури по зададени критерии 
	 * 
	 */
	public void actionSearch(){
	
		SelectMetadata smd = new ProcDefDAO(getUserData()).createSelectDefProceduri(idReg, nomerProc, nameProc, null, status, null, null, null, null);
		String defaultSortColumn = "a0";
		this.defProcList = new LazyDataModelSQL2Array(smd, defaultSortColumn);			
	} 
	
	/**
	 * премахва избраните критерии за търсене
	 */
	public void actionClear() {		
		
		this.idReg = Integer.valueOf(BabhConstants.CODE_REGISTRATURA_BABH);
		this.nomerProc = null;
		this.nameProc = null;
		this.status = null;
				
		this.defProcList = null;
	}
	
	public String actionCopyDefProc(Integer idDef) {
		
		try {
			
			JPA.getUtil().runInTransaction(() -> this.newProc = new ProcDefDAO(getUserData()).copyProcDef(idDef, null, this.idReg, getSystemData()));

			getSystemData().reloadClassif(BabhConstants.CODE_CLASSIF_PROCEDURI, false, false);
			getSystemData().reloadClassif(BabhConstants.CODE_CLASSIF_DOC_VID_SETTINGS, false, false);
			
			actionSearch();			
			
		} catch (BaseException e) {			
			LOGGER.error("Грешка при копиране на дефиниция на процедура! ", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
		}
		
		return "procDefEdit.jsf?faces-redirect=true&idObj=" + this.newProc.getId();
	}
	
	public String actionGoto(Integer idObj) {
		
		return "procDefEdit.jsf?faces-redirect=true&idObj=" + idObj;
	}
	
	public String actionGotoNew() {
		
		return "procDefEdit.jsf?faces-redirect=true";	
	}
	
	
/******************************** EXPORTS **********************************/
	
	public Integer getIdReg() {
		return idReg;
	}

	public void setIdReg(Integer idReg) {
		this.idReg = idReg;
	}
		
	public Integer getNomerProc() {
		return nomerProc;
	}

	public void setNomerProc(Integer nomerProc) {
		this.nomerProc = nomerProc;
	}

	public String getNameProc() {
		return nameProc;
	}

	public void setNameProc(String nameProc) {
		this.nameProc = nameProc;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}	

	public LazyDataModelSQL2Array getDefProcList() {
		return defProcList;
	}

	public void setDefProcList(LazyDataModelSQL2Array defProcList) {
		this.defProcList = defProcList;
	}	
	
	public Date getDecodeDate() {
		return new Date(decodeDate.getTime()) ;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate != null ? new Date(decodeDate.getTime()) : null;
	}

	public ProcDef getNewProc() {
		return newProc;
	}

	public ProcDef setNewProc(ProcDef newProc) {
		this.newProc = newProc;
		return newProc;
	}

	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др. - за кореспондентите в групата
	 */
	public void postProcessXLSProcDefList(Object document) {
		
		String title = getMessageResourceString(LABELS, "procDefList.reportTitle");		  
    	new CustomExpPreProcess().postProcessXLS(document, title, dopInfoReport(), null, null);		
     
	}

	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката - за кореспондентите в групата
	 */
	public void preProcessPDFProcDefList(Object document)  {
		try{
			
			String title = getMessageResourceString(LABELS, "procDefList.reportTitle");		
			new CustomExpPreProcess().preProcessPDF(document, title, dopInfoReport(), null, null);		
						
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
	
	/**
	 * подзаглавие за екпсорта - дали да остане???
	 */
	public Object[] dopInfoReport() {
		Object[] dopInf = null;
		dopInf = new Object[2];
	
		return dopInf;
	}

}