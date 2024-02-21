package com.ib.babhregs.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.components.RefCorrespData;
import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.LockObjectDAO;
import com.ib.babhregs.db.dao.MpsDAO;
import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.db.dto.MpsKapacitetJiv;
import com.ib.babhregs.db.dto.MpsLice;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocinActivity;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.search.MPSsearch;
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
 * регистриране на МПС за превоз на животни
 * 
 * Не се записва нищо в таблици event_deinost_jiv!!!
 * записва се директно в таблиците mps и  vpisvane. Връзката между ТС и вписване е колони licenziant_type = 3 (ТС) и id_licenziant
 * Таблица MPS_Deinost тук не се изпозлва! Тя се изполва при издаване на лицензия за превозвач и в нея се записва връзката  ТС - превозвач 
 */

@Named
@ViewScoped
public class RegMpsZJ extends IndexUIbean {

	private static final long serialVersionUID = -1807557045298389672L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RegMpsZJ.class);
	public  static final  String IDFORM = "mpsZJForm";
	
	private SystemData sd;
	private UserData ud;
	private Date decodeDate;
	private transient VpisvaneDAO	daoVp;
	@Inject
	private Flash flash;
	private int isView;
	private int migration;
	
	private DataTable bindigMpsList;
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
	
	private Integer indexTabl;   

	private Integer vidJiv; // Вид животно
	private Integer broiJiv;  // Брой 
	
	private Referent sobstvenik; // собственик - в таблица връзки - таблица MPS_lica
	private RefCorrespData	bindingSobstvenik;
	private Referent sobstvenikLast; // запомня последния въведен собственик 
