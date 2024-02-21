package com.ib.babhregs.db.dao;

import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ETAP_DOC_MODE_PREV_IN;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ETAP_DOC_MODE_PREV_OUT;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ETAP_STAT_CANCEL;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ETAP_STAT_DECISION;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ETAP_STAT_EXE;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ETAP_STAT_IZP;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ETAP_STAT_IZP_SROK;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_ETAP_STAT_WAIT;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_PROC_STAT_EXE;
import static com.ib.indexui.system.Constants.CODE_CLASSIF_ADMIN_STR;
import static com.ib.system.SysConstants.CODE_ZNACHENIE_DA;
import static com.ib.system.SysConstants.CODE_ZNACHENIE_NE;
import static com.ib.system.utils.SearchUtils.trimToNULL;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.ProcDefEtap;
import com.ib.babhregs.db.dto.ProcExe;
import com.ib.babhregs.db.dto.ProcExeEtap;
import com.ib.babhregs.system.SystemData;
import com.ib.system.ActiveUser;
import com.ib.system.BaseSystemData;
import com.ib.system.SysConstants;
import com.ib.system.db.AbstractDAO;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.InvalidParameterException;
import com.ib.system.utils.DateUtils;

/**
 * DAO for {@link ProcExeEtap}
 *
 * @author belev
 */
public class ProcExeEtapDAO extends AbstractDAO<ProcExeEtap> {

	/**  */
	static final Logger LOGGER = LoggerFactory.getLogger(ProcExeEtapDAO.class);

	/**  */
	protected static final List<Integer> EXE_ETAP_ACTIVE_STATUS_LIST = Arrays.asList( //
			CODE_ZNACHENIE_ETAP_STAT_WAIT//
			, CODE_ZNACHENIE_ETAP_STAT_EXE//
			, CODE_ZNACHENIE_ETAP_STAT_DECISION);

	/** @param user */
	public ProcExeEtapDAO(ActiveUser user) {
		super(ProcExeEtap.class, user);
	}

	/**
	 * @param begin
	 * @param srokDays
	 * @param srokHours
	 * @param workDaysOnly
	 * @param systemData
	 * @return
	 */
	public Date calcSrokDate(Date begin, Integer srokDays, Integer srokHours, Integer workDaysOnly, BaseSystemData systemData) {
		if (Objects.equals(workDaysOnly, CODE_ZNACHENIE_DA)) {
			if (srokDays == null) {
				return null; // гледат се само дните, така че ако ги няма - няма дата
			}
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(begin);

			gc.set(Calendar.HOUR_OF_DAY, 0);
			gc.set(Calendar.MINUTE, 0);
			gc.set(Calendar.SECOND, 0);
			gc.set(Calendar.MILLISECOND, 0);

			Set<Long> praznici = ((SystemData) systemData).getPraznici();
			int i = 0;
			while (i < srokDays) {
				gc.set(DAY_OF_YEAR, gc.get(DAY_OF_YEAR) + 1);

				if (gc.get(DAY_OF_WEEK) == SATURDAY || gc.get(DAY_OF_WEEK) == SUNDAY) {
					// пропускам
				} else if (praznici != null && praznici.contains(gc.getTimeInMillis())) {
					// пропускам
				} else {
					i++;
				}
			}

			gc.set(Calendar.HOUR_OF_DAY, 23);
			gc.set(Calendar.MINUTE, 59);
			gc.set(Calendar.SECOND, 59);

			return gc.getTime();
		}

		// периода е непрекъснат и се добавят директно дни и часове към началната дата
		long srokMillis = 0;
		if (srokDays != null) {
			srokMillis += ((long)srokDays * 24 * 60 * 60 * 1000);
		}
		if (srokHours != null) {
			srokMillis += ((long)srokHours * 60 * 60 * 1000);
		}
		return srokMillis > 0 ? new Date(begin.getTime() + srokMillis) : null;
	}

