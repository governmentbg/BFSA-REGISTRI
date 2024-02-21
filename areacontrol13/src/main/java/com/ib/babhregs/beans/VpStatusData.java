package com.ib.babhregs.beans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.components.CompUdostDokument;
import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.db.dto.RegisterOptionsDocsOut;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.db.dto.VpisvaneStatus;
import com.ib.babhregs.system.BabhClassifAdapter;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.SearchUtils;


/**
 * 
 * Статус на вписването - смяна на статус и история 
 * 
 * 
 */

@Named
@ViewScoped
public class VpStatusData extends IndexUIbean {

	private static final long serialVersionUID = 6107202601342571098L;
	private static final Logger LOGGER = LoggerFactory.getLogger(VpStatusData.class);
	public  static final  String BEANNAME_ZJ   = "regLicaDeinZJ";
	public  static final  String BEANNAME_MPS_ZJ   = "regMpsZJ";
	public  static final  String BEANNAME_VLZ  = "regVLZ";
	public  static final  String BEANNAME_FR   = "regTargovtsiFuraj";
	public  static final  String BEANNAME_MPS_FR   = "regMpsFuraj";
	public  static final  String BEANNAME_VLP  = "regVLP";
	public  static final  String BEANNAME_TURGOVIA_VLP  = "regObektVLP";
	public  static final  String BEANNAME_OEZ ="regOEZ";
	
	private SystemData sd;
	private UserData ud;
	private Date decodeDate;
	private transient VpisvaneDAO	daoVp;
	private Doc delovoden;
	private RegisterOptionsDocsOut udostDelovDoc;
	private Integer vidUdostDoc;// има смисъл, само ако в списъка има повече от един УД
	private String udRn; // произволен номер на УД - само при генериране на УД
	
	private  String newRegNomer;
	
	// компонентът за удостоверителни докумемнти
	private CompUdostDokument bindCompUdostDoc;
	

	/**
	 *  името на бийна, в който е инклудната страницата
	 */
	private String beanName;
	private String clId;
	private String clFr;
	/**
	 * вписване - от бийна, в който е инклудната страницата
	 */ 
	private Vpisvane vpObj;
	
	private List<VpisvaneStatus> statusList;
	private VpisvaneStatus vpStatus;
	private int rowIdx;
	/**
	 * списък със всички УД, описани в настройките за този регистър 
	 */
	private List<RegisterOptionsDocsOut> listDocsOut ;
	/**
	 * да се вижда ли жълтия бутон за генериране на файл за УД
	 */
	private boolean viewBtnUD;  
	/**
	 * запазва стария статус на вписването
	 */
	private Integer oldStatus;
	/**
	 * true - ако се изисква издаване на  УД
	 */
	private boolean nastrUd;
	/**
	 * дали номера на УД е с произволен алг. и трябва да се подаде ръчно
	 */
	private boolean freeNomUd;
	 
	/**
	 * номер на обект от миграция - ръчна
	 * видими са само, ако vpObj.getFromМigr()=0
	 */
	private String migrNomObject;	
 

