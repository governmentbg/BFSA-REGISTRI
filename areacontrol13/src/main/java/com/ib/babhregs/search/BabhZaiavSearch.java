package com.ib.babhregs.search;

import static com.ib.system.utils.SearchUtils.trimToNULL_Upper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.UserData;
import com.ib.system.BaseSystemData;
import com.ib.system.db.AuditExt;
import com.ib.system.db.DialectConstructor;
import com.ib.system.db.JPA;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.SelectMetadata;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;



/*** ТЪРСЕНЕ НА ПОСТЪПИЛИ ЗАЯВЛЕНИЯ ЗА ВЪВЕЖДАНЕ/АКТУАЛИЗАЦИЯ НА РЕГИСТРИ
 * 
 * @author s.arnaudova
 */

public class BabhZaiavSearch extends SelectMetadata implements AuditExt {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2545764760480622145L;
	
	private static final String AND = " AND ";
	private static final String WHERE = " WHERE ";

	
	/**РЕГИСТЪР***/
	@JournalAttr(label="registerIdList", defaultText = "Регистър", classifID = ""+BabhConstants.CODE_CLASSIF_REGISTRI)
	private List<Integer>	registerIdList;		
	
	
	/**ВИД НА ДОКУМЕНТА***/
	@JournalAttr(label="docVidList",defaultText = "Вид на документа", classifID = ""+ BabhConstants.CODE_CLASSIF_DOC_VID)
	private List<Integer>	docVidList;
	
	
	/**РЕГ.НОМЕР***/
	@JournalAttr(label="rnDoc",defaultText = "Регистрационен номер")
	private String	rnDoc;
	
	
	/**ПЕРИОД НА РЕГИСТРАЦИЯ - ДАТА ОТ***/
	@JournalAttr(label="docDateFrom",defaultText = "Дата на документ - от")
	private Date	docDateFrom;
	
	
	/**ПЕРИОД НА РЕГИСТРАЦИЯ - ДАТА ДО***/
	@JournalAttr(label="docDateTo",defaultText = "Дата на документ - до")
	private Date	docDateTo;
	
	
	/**СТАТУС НА ДОКУМЕНТА***/
	@JournalAttr(label="statusArr",defaultText = "Статус", classifID = ""+ BabhConstants.CODE_CLASSIF_DOC_STATUS)
	private List<Integer>	statusList = new ArrayList<>();	
	
	/**ПЕРИОД НА СТАТУСА - ДАТА ОТ***/
	@JournalAttr(label="statusDateFrom",defaultText = "Дата на статус надокумент - от")
	private Date	statusDateFrom;
	
	
	/**ПЕРИОД НА СТАТУСА - ДАТА ДО***/
	@JournalAttr(label="statusDateTo",defaultText = "Дата на статус на документ - до")
	private Date	statusDateTo;
	
	
	/**РЕГИСТРАТУРА***/
	@JournalAttr(label="registraturaArr",defaultText = "Регистратура", classifID = ""+ BabhConstants.CODE_CLASSIF_REGISTRATURI)
	private List<Integer>	registraturaList = new ArrayList<>();	
	
	/**ОТНОСНО***/
	@JournalAttr(label="otnosno", defaultText = "Относно")
	private String otnosno;
	
	
	private Integer codeRef; // този, за който се търси
	
	private boolean checkAccess = true; // прилагат ли се проверките за достъп