	/**
	 * @param end
	 * @param srokDays
	 * @param srokHours
	 * @param workDaysOnly
	 * @param systemData
	 * @return
	 */
	public Date calcSrokDateBackward(Date end, Integer srokDays, Integer srokHours, Integer workDaysOnly, BaseSystemData systemData) {
		if (Objects.equals(workDaysOnly, CODE_ZNACHENIE_DA)) {
			if (srokDays == null) {
				return null; // гледат се само дните, така че ако ги няма - няма дата
			}
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(end);

			gc.set(Calendar.HOUR_OF_DAY, 0);
			gc.set(Calendar.MINUTE, 0);
			gc.set(Calendar.SECOND, 0);
			gc.set(Calendar.MILLISECOND, 0);

			Set<Long> praznici = ((SystemData) systemData).getPraznici();
			int i = 0;
			while (i < srokDays) {
				gc.set(DAY_OF_YEAR, gc.get(DAY_OF_YEAR) - 1);

				if (gc.get(DAY_OF_WEEK) == SATURDAY || gc.get(DAY_OF_WEEK) == SUNDAY) {
					// пропускам
				} else if (praznici != null && praznici.contains(gc.getTimeInMillis())) {
					// пропускам
				} else {
					i++;
				}
			}

			gc.set(Calendar.HOUR_OF_DAY, 23);
			gc.set(Calendar.MINUTE, 59);
			gc.set(Calendar.SECOND, 59);

			return gc.getTime();
		}

		// периода е непрекъснат и се изваждат директно дни и часове от крайната дата
		long srokMillis = 0;
		if (srokDays != null) {
			srokMillis += ((long)srokDays * 24 * 60 * 60 * 1000);
		}
		if (srokHours != null) {
			srokMillis += ((long)srokHours * 60 * 60 * 1000);
		}
		return srokMillis > 0 ? new Date(end.getTime() - srokMillis) : null;
	}

