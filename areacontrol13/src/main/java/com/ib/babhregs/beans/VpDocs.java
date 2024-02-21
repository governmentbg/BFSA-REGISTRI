package com.ib.babhregs.beans;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.components.CompUdostDokument;
import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.db.dto.RegisterOptionsDocsOut;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.daeu.esb.epay.PayClient;
import com.ib.daeu.esb.epay.common.CommonTypeActor;
import com.ib.daeu.esb.epay.common.CommonTypeActor.ActorType;
import com.ib.daeu.esb.epay.common.CommonTypeUID;
import com.ib.daeu.esb.epay.common.CommonTypeUID.EType;
import com.ib.daeu.esb.epay.common.RegisterPaymentResponse;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.BaseUserData;
import com.ib.system.SysConstants;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.SearchUtils;

/**
 * Документи към вписването 
 */

//TODO изтриването на вече създадени УД!!!????

@Named
@ViewScoped
public class VpDocs extends IndexUIbean {

	private static final long serialVersionUID = 455998486008051946L;
	private static final Logger LOGGER = LoggerFactory.getLogger(VpDocs.class);
	public  static final  String BEANNAME_ZJ   = "regLicaDeinZJ";
	public  static final  String BEANNAME_MPS_ZJ   = "regMpsZJ";
	public  static final  String BEANNAME_VLZ  = "regVLZ";
	public  static final  String BEANNAME_FR   = "regTargovtsiFuraj";
	public  static final  String BEANNAME_MPS_FR   = "regMpsFuraj";
	public  static final  String BEANNAME_VLP  = "regVLP";
	public  static final  String BEANNAME_TURGOVIA_VLP  = "regObektVLP";
	public  static final  String BEANNAME_OEZ ="regOEZ";
	
	public  static final  String OBJECTINUSE   = "general.objectInUse";
	
	private SystemData sd;
	private UserData ud;
	private Date decodeDate;
	private transient VpisvaneDAO	daoVp;	
	private transient DocDAO docDao;
	
	/**
	 *  името на бийна, в който е инклудната страницата
	 */
	private String beanName; 
	private String clId;
	/**
	 * вписване - от бийна, в който е инклудната страницата
	 */ 
	private Vpisvane vpObj;
	private List<Object[]> docsList; 
	private Doc doc = new Doc();
	private int rowIdx;
	private Referent referent2; // заявител;
	
	private FilesDAO fDao;
	private List<Files> filesList;
	private List<Files> filesEditList; //файловете на документа за редакция
	//private Map<Integer, List<Files>> filesMap = new HashMap<Integer, List<Files>>();
	/**
	 * да може да се редактира документа,само ако е собствен
	 */
	private boolean readonly = false; 
	/**
	 * заявлението за първоначално вписване не може да се премахне - трябва да се изтрие самото вписване и тогава статуса на заявл. за първ. вписване става "необработено"
	 * позовлява се премахването на връзката между заявление за промяна и вписването
	 * Уд - не се позволява изтриване
	 * останалите могат да бъдат изтривани
	 */
	private boolean removeDoc = false; 
	/**
	 * true - ако се изисква издаване на  УД за регистъра
	 */
	private boolean nastrUd; 
	/**
	 * списък със всички УД, описани в настройките за този регистър 
	 */
	private List<RegisterOptionsDocsOut> listDocsOut ;
	private RegisterOptionsDocsOut udostDelovDoc;
	/**
	 * УД - да се забрани изтривнето и промяната на вида док.
	 * само в този случай д се виждат бутоните за генериране на УД от шаблон
	 */
	private boolean udosDoc = false;
	private boolean newUdosDoc = false; 
	/**
	 * дали номера на новия удост. документа  да се копира от предишния УД
	 */
	private boolean copyNomUd; 
	/**
	 * дали е срочен или безсрочен
	 */
	private boolean srokValid = false;  
	
	private CompUdostDokument bindCompUdostDoc;
	//private boolean viewBtnUD; // това е за дублиакта - за сега го коментирам
	//private boolean viewBtnUDNew; // вече е излишно
	
