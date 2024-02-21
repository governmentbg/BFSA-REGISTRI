package com.ib.babhregs.beans;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.facelets.FaceletContext;

import org.omnifaces.cdi.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.components.RefCorrespData;
import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.LockObjectDAO;
import com.ib.babhregs.db.dao.MpsDAO;
import com.ib.babhregs.db.dao.MpsFurajiDAO;
import com.ib.babhregs.db.dao.ObektDeinostDAO;
import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.EventDeinostFuraji;
import com.ib.babhregs.db.dto.EventDeinostFurajiPrednaznJiv;
import com.ib.babhregs.db.dto.EventDeinostFurajiSert;
import com.ib.babhregs.db.dto.MpsDeinost;
import com.ib.babhregs.db.dto.MpsFuraji;
import com.ib.babhregs.db.dto.MpsVidProducts;
import com.ib.babhregs.db.dto.EventDeinostFurajiPredmet;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
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
 * РЕГИСТЪР НА ТЪРГОВЦИ НА ФУРАЖНИ ДОБАВКИ ПО ЧЛ. 20. АЛ. 3 ОТ ЗАКОНА ЗА ФУРАЖИТЕ
 * И ВСИЧКИ ДЕЙНОСТИ ОТ НЕГО..
 * 
 * @author s.arnaudova
 */

