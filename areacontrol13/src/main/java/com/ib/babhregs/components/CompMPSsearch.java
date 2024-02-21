package com.ib.babhregs.components;

import java.io.IOException;
import java.util.Date;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.primefaces.PrimeFaces;
import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.MpsDAO;
import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.search.MPSsearch;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;

/**
 * КОМПОНЕНТА ЗА ТЪРСЕНЕ НА МПС
 * 
 * @author s.arnaudova
 * 
 */

@FacesComponent(value = "compMPSsearch", createTag = true)
public class CompMPSsearch extends UINamingContainer {

	private enum PropertyKeys {
		MPSLIST, MPSSEARCH
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CompMPSsearch.class);

	private SystemData 		systemData = null;
	private UserData 		userData = null;
	private Date 			dateClassif = null;
	private Mps 			MPS = new Mps();
	private Integer 		codePage;

	/**
	 * Инициализиране на компонентата
	 */
	public void initComp() {
		
		LOGGER.debug("compMPSsearch >>> ");
		
		MPSsearch searchMPS = new MPSsearch();
		setMPSsearch(searchMPS);
		
	}

	/**
	 * Търсене на МПС
	 */
	public void actionSearchMPS() {
		try {
			
			Boolean registered = (Boolean) getAttributes().get("registered");
			getMPSseach().setRegistered(registered);
			
			this.codePage = (Integer) getAttributes().get("codePage");
			if (codePage != null)  {
				getMPSseach().setCodePage(codePage);
			}
			
			getMPSseach().buildQueryMPSList();
			setMPSList(new LazyDataModelSQL2Array(getMPSseach(), "a0 desc"));

		} catch (Exception e) {
			LOGGER.debug("Грешка при търсене на МПС");
		}
	}

	/**
	 * Затваряне на моделния
	 */
	public void actionClear() {
		LOGGER.debug("actionClear");

		MPSsearch searchMPS = new MPSsearch();
		setMPSsearch(searchMPS);
		setMPSList(null);
	}

	/**
	 * При избор от модалния връща обект от тип MPS
	 */
	public void actionSelectMPS(Object[] row) {

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("actionSelectMPS>>>> {}", row[0]);
			}

			if (row != null && row[0] != null) {

				ValueExpression expr1 = getValueExpression("selectedMPSid"); // само ид-то на обекта
				ValueExpression expr2 = getValueExpression("selectedMPS"); // целия ред
				
				ELContext ctx1 = getFacesContext().getELContext();
				if (expr1 != null) {
					expr1.setValue(ctx1, row[0]);
				}
				
				if (expr2 != null) {
					
					//връщаме целия обект MPS
					String mpsID = row[0].toString();
					JPA.getUtil().runWithClose(() -> this.MPS = new MpsDAO(getUserData()).findById(Integer.valueOf(mpsID)));
					
					expr2.setValue(ctx1, this.MPS);
				}
			}

			// извиква remoteCommnad - ако има такава....
			String remoteCommnad = (String) getAttributes().get("onComplete");
			if (remoteCommnad != null && !remoteCommnad.equals("")) {
				PrimeFaces.current().executeScript(remoteCommnad);
			}

		} catch (Exception e) {
			LOGGER.debug("Грешка при избор на МПС");
			e.printStackTrace();
		}
	}

	/************** ЕКСПОРТИ ******************/
	public void postProcessXLS(Object document) {
		try {
			new CustomExpPreProcess().postProcessXLS(document, "Списък моторни превозни средства", null, null, null);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void preProcessPDF(Object document) {
		try {
			new CustomExpPreProcess().preProcessPDF(document, "Списък моторни превозни средства", null, null, null);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public PDFOptions pdfOptions() {
		PDFOptions pdfOpt = new CustomExpPreProcess().pdfOptions(null, null, null);
		return pdfOpt;
	}

	/*******************************************/
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

	public LazyDataModelSQL2Array getMPSList() {
		return (LazyDataModelSQL2Array) getStateHelper().eval(PropertyKeys.MPSLIST, null);
	}

	public void setMPSList(LazyDataModelSQL2Array MPSList) {
		getStateHelper().put(PropertyKeys.MPSLIST, MPSList);
	}

	public MPSsearch getMPSseach() {
		MPSsearch eval = (MPSsearch) getStateHelper().eval(PropertyKeys.MPSSEARCH, null);
		return eval != null ? eval : new MPSsearch();
	}

	public void setMPSsearch(MPSsearch MPSsearch) {
		getStateHelper().put(PropertyKeys.MPSSEARCH, MPSsearch);
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}
	
}
