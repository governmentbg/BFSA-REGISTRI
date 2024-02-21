package com.ib.babhregs.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com.ib.babhregs.db.dao.MpsDAO;
import com.ib.babhregs.db.dao.ObektDeinostDAO;
import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.EventDeinJiv;
import com.ib.babhregs.db.dto.EventDeinJivIdentif;
import com.ib.babhregs.db.dto.EventDeinJivLice;
import com.ib.babhregs.db.dto.EventDeinJivPredmet;
import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.db.dto.MpsDeinost;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.ObektDeinostLica;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.ReferentDoc;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocinActivity;
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
import com.ib.system.utils.ValidationUtils;

import bg.egov.eforms.utils.EFormUtils;
import bg.egov.eforms.utils.EgovContainer;


/**
 * 
 * Регистри с обект на лицензиране "Лице - дейност животни"
 * 
 * 
 */

@Named
@ViewScoped
public class RegLicaDeinZJ extends IndexUIbean {

	private static final long serialVersionUID = -1807557045298389672L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegLicaDeinZJ.class);
	
	public static final String  OEZERRMSG   = "oez.errmsg";
	public  static final  String IDFORM = "dZJForm";
	
	private SystemData sd;
	private UserData ud;
	private Date decodeDate;
	private transient VpisvaneDAO	daoVp;
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
	/** Списък на файловете към заявлението за вписване */
	private List<Files> filesList;
	/** Информацията за последното заявление към вписването (ако има такова) */
	private Object[] lastDoc;


	/**
	 * допустими дейности в зависимост от регистъра
	 */
	private  List<Integer> zDein;
	
	
	/**
	 * Физическo lice - Представляващо лице - code_ref_corresp от заявлението
	 */
	private Referent referent1;
	private RefCorrespData	bindingReferent1;	
	
	/**
	 * заявителя (лицензиант) - code_ref_vpivane от tablica вписване
	 */
	private Referent referent2;
	private RefCorrespData	bindingReferent2;	

	
	/**
	 * вписване 
	 */
	private Vpisvane vpisvane;
	private boolean access;



	/**
	 *  предмет на дейността
	 */
	private Map<Integer, Integer> predmetDeinJivM;
	
	
	/** предмет на дейност*/
	private List<Integer> predmetDeinJiv;
	
	/** предмет на дейност за СЖП*/
	private  List<EventDeinJivPredmet>  predmetDeinSjp;
	
	/**
	 * obekt OEZ
	 */
	private ObektDeinost oezOne;
	private String oezOneOperator;
	private Integer oezId;
