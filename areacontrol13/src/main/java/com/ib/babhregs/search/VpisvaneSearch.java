package com.ib.babhregs.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.UserData;
import com.ib.system.BaseSystemData;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.SelectMetadata;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;

/**
 * Търсене на документи
 *
 * @author Banksy 
 */
public class VpisvaneSearch extends SelectMetadata implements AuditExt {
	
	/**  */
	private static final String AND = " and ";
	/**  */
	private static final String WHERE = " where ";
	
	@JournalAttr(label="sql",defaultText = "SQL зявка")
	private String	sql;
	@JournalAttr(label="sqlCount",defaultText = "SQL зявка за брой")
	private String	sqlCount;
	
	private StringBuilder sqlSelect ;
	private StringBuilder sqlFrom ;
	private StringBuilder sqlWhere ;
	private Map<String, Object> sqlParams;

	/**  */
	private static final long serialVersionUID = -8747997555646042642L;

	/**РЕГИСТРАТУРА***/
	@JournalAttr(label="registraturaArr",defaultText = "Регистратура", classifID = ""+ BabhConstants.CODE_CLASSIF_REGISTRATURI)
	private List<Integer>	registraturaList = new ArrayList<>();	

	@JournalAttr(label="registerId",defaultText = "Регистър",classifID = ""+BabhConstants.CODE_CLASSIF_REGISTRI)
	private Integer			registerId;
	
	@JournalAttr(label="status", defaultText = "Статус",classifID = ""+BabhConstants.CODE_CLASSIF_STATUS_VPISVANE)
	private Integer	status;
	
	@JournalAttr(label="statusArr",defaultText = "Статус", classifID = ""+ BabhConstants.CODE_CLASSIF_STATUS_VPISVANE)
	private List<Integer>	statusList;	// статус

	@JournalAttr(label="statDateFrom",defaultText = "Дата на статус - от", dateMask = "dd.MM.yyyy")
	private Date	statDateFrom;
	
	@JournalAttr(label="statDateTo",defaultText = "Дата на документ - до", dateMask = "dd.MM.yyyy")
	private Date	statDateTo;
	
	@JournalAttr(label="codeRefVpisvane",defaultText = "Системен идентификато на завител на вписване")
	private Integer	codeRefVpisvane;
	
	@JournalAttr(label="idLicenziant ",defaultText = "Системен идентификато на завител на лицензиант")
	private Integer	idLicenziant;
	
	@JournalAttr(label="licenziantType", defaultText = "Тип на обекта за лицензиране",classifID = ""+BabhConstants.CODE_CLASSIF_TIP_OBEKT_LICENZ)
	private Integer	licenziantType;	
	
	@JournalAttr(label="regNomZaiav",defaultText = "Номер на заявление за регистрация")
	private String	regNomZaiav;
	
	@JournalAttr(label="zaiavDateFrom",defaultText = "Дата на заявление за регистрация - от", dateMask = "dd.MM.yyyy")
	private Date	zaiavDateFrom;
	
	@JournalAttr(label="zaiavDateTo",defaultText = "Дата на заявление за регистрация - до", dateMask = "dd.MM.yyyy")
	private Date	zaiavDateTo;
	
	@JournalAttr(label="regNomResult",defaultText = "Номер на удостоверителен документ")
	private String	regNomResult;
	
	@JournalAttr(label="resultDateFrom",defaultText = "Дата на издавне на удост. документ - от", dateMask = "dd.MM.yyyy")
	private Date	resultDateFrom;
	
	@JournalAttr(label="resultDateТо",defaultText = "Дата на издавне на удост. документ - до", dateMask = "dd.MM.yyyy")
	private Date	resultDateTo;
	
	@JournalAttr(label="licenziant",defaultText = "Лицензиант")
	private String licenziant;

	@JournalAttr(label="licenziantType", defaultText = "Тип на обекта за лицензиране",classifID = ""+BabhConstants.CODE_CLASSIF_TIP_OBEKT_LICENZ)
	private Integer	codePage;
	
	
	private String			rnDocZaiav;				
	private Date			docDateFromZaiav;		
	private Date			docDateToZaiav;
	
