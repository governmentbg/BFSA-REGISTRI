package com.ib.babhregs.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.system.BabhClassifAdapter;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.BaseSystemData;
import com.ib.system.BaseUserData;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.BaseException;
import com.ib.system.utils.X;

/** */
@FacesComponent(value = "compAccess", createTag = true)
public class CompAccess extends UINamingContainer {

	private enum PropertyKeys {
		LISTLICA, NEWLISTLICA, SCLIST, CODEEXTCHECK
	}	
	public static final String	UI_beanMessages	= "ui_beanMessages";
	private static final Logger LOGGER = LoggerFactory.getLogger(CompAccess.class);
	private BaseSystemData	systemData	= null;
	private BaseUserData	userData	= null;
	private Date			dateClassif	= null;
	private static final Object[][] SPECADMOBJ = {{BabhClassifAdapter.ADM_STRUCT_INDEX_REF_TYPE, X.of(BabhConstants.CODE_ZNACHENIE_REF_TYPE_EMPL)},};
		 
	private List<Integer> lstTmp; 
	
	
	
	public void init() {
		getDateClassif();
		Integer idObj = (Integer) getAttributes().get("idObj");
		Integer codeObj = (Integer) getAttributes().get("codeObj");
		if(codeObj != null) {		
			if(Objects.equals(codeObj,BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC )) {
				
				try {
					JPA.getUtil().runWithClose(() -> lstTmp = new DocDAO(getUserData()).findDocAccess(idObj));
				} catch (BaseException e) {
					LOGGER.error(e.getMessage(), e);
				}
				
			} else { //if(Objects.equals(codeObj,CODE_ZNACHENIE_JOURNAL_VPISVANE )) {
				try {
					JPA.getUtil().runWithClose(() -> lstTmp = new VpisvaneDAO(getUserData()).findVpisvaneAccess (idObj));
				} catch (BaseException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if(lstTmp != null) {
				setListLica(lstTmp);
			}
			//Служителят  може да бъде от друга регистратура
			Integer s1 = BabhConstants.CODE_ZNACHENIE_DA; //ако текущия потребител има права - може да даде достъп на лице извън регистратурата
			if(((UserData) getUserData()).isLimitedAccess()) {
				s1 = BabhConstants.CODE_ZNACHENIE_NE; // само в рамките на регистратурата
			}
			if(Objects.equals(s1,  BabhConstants.CODE_ZNACHENIE_NE)) {
				Object[] codeExt = new Object[] {BabhClassifAdapter.ADM_STRUCT_INDEX_REGISTRATURA, 
						((UserData) getUserData()).getRegistratura().toString(), IndexUIbean.getMessageResourceString("beanMessages","task.msgCodeExt")};
				setCodeExtCheck(codeExt);
			}else {
				setCodeExtCheck(null);
			}
		}
	}
	
	/**
	 * премахване на достъп на лице и директен запис
	 * @param pr
	 * @param idx
	 */
	 public void actionRemoveLiceFromList (Integer lice, Integer idx) {
		getListLica().remove(lice);
		 actionSave(IndexUIbean.getMessageResourceString(UI_beanMessages, "general.successDeleteMsg"));
	 }
	 
	 /**
	  *  добавяне и запис на новите лица
	  */
	 public void actionAddLica() {
		 lstTmp =  getListLica();
		 for(Integer l: getNewListLica()) {
			 if(!lstTmp.contains(l)) {
				 lstTmp.add(l);
			 }
		 }
		 setListLica(lstTmp);
		 actionSave(IndexUIbean.getMessageResourceString(UI_beanMessages, "general.succesSaveMsg"));
		 setNewListLica(null);
		 setScList(null);
	 }
	 
	
	/**
	 * При натискане на бутон - "Запис"
	 * Дава достъп на избаните лица
	 *
	 */
	public void actionSave(String infoMsg) { 

		Integer idObj = (Integer) getAttributes().get("idObj");
		String infoJournal = (String) getAttributes().get("infoJournal"); 
		Integer codeObj = (Integer) getAttributes().get("codeObj");
		lstTmp = getListLica();
		if(codeObj != null) {
			if( infoJournal == null ) {
				LOGGER.error("Не е подадено полето за журнала. Инф. за лицензианта от вписването (licenziant) или инф. от заявлението - identInfo");
				return;
			}
			try {
				if(Objects.equals(codeObj,BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC )) {
					JPA.getUtil().runInTransaction(() -> { 
						new DocDAO(getUserData()).saveDocAccess(idObj, infoJournal, lstTmp);
					});										
				} else { //if(Objects.equals(codeObj,CODE_ZNACHENIE_JOURNAL_VPISVANE )) {
					JPA.getUtil().runInTransaction(() -> { 
						new VpisvaneDAO(getUserData()).saveVpisvaneAccess(idObj, infoJournal, lstTmp);
					});
				}
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, infoMsg);
			} catch (BaseException e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  IndexUIbean.getMessageResourceString(UI_beanMessages, "general.errDataBaseMsg"),e.getMessage());
			}
			
		} else {
			LOGGER.error("Не е подаден кода на обекта - codeObj!! ");
		}
		setListLica(lstTmp);
		 
	
//		ELContext ctx = getFacesContext().getELContext();
//		// извиква remoteCommnad - ако има такава....
//		String remoteCommnad = (String) getAttributes().get("onComplete");
//		if (remoteCommnad != null && !remoteCommnad.equals("")) {
//			PrimeFaces.current().executeScript(remoteCommnad);
//		}
	 }