	/** Информацията за последното заявление към вписването (ако има такова) */
	private Object[] lastDoc;
	


	public void initTab() {
		
		sd = (SystemData) getSystemData();
		ud = getUserData(UserData.class);			
		daoVp = new VpisvaneDAO(ud);		
		docDao = new DocDAO(ud);
		fDao = new FilesDAO(ud);
		clId = "";

		try {
			
			beanName = (String)JSFUtils.getFlashScopeValue("beanName");			
			if(BEANNAME_ZJ.equals(beanName)) { // лица - дейности животни
				RegLicaDeinZJ bean = (RegLicaDeinZJ) JSFUtils.getManagedBean(BEANNAME_ZJ);
				vpObj = bean.getVpisvane();
				referent2 = bean.getReferent2(); 
				lastDoc = bean.getLastDoc();
				clId = "dZJForm:registerTabs:";
			} else if(BEANNAME_MPS_ZJ.equals(beanName)){ // МПС - регистриране на МПС животни
				RegMpsZJ bean = (RegMpsZJ) JSFUtils.getManagedBean(BEANNAME_MPS_ZJ);
				vpObj = bean.getVpisvane();
				referent2 = bean.getReferent2(); 
				lastDoc = bean.getLastDoc();
				clId = "mpsZJForm:registerTabs:";
			} else if(BEANNAME_VLZ.equals(beanName)){ // ЗЖ - регистриране на ВЛЗ
				RegVLZ bean = (RegVLZ) JSFUtils.getManagedBean(BEANNAME_VLZ);
				vpObj = bean.getVpisvane();
				referent2 = bean.getReferent2(); 
				lastDoc = bean.getLastDoc();
				clId = "vlzForm:registerTabs:";
		    } else if(BEANNAME_FR.equals(beanName)){ // лица - дейности фуражи
				RegTargovtsiFuraj bean = (RegTargovtsiFuraj) JSFUtils.getManagedBean(BEANNAME_FR);
				vpObj = bean.getVpisvane();
				referent2 = bean.getReferent2(); 
				lastDoc = bean.getLastDoc();
				clId = "targovtsiFurajForm:registerTabs:";
			} else if(BEANNAME_MPS_FR.equals(beanName)){ // МПС - регистриране на МПС фуражи
				RegMpsFuraj bean = (RegMpsFuraj) JSFUtils.getManagedBean(BEANNAME_MPS_FR);
				vpObj = bean.getVpisvane();
				referent2 = bean.getReferent2(); 
				lastDoc = bean.getLastDoc();
				clId = "targovtsiFurajForm:registerTabs:";
		    } else if(BEANNAME_VLP.equals(beanName)){ // лица - дейности ВЛП 
		    	RegVLP bean = (RegVLP) JSFUtils.getManagedBean(BEANNAME_VLP);
				vpObj = bean.getVpisvane();
				referent2 = bean.getReferent2(); 
				lastDoc = bean.getLastDoc();
				clId = "vlpTargForm:registerTabs:";
			} else if(BEANNAME_TURGOVIA_VLP.equals(beanName)){ // търговия с ВЛП 
				RegObektVLP bean = (RegObektVLP) JSFUtils.getManagedBean(BEANNAME_TURGOVIA_VLP);
				vpObj = bean.getVpisvane();
				referent2 = bean.getReferent2(); 
				lastDoc = bean.getLastDoc();
				clId = "obektVlpForm:registerTabs:";
			}  else if(BEANNAME_OEZ.equals(beanName)){ // OEZ
				RegOEZ bean = (RegOEZ) JSFUtils.getManagedBean(BEANNAME_OEZ);
				vpObj = bean.getVpisvane();
				referent2 = bean.getReferent2(); 
				lastDoc = bean.getLastDoc();
				clId = "oezForm:registerTabs:";
			}
			
			if(vpObj != null) {
				actionNew(false);
				rowIdx = -2;	
				JPA.getUtil().runWithClose(() -> docsList = daoVp.findVpisvaneDocList(vpObj.getId()));
			
				// проверка дали в настройките на регитъра е указано, че се издава УД
				RegisterOptions registerOptions = sd.getRoptions().get(this.vpObj.getIdRegister()); // всички настройки за регистъра
				RegisterOptionsDocsIn tmpInDoc = registerOptions.getDocsIn().get(0); // вземам 1-я вх. док. - за всички док.,  настройкта за УД, е еднаква
				setNastrUd(sd.isElementVisible(tmpInDoc.getVidDoc(), BabhConstants.EKRAN_ATTR_UDOST_DOC)); 
				
				if(Objects.equals(vpObj.getFromМigr(), BabhConstants.CODE_ZNACHENIE_MIGR_MANUAL)){
					// при ръчно въвеждане на данни от миграция се позволява корекция на вида и номeра на УД
					loadNastrDocsOut();	
				}
					
//				if(docsList!=null && !docsList.isEmpty()) {
//					List<Files> filesList  = new ArrayList<Files>();
//					
//					FilesDAO fDao = new FilesDAO(ud);
//					for(Object [] item : docsList) {
//						filesList.clear();
//						filesList = fDao.selectByFileObjectDop(SearchUtils.asInteger(item[0]), BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
//						filesMap.put(SearchUtils.asInteger(item[0]), filesList);
//						
//					}
//				}
			}
					
		} catch (Exception e) {
			LOGGER.error("Грешка при извличане на документи към вписване!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		}
		
		this.decodeDate = new Date();
	}

	
	
	/**
	 * Избран е ред от списъка
	 * @param row
	 */
   public void actionLoadDataFromList(int idx) {
	   newUdosDoc = false;
	   rowIdx = idx;
	   try {
		  //  viewBtnUD = false;
		  //  viewBtnUDNew = false;
			srokValid = false; // безсрочен
			doc = docDao.findById(SearchUtils.asInteger(docsList.get(idx)[0]));
			udosDoc = false;
			RegisterOptions registerOptions = sd.getRoptions().get(this.vpObj.getIdRegister()); // всички настройки за регистъра
			Object[] docSettings = docDao.findDocSettings(doc.getRegistraturaId(),doc.getDocVid(),getSystemData());
		    if(docSettings != null && 
		      (Objects.equals( docSettings[8], BabhConstants.CODE_ZNACHENIE_HAR_DOC_UDOST)||
     		   Objects.equals( docSettings[8], BabhConstants.CODE_ZNACHENIE_HAR_IZMENENIE_UD))) {
		    	udosDoc = true;
		    }
		   
			filesEditList = fDao.selectByFileObjectDop(doc.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC); // използва се този метод за зареждане на файлове, за да се заредят доп. полета - лични дании, официален док и т.н.
			
			removeDoc = true;
			if(doc.getDocType().equals(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_OWN)){
				readonly = false;
				List<RegisterOptionsDocsOut> listDocsOut1 =   registerOptions.getDocsOut();
				for(RegisterOptionsDocsOut d: listDocsOut1) {
					if(Objects.equals(d.getVidDoc(), doc.getDocVid()) ) {
						if (d.getTypePeriodValid() != null && d.getPeriodValid() != null) {
							srokValid = true; //  УД е за определeн период от време
						}
						break;
					}
				}
			   
			}else {
				readonly = true; // това е за вид док. , рег. номер и дата
				List<RegisterOptionsDocsIn> listDocsIn =   registerOptions.getDocsIn();
				for(RegisterOptionsDocsIn d: listDocsIn) {
					if(Objects.equals(d.getVidDoc(), doc.getDocVid()) ) {
						if(Objects.equals(d.getPurpose(),BabhConstants.CODE_ZNACHENIE_ZAIAV_PARVONACHALNO)) {
							removeDoc = false;
						}
						break;
					}
				}
			}
			PrimeFaces.current().focus(clId+"otnosno");
		} catch (BaseException e) {
			LOGGER.error("Грешка при зареждане на документ!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
   }
   
//	/**
//	 * Проверка дали вече има генериран УД - като файл
//	 * ако върне <> нулл има два варианта
//	 * [4]-file_id == нулл значи няма файл и трябва да има бутон
//	 * [4]-file_id <> нулл значи има файл и не трябва да има бутона
//	 * 
//	 */
//	public boolean checkForUdostDoc() {
//		boolean rez = true;
//		try { 		 
//			Object[] info = daoVp.findUdostDocIdentFile(vpObj.getId(),BabhConstants.CODE_ZNACHENIE_DOC_VID_ATTACH_UD);
//			if(info != null && info[4] != null ) {
//				rez = false; //намерен е файл
//			} 
//			
//		} catch (BaseException e) {
//			LOGGER.error("Грешка при проверка дали вече има генериран УД от шаблон!", e);				
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
//		} finally {
//			JPA.getUtil().closeConnection();
//		}
//		return rez; 
//	}
   
   /**
    * Нов документ
    */
   public void actionNew(boolean ud) {
	   setDoc(new Doc());
	   doc.setDocType(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_OWN);
	   doc.setRegistraturaId(this.vpObj.getRegistraturaId());
	   doc.setDocDate(new Date());
	   doc.setTlpLevel(BabhConstants.CODE_ZNACHENIE_TLP_LEVEL_WHITE);
	   rowIdx = -1;
	   filesEditList = new ArrayList<Files>();
	   readonly = false;
	   removeDoc = true;
	  // viewBtnUD = false;
	   srokValid = false;
	   udosDoc = ud;
	   newUdosDoc = ud;
	   copyNomUd = false;
	   if(nastrUd && newUdosDoc) {
		 //извлича видовете изходящите документи 
		   loadNastrDocsOut();
	   }
	   
	   PrimeFaces.current().focus(clId+"ddata1");
   }
   
   
   /**
    * извлича видовете изходящите документи 
    */
   private void loadNastrDocsOut() {
		RegisterOptions registerOptions = sd.getRoptions().get(this.vpObj.getIdRegister()); // всички настройки за регистъра
		listDocsOut = registerOptions.getDocsOut();
		
		if(listDocsOut == null || listDocsOut.isEmpty() || listDocsOut.get(0) == null) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Няма зададени удостоверителни документи в настройките на регистъра!");
			PrimeFaces.current().executeScript("scrollToErrors()");
		} else if(listDocsOut.size() == 1) {
			RegisterOptionsDocsOut d = listDocsOut.get(0);
			doc.setDocVid(d.getVidDoc());// за да се вижда на екранa
			settingsUD2(d);// ако е само един - да се зареди по подразбиране.
		// ако обаче са повече -  да се покаже поле за избор на УД (удост. док.) - да направят задължително избор
			settingsUD(false);
		} 
   }
   
   /**
    * извлича настройките на УД от регистър БАБХ
    * @param d
    */
   private void settingsUD2(RegisterOptionsDocsOut d) {
	    copyNomUd = false;
	    udostDelovDoc = d;
		doc.setRnDoc(null);	
		if (d.getTypePeriodValid() != null && d.getPeriodValid() != null) {
			srokValid = true; //  УД е за определeн период от време
		}
		if(Objects.equals(d.getSaveNomReissue(), BabhConstants.CODE_ZNACHENIE_DA) && vpObj.getIdResult() != null) {
			// само, ако документа е от същия вид да взема номера на предишния УД и
			// да заредя регистъра да е със свободен номер, за да мине записа успешно
			try {		
				JPA.getUtil().runWithClose(() -> {
					Doc oldUD =  new DocDAO(ud).findById(Integer.valueOf(vpObj.getIdResult()));
					if(oldUD == null){
						LOGGER.error("Грешка при извличане на УД с ид: " +vpObj.getIdResult());
					} else if( Objects.equals(oldUD.getDocVid(), doc.getDocVid())) {
						doc.setRnDoc(vpObj.getRegNomResult());						
						doc.setRegisterId(BabhConstants.CODE_ZNACHENIE_REG_OWN_PROIZVOLN_NOMER);
						copyNomUd = true;
					}  
				});
				
			} catch (BaseException e) {
				LOGGER.error("Грешка при извличане на натройки за УД!", e);				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
			}		
		}	
   }
    
	
	/**
	 * настройките по вид документ
	 */
   public void settingsUD(boolean loadUD) {
		if(doc.getDocVid() != null) {
			try {
				if(loadUD) {
					srokValid = false;
					// ако е само един -> udostDelovDoc е вече зареден
					for(RegisterOptionsDocsOut d: listDocsOut) {
						if(Objects.equals(d.getVidDoc(), doc.getDocVid())) {
							settingsUD2(d);
							break;
						}
					}
				}
				// търся настройките по вид документ - ТРЯБВА да има за УД
				if(!copyNomUd) {
					Object[] settings = new DocDAO(getUserData()).findDocSettings(((UserData)getUserData()).getRegistratura(), doc.getDocVid(), getSystemData());
				 	if(settings != null && settings[1] != null) {
				 		Integer registerId = (Integer) settings[1];
				 		 if(registerId != null) {
				 			doc.setRegisterId(registerId);
						 }
				 	}else if( doc.getId()== null) {
				 		// за нов документ - ако не намеря настройки - да сложа регистър с произволен номер
				 		doc.setRegisterId(BabhConstants.CODE_ZNACHENIE_REG_OWN_PROIZVOLN_NOMER);
				 	}
				}
			 	
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при извличане на настройки на УД!");
				e.printStackTrace();
			}
		}
	}
	
   
   /**
    * Запис и актуализаиця на документ
    */
   public void actionSave() {	  
	
	   if(checkData()) {
		   try {
			    if(newUdosDoc) {
				   // само за нов УД
				   JPA.getUtil().runInTransaction(() ->  {
					   String newRegNomer = this.daoVp.saveNewUdostDoc(vpObj,udostDelovDoc , doc, sd); 
					   if(newRegNomer != null) {
						   // TODO това е номер на обект ако се е генерирал в този момент
						   // не е ясно дали тук ми трябва за нещо...
					   } });
				   newUdosDoc = false;
	   		    }else {
				   JPA.getUtil().runInTransaction(() -> this.doc = this.docDao.saveDelovoden(doc, vpObj, false, false, sd));   
				}
			   	JPA.getUtil().runWithClose(() -> docsList = daoVp.findVpisvaneDocList(vpObj.getId()));
		   		JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG) );
		   		PrimeFaces.current().executeScript("document.body.scrollTop = 0; document.documentElement.scrollTop = 0;");
			} catch (ObjectInUseException e) {
				LOGGER.error("Грешка при запис на документ към вписване! ObjectInUseException={} ", e.getMessage()); 
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			} catch (Exception e) {
				LOGGER.error("Грешка при запис на документ към вписване! ", e);					
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
			}
		}
   }
   
   public void actionDelete() {
	   try {
			
		    if(removeDoc && readonly) {
		    	// заявление за промяна...
		    	JPA.getUtil().runInTransaction(() ->  {
					this.daoVp.removeZaqavlenie(doc.getId(), vpObj.getId(), sd);
				
					docsList = daoVp.findVpisvaneDocList(vpObj.getId());
				
				}); 
		    	JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,  "Премахването на връзката между заявлението и вписването е успешно!" );
		    } else {
		   
				JPA.getUtil().runInTransaction(() ->  {
					this.docDao.deleteDelovoden(doc, vpObj, sd);
					
					if (filesList != null && !filesList.isEmpty()) {								
						for (Files f : filesList) {
							fDao.deleteFileObject(f);	
						}
					}
					docsList = daoVp.findVpisvaneDocList(vpObj.getId());
				
				});
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,  IndexUIbean.getMessageResourceString(UI_beanMessages, "general.successDeleteMsg") );
		    }
			
			actionNew(false);
		} catch (ObjectInUseException e) {
			LOGGER.error("Грешка при изтриване на документ към вписване! ObjectInUseException "); 
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		} catch (BaseException e) {			
			LOGGER.error("Грешка при изтриване на документ към вписване!", e);			
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		} 
   }
	
	/**
	 * Проверка за валидни данни
	 * @return
	 */
	public boolean checkData() {
		boolean flag = true; 
		if(doc.getDocVid() == null) {
			flag = false;
			JSFUtils.addMessage(clId+"vid",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "docu.vid")));
		}
			
		// Позволява се да се запише документ без рег. номер!! Това не е истински дел. документ!
