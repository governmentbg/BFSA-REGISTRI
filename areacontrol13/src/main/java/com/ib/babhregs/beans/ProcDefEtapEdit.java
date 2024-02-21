package com.ib.babhregs.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.ProcDefDAO;
import com.ib.babhregs.db.dao.ProcDefEtapDAO;
import com.ib.babhregs.db.dto.ProcDef;
import com.ib.babhregs.db.dto.ProcDefEtap;
import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.navigation.Navigation;
import com.ib.indexui.navigation.NavigationData;
import com.ib.indexui.navigation.NavigationDataHolder;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;

@Named
@ViewScoped
public class ProcDefEtapEdit extends IndexUIbean  implements Serializable {	
	
	/**
	 * Въвеждане / актуализация на дефиниция на процедура 
	 * 
	 */
	private static final long serialVersionUID = 1440311381608392101L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcDefEtapEdit.class);
	
	private static final String ID_OBJ = "idObj";
	private static final String ID_PROC = "idProc";
	private static final String FORM_PROC_DEF_ETAP = "formProcDefEtap";
	
	private Date decodeDate;
	
	private ProcDef proc;
	private ProcDefEtap defEtap;
	private List<ProcDefEtap> etapsList;	
	
	private transient ProcDefDAO procDAO;
	private transient ProcDefEtapDAO etapDAO;
	
	private int nextNomerEtap;	
	