	private StringBuilder sqlSelect ;
	private StringBuilder sqlFrom ;
	private StringBuilder sqlWhere ;
	private Map<String, Object> sqlParams;


	
	/**
	 * [0]- DOC_ID
	 * [1]- RN_DOC
	 * [2]- REGISTRATURA_ID
     * [3]- DOC_VID
     * [4]- DOC_DATE
     * [5]- OTNOSNO
     * [6]- ID_VPISVANE 
	 * @throws DbErrorException 
	 */
	public void buildQueryZaiavList(UserData userData, BaseSystemData systemData) throws DbErrorException {
		if (this.checkAccess && userData.isLimitedAccess()) { // това долното трябва да дойде от екрана, но за всеки случай
			this.codeRef = userData.getUserAccess();
			this.registraturaList = new ArrayList<>();
		}

		String dialect = JPA.getUtil().getDbVendorName();

		sqlParams = new HashMap<>();
		sqlFrom = new StringBuilder();
		sqlSelect = new StringBuilder();
		sqlWhere = new StringBuilder();

		sqlSelect.append("select distinct d.DOC_ID a0, " + DocDAO.formRnDocSelect("d.", dialect)+ " a1, d.REGISTRATURA_ID a2, d.DOC_VID a3, d.DOC_DATE a4, ");
		//sqlSelect.append(DialectConstructor.limitBigString(dialect, "d.OTNOSNO", 300) + " a5, " + " v.ID a6 ");
		//sqlFrom.append(" from DOC d left outer join VPISVANE v on d.DOC_ID = v.ID_ZAQAVLENIE ");
		
		// заради размножаването на вписванията за рег. МПС - много МПС идват с едно заявление!
		sqlSelect.append(DialectConstructor.limitBigString(dialect, "d.OTNOSNO", 300) + " a5, " + " (select min(vd.id_vpisvane)   from vpisvane_doc vd where  vd.id_doc = d.doc_id) a6 ");
		sqlFrom.append(" from DOC d ");
		
		sqlWhere.append("WHERE d.DOC_TYPE = " + BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN);
		
		/** РЕГИСТЪР - МНОЖЕСТВЕН ИЗБОР */
		if (this.registerIdList != null && !this.registerIdList.isEmpty()) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.REGISTER_ID in (:registerIdList) ");
			
			Set<Integer> registerIdSet = new HashSet<>();
			
			for (Integer code : this.registerIdList) {
				if (systemData.matchClassifItems(BabhConstants.CODE_CLASSIF_OBLAST_KONTROL, code, null)) {
					registerIdSet.addAll(systemData.getCodesOnNextLevel(BabhConstants.CODE_CLASSIF_VIDOVE_REGISTRI, code, null, userData.getCurrentLang()));
				} else {
					registerIdSet.add(code);
				}
			}
			sqlParams.put("registerIdList", registerIdSet);
		}
		
		
		/** ВИД ДОКУМЕНТ - МНОЖЕСТВЕН ИЗБОР */
		if (this.docVidList != null && !this.docVidList.isEmpty()) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.DOC_VID in (:docVidList) ");
			sqlParams.put("docVidList", this.docVidList);
		}
		
		
		/** РЕГ.НОМЕР  */
		String t = trimToNULL_Upper(this.rnDoc);
		if (t != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " upper(d.RN_DOC) like :rnDoc ");
			sqlParams.put("rnDoc", "%" + t + "%");
		}
		
		
		/** ПЕРИОД НА РЕГИСТРАЦИЯ НА ДОКУМЕНТА  */
		if (this.docDateFrom != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.DOC_DATE >= :docDateFrom ");
			sqlParams.put("docDateFrom", DateUtils.startDate(this.docDateFrom));
		}
		if (this.docDateTo != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.DOC_DATE <= :docDateTo ");
			sqlParams.put("docDateTo", DateUtils.endDate(this.docDateTo));
		}
		
		
		/** СТАТУС НА ДОКУМЕНТА  */
		if (this.statusList != null && !this.statusList.isEmpty()) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.STATUS in (:statusList) ");
			sqlParams.put("statusList", this.statusList);
		}
		
		/** ПЕРИОД НА СТАТУС НА ДОКУМЕНТА  */
		if (this.statusDateFrom != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.STATUS_DATE >= :statusDateFrom ");
			sqlParams.put("statusDateFrom", DateUtils.startDate(this.statusDateFrom));
		}
		if (this.statusDateTo != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.STATUS_DATE <= :statusDateTo ");
			sqlParams.put("statusDateTo", DateUtils.endDate(this.statusDateTo));
		}

		
		/** РЕГИСТРАТУРА - трябва да може да избира само от позволените му ! */
		if (this.registraturaList != null && !this.registraturaList.isEmpty()) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.REGISTRATURA_ID in (:registraturaArr) ");
			sqlParams.put("registraturaArr", this.registraturaList);
			
		}
		
		/** ОТНОСНО  */
		t = trimToNULL_Upper(this.otnosno);
		if (t != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " upper(d.OTNOSNO) like :otnosno ");
			sqlParams.put("otnosno", "%" + t + "%");
		}
		
		if (this.checkAccess) {
			addAccessRules(userData, systemData);
		}

		super.setSqlCount(" select count(distinct d.DOC_ID) " + sqlFrom.toString() + sqlWhere.toString());
		super.setSql(sqlSelect.toString() + sqlFrom.toString() + sqlWhere.toString());

		setSqlParameters(sqlParams);
	}
	

	/**
	 * слага достъпа
	 * @throws DbErrorException 
	 */
	void addAccessRules(UserData userData, BaseSystemData systemData) throws DbErrorException {
		if (this.registraturaList == null || this.registraturaList.isEmpty() // няма избрани регистратури
				&& !userData.isLimitedAccess()  // няма избрани регистратури и служителят има разширен достъп
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
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.REGISTRATURA_ID in (:allowedRegistraturi) ");
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
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.REGISTER_ID in (:allowedRegistri) ");
			sqlParams.put("allowedRegistri", allowedRegistri);
		}
		
		if (this.codeRef != null) { // налага условие избраният потребител да има регистриран достъп в doc_access
			sqlFrom.append(" inner join doc_access da on da.doc_id = d.doc_id and da.code_ref = :codeRef ");
			sqlParams.put("codeRef", this.codeRef);
		}
	}


	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal dj = new  SystemJournal();				
		dj.setCodeObject(BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
		dj.setIdentObject("Търсене на постъпили заявления за въвеждане/актуализация на регистри");
		return dj;
	}

