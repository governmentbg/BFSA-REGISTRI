package com.ib.babhregs.db.dao;

import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_PROC_DEF_STAT_ACTIVE;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_PROC_STAT_EXE;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_PROC_STAT_IZP;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_PROC_STAT_IZP_SROK;
import static com.ib.indexui.system.Constants.CODE_CLASSIF_ADMIN_STR;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.ProcDef;
import com.ib.babhregs.db.dto.ProcDefEtap;
import com.ib.babhregs.db.dto.ProcExe;
import com.ib.babhregs.db.dto.ProcExeEtap;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.BaseSystemData;
import com.ib.system.db.AbstractDAO;
import com.ib.system.db.DialectConstructor;
import com.ib.system.db.JPA;
import com.ib.system.db.SelectMetadata;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;

/**
 * DAO for {@link ProcExe}
 *
 * @author belev
 */
public class ProcExeDAO extends AbstractDAO<ProcExe> {

	/**  */
	static final Logger LOGGER = LoggerFactory.getLogger(ProcExeDAO.class);

	/** @param user */
	public ProcExeDAO(ActiveUser user) {
		super(ProcExe.class, user);
	}

	/**
	 * Търсене на изпълнение етапи на процедура<br>
	 * [0]-EXE_ETAP_ID<br>
	 * [1]-NOMER<br>
	 * [2]-ETAP_NAME<br>
	 * [3]-ETAP_INFO<br>
	 * [4]-CONDITIONAL<br>
	 * [5]-PREV.EXE_ETAP_ID<br>
	 * [6]-PREV.NOMER<br>
	 * [7]-PREV.ETAP_NAME<br>
	 * [8]-BEGIN_DATE<br>
	 * [9]-SROK_DATE<br>
	 * [10]-END_DATE<br>
	 * [11]-STATUS<br>
	 * [12]-CODE_REF<br>
	 * [13]-COMMENTS<br>
	 * [14]-INSTRUCTIONS<br>
	 * [15]-DOC_ID<br>
	 * [16]-RN_DOC<br>
	 * [17]-DOC_DATE<br>
	 * [18]-IS_MERGE<br>
	 *
	 * @param exeId
	 * @param defId
	 * @return
	 */
	public SelectMetadata createSelectEtapExeList(Integer exeId, Integer defId) {
		String dialect = JPA.getUtil().getDbVendorName();

		StringBuilder select = new StringBuilder();
		select.append(" select ee.EXE_ETAP_ID a0, ee.NOMER a1, ee.ETAP_NAME a2, " //
				+ DialectConstructor.limitBigString(dialect, "ee.ETAP_INFO", 300) + " a3, ee.CONDITIONAL a4 ");
		select.append(" , prev.EXE_ETAP_ID a5, prev.NOMER a6, prev.ETAP_NAME a7 ");
		select.append(" , ee.BEGIN_DATE a8 , ee.SROK_DATE a9, ee.END_DATE a10 ");
		select.append(" , ee.STATUS a11, ee.CODE_REF a12, ee.COMMENTS a13, " //
				+ DialectConstructor.limitBigString(dialect, "ee.INSTRUCTIONS", 300) + " a14 ");
		select.append(" , d.DOC_ID a15, " + DocDAO.formRnDocSelect("d.", dialect) + " a16, d.DOC_DATE a17, de.IS_MERGE a18 ");

		StringBuilder from = new StringBuilder();
		from.append(" from PROC_EXE_ETAP ee ");
		from.append(" left outer join PROC_EXE_ETAP prev on prev.EXE_ETAP_ID = ee.EXE_ETAP_ID_PREV ");
		from.append(" left outer join DOC d on d.DOC_ID = ee.DOC_ID ");
		from.append(" inner join PROC_DEF_ETAP de on de.NOMER = ee.NOMER and de.DEF_ID = :defId ");

		String where = " where ee.EXE_ID = :exeId ";

		Map<String, Object> params = new HashMap<>();
		params.put("exeId", exeId);
		params.put("defId", defId);

		SelectMetadata sm = new SelectMetadata();

		sm.setSql(select.toString() + from + where);
		sm.setSqlCount(" select count(*) " + from + where);
		sm.setSqlParameters(params);

		return sm;

	}

