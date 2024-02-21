package com.ib.babhregs.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.TransientPropUtil;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.Vlp;
import com.ib.babhregs.db.dto.VlpFarmForm;
import com.ib.babhregs.db.dto.VlpLice;
import com.ib.babhregs.db.dto.VlpOpakovka;
import com.ib.babhregs.db.dto.VlpVeshtva;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;

/**
 * DAO for {@link Vlp}
 */
public class VlpDAO extends AbstractDAO<Vlp> {
	/**  */
	private static final Logger LOGGER = LoggerFactory.getLogger(VlpDAO.class);

	/** @param user */
	public VlpDAO(ActiveUser user) {
		super(Vlp.class, user);
	}

	/**
	 * + разните му не JPA каши
	 */
	@Override
	public Vlp findById(Object id) throws DbErrorException {
		if (id == null) {
			return null;
		}
		Vlp entity = super.findById(id);
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
	public Vlp findByVpisvaneId(Integer idVpisvane) throws DbErrorException {
		if (idVpisvane == null) {
			return null;
		}

		Vlp entity;
		try {
			@SuppressWarnings("unchecked")
			List<Vlp> list = createQuery("select x from Vlp x where x.idVpisvane = ?1") //
					.setParameter(1, idVpisvane).getResultList();
			if (list.isEmpty()) {
				return null;
			}
			entity = list.get(0);

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на Vlp по idVpisvane=" + idVpisvane, e);
		}
		loadFull(entity);

		return entity;
	}

	/** */
	@Override
	public Vlp save(Vlp entity) throws DbErrorException {
		Vlp saved = save(entity, new ArrayList<>());
		return saved;
	}

	/**
	 * след базовия merge прави обработка на transient полетата
	 */
	@Override
	protected Vlp merge(Vlp entity) throws DbErrorException {
		Vlp merged = super.merge(entity);

		mergeTransientProps(entity, merged);

		return merged;
	}

	/**
	 * след базовия persist прави обработка на transient полетата
	 */
	@Override
	protected void persist(Vlp entity) throws DbErrorException {
		super.persist(entity);

		mergeTransientProps(entity, entity);
	}

	/**
	 * преди базовия remove изтрива transient полетата
	 */
	@Override
	protected void remove(Vlp entity) throws DbErrorException, ObjectInUseException {
		try {
			int deleted = createNativeQuery("delete from vlp_farm_form where vlp_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} vlp_farm_form за vlp_id={}", deleted, entity.getId());

			deleted = createNativeQuery("delete from vlp_opakovka where vlp_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} vlp_opakovka за vlp_id={}", deleted, entity.getId());

		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на свързани обекти на ВЛП!", e);
		}
		super.remove(entity);
	}

	/**
	 * Допълва всичко, което е jpa-lazy или transient
	 *
	 * @param entity
	 * @throws DbErrorException
	 */
	void loadFull(Vlp entity) throws DbErrorException {
		if (entity.getVlpLice().size() > 0) { // lazy, но трябва и лицето да се вдигне
			ReferentDAO referentDao = new ReferentDAO(getUser());

			for (VlpLice vlpLice : entity.getVlpLice()) {
				if (vlpLice.getCodeRef() != null) {
					vlpLice.setReferent(referentDao.findByCodeRef(vlpLice.getCodeRef()));
				}
			}
		}

		// не винаги ги има тези долу и да се направи както е в дейностите с животни

		entity.getVlpVeshtva().size(); // lazy
		entity.getVlpVidJiv().size(); // lazy
		entity.getVlpPrilagane().size(); // lazy

		try {
			@SuppressWarnings("unchecked")
			List<Integer> farmFormList = createQuery( //
					"select x.farmForm from VlpFarmForm x where x.vlpId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
			if (!farmFormList.isEmpty()) {
				entity.setFarmFormList(farmFormList);
				entity.setDbFarmFormList(new ArrayList<>(farmFormList));
			}

			@SuppressWarnings("unchecked")
			List<Integer> opakovkaList = createQuery( //
					"select x.opakovka from VlpOpakovka x where x.vlpId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
			if (!opakovkaList.isEmpty()) {
				entity.setOpakovkaList(opakovkaList);
				entity.setDbOpakovkaList(new ArrayList<>(opakovkaList));
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
	void mergeTransientProps(Vlp entity, Vlp managed) throws DbErrorException {

		//
		// -- vlp_farm_form.farm_form
		if (entity.getFarmFormList() != null || entity.getDbFarmFormList() != null) { // само ако някъде е сетнато нещо
			managed.setFarmFormList(entity.getFarmFormList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbFarmFormList(), entity.getFarmFormList(), new VlpFarmForm(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbFarmFormList(entity.getFarmFormList() != null ? new ArrayList<>(entity.getFarmFormList()) : null);
			managed.setDbFarmFormList(entity.getDbFarmFormList());
		}

		//
		// -- vlp_opakovka.opakovka
		if (entity.getOpakovkaList() != null || entity.getDbOpakovkaList() != null) { // само ако някъде е сетнато нещо
			managed.setOpakovkaList(entity.getOpakovkaList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbOpakovkaList(), entity.getOpakovkaList(), new VlpOpakovka(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbOpakovkaList(entity.getOpakovkaList() != null ? new ArrayList<>(entity.getOpakovkaList()) : null);
			managed.setDbOpakovkaList(entity.getDbOpakovkaList());
		}

	}

	/**
	 * Запис на влп и всевъзможните му логики. Подават се и текущо записаните лица, защото има логики по проверка на еднакви лица.
	 *
	 * @param entity
	 * @param allReferents
	 * @return
	 * @throws DbErrorException
	 */
	Vlp save(Vlp entity, List<Referent> allReferents) throws DbErrorException {
		if (entity.getVlpVeshtva() == null) {
			entity.setVlpVeshtva(new ArrayList<>()); // за да може да се направи повторен запис след първият
		} else {
			for (VlpVeshtva v : entity.getVlpVeshtva()) {
				if (v.getVidIdentifier() != null) {
					v.setVidIdentifier(v.getVidIdentifier().manage());
				}
			}
		}
		if (entity.getVlpVidJiv() == null) {
			entity.setVlpVidJiv(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}
		if (entity.getVlpPrilagane() == null) {
			entity.setVlpPrilagane(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}

		Map<Integer, Referent> referentMap = new HashMap<>(); // заради JPA MEGRE
		if (entity.getVlpLice() == null) {
			entity.setVlpLice(new ArrayList<>()); // за да може да се направи повторен запис след първият

		} else {
			ReferentDAO referentDao = new ReferentDAO(getUser());
			for (VlpLice vlpLice : entity.getVlpLice()) {

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

		Vlp saved = super.save(entity);

		// VlpLice минава през jpa но заради MERGE трябват врътки !?!?
		if (saved.getVlpLice() != null && !saved.getVlpLice().isEmpty()) {
			for (VlpLice vlpLice : saved.getVlpLice()) {
				vlpLice.setReferent(referentMap.get(vlpLice.getCodeRef()));
			}
		}

		return saved;
	}
}
