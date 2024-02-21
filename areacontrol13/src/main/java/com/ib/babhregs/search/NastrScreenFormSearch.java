package com.ib.babhregs.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.BaseSystemData;
import com.ib.system.BaseUserData;
import com.ib.system.db.AuditExt;

import com.ib.system.db.JournalAttr;
import com.ib.system.db.SelectMetadata;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;



/*** ТЪРСЕНЕ НА НАСТРОЙКИ НА ЕКРАННИ ФОРМИ
 * 
 * @author s.arnaudova
 */

public class NastrScreenFormSearch extends SelectMetadata implements AuditExt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2556132705308079298L;
	
	private static final String AND = " AND ";
	private static final String WHERE = " WHERE ";
	
	private StringBuilder sqlSelect ;
	private StringBuilder sqlFrom ;
	private StringBuilder sqlWhere ;
	private Map<String, Object> sqlParams;
	
	/** ИД НА РЕГИСТЪР ***/
	@JournalAttr(label="registerId", defaultText = "ID Регистър", classifID = "" + BabhConstants.CODE_CLASSIF_VID_REGISTRI)
	private Integer	registerId;	
	
	/** ТИП НА ОБЕКТ ***/
	@JournalAttr(label="objType", defaultText = "Тип на обект", classifID = "" + BabhConstants.CODE_CLASSIF_TIP_OBEKT_LICENZ)
	private Integer	objType;	
	

	/** КОД НА УСЛУГА ***/
	@JournalAttr(label="meuNumber", defaultText = "Код на услуга")
	private String	meuNumber;	
	
	

	public void buildQueryFormsList(BaseUserData userData, BaseSystemData systemData) throws DbErrorException {

		sqlParams = new HashMap<>();
		sqlFrom = new StringBuilder();
		sqlSelect = new StringBuilder();
		sqlWhere = new StringBuilder();

		sqlSelect.append("select distinct r.id a0, r.register_id a1, r.object_type a2");
		sqlFrom.append(" from register_options r join register_options_docs_in d on r.id = d.register_options_id");
		
	
		if (this.registerId != null) {
			//слава всички заявления в съответната област на контрол
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " r.register_id in (:registerIdList) ");
			
			Set<Integer> registerIdSet = new HashSet<>();
			
			if (systemData.matchClassifItems(BabhConstants.CODE_CLASSIF_OBLAST_KONTROL, this.registerId, null)) {
				registerIdSet.addAll(systemData.getCodesOnNextLevel(BabhConstants.CODE_CLASSIF_VIDOVE_REGISTRI, this.registerId, null, userData.getCurrentLang()));
			} else {
				registerIdSet.add(this.registerId);
			}
			sqlParams.put("registerIdList", registerIdSet);
		}

		
		if (this.objType != null) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " r.object_type = :objType ");
			sqlParams.put("objType", this.objType);
		}
		
		if (this.meuNumber != null && !SearchUtils.isEmpty(this.meuNumber)) {
			sqlWhere.append((sqlWhere.length() == 0 ? WHERE : AND) + " d.meu_nimber = :meuNumber ");
			sqlParams.put("meuNumber", this.meuNumber);
		}



		super.setSqlCount(" select count(distinct r.id) " + sqlFrom.toString() + sqlWhere.toString());
		super.setSql(sqlSelect.toString() + sqlFrom.toString() + sqlWhere.toString());

		setSqlParameters(sqlParams);
	}
	
	
	
	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal dj = new  SystemJournal();				
		dj.setCodeObject(BabhConstants.CODE_ZNACHENIE_JOURNAL_NAST_OBJECT);
		dj.setIdentObject("Търсене на настройки на екранни форми");
		return dj;
	}



	public Integer getRegisterId() {
		return registerId;
	}

	public void setRegisterId(Integer registerId) {
		this.registerId = registerId;
	}

	public Integer getObjType() {
		return objType;
	}

	public void setObjType(Integer objType) {
		this.objType = objType;
	}



	public String getMeuNumber() {
		return meuNumber;
	}



	public void setMeuNumber(String meuNumber) {
		this.meuNumber = meuNumber;
	}


}
