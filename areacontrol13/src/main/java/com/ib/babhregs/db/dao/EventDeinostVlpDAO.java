package com.ib.babhregs.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.TransientPropUtil;
import com.ib.babhregs.db.dto.EventDeinostVlp;
import com.ib.babhregs.db.dto.EventDeinostVlpBolesti;
import com.ib.babhregs.db.dto.EventDeinostVlpLice;
import com.ib.babhregs.db.dto.EventDeinostVlpPredmet;
import com.ib.babhregs.db.dto.EventDeinostVlpPrvnos;
import com.ib.babhregs.db.dto.EventDeinostVlpPrvnosSubst;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;

/**
 * DAO for {@link EventDeinostVlp}
 */
public class EventDeinostVlpDAO extends AbstractDAO<EventDeinostVlp> {

	/**  */
	private static final Logger LOGGER = LoggerFactory.getLogger(EventDeinostVlpDAO.class);

	/** @param user */
	public EventDeinostVlpDAO(ActiveUser user) {
		super(EventDeinostVlp.class, user);
	}

	/**
	 * + разните му не JPA каши
	 */
	@Override
	public EventDeinostVlp findById(Object id) throws DbErrorException {
		if (id == null) {
			return null;
		}
		EventDeinostVlp entity = super.findById(id);
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
	public EventDeinostVlp findByVpisvaneId(Integer idVpisvane) throws DbErrorException {
		if (idVpisvane == null) {
			return null;
		}

		EventDeinostVlp entity;
		try {
			@SuppressWarnings("unchecked")
			List<EventDeinostVlp> list = createQuery("select x from EventDeinostVlp x where x.idVpisvane = ?1") //
					.setParameter(1, idVpisvane).getResultList();
			if (list.isEmpty()) {
				return null;
			}
			entity = list.get(0);

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на EventDeinostVlp по idVpisvane=" + idVpisvane, e);
		}
		loadFull(entity);

		return entity;
	}

	/** */
	@Override
	public EventDeinostVlp save(EventDeinostVlp entity) throws DbErrorException {
		EventDeinostVlp saved = save(entity, new ArrayList<>());
		return saved;
	}

	/**
	 * след базовия merge прави обработка на transient полетата
	 */
	@Override
	protected EventDeinostVlp merge(EventDeinostVlp entity) throws DbErrorException {
		EventDeinostVlp merged = super.merge(entity);

		mergeTransientProps(entity, merged);

		return merged;
	}

	/**
	 * след базовия persist прави обработка на transient полетата
	 */
	@Override
	protected void persist(EventDeinostVlp entity) throws DbErrorException {
		super.persist(entity);

		mergeTransientProps(entity, entity);
	}

	/**
	 * преди базовия remove изтрива transient полетата
	 */
	@Override
	protected void remove(EventDeinostVlp entity) throws DbErrorException, ObjectInUseException {
		try {
			int deleted = createNativeQuery("delete from event_deinost_vlp_bolesti where event_dejnost_vlp_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} event_deinost_vlp_bolesti за event_dejnost_vlp_id={}", deleted, entity.getId());

		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на свързани обекти на Дейности с ВЛП!", e);
		}
		super.remove(entity);
	}

	/**
	 * Допълва всичко, което е jpa-lazy или transient
	 *
	 * @param entity
	 * @throws DbErrorException
	 */
	void loadFull(EventDeinostVlp entity) throws DbErrorException {
		// не винаги ги има тези долу и да се направи както е в дейностите с животни

		entity.getEventDeinostVlpVid().size(); // lazy
		entity.getEventDeinostVlpPredmet().size(); // lazy
		if (entity.getEventDeinostVlpPrvnos().size() > 0) { // lazy, но има още едно ниво със списъци
			for (EventDeinostVlpPrvnos prvnos : entity.getEventDeinostVlpPrvnos()) {
				prvnos.getEventDeinostVlpPrvnosSubst().size();
				prvnos.getEventDeinostVlpPrvnosOpakovka().size();
			}
		}

		if (entity.getObektDeinostDeinost().size() > 0) { // lazy, но трябва и обекта да се вдигне
			ObektDeinostDAO obektDeinostDao = new ObektDeinostDAO(getUser());
			for (ObektDeinostDeinost odd : entity.getObektDeinostDeinost()) {
				odd.setObektDeinost(obektDeinostDao.findById(odd.getObektDeinostId(), true)); // + лицата
			}
		}
		if (entity.getEventDeinostVlpLice().size() > 0) { // lazy, но трябва и лицето да се вдигне
			ReferentDAO referentDao = new ReferentDAO(getUser());

			for (EventDeinostVlpLice vlpLice : entity.getEventDeinostVlpLice()) {
				if (vlpLice.getCodeRef() != null) {
					vlpLice.setReferent(referentDao.findByCodeRef(vlpLice.getCodeRef()));
				}
			}
		}

		try {
			@SuppressWarnings("unchecked")
			List<Integer> bolestList = createQuery( //
					"select x.bolest from EventDeinostVlpBolesti x where x.eventDejnostVlpId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
			if (!bolestList.isEmpty()) {
				entity.setBolestList(bolestList);
				entity.setDbBolestList(new ArrayList<>(bolestList));
			}
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
	void mergeTransientProps(EventDeinostVlp entity, EventDeinostVlp managed) throws DbErrorException {

		//
		// -- event_deinost_vlp_bolesti.bolest
		if (entity.getBolestList() != null || entity.getDbBolestList() != null) { // само ако някъде е сетнато нещо
			managed.setBolestList(entity.getBolestList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbBolestList(), entity.getBolestList(), new EventDeinostVlpBolesti(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbBolestList(entity.getBolestList() != null ? new ArrayList<>(entity.getBolestList()) : null);
			managed.setDbBolestList(entity.getDbBolestList());
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
	EventDeinostVlp save(EventDeinostVlp entity, List<Referent> allReferents) throws DbErrorException {
		if (entity.getEventDeinostVlpVid() == null) {
			entity.setEventDeinostVlpVid(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}
		if (entity.getEventDeinostVlpPredmet() == null) {
			entity.setEventDeinostVlpPredmet(new ArrayList<>()); // за да може да се направи повторен запис след първият
		} else {
			for (EventDeinostVlpPredmet v : entity.getEventDeinostVlpPredmet()) {
				if (v.getVidIdentifier() != null) {
					v.setVidIdentifier(v.getVidIdentifier().manage());
				}
			}
		}
		if (entity.getEventDeinostVlpPrvnos() == null) {
			entity.setEventDeinostVlpPrvnos(new ArrayList<>()); // за да може да се направи повторен запис след първият
		} else {
			for (EventDeinostVlpPrvnos t : entity.getEventDeinostVlpPrvnos()) {
				if (t.getEventDeinostVlpPrvnosSubst() != null) {
					for (EventDeinostVlpPrvnosSubst v : t.getEventDeinostVlpPrvnosSubst()) {
						if (v.getVidIdentifier() != null) {
							v.setVidIdentifier(v.getVidIdentifier().manage());
						}
					}
				}
			}
		}

		Map<Integer, ObektDeinost> obektDeinostMap = new HashMap<>(); // заради JPA MEGRE
		if (entity.getObektDeinostDeinost() == null) {
			entity.setObektDeinostDeinost(new ArrayList<>()); // за да може да се направи повторен запис след първият

		} else {
			ObektDeinostDAO obektDeinostDao = new ObektDeinostDAO(getUser());
			for (ObektDeinostDeinost odd : entity.getObektDeinostDeinost()) {

				if (odd.getObektDeinost() != null) {
					odd.getObektDeinost().setVid(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLP);
					odd.setObektDeinost(obektDeinostDao.save(odd.getObektDeinost(), true, allReferents));

					odd.setObektDeinostId(odd.getObektDeinost().getId());

					obektDeinostMap.put(odd.getObektDeinostId(), odd.getObektDeinost());
				}
				odd.setTablEventDeinost(entity.getCodeMainObject());
				if (odd.getDateBeg() == null) {
					odd.setDateBeg(new Date());
				}
			}
		}

		Map<Integer, Referent> referentMap = new HashMap<>(); // заради JPA MEGRE
		if (entity.getEventDeinostVlpLice() == null) {
			entity.setEventDeinostVlpLice(new ArrayList<>()); // за да може да се направи повторен запис след първият

		} else {
			ReferentDAO referentDao = new ReferentDAO(getUser());
			for (EventDeinostVlpLice vlpLice : entity.getEventDeinostVlpLice()) {

				if (vlpLice.getReferent() != null && vlpLice.getReferent().getRefType() != null) {
					vlpLice.setReferent(referentDao.save(vlpLice.getReferent(), allReferents, true));

					vlpLice.setCodeRef(vlpLice.getReferent().getCode());

					referentMap.put(vlpLice.getCodeRef(), vlpLice.getReferent());
				}
				if (vlpLice.getDateBeg() == null) {
					vlpLice.setDateBeg(new Date());
				}
			}
		}

		EventDeinostVlp saved = super.save(entity);
		// EventDeinostVlpVid минава през jpa
		// EventDeinostVlpPredmet минава през jpa
		// EventDeinostVlpPrvnos минава през jpa

		// ObektDeinostDeinost минава през jpa но заради MERGE трябват врътки !?!?
		if (saved.getObektDeinostDeinost() != null && !saved.getObektDeinostDeinost().isEmpty()) {
			for (ObektDeinostDeinost odd : saved.getObektDeinostDeinost()) {
				odd.setObektDeinost(obektDeinostMap.get(odd.getObektDeinostId()));
			}
		}

		// EventDeinostVlpLice минава през jpa но заради MERGE трябват врътки !?!?
		if (saved.getEventDeinostVlpLice() != null && !saved.getEventDeinostVlpLice().isEmpty()) {
			for (EventDeinostVlpLice vlpLice : saved.getEventDeinostVlpLice()) {
				vlpLice.setReferent(referentMap.get(vlpLice.getCodeRef()));
			}
		}

		return saved;
	}
}
