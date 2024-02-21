package com.ib.babhregs.beans;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.SelectMetadata;


@Named
@ViewScoped
public class CorrespSpr extends IndexUIbean  implements Serializable {	
	
	/**
	 * view на данни за  лица
	 * 
	 */
	private static final long serialVersionUID = 1087155210424826840L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CorrespSpr.class);
	
	private static final String CODE_REF = "codeRef";
	
	private Integer codeCorresp;	
	private Date decodeDate = new Date();
	private LazyDataModelSQL2Array vpisvList;           //  Списък с вписвания, с участие на избраното лице	
	
	private SystemData sd;
	private UserData ud;
	private  VpisvaneDAO	dao;
	
	private LazyDataModelSQL2Array lazy;
	private List<Object[]> lstVpisv = null;
	private Integer brZap ;
	
	/** 
	 * 
	 * 
	 **/
	@PostConstruct
	public void initData() {
		
		LOGGER.debug("PostConstruct!!!");	
		this.sd = (SystemData) getSystemData();
		this.ud = getUserData(UserData.class);
		 this.lstVpisv  = null;
		 this.setBrZap(Integer.valueOf(0));
		
		if (JSFUtils.getRequestParameter(CODE_REF) !=  null && !JSFUtils.getRequestParameter(CODE_REF).isEmpty()) {
			this.codeCorresp = Integer.valueOf(JSFUtils.getRequestParameter(CODE_REF));
		}		
	   if (this.codeCorresp != null)  {
		   dao = new VpisvaneDAO(this.ud);
		   try {
		        SelectMetadata search = dao.createSelectReportLice(this.codeCorresp, null);
			
			     this. lazy = new LazyDataModelSQL2Array(search, "a0");
			      this.lstVpisv = lazy.load(0, lazy.getRowCount(), null, null);
			      if ( this.lstVpisv != null &&  this.lstVpisv.size() > 0) this.setBrZap(Integer.valueOf(this.lstVpisv.size()));
			    
		     } catch (Exception e) {
				e.printStackTrace();
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						"Грешка при търсене на вписвания за лицето! -  " + e.getLocalizedMessage());
				this.lstVpisv = null;
			}
	   }
		
	}
	
	public String actionGoto(Integer idVpisvane,  Integer codePage) {

			
		String outcome = "dashboard.xhtml?faces-redirect=true";
		
		
			//Лице - дейности фуражи
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI)) {
				outcome = "regTargovtsiFurajView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			//Лице - дейности ЗЖ
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ)) {
				outcome = "regLicaDeinZJView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
						
			//Лице - дейност ВЛП
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP)) {
				outcome = "regVLPView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			//регситарция на ВЛЗ
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ)) {
				outcome = "regVLZView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
	
			//регситарция на ОЕЗ
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ)) {
				outcome = "regOEZView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
	
			//регситарция на МПС - животни
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ)) {
				outcome = "regMpsZJView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			//регситарция на МПС - фуражи
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS)) {
				outcome = "regMpsFurajView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			// Търговия с ВЛП
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP)) {
				outcome = "regObektVLPView.xhtml?faces-redirect=true&isView=1&idV=" + idVpisvane;
				
			}
		return outcome;
	}

		
	public Integer getCodeCorresp() {
		return codeCorresp;
	}

	public void setCodeCorresp(Integer codeCorresp) {
		this.codeCorresp = codeCorresp;
	}
	
	public Date getDecodeDate() {
		return new Date(decodeDate.getTime()) ;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate != null ? new Date(decodeDate.getTime()) : null;
	}

	public LazyDataModelSQL2Array getVpisvList() {
		return vpisvList;
	}

	public void setVpisvList(LazyDataModelSQL2Array vpisvList) {
		this.vpisvList = vpisvList;
	}	
		

	public List<Object[]> getLstVpisv() {
		return lstVpisv;
	}

	public void setLstVpisv(List<Object[]> lstVpisv) {
		this.lstVpisv = lstVpisv;
	}

	public Integer getBrZap() {
		return brZap;
	}

	public void setBrZap(Integer brZap) {
		this.brZap = brZap;
	}
	
}