	/**
	 * Приклщчване на етап от процедура
	 *
	 * @param sd
	 * @param exeEtap
	 * @param ok           true-NEXT_OK, false-NEXT_NOT
	 * @param optional     по каквото дойде
	 * @param comments
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public ProcExeEtap completeEtap(ProcExeEtap exeEtap, SystemData sd, Boolean ok, String optional, String comments) throws DbErrorException {
		if (exeEtap == null //
				|| !(exeEtap.getStatus().equals(CODE_ZNACHENIE_ETAP_STAT_EXE) || exeEtap.getStatus().equals(CODE_ZNACHENIE_ETAP_STAT_DECISION))) {
			return exeEtap; // само в изброените статуси може да се приключи
		}

		exeEtap.setEndDate(new Date());
		exeEtap.setComments(comments);
		if (exeEtap.getSrokDate() != null && exeEtap.getSrokDate().getTime() < exeEtap.getEndDate().getTime()) {
			exeEtap.setStatus(CODE_ZNACHENIE_ETAP_STAT_IZP_SROK);
		} else {
			exeEtap.setStatus(CODE_ZNACHENIE_ETAP_STAT_IZP);
		}
		exeEtap = save(exeEtap); // този е готов

		// и трябва да се види какво се случва с изпълнението на процедурата и следващи етапи ако има

		ProcExeDAO exeDao = new ProcExeDAO(getUser());
		ProcExe exe = exeDao.findById(exeEtap.getExeId()); // изпълнението

		String next;
		if (ok != null) { // по да или по не
			List<Object> nextList = createNativeQuery("select " + (ok.booleanValue() ? "NEXT_OK" : "NEXT_NOT") //
					+ " from PROC_DEF_ETAP where DEF_ID = ?1 and NOMER = ?2") //
					.setParameter(1, exe.getDefId()).setParameter(2, exeEtap.getNomer()) //
					.getResultList();

			next = nextList.isEmpty() ? null : trimToNULL((String) nextList.get(0));

		} else { // или кой каквото му е на сърце
			next = trimToNULL(optional);
		}

		if (exe.getSrokDate() != null) { // трябва да има определен срок на процедурата
			// проверка дали не трябва да се върне срока на процедурата
			checkReturnSrok(exe, exeEtap, exeEtap.getEndDate(), sd);
		}

		if (next == null) { // няма следващи етапи и процедурата би могла да е готова
			exeDao.completeProc(exe);

		} else {
			List<ProcDefEtap> defEtapList = createQuery( //
					"select e from ProcDefEtap e where e.defId = ?1 and e.nomer in (" + next + ")") //
					.setParameter(1, exe.getDefId()).getResultList();

			String branchPath = trimToNULL(exeEtap.getBranchPath()); // за да знам дали текущия е следствие на разклонение

			for (ProcDefEtap defEtap : defEtapList) {
				boolean start = true; // има ситуации, в които не трябва да се стартира следващият
				String nextBranchPath = null;

				if (branchPath != null // текущия е следствие на разклонение
						&& Objects.equals(defEtap.getIsMerge(), CODE_ZNACHENIE_DA)) { // което се влива в събирателен етап

					Number cnt = (Number) createNativeQuery( //
							"select count(*) cnt from PROC_EXE_ETAP where EXE_ID = ?1 and STATUS in (?2) and BRANCH_PATH like ?3 ") //
							.setParameter(1, exe.getId()).setParameter(2, EXE_ETAP_ACTIVE_STATUS_LIST) //
							.setParameter(3, "%" + branchPath + "%") //
							.getResultList().get(0);

					if (cnt.intValue() > 0) {
						start = false; // не може да се стартира следващ, защото по разклонението има все още незавършени етапи

					} else { // ще се стартира и тъй като е събирателен трябва да му мина една стъпка назад справмо текущия който
								// приключва
						nextBranchPath = branchPath.substring(0, branchPath.lastIndexOf('|'));
						nextBranchPath = nextBranchPath.substring(0, nextBranchPath.lastIndexOf('|'));
					}
				} else { // следващия ще се стартира безусловно

					if (defEtapList.size() > 1) { // текущият който приключва се явява разклонение на няколко други

						if (branchPath == null) { // първо разклонение
							nextBranchPath = "|" + exeEtap.getNomer() + "|";
						} else { // пореното
							nextBranchPath = branchPath + "|" + exeEtap.getNomer() + "|";
						}
					} else {
						nextBranchPath = branchPath; // само си се предава каквато е пътеката на разклоненията
					}
				}

				if (start) {
					Number cnt = (Number) createNativeQuery( //
							"select count (*) CNT from PROC_EXE_ETAP where EXE_ID = ?1 and NOMER = ?2 and STATUS in (?3)") //
							.setParameter(1, exeEtap.getId()).setParameter(2, defEtap.getNomer()) //
							.setParameter(3, EXE_ETAP_ACTIVE_STATUS_LIST) //
							.getResultList().get(0);

					if (cnt.intValue() == 0) { // винаги трябва да се провери дали няма активен етап с този номер, защото тогава
												// не се стартира за да се избегне дублирането
						Integer docId;

						if (defEtap.getEtapDocMode() == null) {
							docId = exe.getDocId(); // ако няма изискване го взимам от процедурата каквото има там

						} else if (defEtap.getEtapDocMode().equals(CODE_ZNACHENIE_ETAP_DOC_MODE_PREV_IN)) {
							docId = exeEtap.getDocId();

						} else if (defEtap.getEtapDocMode().equals(CODE_ZNACHENIE_ETAP_DOC_MODE_PREV_OUT)) {
							docId = exeEtap.getDocId(); // ако е празно взимам от стартиращ
																								// предходния етап
						} else {
							docId = null;
						}
						startEtap(exe, exeEtap.getId(), defEtap, sd, nextBranchPath, docId);
					}
				}
			}
		}

		return exeEtap;
	}

	/**
	 * ако е специален етап, който удължава срока и се приключва по рано се налага да се върне срока на процедурата
	 */
	private void checkReturnSrok(ProcExe exe, ProcExeEtap exeEtap, Date realEnd, SystemData systemData) throws DbErrorException {
		if (exeEtap.getSrokDate() == null || realEnd == null) {
			return; // няма по какво да се изчислява
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> defEtapRows = createNativeQuery( // ще се търси дали не е специален етап
				"select def_etap_id, extend_proc_srok from proc_def_etap where def_id = ?1 and nomer = ?2")
				.setParameter(1, exe.getDefId()).setParameter(2, exeEtap.getNomer())
				.getResultList();
		if (defEtapRows.isEmpty() || defEtapRows.get(0)[1] == null
				|| ((Number) defEtapRows.get(0)[1]).intValue() == SysConstants.CODE_ZNACHENIE_NE) {
			return; // такъв не влияе
		}

		// това, че е -1 означава че ще се върне с един едн по малко и ако се пусне и спре в рамките на един и същи ден
		// ако 0 няма да влияе на срока след като се върне и ще се върне на същия ден
		int returnDays = -1; // ако е приключен по рано трябва да върнем процедурата назад !!!
		boolean workDaysOnly = exe.getWorkDaysOnly() != null && exe.getWorkDaysOnly().intValue() == SysConstants.CODE_ZNACHENIE_DA;

		GregorianCalendar end = new GregorianCalendar();
		end.setTime(realEnd);

		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);

		Date srok = DateUtils.startDate(exeEtap.getSrokDate());

		int iter = 0; // все пак да не зациклим 
		while (iter < 365 && end.getTimeInMillis() < srok.getTime()) {
			iter++;
			if (iter > 360) {
				LOGGER.error("!!!! checkReturnSrok !!!! PROBLEM {} !!!!", iter);
			}

			boolean skip = false;
			if (workDaysOnly) {
				if (systemData.getPraznici().contains(end.getTimeInMillis())
						|| end.get(DAY_OF_WEEK) == SATURDAY || end.get(DAY_OF_WEEK) == SUNDAY) {
					skip = true;
				}
			}
			if (!skip) {
				returnDays++; // ако не е почивен или празни, само товагава го броим
			}
			end.set(DAY_OF_YEAR, end.get(DAY_OF_YEAR) + 1); // следващ ден
		}

		if (returnDays > 0) {
			Date newProcSrok = calcSrokDateBackward(exe.getSrokDate() // тръгвам от сегашния срок на процедурата
					, returnDays, null, exe.getWorkDaysOnly(), systemData);

			if (newProcSrok != null) { // ако нещо не може да се определи няма да се нулира срока все пак
				exe.setSrokDate(newProcSrok);
				new ProcExeDAO(getUser()).save(exe);
			}
		}
	}