//	private String oezNomSrch;
	
	/**
	 * Местонахождението на oezOne като разкодиран текст
	 */
	private String oezOneAdres; 
	
	
	/** За дейност "Търговия и производство с идентификатори" */
	private Integer indexTabl;      // Индекс на избран идентификатор от таблицата
	private EventDeinJivIdentif identifikator = new EventDeinJivIdentif();
	
	/**
	 * Общ брой животни - за дейност "изплащане на обезщ. за унищожени животни"; "Опити с животни"; "мероприятия с животни"
	 */
	private Integer sumaJiv;  
	private Integer roliaLice;
	private Referent liceEkip;
	private String txtLiceEkip;
	private RefCorrespData bindingLiceEkip;
	private Integer liceCtrl; // контролиращ - избор от адм. структура
	
	/**
	 * за дейност превоз на живи животни
	 */
	private String refDocNom1;   	//Лиценз за международен превоз на товари - номер
	private Date refDocDate1;   	//Лиценз за международен превоз на товари - дата
	private String refDocNom2;   	//Лиценз за вътрешен превоз на товари - номер
	private Date refDocDate2;   	//Лиценз за вътрешен превоз на товари - дата
	
	private Mps mps = new Mps();
	private List<SystemClassif> darjClassifList; // заради autocomplete
	private List<SystemClassif> gkppClassifList; // заради autocomplete
	private List<SystemClassif> jivMClassifList;// заради autocomplete
	
	/**
	 * Дължима сума
	 */
 	private BigDecimal daljimaSuma;
	
 	/**
	 * списък с подзаявления  - за услуга 3364 
	 */
	private List<SystemClassif> podZaiavList;
	private Integer sferaObucenie;
	
	
 	
	@PostConstruct
	public void init() {
		boolean lockOK = false;
		sd = (SystemData) getSystemData();
		ud = getUserData(UserData.class);			
		daoVp = new VpisvaneDAO(getUserData());
		this.filesList = new ArrayList<>();
 
		FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
		String param3 = (String) faceletContext.getAttribute("isView"); 
		isView = !SearchUtils.isEmpty(param3) ? Integer.valueOf(param3) : 0;// 0 - актуализация; 1 - разглеждане
		
		String paramM = JSFUtils.getRequestParameter("m");
		migration = SearchUtils.isEmpty(paramM) ? BabhConstants.CODE_ZNACHENIE_NE :  0; // Ако е подаден параметър - значи идва от екрана за ръчно въвеждане на миграция
		
		String paramZ = JSFUtils.getRequestParameter("idZ");
		String paramV = JSFUtils.getRequestParameter("idV");
		setPredmetDeinJivM(new HashMap<Integer, Integer>()); 
		try {
			if (!SearchUtils.isEmpty(paramV)){
				Integer idVp = Integer.valueOf(paramV);		

				lockOK = true;
				if(isView == 0) {	// проверка за заключено вписване - само при актуализация
					lockOK = checkAndLockDoc(idVp, BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
				}
				if (lockOK) {
					// идваме от актуализация на вписване - подадено е ид на вписване
					try {
						
						JPA.getUtil().runWithClose(() -> this.vpisvane =  daoVp.findById(Integer.valueOf(idVp)));
						 
						if (vpisvane != null) {
							
							// ако заявлението е за промяна или заличаване.. винаги позволяваме корекция на данните
							String zaiavChange = JSFUtils.getRequestParameter("change"); 
							if (zaiavChange != null && !zaiavChange.isEmpty()) {
								this.vpisvane.setVpLocked(Constants.CODE_ZNACHENIE_NE);
							}
									
							// проверка за достъп до впсиване							 
							JPA.getUtil().runWithClose(() -> access = daoVp.hasVpisvaneAccess(vpisvane, isView==0, sd));
							
							if(access) {
								
								loadZaiavl(vpisvane.getIdZaqavlenie(), false);
								loadOptions(); //  Извличане на настройките по вида на документа
								
								// основен обект  за дейност
								initOsnDeinost();
								
								loadLastZaiav(true); // зарежда последното заявление
							} 
						}
						
					} catch (Exception e) {
						LOGGER.error("Грешка при зареждане на вписване и обектите към него", e);
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
								getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
						throw new DbErrorException(e.getMessage());
					}
					
				} else {
					access = false;
				}

			} else if (!SearchUtils.isEmpty(paramZ))  {
				Integer idZ = Integer.valueOf(paramZ);
				// не е подадено ид на вписване - 
				// Ново вписване - идваме от необработено заявление	
				// проверка за закл. заявление
				lockOK = checkAndLockDoc(idZ, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
				if (lockOK) {				
					// Vassil Change - са в loadZaiavl
					loadZaiavl(idZ, true); // зареждане на заявлението и лицата - заявителите; парсване за ново завление
					// проверка за достъп до заявлението
					JPA.getUtil().runWithClose(() -> access = new DocDAO(ud).hasDocAccess(zaiavlVp, isView==0, sd));
				} else {
					access = false;
				}
			
			} else {
				throw new Exception("Достъпът е отказан!");
			}
		
			if(access) {			
				loadFilesList(zaiavlVp.getId());
				
				this.decodeDate = new Date();
				
		   		if(isView == 1) {
		   			viewMode();
		   		}
		   		checkDaljimaSuma();
			} else {
				 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,"Достъпът е отказан!");
				 if(lockOK) {
					 unlockDoc();
				 }
			}
			
			
		} catch (Exception e) {
			access = false;
			LOGGER.error( e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при инициализация! ", e.getMessage());
		}
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
		} finally {
			JPA.getUtil().closeConnection();
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
			JPA.getUtil().runInTransaction(() -> 
				daoL.lock(ud.getUserId(), codeObj, idObj, null)
			);
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
		}
		return res;
	}

	

	/***
	 * ИЗВЛИЧА ПОСЛЕДНОТО ЗАЯВЛЕНИЕ КЪМ ДАДЕНО ВПИСВАНЕ
	 * 
	 * @param exludeFirst - ако е true игнорира заявлението за първоначално вписване
	 * */
	private void loadLastZaiav(boolean exludeFirst) {
		try {
			
			JPA.getUtil().runWithClose(() -> lastDoc =  daoVp.findLastIncomeDoc(vpisvane.getId(), exludeFirst));
			
		} catch (BaseException e) {
			LOGGER.error("Грешка при извличане на последно заявление! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}
	
	/**
	 * основен обект  за дейност
	 * @throws DbErrorException
	 */
	private void initOsnDeinost() throws DbErrorException {
		try {
			
			EventDeinJiv dejnJivObj = vpisvane.getEventDeinJiv();
			if(dejnJivObj != null ) {
			
				if(dejnJivObj.getVidList() == null) {
					dejnJivObj.setVidList(new ArrayList<>());
				}
				
				// за всяка дейност - ако има нещо специфично							
				if( zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_JIV) || 
					zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD)  ||
					zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_SJP) ) {
					initVpPredmetInt(dejnJivObj);
					initVpOezOne(dejnJivObj);
				} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_MEROPRIATIE_JIV)  ){
					initVpPredmetInt(dejnJivObj);
					initVpLicaJiv(); // вет. лекар и лице за контрол на заповедта - въвежда се от БАБХ
					setJivMClassifList(loadScList(predmetDeinJiv, BabhConstants.CODE_CLASSIF_VID_JIV_MEROPRIATIE)); // живoтни autocomplete
				} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBEZSHЕТЕНИЕ_JIV) ||
						  zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OPITI_JIV) ){
					initVpOezOne(dejnJivObj);  
					initVpSumaJiv();
				} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PREVOZ_JIV)  ){
					initVpPredmetInt(dejnJivObj);
					initVpPrevozJiv();
					setJivMClassifList(loadScList(predmetDeinJiv, BabhConstants.CODE_CLASSIF_VID_JIVOTNO)); // живoтни autocomplete
				} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_IZNOS_JIV)  ){
					initVpIznosJiv();
				} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_SAHRANENIE_EMBRIONI)) {
					initVpLicaJiv(); // лице за контрол на заповедта - въвежда се от БАБХ
				} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE) || 
						  zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_UCHEBNA_PROGR)) {
					//услуга 3364 В настройките за общото заявление - да са включени всички дейности за подзаявленията!!!
					if(dejnJivObj.getObuchenieList() != null && !dejnJivObj.getObuchenieList().isEmpty()) {
						sferaObucenie = dejnJivObj.getObuchenieList().get(0);
					}
					podZaiavList = new ArrayList<>();
					podZaiavList = this.sd.getChildrenOnNextLevel(BabhConstants.CODE_CLASSIF_DOC_VID, zaiavlVp.getDocVid(), decodeDate, getCurrentLang());
				}
			}
		} catch (Exception e) {
			throw new DbErrorException(e.getMessage());
		}
	}

	/**
	 * зарежда държави и ГКПП - заради autocomplete!! за "износ на животни"
	 * @param tmpCodes
	 * @param codeClassif
	 * @return
	 */
	private List<SystemClassif> loadScList(List<Integer> tmpCodes , int codeClassif){
		List<SystemClassif> tmpLst = new ArrayList<>();				 
		if(tmpCodes != null) {
			for( Integer item : tmpCodes) {
				String tekst = "";
				SystemClassif scItem = new SystemClassif();
				try {
					scItem.setCodeClassif(codeClassif);
					scItem.setCode(item);
					tekst = getSystemData().decodeItem(codeClassif, item, getUserData().getCurrentLang(), new Date());		
					scItem.setTekst(tekst);
					tmpLst.add(scItem);
				} catch (DbErrorException e) {
					LOGGER.error("Грешка при зареждане на държави на документ! ", e);
				}		
			}				
		}
		return tmpLst; 
	}
	
	
	/**
	 * Зарежда ОЕЗ, ако е едно
	 */
	private void initVpOezOne(EventDeinJiv dejnJivObj ) throws DbErrorException{
			
		if(dejnJivObj.getObektDeinostDeinost()!= null && !dejnJivObj.getObektDeinostDeinost().isEmpty()) {
			ObektDeinostDeinost dJivOez = dejnJivObj.getObektDeinostDeinost().get(0);	 //връзка дейност животни - оез
			oezOne = dJivOez.getObektDeinost();						
		}
		
		if(oezOne == null) {
			oezOne = new ObektDeinost(); 
		}
	}
	
	/**
	 * 	ОБЩ брой животни
	 */
	private void initVpSumaJiv() throws DbErrorException{	
		if(vpisvane.getEventDeinJiv().getEventDeinJivPredmet() != null) {
			sumaJiv = 0; 
			for (EventDeinJivPredmet item :vpisvane.getEventDeinJiv().getEventDeinJivPredmet()) {
				sumaJiv += item.getBroi();
			}
		}
	}
	
	/**
	 * специфични полета за дейност "Превоз на животни" 
	 */
	private void initVpPrevozJiv() {
		// да зареди данните за Лицензиите за превози
		if(referent2 != null ) { //&& referent2.getId() != null
			if(referent2.getReferentDocs() != null && !referent2.getReferentDocs().isEmpty()) {
				for(ReferentDoc doc: referent2.getReferentDocs() ) {
					if(Objects.equals(doc.getVidDoc(),BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_MPREVOZ )) {
						refDocNom1 = doc.getNomDoc();
						refDocDate1 = doc.getDateIssued();
					} else if (Objects.equals(doc.getVidDoc(),BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_VPREVOZ )) {
						refDocNom2 = doc.getNomDoc();
						refDocDate2 = doc.getDateIssued();
					}
				}
			}
		}
	}

	
	/**
	 * специфични полета за дейност "мероприятия с животни"  - връзка с лица
	 * @throws BaseException 
	 */
	private void initVpLicaJiv() throws BaseException  {
		if (vpisvane.getEventDeinJiv().getEventDeinJivLice() != null && !vpisvane.getEventDeinJiv().getEventDeinJivLice().isEmpty()) {
			List<EventDeinJivLice> tmpL = vpisvane.getEventDeinJiv().getEventDeinJivLice();
			for(EventDeinJivLice item: tmpL) {
				if(Objects.equals(item.getTipVraz(),BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_HUMANNO_OTN)) {
					Integer liceId = item.getCodeRef();
					JPA.getUtil().runWithClose(() -> liceEkip = new ReferentDAO(ud).findByCodeRef(liceId));					
				} else if(Objects.equals(item.getTipVraz(),BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_CTRL_LICE)) {
					liceCtrl = item.getCodeRef();
				}
			}
		}
	}
	
	/**
	 *  специфични полета за дейност "износ на живи животни и зародишни продукти" 
	 */
	private void initVpIznosJiv() {
		if (vpisvane.getEventDeinJiv().getDarjList() == null) {
			vpisvane.getEventDeinJiv().setDarjList(new ArrayList<>());
		}
		setDarjClassifList(loadScList(vpisvane.getEventDeinJiv().getDarjList(), BabhConstants.CODE_CLASSIF_DARJAVI_TRETI_STRANI)); /// държави autocomplete
		if (vpisvane.getEventDeinJiv().getGkppList() == null) {
			vpisvane.getEventDeinJiv().setGkppList(new ArrayList<>());
		}
		setGkppClassifList(loadScList(vpisvane.getEventDeinJiv().getGkppList(), BabhConstants.CODE_CLASSIF_GKPP)); // гкпп autocomplete
		oezOne = new ObektDeinost(); 
	}
	
	
	/**
	 *  ако има нещо в основната таблица за предмет на дейността и НЕ се изпозлва директно обекта, а само id-та или други особености за СЖП...
	 * @param dejnJivObj
	 * @throws DbErrorException
	 */
	private void initVpPredmetInt(EventDeinJiv dejnJivObj ) throws DbErrorException{
		if(dejnJivObj.getEventDeinJivPredmet() != null && !dejnJivObj.getEventDeinJivPredmet().isEmpty()) {
			 predmetDeinJiv = new ArrayList<>();
			 predmetDeinSjp = new ArrayList<>();
			 for(EventDeinJivPredmet item : dejnJivObj.getEventDeinJivPredmet() ) {				 
				 if(Objects.equals(item.getCodeClassif(),BabhConstants.CODE_CLASSIF_FURAJI_SJP_PP) ) {
					 predmetDeinSjp.add(item);
				 } else {
					 predmetDeinJiv.add(item.getPredmet());	 
				 }
				 predmetDeinJivM.put(item.getPredmet(), item.getId()); // при записа после да знам дали има промяна
			 }
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
					 
					LOGGER.error("Грешка при парсване на заявление! ", e1);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
							"Грешка при парсване на заявление! Данните не могат да бъдат заредени от прикачения файл!");
				}
				
				if(container.doc != null) {
				   this.zaiavlVp = container.doc;
				} 
				vpisvane  = container.vpisvane;
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
				
				
				//TODO!!!!!
			
				vpisvane.setRegistraturaId(zaiavlVp.getRegistraturaId());
				vpisvane.setIdZaqavlenie(zaiavlVp.getId());
				vpisvane.setRegNomZaqvlVpisvane(zaiavlVp.getRnDoc());
				vpisvane.setDateZaqvlVpis(zaiavlVp.getDocDate());
				vpisvane.setCodeRefVpisvane(referent2.getCode()); // от парсването на заявлението 
				vpisvane.setIdRegister(registerId); // ако е ново вписване... 
				vpisvane.setLicenziantType(BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_LICE); 
				vpisvane.setCodePage(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ);
				vpisvane.setStatus(BabhConstants.STATUS_VP_WAITING);
				
				// това не би трябвало да се случва
				if(vpisvane.getEventDeinJiv() == null) {
					vpisvane.setEventDeinJiv(new EventDeinJiv());
				}
			//	vpisvane.getEventDeinJiv().setEventDeinJivPredmet(new ArrayList<>());
			//	vpisvane.getEventDeinJiv().setVidList(new ArrayList<>()); // вид на дейността
				
				// 0 - миграция, ръчно въвеждане,1 - миграция - завършена, 2 - не е миграция;
				if (migration == 0) {
					vpisvane.setFromМigr(migration); // ако е от миграция - ръчно въвеждане, иначе не се променя
				}
						
				
				// основен обект  за дейност
				initOsnDeinost();
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
					referent1 = new Referent(); 
					if(zaiavlVp.getCodeRefCorresp() != null) {
						JPA.getUtil().runWithClose(() -> referent1 = new ReferentDAO(ud).findByCodeRef(zaiavlVp.getCodeRefCorresp()));
						referent1.setKachestvoLice(zaiavlVp.getKachestvoLice());  
					} 
				
					referent2 = new Referent();
					referent2.setAddressKoresp(new ReferentAddress()); //това дали трябв да е тук ...
					
					if(vpisvane != null && vpisvane.getCodeRefVpisvane() != null) {
						JPA.getUtil().runWithClose(() -> referent2 = new ReferentDAO(ud).findByCodeRef(vpisvane.getCodeRefVpisvane()));
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

	/**
	 *   Извличане на настройките по вида на документа
	 * @return ид на регистъра
	 */ 
	private Integer loadOptions() {	
		 Integer registerId  = null;
		 if(zaiavlVp != null) {
			 Integer docVid = zaiavlVp.getDocVid();
			 //ако има подзаявление зареждаме настройките по него, ако няма си ги зареждаме по стария начин
			 if (zaiavlVp.getDocPodVid() != null) {
				 docVid = zaiavlVp.getDocPodVid();
			 }
			 registerId = sd.getRegisterByVidDoc().get(docVid); 
			 registerOptions = sd.getRoptions().get(registerId); // всички настройки за регистъра
			 List<RegisterOptionsDocsIn> lstDocsIn = registerOptions.getDocsIn(); // само входните док. за регистъра
			 docOpt = new RegisterOptionsDocsIn();
			 for(RegisterOptionsDocsIn item : lstDocsIn ) {
				if ((item.getVidDoc()).equals(docVid)) {
					docOpt = item;
					break;
				}
			 }
			//конкретни видове дейности за регистъра и заявлението
			 zDein = new ArrayList<>();
			 if(docOpt.getDocsInActivity() != null && !docOpt.getDocsInActivity().isEmpty()) {
				 for(RegisterOptionsDocinActivity item : docOpt.getDocsInActivity() ) {
					 zDein.add(item.getActivityId()); 
				 }
			 }
			 // за ново вписване да сетне сумата за плащане по подразбиране и типа плащане, ако вече не са записани!!!
			 if(vpisvane == null || vpisvane.getId() == null) {
				//ако в заявлението има настройки за плащането - не ги пипаме!
				if (zaiavlVp.getPayType() == null ) {  		
					zaiavlVp.setPayType(this.docOpt.getPayCharacteristic());
				}
				if(zaiavlVp.getPayAmount() == null || zaiavlVp.getPayAmount().equals(Float.valueOf(0))) { 
					zaiavlVp.setPayAmount(this.docOpt.getPayAmount());
				}
	//			if(Objects.equals(docOpt.getPayCharacteristic(), BabhConstants.CODE_ZNACHENIE_PAY_TYPE_NOPAY)){
	//				 zaiavlVp.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
	//			}else {
	//				 zaiavlVp.setIndPlateno(BabhConstants.CODE_ZNACHENIE_NE);
	//			}
			 }
		 }
		 return registerId;
	}
	
	/**
	 * за услуга 3364 - избрано е конкретен вид подзаявление
	 * да оставя само неговата дейност!
	 */
	public void actionSelectPodVidZaiav() {
		sferaObucenie = null;
		zDein = new ArrayList<>();
		if(getSd().isActionVisible(zaiavlVp.getDocPodVid(), BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_UCHEBNA_PROGR)) {
			zDein.add(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_UCHEBNA_PROGR));
		} else {
			zDein.add(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE));
		}
	}
	
	public void clearOezOne() {
		oezOne = new ObektDeinost();
		oezOneAdres = null;
		oezOneOperator = null;
	}
	
	/**
	 * извиква се на onchange на полето рег. номер на оез - търси се с OR за следните колони: reg_nomer_reglament, reg_nomer_star, reg_nom.</br>
	 * зарежда връзките дейности животни - оез 
	 *  
	 */
	public void actionFindOezByRegNom(){	
		if(!SearchUtils.isEmpty(oezOne.getRegNom())) {
			try {
				
//				JPA.getUtil().runWithClose(() -> {
//					oezOne = new ObektDeinostDAO(getUserData()).findByRegNomer(regNomOez, BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ, false); // рег. номер.....
//				});
				String regNomOez = oezOne.getRegNom().trim(); //номер на ОЕЗ (един от трите възможни!)
				JPA.getUtil().runWithClose(() -> {
					List<ObektDeinost> oezOneLst = new ObektDeinostDAO(getUserData()).findByIdent(regNomOez, BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ, false);
						if(oezOneLst != null && !oezOneLst.isEmpty()) {
							oezOne = oezOneLst.get(0); // взема 1-то намерено значние.... не зная какво се случва, ако намери повече от едно
						} else {
							oezOne = null;
						}
				});
				
				loadOezData();
			} catch (BaseException e) {
		 		LOGGER.error("Грешка при зареждане на OEZ ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		 	}
		 }else {
			 clearOezOne(); 
		 }
	}

	
	/**
	 * зарежда OЕЗ по id
	 */
	public void actionFindOezById(){	
		if(oezId != null) {
			try {
				JPA.getUtil().runWithClose(() -> {
					ObektDeinostDAO daoOez = new ObektDeinostDAO(getUserData());
					oezOne =  daoOez.findById(oezId);
				});
				loadOezData();
			} catch (BaseException e) {
		 		LOGGER.error("Грешка при зареждане на OEZ ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		 	}
		 }else {
			 clearOezOne(); 
		 }
	}
	

	/**
	 * допълва всички данни зa ОЕЗ, които са необходими
	 */
	private void loadOezData() {
		if( vpisvane.getEventDeinJiv().getObektDeinostDeinost() == null ){
			 vpisvane.getEventDeinJiv().setObektDeinostDeinost(new ArrayList<>());
		} 
		ObektDeinostDeinost dJivOez = new ObektDeinostDeinost(); 	 //връзка дейност животни - оез
		if(oezOne != null) {
			 dJivOez.setObektDeinost(oezOne);	
			 oezOneAdres = null;
			 oezOneOperator = null;
			 if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_IZNOS_JIV)){
				// проверка за дублиране
				boolean bb = true;
				for(ObektDeinostDeinost oez:  vpisvane.getEventDeinJiv().getObektDeinostDeinost()) {
					if(Objects.equals(oez.getObektDeinost().getId(),oezOne.getId())) {
						msgScrollTop("ОЕЗ вече е добавено!");
						bb = false;
						break;
					}
				}
				if(bb) {
					vpisvane.getEventDeinJiv().getObektDeinostDeinost().add(dJivOez);
				}
				clearOezOne(); // за износа са много ОЕЗ-та
			 } else {
				 vpisvane.getEventDeinJiv().getObektDeinostDeinost().clear();
				 vpisvane.getEventDeinJiv().getObektDeinostDeinost().add(dJivOez);
			 }
		} else {
			clearOezOne();
			msgScrollTop(getMessageResourceString(beanMessages, OEZERRMSG));
		}
	}
	
	/**
	 * разкодира адреса на oezOne като текст по ekkate
	 */
	public String loadOezAdres(ObektDeinostDeinost oezd) {
		return loadOezOneAdres(oezd.getObektDeinost());
	}
	
	/**
	 * разкодира адреса на oezOne като текст по ekkate
	 */
	public String loadOezOneAdres(ObektDeinost oez) {
		String adres = null;
		String nm = null;
		StringBuilder adresText = new StringBuilder("");
		if(oez != null && oez.getNasMesto() != null) {
			try {
				nm = sd.decodeItemDopInfo(BabhConstants.CODE_CLASSIF_EKATTE, oez.getNasMesto(), getCurrentLang(),new Date());
				if(nm != null) {
					adresText.append(nm);
				}
				if(oez.getAddress() != null) {
					adresText.append("  "+ oez.getAddress());
				}
				adres = adresText.toString();
				
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при извличане на местонахождение на ОЕЗ ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
			}			
		}else {
			adres = null; 
		}
		return adres;
	}
	

	/**
	 * разкодира лицето "Оператор" на oezOne като текст 
	 */
	public void loadOezOneOperator() {

		if(oezOne != null) {
			try {
				List<ObektDeinostLica> tmpLica = oezOne.getObektDeinostLica(); 
				if(oezOne.getObektDeinostLica()!=null && !oezOne.getObektDeinostLica().isEmpty()) {
					oezOneOperator = "";
					for(ObektDeinostLica item : tmpLica ) {
						if(item.getCodeRef() != null) {
							oezOneOperator +=  sd.decodeItem(BabhConstants.CODE_CLASSIF_REFERENTS, item.getCodeRef(), getCurrentLang(), decodeDate)+" ("+ sd.decodeItem(BabhConstants.CODE_CLASSIF_VRAZ_LICE_OBEKT, item.getRole(), getCurrentLang(), decodeDate) +")  ";
						}
					}
				}
				
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при извличане на връзки лица - ОЕЗ ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
			}
			
		}else {
			this.oezOneOperator= null; 
		}
		
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
						
				predmetDeinJivM.clear();
				if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_JIV) ||
				   zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD) ||
				   zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_SJP)) {
					if (vpisvane.getEventDeinJiv().getEventDeinJivPredmet() != null ) {
						for(EventDeinJivPredmet tmpJivPr : vpisvane.getEventDeinJiv().getEventDeinJivPredmet() ) {
							predmetDeinJivM.put(tmpJivPr.getPredmet(), tmpJivPr.getId()); // ако записа е успешен, да ги сложа в predmetDeinTargJivM, така не се променят id при update
						}
					}	
				}
				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG) );		
			} catch (ObjectInUseException e) {
				LOGGER.error("Грешка при запис на вписването! ObjectInUseException "); 
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			} catch (BaseException e) {			
				LOGGER.error("Грешка при запис на вписването! BaseException", e);				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
			} catch (Exception e) {
				LOGGER.error("Грешка при запис на вписването! ", e);					
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
			}
		}
	}
	

	/**
	 * 
	 * @return
	 */
	public boolean checkDataVp() {
		boolean rez = true;

		referent1 = getBindingReferent1().getRef();
		if( SearchUtils.isEmpty(referent1.getRefName())){
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:licеPоdatel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "podatel.msgName"));	
		} 
		// 0 - миграция, ръчно въвеждане,1 - миграция - завършена, 2 - не е миграция;
		if(vpisvane.getFromМigr() == null || Objects.equals(vpisvane.getFromМigr(),BabhConstants.CODE_ZNACHENIE_MIGR_NO)) {
			rez = checkIdentifFZL(rez, referent1.getFzlEgn(), referent1.getFzlLnc(), IDFORM+":registerTabs:licеPоdatel:egn", getMessageResourceString(beanMessages, "podatel.msgValidIdentFZL"), false);
		}
		
		if( referent1.getKachestvoLice() == null ){ 
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:licеPоdatel:kachestvoZaqvitel",FacesMessage.SEVERITY_ERROR,"Посочете качество на лицето!");
		}	
		
		
		referent2 = getBindingReferent2().getRef();		
		if( SearchUtils.isEmpty(referent2.getRefName())){ 
			rez = false;
			if(referent2.getRefType().equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL)) {
				JSFUtils.addMessage(IDFORM+":registerTabs:liceZaqvitel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "zaiavitel.msgName"));
			}else {
				JSFUtils.addMessage(IDFORM+":registerTabs:liceZaqvitel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "zaiavitel.msgNameNFZL"));
			}
		}
		if(vpisvane.getFromМigr() == null || Objects.equals(vpisvane.getFromМigr(),BabhConstants.CODE_ZNACHENIE_MIGR_NO)) {
			if(referent2.getRefType().equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL)) {
				boolean bdate =  zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PRAVOSPOSOBN_JIV);
				rez = checkIdentifFZL(rez, referent2.getFzlEgn(), referent2.getFzlLnc(), IDFORM+":registerTabs:liceZaqvitel:egn", 	getMessageResourceString(beanMessages, "zaiavitel.msgValidIdentFZL"), bdate);
			}else {
				rez = checkIdentifNFZL(rez, referent2.getNflEik(), referent2.getFzlLnEs(), IDFORM+":registerTabs:liceZaqvitel:eik", getMessageResourceString(beanMessages, "zaiavitel.msgValidIdentNFZL"));
			}
		}
		
		if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_JIV) || zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD) || zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_SJP)  ) {
			rez = checkDataTargJiv(rez);
		} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_MEROPRIATIE_JIV)) {
			rez = checkDataMeropJiv(rez);
		} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PROIZV_IDENT) || zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_IDENT)){
			rez = checkDataIdentJiv(rez);
		} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBEZSHЕТЕНИЕ_JIV)){
			rez = checkDataObezJiv(rez);
		} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OPITI_JIV)){
			rez = checkDataOpitiJiv(rez);
		} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PREVOZ_JIV)){
			rez = checkDataPrevozJiv(rez);
		} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_SAHRANENIE_EMBRIONI)){
			rez = checkDataDobivJiv(rez);
		}  else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_IZNOS_JIV)  ){
			rez = checkDataIznosJiv(rez);
		} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PRAVOSPOSOBN_JIV) && vpisvane.getEventDeinJiv().getLiceKachestvo() == null){
		    rez = false;
		    JSFUtils.addMessage(IDFORM+":registerTabs:rolia",FacesMessage.SEVERITY_ERROR,"Посочете oтношението на лицето към превоза!");
		}else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_UCHEBNA_PROGR) ||
				 zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE)){
			if(zaiavlVp.getDocPodVid() == null) {
				rez = false;
			    JSFUtils.addMessage(IDFORM+":registerTabs:vidPodzaiav",FacesMessage.SEVERITY_ERROR,"Изберете вид дейност!");
			} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_UCHEBNA_PROGR) ){
				rez = checkDataObucenie(rez);
			} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE)){
				rez = checkDataObucenie2(rez);
			}
		}
		
		return rez;
	}
	
	/**
	 * проверка за валидни ЕГН/ЛНЧ  
	 * @param egn
	 * @param lnch
	 * @param errLabel
	 * @param errMsg
	 * @return
	 */
	private boolean checkIdentifFZL(boolean rez, String egn, String lnch, String errLabel,  String  errMsg, boolean bdate) {
		boolean b1 = SearchUtils.isEmpty(egn);
		boolean b2 = SearchUtils.isEmpty(lnch);
		if(  bdate ){
			if(referent2.getFzlBirthDate() == null) {
				rez = false;
				JSFUtils.addMessage( IDFORM+":registerTabs:liceZaqvitel:bDate",FacesMessage.SEVERITY_ERROR, "Въведете дата на раждане на заявителя!");
			}
			if(SearchUtils.isEmpty(referent2.getFzlBirthPlace())) {
				rez = false;
				JSFUtils.addMessage( IDFORM+":registerTabs:liceZaqvitel:bPlace",FacesMessage.SEVERITY_ERROR, "Въведете място на раждане на заявителя!");
			}
		} else if( b1 && b2 && !bdate){ 
			rez = false;
			JSFUtils.addMessage(errLabel,FacesMessage.SEVERITY_ERROR,errMsg);
		} else if (!b1  && !ValidationUtils.isValidEGN(egn.trim())) {			   
			JSFUtils.addMessage(errLabel,FacesMessage.SEVERITY_ERROR,errMsg);
			rez = false;
		} else if(!b2   && !ValidationUtils.isValidLNCH(lnch.trim())) {
			JSFUtils.addMessage(errLabel,FacesMessage.SEVERITY_ERROR,errMsg);
			rez = false;
		}
		
		return rez;
	}
	
	/**
	 * проверка за валиден ЕИК - Юридическо лице  
	 * @param egn
	 * @param lnch
	 * @param errLabel
	 * @param errMsg
	 * @return
	 */
	private boolean checkIdentifNFZL(boolean rez, String eik, String fzlLnEs, String errLabel,  String  errMsg) {
		boolean b1 = SearchUtils.isEmpty(eik);
		boolean b2 = SearchUtils.isEmpty(fzlLnEs);
		if( b1 && b2 ){ 
			rez = false;
			JSFUtils.addMessage(errLabel,FacesMessage.SEVERITY_ERROR,errMsg);
		} else if (!b1  && !ValidationUtils.isValidBULSTAT(eik.trim())) {			   
			JSFUtils.addMessage(errLabel,FacesMessage.SEVERITY_ERROR,errMsg);
			rez = false;
		} 
		return rez;
	}
	
	
	/**
	 * проверка на данните за дейност Търговия с животни, зар. продукти и пратки СЖП
	 * @param rez
	 * @return
	 */
	public boolean checkDataTargJiv(boolean rez) {
		if( vpisvane.getEventDeinJiv().getVidList() == null ||  vpisvane.getEventDeinJiv().getVidList() .isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:prTrJ",FacesMessage.SEVERITY_ERROR,"Изберете предмет на дейността!");
			return false;
		} 
		if( vpisvane.getEventDeinJiv().getVidList().contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD) &&
				(predmetDeinJiv == null || predmetDeinJiv.isEmpty()) ) {
			// трябва да има поне един въведен предмет на дейността за зародишните продукти
			// за търговия със животни - не с е записва нищо в таблицте predmet
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:prTrJ",FacesMessage.SEVERITY_ERROR,"Изберете конкретни значения за предмет на дейността за търговия със зародишни продукти!");
		}
		if(vpisvane.getEventDeinJiv().getVidList().contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_SJP) &&
				(predmetDeinSjp == null || predmetDeinSjp.isEmpty())) {
			//  Пратки СЖП
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:prTrJ",FacesMessage.SEVERITY_ERROR,"Изберете конкретни значения за видове СЖП!");
		}
		if(!vpisvane.getEventDeinJiv().getVidList().contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD)  ) {
			// не включва търговия със зародишните продукти
			predmetDeinJiv = new ArrayList<>();
		}
		if(!vpisvane.getEventDeinJiv().getVidList().contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_SJP) ) {
			// не включва търговия със СЖП
			predmetDeinSjp = new ArrayList<>();
			vpisvane.getEventDeinJiv().setCategoria(null);
		}
		if(vpisvane.getEventDeinJiv().getVidList().contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD) ||
		   vpisvane.getEventDeinJiv().getVidList().contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_JIV) ) {
			// трябва да има въведен обект - регистрирано ОЕЗ 
			if(oezOne == null || SearchUtils.isEmpty(oezOne.getRegNom()) ){
				rez = false;
				JSFUtils.addMessage(IDFORM+":registerTabs:nomOEz",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "reg.regNomOez")));
			}
		} else {
			clearOezOne();
			vpisvane.getEventDeinJiv().setObektDeinostDeinost(null);
		}
		
		return rez;	
	}
	

	/**
	 * проверка на данните за дейност мероприятия с животни
	 * @param rez
	 * @return
	 */
	public boolean checkDataMeropJiv(boolean rez) {
		//вид мероприятие
		if (vpisvane.getEventDeinJiv().getMeroptiatie() == null ) {
			JSFUtils.addMessage(IDFORM+":registerTabs:eventType",FacesMessage.SEVERITY_ERROR, "Не е въведен вид на мероприятието!");
			rez = false;
		}
		//адрес на мероприятие
		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getAdres())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:address",FacesMessage.SEVERITY_ERROR, "Не е въведен адрес на мероприятието!");
			rez = false;
		}

		//Начална дата на мероприятие
		boolean d1 = vpisvane.getEventDeinJiv().getDateBegMeropriatie()  == null;
		if (d1) {
			JSFUtils.addMessage(IDFORM+":registerTabs:eventBegDate_",FacesMessage.SEVERITY_ERROR, "Не е въведена начална дата на мероприятието!");
			rez = false;
		}
		//Крайна дата на мероприятие
		boolean d2 = vpisvane.getEventDeinJiv().getDateEndMeropriatie() == null ; 
		if (d2) {
			JSFUtils.addMessage(IDFORM+":registerTabs:eventEndDate_",FacesMessage.SEVERITY_ERROR, "Не е въведена крайна дата на мероприятието!");
			rez = false;
		}
		//крайната дата трябва да е по-голяма от началната дата на мероприятие
		if ( !d1 && !d2 && vpisvane.getEventDeinJiv().getDateEndMeropriatie().compareTo(vpisvane.getEventDeinJiv().getDateBegMeropriatie())<0 ) {
			JSFUtils.addMessage(IDFORM+":registerTabs:eventEndDate_",FacesMessage.SEVERITY_ERROR, "Крайната дата трябва да е по-голяма от началната дата на мероприятието!");
			rez = false;
		}
		
		if(predmetDeinJiv == null || predmetDeinJiv.size() == 0) {
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:typeAnimalM:autoComplM",FacesMessage.SEVERITY_ERROR,"Изберете видове животни за мероприятие!");
		}
	
		return rez;	
	}	
	
	/**
	 * проверка на данните за дейност производство и търговия с идентификатори
	 * @param rez
	 * @return
	 */
	public boolean checkDataIdentJiv(boolean rez) {
		if( vpisvane.getEventDeinJiv().getVidList()  == null ||  vpisvane.getEventDeinJiv().getVidList().isEmpty()) {
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:vidDein3",FacesMessage.SEVERITY_ERROR,"Изберете предмет на дейността!");
		}
//		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getOpisanieCyr())) {
//			JSFUtils.addMessage(IDFORM+":registerTabs:opisCyr3",FacesMessage.SEVERITY_ERROR,"Въведете описание за дейността!");
//			rez = false;
//		}
		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getCel())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:celMostri",FacesMessage.SEVERITY_ERROR,"Въведете номер и дата на придружителното писмо, с което са изпратени мострите!");
			rez = false;
		}
		if (vpisvane.getEventDeinJiv().getEventDeinJivIdentif() == null || vpisvane.getEventDeinJiv().getEventDeinJivIdentif().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:tblIdentifList",  FacesMessage.SEVERITY_ERROR, "Въведете идентификатори!");
			rez = false;
		}
		  
		return rez;	
	}	
	
	/**
	 * проверка на данните за дейност производство и търговия с идентификатори
	 * @param rez
	 * @return
	 */
	public boolean checkDataObezJiv(boolean rez) {
		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getOpisanieCyr())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:opisCyr4",FacesMessage.SEVERITY_ERROR,"Въведете описание за дейността!");
			rez = false;
		}
		if (vpisvane.getEventDeinJiv().getEventDeinJivPredmet() == null || vpisvane.getEventDeinJiv().getEventDeinJivPredmet().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:tblVidJivList",FacesMessage.SEVERITY_ERROR, "Въведете категория за обезщетение!");
			rez = false;
		}
		return rez;	
	}
	
	/**
	 * проверка на данните за дейност опити с животни
	 * @param rez
	 * @return
	 */
	public boolean checkDataOpitiJiv(boolean rez) {
		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getCel())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:cel",FacesMessage.SEVERITY_ERROR,"Въведете цел на опитната дейност!");
			rez = false;
		}
