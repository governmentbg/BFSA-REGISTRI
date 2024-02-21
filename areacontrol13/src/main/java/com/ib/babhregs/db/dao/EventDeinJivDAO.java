package com.ib.babhregs.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceUnitUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.TransientPropUtil;
import com.ib.babhregs.db.dto.EventDeinJiv;
import com.ib.babhregs.db.dto.EventDeinJivDarj;
import com.ib.babhregs.db.dto.EventDeinJivGkpp;
import com.ib.babhregs.db.dto.EventDeinJivLice;
import com.ib.babhregs.db.dto.EventDeinJivObuchenie;
import com.ib.babhregs.db.dto.EventDeinJivVid;
import com.ib.babhregs.db.dto.MpsDeinost;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;

/**
 * DAO for {@link EventDeinJiv}
 */
public class EventDeinJivDAO extends AbstractDAO<EventDeinJiv> {

	/**  */
	private static final Logger LOGGER = LoggerFactory.getLogger(EventDeinJivDAO.class);

	/** @param user */
	public EventDeinJivDAO(ActiveUser user) {
		super(EventDeinJiv.class, user);
	}

	/**
	 * + разните му не JPA каши
	 */
	@Override
	public EventDeinJiv findById(Object id) throws DbErrorException {
		if (id == null) {
			return null;
		}
		EventDeinJiv entity = super.findById(id);
		if (entity == null) {
			return entity;
		}
		loadFull(entity);

		return entity;
	}

	/**
	 * Зарежда обекта за конкретно вписване
	 *
	 * @param idVpisvane
	 * @return
	 * @throws DbErrorException
	 */
	public EventDeinJiv findByVpisvaneId(Integer idVpisvane) throws DbErrorException {
		if (idVpisvane == null) {
			return null;
		}

		EventDeinJiv entity;
		try {
			@SuppressWarnings("unchecked")
			List<EventDeinJiv> list = createQuery("select x from EventDeinJiv x where x.idVpisvane = ?1") //
					.setParameter(1, idVpisvane).getResultList();
			if (list.isEmpty()) {
				return null;
			}
			entity = list.get(0);

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на EventDeinJiv по idVpisvane=" + idVpisvane, e);
		}
		loadFull(entity);

		return entity;
	}

	/** */
	@Override
	public EventDeinJiv save(EventDeinJiv entity) throws DbErrorException {
		EventDeinJiv saved = save(entity, new ArrayList<>());
		return saved;
	}

	/**
	 * след базовия merge прави обработка на transient полетата
	 */
	@Override
	protected EventDeinJiv merge(EventDeinJiv entity) throws DbErrorException {
		EventDeinJiv merged = super.merge(entity);

		mergeTransientProps(entity, merged);

		return merged;
	}

	/**
	 * след базовия persist прави обработка на transient полетата
	 */
	@Override
	protected void persist(EventDeinJiv entity) throws DbErrorException {
		super.persist(entity);

		mergeTransientProps(entity, entity);
	}

	/**
	 * преди базовия remove изтрива transient полетата
	 */
	@Override
	protected void remove(EventDeinJiv entity) throws DbErrorException, ObjectInUseException {
		try {
			int deleted = createNativeQuery("delete from event_deinost_jiv_vid where event_deinost_jiv_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} event_deinost_jiv_vid за event_deinost_jiv_id={}", deleted, entity.getId());

			deleted = createNativeQuery("delete from event_deinost_jiv_darj where event_deinost_jiv_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} event_deinost_jiv_darj за event_deinost_jiv_id={}", deleted, entity.getId());

			deleted = createNativeQuery("delete from event_deinost_jiv_gkpp where event_deinost_jiv_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} event_deinost_jiv_gkpp за event_deinost_jiv_id={}", deleted, entity.getId());

			deleted = createNativeQuery("delete from event_deinost_jiv_obuchenie where event_deinost_jiv_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} event_deinost_jiv_obuchenie за event_deinost_jiv_id={}", deleted, entity.getId());

		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на свързани обекти на Дейности с животни!", e);
		}
		super.remove(entity);
	}

