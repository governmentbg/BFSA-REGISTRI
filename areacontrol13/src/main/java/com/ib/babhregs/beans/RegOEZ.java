package com.ib.babhregs.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.components.RefCorrespData;
import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.LockObjectDAO;
import com.ib.babhregs.db.dao.OezRegDAO;
import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.ObektDeinostLica;
import com.ib.babhregs.db.dto.OezHarakt;
import com.ib.babhregs.db.dto.OezNomKadastar;
import com.ib.babhregs.db.dto.OezReg;
import com.ib.babhregs.db.dto.OezSubOez;
import com.ib.babhregs.db.dto.OezSubOezHarakt;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.navigation.Navigation;
import com.ib.indexui.system.Constants;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

import bg.egov.eforms.utils.EFormUtils;
import bg.egov.eforms.utils.EgovContainer;


/**
 * 
 * Регистри с обект на лицензиране "ОЕЗ"
 * 
 * 
 */

@Named
@ViewScoped
public class RegOEZ extends IndexUIbean {

	private static final long serialVersionUID = -1807557045298389672L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegOEZ.class);
	
	public static final String  OEZERRMSG   = "oez.errmsg";
	public  static final  String IDFORM = "oezForm";
	
	private SystemData sd;
	private UserData ud;
	private Date decodeDate;
	private transient VpisvaneDAO	daoVp;
	private transient OezRegDAO	oezDAO;
	@Inject
	private Flash flash;
	private int isView;
	private int migration;

	/**
	 * всички настройки за регистър Бабх
	 */
	private RegisterOptions registerOptions;
	
	/**
	 * настройките на конкретното заявление
	 */
	private RegisterOptionsDocsIn docOpt;
	
	/**
	 * заявление за вписване
	 */
	private Doc zaiavlVp;
	
	/**
	 * Физическo lice - Представляващо лице - code_ref_corresp от заявлението
	 * капсуловано е в бийна  PodatelBean
	 */
	//private PodatelBean podatelBean;   // Da se iztrie ROSI
	private Referent referent1;
	private RefCorrespData	bindingReferent1;
	
	/**
	 * заявителя (лицензиант) - code_ref_vpivane от tablica вписване
	 */
	private Referent referent2;
	private String txtRef2;
	
	/**
	 * вписване 
	 */
	private Vpisvane vpisvane;
	
	/** Списък на файловете към заявлението за вписване */
	private List<Files> filesList;


	/**
	 * obekt OEZ
	 */
	private OezReg oez;
	private OezHarakt harakt;
	private OezSubOez subOez;
	private OezSubOezHarakt subOezHarakt;
	
	private ObektDeinostLica oezLica;
	private Referent referentLica;
	
	//535 klasifikaciq cod - 1 i kod 13
	private List<SystemClassif> tipVrazkaLiceOezClassif;
	
	private boolean access;
	
	private Integer indexRowEdit;
	
	/** Информацията за последното заявление към вписването (ако има такова) */
	private Object[] lastDoc;
	
	
	private List<SystemClassif> logListOezJiv;
	 
	private List<SystemClassif> logListJivTehno;
	private List<SystemClassif> logListJivPred;
	 
	private List<SystemClassif> logListJivTehnoSubOez;
	private List<SystemClassif> logListJivPredSubOez;
	 
	private Integer vidOez;
	private Integer oldVidOez;
	
	private String messageChangeVidOez;
	
	/**
	 * Дължима сума
	 */
 	private BigDecimal daljimaSuma;
	
 	private boolean biosigurnost;
 	
 	private String nomKadast;
 	
	@PostConstruct
	public void init() {
		boolean lockOK = false;
		sd = (SystemData) getSystemData();
		ud = getUserData(UserData.class);			
		daoVp  = new VpisvaneDAO(getUserData());
		oezDAO = new OezRegDAO(getUserData());
		
		decodeDate = new Date();
		
		filesList = new ArrayList<>();
		
		logListOezJiv   = new ArrayList<SystemClassif>();
		logListJivPred  = new ArrayList<SystemClassif>();
		logListJivTehno = new ArrayList<SystemClassif>();
		logListJivPredSubOez  = new ArrayList<SystemClassif>();
		logListJivTehnoSubOez = new ArrayList<SystemClassif>();
		
		messageChangeVidOez = "";
		
		vidOez = null;
		
		 try {
			List<SystemClassif> tmpClassif = sd.getSysClassification(BabhConstants.CODE_CLASSIF_VRAZ_LICE_OBEKT, new Date(), getCurrentLang());
			tipVrazkaLiceOezClassif = new ArrayList<SystemClassif>();
			for(SystemClassif tmp_ : tmpClassif) {
				if(tmp_.getCode() == BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK ||
						tmp_.getCode() == BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OPERATOR) {
					tipVrazkaLiceOezClassif.add(tmp_);
				}
			}
		} catch (DbErrorException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		
		FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
		String param3 = (String) faceletContext.getAttribute("isView"); 
		isView = !SearchUtils.isEmpty(param3) ? Integer.valueOf(param3) : 0;// 0 - актуализация; 1 - разглеждане
		
		String paramM = JSFUtils.getRequestParameter("m");
		migration = SearchUtils.isEmpty(paramM) ? BabhConstants.CODE_ZNACHENIE_NE :  0; // Ако е подаден параметър - значи идва от екрана за ръчно въвеждане на миграция
		
		String paramZ = JSFUtils.getRequestParameter("idZ");
		String paramV = JSFUtils.getRequestParameter("idV");
		
		oez = new OezReg();
		oez.setVid(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ);
		oez.setDarj(BabhConstants.CODE_ZNACHENIE_BULGARIA);
		
		oez.setBiosgurnostOez(BabhConstants.CODE_ZNACHENIE_NE);
		biosigurnost = false;
		
		oezLica = new ObektDeinostLica();
		harakt = new OezHarakt();
		subOez = new OezSubOez();
		subOezHarakt = new OezSubOezHarakt();
		referentLica = new Referent();
		referentLica.setAddressKoresp(new ReferentAddress()); 
		
		
		try {
			if (!SearchUtils.isEmpty(paramV)){
				Integer idVp = Integer.valueOf(paramV);
				
				// проверка за заключено вписване.
				lockOK = true;
				if(isView == 0) {
					lockOK = checkAndLockDoc(idVp, BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
				}
				
				if (lockOK) {
					
					
					// идваме от актуализация на вписване - подадено е ид на вписване
					initVpisane(idVp );
					
					// ако заявлението е за промяна или заличаване.. винаги позволяваме корекция на данните
					String zaiavChange = JSFUtils.getRequestParameter("change"); 
					if (zaiavChange != null && !zaiavChange.isEmpty()) {
						this.vpisvane.setVpLocked(Constants.CODE_ZNACHENIE_NE);
					}
					
					// проверка за достъп до впсиване							 
					JPA.getUtil().runWithClose(() -> access = daoVp.hasVpisvaneAccess(vpisvane, isView==0, sd));
					
					if(access) {
						
						loadZaiavl(vpisvane.getIdZaqavlenie(), false);
						loadLastZaiav(true); // зарежда последното заявление
						
					}
				} else {
					access = false;
				}					
				
			} else if (!SearchUtils.isEmpty(paramZ))  {  
				
				// не е подадено ид на вписване - 
				// Ново вписване - идваме от необработено заявление
	
				Integer idZ = Integer.valueOf(paramZ);
				// проверка за закл. заявление
				lockOK = checkAndLockDoc(idZ, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
				if (lockOK) {
					loadZaiavl(idZ, true); // зареждане на заявлението и лицата - заявителите
					// проверка за достъп до заявлението
					JPA.getUtil().runWithClose(() -> access = new DocDAO(ud).hasDocAccess(zaiavlVp, isView==0, sd));
				
				} else {
					access = false;
				}
			} else {
				throw new Exception("Достъпът е отказан!");
			}
			
			LOGGER.info("RegOEZ - postConst - OK");
			
			if(access) {			
				if(isView == 1) {
		   			viewMode();
		   		}
		   		checkDaljimaSuma();
			} else {
				 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,"Достъпът е отказан!");
			}
		} catch (Exception e) {
			access = false;
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при инициализация! ", e.getMessage());
		}
		 
		 
		
	}


    /**
     * Режим - разглеждане на вписване
     */
    private void viewMode() {
	    String  cmdStr;  
		// 1. забранявам всички инпутполета
		cmdStr = "$(':input').attr('readonly','readonly')";
		PrimeFaces.current().executeScript(cmdStr);
    }
	
	/**
	 * проверка за заключенo вписване / заявление и друг обекр, ако се наложи 
	 * заключване на съответния обект  
	 * @param idLock
	 * @param codeObj
	 * @return true - OK 
	 */
	private boolean checkAndLockDoc(Integer idLock, Integer codeObj) {	
		boolean fLockOk = checkForLockDoc(idLock, codeObj);
		if (fLockOk) {	
			lockDoc(idLock, codeObj);					
		}
		return fLockOk;
	}
	
	/**
	 * Заключване на документ, като преди това отключва всички обекти, заключени от потребителя
	 * @param idObj
	 */
	public void lockDoc(Integer idObj, Integer codeObj) {	
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("lockDoc! {}", ud.getPreviousPage() );
		}
		LockObjectDAO daoL = new LockObjectDAO();		
		try { 
			JPA.getUtil().runInTransaction(() ->daoL.lock(ud.getUserId(), codeObj, idObj, null));
		} catch (BaseException e) {
			LOGGER.error("Грешка при заключване на документ! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		} 		
	}
	
	
	/**
	 * Проверка за заключен документ 
	 * @param idObj
	 * @return true - ОК (док. не е заключен)
	 */
	private boolean checkForLockDoc(Integer idObj, Integer codeObj) {
		boolean res = true;	
		try { 
			Object[] lockObj =  new LockObjectDAO().check(ud.getUserId(), codeObj, idObj);
			if (lockObj != null) {
				 res = false;
				 String msg = getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_ADMIN_STR, Integer.valueOf(lockObj[0].toString()), getUserData().getCurrentLang(), new Date())   
						       + " / " + DateUtils.printDateFull((Date)lockObj[1]);
				 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,getMessageResourceString(LABELS, "docu.docLocked"), msg);
			}
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при проверка за заключен документ! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
		return res;
	}
	
	
	/**
	 * зарежда данни за вписванем ако е подадено ид на вписване  
	 * @param idVp
	 * @throws DbErrorException 
	 */
	private void initVpisane(Integer idVp) throws DbErrorException {	
		try {
			
			JPA.getUtil().runWithClose(() -> this.vpisvane =  daoVp.findById(Integer.valueOf(idVp)));
			 oez = vpisvane.getOezReg();
			 
			 vidOez = oez.getVidOez();
			 
			 logListOezJiv = getSystemData().getClassifByListVod(BabhConstants.CODE_LIST_OEZ_JIV, oez.getVidOez(), getCurrentLang(),  oez.getDateLastMod());
			 
			 
			 
			 if( oez.getBiosgurnostOez() !=null && oez.getBiosgurnostOez() == BabhConstants.CODE_ZNACHENIE_DA) {
				biosigurnost = true;
			 } else {
				 biosigurnost = false;
			 }
			 
		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на вписване и oez към него", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
			throw new DbErrorException(e.getMessage());
		}
	}
	
	/**
	 * парсване на заявлението и зареждане на данните от него в zaiavlVp 
	 * зареждане на днните за заявител 1 и заявител 2 - в обектi adm_referent
	 * @param idZ - ид заявление
	 * @param parse - ид заявление
	 * @return
	 * @throws DbErrorException 
	 */
	private void loadZaiavl(Integer idZ, boolean parse) throws DbErrorException {
		//  извличане на данните от заявлението
	  try {
		  
		  if(parse) {
				// за ново заявление
				// ---------------- Start of Vassil Change ---------------------
				EgovContainer container = new EgovContainer();
				try {
					container = new EFormUtils().convertEformToVpisvane(idZ, sd);
				} catch (Exception e1) {
					// да заредя все пак заявлението!!!
					// въпреки, че не се парсва файлът - да може да се отоври екрана!
					JPA.getUtil().runWithClose(() -> this.zaiavlVp =   new DocDAO(ud).findById(Integer.valueOf(idZ)));
					 
					LOGGER.error("Грешка при парсване на заявление - ВЛЗ! ", e1);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
							"Грешка при парсване на заявление! Данните не могат да бъдат заредени от прикачения файл!");
				}
				if(container.doc != null) {
				   this.zaiavlVp = container.doc;
				}
				vpisvane = container.vpisvane;
				referent2 = container.ref2;
				referent1 = container.ref1;

				//----------------- End of Vassil Chaneg -----

				if(referent1 == null && zaiavlVp.getCodeRefCorresp() != null) {				
					JPA.getUtil().runWithClose(() -> referent1 = new ReferentDAO(ud).findByCodeRef(zaiavlVp.getCodeRefCorresp()));
				} 
				if(referent1 == null) {
					referent1 = new Referent();
					referent1.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
				}
				referent1.setKachestvoLice(zaiavlVp.getKachestvoLice());  
			
				if(referent2 == null && zaiavlVp.getCodeRefZaiavitel() != null) {				
					JPA.getUtil().runWithClose(() -> referent2 = new ReferentDAO(ud).findByCodeRef(zaiavlVp.getCodeRefZaiavitel()));
				} 
				if (referent2 == null) {
					referent2 = new Referent();
			 	}
				if(referent2.getAddressKoresp() == null) {
					referent2.setAddressKoresp(new ReferentAddress()); //това дали трябв да е тук ...
				}
				
				if(vpisvane == null) {
					vpisvane = new Vpisvane();
				}

				Integer registerId = loadOptions(); //  Извличане на настройките по вида на документа
				 // ако идваме от списъкът на  заявленията и ще имаме ново вписване 
				
				 // ако идваме от списъкът на  заявленията и ще имаме ново вписване 
				vpisvane.setRegistraturaId(zaiavlVp.getRegistraturaId());
				vpisvane.setIdZaqavlenie(zaiavlVp.getId());
				vpisvane.setRegNomZaqvlVpisvane(zaiavlVp.getRnDoc());
				vpisvane.setDateZaqvlVpis(zaiavlVp.getDocDate());
				vpisvane.setCodeRefVpisvane(referent2.getCode()); // от парсването на заявлението 
				vpisvane.setIdRegister(registerId); // ако е ново вписване... 
				vpisvane.setLicenziantType(BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_OEZ); 
				vpisvane.setCodePage(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ);
				vpisvane.setStatus(BabhConstants.STATUS_VP_WAITING);
			
				vpisvane.setEventDeinJiv(null); //new EventDeinJiv()
				
				// 0 - миграция, ръчно въвеждане,1 - миграция - завършена, 2 - не е миграция;
				if (migration == 0) {
					vpisvane.setFromМigr(migration); // ако е от миграция - ръчно въвеждане, иначе не се променя
				}
						
				//проверка за грешки при парсването
				if(!container.warnings.isEmpty()) {
					for(String msg: container.warnings) {
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,msg);		
					}
				}
				
			} else {
		        DocDAO daoDoc = new DocDAO(ud);
				JPA.getUtil().runWithClose(() -> this.zaiavlVp =  daoDoc.findById(Integer.valueOf(idZ)));
				 
				if (zaiavlVp != null) {
					loadFilesList(zaiavlVp.getId());
					
					referent1 = new Referent(); 
					if(zaiavlVp.getCodeRefCorresp() != null) {
	
						JPA.getUtil().runWithClose(() -> referent1 = new ReferentDAO(ud).findByCodeRef(zaiavlVp.getCodeRefCorresp()));
						referent1.setKachestvoLice(zaiavlVp.getKachestvoLice());  
						
					}
					Integer ref2 = null;
					if(vpisvane != null){
						// актуализаиция на вписване
						ref2 = vpisvane.getCodeRefVpisvane(); 
					}
					referent2 = new Referent();
					if(referent2.getAddressKoresp() == null) {
						referent2.setAddressKoresp(new ReferentAddress()); 
					}
					if(ref2 != null) {
						referent2 = new ReferentDAO(ud).findByCodeRef(ref2);
					}
					
				} 
			}
		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на заявление", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
			throw new DbErrorException(e.getMessage());
		}
	 
	}
	
	
	/**
	 *   Извличане на настройките по вида на документа
	 * @return ид на регистъра
	 */ 
	private Integer loadOptions() {	
		 Integer registerId  = null;
		 if(zaiavlVp != null) {
			 registerId = sd.getRegisterByVidDoc().get(zaiavlVp.getDocVid()); 
			 registerOptions = sd.getRoptions().get(registerId); // всички настройки за регистъра
			 List<RegisterOptionsDocsIn> lstDocsIn = registerOptions.getDocsIn(); // само входните док. за регистъра
			 docOpt = new RegisterOptionsDocsIn();
			 for(RegisterOptionsDocsIn item : lstDocsIn ) {
				if ((item.getVidDoc()).equals(zaiavlVp.getDocVid())) {
					docOpt = item;
					break;
				}
			 }
			//конкретни видове дейности за регистъра и заявлението
//			 zDein = new ArrayList<>();
//			 if(docOpt.getDocsInActivity() != null && !docOpt.getDocsInActivity().isEmpty()) {
//				 for(RegisterOptionsDocinActivity item : docOpt.getDocsInActivity() ) {
//					 zDein.add(item.getActivityId()); 
//				 }
//			 }
			// за ново вписване да сетне сумата за плащане по подразбиране и типа плащане
			 if(vpisvane == null || vpisvane.getId() == null) {
				//ако в заявлението има настройки за плащането - не ги пипаме!
				if (zaiavlVp.getPayType() == null ) {  		
					zaiavlVp.setPayType(this.docOpt.getPayCharacteristic());
				}
				if(zaiavlVp.getPayAmount() == null || zaiavlVp.getPayAmount().equals(Float.valueOf(0))) { 
					zaiavlVp.setPayAmount(this.docOpt.getPayAmount());
				}
			 }
			 
		 }
		 return registerId;
	}
	
	

	/**
	 * бутон Запис
	 */
	public void actionSave() {
		if(checkDataVp()) { 			
			try {
				
				boolean newVp = this.vpisvane.getId() == null;
			
				saveVp(newVp); 
			
				if(newVp) {// само за нов документ	
					// заключване на вписване.
					lockObj(this.vpisvane.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
					checkDaljimaSuma();
				}
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG) );		
			} catch (ObjectInUseException e) {
				LOGGER.error("Грешка при запис на документа! ObjectInUseException "); 
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			} catch (BaseException e) {			
				LOGGER.error("Грешка при запис на документа! BaseException", e);				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
			} catch (Exception e) {
				LOGGER.error("Грешка при запис на документа! ", e);					
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
			}
		}
	}
	
	/**
	 * 
	 *  @return
	 */
	public boolean checkDataVp() {
		boolean rez = true;
		
//		if(podatelBean == null) {
//			podatelBean = (PodatelBean) JSFUtils.getManagedBean("podatelBean");
//		}
//		if(podatelBean == null || podatelBean.getRef() == null ){ 
//			rez = false;
//			JSFUtils.addMessage(IDFORM+":registerTabs:egnZaqvitel",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "docu.podatel")));
//		}
		
		
		referent1 = getBindingReferent1().getRef();
		
			
		if(referent1 == null ){ 
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:egnZaqvitel",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "docu.podatel")));
		} else {
			if( SearchUtils.isEmpty(referent1.getRefName())){
				rez = false;
				JSFUtils.addMessage(IDFORM+":registerTabs:licеPоdatel:nameCorr",FacesMessage.SEVERITY_ERROR,"Въведете име на представляващото лице");	
			} 
			
			if(vpisvane.getFromМigr() == null || vpisvane.getFromМigr().intValue() == BabhConstants.CODE_ZNACHENIE_MIGR_NO) { //Проверката не важи за тези от миграция
			
				if( SearchUtils.isEmpty(referent1.getFzlEgn()) && SearchUtils.isEmpty(referent1.getFzlLnc()) && SearchUtils.isEmpty(referent1.getNflEik())){ 
					rez = false;
					JSFUtils.addMessage(IDFORM+":registerTabs:licеPоdatel:egn",FacesMessage.SEVERITY_ERROR,"Въведете ЕГН или ЛНЧ на представляващото лице!");
				}
			
			}
			if( referent1.getKachestvoLice() == null ){   
				rez = false;
				JSFUtils.addMessage(IDFORM+":registerTabs:licеPоdatel:kachestvoZaqvitel",FacesMessage.SEVERITY_ERROR,"Посочете качество на лицето!");
			}
		}
		
		
		referent2 = ((RefCorrespData)FacesContext.getCurrentInstance().getViewRoot().findComponent("oezForm").findComponent("registerTabs:liceZaqvitel")).getRef();
		if(referent2 == null){ 
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:liceZaqvitel",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "registerMainTab.zaiavitel")));
		} else {
			if( SearchUtils.isEmpty(referent2.getRefName())){ 
				rez = false;
				if(referent2.getRefType().equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL)) {
					JSFUtils.addMessage(IDFORM+":registerTabs:liceZaqvitel:nameCorr",FacesMessage.SEVERITY_ERROR,"Въведете име на заявител!");
				}else {
					JSFUtils.addMessage(IDFORM+":registerTabs:liceZaqvitel:nameCorr",FacesMessage.SEVERITY_ERROR,"Въведте наименование на заявител!");
				}
			}
			if((SearchUtils.isEmpty(referent2.getNflEik()) && SearchUtils.isEmpty(referent2.getFzlLnEs())&& SearchUtils.isEmpty(referent2.getFzlEgn()) && SearchUtils.isEmpty(referent2.getFzlLnc()))) {
				rez = false;
				if(referent2.getRefType().equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL)) {
					JSFUtils.addMessage(IDFORM+":registerTabs:liceZaqvitel:egn",FacesMessage.SEVERITY_ERROR,"Въведете ЕГН или ЛНЧ на заявител!");
				}else {
					JSFUtils.addMessage(IDFORM+":registerTabs:liceZaqvitel:eik",FacesMessage.SEVERITY_ERROR,"Въведете ЕИК/Идент. код на заявител!");
				}
			}
		}
		//check OEZ data
		

		//vid oez
		if(oez.getVidOez()==null){
			JSFUtils.addMessage(IDFORM+":registerTabs:vidOez:аutoCompl_input", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.vid")));
			rez = false;
		}
		
		//рег. номер (нов) za momenta otpada i v negowoto pole ще пишем номера по регламент
//		if(oez.getRegNom()!=null && !oez.getRegNom().trim().isEmpty())  {
//			//-------- proverka dali reg momer ne e veche vaveden za drug obekt ------------------
//			
//			try {
//				Integer regNom_ = oezDAO.findOezByRegNomStar(oez.getRegNom().trim() , oez.getId());
//				
//				if(regNom_!=null){
//					JSFUtils.addMessage(IDFORM+":registerTabs:nomOEzNew", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regOez.regNomerNew")+" "+regNom_);
//					rez = false;
//				}
//				
//			} catch (Exception e) {
//				LOGGER.error(e.getMessage(), e);
//				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, ERRDATABASEMSG));
//				rez = false;
//			} finally {
//				JPA.getUtil().closeConnection();
//			}
//		}
		
		//reg nomer star
		if(oez.getRegNomerStar()!=null && !oez.getRegNomerStar().trim().isEmpty())  {
			//-------- proverka dali stariyat reg nomer ne e veche vaveden za drug obekt ------------------
			
			try {
				Integer regNom_ = oezDAO.findOezByRegNomStar(oez.getRegNomerStar().trim() , oez.getId());
				
				if(regNom_!=null){
					JSFUtils.addMessage(IDFORM+":registerTabs:nomOEzStar", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regOez.regNomerStar")+" "+regNom_);
					rez = false;
				}
				
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, ERRDATABASEMSG));
				rez = false;
			} finally {
				JPA.getUtil().closeConnection();
			}
		}
				
		//status
		if(oez.getStatus()==null){
			JSFUtils.addMessage(IDFORM+":registerTabs:statusOez", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.status")));
			rez = false;
		} 
		
		
		//status date
		if(oez.getStatusDate()==null){
			JSFUtils.addMessage(IDFORM+":registerTabs:statusDate", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.statusDate")));
			rez = false;
		} 
//		else { //neznam dali ima takawa prowerka
//			if(oez.getStatusDate().getTime() > timestamp){ 
//				JSFUtils.addMessage(IDFORM+":registerTabs:statusDate", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "animalArea.statDateToday"));
//				rez = false;
//			}
//		}
		
		//Obl
		//obsht
		//nas mesto
		if(oez.getNasMesto()==null){
			JSFUtils.addMessage(IDFORM+":registerTabs:mestoOez:аutoCompl_input", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "registerMainTab.city")));
			rez = false;
		} 
		
		// getGpsLat = N  / getGpsLon = E    //19.1.24 komentirana e prowerkata po iskane na KK