//		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getOpisanieCyr())) {
//			JSFUtils.addMessage(IDFORM+":registerTabs:opisCyr5",FacesMessage.SEVERITY_ERROR,"Въведете описание за дейността!");
//			rez = false;
//		}
		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getNachinMqstoPridobiv())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:nachin",FacesMessage.SEVERITY_ERROR,"Въведете начин и място на придобиване на животните!");
			rez = false;
		}
		if (vpisvane.getEventDeinJiv().getEventDeinJivPredmet() == null || vpisvane.getEventDeinJiv().getEventDeinJivPredmet().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:tblVidJivList",FacesMessage.SEVERITY_ERROR, "Въведете брой и видове животни за опити!");
			rez = false;
		}
		if (vpisvane.getEventDeinJiv().getEventDeinJivLice() == null || vpisvane.getEventDeinJiv().getEventDeinJivLice().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:tblLicaList1",FacesMessage.SEVERITY_ERROR, "Въведете екип!");
			rez = false;
		}
		if(oezOne == null || SearchUtils.isEmpty(oezOne.getRegNom()) ){
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:nomOEz",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "reg.regNomOez")));
		}
		if(referent2.getRefType().equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL) &&
				SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getAdres())) {
			rez = false; 
			JSFUtils.addMessage(IDFORM+":registerTabs:mestorabota",FacesMessage.SEVERITY_ERROR, "Въведете месторабота на лицето заявител!");
		}
		return rez;	
	}
	
	/**
	 * проверка на данните за дейност превоз на животни
	 * @param rez
	 * @return
	 */
	public boolean checkDataPrevozJiv(boolean rez) {
		if (vpisvane.getEventDeinJiv().getPatuvane() == null) {
			JSFUtils.addMessage(IDFORM+":registerTabs:typeTravel",FacesMessage.SEVERITY_ERROR,"Въведете вид на пътуването!");
			rez = false;
		}
		
		if (vpisvane.getEventDeinJiv().getNachinTransp() == null) {
			JSFUtils.addMessage(IDFORM+":registerTabs:nachinTransport",FacesMessage.SEVERITY_ERROR,"Начин на транспортиране!");
			rez = false;
		}
//		if ((SearchUtils.isEmpty(refDocNom1) || refDocDate1 == null) && (SearchUtils.isEmpty(refDocNom2) || refDocDate2 == null)) {
//			JSFUtils.addMessage(IDFORM+":registerTabs:lizPr",FacesMessage.SEVERITY_ERROR,"Въведете данни за лиценз за извършване на международни и/или вътрешни превози!");
//			rez = false;
//		}
		
		if(predmetDeinJiv == null || predmetDeinJiv.size() == 0) {
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:typeAnimalMp:autoComplM",FacesMessage.SEVERITY_ERROR,"Изберете видове животни за превоз!");
		}
		
		if (vpisvane.getEventDeinJiv().getMpsDeinost() == null || vpisvane.getEventDeinJiv().getMpsDeinost().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:tblMpsList",FacesMessage.SEVERITY_ERROR, "Въведете Транспортните средства, с които ще се превозват животните!");
			rez = false;
		}
		
		
		return rez;	
	}
	
	/**
	 * проверка на данните за дейност одобрение на екипи за добив и съхранение на ембриони и яйцеклетки
	 * @param rez
	 * @return
	 */
	public boolean checkDataDobivJiv(boolean rez) {
	
		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getOpisanieCyr())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:opisCyr6",FacesMessage.SEVERITY_ERROR,"Въведете описание на оборудването!");
			rez = false;
		}
		
		if (vpisvane.getEventDeinJiv().getEventDeinJivLice() == null || vpisvane.getEventDeinJiv().getEventDeinJivLice().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:tblLicaList2",FacesMessage.SEVERITY_ERROR, "Въведете екип!");
			rez = false;
		}
		return rez;	
	}
	
	/**
	 * проверка на данните за дейност износ на живи животни и зародишни продукти
	 * @param rez
	 * @return
	 */
	public boolean checkDataIznosJiv(boolean rez) {
	
		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getIznos())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:iznos",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "deinZj.iznosNa")));
			rez = false;
		}
		
		if (vpisvane.getEventDeinJiv().getDarjList() == null || vpisvane.getEventDeinJiv().getDarjList().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:drIznos:autoComplM",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS, getMessageResourceString(LABELS, "deinZj.iznosDarjavi")));
			rez = false;
		}
		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getNachinMqstoPridobiv())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:zagotovka",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "deinZj.zagotovkaUsl")));
			rez = false;
		}
		
		if (vpisvane.getEventDeinJiv().getGkppList() == null || vpisvane.getEventDeinJiv().getGkppList().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:gkpIznos:autoComplM",FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,MSGPLSINS, getMessageResourceString(LABELS, "deinZj.gkpp")));
			rez = false;
		}
		return rez;	
	}
	
	/**
	 * проверка на данните за дейност "Издаване на становище за учебна програма" 
	 * @param rez
	 * @return
	 */
	public boolean checkDataObucenie(boolean rez) {
		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getOpisanieCyr())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:opisCyr9",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOb.opisanieDeinUchPr")));
			rez = false;
		}
		if(sferaObucenie == null) {
			JSFUtils.addMessage(IDFORM+":registerTabs:sfOb",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regZJ.spheraObuch")));
			rez = false;
		}
		return rez;	
	}
	
	/**
	 * проверка на данните за дейност "Издаване на документ за преминато обучение" 
	 * @param rez
	 * @return
	 */
	public boolean checkDataObucenie2(boolean rez) {
		if (SearchUtils.isEmpty(vpisvane.getEventDeinJiv().getOpisanieCyr())) {
			JSFUtils.addMessage(IDFORM+":registerTabs:opisCyr91",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regOb.opisanieDeinIObTema")));
			rez = false;
		}	
		if(sferaObucenie == null) {
			JSFUtils.addMessage(IDFORM+":registerTabs:sfOb1",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "regZJ.spheraObuch")));
			rez = false;
		}
		//Начална дата на обучение
		boolean d1 = vpisvane.getEventDeinJiv().getDateBegMeropriatie()  == null;
		if (d1) {
			JSFUtils.addMessage(IDFORM+":registerTabs:eventBegDate2",FacesMessage.SEVERITY_ERROR, "Не е въведена начална дата на обучението!");
			rez = false;
		}
		//Крайна дата на обучение
		boolean d2 = vpisvane.getEventDeinJiv().getDateEndMeropriatie() == null;
		if (d2) {
			JSFUtils.addMessage(IDFORM+":registerTabs:eventEndDate2",FacesMessage.SEVERITY_ERROR, "Не е въведена крайна дата на обучението!");
			rez = false;
		}
		//крайната дата трябва да е по-голяма от началната дата на обучение
		if (!d1 && !d2 && vpisvane.getEventDeinJiv().getDateEndMeropriatie().compareTo(vpisvane.getEventDeinJiv().getDateBegMeropriatie())<0 ) {
			JSFUtils.addMessage(IDFORM+":registerTabs:eventEndDate2",FacesMessage.SEVERITY_ERROR, "Крайната дата трябва да е по-голяма от началната дата на обучението!");
			rez = false;
		}
		if (vpisvane.getEventDeinJiv().getEventDeinJivLice() == null || vpisvane.getEventDeinJiv().getEventDeinJivLice().isEmpty()) {
			JSFUtils.addMessage(IDFORM+":registerTabs:tblLicaList11",FacesMessage.SEVERITY_ERROR, "Въведете лица, преминалите обучение в сферата на животновъдството!");
			rez = false;
		}
		
		return rez;	
	}
	
	/**
	 * запис на данни  за вписване
	 * @param newVp
	 * @throws BaseException
	 */
	private void saveVp(boolean newVp) throws BaseException {		
		if(!(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_JIV) || zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD) ||  
		     zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_IDENT) || zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PROIZV_IDENT))	) {
			// за "търговия с животни и зародишни продукти" и "производтво и търговия с идентификатори"
			// дейностите са повече от една и се попълват от екрана
			// за останалите - тук зареждаме вида дейност когато тя е само една и не се избира от екрна			
			vpisvane.getEventDeinJiv().setVidList(new ArrayList<>()); // ако по някаква причина не е била добавена дейност - сега ще я добави
			vpisvane.getEventDeinJiv().getVidList().add(zDein.get(0));
			
			if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_UCHEBNA_PROGR)||
				  zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE)){  //  услуга 3364 обединява две заявления 
				vpisvane.getEventDeinJiv().setObuchenieList(new ArrayList<>());
				vpisvane.getEventDeinJiv().getObuchenieList().add(sferaObucenie);
			} 
		}
	
		
		if( zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_JIV) || 
			zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD) || 
			zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_SJP) ) {
			saveVpJivPr();
		} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_MEROPRIATIE_JIV)  ){
			saveVpJivPr();
			//връзка с лице - следящ за спазването на изискванията за хуманно отношение към животните
			liceEkip = bindingLiceEkip.getRef();
			saveVpLice(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_HUMANNO_OTN, liceEkip.getCode(), liceEkip);
			//връзка с лице - контролиращ изпълнението на заповедта
			saveVpLice(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_CTRL_LICE, liceCtrl, null);
		} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PREVOZ_JIV)  ){
			saveVpJivPr();
			saveVpPrevoz3();
		} else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_SAHRANENIE_EMBRIONI)) {
			//връзка с лице - контролиращ изпълнението на заповедта
			saveVpLice(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_CTRL_LICE, liceCtrl, null);
		}
		
		zaiavlVp.setKachestvoLice(referent1.getKachestvoLice()); 
		
		zaiavlVp.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN); // от класификация 	CODE_CLASSIF_DOC_STATUS(120)
		
	// TODO  да добaвим параметър дали обекта дейност (в случая ОЕЗ) да се записва, т.е. дали подлежи на актуализация
		JPA.getUtil().runInTransaction(() -> { 
			this.vpisvane= this.daoVp.save(vpisvane, zaiavlVp, referent1, referent2, null, sd);  
		});
		
	}
	
	/**
	 * спец. обработки 1.
	 * 1.предмета на дейността за  търговия с животни и зар. продукти, пратки СЖП;  
	 * 2. мероприятия с животни
	 * 3. превоз на животни
	 */
	private void saveVpJivPr() {
		 List<EventDeinJivPredmet> eventDeinJivPredmet = new ArrayList<>();
		 if (predmetDeinJiv != null && !predmetDeinJiv.isEmpty() ) {
			EventDeinJivPredmet tmpJivPr;
			for(Integer item : predmetDeinJiv ) {
					tmpJivPr = new EventDeinJivPredmet();
					tmpJivPr.setId(predmetDeinJivM.get(item)); // ако е старо и има id - да не се променя
					tmpJivPr.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_JIVOTNO);
					tmpJivPr.setPredmet(item);
					eventDeinJivPredmet.add(tmpJivPr);
				}
			}
		 	if (predmetDeinSjp != null && !predmetDeinSjp.isEmpty() ) {
		 		for(EventDeinJivPredmet item : predmetDeinSjp) {
		 			eventDeinJivPredmet.add(item);
		 		}
		 	}
			vpisvane.getEventDeinJiv().setEventDeinJivPredmet(eventDeinJivPredmet); 
	 }

	
	
	/**
	 * за дейност мероприятия с животни
	 * връзка с лице - следящ за спазването на изискванията за хуманно отношение към животните
	 * връзка с лице - контролиращ изпълнението на заповедта
	 * @param rolia
	 * @param codeLice
	 * @param ref
	 */
	private void saveVpLice(Integer rolia, Integer codeLice, Referent ref) {
		if (vpisvane.getEventDeinJiv().getEventDeinJivLice() == null || vpisvane.getEventDeinJiv().getEventDeinJivLice().isEmpty() ) {
			vpisvane.getEventDeinJiv().setEventDeinJivLice(new ArrayList<>());
		}
		List<EventDeinJivLice> tmpL = vpisvane.getEventDeinJiv().getEventDeinJivLice();
		boolean bb = true;
		for(EventDeinJivLice item: tmpL) {
			if(Objects.equals(item.getTipVraz(),rolia)) {
				item.setCodeRef(codeLice);
				item.setReferent(ref);
				bb = false;
				break;
			} 
		}
		if(bb) {
			EventDeinJivLice lice = new EventDeinJivLice ();
			lice.setCodeRef(codeLice);
			lice.setTipVraz(rolia);
			vpisvane.getEventDeinJiv().getEventDeinJivLice().add(lice);
		}
	}
	
// CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_CTRL_LICE

	/**
	 * спец. обработки 3.
	 * предмета на дейността за  "превоз на живи животни" 
	 */
	private void saveVpPrevoz3() {
		if(referent2.getReferentDocs() == null) {
			referent2.setReferentDocs(new ArrayList<>());
		}
		boolean add1 = true;
		boolean add2 = true;
		boolean refdoc1 = (SearchUtils.isEmpty(refDocNom1) || refDocDate1 == null);
		boolean refdoc2 = (SearchUtils.isEmpty(refDocNom2) || refDocDate2 == null);
		ReferentDoc tmpRefDoc = null; 
		if(!referent2.getReferentDocs().isEmpty()) {
			for(ReferentDoc doc: referent2.getReferentDocs() ) {
				if(Objects.equals(doc.getVidDoc(),BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_MPREVOZ )) {
					if(refdoc1) {
						tmpRefDoc = doc;
					}else {
						doc.setNomDoc(refDocNom1);
					   	doc.setDateIssued(refDocDate1);
					}
				   	add1 = false;
				} else if(Objects.equals(doc.getVidDoc(),BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_VPREVOZ )) {
					if(refdoc2) {
						tmpRefDoc = doc;
					}else {
						doc.setNomDoc(refDocNom2);
						doc.setDateIssued(refDocDate2);
					}
					add2 = false;
				}
			}
			if(tmpRefDoc != null) {
				referent2.getReferentDocs().remove(tmpRefDoc);
			}
		}
		
		if(add1 && !refdoc1) {
			tmpRefDoc = new ReferentDoc(); 
			tmpRefDoc.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_MPREVOZ );
			tmpRefDoc.setNomDoc(refDocNom1);
			tmpRefDoc.setDateIssued(refDocDate1);
			referent2.getReferentDocs().add(tmpRefDoc);
		}
		if(add2 && !refdoc2) {
			tmpRefDoc = new ReferentDoc(); 
			tmpRefDoc.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_VPREVOZ );
			tmpRefDoc.setNomDoc(refDocNom2);
			tmpRefDoc.setDateIssued(refDocDate2);
			referent2.getReferentDocs().add(tmpRefDoc);
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
				flash.put("beanName", "regLicaDeinZJ"); // задължително се подава името на бийна, ако отиваме към таба със статусите
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
			JPA.getUtil().runInTransaction(() -> 
				daoL.lock(ud.getUserId(), codeObj, idObj, null)
			);
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
				JPA.getUtil().runInTransaction(() -> 
					daoL.unlock(ud.getUserId()));
			
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
	
	public String actionGotoLastZaiav() {
		return "babhZaiavView.xhtml?faces-redirect=true&idObj=" + this.lastDoc[0];
	}	
	
	/**
	 * изтрива вписване при определени условия
	 */
	public void actionDelete() {
		
		try {
			
			JPA.getUtil().runInTransaction(() ->  {
				this.daoVp.deleteById(vpisvane.getId());
			}); 
		
			Navigation navHolder = new Navigation();
			navHolder.goBack();   //връща към предходната страница
			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,  IndexUIbean.getMessageResourceString(UI_beanMessages, "general.successDeleteMsg") ); 
		
		} catch (ObjectInUseException e) {
			LOGGER.error("Грешка при изтриване на вписване!", e); 
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, "general.objectInUse"), e.getMessage());
		} catch (BaseException e) {			
			LOGGER.error("Грешка при изтриване на вписване!", e);			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		} 
		
	}
	
	
	
	/*** Методи за дейност търговия с идентификатори*/

	
	
	/**
	 * бутон Нов идентификатор
	 */
	public void actionInpNewIdent () {
		indexTabl = null;
		identifikator = new EventDeinJivIdentif();
	}

	
	/**
	 * Корекция на ред от списъка с идентификатори - отваря модалния
	 * @param pr
	 * @param idx
	 */
	public void actionEditIdent( EventDeinJivIdentif pr, Integer idx) {		
		 indexTabl = idx;
		 identifikator = new EventDeinJivIdentif();
		 identifikator.setVidIdentif(pr.getVidIdentif());// Вид идентификатор
		 identifikator.setVidJiv(pr.getVidJiv());// Вид животни
		 identifikator.setModel(pr.getModel());  //Модел за идентификатор
		 identifikator.setKod(pr.getKod());// kod
		 PrimeFaces.current().executeScript("PF('dlgIdentif').show();");
    }
	
	 /**
	  * Добавяне на идентификатор в таблицата при връщане от модалния
	  */
	public void actionAddIdentifikator () {
		 if (!checkModalIdent()) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Не са въведени всички данни за идентификатор!");
			return;
		 }
		 if (vpisvane.getEventDeinJiv().getEventDeinJivIdentif() == null ) {
			 vpisvane.getEventDeinJiv().setEventDeinJivIdentif(new ArrayList<EventDeinJivIdentif>());
		 }
		 // БАБХ - не искат прoверка за дублирнае