//		if( SearchUtils.isEmpty(doc.getRnDoc() == null) {
//		//	flag = false;
//		//	JSFUtils.addMessage(clId+"rnDoc",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "docu.regNom")));
//		}
		
		if(doc.getDocDate() == null || doc.getDocDate().after(new Date())) {
			flag = false;
			JSFUtils.addMessage(clId+"dataDoc",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS," валидна дата. Не се позволяват бъдещи дати"));
		}
		
		if(SearchUtils.isEmpty(doc.getOtnosno())) {
			flag = false;
			JSFUtils.addMessage(clId+"otnosno",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "docu.otnosno")));
		}
		
		return flag;
	}
	
	public void docSelect(Integer idDoc) { 
		try {
			filesList = fDao.selectByFileObjectDop(idDoc, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
		} catch (DbErrorException e) {
			LOGGER.error("Грешка при работа с базата данни!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Грешка при извличане на файлове!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	
	/**
	 * разглеждане на заявление
	 * @return
	 */
	public String actionGotoZaiavView(Integer idZ) {
		String urlStr = null; 
		if(idZ != null) {
			urlStr =  "babhZaiavView.xhtml?faces-redirect=true&idObj="+idZ;
		}
		return urlStr;
	}
	/**
	 * Download selected file
	 *
	 * @param files
	 */	
	public void download(Files files) {
		
		try {
			
			if (files.getId() != null){
			
				FilesDAO dao = new FilesDAO(getUserData());					
			
				try {
					
					files = dao.findById(files.getId());	
				
				} finally {
					JPA.getUtil().closeConnection();
				}
				
				if(files.getContent() == null){					
					files.setContent(new byte[0]);
				}
			}

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();

			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			String agent = request.getHeader("user-agent");

			String codedfilename = "";

			if (null != agent && (-1 != agent.indexOf("MSIE") || -1 != agent.indexOf("Mozilla") && -1 != agent.indexOf("rv:11") || -1 != agent.indexOf("Edge"))) {
				codedfilename = URLEncoder.encode(files.getFilename(), "UTF8");
			} else if (null != agent && -1 != agent.indexOf("Mozilla")) {
				codedfilename = MimeUtility.encodeText(files.getFilename(), "UTF8", "B");
			} else {
				codedfilename = URLEncoder.encode(files.getFilename(), "UTF8");
			}

			externalContext.setResponseHeader("Content-Type", "application/x-download");
			externalContext.setResponseHeader("Content-Length", files.getContent().length + "");
			externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + codedfilename + "\"");
			externalContext.getResponseOutputStream().write(files.getContent());

			facesContext.responseComplete();

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	

	/**
	 * След генериране на файла за УД 
	 */
	public void actionAfterGenerateUD() {
		//viewBtnUD = false; // да се скрие бутона - вече има генериран файл
	   try {		
			filesEditList = fDao.selectByFileObjectDop(doc.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
			this.doc = this.docDao.findById(this.doc.getId());
		} catch (BaseException e) {
			LOGGER.error("Грешка при зареждане на файловете към документ!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
		PrimeFaces.current().ajax().update(clId+"docData");
	}
		
	
	
	/**
	 * генериране на УД от шаблон за първи път
	 * файлът се прикачва към последния актуален деловоден УД 
	 * Генериране на удостоверителен документ - като файл
	 */
	public void actionGenUdoc() {
		this.bindCompUdostDoc.initComponent();
		LOGGER.debug(doc.getId().toString());
	}
	
	
//	/**
//	 * Генриране на УД - като нов деловоден документ + файл от шаблона
//	 * така генерирания деловоден УД става актуалния за вписването
//	 */
//	public void generateUdostNovRegNom() {
//		System.out.println("NOV REG NOMER");
//		// Може би тук трябва да вместим ситуацията, когато трябва да се избере и различен вид документ!
//		// Това се случва, ако имаме издадено  УД за временно впшисване и после се издава УД за постоянно вписване
//		// Такава ситуация има за фуражите
//		// TODO
//	}
//	
//	/**
//	 * Генриране на УД - като дубликат - Не се създава нов деловоден документ
//	 */
//	public void generateUdostDublikat() {
//		System.out.println("DUBLIKAT");
//		// TODO
//	}
//	
	/**
	 * Дава ни информация за текущия език на логнатия потребител. Ако не се намери дава SysConstants.CODE_LANG_BG.
	 *
	 * @return код на езика от константите
	 */
	public int getCurrentLang() {
		BaseUserData userData = getUserData();

		return userData != null ? userData.getCurrentLang() : SysConstants.CODE_LANG_BG;
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

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Vpisvane getVpObj() {
		return vpObj;
	}

	public void setVpObj(Vpisvane vpObj) {
		this.vpObj = vpObj;
	}

	public String getClId() {
		return clId;
	}

	public void setClId(String clId) {
		this.clId = clId;
	}

	public int getRowIdx() {
		return rowIdx;
	}

	public void setRowIdx(int rowIdx) {
		this.rowIdx = rowIdx;
	}

	public Doc getDoc() {
		return doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	public List<Object[]> getDocsList() {
		return docsList;
	}

	public void setDocsList(List<Object[]> docsList) {
		this.docsList = docsList;
	}
	
	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public List<Files> getFilesEditList() {
		return filesEditList;
	}

	public void setFilesEditList(List<Files> filesEditList) {
		this.filesEditList = filesEditList;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public CompUdostDokument getBindCompUdostDoc() {
		return bindCompUdostDoc;
	}

	public void setBindCompUdostDoc(CompUdostDokument bindCompUdostDoc) {
		this.bindCompUdostDoc = bindCompUdostDoc;
	}

	public boolean isUdosDoc() {
		return udosDoc;
	}

	public void setUdosDoc(boolean udosDoc) {
		this.udosDoc = udosDoc;
	}

//	public boolean isViewBtnUD() {
//		return viewBtnUD;
//	}
//
//	public void setViewBtnUD(boolean viewBtnUD) {
//		this.viewBtnUD = viewBtnUD;
//	}


//
//	public boolean isViewBtnUDNew() {
//		return viewBtnUDNew;
//	}
//
//
//	public void setViewBtnUDNew(boolean viewBtnUDNew) {
//		this.viewBtnUDNew = viewBtnUDNew;
//	}
//	

	/**
	 *  Генериране на заявка за плащане с код
	 *  TODO - изпраща e-mail
	 * @return
	 */
	public String actionGenEPay() {
		Calendar calendar = Calendar.getInstance();
        // Get today's date
       // Date today = calendar.getTime();
        // Add one day to the date
        calendar.add(Calendar.DAY_OF_YEAR, 30);
        // Get tomorrow's date
        Date tomorrow = calendar.getTime();
        try {
        	String vidZ = sd.decodeItem(BabhConstants.CODE_CLASSIF_VID_REGISTRI, vpObj.getIdRegister(), getCurrentLang(), new Date());
        	if(referent2 != null && doc.getPayAmount() != null) {
        		String suma = String.valueOf(doc.getPayAmount());
        		
        		PayClient pClient = PayClient.getInstance();
        		ActorType typeActor = null;
        		EType eType = null;
        		String ident = null;
        		if(!SearchUtils.isEmpty(referent2.getFzlEgn())) {
	        		// ако заявителя е физ. лице
        			 typeActor =  CommonTypeActor.ActorType.PERSON;
        			 eType = CommonTypeUID.EType.EGN;
        			 ident = referent2.getFzlEgn();
        		} else if(!SearchUtils.isEmpty(referent2.getNflEik())) {
        			 typeActor =  CommonTypeActor.ActorType.LEGALPERSON; 
        			 eType = CommonTypeUID.EType.BULSTAT; ////CommonTypeUID.EType.EIK
        			 ident = referent2.getNflEik();
        		}
        		
        		  RegisterPaymentResponse registerPaymentResponse = pClient.registerPayment(
        				    typeActor,
	                        eType,
	                        ident,
	                        referent2.getRefName(),                      
	                        suma,
	                        "такса за: " + vidZ,
	                        "1",
	                        UUID.randomUUID().toString(),
	                        new Date(),
	                        tomorrow);
        		  LOGGER.debug(registerPaymentResponse.getPaymentId());
        		  // при успешно изпращане - записва PaymentId в заявлението
        		  JPA.getUtil().runInTransaction(() -> docDao.updatePaymentId(doc.getId(),registerPaymentResponse.getPaymentId()));
        		  JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Заявката е изпратена успешно към е-Плащане! ");
        		  //TODO - трябва да изпратим е-мейл на заявителя, че има заявка за плащане!!
        		  //TODO  ще нулираме някога това ид? Ще позволим ли и при какви условия повторно изпращане на заявка?
        	}
            
          //  assertNotNull(registerPaymentResponse);
        } catch (IOException e) {
        	LOGGER.error("Грешка при изпращане на заявка към е-Плащане!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());

        } catch (Exception e) {
        	LOGGER.error("Грешка при изпращане на заявка към е-Плащане!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());

        }
		return "";
	}

	public boolean viewBtnPayRequest() {
		boolean bb = false;
		if(doc.getPayAmount() != null &&  doc.getPayAmountReal() != null && 
		   !doc.getPayAmount().equals(Float.valueOf(0)) &&
		   doc.getPayAmount().compareTo( doc.getPayAmountReal())>0 ) {
			bb = true; 		
		}
		return bb;
	}
	
	public Referent getReferent2() {
		return referent2;
	}


	public void setReferent2(Referent referent2) {
		this.referent2 = referent2;
	}


	public boolean isRemoveDoc() {
		return removeDoc;
	}


	public void setRemoveDoc(boolean removeDoc) {
		this.removeDoc = removeDoc;
	}


	public boolean isSrokValid() {
		return srokValid;
	}


	public void setSrokValid(boolean srokValid) {
		this.srokValid = srokValid;
	}


	public boolean isNastrUd() {
		return nastrUd;
	}


	public void setNastrUd(boolean nastrUd) {
		this.nastrUd = nastrUd;
	}
	

	public List<RegisterOptionsDocsOut> getListDocsOut() {
		return listDocsOut;
	}


	public void setListDocsOut(List<RegisterOptionsDocsOut> listDocsOut) {
		this.listDocsOut = listDocsOut;
	}


	public boolean isNewUdosDoc() {
		return newUdosDoc;
	}


	public void setNewUdosDoc(boolean newUdosDoc) {
		this.newUdosDoc = newUdosDoc;
	}


	public RegisterOptionsDocsOut getUdostDelovDoc() {
		return udostDelovDoc;
	}


	public void setUdostDelovDoc(RegisterOptionsDocsOut udostDelovDoc) {
		this.udostDelovDoc = udostDelovDoc;
	}


	public Object[] getLastDoc() {
		return lastDoc;
	}


	public void setLastDoc(Object[] lastDoc) {
		this.lastDoc = lastDoc;
	}


	public boolean isCopyNomUd() {
		return copyNomUd;
	}


	public void setCopyNomUd(boolean copyNomUd) {
		this.copyNomUd = copyNomUd;
	}

}