//		if(oez.getGpsLon() == null || oez.getGpsLat() == null){
//			JSFUtils.addMessage(IDFORM+":registerTabs:gpsLat", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regOez.gpsOezMsg"));
//			rez = false;
//		} else { 
//			if(oez.getGpsLon().doubleValue()>= 22.35d && oez.getGpsLon().doubleValue() <= 28.6d){
//				if( !(oez.getGpsLat().doubleValue()>=  41.23d && oez.getGpsLat().doubleValue() <= 45.51d) ){
//					JSFUtils.addMessage(IDFORM+":registerTabs:gpsLat", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regOez.gpsNOezMsgError"));
//					rez = false;
//				} 
//			} else {
//				JSFUtils.addMessage(IDFORM+":registerTabs:gpsLon", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regOez.gpsSOezMsgError"));
//				rez = false;
//			}
//			
//		}
		
		if(oez.getGpsLon() != null && oez.getGpsLat() != null) {
			if(oez.getGpsLon().doubleValue()>= 22.35d && oez.getGpsLon().doubleValue() <= 28.6d){
				if( !(oez.getGpsLat().doubleValue()>=  41.23d && oez.getGpsLat().doubleValue() <= 45.51d) ){
					JSFUtils.addMessage(IDFORM+":registerTabs:gpsLat", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regOez.gpsNOezMsgError"));
					rez = false;
				} 
			} else {
				JSFUtils.addMessage(IDFORM+":registerTabs:gpsLon", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regOez.gpsSOezMsgError"));
				rez = false;
			}
		}
				
		// ako e vuveden e-mail da se proveri za corektnost
		if(oez.getEmail()!=null && !oez.getEmail().trim().equals("")){
			String enteredEmail = oez.getEmail();
			Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher m = p.matcher(enteredEmail);
			boolean matchFound = m.matches();
			if (!matchFound) {
				
				JSFUtils.addMessage(IDFORM+":registerTabs:emailOez", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "general.mail"));
				rez = false;
			}
		}
		
		return rez;
	}
	
	/**
	 * запис на данни  за вписване
	 * @param newVp
	 * @throws BaseException
	 */
	private void saveVp(boolean newVp) throws BaseException {
				 
	
		
//		if(podatelBean == null) {
//			podatelBean = (PodatelBean) JSFUtils.getManagedBean("podatelBean");
//		}
//		zaiavlVp.setKachestvoLice(podatelBean.getKachestvoLice());
		
		zaiavlVp.setKachestvoLice(referent1.getKachestvoLice());
		
		zaiavlVp.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN); // от класификация 	CODE_CLASSIF_DOC_STATUS(120)
		
		//referent2 = ((RefCorrespData)FacesContext.getCurrentInstance().getViewRoot().findComponent("oezForm").findComponent("registerTabs:liceZaqvitel")).getRef();
		if(biosigurnost) {
			oez.setBiosgurnostOez(BabhConstants.CODE_ZNACHENIE_DA);
		} else {
			oez.setBiosgurnostOez(BabhConstants.CODE_ZNACHENIE_NE);
		}
		
		
		vpisvane.setOezReg(oez);
		
		JPA.getUtil().runInTransaction(() -> { 
			this.vpisvane= this.daoVp.save(vpisvane, zaiavlVp, referent1, referent2, null, sd);
			
			if(vpisvane.getOezReg()!=null) { // прави се с цел да се сетнат ид тата на подтаблиците след запис на Оез то
				
				oez = vpisvane.getOezReg();
			}
		});
		
	}
	
	
	/**
	 * изтрива вписване при определени условия
	 */
	public void actionDelete() {
		
		try {
			
			JPA.getUtil().runInTransaction(() ->  {
				daoVp.deleteById(vpisvane.getId());
			}); 
			

			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,  IndexUIbean.getMessageResourceString(UI_beanMessages, "general.successDeleteMsg") ); 
		
			Navigation navHolder = new Navigation();
			navHolder.goBack();   //връща към предходната страница
			
		
		} catch (ObjectInUseException e) {
			LOGGER.error("Грешка при изтриване на вписване!", e); 
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, "general.objectInUse"), e.getMessage());
		} catch (BaseException e) {			
			LOGGER.error("Грешка при изтриване на вписване!", e);			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		} 
		
	}
	
	/**
	 * Смяна на таб
	 * @param event
	 */
    public void onTabChange(TabChangeEvent<?> event) {
	   	if(event != null) {
	   		if (LOGGER.isDebugEnabled()) {
	   			LOGGER.debug("onTabChange Active Tab: {}", event.getTab().getId());
	   		}
	
			String activeTab =  event.getTab().getId();
			if (activeTab.equals("danniVpisvaneTab") || activeTab.equals("etapiVpisvaneTab") || activeTab.equals("docVpisvaneTab")) {
				flash.put("beanName", "regOEZ"); // задъжлително се подава името на бийна, ако отиваме към таба със статусите
				LOGGER.error(vpisvane.getInfo());
			} else if(activeTab.equals("osnovniDanniTab") && Objects.equals(zaiavlVp.getPayType(), BabhConstants.CODE_ZNACHENIE_PAY_TYPE_FLOAD)) {
				checkDaljimaSuma();// ако е с плаваща тарифа и тя случйно е променена в таб документи- да се упдейтне екрана
			} 
			
	   	}
   }

	/**
	 * Заключване на обект, като преди това отключва всички обекти, заключени от потребителя
	 * @param idObj
	 */
	public void lockObj(Integer idObj, Integer codeObj) {	
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("lockDoc! {}", ud.getPreviousPage() );
		}
		LockObjectDAO daoL = new LockObjectDAO();		
		try { 
			JPA.getUtil().runInTransaction(() -> daoL.lock(ud.getUserId(), codeObj, idObj, null));
		} catch (BaseException e) {
			LOGGER.error("Грешка при заключване на вписванe! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		} 		
	}

		
	/**
	 * при излизане от страницата - отключва обекта и да го освобождава за актуализация от друг потребител
	 */
	@PreDestroy
	public void unlockDoc(){
        if (!ud.isReloadPage()) {
        	unlockAll();
        	ud.setPreviousPage(null);
        }          
        ud.setReloadPage(false);
	}
	
	
	/**
	 * отключва всички обекти на потребителя - при излизане от страницата 
	 */
	private void unlockAll( ) {
		LockObjectDAO daoL = new LockObjectDAO();		
		try { 
				JPA.getUtil().runInTransaction(() -> daoL.unlock(ud.getUserId()));
			
		} catch (BaseException e) {
			LOGGER.error("Грешка при отключване на вписване! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}
	
	/**
	 * разглеждане на заявление
	 * @return
	 */
	public String actionGotoZaiav() {
		String urlStr = "babhZaiavView.xhtml?faces-redirect=true&idObj="; 
		if(zaiavlVp != null) {
			urlStr +=  this.zaiavlVp.getId();
		}
		return urlStr;
	}
	
	/**
	 * разглеждане на заявление
	 * @return
	 */
	public String actionGotoLastZaiav() {
		return "babhZaiavView.xhtml?faces-redirect=true&idObj=" + lastDoc[0];
	}
	
	public void actionNewHarakt() {
		harakt = new OezHarakt();
		
		logListJivPred  = new ArrayList<SystemClassif>();
		logListJivTehno = new ArrayList<SystemClassif>();
		
		if(oez.getOezHarakt()==null) {
			oez.setOezHarakt(new ArrayList<OezHarakt>());
		}
	}
	
	public void actionAddHarakt() {
		
		if(checkHaract()) {
			oez.getOezHarakt().add(harakt);
			
			String dialog = "PF('modalHaract').hide();";
			PrimeFaces.current().executeScript(dialog);
		}
		
	}
	
	private boolean checkHaract() {
		boolean save = true;
		
		if(harakt.getVidJivotno()==null){
			JSFUtils.addMessage(IDFORM+":registerTabs:vidJiv:аutoCompl_input", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.vidJiv")));
			save = false;
		}
		
//		TODO няма да са задължителни до второ нареждане
//		if(harakt.getPrednaznachenie()==null){
//			JSFUtils.addMessage(IDFORM+":registerTabs:prednaznachenie", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.prednaznachenie")));
//			save = false;
//		}
//		
//		if(harakt.getTehnologia()==null){
//			JSFUtils.addMessage(IDFORM+":registerTabs:tehnologia", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.tehnologia")));
//			save = false;
//		}
		
		
		if(harakt.getKapacitet()==null && (harakt.getKapacitetText()==null|| harakt.getKapacitetText().isEmpty() )) {
			JSFUtils.addMessage(IDFORM+":registerTabs:kapacitet", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.kapacitet")));
			save = false;
		}
		
		if(harakt.getKapacitet()!=null && harakt.getKapacitetText()!=null && !harakt.getKapacitetText().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:kapacitetText", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regOez.haractKapacitet"));
			save = false;
		}
		
		//проверявам дали вече няма въведена същата характеристика TODO няма да правя проверка до второ нареждане
//		if(save) {
//			for(OezHarakt harakt_: oez.getOezHarakt()) {
//				if(harakt_.getVidJivotno().intValue() == harakt.getVidJivotno().intValue()) {
//					JSFUtils.addMessage(IDFORM+":registerTabs:vidJiv:аutoCompl_input", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages,"regOez.equalVidHarakt"));
//					save = false;
//					break;
//				}
//			}
//		}
		
		return save;
	}
	
	public void actionDeleteOezHarakt(Integer indexRow) {
		if(oez.getOezHarakt()!=null && indexRow!=null) {
			oez.getOezHarakt().remove(indexRow.intValue());
		}
	} 
	
	public void actionNewSubOez() {
		
		indexRowEdit = null;
		
		subOez = new OezSubOez();
		subOez.setSubOezHaraktList(new ArrayList<OezSubOezHarakt>());
		
		
		if(oez.getOezSubOez().isEmpty()) {
			subOez.setNomer(1);
		}else {
			Integer maxNum=0;
			for(OezSubOez item:oez.getOezSubOez()) {
				if(item.getNomer()>maxNum) {
					maxNum = item.getNomer();
				}
			}		
			subOez.setNomer(maxNum+1);
		}
		
		subOez.setStatus(BabhConstants.CODE_ZNACHENIE_STATUS_ACTIVE);
		subOez.setStatusDate(new Date());
		
		actionNewSubOezHarakt();
	}
	
	public void actionAddSubOez() {
				
		boolean ischecked = true;
	//	long timestamp = new Date().getTime();
		
		if(subOez.getNomer()==null) {
			JSFUtils.addMessage(IDFORM+":registerTabs:subOezNomer",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.subOezNomer")));
			ischecked = false;
		}
		
		if(subOez.getNaimenovanie()==null || "".equals(subOez.getNaimenovanie())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:subOezName",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.subOezName")));
			ischecked = false;
		}
		if(subOez.getStatus()==null){
			JSFUtils.addMessage(IDFORM+":registerTabs:subOezStatus",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.subOezStatus")));
			ischecked = false;
		}
		if(subOez.getStatusDate()==null){
			JSFUtils.addMessage(IDFORM+":registerTabs:subOezStatusDate",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.subOezStatusDate")));
			ischecked = false;
		} 
		
		//Незнам дали ще има такава проверка
//		else {
//			if(subOez.getStatusDate().getTime() > timestamp){
//				JSFUtils.addMessage(IDFORM+":registerTabs:subOezStatusDate", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "general.dateStatusMsgR"));
//				ischecked = false;
//			}
//		}	
		
		if(ischecked){
			if(oez.getOezSubOez()==null) {
				oez.setOezSubOez(new ArrayList<OezSubOez>());
			}
			
			if(indexRowEdit!=null) {
				oez.getOezSubOez().remove(indexRowEdit.intValue());
				oez.getOezSubOez().add(indexRowEdit.intValue() ,subOez);
			} else {
				oez.getOezSubOez().add(subOez);
			}
			
			String dialog = "PF('modalSubOez').hide();";
			PrimeFaces.current().executeScript(dialog);
		}
	}
	
	public void actionDeleteSubOez(Integer indexRow) {
		if(oez.getOezSubOez()!=null && indexRow!=null) {
			oez.getOezSubOez().remove(indexRow.intValue());
		}
	}
	
	public void actionEditSubOez(Integer indexRow) {
		indexRowEdit = indexRow;
		if(oez.getOezSubOez()!=null && indexRow!=null) {
			subOez = (oez.getOezSubOez().get(indexRow.intValue())).clone(); 
		}
	}
	
	private void actionNewSubOezHarakt() {
		subOezHarakt = new OezSubOezHarakt();
		

		logListJivPredSubOez  = new ArrayList<SystemClassif>();
		logListJivTehnoSubOez = new ArrayList<SystemClassif>();
	}
	
	public void actionAddSubOezHarakt() {
		
		if(checkHaractSubObekt()) {
		
			subOez.getSubOezHaraktList().add(subOezHarakt);
			actionNewSubOezHarakt();
		
		}
	}
	
	private boolean checkHaractSubObekt() {
		boolean save = true;
		
		if(subOezHarakt.getVidJivotno()==null){
			JSFUtils.addMessage(IDFORM+":registerTabs:vidJivSub:аutoCompl_input", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.vidJiv")));
			save = false;
		}
		
//		TODO няма да са задължителни до второ нареждане
//		if(subOezHarakt.getPrednaznachenie()==null){
//			JSFUtils.addMessage(IDFORM+":registerTabs:prednaznachenieSub", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.prednaznachenie")));
//			save = false;
//		}
//		
//		if(subOezHarakt.getTehnologia()==null){
//			JSFUtils.addMessage(IDFORM+":registerTabs:tehnologiaSub", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.tehnologia")));
//			save = false;
//		}
		
		
		if(subOezHarakt.getKapacitet()==null && (subOezHarakt.getKapacitetText()==null|| subOezHarakt.getKapacitetText().isEmpty() )) {
			JSFUtils.addMessage(IDFORM+":registerTabs:kapacitetSub", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.kapacitet")));
			save = false;
		}
		
		if(subOezHarakt.getKapacitet()!=null && subOezHarakt.getKapacitetText()!=null&& !subOezHarakt.getKapacitetText().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:kapacitetTextSub", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regOez.haractKapacitet"));
			save = false;
		}
		
		//проверявам дали вече няма въведена същата характеристика TODO do wtoro narevdane
//		if(save) {
//			for(OezSubOezHarakt subHarakt: subOez.getSubOezHaraktList()) {
//				if(subHarakt.getVidJivotno().intValue() == subOezHarakt.getVidJivotno().intValue()) {
//					JSFUtils.addMessage(IDFORM+":registerTabs:vidJivSub:аutoCompl_input", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages,"regOez.equalVidHarakt"));
//					save = false;
//					break;
//				}
//			}
//		}
		
		return save;
	}
	
	public void actionDeleteSubOezHarakt(Integer indexRow) {
		if(subOez.getSubOezHaraktList()!=null && indexRow!=null) {
			subOez.getSubOezHaraktList().remove(indexRow.intValue());
		}
	}
	
	public void actionNewLice() {
		indexRowEdit = null;
		if(oez.getObektDeinostLica()==null) {
			oez.setObektDeinostLica(new ArrayList<ObektDeinostLica>());
		}
		oezLica = new ObektDeinostLica();
		referentLica = new Referent();
		referentLica.setAddressKoresp(new ReferentAddress()); 
		oezLica.setReferent(referentLica);
		oezLica.setRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK);
		
		if(vpisvane.getDateStatus() == null) {
			oezLica.setDateBeg(new Date());	
		}else {
			oezLica.setDateBeg(vpisvane.getDateStatus());
		}
		
		referentLica.setDateBeg(oezLica.getDateBeg());
	}
	
	public void actionEditLicaOez(Integer indexRow) {
		indexRowEdit = indexRow;
		if(oez.getObektDeinostLica()!=null && indexRow!=null) {
			oezLica = (oez.getObektDeinostLica().get(indexRow.intValue())).clone(); 
			referentLica = oezLica.getReferent();
			referentLica.setDateBeg(oezLica.getDateBeg());
			referentLica.setDateEnd(oezLica.getDateEnd());
		}
	}
	
	public void actionAddLiceOez() {
		boolean flag= true;
		referentLica = ((RefCorrespData)FacesContext.getCurrentInstance().getViewRoot().findComponent("oezForm").findComponent("registerTabs:liceRefOez")).getRef();
		if(referentLica == null){ // TODO prowerka za zad. poleta - ime, egn, eik ....
			JSFUtils.addMessage(IDFORM+":registerTabs:liceRefOez",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "registerMainTab.zaiavitel")));
			flag = false;
		} else {
			oezLica.setDateBeg(referentLica.getDateBeg());
			oezLica.setDateEnd(referentLica.getDateEnd());
			
			
			 
			if(referentLica.getRefType().equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL)) {
				
				if( SearchUtils.isEmpty(referentLica.getRefName())){
					flag = false;
					JSFUtils.addMessage(IDFORM+":registerTabs:liceRefOez:nameCorr",FacesMessage.SEVERITY_ERROR,"Въведете име на лице!");	
				}
				
				if( SearchUtils.isEmpty(referentLica.getFzlEgn()) && SearchUtils.isEmpty(referentLica.getFzlLnc())){ 
					flag = false;
					JSFUtils.addMessage(IDFORM+":registerTabs:liceRefOez:egn",FacesMessage.SEVERITY_ERROR,"Въведете ЕГН или ЛНЧ на лице!");
				}
			} else {
				if( SearchUtils.isEmpty(referentLica.getNflEik())){ 
					flag = false;
					JSFUtils.addMessage(IDFORM+":registerTabs:liceRefOez:egn",FacesMessage.SEVERITY_ERROR,"Въведете ЕИК на фирмата!");
				}
				
				if( SearchUtils.isEmpty(referentLica.getRefName())){
					flag = false;
					JSFUtils.addMessage(IDFORM+":registerTabs:liceRefOez:nameCorr",FacesMessage.SEVERITY_ERROR,"Въведете наименование на фирмата!");	
				}
			}
		} 
			
		//проверка за собственик / оператор
		if(indexRowEdit== null) { //ако не е редакция
			for(ObektDeinostLica  odl: oez.getObektDeinostLica()) {
				if(odl.getDateEnd() == null && odl.getRole().intValue() == oezLica.getRole().intValue()) {
					// вече има въведена такава роля
					if(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK == oezLica.getRole().intValue()) {
						JSFUtils.addMessage(IDFORM+":registerTabs:liceRole",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages,"regOez.sobstvenik"));
					} else {
						JSFUtils.addMessage(IDFORM+":registerTabs:liceRole",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages,"regOez.operator"));
					}
					flag = false;
				}
			}
		}
		
		if(flag) {
			oezLica.setReferent(referentLica);
			
			if(indexRowEdit==null) {
				oez.getObektDeinostLica().add(oezLica);
				
			} else {
				
				oez.getObektDeinostLica().remove(indexRowEdit.intValue());
				oez.getObektDeinostLica().add(indexRowEdit.intValue(),oezLica);
			}
			
			PrimeFaces.current().executeScript("PF('modalLica').hide()");
		}
	}
	
	public void actionDeleteLiceOez(Integer indexRow) {
		if(oez.getObektDeinostLica()!=null && indexRow!=null) {
			oez.getObektDeinostLica().remove(indexRow.intValue());
		}
	}
	
	/**
	 * Извлича списъка с документи към заявлението за вписване
	 */
	private void loadFilesList(Integer docId) {

		try {
			FilesDAO daoF = new FilesDAO(getUserData());
			filesList = daoF.selectByFileObjectDop(docId, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);

		} catch (BaseException e) {
			LOGGER.error("Грешка при извличане на файловете към документ! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	/***
	 * ИЗВЛИЧА ПОСЛЕДНОТО ЗАЯВЛЕНИЕ КЪМ ДАДЕНО ВПИСВАНЕ
	 * 
	 * @param exludeFirst - ако е true игнорира заявлението за първоначално вписване
	 * */
	private void loadLastZaiav(boolean exludeFirst) {
		try {
			
			lastDoc = daoVp.findLastIncomeDoc(vpisvane.getId(), exludeFirst);

		} catch (BaseException e) {
			LOGGER.error("Грешка при извличане на последно заявление! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}
	
	/**
	* занулява полетата при смяна на държава
	*/
	public void  actionChangeCountry() {
		getOez().setNasMesto(null);		
		getOez().setObl(null);
		getOez().setObsht(null);
		getOez().setAddress(null);
		getOez().setPostCode(null);
		 
	}

	public void actionChangeOezVid() {
		
		logListJivPred = new ArrayList<SystemClassif>();
		logListJivTehno = new ArrayList<SystemClassif>();
		logListJivPredSubOez = new ArrayList<SystemClassif>();
		logListJivTehnoSubOez = new ArrayList<SystemClassif>();
		
		if(oez !=null && oez.getVidOez()!=null)
		
		 try {
			logListOezJiv = getSystemData().getClassifByListVod(BabhConstants.CODE_LIST_OEZ_JIV, oez.getVidOez(), getCurrentLang(),  oez.getDateLastMod());
		 } catch (DbErrorException e) {
			LOGGER.error("Грешка при actionChangeOezVid! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		 }
	}
	
	public void actionChangeJivVidHaract() {
		
		logListJivPred = new ArrayList<SystemClassif>();
		logListJivTehno = new ArrayList<SystemClassif>();
		
		if(harakt!=null && harakt.getVidJivotno()!=null) {
		
			try {
				logListJivPred  = getSystemData().getClassifByListVod(BabhConstants.CODE_LIST_JIV_PRED,  harakt.getVidJivotno(), getCurrentLang(),  oez.getDateLastMod());
				logListJivTehno = getSystemData().getClassifByListVod(BabhConstants.CODE_LIST_JIV_TEHNO, harakt.getVidJivotno(), getCurrentLang(),  oez.getDateLastMod());
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при ctionChangeJivVidHaract! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
			}
		}
	}
	
	public void actionChangeJivVidSubHaract() {
		
		logListJivPredSubOez = new ArrayList<SystemClassif>();
		logListJivTehnoSubOez = new ArrayList<SystemClassif>();
		
		if(subOezHarakt!=null && subOezHarakt.getVidJivotno()!=null) {
		
			try {
				logListJivPredSubOez  = getSystemData().getClassifByListVod(BabhConstants.CODE_LIST_JIV_PRED,   subOezHarakt.getVidJivotno(), getCurrentLang(),  oez.getDateLastMod());
				logListJivTehnoSubOez = getSystemData().getClassifByListVod(BabhConstants.CODE_LIST_JIV_TEHNO,  subOezHarakt.getVidJivotno(), getCurrentLang(),  oez.getDateLastMod());
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при actionChangeJivVidSubHaract! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
			}
		} 
	}
	
	
	public void actionCheckChangeVidOez() {
		messageChangeVidOez = "";
		if(vidOez!=null && oldVidOez!=null) {
			if(!oldVidOez.equals(vidOez)) {
				//ima priomqna na vida i shte pitame дали да затриваме характеристикете или да върнем старият вид на оез то
				if(oez!=null) {
					
					if(oez.getOezHarakt().size()>0) {
						//message
						messageChangeVidOez = " При промяна на вида ще бъдат изтрити вече въведените характеристики за обекта";
					} else if(oez.getOezSubOez().size()>0) {
						for(OezSubOez ss: oez.getOezSubOez()) {
							if(ss.getSubOezHaraktList().size()>0) {
								//message
								messageChangeVidOez +=", както и характеристиките въведени и за подобектите";
							}
						}
					}
					
					if(!messageChangeVidOez.isEmpty()) {
						PrimeFaces.current().executeScript("PF('modalAskChangeOez').show()");
					}
				}
			}
		}
	}
	
	//смяна на вида
	public void actionChangeYesVidOez() {
		if(oez.getOezHarakt().size()>0) {
			oez.getOezHarakt().clear();
		} else if(oez.getOezSubOez().size()>0) {
			for(OezSubOez ss: oez.getOezSubOez()) {
				if(ss.getSubOezHaraktList().size()>0) {
					ss.getSubOezHaraktList().clear();
				}
			}
		}
	}
	
	//отказ от смяна на вида
	public void actionChangeNoVidOez() {
		vidOez = Integer.valueOf(oldVidOez);
		oez.setVidOez(Integer.valueOf(oldVidOez));
	}
	
	
	
	/**
	 *  проверка за дължима такса
	 */
	private void checkDaljimaSuma() {
		daljimaSuma = null;
	
   		Object[] payStatus;
		try {
			payStatus = daoVp.findUnpaidTaxInfo(vpisvane.getId(), zaiavlVp.getId());
			if(payStatus != null) {
	   			daljimaSuma = BigDecimal.ZERO;
	   			BigDecimal s0 = BigDecimal.ZERO;
	   			BigDecimal s1 = BigDecimal.ZERO;
	   			if(payStatus[0] != null) {
	   				s0 = (BigDecimal) payStatus[0];
	   			}
	   			if(payStatus[1] != null) {
	   				s1 = (BigDecimal) payStatus[1];
	   			}
	   			daljimaSuma = s0.subtract(s1);
	   			LOGGER.debug("Има неплатени такси: "+daljimaSuma);
	   		}
		} catch (DbErrorException e) {
			LOGGER.error( "Грешка при проверка за дължими такси! ", e.getMessage());
			//JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при инициализация! ", e.getMessage());
		}  finally {
			JPA.getUtil().closeConnection();
		}
		
	}
	/************************************************/
	
	
	/********************nom kadastar ***************/
	public void actionNewNomKadastar() {
		
		if(nomKadast==null || nomKadast.trim().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:nomKadast", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOez.nomerKadastyr")));
		} else {
			if(oez.getOezNomKadastar()==null) {
				oez.setOezNomKadastar(new ArrayList<OezNomKadastar>());
			}
			boolean checkDub = false;
			for (OezNomKadastar k :oez.getOezNomKadastar()) {
				if(k.getNomer().equals(nomKadast)) {
					checkDub = true;
				}
			}
			
			if(!checkDub) {
				OezNomKadastar nk = new OezNomKadastar();
				nk.setIdOez(oez.getId());
				nk.setNomer(nomKadast);
				oez.getOezNomKadastar().add(nk);
			} else {
				JSFUtils.addMessage(IDFORM+":registerTabs:nomKadast", FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages,"regOez.nomKadastDbl"));
			}
		}
	}
	
	public void actionDeleteNomKadastarOez(Integer indexRow) {
		if(oez.getOezNomKadastar() !=null && indexRow!=null) {
			oez.getOezNomKadastar().remove(indexRow.intValue());
		}
	}
	/************************************************/
	
	public Date getDecodeDate() {
		return decodeDate;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate;
	}

	public SystemData getSd() {
		return sd;
	}

	public void setSd(SystemData sd) {
		this.sd = sd;
	}

	public RegisterOptionsDocsIn getDocOpt() {
		return docOpt;
	}

	public void setDocOpt(RegisterOptionsDocsIn docOpt) {
		this.docOpt = docOpt;
	}

	public RegisterOptions getRegisterOptions() {
		return registerOptions;
	}

	public void setRegisterOptions(RegisterOptions registerOptions) {
		this.registerOptions = registerOptions;
	}

	public Doc getZaiavlVp() {
		return zaiavlVp;
	}

	public void setZaiavlVp(Doc zaiavlVp) {
		this.zaiavlVp = zaiavlVp;
	}

	public OezReg getOez() {
		return oez;
	}

	public void setOez(OezReg oez) {
		this.oez = oez;
	}

	public UserData getUd() {
		return ud;
	}

	public void setUd(UserData ud) {
		this.ud = ud;
	}

	public VpisvaneDAO getDaoVp() {
		return daoVp;
	}

	public void setDaoVp(VpisvaneDAO daoVp) {
		this.daoVp = daoVp;
	}

	public Referent getReferent2() {
		return referent2;
	}

	public void setReferent2(Referent referent2) {
		this.referent2 = referent2;
	}

	public String getTxtRef2() {
		return txtRef2;
	}

	public void setTxtRef2(String txtRef2) {
		this.txtRef2 = txtRef2;
	}

	public boolean isAccess() {
		return access;
	}

	public void setAccess(boolean access) {
		this.access = access;
	}

	public Vpisvane getVpisvane() {
		return vpisvane;
	}

	public void setVpisvane(Vpisvane vpisvane) {
		this.vpisvane = vpisvane;
	}
	
	public OezHarakt getHarakt() {
		return harakt;
	}

	public void setHarakt(OezHarakt harakt) {
		this.harakt = harakt;
	}

	public OezSubOez getSubOez() {
		return subOez;
	}

	public void setSubOez(OezSubOez subOez) {
		this.subOez = subOez;
	}

	public OezSubOezHarakt getSubOezHarakt() {
		return subOezHarakt;
	}

	public void setSubOezHarakt(OezSubOezHarakt subOezHarakt) {
		this.subOezHarakt = subOezHarakt;
	}

	public ObektDeinostLica getOezLica() {
		return oezLica;
	}

	public void setOezLica(ObektDeinostLica oezLica) {
		this.oezLica = oezLica;
	}

	public Referent getReferentLica() {
		return referentLica;
	}

	public void setReferentLica(Referent referentLica) {
		this.referentLica = referentLica;
	}

	public List<SystemClassif> getTipVrazkaLiceOezClassif() {
		return tipVrazkaLiceOezClassif;
	}

	public void setTipVrazkaLiceOezClassif(List<SystemClassif> tipVrazkaLiceOezClassif) {
		this.tipVrazkaLiceOezClassif = tipVrazkaLiceOezClassif;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public Integer getIndexRowEdit() {
		return indexRowEdit;
	}

	public void setIndexRowEdit(Integer indexRowEdit) {
		this.indexRowEdit = indexRowEdit;
	}

	public Object[] getLastDoc() {
		return lastDoc;
	}

	public void setLastDoc(Object[] lastDoc) {
		this.lastDoc = lastDoc;
	}



	public List<SystemClassif> getLogListOezJiv() {
		return logListOezJiv;
	}



	public void setLogListOezJiv(List<SystemClassif> logListOezJiv) {
		this.logListOezJiv = logListOezJiv;
	}



	public List<SystemClassif> getLogListJivTehno() {
		return logListJivTehno;
	}



	public void setLogListJivTehno(List<SystemClassif> logListJivTehno) {
		this.logListJivTehno = logListJivTehno;
	}



	public List<SystemClassif> getLogListJivPred() {
		return logListJivPred;
	}

	public void setLogListJivPred(List<SystemClassif> logListJivPred) {
		this.logListJivPred = logListJivPred;
	}

	public List<SystemClassif> getLogListJivTehnoSubOez() {
		return logListJivTehnoSubOez;
	}

	public void setLogListJivTehnoSubOez(List<SystemClassif> logListJivTehnoSubOez) {
		this.logListJivTehnoSubOez = logListJivTehnoSubOez;
	}

	public List<SystemClassif> getLogListJivPredSubOez() {
		return logListJivPredSubOez;
	}

	public void setLogListJivPredSubOez(List<SystemClassif> logListJivPredSubOez) {
		this.logListJivPredSubOez = logListJivPredSubOez;
	}

	public Integer getVidOez() {
		return vidOez;
	}

	public void setVidOez(Integer vidOez) {
		
		if(this.vidOez != null && vidOez!=null) {  //това го правя за да сравнявам дали е сменен вида на оез-то
			if(!vidOez.equals(this.vidOez)) {
				oldVidOez = Integer.valueOf(this.vidOez);
			}
		}
		
		this.vidOez = vidOez;
		oez.setVidOez(this.vidOez);
		
		actionChangeOezVid();
		
	}

	public Integer getOldVidOez() {
		return oldVidOez;
	}

	public void setOldVidOez(Integer oldVidOez) {
		this.oldVidOez = oldVidOez;
	}

	public String getMessageChangeVidOez() {
		return messageChangeVidOez;
	}

	public void setMessageChangeVidOez(String messageChangeVidOez) {
		this.messageChangeVidOez = messageChangeVidOez;
	}

	public BigDecimal getDaljimaSuma() {
		return daljimaSuma;
	}

	public void setDaljimaSuma(BigDecimal daljimaSuma) {
		this.daljimaSuma = daljimaSuma;
	}
	
	public int getIsView() {
		return isView;
	}

	public void setIsView(int isView) {
		this.isView = isView;
	}


	public Referent getReferent1() {
		return referent1;
	}


	public void setReferent1(Referent referent1) {
		this.referent1 = referent1;
	}


	public RefCorrespData getBindingReferent1() {
		return bindingReferent1;
	}


	public void setBindingReferent1(RefCorrespData bindingReferent1) {
		this.bindingReferent1 = bindingReferent1;
	}


	public boolean isBiosigurnost() {
		return biosigurnost;
	}


	public void setBiosigurnost(boolean biosigurnost) {
		this.biosigurnost = biosigurnost;
	}


	public String getNomKadast() {
		return nomKadast;
	}


	public void setNomKadast(String nomKadast) {
		this.nomKadast = nomKadast;
	}


	public int getMigration() {
		return migration;
	}


	public void setMigration(int migration) {
		this.migration = migration;
	}

}