//		 else if (!vpisvane.getEventDeinJiv().getEventDeinJivIdentif().isEmpty()) {  // Проверка дали вече идентификаторът не е бил въведен 	
//			int i = 0;
//			for (EventDeinJivIdentif item : vpisvane.getEventDeinJiv().getEventDeinJivIdentif()) {
//				if (!Objects.equals(i, indexTabl)) {
//					if (Objects.equals(identifikator.getVidIdentif(),item.getVidIdentif()) && Objects.equals(identifikator.getVidJiv(),item.getVidJiv())) {
//						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Идентификатор от този вид вече е добавен за посоченото животно!");
//						return;
//					}
//				}
//				i++;
//			}
//		 }
		 
		 EventDeinJivIdentif pr = new EventDeinJivIdentif ();
		
		 if (this.indexTabl != null) {
			   // Актуализация
		   pr = vpisvane.getEventDeinJiv().getEventDeinJivIdentif().get(this.indexTabl);
		 }
		 pr.setVidIdentif(identifikator.getVidIdentif());
		 pr.setVidJiv(identifikator.getVidJiv());
		 pr.setModel(identifikator.getModel()); // отпада
		 pr.setModel(identifikator.getModel());  //Модел за идентификатор
		 pr.setKod(identifikator.getKod());// код на производитле/ търговец
		   
	     if (this.indexTabl == null) {
	    	 vpisvane.getEventDeinJiv().getEventDeinJivIdentif().add(pr);
	     } else { 
	    	 vpisvane.getEventDeinJiv().getEventDeinJivIdentif().set(this.indexTabl, pr);
	     }
	     PrimeFaces.current().executeScript("PF('dlgIdentif').hide();");
	}


	/**
	 * проверка за валидни данни в модалния за идентифиактори 
	 * @return
	 */
	public boolean checkModalIdent() {
		boolean rez = false;
		if(this.identifikator != null) {
			rez = true;
			if (identifikator.getVidIdentif() == null || identifikator.getVidJiv() == null) {
				rez = false;
			} 
		}
		return rez;
	}


	
	/**
	 * изтриване на идентификатор от списъка
	 * @param pr
	 * @param idx
	 */
	 public void actionRemoveIdentFromList (EventDeinJivIdentif pr, Integer idx) {
		 vpisvane.getEventDeinJiv().getEventDeinJivIdentif().remove(pr);
		 return;
	 }
	

	/*** Методи за дейност "обезщетение за унищожени животни" и "опити с животни"*/

		private Integer vidJiv; // Вид животно
		private Integer broiJiv;  // Брой
		private String  dopInfoJiv; // допълнителна инфромация за вид животно
		
	/**
	 * бутон Нов вид животно за обезщетение
	 */
	public void actionInpNewVidJiv () {
		indexTabl = null;
		vidJiv = null;   
		broiJiv = null;  
		dopInfoJiv = null;
	}
	
	/**
	 * корекция на ред от таблицата - животни
	 * @param pr
	 * @param idx
	 */
	public void actionEditVidJiv( EventDeinJivPredmet pr, Integer idx) {		
		 indexTabl = idx;
		 vidJiv = pr.getPredmet();
		 broiJiv = pr.getBroi();
		 dopInfoJiv = pr.getDopInfo();
		 PrimeFaces.current().executeScript("PF('dlgObezJZ').show();");
	}

	
	/**
	 * изтриване на ред от  таблицата - животни за обезщетение 
	 * @param pr
	 * @param idx
	 */
	public void actionRemoveJivFromList(EventDeinJivPredmet pr, Integer idx) {
		 vpisvane.getEventDeinJiv().getEventDeinJivPredmet().remove(pr);
		 sumaJiv -= pr.getBroi();
	}
	
	
	/**
	 * Добавяне на животно при връщане от модалния				
	 */
	public void actionAddJiv() {
 		boolean dopinfo =  zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OPITI_JIV)  ? SearchUtils.isEmpty(dopInfoJiv) : false;
		if (this.vidJiv == null || this.broiJiv == null || dopinfo) {  
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Не са въведени всички задължителни полета!");
			return;
		}
		
		if (vpisvane.getEventDeinJiv().getEventDeinJivPredmet() == null ) {
			vpisvane.getEventDeinJiv().setEventDeinJivPredmet(new ArrayList<>());
		}
		if ( !zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OPITI_JIV) ) {
			 // Проверка дали вече този вид не е бил въведен - За опити с животни - да няма проверка!
			int i = 0;
			for (EventDeinJivPredmet item : vpisvane.getEventDeinJiv().getEventDeinJivPredmet()) {
				if (!Objects.equals(i, indexTabl)) {
					if (Objects.equals(vidJiv,item.getPredmet())) {
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Вече е избрано животно от този вид!");
						return;
					}
				}
				i++;
			}
		}
	    int oldSumaJiv = 0;
		EventDeinJivPredmet pr = new EventDeinJivPredmet ();
		Integer codeClass = BabhConstants.CODE_CLASSIF_VID_JIVOTNO;
		if (zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OPITI_JIV)) {
			codeClass = BabhConstants.CODE_CLASSIF_VID_JIV_OPIT;
		}				
		if (this.indexTabl == null) {
			// Добавяне на нов вид животно
			pr.setPredmet(vidJiv);
			pr.setCodeClassif(codeClass);
			pr.setBroi(broiJiv);
			pr.setDopInfo(dopInfoJiv);
		  	vpisvane.getEventDeinJiv().getEventDeinJivPredmet().add(pr);
	    } else { 
	    	pr = vpisvane.getEventDeinJiv().getEventDeinJivPredmet().get(this.indexTabl.intValue());
	    	oldSumaJiv = pr.getBroi();
			pr.setPredmet(this.vidJiv);
			pr.setCodeClassif(codeClass);
			pr.setBroi(this.broiJiv);
			pr.setDopInfo(dopInfoJiv);
	    }
		
		if(sumaJiv == null) {
			sumaJiv = 0;
		}
		sumaJiv -= oldSumaJiv;
		sumaJiv += pr.getBroi();
		 
		PrimeFaces.current().executeScript("PF('dlgObezJZ').hide();");
	}
	
	
	/**
	 * бутон Новo лице от екипа
	 */
	public void actionInpNewLiceJiv () {
		indexTabl = null;
		liceEkip = null;   
		roliaLice = null;  
	}
	
	/**
	 * корекция на ред от таблицата - лица
	 * @param lice
	 * @param idx
	 */
	public void actionEditLice( EventDeinJivLice lice, Integer idx) {		
		 indexTabl = idx;
		 try {
			 liceEkip =  lice.getReferent().clone();
			 roliaLice = lice.getTipVraz();
			 liceEkip.setDateBeg(lice.getDateBeg());
			 liceEkip.setDateEnd(lice.getDateEnd());
			 String modal = "";
			 if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_SAHRANENIE_EMBRIONI)  ){
				 modal = "PF('dlgDobivJZ').show();";
			 } else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OPITI_JIV)){
				 modal = "PF('dlgOpitJZ').show();";
			 } else { //CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE
				 modal = "PF('dlgLicaObuchZJ').show();";
			 }
			 PrimeFaces.current().executeScript(modal);
		} catch (CloneNotSupportedException e) {
			LOGGER.error( e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при актуализация на лице! ", e.getMessage());
		}  
		
	}
	
	/**
	 * изтриване на ред от  таблицата - лице от екипа
	 * @param pr
	 * @param idx
	 */
	public void actionRemoveLiceFromList(EventDeinJivLice lice, Integer idx) {
		 vpisvane.getEventDeinJiv().getEventDeinJivLice().remove(lice);
	}

	
	/**
	 * Добавяне на лице, член на екип,  при връщане от модалния			
	 * 
	 */
	public void actionAddLiceEkip() {		 	 
		
		liceEkip = bindingLiceEkip.getRef();
		// за дейност -  Издаване на документи за преминато обучение - няма роля.
		boolean r = (zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE))? false : (roliaLice == null);
		if(r || liceEkip == null || SearchUtils.isEmpty(liceEkip.getRefName()) ){
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Не са въведени всички задължителни полета!");
			return;
		}
	 
		 //при на теглене в актуализация ще е заредно лицето като обект за корекция!!!!
		 if (vpisvane.getEventDeinJiv().getEventDeinJivLice() == null ) {
			 vpisvane.getEventDeinJiv().setEventDeinJivLice(new ArrayList<>());
		 } 
		
		 boolean bb = false;
		 for (int i = 0; i < vpisvane.getEventDeinJiv().getEventDeinJivLice().size(); i++) {
		    EventDeinJivLice item = vpisvane.getEventDeinJiv().getEventDeinJivLice().get(i);
		   
			if (!Objects.equals(item.getTipVraz(), BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_CTRL_LICE) &&
				!Objects.equals(i, indexTabl)) {
				if(liceEkip.getCode() != null && Objects.equals(item.getCodeRef(),liceEkip.getCode())) {
					// избрано е лице, което вече го има в таблица реферети
					bb = true;						
				} else if (!SearchUtils.isEmpty(liceEkip.getFzlEgn()) && Objects.equals(liceEkip.getFzlEgn(), item.getReferent().getFzlEgn())) {
					bb = true;
				} else if (!SearchUtils.isEmpty(liceEkip.getFzlLnc()) && Objects.equals(liceEkip.getFzlLnc(), item.getReferent().getFzlLnc())) {
					bb = true;
				}
				if(bb) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Лицето вече е добавено!");
					return;
				}
			}
		   
		 }
				   
		 EventDeinJivLice lice = new EventDeinJivLice ();
		 if (this.indexTabl == null) {
			// Добавяне на ново лице
			lice.setReferent(liceEkip);
			lice.setTipVraz(roliaLice);		  
		  	if(vpisvane.getDateStatus() == null) {
		  		lice.setDateBeg(new Date());	
			}else {
				lice.setDateBeg(vpisvane.getDateStatus());
			}
			vpisvane.getEventDeinJiv().getEventDeinJivLice().add(lice);
	     } else { 
	    	lice = vpisvane.getEventDeinJiv().getEventDeinJivLice().get(this.indexTabl.intValue());
	    	lice.setCodeRef(liceEkip.getCode());
	    	lice.setReferent(liceEkip);
			lice.setTipVraz(roliaLice);
			//vpisvane.getEventDeinJiv().getEventDeinJivLice().remove(this.indexTabl.intValue());
	     }			 
		// vpisvane.getEventDeinJiv().getEventDeinJivLice().add(lice);
		 liceEkip = null;
		 String modal = "";
		 if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_SAHRANENIE_EMBRIONI)  ){
			 modal = "PF('dlgDobivJZ').hide();";
		 } else if(zDein.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OPITI_JIV)){
			 modal = "PF('dlgOpitJZ').hide();";
		 } else { //CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE
			 modal = "PF('dlgLicaObuchZJ').hide();";
		 }
		 
		 PrimeFaces.current().executeScript(modal);
	}
		
	
	/**
	 * за дейност "одобряване на екип добив и съхранение на ембриони... "
	 * връща диплом/дата или удост. БВС/дата
	 * @param lice
	 * @param vidD
	 * @return
	 */
	public String actionLiceDoc(EventDeinJivLice lice, Integer vidD){
		if(lice != null && lice.getReferent()!= null) {
			List<ReferentDoc> tmpLst = lice.getReferent().getReferentDocs();
			if(tmpLst != null && !tmpLst.isEmpty()){
				for (ReferentDoc doc: tmpLst) {
					if(Objects.equals(doc.getVidDoc(), vidD)){					
						return doc.getNomDoc() + " / " +DateUtils.printDate(doc.getDateIssued()); 
					}
				}
			}
		}
		return null;		
	}
	
	
	/**
	 * Търсене по рег. номер на МПС
	 * Добавяне, ако бъде намерено регистрирано МПС
	 */
	public void actionChangeRegN() {
		if (mps != null && !SearchUtils.isEmpty(mps.getNomer())) {		
			try {				
				Object[] mpsInfo = null;
				mpsInfo =  new MpsDAO(getUserData()).findMpsInfoByIdOrNomer(null, mps.getNomer().trim());
							 
				String msg = null;
				//въвели са някакъв номер, който вече го има в базата
				//проверка за дублиране и дали вече има регситрация
				if (mpsInfo != null && mpsInfo.length > 0 ) {
					Integer status = null;
					if(mpsInfo[6] != null) {
						status = Integer.valueOf(mpsInfo[6].toString());
					}
					if(Objects.equals(status, BabhConstants.STATUS_VP_VPISAN)) {
						String mpsId = String.valueOf(mpsInfo[0]);
						MpsDeinost mpsZJ = new MpsDeinost();
						mpsZJ.setTablEventDeinost(BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_JIV);
						mpsZJ.setMpsId(Integer.valueOf(mpsId));
						mpsZJ.setMpsInfo(mpsInfo);
						if(vpisvane.getDateStatus() == null) {
							mpsZJ.setDateBeg(new Date());	
						}else {
							mpsZJ.setDateBeg(vpisvane.getDateStatus());
						}
						
						List<MpsDeinost> tmpList  = vpisvane.getEventDeinJiv().getMpsDeinost();
						if(tmpList == null) {
							tmpList = new ArrayList<>();
							tmpList.add(mpsZJ);
							vpisvane.getEventDeinJiv().setMpsDeinost(tmpList);
						} else {
							//проверка за дублиране
							for(MpsDeinost item : tmpList) {
								if(Objects.equals(item.getMpsInfo()[4], mps.getNomer()) ){
									msg = "Транспортно средство с рег. номер: "+mps.getNomer()+" вече е добавено!";
									break;
								}								
							}
							if(msg == null) {
								vpisvane.getEventDeinJiv().getMpsDeinost().add(mpsZJ);
							}
						}
					} else {
						msg = "Tранспортно средство с рег. номер " + mps.getNomer() + " не притежава валидна регистарция за превоз на животни.";
					}
				} else {
					msg = "Не е намерено транспортно средство с рег. номер " + this.mps.getNomer();
				}
				if(msg != null) {
					msgScrollTop(msg);
				} 
				mps.setNomer(null);
			} catch (Exception e) {
				LOGGER.error("Грешка при търсене на транспортно средство по рег. номер!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене на транспортно средство по рег. номер!");
				
			} finally {
				JPA.getUtil().closeConnection();
			}
		}
	}
	
	 

	
	/**
	 * изтриване на ред от таблицта с МПС
	 * @param mps
	 * @param idx
	 */
	public void actionRemoveMPSFromList(MpsDeinost mps, Integer idx){
		 vpisvane.getEventDeinJiv().getMpsDeinost().remove(mps);
	}
	
		
	/**
	 * изтриване на ред от  таблицата - ОЕЗ (дейност износ)
	 * @param pr
	 * @param idx
	 */
	public void actionRemoveOezFromList(ObektDeinostDeinost oez, Integer idx) {
		 vpisvane.getEventDeinJiv().getObektDeinostDeinost().remove(oez);
	}
	
	/**
	 * Прпраща към страницата за разглеждане на вписване на ТС
	 */
	public String actionGotoViewMPS(Integer idVpTs) {
		return "regMpsZJView.xhtml?faces-redirect=true&idV=" + idVpTs;
	}
	
	

	/*** Методи за дейност "Търговия с пратки СЖП и производни продукти по чл. 71 от ЗВД "*/

	private Integer vidSjp; // Вид СЖП
	private String  dopInfoSjp; // допълнителна инфромация за вид СЖП
		
	/**
	 * бутон Нов вид СЖП
	 */
	public void actionInpNewVidSjp () {
		indexTabl = null;
		vidSjp = null;
		dopInfoSjp = null;
	}
	
	/**
	 * корекция на ред от таблицата - животни
	 * @param pr
	 * @param idx
	 */
	public void actionEditVidSjp( EventDeinJivPredmet pr, Integer idx) {		
		 indexTabl = idx;
		 vidSjp = pr.getPredmet();
		 dopInfoSjp = pr.getDopInfo();
		 PrimeFaces.current().executeScript("PF('dlgSJP').show();");
	}

	
	/**
	 * изтриване на ред от  таблицата - животни за обезщетение 
	 * @param pr
	 * @param idx
	 */
	public void actionRemoveSjpFromList(EventDeinJivPredmet pr, Integer idx) {
		 predmetDeinSjp.remove(pr);
		 //vpisvane.getEventDeinJiv().getEventDeinJivPredmet().remove(pr);
	}
	
	
	/**
	 * Добавяне на животно при връщане от модалния				
	 */
	public void actionAddSjp() {
 	
		if (this.vidSjp == null) {  
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Не e въведен Вид на СЖП!");
			return;
		}

		if (predmetDeinSjp == null ) {
			predmetDeinSjp = new ArrayList<>();
		}
		
		
		 // Проверка дали вече този вид не е бил въведен 
		int i = 0;
		for (EventDeinJivPredmet item : predmetDeinSjp) {
			if (!Objects.equals(i, indexTabl)) {
				if (Objects.equals(vidSjp,item.getPredmet())) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Вече е избран продукт от този вид!");
					return;
				}
			}
			i++;
		}
	   
		EventDeinJivPredmet pr = new EventDeinJivPredmet ();
				
		if (this.indexTabl == null) {
			// Добавяне на нов вид СЖП
			pr.setPredmet(vidSjp);
			pr.setCodeClassif(BabhConstants.CODE_CLASSIF_FURAJI_SJP_PP);
			pr.setDopInfo(dopInfoSjp);
			predmetDeinSjp.add(pr);
	    } else { 
	    	pr = predmetDeinSjp.get(this.indexTabl.intValue());
	    	pr.setPredmet(this.vidSjp);
			pr.setCodeClassif(BabhConstants.CODE_CLASSIF_FURAJI_SJP_PP);
			pr.setDopInfo(dopInfoSjp);
	    }
		
		PrimeFaces.current().executeScript("PF('dlgSJP').hide();");
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



	public ObektDeinost getOezOne() {
		return oezOne;
	}



	public void setOezOne(ObektDeinost oezOne) {
		this.oezOne = oezOne;
	}

	public String getOezOneAdres() {
		if(this.oezOneAdres == null) {
			this.oezOneAdres = loadOezOneAdres(oezOne);
		}
		return oezOneAdres;
	}

	
	public void setOezOneAdres(String oezOneAdres) {
		this.oezOneAdres = oezOneAdres;
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



	public String getOezOneOperator() {
		if(this.oezOneOperator == null) {
			loadOezOneOperator();
		}
		return oezOneOperator;
	}



	public void setOezOneOperator(String oezOneOperator) {
		this.oezOneOperator = oezOneOperator;
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



	public List<Integer> getzDein() {
		return zDein;
	}



	public void setzDein(List<Integer> zDein) {
		this.zDein = zDein;
	}



	public Map<Integer, Integer> getPredmetDeinJivM() {
		return predmetDeinJivM;
	}



	public void setPredmetDeinJivM(Map<Integer, Integer> predmetDeinJivM) {
		this.predmetDeinJivM = predmetDeinJivM;
	}



	public List<Integer> getPredmetDeinJiv() {
		return predmetDeinJiv;
	}



	public void setPredmetDeinJiv(List<Integer> predmetDeinJiv) {
		this.predmetDeinJiv = predmetDeinJiv;
	}



	public Integer getIndexTabl() {
		return indexTabl;
	}



	public void setIndexTabl(Integer indexTabl) {
		this.indexTabl = indexTabl;
	}


	public EventDeinJivIdentif getIdentifikator() {
		return identifikator;
	}

	public void setIdentifikator(EventDeinJivIdentif identifikator) {
		this.identifikator = identifikator;
	}
	
	public Integer getOezId() {
		return oezId;
	}



	public void setOezId(Integer oezId) {
		this.oezId = oezId;
	}


	public Integer getSumaJiv() {
		return sumaJiv;
	}



	public void setSumaJiv(Integer sumaJiv) {
		this.sumaJiv = sumaJiv;
	}


	public Integer getVidJiv() {
		return vidJiv;
	}



	public void setVidJiv(Integer vidJiv) {
		this.vidJiv = vidJiv;
	}


	public Integer getBroiJiv() {
		return broiJiv;
	}



	public void setBroiJiv(Integer broiJiv) {
		this.broiJiv = broiJiv;
	}


	public Integer getRoliaLice() {
		return roliaLice;
	}



	public void setRoliaLice(Integer roliaLice) {
		this.roliaLice = roliaLice;
	}


	public Referent getLiceEkip() {
		return liceEkip;
	}



	public void setLiceEkip(Referent liceEkip) {
		this.liceEkip = liceEkip;
	}


	public String getTxtLiceEkip() {
		return txtLiceEkip;
	}



	public void setTxtLiceEkip(String txtLiceEkip) {
		this.txtLiceEkip = txtLiceEkip;
	}



	public Mps getMps() {
		return mps;
	}



	public void setMps(Mps mps) {
		this.mps = mps;
	}


	public String getRefDocNom1() {
		return refDocNom1;
	}



	public void setRefDocNom1(String refDocNom1) {
		this.refDocNom1 = refDocNom1;
	}


	public Date getRefDocDate1() {
		return refDocDate1;
	}



	public void setRefDocDate1(Date refDocDate1) {
		this.refDocDate1 = refDocDate1;
	}


	public String getRefDocNom2() {
		return refDocNom2;
	}



	public void setRefDocNom2(String refDocNom2) {
		this.refDocNom2 = refDocNom2;
	}


	public Date getRefDocDate2() {
		return refDocDate2;
	}



	public void setRefDocDate2(Date refDocDate2) {
		this.refDocDate2 = refDocDate2;
	}

	/**
	 * показва съобщение и скролира до горния край на екрана 
	 * @param msg
	 */
	public void msgScrollTop(String msg) {
		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, msg );
		PrimeFaces.current().executeScript("document.body.scrollTop = 0; document.documentElement.scrollTop = 0;");
	}



	public List<Files> getFilesList() {
		return filesList;
	}



	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}



	public RefCorrespData getBindingReferent2() {
		return bindingReferent2;
	}



	public void setBindingReferent2(RefCorrespData bindingReferent2) {
		this.bindingReferent2 = bindingReferent2;
	}



	public RefCorrespData getBindingLiceEkip() {
		return bindingLiceEkip;
	}



	public void setBindingLiceEkip(RefCorrespData bindingLiceEkip) {
		this.bindingLiceEkip = bindingLiceEkip;
	}



	public Integer getLiceCtrl() {
		return liceCtrl;
	}



	public void setLiceCtrl(Integer liceCtrl) {
		this.liceCtrl = liceCtrl;
	}

	
	public Object[] getLastDoc() {
		return lastDoc;
	}



	public void setLastDoc(Object[] lastDoc) {
		this.lastDoc = lastDoc;
	}



	public int getIsView() {
		return isView;
	}

	public void setIsView(int isView) {
		this.isView = isView;
	}

	public BigDecimal getDaljimaSuma() {
		return daljimaSuma;
	}

	public void setDaljimaSuma(BigDecimal daljimaSuma) {
		this.daljimaSuma = daljimaSuma;
	}

	public List<SystemClassif> getDarjClassifList() {
		return darjClassifList;
	}

	public void setDarjClassifList(List<SystemClassif> darjClassifList) {
		this.darjClassifList = darjClassifList;
	}

	public List<SystemClassif> getGkppClassifList() {
		return gkppClassifList;
	}

	public void setGkppClassifList(List<SystemClassif> gkppClassifList) {
		this.gkppClassifList = gkppClassifList;
	}

	public List<SystemClassif> getJivMClassifList() {
		return jivMClassifList;
	}

	public void setJivMClassifList(List<SystemClassif> jivMClassifList) {
		this.jivMClassifList = jivMClassifList;
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

	public String getDopInfoJiv() {
		return dopInfoJiv;
	}

	public void setDopInfoJiv(String dopInfoJiv) {
		this.dopInfoJiv = dopInfoJiv;
	}

	/**
	 * само за опити с животни
	 */
	public void showMestorabOpiti() {
		referent2 = getBindingReferent2().getRef();
	}

	public List<SystemClassif> getPodZaiavList() {
		return podZaiavList;
	}

	public void setPodZaiavList(List<SystemClassif> podZaiavList) {
		this.podZaiavList = podZaiavList;
	}

	public Integer getSferaObucenie() {
		return sferaObucenie;
	}

	public void setSferaObucenie(Integer sferaObucenie) {
		this.sferaObucenie = sferaObucenie;
	}

	public Integer getVidSjp() {
		return vidSjp;
	}

	public void setVidSjp(Integer vidSjp) {
		this.vidSjp = vidSjp;
	}

	public String getDopInfoSjp() {
		return dopInfoSjp;
	}

	public void setDopInfoSjp(String dopInfoSjp) {
		this.dopInfoSjp = dopInfoSjp;
	}

	public List<EventDeinJivPredmet> getPredmetDeinSjp() {
		return predmetDeinSjp;
	}

	public void setPredmetDeinSjp(List<EventDeinJivPredmet> predmetDeinSjp) {
		this.predmetDeinSjp = predmetDeinSjp;
	}
	
	//TODO 
	public String subjectEmail() {
		return "Система БАБХ ИИСР, във връзка със заявление: " + zaiavlVp.getRnDoc()+"/"+DateUtils.printDate(zaiavlVp.getDocDate());
	}

	public int getMigration() {
		return migration;
	}

	public void setMigration(int migration) {
		this.migration = migration;
	}
}
