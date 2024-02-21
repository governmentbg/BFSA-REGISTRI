package com.ib.babhregs.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.components.RefCorrespData;
import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.LockObjectDAO;
import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.daeu.esb.epay.PayClient;
import com.ib.daeu.esb.epay.common.CommonTypeActor;
import com.ib.daeu.esb.epay.common.CommonTypeUID;
import com.ib.daeu.esb.epay.common.RegisterPaymentResponse;
import com.ib.daeu.esb.epay.common.CommonTypeActor.ActorType;
import com.ib.daeu.esb.epay.common.CommonTypeUID.EType;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

/**
 * 
 * АКТУАЛИЗАЦИЯ/ВЪВЕЖДАНЕ НА ПОДТЪПИЛИ ДОКУМЕНТИ (ЗАЯВЛЕНИЯ)
 * 
 * @author s.arnaudova
 */

@Named
@ViewScoped
public class BabhZaiavEditBean extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2099028078361201777L;
	private static final Logger LOGGER = LoggerFactory.getLogger(BabhZaiavEditBean.class);
	
	private static final String FORM = "zaiavEditForm";
	
	private String isView;

	private SystemData sd;
	private UserData ud;

	private Date decodeDate;
	private String idObj;

	/** Документ, в рамките на БАБХ */
	private Doc doc;
	private transient DocDAO dao;

	/** Списък файлове към документа */
	private List<Files> filesList;

	private boolean showPanel; // ако са подали измислено ИД да виждат само съобщение

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
	 * дали потребителя има достъп до заявлението
	 */
	private boolean access;
	
	private boolean notSave;

	private boolean chCodeAdmUsluga;


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

	
	
	
	@PostConstruct
	public void init() {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("BabhZaiavEditBean init!!!!!!");
		}
		notSave = false;
		this.sd = (SystemData) getSystemData();
		this.ud = getUserData(UserData.class);
		this.decodeDate = new Date();
		this.doc = new Doc();
		this.dao = new DocDAO(getUserData());
		this.showPanel = true;
		this.filesList = new ArrayList<>();
		
		try {
			
			FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
			this.isView = (String) faceletContext.getAttribute("isView"); // 0 - редакция, 1 - разглеждане
			String paramZ = JSFUtils.getRequestParameter("idObj");
			if (paramZ != null && !"".equals(paramZ)) {
				this.idObj = paramZ;		

				if (this.idObj != null) {

					boolean fLockOk = true;
					//ако е само за разглеждане не заключваме заявлението
					if(isView.equals(String.valueOf(0))) {
						fLockOk = checkAndLockDoc(Integer.valueOf(idObj), BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
					}

					if (fLockOk) {
						
						loadDoc(idObj);
						if (this.doc != null) {
							JPA.getUtil().runWithClose(() -> access = new DocDAO(ud).hasDocAccess(this.doc, isView.equals(String.valueOf(0)), sd));
							
							if (!access) {
								unlockDoc(); // отключвам го, за да е достъпен за останалите..
								this.showPanel = false;							
								JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Отказан достъп!");
							}
						}
					}

				} else {
					
					this.showPanel = false;
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Няма намерено заявление с ID: " + idObj);
				}
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на въвеждане/актуализация на документ! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при инициализиране на въвеждане/актуализация на документ! ");
		}

	}
	

	public void actionSave() {
		try {

			if (checkData()) { 

				JPA.getUtil().runInTransaction(() -> this.doc = this.dao.saveZaiavlenie(doc, referent1, referent2));
				notSave = false;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
						getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG));
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при запис/актуализация на заявление!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
		}
	}
	
	private boolean checkData() {
		boolean save = true;

		if (this.doc.getRegisterId() == null) {
			JSFUtils.addMessage(FORM+":licеPоdatel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "podatel.msgName"));
			save = false;
		}

		if (this.doc.getDocVid() == null) {
			JSFUtils.addMessage(FORM + ":vidDoc", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "nastrScreenForm.vidDoc")));
			save = false;
		}
		referent1 = getBindingReferent1().getRef();
  	    if(referent1 != null && 
  	    		(!SearchUtils.isEmpty(referent1.getFzlEgn()) || !SearchUtils.isEmpty(referent1.getFzlLnc())) && 
  	    		  SearchUtils.isEmpty(referent1.getRefName())) {
  	    	JSFUtils.addMessage(FORM+":licеPоdatel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "podatel.msgName"));
			save = false;
  	    } 
  		referent2 = getBindingReferent2().getRef();
  	    if(referent2 != null && 
  	    		(!SearchUtils.isEmpty(referent2.getNflEik()) || !SearchUtils.isEmpty(referent2.getFzlLnEs()) || !SearchUtils.isEmpty(referent2.getFzlEgn()) || !SearchUtils.isEmpty(referent2.getFzlLnc())) && 
	    		  SearchUtils.isEmpty(referent2.getRefName())) {
	    	if(referent2.getRefType().equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL)) {
				JSFUtils.addMessage(FORM+":liceZaqvitel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "zaiavitel.msgName"));
			}else {
				JSFUtils.addMessage(FORM+":liceZaqvitel:nameCorr",FacesMessage.SEVERITY_ERROR,getMessageResourceString(beanMessages, "zaiavitel.msgNameNFZL"));
			}
			save = false;
	    }
		return save;
	}

	/**
	 * извлича настройките за таксите
	 */
	private void loadRegisterOptions() {
		// настройките за таксите би трябвало да са вече заредени - още при прехвърляне на заявл. от деловодството 
		//ако заявлението е старо или вече има опредeлна такса - не променяме нищо по таксите!!!!!
		Integer registerId = sd.getRegisterByVidDoc().get(this.doc.getDocVid());
		this.registerOptions = sd.getRoptions().get(registerId); // всички настройки за регистъра
		if (this.registerOptions != null) {
			List<RegisterOptionsDocsIn> lstDocsIn = registerOptions.getDocsIn(); // само входните док. за регистъра
			docOpt = new RegisterOptionsDocsIn();
			for (RegisterOptionsDocsIn item : lstDocsIn) {
				if ((item.getVidDoc()).equals(this.doc.getDocVid())) {
					docOpt = item;
					break;
				}
			}	
			
			// в този случай трябва да вземем настройките на подзаявлението, за да добавим правилната сума за плащане ???		
			// това не може да стане без да е създадено вписване - тогава се определя подзаявлението
			// дали изобщо е нужно да търсим такси за подзаявление?
			if (this.doc.getDocPodVid() != null) {
				for (RegisterOptionsDocsIn item : lstDocsIn) {
					if ((item.getVidDoc()).equals(this.doc.getDocPodVid())) {
						docOpt = item;
						break;
					}
				} 
			}

			//ако заявлението е старо не променяме таксите му..
			if (this.doc.getPayType() == null ) {  		
				this.doc.setPayType(this.docOpt.getPayCharacteristic());
				notSave = true;
			}
			if(this.doc.getPayAmount() == null || this.doc.getPayAmount().equals(Float.valueOf(0))) { 
				this.doc.setPayAmount(this.docOpt.getPayAmount());
				notSave = true;
			}
			
			
		} else {
			//ако няма настройки за регистъра показваме само информативно съобщение..
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Няма открити настройки за този регистър! ");
		}
		
	}
	
	public void actionChAdmUsluga() {
		RegisterOptionsDocsIn regOpt2= sd.getRegisterByNomUsuga().get(this.doc.getCodeAdmUsluga());
		// ако сменят номера на услугата - трябва да се смени и вида на документа,регистъра, да се нулират таксите!
		// после да се извика loadRegisterOptions();
		if(regOpt2 != null) {			
			doc.setDocVid(regOpt2.getVidDoc());
			doc.setRegisterId(regOpt2.getIdRegister());
			doc.setPayType(null);
			doc.setPayAmount(null);
			doc.setPayAmountReal(null);
			doc.setPayDate(null);
			loadRegisterOptions();
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, "След промяна на кода на услугата са променени вида на заявлението и настройките за таксите! За да запомните промените, натиснете запис!");
		} else {
			//ако няма настройки за регистъра показваме само информативно съобщение..
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Няма открити настройки за регистър с този номер на услуга! ");
		}
	}

	/****
	 * ЗАРЕЖДА ОБЕКТ ОТ ТИП BabhDoc ЗА АКТУАЛИЗАЦИЯ
	 ****/
	private void loadDoc(String idDoc) {
		try {

			JPA.getUtil().runWithClose(() -> this.doc = this.dao.findById(Integer.valueOf(idDoc)));

			if (this.doc != null) {
				String paramV = JSFUtils.getRequestParameter("idV");
				if (paramV != null && !"".equals(paramV)) {
					doc.setVpisvaneId(Integer.valueOf(paramV));
				}
				loadReferents(); // зарежда заявителя и представляващото лице
				loadRegisterOptions(); // настройките на регистъра
				loadFilesList(this.doc.getId()); // файловете
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на обект за актуализация", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на обект за актуализация!");
		}
	}

	/**
	 * зарежда заявителя и представляващото лице
	 */
	private void loadReferents() {
		try {
			referent1 = new Referent(); 
			if(doc.getCodeRefCorresp() != null) {				
				JPA.getUtil().runWithClose(() -> referent1 = new ReferentDAO(ud).findByCodeRef(doc.getCodeRefCorresp()));				
				referent1.setKachestvoLice(doc.getKachestvoLice());  
			} 
		
			referent2 = new Referent();			
			if(doc.getCodeRefZaiavitel() != null) {
				JPA.getUtil().runWithClose(() -> referent2 = new ReferentDAO(ud).findByCodeRef(doc.getCodeRefZaiavitel()));
			}
		} catch (BaseException e) {
			LOGGER.error("Грешка при зареждане на референти в заявлението!", e);
		}
	}
	
	
	
	/**
	 * ИЗВЛИЧА СПИСЪКА НА ФАЙЛОВЕТЕ КЪМ ДОКУМЕНТ
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
				 this.showPanel = false;
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
        	String vidZ = sd.decodeItem(BabhConstants.CODE_CLASSIF_VID_REGISTRI, doc.getRegisterId(), getCurrentLang(), new Date());
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
        		  JPA.getUtil().runInTransaction(() -> dao.updatePaymentId(doc.getId(),registerPaymentResponse.getPaymentId()));
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

	public String subjectEmail() {
		return "Система БАБХ ИИСР, във връзка със заявление: " + doc.getRnDoc()+"/"+DateUtils.printDate(doc.getDocDate());
	}

	public boolean viewBtnPayRequest() {
		boolean bb = false;
		if(doc.getPayAmount() != null && doc.getPayAmountReal() != null && 
			!doc.getPayAmount().equals(Float.valueOf(0)) &&
		   doc.getPayAmount().compareTo( doc.getPayAmountReal())>0 ) {
			bb = true; 		
		}
		return bb;
	}
	
	public Date getDecodeDate() {
		return decodeDate;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate;
	}

	public String getIdObj() {
		return idObj;
	}

	public void setIdObj(String idObj) {
		this.idObj = idObj;
	}

	public Doc getDoc() {
		return doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	public DocDAO getDao() {
		return dao;
	}

	public void setDao(DocDAO dao) {
		this.dao = dao;
	}

	public boolean isShowPanel() {
		return showPanel;
	}

	public void setShowPanel(boolean showPanel) {
		this.showPanel = showPanel;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public String getTxtCorresp() {
		return txtCorresp;
	}

	public void setTxtCorresp(String txtCorresp) {
		this.txtCorresp = txtCorresp;
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


	public String getIsView() {
		return isView;
	}


	public void setIsView(String isView) {
		this.isView = isView;
	}


	public boolean isAccess() {
		return access;
	}


	public void setAccess(boolean access) {
		this.access = access;
	}


	public boolean isNotSave() {
		return notSave;
	}


	public void setNotSave(boolean notSave) {
		this.notSave = notSave;
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


	public Referent getReferent2() {
		return referent2;
	}


	public void setReferent2(Referent referent2) {
		this.referent2 = referent2;
	}


	public RefCorrespData getBindingReferent2() {
		return bindingReferent2;
	}


	public void setBindingReferent2(RefCorrespData bindingReferent2) {
		this.bindingReferent2 = bindingReferent2;
	}


	public boolean isChCodeAdmUsluga() {
		return chCodeAdmUsluga;
	}


	public void setChCodeAdmUsluga(boolean chCodeAdmUsluga) {
		this.chCodeAdmUsluga = chCodeAdmUsluga;
	}

	


}
