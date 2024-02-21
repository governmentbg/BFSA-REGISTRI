package com.ib.babhregs.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.EventDeinostVlp;
import com.ib.babhregs.db.dto.EventDeinostVlpLice;
import com.ib.babhregs.db.dto.EventDeinostVlpPredmet;
import com.ib.babhregs.db.dto.EventDeinostVlpPrvnos;
import com.ib.babhregs.db.dto.EventDeinostVlpPrvnosOpakovka;
import com.ib.babhregs.db.dto.EventDeinostVlpPrvnosSubst;
import com.ib.babhregs.db.dto.EventDeinostVlpVid;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.ObektDeinostLica;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.db.dto.Substance;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.navigation.Navigation;
import com.ib.indexui.system.Constants;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.SysClassifAdapter;
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
public class RegVLP extends IndexUIbean {

	private static final long serialVersionUID = -1807557045298389672L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegVLP.class);
	private SystemData sd;
	private UserData ud;
	private Date decodeDate;
	private transient VpisvaneDAO	daoVp;
	@Inject
	private Flash flash;

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
	 */
	private Referent referent1;
	private RefCorrespData	bindingReferent1;	
	
	/**
	 * заявителя (лицензиант) - code_ref_vpivane от tablica вписване
	 */
	private Referent referent2=new Referent();
	private String txtRef2;
	private Integer ref2Type;
	private Boolean showRadioRef2=false;
	/**
	 * Притежател на серт. -
	 */
	private Referent referent3=new Referent();
	private String txtRef3;
	
	/**
	 * вписване 
	 */
	private Vpisvane vpisvane;
	private boolean access;

	private List<ObektDeinostDeinost> obektDeinList;
	private Date datBegObDeinDein;
	private Date datEndObDeinDein;
	
	private Integer rowObDein=-1;
	private Integer rowObDeinLice=-1;
	private Integer rowVnos=-1;
	private ObektDeinost obektDein;
	
	private EventDeinostVlp eventDeinostVlp;
	private EventDeinostVlpPrvnos eventDeinostVlpPrvnos;
	private EventDeinostVlpPrvnosSubst eventDeinostVlpPrvnosSubst;
	private EventDeinostVlpPrvnosOpakovka parvOpak;
	
	private EventDeinostVlpPredmet eventDeinostVlpPredmet;
	private List<Integer> bolestiCodes;  
	private List<SystemClassif> bolestiClassif;
	
	
	private List<Integer> vidVlpCodes;  
	private List<SystemClassif> vidVlpClassif;
	
	private List<Integer> prednazCodes;  
	private List<SystemClassif> prednazClassif; 
	
	private List<Integer> predmetCodes;  
	private List<SystemClassif> predmetClassif; // заради autocomplete
	
	private EventDeinostVlpLice eventLice=new EventDeinostVlpLice();	
	private String txtLice;
	
	/** Списък на файловете към заявлението за вписване */
	private List<Files> filesList;
	
	/** Информацията за последното заявление към вписването (ако има такова) */
	private Object[] lastDoc;
	
	/** Лица отн. много	 **/
	//tekushtiq currentLice koito dobavqme
	private Referent currentLice;
	private ArrayList<Referent> licaList=new ArrayList<Referent>();
	private Integer tipCurrentVrazkaLice=null;
	private Integer rowLice=-1;
	//code_ref, id ot obekt_dein_lica 
	private HashMap<Integer,Integer> obektLiceRefMap=new HashMap<Integer,Integer>();
	
	
	//tekushtiq lekar koito dobavqme
	private Referent currentLiceObDein;
	private Integer tipCurrentVrazkaLiceObDein=null;
	
	private int isView;
	private int migration;
	
	private Map<Integer, Object> specificsEkatte;
	/**
	 * Дължима сума
	 */
 	private BigDecimal daljimaSuma;
 	
 	private List<SystemClassif> podZaiavList;
	
 	
	@PostConstruct
	public void init() {
		sd = (SystemData) getSystemData();
		ud = getUserData(UserData.class);			
		daoVp = new VpisvaneDAO(getUserData());
		
		specificsEkatte=loadSpecificsEKATTE();

		actionNew();
		
		boolean isOK = false;
		boolean isLocked=false;
		FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
		String param3 = (String) faceletContext.getAttribute("isView"); 
		setIsView(!SearchUtils.isEmpty(param3) ? Integer.valueOf(param3) : 0);// 0 - актуализация; 1 - разглеждане
		
		String paramM = JSFUtils.getRequestParameter("m");
		migration = SearchUtils.isEmpty(paramM) ? BabhConstants.CODE_ZNACHENIE_NE :  0; // Ако е подаден параметър - значи идва от екрана за ръчно въвеждане на миграция
	
		// основен обект  за дейности
		
		Integer registerId = null;
		String paramZaiavlenie = JSFUtils.getRequestParameter("idZ");
		String paramVpisvane = JSFUtils.getRequestParameter("idV");
//		paramZaiavlenie="80204";
//		paramVpisvane="56";

		
		try {
			if ( !SearchUtils.isEmpty(paramVpisvane)){
				
				Integer idVp = Integer.valueOf(paramVpisvane);
				// проверка за заключено вписване. + закл. на текущия обект.
				isLocked = checkAndLockDoc(idVp, BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
				
				if (isLocked) {
					// идваме от актуализация на вписване - подадено е ид на вписване
					initVpisane(idVp);
					
					// ако заявлението е за промяна или заличаване.. винаги позволяваме корекция на данните
					String zaiavChange = JSFUtils.getRequestParameter("change"); 
					if (zaiavChange != null && !zaiavChange.isEmpty()) {
						this.vpisvane.setVpLocked(Constants.CODE_ZNACHENIE_NE);
					}
					
					if (access) {
						loadLastZaiav(true);
						// проверка за заключено вписване!!
						// ако е заключено - някакво съобщение.....
						isOK = loadZaiavl(vpisvane.getIdZaqavlenie(), false);
					}
					
				}else {
					access=false;
				}
				
			} else { 
				if (!SearchUtils.isEmpty(paramZaiavlenie))  { 
				
					// idvame ot zaiavlenieto nishto oshte ne e zapisano
					Integer idZ = Integer.valueOf(paramZaiavlenie);
							
					
					// проверка за заключено вписване. + закл. на текущия обект.
					isLocked = checkAndLockDoc(idZ, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
					if(isLocked) {
						isOK = loadZaiavl(idZ, true); // зареждане на заявлението и лицата - заявителите. опит за парсване.
						// проверка за достъп до заявлението
						registerId = loadOptions(); 
						actionNewVpisvane(registerId);
						JPA.getUtil().runWithClose(() -> access = new DocDAO(ud).hasDocAccess(zaiavlVp, isView==0, sd));
					}else {
						access=false;
					}
					if(access) {
						if(vpisvane == null) {
							
							 // ако идваме от списъкът на  заявленията и ще имаме ново вписване 
							vpisvane = new Vpisvane();
							registerId = loadOptions(); 
							actionNewVpisvane(registerId);
						}
					}
					
				}  else {
					
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Отказан достъп!");
				}
			}	
			
			
			
			
			if(access) {			
				//  Извличане на настройките по вида на документа
				
				actionLoadDifferentParts();
		   		if(isView == 1 ) {
		   			viewMode();
		   		}
		   		checkDaljimaSuma();
		   		
		   		if (this.zaiavlVp.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_VNOS_PROIZV_VLP) || this.zaiavlVp.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_TARG_RAZST_PARALEL_VLP)) {
					this.podZaiavList = new ArrayList<>();
					this.podZaiavList = this.sd.getChildrenOnNextLevel(BabhConstants.CODE_CLASSIF_DOC_VID, this.zaiavlVp.getDocVid(), decodeDate, getCurrentLang());
				}

			} else {
				 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,"Достъпът е отказан!");
			}
		
		} catch (BaseException e) {
			LOGGER.error("Грешка при извличане на последно заявление! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
		 
		 
		 
	}
	
	public void actionNewVpisvane(Integer registerId) {
		
		vpisvane.setIdZaqavlenie(zaiavlVp.getId());
		vpisvane.setRegNomZaqvlVpisvane(zaiavlVp.getRnDoc());
		vpisvane.setDateZaqvlVpis(zaiavlVp.getDocDate());
		if (referent2!=null) {
			vpisvane.setCodeRefVpisvane(referent2.getCode()); // от парсването на заявлнието	
		} 
		if (referent3!=null) {
			vpisvane.setIdLicenziant(referent3.getCode()); // от парсването на заявлнието	
		}
		referent3.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
		
		 
		vpisvane.setIdRegister(registerId); // ако е ново вписване... 
		vpisvane.setLicenziantType(BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_LICE  );
		vpisvane.setCodePage(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP); 
		vpisvane.setStatus(BabhConstants.STATUS_VP_WAITING);
		vpisvane.setDateStatus(new Date());
		vpisvane.setRegistraturaId(zaiavlVp.getRegistraturaId());
		
	}
	
	public void actionNew() {
		this.decodeDate = new Date();
		
		this.filesList = new ArrayList<>();
		obektDeinList=new ArrayList<ObektDeinostDeinost>();
		obektDein=new ObektDeinost();
		try {
			obektDein.setDarj(Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG")));
		} catch (NumberFormatException | DbErrorException e) {
			LOGGER.error("Грешка при сетване на държава! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
		eventLice=new EventDeinostVlpLice();
		// za nark. veshtestva samo i targna edro
		eventLice.setTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_OTG_LICE);	
		eventDeinostVlp=new EventDeinostVlp();
		eventDeinostVlp.setBolestList(new ArrayList<Integer>());										
		eventDeinostVlp.setEventDeinostVlpPredmet(new ArrayList<EventDeinostVlpPredmet>());
		eventDeinostVlpPredmet=new EventDeinostVlpPredmet();				
		eventDeinostVlp.setEventDeinostVlpPrvnos(new ArrayList<EventDeinostVlpPrvnos>());
		eventDeinostVlpPrvnos=new EventDeinostVlpPrvnos();
		eventDeinostVlpPrvnosSubst=new EventDeinostVlpPrvnosSubst();
		this.eventDeinostVlpPrvnos.setEventDeinostVlpPrvnosOpakovka(new ArrayList<EventDeinostVlpPrvnosOpakovka>());
		parvOpak=new EventDeinostVlpPrvnosOpakovka();
		
	}
	
	public void actionLoadDifferentParts() throws DbErrorException {
		if (vpisvane != null) {
			
			if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_EDRO_VLP) {
				tipCurrentVrazkaLice=BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_OTG;
			}
			
			if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_REKLAMI_VLP) {
				ref2Type=BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL;
				showRadioRef2=true;
			}else {
				ref2Type=BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL;
				showRadioRef2=false;
			}
			
			
			if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_NARK_VESHT_VLP) {
				tipCurrentVrazkaLice=BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_OTG_LICE;
			}
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_ACTIVE_VESHT_M)) {
				if (vpisvane.getEventDeinostVlp()!=null) {
					eventDeinostVlp=vpisvane.getEventDeinostVlp();
				}else {
					eventDeinostVlp=new EventDeinostVlp();
				}
			}
			
			//event_deinost_vlp edno.
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_EVENT_DEIN_EDNO)) {
				if (vpisvane.getEventDeinostVlp()!=null) {
					eventDeinostVlp=vpisvane.getEventDeinostVlp();
				}else {
					eventDeinostVlp=new EventDeinostVlp();
				}
			}
			// specifi4no e ...
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_ACTIVE_VESHT_M)) {
				if (eventDeinostVlp==null) {
					eventDeinostVlp=new EventDeinostVlp();
				}
				if (eventDeinostVlp.getEventDeinostVlpPredmet()==null) {
					eventDeinostVlp.setEventDeinostVlpPredmet(new ArrayList<EventDeinostVlpPredmet>());
				}
				if (eventDeinostVlpPredmet==null) {
					eventDeinostVlpPredmet=new EventDeinostVlpPredmet();				
				}
			}
			
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_PREDMET_ONE)) {
				//predmet edno
				if (eventDeinostVlp.getEventDeinostVlpPredmet()!=null && eventDeinostVlp.getEventDeinostVlpPredmet().size()>0) {
					eventDeinostVlpPredmet=eventDeinostVlp.getEventDeinostVlpPredmet().get(0);
				}else {
					eventDeinostVlpPredmet=new EventDeinostVlpPredmet();
				}
			}
			
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_LICE_OTN_EDNO)) {
				// edno lice
				if (eventDeinostVlp.getEventDeinostVlpLice()!=null && eventDeinostVlp.getEventDeinostVlpLice().size()>0) {
					eventLice=eventDeinostVlp.getEventDeinostVlpLice().get(0);
				}else {
					eventLice=new EventDeinostVlpLice();
				}
			}
			
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_LICA_MNOGO)) {
				// mnogo lica
				if (eventDeinostVlp.getEventDeinostVlpLice()!=null && eventDeinostVlp.getEventDeinostVlpLice().size()>=1) {
					setLiceRefMap();
				}
			}
			
			
			//obekt_deinost  EDNO
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_OBEKT_DEIN_EDNO)) {
				// prednaznachenie
				if (eventDeinostVlp.getObektDeinostDeinost()!=null && eventDeinostVlp.getObektDeinostDeinost().size()==1) {
					obektDein=eventDeinostVlp.getObektDeinostDeinost().get(0).getObektDeinost();
					if (obektDein.getPrednaznachenieList()!=null && !obektDein.getPrednaznachenieList().isEmpty()) {
						prednazCodes=new ArrayList<Integer>();
						prednazClassif=new ArrayList<SystemClassif>();
						for (int i = 0; i < obektDein.getPrednaznachenieList().size(); i++) {
							 prednazCodes.add(obektDein.getPrednaznachenieList().get(i));
							 
							 SystemClassif scItem = new SystemClassif();
							 scItem.setCodeClassif(BabhConstants.CODE_CLASSIF_PREDN_OBEKT);
							 scItem.setCode(obektDein.getPrednaznachenieList().get(i));
							 scItem.setTekst(getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_PREDN_OBEKT, obektDein.getPrednaznachenieList().get(i), getCurrentLang(), new Date()));
							 prednazClassif.add(scItem);
							
						}
					}
				}else {
					obektDein=new ObektDeinost();
					try {
						obektDein.setDarj(Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG")));
					} catch (NumberFormatException | DbErrorException e) {
						LOGGER.error("Грешка при сетване на държава! ", e);
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
					}
				}
			}
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_OB_DEIN_LICA_M_TABLE)) {
				// mnogo lica
				if (obektDein.getObektDeinostLica()!=null && obektDein.getObektDeinostLica().size()>=1) {
					setLiceRefMap();
				}
			}
			
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_OBEKT_DEIN_M)) {
				//EKRAN_ATTR_VLP_OBEKT_DEIN_M
				obektDeinList=new ArrayList<ObektDeinostDeinost>();
				if (eventDeinostVlp.getObektDeinostDeinost()!= null && eventDeinostVlp.getObektDeinostDeinost().size()>=1) {
					for (int i = 0; i < eventDeinostVlp.getObektDeinostDeinost().size(); i++) {
						obektDeinList.add(eventDeinostVlp.getObektDeinostDeinost().get(i));
					}
				}
			}
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_EVENT_VID_М)) {
				//vid vlp ot event_deinost_vlp_vid
				if (eventDeinostVlp.getEventDeinostVlpVid()!=null && eventDeinostVlp.getEventDeinostVlpVid().size()>0) {
					vidVlpCodes=new ArrayList<Integer>();
					vidVlpClassif=new ArrayList<SystemClassif>();
					for (int i = 0; i < eventDeinostVlp.getEventDeinostVlpVid().size(); i++) {
						vidVlpCodes.add(eventDeinostVlp.getEventDeinostVlpVid().get(i).getVid());
						 
						 SystemClassif scItem = new SystemClassif();
						 scItem.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_DEINOST);
						 scItem.setCode(eventDeinostVlp.getEventDeinostVlpVid().get(i).getVid());
						 scItem.setTekst(getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_VID_DEINOST, eventDeinostVlp.getEventDeinostVlpVid().get(i).getVid(), getCurrentLang(), new Date()));
						 vidVlpClassif.add(scItem);
					}
				}
			}
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_PREDMET_MANY)) {
				//predmet mnojestveno.
				if(eventDeinostVlp.getEventDeinostVlpPredmet() != null && !eventDeinostVlp.getEventDeinostVlpPredmet().isEmpty()) {
					 predmetClassif = new ArrayList<>();
					 predmetCodes = new ArrayList<>();
					 for(EventDeinostVlpPredmet item : eventDeinostVlp.getEventDeinostVlpPredmet() ) {
						 //tova e zashtoto ne vinagi imame klasif ponqkoga rabotim samo s drugi koloni v bazta.
						 if (item.getCodeClassif()!=null) {
							 predmetCodes.add(item.getPredmet());
							 
							 SystemClassif scItem = new SystemClassif();
							 scItem.setCodeClassif(item.getCodeClassif());
							 scItem.setCode(item.getPredmet());
							 scItem.setTekst(getSystemData().decodeItem(item.getCodeClassif(), item.getPredmet(), getCurrentLang(), new Date()));
							 predmetClassif.add(scItem);
						}
						 
					 }
					
				}
			}
			
			
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_BOLESTI_MNOGO)) {
				//болести mnojestveno.
				if(eventDeinostVlp.getBolestList() != null && !eventDeinostVlp.getBolestList().isEmpty()) {
					bolestiClassif = new ArrayList<>();
					bolestiCodes = new ArrayList<>();
					for(Integer item : eventDeinostVlp.getBolestList() ) {
						bolestiCodes.add(item);
						
						SystemClassif scItem = new SystemClassif();
						scItem.setCodeClassif(BabhConstants.CODE_CLASSIF_BOLESTI);
						scItem.setCode(item);
						scItem.setTekst(getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_BOLESTI, item, getCurrentLang(), new Date()));
						bolestiClassif.add(scItem);
					}
					
				}
			}
			 
			
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_PRVNOS_PROIZV_MANY_TABLE)) {
				if (eventDeinostVlp==null) {
					eventDeinostVlp=new EventDeinostVlp();								
				}
				if (eventDeinostVlp.getEventDeinostVlpPrvnos()==null) {
					eventDeinostVlp.setEventDeinostVlpPrvnos(new ArrayList<EventDeinostVlpPrvnos>());
				}			
				if (eventDeinostVlpPrvnos==null) {
					eventDeinostVlpPrvnos=new EventDeinostVlpPrvnos();
				}
				if (eventDeinostVlpPrvnosSubst==null) {
					eventDeinostVlpPrvnosSubst=new EventDeinostVlpPrvnosSubst();
				}
			}
			
		}
		
		if (eventDeinostVlp.getEventDeinostVlpVid()==null || eventDeinostVlp.getEventDeinostVlpVid().size()==0)  {
			EventDeinostVlpVid ev=new EventDeinostVlpVid();
			eventDeinostVlp.setEventDeinostVlpVid(new ArrayList<EventDeinostVlpVid>());
			if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_DREBNO_VLP) {				
				ev.setVid(BabhConstants.CODE_ZNACHENIE_DEIN_VLP_DREBNO);
			}
			if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_EDRO_VLP) {				
				ev.setVid(BabhConstants.CODE_ZNACHENIE_DEIN_VLP_TR_EDRO);
			}
			if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_VNOS_PROIZV_VLP) {
				if (zaiavlVp.getDocPodVid()!=null) {
					if (zaiavlVp.getDocPodVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_PROIZV_VLP) {
						ev.setVid(BabhConstants.CODE_ZNACHENIE_DEIN_VLP_PROIZV);	
					}
					if (zaiavlVp.getDocPodVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_VNOS_VLP) {
						ev.setVid(BabhConstants.CODE_ZNACHENIE_DEIN_VLP_VNOS);	
					}
				}
			}
			
			if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_INVITRO_VLP) {				
				ev.setVid(BabhConstants.CODE_ZNACHENIE_DEIN_VLP_INVITRO);
			}
			if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_REKLAMI_VLP) {				
				ev.setVid(BabhConstants.CODE_ZNACHENIE_DEIN_VLP_REKLAMA);
			}
			if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_TARG_RAZST_PARALEL_VLP) {
				if (zaiavlVp.getDocPodVid()!=null) {
					if (zaiavlVp.getDocPodVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_TARG_RAZST_VLP) {				
						ev.setVid(BabhConstants.CODE_ZNACHENIE_DEIN_VLP_RAZSTOQNIE);
					}
					if (zaiavlVp.getDocPodVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_PARALELNA_VLP) {				
						ev.setVid(BabhConstants.CODE_ZNACHENIE_DEIN_VLP_PARALEL_TARG);
					}	
				}
				
			}
			
			
			
			eventDeinostVlp.getEventDeinostVlpVid().add(ev);
		}
		
		
		
		
	}
	
	/**
	* При смяна на вида позаявление
	* 1. добавя дейността в обекта
	*/
	public void actionChangePodvid() {
		if (eventDeinostVlp.getEventDeinostVlpPredmet()!=null) {
			eventDeinostVlp.getEventDeinostVlpPredmet().clear();	
		}
		
		eventDeinostVlpPredmet=new EventDeinostVlpPredmet();
		vpisvane.setEventDeinostVlp(new EventDeinostVlp());
		

		
		try {
			actionLoadDifferentParts();
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при работа с базата данни! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}
	
	/** 
	 *   1 само области; 2 - само общини; 3 - само населени места; без специфики - всикчи
	 * @return
	 */
	public Map<Integer, Object> loadSpecificsEKATTE() {

		return Collections.singletonMap(SysClassifAdapter.EKATTE_INDEX_TIP, 3);
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
	 * Смяна на таб
	 * @param event
	 */
    public void onTabChange(TabChangeEvent<?> event) {
	   	if(event != null) {
	   		if (LOGGER.isDebugEnabled()) {
	   			LOGGER.debug("onTabChange active Tab: {}", event.getTab().getId());
	   		}
	
			String activeTab =  event.getTab().getId();
			if (activeTab.equals("danniVpisvaneTab") || activeTab.equals("docVpisvaneTab")|| activeTab.equals("etapiVpisvaneTab")) {
				getFlash().put("beanName", "regVLP"); // задъжлително се подава името на бийна, ако отиваме към таба със статусите
			} else if(activeTab.equals("osnovniDanniTab") && Objects.equals(zaiavlVp.getPayType(), BabhConstants.CODE_ZNACHENIE_PAY_TYPE_FLOAD)) {
				checkDaljimaSuma(); // ако е с плаваща тарифа и тя случйно е променена - да се упдейтне екрана
			} 
//			else if(activeTab.equals("osnovniDanniTab")) {
//				
//			} 
			
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
		}finally {
			JPA.getUtil().closeConnection();
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
	 * зарежда данни за вписванем ако е подадено ид на вписване  
	 * @param idVp
	 */
	private boolean initVpisane(Integer idVp) {
		boolean rez = false;
		try {
			
			
			JPA.getUtil().runInTransaction(()-> {
				this.vpisvane =  daoVp.findById(Integer.valueOf(idVp));				
			});
			
			if (vpisvane != null) {
				// проверка за достъп до впсиване							 
				JPA.getUtil().runWithClose(() -> access = daoVp.hasVpisvaneAccess(vpisvane, getIsView()==0, sd));
				rez = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при зареждане на вписване и обектите към него", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
		}
		 
			
		return rez;
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
				// за ново заявление
				// ---------------- Start of Vassil Change ---------------------
				EgovContainer container = new EgovContainer();
				try {
					container = new EFormUtils().convertEformToVpisvane(idZ, sd);
				} catch (Exception e1) {
					// да заредя все пак заявлението!!!
					JPA.getUtil().runWithClose(() -> this.zaiavlVp =   new DocDAO(ud).findById(Integer.valueOf(idZ)));
					 
					LOGGER.error("Грешка при парсване на заявление - ВЛЗ! ", e1);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
							"Грешка при парсване на заявление! Данните не могат да бъдат заредени от прикачения файл!");
					rez=false;
				}
				if(container.doc != null) {
				   this.zaiavlVp = container.doc;
				   loadFilesList(zaiavlVp.getId());
				}
				this.vpisvane = container.vpisvane;
				this.referent2 = container.ref2;
				//Още го няма ама би трябвало така да стане.
				this.referent3 = container.ref3;
				referent1 = container.ref1;
				
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
				
				if (referent3 == null) {
					referent3 = new Referent();
					referent3.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
				}
				
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
				JPA.getUtil().runInTransaction(() -> this.zaiavlVp =  daoDoc.findById(Integer.valueOf(idZ)));
				 
				if (zaiavlVp != null) {
					
					
					loadFilesList(zaiavlVp.getId());
					
					if(zaiavlVp.getCodeRefCorresp() != null) {
						JPA.getUtil().runWithClose(() -> referent1 = new ReferentDAO(ud).findByCodeRef(zaiavlVp.getCodeRefCorresp()));
						referent1.setKachestvoLice(zaiavlVp.getKachestvoLice());  
					}
					Integer ref2 = null;
					if(vpisvane != null){
						// актуализаиция на вписване
						ref2 = vpisvane.getCodeRefVpisvane(); 
					}
					Integer ref3 = null;
					if(vpisvane != null){
						// актуализаиция на вписване
						ref3 = vpisvane.getIdLicenziant(); 
					}
					
					if(referent1 == null) {
						referent1 = new Referent();
						referent1.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
					}
					referent2 = new Referent();
					if(ref2 != null) {
						referent2 = new ReferentDAO(ud).findByCodeRef(ref2);
					}
					referent3 = new Referent();
					if(ref3 != null) {
						referent3 = new ReferentDAO(ud).findByCodeRef(ref3);
					}
					
					
				}
			}
		} catch (Exception e) {
			rez=false;
			LOGGER.error("Грешка при зареждане на заявление", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
		}
	  
	  
	 
		
		return rez;
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
	
	/***
	 * ИЗВЛИЧА ПОСЛЕДНОТО ЗАЯВЛЕНИЕ КЪМ ДАДЕНО ВПИСВАНЕ
	 * 
	 * @param exludeFirst - ако е true игнорира заявлението за първоначално вписване
	 * */
	private void loadLastZaiav(boolean exludeFirst) {
		try {
			
			lastDoc = this.daoVp.findLastIncomeDoc(this.vpisvane.getId(), exludeFirst);

		} catch (BaseException e) {
			LOGGER.error("Грешка при извличане на последно заявление! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}
	
	public String actionGotoLastZaiav() {
		return "babhZaiavView.xhtml?faces-redirect=true&idObj=" + this.lastDoc[0];
	}
	
	/**
	 *   Извличане на настройките по вида на документа
	 * @return ид на регистъра
	 */ 
	private Integer loadOptions() {		
		Integer docVid=zaiavlVp.getDocVid();
//		if (docVid.equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_VNOS_PROIZV_VLP) || docVid.equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_TARG_RAZST_PARALEL_VLP)) {
//			docVid=zaiavlVp.getDocPodVid();
//		}
		 Integer registerId = sd.getRegisterByVidDoc().get(docVid); 
		 registerOptions = sd.getRoptions().get(registerId); // всички настройки за регистъра
		 List<RegisterOptionsDocsIn> lstDocsIn = registerOptions.getDocsIn(); // само входните док. за регистъра
		 docOpt = new RegisterOptionsDocsIn();
		 for(RegisterOptionsDocsIn item : lstDocsIn ) {
			if ((item.getVidDoc()).equals(docVid)) {
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
	
	
	  
	
	
//	/**
//	 * конкретни видове дейности за регистъра и заявлението
//	 */
//	private void loadDeinJivVid() {
//		 EventDeinJivVid deinJivVid;
//		 List<EventDeinJivVid> tmpDein = new ArrayList<>();
//		 if(docOpt.getDocsInActivity() != null && !docOpt.getDocsInActivity().isEmpty()) {
//			 for(RegisterOptionsDocinActivity item : docOpt.getDocsInActivity() ) {
//				 deinJivVid = new EventDeinJivVid();
//				 deinJivVid.setVid(item.getActivityId());
//				 tmpDein.add(deinJivVid); //ид на дейността - кога ще се сетне?
//			 }
//		 }
//		 eventDeinostVlp.setEventDeinJivVid(tmpDein);
//	}
	
	/**
	* занулява полетата при смяна на държава
	*/
	public void  actionChangeCountry() {
		this.obektDein.setNasMesto(null);		
		this.obektDein.setObl(null);
		this.obektDein.setObsht(null);
		this.obektDein.setAddress(null);
		this.obektDein.setPostCode(null);
		 
	 }
	
	
	 

	
	public void actionSave() {
		if(checkDataVp()) { 			
			try {
				
				
				boolean newVp = this.vpisvane.getId() == null;
				
//				List<String> ocrDocs = this.document.getOcr(this.filesList);
			
				saveVp(newVp);
			
				if(newVp) {// само за нов документ	
					
					// заключване на вписване.
					lockObj(this.vpisvane.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
					
				}
				
				setLiceRefMap();
				
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
	 * 
	 * @return
	 */
	public boolean checkDataVp() {
		boolean save=true;
		
		
		referent1 = getBindingReferent1().getRef();
		if( SearchUtils.isEmpty(referent1.getRefName())){
			save = false;
			JSFUtils.addMessage("vlpTargForm:registerTabs:licеPоdatel:nameCorr",FacesMessage.SEVERITY_ERROR,"Въведете име на представляващото лице");	
		} 
		if(vpisvane.getFromМigr() == null || Objects.equals(vpisvane.getFromМigr(),BabhConstants.CODE_ZNACHENIE_MIGR_NO)) {
			if( SearchUtils.isEmpty(referent1.getFzlEgn()) && SearchUtils.isEmpty(referent1.getFzlLnc()) && SearchUtils.isEmpty(referent1.getNflEik()) ){ 
				save = false;
				JSFUtils.addMessage("vlpTargForm:registerTabs:licеPоdatel:egn",FacesMessage.SEVERITY_ERROR,"Въведете ЕГН или ЛНЧ на представляващото лице!");
			}
		}
		if( referent1.getKachestvoLice() == null ){   
			save = false;
			JSFUtils.addMessage("vlpTargForm:registerTabs:licеPоdatel:kachestvoZaqvitel",FacesMessage.SEVERITY_ERROR,"Посочете качество на лицето!");
		}

		referent2 = ((RefCorrespData)FacesContext.getCurrentInstance().getViewRoot().findComponent("vlpTargForm").findComponent("registerTabs:liceZaqvitel")).getRef();
		if (referent2.getRefName()==null || referent2.getRefName().isEmpty()) {
			save=false;
			JSFUtils.addMessage("vlpTargForm:registerTabs:liceZaqvitel:nameCorr", FacesMessage.SEVERITY_ERROR, "Моля, въведете имена на заявителя!");
		}
		if(referent2.getContactEmail()!=null && !"".equals(referent2.getContactEmail())
				&& !ValidationUtils.isEmailValid(referent2.getContactEmail())) {
			JSFUtils.addMessage("vlpTargForm:registerTabs:liceZaqvitel:contactEmail",FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, "general.validE-mail"));
			
			save = false;
		}
		if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_REKLAMI_VLP) {
			if ((FacesContext.getCurrentInstance().getViewRoot().findComponent("vlpTargForm").findComponent("registerTabs:licePritejatel"))!=null) {
				referent3 = ((RefCorrespData)FacesContext.getCurrentInstance().getViewRoot().findComponent("vlpTargForm").findComponent("registerTabs:licePritejatel")).getRef();
				if (referent3.getRefLatin()==null || referent3.getRefLatin().isEmpty()) {
					save=false;
					JSFUtils.addMessage("vlpTargForm:registerTabs:licePritejatel:nameCorrEN", FacesMessage.SEVERITY_ERROR, "Моля, въведете имена на притежател!");
				}	
			}
		}else {
			referent3=null;
		}
		
		//ИНВИТРО ПРОВЕРКИ
		if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_INVITRO_VLP) {
			if (eventDeinostVlpPredmet!=null && (eventDeinostVlpPredmet.getNaimenovanie()==null || eventDeinostVlpPredmet.getNaimenovanie().isEmpty())) {
				save = false;
				JSFUtils.addMessage("vlpTargForm:registerTabs:naimKirilica",FacesMessage.SEVERITY_ERROR, "Моля, въведете Инвитро-диагностично ветеринарномедицинско средство!");
			}
			Boolean foundProizv=false;
			Boolean foundPritLic=false;
			for (int i = 0; i < licaList.size(); i++) {
				if (licaList.get(i).getDbEmplPosition()!=null) {
					if(licaList.get(i).getDbEmplPosition()==BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_PROIZVODITEL
						&& (licaList.get(i).getDateDo()==null || this.vpisvane.getDateReg()==null || licaList.get(i).getDateDo().after(this.vpisvane.getDateReg()))) {
						foundProizv=true;
					}
					if(licaList.get(i).getDbEmplPosition()==BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_PREDL_PRITEJ_LICENZ
							&& (licaList.get(i).getDateDo()==null || this.vpisvane.getDateReg()==null || licaList.get(i).getDateDo().after(this.vpisvane.getDateReg()))) {
						foundPritLic=true;
					}
					
				}
			}
			if (!foundProizv) {
				save = false;
				JSFUtils.addMessage("vlpTargForm:registerTabs:tblLekari",FacesMessage.SEVERITY_ERROR, "Моля, въведете действащ \"Производител\"!");
			}
//			if (!foundPritLic) {
//				save = false;
//				JSFUtils.addMessage("vlpTargForm:registerTabs:tblLekari",FacesMessage.SEVERITY_ERROR, "Моля, въведете действащ \"Предлаган притежател на лиценз\"!");
//			}
		}
		
		// DREBNO VLP
		if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_DREBNO_VLP) {
			Boolean foundUpr=false;
			for (int i = 0; i < licaList.size(); i++) {
				if (licaList.get(i).getDbEmplPosition()!=null 
						&& licaList.get(i).getDbEmplPosition().equals(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL)
						&& (licaList.get(i).getDateDo()==null || this.vpisvane.getDateReg()==null || licaList.get(i).getDateDo().after(this.vpisvane.getDateReg()))) {
					foundUpr=true;
					break;
				}
			}
			if (!foundUpr) {
				save = false;
				JSFUtils.addMessage("vlpTargForm:registerTabs:tblLekari",FacesMessage.SEVERITY_ERROR, "Моля, въведете действащ управител!");
			}
			
		}
		
		//EDRO VLP
		if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_EDRO_VLP) {
			
			if (predmetCodes==null || predmetCodes.size()==0) {
				save = false;
				JSFUtils.addMessage("vlpTargForm:registerTabs:vlpPredmets",FacesMessage.SEVERITY_ERROR, "Моля, изберете Фармакологични групи, с които ще се търгува!");
			}
			
		}
		
		return save;
	}
	
	
	private void saveVp(boolean newVp) throws BaseException {
				 
		
		
		//предмет на дейността
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_PREDMET_MANY)) {
			List<EventDeinostVlpPredmet> eventDeinVlpPredmet = new ArrayList<>();
			if (predmetClassif != null && !predmetClassif.isEmpty() ) {
				EventDeinostVlpPredmet tmpVlpPr;
				for(SystemClassif item : predmetClassif ) {
					tmpVlpPr = new EventDeinostVlpPredmet();
					tmpVlpPr.setPredmet(item.getCode());
					tmpVlpPr.setCodeClassif(item.getCodeClassif());
					eventDeinVlpPredmet.add(tmpVlpPr);
				}
			}
			if (eventDeinostVlp==null) {
				eventDeinostVlp=new EventDeinostVlp();
			}
			eventDeinostVlp.setEventDeinostVlpPredmet(eventDeinVlpPredmet); 
		}
		//болести
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_BOLESTI_MNOGO)) {
			
			if (eventDeinostVlp==null) {
				eventDeinostVlp=new EventDeinostVlp();
			}
			eventDeinostVlp.setBolestList(bolestiCodes); 
		}
		//predmet EDNO
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_PREDMET_ONE)) {
			if (eventDeinostVlpPredmet!=null) {
				if (eventDeinostVlp==null) {
					eventDeinostVlp=new EventDeinostVlp();
				}
				if (eventDeinostVlp.getEventDeinostVlpPredmet()==null) {
					eventDeinostVlp.setEventDeinostVlpPredmet(new ArrayList<EventDeinostVlpPredmet>()); 
				}else {
					eventDeinostVlp.getEventDeinostVlpPredmet().clear();
				}
				eventDeinostVlp.getEventDeinostVlpPredmet().add(eventDeinostVlpPredmet);
			}
		}
		
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_EVENT_VID_М)) {
			// vid deinost s VLP - event_deinost_vlp_vid
			if (vidVlpCodes != null && !vidVlpCodes.isEmpty() ) {
				EventDeinostVlpVid tmpVlpVid;
				if (eventDeinostVlp.getEventDeinostVlpVid()==null) {
					eventDeinostVlp.setEventDeinostVlpVid(new ArrayList<EventDeinostVlpVid>());
				}else {
					eventDeinostVlp.getEventDeinostVlpVid().clear();
				}
				for(Integer item : vidVlpCodes ) {
					tmpVlpVid = new EventDeinostVlpVid();
					tmpVlpVid.setVid(item);
					eventDeinostVlp.getEventDeinostVlpVid().add(tmpVlpVid);
				}
			}
		}
		
		
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_OBEKT_DEIN_EDNO)) {
			//prednaznachenie obekt dein
			if (prednazCodes != null && !prednazCodes.isEmpty() ) {
				obektDein.setPrednaznachenieList(prednazCodes);
			}
		}
		
		
		// lice imashto otn. edno
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_LICE_OTN_EDNO)) {
			eventDeinostVlp.getEventDeinostVlpLice().clear();
			if (eventLice!=null) {
				eventDeinostVlp.getEventDeinostVlpLice().add(eventLice);
			}
		}
		
		
		// lice imashto otn. mnogo tuk vlizat v event_vlp_lice
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_LICA_MNOGO)) {
			if (eventDeinostVlp.getEventDeinostVlpLice()==null) {
				eventDeinostVlp.setEventDeinostVlpLice(new ArrayList<EventDeinostVlpLice>());
			}
			eventDeinostVlp.getEventDeinostVlpLice().clear();
			for (int i = 0; i < getLicaList().size(); i++) {
				EventDeinostVlpLice odl=new EventDeinostVlpLice();
				
				odl.setReferent(getLicaList().get(i));
				odl.setTipVraz(getLicaList().get(i).getDbEmplPosition());
				
				if (obektLiceRefMap.containsKey(getLicaList().get(i).getCode())) {
					odl.setId(obektLiceRefMap.get(getLicaList().get(i).getCode()));
				}
				eventDeinostVlp.getEventDeinostVlpLice().add(odl);
			}
		}
		// lice imashto otn. mnogo tuk vlizat v obekt_dein_lica
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_OB_DEIN_LICA_M_TABLE)) {
			if (obektDein.getObektDeinostLica()==null) {
				obektDein.setObektDeinostLica(new ArrayList<ObektDeinostLica>());
			}
			obektDein.getObektDeinostLica().clear();
			for (int i = 0; i < getLicaList().size(); i++) {
				ObektDeinostLica odl=new ObektDeinostLica();
				
				odl.setReferent(getLicaList().get(i));
				odl.setRole(getLicaList().get(i).getDbEmplPosition());
				odl.setDateBeg(getLicaList().get(i).getDateBeg());
				odl.setDateEnd(getLicaList().get(i).getDateEnd());
				if (obektLiceRefMap.containsKey(getLicaList().get(i).getCode())) {
					odl.setId(obektLiceRefMap.get(getLicaList().get(i).getCode()));
				}
				obektDein.getObektDeinostLica().add(odl);
			}
		}
 
		
		//event_deinost_vlp - edno 
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_EVENT_DEIN_EDNO)) {
			vpisvane.setEventDeinostVlp(eventDeinostVlp);
		}
		
		//obekt deinost - vliza v event_deinost_vlp - obekt deinost deinost
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_OBEKT_DEIN_EDNO)) {
			if (this.vpisvane.getEventDeinostVlp().getObektDeinostDeinost()==null) {
				this.vpisvane.getEventDeinostVlp().setObektDeinostDeinost(new ArrayList<>());	
			}
			if (this.vpisvane.getEventDeinostVlp().getObektDeinostDeinost().size()>0) {
				this.vpisvane.getEventDeinostVlp().getObektDeinostDeinost().get(0).setObektDeinost(this.obektDein);
			}else {
				ObektDeinostDeinost tmpObekt = new ObektDeinostDeinost();
				tmpObekt.setObektDeinost(this.obektDein);
				this.vpisvane.getEventDeinostVlp().getObektDeinostDeinost().add(tmpObekt);
			}
			
			
		}
		
		//obekt deinost - МНОГО vliza v event_deinost_vlp - obekt deinost deinost
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_OBEKT_DEIN_M)) {
			if (this.vpisvane.getEventDeinostVlp().getObektDeinostDeinost()==null) {
				this.vpisvane.getEventDeinostVlp().setObektDeinostDeinost(new ArrayList<>());				
			}else {
				this.vpisvane.getEventDeinostVlp().getObektDeinostDeinost().clear();
			} 
			for (int i = 0; i < obektDeinList.size(); i++) {
				this.vpisvane.getEventDeinostVlp().getObektDeinostDeinost().add(this.obektDeinList.get(i));
			}
		}
		 
		
		zaiavlVp.setKachestvoLice(referent1.getKachestvoLice());