	public void initTab() {
		sd = (SystemData) getSystemData();
		ud = getUserData(UserData.class);			
		daoVp = new VpisvaneDAO(getUserData());		
		clId = "";
		clFr = "";
		udostDelovDoc = null;
		newRegNomer = null;
		try {
			
			beanName = (String)JSFUtils.getFlashScopeValue("beanName");			
			if(BEANNAME_ZJ.equals(beanName)) { // лица - дейности животни
				RegLicaDeinZJ bean = (RegLicaDeinZJ) JSFUtils.getManagedBean(BEANNAME_ZJ);
				vpObj = bean.getVpisvane();
				clId = "dZJForm:registerTabs:";
				clFr = "dZJForm:";
			} else if(BEANNAME_MPS_ZJ.equals(beanName)){ // МПС - регистриране на МПС животни
				RegMpsZJ bean = (RegMpsZJ) JSFUtils.getManagedBean(BEANNAME_MPS_ZJ);
				vpObj = bean.getVpisvane();
				clId = "mpsZJForm:registerTabs:";
				clFr = "mpsZJForm:";
			} else if(BEANNAME_VLZ.equals(beanName)){ // ЗЖ - регистриране на ВЛЗ
				RegVLZ bean = (RegVLZ) JSFUtils.getManagedBean(BEANNAME_VLZ);
				vpObj = bean.getVpisvane();
				clId = "vlzForm:registerTabs:";
				clFr = "vlzForm:";
		    } else if(BEANNAME_FR.equals(beanName)){ // лица - дейности фуражи
				RegTargovtsiFuraj bean = (RegTargovtsiFuraj) JSFUtils.getManagedBean(BEANNAME_FR);
				vpObj = bean.getVpisvane();
				clId = "targovtsiFurajForm:registerTabs:";
				clFr = "targovtsiFurajForm:";
			} else if(BEANNAME_MPS_FR.equals(beanName)){ // МПС - регистриране на МПС фуражи
				RegMpsFuraj bean = (RegMpsFuraj) JSFUtils.getManagedBean(BEANNAME_MPS_FR);
				vpObj = bean.getVpisvane();
				clId = "targovtsiFurajForm:registerTabs:";
				clFr = "targovtsiFurajForm:";
		    } else if(BEANNAME_VLP.equals(beanName)){ // лица - дейности ВЛП 
		    	RegVLP bean = (RegVLP) JSFUtils.getManagedBean(BEANNAME_VLP);
				vpObj = bean.getVpisvane();
				clId = "vlpTargForm:registerTabs:";
				clFr = "vlpTargForm:";
			} else if(BEANNAME_TURGOVIA_VLP.equals(beanName)){ // търговия с ВЛП 
				RegObektVLP bean = (RegObektVLP) JSFUtils.getManagedBean(BEANNAME_TURGOVIA_VLP);
				vpObj = bean.getVpisvane();
				clId = "obektVlpForm:registerTabs:";
				clFr = "obektVlpForm:";
			} else if(BEANNAME_OEZ.equals(beanName)){ // OEZ
				RegOEZ bean = (RegOEZ) JSFUtils.getManagedBean(BEANNAME_OEZ);
				vpObj = bean.getVpisvane();
				clId = "oezForm:registerTabs:";
				clFr = "oezForm:";
			}
			
			if(vpObj != null) {
				actionNew();
				rowIdx = -2;	
				JPA.getUtil().runWithClose(() -> statusList = daoVp.findVpisvaneStatusList(vpObj.getId()));
				oldStatus = vpObj.getStatus();
				vpStatus.setIdVpisvane(vpObj.getId());

				 // проверка дали в настройките на регитъра е указано, че се издава УД
				RegisterOptions registerOptions = sd.getRoptions().get(this.vpObj.getIdRegister()); // всички настройки за регистъра
				RegisterOptionsDocsIn tmpInDoc = registerOptions.getDocsIn().get(0); // вземам 1-я вх. док. - за всички док.,  настройкта за УД, е еднаква
				nastrUd =  sd.isElementVisible(tmpInDoc.getVidDoc(), BabhConstants.EKRAN_ATTR_UDOST_DOC); 
				
				if(nastrUd) { // само, ако в настройките е указано, че има УД
					viewBtnUD = Objects.equals(vpObj.getStatus(),BabhConstants.STATUS_VP_VPISAN) && checkForUdostDoc();
					if(viewBtnUD) { // още не е генериран файл с УД
						delovoden = new DocDAO(ud).findById(vpObj.getIdResult());// да зареди деловодния УД, свързан с вписването
					}
				}
				
			}
		} catch (BaseException e) {
			LOGGER.error("Грешка при извличане статуси на вписване!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		}  
		
		this.decodeDate = new Date();
	
	}

	
	/**
	 * Избран е ред от списъка
	 * @param row
	 */
   public void actionLoadDataFromList(VpisvaneStatus row, int idx) {
	   vpStatus = new VpisvaneStatus();
	   vpStatus.setIdVpisvane(row.getIdVpisvane());
	   vpStatus.setId(row.getId());
	   vpStatus.setStatus(row.getStatus());
	   vpStatus.setDateStatus(row.getDateStatus());
	   vpStatus.setReason(row.getReason());
	   vpStatus.setReasonDop(row.getReasonDop());
	   vpStatus.setRegNomZapoved(row.getRegNomZapoved());
	   vpStatus.setDateZapoved(row.getDateZapoved());
	   vpStatus.setRegNomProtokol(row.getRegNomProtokol());
	   vpStatus.setDateProtokol(row.getDateProtokol());
	   vpStatus.setUserReg(row.getUserReg());
	   vpStatus.setDateReg(row.getDateReg());
	 //  vpStatus = row;
	   rowIdx = idx;
   }
   
   /**
    * Нов статус
    */
   public void actionNew() {
	   vpStatus = new VpisvaneStatus();
	   vpStatus.setIdVpisvane(vpObj.getId());
	   vpStatus.setDateStatus(new Date()); 
	   rowIdx = -1;
	   vidUdostDoc = null;
   }
   
   
   /**
    * Запис и актуализаиця на статус
    */
   public void actionSave() {	  
	
	   if(checkDataStatus()) {
		   try {
			  
				JPA.getUtil().runInTransaction(() -> { 				
					newRegNomer = this.daoVp.saveVpisvaneStatus(vpObj, vpStatus, (rowIdx < 0 || ((statusList.size() - 1) == rowIdx) ) , udostDelovDoc, udRn, getSystemData(), migrNomObject );
				});		
			  
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG) );
				if(newRegNomer !=null) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "За обекта от вписването е генериран е рег. номер: "+ newRegNomer );	
				}
				String udStr = null;
			
				if(udostDelovDoc != null ) {
					udStr = checkForUdostDoc2();
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "Записан е удостоверителен документ под номер: "+udStr+"!");
				} 
			