	/**
	 * @param id
	 * @param mainData при <code>true</code> се зареждат данни, който ще се покажат на Секция „Основни данни“
	 * @return
	 * @throws DbErrorException
	 */
	public ProcExeEtap findById(Object id, boolean mainData) throws DbErrorException {
		ProcExeEtap entity = super.findById(id);
		if (entity == null) {
			return entity;
		}

		if (mainData && entity.getExeEtapIdPrev() != null && entity.getExeEtapIdPrev().intValue() != 0) {

			@SuppressWarnings("unchecked")
			List<Object[]> prevList = createNativeQuery( //
					"select EXE_ETAP_ID, NOMER, ETAP_NAME from PROC_EXE_ETAP where EXE_ETAP_ID = ?1") //
					.setParameter(1, entity.getExeEtapIdPrev()).getResultList();
			if (!prevList.isEmpty()) {
				entity.setPrev(prevList.get(0));
			}
		}
		if (mainData) {
			@SuppressWarnings("unchecked")
			List<Object[]> defList = createNativeQuery( //
					"select de.DEF_ETAP_ID, de.IS_MERGE from PROC_DEF_ETAP de inner join PROC_EXE e on e.DEF_ID = de.DEF_ID where de.NOMER = ?1 and e.EXE_ID = ?2") //
					.setParameter(1, entity.getNomer()).setParameter(2, entity.getExeId()) //
					.getResultList();
			if (!defList.isEmpty()) {
				entity.setDefEtapData(defList.get(0));
			}
		}
		return entity;
	}