	@JournalAttr(label="docVidList",defaultText = "Вид на документа", classifID = ""+ BabhConstants.CODE_CLASSIF_DOC_VID)
	private List<Integer>	docVidList;

	private String			rnDocUdost;				
	private Date			docDateFromUdost;		
	private Date			docDateToUdost;
	
	private Boolean onlyActiveProc; // true - само за активни процедури по документи на вписването
	
	private Integer codeRef; // този, за който се търси

	private boolean checkAccess = true; // прилагат ли се проверките за достъп

	/** */
	public VpisvaneSearch() {
		super();
	}

	/** */
	@Override
	public void setSql(String sql) {
		this.sql = sql;
		super.setSql(sql);
	}
	/** */
	@Override
	public void setSqlCount(String sqlCount) {
		this.sqlCount = sqlCount;
		super.setSqlCount(sqlCount);
	}
	

	


	/**
	 * [0] = id = на вписването,
	 * [1] = v.id_register = ,
	 * [2] = v.code_ref_vpisvane = ,
	 * [3] = zaiavitel.fzl_egn = ЕГН на заявител,
	 * [4] = zaiavitel.fzl_ln_es = Личен номер на чужденец от ЕС на заявител,
	 * [5] = zaiavitel.fzl_lnc = Личен номер на чужденец на заявител,
	 * [6] = zaiavitel.ref_name = Име на заявител,
	 * [7] = zaiavitel.nfl_eik = ,
	 * [8] = v.status,
	 * [9] = v.date_status,
	 * [10] = v.reg_nom_result,
	 * [11] = v.date_result,
	 * [12] = v.licenziant,
	 * [13] = licenziant_type = за списъка ми трябва,
	 * [14] = ,
	 * [15] = id_zaqavlenie = за списъка ми трябва
	 * [16] = code_page = bivat 7, 8, 9 ...
	 * [17] = reg_nom_zaqvl_vpisvane = за списъка ми трябва
	 * [18] = date_zaqvl_vpis = за списъка ми трябва
	 * [19] = регистратура
	 * [20] = вид на документа
	 * [21] = заключено да/не
	 * @throws DbErrorException 

	 * 
	 * */
	public void buildQueryVpisvaneList(UserData userData, BaseSystemData systemData) throws DbErrorException {
		if (this.checkAccess && userData.isLimitedAccess()) { // това долното трябва да дойде от екрана, но за всеки случай
			this.codeRef = userData.getUserAccess();
			this.registraturaList = new ArrayList<>();
		}

		//String dialect = JPA.getUtil().getDbVendorName();

		sqlParams = new HashMap<>();

		sqlFrom = new StringBuilder();
		sqlSelect = new StringBuilder();
		sqlWhere = new StringBuilder();


		
		
		sqlSelect.append("select distinct v.id, v.id_register, v.code_ref_vpisvane, zaiavitel.fzl_egn, zaiavitel.fzl_ln_es, zaiavitel.fzl_lnc, zaiavitel.ref_name , zaiavitel.nfl_eik, "
				+ "v.status, v.date_status, v.reg_nom_result, v.date_result, v.licenziant, v.licenziant_type, v.id_licenziant, "
				+ "v.id_zaqavlenie, v.code_page , v.reg_nom_zaqvl_vpisvane , v.date_zaqvl_vpis, v.registratura_id, d.doc_vid, v.vp_locked ");
		

		sqlFrom.append(" from vpisvane v left outer join adm_referents zaiavitel on v.code_ref_vpisvane = zaiavitel.code ");
		sqlFrom.append(" left outer join doc d on v.id_zaqavlenie = d.doc_id ");

		

		if (status != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.status = :status ");
			sqlParams.put("status", status);
		}
		if (this.statusList != null && !this.statusList.isEmpty()) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.status in (:statusList) ");
			sqlParams.put("statusList", this.statusList);
		}

