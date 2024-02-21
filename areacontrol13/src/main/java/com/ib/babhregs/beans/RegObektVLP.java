package com.ib.babhregs.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.components.RefCorrespData;
import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.LockObjectDAO;
import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.db.dto.Vlp;
import com.ib.babhregs.db.dto.VlpLice;
import com.ib.babhregs.db.dto.VlpPrilagane;
import com.ib.babhregs.db.dto.VlpPrilaganeVid;
import com.ib.babhregs.db.dto.VlpVeshtva;
import com.ib.babhregs.db.dto.VlpVidJiv;
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
import eu.europa.ema.v1_26.EAFUtils;


/**
 * 	Търговия с ветеринарни лекарствени продукти 
 */

@Named
@ViewScoped
public class RegObektVLP extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7563430991554561600L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegObektVLP.class);
	private SystemData sd;
	private UserData ud;
	private Date decodeDate;
	private transient VpisvaneDAO	daoVp;
	@Inject
	private Flash flash;
	
	private VlpVeshtva vlpVestvaForModalObject = new VlpVeshtva();
	private Integer tmpI = null;
	private boolean showTextInput;
	private String txtCorresp;
	
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
	 * заявителя (лицензиант) - code_ref_vpivane от tablica вписване
	 */
	private Referent referent2;
	private RefCorrespData	bindingReferent2;	
	private String txtRef2;
	private String txtRef3;
	
	private Referent referent3;
	/**
	 * Представялващо лице
	 */
	private Referent referent1;
	private RefCorrespData	bindingReferent1;	
	/**
	 * вписване 
	 */
	private Vpisvane vpisvane;
	
	/**
	 * за Панел за Дейност: Заявление за търговия на едро с ветеринарни лекарствени продукти " 
	 */
	private List<Integer> predmetDeinostVlPPharmFormCodes;  
	private List<SystemClassif> predmetDeinostVlPPharmFormClassif; // заради autocomplete
	
	/** Списък на файловете към заявлението за вписване */
	private List<Files> filesList;
	private boolean access = false;
	private int isView;
	private int migration;
	
	private Integer codeProizvoditel;
	private Integer codePritezatel;
	private String imeProizvoditel;
	private String imePritezatel;
	private VlpLice vlpLiceForModalObjectProizvoditel = new VlpLice(); 			// Proizvoditel
	private VlpLice vlpLiceForModalObjectPritezatel = new VlpLice(); // Pritezatel
	private String vidoveJivotniForNachin = "";
	private VlpPrilagane vlpPrilagane = new VlpPrilagane(); // Nachin prilagane
	private boolean isNewVlpPrilagane = false;
	private int indexRowNachinSelected = 0;
	private List<Integer> selectedFarmFormsList; 
	private List<SystemClassif> selectedFarmFormsListSC; // заради autocomplete
	private List<Integer> selectedVidJivotniList; 
	private List<SystemClassif> selectedVidJivotniListSC; // заради autocomplete
	private List<Integer> selectedNachinPrilageneList; 
	private List<SystemClassif> selectedNachinPrilageneListSC; // заради autocomplete
	private SystemClassif selectedRezimOtpuskaneSC;
	
	/** Информацията за последното заявление към вписването (ако има такова) */
	private Object[] lastDoc;
	/**
	 * Дължима сума
	 */
 	private BigDecimal daljimaSuma;
 	
 	private List<Integer> selectOpakovkaList;
 	private List<SystemClassif> selectOpakovkaSC; // za opakovka
	
	@PostConstruct
	public void init() {
		sd = (SystemData) getSystemData();
		ud = getUserData(UserData.class);			
		daoVp = new VpisvaneDAO(getUserData());
		actionNew();

		boolean isOK = true;
		// основен обект  за  ветеринарни лекарствени продукти
		String paramZaiavlenie = JSFUtils.getRequestParameter("idZ");
		String paramVpisvane = JSFUtils.getRequestParameter("idV");
		try {
			FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
			String paramRazglezdaneFromPage = (String) faceletContext.getAttribute("isView"); 
			if( (String) faceletContext.getAttribute("isView") == null ) {
				paramRazglezdaneFromPage = JSFUtils.getRequestParameter("isView"); 
				if(paramRazglezdaneFromPage == null)
					paramRazglezdaneFromPage = "";
			}
			
			setIsView(!SearchUtils.isEmpty(paramRazglezdaneFromPage) ? Integer.valueOf(paramRazglezdaneFromPage) : 0);// 0 - актуализация; 1 - разглеждане
			
			String paramM = JSFUtils.getRequestParameter("m");
			migration = SearchUtils.isEmpty(paramM) ? BabhConstants.CODE_ZNACHENIE_NE :  0; // Ако е подаден параметър - значи идва от екрана за ръчно въвеждане на миграция
			
			if ( !SearchUtils.isEmpty(paramVpisvane)){
	
				Integer idVp = Integer.valueOf(paramVpisvane);
				// проверка за заключено вписване. + закл. на текущия обект.
				if(getIsView() == 0) {
					isOK = checkAndLockDoc(idVp, BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
				}
				if (isOK) {
					// идваме от актуализация на вписване - подадено е ид на вписване
					initVpisane(idVp);
					
					// ако заявлението е за промяна или заличаване.. винаги позволяваме корекция на данните
					String zaiavChange = JSFUtils.getRequestParameter("change"); 
					if (zaiavChange != null && !zaiavChange.isEmpty()) {
						this.vpisvane.setVpLocked(Constants.CODE_ZNACHENIE_NE);
					}
					
					// проверка за заключено вписване!!
					// TODO - ако е заключено - някакво съобщение.....
					isOK = loadZaiavl(vpisvane.getIdZaqavlenie(), false);
					loadOptions(); //  Извличане на настройките по вида на документа
					loadLastZaiav(true); // зарежда последното заявление
					makeStringForVidJivotniForNachin(vpisvane.getVlp().getVlpPrilagane());
					makeStringForFarmForm(vpisvane.getVlp().getFarmFormList());
					makeStringForVidJivotni();
					makeStringForOpakovka(vpisvane.getVlp().getOpakovkaList());
					access = true;
				}else {
					access = false;
				}
			} else  if (!SearchUtils.isEmpty(paramZaiavlenie))  { 
					// idvame ot zaiavlenieto nishto oshte ne e zapisano
					Integer idZ = Integer.valueOf(paramZaiavlenie);
					// проверка за заключено вписване. + закл. на текущия обект.
					isOK = checkAndLockDoc(idZ, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
					if(isOK) {
						isOK = loadZaiavl(idZ, true); // зареждане на заявлението и лицата - заявителите
						// проверка за достъп до заявлението
						JPA.getUtil().runWithClose(() -> access = new DocDAO(ud).hasDocAccess(zaiavlVp, getIsView()==0, sd));
					} else {
						access = false;
					}
			} else {
				throw new Exception("Достъпът е отказан!");
			} 

			if(access) {			
				loadFilesList(zaiavlVp.getId());
				checkDaljimaSuma();
			} else {
				 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,"Достъпът е отказан!");
				 access = false;
			}	
		} catch(Exception e) {
			LOGGER.error( "Error in regObektVlp! ", e.getMessage());
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
			payStatus = daoVp.findUnpaidTaxInfo(vpisvane == null ? null : vpisvane.getId(), zaiavlVp == null ? null : zaiavlVp.getId());
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
	 * зарежда данни за вписванем ако е подадено ид на вписване  
	 * @param idVp
	 */
	private boolean initVpisane(Integer idVp) {
		boolean rez = false;
		try {
			JPA.getUtil().runInTransaction(()-> { 
				this.vpisvane =  daoVp.findById(Integer.valueOf(idVp));				
			});
			if(vpisvane != null) {
				vpisvane.getVlp().getVlpLice().size();
				vpisvane.getVlp().getVlpVeshtva().size();
				vpisvane.getVlp().getVlpVidJiv().size();
			}
		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на вписване и обектите към него", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
		}
		return rez;
	}
	
	public void actionNew() {
		vpisvane = new Vpisvane();
		this.decodeDate = new Date();
		codePritezatel = null;
		codeProizvoditel = null;
		imePritezatel = null;
		imeProizvoditel = null;
		vlpLiceForModalObjectProizvoditel = new VlpLice();
		vlpLiceForModalObjectPritezatel = new VlpLice();
		indexRowNachinSelected = 0;
		isNewVlpPrilagane = false;
		selectedVidJivotniList = new ArrayList<>();
		selectedVidJivotniListSC = new ArrayList<>();
		selectedNachinPrilageneList = new ArrayList<>();
		selectedNachinPrilageneListSC = new ArrayList<>();
		selectedFarmFormsList = new ArrayList<>();
		selectedFarmFormsListSC = new ArrayList<>();
		selectOpakovkaList = new ArrayList<>();
		selectOpakovkaSC = new ArrayList<>();
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
			if (activeTab.equals("danniVpisvaneTab") || activeTab.equals("docVpisvaneTab")  || activeTab.equals("etapiVpisvaneTab") ) {
				getFlash().put("beanName", "regObektVLP"); // задъжлително се подава името на бийна, ако отиваме към таба със статусите
			} else if(activeTab.equals("osnovniDanniTab") && Objects.equals(zaiavlVp.getPayType(), BabhConstants.CODE_ZNACHENIE_PAY_TYPE_FLOAD)) {
				checkDaljimaSuma();// ако е с плаваща тарифа и тя случйно е променена в таб документи- да се упдейтне екрана
			} 
			
	   	}
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
		}
	}
	
	
	/**
	 * парсване на заявлението и зареждане на данните от него в zaiavlVp 
	 * зареждане на днните за заявител 1 и заявител 2 - в обектi adm_referent
	 * @param idZ - ид заявление
	 * @param parse - ид заявление
	 * @return
	 */
	private boolean loadZaiavl(Integer idZ, boolean parse) {
		//  извличане на данните от заявлението
	  boolean rez= true;
	  try {
			if(parse) {
				// ---------------- Start of Vassil Change ---------------------
				EgovContainer container = new EgovContainer();
				try {
					container = new EFormUtils().convertEformToVpisvane(idZ, sd);
				} catch (Exception e1) {
					// да заредя все пак заявлението!!!
					// въпреки, че не се парсва файлът - да може да се отоври екрана!
					JPA.getUtil().runWithClose(() -> this.zaiavlVp =   new DocDAO(ud).findById(Integer.valueOf(idZ)));
					LOGGER.error("Грешка при парсване на заявление - ВЛЗ! ", e1); JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
									"Грешка при парсване на заявление! Данните не могат да бъдат заредени от прикачения файл!");
				}
				if(container.doc != null) {
				   this.zaiavlVp = container.doc;
				}
				this.vpisvane = container.vpisvane;
				referent1 = container.ref1;
				referent2 = container.ref2;
				//Още го няма ама би трябвало така да стане.
//				this.referent3 = container.ref3;

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
				
		
				if(referent3 == null) {
					referent3 = new Referent();
					referent3.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
				}
				
				Integer registerId = loadOptions(); //  Извличане на настройките по вида на документа
				 // ако идваме от списъкът на  заявленията и ще имаме ново вписване 
				if(vpisvane == null) {
					vpisvane = new Vpisvane();
				}
				vpisvane.setRegistraturaId(zaiavlVp.getRegistraturaId());
				vpisvane.setIdZaqavlenie(zaiavlVp.getId());
				vpisvane.setRegNomZaqvlVpisvane(zaiavlVp.getRnDoc());
				vpisvane.setDateZaqvlVpis(zaiavlVp.getDocDate());
				if(referent2 != null && referent2.getCode() != null) {
					vpisvane.setCodeRefVpisvane(referent2.getCode()); // от парсването на заявлението
				}
				
				vpisvane.setIdRegister(registerId); // ако е ново вписване... 
				vpisvane.setLicenziantType(BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_LICE  );
				vpisvane.setCodePage(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP);  // Код на класификация "Страници за обработка на обекта за лицензиране" 522, /*регистриране на ВЛП=6 */
				vpisvane.setStatus(BabhConstants.STATUS_VP_WAITING);
				vpisvane.setDateStatus(new Date());
				if(vpisvane.getVlp() == null) {
					vpisvane.setVlp(new Vlp());
					vpisvane.getVlp().setVlpVidJiv(new ArrayList<>());
					vpisvane.getVlp().setVlpLice(new ArrayList<>());
					vpisvane.getVlp().setVlpVeshtva(new ArrayList<>());
				}
				// 0 - миграция, ръчно въвеждане,1 - миграция - завършена, 2 - не е миграция;
				if (migration == 0) {
					vpisvane.setFromМigr(migration); // ако е от миграция - ръчно въвеждане, иначе не се променя
				}
				
			} else {
				DocDAO daoDoc = new DocDAO(ud);
				JPA.getUtil().runInTransaction(() -> this.zaiavlVp =  daoDoc.findById(Integer.valueOf(idZ)));
				if (zaiavlVp != null) {
					loadFilesList(zaiavlVp.getId());
					if(zaiavlVp.getCodeRefCorresp() != null) {
						JPA.getUtil().runWithClose(() -> referent1 = new ReferentDAO(ud).findByCodeRef(zaiavlVp.getCodeRefCorresp()));
						referent1.setKachestvoLice(zaiavlVp.getKachestvoLice());  
					} 
					if(vpisvane != null && vpisvane.getCodeRefVpisvane() != null) {
						JPA.getUtil().runWithClose( () ->  referent2 = new ReferentDAO(ud).findByCodeRef(vpisvane.getCodeRefVpisvane()) );
					}
					if(vpisvane != null && vpisvane.getIdLicenziant() != null) {
						JPA.getUtil().runWithClose( () ->  referent3 = new ReferentDAO(ud).findByCodeRef(vpisvane.getIdLicenziant()) );
					}
				}
			}
		} catch (Exception e) {
			rez = false;
			LOGGER.error("Грешка при зареждане на заявление", e); JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
		}
		return rez;
	}
	
	/***
	 * ИЗВЛИЧА ПОСЛЕДНОТО ЗАЯВЛЕНИЕ КЪМ ДАДЕНО ВПИСВАНЕ
	 * 
	 * @param exludeFirst - ако е true игнорира заявлението за първоначално вписване
	 * */
	private void loadLastZaiav(boolean exludeFirst) {
		try {
			
			setLastDoc(this.daoVp.findLastIncomeDoc(this.vpisvane.getId(), exludeFirst));

		} catch (BaseException e) {
			LOGGER.error("Грешка при извличане на последно заявление! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}
	
	public String actionGotoLastZaiav() {
		return "babhZaiavView.xhtml?faces-redirect=true&idObj=" + this.getLastDoc()[0];
	}
	
	
	/**
	 *   Извличане на настройките по вида на документа
	 * @return ид на регистъра
	 */ 
	private Integer loadOptions() {		
		 Integer registerId = sd.getRegisterByVidDoc().get(zaiavlVp.getDocVid()); 
		 registerOptions = sd.getRoptions().get(registerId); // всички настройки за регистъра
		 List<RegisterOptionsDocsIn> lstDocsIn = registerOptions.getDocsIn(); // само входните док. за регистъра
		 docOpt = new RegisterOptionsDocsIn();
		 for(RegisterOptionsDocsIn item : lstDocsIn ) {
			if ((item.getVidDoc()).equals(zaiavlVp.getDocVid())) {
				docOpt = item;
				break;
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
		 }
		 return registerId;
	}

	
	public void actionSave() {
		try {
			referent1 = ((RefCorrespData)FacesContext.getCurrentInstance().getViewRoot().findComponent("obektVlpForm").findComponent("registerTabs:licеPоdatel")).getRef();
			referent2 = ((RefCorrespData)FacesContext.getCurrentInstance().getViewRoot().findComponent("obektVlpForm").findComponent("registerTabs:liceZaqvitel")).getRef();
			referent3 = ((RefCorrespData)FacesContext.getCurrentInstance().getViewRoot().findComponent("obektVlpForm").findComponent("registerTabs:licePritezatelRaz")).getRef();
			if(checkDataVp()) { 			
					boolean newVp = this.vpisvane.getId() == null;
					addVidJiv();
					addFarmForm();
					addOpakovka();
					saveVp(newVp);
					if(newVp) {// само за нов документ	
						// заключване на вписване.
						lockObj(this.vpisvane.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
					}
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG) );		
					makeStringForVidJivotniForNachin(vpisvane.getVlp().getVlpPrilagane());
			}	
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
	
	/*private void setVlpLice() {
		vlpLiceForModalObjectProizvoditel.setVlpId(vpisvane.getVlp().getId());
		vlpLiceForModalObjectProizvoditel.setTip(BabhConstants.CODE_ZNACHENIE_VLP_LICE_TIP_PROIZVODITEL);
		vlpLiceForModalObjectProizvoditel.setCodeRef(codeProizvoditel);
		
		vlpLiceForModalObjectPritezatel.setVlpId(vpisvane.getVlp().getId());
		vlpLiceForModalObjectPritezatel.setTip(BabhConstants.CODE_ZNACHENIE_VLP_LICE_TIP_PRITEZATEL);
		vlpLiceForModalObjectPritezatel.setCodeRef(codePritezatel);
		
		vpisvane.getVlp().getVlpLice().clear();
		vpisvane.getVlp().getVlpLice().add(vlpLiceForModalObjectProizvoditel);
		vpisvane.getVlp().getVlpLice().add(vlpLiceForModalObjectPritezatel);
	}*/
	
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
	
	/**
	 * TODO
	 * @return
	 */
	public boolean checkDataVp() {
		boolean checkOk = true;
		if(referent1 == null) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:licеPоdatel", FacesMessage.SEVERITY_ERROR, "Моля, въведете Представялващо лице!" );
			checkOk = false;
		} else if(referent1.getRefName() == null || referent1.getRefName().trim().isEmpty()) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:licеPоdatel", FacesMessage.SEVERITY_ERROR, "Моля, въведете Име на Представялващо лице!" );
			checkOk = false;
		}
		if(referent1 != null && referent1.getKachestvoLice() == null) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:licеPоdatel", FacesMessage.SEVERITY_ERROR, "Моля, въведете 'В качествотo' при Представялващо лице!" );
			checkOk = false;
		}
		
		if(referent2 == null) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:liceZaqvitel", FacesMessage.SEVERITY_ERROR, "Моля, въведете Заявител!" );
			checkOk = false;
		} else if(referent2.getRefName() == null || referent2.getRefName().trim().isEmpty()) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:liceZaqvitel", FacesMessage.SEVERITY_ERROR, "Моля, въведете Име на Заявител!" );
			checkOk = false;
		}
		if(referent3 == null) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:licePritezatelRaz", FacesMessage.SEVERITY_ERROR, "Моля, въведете Притежател!" );
			checkOk = false;
		} else if(referent3.getRefName() == null || referent3.getRefName().trim().isEmpty()) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:licePritezatelRaz", FacesMessage.SEVERITY_ERROR, "Моля, въведете Име на Притежател!" );
			checkOk = false;
		}
		
		for(VlpVidJiv vlpVidJivotno : vpisvane.getVlp().getVlpVidJiv()) {
			if(vlpVidJivotno.getVidJiv() == null) {
				JSFUtils.addMessage("obektVlpForm:tblJivotniFuraj", FacesMessage.SEVERITY_ERROR, "Моля, въведете Вид животно, за което е предназначен ВЛП!");
				checkOk = false;
			}
		}
		return checkOk;
	}
	
	public boolean checkProizvoditel() {
		if(vlpLiceForModalObjectProizvoditel.getReferent() == null) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:liceProizvoditel", FacesMessage.SEVERITY_ERROR, "Моля, въведете Производител, отговорен за освобождаване на партидата!" );
			return false;
		} 
		if(vlpLiceForModalObjectProizvoditel.getReferent().getRefName() == null || vlpLiceForModalObjectProizvoditel.getReferent().getRefName().trim().isEmpty()) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:liceProizvoditel", FacesMessage.SEVERITY_ERROR, "Моля, въведете Име на Производител, отговорен за освобождаване на партидата!" );
			return false;
		}
		return true;
	}
	public boolean checkPritezatel() {
		if(vlpLiceForModalObjectPritezatel.getReferent() == null) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:licePritezatel", FacesMessage.SEVERITY_ERROR, "Моля, въведете Притежател на разрешение за търговия!" );
			return false;
		} 
		if(vlpLiceForModalObjectPritezatel.getReferent().getRefName() == null || vlpLiceForModalObjectPritezatel.getReferent().getRefName().trim().isEmpty()) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:licePritezatel", FacesMessage.SEVERITY_ERROR, "Моля, въведете Име на Притежател на разрешение за търговия!" );
			return false;
		}
		return true;
	}
	
	
	private void saveVp(boolean newVp) throws BaseException {
		zaiavlVp.setKachestvoLice(referent1.getKachestvoLice()); // ROSI naprawi promqna
		zaiavlVp.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN); // от класификация 	CODE_CLASSIF_DOC_STATUS(120)
		
		JPA.getUtil().runInTransaction(() -> { 
			this.vpisvane= this.daoVp.save(vpisvane, zaiavlVp, referent1, referent2, referent3, sd);
			this.vpisvane.getVlp();
			this.vpisvane.getVlp().getVlpLice().size();
			this.vpisvane.getVlp().getVlpVeshtva().size();
			this.vpisvane.getVlp().getVlpVidJiv().size();
		});
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
	
	public void addVidJiv() {
		vpisvane.getVlp().getVlpVidJiv().clear();
		for (Integer tmpVidJivotno : selectedVidJivotniList) {
			VlpVidJiv vvj = new VlpVidJiv();
			vvj.setVidJiv(tmpVidJivotno);
			vvj.setVlpId(vpisvane.getVlp().getId());
			vpisvane.getVlp().getVlpVidJiv().add(vvj);
		}
	}
	
	public void addFarmForm() {
		if(vpisvane.getVlp().getFarmFormList() == null)
			vpisvane.getVlp().setFarmFormList(new ArrayList<>()) ;
		else
			vpisvane.getVlp().getFarmFormList().clear();
		for (Integer tmpFarmForm : selectedFarmFormsList) {
			vpisvane.getVlp().getFarmFormList().add(tmpFarmForm);
		}
	}
	public void addOpakovka() {
		if( vpisvane.getVlp().getOpakovkaList() == null )
			vpisvane.getVlp().setOpakovkaList(new ArrayList<>());
		else	
			vpisvane.getVlp().getOpakovkaList().clear();
		for (Integer opakovkaInt : selectOpakovkaList) {
			vpisvane.getVlp().getOpakovkaList().add(opakovkaInt);
		}
	}
	public void makeStringForVidJivotni() {
		selectedVidJivotniList = new ArrayList<>();
		selectedVidJivotniListSC = new ArrayList<>();
		if(vpisvane.getVlp().getVlpVidJiv() == null)
			return;
		
		for (VlpVidJiv tmpVvj : vpisvane.getVlp().getVlpVidJiv()) {
			selectedVidJivotniList.add(tmpVvj.getVidJiv());
			
			SystemClassif scItem = new SystemClassif();
			scItem.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_JIV_ES);
			scItem.setCode(tmpVvj.getVidJiv());
			try {
					scItem.setTekst(getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_VID_JIV_ES, tmpVvj.getVidJiv(), getCurrentLang(), new Date()));
			} catch (DbErrorException e) {
				LOGGER.error(" Error in decoding type animal: ", tmpVvj.getVidJiv() + " in method setVidJivotniSC! ");
				scItem.setTekst( "Не разкодирано значение:" + tmpVvj.getVidJiv() );
			}
			selectedVidJivotniListSC.add(scItem);
		}
	}
	
	public void actionDeleteJiv(VlpVidJiv selectedRow) {
		vpisvane.getVlp().getVlpVidJiv().remove(selectedRow);
	}
	
	/**
	 * Отваря модалния прозорец за въвеждане на вещества (Активини / Помощни). Тук сетват помощние обект от основния обект vpisvane.getVlp().getVlpVeshtva().
	 * На затварянето ще се сетва обатно в основния обект за страницата - vpisvane.getVlp ... ......
	 * @param typeSusbstance - за да разберем дали е нов обект или ще редактираме!!!
	 * */
	
	public void openModalSubstance(Integer indextOfSubstanceObject) {
		tmpI = indextOfSubstanceObject;
		if(indextOfSubstanceObject == null)
			vlpVestvaForModalObject = new VlpVeshtva();
		else
			vlpVestvaForModalObject = vpisvane.getVlp().getVlpVeshtva().get(indextOfSubstanceObject);
	
		if(vlpVestvaForModalObject.getType() == null || vlpVestvaForModalObject.getType().intValue() == 1)
			showTextInput = true;
		else
			showTextInput = false;
	}
	
	public void confirmSubstance() {
		if(tmpI == null)
			vpisvane.getVlp().getVlpVeshtva().add(vlpVestvaForModalObject);
		else
			vpisvane.getVlp().getVlpVeshtva().set(tmpI, vlpVestvaForModalObject);
		
		
		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Успешно добавяне на субстанция!" );		

		PrimeFaces.current().executeScript("PF('modalSubstance').hide();");
	}
	public void actionDeleteActiveSubstance(VlpVeshtva selectedSubstance) {
		vpisvane.getVlp().getVlpVeshtva().remove(selectedSubstance);
	}
	
	public void onOptionChange() {
//		vlpVestvaForModalObject.setCodeRefProizvoditel(null);
//		vlpVestvaForModalObject.setMqstoProizvodstvo(null);
//		vlpVestvaForModalObject.setOpisEtapi(null);
//		
		if(vlpVestvaForModalObject.getType() == null || vlpVestvaForModalObject.getType().intValue() == 1)
			showTextInput = true;
		else
			showTextInput = false;
			
	}

	/** Методи за работа с Лица, които се добавя към ВЛП
	 * */
	
	
	public void openModalLice(Integer indextOfLiceObject) {
		tmpI = indextOfLiceObject;
		if(indextOfLiceObject == null)
			vlpLiceForModalObjectProizvoditel = new VlpLice();
		else
			vlpLiceForModalObjectProizvoditel = vpisvane.getVlp().getVlpLice().get(indextOfLiceObject);
	}
	
	public void confirmLice() {
		boolean willReturn = false;
		/*if(vlpLiceForModalObjectProizvoditel.getReferent() == null ||  vlpLiceForModalObjectProizvoditel.getReferent().getCode() == null) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:licePritezatel2", FacesMessage.SEVERITY_ERROR, "Моля, въведете лице!" );
			willReturn = true;
		}
		if( vlpLiceForModalObjectProizvoditel.getReferent().getRefName() == null || vlpLiceForModalObjectProizvoditel.getReferent().getRefName().trim().isEmpty() )  {
			JSFUtils.addMessage("obektVlpForm:registerTabs:licePritezatel2", FacesMessage.SEVERITY_ERROR, "Моля, въведете име на лице!" );
			willReturn = true;
		}*/
		vlpLiceForModalObjectProizvoditel.setReferent( getBindingReferent2().getRef() );
		if(SearchUtils.isEmpty(vlpLiceForModalObjectProizvoditel.getReferent().getRefName()) ){ 
			willReturn = true;
			JSFUtils.addMessage("obektVlpForm:registerTabs:licePritezatel2",FacesMessage.SEVERITY_ERROR,"Въведeте име на Квалифицирно лице!");
		}
		if(vlpLiceForModalObjectProizvoditel.getDateBeg() != null && vlpLiceForModalObjectProizvoditel.getDateEnd() != null) {
			if(vlpLiceForModalObjectProizvoditel.getDateBeg().getTime() > vlpLiceForModalObjectProizvoditel.getDateEnd().getTime()) {
				JSFUtils.addMessage("obektVlpForm:registerTabs:dateBeg", FacesMessage.SEVERITY_ERROR, "Моля, въведете Начална дата, по-малка или равна на Крайната дата!" );
				willReturn = true;
			}
		}
		if(willReturn)
			return;
		
		vlpLiceForModalObjectProizvoditel.setCodeRef(vlpLiceForModalObjectProizvoditel.getReferent().getCode());
		if(tmpI == null) {
			vpisvane.getVlp().getVlpLice().add(vlpLiceForModalObjectProizvoditel);
			
		}else {
			vpisvane.getVlp().getVlpLice().set(tmpI, vlpLiceForModalObjectProizvoditel);
		}
		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Успешно добавяне на лице!" );		
		PrimeFaces.current().executeScript("PF('modalLice').hide();");
	}
	
	public void actionDeleteLice(VlpLice selectedLice) {
		vpisvane.getVlp().getVlpLice().remove(selectedLice);
	}
	
	/** КРАЙ на методи за работа с Лица, които се добавя към ВЛП
	 * */
	/** 8.	Начин на прилагане
	 * */
	
	public void addNachin() {
		isNewVlpPrilagane = true;
		vlpPrilagane = new VlpPrilagane();
		selectedNachinPrilageneList = new ArrayList<>();
		selectedNachinPrilageneListSC = new ArrayList<>();
	}
	
	public void openModalNachin(int indxVlpPrilagane) {
		isNewVlpPrilagane = false;
		indexRowNachinSelected = indxVlpPrilagane;
		vlpPrilagane = vpisvane.getVlp().getVlpPrilagane().get(indxVlpPrilagane);
		
		selectedNachinPrilageneList = new ArrayList<>();
		selectedNachinPrilageneListSC = new ArrayList<>();
		for (VlpPrilaganeVid tmpVpv : vlpPrilagane.getVlpPrilaganeVid()) {
			selectedNachinPrilageneList.add(tmpVpv.getVid());

			SystemClassif scItem = new SystemClassif();
			scItem.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_JIV_ES);
			scItem.setCode(tmpVpv.getVid());
			try {
					scItem.setTekst(getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_VID_JIV_ES, tmpVpv.getVid(), getCurrentLang(), new Date()));
			} catch (DbErrorException e) {
				LOGGER.error(" Error in decoding type animal: ", tmpVpv.getVid() + " in method openModalNachin! ");
				scItem.setTekst( "Не разкодирано значение:" + tmpVpv.getVid() );
			}
			selectedNachinPrilageneListSC.add(scItem);
		}
	}
	
	public void addVidJivForNachin() {
		if(vlpPrilagane.getVlpPrilaganeVid() == null) {
			vlpPrilagane.setVlpPrilaganeVid(new ArrayList<>());
			vlpPrilagane.getVlpPrilaganeVid().add(new VlpPrilaganeVid());
		}else	
			vlpPrilagane.getVlpPrilaganeVid().add(new VlpPrilaganeVid());
	}
	
	public void confirmNachin() {
		boolean willReturn = false;
		if(vlpPrilagane.getCodeNachin() == null) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:nachin", FacesMessage.SEVERITY_ERROR, "Моля, въведете Начин!" );
			willReturn = true;
		}
		if(selectedNachinPrilageneList == null || selectedNachinPrilageneList.size() == 0) {
			JSFUtils.addMessage("obektVlpForm:registerTabs:nachin", FacesMessage.SEVERITY_ERROR, "Моля, въведете Видове животни!" );
			willReturn = true;
		}
		if(willReturn)
			return;
		
		List<VlpPrilaganeVid> tmpVpvList = new ArrayList<>();
		for (Integer vidJivotnoForNachinInt : selectedNachinPrilageneList) {
			VlpPrilaganeVid vpv = new VlpPrilaganeVid();
			vpv.setVlpPrilaganeId(vlpPrilagane.getId());
			vpv.setVid(vidJivotnoForNachinInt);
			tmpVpvList.add(vpv);
		}
		vlpPrilagane.setVlpPrilaganeVid(tmpVpvList);
		makeStringForVidJivotniForNachin(vlpPrilagane);
		if(vlpPrilagane.getId() == null && isNewVlpPrilagane)
			vpisvane.getVlp().getVlpPrilagane().add(vlpPrilagane);
		else {
			 vpisvane.getVlp().getVlpPrilagane().set( indexRowNachinSelected, vlpPrilagane);
		}
		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Успешно добавяне на Начин на прилагане!" );	
		PrimeFaces.current().executeScript("PF('mNP').hide();");
	}
	
	public void makeStringForVidJivotniForNachin(VlpPrilagane vlpPrilagane2) {
		vlpPrilagane2.setVidoveJivotniForNachin(null);
		for (VlpPrilaganeVid vlpPVid : vlpPrilagane2.getVlpPrilaganeVid()) {
			if(vlpPrilagane2.getVidoveJivotniForNachin() == null || vlpPrilagane2.getVidoveJivotniForNachin().trim().isEmpty() )
				vlpPrilagane2.setVidoveJivotniForNachin(vidJivotnoAsString(BabhConstants.CODE_CLASSIF_VID_JIV_ES, vlpPVid.getVid()));
			else {
				String tmp =  vlpPrilagane2.getVidoveJivotniForNachin();
				tmp += " , " + vidJivotnoAsString(BabhConstants.CODE_CLASSIF_VID_JIV_ES, vlpPVid.getVid());
				vlpPrilagane2.setVidoveJivotniForNachin(tmp);
			}
		}
	}
	public void makeStringForVidJivotniForNachin(List<VlpPrilagane> vlpPrilagane) {
		if(vlpPrilagane == null)
			return;
		
		this.vlpPrilagane.setVidoveJivotniForNachin(null);
		for (VlpPrilagane vlpPrilagane2 : vlpPrilagane) {
			vlpPrilagane2.setVidoveJivotniForNachin(null);
			for (VlpPrilaganeVid vlpPVid : vlpPrilagane2.getVlpPrilaganeVid()) {
				if(vlpPrilagane2.getVidoveJivotniForNachin() == null || vlpPrilagane2.getVidoveJivotniForNachin().trim().isEmpty() )
					vlpPrilagane2.setVidoveJivotniForNachin(vidJivotnoAsString(BabhConstants.CODE_CLASSIF_VID_JIV_ES, vlpPVid.getVid()));
				else {
					String tmp =  vlpPrilagane2.getVidoveJivotniForNachin();
					tmp += " , " + vidJivotnoAsString(BabhConstants.CODE_CLASSIF_VID_JIV_ES, vlpPVid.getVid());
					vlpPrilagane2.setVidoveJivotniForNachin(tmp);
				}
			}
		}
	}
	
	private String vidJivotnoAsString(Integer codeClassif, Integer vid) {
		try {
			return getSystemData().decodeItem(codeClassif, vid, getCurrentLang(), new Date());
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при разкодиране на вид животно: ", vid);
			return "Не разкодирано значение:" + vid;
		}
	}
	
	public void actionDeleteNachin(VlpPrilagane row) {
		vpisvane.getVlp().getVlpPrilagane().remove(row);
	}
	public void actionDeleteJivNachin(VlpPrilaganeVid row) {
		vlpPrilagane.getVlpPrilaganeVid().remove(row);
	}
	
	public void makeStringForOpakovka(List<Integer> opakovkaList) throws DbErrorException {
		selectOpakovkaList = new ArrayList<>();
		selectOpakovkaSC = new ArrayList<>();
		if(opakovkaList == null)
			return;
		
		for (Integer opakovka : opakovkaList) {
			selectOpakovkaList.add(opakovka);
			SystemClassif scItem = new SystemClassif();
			scItem.setCodeClassif(BabhConstants.CODE_CLASSIF_PARVICHNA_OPAKOVKA);
			scItem.setCode(opakovka);
			scItem.setTekst(getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_PARVICHNA_OPAKOVKA, opakovka, getCurrentLang(), new Date()));
			selectOpakovkaSC.add(scItem);
		}
	}
	public void makeStringForFarmForm(List<Integer> farmFormList) throws DbErrorException {
		/*
		 * for (Integer farmForm : farmFormList) { if(selectedFarmForms == null ||
		 * selectedFarmForms.trim().isEmpty() ) selectedFarmForms =
		 * vidJivotnoAsString(BabhConstants.CODE_CLASSIF_PHARM_FORMI , farmForm); else
		 * selectedFarmForms += " , " +
		 * vidJivotnoAsString(BabhConstants.CODE_CLASSIF_PHARM_FORMI , farmForm); }
		 */
		selectedFarmFormsList = new ArrayList<>();
		selectedFarmFormsListSC = new ArrayList<>();
		if(farmFormList == null)
			return;
		
		for (Integer farmForm : farmFormList) {
			selectedFarmFormsList.add(farmForm);
			
			SystemClassif scItem = new SystemClassif();
			 scItem.setCodeClassif(BabhConstants.CODE_CLASSIF_PHARM_FORMI);
			 scItem.setCode(farmForm);
			 scItem.setTekst(getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_PHARM_FORMI, farmForm, getCurrentLang(), new Date()));
			 selectedFarmFormsListSC.add(scItem);
		}
	}
	
	
	
	/** Прикачване на XML файл от еФорми.
	 * */
	public void uploadVlpXml(FileUploadEvent event)  {
		try {
			UploadedFile item = event.getFile();
			
			byte[] bytes = item.getContent();
			
			String xml = new String(bytes);
			
			vpisvane.setVlp(new EAFUtils().parseXML(xml, sd));
			
			if (vpisvane.getVlp().getVlpLice() != null) {
				for (VlpLice lice : vpisvane.getVlp().getVlpLice()) {
					if (lice.getTip() != null && lice.getTip().equals(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_PRITEJ_LICENZ) ) {
						referent3 = lice.getReferent();
						referent3.setRefLatin(referent3.getRefName());
						txtRef3 = referent3.getRefName();
						
					}
				}
			}
			
			
			vpisvane.setLicenziant(xml);
			
			loadOptions(); //  Извличане на настройките по вида на документа
			//loadLastZaiav(true); // зарежда последното заявление
			makeStringForVidJivotniForNachin(vpisvane.getVlp().getVlpPrilagane());
			makeStringForFarmForm(vpisvane.getVlp().getFarmFormList());
			makeStringForOpakovka(vpisvane.getVlp().getOpakovkaList());
			makeStringForVidJivotni();
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "XML файлът e прочетен успешно!" );	
		} catch (DbErrorException e) {
			LOGGER.error( "Error in parse XML fro eform or filling the fields! ", e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при четене на XML файлът!", e.getMessage() );
		} catch (Exception e) {
			LOGGER.error( "Error in parse XML fro eform ! ", e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при четене на XML файлът!", e.getMessage() );
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




	 



	public Vpisvane getVpisvane() {
		return vpisvane;
	}



	public void setVpisvane(Vpisvane vpObj) {
		this.vpisvane = vpObj;
	}



	 



	public Doc getZaiavlVp() {
		return zaiavlVp;
	}



	public void setZaiavlVp(Doc zaiavlVp) {
		this.zaiavlVp = zaiavlVp;
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


	public List<Integer> getPredmetDeinostVlPPharmFormCodes() {
		return predmetDeinostVlPPharmFormCodes;
	}



	public void setPredmetDeinostVlPPharmFormCodes(List<Integer> predmetDeinostVlPPharmFormCodes) {
		this.predmetDeinostVlPPharmFormCodes = predmetDeinostVlPPharmFormCodes;
	}



	public List<SystemClassif> getPredmetDeinostVlPPharmFormClassif() {
		return predmetDeinostVlPPharmFormClassif;
	}



	public void setPredmetDeinostVlPPharmFormClassif(List<SystemClassif> predmetDeinostVlPPharmFormClassif) {
		this.predmetDeinostVlPPharmFormClassif = predmetDeinostVlPPharmFormClassif;
	}






	public boolean isAccess() {
		return access;
	}



	public void setAccess(boolean access) {
		this.access = access;
	}


	public Flash getFlash() {
		return flash;
	}


	public void setFlash(Flash flash) {
		this.flash = flash;
	}

	public VlpVeshtva getVlpVestvaForModalObject() {
		return vlpVestvaForModalObject;
	}
	public void setVlpVestvaForModalObject(VlpVeshtva vlpVestvaForModalObject) {
		this.vlpVestvaForModalObject = vlpVestvaForModalObject;
	}

	public void setVlpLiceForModalObjectProizvoditel(VlpLice vlpLiceForModalObject) {
		this.vlpLiceForModalObjectProizvoditel = vlpLiceForModalObject;
	}
	public VlpLice getVlpLiceForModalObjectProizvoditel() {
		return vlpLiceForModalObjectProizvoditel;
	}
	

	public boolean isShowTextInput() {
		return showTextInput;
	}

	public void setShowTextInput(boolean showTextInput) {
		this.showTextInput = showTextInput;
	}

	public String getTxtCorresp() {
		return txtCorresp;
	}

	public void setTxtCorresp(String txtCorresp) {
		this.txtCorresp = txtCorresp;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public VlpLice getVlpLiceForModalObjectPritezatel() {
		return vlpLiceForModalObjectPritezatel;
	}

	public void setVlpLiceForModalObjectPritezatel(VlpLice vlpLiceForModalObjectPritezatel) {
		this.vlpLiceForModalObjectPritezatel = vlpLiceForModalObjectPritezatel;
	}

	public String getImeProizvoditel() {
		return imeProizvoditel;
	}

	public void setImeProizvoditel(String imeProizvoditel) {
		this.imeProizvoditel = imeProizvoditel;
	}

	public String getImePritezatel() {
		return imePritezatel;
	}

	public void setImePritezatel(String imePritezatel) {
		this.imePritezatel = imePritezatel;
	}

	public Integer getCodeProizvoditel() {
		return codeProizvoditel;
	}

	public void setCodeProizvoditel(Integer codeProizvoditel) {
		this.codeProizvoditel = codeProizvoditel;
	}

	public Integer getCodePritezatel() {
		return codePritezatel;
	}

	public void setCodePritezatel(Integer codePritezatel) {
		this.codePritezatel = codePritezatel;
	}

	public Object[] getLastDoc() {
		return lastDoc;
	}

	public void setLastDoc(Object[] lastDoc) {
		this.lastDoc = lastDoc;
	}

	public VlpPrilagane getVlpPrilagane() {
		return vlpPrilagane;
	}

	public void setVlpPrilagane(VlpPrilagane vlpPrilagane) {
		this.vlpPrilagane = vlpPrilagane;
	}

	public String getVidoveJivotniForNachin() {
		return vidoveJivotniForNachin;
	}

	public void setVidoveJivotniForNachin(String vidoveJivotniForNachin) {
		this.vidoveJivotniForNachin = vidoveJivotniForNachin;
	}

	public List<SystemClassif> getSelectedFarmFormsListSC() {
		return selectedFarmFormsListSC;
	}

	public void setSelectedFarmFormsListSC(List<SystemClassif> selectedFarmFormsListSC) {
		this.selectedFarmFormsListSC = selectedFarmFormsListSC;
	}

	public BigDecimal getDaljimaSuma() {
		return daljimaSuma;
	}

	public void setDaljimaSuma(BigDecimal daljimaSuma) {
		this.daljimaSuma = daljimaSuma;
	}

	public List<Integer> getSelectedFarmFormsList() {
		return selectedFarmFormsList;
	}

	public void setSelectedFarmFormsList(List<Integer> selectedFarmFormsList) {
		this.selectedFarmFormsList = selectedFarmFormsList;
	}

	public List<Integer> getSelectedVidJivotniList() {
		return selectedVidJivotniList;
	}

	public void setSelectedVidJivotniList(List<Integer> selectedVidJivotniList) {
		this.selectedVidJivotniList = selectedVidJivotniList;
	}

	public List<SystemClassif> getSelectedVidJivotniListSC() {
		return selectedVidJivotniListSC;
	}

	public void setSelectedVidJivotniListSC(List<SystemClassif> selectedVidJivotniListSC) {
		this.selectedVidJivotniListSC = selectedVidJivotniListSC;
	}

	public List<Integer> getSelectedNachinPrilageneList() {
		return selectedNachinPrilageneList;
	}

	public void setSelectedNachinPrilageneList(List<Integer> selectedNachinPrilageneList) {
		this.selectedNachinPrilageneList = selectedNachinPrilageneList;
	}

	public List<SystemClassif> getSelectedNachinPrilageneListSC() {
		return selectedNachinPrilageneListSC;
	}

	public void setSelectedNachinPrilageneListSC(List<SystemClassif> selectedNachinPrilageneListSC) {
		this.selectedNachinPrilageneListSC = selectedNachinPrilageneListSC;
	}

	public RefCorrespData getBindingReferent2() {
		return bindingReferent2;
	}

	public void setBindingReferent2(RefCorrespData bindingReferent2) {
		this.bindingReferent2 = bindingReferent2;
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

	public List<SystemClassif> getSelectOpakovkaSC() {
		return selectOpakovkaSC;
	}

	public void setSelectOpakovkaSC(List<SystemClassif> selectOpakovkaSC) {
		this.selectOpakovkaSC = selectOpakovkaSC;
	}

	public List<Integer> getSelectOpakovkaList() {
		return selectOpakovkaList;
	}

	public void setSelectOpakovkaList(List<Integer> selectOpakovkaList) {
		this.selectOpakovkaList = selectOpakovkaList;
	}

	public SystemClassif getSelectedRezimOtpuskaneSC() {
		return selectedRezimOtpuskaneSC;
	}

	public void setSelectedRezimOtpuskaneSC(SystemClassif selectedRezimOtpuskaneSC) {
		this.selectedRezimOtpuskaneSC = selectedRezimOtpuskaneSC;
	}

	public Referent getReferent3() {
		return referent3;
	}

	public void setReferent3(Referent referent3) {
		this.referent3 = referent3;
	}

	public String getTxtRef3() {
		return txtRef3;
	}

	public void setTxtRef3(String txtRef3) {
		this.txtRef3 = txtRef3;
	}

	public int getIsView() {
		return isView;
	}

	public void setIsView(int isView) {
		this.isView = isView;
	}

	public int getMigration() {
		return migration;
	}

	public void setMigration(int migration) {
		this.migration = migration;
	}


}