	/**
	 * Връщане на изпълнението
	 *
	 * @param exe
	 * @param selected
	 * @param systemData
	 * @return
	 * @throws DbErrorException
	 * @throws InvalidParameterException
	 */
	@SuppressWarnings("unchecked")
	public ProcExeEtap returnEtap(ProcExe exe, ProcExeEtap selected, BaseSystemData systemData) throws DbErrorException, InvalidParameterException {
		if (exe == null || !exe.getStatus().equals(CODE_ZNACHENIE_PROC_STAT_EXE)) {
			return selected; // само в изброените статуси може да се върнем
		}
		if (selected == null //
				|| !(selected.getStatus().equals(CODE_ZNACHENIE_ETAP_STAT_IZP) || selected.getStatus().equals(CODE_ZNACHENIE_ETAP_STAT_IZP_SROK))) {
			return selected; // само в изброените статуси може да се върнем
		}

		Set<Integer> nextIdSet = new HashSet<>(); // трябва да се намерят всички етапи след текущия и са следвтвие неговото
													// изпълнение
		recursiveFindNextExeEtapIdList(selected.getId(), nextIdSet);

		Integer exeEtapIdPrev = 0; // този ще дойде предходен за новото изпълнение

		List<ProcExeEtap> nextActiveList = null;
		if (!nextIdSet.isEmpty()) {
			exeEtapIdPrev = Collections.max(nextIdSet); // ще прдължи след посленият

			nextActiveList = createQuery( //
					"select e from ProcExeEtap e where e.id in (?1) and e.status in (?2)") //
					.setParameter(1, nextIdSet).setParameter(2, ProcExeEtapDAO.EXE_ETAP_ACTIVE_STATUS_LIST) //
					.getResultList();
		}

		if (nextActiveList != null && !nextActiveList.isEmpty()) {
			Date now = new Date();
			StringBuilder etapComments = new StringBuilder();

			SystemClassif empl = systemData.decodeItemLite(CODE_CLASSIF_ADMIN_STR, getUserId(), getUserLang(), now, false);
			if (empl != null) {
				etapComments.append("Етапът е отменен от " + empl.getTekst());
				etapComments.append(" от " + systemData.decodeItem(CODE_CLASSIF_ADMIN_STR, empl.getCodeParent(), getUserLang(), now));
				etapComments.append(" на " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(now));
				etapComments.append(" поради връщане изпълнението на процедурата на етап №" + selected.getNomer());
				etapComments.append(" " + selected.getEtapName() + ".");
			}

			for (ProcExeEtap nextActive : nextActiveList) {
				cancelEtap(exe, CODE_ZNACHENIE_ETAP_STAT_CANCEL, nextActive, etapComments.toString(), systemData);
			}
		}

		// намирам дефиницията и мога да е стартирам вече
		List<ProcDefEtap> defEtapList = createQuery( //
				"select e from ProcDefEtap e where e.defId = ?1 and e.nomer = ?2") //
				.setParameter(1, exe.getDefId()).setParameter(2, selected.getNomer()) //
				.getResultList();

		if (defEtapList.isEmpty()) {
			throw new InvalidParameterException("Не е намерена дефиниция на етап с номер=" + selected.getNomer() //
					+ " и дефиниция на процедура с ИД=" + exe.getDefId());
		}
		ProcExeEtap restarted = startEtap(exe, exeEtapIdPrev, defEtapList.get(0), systemData, null, selected.getDocId());
		return restarted;
	}