		if (registerId != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.id_register in (:registerIdList) ");
			
			Set<Integer> registerIdSet = new HashSet<>();
			
			if (systemData.matchClassifItems(BabhConstants.CODE_CLASSIF_OBLAST_KONTROL, this.registerId, null)) {
				registerIdSet.addAll(systemData.getCodesOnNextLevel(BabhConstants.CODE_CLASSIF_VIDOVE_REGISTRI, this.registerId, null, userData.getCurrentLang()));
			} else {
				registerIdSet.add(this.registerId);
			}
			sqlParams.put("registerIdList", registerIdSet);
		}

		if (this.registraturaList != null && !this.registraturaList.isEmpty()) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.registratura_id in (:registraturaArr) ");
			sqlParams.put("registraturaArr", this.registraturaList);
		}
		
		if (this.statDateFrom != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.date_status >= :statDateFrom ");
			sqlParams.put("statDateFrom", DateUtils.startDate(this.statDateFrom));
		}
		if (this.statDateTo != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.date_status <= :statDateTo ");
			sqlParams.put("statDateTo", DateUtils.endDate(this.statDateTo));
		}
		
		
		if (this.resultDateFrom != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.date_result >= :resultDateFrom ");
			sqlParams.put("resultDateFrom", DateUtils.startDate(this.resultDateFrom));
		}
		if (this.resultDateTo != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.date_result <= :resultDateTo ");
			sqlParams.put("resultDateTo", DateUtils.endDate(this.resultDateTo));
		}
		
		
		if (codeRefVpisvane != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.code_ref_vpisvane = :codeRefVpisvane ");
			sqlParams.put("codeRefVpisvane", codeRefVpisvane);
		}
		
		if (idLicenziant != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.id_licenziant = :idLicenziant ");
			sqlParams.put("idLicenziant", idLicenziant);
		}
		
		if (regNomResult != null && !regNomResult.trim().isEmpty()) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " upper(v.reg_nom_result) = :regNomResult ");
			sqlParams.put("regNomResult", regNomResult.trim().toUpperCase());
		}
		
		
		if (this.zaiavDateFrom != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.date_zaqvl_vpis >= :zaiavDateFrom ");
			sqlParams.put("zaiavDateFrom", DateUtils.startDate(this.zaiavDateFrom));
		}
		if (this.zaiavDateTo != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.date_zaqvl_vpis <= :zaiavDateTo ");
			sqlParams.put("zaiavDateTo", DateUtils.endDate(this.zaiavDateTo));
		}
		
		if (regNomZaiav != null && !regNomZaiav.trim().isEmpty()) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " upper(v.reg_nom_zaqvl_vpisvane) = :regNomZaiav ");
			sqlParams.put("regNomZaiav", regNomZaiav.trim().toUpperCase());
		}
		
		if (licenziantType != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.licenziant_type = :licenziantType ");
			sqlParams.put("licenziantType", licenziantType);
		}
		
		
		
		boolean joinAllZaiav = false;
		if (rnDocZaiav != null && !rnDocZaiav.trim().isEmpty()) {
			joinAllZaiav = true;
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " upper(allzaiav.rn_doc) = :rnDocZaiav ");
			sqlParams.put("rnDocZaiav", rnDocZaiav.trim().toUpperCase());
		}
		if (this.docDateFromZaiav != null) {
			joinAllZaiav = true;
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " allzaiav.doc_date >= :docDateFromZaiav ");
			sqlParams.put("docDateFromZaiav", DateUtils.startDate(this.docDateFromZaiav));
		}
		if (this.docDateToZaiav != null) {
			joinAllZaiav = true;
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " allzaiav.doc_date <= :docDateToZaiav ");
			sqlParams.put("docDateToZaiav", DateUtils.endDate(this.docDateToZaiav));
		}
		if (this.docVidList != null && !this.docVidList.isEmpty()) {
			joinAllZaiav = true;
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " allzaiav.doc_vid in (:docVidList) ");
			sqlParams.put("docVidList", this.docVidList);
		}
		if (joinAllZaiav) {
			sqlFrom.append(" left outer join vpisvane_doc vdzaiav on vdzaiav.id_vpisvane = v.id ");
			sqlFrom.append(" left outer join doc allzaiav on allzaiav.doc_id = vdzaiav.id_doc and allzaiav.doc_type=1 ");
		}
		
		boolean joinAllUdost = false;
		if (rnDocUdost != null && !rnDocUdost.trim().isEmpty()) {
			joinAllUdost = true;
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " upper(alludost.rn_doc) = :rnDocUdost ");
			sqlParams.put("rnDocUdost", rnDocUdost.trim().toUpperCase());
		}
		if (this.docDateFromUdost != null) {
			joinAllUdost = true;
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " alludost.doc_date >= :docDateFromUdost ");
			sqlParams.put("docDateFromUdost", DateUtils.startDate(this.docDateFromUdost));
		}
		if (this.docDateToUdost != null) {
			joinAllUdost = true;
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " alludost.doc_date <= :docDateToUdost ");
			sqlParams.put("docDateToUdost", DateUtils.endDate(this.docDateToUdost));
		}
		if (joinAllUdost) {
			sqlFrom.append(" left outer join vpisvane_doc vdudost on vdudost.id_vpisvane = v.id ");
			sqlFrom.append(" left outer join doc alludost on alludost.doc_id = vdudost.id_doc and alludost.doc_type=2 ");
		}
		
		
		if (licenziant != null && !licenziant.trim().isEmpty()) {
			
			String searchString = "";
			 String words[] = licenziant.split(" ");
			 boolean isFirst = true;
			 for (String word : words) {
				 word = word.trim();
				 if (word.endsWith("*")) {
					 word = word.substring(0, word.length()-2) + ":*";
				 }
				 
				 if (word.startsWith("*")) {
					 //word = word.substring(0, word.length()-2) + ":*";
				 }
				 
				 if (isFirst) {
					 searchString = word;
				 }else {
					 searchString += " & " + word;
				 }
				 
				 isFirst = false;
			 }
			
			
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " to_tsvector('bulgarian',v.licenziant) @@ to_tsquery('bulgarian','"+searchString+"')");
			
		}

		if (Boolean.TRUE.equals(this.onlyActiveProc)) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " EXISTS ( ");
			sqlWhere.append(" select 1 from proc_exe proc ");
			sqlWhere.append(" inner join vpisvane_doc docproc on docproc.id_doc = proc.doc_id ");
			sqlWhere.append(" where docproc.id_vpisvane = v.id and proc.status = :procStatus ");
			sqlWhere.append(" ) ");
			sqlParams.put("procStatus", BabhConstants.CODE_ZNACHENIE_PROC_STAT_EXE);
		}

		if (this.checkAccess) {
			addAccessRules(userData, systemData);
		}
		
		
		setSqlCount("select count(distinct v.id) " + sqlFrom.toString() + sqlWhere.toString());
		
		
		setSql(sqlSelect.toString() + sqlFrom.toString() + sqlWhere.toString());

		setSqlParameters(sqlParams);
	}
	
	

	

	/**
	 * слага достъпа
	 * @throws DbErrorException 
	 */
	void addAccessRules(UserData userData, BaseSystemData systemData) throws DbErrorException {
		if (this.registraturaList == null || this.registraturaList.isEmpty() // няма избрани регистратури
				&& !userData.isLimitedAccess()  // и служителят има разширен достъп
				&& (this.codeRef == null || !this.codeRef.equals(userData.getUserAccess()))) { // няма избран служител, или има, но е за някой друг
			
			// трябва да се филтрира само по позволените му регистратури
			Map<Integer, Boolean> allowed = userData.getAccessValues().get(BabhConstants.CODE_CLASSIF_REGISTRATURI);
			
			Set<Integer> allowedRegistraturi;
			if (allowed == null || allowed.isEmpty()) { // не би трябвало, но все пак
				allowedRegistraturi = new HashSet<>();
				allowedRegistraturi.add(userData.getRegistratura());

			} else { // каквото е дефинирано
				allowedRegistraturi = allowed.keySet();
			}
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.registratura_id in (:allowedRegistraturi) ");
			sqlParams.put("allowedRegistraturi", allowedRegistraturi);
		}
		
		if (!userData.isLimitedAccess()  // служителят има разширен достъп
				&& (this.codeRef == null || !this.codeRef.equals(userData.getUserAccess()))) { // няма избран служител, или има, но е за някой друг
			Map<Integer, Boolean> allowed = userData.getAccessValues().get(BabhConstants.CODE_CLASSIF_OBLAST_KONTROL);

			// само от позволените области на котрол
			Set<Integer> allowedRegistri;
			if (allowed == null || allowed.isEmpty()) { // не би трябвало, но все пак
				allowedRegistri = new HashSet<>();
				allowedRegistri.add(Integer.MIN_VALUE); // така няма да гърми, но и няма да върне нищо

			} else { // каквото е дефинирано
				allowedRegistri = new HashSet<>();
				for (Integer oblast : allowed.keySet()) {
					allowedRegistri.addAll(systemData.getCodesOnNextLevel(BabhConstants.CODE_CLASSIF_VIDOVE_REGISTRI, oblast, null, userData.getCurrentLang()));
				}
				
			}
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " v.id_register in (:allowedRegistri) ");
			sqlParams.put("allowedRegistri", allowedRegistri);
		}

		if (this.codeRef != null) { // налага условие избраният потребител да има регистриран достъп в doc_access
			sqlFrom.append(" inner join vpisvane_access va on va.vpisvane_id = v.id and va.code_ref = :codeRef ");
			sqlParams.put("codeRef", this.codeRef);
		}
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal dj = new  SystemJournal();				
		dj.setCodeObject(BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
//		dj.setIdObject(getId());
//		dj.setIdentObject("Търсене на документи");
		return dj;
	}

	public StringBuilder getSqlSelect() {
		return sqlSelect;
	}

	public void setSqlSelect(StringBuilder sqlSelect) {
		this.sqlSelect = sqlSelect;
	}

	public StringBuilder getSqlFrom() {
		return sqlFrom;
	}

	public void setSqlFrom(StringBuilder sqlFrom) {
		this.sqlFrom = sqlFrom;
	}

	public StringBuilder getSqlWhere() {
		return sqlWhere;
	}

	public void setSqlWhere(StringBuilder sqlWhere) {
		this.sqlWhere = sqlWhere;
	}


	public Integer getRegisterId() {
		return registerId;
	}

	public void setRegisterId(Integer registerId) {
		this.registerId = registerId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getStatDateFrom() {
		return statDateFrom;
	}

	public void setStatDateFrom(Date statDateFrom) {
		this.statDateFrom = statDateFrom;
	}

	public Date getStatDateTo() {
		return statDateTo;
	}

	public void setStatDateTo(Date statDateTo) {
		this.statDateTo = statDateTo;
	}

	public Integer getCodeRefVpisvane() {
		return codeRefVpisvane;
	}

	public void setCodeRefVpisvane(Integer codeRefVpisvane) {
		this.codeRefVpisvane = codeRefVpisvane;
	}

	public Integer getIdLicenziant() {
		return idLicenziant;
	}

	public void setIdLicenziant(Integer idLicenziant) {
		this.idLicenziant = idLicenziant;
	}

	public String getRegNomResult() {
		return regNomResult;
	}

	public void setRegNomResult(String regNomResult) {
		this.regNomResult = regNomResult;
	}

	public Date getResultDateFrom() {
		return resultDateFrom;
	}

	public void setResultDateFrom(Date resultDateFrom) {
		this.resultDateFrom = resultDateFrom;
	}

	public Date getResultDateTo() {
		return resultDateTo;
	}

	public void setResultDateTo(Date resultDateTo) {
		this.resultDateTo = resultDateTo;
	}

	public String getSql() {
		return sql;
	}

	public String getSqlCount() {
		return sqlCount;
	}
	
	
	public Integer getLicenziantType() {
		return licenziantType;
	}

	public void setLicenziantType(Integer licenziantType) {
		this.licenziantType = licenziantType;
	}

	public String getRegNomZaiav() {
		return regNomZaiav;
	}

	public void setRegNomZaiav(String regNomZaiav) {
		this.regNomZaiav = regNomZaiav;
	}

	public Date getZaiavDateFrom() {
		return zaiavDateFrom;
	}

	public void setZaiavDateFrom(Date zaiavDateFrom) {
		this.zaiavDateFrom = zaiavDateFrom;
	}

	public Date getZaiavDateTo() {
		return zaiavDateTo;
	}

	public void setZaiavDateTo(Date zaiavDateTo) {
		this.zaiavDateTo = zaiavDateTo;
	}

	public String getLicenziant() {
		return licenziant;
	}

	public void setLicenziant(String licenziant) {
		this.licenziant = licenziant;
	}

	public Integer getCodePage() {
		return codePage;
	}

	public void setCodePage(Integer codePage) {
		this.codePage = codePage;
	}

	/** @return the statusList */
	public List<Integer> getStatusList() {
		return this.statusList;
	}

	/** @param statusList the statusList to set */
	public void setStatusList(List<Integer> statusList) {
		this.statusList = statusList;
	}

	/** @return the registraturaList */
	public List<Integer> getRegistraturaList() {
		return this.registraturaList;
	}
	/** @param registraturaList the registraturaList to set */
	public void setRegistraturaList(List<Integer> registraturaList) {
		this.registraturaList = registraturaList;
	}

	/** @return the codeRef */
	public Integer getCodeRef() {
		return this.codeRef;
	}
	/** @param codeRef the codeRef to set */
	public void setCodeRef(Integer codeRef) {
		this.codeRef = codeRef;
	}

	/** @return the checkAccess */
	public boolean isCheckAccess() {
		return this.checkAccess;
	}
	/** @param checkAccess the checkAccess to set */
	public void setCheckAccess(boolean checkAccess) {
		this.checkAccess = checkAccess;
	}

	public List<Integer> getDocVidList() {
		return docVidList;
	}

	public void setDocVidList(List<Integer> docVidList) {
		this.docVidList = docVidList;
	}

	/** @return the rnDocZaiav */
	public String getRnDocZaiav() {
		return this.rnDocZaiav;
	}
	/** @param rnDocZaiav the rnDocZaiav to set */
	public void setRnDocZaiav(String rnDocZaiav) {
		this.rnDocZaiav = rnDocZaiav;
	}
	/** @return the docDateFromZaiav */
	public Date getDocDateFromZaiav() {
		return this.docDateFromZaiav;
	}
	/** @param docDateFromZaiav the docDateFromZaiav to set */
	public void setDocDateFromZaiav(Date docDateFromZaiav) {
		this.docDateFromZaiav = docDateFromZaiav;
	}
	/** @return the docDateToZaiav */
	public Date getDocDateToZaiav() {
		return this.docDateToZaiav;
	}
	/** @param docDateToZaiav the docDateToZaiav to set */
	public void setDocDateToZaiav(Date docDateToZaiav) {
		this.docDateToZaiav = docDateToZaiav;
	}
	/** @return the rnDocUdost */
	public String getRnDocUdost() {
		return this.rnDocUdost;
	}
	/** @param rnDocUdost the rnDocUdost to set */
	public void setRnDocUdost(String rnDocUdost) {
		this.rnDocUdost = rnDocUdost;
	}
	/** @return the docDateFromUdost */
	public Date getDocDateFromUdost() {
		return this.docDateFromUdost;
	}
	/** @param docDateFromUdost the docDateFromUdost to set */
	public void setDocDateFromUdost(Date docDateFromUdost) {
		this.docDateFromUdost = docDateFromUdost;
	}
	/** @return the docDateToUdost */
	public Date getDocDateToUdost() {
		return this.docDateToUdost;
	}
	/** @param docDateToUdost the docDateToUdost to set */
	public void setDocDateToUdost(Date docDateToUdost) {
		this.docDateToUdost = docDateToUdost;
	}

	/** @return the onlyActiveProc */
	public Boolean getOnlyActiveProc() {
		return this.onlyActiveProc;
	}
	/** @param onlyActiveProc the onlyActiveProc to set */
	public void setOnlyActiveProc(Boolean onlyActiveProc) {
		this.onlyActiveProc = onlyActiveProc;
	}
}