//	private List<SystemClassif> selNextOk;
//	private List<SystemClassif> selNextNot;
//	private List<SystemClassif> selNextOpt; // засега скриваме опционалните етапи - Жоро каза, че не може с тях да се работи, щом няма да се указва, че  етапа е събирателен - 10.07.2023
	
	private List<SelectItem> selNextOk;
	private List<SelectItem> selNextNot;
	
	private Integer oldNumberEtap;
	
	/** 
	 * 
	 * 
	 **/
	@PostConstruct
	public void initData() {
		
		LOGGER.debug("PostConstruct!!!");
		
		try {
		
			this.decodeDate = new Date();
			this.proc = new ProcDef();
			this.procDAO = new ProcDefDAO(getUserData());
			this.etapDAO = new ProcDefEtapDAO(getUserData());
			
			this.defEtap = new ProcDefEtap();
			this.etapsList = new ArrayList<>();			
			this.nextNomerEtap = 0;
			
			this.selNextOk = new ArrayList<>();
			this.selNextNot = new ArrayList<>();
//			this.selNextOpt = new ArrayList<>();
						
			if (JSFUtils.getRequestParameter(ID_PROC) != null && !"".equals(JSFUtils.getRequestParameter(ID_PROC))) {					
				
				JPA.getUtil().runWithClose(() -> {
					
					this.proc = this.procDAO.findById(Integer.valueOf(JSFUtils.getRequestParameter(ID_PROC)));
					
					this.etapsList = this.procDAO.selectDefEtapList(this.proc.getId(), null);
				}); 
				
				for (ProcDefEtap etap : this.etapsList) {					
					this.nextNomerEtap = etap.getNomer();					
				}
			}		
			
			if (JSFUtils.getRequestParameter(ID_OBJ) != null && !"".equals(JSFUtils.getRequestParameter(ID_OBJ))) {
				
				Integer idObj = Integer.valueOf(JSFUtils.getRequestParameter(ID_OBJ));	
				
				if (idObj != null) {
					
					JPA.getUtil().runWithClose(() -> {
						
						this.defEtap = this.etapDAO.findById(idObj);						
					});
					
					this.oldNumberEtap = this.defEtap.getNomer();		
				}				
			
			} else {
				
				actionNewEtap();
			} 
			
			for (ProcDefEtap etap : this.etapsList) {					
				
				if (!this.defEtap.getNomer().equals(etap.getNomer())) {
					this.selNextOk.add(new SelectItem(SearchUtils.asString(etap.getNomer()), SearchUtils.asString(etap.getNomer() + " - " + etap.getEtapName()))); 
					this.selNextNot.add(new SelectItem(SearchUtils.asString(etap.getNomer()), SearchUtils.asString(etap.getNomer() + " - " + etap.getEtapName()))); 
				}
			}
		
		} catch (BaseException e) {
			LOGGER.error("Грешка при зареждане данните на дефиниция на процедура ! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
		}
		
	}	
	
	/******************************************************* ETAP ***********************************************************/	
	
	public void actionNewEtap() {			
		
		this.defEtap = new ProcDefEtap();
		this.defEtap.setDefId(this.proc.getId());		
		this.defEtap.setExtendProcSrok(BabhConstants.CODE_ZNACHENIE_NE);
		this.defEtap.setNomer(this.nextNomerEtap + 1);	
		
//		this.selNextOk = new ArrayList<>();
//		this.selNextNot = new ArrayList<>();
//		this.selNextOpt = new ArrayList<>();
	}
	
	public void actionEditEtap(Integer idObj) {	
		
		try {
			
			if (idObj != null) {		
				
				JPA.getUtil().runWithClose(() -> {
					
					this.defEtap = this.etapDAO.findById(idObj);
					
				});				
			}
		
		} catch (BaseException e) {
			LOGGER.error("Грешка при зареждане данните на етап! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
		}
		
	}
	
	public boolean actionCheckForEtapNomer() {
		
		if (this.defEtap.getNomer() != null) {			
			
			try {
				
				boolean existNomer = false;
				
				if (!this.etapsList.isEmpty()) {
					for (ProcDefEtap etap : etapsList) {
						if (this.defEtap.getId() == null) {
							if (this.defEtap.getNomer().equals(etap.getNomer())) {
								existNomer = this.etapDAO.isEtapNomerExist(this.proc.getId(), this.defEtap.getNomer());
								break;
							}
						} else if (!this.defEtap.getId().equals(etap.getId())) {
							if (this.defEtap.getNomer().equals(etap.getNomer())) {
								existNomer = this.etapDAO.isEtapNomerExist(this.proc.getId(), this.defEtap.getNomer());
								break;
							}
						}
					}
				}
				
				if (existNomer) {
					JSFUtils.addMessage(FORM_PROC_DEF_ETAP + ":etapNomer", FacesMessage.SEVERITY_ERROR, getMessageResourceString (beanMessages, "procDefEtapEdit.existNomer"));
					return true;
				}
			
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при проверка номер на етап!!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
			}
		}
		
		return false; 		
	}

	private boolean checkDataForEtap() {
		
		boolean save = false;	
		
		if (this.defEtap.getNomer() == null) {
			JSFUtils.addMessage(FORM_PROC_DEF_ETAP + ":etapNomer", FacesMessage.SEVERITY_ERROR, getMessageResourceString (UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "procDefList.nomProc")));
			save = true;
		
		} else {
			
			if(actionCheckForEtapNomer()) {
				save = true;				
			}
		}
		
		if(SearchUtils.isEmpty(this.defEtap.getEtapName())) {
			JSFUtils.addMessage(FORM_PROC_DEF_ETAP + ":etapName", FacesMessage.SEVERITY_ERROR, getMessageResourceString (UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "procDefList.nameProc")));
			save = true;
		}			
		
		if (this.defEtap.getSrokDays() == null) {
			JSFUtils.addMessage(FORM_PROC_DEF_ETAP + ":etapSrokDays", FacesMessage.SEVERITY_ERROR, getMessageResourceString (UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "procDefList.srokDni")));
			save = true;
		}
		
		// Марияна поиска премахване ограничението "Следв. етапи при НЕ и Следв. етапи - опц. да се попълват само, ако има въведено Следв. етапи при ДА" - 07.06.2022 