	/**
	 * Допълва всичко, което е jpa-lazy или transient
	 *
	 * @param entity
	 * @throws DbErrorException
	 */
	void loadFull(EventDeinJiv entity) throws DbErrorException {
		entity.getEventDeinJivPredmet().size(); // lazy

		if (entity.getEventDeinJivLice().size() > 0) { // lazy, но трябва и лицето да се вдигне
			ReferentDAO referentDao = new ReferentDAO(getUser());
			for (EventDeinJivLice jivLice : entity.getEventDeinJivLice()) {
				if (jivLice.getCodeRef() != null) {
					jivLice.setReferent(referentDao.findByCodeRef(jivLice.getCodeRef()));
				}
			}
		}
		if (entity.getObektDeinostDeinost().size() > 0) { // lazy, но трябва и обекта да се вдигне
			ObektDeinostDAO obektDeinostDao = new ObektDeinostDAO(getUser());
			for (ObektDeinostDeinost odd : entity.getObektDeinostDeinost()) {
				odd.setObektDeinost(obektDeinostDao.findById(odd.getObektDeinostId(), false)); // без да се вдигат лицата
			}
		}

		try {
			@SuppressWarnings("unchecked")
			List<Integer> vidList = createQuery( //
					"select x.vid from EventDeinJivVid x where x.eventDeinJivId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
			entity.setVidList(vidList); // това е задължително и ако няма все пак нещо да е празен списък !
			entity.setDbVidList(new ArrayList<>(vidList));

			if (vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_IZNOS_JIV)) { // тези долу са приложими само за тази дейност
				@SuppressWarnings("unchecked")
				List<Integer> darjList = createQuery( //
						"select x.darj from EventDeinJivDarj x where x.eventDeinJivId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
				if (!darjList.isEmpty()) {
					entity.setDarjList(darjList);
					entity.setDbDarjList(new ArrayList<>(darjList));
				}
				@SuppressWarnings("unchecked")
				List<Integer> gkppList = createQuery( //
						"select x.gkpp from EventDeinJivGkpp x where x.eventDeinJivId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
				if (!gkppList.isEmpty()) {
					entity.setGkppList(gkppList);
					entity.setDbGkppList(new ArrayList<>(gkppList));
				}
			}

			if (vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PREVOZ_JIV)) { // тези долу са приложими само за тази дейност
				if (entity.getMpsDeinost().size() > 0) { // lazy условно, но трябва и mpsInfo да се вдигне
					MpsDAO mpsDao = new MpsDAO(getUser());
					for (MpsDeinost md : entity.getMpsDeinost()) {
						md.setMpsInfo(mpsDao.findMpsInfoByIdOrNomer(md.getMpsId(), null));
					}
				}
			}

			if (vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_IDENT) // тези долу са приложими само за тези дейности
					|| vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PROIZV_IDENT)) {
				entity.getEventDeinJivIdentif().size(); // lazy условно
			}

			if (vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_UCHEBNA_PROGR) // тези долу са приложими само за тази дейност
					|| vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE)) {
				@SuppressWarnings("unchecked")
				List<Integer> obuchenieList = createQuery( //
						"select x.sferaObucenie from EventDeinJivObuchenie x where x.eventDeinJivId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
				if (!obuchenieList.isEmpty()) {
					entity.setObuchenieList(obuchenieList);
					entity.setDbObuchenieList(new ArrayList<>(obuchenieList));
				}
			}

		} catch (DbErrorException e) {
			throw e; // за да не се пропакова
		} catch (Exception e) {
			throw new DbErrorException("Грешка при извличане на свързани обекти", e);
		}
	}

	/**
	 * Прави обработка на transient полета (запис/корекция/изтриване).
	 *
	 * @param entity  обекта от екрана
	 * @param managed обекта след намесата на jpa, който ще се журналира в последствие и ще се върне на екрана
	 * @throws DbErrorException
	 */
	void mergeTransientProps(EventDeinJiv entity, EventDeinJiv managed) throws DbErrorException {

		//
		// -- event_deinost_jiv_vid.vid
		if (entity.getVidList() != null || entity.getDbVidList() != null) { // само ако някъде е сетнато нещо
			managed.setVidList(entity.getVidList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbVidList(), entity.getVidList(), new EventDeinJivVid(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbVidList(entity.getVidList() != null ? new ArrayList<>(entity.getVidList()) : null);
			managed.setDbVidList(entity.getDbVidList());
		}

		//
		// -- event_deinost_jiv_darj.darj
		if (entity.getDarjList() != null || entity.getDbDarjList() != null) { // само ако някъде е сетнато нещо
			managed.setDarjList(entity.getDarjList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbDarjList(), entity.getDarjList(), new EventDeinJivDarj(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbDarjList(entity.getDarjList() != null ? new ArrayList<>(entity.getDarjList()) : null);
			managed.setDbDarjList(entity.getDbDarjList());
		}

		//
		// -- event_deinost_jiv_gkpp.gkpp
		if (entity.getGkppList() != null || entity.getDbGkppList() != null) { // само ако някъде е сетнато нещо
			managed.setGkppList(entity.getGkppList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbGkppList(), entity.getGkppList(), new EventDeinJivGkpp(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbGkppList(entity.getGkppList() != null ? new ArrayList<>(entity.getGkppList()) : null);
			managed.setDbGkppList(entity.getDbGkppList());
		}

		//
		// -- event_deinost_jiv_obuchenie.sfera_obucenie
		if (entity.getObuchenieList() != null || entity.getDbObuchenieList() != null) { // само ако някъде е сетнато нещо
			managed.setObuchenieList(entity.getObuchenieList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbObuchenieList(), entity.getObuchenieList(), new EventDeinJivObuchenie(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbObuchenieList(entity.getObuchenieList() != null ? new ArrayList<>(entity.getObuchenieList()) : null);
			managed.setDbObuchenieList(entity.getDbObuchenieList());
		}

	}

	/**
	 * Запис на дейности и всевъзможните му логики. Подават се и текущо записаните лица, защото има логики по проверка на еднакви
	 * лица.
	 *
	 * @param entity
	 * @param allReferents
	 * @return
	 * @throws DbErrorException
	 */
	EventDeinJiv save(EventDeinJiv entity, List<Referent> allReferents) throws DbErrorException {
		// с това долу ще може да се види дали даден мапнат през jpa списък е зареден на лоад. ако не значи не се използва и не трябва да
		// се цикли по него.
		PersistenceUnitUtil util = getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();

		if (entity.getEventDeinJivPredmet() == null) {
			entity.setEventDeinJivPredmet(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}
		if (entity.getEventDeinJivIdentif() == null) {
			entity.setEventDeinJivIdentif(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}

		Map<Integer, Referent> referentMap = null; // заради JPA MEGRE
		if (entity.getEventDeinJivLice() == null) {
			entity.setEventDeinJivLice(new ArrayList<>()); // за да може да се направи повторен запис след първият

		} else {
			referentMap = new HashMap<>();

			ReferentDAO referentDao = new ReferentDAO(getUser());
			for (EventDeinJivLice jivLice : entity.getEventDeinJivLice()) {

				if (jivLice.getReferent() != null && jivLice.getReferent().getRefType() != null) {
					jivLice.setReferent(referentDao.save(jivLice.getReferent(), allReferents, true));
					jivLice.setCodeRef(jivLice.getReferent().getCode());

					referentMap.put(jivLice.getCodeRef(), jivLice.getReferent());
				}
				if (jivLice.getDateBeg() == null) {
					jivLice.setDateBeg(new Date());
				}
			}
		}

		Map<Integer, Object[]> mpsInfoMap = null; // заради JPA MEGRE
		if (entity.getMpsDeinost() == null) {
			entity.setMpsDeinost(new ArrayList<>()); // за да може да се направи повторен запис след първият

		} else if (entity.getId() == null || util.isLoaded(entity, "mpsDeinost")) {
			mpsInfoMap = new HashMap<>();

			for (MpsDeinost md : entity.getMpsDeinost()) { // преди записа трябва да се сетне MpsDeinost.tablEventDeinost
				md.setTablEventDeinost(entity.getCodeMainObject());
				mpsInfoMap.put(md.getMpsId(), md.getMpsInfo());
				if (md.getDateBeg() == null) {
					md.setDateBeg(new Date());
				}
			}
		}

		Map<Integer, ObektDeinost> obektDeinostMap = null; // заради JPA MEGRE
		if (entity.getObektDeinostDeinost() == null) {
			entity.setObektDeinostDeinost(new ArrayList<>()); // за да може да се направи повторен запис след първият

		} else {
			obektDeinostMap = new HashMap<>();

			for (ObektDeinostDeinost odd : entity.getObektDeinostDeinost()) { // тука са ОЕЗ-та и те не се записват

				if (odd.getObektDeinost() != null && odd.getObektDeinost().getId() != null) {
					odd.setObektDeinostId(odd.getObektDeinost().getId());
					obektDeinostMap.put(odd.getObektDeinostId(), odd.getObektDeinost());
				}
				odd.setTablEventDeinost(entity.getCodeMainObject());
				if (odd.getDateBeg() == null) {
					odd.setDateBeg(new Date());
				}
			}
		}

		EventDeinJiv saved = super.save(entity);
		// EventDeinJivPredmet минава през jpa
		// EventDeinJivIdentif минава през jpa

		// EventDeinJivLice минава през jpa но заради MERGE трябват врътки !?!?
		if (referentMap != null && saved.getEventDeinJivLice() != null && !saved.getEventDeinJivLice().isEmpty()) {
			for (EventDeinJivLice jivLice : saved.getEventDeinJivLice()) {
				jivLice.setReferent(referentMap.get(jivLice.getCodeRef()));
			}
		}

		// MpsDeinost минава през jpa но след MERGE трябват врътки !?!?
		if (mpsInfoMap != null && saved.getMpsDeinost() != null && !saved.getMpsDeinost().isEmpty()) {
			for (MpsDeinost md : saved.getMpsDeinost()) {
				md.setMpsInfo(mpsInfoMap.get(md.getMpsId()));
			}
		}

		// ObektDeinostDeinost минава през jpa но след MERGE трябват врътки !?!?
		if (obektDeinostMap != null && saved.getObektDeinostDeinost() != null && !saved.getObektDeinostDeinost().isEmpty()) {
			for (ObektDeinostDeinost odd : saved.getObektDeinostDeinost()) {
				odd.setObektDeinost(obektDeinostMap.get(odd.getObektDeinostId()));
			}
		}

		return saved;
	}
}
