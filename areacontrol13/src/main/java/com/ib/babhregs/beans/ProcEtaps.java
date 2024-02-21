package com.ib.babhregs.beans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.ProcDefDAO;
import com.ib.babhregs.db.dao.ProcDefEtapDAO;
import com.ib.babhregs.db.dao.ProcExeDAO;
import com.ib.babhregs.db.dao.ProcExeEtapDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.ProcDef;
import com.ib.babhregs.db.dto.ProcDefEtap;
import com.ib.babhregs.db.dto.ProcExe;
import com.ib.babhregs.db.dto.ProcExeEtap;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.db.SelectMetadata;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;

@Named
@ViewScoped
public class ProcEtaps extends IndexUIbean {

	/**
	 * Етапи на обработка на изпълнение на процедура 
	 * 
	 */
	
	private static final long serialVersionUID = 2812887657801040333L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcEtaps.class);
	
	public  static final  String BEANNAME_ZJ   			= "regLicaDeinZJ";
	public  static final  String BEANNAME_MPS_ZJ   		= "regMpsZJ";
	public  static final  String BEANNAME_VLZ  			= "regVLZ";
	public  static final  String BEANNAME_FR   			= "regTargovtsiFuraj";
	public  static final  String BEANNAME_MPS_FR   		= "regMpsFuraj";
	public  static final  String BEANNAME_VLP  			= "regVLP";
	public  static final  String BEANNAME_TURGOVIA_VLP  = "regObektVLP";
	public  static final  String BEANNAME_OEZ 			="regOEZ";
	
	private SystemData sd;
	private UserData ud;
	private Date decodeDate;
	
	private ProcExe procExe;
	private ProcDef procDef;
	
	private List<ProcDefEtap> defEtapsList;	
	private List<Files> filesListForDefProc;
	
	private ProcExeEtap etapExe;
	private ProcDefEtap etapDef;
	private LazyDataModelSQL2Array etapExeList;
	
	private transient ProcExeDAO procExeDAO;
	private transient ProcExeEtapDAO etapDAO;
	
	/**
	 *  името на бийна, в който е инклудната страницата
	 */
	private String beanName;
	private String clId;
	
	/**
	 * вписване - от бийна, в който е инклудната страницата
	 */ 
	private Vpisvane vpObj;
	
	private boolean nextOk;	
	private Integer next;
	private Integer idEtapExe;
	
	// За следващ при Да и при НЕ - ще се използва името на етапа
	private List<SelectItem> nameNext;	
	
	// Списък с документ, които имат отворена процедура към вписването
	private List<Object[]> docsListWithProc;
	private boolean onlyOneProc;

	public void initTab() {
		
		this.sd = (SystemData) getSystemData();
		this.ud = getUserData(UserData.class);
		this.decodeDate = new Date();
		
		this.procExe = new ProcExe();
		this.procDef = new ProcDef();
		
		this.defEtapsList = new ArrayList<>();	
		
		this.etapExe = new ProcExeEtap();
		this.etapDef = new ProcDefEtap();
		
		this.procExeDAO = new ProcExeDAO(getUserData());
		this.etapDAO = new ProcExeEtapDAO(getUserData());	
		this.clId = "";
		this.nameNext = new ArrayList<>();
		this.docsListWithProc = new ArrayList<>();
		
		try {
		
		this.beanName = (String)JSFUtils.getFlashScopeValue("beanName");			
		
		if(BEANNAME_ZJ.equals(beanName)) { // лица - дейности животни
			RegLicaDeinZJ bean = (RegLicaDeinZJ) JSFUtils.getManagedBean(BEANNAME_ZJ);
			this.vpObj = bean.getVpisvane();
			this.clId = "dZJForm:registerTabs:";
		
		} else if(BEANNAME_MPS_ZJ.equals(beanName)){ // МПС - регистриране на МПС животни
			RegMpsZJ bean = (RegMpsZJ) JSFUtils.getManagedBean(BEANNAME_MPS_ZJ);
			this.vpObj = bean.getVpisvane();
			this.clId = "mpsZJForm:registerTabs:";
		
		} else if(BEANNAME_VLZ.equals(beanName)){ // ЗЖ - регистриране на ВЛЗ
			RegVLZ bean = (RegVLZ) JSFUtils.getManagedBean(BEANNAME_VLZ);
			this.vpObj = bean.getVpisvane();
			this.clId = "vlzForm:registerTabs:";
	   
		} else if(BEANNAME_FR.equals(beanName)){ // лица - дейности фуражи
			RegTargovtsiFuraj bean = (RegTargovtsiFuraj) JSFUtils.getManagedBean(BEANNAME_FR);
			this.vpObj = bean.getVpisvane();
			this.clId = "targovtsiFurajForm:registerTabs:";
		
		} else if(BEANNAME_MPS_FR.equals(beanName)){ // МПС - регистриране на МПС фуражи
			RegMpsFuraj bean = (RegMpsFuraj) JSFUtils.getManagedBean(BEANNAME_MPS_FR);
			this.vpObj = bean.getVpisvane();
			this.clId = "targovtsiFurajForm:registerTabs:";
	    
		} else if(BEANNAME_VLP.equals(beanName)){ // лица - дейности ВЛП 
	    	RegVLP bean = (RegVLP) JSFUtils.getManagedBean(BEANNAME_VLP);
	    	this.vpObj = bean.getVpisvane();
	    	this.clId = "vlpTargForm:registerTabs:";
		
		} else if(BEANNAME_TURGOVIA_VLP.equals(beanName)){ // търговия с ВЛП 
			RegObektVLP bean = (RegObektVLP) JSFUtils.getManagedBean(BEANNAME_TURGOVIA_VLP);
			this.vpObj = bean.getVpisvane();
			this.clId = "obektVlpForm:registerTabs:";
		
		} else if(BEANNAME_OEZ.equals(beanName)){ // OEZ
			RegOEZ bean = (RegOEZ) JSFUtils.getManagedBean(BEANNAME_OEZ);
			this.vpObj = bean.getVpisvane();
			this.clId = "oezForm:registerTabs:";
		}
		
		if (this.vpObj != null) {
			
			JPA.getUtil().runWithClose(() -> {
				
				this.docsListWithProc = new VpisvaneDAO(getUserData()).findProcDocList(this.vpObj.getId());
				
				if (this.docsListWithProc != null && this.docsListWithProc.size() == 1) {
					this.onlyOneProc = true;
				} else if (this.docsListWithProc != null && this.docsListWithProc.size() > 1) {
					this.onlyOneProc = false;
				}
				
				if (this.onlyOneProc) {				

					Integer[] proc = new VpisvaneDAO(getUserData()).findProcExeIdent(vpObj.getId());
					
					if (proc != null) {
						
						this.procExe = this.procExeDAO.findById(SearchUtils.asInteger(proc[0]));
						
						this.procDef = new ProcDefDAO(getUserData()).findById(this.procExe.getDefId());
						
						this.defEtapsList = new ProcDefDAO(getUserData()).selectDefEtapList(this.procDef.getId(), null);
						
						// извличане на файловете към дефиницията на процедурата от таблица с файловете
						this.filesListForDefProc = new FilesDAO(getUserData()).selectByFileObject(this.procDef.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_PROC_DEF);
											
						actionSearchEtapExeList();			
					} 
				
				} else {
					
					this.procExe = new ProcExe();
					this.procDef = new ProcDef();
					this.defEtapsList = new ArrayList<>();	
					
					this.etapExe = new ProcExeEtap();
					this.etapDef = new ProcDefEtap();
					
					this.etapExeList = null;
				}
				
			});
			
		}
		
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при извличане данните за процедурата и етапите към нея!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		
		} catch (BaseException e) {
			LOGGER.error("Грешка при извличане данните за процедурата и етапите към нея!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		}
		
	}
	
	public void actionLoadDataForProc(Integer procExeId, Integer procDefId) {
		
		this.onlyOneProc = true;
		
		try {
			
			JPA.getUtil().runWithClose(() -> {
				
				this.procExe = this.procExeDAO.findById(procExeId);
				
				this.procDef = new ProcDefDAO(getUserData()).findById(procDefId);
				
				this.defEtapsList = new ProcDefDAO(getUserData()).selectDefEtapList(this.procDef.getId(), null);
				
				this.filesListForDefProc = new FilesDAO(getUserData()).selectByFileObject(this.procDef.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_PROC_DEF);
				
				actionSearchEtapExeList();				
			
			});
		
		} catch (BaseException e) {
			LOGGER.error("Грешка при зареждане данните за процедурата и етапите към нея!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		}
	}
	
	public void actionSearchEtapExeList() {
		
		SelectMetadata smd = this.procExeDAO.createSelectEtapExeList(this.procExe.getId(), this.procExe.getDefId());
		String defaultSortColumn = "a0";
		this.etapExeList = new LazyDataModelSQL2Array(smd, defaultSortColumn);
	}
	
	public void actionFindDataForEtap(boolean fromUsl, Integer idEtapExe) {
		
		this.idEtapExe = idEtapExe;
		this.nameNext = new ArrayList<>();
		
		try {
			
			JPA.getUtil().runWithClose(() -> {
				
				this.etapExe = this.etapDAO.findById(idEtapExe, true);
				
				this.etapDef = new ProcDefEtapDAO(getUserData()).findById(SearchUtils.asInteger(this.etapExe.getDefEtapData()[0]), true); 	
				
			});
			
			if (this.etapDef != null) {
				if (!this.etapDef.getNextOkList().isEmpty()) {
					Object[] etapDef =  this.etapDef.getNextOkList().get(0);
					this.nameNext.add(new SelectItem(BabhConstants.CODE_ZNACHENIE_DA, SearchUtils.asString(etapDef[1]) + ". " + SearchUtils.asString(etapDef[2]))); 
				}
				
				if (!this.etapDef.getNextNotList().isEmpty()) {
					Object[] etapDef =  this.etapDef.getNextNotList().get(0);
					this.nameNext.add(new SelectItem(BabhConstants.CODE_ZNACHENIE_NE, SearchUtils.asString(etapDef[1]) + ". " + SearchUtils.asString(etapDef[2]))); 
				}
			}
			
			if (fromUsl) {
				
				if (!this.etapDef.getNextOkList().isEmpty()) {
					next = Integer.valueOf(BabhConstants.CODE_ZNACHENIE_DA);
				} else {
					next = Integer.valueOf(BabhConstants.CODE_ZNACHENIE_NE);
				}
				
				String dialogWidgetVar = "PF('modalEndEtap').show();";
				PrimeFaces.current().executeScript(dialogWidgetVar);
			
			} else {
				next = null;				
			}
			
		} catch (BaseException e) {			
			LOGGER.error("Грешка при зареждане данните на конкретния етап! ", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
		}	
		
	}
	
	public void actionCompleteEtap(boolean fromUsl, Integer idEtapExe) {
		
		if (!fromUsl) {
			actionFindDataForEtap(false, idEtapExe);
		}

		if (next == Integer.valueOf(BabhConstants.CODE_ZNACHENIE_DA)) {
			nextOk = true;

		} else if (next == Integer.valueOf(BabhConstants.CODE_ZNACHENIE_NE)) {
			nextOk = false;
		} 

		try {

			StringBuilder comments = new StringBuilder();

			if (next != null) {

				if (nextOk) { // ако е true - избрано е да се продължи със следващ етап (при ДА)

					if (this.etapDef.getNextOkList().size() > 0) {

						Object[] next = this.etapDef.getNextOkList().get(0);

						comments.append("За следващ етап е определен № " + SearchUtils.asString(next[1]) + " " + SearchUtils.asString(next[2]));
					}

				} else { // ако е false - избрано е да се продължи със следващ етап при НЕ

					if (this.etapDef.getNextNotList().size() > 0) {

						Object[] next = this.etapDef.getNextNotList().get(0);

						comments.append("За следващ етап е определен № " + SearchUtils.asString(next[1]) + " " + SearchUtils.asString(next[2]));
					}
				}

				JPA.getUtil().runInTransaction(() -> {
					
					this.etapExe = this.etapDAO.completeEtap(this.etapExe, this.sd, nextOk, null, comments.toString());
					
					this.procExe = this.procExeDAO.findById(this.procExe.getId());
					
					this.docsListWithProc = new VpisvaneDAO(getUserData()).findProcDocList(this.vpObj.getId());
					
				});

			} else {

				JPA.getUtil().runInTransaction(() -> {
					
					this.etapExe = this.etapDAO.completeEtap(this.etapExe, this.sd, null, null, comments.toString());
					
					this.procExe = this.procExeDAO.findById(this.procExe.getId());
					
					this.docsListWithProc = new VpisvaneDAO(getUserData()).findProcDocList(this.vpObj.getId());
				});
			}

			if (fromUsl) {

				String dialogWidgetVar = "PF('modalEndEtap').hide();";
				PrimeFaces.current().executeScript(dialogWidgetVar);
			}

		} catch (BaseException e) {
			LOGGER.error("Грешка при приключване на етапа! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		}
	}
	
	public void actionReturnEtap(Integer idEtapExe) {
			
		try {
			
			JPA.getUtil().runInTransaction(() -> { 
				
				this.etapExe = new ProcExeEtapDAO(getUserData()).findById(idEtapExe, true);
				
				ProcExeEtap tmpEtapPrev = new ProcExeEtapDAO(getUserData()).findById(SearchUtils.asInteger(this.etapExe.getPrev()[0]), false); 
				
				if (tmpEtapPrev.getStatus().equals(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_ETAP_STAT_IZP)) 
						|| tmpEtapPrev.getStatus().equals(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_ETAP_STAT_IZP_SROK))) {   
					
					new ProcExeEtapDAO(getUserData()).returnEtap(this.procExe, tmpEtapPrev, this.sd);
				
				} else {
					
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "procEtaps.selEtapForReturn"));
					PrimeFaces.current().executeScript("scrollToErrors()"); 
				}				
				
			});
			
			actionSearchEtapExeList();
		
		} catch (BaseException e) {
			LOGGER.error("Грешка при връщане на изпълнението! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		}
				
	}
	
	public void actionStopProc() {
		
		if (SearchUtils.isEmpty(this.procExe.getStopReason())) { 
			JSFUtils.addMessage(clId + "txtStopReason", FacesMessage.SEVERITY_ERROR, getMessageResourceString (UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "procExeEdit.reasonForStop")));
			return;
		
		} else {
			
			try {
				
				JPA.getUtil().runInTransaction(() -> {
					
					this.procExe = this.procExeDAO.stopProc(this.procExe, this.procExe.getStopReason(), false, getSystemData());
					
					this.docsListWithProc = new VpisvaneDAO(getUserData()).findProcDocList(this.vpObj.getId());
				});
				
				String dialogWidgetVar = "PF('modalStopProc').hide();";
				PrimeFaces.current().executeScript(dialogWidgetVar);
				
				actionSearchEtapExeList();			
				
			} catch (BaseException e) {			
				LOGGER.error("Грешка при прекратяване на изпълнението! ", e);	
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
			}			
		}		
	}
	
	/******************************************************* GET & SET *******************************************************/	
	
	/** @return the sd */
	public SystemData getSd() {
		return sd;
	}

	/** @param sd the sd to set */
	public void setSd(SystemData sd) {
		this.sd = sd;
	}

	/** @return the ud */
	public UserData getUd() {
		return ud;
	}

	/** @param ud the ud to set */
	public void setUd(UserData ud) {
		this.ud = ud;
	}

	/** @return the decodeDate */
	public Date getDecodeDate() {
		return decodeDate;
	}

	/** @param decodeDate the decodeDate to set */
	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate;
	}

	/** @return the procExe */
	public ProcExe getProcExe() {
		return procExe;
	}

	/** @param procExe the procExe to set */
	public void setProcExe(ProcExe procExe) {
		this.procExe = procExe;
	}

	/** @return the procDef */
	public ProcDef getProcDef() {
		return procDef;
	}

	/** @param procDef the procDef to set */
	public void setProcDef(ProcDef procDef) {
		this.procDef = procDef;
	}

	/** @return the defEtapsList */
	public List<ProcDefEtap> getDefEtapsList() {
		return defEtapsList;
	}

	/** @param defEtapsList the defEtapsList to set */
	public void setDefEtapsList(List<ProcDefEtap> defEtapsList) {
		this.defEtapsList = defEtapsList;
	}

	/** @return the filesListForDefProc */
	public List<Files> getFilesListForDefProc() {
		return filesListForDefProc;
	}

	/** @param filesListForDefProc the filesListForDefProc to set */
	public void setFilesListForDefProc(List<Files> filesListForDefProc) {
		this.filesListForDefProc = filesListForDefProc;
	}

	/** @return the etapExe */
	public ProcExeEtap getEtapExe() {
		return etapExe;
	}

	/** @param etapExe the etapExe to set */
	public void setEtapExe(ProcExeEtap etapExe) {
		this.etapExe = etapExe;
	}

	/** @return the etapDef */
	public ProcDefEtap getEtapDef() {
		return etapDef;
	}

	/** @param etapDef the etapDef to set */
	public void setEtapDef(ProcDefEtap etapDef) {
		this.etapDef = etapDef;
	}

	/** @return the etapExeList */
	public LazyDataModelSQL2Array getEtapExeList() {
		return etapExeList;
	}

	/** @param etapExeList the etapExeList to set */
	public void setEtapExeList(LazyDataModelSQL2Array etapExeList) {
		this.etapExeList = etapExeList;
	}

	/** @return the beanName */
	public String getBeanName() {
		return beanName;
	}

	/** @param beanName the beanName to set */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/** @return the clId */
	public String getClId() {
		return clId;
	}

	/** @param clId the clId to set */
	public void setClId(String clId) {
		this.clId = clId;
	}

	/** @return the vpObj */
	public Vpisvane getVpObj() {
		return vpObj;
	}

	/** @param vpObj the vpObj to set */
	public void setVpObj(Vpisvane vpObj) {
		this.vpObj = vpObj;
	}
	
	/** @return the nextOk */
	public boolean isNextOk() {
		return nextOk;
	}

	/** @param nextOk the nextOk to set */
	public void setNextOk(boolean nextOk) {
		this.nextOk = nextOk;
	}	

	/** @return the next */
	public Integer getNext() {
		return next;
	}

	/** @param next the next to set */
	public void setNext(Integer next) {
		this.next = next;
	}
	
	/** @return the idEtapExe */
	public Integer getIdEtapExe() {
		return idEtapExe;
	}

	/** @param idEtapExe the idEtapExe to set */
	public void setIdEtapExe(Integer idEtapExe) {
		this.idEtapExe = idEtapExe;
	}	

	/**
	 * @return the nameNext
	 */
	public List<SelectItem> getNameNext() {
		return nameNext;
	}

	/**
	 * @param nameNext the nameNext to set
	 */
	public void setNameNext(List<SelectItem> nameNext) {
		this.nameNext = nameNext;
	}
	
	/**
	 * @return the docsListWithProc
	 */
	public List<Object[]> getDocsListWithProc() {
		return docsListWithProc;
	}

	/**
	 * @param docsListWithProc the docsListWithProc to set
	 */
	public void setDocsListWithProc(List<Object[]> docsListWithProc) {
		this.docsListWithProc = docsListWithProc;
	}
	
	/**
	 * @return the onlyOneProc
	 */
	public boolean isOnlyOneProc() {
		return onlyOneProc;
	}

	/**
	 * @param onlyOneProc the onlyOneProc to set
	 */
	public void setOnlyOneProc(boolean onlyOneProc) {
		this.onlyOneProc = onlyOneProc;
	}
	
	/**************************************************** END GET & SET ******************************************************/	

	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др. - за списък с етапи за изпълнение на процедурата
	 */
	public void postProcessXLSProcEtapExeList(Object document) {
		
		String title = getMessageResourceString(LABELS, "procEtapExeList.reportTitle");		  
    	new CustomExpPreProcess().postProcessXLS(document, title, null, null, null);		
     
	}

	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката - за списък с етапи за изпълнение на процедурата
	 */
	public void preProcessPDFProcEtapExeList(Object document)  {
		try{
			
			String title = getMessageResourceString(LABELS, "procEtapExeList.reportTitle");		
			new CustomExpPreProcess().preProcessPDF(document, title, null, null, null);		
						
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
	
}