//		if (this.selNextOk.isEmpty() && !this.selNextNot.isEmpty()) {
//			JSFUtils.addMessage(FORM_PROC_DEF_ETAP + ":nextOk", FacesMessage.SEVERITY_ERROR, getMessageResourceString (beanMessages, "procDefEtapEdit.selNextEtapsNot"));
//			save = true;			
//		}
//		
//		if (this.selNextOk.isEmpty() && !this.selNextOpt.isEmpty()) {
//			JSFUtils.addMessage(FORM_PROC_DEF_ETAP + ":nextOk", FacesMessage.SEVERITY_ERROR, getMessageResourceString (beanMessages, "procDefEtapEdit.selNextEtapsOpt"));
//			save = true;			
//		}
		
		return save;		
	}
	
	public void actionSaveEtap() {
		
		if(checkDataForEtap()) {
			return;
		}
		
//		this.defEtap.setNextOk(null);
//		this.defEtap.setNextNot(null);
//		this.defEtap.setNextOptional(null);
		
		try {
			
//			for (SystemClassif sel : this.selNextOk) {
//				
//				if (this.defEtap.getNextOk() == null) {
//					this.defEtap.setNextOk(SearchUtils.asString(sel.getCode()) + ",");
//				} else {
//					this.defEtap.setNextOk(this.defEtap.getNextOk() + SearchUtils.asString(sel.getCode()) + ",");
//				}				
//			}
//			
//			for (SystemClassif sel : this.selNextNot) {
//				
//				if (this.defEtap.getNextNot() == null) {
//					this.defEtap.setNextNot(SearchUtils.asString(sel.getCode()) + ",");
//				} else {
//					this.defEtap.setNextNot(this.defEtap.getNextNot() + SearchUtils.asString(sel.getCode()) + ",");
//				}
//			}
//			
//			for (SystemClassif sel : this.selNextOpt) {
//
//				if (this.defEtap.getNextOptional() == null) {
//					this.defEtap.setNextOptional(SearchUtils.asString(sel.getCode()) + ",");
//				} else {
//					this.defEtap.setNextOptional(this.defEtap.getNextOptional() + SearchUtils.asString(sel.getCode()) + ",");
//				}
//			}
//			
//			if (this.defEtap.getNextOk() != null) {
//				this.defEtap.setNextOk(this.defEtap.getNextOk().substring(0, this.defEtap.getNextOk().lastIndexOf(",")));
//			}
//			
//			if (this.defEtap.getNextNot() != null) {
//				this.defEtap.setNextNot(this.defEtap.getNextNot().substring(0, this.defEtap.getNextNot().lastIndexOf(",")));
//			}
//			
//			if (this.defEtap.getNextOptional() != null) {
//				this.defEtap.setNextOptional(this.defEtap.getNextOptional().substring(0, this.defEtap.getNextOptional().lastIndexOf(",")));
//			}	
					
			JPA.getUtil().runInTransaction(() ->  this.defEtap = this.etapDAO.save(defEtap));
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG));	
			
			if (this.oldNumberEtap != null && !this.oldNumberEtap.equals(this.defEtap.getNomer())) { 
				List<ProcDefEtap> tmpEtaps = idsForChange(this.oldNumberEtap);
				if (tmpEtaps != null) {
					for (ProcDefEtap etap : tmpEtaps) {
						JPA.getUtil().runInTransaction(() ->  this.etapDAO.save(etap));					
					}
				}				
			}
			
			JPA.getUtil().runWithClose(() -> this.etapsList = this.procDAO.selectDefEtapList(this.proc.getId(), null)); 
			
			for (ProcDefEtap etap : this.etapsList) {					
				this.nextNomerEtap = etap.getNomer();
			}			
			
			Navigation navHolder = new Navigation();			
		    int i = navHolder.getNavPath().size();	
		   
		    NavigationDataHolder dataHoslder = (NavigationDataHolder) JSFUtils.getManagedBean("navigationSessionDataHolder");
		    Stack<NavigationData> stackPath = dataHoslder.getPageList();
		    NavigationData nd = stackPath.get(i-2);
		    Map<String, Object> mapV = nd.getViewMap();
			
		    ProcDefEdit procEdit = (ProcDefEdit) mapV.get("procDefEdit");	
			procEdit.setDefEtapsList(this.etapsList); 
			
			//02.02.2024 г. - премахва се увеличението срока на процедурата от дефинирането - това ще се смята при етапите на обработка при изпълнение на процедурата			