				statusList = daoVp.findVpisvaneStatusList(vpObj.getId()); // да рефрешна списъка
				if(nastrUd) { 
					viewBtnUD = Objects.equals(vpObj.getStatus(),BabhConstants.STATUS_VP_VPISAN) && (udostDelovDoc != null ||  checkForUdostDoc()) ;
					if(viewBtnUD) {
						delovoden = new DocDAO(ud).findById(vpObj.getIdResult());// да зареди УД, свързан с вписването
					}
				} else {
					viewBtnUD = false;
				}
				actionNew();
				oldStatus = vpObj.getStatus(); // последния статус на вписването
			} catch (ObjectInUseException e) {
				LOGGER.error("Грешка при запис на статус на вписване! ObjectInUseException "); 
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
			} catch (BaseException e) {			
				LOGGER.error("Грешка при запис на  статус на вписване! BaseException", e);				
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
			} catch (Exception e) {
				LOGGER.error("Грешка при запис на  статус на вписване! ", e);					
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
			}finally {
				JPA.getUtil().closeConnection();
			}
		}
   }
   
	/**
	 *  заключване/отключване на вписване за редакция
	 * @param l = BabhConstants.CODE_ZNACHENIE_DA/BabhConstants.CODE_ZNACHENIE_NE
	 */
	public void actionLockVp(int l) {
		try {
			vpObj.setVpLocked(l); 
			JPA.getUtil().runInTransaction(() -> { 				
				this.daoVp.save(vpObj);
			});
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG) );
		} catch (BaseException e) {
			LOGGER.error("Грешка при запис на вписване - заключване/отключване за редакция! ", e);					
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		}		
	}
	
	/**
	 * Проверка за валидни данни
	 * @return
	 */
	public boolean checkDataStatus() {
		boolean rez = true; 
		if(vpStatus.getStatus() == null) {
			rez = false;
			JSFUtils.addMessage(clId+"stat",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS,getMessageResourceString(LABELS, "docu.status")));
		}
		if(vpStatus.getDateStatus() == null || vpStatus.getDateStatus().after(new Date())) {
			// TODO какви проверки трябва да сложа 
			vpStatus.setDateStatus(new Date()); 
		}
		
		if(vpStatus.getDateZapoved() != null && vpStatus.getDateZapoved().after(new Date())) {
			rez = false;
			JSFUtils.addMessage(clId+"doc.dataZapoved",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS," валидна дата. Не се позволяват бъдещи дати"));
		}
		if(vpStatus.getDateProtokol() != null && vpStatus.getDateProtokol().after(new Date())) {
			rez = false;
			JSFUtils.addMessage(clId+"doc.dataProtokol",FacesMessage.SEVERITY_ERROR,getMessageResourceString(UI_beanMessages,MSGPLSINS," валидна дата. Не се позволяват бъдещи дати"));
		}
		//ако статуса е сменен на вписан - да проверя дали има УД		
		if(rez && nastrUd  && !Objects.equals(oldStatus, BabhConstants.STATUS_VP_VPISAN) && Objects.equals(vpStatus.getStatus(), BabhConstants.STATUS_VP_VPISAN))	 {
			if(vidUdostDoc == null || udostDelovDoc == null) {		
				if(listDocsOut == null || listDocsOut.isEmpty()) {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Няма зададени удостоверителни документи в регистъра!");
				} else{
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Изберете удостоверителен документ от списъка!");
				}
				rez = false;
			}
				
			if((freeNomUd || Objects.equals(vpObj.getFromМigr(), 0)) && SearchUtils.isEmpty(udRn)) {
				// ръчно въвеждане на данни
				JSFUtils.addMessage(clId+"udrn",FacesMessage.SEVERITY_ERROR, "Въведете номер на удостоверителен документ!");
				rez = false;
			}				
		}
		 	
		return rez;
	}

	/**
	 * проверка за промяна на статус на вписан
	 * Позволена е корекция на вече записан статус, само ако е последен и то, ако не е вписан!
	 */
	public void actionChangeStatus() {
		// САМО при първия запис!!, когато се генерира деловодния док.!
		// УД се търсят, само ако за този регистър се издават УД! Това се описва от настройките на екранни фпорми - трябва да е добавен атрибут "Удостоверителен документ".
		vidUdostDoc = null;   	
		if( nastrUd && 
			!Objects.equals(oldStatus, BabhConstants.STATUS_VP_VPISAN) && 
			Objects.equals(vpStatus.getStatus(), BabhConstants.STATUS_VP_VPISAN)) {
			udostDelovDoc = null; // вида УД, който трябва да се подаде на деловодтвото
			//извлича видовете изходящите документи 
			RegisterOptions registerOptions = sd.getRoptions().get(this.vpObj.getIdRegister()); // всички настройки за регистъра
			List<RegisterOptionsDocsOut> listDocsOutTmp = registerOptions.getDocsOut();
			listDocsOut = new ArrayList<>();
			
			if(listDocsOutTmp == null || listDocsOutTmp.isEmpty() || listDocsOutTmp.get(0) == null) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Няма зададени удостоверителни документи в настройките на регистъра!");
				PrimeFaces.current().executeScript("scrollToErrors()");
			} else {
				DocDAO docDao = new DocDAO(ud);
				// да останат само документите, за които в делов. регистри, в настройките, за характер на док. е  BabhConstants.CODE_ZNACHENIE_HAR_DOC_UDOST
				Object[] docSettings = null;
				for(RegisterOptionsDocsOut dOut: listDocsOutTmp) {
					try {
						docSettings = docDao.findDocSettings(vpObj.getRegistraturaId(),dOut.getVidDoc(),getSystemData());
						if(docSettings != null && Objects.equals( docSettings[8], BabhConstants.CODE_ZNACHENIE_HAR_DOC_UDOST) ) {
					    	listDocsOut.add(dOut);
					    }
					} catch (DbErrorException e) {
						LOGGER.error("Грешка при извличане на характеристики на документ.", e.getMessage());
					}
				    
				}
				if(listDocsOut.size() == 1) {
					udostDelovDoc =  listDocsOut.get(0);// ако е само един - да се зареди по подразбиране.
					vidUdostDoc = udostDelovDoc.getVidDoc(); // за да се вижда на екранa
					// ако обаче са повече -  да се покаже поле за избор на УД (удост. док.) - да направят задължително избор
					settingsUD(false);
				}
			} 
		}
	}
	
	/**
	 * Приключва ръчното въвеждане на данни от миграция
	 * сетва в полето vpisvane.fromMigr = 1
	 */
	public void actionEndMigr() {
		// само, ако е ръчно въвеждане на данни от миграция
		try {
			vpObj.setFromМigr(BabhConstants.CODE_ZNACHENIE_DA);
			JPA.getUtil().runInTransaction(() -> { 				
				this.daoVp.save(vpObj);
			});
		} catch (BaseException e) {
			LOGGER.error("Грешка при запис на вписване - Приключване на ръчно въвеждане на данни от миграция. ", e.getMessage());
		}	
	}
	
	
	/**
	 * настройките по вид документ
	 */
	public void settingsUD(boolean loadUD) {
		if(vidUdostDoc != null) {
			try {
				if(loadUD) {
					// ако е само един -> udostDelovDoc е вече зареден
					for(RegisterOptionsDocsOut d: listDocsOut) {
						if(Objects.equals(d.getVidDoc(), vidUdostDoc)) {
							udostDelovDoc = d;
							break;
						}
					}
				}
				// търся настройките по вид документ - ТРЯБВА да има
				Object[] settings = new DocDAO(getUserData()).findDocSettings(((UserData)getUserData()).getRegistratura(), vidUdostDoc, getSystemData());
			 	if(settings!=null && settings[1]!=null) {
			 		Integer alg = null;
			 		Integer registerId = (Integer) settings[1];
			 		 if(registerId != null) {
						 alg  = (Integer) getSystemData().getItemSpecific(BabhConstants.CODE_CLASSIF_REGISTRI, registerId, getUserData().getCurrentLang(), new Date(), BabhClassifAdapter.REGISTRI_INDEX_ALG);
					 }
					 if(alg != null && alg.equals(BabhConstants.CODE_ZNACHENIE_ALG_FREE)) {
						 freeNomUd = true;	
					 } else {
						 freeNomUd = false;		 
					 }
			 	}
	
			} catch (DbErrorException e) {
				LOGGER.error("Грешка при извличане на настройки на УД!", e.getMessage());
			}
		}
	}
	
	
	
	/**
	 * изтриване на статус
	 */
	public void actionDelete() {
		
		try {
			JPA.getUtil().runInTransaction(() -> { 				
				 this.daoVp.deleteVpisvaneStatus(vpObj,statusList, vpStatus.getId());
			});
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,  IndexUIbean.getMessageResourceString(UI_beanMessages, "general.successDeleteMsg") );
		
			actionNew(); // списъкът няма нужда да се тегли отново
		} catch (ObjectInUseException e) {
			LOGGER.error("Грешка при запис на статус на вписване! ObjectInUseException "); 
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		} catch (BaseException e) {			
			LOGGER.error("Грешка при запис на  статус на вписване! BaseException", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Грешка при запис на  статус на вписване! ", e);					
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		}
	}
	
	
	
	/**
	 * Проверка дали вече има генериран УД - като файл
	 * ако върне <> нулл има два варианта
	 * [4]-file_id == нулл значи няма файл и трябва да има бутон
	 * [4]-file_id <> нулл значи има файл и не трябва да има бутона
	 * 
	 */
	public boolean checkForUdostDoc() {
		boolean rez = true;
		try { 		 
			Object[] info = daoVp.findUdostDocIdentFile(vpObj.getId(),BabhConstants.CODE_ZNACHENIE_DOC_VID_ATTACH_UD);
			if(info != null && info[4] != null ) {
				rez = false; //намерен е файл
			} 
			
		} catch (BaseException e) {
			LOGGER.error("Грешка при проверка дали вече има генериран УД от шаблон!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
		return rez; 
	}
	
	/**
	 * Връща рег. номера на актуалния деловоден УД 
	 */
	public String checkForUdostDoc2() {
		String rez = null;
		try { 		 
			Object[] info = daoVp.findUdostDocIdentFile(vpObj.getId(),BabhConstants.CODE_ZNACHENIE_DOC_VID_ATTACH_UD);
			if(info != null && info[1] != null &&  info[2] != null) {				
				rez = (String)info[1] +"/"+new SimpleDateFormat("dd.MM.yyyy").format((Date)info[2]); // има документ - връщам номера / дата
			}
			
		} catch (BaseException e) {
			LOGGER.error("Грешка при проверка дали вече има генериран УД от шаблон!", e);				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
		return rez; 
	}
	
	
	
	/**
	 * Генериране на удостоверителен документ - като файл
	 */
	public void actionGenUdoc() {
		if(delovoden == null) {
			LOGGER.error("Грешка при генериране на удостоверителен документ от шаблон! Не е намерен деловоден УД!" );				
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,  getMessageResourceString(UI_beanMessages, ERRDATABASEMSG),"Грешка при генериране на удостоверителен документ - като файл! Не е намерен деловоден УД!");
		} else {
			this.bindCompUdostDoc.initComponent();
			LOGGER.debug(delovoden.getId().toString());
		}
	}
	
	
	/**
	 * След генериране на файла за УД
	 */
	public void actionAfterGenerateUD() {
		viewBtnUD = false; // да се скрие бутона - вече има генериран файл
		PrimeFaces.current().ajax().update(clId+"udPnl");
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


	public List<VpisvaneStatus> getStatusList() {
		return statusList;
	}


	public void setStatusList(List<VpisvaneStatus> statusList) {
		this.statusList = statusList;
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


	public VpisvaneStatus getVpStatus() {
		return vpStatus;
	}


	public void setVpStatus(VpisvaneStatus vpStatus) {
		this.vpStatus = vpStatus;
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

	public CompUdostDokument getBindCompUdostDoc() {
		return bindCompUdostDoc;
	}

	public void setBindCompUdostDoc(CompUdostDokument bindCompUdostDoc) {
		this.bindCompUdostDoc = bindCompUdostDoc;
	}

	public Doc getDelovoden() {
		return delovoden;
	}

	public List<RegisterOptionsDocsOut> getListDocsOut() {
		return listDocsOut;
	}

	public void setListDocsOut(List<RegisterOptionsDocsOut> listDocsOut) {
		this.listDocsOut = listDocsOut;
	}

	public RegisterOptionsDocsOut getUdostDelovDoc() {
		return udostDelovDoc;
	}

	public void setUdostDelovDoc(RegisterOptionsDocsOut udostDelovDoc) {
		this.udostDelovDoc = udostDelovDoc;
	}

	public boolean isViewBtnUD() {
		return viewBtnUD;
	}

	public void setViewBtnUD(boolean viewBtnUD) {
		this.viewBtnUD = viewBtnUD;
	}

	public Integer getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(Integer oldStatus) {
		this.oldStatus = oldStatus;
	}

	public boolean isNastrUd() {
		return nastrUd;
	}

	public void setNastrUd(boolean nastrUd) {
		this.nastrUd = nastrUd;
	}

 
	public Integer getVidUdostDoc() {
		return vidUdostDoc;
	}

	public void setVidUdostDoc(Integer vidUdostDoc) {
		this.vidUdostDoc = vidUdostDoc;
	}


	public String getUdRn() {
		return udRn;
	}


	public void setUdRn(String udRn) {
		this.udRn = udRn;
	}


	public boolean isFreeNomUd() {
		return freeNomUd;
	}


	public void setFreeNomUd(boolean freeNomUd) {
		this.freeNomUd = freeNomUd;
	}


	public String getNewRegNomer() {
		return newRegNomer;
	}


	public void setNewRegNomer(String newRegNomer) {
		this.newRegNomer = newRegNomer;
	}


	public String getMigrNomObject() {
		return migrNomObject;
	}


	public void setMigrNomObject(String migrNomObject) {
		this.migrNomObject = migrNomObject;
	}


	public String getClFr() {
		return clFr;
	}


	public void setClFr(String clFr) {
		this.clFr = clFr;
	}


	
}
