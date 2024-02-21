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
import com.ib.babhregs.db.dto.EventDeinostFuraji;
import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.db.dto.MpsLice;
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
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;
import com.ib.system.utils.ValidationUtils;

import bg.egov.eforms.utils.EFormUtils;
import bg.egov.eforms.utils.EgovContainer;


@Named
@ViewScoped
public class RegMpsFuraj extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8660116876094517568L;
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
	 * когато няма подадено никакво ид на обект скривам панела
	 */
	private boolean showEkran = true; 
	
	private Mps mpsData;
	
	/** Списък на файловете към заявлението за вписване */
	private List<Files> filesList;
	
	/** Информацията за последното заявление към вписването (ако има такова) */
	private Object[] lastDoc;
	
	/**
	 * Дължима сума
	 */
 	private BigDecimal daljimaSuma;
 	
	/**
	 * дали потребителя има достъп до вписването
	 */
	private boolean access;
	
	private String refDocNom1;   	//Лиценз за международен превоз на товари - номер
	private Date refDocDate1;   	//Лиценз за международен превоз на товари - дата
	private String refDocNom2;   	//Лиценз за вътрешен превоз на товари - номер
	private Date refDocDate2;   	//Лиценз за вътрешен превоз на товари - дата

	@PostConstruct
	public void init() {
		LOGGER.info("RegMpsFuraj init!!!!!!");
		
		this.sd = (SystemData) getSystemData();
		this.ud = getUserData(UserData.class);
		this.decodeDate = new Date();
		this.babhDoc = new Doc();
		this.vpisvane = new Vpisvane();
		this.vpisvane.setMps(new Mps());
		this.vpisvane.setEventDeinostFuraji(new EventDeinostFuraji());
		this.vpisvane.getEventDeinostFuraji().setVidList(new ArrayList<>());
		this.vpisvane.getMps().setMpsLice(new ArrayList<>());
		this.vpisvaneDAO = new VpisvaneDAO(getUserData());
		this.docDAO = new DocDAO(getUserData());
		this.filesList = new ArrayList<>();

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
								loadRegOptions();
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



	private void loadRegOptions() {

		// вече имаме някакъв док и от него вадим вида и настройките..
		this.registerId = sd.getRegisterByVidDoc().get(this.babhDoc.getDocVid());
		registerOptions = sd.getRoptions().get(registerId); // всички настройки за регистъра
		if (this.registerOptions != null) {
			List<RegisterOptionsDocsIn> lstDocsIn = registerOptions.getDocsIn(); // само входните док. за регистъра
			docOpt = new RegisterOptionsDocsIn();
			for (RegisterOptionsDocsIn item : lstDocsIn) {
				if ((item.getVidDoc()).equals(this.babhDoc.getDocVid())) {
					docOpt = item;
					break;
				}
			}
			
			loadDeinFuraji(); // зареждаме дейностите към съответния регистър

			// за ново вписване да сетне сумата за плащане по подразбиране от настройките) и типа плащане, ако вече не са записани!!!
			if(vpisvane == null || vpisvane.getId() == null) {
				//ако в заявлението има настройки за плащането - не ги пипаме!
				if (babhDoc.getPayType() == null ) {  		
					babhDoc.setPayType(this.docOpt.getPayCharacteristic());
				}
				if(babhDoc.getPayAmount() == null || babhDoc.getPayAmount().equals(Float.valueOf(0))) { 
					babhDoc.setPayAmount(this.docOpt.getPayAmount());
				}
				// това поле ще отпадне - затова го коментирам
	//			if(Objects.equals(docOpt.getPayCharacteristic(), BabhConstants.CODE_ZNACHENIE_PAY_TYPE_NOPAY)){
	//				 zaiavlVp.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
	//			}else {
	//				 zaiavlVp.setIndPlateno(BabhConstants.CODE_ZNACHENIE_NE);
	//			}
			}
			
		} else {
			//ако няма настройки за регистъра показваме само информативно съобщение..
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Няма открити настройки за този регистър! ");
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
			if (referent2.getReferentDocs() != null && !referent2.getReferentDocs().isEmpty()) {
				for (ReferentDoc doc: referent2.getReferentDocs() ) {
					if (Objects.equals(doc.getVidDoc(), BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_MPREVOZ)) {
						refDocNom1 = doc.getNomDoc();
						refDocDate1 = doc.getDateIssued();
					} else if (Objects.equals(doc.getVidDoc(), BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_VPREVOZ)) {
						refDocNom2 = doc.getNomDoc();
						refDocDate2 = doc.getDateIssued();
					}
				}
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
			vpisvane.setLicenziantType(BabhConstants.CODE_ZNACHENIE_TIP_LICENZ_MPS); 
			vpisvane.setCodePage(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS);
			vpisvane.setStatus(BabhConstants.STATUS_VP_WAITING);
			
			if (this.vpisvane.getEventDeinostFuraji() == null) {				
				this.vpisvane.setEventDeinostFuraji(new EventDeinostFuraji());
				this.vpisvane.getEventDeinostFuraji().setVidList(new ArrayList<>());
				this.vpisvane.setMps(new Mps());
				this.vpisvane.getMps().setMpsLice(new ArrayList<>());
			}
			
			loadRegOptions();
			
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
	 * ЗАРЕЖДА ВИДОВЕТЕ ДЕЙНОСТИ ЗА СЪОТВЕТНОТО ЗАЯВЛЕНИЕ.. 
	 * в случай, че дейността е само една директно я избираме и я бутаме в списъка.. ако са повече от една трябва да изберат от екрана за кои дейности се отнася..
	 */
	private void loadDeinFuraji() {
		
		if (this.vpisvane.getEventDeinostFuraji() == null) {
			this.vpisvane.setEventDeinostFuraji(new EventDeinostFuraji());
		}
		
		if (docOpt.getDocsInActivity() != null && !docOpt.getDocsInActivity().isEmpty()) {	
			for (RegisterOptionsDocinActivity item : docOpt.getDocsInActivity()) {	

				if (this.vpisvane.getEventDeinostFuraji().getVidList() == null) {
					this.vpisvane.getEventDeinostFuraji().setVidList(new ArrayList<>());
				}				
				this.vpisvane.getEventDeinostFuraji().getVidList().add(item.getActivityId());					
			}
		}
	}

	
	/**
	 * зарежда BabhDoc
	 */
	private void loadBabhDoc(Integer babhDocId) {
		try {

			JPA.getUtil().runInTransaction(() -> this.babhDoc = this.docDAO.findById(babhDocId));
			
			if (this.babhDoc != null) {
				
				loadFilesList(this.babhDoc.getId());
				
				if (this.babhDoc.getCodeRefCorresp() != null) {
					JPA.getUtil().runWithClose(() -> referent1 = new ReferentDAO(ud).findByCodeRef(this.babhDoc.getCodeRefCorresp()));
					referent1.setKachestvoLice(this.babhDoc.getKachestvoLice());			
				}
							
				this.referent2 = new Referent();
				
				if (this.vpisvane != null && vpisvane.getCodeRefVpisvane() != null) {
					JPA.getUtil().runWithClose(() -> referent2 = new ReferentDAO(ud).findByCodeRef(vpisvane.getCodeRefVpisvane()));					
				} 
								
				if (this.referent2.getReferentDocs() != null && !this.referent2.getReferentDocs().isEmpty()) {
					for (ReferentDoc doc: referent2.getReferentDocs() ) {
						if (Objects.equals(doc.getVidDoc(), BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_MPREVOZ)) {
							this.refDocNom1 = doc.getNomDoc();
							this.refDocDate1 = doc.getDateIssued();
						} else if (Objects.equals(doc.getVidDoc(), BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_VPREVOZ)) {
							this.refDocNom2 = doc.getNomDoc();
							this.refDocDate2 = doc.getDateIssued();
						}
					}
				}
			} 


		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на вписване", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на вписване ");
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
				this.vpisvane.setMps(new Mps());
				this.vpisvane.setEventDeinostFuraji(new EventDeinostFuraji());
				this.vpisvane.getEventDeinostFuraji().setVidList(new ArrayList<>());
				this.vpisvane.getMps().setMpsLice(new ArrayList<>());
			}
			

		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на вписване", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на вписване ");
		}
	}
	
	
	public void actionSave() {
		boolean newVp = this.vpisvane.getId() == null;
		try {

			if (checkDataVp()) {
				
				saveLicense();

				this.babhDoc.setKachestvoLice(referent1.getKachestvoLice());
				this.babhDoc.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN);	
				
				//26.09.23г. КК каза, че тук задължително трябва да се записва заявителя въпреки, че не се въвежда в екрана
				MpsLice mpsLice = new MpsLice();
				mpsLice.setCodeRef(this.referent2.getCode());
				mpsLice.setTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_SOBSTVENIK);

				if (this.vpisvane.getMps().getMpsLice().isEmpty()) {
					this.vpisvane.getMps().getMpsLice().add(mpsLice);
				} else {
					this.vpisvane.getMps().getMpsLice().set(0, mpsLice);
				}
				
				JPA.getUtil().runInTransaction(() -> this.vpisvane = this.vpisvaneDAO.save(vpisvane, babhDoc, referent1, referent2, null, sd));
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG));	


				// заключваме вписването
				if (newVp) {
					lockDoc(this.vpisvane.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);	
					checkDaljimaSuma();
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
			JSFUtils.addMessage(IDFORM + ":registerTabs:imeZaiavitel", FacesMessage.SEVERITY_ERROR, "Моля, въведете име на представляващото лице!");
		}
		
		//Проверката не важи за тези от миграция
		// 0 - миграция, ръчно въвеждане,1 - миграция - завършена, 2 - не е миграция;
		if(vpisvane.getFromМigr() == null || Objects.equals(vpisvane.getFromМigr(),BabhConstants.CODE_ZNACHENIE_MIGR_NO)) {
			save = checkIdentifFZL(save, referent1.getFzlEgn(), referent1.getFzlLnc(), IDFORM+":registerTabs:licеPоdatel:egn", getMessageResourceString(beanMessages, "podatel.msgValidIdentFZL"));
		}
		
		if (referent1.getKachestvoLice() == null) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":registerTabs:kachestvoZaqvitel", FacesMessage.SEVERITY_ERROR, "Моля, посочете качество на представляващото лице!");
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
		
		if (!SearchUtils.isEmpty(referent2.getContactEmail()) && !ValidationUtils.isEmailValid(referent2.getContactEmail())) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":registerTabs:liceZaqvitel:contactEmail", FacesMessage.SEVERITY_ERROR, "Невалиден формат на имейл адрес!");
		}

		
		//TODO в тази класификация има и значения, които са само за животни?? плавателен съд за животни може ли да се лицензира за фураж???
		if (this.vpisvane.getMps().getVid() == null) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":registerTabs:odobTSvid", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "regTargovtsiFuraj.vidTS")));				
		}
		
		if (this.vpisvane.getMps().getNomer() == null || SearchUtils.isEmpty(this.vpisvane.getMps().getNomer())) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":registerTabs:odobTSregN", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "regTarvotsiFuraj.regN")));				
		}
		
		if (this.vpisvane.getMps().getModel() == null || SearchUtils.isEmpty(this.vpisvane.getMps().getModel())) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":registerTabs:model", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "compMPSsearch.model")));				
		}

		if (this.vpisvane.getMps().getMaxMasa() == null || SearchUtils.isEmpty(this.vpisvane.getMps().getMaxMasa())) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":registerTabs:odobTSmaxMasa", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "regTargovtsiFuraj.maxMasa")));				
		}
		
		if ((this.vpisvane.getMps().getTovaropodemnost() == null || SearchUtils.isEmpty(this.vpisvane.getMps().getTovaropodemnost())) & (this.vpisvane.getMps().getObem() == null || SearchUtils.isEmpty(this.vpisvane.getMps().getObem()))) {
			save = false;
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "regTargovtsiFuraj.plsInsObemTovar"));			
		}
		
		if (this.vpisvane.getMps().getCategoryList() == null || this.vpisvane.getMps().getCategoryList().isEmpty()) {
			save = false;
			JSFUtils.addMessage(IDFORM + ":registerTabs:odobTSkategoria", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "regTarvotsiFuraj.kategoriaSJP")));				
		}

		return save;
	}
	
	private void saveLicense() {
		
		if (referent2.getReferentDocs() == null) {
			referent2.setReferentDocs(new ArrayList<>());
		}
		
		boolean add1 = true;
		boolean add2 = true;
		boolean refdoc1 = (SearchUtils.isEmpty(refDocNom1) || refDocDate1 == null);
		boolean refdoc2 = (SearchUtils.isEmpty(refDocNom2) || refDocDate2 == null);
		ReferentDoc tmpRefDoc = null; 
		
		if (!referent2.getReferentDocs().isEmpty()) {
			for (ReferentDoc doc: referent2.getReferentDocs() ) {
				if (Objects.equals(doc.getVidDoc(), BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_MPREVOZ)) {
					if (refdoc1) {
						tmpRefDoc = doc;
					} else {
						doc.setNomDoc(refDocNom1);
					   	doc.setDateIssued(refDocDate1);
					}
				   	add1 = false;
				} else if (Objects.equals(doc.getVidDoc(),BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_VPREVOZ)) {
					if (refdoc2) {
						tmpRefDoc = doc;
					} else {
						doc.setNomDoc(refDocNom2);
						doc.setDateIssued(refDocDate2);
					}
					add2 = false;
				}
			} 
			if (tmpRefDoc != null) {
				referent2.getReferentDocs().remove(tmpRefDoc);
			}
		}
		
		if (add1 && !refdoc1) {
			tmpRefDoc = new ReferentDoc(); 
			tmpRefDoc.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_MPREVOZ);
			tmpRefDoc.setNomDoc(refDocNom1);
			tmpRefDoc.setDateIssued(refDocDate1);
			this.referent2.getReferentDocs().add(tmpRefDoc);
		}
		if (add2 && !refdoc2) {
			tmpRefDoc = new ReferentDoc(); 
			tmpRefDoc.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_VPREVOZ);
			tmpRefDoc.setNomDoc(refDocNom2);
			tmpRefDoc.setDateIssued(refDocDate2);
			this.referent2.getReferentDocs().add(tmpRefDoc);
		}
	}
	
	/*** КОПИРА АДРЕСА ЗА КОРЕНСПОНДЕНЦИЯ НА ЗАЯВИТЕЛЯ ***/
	public void actionCopyAdresZaiav() {
		
		referent2 = ((RefCorrespData) FacesContext.getCurrentInstance().getViewRoot().findComponent(IDFORM).findComponent("registerTabs:liceZaqvitel")).getRef();
		if (referent2 == null || referent2.getRefType() == null || referent2.getAddressKoresp() == null) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Няма посочен адрес на заявител!");
			
			String scroll = "document.body.scrollTop = 0; document.documentElement.scrollTop = 0;";
			PrimeFaces.current().executeScript(scroll);
			
			return;
		}
		
		this.vpisvane.getMps().setNasMesto(this.referent2.getAddressKoresp().getEkatte());
		this.vpisvane.getMps().setAdres(this.referent2.getAddressKoresp().getAddrText());

	}
	
	
	/*** ПРИ ЗАЧИСТВАНЕ НА КОМПОНЕНТАТА ЗА НАСЕЛЕНО МЯСТО ДА ЗАЧИСТИ ВСИЧКО ДАННИ ЗА АДРЕСА ***/
	public void actionClearAdres() {
		this.vpisvane.getMps().setNasMesto(null);
		this.vpisvane.getMps().setAdres(null);
	}

	
	public void actionCheckRegN() {
		try {
			
			if (!SearchUtils.isEmpty(this.vpisvane.getMps().getNomer())) {
	
					Object[] tmpData = null;
					MpsDAO mpsDAO = new MpsDAO(getUserData());
						
					tmpData = mpsDAO.findMpsInfoByIdOrNomer(null, this.vpisvane.getMps().getNomer());
					
					//въвели са някакъв номер, който вече го има в базата
					if (tmpData != null && tmpData.length > 0) {
						
						Integer status = null;
						if(tmpData[6] != null) {
							status = Integer.valueOf(tmpData[6].toString());
						}
						
						//ако МПС-то вече е вписвано и има активна лицензия за превоз на СЖП няма да позволяваме повторното му лицензиране
						if (Objects.equals(status, BabhConstants.STATUS_VP_VPISAN)) {
							
							String msg = "Tранспортно средство с рег. номер " + vpisvane.getMps().getNomer() + " вече притежава валидна регистарция за превоз на СЖП/ПП.";
							JSFUtils.addMessage(IDFORM + ":registerTabs:odobTSregN", FacesMessage.SEVERITY_ERROR, msg);				
							
						} else {	
							
							String mpsId = String.valueOf(tmpData[0]);
							Mps tmpMps = mpsDAO.findById(Integer.valueOf(mpsId));
							this.vpisvane.setMps(tmpMps);
					}
				}
				
			} else {

				this.vpisvane.setMps(new Mps());
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при проверка на транспортно средство по рег. номер!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при проверка на транспортно средство по рег. номер!");
			
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
	
	
	public void actionShowMpsData() {
		try {
			
			if (this.vpisvane.getMps() != null && this.vpisvane.getMps().getId() != null) {
				
				MpsDAO mpsDAO = new MpsDAO(getUserData());				
				JPA.getUtil().runWithClose(() -> this.mpsData = mpsDAO.findById(this.vpisvane.getMps().getId()));
			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на данни за ТС!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на данни за ТС!");
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
				flash.put("beanName", "regMpsFuraj"); // задъжлително се подава името на бийна, ако отиваме към таба със статусите
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
			
			JPA.getUtil().runInTransaction(() ->  {
				this.vpisvaneDAO.deleteById(vpisvane.getId());
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

	public Mps getMpsData() {
		return mpsData;
	}

	public void setMpsData(Mps mpsData) {
		this.mpsData = mpsData;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
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

	public int getMigration() {
		return migration;
	}

	public void setMigration(int migration) {
		this.migration = migration;
	}

}

