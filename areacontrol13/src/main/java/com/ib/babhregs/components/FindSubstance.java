package com.ib.babhregs.components;

import java.util.Date;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.SubstanceDAO;
import com.ib.babhregs.db.dto.Substance;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.utils.JSFUtils;



/**
 * КОМПОНЕНТА ЗА ТЪРСЕНЕ НА Вещества
 * 
 * @author  Николай Косев
 */

@FacesComponent(value = "findSubstance", createTag = true)
public class FindSubstance extends UINamingContainer {

	private enum PropertyKeys {
		substanceList, substance, nameSubstanceText
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(FindSubstance.class);

	private SystemData 		systemData = null;
	private UserData 		userData = null;
	private Date 			dateClassif = null;

	/**
	 * Инициализиране на Вещества
	 */
	public void initComp() {
		setSubstance(new Substance());
		ValueExpression selectedSubstanceName = getValueExpression("selectedSubstanceName"); // само name на обекта
		ELContext selectedSubstanceNameContex = getFacesContext().getELContext();
		if(selectedSubstanceName != null ) {
			if( selectedSubstanceName.getValue(selectedSubstanceNameContex) == null || selectedSubstanceName.getValue(selectedSubstanceNameContex).toString().trim().isEmpty())
				setNameSubstanceText(null);
			else
				setNameSubstanceText(selectedSubstanceName.getValue(selectedSubstanceNameContex).toString());
		}
	}

	/**
	 * Търсене на Вещества
	 */
	public void actionSearchSubstance() {
		try {
			setSubstanceList(new LazyDataModelSQL2Array( new SubstanceDAO().buildSelectMetaDataSubstance(getSubstance().getIdentifier(), getSubstance().getName()), "a1 "));
		} catch (Exception e) {
			LOGGER.debug("Грешка при търсене на Вещества!" + e);
		}
	}

	/**
	 * Затваряне на модaлния
	 */
	public void actionClear() {
		LOGGER.debug("actionClear");
		setSubstance(new Substance());
//		actionSearchSubstance();
	}

	/**
	 * При избор от модалния връща обект от тип Substance
	 */
	public void actionSelectMPS(Object[] row) {
		try {
			if (row != null && row[0] != null) {

				ValueExpression expr1 = getValueExpression("selectedSubstanceName"); // само name на обекта
				ValueExpression expr2 = getValueExpression("selectedSubstance"); // целия ред
				
				ELContext ctx1 = getFacesContext().getELContext();
				if (expr1 != null) {
					expr1.setValue(ctx1, row[1]);
				}
				if (expr2 != null) {
					Substance tmpSubstance = new Substance();
					tmpSubstance.setIdentifier((String)row[0]);
					tmpSubstance.setName((String)row[1]);
					expr2.setValue(ctx1, tmpSubstance);
				}
			}

			// извиква remoteCommnad - ако има такава....
			String remoteCommnad = (String) getAttributes().get("onComplete");
			if (remoteCommnad != null && !remoteCommnad.equals("")) {
				PrimeFaces.current().executeScript(remoteCommnad);
			}
			getSubstance().setName((String)row[1]);
			setNameSubstanceText((String)row[1]);
		} catch (Exception e) {
			LOGGER.debug("Грешка при избор на Вещества!" + e);
		}
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

	public LazyDataModelSQL2Array getSubstanceList() {
		return (LazyDataModelSQL2Array) getStateHelper().eval(PropertyKeys.substanceList, null);
	}

	public void setSubstanceList(LazyDataModelSQL2Array substanceList) {
		getStateHelper().put(PropertyKeys.substanceList, substanceList);
	}

	public Substance getSubstance() {
		Substance eval = (Substance) getStateHelper().eval(PropertyKeys.substance);
		return eval != null ? eval : new Substance();
	}

	public void setSubstance(Substance substance) {
		getStateHelper().put(PropertyKeys.substance, substance);
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}

	public String getNameSubstanceText() {
		String eval = (String) getStateHelper().eval(PropertyKeys.nameSubstanceText);
		return eval != null ? eval : "";
	}

	public void setNameSubstanceText(String nameSubstanceText) {
		getStateHelper().put(PropertyKeys.nameSubstanceText, nameSubstanceText);
	}

	
}