	/**
	 * Стартиране на изпълнение на процедура
	 * 
	 * @param defId
	 * @param docId
	 * @param systemData
	 * @return
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	public ProcExe startProc(Integer defId, Integer docId, BaseSystemData systemData) throws DbErrorException, ObjectInUseException {
		ProcDefDAO defDao = new ProcDefDAO(getUser());

		ProcDef def = defDao.findById(defId); // ПЪРВО ми трябва дефиницията за която иска стартиране
		if (def == null) {
			throw new ObjectInUseException("Не е намерена дефиниция на процедура с ИД=" + defId + "!");
		}
		if (!Objects.equals(def.getStatus(), CODE_ZNACHENIE_PROC_DEF_STAT_ACTIVE)) {
			throw new ObjectInUseException("Дефиниция на процедура с ИД=" + defId + " не е активна и не може да бъде стартирана!");
		}

		ProcExe exe = new ProcExe();

		exe.setDefId(defId);
		exe.setRegistraturaId(def.getRegistraturaId());
		exe.setDocId(docId);

		// прекопирвам данните в изпълнението
		exe.setProcName(def.getProcName());
		exe.setProcInfo(def.getProcInfo());
		exe.setSrokDays(def.getSrokDays());
		exe.setWorkDaysOnly(def.getWorkDaysOnly());
		exe.setInstructions(def.getInstructions());

		ProcExeEtapDAO exeEtapDao = new ProcExeEtapDAO(getUser());

		// НАКРАЯ разните дати и срокове
		exe.setBeginDate(new Date());
		exe.setSrokDate(exeEtapDao.calcSrokDate(exe.getBeginDate(), exe.getSrokDays(), null, exe.getWorkDaysOnly(), systemData));

		exe.setStatus(CODE_ZNACHENIE_PROC_STAT_EXE);

		exe = save(exe); // !!! основния запис !!!

		List<ProcDefEtap> defEtapList = defDao.selectDefEtapList(def.getId(), 1); // само първия

		if (!defEtapList.isEmpty()) {
			exeEtapDao.startEtap(exe, 0, defEtapList.get(0), systemData, null, exe.getDocId());
		}

		return exe;
	}
	
	/**
	 * Прекратяване на процедурата
	 *
	 * @param exe
	 * @param stopReason
	 * @param bySystem
	 * @param systemData
	 * @return
	 * @throws DbErrorException
	 */
	public ProcExe stopProc(ProcExe exe, String stopReason, boolean bySystem, BaseSystemData systemData) throws DbErrorException {
		exe.setStatus(BabhConstants.CODE_ZNACHENIE_PROC_STAT_STOP);
		exe.setStopReason(stopReason);

		Date now = new Date();
		exe.setEndDate(now);

		StringBuilder comments = new StringBuilder();
		if (bySystem) {
			comments.append("Процедурата е прекратена от системата на " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(now) + ".");

		} else {
			SystemClassif empl = systemData.decodeItemLite(CODE_CLASSIF_ADMIN_STR, getUserId(), getUserLang(), now, false);
			if (empl != null) {
				comments.append("Процедурата е прекратена от " + empl.getTekst());
				comments.append(" от " + systemData.decodeItem(CODE_CLASSIF_ADMIN_STR, empl.getCodeParent(), getUserLang(), now));
				comments.append(" на " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(now) + ".");
			}
		}
		exe.setComments(comments.toString());

		ProcExe saved = save(exe);

		@SuppressWarnings("unchecked")
		List<ProcExeEtap> exeEtapList = createQuery("select e from ProcExeEtap e where e.exeId = ?1 and e.status in (?2)") //
			.setParameter(1, exe.getId()).setParameter(2, ProcExeEtapDAO.EXE_ETAP_ACTIVE_STATUS_LIST) //
			.getResultList();

		if (!exeEtapList.isEmpty()) {
			String etapComments = "Етапът е прекратен поради прекратяване изпълнението на процедурата.";

			ProcExeEtapDAO exeEtapDao = new ProcExeEtapDAO(getUser());
			for (ProcExeEtap exeEtap : exeEtapList) { // това няма да пипа по сроковете
				exeEtapDao.cancelEtap(null, BabhConstants.CODE_ZNACHENIE_ETAP_STAT_STOP, exeEtap, etapComments, systemData);
			}
		}
		return saved;
	}

	/**
	 * Първо трие етапите
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void delete(ProcExe entity) throws DbErrorException, ObjectInUseException {
		List<ProcExeEtap> etapList;
		try {
			etapList = createQuery("select x from ProcExeEtap x where x.exeId = ?1")
					.setParameter(1, entity.getId()).getResultList();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на етапи за процедура с ид=" + entity.getId(), e);
		}
		ProcExeEtapDAO exeEtapDao = new ProcExeEtapDAO(getUser());
		for (ProcExeEtap etapExe : etapList) {
			exeEtapDao.delete(etapExe);
		}
		super.delete(entity);
	}

	/**
	 * Приклщчване на процедура
	 *
	 * @param exe
	 * @throws DbErrorException
	 */
	void completeProc(ProcExe exe) throws DbErrorException {
		Number cnt = (Number) createNativeQuery("select count(*) cnt from PROC_EXE_ETAP where EXE_ID = ?1 and STATUS in (?2)") //
				.setParameter(1, exe.getId()).setParameter(2, ProcExeEtapDAO.EXE_ETAP_ACTIVE_STATUS_LIST) //
				.getResultList().get(0);

		if (cnt.intValue() > 0) {
			return; // има етапи по които се работи в тази процедура и нищо не се прави
		}

		// явно е готова

		exe.setEndDate(new Date());

		if (exe.getSrokDate() != null && exe.getSrokDate().getTime() < exe.getEndDate().getTime()) {
			exe.setStatus(CODE_ZNACHENIE_PROC_STAT_IZP_SROK);
		} else {
			exe.setStatus(CODE_ZNACHENIE_PROC_STAT_IZP);
		}
		save(exe);
	}

	/** Изтрива всички процедури, където се среща този документ */
	@SuppressWarnings("unchecked")
	void deleteProcByDoc(Integer docId) throws DbErrorException, ObjectInUseException {
		if (docId == null) {
			return;
		}
		List<ProcExe> procList;
		try {
			procList = createQuery("select x from ProcExe x where x.docId = ?1")
					.setParameter(1, docId).getResultList();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на процедури за документ с ид=" + docId, e);
		}
		for (ProcExe procExe : procList) {
			delete(procExe);
		}
	}
}
