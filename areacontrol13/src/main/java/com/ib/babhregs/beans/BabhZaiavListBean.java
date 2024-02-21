package com.ib.babhregs.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.search.BabhZaiavSearch;
import com.ib.babhregs.search.VpisvaneSearch;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.BaseException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

/**
 * ФИЛТЪР ЗА ТЪРСЕНЕ НА ПОСТЪПИЛИ ЗАЯВЛЕНИЯ ЗА ВЪВЕЖДАНЕ/АКТУАЛИЗАЦИЯ НА
 * РЕГИСТРИ
 * 
 * @author s.arnaudova
 */

@Named
@ViewScoped
public class BabhZaiavListBean extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1371381446040561125L;
	private static final Logger LOGGER = LoggerFactory.getLogger(BabhZaiavListBean.class);

	private String isView;

	private SystemData sd;
	private UserData ud;

	private BabhZaiavSearch docSearch;
	private LazyDataModelSQL2Array docsList;

	private Date decodeDate;

	private Integer periodR; // период на регистрация на документа
	private Integer periodStatus; // период на статус

	private List<SystemClassif> registerIdClList;
	private List<SystemClassif> docVidList;
	private List<SelectItem> registraturiList; // спсисък с позволените регистратури

	private RegisterOptions regOptions;
	
	private List<Files> filesList; //списък на файлове към заявлението
	
	private LazyDataModelSQL2Array vpisvaniaList;
	private VpisvaneSearch vpisvaneSearch;
	private Integer tmpDocId;
	private Doc tmpDoc;
	
	private List<Integer> statusList;
	
	private Integer viewedRows; // запазва ид-то на реда, ако вече е бил разглеждан

	private Integer periodStatusP; // период на статус вписван в прозореца за промяна
	private Integer periodUdoc; // период на удостоверителния документ в прозореца за промяна
	
	@PostConstruct
	private void init() {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("BabhZaiavListBean!!!!!!");
		}

		try {

			this.sd = (SystemData) getSystemData();
			this.ud = getUserData(UserData.class);
			this.decodeDate = new Date();
			this.docSearch = new BabhZaiavSearch();
			this.registerIdClList = new ArrayList<>();
			this.docVidList = new ArrayList<>();

			this.registraturiList = createItemsList(true, BabhConstants.CODE_CLASSIF_REGISTRATURI, decodeDate, true);

			this.docSearch.getStatusList().add(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_WAIT); // по дефолт зареждаме само чакащите за обработка
			//this.docSearch.getRegistraturaList().add(this.ud.getRegistratura()); // от съответната регистратура на потребителя
			
			//if (this.ud.isLimitedAccess()) {
				//this.docSearch.setCodeRef(ud.getUserId());
			//}
			this.docSearch.setCodeRef(ud.getUserId());

			FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
			this.isView = (String) faceletContext.getAttribute("isView");
			
			//ТЕЗИ НЕЩА СЕ ИЗПОЛЗВАТ ОТ МОДАЛНИЯ ЗА ПРОМЯНА ИЛИ ЗАЛИЧАВАНЕ..
			this.vpisvaneSearch = new VpisvaneSearch();
			
			this.statusList = new ArrayList<>();
			this.statusList.add(BabhConstants.STATUS_VP_WAITING);
			this.statusList.add(BabhConstants.STATUS_VP_VPISAN);
			
			this.vpisvaneSearch.setStatusList(statusList);

		} catch (Exception e) {
			LOGGER.error("Грешка инициализиране на BabhZaiavListBean! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(beanMessages, "general.exception"), e.getMessage());
		}
	}

	public void changePeriodR() {

		if (this.periodR != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.periodR);
			docSearch.setDocDateFrom(di[0]);
			docSearch.setDocDateTo(di[1]);
		} else {
			docSearch.setDocDateFrom(null);
			docSearch.setDocDateTo(null);
		}
	}

	public void changeDate() {
		this.setPeriodR(null);
	}

	public void changePeriodStatus() {

		if (this.periodStatus != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.periodStatus);
			docSearch.setStatusDateFrom(di[0]);
			docSearch.setStatusDateTo(di[1]);
		} else {
			docSearch.setStatusDateFrom(null);
			docSearch.setStatusDateTo(null);
		}
	}

	public void changeDateStatus() {
		this.setPeriodStatus(null);
	}

	public void actionSearch() {
		try {

			docSearch.buildQueryZaiavList((UserData) getUserData(), getSystemData());
			docsList = new LazyDataModelSQL2Array(docSearch, "a4 desc");
			
			this.viewedRows = null; // зачиства списъка с видяни редове

		} catch (Exception e) {
			LOGGER.error("Грешка при търсене на документи! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(beanMessages, "general.exception"), e.getMessage());
		}
	}

	public void actionClear() {
		this.periodR = null;
		this.periodStatus = null;
		docsList = null;
		changePeriodR();
		changePeriodStatus();
		this.docSearch = new BabhZaiavSearch();
		this.docSearch.getStatusList().add(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_WAIT);
		this.docSearch.setCodeRef(ud.getUserId());

		this.registerIdClList = new ArrayList<>();
		this.docVidList = new ArrayList<>();
		this.viewedRows = null;

	}

	public String actionGotoNew() {
		return "babhZaiavEdit.xhtml?faces-redirect=true";
	}

	public String actionGotoEdit(String idObj,String idV) {
		String result="";
		setViewedRows(Integer.valueOf(idObj));
		if (SearchUtils.isEmpty(idV)) {
			result = "babhZaiavEdit.xhtml?faces-redirect=true&idObj=" + idObj;
		} else {
			result = "babhZaiavEdit.xhtml?faces-redirect=true&idObj=" + idObj+"&idV="+idV;
		}
		
		return result;
	}

	/**
	 * ПРЕНАСОЧВАНЕ КЪМ ВПИСВАНЕ (АКО ИМА ТАКОВА ГО ОТВАРЯМЕ ЗА АКТУАЛИЗАЦИЯ.. АКО НЕ.. ВЪВЕЖДАНЕ)
	 * 
	 */
	public String actionGoToVpis(String idZ, String idV, Integer vidDoc) {
		String outcome = "index.xhtml?faces-redirect=true";

		setViewedRows(Integer.valueOf(idZ));

		Integer objType = loadRegOptionsType(vidDoc);
		if (this.regOptions != null) {
			
			Integer docPurpose = null; // предназначението на документа ми е необходимо за да преценя на къде да пренасочвам странницата			
			List<RegisterOptionsDocsIn> lstDocsIn = regOptions.getDocsIn(); 			
			for (RegisterOptionsDocsIn item : lstDocsIn) {
				if ((item.getVidDoc()).equals(vidDoc)) {
					docPurpose = item.getPurpose();
					break;
				}
			}	
			
			// отваряме подалния за връзка само ако заявлението е за промяна и за НОВО вписване..
			if ( (docPurpose.equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_PROMIANA) | (docPurpose.equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_ZALICHAVANE)) ) && SearchUtils.isEmpty(idV)) {
				
				outcome = null;				
				String dialog = "PF('modalSlcVpisVar').show();";
				PrimeFaces.current().executeScript(dialog);
				
			} else {
				
				// Лице - дейности фуражи
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI)) {
					if (idV != null && !SearchUtils.isEmpty(idV)) {
						// имаме актуализация на вписвание
						outcome = "regTargovtsiFuraj.xhtml?faces-redirect=true&idV=" + idV;
					} else {
						// имаме ново вписване
						outcome = "regTargovtsiFuraj.xhtml?faces-redirect=true&idZ=" + idZ;
					}
				}

				// Лице - дейности ЗЖ
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ)) {
					if (idV != null && !SearchUtils.isEmpty(idV)) {
						outcome = "regLicaDeinZJ.xhtml?faces-redirect=true&idV=" + idV;
					} else {
						outcome = "regLicaDeinZJ.xhtml?faces-redirect=true&idZ=" + idZ;
					}
				}
				
				//Лице - дейност ВЛП
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP)) {
					if (idV != null && !SearchUtils.isEmpty(idV)) {
						outcome = "regVLPEdit.xhtml?faces-redirect=true&idV=" + idV;
					} else {
						outcome = "regVLPEdit.xhtml?faces-redirect=true&idZ=" + idZ;
					}
				}
				
				//регситарция на МПС - животни
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ)) {
					if (idV != null && !SearchUtils.isEmpty(idV)) {
						outcome = "regMpsZJ.xhtml?faces-redirect=true&idV=" + idV+"&z=1";
					} else {
						outcome = "regMpsZJ.xhtml?faces-redirect=true&idZ=" + idZ;
					}
				}
				
				//регситарция на МПС - фуражи
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS)) {
					if (idV != null && !SearchUtils.isEmpty(idV)) {
						outcome = "regMpsFuraj.xhtml?faces-redirect=true&idV=" + idV;
					} else {
						outcome = "regMpsFuraj.xhtml?faces-redirect=true&idZ=" + idZ;
					}
				}
				
				//регситарция на ОЕЗ
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ)) {
					if (idV != null && !SearchUtils.isEmpty(idV)) {
						outcome = "regOEZ.xhtml?faces-redirect=true&idV=" + idV;
					} else {
						outcome = "regOEZ.xhtml?faces-redirect=true&idZ=" + idZ;
					}
				}
				//регситарция на Заявление за търговия с ВЛП
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP)) {
					if (idV != null && !SearchUtils.isEmpty(idV)) {
						outcome = "regObektVLPEdit.xhtml?faces-redirect=true&idV=" + idV;
					} else {
						outcome = "regObektVLPEdit.xhtml?faces-redirect=true&idZ=" + idZ;
					}
				}
				//регситарция на ВЛЗ
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ)) {
					if (idV != null && !SearchUtils.isEmpty(idV)) {
						outcome = "regVLZ.xhtml?faces-redirect=true&idV=" + idV;
					} else {
						outcome = "regVLZ.xhtml?faces-redirect=true&idZ=" + idZ;
					}
				}
			}
		
		} else {
			
			if (SearchUtils.isEmpty(idV) && vidDoc.equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_ODOB_OPERATORI_18)) {
				outcome = "regTargovtsiFuraj.xhtml?faces-redirect=true&idZ=" + idZ;				
			} else {
				outcome = null;
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Няма открити настройки за този регистър! ");
			}
		}

		return outcome;
	}
	
	
	
	public void actionSearchVpisvaneDlg() {
	
		try {

			if (this.tmpDocId != null) {
				
				this.tmpDoc = new Doc();
				JPA.getUtil().runWithClose(() -> this.tmpDoc = new DocDAO(ud).findById(tmpDocId));
				
				if (this.tmpDoc != null) {
					
					vpisvaneSearch.setRegisterId(tmpDoc.getRegisterId());
					
					//скривам временно регистратурата заради обучението.. нещо не работи ОК
					//List<Integer> tmpRegistraturiList = new ArrayList<>();
					//tmpRegistraturiList.add(tmpDoc.getRegistraturaId());

					//this.vpisvaneSearch.setRegistraturaList(tmpRegistraturiList);
					
					//само вписваните заявления
					List<Integer> tmpStatusList = new ArrayList<>();
					tmpStatusList.add(BabhConstants.STATUS_VP_VPISAN);

					this.vpisvaneSearch.setStatusList(tmpStatusList);

				}
			}
			vpisvaneSearch.setCheckAccess(false); // без достъп до вписванията !!!
			vpisvaneSearch.buildQueryVpisvaneList((UserData)getUserData(), getSystemData());
			vpisvaniaList = new LazyDataModelSQL2Array(vpisvaneSearch, "v.status");
			
			
		} catch (Exception e) {
			LOGGER.error("Грешка при търсене на вписвания! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(beanMessages, "general.exception"), e.getMessage());
		}
	}
	
	public void changePeriodProm() {
		
    	if (this.periodStatusP != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.periodStatusP);
			vpisvaneSearch.setStatDateFrom(di[0]) ;
			vpisvaneSearch.setStatDateTo(di[1]);		
    	} else {
    		vpisvaneSearch.setStatDateFrom(null);
    		vpisvaneSearch.setStatDateTo(null);
    	}
    }
	
	public void changeDateStatusVp() { 
		this.setPeriodStatusP(null);
	}
	
	public void changePeriodStVpisvane() {
		
    	if (this.periodUdoc != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.periodUdoc);
			vpisvaneSearch.setResultDateFrom(di[0]);
			vpisvaneSearch.setResultDateTo(di[1]);		
    	} else {
    		vpisvaneSearch.setResultDateFrom(null);
    		vpisvaneSearch.setResultDateTo(null);
		}
    }
	
	public void changeDateStVpisvane() { 
		this.setPeriodUdoc(null); 
	}
	
	
	
	public void actionClearM() {		
		this.vpisvaneSearch = new VpisvaneSearch();		
		this.vpisvaniaList = null;
		this.periodStatusP = null;
		this.periodUdoc = null;
		
	}
	
	
	
	public void actionAddZaiavToVpis(Integer idV, Integer vidDoc) {
		try {
			
			VpisvaneDAO vpisvaneDAO = new VpisvaneDAO(ud);
			JPA.getUtil().runInTransaction(() -> vpisvaneDAO.addZaqavlenie(tmpDocId, idV, sd));
			
			Integer objType = loadRegOptionsType(vidDoc);
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			
			if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI)) {
				ec.redirect(ec.getRequestContextPath() + "/pages/regTargovtsiFuraj.xhtml?faces-redirect=true&idV=" + idV + "&change=1");
			}

			// Лице - дейности ЗЖ
			if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ)) {
				ec.redirect(ec.getRequestContextPath() + "/pages/regLicaDeinZJ.xhtml?faces-redirect=true&idV=" + idV + "&change=1");
			}
			
			//Лице - дейност ВЛП
			if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP)) {
				ec.redirect(ec.getRequestContextPath() + "/pages/regVLPEdit.xhtml?faces-redirect=true&idV=" + idV + "&change=1");
			}
			
			//регситарция на МПС - животни
			if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ)) {
				ec.redirect(ec.getRequestContextPath() + "/pages/regMpsZJ.xhtml?faces-redirect=true&idV=" + idV + "&change=1");
			}
			
			//регситарция на МПС - фуражи
			if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS)) {
				ec.redirect(ec.getRequestContextPath() + "/pages/regMpsFuraj.xhtml?faces-redirect=true&idV=" + idV + "&change=1");
			}
			
			//регситарция на ОЕЗ
			if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ)) {
				ec.redirect(ec.getRequestContextPath() + "/pages/regOEZ.xhtml?faces-redirect=true&idV=" + idV + "&change=1");
			}
			//регситарция на Заявление за търговия с ВЛП
			if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP)) {
				ec.redirect(ec.getRequestContextPath() + "/pages/regObektVLPEdit.xhtml?faces-redirect=true&idV=" + idV + "&change=1");
			}
			//регситарция на ВЛЗ
			if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ)) {
				ec.redirect(ec.getRequestContextPath() + "/pages/regVLZ.xhtml?faces-redirect=true&idV=" + idV + "&change=1");
			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при добавяне на заявление към вписване! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Грешка при добавяне на заявление към вписване!", e.getMessage());
		}
	}

	/**
	 * ЗАРЕЖДА НАСТРОЙКИТЕ НА РЕГИСТЪРА СПОРЕД ВИДА НА ДОКУМЕНТА.. ОТ НЕГО ИЗВЛИЧАМЕ
	 * ТИПА НА ОБЕКТА ЗА ЛИЦЕНЗИРАНЕ И ПРЕЦЕНЯМЕ КЪМ КОЯ СТРАННИЦА ДА НАСОЧИМ
	 */
	private Integer loadRegOptionsType(Integer vidDoc) {
		Integer objType = null; // тип на обекта от настройката

		Integer registerId = sd.getRegisterByVidDoc().get(vidDoc);
		this.regOptions = sd.getRoptions().get(registerId); // всички настройки за регистъра
		if (this.regOptions != null) {
			objType = this.regOptions.getObjectType();
		}

		return objType;
	}
	
	/** Търси файловете към конкретно заявление по ид на заявление  */
	public void docExpand(Integer docId) {
		
		this.filesList = new ArrayList<>();
		try {
			FilesDAO daoF = new FilesDAO(getUserData());
			filesList = daoF.selectByFileObjectDop(docId, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);

		} catch (BaseException e) {
			LOGGER.error("Грешка при извличане на файловете към документ! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG), e.getMessage());
		}
	}
	

	/**************************************************
	 * ЕКСПОРТИ
	 ***********************************************************/
	public void postProcessXLS(Object document) {
		try {

			String title = getMessageResourceString(LABELS, "babZaiavList.reportTitle");
			new CustomExpPreProcess().postProcessXLS(document, title, null, null, null);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(beanMessages, "general.exception"), e.getMessage());

		}
	}

	public void preProcessPDF(Object document) {
		try {

			String title = getMessageResourceString(LABELS, "babZaiavList.reportTitle");
			new CustomExpPreProcess().preProcessPDF(document, title, null, null, null);

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(beanMessages, "general.exception"), e.getMessage());
		}
	}

	public PDFOptions pdfOptions() {

		PDFOptions pdfOpt = new CustomExpPreProcess().pdfOptions(null, null, null);
		return pdfOpt;
	}

	public Date getDecodeDate() {
		return decodeDate;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate;
	}

	public Integer getPeriodR() {
		return periodR;
	}

	public void setPeriodR(Integer periodR) {
		this.periodR = periodR;
	}

	public LazyDataModelSQL2Array getDocsList() {
		return docsList;
	}

	public void setDocsList(LazyDataModelSQL2Array docsList) {
		this.docsList = docsList;
	}

	public BabhZaiavSearch getDocSearch() {
		return docSearch;
	}

	public void setDocSearch(BabhZaiavSearch docSearch) {
		this.docSearch = docSearch;
	}

	public Integer getPeriodStatus() {
		return periodStatus;
	}

	public void setPeriodStatus(Integer periodStatus) {
		this.periodStatus = periodStatus;
	}

	public String getIsView() {
		return isView;
	}

	public void setIsView(String isView) {
		this.isView = isView;
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

	public List<SystemClassif> getRegisterIdClList() {
		return registerIdClList;
	}

	public void setRegisterIdClList(List<SystemClassif> registerIdClList) {
		this.registerIdClList = registerIdClList;
	}

	public RegisterOptions getRegOptions() {
		return regOptions;
	}

	public void setRegOptions(RegisterOptions regOptions) {
		this.regOptions = regOptions;
	}

	public List<SystemClassif> getDocVidList() {
		return docVidList;
	}

	public void setDocVidList(List<SystemClassif> docVidList) {
		this.docVidList = docVidList;
	}

	public List<Files> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<Files> filesList) {
		this.filesList = filesList;
	}

	public LazyDataModelSQL2Array getVpisvaniaList() {
		return vpisvaniaList;
	}

	public void setVpisvaniaList(LazyDataModelSQL2Array vpisvaniaList) {
		this.vpisvaniaList = vpisvaniaList;
	}

	public VpisvaneSearch getVpisvaneSearch() {
		return vpisvaneSearch;
	}

	public void setVpisvaneSearch(VpisvaneSearch vpisvaneSearch) {
		this.vpisvaneSearch = vpisvaneSearch;
	}

	public Integer getTmpDocId() {
		return tmpDocId;
	}

	public void setTmpDocId(Integer tmpDocId) {
		this.tmpDocId = tmpDocId;
	}

	public Doc getTmpDoc() {
		return tmpDoc;
	}

	public void setTmpDoc(Doc tmpDoc) {
		this.tmpDoc = tmpDoc;
	}

	public List<Integer> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Integer> statusList) {
		this.statusList = statusList;
	}

	public List<SelectItem> getRegistraturiList() {
		return registraturiList;
	}

	public void setRegistraturiList(List<SelectItem> registraturiList) {
		this.registraturiList = registraturiList;
	}

	public Integer getViewedRows() {
		return viewedRows;
	}

	public void setViewedRows(Integer viewedRows) {
		this.viewedRows = viewedRows;
	}

	public Integer getPeriodStatusP() {
		return periodStatusP;
	}

	public void setPeriodStatusP(Integer periodStatusP) {
		this.periodStatusP = periodStatusP;
	}

	public Integer getPeriodUdoc() {
		return periodUdoc;
	}

	public void setPeriodUdoc(Integer periodUdoc) {
		this.periodUdoc = periodUdoc;
	}


}
