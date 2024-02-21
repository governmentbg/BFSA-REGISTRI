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
import com.ib.babhregs.db.dto.EventDeinostFuraji;
import com.ib.babhregs.db.dto.EventDeinostFurajiGip;
import com.ib.babhregs.db.dto.EventDeinostFurajiKategoria;
import com.ib.babhregs.db.dto.EventDeinostFurajiSgp;
import com.ib.babhregs.db.dto.EventDeinostFurajiVid;
import com.ib.babhregs.db.dto.MpsDeinost;
import com.ib.babhregs.db.dto.MpsFuraji;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.SearchUtils;

/**
 * DAO for {@link EventDeinostFuraji}
 */
public class EventDeinostFurajiDAO extends AbstractDAO<EventDeinostFuraji> {

	/**  */
	private static final Logger LOGGER = LoggerFactory.getLogger(EventDeinostFurajiDAO.class);

	/** @param user */
	public EventDeinostFurajiDAO(ActiveUser user) {
		super(EventDeinostFuraji.class, user);
	}

	/**
	 * + разните му не JPA каши
	 */
	@Override
	public EventDeinostFuraji findById(Object id) throws DbErrorException {
		if (id == null) {
			return null;
		}
		EventDeinostFuraji entity = super.findById(id);
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
	public EventDeinostFuraji findByVpisvaneId(Integer idVpisvane) throws DbErrorException {
		if (idVpisvane == null) {
			return null;
		}

		EventDeinostFuraji entity;
		try {
			@SuppressWarnings("unchecked")
			List<EventDeinostFuraji> list = createQuery("select x from EventDeinostFuraji x where x.idVpisvane = ?1") //
					.setParameter(1, idVpisvane).getResultList();
			if (list.isEmpty()) {
				return null;
			}
			entity = list.get(0);

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на EventDeinostFuraji по idVpisvane=" + idVpisvane, e);
		}
		loadFull(entity);

		return entity;
	}

	/**
	 * @param vpisvaneId
	 * @return
	 * @throws DbErrorException
	 */
	public List<Object[]> findMigrInfo(Integer vpisvaneId) throws DbErrorException {
		List<Object[]> info = new ArrayList<>();
		try {
			@SuppressWarnings("unchecked")
			List<Object[]> dataRows = createNativeQuery( // данни за вписването
					"select * from migr_furaji m where m.vpisvane_id = ?1") //
					.setParameter(1, vpisvaneId).getResultList();
			if (dataRows.isEmpty()) {
				return info; // няма данни
			}
			Object[] dataArr = dataRows.get(0);

			@SuppressWarnings("unchecked")
			List<Object[]> headerRows = createNativeQuery( // имената на колоните
					"select * from migr_furaji m where m.header = 1 and m.register = ?1 and m.file_key = ?2") //
					.setParameter(1, dataArr[2]).setParameter(2, dataArr[3]).getResultList();
			if (headerRows.isEmpty()) {
				return info; // няма данни
			}
			Object[] headerArr = headerRows.get(0);

			for (int i = 6; i < 100; i++) { // 6=A
				String header = (String) headerArr[i];

				if (SearchUtils.isEmpty(header)) {
					break;
				}
				header = header.replace("\r\n", "</br>");
				header = header.replace("\n", "</br>");

				String data = (String) dataArr[i];
				if (data != null) {
					data = data.replace("\r\n", "</br>");
					data = data.replace("\n", "</br>");
				}

				info.add(new Object[] { header, data });
			}

		} catch (Exception e) {
			throw new DbErrorException("Грешка при зареждане на данни.", e);
		}
		return info;
	}

	/** */
	@Override
	public EventDeinostFuraji save(EventDeinostFuraji entity) throws DbErrorException {
		EventDeinostFuraji saved = save(entity, new ArrayList<>());
		return saved;
	}

	/**
	 * след базовия merge прави обработка на transient полетата
	 */
	@Override
	protected EventDeinostFuraji merge(EventDeinostFuraji entity) throws DbErrorException {
		EventDeinostFuraji merged = super.merge(entity);

		mergeTransientProps(entity, merged);

		return merged;
	}

	/**
	 * след базовия persist прави обработка на transient полетата
	 */
	@Override
	protected void persist(EventDeinostFuraji entity) throws DbErrorException {
		super.persist(entity);

		mergeTransientProps(entity, entity);
	}

	/**
	 * преди базовия remove изтрива transient полетата
	 */
	@Override
	protected void remove(EventDeinostFuraji entity) throws DbErrorException, ObjectInUseException {
		try {
			int deleted = createNativeQuery("delete from event_deinost_furaji_vid where event_deinost_furaji_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} event_deinost_furaji_vid за event_deinost_furaji_id={}", deleted, entity.getId());

			deleted = createNativeQuery("delete from event_deinost_furaji_sgp where event_deinost_furaji_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} event_deinost_furaji_sgp за event_deinost_furaji_id={}", deleted, entity.getId());

			deleted = createNativeQuery("delete from event_deinost_furaji_gip where event_deinost_furaji_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} event_deinost_furaji_gip за event_deinost_furaji_id={}", deleted, entity.getId());

			deleted = createNativeQuery("delete from event_deinost_furaji_kategoria where event_deinost_furaji_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} event_deinost_furaji_kategoria за event_deinost_furaji_id={}", deleted, entity.getId());

		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на свързани обекти на Дейности с фуражи!", e);
		}
		super.remove(entity);
	}

	/**
	 * Допълва всичко, което е jpa-lazy или transient
	 *
	 * @param entity
	 * @throws DbErrorException
	 */
	void loadFull(EventDeinostFuraji entity) throws DbErrorException {
		entity.getEventDeinostFurajiPredmet().size(); // lazy
		entity.getEventDeinostFurajiPrednaznJiv().size(); // lazy

		if (entity.getObektDeinostDeinost().size() > 0) { // lazy, но трябва и обекта да се вдигне
			ObektDeinostDAO obektDeinostDao = new ObektDeinostDAO(getUser());
			for (ObektDeinostDeinost odd : entity.getObektDeinostDeinost()) {
				odd.setObektDeinost(obektDeinostDao.findById(odd.getObektDeinostId(), false)); // без да се вдигат лицата
			}
		}

		try {
			@SuppressWarnings("unchecked")
			List<Integer> vidList = createQuery( //
					"select x.vid from EventDeinostFurajiVid x where x.eventDeinostFurajiId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
			entity.setVidList(vidList); // това е задължително и ако няма все пак нещо да е празен списък !
			entity.setDbVidList(new ArrayList<>(vidList));

			if (vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_CERT_DOBRA_PRAKTIKA)) { // тези долу са приложими само за тази дейност
				entity.getEventDeinostFurajiSert().size(); // lazy
			}

			if (vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_REG_PREVOZVACHI)) { // тези долу са приложими само за тази дейност
				if (entity.getMpsDeinost().size() > 0) { // lazy, но трябва и MpsFuraji да се вдигне
					MpsFurajiDAO mpsFurajiDao = new MpsFurajiDAO(getUser());
					for (MpsDeinost md : entity.getMpsDeinost()) {
						md.setMpsFuraji(mpsFurajiDao.findById(md.getId()));
					}
				}
			}

			if (vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_SJP_NAUCHNI_CELI)) { // тези долу са приложими само за тази дейност
				@SuppressWarnings("unchecked")
				List<Integer> celList = createQuery( //
						"select x.cel from EventDeinostFurajiSgp x where x.eventDeinostFurajiId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
				if (!celList.isEmpty()) {
					entity.setCelList(celList);
					entity.setDbCelList(new ArrayList<>(celList));
				}

			}

			if (vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_SJP) //
					|| vidList.contains(BabhConstants.CODE_ZNACHENIE_DEIN_IZNOS_FURAJI)) { // тези долу са приложими само за тази дейност
				@SuppressWarnings("unchecked")
				List<Integer> gipList = createQuery( //
						"select x.gip from EventDeinostFurajiGip x where x.eventDeinostFurajiId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
				if (!gipList.isEmpty()) {
					entity.setGipList(gipList);
					entity.setDbGipList(new ArrayList<>(gipList));
				}
			}

			@SuppressWarnings("unchecked")
			List<Integer> kategoriaList = createQuery( //
					"select x.kategoria from EventDeinostFurajiKategoria x where x.eventDeinostFurajiId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
			if (!kategoriaList.isEmpty()) {
				entity.setKategoriaList(kategoriaList);
				entity.setDbKategoriaList(new ArrayList<>(kategoriaList));
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
	void mergeTransientProps(EventDeinostFuraji entity, EventDeinostFuraji managed) throws DbErrorException {

		//
		// -- event_deinost_furaji_vid.vid
		if (entity.getVidList() != null || entity.getDbVidList() != null) { // само ако някъде е сетнато нещо
			managed.setVidList(entity.getVidList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbVidList(), entity.getVidList(), new EventDeinostFurajiVid(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbVidList(entity.getVidList() != null ? new ArrayList<>(entity.getVidList()) : null);
			managed.setDbVidList(entity.getDbVidList());
		}

		//
		// -- event_deinost_furaji_sgp.cel
		if (entity.getCelList() != null || entity.getDbCelList() != null) { // само ако някъде е сетнато нещо
			managed.setCelList(entity.getCelList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbCelList(), entity.getCelList(), new EventDeinostFurajiSgp(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbCelList(entity.getCelList() != null ? new ArrayList<>(entity.getCelList()) : null);
			managed.setDbCelList(entity.getDbCelList());
		}

		//
		// -- event_deinost_furaji_gip.gip
		if (entity.getGipList() != null || entity.getDbGipList() != null) { // само ако някъде е сетнато нещо
			managed.setGipList(entity.getGipList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbGipList(), entity.getGipList(), new EventDeinostFurajiGip(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbGipList(entity.getGipList() != null ? new ArrayList<>(entity.getGipList()) : null);
			managed.setDbGipList(entity.getDbGipList());
		}

		//
		// -- event_deinost_furaji_kategoria.kategoria
		if (entity.getKategoriaList() != null || entity.getDbKategoriaList() != null) { // само ако някъде е сетнато нещо
			managed.setKategoriaList(entity.getKategoriaList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbKategoriaList(), entity.getKategoriaList(), new EventDeinostFurajiKategoria(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbKategoriaList(entity.getKategoriaList() != null ? new ArrayList<>(entity.getKategoriaList()) : null);
			managed.setDbKategoriaList(entity.getDbKategoriaList());
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
	EventDeinostFuraji save(EventDeinostFuraji entity, List<Referent> allReferents) throws DbErrorException {
		// с това долу ще може да се види дали даден мапнат през jpa списък е зареден на лоад. ако не значи не се използва и не трябва да
		// се цикли по него.
		PersistenceUnitUtil util = getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();

		if (entity.getEventDeinostFurajiPredmet() == null) {
			entity.setEventDeinostFurajiPredmet(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}
		if (entity.getEventDeinostFurajiPrednaznJiv() == null) {
			entity.setEventDeinostFurajiPrednaznJiv(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}
		if (entity.getEventDeinostFurajiSert() == null) {
			entity.setEventDeinostFurajiSert(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}

		Map<Integer, MpsFuraji> mpsFurajiMap = null; // заради JPA MEGRE
		if (entity.getMpsDeinost() == null) {
			entity.setMpsDeinost(new ArrayList<>()); // за да може да се направи повторен запис след първият

		} else if (entity.getId() == null || util.isLoaded(entity, "mpsDeinost")) {
			mpsFurajiMap = new HashMap<>();

			MpsFurajiDAO mpsFurajiDao = new MpsFurajiDAO(getUser());
			for (MpsDeinost md : entity.getMpsDeinost()) {

				if (md.getMpsFuraji() != null && md.getMpsFuraji().getVid() != null) {
					md.setMpsFuraji(mpsFurajiDao.save(md.getMpsFuraji()));

					md.setMpsId(md.getMpsFuraji().getId());

					mpsFurajiMap.put(md.getMpsId(), md.getMpsFuraji());
				}
				md.setTablEventDeinost(entity.getCodeMainObject());
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

			ObektDeinostDAO obektDeinostDao = new ObektDeinostDAO(getUser());
			for (ObektDeinostDeinost odd : entity.getObektDeinostDeinost()) {

				if (odd.getObektDeinost() != null) {
					if (entity.isSaveObektDeinost()) {
						odd.getObektDeinost().setVid(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_FURAJI);
						odd.setObektDeinost(obektDeinostDao.save(odd.getObektDeinost(), false, allReferents)); // без лицата да се записват
					}
					odd.setObektDeinostId(odd.getObektDeinost().getId());

					obektDeinostMap.put(odd.getObektDeinostId(), odd.getObektDeinost());
				}
				odd.setTablEventDeinost(entity.getCodeMainObject());
				if (odd.getDateBeg() == null) {
					odd.setDateBeg(new Date());
				}
			}
		}

		EventDeinostFuraji saved = super.save(entity);
		// EventDeinostFurajiPredmet минава през jpa
		// EventDeinostFurajiPrednaznJiv минава през jpa
		// EventDeinostFurajiSert минава през jpa

		// MpsDeinost минава през jpa но след MERGE трябват врътки !?!?
		if (mpsFurajiMap != null && saved.getMpsDeinost() != null && !saved.getMpsDeinost().isEmpty()) {
			for (MpsDeinost md : saved.getMpsDeinost()) {
				md.setMpsFuraji(mpsFurajiMap.get(md.getMpsId()));
			}
		}

		// ObektDeinostDeinost минава през jpa но заради MERGE трябват врътки !?!?
		if (obektDeinostMap != null && saved.getObektDeinostDeinost() != null && !saved.getObektDeinostDeinost().isEmpty()) {
			for (ObektDeinostDeinost odd : saved.getObektDeinostDeinost()) {
				odd.setObektDeinost(obektDeinostMap.get(odd.getObektDeinostId()));
			}
		}
		return saved;
	}
}
