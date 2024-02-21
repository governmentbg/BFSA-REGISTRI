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
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostLica;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.ReferentDoc;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.navigation.Navigation;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.SysClassifAdapter;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
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
 * Регистри с обект на лицензиране "ВЛЗ"
 * 
 * 
 */

@Named
@ViewScoped
public class RegVLZ extends IndexUIbean {

	private static final long serialVersionUID = -1807557045298389672L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegVLZ.class);
	public  static final  String IDFORM = "vlzForm";
	private SystemData sd;
	private UserData ud;
	private Date decodeDate;
	private transient VpisvaneDAO	daoVp;
	@Inject
	private Flash flash;
	private int isView;
	private int migration;
	private String msgNasMesto;


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
	
	/**oon
	 * заявителя (лицензиант) - code_ref_vpivane от tablica вписване
	 */
	private Referent referent2;
	private RefCorrespData	bindingReferent2;	
	private String txtRef2;
	
	/**
	 * вписване 
	 */
	private Vpisvane vpisvane;
	
	//tekushtiq lekar koito dobavqme
	private Referent lekar;
	private ArrayList<Referent> lekarList=new ArrayList<Referent>();
	private Integer idxRowLica; // при редакция да се запомни точно кой ред от таблицата се редактира
	private RefCorrespData bindingLekar;
	
	private ObektDeinost vlz;
	private Integer tipCurrentVrazkaLice=null;
	
	//code_ref, id ot obekt_dein_lica 
	private HashMap<Integer,Integer> obektLiceRefMap=new HashMap<Integer,Integer>();
	
	private boolean access;
	/** Списък на файловете към заявлението за вписване */
	private List<Files> filesList;
	/** Информацията за последното заявление към вписването (ако има такова) */
	private Object[] lastDoc;
	/**
	 * област, обшина за ВЛЗ
	 */
	private String vlzObhObl;
		
	/**
	 * Дължима сума
	 */
 	private BigDecimal daljimaSuma;
 	
 	private Map<Integer, Object> specificsEkatte;
	
	@PostConstruct
	public void init() {
		boolean lockOK = false;
		sd = (SystemData) getSystemData();
		ud = getUserData(UserData.class);			
		daoVp = new VpisvaneDAO(getUserData());
		this.filesList = new ArrayList<>();
		
		specificsEkatte=loadSpecificsEKATTE();
		
		FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
		String param3 = (String) faceletContext.getAttribute("isView"); 
		isView = !SearchUtils.isEmpty(param3) ? Integer.valueOf(param3) : 0;// 0 - актуализация; 1 - разглеждане
		
		String paramM = JSFUtils.getRequestParameter("m");
		migration = SearchUtils.isEmpty(paramM) ? BabhConstants.CODE_ZNACHENIE_NE :  0; // Ако е подаден параметър - значи идва от екрана за ръчно въвеждане на миграция
	
		String paramZ = JSFUtils.getRequestParameter("idZ");
		String paramV = JSFUtils.getRequestParameter("idV");
		msgNasMesto = "";
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
					try {
						
						JPA.getUtil().runWithClose(() -> this.vpisvane =  daoVp.findById(Integer.valueOf(idVp)));
						 
						if (vpisvane != null) {
							
							// проверка за достъп до впсиване							 
							JPA.getUtil().runWithClose(() -> access = daoVp.hasVpisvaneAccess(vpisvane, isView==0, sd));
							
							if(access) {
								
								loadZaiavl(vpisvane.getIdZaqavlenie(), false);
								loadOptions(); //  Извличане на настройките по вида на документа
								
								// основен обект  за дейност
								if(vpisvane.getObektDeinost() != null) {
									vlz = vpisvane.getObektDeinost();
									vlzObhObl = sd.decodeItemDopInfo(BabhConstants.CODE_CLASSIF_EKATTE, vlz.getNasMesto(), getCurrentLang(), new Date());
								}else {
									vlz = new ObektDeinost();
									try {
										vlz.setDarj(Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG")));
									} catch (NumberFormatException | DbErrorException e) {
										LOGGER.error(e.getMessage(), e);
									}
								}
								vlz.setVid(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLZ);
								loadLastZaiav(true); // зарежда последното заявление
							}
						}
						
					} catch (Exception e) {
						LOGGER.error("Грешка при зареждане на вписване и обектите към него", e);
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
								getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
						throw new DbErrorException(e.getMessage());
					}
					
					
				}else {
					access = false;
				}

			} else if (!SearchUtils.isEmpty(paramZ))  {
				
				// не е подадено ид на вписване - 
				// Ново вписване - идваме от необработено заявление
	
				Integer idZ = Integer.valueOf(paramZ);
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
	   			
				if(vlz.getDarj() == null) {
					vlz.setDarj(BabhConstants.CODE_ZNACHENIE_BULGARIA);
				}
				
	   			loadFilesList(zaiavlVp.getId());
	 			
				this.decodeDate = new Date();

		   		if(isView == 1 && access) {
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
		}finally {
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
	
	public void actionNew() {
		vlz=new ObektDeinost();
		try {
			vlz.setDarj(Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG")));
		} catch (NumberFormatException | DbErrorException e) {
			e.printStackTrace();
		}
		vlz.setVid(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLZ);
		lekar=new Referent();
		lekar.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
		this.decodeDate = new Date();
	}
	
	/************************** LEKARI STUFF ************************/
	public void actionNewLekar() {
		idxRowLica = null;
		lekar=new Referent();
		lekar.setAddress(new ReferentAddress());
		lekar.getAddress().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN);
		lekar.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
		try {
			lekar.getAddress().setAddrCountry(Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG")));
		} catch (NumberFormatException | DbErrorException e) {
			e.printStackTrace();
		}
		tipCurrentVrazkaLice=BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_VET_LEKAR;
	}

	
	public void actionEditLekar(Referent row, Integer idxRow) {
		try {
			lekar = row.clone();
			idxRowLica = idxRow;
			if(Objects.equals(row.getDbEmplPosition(), BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL) ) {
				tipCurrentVrazkaLice=BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL;
			} else  {
				tipCurrentVrazkaLice=BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_VET_LEKAR;
			}
		} catch (CloneNotSupportedException e) {
			LOGGER.error( e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при актуализация на лице! ", e.getMessage());
		} 
		
	}
	
	
	public void actionRemoveLekar(Integer rowNum) {
		this.lekarList.remove(this.lekarList.get(rowNum));
	}
	
	
	
	/**
	 * Добавяне на лице, член на екип,  при връщане от модалния			
	 * 
	 */
	public void actionAddLice() {		 	 
		lekar = bindingLekar.getRef();
		if( lekar == null || SearchUtils.isEmpty(lekar.getRefName()) ){
			// ЕГН, ЛНЧ - задължителни ли са?
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Не са въведени имена на лицето!");
			return;
		}
		if (this.vlz.getObektDeinostLica()==null) {
			this.vlz.setObektDeinostLica(new ArrayList<ObektDeinostLica>());
		}
		
		boolean found = false;	 
		for (int i = 0; i < lekarList.size(); i++) {
			Referent item = lekarList.get(i);
			if (!Objects.equals(i, idxRowLica)) {
				if(lekar.getCode() != null && Objects.equals(item.getCode(),lekar.getCode())) {
					// избрано е лице, което вече го има в таблица реферети 
					found = true;						
				} else if (!SearchUtils.isEmpty(lekar.getFzlEgn()) && Objects.equals(lekar.getFzlEgn(), item.getFzlEgn())) {
					found = true;
				} else if (!SearchUtils.isEmpty(lekar.getFzlLnc())  && Objects.equals(lekar.getFzlLnc(), item.getFzlLnc())) {
					found = true;
				}
				
				if(found) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Лицето вече е добавено!");
					return;
				}
			}
		}
		
		if (this.idxRowLica == null) {
			// Добавяне на ново лице
			lekar.setDbEmplPosition(tipCurrentVrazkaLice);
			lekarList.add(lekar);
		} else { 
			// когато коригираме лице от списка
			lekar.setDbEmplPosition(tipCurrentVrazkaLice);
			lekarList.set(idxRowLica, lekar);
		}		

		idxRowLica = null;
		PrimeFaces.current().executeScript("PF('mLekarD').hide();");
	}
	
	
	/**
	 * връща диплом/дата или удост. БВС/дата
	 * @param lice
	 * @param vidD
	 * @return
	 */
	
	public String actionLiceDoc(Referent lice, Integer vidD){
		List<ReferentDoc> tmpLst = lice.getReferentDocs();
		if(tmpLst != null && !tmpLst.isEmpty()){
			for (ReferentDoc doc: tmpLst) {
				if(Objects.equals(doc.getVidDoc(), vidD)){					
					return doc.getNomDoc() + " / " +DateUtils.printDate(doc.getDateIssued()); 
				}
			}
		}
		return null;		
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
				getFlash().put("beanName", "regVLZ"); // задъжлително се подава името на бийна, ако отиваме към таба със статусите
			} 
			else if(activeTab.equals("osnovniDanniTab")) {
				vlz = vpisvane.getObektDeinost();
				if(Objects.equals(zaiavlVp.getPayType(), BabhConstants.CODE_ZNACHENIE_PAY_TYPE_FLOAD)) {
					checkDaljimaSuma(); // ако е с плаваща тарифа и тя случйно е променена - да се упдейтне екрана	
				}
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
	 *   1 само области; 2 - само общини; 3 - само населени места; без специфики - всикчи
	 * @return
	 */
	public Map<Integer, Object> loadSpecificsEKATTE() {

		return Collections.singletonMap(SysClassifAdapter.EKATTE_INDEX_TIP, 3);
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
				
				
				//TODO!!!!!
				// Коментар Васил - тези неща също може да се изнесат при мен
				vpisvane.setRegistraturaId(zaiavlVp.getRegistraturaId());
				vpisvane.setIdZaqavlenie(zaiavlVp.getId());
				vpisvane.setRegNomZaqvlVpisvane(zaiavlVp.getRnDoc());
				vpisvane.setDateZaqvlVpis(zaiavlVp.getDocDate());
				vpisvane.setCodeRefVpisvane(referent2.getCode()); // от парсването на заявлението 
				vpisvane.setIdRegister(registerId); // ако е ново вписване... 
				vpisvane.setLicenziantType(BabhConstants.CODE_ZNACHENIE_TIP_LICENZ_VLZ);
				vpisvane.setCodePage(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ); 
				vpisvane.setStatus(BabhConstants.STATUS_VP_WAITING);
		 
				// основен обект  за дейност
				if(vpisvane.getObektDeinost() != null) {
					vlz = vpisvane.getObektDeinost();	
				}else {
					vlz = new ObektDeinost();
					try {
						vlz.setDarj(Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG")));
					} catch (NumberFormatException | DbErrorException e) {
						e.printStackTrace();
					}
				}
				vlz.setVid(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLZ);
			
				// 0 - миграция, ръчно въвеждане,1 - миграция - завършена, 2 - не е миграция;
				if (migration == 0) {
					vpisvane.setFromМigr(migration); // ако е от миграция - ръчно въвеждане, иначе не се променя
				}
				
				if(vlz.getNasMesto() != null) {
					actionVlzOblCheck(); // да провери дали е за текущото ОДБХ
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
					//loadFilesList(zaiavlVp.getId());
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
			setLiceRefMap();
		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на заявление", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
		//	throw new DbErrorException(e.getMessage());
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
	 * 
	 */
	public void setLiceRefMap() {
		obektLiceRefMap.clear();
		lekarList.clear();
		if (vpisvane!=null && vpisvane.getObektDeinost()!=null) {
			for (int i = 0; i < vpisvane.getObektDeinost().getObektDeinostLica().size(); i++) {
				Referent r=vpisvane.getObektDeinost().getObektDeinostLica().get(i).getReferent();
				r.setDbEmplPosition(vpisvane.getObektDeinost().getObektDeinostLica().get(i).getRole());
				lekarList.add(r);
				obektLiceRefMap.put(vpisvane.getObektDeinost().getObektDeinostLica().get(i).getCodeRef(), vpisvane.getObektDeinost().getObektDeinostLica().get(i).getId());					
			}	
		}
		
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
		 // за ново вписване да сетне сумата за плащане по подразбиране и типа плащане
		 if(vpisvane == null || vpisvane.getId() == null) {
			//ако в заявлението има настройки за плащането - не ги пипаме!
			if (zaiavlVp.getPayType() == null ) {  		
				zaiavlVp.setPayType(this.docOpt.getPayCharacteristic());
			}
			if(zaiavlVp.getPayAmount() == null || zaiavlVp.getPayAmount().equals(Float.valueOf(0))) { 
				zaiavlVp.setPayAmount(this.docOpt.getPayAmount());
			}
//			 if(Objects.equals(docOpt.getPayCharacteristic(), BabhConstants.CODE_ZNACHENIE_PAY_TYPE_NOPAY)){
//				 zaiavlVp.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
//			 }else {
//				 zaiavlVp.setIndPlateno(BabhConstants.CODE_ZNACHENIE_NE);
//			 }
		 }
		 return registerId;
	}
	
	
	
	public void actionSave() {
		if(checkDataVp()) { 			
			try {
				
				
				boolean newVp = this.vpisvane.getId() == null;
				
				//TODO Трябва да се измисли първо как ще се индексират прикачените файлове
//				List<String> ocrDocs = this.document.getOcr(this.filesList);
				
				saveVp(newVp);
			
				if(newVp) {// само за нов документ	
					
					// заключване на вписване.
					lockObj(this.vpisvane.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
					checkDaljimaSuma();
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
	 * TODO
	 * @return
	 */
	public boolean checkDataVp() {
		boolean rez = true;
	
		referent1 = getBindingReferent1().getRef();
		if( SearchUtils.isEmpty(referent1.getRefName())){
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:licеPоdatel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "podatel.msgName"));	
		} 
		if(vpisvane.getFromМigr() == null || Objects.equals(vpisvane.getFromМigr(),BabhConstants.CODE_ZNACHENIE_MIGR_NO)) {
			rez = checkIdentifFZL(rez, referent1.getFzlEgn(), referent1.getFzlLnc(), IDFORM+":registerTabs:licеPоdatel:egn", getMessageResourceString(beanMessages, "podatel.msgValidIdentFZL"));
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
				rez = checkIdentifFZL(rez, referent2.getFzlEgn(), referent2.getFzlLnc(), IDFORM+":registerTabs:liceZaqvitel:egn",getMessageResourceString(beanMessages, "zaiavitel.msgValidIdentFZL"));
			}else {
				rez = checkIdentifNFZL(rez, referent2.getNflEik(), referent2.getFzlLnEs(), IDFORM+":registerTabs:liceZaqvitel:eik", getMessageResourceString(beanMessages, "zaiavitel.msgValidIdentNFZL"));
			}
		}
		
		if(vlz.getVidVlz() == null) {
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:vidPraktika",FacesMessage.SEVERITY_ERROR,"Въведете вида на ветеринарната практика");
		}
		if(vlz.getObslujvaniJiv() == null) {
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:obslJiv",FacesMessage.SEVERITY_ERROR,"Въведете обслужвани животни");
		}
		if (getLekarList() == null || getLekarList().isEmpty()) {
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:tblLekari",FacesMessage.SEVERITY_ERROR, "Въведете управител и ветеринарни лекари!");
		}
		if(vlz.getNasMesto() == null ||  !SearchUtils.isEmpty(msgNasMesto)) {
			rez = false;		
			JSFUtils.addMessage(IDFORM+":registerTabs:mestoC:аutoCompl",FacesMessage.SEVERITY_ERROR,"Въведете населено място за ветеринарната практика "+ msgNasMesto);
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
	* занулява полетата при смяна на държава
	*/
	public void  actionChangeCountry() {
		this.vlz.setNasMesto(null);		
		this.vlz.setObl(null);  // НЕ се попълват изобщо!!?? 
		this.vlz.setObsht(null);// НЕ се попълват изобщо!!??
		this.vlz.setAddress(null);
		this.vlz.setPostCode(null);
	}
	
	
	
	/**
	 * населено място трябва да е в областта на съответното ОДБХ,
	 * защото 1-те две цифри в УРН-то са кода на областта 
	 * 
	 */
	public void actionVlzOblCheck() {
		try {
			msgNasMesto = "";
			String 	vlzObhObl1 = sd.decodeItemDopInfo(BabhConstants.CODE_CLASSIF_EKATTE, vlz.getNasMesto(), getCurrentLang(), new Date());
			if(Objects.equals(ud.getRegistratura(), BabhConstants.CODE_ZNACHENIE_REGISTRATURA_BABH)){
				// това допустимо ли е? Заявлението да е към централното БАБХ?
				// Тогава номера се определя от посочената област!!
				if(vlz.getRegNom() != null &&  vlzObhObl != null) {	
					// може да се мине от тук при корецкия на населеното място за ВЛЗ с вече издаден УРН, т.е. вече е вписано 
					String s1[] = vlzObhObl1.split(",");
					String s2[] = vlzObhObl.split(",");
					if(Objects.equals(s1[1], s2[1])) {
						vlzObhObl = vlzObhObl1;
					} else {
						vlz.setNasMesto(null);
						JSFUtils.addMessage(IDFORM+":registerTabs:mestoC:аutoCompl",
								FacesMessage.SEVERITY_WARN,"За ветеринарната практика има УНР! Населеното място може да е само в "+s2[1]);
						PrimeFaces.current().executeScript("document.body.scrollTop = 0; document.documentElement.scrollTop = 0;");
					}			
				}else {
					vlzObhObl = vlzObhObl1;	
				}
				
			} else {
				Integer obektRegistratura = daoVp.findRegistraturaByEkatte(vlz.getNasMesto());		
				if (Objects.equals(obektRegistratura,ud.getRegistratura())) {
					vlzObhObl = vlzObhObl1;
				}
				else {	
					//vlz.setNasMesto(null);	
					vlzObhObl = vlzObhObl1;
					msgNasMesto = "Населеното място трябва да е на територията на "+ 
									 sd.decodeItemDopInfo(BabhConstants.CODE_CLASSIF_REGISTRATURI, ud.getRegistratura(), getCurrentLang(), new Date());
					JSFUtils.addMessage(IDFORM+":registerTabs:mestoC:аutoCompl",
							FacesMessage.SEVERITY_WARN,msgNasMesto);
					PrimeFaces.current().executeScript("document.body.scrollTop = 0; document.documentElement.scrollTop = 0;");
					 			
				}
			}
		
		} catch (DbErrorException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	
	public void actionVlzOblClear() {
		if(vlz.getRegNom() == null) {
			vlzObhObl = null; //само, ако все още няма УРН!
		}
	}
	

	/*** КОПИРА АДРЕСА ЗА КОРЕНСПОНДЕНЦИЯ НА ЗАЯВИТЕЛЯ в адреса на практиката  
	 * Бутона е видим само, ако все още няма УРН!!!
	 * ***/
	public void actionCopyAdresZaiav() {
		referent2 = getBindingReferent2().getRef();
		if (referent2 == null || referent2.getRefType() == null || referent2.getAddressKoresp() == null
			|| this.referent2.getAddressKoresp().getEkatte() == null) {
			//msgScrollTop("Няма посочен адрес на заявител!");
			return;
		}
		vlz.setDarj(referent2.getAddressKoresp().getAddrCountry());
		vlz.setNasMesto(this.referent2.getAddressKoresp().getEkatte());
		vlz.setAddress(this.referent2.getAddressKoresp().getAddrText()); 
	 
	}
	
	

	/*** КОПИРА данните заявителя, ако е физическо лице ***/
	public void actionCopyDataZaiav() {
		referent2 = getBindingReferent2().getRef();
		if (referent2 == null || referent2.getRefType() == null || Objects.equals(referent2.getRefType(), BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL)) {
			return;
		}
		lekar = referent2; 	  
	}
	
	
	/**
	 * връща true, ако завителя е физ. лице
	 * @return
	 */
	public boolean referent2TypeFZL() {
		boolean rez = false;
		referent2 = getBindingReferent2().getRef();
		if (referent2 != null && Objects.equals(referent2.getRefType(), BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL) ) {
			rez = true;
		}
		return rez;
	}
	
	/**
	 * запис на вписване
	 * @param newVp
	 * @throws BaseException
	 */
	private void saveVp(boolean newVp) throws BaseException {
		
		zaiavlVp.setKachestvoLice(referent1.getKachestvoLice());  
		
		zaiavlVp.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN); // от класификация 	CODE_CLASSIF_DOC_STATUS(120)
		
		//referent2 = ((RefCorrespData)FacesContext.getCurrentInstance().getViewRoot().findComponent("vlzForm").findComponent("registerTabs:liceZaqvitel")).getRef();
		referent2 = getBindingReferent2().getRef();
		vpisvane.setObektDeinost(this.vlz);
		
		vpisvane.getObektDeinost().getObektDeinostLica().clear();
		for (int i = 0; i < getLekarList().size(); i++) {
			ObektDeinostLica odl=new ObektDeinostLica();
			
			odl.setReferent(getLekarList().get(i));
			odl.setRole(getLekarList().get(i).getDbEmplPosition());
					
			if (obektLiceRefMap.containsKey(getLekarList().get(i).getCode())) {
				odl.setId(obektLiceRefMap.get(getLekarList().get(i).getCode()));
			}
			vpisvane.getObektDeinost().getObektDeinostLica().add(odl);
		}
		
		JPA.getUtil().runInTransaction(() -> { 
			//vpObj.setCountFiles(filesList == null ? 0 : filesList.size());
			this.vpisvane= this.daoVp.save(vpisvane, zaiavlVp, referent1, referent2, null, sd);
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

	public Referent getLekar() {
		return lekar;
	}

	public void setLekar(Referent lekar) {
		this.lekar = lekar;
	}
	


	public ObektDeinost getVlz() {
		return vlz;
	}

	public void setVlz(ObektDeinost vlz) {
		this.vlz = vlz;
	}

	public ArrayList<Referent> getLekarList() {
		return lekarList;
	}

	public void setLekarList(ArrayList<Referent> lekarList) {
		this.lekarList = lekarList;
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

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public Integer getIdxRowLica() {
		return idxRowLica;
	}

	public void setIdxRowLica(Integer idxRowLica) {
		this.idxRowLica = idxRowLica;
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

	public RefCorrespData getBindingLekar() {
		return bindingLekar;
	}

	public void setBindingLekar(RefCorrespData bindingLekar) {
		this.bindingLekar = bindingLekar;
	}

	public BigDecimal getDaljimaSuma() {
		return daljimaSuma;
	}

	public void setDaljimaSuma(BigDecimal daljimaSuma) {
		this.daljimaSuma = daljimaSuma;
	}


	public RefCorrespData getBindingReferent2() {
		return bindingReferent2;
	}


	public void setBindingReferent2(RefCorrespData bindingReferent2) {
		this.bindingReferent2 = bindingReferent2;
	}


	public RefCorrespData getBindingReferent1() {
		return bindingReferent1;
	}


	public void setBindingReferent1(RefCorrespData bindingReferent1) {
		this.bindingReferent1 = bindingReferent1;
	}


	public Referent getReferent1() {
		return referent1;
	}


	public void setReferent1(Referent referent1) {
		this.referent1 = referent1;
	}


	public Map<Integer, Object> getSpecificsEkatte() {
		return specificsEkatte;
	}


	public void setSpecificsEkatte(Map<Integer, Object> specificsEkatte) {
		this.specificsEkatte = specificsEkatte;
	}


	public int getMigration() {
		return migration;
	}


	public void setMigration(int migration) {
		this.migration = migration;
	}


	public String getVlzObhObl() {
		return vlzObhObl;
	}


	public void setVlzObhObl(String vlzObhObl) {
		this.vlzObhObl = vlzObhObl;
	}



	public String getMsgNasMesto() {
		return msgNasMesto;
	}


	public void setMsgNasMesto(String msgNasMesto) {
		this.msgNasMesto = msgNasMesto;
	}

}
