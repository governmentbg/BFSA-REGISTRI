package com.ib.babhregs.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.TransientPropUtil;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostLica;
import com.ib.babhregs.db.dto.ObektDeinostPrednaznachenie;
import com.ib.babhregs.db.dto.Referent;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.SearchUtils;

/**
 * DAO for {@link ObektDeinost}
 *
 * @author belev
 */
public class ObektDeinostDAO extends AbstractDAO<ObektDeinost> {

	/**  */
	private static final Logger LOGGER = LoggerFactory.getLogger(ObektDeinostDAO.class);

	/** @param user */
	public ObektDeinostDAO(ActiveUser user) {
		super(ObektDeinost.class, user);
	}

	/**
	 * + разни lazy атрибути<br>
	 * <b>Зареда и лицата чрез</b> {@link ReferentDAO#findByCodeRef(Integer)}
	 */
	@Override
	public ObektDeinost findById(Object id) throws DbErrorException {
		ObektDeinost entity = findById(id, true);
		return entity;
	}

	/**
	 * @param id
	 * @param loadReferent дали да се зареди лицето в {@link ObektDeinostLica#setReferent(Referent)}
	 * @return
	 * @throws DbErrorException
	 */
	public ObektDeinost findById(Object id, boolean loadReferent) throws DbErrorException {
		if (id == null) {
			return null;
		}
		ObektDeinost entity = super.findById(id);
		if (entity == null) {
			return entity;
		}
		loadFull(entity, loadReferent);

		return entity;
	}

	/**
	 * Зареждане на обект дейност по идентификационни данни и вид на обекта.</br>
	 * Търси се с OR за следните колони: reg_nomer_star, reg_nom.</br>
	 * Ако няма намерено дава празен списък. Може да има повече от един заради търсенето по няколко колони.
	 *
	 * @param nomer        проверява се за всички познати номера, по които може да се идентифицира
	 * @param vid          налага се условие само ако се иска
	 * @param loadReferent дали да се зареди лицето в {@link ObektDeinostLica#setReferent(Referent)}
	 * @return сортирани по ИД DESC
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public List<ObektDeinost> findByIdent(String nomer, Integer vid, boolean loadReferent) throws DbErrorException {
		String arg = SearchUtils.trimToNULL_Upper(nomer);
		if (arg == null) {
			return null;
		}

		List<ObektDeinost> list;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select x from ObektDeinost x where ( upper(x.regNomerStar) = :nomer or upper(x.regNom) = :nomer ) ");
			if (vid != null) {
				sql.append(" and x.vid = :vidArg ");
			}

			Query query = createQuery(sql + " order by x.id desc ").setParameter("nomer", arg);
			if (vid != null) {
				query.setParameter("vidArg", vid);
			}
			list = query.getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при идентификация на ObektDeinost с подаден номер=" + nomer, e);
		}

		for (ObektDeinost entity : list) {
			loadFull(entity, loadReferent);
		}
		return list;

	}

	/**
	 * Зареждане на данни по рег.номер и вид на обекта
	 *
	 * @param regNomer
	 * @param vid
	 * @param loadReferent дали да се зареди лицето в {@link ObektDeinostLica#setReferent(Referent)}
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public ObektDeinost findByRegNomer(String regNomer, Integer vid, boolean loadReferent) throws DbErrorException {
		String arg = SearchUtils.trimToNULL_Upper(regNomer);
		if (arg == null) {
			return null;
		}

		ObektDeinost entity;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select x from ObektDeinost x where upper(x.regNom) = ?1 ");
			if (vid != null) {
				sql.append(" and x.vid = ?2 ");
			}

			Query query = createQuery(sql + " order by x.id desc ").setParameter(1, arg);
			if (vid != null) {
				query.setParameter(2, vid);
			}

			List<ObektDeinost> list = query.getResultList();
			if (list.isEmpty()) {
				return null;
			}
			entity = list.get(0);

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на ObektDeinost по regNom=" + regNomer, e);
		}
		loadFull(entity, loadReferent);

		return entity;

	}

	/** */
	@Override
	public ObektDeinost save(ObektDeinost entity) throws DbErrorException {
		ObektDeinost saved = save(entity, true, new ArrayList<>());
		return saved;
	}

	/**
	 * след базовия merge прави обработка на transient полетата
	 */
	@Override
	protected ObektDeinost merge(ObektDeinost entity) throws DbErrorException {
		ObektDeinost merged = super.merge(entity);

		mergeTransientProps(entity, merged);

		return merged;
	}