//			if (this.defEtap.getExtendProcSrok().equals(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_DA)) && this.extendedSrok) {
//				
//				procEdit.getProcDef().setSrokDays(procEdit.getProcDef().getSrokDays() + this.defEtap.getSrokDays()); 
//				
//				procEdit.actionSave(false);
//			
//			} else if (this.defEtap.getExtendProcSrok().equals(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_NE)) && this.extendedSrok) {
//				
//				procEdit.getProcDef().setSrokDays(procEdit.getProcDef().getSrokDays() - this.defEtap.getSrokDays()); 
//				
//				procEdit.actionSave(false);
//			}
		    
		} catch (BaseException e) {			
			LOGGER.error("Грешка при запис на дефиниция на процедура! ", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
		}
		
	}
	
	private List<ProcDefEtap> idsForChange(Integer oldNumber) {
		
		List<ProcDefEtap> etaps = new ArrayList<>();
		
		String etapsNumber = checkExistEtapInConn(oldNumber);
		
		if (etapsNumber != null && !etapsNumber.isEmpty()) {
			String[] number = etapsNumber.split(",");
			
			for (String str : number) {
				
				for (ProcDefEtap etap : this.etapsList) {
					
					if (str.equals(SearchUtils.asString(etap.getNomer()))) {
						
						String nextOK = etap.getNextOk();
						if(nextOK != null && !nextOK.isEmpty()) {
							etap.setNextOk(nextOK.replace(SearchUtils.asString(oldNumber), SearchUtils.asString(this.defEtap.getNomer()))); 
						}
						
						String nextNot = etap.getNextNot();	
						if(nextNot != null && !nextNot.isEmpty()) {
							etap.setNextNot(nextNot.replace(SearchUtils.asString(oldNumber), SearchUtils.asString(this.defEtap.getNomer()))); 
						}
						
						String nextOpt = etap.getNextOptional();
						if(nextOpt != null && !nextOpt.isEmpty()) {
							etap.setNextOptional(nextOpt.replace(SearchUtils.asString(oldNumber), SearchUtils.asString(this.defEtap.getNomer())));  
						}
						
						etaps.add(etap);
						
						break;
					}
				}
			}

		}
		 
		return etaps;
	}
	
	
	private String checkExistEtapInConn(Integer nomer) {
		
		String etapNomer = null;
		
		for (ProcDefEtap etap : this.etapsList) {
			
			if (!etap.getId().equals(this.defEtap.getId())) {					
			
				if (etap.getNextOk() != null && !etap.getNextOk().isEmpty()) {
					String[] nextOk = etap.getNextOk().split(",");
					
					for (String str : nextOk) {	
						if (str.equals(SearchUtils.asString(nomer))) {
							if (etapNomer != null && !etapNomer.contains("" + etap.getNomer() + "")) {
								etapNomer += "," +  SearchUtils.asString(etap.getNomer());
							} else if (etapNomer == null){
								etapNomer = SearchUtils.asString(etap.getNomer()); 
							}							
						}
					}
				}
				
				if (etap.getNextNot() != null && !etap.getNextNot().isEmpty()) {
					String[] nextNot = etap.getNextNot().split(",");
					
					for (String str : nextNot) {	
						if (str.equals(SearchUtils.asString(nomer))) {
							if (etapNomer != null && !etapNomer.contains("" + etap.getNomer() + "")) {
								etapNomer += "," +  SearchUtils.asString(etap.getNomer());
							} else if (etapNomer == null){
								etapNomer = SearchUtils.asString(etap.getNomer()); 
							}								
						}
					}				
				}
				
				if (etap.getNextOptional() != null && !etap.getNextOptional().isEmpty()) {
					String[] nextOpt = etap.getNextOptional().split(",");
					
					for (String str : nextOpt) {	
						if (str.equals(SearchUtils.asString(nomer))) {
							if (etapNomer != null && !etapNomer.contains("" + etap.getNomer() + "")) {
								etapNomer += "," + SearchUtils.asString(etap.getNomer());
							} else if (etapNomer == null){
								etapNomer = SearchUtils.asString(etap.getNomer()); 
							}
						}
					}
				}			
			}
		}
		 
		return etapNomer;
	}
	
	public void actionDeleteEtap() {
		
		try {	
			
			//Integer srokDays = this.defEtap.getSrokDays();
			//Integer extendedSrok = this.defEtap.getExtendProcSrok();
			String etapsNumber = checkExistEtapInConn(this.defEtap.getNomer());
			
			if (etapsNumber != null && !etapsNumber.isEmpty()) {
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "procDefEtapEdit.notDelEtap", etapsNumber));
			
			} else {
				
				JPA.getUtil().runInTransaction(() ->  this.etapDAO.delete(this.defEtap));
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, "general.successDeleteMsg"));				
				
				JPA.getUtil().runWithClose(() -> this.etapsList = this.procDAO.selectDefEtapList(this.proc.getId(), null)); 
				
				for (ProcDefEtap etap : this.etapsList) {					
					this.nextNomerEtap = etap.getNomer();
				}	
				
				actionNewEtap();
				
				Navigation navHolder = new Navigation();			
			    int i = navHolder.getNavPath().size();	
			   
			    NavigationDataHolder dataHoslder = (NavigationDataHolder) JSFUtils.getManagedBean("navigationSessionDataHolder");
			    Stack<NavigationData> stackPath = dataHoslder.getPageList();
			    NavigationData nd = stackPath.get(i-2);
			    Map<String, Object> mapV = nd.getViewMap();
				
			    ProcDefEdit procEdit = (ProcDefEdit) mapV.get("procDefEdit");	
				procEdit.setDefEtapsList(this.etapsList); 	
				
				//02.02.2024 г. - премахва се увеличението срока на процедурата от дефинирането - това ще се смята при етапите на обработка при изпълнение на процедурата			
