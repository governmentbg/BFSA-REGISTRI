package com.ib.babhregs.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.ProcDefDAO;
import com.ib.babhregs.db.dto.ProcDef;
import com.ib.babhregs.db.dto.ProcDefEtap;
import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.navigation.Navigation;
import com.ib.indexui.navigation.NavigationData;
import com.ib.indexui.navigation.NavigationDataHolder;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.SearchUtils;

@Named
@ViewScoped
public class ProcDefEdit extends IndexUIbean  implements Serializable {	
	
	
	/**
	 * Въвеждане / актуализация на дефиниция на процедура 
	 * 
	 */
	private static final long serialVersionUID = -6080711597503598046L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcDefEdit.class);
	
	private static final String ID_OBJ = "idObj";
	private static final String FORM_PROC_DEF = "formProcDef";
	
	private Date decodeDate = new Date();
	
	private ProcDef procDef;	
	private List<ProcDefEtap> defEtapsList;
	
	private transient ProcDefDAO procDAO;
	
	private List<Files> filesList;
	
	private Integer oldStatus;	
	
	/** 
	 * 
	 * 
	 **/
	@PostConstruct
	public void initData() {
		
		LOGGER.debug("PostConstruct!!!");
		
		try {
		
			this.procDAO = new ProcDefDAO(getUserData());
		
			this.procDef = new ProcDef();
			this.defEtapsList = new ArrayList<>();	
			
			if (JSFUtils.getRequestParameter(ID_OBJ) != null && !"".equals(JSFUtils.getRequestParameter(ID_OBJ))) {
				
				Integer idObj = Integer.valueOf(JSFUtils.getRequestParameter(ID_OBJ));	
				
				if (idObj != null) {
					
					JPA.getUtil().runWithClose(() -> {
						
						this.procDef = this.procDAO.findById(idObj);					
	
						// извличане на файловете от таблица с файловете
						this.filesList = new FilesDAO(getUserData()).selectByFileObject(this.procDef.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_PROC_DEF);
						
						this.defEtapsList = this.procDAO.selectDefEtapList(this.procDef.getId(), null);
						
						this.oldStatus = this.procDef.getStatus();
					});
				}				
			
			} else {
				
				this.procDef.setRegistraturaId(Integer.valueOf(BabhConstants.CODE_REGISTRATURA_BABH));
				this.procDef.setStatus(BabhConstants.CODE_ZNACHENIE_PROC_DEF_STAT_DEV );
				this.procDef.setWorkDaysOnly(BabhConstants.CODE_ZNACHENIE_NE);
				this.oldStatus = this.procDef.getStatus();
			}			
					
		} catch (BaseException e) {
			LOGGER.error("Грешка при зареждане данните на дефиниция на процедура ! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
		}
		
	}
	
	public void actionNew() {
		
		this.procDef = new ProcDef();
		this.procDef.setRegistraturaId(Integer.valueOf(BabhConstants.CODE_REGISTRATURA_BABH));
		this.procDef.setStatus(BabhConstants.CODE_ZNACHENIE_PROC_DEF_STAT_DEV );
		this.procDef.setWorkDaysOnly(BabhConstants.CODE_ZNACHENIE_NE);
		this.defEtapsList = new ArrayList<>();		
	}
	
	private boolean checkDataForProc() {
		
		boolean save = false;	
		
		if(SearchUtils.isEmpty(this.procDef.getProcName())) {
			JSFUtils.addMessage(FORM_PROC_DEF + ":procName", FacesMessage.SEVERITY_ERROR, getMessageResourceString (UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "procDefList.nameProc")));
			save = true;
		}
		
		if (this.procDef.getSrokDays() == null) {
			JSFUtils.addMessage(FORM_PROC_DEF + ":srokDays", FacesMessage.SEVERITY_ERROR, getMessageResourceString (UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "procDefList.srokDni")));
			save = true;
		}
		
		if (this.procDef.getWorkDaysOnly() == null) {
			JSFUtils.addMessage(FORM_PROC_DEF + ":workDaysOnly", FacesMessage.SEVERITY_ERROR, getMessageResourceString (UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "procDefEdit.workDaysOnly")));
			save = true;
		}
		
		if (this.procDef.getStatus() == null) {
			JSFUtils.addMessage(FORM_PROC_DEF + ":status", FacesMessage.SEVERITY_ERROR, getMessageResourceString (UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "procDefList.status")));
			save = true;
		}
		
		return save;		
	}	
	
	public void actionSave(boolean mess) {
		
		if(checkDataForProc()) {
			return;
		}
		
		try {
			
			JPA.getUtil().runInTransaction(() -> this.procDef = this.procDAO.save(this.procDef, getSystemData()));			

			getSystemData().reloadClassif(BabhConstants.CODE_CLASSIF_PROCEDURI, false, false);			
			getSystemData().reloadClassif(BabhConstants.CODE_CLASSIF_DOC_VID_SETTINGS, false, false);
			
			if (mess) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG));
			}
			
		} catch (BaseException e) {			
			LOGGER.error("Грешка при запис на дефиниция на процедура! ", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			
			if (!this.oldStatus.equals(this.procDef.getStatus())) {
				this.procDef.setStatus(this.oldStatus);	 			
			}
		}
	}
	
	public void actionValidate() {
		
		try {
			
			JPA.getUtil().runWithClose(() ->  {
				
				String err = this.procDAO.validate(this.procDef, getSystemData());
				
				if (err == null) {					
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(beanMessages, "procDefEdit.validDefProc"));					
				
				} else {	
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, err);
				}
			});	
			
		} catch (BaseException e) {			
			LOGGER.error("Грешка при валидиране на дефиниция на процедура! ", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
		}
	}
	
	public void actionDelete() {
		
		try {			
			
			JPA.getUtil().runInTransaction(() ->  this.procDAO.delete(this.procDef));
			
			getSystemData().reloadClassif(BabhConstants.CODE_CLASSIF_PROCEDURI, false, false);		
			getSystemData().reloadClassif(BabhConstants.CODE_CLASSIF_DOC_VID_SETTINGS, false, false);
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, "general.successDeleteMsg"));
			
			actionNew();
			
		    
		} catch (ObjectInUseException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());		
	
		} catch (BaseException e) {
			LOGGER.error("Грешка при изтриване на процедура! ", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
		}
		
		Navigation navHolder = new Navigation();			
	    int i = navHolder.getNavPath().size();	
	   
	    NavigationDataHolder dataHoslder = (NavigationDataHolder) JSFUtils.getManagedBean("navigationSessionDataHolder");
	    Stack<NavigationData> stackPath = dataHoslder.getPageList();
	    NavigationData nd = stackPath.get(i-2);
	    Map<String, Object> mapV = nd.getViewMap();
	    
	    ProcDefList procList = (ProcDefList) mapV.get("procDefList");	    
	    procList.actionSearch();
		
	} 
	
	public String actionGotoEtap(Integer idObj) {
		
		return "procDefEtapEdit.jsf?faces-redirect=true&idObj=" + idObj + "&idProc=" + this.procDef.getId();
	}
	
	public String actionGotoNewEtap() {
		
		return "procDefEtapEdit.jsf?faces-redirect=true&idProc=" + this.procDef.getId();
	}
	
	public String actionGotoDiagram() {
		
		return "testDiagramEdit.jsf?faces-redirect=true&idProc=" + this.procDef.getId();
	}
	
	/******************************************************* GET & SET *******************************************************/	
	
	public Date getDecodeDate() {
		return new Date(decodeDate.getTime()) ;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate != null ? new Date(decodeDate.getTime()) : null;
	}

	public ProcDef getProcDef() {
		return procDef;
	}

	public void setProcDef(ProcDef procDef) {
		this.procDef = procDef;
	}	

	public List<ProcDefEtap> getDefEtapsList() {
		return defEtapsList;
	}

	public void setDefEtapsList(List<ProcDefEtap> defEtapsList) {
		this.defEtapsList = defEtapsList;
	}
	
	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public Integer getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(Integer oldStatus) {
		this.oldStatus = oldStatus;
	}	

	/**************************************************** END GET & SET ****************************************************/	
		
}