//		if(podatelBean == null) {
//			podatelBean = (PodatelBean) JSFUtils.getManagedBean("podatelBean");
//		}
//		zaiavlVp.setKachestvoLice(podatelBean.getKachestvoLice());
		zaiavlVp.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN); // от класификация 	CODE_CLASSIF_DOC_STATUS(120)
//		zaiavlVp.setStatusDate(new Date());
		
		
		
		
		
		JPA.getUtil().runInTransaction(() -> { 
			//vpObj.setCountFiles(filesList == null ? 0 : filesList.size());
			if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_INVITRO_VLP) {				
				this.daoVp.saveWithVlpLiceLicenziant(vpisvane, zaiavlVp, referent1, referent2, sd);
			}else {
				this.vpisvane= this.daoVp.save(vpisvane, zaiavlVp, referent1, referent2, referent3, sd);	
			}
			
			
			
			actionLoadDifferentParts();
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
	
	public void someActionAfterSelection() {
	}
	
	/*********************** PR VNOS I AKT SUBST **********************/
	public void actionNewVnosM() {
		this.eventDeinostVlpPrvnos=new EventDeinostVlpPrvnos();
		this.eventDeinostVlpPrvnos.setEventDeinostVlpPrvnosOpakovka(new ArrayList<EventDeinostVlpPrvnosOpakovka>());
		this.rowVnos=-1;
	}
	public void actionRemoveVnosM(Integer rowNum) {
		this.eventDeinostVlp.getEventDeinostVlpPrvnos().remove(this.eventDeinostVlp.getEventDeinostVlpPrvnos().get(rowNum));
	}
	
	public void actionEditVnos(EventDeinostVlpPrvnos vn, Integer idx) {		
		eventDeinostVlpPrvnos=vn;
		this.rowVnos=idx;
		PrimeFaces.current().executeScript("PF('mVnos').show();");
	}
	
	
	public void actionReturnVnos() {	
		if (eventDeinostVlp.getEventDeinostVlpPrvnos()==null) {
			eventDeinostVlp.setEventDeinostVlpPrvnos(new ArrayList<EventDeinostVlpPrvnos>());
		}
		if (rowVnos==-1) {
			
			eventDeinostVlp.getEventDeinostVlpPrvnos().add(eventDeinostVlpPrvnos);			
		}else {
			eventDeinostVlp.getEventDeinostVlpPrvnos().set(rowVnos, eventDeinostVlpPrvnos);
		}
		rowVnos=-1;
 
		PrimeFaces.current().executeScript("PF('mVnos').hide();");
	}
	
	/***************** aktivni subs ************/
	public void actionNewActiveSubst() {
		this.eventDeinostVlpPrvnosSubst=new EventDeinostVlpPrvnosSubst();
		this.eventDeinostVlpPrvnosSubst.setVidIdentifier(new Substance());
	}
	public void actionRemoveActiveSubst(Integer rowNum) {
		this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst().remove(this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst().get(rowNum));
	}

	
	
	
	
	public void actionAddActiveSubstM() {		
		Boolean save=true;
		if (eventDeinostVlpPrvnosSubst.getVid()!=null) {
			EventDeinostVlpPrvnosSubst tmp=new EventDeinostVlpPrvnosSubst();
			tmp.setVid(eventDeinostVlpPrvnosSubst.getVid());
			tmp.setDoza(eventDeinostVlpPrvnosSubst.getDoza());
			tmp.setDopInfo(eventDeinostVlpPrvnosSubst.getDopInfo());
			
			if (this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst()==null) {
				this.eventDeinostVlpPrvnos.setEventDeinostVlpPrvnosSubst(new ArrayList<EventDeinostVlpPrvnosSubst>());
			}
			boolean add=true;
			for (int i = 0; i < this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst().size(); i++) {
				if (this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst().get(i).getVid()==tmp.getVid()) {
					add=false;
					this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst().set(i, tmp);
					break;
				}
			}
			if (add) {
				this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst().add(tmp);
			}
		}else {
			if (eventDeinostVlpPrvnosSubst.getVidIdentifier()!=null && eventDeinostVlpPrvnosSubst.getVidIdentifier().getIdentifier()!=null) {
				EventDeinostVlpPrvnosSubst tmp=new EventDeinostVlpPrvnosSubst();
				tmp.setVidIdentifier(eventDeinostVlpPrvnosSubst.getVidIdentifier());
				tmp.setDoza(eventDeinostVlpPrvnosSubst.getDoza());
				tmp.setDopInfo(eventDeinostVlpPrvnosSubst.getDopInfo());
				
				if (this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst()==null) {
					this.eventDeinostVlpPrvnos.setEventDeinostVlpPrvnosSubst(new ArrayList<EventDeinostVlpPrvnosSubst>());
				}
				boolean add=true;
				for (int i = 0; i < this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst().size(); i++) {
					if (this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst().get(i).getVidIdentifier()==tmp.getVidIdentifier()) {
						add=false;
						this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst().set(i, tmp);
						break;
					}
				}
				if (add) {
					this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosSubst().add(tmp);
				}
			}else {
				save=false;
				JSFUtils.addMessage("vlpTargForm:registerTabs:findSubstanceInVlpProizv:selectedSubstanceNameComp",FacesMessage.SEVERITY_ERROR,"Въведете изберете вид!");
			}
		}
		if (save) {
			PrimeFaces.current().executeScript("PF('mActiveSubstMD').hide();");
		}
	}
	
	
	/*************************** Parvichna opakovka *****************/
	public void actionNewParvOpak() {
		parvOpak=new EventDeinostVlpPrvnosOpakovka();		
	}
	public void actionRemoveParvOpak(Integer rowNum) {
		this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosOpakovka().remove(this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosOpakovka().get(rowNum));
	}

	
	
	
	
	public void actionAddParvOpak() {		
		
		if (parvOpak!=null && parvOpak.getOpakovka()!=null) {
			boolean add=true;
			for (int i = 0; i < this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosOpakovka().size(); i++) {
				if (this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosOpakovka().get(i).getOpakovka()==parvOpak.getOpakovka()) {
					add=false;
					this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosOpakovka().set(i, parvOpak);
					break;
				}
			}
			if (add) {
				this.eventDeinostVlpPrvnos.getEventDeinostVlpPrvnosOpakovka().add(parvOpak);
			}
			PrimeFaces.current().executeScript("PF('mParvichnaOpakovka').hide();");
		}else {
			JSFUtils.addMessage("vlpTargForm:registerTabs:parvOpakDropMenu",FacesMessage.SEVERITY_ERROR,"Моля, въведете първична опаковка!");
			
		}
		 
	}
	
	
	
	
	/*********************** Лица отн. много ****************/
	public void actionNewLice() {
		currentLice=new Referent();
		currentLice.setAddress(new ReferentAddress());
		currentLice.getAddress().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN);
		currentLice.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
		if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_INVITRO_VLP) {
			currentLice.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
		}
		
		try {
			currentLice.getAddress().setAddrCountry(Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG")));
		} catch (NumberFormatException | DbErrorException e) {
			LOGGER.error("Грешка при сетване на държава! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
		currentLice.setDbEmplPosition(tipCurrentVrazkaLice);
		this.setRowLice(-1);
	}
	public void actionEditLice(Referent l, Integer idx) {
		currentLice=l;
		this.setRowLice(idx);
		PrimeFaces.current().executeScript("PF('mLekarD').show();");
	}
	/**
	 * 
	 */
	public void setLiceRefMap() {
		obektLiceRefMap.clear();
		licaList.clear();
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_OB_DEIN_LICA_M_TABLE)) {
			for (int i = 0; i < obektDein.getObektDeinostLica().size(); i++) {
				Referent r=obektDein.getObektDeinostLica().get(i).getReferent();
				if (r!=null) {
					r.setDbEmplPosition(obektDein.getObektDeinostLica().get(i).getRole());
					r.setDateBeg(obektDein.getObektDeinostLica().get(i).getDateBeg());
					r.setDateEnd(obektDein.getObektDeinostLica().get(i).getDateEnd());
					licaList.add(r);
					obektLiceRefMap.put(obektDein.getObektDeinostLica().get(i).getCodeRef(), obektDein.getObektDeinostLica().get(i).getId());	
				}
									
			}	
		}
		if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_LICA_MNOGO)) {
			if (vpisvane!=null && vpisvane.getEventDeinostVlp().getEventDeinostVlpLice()!=null) {
				for (int i = 0; i < vpisvane.getEventDeinostVlp().getEventDeinostVlpLice().size(); i++) {
					Referent r=vpisvane.getEventDeinostVlp().getEventDeinostVlpLice().get(i).getReferent();
					if (r!=null) {
						r.setDbEmplPosition(vpisvane.getEventDeinostVlp().getEventDeinostVlpLice().get(i).getTipVraz());
						r.setDateBeg(vpisvane.getEventDeinostVlp().getEventDeinostVlpLice().get(i).getDateBeg());
						r.setDateEnd(vpisvane.getEventDeinostVlp().getEventDeinostVlpLice().get(i).getDateEnd());
						licaList.add(r);
						obektLiceRefMap.put(vpisvane.getEventDeinostVlp().getEventDeinostVlpLice().get(i).getCodeRef(), vpisvane.getEventDeinostVlp().getEventDeinostVlpLice().get(i).getId());	
					}
										
				}	
			}
		}
	}
	 
	
 
	public void actionRemoveLice(Integer rowNum) {
		this.licaList.remove(this.licaList.get(rowNum));
//		setLiceRefMap();
	}
	
	public void setCurrentLice(Referent currentLice) {
		if (eventDeinostVlp==null) {
			eventDeinostVlp=new EventDeinostVlp();
		}
		if (licaList.size()>0) {
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_LICA_MNOGO)) {
				this.eventDeinostVlp.setEventDeinostVlpLice(new ArrayList<EventDeinostVlpLice>());
			}
			if (sd.isElementVisible(zaiavlVp.getDocVid(), BabhConstants.EKRAN_ATTR_VLP_OB_DEIN_LICA_M_TABLE)) {
				obektDein.setObektDeinostLica(new ArrayList<ObektDeinostLica>());
			}
		}
		if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_EDRO_VLP) {
			tipCurrentVrazkaLice=BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_OTG;
		}
		if (zaiavlVp.getDocVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_NARK_VESHT_VLP) {
			tipCurrentVrazkaLice=BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_OTG_LICE;
		}
		boolean found=false;
		if (rowLice==-1) {
			if (currentLice.getCode()!=null) {
				for (int i = 0; i < licaList.size(); i++) {
					if (licaList.get(i).getCode()!=null && currentLice.getCode().equals(licaList.get(i).getCode())) {
						currentLice.setDbEmplPosition(tipCurrentVrazkaLice);	
						licaList.set(i, currentLice);
						found=true;
						break;
					}
				}	
			}
			if (!found) {
				currentLice.setDbEmplPosition(tipCurrentVrazkaLice);
				licaList.add(currentLice);
			}
		}else {
			if (currentLice.getDbEmplPosition()==null) {
				currentLice.setDbEmplPosition(tipCurrentVrazkaLice);	
			}
			licaList.set(rowLice,currentLice);
		}
		rowLice=-1;
		
	}
	
	public void actionNewPredmetM() {
		this.eventDeinostVlpPredmet=new EventDeinostVlpPredmet();
		
		if (zaiavlVp.getDocPodVid()==null) {
			eventDeinostVlpPredmet.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_AKT_VESHT);
			this.eventDeinostVlpPredmet.setVidIdentifier(new Substance());
		}else {
			if (zaiavlVp.getDocPodVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_PROIZV_VLP) {
				eventDeinostVlpPredmet.setCodeClassif(BabhConstants.CODE_CLASSIF_FARM_FORM_PROIZV);
			}else {
				if (zaiavlVp.getDocPodVid()==BabhConstants.CODE_ZNACHENIE_ZAIAV_VNOS_VLP) {
					eventDeinostVlpPredmet.setCodeClassif(BabhConstants.CODE_CLASSIF_FORMI_DEINOST_VNOS_VLP);
				}
			}
		}
	}
	
	public void actionRemovePredmetM(Integer row) {
		this.eventDeinostVlp.getEventDeinostVlpPredmet().remove(this.eventDeinostVlp.getEventDeinostVlpPredmet().get(row));
	}
	
	public void actionAddPredmetM() { 
		if (eventDeinostVlpPredmet.getPredmet()!=null) {
			EventDeinostVlpPredmet tmp=new EventDeinostVlpPredmet();
			tmp.setCodeClassif(eventDeinostVlpPredmet.getCodeClassif());
			tmp.setPredmet(eventDeinostVlpPredmet.getPredmet());
			tmp.setDopInfo(eventDeinostVlpPredmet.getDopInfo());
			
			boolean add=true;
			for (int i = 0; i < eventDeinostVlp.getEventDeinostVlpPredmet().size(); i++) {
				if (eventDeinostVlp.getEventDeinostVlpPredmet().get(i).getPredmet()==tmp.getPredmet()) {
					add=false;
					eventDeinostVlp.getEventDeinostVlpPredmet().set(i, tmp);
					break;
				}
			}
			if (add) {
				eventDeinostVlp.getEventDeinostVlpPredmet().add(tmp);
			}
		}else {
			if (eventDeinostVlpPredmet.getVidIdentifier()!=null) {
				EventDeinostVlpPredmet tmp=new EventDeinostVlpPredmet();
				tmp.setCodeClassif(eventDeinostVlpPredmet.getCodeClassif());
				tmp.setVidIdentifier(eventDeinostVlpPredmet.getVidIdentifier());
				tmp.setDopInfo(eventDeinostVlpPredmet.getDopInfo());
				
				boolean add=true;
				for (int i = 0; i < eventDeinostVlp.getEventDeinostVlpPredmet().size(); i++) {
					if (eventDeinostVlp.getEventDeinostVlpPredmet().get(i).getVidIdentifier()!=null && eventDeinostVlp.getEventDeinostVlpPredmet().get(i).getVidIdentifier().getIdentifier()==tmp.getVidIdentifier().getIdentifier()) {
						add=false;
						eventDeinostVlp.getEventDeinostVlpPredmet().set(i, tmp);
						break;
					}
				}
				if (add) {
					eventDeinostVlp.getEventDeinostVlpPredmet().add(tmp);
				}
			}
		}
	}
	
	/************* Obekt deinost mnogo ***********/
	public void actionAddObektDein() {
		obektDein=new ObektDeinost();
		this.datBegObDeinDein=null;
		this.datEndObDeinDein=null;
		try {
			obektDein.setDarj(Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG")));
		} catch (NumberFormatException | DbErrorException e) {
			LOGGER.error("Грешка при сетване на държава! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
		this.rowObDein=-1;
	}
	
	public void actionRemoveObektDein(Integer rowNum) {
		this.obektDeinList.remove(this.obektDeinList.get(rowNum));
	}
	

	public void actionEditObektDein(ObektDeinostDeinost od, Integer idx) {		
		this.obektDein=od.getObektDeinost();
		this.datBegObDeinDein=od.getDateBeg();
		this.datEndObDeinDein=od.getDateEnd();
		if (obektDein.getPrednaznachenieList()!=null && !obektDein.getPrednaznachenieList().isEmpty()) {
			prednazCodes=new ArrayList<Integer>();
			prednazClassif=new ArrayList<SystemClassif>();
			for (int i = 0; i < obektDein.getPrednaznachenieList().size(); i++) {
				 prednazCodes.add(obektDein.getPrednaznachenieList().get(i));
				 
				 SystemClassif scItem = new SystemClassif();
				 scItem.setCodeClassif(BabhConstants.CODE_CLASSIF_PREDN_OBEKT);
				 scItem.setCode(obektDein.getPrednaznachenieList().get(i));
				 try {
					scItem.setTekst(getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_PREDN_OBEKT, obektDein.getPrednaznachenieList().get(i), getCurrentLang(), new Date()));
				} catch (DbErrorException e) {
					LOGGER.error("Грешка при работа с базата данни! ", e);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
				}
				 prednazClassif.add(scItem);
				
			}
		}
		this.setRowObDein(idx);
		PrimeFaces.current().executeScript("PF('mObektDein').show();");
	}
	
	public void actionCancelReturnObDein() {
		prednazCodes=new ArrayList<Integer>();
		prednazClassif=new ArrayList<SystemClassif>();
	}
	
	public void actionReturnObDein() {
		
		if (obektDeinList==null) {
			obektDeinList=new ArrayList<ObektDeinostDeinost>();
		}
		
		Boolean add=true;
		if (prednazCodes==null || prednazCodes.size()==0) {
			JSFUtils.addMessage("vlpTargForm:registerTabs:selectManyModalPredn:autoComplM",FacesMessage.SEVERITY_ERROR,"Моля, въведете предназначение!");
			add=false;
		}
		
		if (add) {
			if (obektDein.getPrednaznachenieList()==null) {
				obektDein.setPrednaznachenieList(new ArrayList<Integer>());
			}else {
				obektDein.getPrednaznachenieList().clear();
			}
			if (prednazCodes!=null) {
				obektDein.getPrednaznachenieList().addAll(prednazCodes);
			}
			
			ObektDeinostDeinost tmp=new ObektDeinostDeinost();
			tmp.setObektDeinost(obektDein);
			tmp.setDateBeg(this.datBegObDeinDein);
			tmp.setDateEnd(this.datEndObDeinDein);
			if (rowObDein==-1) {
				
				obektDeinList.add(tmp);			
			}else {
				obektDeinList.set(rowObDein, tmp);
			}
			rowObDein=-1;
	 
			PrimeFaces.current().executeScript("PF('mObektDein').hide();");
		}
	}
	
	public void actionNewLiceObDein() {
		this.currentLiceObDein=new Referent();
		rowObDeinLice=-1;
	}
	
	public void setCurrentLiceObDein(Referent currentLiceObDein) {
		this.currentLiceObDein = currentLiceObDein;
		
		if (obektDein==null) {
			obektDein=new ObektDeinost();
			try {
				obektDein.setDarj(Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG")));
			} catch (NumberFormatException | DbErrorException e) {
				LOGGER.error("Грешка при сетване на държава! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
			}
		}
		if (obektDein.getObektDeinostLica()==null) {
			obektDein.setObektDeinostLica(new ArrayList<ObektDeinostLica>());
		}
		
		boolean found=false;
		ObektDeinostLica odl=new ObektDeinostLica();
		
		odl.setReferent(currentLiceObDein);
		odl.setRole(tipCurrentVrazkaLiceObDein);
		odl.setDateBeg(currentLiceObDein.getDateBeg());		
		odl.setDateEnd(currentLiceObDein.getDateEnd());		

		if (rowObDeinLice==-1) {				
			if (currentLiceObDein.getCode()!=null) {
				for (int i = 0; i < obektDein.getObektDeinostLica().size(); i++) {
					if (obektDein.getObektDeinostLica().get(i).getReferent().getCode()!=null 
							&& currentLiceObDein.getCode().equals(obektDein.getObektDeinostLica().get(i).getReferent().getCode())) {
						obektDein.getObektDeinostLica().set(i, odl);
						found=true;
						break;
					}
				}	
			}
			if (!found) {
				obektDein.getObektDeinostLica().add(odl);
			}
		}else {
			odl.setRole(currentLiceObDein.getDbEmplPosition());
			obektDein.getObektDeinostLica().set(rowObDeinLice,odl);
		}
		rowObDeinLice=-1;
	}
	
	public void actionRemoveLiceObDein(Integer rowNum) {
		obektDein.getObektDeinostLica().remove(obektDein.getObektDeinostLica().get(rowNum));
	}
	
	public void actionEditObektDeinLice(ObektDeinostLica l, Integer rowNum) {
		this.currentLiceObDein=l.getReferent();
		this.currentLiceObDein.setDbEmplPosition(l.getRole());
		this.rowObDeinLice=rowNum;
		PrimeFaces.current().executeScript("PF('mLiceObDeinD').hide();");
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

	public ObektDeinost getObektDein() {
		return obektDein;
	}

	public void setObektDein(ObektDeinost obektDein) {
		this.obektDein = obektDein;
	}

	public EventDeinostVlp getEventDeinostVlp() {
		return eventDeinostVlp;
	}

	public void setEventDeinostVlp(EventDeinostVlp eventDeinostVlp) {
		this.eventDeinostVlp = eventDeinostVlp;
	}

	public List<Integer> getVidVlpCodes() {
		return vidVlpCodes;
	}

	public void setVidVlpCodes(List<Integer> vidVlpCodes) {
		this.vidVlpCodes = vidVlpCodes;
	}

	public List<SystemClassif> getVidVlpClassif() {
		return vidVlpClassif;
	}

	public void setVidVlpClassif(List<SystemClassif> vidVlpClassif) {
		this.vidVlpClassif = vidVlpClassif;
	}

	public List<Integer> getPredmetCodes() {
		return predmetCodes;
	}

	public void setPredmetCodes(List<Integer> predmetCodes) {
		this.predmetCodes = predmetCodes;
	}

	public List<SystemClassif> getPredmetClassif() {
		return predmetClassif;
	}

	public void setPredmetClassif(List<SystemClassif> predmetClassif) {
		this.predmetClassif = predmetClassif;
	}

	public List<Integer> getPrednazCodes() {
		return prednazCodes;
	}

	public void setPrednazCodes(List<Integer> prednazCodes) {
		this.prednazCodes = prednazCodes;
	}

	public List<SystemClassif> getPrednazClassif() {
		return prednazClassif;
	}

	public void setPrednazClassif(List<SystemClassif> prednazClassif) {
		this.prednazClassif = prednazClassif;
	}
 

	public String getTxtLice() {
		return txtLice;
	}

	public void setTxtLice(String txtLice) {
		this.txtLice = txtLice;
	}

	public EventDeinostVlpLice getEventLice() {
		return eventLice;
	}

	public void setEventLice(EventDeinostVlpLice eventLice) {
		this.eventLice = eventLice;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public Referent getCurrentLice() {
		return currentLice;
	}

	public ArrayList<Referent> getLicaList() {
		return licaList;
	}

	public void setLicaList(ArrayList<Referent> licaList) {
		this.licaList = licaList;
	}

	public Integer getTipCurrentVrazkaLice() {
		return tipCurrentVrazkaLice;
	}

	public void setTipCurrentVrazkaLice(Integer tipCurrentVrazkaLice) {
		this.tipCurrentVrazkaLice = tipCurrentVrazkaLice;
	}

	public HashMap<Integer,Integer> getObektLiceRefMap() {
		return obektLiceRefMap;
	}

	public void setObektLiceRefMap(HashMap<Integer,Integer> obektLiceRefMap) {
		this.obektLiceRefMap = obektLiceRefMap;
	}

	public EventDeinostVlpPredmet getEventDeinostVlpPredmet() {
		return eventDeinostVlpPredmet;
	}

	public void setEventDeinostVlpPredmet(EventDeinostVlpPredmet eventDeinostVlpPredmet) {
		this.eventDeinostVlpPredmet = eventDeinostVlpPredmet;
	}

	public List<Integer> getBolestiCodes() {
		return bolestiCodes;
	}

	public void setBolestiCodes(List<Integer> bolestiCodes) {
		this.bolestiCodes = bolestiCodes;
	}

	public List<SystemClassif> getBolestiClassif() {
		return bolestiClassif;
	}

	public void setBolestiClassif(List<SystemClassif> bolestiClassif) {
		this.bolestiClassif = bolestiClassif;
	}

	public List<ObektDeinostDeinost> getObektDeinList() {
		return obektDeinList;
	}

	public void setObektDeinList(List<ObektDeinostDeinost> obektDeinList) {
		this.obektDeinList = obektDeinList;
	}

 

	public Referent getCurrentLiceObDein() {
		return currentLiceObDein;
	}

	public Integer getTipCurrentVrazkaLiceObDein() {
		return tipCurrentVrazkaLiceObDein;
	}

	public void setTipCurrentVrazkaLiceObDein(Integer tipCurrentVrazkaLiceObDein) {
		this.tipCurrentVrazkaLiceObDein = tipCurrentVrazkaLiceObDein;
	}

	public Integer getRowObDein() {
		return rowObDein;
	}

	public void setRowObDein(Integer rowObDein) {
		this.rowObDein = rowObDein;
	}

	public Object[] getLastDoc() {
		return lastDoc;
	}

	public void setLastDoc(Object[] lastDoc) {
		this.lastDoc = lastDoc;
	}

	public EventDeinostVlpPrvnos getEventDeinostVlpPrvnos() {
		return eventDeinostVlpPrvnos;
	}

	public void setEventDeinostVlpPrvnos(EventDeinostVlpPrvnos eventDeinostVlpPrvnos) {
		this.eventDeinostVlpPrvnos = eventDeinostVlpPrvnos;
	}

	public EventDeinostVlpPrvnosSubst getEventDeinostVlpPrvnosSubst() {
		return eventDeinostVlpPrvnosSubst;
	}

	public void setEventDeinostVlpPrvnosSubst(EventDeinostVlpPrvnosSubst eventDeinostVlpPrvnosSubst) {
		this.eventDeinostVlpPrvnosSubst = eventDeinostVlpPrvnosSubst;
	}

	public Integer getRowVnos() {
		return rowVnos;
	}

	public void setRowVnos(Integer rowVnos) {
		this.rowVnos = rowVnos;
	}


 
	public BigDecimal getDaljimaSuma() {
		return daljimaSuma;
	}

	public void setDaljimaSuma(BigDecimal daljimaSuma) {
		this.daljimaSuma = daljimaSuma;
	}

	public Integer getRowLice() {
		return rowLice;
	}

	public void setRowLice(Integer rowLice) {
		this.rowLice = rowLice;
	}

	public Integer getRowObDeinLice() {
		return rowObDeinLice;
	}

	public void setRowObDeinLice(Integer rowObDeinLice) {
		this.rowObDeinLice = rowObDeinLice;
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

	public int getIsView() {
		return isView;
	}

	public void setIsView(int isView) {
		this.isView = isView;
	}

	public Map<Integer, Object> getSpecificsEkatte() {
		return specificsEkatte;
	}

	public void setSpecificsEkatte(Map<Integer, Object> specificsEkatte) {
		this.specificsEkatte = specificsEkatte;
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

	public Integer getRef2Type() {
		return ref2Type;
	}

	public void setRef2Type(Integer ref2Type) {
		this.ref2Type = ref2Type;
	}

	public Boolean getShowRadioRef2() {
		return showRadioRef2;
	}

	public void setShowRadioRef2(Boolean showRadioRef2) {
		this.showRadioRef2 = showRadioRef2;
	}

	public List<SystemClassif> getPodZaiavList() {
		return podZaiavList;
	}

	public void setPodZaiavList(List<SystemClassif> podZaiavList) {
		this.podZaiavList = podZaiavList;
	}

	public Date getDatEndObDeinDein() {
		return datEndObDeinDein;
	}

	public void setDatEndObDeinDein(Date datEndObDeinDein) {
		this.datEndObDeinDein = datEndObDeinDein;
	}

	public Date getDatBegObDeinDein() {
		return datBegObDeinDein;
	}

	public void setDatBegObDeinDein(Date datBegObDeinDein) {
		this.datBegObDeinDein = datBegObDeinDein;
	}

	public EventDeinostVlpPrvnosOpakovka getParvOpak() {
		return parvOpak;
	}

	public void setParvOpak(EventDeinostVlpPrvnosOpakovka parvOpak) {
		this.parvOpak = parvOpak;
	}

	public int getMigration() {
		return migration;
	}

	public void setMigration(int migration) {
		this.migration = migration;
	}



}
