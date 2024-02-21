package com.ib.babhregs.search;

import static com.ib.system.utils.SearchUtils.trimToNULL_Upper;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.BaseSystemData;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JPA;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.SelectMetadata;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;

/**
 * Търсене на документи
 *
 * @author belev
 */
public class DocSearch extends SelectMetadata implements AuditExt {
	
	/**  */
	private static final String AND = " and ";
	/**  */
	private static final String WHERE = " where ";

	/**  */
	private static final long serialVersionUID = -8747997444646042641L;

	@JournalAttr(label="codeRefCorresp",defaultText = "Кореспондент",classifID = ""+BabhConstants.CODE_CLASSIF_REFERENTS)
	private Integer	codeRefCorresp;	// по код от класификацията
	
	@JournalAttr(label="rnDoc",defaultText = "Регистрационен номер")
	private String	rnDoc;
	
	@JournalAttr(label="rnDocEQ",defaultText = "Регистрационен номер (пълно съвпадение)")
	private boolean	rnDocEQ = true;	// ако е true се търси по пълно съвпадение по номер на документ

	// период на дата на документа
	@JournalAttr(label="docDateFrom",defaultText = "Дата на документ - от")
	private Date	docDateFrom;
	
	@JournalAttr(label="docDateTo",defaultText = "Дата на документ - до")
	private Date	docDateTo;
	
	private String triCheckPlateno = "2";
	
	/**ВИД НА ДОКУМЕНТА***/
	@JournalAttr(label="docVidList",defaultText = "Вид на документа", classifID = ""+ BabhConstants.CODE_CLASSIF_DOC_VID)
	private List<Integer>	docVidList;

	private StringBuilder sqlSelect ;
	private StringBuilder sqlFrom ;
	private StringBuilder sqlWhere ;
	private Map<String, Object> sqlParams;
	
	//Това са малко изкуствени атрибути, за да не се преписва рефлекшъна за супер класовете
	
	// като са с еднакви имена няма да го има 2 пъти в xml-a
	@JournalAttr(label="sql",defaultText = "SQL зявка")
	private String	sql;
	@JournalAttr(label="sqlCount",defaultText = "SQL зявка за брой")
	private String	sqlCount;
	