	public Map<Integer, Object> getSpecificsAdm() {
		return Stream.of(SPECADMOBJ).collect(Collectors.toMap(data -> (Integer) data[0], data ->  data[1]));  // X.of() -> така ще дава само служители през аутокомплете, а в дървото ще е цялата
	}


	/** @return */
	@SuppressWarnings("unchecked")
	public List<Integer> getListLica() {
		List<Integer> eval = (List<Integer>) getStateHelper().eval(PropertyKeys.LISTLICA, null);
		return eval != null ? eval : new ArrayList<>();
	}
	
	/** * @param listLica */
	public void setListLica(List<Integer> listLica) {
		getStateHelper().put(PropertyKeys.LISTLICA, listLica);
	}

	/** @return */
	@SuppressWarnings("unchecked")
	public List<Integer> getNewListLica() {
		List<Integer> eval = (List<Integer>) getStateHelper().eval(PropertyKeys.NEWLISTLICA, null);
		return eval != null ? eval : new ArrayList<>();
	}
	
	/** * @param listLica */
	public void setNewListLica(List<Integer> newListLica) {
		getStateHelper().put(PropertyKeys.NEWLISTLICA, newListLica);
	}
	
	/**
	 * списък с нови лица
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SystemClassif> getScList() {
		List<SystemClassif> eval = (List<SystemClassif>) getStateHelper().eval(PropertyKeys.SCLIST, null);
		return eval != null ? eval : new ArrayList<>();
	}

	public void setScList(List<SystemClassif> scList) {
		getStateHelper().put(PropertyKeys.SCLIST, scList);
	}
	

	public Object[] getCodeExtCheck() {
		return (Object[]) getStateHelper().eval(PropertyKeys.CODEEXTCHECK, null);
	}


	public void setCodeExtCheck(Object[] codeExtCheck) {
		getStateHelper().put(PropertyKeys.CODEEXTCHECK, codeExtCheck);
	}	
	
	
	/** @return */
	public Integer getLang() {
		return getUserData().getCurrentLang();
	}
	
	public Date getCurrentDate() {
		return getDateClassif();
	}
	
	/** @return the dateClassif */
	private Date getDateClassif() {
		if (this.dateClassif == null) {
			this.dateClassif = (Date) getAttributes().get("dateClassif");
			if (this.dateClassif == null) {
				this.dateClassif = new Date();
			}
		}
		return this.dateClassif;
	}

	/** @return the systemData */
	@SuppressWarnings("unused")
	private BaseSystemData getSystemData() {
		if (this.systemData == null) {
			this.systemData = (BaseSystemData) JSFUtils.getManagedBean("systemData");
		}
		return this.systemData;
	}

	/** @return the userData */
	private BaseUserData getUserData() {
		if (this.userData == null) {
			this.userData = (BaseUserData) JSFUtils.getManagedBean("userData");
		}
		return this.userData;
	}

 	
}