//				if (extendedSrok.equals(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_DA))) {
//					
//					procEdit.getProcDef().setSrokDays(procEdit.getProcDef().getSrokDays() - srokDays); 
//					
//					procEdit.actionSave(false);
//				}
			}
			
		} catch (BaseException e) {
			LOGGER.error("Грешка при изтриване на етап! ", e);	
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());	
		}
	}
		
//	
//	/**
//	 * autocomplete - търсене на значение
//	 *
//	 * @param query
//	 * @return
//	 */	
//	public List<SystemClassif> actionComplete(String query) {
//		
//		List<SystemClassif> result = new ArrayList<>();
//			
//		for (ProcDefEtap etap : this.etapsList) {
//			if (!this.defEtap.getNomer().equals(etap.getNomer())) {
//				SystemClassif tmp = new SystemClassif();
//				tmp.setCode(etap.getNomer());
//				tmp.setTekst(etap.getEtapName());
//				result.add(tmp);
//			}			
//		}
//		
//		return result;
//	}	
	
//	public void actionCompleteNext(boolean addItem, SystemClassif item, int next) throws DbErrorException { 
//		
//		if (next == 1) {
//		
//			List<SystemClassif> selClassif = this.selNextOk;
//	
//			if (selClassif != null && !selClassif.isEmpty()) {
//				
//				for (int i = 0; i < selClassif.size() - 1; i++) {
//					Integer codeEtap = selClassif.get(i).getCode();
//					
//					if (codeEtap.equals(item.getCode()) && addItem) {
//						selClassif.remove(selClassif.size() - 1); // дублира се - изтрива се последния
//						break;
//					}
//				}			
//			}
//		
//		} else if (next == 2) {
//			
//			List<SystemClassif> selClassif = this.selNextNot;
//
//			if (selClassif != null && !selClassif.isEmpty()) {
//				
//				for (int i = 0; i < selClassif.size() - 1; i++) {
//					Integer codeEtap = selClassif.get(i).getCode();
//					
//					if (codeEtap.equals(item.getCode()) && addItem) {
//						selClassif.remove(selClassif.size() - 1); // дублира се - изтрива се последния
//						break;
//					}
//				}			
//			}			
//		
//		} else if (next == 3) {
//			
//			List<SystemClassif> selClassif = this.selNextOpt;
//
//			if (selClassif != null && !selClassif.isEmpty()) {
//				
//				for (int i = 0; i < selClassif.size() - 1; i++) {
//					Integer codeEtap = selClassif.get(i).getCode();
//					
//					if (codeEtap.equals(item.getCode()) && addItem) {
//						selClassif.remove(selClassif.size() - 1); // дублира се - изтрива се последния
//						break;
//					}
//				}			
//			}
//		}
//	}	
//	
//	public void onItemSelectNextOk(SelectEvent<?> event) throws DbErrorException {
//		
//		SystemClassif item = (SystemClassif) event.getObject();	
//		actionCompleteNext(true, item, 1);			
//	}
//	
//	@SuppressWarnings("rawtypes")
//	public void onItemUnselectNextOk(UnselectEvent event) throws DbErrorException {
//		
//		SystemClassif item = (SystemClassif) event.getObject();	
//		actionCompleteNext(false, item, 1);			
//	}
//	
//	public void onItemSelectNextNot(SelectEvent<?> event) throws DbErrorException {
//		
//		SystemClassif item = (SystemClassif) event.getObject();	
//		actionCompleteNext(true, item, 2);			
//	}
//	
//	@SuppressWarnings("rawtypes")
//	public void onItemUnselectNextNot(UnselectEvent event) throws DbErrorException {
//		
//		SystemClassif item = (SystemClassif) event.getObject();	
//		actionCompleteNext(false, item, 2);			
//	}
//	
//	public void onItemSelectNextOpt(SelectEvent<?> event) throws DbErrorException {
//		
//		SystemClassif item = (SystemClassif) event.getObject();	
//		actionCompleteNext(true, item, 3);			
//	}
//	
//	@SuppressWarnings("rawtypes")
//	public void onItemUnselectNextOpt(UnselectEvent event) throws DbErrorException {
//		
//		SystemClassif item = (SystemClassif) event.getObject();	
//		actionCompleteNext(false, item, 3);			
//	}	
	
	/******************************************************* END ETAP *******************************************************/	
	
	/******************************************************* GET & SET *******************************************************/	
	
	public Date getDecodeDate() {
		return new Date(decodeDate.getTime()) ;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate != null ? new Date(decodeDate.getTime()) : null;
	}

	public ProcDef getProc() {
		return proc;
	}

	public void setProc(ProcDef proc) {
		this.proc = proc;
	}

	public ProcDefEtap getDefEtap() {
		return defEtap;
	}

	public void setDefEtap(ProcDefEtap defEtap) {
		this.defEtap = defEtap;
	}	

	public List<ProcDefEtap> getEtapsList() {
		return etapsList;
	}

	public void setEtapsList(List<ProcDefEtap> etapsList) {
		this.etapsList = etapsList;
	}

	public int getNextNomerEtap() {
		return nextNomerEtap;
	}

	public void setNextNomerEtap(int nextNomerEtap) {
		this.nextNomerEtap = nextNomerEtap;
	}