	/** */
	public DocSearch() {
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
	
	/** @return the codeRefCorresp */
	public Integer getCodeRefCorresp() {
		return this.codeRefCorresp;
	}

	/** @return the docDateFrom */
	public Date getDocDateFrom() {
		return this.docDateFrom;
	}

	/** @return the docDateTo */
	public Date getDocDateTo() {
		return this.docDateTo;
	}

	/** @return the rnDoc */
	public String getRnDoc() {
		return this.rnDoc;
	}

	/** @return the rnDocEQ */
	public boolean isRnDocEQ() {
		return this.rnDocEQ;
	}

	/** @param codeRefCorresp the codeRefCorresp to set */
	public void setCodeRefCorresp(Integer codeRefCorresp) {
		this.codeRefCorresp = codeRefCorresp;
	}

	/** @param docDateFrom the docDateFrom to set */
	public void setDocDateFrom(Date docDateFrom) {
		this.docDateFrom = docDateFrom;
	}

	/** @param docDateTo the docDateTo to set */
	public void setDocDateTo(Date docDateTo) {
		this.docDateTo = docDateTo;
	}

	/** @param rnDoc the rnDoc to set */
	public void setRnDoc(String rnDoc) {
		this.rnDoc = rnDoc;
	}

	/** @param rnDocEQ the rnDocEQ to set */
	public void setRnDocEQ(boolean rnDocEQ) {
		this.rnDocEQ = rnDocEQ;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal dj = new  SystemJournal();				
		dj.setCodeObject(BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
		//dj.setIdObject(getId());
		//dj.setIdentObject("Търсене на документи");
		return dj;
	}

	public void buildQueryUnpaidDocs(UserData userData) throws DbErrorException {
		String dialect = JPA.getUtil().getDbVendorName();

		sqlParams = new HashMap<>();

		sqlFrom = new StringBuilder();
		sqlSelect = new StringBuilder();
		sqlWhere = new StringBuilder();


		String firstCol = "d.DOC_ID";

		sqlSelect.append("select distinct "+firstCol+" a0, "+DocDAO.formRnDocSelect("d.", dialect)+" a1, d.DOC_TYPE a2, d.DOC_VID a3, d.DOC_DATE a4, zaiavitel.code a5, z.object_id a6 ");
		sqlSelect.append(" , z.user_id a7 , z.lock_date a8 , d.pay_amount a9 ");
		sqlSelect.append(" , case  ");
		sqlSelect.append(		" when zaiavitel.nfl_eik is not null then zaiavitel.nfl_eik ");
		sqlSelect.append(		" when zaiavitel.fzl_egn is not null then zaiavitel.fzl_egn ");
		sqlSelect.append("	end a10 ");
		sqlSelect.append(", zaiavitel.ref_name a11 ");
		sqlSelect.append(", d.plateno  a12");
		sqlSelect.append(", d.pay_date a13 ");
		sqlSelect.append(", d.pay_amount_real a14 ");
		
		sqlFrom.append(" from 	DOC d left join vpisvane on d.doc_id = vpisvane.id_zaqavlenie ");
		sqlFrom.append(" left join adm_referents zaiavitel on vpisvane.code_ref_vpisvane = zaiavitel.code ");
		sqlFrom.append(" left outer join LOCK_OBJECTS z on z.OBJECT_TIP = 51 and z.OBJECT_ID = d.DOC_ID");
		
		sqlWhere.append(" where ( d.status = "); sqlWhere.append(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN); 
//				sqlWhere.append(" or d.status = ");  sqlWhere.append(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_WAIT); 
					sqlWhere.append(" ) ");

		if(triCheckPlateno.equals("2"))
			sqlWhere.append(" and ( d.plateno = 2 or d.plateno is null ) ");
		if(triCheckPlateno.equals("1"))
			sqlWhere.append(" and d.plateno = 1 ");
		
		String t = trimToNULL_Upper(this.rnDoc);
		if (t != null) {
			if (this.rnDocEQ) { // пълно съвпадение case insensitive
				sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " upper(d.RN_DOC) = :rnDoc ");
				sqlParams.put("rnDoc", t);

			} else {
				sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " upper(d.RN_DOC) like :rnDoc ");
				sqlParams.put("rnDoc", "%" + t + "%");
			}
		}
		if(docVidList != null && !docVidList.isEmpty()) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.doc_vid in (:registerIdList) ");
			sqlParams.put("registerIdList", docVidList);
		}
		
		if (this.docDateFrom != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.DOC_DATE >= :docDateFrom ");
			sqlParams.put("docDateFrom", DateUtils.startDate(this.docDateFrom));
		}
		if (this.docDateTo != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.DOC_DATE <= :docDateTo ");
			sqlParams.put("docDateTo", DateUtils.endDate(this.docDateTo));
		}
		if (this.codeRefCorresp != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " zaiavitel.code = :codeRefCorresp ");
			sqlParams.put("codeRefCorresp", this.codeRefCorresp);
		}
		
		// трябва да се филтрира само по позволените му регистратури
		Map<Integer, Boolean> allowed = userData.getAccessValues().get(BabhConstants.CODE_CLASSIF_REGISTRATURI);
		Set<Integer> allowedRegistraturi;
		if (allowed == null || allowed.isEmpty()) { // не би трябвало, но все пак
			allowedRegistraturi = new HashSet<>();
			allowedRegistraturi.add(((UserData) userData).getRegistratura());
		} else { // каквото е дефинирано
			allowedRegistraturi = allowed.keySet();
		}
		sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.registratura_id in (:allowedRegistraturi) ");
		sqlParams.put("allowedRegistraturi", allowedRegistraturi);

		super.setSqlCount(" select count(distinct "+firstCol+") " + sqlFrom.toString() + sqlWhere.toString());
		
		super.setSql(sqlSelect.toString() + sqlFrom.toString() + sqlWhere.toString());

		setSqlParameters(sqlParams);
		
	}

	public String getTriCheckPlateno() {
		return triCheckPlateno;
	}

	public void setTriCheckPlateno(String triCheckPlateno) {
		this.triCheckPlateno = triCheckPlateno;
	}

	public List<Integer> getDocVidList() {
		return docVidList;
	}

	public void setDocVidList(List<Integer> docVidList) {
		this.docVidList = docVidList;
	}
	
	public BaseSystemData getSystemData() {
		return (BaseSystemData) JSFUtils.getManagedBean("systemData");
	}
}