@Named
@ViewScoped
public class RegTargovtsiFuraj extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8061137828429958128L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegTargovtsiFuraj.class);
	public  static final  String IDFORM = "targovtsiFurajForm";
	
	@Inject
	private Flash flash;

	private Date decodeDate;
	private SystemData sd;
	private UserData ud;

	private Integer idZaiav;
	private Integer idVpisv;

	private Integer registerId;
	
	private int isView;
	private int migration;


	/**
	 * заявлението за вписване
	 */
	private Doc babhDoc;
	private transient DocDAO docDAO;

	/**
	 * всички настройки за регистър Бабх
	 */
	private RegisterOptions registerOptions;

	/**
	 * настройките на конкретното заявление
	 */
	private RegisterOptionsDocsIn docOpt;

	/**
	 * заявлението за вписване
	 */
	private Vpisvane vpisvane;
	private transient VpisvaneDAO vpisvaneDAO;


	/**
	 * заявителя (лицензиант) - code_ref_vpivane от tablica вписване
	 */
	private Referent referent1;
	private RefCorrespData	bindingReferent1;	
	
	/**
	 * заявителя (лицензиант) - code_ref_vpivane от tablica вписване
	 */
	private Referent referent2;
	private RefCorrespData	bindingReferent2;	
	
	/**
	 *  вида дейности, които съдържа съответното заявление
	 */
	private List<Integer> vidDeinFuraji;  


	/** 
	 * вид фуражи, които се изпозлват
	*/
	private List<Integer> vidFurajiList; 
	private List<SystemClassif> vidFurajiClassif; // заради autocomplete
	private Map<Integer, Integer> vidFurajiMap = new HashMap<Integer, Integer>(); //мап със старите стойности на ид-тата, за да не се сменят при всеки запис
	
	/**
	 * видове животни, за които са предназначени комбинираните фуражи
	 */
	private EventDeinostFurajiPrednaznJiv prednaznachenieJiv;
	private Integer newDeinJiv; // подава се при отваряне на модалния за ново въвеждане/или актуализация на животно

	
	/**
	 * когато няма подадено никакво ид на обект скривам панела
	 */
	private boolean showEkran = true; 
	
	/**
	 * дали да показва панела за Обекти на дейност
	 */
	private boolean showObektDeinPnl = false; 
	
	/**
	 * Когато става дума само за търговия с някакви фуражи, не трябва да се изисква задължително въвеждане на обект. Ако не въведат обект, атрибутът „Задържа ли се стоката на склад“ трябва да стане ‚НЕ“ и не трябва да може да се променя. Ако има обект, да може да се избира да или не.
	 */
	private boolean zaiavlenieZaTargovia = false; 
	
	/**
	 * дали да проверява данните за обект на дейност
	 */
	private boolean checkPredmetDein = false;
	
	/**
	 * подава се от екрана и указва дали да се визуализира полето "Предназначение" в модалния за избор на вид фураж
	 */
	private boolean showPredn = true; 
	
	/**
	 * Предмет на дейността
	 */
	private EventDeinostFurajiPredmet predmetDeinost;
	private Integer newDeinPredmet; // подава се при отваряне на модалния за ново въвеждане/или актуализация на вид фураж 
	private boolean predmetDeinDtbl = true; // дали Предмета на дейност се въвежда в таблица или в selectOneModalA (спестява ми доста if-ове..) При инициализиране на съответната дейност указвам дали е true или false

	
	/**
	 * Обект на дейността
	 */
	private Integer obektDeinostId;
	private ObektDeinost obektDeinost;
	private boolean foundedObektDein = false;
	private boolean obektDeinWithoutSave = false;
	
	/**
	 * МПС дейност
	 */
	private MpsDeinost mpsDeinost;
	private Integer mpsId;
	private MpsFuraji mpsFuraj;
	private List<Integer> sastonieList;
	private Map<Integer, Integer> sastonieMap = new HashMap<Integer, Integer>(); //мап със старите стойности на ид-тата, за да не се сменят при всеки запис
	private Integer newDeinMps; // подава се при отваряне на модалния за ново въвеждане/или актуализация на ТС
	
	
	/**
	 * Сертификат за добра практика
	 */
	private EventDeinostFurajiSert eventDeinostSert;
	private Integer newSert; // подава се при отваряне на модалния за ново въвеждане/или актуализация на сертификат
	
	/**
	 * видове животни /САМО ЗА ПРИЛАГАНЕ НА ПОДМЕРКИ И ИЗПОЛЗВАНЕ НА СЖП ПО ЧЛ.23е !/
	 */
	private List<Integer> jivotniList;
	private Map<Integer, Integer> jivotniMap = new HashMap<Integer, Integer>();
	
	/** Списък на файловете към заявлението за вписване */
	private List<Files> filesList;
	
	
	/** Информацията за последното заявление към вписването (ако има такова) */
	private Object[] lastDoc;
	
	
	/**
	 * ЗА ДЕЙНОСТИ С ИЗДАВАНЕ НА СЕРТИФИКАТ ЗА ИЗНОС.. обекта е множествен, но за фуражи е единичен..
	 */
	private Integer gip;
	
	/**
	 * Дължима сума
	 */
 	private BigDecimal daljimaSuma;
	
	/**
	 * дали потребителя има достъп до вписването
	 */
	private boolean access;

	/**
	 * списък с подзаявления по чл.18 и чл. 16
	 */
	private List<SystemClassif> podZaiavList;
	
	
	/**
	 * за избор на населено място.. премахва областите
	 */
	private Map<Integer, Object> ekatteSpec;
	
	
	/**
	 * за заявленията за внос на фуражи се избира само една дейност
	 */
	private Integer vidDeinVnos; 
	
	
	
	
	@PostConstruct
	public void init() {
		LOGGER.info("RegTargovtsiFuraj init!!!!!!");
		
		this.sd = (SystemData) getSystemData();
		this.ud = getUserData(UserData.class);
		this.decodeDate = new Date();
		this.babhDoc = new Doc();
		this.vpisvane = new Vpisvane();
		this.vpisvaneDAO = new VpisvaneDAO(getUserData());
		this.docDAO = new DocDAO(getUserData());
		this.filesList = new ArrayList<>();
		
		this.vpisvane.setEventDeinostFuraji(new EventDeinostFuraji());
		this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiPrednaznJiv(new ArrayList<>());
		this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiPredmet(new ArrayList<>());
		this.vpisvane.getEventDeinostFuraji().setMpsDeinost(new ArrayList<>());
		this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiSert(new ArrayList<>());
		this.vpisvane.getEventDeinostFuraji().setObektDeinostDeinost(new ArrayList<>());
		this.vpisvane.getEventDeinostFuraji().setVidList(new ArrayList<>());

		this.prednaznachenieJiv = new EventDeinostFurajiPrednaznJiv();
		this.vidDeinFuraji = new ArrayList<>();
		this.obektDeinost = new ObektDeinost();
		this.predmetDeinost = new EventDeinostFurajiPredmet();
		this.vidFurajiList = new ArrayList<>();
		this.vidFurajiClassif = new ArrayList<>();
		this.vidFurajiMap = new HashMap<Integer, Integer>();

		try {
			
			FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
			String viewParam = (String) faceletContext.getAttribute("isView"); 
			isView = !SearchUtils.isEmpty(viewParam) ? Integer.valueOf(viewParam) : 0; // 0 - актуализация; 1 - разглеждане

			String paramM = JSFUtils.getRequestParameter("m");
			migration = SearchUtils.isEmpty(paramM) ? BabhConstants.CODE_ZNACHENIE_NE :  0; // Ако е подаден параметър - значи идва от екрана за ръчно въвеждане на миграция
			
			String idZaiavlenie = JSFUtils.getRequestParameter("idZ");
			String idVpisvane = JSFUtils.getRequestParameter("idV");

			if (idZaiavlenie == null && idVpisvane == null) {
				// достъпили са странницата без да има подадено id
				this.showEkran = false;
				LOGGER.error("Не е подадено ID на обект");
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Не е подадено ID на обект!");
				
			} else {

				// идваме от филтъра с Вписвания
				if (!SearchUtils.isEmpty(idVpisvane)) {
					this.idVpisv = Integer.valueOf(idVpisvane);

					boolean fLockOk = true;
					
					//ако е само за разглеждане не заключваме заявлението
					if(isView == 0) {
						fLockOk = checkAndLockDoc(this.idVpisv, BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
					}
					
					if (fLockOk) {
						loadVpisvane(idVpisv);
						
						if (this.vpisvane != null) {
							
							// ако заявлението е за промяна или заличаване.. винаги позволяваме корекция на данните
							String zaiavChange = JSFUtils.getRequestParameter("change"); 
							if (zaiavChange != null && !zaiavChange.isEmpty()) {
								this.vpisvane.setVpLocked(Constants.CODE_ZNACHENIE_NE);
							}
							
							// проверка за достъп до впсиване							 
							JPA.getUtil().runWithClose(() -> this.access = this.vpisvaneDAO.hasVpisvaneAccess(this.vpisvane, isView == 0, sd));
							
							if (access) {
								loadLastZaiav(true); // зарежда последното заявление
								loadBabhDoc(this.vpisvane.getIdZaqavlenie()); // тук имаме номер на заявлението и си го зареждаме
								
								//ако има подзаявление зареждаме настройките по него, ако няма си ги зареждаме по стария начин
								if (this.babhDoc.getDocPodVid() != null) {
									loadRegOptions(this.babhDoc.getDocPodVid());
									
								} else {
									loadRegOptions(this.babhDoc.getDocVid());
								}
								
							} else {
								this.showEkran = false;						
								JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Отказан достъп!");
							}
						}
					}
				}
				
				// идваме от Заявления и нямаме вписване.. създаваме ново вписване..
				if (!SearchUtils.isEmpty(idZaiavlenie)) {

					this.idZaiav = Integer.valueOf(idZaiavlenie);

					boolean fLockOk = checkAndLockDoc(this.idZaiav, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
					if (fLockOk) {
						parseZaiav(); // тук нямаме вписваме и правим ново с данните от заявлението...

						// проверка за достъп до заявлението
						JPA.getUtil().runWithClose(() -> this.access = this.docDAO.hasDocAccess(this.babhDoc, isView == 0, sd));
						if (!access) {
							this.showEkran = false;							
							JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Отказан достъп!");
						}
					}
				}
			}

			if (!access) {
				unlockDoc();
			} else {
				
				checkDaljimaSuma();
				if(isView == 1) {
		   			viewMode();
		   		}
				
				if (this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_ODOB_OPERATORI_18) 
						| this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_REG_OPERATORI_16) 
						| this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_MEDIKAMENTOZNI_FURAJI)
						| this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_IZNOS_FURAJI)) {
					
					this.podZaiavList = new ArrayList<>();
					this.podZaiavList = this.sd.getChildrenOnNextLevel(BabhConstants.CODE_CLASSIF_DOC_VID, this.babhDoc.getDocVid(), decodeDate, getCurrentLang());
				}
			}
			
			
		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на обект", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при инициализиране на обект ");
		}
	}
	
	
	/**
	* Режим - разглеждане на вписване
	*/
	private void viewMode() {
		String  cmdStr;  
		//забранявам всички инпутполета
		cmdStr = "$(':input').attr('readonly','readonly')";
		PrimeFaces.current().executeScript(cmdStr);
	}
	
	/**
	 *  проверка за дължима такса
	 */
	private void checkDaljimaSuma() {
		daljimaSuma = null;
	
   		Object[] payStatus;
		try {
			payStatus = vpisvaneDAO.findUnpaidTaxInfo(this.vpisvane.getId(), this.babhDoc.getId());
			
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
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при извличане на дължими такси! ", e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
		
	}


	private void loadRegOptions(Integer docVid) {

		this.registerId = sd.getRegisterByVidDoc().get(this.babhDoc.getDocVid());
		registerOptions = sd.getRoptions().get(registerId); // всички настройки за регистъра
		if (this.registerOptions != null) {
			List<RegisterOptionsDocsIn> lstDocsIn = registerOptions.getDocsIn(); // само входните док. за регистъра
			docOpt = new RegisterOptionsDocsIn();
			for (RegisterOptionsDocsIn item : lstDocsIn) {
				if ((item.getVidDoc()).equals(docVid)) {
					docOpt = item;
					break;
				}
			}
			
			loadDeinFuraji(); // зареждаме дейностите към съответния регистър
			
			//за всяка от дейностите има специфични атрибути и списъци.. с този метод зареждаме специфичните неща
			loadSpecificDeinAttr();
			
			// за ново вписване сетваме сумата и типа на плащане (от настройките)
			// и типа плащане, ако вече не са записани!!!
			if(vpisvane == null || vpisvane.getId() == null) {
				//ако в заявлението има настройки за плащането - не ги пипаме!
				if (babhDoc.getPayType() == null ) {  		
					babhDoc.setPayType(this.docOpt.getPayCharacteristic());
				}
				if(babhDoc.getPayAmount() == null || babhDoc.getPayAmount().equals(Float.valueOf(0))) { 
					babhDoc.setPayAmount(this.docOpt.getPayAmount());
				}
			}
			 
			
		} else {
			//ако няма настройки за регистъра показваме само информативно съобщение..
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Няма открити настройки за този регистър! ");
		}
	}
	
	
	/**
	* При смяна на вида позаявление /ЗА ОБЕДИНЕНИТЕ ЗАЯВЛЕНИЕ ПО ЧЛ. 16 И ЧЛ. 18/
	* 1. добавя дейността в обекта
	*/
	public void actionChangePodvid() {
		
		this.vpisvane.getEventDeinostFuraji().setVidList(null);
		this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiPrednaznJiv(new ArrayList<>());
		this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiPredmet(new ArrayList<>());
		this.vpisvane.getEventDeinostFuraji().setSklad(null); // за да кординира правилно панелите за търговия и производство
		if (this.vpisvane.getObektDeinost() != null) {
			this.vpisvane.setObektDeinost(null);
		}

		this.prednaznachenieJiv = new EventDeinostFurajiPrednaznJiv();
		this.vidDeinFuraji = new ArrayList<>();		
		this.predmetDeinost = new EventDeinostFurajiPredmet();
		this.vidFurajiList = new ArrayList<>();
		this.vidFurajiClassif = new ArrayList<>();
		this.vidFurajiMap = new HashMap<Integer, Integer>();

		//презареждаме и всички дейности и настройки за новото заявление
		loadRegOptions(this.babhDoc.getDocPodVid()); 
		
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_FURAJI) | this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_CERT_IZNOS_FURAJI_53)) {			
			if (this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost() == null || this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().isEmpty()) {
				ObektDeinostDeinost obektDeinDein = new ObektDeinostDeinost();
				ObektDeinost tmpObekt = new ObektDeinost();	
				obektDeinDein.setObektDeinost(tmpObekt);
				this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().add(obektDeinDein);				
			}
		}
	}
	
	
	/**
	* При смяна на вида дейност в заявлението за ВНОС на фуражи подменя дейността
	*/
	public void actionChangeVnosDein() {
			
		this.vpisvane.getEventDeinostFuraji().setVidList(new ArrayList<>());
		this.vpisvane.getEventDeinostFuraji().getVidList().add(this.vidDeinVnos);
	}

	/**
	 * ЗАРЕЖДА ВИДОВЕТЕ ДЕЙНОСТИ ЗА СЪОТВЕТНОТО ЗАЯВЛЕНИЕ.. 
	 * в случай, че дейността е само една директно я избираме и я бутаме в списъка.. ако са повече от една трябва да изберат от екрана за кои дейности се отнася..
	 */
	private void loadDeinFuraji() {

		if (docOpt.getDocsInActivity() != null && !docOpt.getDocsInActivity().isEmpty()) {	
			for (RegisterOptionsDocinActivity item : docOpt.getDocsInActivity()) {				
				this.vidDeinFuraji.add(item.getActivityId()); //запълва списъка на екрана
				
				//TODO защо?
				if (this.vpisvane.getEventDeinostFuraji().getVidList() == null) {
					this.vpisvane.getEventDeinostFuraji().setVidList(new ArrayList<>());
				}
				
				if (docOpt.getDocsInActivity().size() == 1 && this.vpisvane.getEventDeinostFuraji().getVidList().isEmpty()) {
					this.vpisvane.getEventDeinostFuraji().getVidList().add(item.getActivityId()); //ако имаме ново заявления и дейността е само една я зареждаме и показваме информативно на екрана
				}
			}
		}
	}

	
	/**
	 * зарежда BabhDoc
	 */
	private void loadBabhDoc(Integer babhDocId) {
		try {

			JPA.getUtil().runWithClose(() -> this.babhDoc = this.docDAO.findById(babhDocId));
			
			if (this.babhDoc != null) {
				
				loadFilesList(this.babhDoc.getId());
				
				if(this.babhDoc.getCodeRefCorresp() != null) {
					JPA.getUtil().runWithClose(() -> referent1 = new ReferentDAO(ud).findByCodeRef(this.babhDoc.getCodeRefCorresp()));
					referent1.setKachestvoLice(this.babhDoc.getKachestvoLice());			
				}
				
				this.referent2 = new Referent();
				
				if (this.vpisvane != null && vpisvane.getCodeRefVpisvane() != null) {
					JPA.getUtil().runWithClose(() -> referent2 = new ReferentDAO(ud).findByCodeRef(vpisvane.getCodeRefVpisvane()));
				} 
			} 

		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на вписване", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на вписване ");
		}
	}
	
	
	/***
	 * ИЗВЛИЧА ПОСЛЕДНОТО ЗАЯВЛЕНИЕ КЪМ ДАДЕНО ВПИСВАНЕ
	 * 
	 * @param exludeFirst - ако е true игнорира заявлението за първоначално вписване
	 * */
	private void loadLastZaiav(boolean exludeFirst) {
		try {
			
			JPA.getUtil().runWithClose(() -> lastDoc = this.vpisvaneDAO.findLastIncomeDoc(this.idVpisv, exludeFirst));

		} catch (BaseException e) {
			LOGGER.error("Грешка при извличане на последно заявление! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}
	
	public String actionGotoLastZaiav() {
		return "babhZaiavView.xhtml?faces-redirect=true&idObj=" + this.lastDoc[0];
	}
	
	/***
	 * ПАРСВА НА ЗАЯВЛЕНИЕ ПОЛУЧЕНО ОТ Е-ФОРМИ И ЗАРЕЖДА НУЖНИТЕ ДАННИ
	 * 
	 * TODO тестово
	 * */
	private void parseZaiav() {		
		try {
			
			EgovContainer container = new EgovContainer();
			try {
				container = new EFormUtils().convertEformToVpisvane(idZaiav, sd);
			} catch (Exception e1) {
				// да заредя все пак заявлението!!!
				JPA.getUtil().runWithClose(() -> this.babhDoc =   new DocDAO(ud).findById(Integer.valueOf(idZaiav)));
				 
				LOGGER.error("Грешка при парсване на заявление - ВЛЗ! ", e1);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
						"Грешка при парсване на заявление! Данните не могат да бъдат заредени от прикачения файл!");
			}
			if(container.doc != null) {
				this.babhDoc = container.doc;
			}
			this.vpisvane = container.vpisvane;
			referent2 = container.ref2;			
			referent1 = container.ref1;			

			if(referent1 == null && babhDoc.getCodeRefCorresp() != null) {				
				JPA.getUtil().runWithClose(() -> referent1 = new ReferentDAO(ud).findByCodeRef(babhDoc.getCodeRefCorresp()));
			} 
			if(referent1 == null) {
				referent1 = new Referent();
				referent1.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
			}
			referent1.setKachestvoLice(babhDoc.getKachestvoLice());  
		
			if(referent2 == null && babhDoc.getCodeRefZaiavitel() != null) {				
				JPA.getUtil().runWithClose(() -> referent2 = new ReferentDAO(ud).findByCodeRef(babhDoc.getCodeRefZaiavitel()));
			} 
			if (referent2 == null) {
				referent2 = new Referent();
		 	}
			if(referent2.getAddressKoresp() == null) {
				referent2.setAddressKoresp(new ReferentAddress()); //това дали трябв да е тук ...
			}
		
			if (this.vpisvane == null) {
				this.vpisvane = new Vpisvane();
			}
			
			loadFilesList(this.babhDoc.getId());

			// TODO тия неща при Васко..
			vpisvane.setRegistraturaId(babhDoc.getRegistraturaId());
			vpisvane.setIdZaqavlenie(babhDoc.getId());
			vpisvane.setRegNomZaqvlVpisvane(babhDoc.getRnDoc());
			vpisvane.setDateZaqvlVpis(babhDoc.getDocDate());
			vpisvane.setCodeRefVpisvane(referent2.getCode()); // от парсването на заявлението 
			vpisvane.setIdRegister(babhDoc.getRegisterId()); // ако е ново вписване... 
			vpisvane.setLicenziantType(BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_LICE); 
			vpisvane.setCodePage(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI);
			vpisvane.setStatus(BabhConstants.STATUS_VP_WAITING);
			
			if (this.vpisvane.getEventDeinostFuraji() == null) {				
				this.vpisvane.setEventDeinostFuraji(new EventDeinostFuraji());
				this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiPrednaznJiv(new ArrayList<>());
				this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiPredmet(new ArrayList<>());
				this.vpisvane.getEventDeinostFuraji().setMpsDeinost(new ArrayList<>());
				this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiSert(new ArrayList<>());
				this.vpisvane.getEventDeinostFuraji().setObektDeinostDeinost(new ArrayList<>());
				this.vpisvane.getEventDeinostFuraji().setVidList(new ArrayList<>());
			}
			
			loadRegOptions(this.babhDoc.getDocVid());
			
			// 0 - миграция, ръчно въвеждане,1 - миграция - завършена, 2 - не е миграция;
			if (migration == 0) {
				vpisvane.setFromМigr(migration); // ако е от миграция - ръчно въвеждане, иначе не се променя				
			}
					
			//проверка за грешки при парсването
			if(!container.warnings.isEmpty()) {
				for(String msg: container.warnings) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, msg);		
				}
			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при парсване на заявления! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
			
		} finally {
			JPA.getUtil().closeConnection();
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
	 * зарежда Vpisvane
	 */
	private void loadVpisvane(Integer idVpisvane) {
		try {

			JPA.getUtil().runWithClose(() -> this.vpisvane = this.vpisvaneDAO.findById(idVpisvane));
			
			if (this.vpisvane == null) {

				if (this.vpisvane.getEventDeinostFuraji() == null) {
					this.vpisvane.setEventDeinostFuraji(new EventDeinostFuraji());
					this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiPrednaznJiv(new ArrayList<>());
					this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiPredmet(new ArrayList<>());
					this.vpisvane.getEventDeinostFuraji().setMpsDeinost(new ArrayList<>());
					this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiSert(new ArrayList<>());
					this.vpisvane.getEventDeinostFuraji().setObektDeinostDeinost(new ArrayList<>());
					this.vpisvane.getEventDeinostFuraji().setVidList(new ArrayList<>());
				}
			} 
			
		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на вписване", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на вписване ");
		}
	}
	
	/**
	 * ЗАРЕЖДА СПЕЦИФИЧНИТЕ НЕЩА ЗА ВСЯКО ОТДЕЛНА ДЕЙНОСТ..
	 */
	private void loadSpecificDeinAttr() {
		
		//производство на комбинирани фуражи по чл. 18
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_FR_PROIZV_KF18)) {
			this.predmetDeinDtbl = true;
			this.showObektDeinPnl = true;
			this.checkPredmetDein = true; 
			this.zaiavlenieZaTargovia = false;
		}
		
		//производство/търговия на премикси по чл. 18
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_PREMIKS18)|| this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PR_PREM18)) {
			this.predmetDeinDtbl = false;
			this.showObektDeinPnl = true;
			this.checkPredmetDein = true; 
			this.zaiavlenieZaTargovia = false;
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_PREMIX_TARGOVIA);
			
			if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_PREMIKS18)) {
				this.zaiavlenieZaTargovia = true;
				if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
					this.vpisvane.getEventDeinostFuraji().setSklad(BabhConstants.CODE_ZNACHENIE_NE);
				}
			}
		}
		
		//търговия на фуражни добавки по чл. 18
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_FR18)) {
			this.predmetDeinDtbl = false;
			this.showObektDeinPnl = true;
			this.checkPredmetDein = true; 			
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_TF_DOBAVKI);
			
			this.zaiavlenieZaTargovia = true;
			if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
				this.vpisvane.getEventDeinostFuraji().setSklad(BabhConstants.CODE_ZNACHENIE_NE);
			}
		}
		
		//производство на фуражни добавки по чл. 18
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PR_FRDOB18)) {
			this.showObektDeinPnl = true;
			this.predmetDeinDtbl = false;
			this.checkPredmetDein = true; 
			this.zaiavlenieZaTargovia = false;
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_PF_DOBAVKI);
		}
				
		//търговия на фуражи от разстояние по чл. 23и
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_F23I)) {
			this.predmetDeinDtbl = false;
			this.showObektDeinPnl = false;
			this.obektDeinWithoutSave = true;
			this.checkPredmetDein = true; 
			this.zaiavlenieZaTargovia = false; 
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_TARGOVIA_RAZSTOIANIE);

		}
		
		//отдаване на склад под наем по чл. 16
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_SKLAD_NAEM)) {
			this.predmetDeinDtbl = false;
			this.checkPredmetDein = true; 
			this.showObektDeinPnl = true;
			this.zaiavlenieZaTargovia = false; 
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_FURAJI_NAEM16);
		}
		
		//внос на фуражи
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_VNOS_FURAJI)) {
			this.predmetDeinDtbl = true;
			this.showObektDeinPnl = false;
			this.obektDeinWithoutSave = true;
			this.checkPredmetDein = true; 
			this.zaiavlenieZaTargovia = false; 
			
			//тук дейността е само една
			if (this.vpisvane.getId() != null) {
				if (this.vpisvane.getEventDeinostFuraji().getVidList() != null && !this.vpisvane.getEventDeinostFuraji().getVidList().isEmpty()) {			
					this.vidDeinVnos = this.vpisvane.getEventDeinostFuraji().getVidList().get(0);
				}
			}
		}
		
		//обработка на растителни масла, мазнини, мастни киселини, биодизел 
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_OBRABOTKA_SRM_18)) {
			this.showObektDeinPnl = true;
			this.obektDeinWithoutSave = false;
			this.checkPredmetDein = false; 
			this.zaiavlenieZaTargovia = false; 
		}
		
		//износ на фуражи за трети страни
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_FURAJI)) {
			this.showObektDeinPnl = false;
			this.obektDeinWithoutSave = true;
			this.predmetDeinDtbl = false;
			this.checkPredmetDein = true; 
			this.zaiavlenieZaTargovia = false; 
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_FURAJNI_PRODUKTI_IZNOS);
			
			//за ново вписване зареждаме по подразбиране Китай
			if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_FURAJI) || this.vpisvane.getId() == null) {
				this.vpisvane.getEventDeinostFuraji().setDarj(BabhConstants.CODE_ZNACHENIE_DARJ_KITAI);
			}
		}
		
		//регистрация на превозвачи
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_REG_PREVOZVACHI)) {
			this.showObektDeinPnl = false;
			this.predmetDeinDtbl = true;
			this.checkPredmetDein = true; 
			this.zaiavlenieZaTargovia = false; 
			
			if (this.vpisvane.getEventDeinostFuraji().getMpsDeinost() != null && !this.vpisvane.getEventDeinostFuraji().getMpsDeinost().isEmpty()) {
				
				this.mpsDeinost = new MpsDeinost();
				this.mpsFuraj = new MpsFuraji();
				this.mpsFuraj.setMpsVidProducts(new ArrayList<>());
				this.mpsDeinost.setMpsFuraji(mpsFuraj);
				this.sastonieList = new ArrayList<>();
				this.sastonieMap = new HashMap<Integer, Integer>();
				
				loadMpsFurajiList();
			}
			
			//TODO тук няма обект на дейност, но трябва да има „Регистрационен номер на оператор“. Това е номер на дейността. Той се генерира от системата - трябва да е даден алгоритъм!      
		}
		
		//търговия/производство на медикаментозни фуражи по чл. 55
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_MEDIKAMENT_FURAJI) || this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_MEDIKAMENT_FURAJI)) {
			this.showObektDeinPnl = false;
			this.obektDeinWithoutSave = true;
			this.predmetDeinDtbl = false;
			this.checkPredmetDein = true;
			this.zaiavlenieZaTargovia = false; 
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_VID_MEDI_FURAJ);
			
			if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_MEDIKAMENT_FURAJI)) {
				if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
					this.vpisvane.getEventDeinostFuraji().setSklad(BabhConstants.CODE_ZNACHENIE_NE);
				}
			}
		}
		
		//търговия на фураж по чл. 16
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_F16)) {
			this.showObektDeinPnl = true;
			this.predmetDeinDtbl = false;
			this.checkPredmetDein = true;			
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_FURAJI_KOMBINIRANI_16);
			
			this.zaiavlenieZaTargovia = true;
			if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
				this.vpisvane.getEventDeinostFuraji().setSklad(BabhConstants.CODE_ZNACHENIE_NE);
			}
		}
		
		//произвдоство на фураж по чл. 16
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_F16)) {
			this.showObektDeinPnl = true;
			this.predmetDeinDtbl = true;
			this.checkPredmetDein = true;
			this.zaiavlenieZaTargovia = false;
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_VID_FURAJ_PREM16);
		}
		
		//производство на комбирани фуражи, съдържащи фуражни добавки и/или премикси, по чл. 16
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_KF_FDOB_PREM_16)) {
			this.showObektDeinPnl = true;
			this.predmetDeinDtbl = true;
			this.checkPredmetDein = true;
			this.zaiavlenieZaTargovia = false; 
		}
		
		//търговия/производство на премикси по чл. 16
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_PREMIKS_16) || this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_PREM_16)) {
			this.showObektDeinPnl = true;
			this.predmetDeinDtbl = false;
			this.checkPredmetDein = true;
			this.zaiavlenieZaTargovia = false; 
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_VID_FURAJ_PREM16);
			
			if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_PREMIKS_16)) {
				this.zaiavlenieZaTargovia = true;
				if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
					this.vpisvane.getEventDeinostFuraji().setSklad(BabhConstants.CODE_ZNACHENIE_NE);
				}
			}
		}
		
		//производство/търговия на фуражни добавки по чл. 16
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_F_DOBAVKI_16) || this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_F_DOBAVKI_16)) {
			this.predmetDeinDtbl = false;
			this.showObektDeinPnl = true;
			this.checkPredmetDein = true;
			this.zaiavlenieZaTargovia = false; 
			loadFurajiPredmetClassif(BabhConstants.CODE_CLASSIF_VID_FURAJNI_DOB16);

			if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_F_DOBAVKI_16)) {
				this.zaiavlenieZaTargovia = true;
				if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
					this.vpisvane.getEventDeinostFuraji().setSklad(BabhConstants.CODE_ZNACHENIE_NE);
				}
			}

		}
		
		//Търговия със СЖП/ПП без задържане пратки на склад
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_SJP_BEZ_SAHRANENIE) && this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_REGTARGOVTSI_SJP)) {
			//дейността я имала в няколко заявления.. взимам предвид и вида на заявлението..
			this.predmetDeinDtbl = true;
			this.showObektDeinPnl = false;	
			this.checkPredmetDein = false;
			this.zaiavlenieZaTargovia = false;

		}
		
		//преработка/обработка на СЖП
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZGARIANE_SJP) || this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_MEJDINNI_DEIN)) {
			this.predmetDeinDtbl = true;
			this.showObektDeinPnl = true;	
			this.checkPredmetDein = true;
			this.zaiavlenieZaTargovia = false; 
		}
		
		//използване на СЖП за научни цели
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_SJP_NAUCHNI_CELI)) {
			this.predmetDeinDtbl = true;
			this.showObektDeinPnl = true;	
			this.obektDeinWithoutSave = false;
			this.checkPredmetDein = false;
			this.zaiavlenieZaTargovia = false; 
		}
		
		//износ на СЖП за трети държави
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_SJP)) {
			this.predmetDeinDtbl = true;
			this.showObektDeinPnl = false;
			this.obektDeinWithoutSave = true;
			this.checkPredmetDein = true;
			this.zaiavlenieZaTargovia = false; 
			
			if (this.vpisvane.getEventDeinostFuraji() != null && this.vpisvane.getEventDeinostFuraji().getGipList() != null && !this.vpisvane.getEventDeinostFuraji().getGipList().isEmpty()) {
				this.gip = this.vpisvane.getEventDeinostFuraji().getGipList().get(0);
			}
		}
		
		//деконтаминация/детоксикация
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_FIZ_DEKON_F18)) {
			this.predmetDeinDtbl = false;
			this.showObektDeinPnl = true;
			this.checkPredmetDein = false;
			this.zaiavlenieZaTargovia = false;
		}
		
		//прилагане на подмерки
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_STAN_PODMERKI)) {
			this.showObektDeinPnl = false;		
			this.checkPredmetDein = false;
			this.obektDeinWithoutSave = true;
			this.jivotniList = new ArrayList<>();
			this.zaiavlenieZaTargovia = false; 
			
			//добавям животните в чекбокс менюто на екрана..
			if (this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv() != null && !this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv().isEmpty()) {
				for (EventDeinostFurajiPrednaznJiv tmpJiv : this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv()) {
					this.jivotniList.add(tmpJiv.getVidJiv());
					this.jivotniMap.put(tmpJiv.getVidJiv(), tmpJiv.getId()); // запазвам състоянието на обектите и техните ид-та
				}
			}
		}
		
		//използване на СЖП/ПП по чл.23е
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARGAVOIA_SJP_23)) {
			this.predmetDeinDtbl = false;
			this.showObektDeinPnl = false;
			this.checkPredmetDein = false;
			this.obektDeinWithoutSave = true;
			this.jivotniList = new ArrayList<>();
			this.zaiavlenieZaTargovia = false; 
			
			//номерът на обекта не е задължителен при заявлението. на екрана също не е задължителен, но трябва да може да се впише по-късно
			//ако вече е записано заявление без да се въведе обект.. при зареждане на заявлението добавям нов обект
			if (this.vpisvane.getId() != null && (this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0) == null || this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost() == null) ) {
				this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).setObektDeinost(new ObektDeinost());
			}
			
			//добавям животните в чекбокс менюто на екрана..
			if (this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv() != null && !this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv().isEmpty()) {
				for (EventDeinostFurajiPrednaznJiv tmpJiv : this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv()) {
					this.jivotniList.add(tmpJiv.getVidJiv());
					this.jivotniMap.put(tmpJiv.getVidJiv(), tmpJiv.getId()); // запазвам състоянието на обектите и техните ид-та
				}
			}
		}
		
		//издаване на сертификат за износ на фуражи
		if (this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_DEIN_CERT_IZNOS_FURAJI_53)) {
			this.predmetDeinDtbl = true;
			this.showObektDeinPnl = false;
			this.obektDeinWithoutSave = true;
			this.checkPredmetDein = true;
			this.zaiavlenieZaTargovia = false; 
			
			if (this.vpisvane.getEventDeinostFuraji() != null && this.vpisvane.getEventDeinostFuraji().getGipList() != null && !this.vpisvane.getEventDeinostFuraji().getGipList().isEmpty()) {
				this.gip = this.vpisvane.getEventDeinostFuraji().getGipList().get(0);
			}
		}
		
		//ако не задържат пратките на склад обекта на дейност не се записва и не се генерира рег.номер
		if (this.zaiavlenieZaTargovia && this.vpisvane.getEventDeinostFuraji().getSklad() != null && this.vpisvane.getEventDeinostFuraji().getSklad().equals(BabhConstants.CODE_ZNACHENIE_NE)) {
			this.obektDeinWithoutSave = true;
		}
		
		if (this.showObektDeinPnl | this.obektDeinWithoutSave) {
			
			//задавам на основния обект, че тук няма въвеждане на нов обект на дейност, а само ще правим връзка със съществуващ в базата
			if (this.obektDeinWithoutSave) {
				this.vpisvane.getEventDeinostFuraji().setSaveObektDeinost(false);
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost() == null || this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().isEmpty()) {
				ObektDeinostDeinost obektDeinDein = new ObektDeinostDeinost();
				ObektDeinost tmpObekt = new ObektDeinost();
				tmpObekt.setDarj(BabhConstants.CODE_ZNACHENIE_BULGARIA);
				obektDeinDein.setObektDeinost(tmpObekt);
				this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().add(obektDeinDein);		
			}
			
			// има определена ситуация в която вписването идва със списък, но в него няма обект.. инициализирам такъв за да не гърми екрана
			if (showObektDeinPnl | obektDeinWithoutSave) {
				if (this.vpisvane.getId() != null && (this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0) == null || this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost() == null) ) {
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).setObektDeinost(new ObektDeinost());
				}
			}
		
			//в тази ситуация имаме вече вписване и ТЪРСЕНЕ на обект на дейност
			if (!this.zaiavlenieZaTargovia & !this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARGAVOIA_SJP_23)) { // в тази ситуация може и да няма обект на дейност изобщо
				
				if (this.obektDeinWithoutSave && this.vpisvane.getId() != null && this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getId() != null) {
					this.foundedObektDein = true; // и това трябва да е тру, за да позволи запис
				}
			} 
		}
	}
	


	
	/**
	 * Зарежда списъка с MpsFuraji при отваряне на вписването за актуализация
	 * */
	private void loadMpsFurajiList () {
		try {

			for (MpsDeinost mpsDein : this.vpisvane.getEventDeinostFuraji().getMpsDeinost()) {
					
				MpsFuraji tmpMpsFuraj = searchMpsFuraji(mpsDein.getMpsId());
				mpsDein.setMpsFuraji(tmpMpsFuraj);
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на списък с транспортни средства", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на списък с транспортни средства!");
			
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	
	private void loadFurajiPredmetClassif(Integer codeCl) {
		try {
			
			for (EventDeinostFurajiPredmet furajiPredmet : this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet()) {
				
				this.vidFurajiList.add(furajiPredmet.getVid());		
				SystemClassif scItem = new SystemClassif();
				scItem.setCode(furajiPredmet.getVid());
				scItem.setTekst(getSystemData().decodeItem(codeCl, furajiPredmet.getVid(), getCurrentLang(), new Date()));
				this.vidFurajiClassif.add(scItem);
				this.vidFurajiMap.put(furajiPredmet.getVid(), furajiPredmet.getId()); // запазвам състоянието на обектите и техните ид-та							
			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на  на дейност!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на  на дейност! ");
		}
	}
	
	/****
	 * ПРИ СМЯНА НА ПОЛЕТО "ЗАДЪРЖАТ ЛИ СЕ ПРАТКИТЕ НА СКЛАД" АКО Е ИМАЛО НЕЩО ВЪВЕДЕНО ВЪТРЕ.. ДА ГО ЗАЧИСТИ
	 * *****/
	public void actionChangeSahrFuraj() {
		try {
			
			if (this.vpisvane.getId() != null && this.vpisvane.getEventDeinostFuraji().getSklad().equals(BabhConstants.CODE_ZNACHENIE_NE)) {
				
				ObektDeinost tmpObekt = new ObektDeinost();
				tmpObekt.setDarj(BabhConstants.CODE_ZNACHENIE_BULGARIA);
				
				this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).setObektDeinost(tmpObekt);

			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при зачистване на обект на дейност!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зачистване на обект на дейност! ");
		}
	}

	
	
	public void actionSave() {
	
		boolean newVp = this.vpisvane.getId() == null;
		try {

			if (checkDataVp()) {
				
				if (!predmetDeinDtbl) {
					addEventDeinFurajiPredmet();
				}
				
				//при тази дейност животните са само с чекбокс..
				if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_STAN_PODMERKI) || this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARGAVOIA_SJP_23)) {
					actionAddJivList();
					
					//подменям стойностите в мап-а, за да няма разминаване, ако натиснат запис втори път..
					this.jivotniMap.clear();
					if (this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv() != null && !this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv().isEmpty()) {
						for (EventDeinostFurajiPrednaznJiv vidJiv : this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv()) {				
							this.jivotniMap.put(vidJiv.getVidJiv(), vidJiv.getId()); // запазвам състоянието на обектите и техните ид-та					
						}
					}
				}
								
				//при тези дейности ГИП-а е единичен избор..
				if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_SJP) || (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_FURAJI) && this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_SERT_IZNOS_FURAJ)) ) {
					if (this.vpisvane.getEventDeinostFuraji().getGipList() == null) {
						 this.vpisvane.getEventDeinostFuraji().setGipList(new ArrayList<>());
					}
					
					//идваме от редакция
					if (!this.vpisvane.getEventDeinostFuraji().getGipList().isEmpty()) {
						 this.vpisvane.getEventDeinostFuraji().getGipList().set(0, gip); 
						 
					//ново вписване
					 } else {
						 this.vpisvane.getEventDeinostFuraji().getGipList().add(gip);
					 }
				}
				
				
				this.babhDoc.setKachestvoLice(referent1.getKachestvoLice());
				this.babhDoc.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN);					
				
				//TODO временно решение..
				this.referent2 = ((RefCorrespData) FacesContext.getCurrentInstance().getViewRoot().findComponent("targovtsiFurajForm").findComponent("registerTabs:liceZaqvitel")).getRef();

				
				JPA.getUtil().runInTransaction(() -> this.vpisvane = this.vpisvaneDAO.save(vpisvane, babhDoc, referent1, referent2, null, sd));
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG));	


				// заключваме вписването
				if (newVp) {
					lockDoc(this.vpisvane.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);	
					checkDaljimaSuma();
				}
				
				
				//подменям и подменям стойностите в мап-овете, за да няма разминаване, ако натиснат запис втори път..
				if (!predmetDeinDtbl) {					
					this.vidFurajiMap.clear();
					if (this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet() != null && !this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet().isEmpty()) {
						for (EventDeinostFurajiPredmet predmetDein : this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet()) {				
							this.vidFurajiMap.put(predmetDein.getVid(), predmetDein.getId()); // запазвам състоянието на обектите и техните ид-та					
						}
					}
				}
				
				if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARGAVOIA_SJP_23) || this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_STAN_PODMERKI)) {
					this.jivotniMap.clear();
					if (this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv() != null && !this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv().isEmpty()) {
						for (EventDeinostFurajiPrednaznJiv prednazJiv : this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv()) {				
							this.vidFurajiMap.put(prednazJiv.getVidJiv(), prednazJiv.getId()); // запазвам състоянието на обектите и техните ид-та					
						}
					}
				}
				
				if (this.vpisvane.getId() != null && this.obektDeinWithoutSave) {
					this.vpisvane.getEventDeinostFuraji().setSaveObektDeinost(false);
				}
			}

		
		} catch (ObjectInUseException e) {
			LOGGER.error("Грешка при запис на вписване! ObjectInUseException ");
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		} catch (BaseException e) {
			LOGGER.error("Грешка при запис на вписване! BaseException", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Грешка при запис на вписване! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
		
	}
	
	private void actionAddJivList() {
		
		this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv().clear();
		if (this.jivotniList != null && !this.jivotniList.isEmpty()) {
			
			for (Integer jivCode : this.jivotniList) {
				EventDeinostFurajiPrednaznJiv tmpJiv = new EventDeinostFurajiPrednaznJiv();
				tmpJiv.setId(jivotniMap.get(jivCode)); // ако намери съвпадение в мап-а.. запазваме неговото ид..			
				tmpJiv.setVidJiv(jivCode);
				tmpJiv.setEventDeinostFurajiId(this.vpisvane.getEventDeinostFuraji().getId());

				this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv().add(tmpJiv);
			}
		} 
	}
	
	
	public void addEventDeinFurajiPredmet() {
		try {
			
			if (this.vidFurajiList != null && !this.vidFurajiList.isEmpty()) {
				
				List<EventDeinostFurajiPredmet> eventDeinFurajiPredmetList = new ArrayList<>();
				EventDeinostFurajiPredmet deinFurajiPredmet;	
				
				if (this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet() != null && !this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet().isEmpty()) {
					for (Iterator<Integer> iterator = vidFurajiList.iterator(); iterator.hasNext();) {
						Integer code = iterator.next();
						
						deinFurajiPredmet = new EventDeinostFurajiPredmet();
						deinFurajiPredmet.setId(vidFurajiMap.get(code)); // ако намери съвпадение в мап-а.. запазваме неговото ид..								
						deinFurajiPredmet.setVid(code);
						eventDeinFurajiPredmetList.add(deinFurajiPredmet);
					}
					
				} else {
					
					for (Iterator<Integer> iterator = vidFurajiList.iterator(); iterator.hasNext();) {
						Integer code = iterator.next();
						
						deinFurajiPredmet = new EventDeinostFurajiPredmet();
						deinFurajiPredmet.setVid(code);
						eventDeinFurajiPredmetList.add(deinFurajiPredmet);
					}
				}

				this.vpisvane.getEventDeinostFuraji().setEventDeinostFurajiPredmet(eventDeinFurajiPredmetList);
			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при добавяне на обектите в EventDeinostFurajiPredmet! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}
	
	private boolean checkIdentifFZL(boolean rez, String egn, String lnch, String errLabel,  String  errMsg) {
		boolean b1 = SearchUtils.isEmpty(egn);
		boolean b2 = SearchUtils.isEmpty(lnch);
		if( b1 && b2 ){ 
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
	
	
	public boolean checkDataVp() {
		boolean save = true;
		
		referent1 = getBindingReferent1().getRef();
		if (SearchUtils.isEmpty(referent1.getRefName())) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":registerTabs:licеPоdatel:imeZaiavitel", FacesMessage.SEVERITY_ERROR, "Моля, въведете име на представляващото лице!");
		}
		
		//Проверката не важи за тези от миграция
		// 0 - миграция, ръчно въвеждане,1 - миграция - завършена, 2 - не е миграция;
		if(vpisvane.getFromМigr() == null || Objects.equals(vpisvane.getFromМigr(),BabhConstants.CODE_ZNACHENIE_MIGR_NO)) {
			save = checkIdentifFZL(save, referent1.getFzlEgn(), referent1.getFzlLnc(), IDFORM + ":registerTabs:licеPоdatel:egn", getMessageResourceString(beanMessages, "podatel.msgValidIdentFZL"));
		}
		
		if (referent1.getKachestvoLice() == null) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":registerTabs:licеPоdatel:kachestvoZaqvitel", FacesMessage.SEVERITY_ERROR, "Моля, посочете качество на представляващото лице!");
		}
		
		

		referent2 = getBindingReferent2().getRef();		
		if(SearchUtils.isEmpty(referent2.getRefName())) { 
			save = false;
			if (referent2.getRefType().equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL)) {
				JSFUtils.addMessage(IDFORM+":registerTabs:liceZaqvitel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "zaiavitel.msgName"));
			} else {
				JSFUtils.addMessage(IDFORM+":registerTabs:liceZaqvitel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "zaiavitel.msgNameNFZL"));
			}
		}
		
		if(vpisvane.getFromМigr() == null || Objects.equals(vpisvane.getFromМigr(),BabhConstants.CODE_ZNACHENIE_MIGR_NO)) {
			if(referent2.getRefType().equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL)) {
				save = checkIdentifFZL(save, referent2.getFzlEgn(), referent2.getFzlLnc(), IDFORM+":registerTabs:liceZaqvitel:egn", 	getMessageResourceString(beanMessages, "zaiavitel.msgValidIdentFZL"));
			} else {
				save = checkIdentifNFZL(save, referent2.getNflEik(), referent2.getFzlLnEs(), IDFORM+":registerTabs:liceZaqvitel:eik", getMessageResourceString(beanMessages, "zaiavitel.msgValidIdentNFZL"));
			}
		}
		
		if (!SearchUtils.isEmpty(referent2.getContactEmail()) && !ValidationUtils.isEmailValid(referent2.getContactEmail())) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":registerTabs:liceZaqvitel:contactEmail", FacesMessage.SEVERITY_ERROR, "Невалиден формат на имейл адрес!");
		}

		if (this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_ODOB_OPERATORI_18) 
				| this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_REG_OPERATORI_16)
				| this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_MEDIKAMENTOZNI_FURAJI)
				| this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_IZNOS_FURAJI)) {
			if (this.babhDoc.getDocPodVid() == null) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.podvidZaiav")));
			}
		}
		
		if (this.vpisvane.getEventDeinostFuraji().getVidList() == null || this.vpisvane.getEventDeinostFuraji().getVidList().isEmpty()) {
			save = false;
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.selectOneDein"));
		}
		
		if (!this.zaiavlenieZaTargovia | (this.vpisvane.getEventDeinostFuraji().getSklad() != null && this.vpisvane.getEventDeinostFuraji().getSklad().equals(BabhConstants.CODE_ZNACHENIE_DA))) {
			if (this.showObektDeinPnl) {
				
				for (ObektDeinostDeinost obektDeinostDeinost : this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost()) {

					if (obektDeinostDeinost.getObektDeinost().getDarj() == null) {	
						save = false;
						JSFUtils.addMessage(IDFORM + ":registerTabs:cntryC", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "registerMainTab.country")));	
						
					} else if (obektDeinostDeinost.getObektDeinost().getDarj() == BabhConstants.CODE_ZNACHENIE_BULGARIA && obektDeinostDeinost.getObektDeinost().getNasMesto() == null) {
						save = false;
						JSFUtils.addMessage(IDFORM + ":registerTabs:mestoC", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, "населено място на обект!"));				
					}
				}
			}
		}
		
		if (checkPredmetDein) { //за дейности с растителни масла, мазнини и биодизел и издаване на сертификат  няма предмет на дейността..
			
			//при тези дейности имаме избор от selectManyModalA
			if (predmetDeinDtbl) {
				if (this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet() == null || this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet().isEmpty()) {
					save = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.selectOneFuraj"));
				}
				
			// а при тези от таблица	
			} else {
				if (this.vidFurajiList == null || this.vidFurajiList.isEmpty()) {
					save = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.selectOneFuraj"));
				}
			}
		}

		
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_FR_PROIZV_KF18) 
				|| vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PR_PREM18)
				|| vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_PREMIKS18)
				|| vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_MEDIKAMENT_FURAJI)
				|| vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_MEDIKAMENT_FURAJI)
				|| vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_KF_FDOB_PREM_16)) {
			
			if (this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv() == null || this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv().isEmpty()) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.selectOneAnimal"));
			}
		}	
		
		save = checkDataVpDein(save);
		
		return save;
	}
	
	private boolean checkDataVpDein(boolean save) {

		//Производство на комбинирани фуражи по чл. 18
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_FR_PROIZV_KF18)) {

			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPS", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}

			if (this.vpisvane.getEventDeinostFuraji().getBroiTehnLinii() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:brLinii", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.brLinii")));
			}			
		}
		
		//ПРОИЗВОДСТВО НА ПРЕМИКСИ ПО ЧЛ. 18
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PR_PREM18)) {
							
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSPrPrem", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
										getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}
		}
		
		//ТЪРГОВИЯ НА ПРИМЕКСИ ПО ЧЛ. 18
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_PREMIKS18)) {
	
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSPrPrim", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
							getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.furajiNaSklad")));
			}
			
			//ако са въвели обект и са посочили, че задържат пратки на склад не трябва да се позволява запис
			if (this.vpisvane.getEventDeinostFuraji().getSklad().equals(BabhConstants.CODE_ZNACHENIE_DA) &&
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getDarj() == null &&
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getNasMesto() == null) {
				
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.zadarjNaPratki" ));
			}
		}
		
		//ТЪРГОВИЯ С ФУРАЖНИ ДОБАВКИ ПО ЧЛ. 18
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_FR18)) {
			
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSTrgFr", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.furajiNaSklad")));
			}	
			
			//ако са въвели обект и са посочили, че задържат пратки на склад не трябва да се позволява запис
			if (this.vpisvane.getEventDeinostFuraji().getSklad().equals(BabhConstants.CODE_ZNACHENIE_DA) &&
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getDarj() == null &&
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getNasMesto() == null) {
				
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.zadarjNaPratki" ));
			}
		}
		
		//ПРОИЗВОДСТВО НА ФУРАЖНИ ДОБАВКИ ПО ЧЛ. 18
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PR_FRDOB18)) {
					
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSPrFrDob", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}
		}
		
		//ТЪРГОВИЯ С ФУРАЖИ ОТ РАЗСТОЯНИЕ ПО ЧЛ. 23и
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_F23I)) {
							
			if (this.vpisvane.getEventDeinostFuraji().getSide() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getSide())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:elAdresTrgF23i", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.elAdres")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getUrl() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getUrl())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:iPageTrgF23i", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.integetPage")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getTel() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getTel())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:telF23i", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "registerMainTab.tel")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getAddress() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getAddress())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:pAdresF23i", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "regTargovtsiFuraj.postAdres")));
			}
			
			
			if (SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom())) {
				save = false;				
				JSFUtils.addMessage(IDFORM + ":registerTabs:nomRbF23i", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.regNobekt")));
			
			//ако няма открит обект на дейност с този номер не се позволява запис
			} else if (!SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom()) && !this.foundedObektDein) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(LABELS, "regTargovtsiFuraj.notFoundedObektDein"));
			}
		}
		
		//ДЕКОНТАМИНАЦИЯ-ФИЗИЧНА ПО ЧЛ. 18
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_FIZ_DEKON_F18)) {
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSFizDekon18", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getInstallationType() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:instalFizDekon18", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "regTarvotsiFuraj.vidInstal")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getOpisanie() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getOpisanie())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:opisFizDekon18", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.opisanie")));
			}
		}
		
		
		//ВНОС НА ФУРАЖИ
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_VNOS_FURAJI)) {
			if (this.vpisvane.getEventDeinostFuraji().getDarj() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:cntryC", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "registerMainTab.country")));
			}
					
			//if (this.vpisvane.getEventDeinostFuraji().getNaimenovanie() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getNaimenovanie())) {
				//save = false;
				//JSFUtils.addMessage(IDFORM + ":registerTabs:kontrVnIz", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
										//getMessageResourceString(LABELS, "targovtsiFuraj.kontragent")));
			//}
		
			if (this.vpisvane.getEventDeinostFuraji().getAddress() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getAddress())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:kontrAdVnIz", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
									getMessageResourceString(LABELS, "targovtsiFuraj.kontAdres")));
			}
			
			
			if (SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:regNVnIz", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
							getMessageResourceString(LABELS, "targovtsiFuraj.regNobekt")));
			
			//ако няма открит обект на дейност с този номер не се позволява запис
			} else if (!SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom()) && !this.foundedObektDein) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(LABELS, "regTargovtsiFuraj.notFoundedObektDein"));
			}
		}
		
		//Търговия на СЖП/ПП от категория 3 по чл. 17
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_SJP17)) {
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSSJP", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}		
		}
		
		//Обработка на сурово растително масло по чл. 18
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_OBRABOTKA_SRM_18)) {
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSSRM", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
										getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}	
			
			if (this.vpisvane.getEventDeinostFuraji().getOpisanie() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getOpisanie())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:opisSmaslo", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.opisanie")));
			}
		}
		
		//Износ на фураж
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_FURAJI) && this.babhDoc.getDocPodVid().equals((BabhConstants.CODE_ZNACHENIE_ZAIAV_ODOB_PREDPRIIATIA_IZNOS))) {
			if (this.vpisvane.getEventDeinostFuraji().getDarj() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:cntryC", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.darjIznos")));
			}
			
			if (SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:regNobektIznos", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
							getMessageResourceString(LABELS, "regTargovtsiFuraj.regNobekt")));
			
			//ако няма открит обект на дейност с този номер не се позволява запис
			} else if (!SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom()) && !this.foundedObektDein) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(LABELS, "regTargovtsiFuraj.notFoundedObektDein"));
			}
			
		}
		
		//Производство на СЖП
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_SJP)) {
			if (this.vpisvane.getEventDeinostFuraji().getCel() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getCel())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sjpCel", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.celSJP")));
			}		
		}
		
		//РЕГИСТРАЦИЯ НА ПРЕВОЗВАЧИ
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_REG_PREVOZVACHI)) {					
			if (this.vpisvane.getEventDeinostFuraji().getMpsDeinost() == null || this.vpisvane.getEventDeinostFuraji().getMpsDeinost().isEmpty()) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.selectOneMPS"));
			}
		}
		
		//ТЪРГОВИЯ С МЕДИКАМЕНТОЗНИ ФУРАЖИ ПО ЧЛ.55
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_MEDIKAMENT_FURAJI)) {	

			if (SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom())) {
				save = false;
				
				JSFUtils.addMessage(IDFORM + ":registerTabs:nomRbTrMedi", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.regN20")));
			
			//ако няма открит обект на дейност с този номер не се позволява запис
			} else if (!SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom()) && !this.foundedObektDein) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(LABELS, "regTargovtsiFuraj.notFoundedObektDein"));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSTrMedi", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.furajiNaSklad")));
			}
		}
		
		//ПРОИЗВОДСТВО НА МЕДИКАМЕНТОЗНИ ФУРАЖИ ПО ЧЛ.55
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_MEDIKAMENT_FURAJI)) {	
			
			if (SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:nomRbProizMedi", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
							getMessageResourceString(LABELS, "regTargovtsiFuraj.regN20")));
			
			//ако няма открит обект на дейност с този номер не се позволява запис
			} else if (!SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom()) && !this.foundedObektDein) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(LABELS, "regTargovtsiFuraj.notFoundedObektDein"));
			}
					
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSProizMedi", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}	
			
			if (this.vpisvane.getEventDeinostFuraji().getBroiTehnLinii() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:brLiniiProizMedi", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "targovtsiFuraj.brLinii")));
			}	
		}
		
		//ТЪРГОВИЯ НА ФУРАЖИ ПО ЧЛ. 16
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_F16)) {	
						
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSTrF16", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}	
			
			if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.furajiNaSklad")));
			}	
			
			//ако са въвели обект и са посочили, че задържат пратки на склад не трябва да се позволява запис
			if (this.vpisvane.getEventDeinostFuraji().getSklad().equals(BabhConstants.CODE_ZNACHENIE_DA) &&
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getDarj() == null &&
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getNasMesto() == null) {
				
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.zadarjNaPratki" ));
			}
		}
		
		//ПРОИЗВОДСТВО НА ФУРАЖИ ПО ЧЛ. 16
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_F16)) {	
								
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSProizF16", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}	
							
			if (this.vpisvane.getEventDeinostFuraji().getBroiFurSurovini() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:brSurovProizF16", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.brSurovini")));
				}	
		}
		
		//ПРОИЗВОДСТВО НА КОМБ. ФУРАЖИ, СЪДЪРЖАЩИ ФУРАЖНИ ДОБАВКИ ПО ЧЛ. 16
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_KF_FDOB_PREM_16)) {	
										
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSProizKF16", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}	
									
			if (this.vpisvane.getEventDeinostFuraji().getBroiTehnLinii() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:brLiniiProizKF16", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.brLinii")));
			}	
		}
		
		//производство на премикси по чл. 16, ал. 1 от ЗФ
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_PREM_16) ) {								
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSTargPrem16", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
					getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}
		}
		
		//търговия на премикси по чл. 16, ал. 1 от ЗФ
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_PREMIKS_16)) {
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSTargPrem16", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
					getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.furajiNaSklad")));
			}	
			
			//ако са въвели обект и са посочили, че задържат пратки на склад не трябва да се позволява запис
			if (this.vpisvane.getEventDeinostFuraji().getSklad().equals(BabhConstants.CODE_ZNACHENIE_DA) &&
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getDarj() == null &&
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getNasMesto() == null) {
				
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.zadarjNaPratki" ));
			}
		} 
		
		//производство на фуражни добавки по чл. 16, ал. 1 от ЗФ
		if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_PROIZ_F_DOBAVKI_16)) {								
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSTargFDob16", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}									
		}
		
		//Търговия на фуражни добавки по чл. 16, ал. 1 от ЗФ
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TRG_F_DOBAVKI_16)) {								
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSTargFDob16", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.furajiNaSklad")));
			}	
			
			//ако са въвели обект и са посочили, че задържат пратки на склад не трябва да се позволява запис
			if (this.vpisvane.getEventDeinostFuraji().getSklad().equals(BabhConstants.CODE_ZNACHENIE_DA) &&
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getDarj() == null &&
					this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getNasMesto() == null) {
				
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.zadarjNaPratki" ));
			}
		}
		
		//Търговия със СЖП/ПП без задържане пратки на склад
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARG_SJP_BEZ_SAHRANENIE) && this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_REGTARGOVTSI_SJP)) {								
			if (this.vpisvane.getEventDeinostFuraji().getSklad() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:sahrPratki", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.sahraneniePratki")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getKategoriaList() == null || this.vpisvane.getEventDeinostFuraji().getKategoriaList().isEmpty()) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:category", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTarvotsiFuraj.kategoriaSJP")));
			}
		}
		
		//Използване на СЖП за научни цели
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_SJP_NAUCHNI_CELI)) {								
			if (this.vpisvane.getEventDeinostFuraji().getCelList() == null || this.vpisvane.getEventDeinostFuraji().getCelList().isEmpty()) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:deinCel", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "regTargovtsiFuraj.deinCeli")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getKategoriaList() == null || this.vpisvane.getEventDeinostFuraji().getKategoriaList().isEmpty()) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:category", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
					getMessageResourceString(LABELS, "regTarvotsiFuraj.kategoriaSJP")));
			}
			
		}
		
		//Издаване на сертификат за добра практика
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_CERT_DOBRA_PRAKTIKA)) {			
			if (this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiSert() == null || this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiSert().isEmpty()) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.selectOneSert"));
			}		
		}
		
		//Сертификат за износ на СЖП/ПП
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_SJP)) {
			if (this.vpisvane.getEventDeinostFuraji().getDarj() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:darjSJPiznos", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "registerMainTab.country")));
			}	
			
			
			if (SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:fromObektSJPiznos", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
							getMessageResourceString(LABELS, "regTargovtsiFuraj.otObekt")));
			
			//ако няма открит обект на дейност с този номер не се позволява запис
			} else if (!SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom()) && !this.foundedObektDein) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(LABELS, "regTargovtsiFuraj.notFoundedObektDein"));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getNomPartida() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getNomPartida())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:partidaIznos", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.partida")));
			}
			
			if (this.gip == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:gipSJP", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.gip")));
			}
			
			if (SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getNomObektDrug())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:regNKont", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
							getMessageResourceString(LABELS, "regTargovtsiFuraj.regNomIznos")));

			}
			
			if (this.vpisvane.getEventDeinostFuraji().getDopInfo() == null || SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getDopInfo())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:dopInfoSJPinzos", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.dopUslovia")));
			}	
		}
		
		//Прилагане на подмерки
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_STAN_PODMERKI)) {
			if (this.vpisvane.getEventDeinostFuraji().getMiarka() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:vidPodmiarka", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.podmiarka")));
			} else if (this.vpisvane.getEventDeinostFuraji().getMiarka().equals(BabhConstants.CODE_ZNACHENIE_MIARKA_5_1) && this.jivotniList.isEmpty()) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:jivPodmiarka", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.docAnimals")));
			}
			
			//задължително трябва да има въведен рег. номер на обект ИЛИ местонахождение
			//!!!! тук може да няма рег. номер на обекта.. в такъв случай се въвежда населено място и адрес
			if (SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom()) && this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getNasMesto() == null && SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getAddress())) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(LABELS, "regTargovtsiFuraj.podmerkiPlsIns"));
			}
		}
		
		//Използване на СЖП по чл.23е
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_TARGAVOIA_SJP_23)) {	

			//ако няма открит обект на дейност с този номер не се позволява запис
			if (!SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom()) && !this.foundedObektDein) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(LABELS, "regTargovtsiFuraj.notFoundedObektDein"));
			}
					
			if (this.vpisvane.getEventDeinostFuraji().getPolzvaneMps() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:tipMPSsjp", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.tipMPS")));
			}	
			
			if (this.jivotniList == null || this.jivotniList.isEmpty()) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:vidJivIz", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.jivotni")));
			}
			
			if (this.vpisvane.getEventDeinostFuraji().getDopInfo() == null || this.vpisvane.getEventDeinostFuraji().getDopInfo().isEmpty()) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:jivDopInfo", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "targovtsiFuraj.jivDopInfo")));
			}
		}
		
		//издаване на сертификат за износ на фураж
		if (vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_FURAJI) && this.babhDoc.getDocVid().equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_SERT_IZNOS_FURAJ)) {
			if (this.vpisvane.getEventDeinostFuraji().getDarj() == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:cntryC", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.darjIznos")));
			}
			
			
			if (SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom())) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:regNobektIznosF", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
							getMessageResourceString(LABELS, "regTargovtsiFuraj.otObekt")));
			
			//ако няма открит обект на дейност с този номер не се позволява запис
			} else if (!SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom()) && !this.foundedObektDein) {
				save = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(LABELS, "regTargovtsiFuraj.notFoundedObektDein"));
			}
			
			if (this.gip == null) {
				save = false;
				JSFUtils.addMessage(IDFORM + ":registerTabs:gipSJPIz", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS,
						getMessageResourceString(LABELS, "regTargovtsiFuraj.gip")));
			}
		}

		
		return save;
	}
	
	
	
	/******************************************** МЕТОДИ, СВЪРЗАНИ С ВИДОВЕ ЖИВОТНИ, ЗА КОИТО Е ПРЕДНАЗНАЧЕН ФУРАЖА ***********************************************************/

	/**
	 * визуализиране на диалоговия прозорец за въвеждане на нов вид животно
	 */
	public void actionShowJivDlg() {
		this.prednaznachenieJiv = new EventDeinostFurajiPrednaznJiv();
	}
	
	
	/**
	 * добавяне на вид животно
	 */
	public void actionAddJiv() {
		try {
			
			if (checkVidJivDlg()) {
				
				if (this.newDeinJiv == BabhConstants.CODE_ZNACHENIE_DA) { 
					this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv().add(this.prednaznachenieJiv);
				}
				
				String dialog = "PF('modalAnimalVar').hide();";
				PrimeFaces.current().executeScript(dialog);
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при добавяне на вид животно", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при добавяне на вид животно");
		} 
	}
	
	private boolean checkVidJivDlg() {
		boolean addAnimal = true;

		if (this.prednaznachenieJiv.getVidJiv() == null) {
			addAnimal = false;
			JSFUtils.addMessage(IDFORM + ":vidJiv", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "targovtsiFuraj.vidJivotno")));
			
		} else if (!this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv().isEmpty() && this.newDeinJiv == BabhConstants.CODE_ZNACHENIE_DA) {
			for (EventDeinostFurajiPrednaznJiv vidJiv : vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv()) {
					
				//избраното животно вече го имаме въведено
				if (vidJiv.getVidJiv().equals(this.prednaznachenieJiv.getVidJiv())) {
					addAnimal = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Избрания вид животно вече е въведен за този обект!");
				}
			}
		}
		
		if (this.prednaznachenieJiv.getKolichestvo() == null || SearchUtils.isEmpty(this.prednaznachenieJiv.getKolichestvo())) {
			addAnimal = false;
			JSFUtils.addMessage(IDFORM + ":kolJiv", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "targovtsiFuraj.kolichestvo")));
		}
		
		return addAnimal;
		
	}
	
	/** ИЗТРИВАНЕ НА ВИД ЖИВОТНО ОТ СПИСЪКА */
	public void actionDeleteJiv() {
		try {
			
			if (this.prednaznachenieJiv != null) {
				this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPrednaznJiv().remove(prednaznachenieJiv);
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при изтриване на вид животно", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при изтриване на вид животно");
		}
	}
	
	
	
	/******************************************** МЕТОДИ, СВЪРЗАНИ С ПРЕДМЕТИ НА ДЕЙНОСТТА ***********************************************************/
	
	/**
	 * визуализиране на диалоговия прозорец за въвеждане на нов вид фураж
	 */
	public void actionShowFurajDlg(boolean showPredn) {
		this.showPredn = showPredn;
		this.predmetDeinost = new EventDeinostFurajiPredmet();
	}
	
	/**
	 * добавяне на вид фураж
	 */
	public void actionAddFuraj() {
		try {
			
			if (checkFurajDlg()) {
				
				if (this.newDeinPredmet == BabhConstants.CODE_ZNACHENIE_DA) { 
					this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet().add(this.predmetDeinost);
				}
				
				String dialog = "PF('modalFurajVar').hide();";
				PrimeFaces.current().executeScript(dialog);
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при добавяне/корекция на вид фураж", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при добавяне/корекция на вид фураж!");
		} 
	}
	
	private boolean checkFurajDlg() {
		boolean addFuraj = true;

		if (this.predmetDeinost.getVid() == null) {
			addFuraj = false;
			JSFUtils.addMessage(IDFORM + ":vidFuraj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(
					UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "targovtsiFuraj.furaj")));
			
		} else if (!this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet().isEmpty() && this.newDeinPredmet == BabhConstants.CODE_ZNACHENIE_DA) {
			
			for (EventDeinostFurajiPredmet predmet : this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet()) {					
				//избрания вид фураж вече е въведен
				if (predmet.getVid().equals(this.predmetDeinost.getVid())) {
					addFuraj = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Избрания вид фураж вече е въведен за този обект!");
				}
			}
		}
		
		return addFuraj;
	}
	
	/** ИЗТРИВАНЕ НА ВИД ФУРАЖ */
	public void actionDeletePredmet() {
		try {
			
			if (this.predmetDeinost != null) {
				this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet().remove(this.predmetDeinost);
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при изтриване на вид фураж", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при изтриване на вид фураж");
		}
	}
	

	
	/******************************************** МЕТОДИ, СВЪРЗАНИ С ОБЕКТИ НА ДЕЙНОСТТА ***********************************************************/

	/**
	 * избор на обект на дейност от компонентата за търсене
	 */
	public void actionSlcObektDein() {
		try {

			
			JPA.getUtil().runWithClose(() -> this.obektDeinost =  new ObektDeinostDAO(this.ud).findById(obektDeinostId));
			
			this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).setObektDeinost(this.obektDeinost);
			this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).setObektDeinostId(this.obektDeinostId);
			this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).setDeinostId(this.vpisvane.getEventDeinostFuraji().getId());
			this.foundedObektDein = true;
			
		} catch (Exception e) {
			LOGGER.error("Грешка при избор на обект на дейност", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при избор на обект на дейност");
		} 
	}
	

	
	/**
	 * потвърждение на избрания обект на дейност 
	 */
	public void actionAddObektDein() {
		try {

			String dialog = "PF('obDS').hide();";
			PrimeFaces.current().executeScript(dialog);
			
			
		} catch (Exception e) {
			LOGGER.error("Грешка при добавяне на обект на дейност", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при добавяне на обект на дейност");
		} 
	}
	
	public void actionChangeObektRegN() {
		try {
			
			this.foundedObektDein = false;
			
			if (!SearchUtils.isEmpty(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom())) {

				JPA.getUtil().runWithClose(() -> {
					ObektDeinostDAO obektDeinDAO = new ObektDeinostDAO(getUserData());
					ObektDeinost tmpObekt = obektDeinDAO.findByRegNomer(this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).getObektDeinost().getRegNom(), BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_FURAJI, false); 
					
					if (tmpObekt != null) {
						
						//на представянето поискаха да има проверка в кое ОДБХ е регистриран обекта и ако е в != от това на потребителя да не им позволява запис
						if (this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_STAN_PODMERKI) && tmpObekt.getNasMesto() != null) {
							
							//TODO ако регистратурата е БАБХ би трябвало да има достъп до всички обекти, нали?
							if (!this.ud.getRegistratura().equals(BabhConstants.CODE_ZNACHENIE_REGISTRATURA_BABH)) {
								
								Integer obektRegistratura = this.vpisvaneDAO.findRegistraturaByEkatte(tmpObekt.getNasMesto());						
								if (obektRegistratura != this.ud.getRegistratura()) {
									String scrolltoTop = "document.body.scrollTop = 0; document.documentElement.scrollTop = 0;";
									PrimeFaces.current().executeScript(scrolltoTop);
								
									JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.obektRegistratura"));
									return;
								}
							}
						}
						
						this.foundedObektDein = true; // за определени заявление но трябва само да знаем дали има такъв обект
						this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).setObektDeinost(tmpObekt); 
				
					} else {
						
						//в Заявление за прилагане на подмерки има разлика и не показваме съобщението.. за него може да няма регистрационен номер на обект и трябва да въведат задължително адрес в такъв случай
						if (!this.vidDeinFuraji.contains(BabhConstants.CODE_ZNACHENIE_DEIN_STAN_PODMERKI)) {
							String scrolltoTop = "document.body.scrollTop = 0; document.documentElement.scrollTop = 0;";
							PrimeFaces.current().executeScript(scrolltoTop);
						
							JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(LABELS, "regTargovtsiFuraj.notFoundedObektDein"));
						}
					}
				});
				
			} else {
				this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost().get(0).setObektDeinost(new ObektDeinost());
			}

			
		} catch (Exception e) {
			LOGGER.error("Грешка при търсене на обект на дейност по регистрационен номер!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при търсене на обект на дейност по регистрационен номер!");
		}
	}
	
	
	
	
	/******************************************** МЕТОДИ, СВЪРЗАНИ С ИЗБОРА НА МПС ***********************************************************/

	/**
	 * визуализиране на диалоговия прозорец за въвеждане на МПС
	 */
	public void actionShowMPSDlg() {
		this.mpsDeinost = new MpsDeinost();
		this.mpsFuraj = new MpsFuraji();
		this.mpsFuraj.setMpsVidProducts(new ArrayList<>());
		this.mpsDeinost.setMpsFuraji(mpsFuraj);
		this.sastonieList = new ArrayList<>();
	}
	
	
	/**
	 * визуализиране на диалоговия прозорец за редакция на МПС
	 */
	public void actionEditMPSDlg() {
		
		this.mpsDeinost = new MpsDeinost();
		this.sastonieList = new ArrayList<>();
		this.sastonieMap.clear();
		
		if (this.mpsFuraj != null && this.mpsFuraj.getMpsVidProducts() != null && !this.mpsFuraj.getMpsVidProducts().isEmpty()) {
			for (MpsVidProducts tmpProd : this.mpsFuraj.getMpsVidProducts()) {
				this.sastonieList.add(tmpProd.getSastoianie());
				this.sastonieMap.put(tmpProd.getSastoianie(), tmpProd.getId()); // запазвам състоянието на обектите и техните ид-та	
			}
		}
	}

	
	/**
	 * Добавяне на МПС
	 */
	public void actionAddMps() {
		try {
			
			if (checkMpsData()) {
				
				this.mpsFuraj.getMpsVidProducts().clear();
				
				this.mpsDeinost.setTablEventDeinost(BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI);
				this.mpsDeinost.setDeinostId(this.vpisvane.getEventDeinostFuraji().getId());
				this.mpsDeinost.setMpsFuraji(this.mpsFuraj);

				if (this.sastonieList != null && !this.sastonieList.isEmpty()) {
					 for (Integer vidProduct : this.sastonieList) {
						 
						 MpsVidProducts tmpProduct = new MpsVidProducts();
						 tmpProduct.setMpsId(this.mpsDeinost.getMpsId());
						 tmpProduct.setId(sastonieMap.get(vidProduct));
						 tmpProduct.setSastoianie(vidProduct);

						 this.mpsDeinost.getMpsFuraji().getMpsVidProducts().add(tmpProduct);

				 }

				 if (this.newDeinMps == BabhConstants.CODE_ZNACHENIE_DA) {
					this.vpisvane.getEventDeinostFuraji().getMpsDeinost().add(this.mpsDeinost);
				 }
				 
				 String dialog = "PF('tsSearch').hide();";
				 PrimeFaces.current().executeScript(dialog);
			}
		}

		} catch (Exception e) {
			LOGGER.error("Грешка при добавяне на МПС!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при добавяне на транспортно средство!");
		}
	}
	
	private boolean checkMpsData() {
		boolean save = true;
		
		if (this.mpsFuraj.getNomer() == null || SearchUtils.isEmpty(this.mpsFuraj.getNomer())) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":regNTS", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "regTarvotsiFuraj.regN")));	
			 
		 } 
		
		if (this.mpsFuraj.getVid() == null) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":mpsType", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "regTarvotsiFuraj.vidTS")));	
		
		 }
		
		if (this.mpsFuraj.getModel() == null || SearchUtils.isEmpty(this.mpsFuraj.getModel())) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":modelModal", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "compMPSsearch.model")));				
		}
		
		 if (this.sastonieList == null || this.sastonieList.isEmpty()) {
			 save = false;
			 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Моля, въведете състояние на превозвания фураж!");
		
		 }

		 if (this.vpisvane.getEventDeinostFuraji().getMpsDeinost() != null && !this.vpisvane.getEventDeinostFuraji().getMpsDeinost().isEmpty() && this.newDeinMps == BabhConstants.CODE_ZNACHENIE_DA) {
			for (MpsDeinost mpsDein : this.vpisvane.getEventDeinostFuraji().getMpsDeinost()) {
				if (mpsDein.getMpsFuraji().getNomer().equals(this.mpsFuraj.getNomer())) {
					save = false;
					JSFUtils.addMessage(IDFORM + ":regNTS", FacesMessage.SEVERITY_ERROR, "Избраното транспортно средство вече е въведено!");	
				}
			}  
		 }
		
		return save;
	}
	
	/** Изтриване на МПС */
	public void actionDeleteMps() {
		try {
			
			if (this.mpsDeinost != null) {
				this.vpisvane.getEventDeinostFuraji().getMpsDeinost().remove(this.mpsDeinost);
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при изтриване на МПС", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при изтриване на транспортно средство!");
		}
	}
	
	
	public void actionChangeRegN() {
		try {
			
			if (this.mpsFuraj.getNomer() == null || SearchUtils.isEmpty(this.mpsFuraj.getNomer())) {
				
				this.mpsFuraj = new MpsFuraji();
				this.mpsFuraj.setMpsVidProducts(new ArrayList<>());
				this.mpsDeinost.setMpsFuraji(mpsFuraj);
				
			} else {
				
				//безсмислено е да пускам търсене за две въведени букчи/цифри.. TODO други номера?
				//if (this.mpsFuraj.getNomer().length() == 8) {
					
					Object[] tmpData = null;
					MpsDAO mpsDAO = new MpsDAO(getUserData());
					
					tmpData = mpsDAO.findMpsInfoByIdOrNomer(null, this.mpsFuraj.getNomer());
					
					//въвели са някакъв номер, който вече го има в базата
					if (tmpData != null && tmpData.length > 0) {
							 
						String mpsId = String.valueOf(tmpData[0]);
						this.mpsFuraj = searchMpsFuraji(Integer.valueOf(mpsId));

						String msg = "Намерена е информация за " + getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_VID_MPS, this.mpsFuraj.getVid(), getCurrentLang(), new Date()) 
								+ " с рег. номер " + this.mpsFuraj.getNomer();
						
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, msg);
					 }
				//}
			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при търсене на транспортно средство по рег. номер!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене на транспортно средство по рег. номер!");
			
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	

	 /**
	 * Проверява дали въведения рег. номер е валиден формат за територията на България: две големи букви, последвани от 4 цифри, следвани от две големи букви
	 * 
	 * TODO трябва ли ни? очаква ли се да има транзитни или чужди номера?

	private boolean isRegNumValid(String regN) {
		regN = regN.replaceAll("\\s", "").toUpperCase();
		String regex = "^[\\p{IsLatin}]{2}\\d{4}[\\p{IsLatin}]{2}$"; // ПРИЕМА САМО ЛАТИНИЦА
		return Pattern.matches(regex, regN);
	}	 */
	

	
	/**
	 * Зарежда обекта MpsFuraji
	 */
	private MpsFuraji searchMpsFuraji(Integer mpsId) {
		MpsFuraji mpsFuraj = new MpsFuraji();
		try {

			 MpsFurajiDAO mpsFurajDAO = new MpsFurajiDAO(getUserData());
			 mpsFuraj = mpsFurajDAO.findById(mpsId);


		} catch (Exception e) {
			LOGGER.error("Грешка при търсене на обект MpsFuraji!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на транспортно средство!");
			
		} finally {
			JPA.getUtil().closeConnection();
		}
		
		return mpsFuraj;
	}
	
	
	/**
	 * Избор на МПС от компонентата за търсене..
	 */
	public void actionSlcMPS() {
		try {
			
			if (this.mpsId != null) {
				JPA.getUtil().runWithClose(() -> this.mpsFuraj = searchMpsFuraji(mpsId));
			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при извличане на МПС от компонентата за търсене!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на транспортно средство!");
		} 
	}
	
	/**
	 * занулява полетата при смяна на държава
	 */
	public void actionChangeCountry() {
		
		for (ObektDeinostDeinost obektDeinDein : this.vpisvane.getEventDeinostFuraji().getObektDeinostDeinost()) {
			obektDeinDein.getObektDeinost().setNasMesto(null);
			obektDeinDein.getObektDeinost().setObl(null);
			obektDeinDein.getObektDeinost().setObsht(null);
			obektDeinDein.getObektDeinost().setAddress(null);
			obektDeinDein.getObektDeinost().setPostCode(null);	
		}	
	}
	
	/******************************************** МЕТОДИ, СВЪРЗАНИ СЪС СЕРТИФИКАТИ. ЗА СЕГА ГИ МАХАМЕ.. ДА НЕ СЕ ТРИЯТ!!! ***********************************************************/
	
	/**
	 * визуализиране на диалоговия прозорец за въвеждане на нов сертификат*/

	public void actionShowSertDlg() {
		this.eventDeinostSert = new EventDeinostFurajiSert();
	}
	
	private boolean checkSertData() {
		boolean save = true;
		
		if (this.eventDeinostSert.getSertType() == null) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":docGoodPract", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "regTargovtsiFuraj.vidDoc")));	
		 }
		
		if (this.eventDeinostSert.getVidFuraji() == null || SearchUtils.isEmpty(this.eventDeinostSert.getVidFuraji())) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":docVidF", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "targovtsiFuraj.furaj")));				 
		}
		
		if (this.eventDeinostSert.getDarj() == null) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":docDarj", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "regTargovtsiFuraj.docDarj")));	
		}
		
		if (this.eventDeinostSert.getJivotni() == null || SearchUtils.isEmpty(this.eventDeinostSert.getJivotni())) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":docJiv", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "regTargovtsiFuraj.docAnimals")));	
		}

		
		return save;
	}
	
	/**
	 * добавяне на нов сертификат*/

	public void actionAddSert() {
		try {
			
			if (checkSertData()) {
				
				if (this.newSert == BabhConstants.CODE_ZNACHENIE_DA) { 
					this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiSert().add(this.eventDeinostSert);
				}
				
				String dialog = "PF('addDocsVar').hide();";
				PrimeFaces.current().executeScript(dialog);
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при добавяне/корекция на сертификат", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при добавяне/корекция на сертификат!");
		} 
	}
	
	
	/** Изтриване на сертификат */
	public void actionDeleteSert() {
		try {
			
			if (this.eventDeinostSert != null) {
				this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiSert().remove(this.eventDeinostSert);
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при изтриване на сертификат!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при изтриване на сертификат!");
		}
	} 	 

	
	
	/******************************************** ЗАКЛЮЧВАНЕ/ОТКЛЮЧВАНЕ НА ДОКУМЕНТА ***********************************************************/

	/**
	 * Заключване на документ, като преди това отключва всички обекти, заключени от
	 * потребителя
	 * 
	 * @param idObj
	 */
	public void lockDoc(Integer idObj, Integer codeObj) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("lockDoc! {}", ud.getPreviousPage());
		}
		LockObjectDAO daoL = new LockObjectDAO();
		try {
			JPA.getUtil().runInTransaction(() -> daoL.lock(ud.getUserId(), codeObj, idObj, null));
		} catch (BaseException e) {
			LOGGER.error("Грешка при заключване на документ! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}

	/**
	 * Проверка за заключен документ
	 * 
	 * @param idObj
	 * @return true - ОК (док. не е заключен)
	 */
	private boolean checkForLockDoc(Integer idObj, Integer codeObj) {
		boolean res = true;	
		try { 
			Object[] lockObj =  new LockObjectDAO().check(ud.getUserId(), codeObj, idObj);
			if (lockObj != null) {
				 res = false;
				 this.showEkran = false; //да не виждат панелите
				 String msg = getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_ADMIN_STR, Integer.valueOf(lockObj[0].toString()), getUserData().getCurrentLang(), new Date())   
						       + " / " + DateUtils.printDateFull((Date)lockObj[1]);
				 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,getMessageResourceString("labels", "docu.docLocked"), msg);
			}
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при проверка за заключен документ! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
		return res;
	}

	/**
	 * проверка за заключенo вписване / заявление и друг обекр, ако се наложи
	 * заключване на съответния обект
	 * 
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
	 * при излизане от страницата - отключва обекта и да го освобождава за
	 * актуализация от друг потребител
	 */
	@PreDestroy
	private void unlockDoc() {
		if (!ud.isReloadPage()) {
			unlockAll(true);
			ud.setPreviousPage(null);
		}
		ud.setReloadPage(false);
	}

	/**
	 * отключва всички обекти на потребителя - при излизане от страницата или при
	 * натискане на бутон "Нов"
	 */
	private void unlockAll(boolean all) {
		LockObjectDAO daoL = new LockObjectDAO();
		try {
			if (all) {
				JPA.getUtil().runInTransaction(() -> daoL.unlock(ud.getUserId()));
			} else {
				JPA.getUtil().runInTransaction(
						() -> daoL.unlock(getUserData().getUserId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE));
			}
		} catch (BaseException e) {
			LOGGER.error("Грешка при отключване на документ! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}
	
	/**
	 * Смяна на таб
	 * @param event
	 */
    public void onTabChange(TabChangeEvent<?> event) {
	   	if(event != null) {
	   		
	   	}
	   		if (LOGGER.isDebugEnabled()) {
	   			LOGGER.debug("onTabChange Active Tab: {}", event.getTab().getId());
	   		}
	
			String activeTab =  event.getTab().getId();
			if (activeTab.equals("danniVpisvaneTab") || activeTab.equals("etapiVpisvaneTab") || activeTab.equals("docVpisvaneTab")) {
				flash.put("beanName", "regTargovtsiFuraj"); // задъжлително се подава името на бийна, ако отиваме към таба със статусите
			} else if (activeTab.equals("osnovniDanniTab") && Objects.equals(this.babhDoc.getPayType(), BabhConstants.CODE_ZNACHENIE_PAY_TYPE_FLOAD)) {
				checkDaljimaSuma();// ако е с плаваща тарифа и тя случйно е променена в таб документи- да се упдейтне екрана
			} 
	   	}

	public String actionGotoZaiav() {
		return "babhZaiavView.xhtml?faces-redirect=true&idObj=" + this.babhDoc.getId();
	}
	
	/**
	 * ИЗТРИВАНЕ НА ВПИСВАНЕ ПРИ ОПРЕДЕЛЕНИ УСЛОВИЯ
	 * 
	 * - Позволено е изтриване на вписване само, ако е в статус 'в обработка'
	 * - за обектите на дейност се взима под внимание дали участват и на друго място
	 */
	public void actionDelete() {
		
		try {
			
			//TODO да се изтрива и подвида на заявлението ако е имало такова
			
			JPA.getUtil().runInTransaction(() ->  {
				this.vpisvaneDAO.deleteById(vpisvane.getId());
			}); 
		
			Navigation navHolder = new Navigation();
			navHolder.goBack();   //връща към предходната страница

			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,  IndexUIbean.getMessageResourceString(UI_beanMessages, "general.successDeleteMsg") ); 
		
		} catch (ObjectInUseException e) {
			
			String scrolltoTop = "document.body.scrollTop = 0; document.documentElement.scrollTop = 0;";
			PrimeFaces.current().executeScript(scrolltoTop);
			
			LOGGER.error("Грешка при изтриване на вписване!", e); 
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, "general.objectInUse"), e.getMessage());
		} catch (BaseException e) {		
			
			String scrolltoTop = "document.body.scrollTop = 0; document.documentElement.scrollTop = 0;";
			PrimeFaces.current().executeScript(scrolltoTop);
			
			LOGGER.error("Грешка при изтриване на вписване!", e);			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		} 
		
	}
	

	public String actionMigrInfo() {
		return "migrationView.xhtml?faces-redirect=true&idVpis=" + this.vpisvane.getId();
	}
	
	
	

	/***************************************************************************** GETTERS/SETTERS ******************************************************************************/
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

	public RegisterOptions getRegisterOptions() {
		return registerOptions;
	}

	public void setRegisterOptions(RegisterOptions registerOptions) {
		this.registerOptions = registerOptions;
	}

	public RegisterOptionsDocsIn getDocOpt() {
		return docOpt;
	}

	public void setDocOpt(RegisterOptionsDocsIn docOpt) {
		this.docOpt = docOpt;
	}

	public Vpisvane getVpisvane() {
		return vpisvane;
	}

	public void setVpisvane(Vpisvane vpisvane) {
		this.vpisvane = vpisvane;
	}

	public EventDeinostFurajiPrednaznJiv getPrednaznachenieJiv() {
		return prednaznachenieJiv;
	}

	public void setPrednaznachenieJiv(EventDeinostFurajiPrednaznJiv prednaznachenieJiv) {
		this.prednaznachenieJiv = prednaznachenieJiv;
	}

	public VpisvaneDAO getVpisvaneDAO() {
		return vpisvaneDAO;
	}

	public void setVpisvaneDAO(VpisvaneDAO vpisvaneDAO) {
		this.vpisvaneDAO = vpisvaneDAO;
	}

	public Doc getBabhDoc() {
		return babhDoc;
	}

	public void setBabhDoc(Doc babhDoc) {
		this.babhDoc = babhDoc;
	}

	public DocDAO getDocDAO() {
		return docDAO;
	}

	public void setDocDAO(DocDAO docDAO) {
		this.docDAO = docDAO;
	}

	public Referent getReferent2() {
		return referent2;
	}

	public void setReferent2(Referent referent2) {
		this.referent2 = referent2;
	}

	public UserData getUd() {
		return ud;
	}

	public void setUd(UserData ud) {
		this.ud = ud;
	}

	public Integer getIdZaiav() {
		return idZaiav;
	}

	public void setIdZaiav(Integer idZaiav) {
		this.idZaiav = idZaiav;
	}

	public Integer getIdVpisv() {
		return idVpisv;
	}

	public void setIdVpisv(Integer idVpisv) {
		this.idVpisv = idVpisv;
	}

	public Integer getRegisterId() {
		return registerId;
	}

	public void setRegisterId(Integer registerId) {
		this.registerId = registerId;
	}

	public boolean isShowEkran() {
		return showEkran;
	}

	public void setShowEkran(boolean showEkran) {
		this.showEkran = showEkran;
	}

	public EventDeinostFurajiPredmet getPredmetDeinost() {
		return predmetDeinost;
	}

	public void setPredmetDeinost(EventDeinostFurajiPredmet predmetDeinost) {
		this.predmetDeinost = predmetDeinost;
	}

	public List<Integer> getVidDeinFuraji() {
		return vidDeinFuraji;
	}

	public void setVidDeinFuraji(List<Integer> vidDeinFuraji) {
		this.vidDeinFuraji = vidDeinFuraji;
	}

	public boolean isShowPredn() {
		return showPredn;
	}

	public void setShowPredn(boolean showPredn) {
		this.showPredn = showPredn;
	}

	public Integer getMpsId() {
		return mpsId;
	}

	public void setMpsId(Integer mpsId) {
		this.mpsId = mpsId;
	}

	public Integer getObektDeinostId() {
		return obektDeinostId;
	}

	public void setObektDeinostId(Integer obektDeinostId) {
		this.obektDeinostId = obektDeinostId;
	}

	public ObektDeinost getObektDeinost() {
		return obektDeinost;
	}

	public void setObektDeinost(ObektDeinost obektDeinost) {
		this.obektDeinost = obektDeinost;
	}

	public List<Integer> getVidFurajiList() {
		return vidFurajiList;
	}

	public void setVidFurajiList(List<Integer> vidFurajiList) {
		this.vidFurajiList = vidFurajiList;
	}

	public List<SystemClassif> getVidFurajiClassif() {
		return vidFurajiClassif;
	}

	public void setVidFurajiClassif(List<SystemClassif> vidFurajiClassif) {
		this.vidFurajiClassif = vidFurajiClassif;
	}

	public Map<Integer, Integer> getVidFurajiMap() {
		return vidFurajiMap;
	}

	public void setVidFurajiMap(Map<Integer, Integer> vidFurajiMap) {
		this.vidFurajiMap = vidFurajiMap;
	}

	public MpsFuraji getMpsFuraj() {
		return mpsFuraj;
	}

	public void setMpsFuraj(MpsFuraji mpsFuraj) {
		this.mpsFuraj = mpsFuraj;
	}

	public MpsDeinost getMpsDeinost() {
		return mpsDeinost;
	}

	public void setMpsDeinost(MpsDeinost mpsDeinost) {
		this.mpsDeinost = mpsDeinost;
	}

	public List<Integer> getSastonieList() {
		return sastonieList;
	}

	public void setSastonieList(List<Integer> sastonieList) {
		this.sastonieList = sastonieList;
	}

	public Integer getNewDeinPredmet() {
		return newDeinPredmet;
	}

	public void setNewDeinPredmet(Integer newDeinPredmet) {
		this.newDeinPredmet = newDeinPredmet;
	}

	public Integer getNewDeinJiv() {
		return newDeinJiv;
	}

	public void setNewDeinJiv(Integer newDeinJiv) {
		this.newDeinJiv = newDeinJiv;
	}

	public Integer getNewDeinMps() {
		return newDeinMps;
	}

	public void setNewDeinMps(Integer newDeinMps) {
		this.newDeinMps = newDeinMps;
	}

	public boolean isShowObektDeinPnl() {
		return showObektDeinPnl;
	}

	public void setShowObektDeinPnl(boolean showObektDeinPnl) {
		this.showObektDeinPnl = showObektDeinPnl;
	}

	public Map<Integer, Integer> getSastonieMap() {
		return sastonieMap;
	}

	public void setSastonieMap(Map<Integer, Integer> sastonieMap) {
		this.sastonieMap = sastonieMap;
	}

	public boolean isPredmetDeinDtbl() {
		return predmetDeinDtbl;
	}

	public void setPredmetDeinDtbl(boolean predmetDeinDtbl) {
		this.predmetDeinDtbl = predmetDeinDtbl;
	}

	public EventDeinostFurajiSert getEventDeinostSert() {
		return eventDeinostSert;
	}

	public void setEventDeinostSert(EventDeinostFurajiSert eventDeinostSert) {
		this.eventDeinostSert = eventDeinostSert;
	}

	public Integer getNewSert() {
		return newSert;
	}

	public void setNewSert(Integer newSert) {
		this.newSert = newSert;
	}

	public boolean isCheckPredmetDein() {
		return checkPredmetDein;
	}

	public void setCheckPredmetDein(boolean checkPredmetDein) {
		this.checkPredmetDein = checkPredmetDein;
	}

	public List<Integer> getJivotniList() {
		return jivotniList;
	}

	public void setJivotniList(List<Integer> jivotniList) {
		this.jivotniList = jivotniList;
	}

	public Map<Integer, Integer> getJivotniMap() {
		return jivotniMap;
	}

	public void setJivotniMap(Map<Integer, Integer> jivotniMap) {
		this.jivotniMap = jivotniMap;
	}

	public boolean isFoundedObektDein() {
		return foundedObektDein;
	}

	public void setFoundedObektDein(boolean foundedObektDein) {
		this.foundedObektDein = foundedObektDein;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public Integer getGip() {
		return gip;
	}

	public void setGip(Integer gip) {
		this.gip = gip;
	}

	public Object[] getLastDoc() {
		return lastDoc;
	}

	public void setLastDoc(Object[] lastDoc) {
		this.lastDoc = lastDoc;
	}

	public boolean isObektDeinWithoutSave() {
		return obektDeinWithoutSave;
	}

	public void setObektDeinWithoutSave(boolean obektDeinWithoutSave) {
		this.obektDeinWithoutSave = obektDeinWithoutSave;
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

	public RefCorrespData getBindingReferent2() {
		return bindingReferent2;
	}

	public void setBindingReferent2(RefCorrespData bindingReferent2) {
		this.bindingReferent2 = bindingReferent2;
	}

	public boolean isAccess() {
		return access;
	}

	public void setAccess(boolean access) {
		this.access = access;
	}


	public List<SystemClassif> getPodZaiavList() {
		return podZaiavList;
	}

	public void setPodZaiavList(List<SystemClassif> podZaiavList) {
		this.podZaiavList = podZaiavList;
	}

	public Map<Integer, Object> getEkatteSpec() {
		if (ekatteSpec == null) {
			ekatteSpec = loadEkatteSpec();
        }
		return ekatteSpec;
	}
	
	
	/** 
	 *   1 само области; 2 - само общини; 3 - само населени места; без специфики - всикчи
	 */
	 private Map<Integer, Object> loadEkatteSpec() {
	    Map<Integer, Object> defaultData = new HashMap<>();
	    defaultData.put(SysClassifAdapter.EKATTE_INDEX_TIP, 3);
	    
	    return defaultData;
	 }

	public void setEkatteSpec(Map<Integer, Object> ekatteSpec) {
		this.ekatteSpec = ekatteSpec;
	}


	public boolean isZaiavlenieZaTargovia() {
		return zaiavlenieZaTargovia;
	}


	public void setZaiavlenieZaTargovia(boolean zaiavlenieZaTargovia) {
		this.zaiavlenieZaTargovia = zaiavlenieZaTargovia;
	}


	public Integer getVidDeinVnos() {
		return vidDeinVnos;
	}


	public void setVidDeinVnos(Integer vidDeinVnos) {
		this.vidDeinVnos = vidDeinVnos;
	}


	public int getMigration() {
		return migration;
	}


	public void setMigration(int migration) {
		this.migration = migration;
	}

}