//	private Integer mestoLast;	// запомня последното местодомуване - нас. място ОТПАДА
//	private String  adrLast;	// запомня последното местодомуване - адрес    ОТПАДА
	private Integer patuvaneLast; // запомня последното избрано
 
	private  List<Object[]> mpsList; // списък МПС + idVpisvane_status от заявлнието за първоначално вписване - Всички ТС, подадени с едно заявление за първоначално вписване 
	private Integer indexTablMps;      // Индекс на избрано ТС от таблицата
	private Map<Integer, Object> specificsEkatte;
	

	private Mps mps = new Mps(); // заради комп. за търсене - TODO в момента е коментирна

	//TODO - ако е избрано МПС и то има Id в базата - да се появи бутонче за разглеждане.... 
	
	 

	//TODO - стататуса на заявлението ше ирае ли в случая????
	/**
	 *отворено е от филтъра на заявленията - Само тогава позволява да се добавя ново!! 
	 */
	private boolean fromZaiav;
	private boolean hidePlDein;  
	private boolean focusMps; 
	
	/**
	 * да разреши ли корекция на ред от т аблицта с МПС 
	 */
	private boolean disableEdit; 
	/**
	 * Дължима сума
	 */
 	private BigDecimal daljimaSuma;

	@PostConstruct
	public void init() {
		boolean lockOK = false;
		sd = (SystemData) getSystemData();
		ud = getUserData(UserData.class);			
		daoVp = new VpisvaneDAO(getUserData());
		mpsList = new ArrayList<>();
		this.filesList = new ArrayList<>();
		specificsEkatte=loadSpecificsEKATTE();
		
		FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
		String param3 = (String) faceletContext.getAttribute("isView"); 
		isView = !SearchUtils.isEmpty(param3) ? Integer.valueOf(param3) : 0;// 0 - актуализация; 1 - разглеждане
	
		String paramM = JSFUtils.getRequestParameter("m");
		migration = SearchUtils.isEmpty(paramM) ? BabhConstants.CODE_ZNACHENIE_NE :  0; // Ако е подаден параметър - значи идва от екрана за ръчно въвеждане на миграция
		
		String paramZ = JSFUtils.getRequestParameter("idZ");
		String paramV = JSFUtils.getRequestParameter("idV");

		try {
			if (!SearchUtils.isEmpty(paramV)){
				String fZ = JSFUtils.getRequestParameter("z");
				if (!SearchUtils.isEmpty(fZ)){
					fromZaiav = true;
				} 
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
							
							// ако заявлението е за промяна или заличаване.. винаги позволяваме корекция на данните
							String zaiavChange = JSFUtils.getRequestParameter("change"); 
							if (zaiavChange != null && !zaiavChange.isEmpty()) {
								this.vpisvane.setVpLocked(Constants.CODE_ZNACHENIE_NE);
							}
							
							// проверка за достъп до впсиване							 
							JPA.getUtil().runWithClose(() -> access = daoVp.hasVpisvaneAccess(vpisvane, isView==0, sd));
							
							if(access) {
								
								loadSobstvenikMps();
								 
								loadZaiavl(vpisvane.getIdZaqavlenie(), false);
								loadOptions(); //  Извличане на настройките по вида на документа
							// ако идвам от филтъра на заявленията - да симулирам все едно няма вписване и продължават въвеждане на нови ТС
								if(fromZaiav) {
									actionNewMps(false);
								}
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
				fromZaiav = true;
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
				loadFilesList(zaiavlVp.getId());
			 	
				this.decodeDate = new Date();
				hidePlDein = fromZaiav;
				
		   		if(isView == 1 && access) {
		   			viewMode();
		   		}
		   		checkDaljimaSuma();
		   		focusMps = false;
		   	 	if (JSFUtils.getFlashScopeValue("focusMps") != null) {
		   	 		focusMps = true;
		   	 	}
			} else {
				 JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,"Достъпът е отказан!");
				 if(lockOK) {
					 unlockDoc();
				 }
			}			
		
		} catch (Exception e) {
			access = false;
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при инициализация! ", e.getMessage());
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
	 * собственик на избрано МПС
	 */
	private void loadSobstvenikMps()  {
		if( vpisvane.getMps() != null && vpisvane.getMps().getMpsLice().size() > 0) {
			MpsLice sobst = vpisvane.getMps().getMpsLice().get(0);
			try {
				if(sobst != null) {
					if( sobst.getReferent() != null) {
						sobstvenik = sobst.getReferent();
					}else if(sobst.getCodeRef() != null) {
						JPA.getUtil().runWithClose(() -> sobstvenik = new ReferentDAO(ud).findByCodeRef(sobst.getCodeRef()));
					}
				}
			} catch (BaseException e) {
				LOGGER.error("Грешка при зареждане на собственик на ТС! ", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
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
					container = new EFormUtils().convertEformToVpisvane(idZ, sd); //TODO - NE E gotowo!!! 
				} catch (Exception e1) {
					// да заредя все пак заявлението!!!
					// въпреки, че не се парсва файлът - да може да се отоври екрана!
					JPA.getUtil().runWithClose(() -> this.zaiavlVp =   new DocDAO(ud).findById(Integer.valueOf(idZ)));
					 
					LOGGER.error("Грешка при парсване на заявление - МПС! ", e1);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
							"Грешка при парсване на заявление! Данните не могат да бъдат заредени от прикачения файл!");
				}
				if(container.doc != null) {
				   this.zaiavlVp = container.doc;
				}
				
				vpisvane = container.vpisvane;
				referent2 = container.ref2;
				referent1 = container.ref1;
			
				if(container.allMps != null) {
					 for(Mps mps : container.allMps ) {
						 addNewMPStoList(mps);
					 }	
				}
				
				
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
				vpisvane.setLicenziantType(BabhConstants.CODE_ZNACHENIE_TIP_LICENZ_MPS);  // обекта за лицензиране
				vpisvane.setCodePage(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ); // кода на страницата
				vpisvane.setStatus(BabhConstants.STATUS_VP_WAITING);
				vpisvane.setMps(new Mps());
				// Тук не се използват таблицте event_deinsot_jiv !!!
				
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
				JPA.getUtil().runWithClose(() -> mpsList = 	new MpsDAO(getUserData()).findMpsDataByZaqavlenie(zaiavlVp.getId()));
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
//				 if(Objects.equals(docOpt.getPayCharacteristic(), BabhConstants.CODE_ZNACHENIE_PAY_TYPE_NOPAY)){
//					 zaiavlVp.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
//				 }else {
//					 zaiavlVp.setIndPlateno(BabhConstants.CODE_ZNACHENIE_NE);
//				 }
			 }
		 }
		 return registerId;
	}
	
	
	/**
	 * бутон Запис - 
	 * тук се записват само новите ТС, влезли през заявлнието
	 * или КОНКРЕТНО вписване, отворено през филтъра за вписванията 
	 */
	public void actionSave() {
		if(checkDataVp()) { 			
			try {
				zaiavlVp.setKachestvoLice(referent1.getKachestvoLice());  
				
				zaiavlVp.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN); // от класификация 	CODE_CLASSIF_DOC_STATUS(120)
				
				if(!fromZaiav) {
					// ако сме през филтъра за вписванията - да се погрижа да обновя собственика...
					MpsLice sobst = null;
					if(sobstvenik != null) {
						sobst = new MpsLice();// избраното лице собсвеник  -  като обект MpsLice
						sobst.setReferent(sobstvenik);
						sobst.setTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK);
				
						if( vpisvane.getMps().getMpsLice().size() > 0) {
							vpisvane.getMps().getMpsLice().get(0).setReferent(sobstvenik); // само упдейтвам лицето
							vpisvane.getMps().getMpsLice().get(0).setCodeRef(sobstvenik.getId());
						}else {
							vpisvane.getMps().getMpsLice().add(sobst);	
						}
					} else {
						// Трябва ли да трия нещо? По-скоро не! Собственикът трябва да е задължителен. 
					}
				}
				JPA.getUtil().runInTransaction(() -> { 
					this.vpisvane = this.daoVp.save(vpisvane, zaiavlVp, referent1, referent2, sd, mpsList);
				});
				
				if(fromZaiav) {
					// идваме от филтъра на заявленията
					actionNewMps(false);
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG) );
					
				}else {
					// ако има промяна на данни на МПС - да се обнови в списъка
					for(Object[] item: mpsList) {
						if(Objects.equals(item[1], vpisvane.getId())){
							item[0] =  vpisvane.getMps();
							break;
						}
					}
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG) );	
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
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean checkDataVp() {
		boolean rez = true;
		boolean rez2 = true;

		referent1 = getBindingReferent1().getRef();
		if( SearchUtils.isEmpty(referent1.getRefName())){
			rez = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:licеPоdatel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "podatel.msgName"));	
		}
		if(vpisvane.getFromМigr() == null || Objects.equals(vpisvane.getFromМigr(),BabhConstants.CODE_ZNACHENIE_MIGR_NO)) {
			rez = checkIdentifFZL(rez, referent1.getFzlEgn(), referent1.getFzlLnc(), IDFORM+":registerTabs:licеPоdatel:egn",getMessageResourceString(beanMessages, "podatel.msgValidIdentFZL"));
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
				rez = checkIdentifFZL(rez, referent2.getFzlEgn(), referent2.getFzlLnc(), IDFORM+":registerTabs:liceZaqvitel:egn", getMessageResourceString(beanMessages, "zaiavitel.msgValidIdentFZL"));
			}else {
				rez = checkIdentifNFZL(rez, referent2.getNflEik(), referent2.getFzlLnEs(), IDFORM+":registerTabs:liceZaqvitel:eik", getMessageResourceString(beanMessages, "zaiavitel.msgValidIdentNFZL"));
			}
			if(SearchUtils.isEmpty(referent2.getContactPhone())) {
				rez = false;
				JSFUtils.addMessage(IDFORM+":registerTabs:liceZaqvitel:contactPhone",FacesMessage.SEVERITY_ERROR,"Въведете телефон за контакт със заявителя!");
			}
		}
		
		
		if(fromZaiav) {			
		//	rez = actionAddMpsToList(false); // ако са забравили да натиснат бутона Прехвърли, а натискат директно Запис!
			 
			if(mpsList == null || mpsList.isEmpty()) {
				rez = false;
				JSFUtils.addMessage(IDFORM+":registerTabs:tblMpsList",FacesMessage.SEVERITY_ERROR,"Въведете и прехвърлете транспортно средтсво в списъка!");
			}
		} else {
			rez2 = checkDataMps(); // ако е заредено от вписване да се провери за валидни данни за ТС
		}
		return rez && rez2;
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
				flash.put("beanName", "regMpsZJ"); // задължително се подава името на бийна, ако отиваме към таба със статусите
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
	
	/**
	 * Избор на МПС от компонентата за търсене - в резултата са вкл. само вече регистрираме МПС с валидна регистрация!!
	 * проверка за дублиране
	 */
	public void actionSrchMPS() {
		if (mps != null) {
			actionChangeRegN( mps.getNomer() );	
		}
	}
	
		
	/**
	 * бутон Нов вид животно  
	 */
	public void actionInpNewVidJiv () {
		setIndexTabl(null);
		setVidJiv(null);   
		setBroiJiv(null);  
	}
	
	/**
	 * корекция на ред от таблицата - животни
	 * @param jiv
	 * @param idx
	 */
	public void actionEditVidJiv( MpsKapacitetJiv jiv, Integer idx) {		
		 setIndexTabl(idx);
		 setVidJiv(jiv.getVidJiv());
		 setBroiJiv(jiv.getBroiJiv());
		 PrimeFaces.current().executeScript("PF('dlgVidJiv').show();");
	}


	/**
	 * изтриване на ред от  таблицата - животни за обезщетение 
	 * @param jiv
	 * @param idx
	 */
	public void actionRemoveJivFromList(MpsKapacitetJiv jiv, Integer idx) {
		 vpisvane.getMps().getMpsKapacitetJiv().remove(jiv);
		  
	}
	
	/**
	 * Добавяне на животно при връщане от модалния				
	 */
	public void actionAddJiv() {		 		
		if (this.vidJiv == null ) {  
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Въведете вид на животно за превоз!");
			return;
		}
		
		 // Проверка дали вече този вид не е бил въведен 
		if (vpisvane.getMps().getMpsKapacitetJiv()== null ) {
			vpisvane.getMps().setMpsKapacitetJiv(new ArrayList<>());
		} 
		int i = 0;
		for (MpsKapacitetJiv item : vpisvane.getMps().getMpsKapacitetJiv()) {
			if (!Objects.equals(i, indexTabl)) {
				if (Objects.equals(vidJiv,item.getVidJiv())) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Вече е избрано животно от този вид!");
					return;
				}
			}
			i++;
		}
	    
	    MpsKapacitetJiv jiv = new MpsKapacitetJiv ();
		if (this.indexTabl == null) {
			// Добавяне на нов вид животно
			jiv.setVidJiv(vidJiv);
			jiv.setBroiJiv(broiJiv);
		    vpisvane.getMps().getMpsKapacitetJiv().add(jiv);
	    } else { 
	    	jiv = vpisvane.getMps().getMpsKapacitetJiv().get(this.indexTabl.intValue());
	    	jiv.setVidJiv(vidJiv);
			jiv.setBroiJiv(broiJiv);
	    }
		
		
		PrimeFaces.current().executeScript("PF('dlgVidJiv').hide();");
	}
	
	 
	/**
	 * Търсене по рег. номер на МПС
	 * Търси в всички МПС в таблица Mps - без значение дали са регистрирани или не 
	 * 
	 */
	public void actionChangeRegN( String regNom ) {
		//TODO Преди записа - трябва ли да се правят всички тези проверки? 
		// ако е прасанто заявлнеито - ще има ли проверка при парсването - поне дали вече не е регистрирано? 
		disableEdit = true;
	
		if (regNom != null || !SearchUtils.isEmpty(regNom)) {
			try {
				boolean bb = true;
				String msg = null;
				Integer idMps = null;
				//"Не е намерено транспортно средство с рег. номер " + regNom;
				// проверка за дублиране
				// 
				regNom = MPSsearch.convertMpsNomer(regNom);
				vpisvane.getMps().setNomer(regNom); // конвертирания номер
				int idx = 0;
				for(Object[] item : mpsList) {
					 Mps ts = (Mps)item[0];
					 if( Objects.equals(ts.getNomer(), regNom) && !Objects.equals(idx,indexTablMps)) {
						 msg = "Транспортно средство с рег. номер " + regNom+ " вече е добавено в списъка!";
						 bb = false;
						 break;
					 }
					 idx++;
				}
				if(bb) {
					Object[] mpsInfo = null;					
					mpsInfo = new MpsDAO(getUserData()).findMpsInfoByIdOrNomer(null, regNom.trim());
										
					//въвели са някакъв номер, който вече го има в базата 
					
					if (mpsInfo != null && mpsInfo.length > 0 ) {
						idMps = Integer.valueOf(mpsInfo[0].toString());
						Integer status = null;
						if(mpsInfo[6] != null) {
							status = Integer.valueOf(mpsInfo[6].toString());
						}
						msg = checkRegMps(status,idMps, regNom );//проверка дали вече има регистрация!!
					}
				}
				if(msg == null && idMps != null) {
					mps = new MpsDAO(getUserData()).findById(idMps);
					vpisvane.setMps(mps);
					
					loadSobstvenikMps();
				}else if(msg != null) {
					if(fromZaiav) {
						vpisvane.setMps(new Mps());
					} else {
						vpisvane.getMps().setNomer(null);
					}
					disableEdit = false;
					msgScrollTop(msg);
				} 
				
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при търсене на транспортно средство по рег. номер!", e);
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при търсене на транспортно средство по рег. номер!");
			} catch (Exception e) {
				LOGGER.error("Грешка при търсене на транспортно средство по рег. номер!", e);
			} finally {
				JPA.getUtil().closeConnection();
			}
		}
	}
	
	/**
	 * проверка дали вече има регситрация
	 * @param status
	 * @param idMps
	 * @param regNom
	 * @return
	 */
	private String checkRegMps(Integer status, Integer idMps, String regNom) {
		String msg = null;
		if(Objects.equals(status, BabhConstants.STATUS_VP_VPISAN)) {
			msg = "Tранспортно средство с рег. номер " + vpisvane.getMps().getNomer() + " вече притежава валидна регистарция за превоз на животни.";
		} 
		return msg;
	}
	
	/**
	 * проверка за задъл. полета за МПС
	 * @return
	 */
	private boolean checkDataMps() {
		boolean bb = true;
		if(vpisvane.getMps().getMpsLice() == null){
			vpisvane.getMps().setMpsLice(new ArrayList<>());
		}
		if(SearchUtils.isEmpty(vpisvane.getMps().getNomer())) {
			bb = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:regNTS",FacesMessage.SEVERITY_ERROR,"Въведете рег. номер на ТС!");
		}
		if(vpisvane.getMps().getVid() == null) {
			bb = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:odobTSvid_label",FacesMessage.SEVERITY_ERROR,"Въведете вид на ТС!");
		} 
		if(SearchUtils.isEmpty(vpisvane.getMps().getModel())) {
			bb = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:model",FacesMessage.SEVERITY_ERROR,"Въведете марка/модел на ТС!");
		} 
		if(SearchUtils.isEmpty(vpisvane.getMps().getDopInfo()) && (vpisvane.getMps().getMpsKapacitetJiv() == null || vpisvane.getMps().getMpsKapacitetJiv().isEmpty())) {
			bb = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:tblVidJivList",FacesMessage.SEVERITY_ERROR,"Въведете животни, за които е предназанчено ТС!");
		}
		if(SearchUtils.isEmpty(vpisvane.getMps().getNomDatReg())) {
			bb = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:odobTStalon",FacesMessage.SEVERITY_ERROR,"Въведете номер и дата на регистрационен талон!");
		}
		//отпада 16
//		if(vpisvane.getMps().getNasMesto() == null) {
//			bb = false;
//			JSFUtils.addMessage(IDFORM+":registerTabs:nasMesto",FacesMessage.SEVERITY_ERROR,"Въведете местодомуване на ТС!");
//		}
		
		if(SearchUtils.isEmpty(vpisvane.getMps().getPlosht())) {
			bb = false;
			JSFUtils.addMessage(IDFORM+":registerTabs:odobTSplosht",FacesMessage.SEVERITY_ERROR,"Въведете площ на ТС!");
		}
		// за сега изобщо го махаме!
//		if(vpisvane.getMps().getPatuvane() == null) {
//			bb = false;
//			JSFUtils.addMessage(IDFORM+":registerTabs:typeTravel",FacesMessage.SEVERITY_ERROR,"Изберете вид на пътуването!");
//		}
		if(vpisvane.getMps().getNavigation() == null) {
			vpisvane.getMps().setNavigation(BabhConstants.CODE_ZNACHENIE_NE);
		}
		
		sobstvenik = getBindingSobstvenik().getRef();
		if(sobstvenik == null || SearchUtils.isEmpty(sobstvenik.getRefName())){ 
			 // ако собсвеника не е посочен - да се зареди заявителя
			sobstvenik = referent2;			
		}
		
		
		return bb;
	}

	
	/**
	 * Прехвърля в списъка текущото ТС и подготвя екрана за въвеждане на ново ТС
	 * Само, ако е отворено през екрана за заявлението!!!
	 */
	public boolean actionAddMpsToList(boolean ff) {
		boolean rez = true;
		if(mpsList == null) {
			mpsList = new ArrayList<>();
		}
	
		if(checkDataMps()) {		
			//if(newMps) {
			if(indexTablMps == null) {
				// ново ТС
				MpsLice sobst = null;
				if(sobstvenik != null) {
					sobst = new MpsLice();// избраното лице собственик  -  като обект MpsLice
					sobst.setReferent(sobstvenik);
					sobst.setTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK);
					vpisvane.getMps().getMpsLice().add(sobst);
				}
				addNewMPStoList(vpisvane.getMps());
			}  else {
				Object[] item = mpsList.get(indexTablMps);
				item[0] = vpisvane.getMps();
			}
			sobstvenikLast = sobstvenik; // необходимо е, защото може да избрано ТС, но ТС да не е прехвърлено в списъка и така ще остане собственик, който няма нищо общо....
		//	mestoLast = mps.getNasMesto(); //ОТПАДА
		//	adrLast = mps.getAdres();	 //ОТПАДА
			patuvaneLast = mps.getPatuvane();
			actionNewMps(true);
			if(ff) {
				PrimeFaces.current().focus(IDFORM+":registerTabs:regNTS");
			}
			if(bindigMpsList != null) {
				// да позиционира на последнат страница на таблицата 
				String eStr = "PF('widTblMpsList').getPaginator().setPage("+ (bindigMpsList.getPageCount() - 1) +");";
				PrimeFaces.current().executeScript(eStr);				
			}
			
		} else {
			PrimeFaces.current().executeScript("document.body.scrollTop = 0; document.documentElement.scrollTop = 0;");
			hidePlDein = false;
			rez = false;
		}
		return rez;
	}
 
	private void addNewMPStoList(Mps mps) {
		Object[] item = new Object[3];		
		item[0] = mps;
		item[1] = null;		 	
		item[2] = null;	
		mpsList.add(item);
	}
	
	/**
	 * Зачиства панела за данни за ТС без да прехвърля в списъка!! 
	 */
	public void actionNewMps(boolean f) {

		//да нулирам данните за мпс... и за цялото вписване????
		
		vpisvane.setMps(new Mps());
		vpisvane.getMps().setDarj(BabhConstants.CODE_ZNACHENIE_BULGARIA);
		vpisvane.setId(null);
		vpisvane.setUserLastMod(null);
		vpisvane.setDateLastMod(null);
		vpisvane.setStatus(BabhConstants.STATUS_VP_WAITING);
		vpisvane.setDateStatus(null);			
		disableEdit = false;
		hidePlDein = false;
		indexTablMps = null;

		if(sobstvenikLast != null) {
			sobstvenik = sobstvenikLast; // собственика по подрзбиране да съвпада с последния сoбственик на ТС,избран от таблицата или прехвърлен в нея
		} else {
			sobstvenik = new Referent();
		}
		// отпада
//		if(mestoLast != null) {
//			this.vpisvane.getMps().setNasMesto(mestoLast);
//		}
//		if(adrLast != null) {
//			this.vpisvane.getMps().setAdres(adrLast);
//		}
		
		if(patuvaneLast != null) {
			this.vpisvane.getMps().setPatuvane(patuvaneLast);
		} else if (mpsList != null && !mpsList.isEmpty()){
			Object row[] = (Object[]) mpsList.get(0);
			if(row != null) {
				patuvaneLast = ((Mps)row[0]).getPatuvane();
				this.vpisvane.getMps().setPatuvane(patuvaneLast);
			}
		}
		
		if(f) {
			PrimeFaces.current().focus(IDFORM+":registerTabs:regNTS");
		}
			
	}
	

	/*** КОПИРА  собственика от заявителя***/
	public void actionCopyFromZaiav() {
		referent2 = getBindingReferent2().getRef();
//		if (referent2 == null || referent2.getRefType() == null || referent2.getAddressKoresp() == null
//			|| this.referent2.getAddressKoresp().getEkatte() == null) {
//			msgScrollTop("Няма посочен адрес на заявител!");
//			return;
//		}
//		
//		this.vpisvane.getMps().setNasMesto(this.referent2.getAddressKoresp().getEkatte());
//		this.vpisvane.getMps().setAdres(this.referent2.getAddressKoresp().getAddrText()); 
	 
		sobstvenik = referent2; 	 // собственика по подрзбиране да съвпада със заявителя 
	}
	
	
	
	/**
	 * корекция на ред от таблицата - МПС
	 * @param jiv
	 * @param idx
	 */
	public String actionEditMps( Object[] item, Integer idx ) {	
		indexTablMps = idx;
		String outcome = null;
		if(item != null) {
			if(fromZaiav) {
				Mps ts = (Mps)item[0];
				vpisvane.setMps(ts);
				if(item[1] != null) {
					vpisvane.setId((Integer)item[1]);
				}
				if(item[2] != null) {
					vpisvane.setStatus((Integer)item[2]);
				}			
				
				hidePlDein = false;
				loadSobstvenikMps();
				sobstvenikLast = sobstvenik;
				PrimeFaces.current().focus(IDFORM+":registerTabs:regNTS");
			} else if(item[1] != null) {
				// ако екрана е зареден от филтъра на вписванията - препраща към екрана за вписване на съответното ТС
				flash.put("focusMps", "1");
				if(isView == 0) {
					outcome = "regMpsZJ.xhtml?faces-redirect=true&idV=" + (Integer)item[1];
				}else {
					outcome = "regMpsZJView.xhtml?faces-redirect=true&idV=" + (Integer)item[1];
				}
			}
		}
		return outcome;
	}

	
	/**
	 * изтриване на ред от таблицта с МПС  - само, ако вече няма вписване!!!
	 * @param mps
	 * @param idx
	 */
	public void actionRemoveMPSFromList(Object[] mps){
		 mpsList.remove(mps); 
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


	
	/**
	 * показва съобщение и скролира до горния край на екрана 
	 * @param msg
	 */
	public void msgScrollTop(String msg) {
		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, msg );
		PrimeFaces.current().executeScript("document.body.scrollTop = 0; document.documentElement.scrollTop = 0;");
	}



	public Integer getIndexTabl() {
		return indexTabl;
	}



	public void setIndexTabl(Integer indexTabl) {
		this.indexTabl = indexTabl;
	}



	public Integer getBroiJiv() {
		return broiJiv;
	}



	public void setBroiJiv(Integer broiJiv) {
		this.broiJiv = broiJiv;
	}



	public Integer getVidJiv() {
		return vidJiv;
	}



	public void setVidJiv(Integer vidJiv) {
		this.vidJiv = vidJiv;
	}



	public Referent getSobstvenik() {
		return sobstvenik;
	}



	public void setSobstvenik(Referent sobstvenik) {
		this.sobstvenik = sobstvenik;
	}



	public Mps getMps() {
		return mps;
	}



	public void setMps(Mps mps) {
		this.mps = mps;
	}


	public List<Object[]> getMpsList() {
		return mpsList;
	}



	public void setMpsList(List<Object[]> mpsList) {
		this.mpsList = mpsList;
	}



	public List<Files> getFilesList() {
		return filesList;
	}



	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}



	public boolean isFromZaiav() {
		return fromZaiav;
	}



	public void setFromZaiav(boolean fromZaiav) {
		this.fromZaiav = fromZaiav;
	}



	public boolean isDisableEdit() {
		return disableEdit;
	}



	public void setDisableEdit(boolean disableEdit) {
		this.disableEdit = disableEdit;
	}


	public boolean isHidePlDein() {
		return hidePlDein;
	}



	public void setHidePlDein(boolean hidePlDein) {
		this.hidePlDein = hidePlDein;
	}


	
	public Object[] getLastDoc() {
		return lastDoc;
	}



	public void setLastDoc(Object[] lastDoc) {
		this.lastDoc = lastDoc;
	}



	public RefCorrespData getBindingReferent2() {
		return bindingReferent2;
	}



	public void setBindingReferent2(RefCorrespData bindingReferent2) {
		this.bindingReferent2 = bindingReferent2;
	}



	public RefCorrespData getBindingSobstvenik() {
		return bindingSobstvenik;
	}



	public void setBindingSobstvenik(RefCorrespData bindingSobstvenik) {
		this.bindingSobstvenik = bindingSobstvenik;
	}

	public Referent getSobstvenikLast() {
		return sobstvenikLast;
	}

	public void setSobstvenikLast(Referent sobstvenikLast) {
		this.sobstvenikLast = sobstvenikLast;
	}

	public Integer getIndexTablMps() {
		return indexTablMps;
	}

	public void setIndexTablMps(Integer indexTablMps) {
		this.indexTablMps = indexTablMps;
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
	public boolean isFocusMps() {
		return focusMps;
	}
	public void setFocusMps(boolean focusMps) {
		this.focusMps = focusMps;
	}
//	public Integer getMestoLast() {
//		return mestoLast;
//	}
//	public void setMestoLast(Integer mestoLast) {
//		this.mestoLast = mestoLast;
//	}
//	public String getAdrLast() {
//		return adrLast;
//	}
//	public void setAdrLast(String adrLast) {
//		this.adrLast = adrLast;
//	}
	public DataTable getBindigMpsList() {
		return bindigMpsList;
	}
	public void setBindigMpsList(DataTable bindigMpsList) {
		this.bindigMpsList = bindigMpsList;
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
	public Map<Integer, Object> getSpecificsEkatte() {
		return specificsEkatte;
	}
	public void setSpecificsEkatte(Map<Integer, Object> specificsEkatte) {
		this.specificsEkatte = specificsEkatte;
	}

	public Integer getPatuvaneLast() {
		return patuvaneLast;
	}

	public void setPatuvaneLast(Integer patuvaneLast) {
		this.patuvaneLast = patuvaneLast;
	}

	public int getMigration() {
		return migration;
	}

	public void setMigration(int migration) {
		this.migration = migration;
	}


}

