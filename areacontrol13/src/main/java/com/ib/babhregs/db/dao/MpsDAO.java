package com.ib.babhregs.db.dao;

import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.TransientPropUtil;
import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.db.dto.MpsCategory;
import com.ib.babhregs.db.dto.MpsDeinost;
import com.ib.babhregs.db.dto.MpsLice;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.search.MPSsearch;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.SearchUtils;

/**
 * DAO for {@link Mps}
 */
public class MpsDAO extends AbstractDAO<Mps> {

	/**  */
	private static final Logger LOGGER = LoggerFactory.getLogger(MpsDAO.class);

	/** @param user */
	public MpsDAO(ActiveUser user) {
		super(Mps.class, user);
	}

	/**
	 * + разни lazy атрибути<br>
	 * <b>Зареда и лицата чрез</b> {@link ReferentDAO#findByCodeRef(Integer)}
	 */
	@Override
	public Mps findById(Object id) throws DbErrorException {
		Mps entity = findById(id, true);
		return entity;
	}

	/**
	 * @param id
	 * @param loadReferent дали да се зареди лицето в {@link MpsLice#setReferent(Referent)}
	 * @return
	 * @throws DbErrorException
	 */
	public Mps findById(Object id, boolean loadReferent) throws DbErrorException {
		if (id == null) {
			return null;
		}
		Mps entity = super.findById(id);
		if (entity == null) {
			return entity;
		}
		loadFull(entity, loadReferent);

		return entity;
	}

	/**
	 * Намира данни за мпс-та дошли с това заявление за първоначално вписване</br>
	 * [0]-{@link Mps} заредено в всички данни</br>
	 * [1]-vp_id</br>
	 * [2]-vp_status</br>
	 *
	 * @param idZaqavlenie
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> findMpsDataByZaqavlenie(Integer idZaqavlenie) throws DbErrorException {
		if (idZaqavlenie == null) {
			return new ArrayList<>();
		}

		List<Object[]> list;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select m, v.id, v.status");
			sql.append(" from Vpisvane v ");
			sql.append(" inner join Mps m on m.id = v.idLicenziant ");
			sql.append(" where v.licenziantType = ?1 and v.idZaqavlenie = ?2 ");
			sql.append(" order by m.id ");

			list = createQuery(sql.toString()) //
					.setParameter(1, BabhConstants.CODE_ZNACHENIE_TIP_LICENZ_MPS).setParameter(2, idZaqavlenie) //
					.getResultList();

			for (Object[] row : list) { // заредено е все едно с findById
				loadFull((Mps) row[0], true);
			}

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на данни за МПС по заявление с ИД=" + idZaqavlenie, e);
		}
		return list;
	}

	/**
	 * yoni - function that will return mps for particular number или id
	 *
	 * [0]-id</br>
	 * [1]-vid</br>
	 * [2]-null marka-няма вече</br>
	 * [3]-model</br>
	 * [4]-nomer</br>
	 * [5]-mps_lice.codeRef (собственик)</br>
	 * [6]-vpisvane.status Рег.Животни - като може и да е NULL</br>
	 * [7]-vpisvane.id_register Рег.Животни - като може и да е NULL</br>
	 * [8]-vpisvane.status Рег.Фуражи - като може и да е NULL</br>
	 * [9]-vpisvane.id_register Рег.Фуражи - като може и да е NULL</br>
	 * [10]-plosht</br>
	 * [11]-vpisvane.id Рег.Животни - като може и да е NULL</br>
	 * [12]-vpisvane.id Рег.Фуражи - като може и да е NULL</br>
	 *
	 * @param mpsId
	 * @param nomer
	 * @see MpsDeinost#getMpsInfo()
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	public Object[] findMpsInfoByIdOrNomer(Integer mpsId, String nomer) throws DbErrorException {
		Object[] mpsInfo = null;
		try {
			StringBuilder sql = new StringBuilder();

			sql.append(" select m.id, m.vid, null marka, m.model, m.nomer, ml.code_ref, v.status, v.id_register, v2.status stsFur, v2.id_register regFur, m.plosht ");
			sql.append(" , v.id vpIdJiv, v2.id vpIdFur ");
			sql.append(" from mps m ");
			sql.append(" left outer join mps_lice ml on ml.mps_id = m.id and ml.tip_vraz = :sobst "); // само собствник в този метод
			sql.append(" left outer join vpisvane v on v.id_licenziant = m.id and v.licenziant_type = :licType and v.code_page = :jivPage ");
			sql.append(" left outer join vpisvane v2 on v2.id_licenziant = m.id and v2.licenziant_type = :licType and v2.code_page = :furPage ");
			sql.append(" where ");

			List<Object[]> rows = null;

			if (mpsId != null) { // приоритетно
				rows = createNativeQuery(sql + " m.id = :mId ") //
						.setParameter("mId", mpsId) //
						.setParameter("sobst", BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK) //
						.setParameter("licType", BabhConstants.CODE_ZNACHENIE_TIP_LICENZ_MPS) //
						.setParameter("jivPage", CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ).setParameter("furPage", CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS) //
						.getResultList();

			} else {
				nomer = SearchUtils.trimToNULL_Upper(nomer);

				if (nomer != null) { // ако има дублиране по номер ще даде последното по ИД
					rows = createNativeQuery(sql + " upper(m.nomer) = :mNomer order by m.id desc ") //
							.setParameter("mNomer", MPSsearch.convertMpsNomer(nomer)) // винаги по ЛАТ търсим
							.setParameter("sobst", BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK) //
							.setParameter("licType", BabhConstants.CODE_ZNACHENIE_TIP_LICENZ_MPS) //
							.setParameter("jivPage", CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ).setParameter("furPage", CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS) //
							.getResultList();

				} else {
					LOGGER.error("findMpsInfoByIdOrNomer - NULL argumenets (mpsId, nomer)");
				}
			}

			if (rows != null && !rows.isEmpty()) {
				mpsInfo = rows.get(0);
			}

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на данни за МПС!", e);
		}
		return mpsInfo;
	}

	/**
	 * Запис на мпс и всевъзможните му логики
	 *
	 * @param entity
	 * @return
	 * @throws DbErrorException
	 */
	@Override
	public Mps save(Mps entity) throws DbErrorException {
		Mps saved = save(entity, true, new ArrayList<>());
		return saved;
	}