	/**
	 * Прекратяване на етап
	 *
	 * @param newStatus
	 * @param exeEtap
	 * @param etapComments
	 * @return
	 * @throws DbErrorException
	 */
	ProcExeEtap cancelEtap(ProcExe exe, Integer newStatus, ProcExeEtap exeEtap, String etapComments, BaseSystemData sd) throws DbErrorException {
		exeEtap.setStatus(newStatus);
		exeEtap.setComments(etapComments);

		exeEtap = save(exeEtap);

		if (exe != null && exe.getSrokDate() != null) { // трябва да има определен срок на процедурата
			// проверка дали не трябва да се върне срока на процедурата
			checkReturnSrok(exe, exeEtap, new Date(), (SystemData) sd); // в момента го третирам като приклюване на днешна дата
		}

		return exeEtap;
	}

	/**
	 * Стартиране на етап от процедура
	 *
	 * @param exe           (id,docId,workDaysOnly)
	 * @param exeEtapIdPrev
	 * @param defEtap
	 * @param systemData
	 * @param branchPath
	 * @param docId
	 * @return
	 * @throws DbErrorException
	 */
	ProcExeEtap startEtap(ProcExe exe, Integer exeEtapIdPrev, ProcDefEtap defEtap, BaseSystemData systemData, String branchPath, Integer docId) throws DbErrorException {
		ProcExeEtap exeEtap = new ProcExeEtap();

		exeEtap.setExeId(exe.getId()); // изпълнението
		exeEtap.setExeEtapIdPrev(exeEtapIdPrev); // и предходния етап
		exeEtap.setBranchPath(branchPath);
		exeEtap.setDocId(docId);

		// прекопирвам данните в изпълнението
		exeEtap.setNomer(defEtap.getNomer());
		exeEtap.setEtapName(defEtap.getEtapName());
		exeEtap.setEtapInfo(defEtap.getEtapInfo());

		String nextNot = trimToNULL(defEtap.getNextNot());
		String nextOptional = trimToNULL(defEtap.getNextOptional());

		exeEtap.setConditional(nextNot != null || nextOptional != null ? CODE_ZNACHENIE_DA : CODE_ZNACHENIE_NE);

		exeEtap.setSrokDays(defEtap.getSrokDays());
		exeEtap.setSrokHours(defEtap.getSrokHours());
		exeEtap.setInstructions(defEtap.getInstructions());

		exeEtap.setBeginDate(new Date());
		exeEtap.setSrokDate(calcSrokDate(exeEtap.getBeginDate(), exeEtap.getSrokDays(), exeEtap.getSrokHours(), exe.getWorkDaysOnly(), systemData));

		exeEtap.setStatus(CODE_ZNACHENIE_ETAP_STAT_EXE);

		exeEtap = save(exeEtap); // !!! основния запис !!!

		if (exe.getSrokDate() != null // процедурата вече има някакъв срок 
				&& defEtap.getSrokDays() != null // има някакви дни в новия етап
				&& defEtap.getExtendProcSrok() != null // има сетнато което удължава общия срок
				&& defEtap.getExtendProcSrok().intValue() == SysConstants.CODE_ZNACHENIE_DA) {

			Date newProcSrok = calcSrokDate(exe.getSrokDate() // тръгвам от сегашния срок на процедурата
					, defEtap.getSrokDays(), null, exe.getWorkDaysOnly(), systemData);

			if (newProcSrok != null) { // ако нещо не може да се определи няма да се нулира срока все пак
				exe.setSrokDate(newProcSrok);
				new ProcExeDAO(getUser()).save(exe);
			}
		}

		return exeEtap;
	}

	private void recursiveFindNextExeEtapIdList(Integer exeEtapId, Set<Integer> nextIdSet) {
		@SuppressWarnings("unchecked")
		List<Object> found = createNativeQuery("select EXE_ETAP_ID from PROC_EXE_ETAP where EXE_ETAP_ID_PREV = ?1") //
				.setParameter(1, exeEtapId).getResultList();
		if (found.isEmpty()) {
			return;
		}

		for (Object tmp : found) {
			Integer id = ((Number) tmp).intValue();
			nextIdSet.add(id);

			recursiveFindNextExeEtapIdList(id, nextIdSet);
		}
	}
}
