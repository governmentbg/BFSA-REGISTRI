package com.ib.babhregs.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.Substance;
import com.ib.babhregs.search.SpecReportSearch;
import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.SysClassifAdapter;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.InvalidParameterException;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.DateUtils;

@Named
@ViewScoped
public class SpecialReportsList extends IndexUIbean {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpecialReportsList.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -335797201303155180L;

	private SpecReportSearch srs = new SpecReportSearch();
	private List<SystemClassif> docVidList = new ArrayList<>();
	private Integer periodRegisterZ;
	private Integer periodStatus;
	private Integer periodStVpisvane;
	private Integer periodUD;
	private Integer periodUdStatus;
	private Integer refType = BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL;
	private LazyDataModelSQL2Array specReportList;
	private Integer viewedRows; // запазва ид-то на реда, ако вече е бил разглеждан
	private List<SelectItem> registraturiList; // спсисък с позволените регистратури
	private int countryBG; // ще се инициализира в инита през системна настройка: delo.countryBG
	private List<SystemClassif> registerSC = new ArrayList<>();
	private String udValidnost;
	private List<SystemClassif> selectedFarmFormsListSC; // заради autocomplete
	private List<SystemClassif> selectedudDocVidListSC; // заради autocomplete
	private List<SystemClassif> selectedvlpVidDeinListSC; // заради autocomplete
	private List<SystemClassif> selectedvlpPredmetPrvnosListSC; // заради autocomplete
	private List<SystemClassif> selectedjivVidDeinListSC; // заради autocomplete
	private List<SystemClassif> selectedjivVidJivotniListSC; // заради autocomplete
	private List<SystemClassif> selectedjivTehnologiaListSC; // заради autocomplete
	private List<SystemClassif> selectedjivPrednJivOezListSC; // заради autocomplete
	private List<SystemClassif> selectedfurVidFurajListSC; // заради autocomplete
	private List<SystemClassif> selectedfurVidJivotniListSC; // заради autocomplete
	private List<SystemClassif> selectedfurVidDeinListSC; // заради autocomplete
	private List<SystemClassif> selectedvlpPredmetFarmakolListSC; // заради autocomplete
	private Substance vidIdentifier;
	
	private List<SystemClassif> registriOEZ = new ArrayList<SystemClassif>();
	private List<SystemClassif> zaiavOEZ = new ArrayList<SystemClassif>();
	private List<SystemClassif> registriVLP = new ArrayList<SystemClassif>();
	private List<SystemClassif> zaiavVLP = new ArrayList<SystemClassif>();
	private List<SystemClassif> registriFur = new ArrayList<SystemClassif>();
	private List<SystemClassif> zaiavFur = new ArrayList<SystemClassif>();
	private List<SystemClassif> vidUdDocs = new ArrayList<SystemClassif>();
	private List<SystemClassif> vidUdDocsFur = new ArrayList<SystemClassif>();
	private List<SystemClassif> vidUdDocsVLP = new ArrayList<SystemClassif>();
	private List<SystemClassif> vlpVidDeinList = new ArrayList<SystemClassif>();
	private List<SystemClassif> furVidDeinList = new ArrayList<SystemClassif>();
	private List<SystemClassif> oezVidDeinList = new ArrayList<SystemClassif>();
	
	private String vidZPrednz;
	
