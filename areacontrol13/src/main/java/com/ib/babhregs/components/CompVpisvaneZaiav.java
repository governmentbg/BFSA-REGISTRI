package com.ib.babhregs.components;

import java.util.Date;
import java.util.TimeZone;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.SelectMetadata;

/**
 * КОМПОНЕНТА ЗА показване на всикчи вписвания, в които подаденото лице е заявител
 * 
 */

@FacesComponent(value = "compVpisvaneZaiav", createTag = true)
public class CompVpisvaneZaiav extends UINamingContainer {

	private enum PropertyKeys {
		VPLIST
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CompVpisvaneZaiav.class);

	private SystemData 		systemData = null;
	private UserData 		userData = null;
	private Date 			dateClassif = null;
	private TimeZone timeZone = TimeZone.getDefault();

	/**
	 * зарежда всички вписвания където подаденото лице е заявител, като изключва текущото
	 */
	public void actionSearch() {
		try {
			LOGGER.debug("compVpisvaneZaiav >>> ");
			Integer refCode = (Integer) getAttributes().get("codeRef"); 
			Integer idVp = (Integer) getAttributes().get("idVpisvane"); 
			SelectMetadata search = new VpisvaneDAO(getUserData()).createSelectReportLice(refCode, idVp);
			setVpList( new LazyDataModelSQL2Array(search, "a0"));
		}catch(Exception e)  {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * отваря избраното вписване за разглеждане
	 * @param idVpisvane
	 * @param codePage
	 * @return
	 */
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
	public Integer getLang() {
		return getUserData().getCurrentLang();
	}

	public SystemData getSystemData() {
		if (this.systemData == null) {
			this.systemData = (SystemData) JSFUtils.getManagedBean("systemData");
		}
		return this.systemData;
	}

	public void setSystemData(SystemData systemData) {
		this.systemData = systemData;
	}

	/** @return the userData */
	private UserData getUserData() {
		if (this.userData == null) {
			this.userData = (UserData) JSFUtils.getManagedBean("userData");
		}
		return this.userData;
	}

	public Date getDateClassif() {
		if (this.dateClassif == null) {
			this.dateClassif = (Date) getAttributes().get("dateClassif");
			if (this.dateClassif == null) {
				this.dateClassif = new Date();
			}
		}
		return this.dateClassif;
	}

	public void setDateClassif(Date dateClassif) {
		this.dateClassif = dateClassif;
	}

	public LazyDataModelSQL2Array getVpList() {
		return (LazyDataModelSQL2Array) getStateHelper().eval(PropertyKeys.VPLIST, null);
	}

	public void setVpList(LazyDataModelSQL2Array vpList) {
		getStateHelper().put(PropertyKeys.VPLIST, vpList);
	}

 
	public void setUserData(UserData userData) {
		this.userData = userData;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}


	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

}