/*****************************************************************************************************************************************************************/
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

	public Map<String, Object> getSqlParams() {
		return sqlParams;
	}

	public void setSqlParams(Map<String, Object> sqlParams) {
		this.sqlParams = sqlParams;
	}

	public List<Integer> getRegisterIdList() {
		return registerIdList;
	}

	public void setRegisterIdList(List<Integer> registerIdList) {
		this.registerIdList = registerIdList;
	}

	public List<Integer> getDocVidList() {
		return docVidList;
	}

	public void setDocVidList(List<Integer> docVidList) {
		this.docVidList = docVidList;
	}

	public String getRnDoc() {
		return rnDoc;
	}

	public void setRnDoc(String rnDoc) {
		this.rnDoc = rnDoc;
	}

	public Date getDocDateFrom() {
		return docDateFrom;
	}

	public void setDocDateFrom(Date docDateFrom) {
		this.docDateFrom = docDateFrom;
	}

	public Date getDocDateTo() {
		return docDateTo;
	}

	public void setDocDateTo(Date docDateTo) {
		this.docDateTo = docDateTo;
	}

	public Date getStatusDateFrom() {
		return statusDateFrom;
	}

	public void setStatusDateFrom(Date statusDateFrom) {
		this.statusDateFrom = statusDateFrom;
	}

	public Date getStatusDateTo() {
		return statusDateTo;
	}

	public void setStatusDateTo(Date statusDateTo) {
		this.statusDateTo = statusDateTo;
	}

	public List<Integer> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Integer> statusList) {
		this.statusList = statusList;
	}

	public List<Integer> getRegistraturaList() {
		return registraturaList;
	}

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


	public String getOtnosno() {
		return otnosno;
	}


	public void setOtnosno(String otnosno) {
		this.otnosno = otnosno;
	}
}