	@PostConstruct
	public void init() {
		try {
			this.registraturiList = createItemsList(true, BabhConstants.CODE_CLASSIF_REGISTRATURI, new Date(), true);
			this.countryBG = Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG"));
			srs.setJivObektCountry(countryBG);
			srs.setFurObektCountry(countryBG);
			srs.setVlpObektCountry(countryBG);
			setUdValidnost(0l + "");
			registriOEZ = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_REGISTRI), BabhConstants.REGISTER_ZJ , new Date(), Integer.valueOf(getCurrentLang()));
			zaiavOEZ = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 216 , new Date(), Integer.valueOf(getCurrentLang()));
			registriVLP = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_REGISTRI), BabhConstants.REGISTER_VLP , new Date(), Integer.valueOf(getCurrentLang()));
			zaiavVLP = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 222 , new Date(), Integer.valueOf(getCurrentLang()));
			registriFur = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_REGISTRI), BabhConstants.REGISTER_KF , new Date(), Integer.valueOf(getCurrentLang()));
			zaiavFur = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 197 , new Date(), Integer.valueOf(getCurrentLang()));
			vidUdDocs = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VID_UD_DOCS), 210 , new Date(), Integer.valueOf(getCurrentLang()));
			vidUdDocsFur = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VID_UD_DOCS), 214 , new Date(), Integer.valueOf(getCurrentLang()));
			vidUdDocsVLP = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VID_UD_DOCS), 213 , new Date(), Integer.valueOf(getCurrentLang()));
			vlpVidDeinList = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VID_DEINOST), 15 , new Date(), Integer.valueOf(getCurrentLang()));
			furVidDeinList = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VID_DEINOST), 16 , new Date(), Integer.valueOf(getCurrentLang()));
			oezVidDeinList = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VID_DEINOST), 14 , new Date(), Integer.valueOf(getCurrentLang()));
		} catch (DbErrorException | UnexpectedResultException e) {
			LOGGER.error("erorr na registraturiList w SpecialReportsList! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "general.exception"), e.getMessage());
		}
	}
	
	public void actionSearch() {
		try {
			if(udValidnost != null && ! udValidnost.isEmpty()) {
				if(udValidnost.equals("0"))
					srs.setUdValidList( null );
				else	
					srs.setUdValidList(List.of(Integer.valueOf( udValidnost ) ) );
			}
			String oblast = JSFUtils.getRequestParameter("oblast");
			srs.setOblast(Integer.valueOf(oblast));
			srs.buildQueryReport(getSystemData());
			setSpecReportList(new LazyDataModelSQL2Array(srs, "a1"));
			this.viewedRows = null; // зачиства списъка с видяни редове
		} catch (InvalidParameterException | DbErrorException e) {
			LOGGER.error("error in search for specReport!" + e.getMessage());
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		}
	}
	
	public void actionClear(){
		srs = new SpecReportSearch();
		docVidList = new ArrayList<>();
		periodRegisterZ = null;
		periodStatus = null;
		periodStVpisvane= null;
		periodUD = null;
		periodUdStatus = null;
		specReportList = null;
		this.viewedRows = null; // зачиства списъка с видяни редове
		specReportList = null;
		refType = BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL;
		registerSC = new ArrayList<>();
		selectedFarmFormsListSC = new ArrayList<SystemClassif>();
		selectedfurVidDeinListSC= new ArrayList<SystemClassif>();
		selectedfurVidFurajListSC = new ArrayList<SystemClassif>();
		selectedfurVidJivotniListSC = new ArrayList<SystemClassif>();
		selectedjivPrednJivOezListSC = new ArrayList<SystemClassif>();
		selectedjivTehnologiaListSC = new ArrayList<SystemClassif>();
		selectedjivVidDeinListSC = new ArrayList<SystemClassif>();
		selectedjivVidJivotniListSC = new ArrayList<SystemClassif>();
		selectedudDocVidListSC = new ArrayList<SystemClassif>();
		selectedvlpPredmetPrvnosListSC = new ArrayList<SystemClassif>();
		selectedvlpVidDeinListSC = new ArrayList<SystemClassif>();
		selectedvlpPredmetFarmakolListSC = new ArrayList<SystemClassif>();
	}
	
	public void changeVidZPrednaznachenie(int fromOblast) {
		try {
			switch (fromOblast) {
			case BabhConstants.REGISTER_ZJ:
				switch (Integer.valueOf(vidZPrednz).intValue()) {
				case 1:
					zaiavOEZ = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 216 , new Date(), Integer.valueOf(getCurrentLang()));
					break;
				case 2:
					zaiavOEZ = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 217 , new Date(), Integer.valueOf(getCurrentLang()));
					break;
				case 3:
					zaiavOEZ = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 218 , new Date(), Integer.valueOf(getCurrentLang()));
					break;
				}
				break;
			case BabhConstants.REGISTER_VLP:
				switch (Integer.valueOf(vidZPrednz).intValue()) {
				case 1:
					zaiavVLP = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 222 , new Date(), Integer.valueOf(getCurrentLang()));
					break;
				case 2:
					zaiavVLP = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 223 , new Date(), Integer.valueOf(getCurrentLang()));
					break;
				case 3:
					zaiavVLP = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 224 , new Date(), Integer.valueOf(getCurrentLang()));
					break;
				}
				break;
			case BabhConstants.REGISTER_KF:
				switch (Integer.valueOf(vidZPrednz).intValue()) {
				case 1:
					zaiavFur = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 197 , new Date(), Integer.valueOf(getCurrentLang()));
					break;
				case 2:
					zaiavFur = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 198 , new Date(), Integer.valueOf(getCurrentLang()));
					break;
				case 3:
					zaiavFur = getSystemData().getChildrenOnNextLevel(Integer.valueOf( BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA), 199 , new Date(), Integer.valueOf(getCurrentLang()));
					break;
				}
				break;
			}
		} catch (Exception e) {
			
		}
	}
	
	public void changePeriodRegisterZ() {
    	if (this.periodRegisterZ != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.periodRegisterZ);
			srs.setZaiavDateFrom(di[0]);
			srs.setZaiavDateTo( di[1] );		
    	} else {
    		srs.setZaiavDateFrom(null);
    		srs.setZaiavDateTo(null);
    	}
    }
	
	public void changeDateRegisterZ() { 
		this.setPeriodRegisterZ(null);
	}
	
	public void changePeriodStatus() {
		if (this.periodStatus != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.periodStatus);
			srs.setZaiavStatusDateFrom(di[0]);
			srs.setZaiavStatusDateTo( di[1] );		
		} else {
			srs.setZaiavStatusDateFrom(null);
			srs.setZaiavStatusDateTo(null);
		}
	}
	public void changeDateStatus() { 
		this.setPeriodStatus(null);
	}

	public void changePeriodStVpisvane() {
		if (this.getPeriodStVpisvane() != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(this.getPeriodStVpisvane());
			srs.setVpStatusDateFrom(di[0]);
			srs.setVpStatusDateTo( di[1] );		
		} else {
			srs.setVpStatusDateFrom(null);
			srs.setVpStatusDateTo(null);
		}
	}
	public void changeDateStVpisvane() { 
		this.setPeriodStVpisvane(null);
	}

	public void changePeriodUD() {
		if (periodUD != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(periodUD);
			srs.setUdDateFrom(di[0]);
			srs.setUdDateTo( di[1] );		
		} else {
			srs.setUdDateFrom(null);
			srs.setUdDateTo(null);
		}
	}
	public void changeDateUD() { 
		this.setPeriodUD(null);
	}

	public void changePeriodUDStatus() {
		if (periodUdStatus != null) {
			Date[] di;
			di = DateUtils.calculatePeriod(periodUdStatus);
			srs.setUdValidDateFrom(di[0]);
			srs.setUdValidDateTo( di[1] );		
		} else {
			srs.setUdValidDateFrom(null);
			srs.setUdValidDateTo(null);
		}
	}
	public void changeDateUdStatus() { 
		this.setPeriodUdStatus(null);
	}
	
	public void actionChTypRef() {
		srs.setPredstLice(false); // При смяна от бутона размаркирам Представляващо лице
	}
	
	public void changeObektLice() {
		
	}
	
	/**
	* занулява полетата при смяна на държава
	*/
	public void  actionChangeCountry() {
		srs.setJivObektEkatte(null);
		srs.setFurObektEkatte(null);
		srs.setVlpObektEkatte(null);
	}
	
	/** 
	 *   1 само области; 2 - само общини; 3 - само населени места; без специфики - всикчи
	 * @return
	 */
	public Map<Integer, Object> getSpecificsEKATTE() {
		return Collections.singletonMap(SysClassifAdapter.EKATTE_INDEX_TIP, 3);
	}
	
	public String actionGoto(Integer idVpisvane, Integer licenziantType, Integer codePage) {

		setViewedRows(idVpisvane);
		
		String outcome = "dashboard.xhtml?faces-redirect=true";
		
		
			//Лице - дейности фуражи
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI)) {
				outcome = "regTargovtsiFurajView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			//Лице - дейности ЗЖ
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ)) {
				outcome = "regLicaDeinZJView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
						
			//Лице - дейност ВЛП
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP)) {
				outcome = "regVLPView.xhtml?faces-redirect=true&idV=" + idVpisvane ;
			}
			
			//регситарция на ВЛЗ
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ)) {
				outcome = "regVLZView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
	
			//регситарция на ОЕЗ
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ)) {
				outcome = "regOEZView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
	
			//регситарция на МПС - животни
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ)) {
				outcome = "regMpsZJView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			//регситарция на МПС - фуражи
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS)) {
				outcome = "regMpsFurajView.xhtml?faces-redirect=true&idV=" + idVpisvane;
			}
			
			if (codePage.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP)) {
				outcome = "regObektVLPView.xhtml?faces-redirect=true&idV=" + idVpisvane ;
				// Търговия с ВЛП
			}
		return outcome;
	}
	
	/**************************************************
	 * ЕКСПОРТИ
	 ***********************************************************/
	public void postProcessXLS(Object document) {
		try {

			String title = getMessageResourceString(LABELS, "specReportList.reportTitle");
			new CustomExpPreProcess().postProcessXLS(document, title, null, null, null);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(beanMessages, "general.exception"), e.getMessage());

		}
	}

	public void preProcessPDF(Object document) {
		try {

			String title = getMessageResourceString(LABELS, "specReportList.reportTitle");
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
	

	/** Getters & Setters
	 * */
	public SpecReportSearch getSrs() {
		return srs;
	}


	public void setSrs(SpecReportSearch srs) {
		this.srs = srs;
	}


	public List<SystemClassif> getDocVidList() {
		return docVidList;
	}


	public void setDocVidList(List<SystemClassif> docVidList) {
		this.docVidList = docVidList;
	}


	public Integer getPeriodRegisterZ() {
		return periodRegisterZ;
	}


	public void setPeriodRegisterZ(Integer periodRegisterZ) {
		this.periodRegisterZ = periodRegisterZ;
	}

	public Integer getPeriodStatus() {
		return periodStatus;
	}

	public void setPeriodStatus(Integer periodStatus) {
		this.periodStatus = periodStatus;
	}

	public Integer getPeriodStVpisvane() {
		return periodStVpisvane;
	}

	public void setPeriodStVpisvane(Integer periodStVpisvane) {
		this.periodStVpisvane = periodStVpisvane;
	}

	public Integer getPeriodUD() {
		return periodUD;
	}

	public void setPeriodUD(Integer periodUD) {
		this.periodUD = periodUD;
	}

	public Integer getPeriodUdStatus() {
		return periodUdStatus;
	}

	public void setPeriodUdStatus(Integer periodUdStatus) {
		this.periodUdStatus = periodUdStatus;
	}

	public LazyDataModelSQL2Array getSpecReportList() {
		return specReportList;
	}

	public void setSpecReportList(LazyDataModelSQL2Array specReportList) {
		this.specReportList = specReportList;
	}

	public Integer getViewedRows() {
		return viewedRows;
	}

	public void setViewedRows(Integer viewedRows) {
		this.viewedRows = viewedRows;
	}

	public Integer getRefType() {
		return refType;
	}

	public void setRefType(Integer refType) {
		this.refType = refType;
	}

	public List<SelectItem> getRegistraturiList() {
		return registraturiList;
	}

	public void setRegistraturiList(List<SelectItem> registraturiList) {
		this.registraturiList = registraturiList;
	}

	public int getCountryBG() {
		return countryBG;
	}

	public void setCountryBG(int countryBG) {
		this.countryBG = countryBG;
	}

	public List<SystemClassif> getRegisterSC() {
		return registerSC;
	}

	public void setRegisterSC(List<SystemClassif> registerSC) {
		this.registerSC = registerSC;
	}

	public String getUdValidnost() {
		return udValidnost;
	}

	public void setUdValidnost(String udValidnost) {
		this.udValidnost = udValidnost;
	}

	public List<SystemClassif> getSelectedFarmFormsListSC() {
		return selectedFarmFormsListSC;
	}

	public void setSelectedFarmFormsListSC(List<SystemClassif> selectedFarmFormsListSC) {
		this.selectedFarmFormsListSC = selectedFarmFormsListSC;
	}

	public List<SystemClassif> getSelectedudDocVidListSC() {
		return selectedudDocVidListSC;
	}

	public void setSelectedudDocVidListSC(List<SystemClassif> selectedudDocVidListSC) {
		this.selectedudDocVidListSC = selectedudDocVidListSC;
	}

	public List<SystemClassif> getSelectedvlpVidDeinListSC() {
		return selectedvlpVidDeinListSC;
	}

	public void setSelectedvlpVidDeinListSC(List<SystemClassif> selectedvlpVidDeinListSC) {
		this.selectedvlpVidDeinListSC = selectedvlpVidDeinListSC;
	}

	public List<SystemClassif> getSelectedvlpPredmetPrvnosListSC() {
		return selectedvlpPredmetPrvnosListSC;
	}

	public void setSelectedvlpPredmetPrvnosListSC(List<SystemClassif> selectedvlpPredmetPrvnosListSC) {
		this.selectedvlpPredmetPrvnosListSC = selectedvlpPredmetPrvnosListSC;
	}

	public List<SystemClassif> getSelectedjivVidDeinListSC() {
		return selectedjivVidDeinListSC;
	}

	public void setSelectedjivVidDeinListSC(List<SystemClassif> selectedjivVidDeinListSC) {
		this.selectedjivVidDeinListSC = selectedjivVidDeinListSC;
	}

	public List<SystemClassif> getSelectedjivVidJivotniListSC() {
		return selectedjivVidJivotniListSC;
	}

	public void setSelectedjivVidJivotniListSC(List<SystemClassif> selectedjivVidJivotniListSC) {
		this.selectedjivVidJivotniListSC = selectedjivVidJivotniListSC;
	}

	public Substance getVidIdentifier() {
		return vidIdentifier;
	}

	public void setVidIdentifier(Substance vidIdentifier) {
		this.vidIdentifier = vidIdentifier;
	}

	public List<SystemClassif> getSelectedjivTehnologiaListSC() {
		return selectedjivTehnologiaListSC;
	}

	public void setSelectedjivTehnologiaListSC(List<SystemClassif> selectedjivTehnologiaListSC) {
		this.selectedjivTehnologiaListSC = selectedjivTehnologiaListSC;
	}

	public List<SystemClassif> getSelectedjivPrednJivOezListSC() {
		return selectedjivPrednJivOezListSC;
	}

	public void setSelectedjivPrednJivOezListSC(List<SystemClassif> selectedjivPrednJivOezListSC) {
		this.selectedjivPrednJivOezListSC = selectedjivPrednJivOezListSC;
	}

	public List<SystemClassif> getSelectedfurVidFurajListSC() {
		return selectedfurVidFurajListSC;
	}

	public void setSelectedfurVidFurajListSC(List<SystemClassif> selectedfurVidFurajListSC) {
		this.selectedfurVidFurajListSC = selectedfurVidFurajListSC;
	}

	public List<SystemClassif> getSelectedfurVidJivotniListSC() {
		return selectedfurVidJivotniListSC;
	}

	public void setSelectedfurVidJivotniListSC(List<SystemClassif> selectedfurVidJivotniListSC) {
		this.selectedfurVidJivotniListSC = selectedfurVidJivotniListSC;
	}

	public List<SystemClassif> getSelectedfurVidDeinListSC() {
		return selectedfurVidDeinListSC;
	}

	public void setSelectedfurVidDeinListSC(List<SystemClassif> selectedfurVidDeinListSC) {
		this.selectedfurVidDeinListSC = selectedfurVidDeinListSC;
	}

	public List<SystemClassif> getRegistriOEZ() {
		return registriOEZ;
	}

	public void setRegistriOEZ(List<SystemClassif> registriOEZ) {
		this.registriOEZ = registriOEZ;
	}

	public List<SystemClassif> getRegistriVLP() {
		return registriVLP;
	}

	public void setRegistriVLP(List<SystemClassif> registriVLP) {
		this.registriVLP = registriVLP;
	}

	public List<SystemClassif> getRegistriFur() {
		return registriFur;
	}

	public void setRegistriFur(List<SystemClassif> registriFur) {
		this.registriFur = registriFur;
	}

	public List<SystemClassif> getZaiavOEZ() {
		return zaiavOEZ;
	}

	public void setZaiavOEZ(List<SystemClassif> zaiavOEZ) {
		this.zaiavOEZ = zaiavOEZ;
	}

	public List<SystemClassif> getZaiavVLP() {
		return zaiavVLP;
	}

	public void setZaiavVLP(List<SystemClassif> zaiavVLP) {
		this.zaiavVLP = zaiavVLP;
	}

	public List<SystemClassif> getZaiavFur() {
		return zaiavFur;
	}

	public void setZaiavFur(List<SystemClassif> zaiavFur) {
		this.zaiavFur = zaiavFur;
	}

	public List<SystemClassif> getVidUdDocs() {
		return vidUdDocs;
	}

	public void setVidUdDocs(List<SystemClassif> vidUdDocs) {
		this.vidUdDocs = vidUdDocs;
	}

	public List<SystemClassif> getVidUdDocsFur() {
		return vidUdDocsFur;
	}

	public void setVidUdDocsFur(List<SystemClassif> vidUdDocsFur) {
		this.vidUdDocsFur = vidUdDocsFur;
	}

	public List<SystemClassif> getVidUdDocsVLP() {
		return vidUdDocsVLP;
	}

	public void setVidUdDocsVLP(List<SystemClassif> vidUdDocsVLP) {
		this.vidUdDocsVLP = vidUdDocsVLP;
	}

	public List<SystemClassif> getVlpVidDeinList() {
		return vlpVidDeinList;
	}

	public void setVlpVidDeinList(List<SystemClassif> vlpVidDeinList) {
		this.vlpVidDeinList = vlpVidDeinList;
	}

	public List<SystemClassif> getFurVidDeinList() {
		return furVidDeinList;
	}

	public void setFurVidDeinList(List<SystemClassif> furVidDeinList) {
		this.furVidDeinList = furVidDeinList;
	}

	public List<SystemClassif> getOezVidDeinList() {
		return oezVidDeinList;
	}

	public void setOezVidDeinList(List<SystemClassif> oezVidDeinList) {
		this.oezVidDeinList = oezVidDeinList;
	}

	public String getVidZPrednz() {
		return vidZPrednz;
	}

	public void setVidZPrednz(String vidZPrednz) {
		this.vidZPrednz = vidZPrednz;
	}

	public List<SystemClassif> getSelectedvlpPredmetFarmakolListSC() {
		return selectedvlpPredmetFarmakolListSC;
	}

	public void setSelectedvlpPredmetFarmakolListSC(List<SystemClassif> selectedvlpPredmetFarmakolListSC) {
		this.selectedvlpPredmetFarmakolListSC = selectedvlpPredmetFarmakolListSC;
	}
	
}