//	public List<SystemClassif> getSelNextOk() {
//		return selNextOk;
//	}
//
//	public void setSelNextOk(List<SystemClassif> selNextOk) {
//		this.selNextOk = selNextOk;
//	}
//
//	public List<SystemClassif> getSelNextNot() {
//		return selNextNot;
//	}
//
//	public void setSelNextNot(List<SystemClassif> selNextNot) {
//		this.selNextNot = selNextNot;
//	}
//
//	public List<SystemClassif> getSelNextOpt() {
//		return selNextOpt;
//	}
//
//	public void setSelNextOpt(List<SystemClassif> selNextOpt) {
//		this.selNextOpt = selNextOpt;
//	}
	
	public List<SelectItem> getSelNextOk() {
		return selNextOk;
	}

	public void setSelNextOk(List<SelectItem> selNextOk) {
		this.selNextOk = selNextOk;
	}

	public List<SelectItem> getSelNextNot() {
		return selNextNot;
	}

	public void setSelNextNot(List<SelectItem> selNextNot) {
		this.selNextNot = selNextNot;
	}

	public Integer getOldNumberEtap() {
		return oldNumberEtap;
	}

	public void setOldNumberEtap(Integer oldNumberEtap) {
		this.oldNumberEtap = oldNumberEtap;
	}	
	
	/**************************************************** END GET & SET ****************************************************/	
	
	transient Comparator<SelectItem> compatator = new Comparator<SelectItem>() {
		public int compare(SelectItem s1, SelectItem s2) {
			return (s1.getLabel().toUpperCase().compareTo(s2.getLabel().toUpperCase()));
		}
	};
}