	/**
	 * след базовия persist прави обработка на transient полетата
	 */
	@Override
	protected void persist(ObektDeinost entity) throws DbErrorException {
		super.persist(entity);

		mergeTransientProps(entity, entity);
	}

	/**
	 * преди базовия remove изтрива transient полетата
	 */
	@Override
	protected void remove(ObektDeinost entity) throws DbErrorException, ObjectInUseException {
		try {
			int deleted = createNativeQuery("delete from obekt_deinost_prednaznachenie where obekt_deinost_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} obekt_deinost_prednaznachenie за obekt_deinost_id={}", deleted, entity.getId());

		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на свързани обекти на Обект на дейност!", e);
		}
		super.remove(entity);
	}

	/**
	 * Допълва всичко, което е jpa-lazy или transient
	 */
	void loadFull(ObektDeinost entity, boolean loadReferent) throws DbErrorException {
		if (entity.getObektDeinostLica().size() > 0 && loadReferent) { // lazy, но трябва и лицето да се вдигне (само ако се иска)
			ReferentDAO referentDao = new ReferentDAO(getUser());

			for (ObektDeinostLica obektDeinostLica : entity.getObektDeinostLica()) {
				if (obektDeinostLica.getCodeRef() != null) {
					obektDeinostLica.setReferent(referentDao.findByCodeRef(obektDeinostLica.getCodeRef()));
				}
			}
		}

		try {
			@SuppressWarnings("unchecked")
			List<Integer> prednaznachenieList = createQuery( //
					"select x.prednaznachenie from ObektDeinostPrednaznachenie x where x.obektDeinostId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
			if (!prednaznachenieList.isEmpty()) {
				entity.setPrednaznachenieList(prednaznachenieList);
				entity.setDbPrednaznachenieList(new ArrayList<>(prednaznachenieList));
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
	void mergeTransientProps(ObektDeinost entity, ObektDeinost managed) throws DbErrorException {

		//
		// -- obekt_deinost_prednaznachenie.prednaznachenie
		if (entity.getPrednaznachenieList() != null || entity.getDbPrednaznachenieList() != null) { // само ако някъде е сетнато нещо
			managed.setPrednaznachenieList(entity.getPrednaznachenieList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbPrednaznachenieList(), entity.getPrednaznachenieList(), new ObektDeinostPrednaznachenie(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbPrednaznachenieList(entity.getPrednaznachenieList() != null ? new ArrayList<>(entity.getPrednaznachenieList()) : null);
			managed.setDbPrednaznachenieList(entity.getDbPrednaznachenieList());
		}
	}

	/**
	 * Запис на обект дейност и всевъзможните му логики. Подават се и текущо записаните лица, защото има логики по проверка на еднакви
	 * лица.
	 *
	 * @param entity
	 * @param saveReferent Referent се записва само ако изрично се иска, защото има случаи в които може да е записан
	 * @param allReferents
	 * @return
	 * @throws DbErrorException
	 */
	ObektDeinost save(ObektDeinost entity, boolean saveReferent, List<Referent> allReferents) throws DbErrorException {
		Map<Integer, Referent> referentMap = new HashMap<>(); // заради JPA MEGRE
		if (entity.getObektDeinostLica() == null) {
			entity.setObektDeinostLica(new ArrayList<>()); // за да може да се направи повторен запис след първият

		} else {
			ReferentDAO referentDao = new ReferentDAO(getUser());
			for (ObektDeinostLica obektDeinostLica : entity.getObektDeinostLica()) {

				if (obektDeinostLica.getReferent() != null && obektDeinostLica.getReferent().getRefType() != null) {
					if (saveReferent) {
						obektDeinostLica.setReferent(referentDao.save(obektDeinostLica.getReferent(), allReferents, true));
					}
					obektDeinostLica.setCodeRef(obektDeinostLica.getReferent().getCode());

					referentMap.put(obektDeinostLica.getCodeRef(), obektDeinostLica.getReferent());
				}
				if (obektDeinostLica.getDateBeg() == null) {
					obektDeinostLica.setDateBeg(new Date());
				}
			}
		}

		entity.fixEmptyStringValues();

		ObektDeinost saved = super.save(entity);

		// ObektDeinostLica минава през jpa но заради MERGE трябват врътки !?!?
		if (saved.getObektDeinostLica() != null && !saved.getObektDeinostLica().isEmpty()) {
			for (ObektDeinostLica obektDeinostLica : saved.getObektDeinostLica()) {
				obektDeinostLica.setReferent(referentMap.get(obektDeinostLica.getCodeRef()));
			}
		}

		return saved;
	}
}