	/**
	 * след базовия merge прави обработка на transient полетата
	 */
	@Override
	protected Mps merge(Mps entity) throws DbErrorException {
		Mps merged = super.merge(entity);

		mergeTransientProps(entity, merged);

		return merged;
	}

	/**
	 * след базовия persist прави обработка на transient полетата
	 */
	@Override
	protected void persist(Mps entity) throws DbErrorException {
		super.persist(entity);

		mergeTransientProps(entity, entity);
	}

	/**
	 * преди базовия remove изтрива transient полетата
	 */
	@Override
	protected void remove(Mps entity) throws DbErrorException, ObjectInUseException {
		try {
			int deleted = createNativeQuery("delete from mps_category where mps_id = ?1").setParameter(1, entity.getId()).executeUpdate();
			LOGGER.debug("Изтрити са {} mps_category за mps_id={}", deleted, entity.getId());

		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на свързани обекти на МПС!", e);
		}
		super.remove(entity);
	}

	/**
	 * Допълва всичко, което е jpa-lazy или transient
	 *
	 * @param entity
	 * @param loadReferent
	 * @throws DbErrorException
	 */
	void loadFull(Mps entity, boolean loadReferent) throws DbErrorException {
		entity.getMpsKapacitetJiv().size(); // lazy

		if (entity.getMpsLice().size() > 0 && loadReferent) { // lazy, но трябва и лицето да се вдигне (само ако се иска)
			ReferentDAO referentDao = new ReferentDAO(getUser());

			for (MpsLice mpsLice : entity.getMpsLice()) {
				if (mpsLice.getCodeRef() != null) {
					mpsLice.setReferent(referentDao.findByCodeRef(mpsLice.getCodeRef()));
				}
			}
		}

		try {
			@SuppressWarnings("unchecked")
			List<Integer> categoryList = createQuery( //
					"select x.category from MpsCategory x where x.mpsId = ?1 order by x.id").setParameter(1, entity.getId()).getResultList();
			if (!categoryList.isEmpty()) {
				entity.setCategoryList(categoryList);
				entity.setDbCategoryList(new ArrayList<>(categoryList));
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
	void mergeTransientProps(Mps entity, Mps managed) throws DbErrorException {

		//
		// -- mps_category.category
		if (entity.getCategoryList() != null || entity.getDbCategoryList() != null) { // само ако някъде е сетнато нещо
			managed.setCategoryList(entity.getCategoryList()); // сетвам трансиент и в managed обекта

			// обработката
			TransientPropUtil.saveMultiClassifPropList(entity.getDbCategoryList(), entity.getCategoryList(), new MpsCategory(entity.getId()));
			// за да се знае пак какво е старото състоянието за последващи записи
			entity.setDbCategoryList(entity.getCategoryList() != null ? new ArrayList<>(entity.getCategoryList()) : null);
			managed.setDbCategoryList(entity.getDbCategoryList());
		}
	}

	/**
	 * Запис на mps и всевъзможните му логики. Подават се и текущо записаните лица, защото има логики по проверка на еднакви лица.
	 *
	 * @param entity
	 * @param saveReferent Referent се записва само ако изрично се иска, защото има случаи в които може да е записан или да не трябва
	 * @param allReferents
	 * @return
	 * @throws DbErrorException
	 */
	Mps save(Mps entity, boolean saveReferent, List<Referent> allReferents) throws DbErrorException {
		if (entity.getMpsKapacitetJiv() == null) {
			entity.setMpsKapacitetJiv(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}

		Map<Integer, Referent> referentMap = new HashMap<>(); // заради JPA MEGRE
		if (entity.getMpsLice() == null) {
			entity.setMpsLice(new ArrayList<>()); // за да може да се направи повторен запис след първият

		} else {
			ReferentDAO referentDao = new ReferentDAO(getUser());
			for (MpsLice mpsLice : entity.getMpsLice()) {

				if (mpsLice.getReferent() != null && mpsLice.getReferent().getRefType() != null) {
					if (saveReferent) {
						mpsLice.setReferent(referentDao.save(mpsLice.getReferent(), allReferents, true));
					}
					mpsLice.setCodeRef(mpsLice.getReferent().getCode());

					referentMap.put(mpsLice.getCodeRef(), mpsLice.getReferent());
				}
			}
		}

		entity.setNomer(MPSsearch.convertMpsNomer(entity.getNomer()));

		Mps saved = super.save(entity);

		// MpsLice минава през jpa но заради MERGE трябват врътки !?!?
		if (saved.getMpsLice() != null && !saved.getMpsLice().isEmpty()) {
			for (MpsLice mpsLice : saved.getMpsLice()) {
				mpsLice.setReferent(referentMap.get(mpsLice.getCodeRef()));
			}
		}

		return saved;
	}
}
