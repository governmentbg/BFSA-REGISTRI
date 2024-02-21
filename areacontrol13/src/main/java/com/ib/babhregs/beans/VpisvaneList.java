package com.ib.babhregs.beans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.search.VpisvaneSearch;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.utils.DateUtils;

@Named(value = "vpisvaneList")
@ViewScoped
public class VpisvaneList extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7681834306252337562L;
	private static final Logger LOGGER = LoggerFactory.getLogger(VpisvaneList.class);
	
	private UserData ud;
	
	private VpisvaneSearch vs = new VpisvaneSearch();
	private Integer periodStatus = null;
	private Integer periodStVpisvane = null;
	private Integer periodDoc = null;
	private LazyDataModelSQL2Array vpisvaniaList;
	
	private List<SelectItem> registraturiList; // спсисък с позволените регистратури
	private List<SystemClassif> docVidList;
	
	private String licText;
	
	private Integer viewedRows; // запазва ид-то на реда, ако вече е бил разглеждан
	
	@PostConstruct
	public void init() {
		
		this.ud = getUserData(UserData.class);		
		this.docVidList = new ArrayList<>();
		
		try {
			
			this.registraturiList = createItemsList(true, BabhConstants.CODE_CLASSIF_REGISTRATURI, new Date(), true);			
			this.vs.setCodeRef(ud.getUserId());
			this.vs.setOnlyActiveProc(true);
			
		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на вписвания!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при инициализиране на вписвания");	
		}
		
	}
	
	
	public void search() {
		try {
			if(vs.getIdLicenziant() != null)
				vs.setLicenziantType(7);
				
			vs.buildQueryVpisvaneList((UserData)getUserData(), getSystemData());
			vpisvaniaList = new LazyDataModelSQL2Array(vs, "v.status");
			
			this.viewedRows = null;
			
		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на вписвания по зададени критерии!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());			
		}
	}
	

	/**
	 * премахва избраните критерии за търсене
	 */
	public void actionClear() {		
		vs = new VpisvaneSearch();
		this.vs.setCodeRef(ud.getUserId());
		this.vs.setOnlyActiveProc(true);

		this.periodStatus = null;
		this.periodStVpisvane = null;
		this.vpisvaniaList = null;
		this.licText = null;
		this.viewedRows = null;
		this.docVidList = new ArrayList<>();
	}

	public void changePeriodStatus() {
		
    	if (this.periodStatus != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.periodStatus);
			vs.setStatDateFrom( di[0] ) ;
			vs.setStatDateTo( di[1] );		
    	} else {
    		vs.setStatDateFrom(null);
    		vs.setStatDateTo(null);
    	}
    }
	
	public void changeDateStatus() { 
		this.setPeriodStatus(null);
	}
	

	public void changePeriodStVpisvane() {
		
    	if (this.periodStVpisvane != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.periodStVpisvane);
			this.vs.setDocDateFromUdost(di[0]);
			this.vs.setDocDateToUdost(di[1]);	
    	} else {
    		this.vs.setDocDateFromUdost(null);
    		this.vs.setDocDateToUdost(null);
		}
    }
	
	public void changeDateStVpisvane() { 
		this.setPeriodStVpisvane(null); 
	}
	
	public void changePeriodDoc() {
		
    	if (this.periodDoc != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.periodDoc);
			vs.setDocDateFromZaiav(di[0]);
			vs.setDocDateToZaiav(di[1]);		
    	} else {
    		vs.setDocDateFromZaiav(null);
    		vs.setDocDateToZaiav(null);
    	}
    }
	
	public void changeDateDoc() { 
		this.setPeriodDoc(null);
	}
	
	public VpisvaneSearch getVs() {
		return vs;
	}

	public void setVs(VpisvaneSearch vs) {
		this.vs = vs;
	}


	public Integer getPeriodStatus() {
		return periodStatus;
	}


	public void setPeriodStatus(Integer periodStatus) {
		this.periodStatus = periodStatus;
	}

	public Integer getPeriodStVpisvane() {
		return periodStVpisvane;
	}

	public void setPeriodStVpisvane(Integer periodStVpisvane) {
		this.periodStVpisvane = periodStVpisvane;
	}


	public LazyDataModelSQL2Array getVpisvaniaList() {
		return vpisvaniaList;
	}


	public void setVpisvaniaList(LazyDataModelSQL2Array vpisvaniaList) {
		this.vpisvaniaList = vpisvaniaList;
	}


	public String actionGoto(Integer idVpisvane, Integer licenziantType, Integer codePage) {

		setViewedRows(idVpisvane);
		
		String outcome = "dashboard.xhtml?faces-redirect=true";
		
		
			//Лице - дейности фуражи
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI)) {
				outcome = "regTargovtsiFuraj.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			//Лице - дейности ЗЖ
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ)) {
				outcome = "regLicaDeinZJ.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
						
			//Лице - дейност ВЛП
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP)) {
				outcome = "regVLPEdit.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			//регситарция на ВЛЗ
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ)) {
				outcome = "regVLZ.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
	
			//регситарция на ОЕЗ
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ)) {
				outcome = "regOEZ.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
	
			//регситарция на МПС - животни
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ)) {
				outcome = "regMpsZJ.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			//регситарция на МПС - фуражи
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS)) {
				outcome = "regMpsFuraj.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP)) {
				outcome = "regObektVLPEdit.xhtml?faces-redirect=true&idV=" + idVpisvane;
				// Търговия с ВЛП
			}
		return outcome;
	}

	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др.
	 */
	public void postProcessXLS(Object spForm) {
		
		String title = getMessageResourceString(LABELS, "vpisvaneList.reportTitle");		  
    	new CustomExpPreProcess().postProcessXLS(spForm, title, dopInfoReport(), null, null);		
     
	}
	
	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката
	 */
	public void preProcessPDF(Object spForm)  {
		
		try {
			
			String title = getMessageResourceString(LABELS, "vpisvaneList.reportTitle");		
			new CustomExpPreProcess().preProcessPDF(spForm, title, dopInfoReport(), null, null);		
						
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
	 * подзаглавие за екпсорта 
	 */
	public Object[] dopInfoReport() {
		
		Object[] dopInf = null;
		dopInf = new Object[3];
		
		if(vs.getResultDateFrom() != null && vs.getResultDateTo() != null) {
			dopInf[0] = "период на издавне на удостостоверителен документ: "+ DateUtils.printDate(vs.getResultDateFrom()) + " - "+ DateUtils.printDate(vs.getResultDateTo());
		} 
		
		if(vs.getStatDateFrom() != null && vs.getStatDateTo() != null) {			
			dopInf[1] = "период на статус: "+ DateUtils.printDate(vs.getStatDateFrom()) + " - "+ DateUtils.printDate(vs.getStatDateTo());
		} 
	
		return dopInf;
	}


	public String getLicText() {
		return licText;
	}


	public void setLicText(String licText) {
		this.licText = licText;
	}


	public List<SelectItem> getRegistraturiList() {
		return registraturiList;
	}


	public void setRegistraturiList(List<SelectItem> registraturiList) {
		this.registraturiList = registraturiList;
	}


	public UserData getUd() {
		return ud;
	}


	public void setUd(UserData ud) {
		this.ud = ud;
	}


	public List<SystemClassif> getDocVidList() {
		return docVidList;
	}


	public void setDocVidList(List<SystemClassif> docVidList) {
		this.docVidList = docVidList;
	}


	public Integer getViewedRows() {
		return viewedRows;
	}


	public void setViewedRows(Integer viewedRows) {
		this.viewedRows = viewedRows;
	}


	public Integer getPeriodDoc() {
		return periodDoc;
	}


	public void setPeriodDoc(Integer periodDoc) {
		this.periodDoc = periodDoc;
	}

	
}
