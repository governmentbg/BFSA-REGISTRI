package com.ib.babhregs.beans;

import static com.ib.system.SysConstants.CODE_CLASSIF_USERS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.navigation.NavigationDataHolder;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.SearchUtils;
import com.ib.system.utils.X;

/**
 * В този клас са показани начините за достъпване на х-ки от т.н. потребителски контекст.
 *
 * @author belev
 */
@Named
@SessionScoped
public class UserContext extends IndexUIbean {

	/**  */
	private static final long serialVersionUID = 1400459123152353504L;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserContext.class);

	private boolean customNotif;
	private List<SelectItem> optionsNotifList;
	private Integer [] selectedNotifList;
		
	private Integer [] selectedNstrLst;
	private List<SelectItem> optionsNstrLst;
	
	private boolean ldapLogin;

	/**  */
	public UserContext() {
		try {
			this.ldapLogin = BabhConstants.LOGIN_TYPE_LDAP.equals(getSystemData().getSettingsValue(BabhConstants.LOGIN_TYPE));
	
		} catch (DbErrorException e) { //
			LOGGER.error("ldapLogin check error!", e);
		}
	}

	//----------------------------------------------------------------------------------
	private boolean showModalNastr;
	
	
	public void actionSaveNastrUesr() {
		
		
//		System.out.println("actionSaveNastrUesr->  "+selectedNstrLst.length);
//		for(Integer test: selectedNstrLst) {
//			System.out.println("selectedNstrLst: "+test);
//		}
//		
//		System.out.println("actionSaveNastrUesr->  "+selectedNotifList.length);
//		for(Integer test: selectedNotifList) {
//			System.out.println("selectedNotifLst: "+test);
//		}
		
		try {
			UserData ud = getUserData(UserData.class);
			Map<Integer, Map<Integer, Boolean>> rez = new HashMap<>();
			
			X<Boolean> refreshSettings = X.of(Boolean.FALSE);
			X<Boolean> refreshNotifs = X.of(Boolean.FALSE);
			
			JPA.getUtil().runInTransaction(() -> rez.putAll(new ReferentDAO(ud).saveUserSettings(ud.getUserId(),  Arrays.asList(selectedNstrLst), Arrays.asList(selectedNotifList), getSystemData(), refreshSettings, refreshNotifs)));
			
			if (refreshSettings.isPresent() && refreshSettings.get()) {
				getSystemData().reloadClassif(CODE_CLASSIF_USERS, false, false);
			}
			if (refreshNotifs.isPresent() && refreshNotifs.get()) {
				((SystemData) getSystemData()).setUserNotifications(null);
			}

			ud.getAccessValues().putAll(rez); //за да ги презареди в УД
			
		} catch (BaseException e1) {
			LOGGER.error(e1.getMessage(), e1);
			JSFUtils.addErrorMessage(e1.getMessage(), e1);
		}
		
		
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		try {
			context.redirect(context.getRequestContextPath() + "/pages/dashboard.xhtml");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			actionClearNastrUesr();
		}
		
	}
	
	public void actionClearNastrUesr() {
		showModalNastr = false;
		
		selectedNotifList = null;
		selectedNstrLst = null;
	}
	
	public void actionShowModalNstr() {
		showModalNastr = true;
		
		UserData ud = getUserData(UserData.class);
		
		if(selectedNotifList ==null) {
			
			List<Integer> selectedNotifListTmp = new ArrayList<>();
			Map<Integer, Boolean> userNotifList = ud.getAccessValues().get(Integer.valueOf(BabhConstants.CODE_CLASSIF_NOTIFF_EVENTS_ACTIVE));
			
			if(userNotifList != null) {
			
				for(Entry<Integer, Boolean> entry  :userNotifList.entrySet()) {
					
					//System.out.println("entry -> "+entry.getKey() +" / value->"+entry.getValue());
					if(entry.getValue() !=null && entry.getValue().booleanValue()) {
						selectedNotifListTmp.add(entry.getKey());
					}
				}
			}
			selectedNotifList = new Integer[selectedNotifListTmp.size()];
			selectedNotifList = selectedNotifListTmp.toArray(selectedNotifList);
			
			//System.out.println("selectedNotifList--> "+selectedNotifList.length);
			
			customNotif=false;
			if(selectedNotifList.length != getOptionsNotifList().size()) {
				customNotif = true;
			}
			
		}
		if(selectedNstrLst ==null) {
			
			//selectedNstrLst = new Integer[0];
			List<Integer> selectedNstrListTmp = new ArrayList<>();
			Map<Integer, Boolean> userNastrList = ud.getAccessValues().get(Integer.valueOf(BabhConstants.CODE_CLASSIF_USER_SETTINGS));
			if(userNastrList!=null ) {
				for(Entry<Integer, Boolean> entry  :userNastrList.entrySet()) {
					System.out.println("entry -> "+entry.getKey() +" / value->"+entry.getValue());
					if(entry.getValue() !=null && entry.getValue().booleanValue()) {
						selectedNstrListTmp.add(entry.getKey());
					}
				}
			}
			selectedNstrLst = new Integer[selectedNstrListTmp.size()];
			selectedNstrLst = selectedNstrListTmp.toArray(selectedNstrLst);
			
			
		}
		
	}
	//-------------------------------------------------------------------------------------
	
	public boolean isShowModalNastr() {
		return showModalNastr;
	}

	public void setShowModalNastr(boolean showModalNastr) {
		this.showModalNastr = showModalNastr;
	}

	public List<SelectItem> getOptionsNotifList() {
		if(optionsNotifList == null) {
			
			try {
				optionsNotifList =   createItemsList(false, BabhConstants.CODE_CLASSIF_NOTIFF_EVENTS_ACTIVE,  new Date(), false);  //getSystemData().getSysClassification(BabhConstants.CODE_CLASSIF_NOTIFF_EVENTS_ACTIVE , new Date(), getCurrentLang());
			} catch (DbErrorException | UnexpectedResultException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
		return optionsNotifList;
	}

	public void setOptionsNotifList(List<SelectItem> optionsNotifList) {
		this.optionsNotifList = optionsNotifList;
	}

	public boolean isCustomNotif() {
		return customNotif;
	}

	public void setCustomNotif(boolean customNotif) {
		this.customNotif = customNotif;
	}

	public Integer [] getSelectedNotifList() {
		return selectedNotifList;
	}

	public void setSelectedNotifList(Integer [] selectedNotifList) {
		this.selectedNotifList = selectedNotifList;
	}

	public List<SelectItem> getOptionsNstrLst() {
		if(optionsNstrLst == null) {
			
			try {
				optionsNstrLst =   createItemsList(false, BabhConstants.CODE_CLASSIF_USER_SETTINGS,  new Date(), false);  //getSystemData().getSysClassification(BabhConstants.CODE_CLASSIF_USER_SETTINGS , new Date(), getCurrentLang());
			} catch (DbErrorException | UnexpectedResultException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return optionsNstrLst;
	}

	public void setOptionsNstrLst(List<SelectItem> optionsNstrLst) {
		this.optionsNstrLst = optionsNstrLst;
	}

	public Integer [] getSelectedNstrLst() {
		return selectedNstrLst;
	}

	public void setSelectedNstrLst(Integer [] selectedNstrLst) {
		this.selectedNstrLst = selectedNstrLst;
	}
	
	public void actionChngeNotif() {
		
		if(!customNotif) {
			selectedNotifList = new Integer[optionsNotifList.size()];
			int i=0;
			for(SelectItem item: optionsNotifList) {
				selectedNotifList[i] = (Integer) item.getValue();
				i++;
			}
		}
	}


	//------------------ global search-----------------------
	
	private Object[] glText;// = new Object[6];
	
	public List<Object[]> actionGlobalSearch(String glText) {
		
		if(glText==null || glText.trim().isEmpty()  ) {
			//mess
			
			return  new ArrayList<>();
		}
		
//		try {
//			return new DashboardUtils().searchObjectsByRN(glText, getUserData(UserData.class));
//		} catch (DbErrorException e) {
//			LOGGER.error("Грешка при глобално търсене! ", e);			
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
//		} finally {
//			 JPA.getUtil().closeConnection(); 
//		}
		
		return  new ArrayList<>();
	}
	
	
	public void onGlItemSelect(SelectEvent<Object[]> event) {
		
        // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", ));  
		
		//"docView.xhtml?faces-redirect=true&idObj="+idDoc;
		//result = "docEdit.jsf?faces-redirect=true&idObj=" + asInteger(row[0]);
		
		Object[] element = event.getObject();
		if(element !=null) {
			String idObj = SearchUtils.asString(element[0]);
			Integer type = SearchUtils.asInteger(element[3]); //1-док,2-дело,3-задача
			Integer mode = SearchUtils.asInteger(element[4]); //1-актуализация,0-преглед
			
			
			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			
			StringBuilder url = new StringBuilder(context.getRequestContextPath());
			url.append("/pages/");
			
			switch (type.intValue()) {
			
				case 1: //док
					if(mode.intValue()==1) {
						url.append("docEdit.xhtml?faces-redirect=true&idObj=");
						url.append(idObj);
					} else {
						url.append("docView.xhtml?faces-redirect=true&idObj=");
						url.append(idObj);
					}
				break;
				
				default:
					url = null;
					return;
			}
			
			
			
			if(url!=null) {
				try {
					if(mode.intValue()==1) {
						// следващите два реда зачистват навигацията, защото  Request-a e ajax.
						// това блокира извикването на навигацията и в екрана на обекта, бутона "Обратно" връща в логин страницата,ако преди това не е била отворено нищо друго, освен работния плот.
						// за да избегна това, най-лесно да зачистя навигацията и по този начин да скрия бутона "обратно" - поведението е аналогино на отваряне на дейност от менюто
						NavigationDataHolder holder = (NavigationDataHolder) JSFUtils.getManagedBean("navigationSessionDataHolder");
						holder.getPageList().clear();
						
						context.redirect(url.toString());
					}else {						
						Map<String, String> reqMap = context.getRequestHeaderMap();
						String origin = reqMap.get("origin");						
						PrimeFaces.current().executeScript("window.open('"+origin+url.toString()+"','_blank')");
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
    }
	
	
	
	public void onGlItemUnSelect(SelectEvent<Object[]> event) {
		//LOGGER.error("onGlItemUnSelect-->");
	}
	
	
	public Object[] getGlText() {
		return glText;
	}

	public void setGlText(Object[] glText) {
		this.glText = glText;
	}

	/** @return the ldapLogin */
	public boolean isLdapLogin() {
		return this.ldapLogin;
	}
}