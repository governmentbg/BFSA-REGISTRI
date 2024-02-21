package com.ib.babhregs.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.RegisterOptionsDAO;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDisplay;
import com.ib.babhregs.db.dto.RegisterOptionsDocinActivity;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.db.dto.RegisterOptionsDocsOut;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;

/**
 * ВЪВЕЖДАНЕ/АКТУАЛИЗАЦИЯ НАСТРОЙКИ НА ИНФОРМАЦИОННИ ОБЕКТИ ОПИСАНИЕТО НА МОДУЛА
 * СЕ НАМИРА В src/docs -> Модул за въвеждане на настройки на ИИСР.docx
 * 
 * @author s.arnaudova
 */

@Named
@ViewScoped
public class NastrScreenFormBean extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2908471504138682771L;
	private static final Logger LOGGER = LoggerFactory.getLogger(NastrScreenFormBean.class);
	private static final String FORM = "nastScreenForm";
	private static final String ID_OBJ = "idObj";

	private Date decodeDate;
	private SystemData sd;

	private String idObj;

	private transient RegisterOptionsDAO dao;

	private RegisterOptions registerOption;
	private RegisterOptionsDocsIn docsIn; // входни документи
	private RegisterOptionsDocsOut docsOut; // изходни документи
	private RegisterOptionsDisplay display; // структура на екрана

	private List<Integer> docInActivities; // списък с дейности към входен документ
	private List<SystemClassif> deinList; // заради autocomplete

	private String selectedUslugaText; // текста на избрания вид услуга от списъка

	private boolean flagEditDocIn = false;
	private boolean flagEditDocOut  = false;
	private boolean flagEditDisplay = false;

	private boolean showEkranPanel = true; // когато са подали измислено ИД в линка се сетва на false и скрива панела

	private List<Integer> readOnlyCodesVidReg;
	
	private Integer srokDocOut = BabhConstants.CODE_ZNACHENIE_DA; // дали изходния документ е срочен или безсрочен
	private Integer periodType; // типа на периода /дни, месеци години/
	
	
	public void init() {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("NastrScreenForm init!!!!!!");
		}

		this.decodeDate = new Date();
		this.sd = (SystemData) getSystemData();
		this.registerOption = new RegisterOptions();
		this.docsIn = new RegisterOptionsDocsIn();
		this.docsOut = new RegisterOptionsDocsOut();
		this.display = new RegisterOptionsDisplay();
		this.dao = new RegisterOptionsDAO(getUserData());
		this.docInActivities = new ArrayList<>();
		this.deinList = new ArrayList<>();
		readOnlyCodesVidReg = new ArrayList<>();
		readOnlyCodesVidReg.add(1); //  само най-горните нива да не могат да се избират
		readOnlyCodesVidReg.add(2);
		readOnlyCodesVidReg.add(3);
		
		try {

			if (JSFUtils.getRequestParameter(ID_OBJ) != null && !"".equals(JSFUtils.getRequestParameter(ID_OBJ))) {
				this.idObj = (JSFUtils.getRequestParameter(ID_OBJ));

				if (this.idObj != null) {
					loadRegisterOptionObj(Integer.valueOf(idObj));

					// подали са измислено ИД
					if (this.registerOption == null) {

						this.showEkranPanel = false;
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Няма намерена настройка с ID: " + idObj);
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на настройка на екранен обект", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при инициализиране на настройка на екранен обект ");
		}
	}

	/****
	 * ЗАРЕЖДА ОБЕКТ ОТ ТИП REGISTEROPTION И ДОПЪЛНЕНИЯТА МУ ЗА АКТУАЛИЗАЦИЯ
	 ****/
	private void loadRegisterOptionObj(Integer idRegOpt) {
		try {

			JPA.getUtil().runInTransaction(() -> this.registerOption = this.dao.findById(Integer.valueOf(idRegOpt)));

		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на обект за актуализация", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
		}
	}

	/****
	 * ПРИ ПРОМЯНА НА ИЗБРАНИЯ РЕГИСТЪР ТЪРСИ ДАЛИ ИМА ВЕЧЕ ВЪВЕДЕН ТАКЪВ.. АКО
	 * НАМЕРИ ГО ЗАРЕЖДА ЗА РЕДАКЦИЯ
	 ****/
	public void actionChangeRegisterId() {
		try {

			if (this.registerOption.getRegisterId() != null) {

				Integer registerId = this.registerOption.getRegisterId();

				// зачиствам обекта и всички свързани обекти, за да не се разбъркват и показват глупости
				this.registerOption = new RegisterOptions();
				this.registerOption.setRegisterId(registerId);
				clearScreen();

				JPA.getUtil().runWithClose(() -> {

					RegisterOptions tmpRegister = this.dao.findByIdRegister(registerId);

					// има въведена настройка за този регистър
					if (tmpRegister != null) {
						loadRegisterOptionObj(tmpRegister.getId());
						JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
								"Намерена е настройка за избрания регистър!");

						String dialog = "document.body.scrollTop = 0; document.documentElement.scrollTop = 0;";
						PrimeFaces.current().executeScript(dialog);
					}
				});

			}

		} catch (Exception e) {
			LOGGER.error("Грешка при търсене на регистър", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
		}
	}

	public void actionClearRegister() {
		this.registerOption = new RegisterOptions();
		clearScreen();
	}

	/****
	 * ИЗТРИВАНЕ НА НАСТРОЙКА
	 ****/
	public void actionDelete() {
		try {

			JPA.getUtil().runInTransaction(() -> this.dao.delete(this.registerOption));

			this.sd.loadROptions();

			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
					IndexUIbean.getMessageResourceString(UI_beanMessages, "general.successDeleteMsg"));

			this.registerOption = new RegisterOptions();
			clearScreen();

		} catch (Exception e) {
			LOGGER.error("Грешка при изтриване на настройка на екранен обект", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при изтриване на настройка на екранен обект");
		}
	}

	/**** ЗАЧИСТВА ВСИЧКИ СВЪРЗАНИ ПО ЕКРАНА ОБЕКТИ ****/
	private void clearScreen() {

		this.docsIn = new RegisterOptionsDocsIn();
		this.docsOut = new RegisterOptionsDocsOut();
		this.display = new RegisterOptionsDisplay();

	}

	/**** ЗАПИС НА ЦЕЛИЯ ОБЕКТ ****/
	public void actionSave() {
		try {

			if (checkData()) {

				JPA.getUtil().runInTransaction(() -> this.registerOption = this.dao.save(this.registerOption));

				this.sd.loadROptions();

				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
						getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG));
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при запис/актуализация на настройка на екранен обект", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e));
		}
	}

	private boolean checkData() {
		boolean save = true;

		if (this.registerOption.getRegisterId() == null) {
			JSFUtils.addMessage(FORM + ":vidRegist", FacesMessage.SEVERITY_ERROR, getMessageResourceString(
					UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "nastrScreenFormsList.vidRegis")));
			save = false;
		}

		if (this.registerOption.getObjectType() == null) {
			JSFUtils.addMessage(FORM + ":objType", FacesMessage.SEVERITY_ERROR, getMessageResourceString(
					UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "nastrScreenFormsList.objType")));
			save = false;
		}

		return save;
	}

	/*************** ВХОДНИ ДОКУМЕНТИ *******************/
	public void actionNewDocIn() {
		this.flagEditDocIn = false;
		this.docsIn = new RegisterOptionsDocsIn();
		this.docInActivities = new ArrayList<>();
		this.deinList = new ArrayList<>();
	}

	public void actionEditDocIn() {
		this.flagEditDocIn = true;
		this.docInActivities = new ArrayList<>();
		this.deinList = new ArrayList<>();
		loadDeinList(); // зарежда дейностите към документа
	}

	private void loadDeinList() {

		List<SystemClassif> tmpList = new ArrayList<>();

		if (this.registerOption != null && this.registerOption.getDocsIn() != null
				&& !this.registerOption.getDocsIn().isEmpty()) {
			for (RegisterOptionsDocsIn docIn : this.registerOption.getDocsIn()) {

				// документа, който искат да редактират
				if (docIn.equals(this.docsIn)) {

					// обхождаме дейностите на избрания документ
					for (RegisterOptionsDocinActivity docAct : docsIn.getDocsInActivity()) {

						this.docInActivities.add(docAct.getActivityId());

						String tekst = "";
						SystemClassif scItem = new SystemClassif();
						try {
							scItem.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_DEINOST);
							scItem.setCode(docAct.getActivityId());
							tekst = getSystemData().decodeItem(BabhConstants.CODE_CLASSIF_VID_DEINOST,
									docAct.getActivityId(), getUserData().getCurrentLang(), new Date());
							scItem.setTekst(tekst);
							tmpList.add(scItem);

						} catch (DbErrorException e) {
							LOGGER.error("Грешка при зареждане на дейности! ", e);
						}
					}
				}
			}
		}

		setDeinList(tmpList);
	}

	/** ДОБАВЯНЕ НА НОВО ВХОДНО ЗАЯВЛЕНИЕ */
	public void actionAddDocIn() {

		if (checkDocInData()) {

			// ако има въведени дейности ги добавяме в обекта
			if (this.docInActivities != null && this.docInActivities.size() > 0) {

				this.docsIn.getDocsInActivity().clear(); // зачиствам старите обекти

				for (Integer code : docInActivities) {
					RegisterOptionsDocinActivity tmpActivity = new RegisterOptionsDocinActivity();
					tmpActivity.setActivityId(code);
					tmpActivity.setDocIn(docsIn);

					this.docsIn.getDocsInActivity().add(tmpActivity);
				}
			}

			if (!flagEditDocIn) {
				this.docsIn.setRegisterOptions(this.registerOption);
				this.registerOption.getDocsIn().add(this.docsIn);
			}

			String dialog = "PF('modalInsDoc').hide();";
			PrimeFaces.current().executeScript(dialog);

		}
	}

	/** ПРОВЕРКА НА ВЪВЕДЕНИТЕ ДАННИ ЗА НОВО ВХОДНО ЗАЯВЛЕНИЕ */
	private boolean checkDocInData() {
		boolean save = true;

		try {

			if (this.docsIn.getMeuNimber() == null || SearchUtils.isEmpty(this.docsIn.getMeuNimber())) {
				JSFUtils.addMessage(FORM + ":numUslDlg", FacesMessage.SEVERITY_ERROR, getMessageResourceString(
						UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "nastrScreenForm.uslugaNum")));
				save = false;
			}

			if (this.docsIn.getVidDoc() != null && !flagEditDocIn) {

				// ПРОВЕРКАТА Е ПО ВИД ДОК!
				boolean savedDoc = this.dao.checkDocIn(this.docsIn.getVidDoc());
				if (savedDoc) {
					JSFUtils.addMessage(FORM + ":numUslDlg", FacesMessage.SEVERITY_ERROR,
							getMessageResourceString(beanMessages, "nastrScreenForm.savedDocIn"));
					save = false;
				}
			}

			if (this.docsIn.getVidDoc() == null) {
				JSFUtils.addMessage(FORM + ":vidDocDlg", FacesMessage.SEVERITY_ERROR, getMessageResourceString(
						UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "nastrScreenForm.vidDoc")));
				save = false;
			}

			if (this.docsIn.getPurpose() == null) {
				JSFUtils.addMessage(FORM + ":docForDlg", FacesMessage.SEVERITY_ERROR, getMessageResourceString(
						UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "nastrScreenForm.docFor")));
				save = false;
			}

			// TODO в базата е not null.. задължително ли трябва да е?
			if (this.docsIn.getPayCharacteristic() == null) {
				JSFUtils.addMessage(FORM + ":payDlg", FacesMessage.SEVERITY_ERROR, getMessageResourceString(
						UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "nastrScreenForm.pay")));
				save = false;
			}

		} catch (Exception e) {
			LOGGER.error("Грешка при проверка на въведените данни за входящи документи", e);
		}

		return save;
	}

	/** ПРЕМАХВАНЕ НА ДОКУМЕНТ ОТ СПИСЪКА */
	public void actionDeleteDocIn() {
		try {

			this.registerOption.getDocsIn().remove(this.docsIn);

		} catch (Exception e) {
			LOGGER.error("Грешка при изтриване на входен документ от списъка", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при изтриване на входен документ от списъка");
		}
	}

	

	/*************** ИЗХОДНИ ДОКУМЕНТИ *******************/
	public void actionNewDocOut() {
		this.docsOut = new RegisterOptionsDocsOut();
		this.srokDocOut = BabhConstants.CODE_ZNACHENIE_NE;
	}

	public void actionEditDocOut() {
		
		this.flagEditDocOut = true;
		
		this.srokDocOut = BabhConstants.CODE_ZNACHENIE_DA;
		if (this.docsOut.getTypePeriodValid() == null && this.docsOut.getPeriodValid() == null) {
			this.srokDocOut = BabhConstants.CODE_ZNACHENIE_NE;
		}  
	}

	/** ДОБАВЯНЕ НА НОВО ИЗХОДНО ЗАЯВЛЕНИЕ */
	public void actionAddDocOut() {
		boolean save = true;

		// TODO има ли ограничение при въвеждане на изходни документи

		if (this.docsOut.getVidDoc() == null) {
			JSFUtils.addMessage(FORM + ":vidDocOutDlg", FacesMessage.SEVERITY_ERROR, getMessageResourceString(
					UI_beanMessages, MSGPLSINS, getMessageResourceString(LABELS, "nastrScreenForm.vidDoc")));
			save = false;
		}

		if (this.docsOut.getSaveNomReissue() == null) {
			JSFUtils.addMessage(FORM + ":saveState", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, MSGPLSINS,
							getMessageResourceString(LABELS, "nastrScreenForm.saveNumPreizdavane")));
			save = false;
		}
		
		if (this.srokDocOut == null) {
			JSFUtils.addMessage(FORM + ":srokDocOut", FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, MSGPLSINS,
							getMessageResourceString(LABELS, "nastrScreenForm.srok")));
			save = false;
			
		} else if (this.srokDocOut == BabhConstants.CODE_ZNACHENIE_DA) {
			
			//имаме срочен документ
			if (this.docsOut.getPeriodValid() == null) {
				JSFUtils.addMessage(FORM + ":periodValue", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "nastrScreenForm.periodVal")));
				save = false;
			}
			
			if (this.docsOut.getTypePeriodValid() == null) {
				JSFUtils.addMessage(FORM + ":periodType", FacesMessage.SEVERITY_ERROR,
						getMessageResourceString(UI_beanMessages, MSGPLSINS,
								getMessageResourceString(LABELS, "nastrScreenForm.periodType")));
				save = false;
			}			
		}


		if (save) {

			if (!flagEditDocOut) {
				this.docsOut.setRegisterOptions(this.registerOption);
				this.registerOption.getDocsOut().add(this.docsOut);
			}

			String dialog = "PF('modalOutDoc').hide();";
			PrimeFaces.current().executeScript(dialog);
			this.flagEditDocOut = false;
		}
	}
	
	
	/** ПРИ ПРОМЯНА ТЪПА ПРОДЪЛЖИТЕЛНОСТ НА БЕЗСРОЧЕН ДОКУМЕНТ ИЗЧИСТВА СТОЙНОСТИТЕ */
	public void actionChangePerType() {
		if (this.srokDocOut != null && this.srokDocOut == BabhConstants.CODE_ZNACHENIE_NE) {
			this.docsOut.setPeriodValid(null);
			this.docsOut.setTypePeriodValid(null);
		}
	}

	/** ПРЕМАХВАНЕ НА ИЗХОДЕН ДОКУМЕНТ ОТ СПИСЪКА */
	public void actionDeleteDocOut() {
		try {

			this.registerOption.getDocsOut().remove(this.docsOut);

		} catch (Exception e) {
			LOGGER.error("Грешка при изтриване на входен документ от списъка", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при изтриване на входен документ от списъка");
		}
	}

	/*************** ЕКРАННА СТРУКТУРА *******************/
	public void actionNewDisplay() {
		this.display = new RegisterOptionsDisplay();
		this.flagEditDisplay = false;
	}

	public void actionEditDisplay() {
		this.flagEditDisplay = true;
	}

	/** ДОБАВЯНЕ НА НОВ АТРИБУТ В ЕКРАННАТА ФОРМА */
	public void actionAddDisplay() {
		boolean save = true;

		if (this.display.getObjectClassifId() == null) {
			JSFUtils.addMessage(FORM + ":attr", FacesMessage.SEVERITY_ERROR, getMessageResourceString(UI_beanMessages,
					MSGPLSINS, getMessageResourceString(LABELS, "nastrScreenForm.vidDoc")));
			save = false;

		} else {

			for (Iterator<RegisterOptionsDisplay> iterator = this.getRegisterOption().getDisplay().iterator(); iterator
					.hasNext();) {
				RegisterOptionsDisplay tmpDisplay = (RegisterOptionsDisplay) iterator.next();

				// избрания атрибут вече е въведен за конкретния регистър
				if (tmpDisplay.getObjectClassifId().equals(this.display.getObjectClassifId()) && !flagEditDisplay) {
					JSFUtils.addMessage(FORM + ":attr", FacesMessage.SEVERITY_ERROR,
							getMessageResourceString(beanMessages, "nastrObjBean.attr"));
					save = false;
				}
			}
		}

		if (save) {

			if (!flagEditDisplay) {
				this.display.setRegisterOptions(this.registerOption);
				this.registerOption.getDisplay().add(this.display);
			}

			String dialog = "PF('modalPageStruct').hide();";
			PrimeFaces.current().executeScript(dialog);
		}
	}

	/** ПРЕМАХВАНЕ НА АТРИБУТ СПИСЪКА */
	public void actionDeleteDisplay() {
		try {

			this.registerOption.getDisplay().remove(this.display);

		} catch (Exception e) {
			LOGGER.error("Грешка при изтриване на входен документ от списъка", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при изтриване на входен документ от списъка");
		}
	}
	
	
	/** ПРИ ПРОМЯНА НА ТИПА ПЛАЩАНЕ ЗАНУЛЯВА СУМАТА */
	public void actionChangePayType() {
		if (this.docsIn.getPayCharacteristic().equals(BabhConstants.CODE_ZNACHENIE_PAY_TYPE_NOPAY)) {
			this.docsIn.setPayAmount(0F);
		}
	}



	/***************************************
	 * GET/SET
	 **************************************************/
	public Date getDecodeDate() {
		return decodeDate;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate;
	}

	public RegisterOptions getRegisterOption() {
		return registerOption;
	}

	public void setRegisterOption(RegisterOptions registerOption) {
		this.registerOption = registerOption;
	}

	public RegisterOptionsDocsIn getDocsIn() {
		return docsIn;
	}

	public void setDocsIn(RegisterOptionsDocsIn docsIn) {
		this.docsIn = docsIn;
	}

	public RegisterOptionsDocsOut getDocsOut() {
		return docsOut;
	}

	public void setDocsOut(RegisterOptionsDocsOut docsOut) {
		this.docsOut = docsOut;
	}

	public RegisterOptionsDisplay getDisplay() {
		return display;
	}

	public void setDisplay(RegisterOptionsDisplay display) {
		this.display = display;
	}

	public RegisterOptionsDAO getDao() {
		return dao;
	}

	public void setDao(RegisterOptionsDAO dao) {
		this.dao = dao;
	}

	public List<Integer> getDocInActivities() {
		return docInActivities;
	}

	public void setDocInActivities(List<Integer> docInActivities) {
		this.docInActivities = docInActivities;
	}

	public String getIdObj() {
		return idObj;
	}

	public void setIdObj(String idObj) {
		this.idObj = idObj;
	}

	public String getSelectedUslugaText() {
		return selectedUslugaText.substring(0, 3);
	}

	public void setSelectedUslugaText(String selectedUslugaText) {
		this.selectedUslugaText = selectedUslugaText;
	}

	public boolean isShowEkranPanel() {
		return showEkranPanel;
	}

	public void setShowEkranPanel(boolean showEkranPanel) {
		this.showEkranPanel = showEkranPanel;
	}

	public boolean isFlagEditDocIn() {
		return flagEditDocIn;
	}

	public void setFlagEditDocIn(boolean flagEditDocIn) {
		this.flagEditDocIn = flagEditDocIn;
	}

	public boolean isFlagEditDocOut() {
		return flagEditDocOut;
	}

	public void setFlagEditDocOut(boolean flagEditDocOut) {
		this.flagEditDocOut = flagEditDocOut;
	}

	public boolean isFlagEditDisplay() {
		return flagEditDisplay;
	}

	public void setFlagEditDisplay(boolean flagEditDisplay) {
		this.flagEditDisplay = flagEditDisplay;
	}

	public List<SystemClassif> getDeinList() {
		return deinList;
	}

	public void setDeinList(List<SystemClassif> deinList) {
		this.deinList = deinList;
	}

	
	public List<Integer> getReadOnlyCodesVidReg() {
		return readOnlyCodesVidReg;
	}

	public void setReadOnlyCodesVidReg(List<Integer> readOnlyCodesVidReg) {
		this.readOnlyCodesVidReg = readOnlyCodesVidReg;
	}

	public SystemData getSd() {
		return sd;
	}

	public void setSd(SystemData sd) {
		this.sd = sd;
	}

	public Integer getPeriodType() {
		return periodType;
	}

	public void setPeriodType(Integer periodType) {
		this.periodType = periodType;
	}

	public Integer getSrokDocOut() {
		return srokDocOut;
	}

	public void setSrokDocOut(Integer srokDocOut) {
		this.srokDocOut = srokDocOut;
	}

}