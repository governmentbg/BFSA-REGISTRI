package com.ib.babhregs.db.dao;

import static com.ib.babhregs.system.BabhConstants.CODE_CLASSIF_REGISTRATURI;
import static com.ib.babhregs.system.BabhConstants.CODE_CLASSIF_REGISTRI;
import static com.ib.babhregs.system.BabhConstants.CODE_CLASSIF_REISTRATURA_SETTINGS;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_JOURNAL_REGISTER;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_REF_TYPE_EMPL;
import static com.ib.indexui.system.Constants.CODE_CLASSIF_ADMIN_STR;
import static com.ib.system.SysConstants.CODE_DEFAULT_LANG;
import static com.ib.system.SysConstants.CODE_DEIN_ZAPIS;
import static com.ib.system.SysConstants.CODE_ZNACHENIE_DA;
import static com.ib.system.SysConstants.CODE_ZNACHENIE_NE;
import static com.ib.system.utils.SearchUtils.asInteger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.DocVidSetting;
import com.ib.babhregs.db.dto.Register;
import com.ib.babhregs.db.dto.Registratura;
import com.ib.babhregs.db.dto.RegistraturaGroup;
import com.ib.babhregs.db.dto.RegistraturaSetting;
import com.ib.babhregs.system.BabhClassifAdapter;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.BaseSystemData;
import com.ib.system.db.AbstractDAO;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.SysClassifUtils;

/**
 * DAO for {@link Registratura}
 *
 * @author belev
 */
public class RegistraturaDAO extends AbstractDAO<Registratura> {

	/**
	 * DAO for {@link RegistraturaSetting}
	 *
	 * @author belev
	 */
	static class RegistraturaSettingDAO extends AbstractDAO<RegistraturaSetting> {
		/** @param user */
		RegistraturaSettingDAO(ActiveUser user) {
			super(RegistraturaSetting.class, user);
		}

		@Override
		public RegistraturaSetting save(RegistraturaSetting entity) throws DbErrorException {
			boolean isNew = entity.getId() == null;

			String ident = entity.getTekst();
			if (!isNew) {
				entity = merge(entity); // това ще гарантира, че като се извика корекция и се журналира IdentInfo ще е налична
			}
			entity.setTekst(ident); // тука е сетвам в persistent обекта

			entity = super.save(entity);

			return entity;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistraturaDAO.class);

	/** @param user */
	public RegistraturaDAO(ActiveUser user) {
		super(Registratura.class, user);
	}

	/**
	 * Копиране на подадените регистри в текущата регистра.
	 *
	 * @see RegisterDAO#findRegistersCopy(Integer, Integer, boolean)
	 * @param registratura
	 * @param registers
	 * @param sd
	 * @throws DbErrorException
	 */
	public void copyRegisters(Integer registratura, List<Object[]> registers, BaseSystemData sd) throws DbErrorException {
		LOGGER.debug("Copy {} registers in registratura={}", registers.size(), registratura);

//		 * [0]-REGISTER_ID
//		 * [1]-REGISTRATURA_ID
//		 * [2]-REGISTER
//		 * [3]-REGISTER_TYPE
//		 * [4]-VALID
//		 * [5]-COMMON
//		 * [6]-DOC_TYPE
//		 * [7]-PREFIX
//		 * [8]-ALG
//		 * [9]-SORT_NOMER
//		 * [10]-BEG_NOMER
//		 * [11]-STEP

		try {
			RegisterDAO registerDao = new RegisterDAO(getUser());

			for (Object[] row : registers) {
				Integer common = asInteger(row[5]);

				if (Objects.equals(common, CODE_ZNACHENIE_DA)) { // само ще се направи връзка в таблица REGISTRATURA_REGISTER
					Integer register = asInteger(row[0]);

					Query query = createNativeQuery(" insert into REGISTRATURA_REGISTER (REGISTRATURA_ID, REGISTER_ID) values (:registratura, :register) ");

					query.setParameter("registratura", registratura);
					query.setParameter("register", register);

					query.executeUpdate();
					LOGGER.debug("Save REGISTRATURA_REGISTER for REGISTRATURA_ID={},REGISTER_ID={}", registratura, register);

					SystemJournal journal = new SystemJournal();
					Date date = new Date();

					journal.setCodeAction(CODE_DEIN_ZAPIS);
					journal.setCodeObject(CODE_ZNACHENIE_JOURNAL_REGISTER);
					journal.setDateAction(date);
					journal.setIdentObject("Добавяне на " + sd.decodeItem(CODE_CLASSIF_REGISTRI, register, getUserLang(), date) //
						+ " в регистратура " + sd.decodeItem(CODE_CLASSIF_REGISTRATURI, registratura, getUserLang(), date));
					journal.setIdObject(register);
					journal.setIdUser(getUserId());

					journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_REISTRATURA);
					journal.setJoinedIdObject1(registratura);

					saveAudit(journal);

				} else { // нов регистър за текущата регистратура
					Register register = new Register();

					register.setRegistraturaId(registratura);
					register.setRegister((String) row[2]);
					register.setRegisterType(asInteger(row[3]));
					register.setValid(CODE_ZNACHENIE_DA);
					register.setCommon(CODE_ZNACHENIE_NE);
					register.setDocType(asInteger(row[6]));
					register.setPrefix((String) row[7]);
					register.setAlg(asInteger(row[8]));
					register.setSortNomer(1);
					register.setBegNomer(asInteger(row[10]));
					register.setActNomer(1);
					register.setStep(asInteger(row[11]));

					registerDao.save(register);
				}
			}

		} catch (DbErrorException e) {
			throw e; // за да не се преопакова

		} catch (Exception e) {
			throw new DbErrorException("Грешка при копиране на регистри в регистратура!", e);
		}
	}

	/** */
	@Override
	public void delete(Registratura entity) throws DbErrorException, ObjectInUseException {
		try {
			Integer cnt;

			cnt = asInteger( // ADM_REFERENTS.REF_REGISTRATURA
				createNativeQuery("select count (*) as cnt from ADM_REFERENTS where REF_REGISTRATURA = :regId") //
					.setParameter("regId", entity.getId()) //
					.getResultList().get(0));
			if (cnt != null && cnt.intValue() > 0) {
				throw new ObjectInUseException("В регистратурата има създадена административна структура и не може да бъде изтрита!");
			}

			cnt = asInteger( // REGISTRI.REGISTRATURA_ID
				createNativeQuery("select count (*) as cnt from REGISTRI where REGISTRATURA_ID = :regId") //
					.setParameter("regId", entity.getId()) //
					.getResultList().get(0));
			if (cnt != null && cnt.intValue() > 0) {
				throw new ObjectInUseException("В регистратурата има дефинирани регистри и не може да бъде изтрита!");
			}

			cnt = asInteger( // DOC.REGISTRATURA_ID
				createNativeQuery("select count (*) as cnt from DOC where REGISTRATURA_ID = :regId") //
					.setParameter("regId", entity.getId()) //
					.getResultList().get(0));
			if (cnt != null && cnt.intValue() > 0) {
				throw new ObjectInUseException("В регистратурата има регистрирани документи и не може да бъде изтрита!");
			}

			// явно ще се трие и трябва да се изтрият и няколко други починени таблици

//		REGISTRATURA_SETTINGS
			List<RegistraturaSetting> settings = selectRegistraturaSettings(entity.getId());
			if (!settings.isEmpty()) {
				RegistraturaSettingDAO settingDao = new RegistraturaSettingDAO(getUser());
				for (RegistraturaSetting setting : settings) {
					settingDao.delete(setting);
				}
				LOGGER.debug("Delete {} REGISTRATURA_SETTINGS for REGISTRATURA_ID={}", settings.size(), entity.getId());
			}

//		REGISTRATURA_GROUPS
			RegistraturaGroupDAO groupDao = new RegistraturaGroupDAO(getUser());
			List<RegistraturaGroup> groups = groupDao.findByRegistraturaId(Arrays.asList(entity.getId()), null, null, null, null);
			if (!groups.isEmpty()) {
				for (RegistraturaGroup group : groups) {
					groupDao.delete(group);
				}
				LOGGER.debug("Delete {} REGISTRATURA_GROUPS for REGISTRATURA_ID={}", groups.size(), entity.getId());
			}

//		REGISTRATURA_REGISTER
			Query query = createNativeQuery(" delete from REGISTRATURA_REGISTER where REGISTRATURA_ID = :registratura ");
			query.setParameter("registratura", entity.getId());
			cnt = query.executeUpdate();
			LOGGER.debug("Delete {} REGISTRATURA_REGISTER for REGISTRATURA_ID={}", cnt, entity.getId());

			// ADM_USER_ROLES.CODE_ROLE (CODE_CLASSIF in
			// (BabhConstants.CODE_CLASSIF_REGISTRATURI_OBJACCESS
			// ,BabhConstants.CODE_CLASSIF_REGISTRATURI_REQDOC
			// ,BabhConstants.CODE_CLASSIF_REGISTRATURI)
			query = createNativeQuery(" delete from ADM_USER_ROLES where CODE_CLASSIF in ( :c3 ) and CODE_ROLE = :registratura ");
			query.setParameter("c3", CODE_CLASSIF_REGISTRATURI);
			query.setParameter("registratura", entity.getId());
			cnt = query.executeUpdate();
			LOGGER.debug("Delete {} ADM_USER_ROLES for CODE_CLASSIFin({}),CODE_ROLE={}", cnt //
				,  CODE_CLASSIF_REGISTRATURI, entity.getId());

//		DOC_VID_SETTINGS
			query = createQuery("select s from DocVidSetting s where s.registraturaId = :registratura") //
				.setParameter("registratura", entity.getId());
			@SuppressWarnings("unchecked")
			List<DocVidSetting> vidSettings = query.getResultList();
			if (vidSettings != null && !vidSettings.isEmpty()) {
				DocVidSettingDAO settingDao = new DocVidSettingDAO(getUser());
				for (DocVidSetting setting : vidSettings) {
					settingDao.delete(setting);
				}
			}
		} catch (ObjectInUseException | DbErrorException e) {
			throw e; // за да не се преопакова

		} catch (Exception e) {
			throw new DbErrorException("Грешка при изтриване на подчинени обекти за регистратура!", e);
		}

		super.delete(entity);
	}

	/**
	 * Намира списък с регистратури, в които са заредени необходимите данни за екрана. Сортира ги по ИД.
	 *
	 * @return
	 * @throws DbErrorException
	 */
	public List<Registratura> findAllSelected() throws DbErrorException {
		try {
			TypedQuery<Registratura> query = getEntityManager().createQuery( //
				"select NEW Registratura(r.id, r.registratura, r.valid, r.orgEik, r.orgName) from Registratura r order by r.registratura, r.nomer", Registratura.class);

			return query.getResultList();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на участници по документа!", e);
		}
	}

	/**
	 * Намира настройките на регистратура. Използва класификация {@link BabhConstants#CODE_CLASSIF_REISTRATURA_SETTINGS}.
	 *
	 * @param registratura
	 * @param sd
	 * @return
	 * @throws DbErrorException
	 * @throws UnexpectedResultException
	 */
	public List<RegistraturaSetting> findRegistraturaSettings(Integer registratura, BaseSystemData sd) throws DbErrorException, UnexpectedResultException {
		List<SystemClassif> items = sd.getSysClassification(CODE_CLASSIF_REISTRATURA_SETTINGS, null, CODE_DEFAULT_LANG);
		SysClassifUtils.doSortClassifPrev(items); // сортирани всички настройки взети от класификацията

		List<RegistraturaSetting> dbSettings = selectRegistraturaSettings(registratura);
		List<RegistraturaSetting> settings = new ArrayList<>();

		if (dbSettings.isEmpty()) { // няма нищо в БД и значи всички идват като нови през класификацията
			for (SystemClassif item : items) {
				settings.add(new RegistraturaSetting(registratura, item.getCode(), item.getCodeExt(), item.getTekst()));
			}
		} else { // има нещо от БД и значи ще се гледа дали са както в класификацията и най вече, за да се сортират в правилния
					// ред
			for (SystemClassif item : items) {

				int toRemove = -1;
				for (int i = 0; i < dbSettings.size(); i++) {
					RegistraturaSetting setting = dbSettings.get(i);

					if (Objects.equals(setting.getSettingCode(), item.getCode())) {
						toRemove = i;

						setting.setTekst(item.getTekst());
						setting.setDbSettingValue(setting.getSettingValue());

						settings.add(setting); // за екрана тези ще са

						break;
					}
				}

				if (toRemove > -1) {
					dbSettings.remove(toRemove); // за да се оптимизира малко, защото този вече не ни трябва тука

				} else { // явно е новопоявила се в класификацията и трябва да се добави
					settings.add(new RegistraturaSetting(registratura, item.getCode(), item.getCodeExt(), item.getTekst()));
				}
			}
		}

		return settings;
	}

	/**
	 * Зарежда списък с елементи от административната структура, които са за подадената регистратура. По подразбиране се зареждат
	 * само звената.
	 *
	 * @param registratura
	 * @param showEmployees при <code>true</code> се показват и служителите
	 * @param sd
	 * @return
	 * @throws DbErrorException
	 * @throws UnexpectedResultException
	 */
	public List<SystemClassif> loadAdmStruct(Integer registratura, boolean showEmployees, BaseSystemData sd) throws DbErrorException, UnexpectedResultException {
		Date date = new Date();

		List<SystemClassif> items = sd.getSysClassification(CODE_CLASSIF_ADMIN_STR, date, getUserLang());

		SysClassifUtils.doSortClassifPrev(items); // !!! това е много важно за останалия код в метода

		Map<Integer, Integer> removedParents = new HashMap<>(); // на всеки махнат да му знам парента

		int i = 0;
		while (i < items.size()) { // ще се махам служители и трябва да се оправи дървото за да се покаже коректно

			SystemClassif item = items.get(i);

			Object[] specifics = (Object[]) sd.getItemSpecific(CODE_CLASSIF_ADMIN_STR, item.getCode(), getUserLang(), date, -1);

			Object refType = specifics[BabhClassifAdapter.ADM_STRUCT_INDEX_REF_TYPE];
			Object refRegistratura = specifics[BabhClassifAdapter.ADM_STRUCT_INDEX_REGISTRATURA];

			item.setCodeExt(String.valueOf(refType));

			if (!refRegistratura.equals(registratura) //
				|| !showEmployees && Objects.equals(refType, CODE_ZNACHENIE_REF_TYPE_EMPL)) {

				items.remove(i);

				removedParents.put(item.getCode(), item.getCodeParent());
			} else {
				i++;
			}
		}

		if (!removedParents.isEmpty()) { // !!! ако има махания трябва да се поправи класификацията !!!

			Map<Integer, Integer> actualPrevCodes = new HashMap<>(); // за всеки парент да знам последния добавен код

			for (SystemClassif item : items) {
				item.setCodeParent(findNewCodeParent(item.getCodeParent(), removedParents));

				Integer codePrev = actualPrevCodes.get(item.getCodeParent());
				item.setCodePrev(codePrev == null ? 0 : codePrev);

				actualPrevCodes.put(item.getCodeParent(), item.getCode());
			}
		}

		return items;
	}

	/**
	 * Запис на настройките
	 *
	 * @param settings
	 * @throws DbErrorException
	 */
	public void saveRegistraturaSettings(List<RegistraturaSetting> settings) throws DbErrorException {
		try {
			RegistraturaSettingDAO settDao = new RegistraturaSettingDAO(getUser());

			for (int i = 0; i < settings.size(); i++) {
				RegistraturaSetting setting = settings.get(i);

				if (setting.getId() == null) { // запис на нова
					settDao.save(setting);

				} else if (!Objects.equals(setting.getDbSettingValue(), setting.getSettingValue())) {
					RegistraturaSetting saved = settDao.save(setting);

					saved.setDbSettingValue(setting.getSettingValue());

					settings.set(i, saved); // пуска се промяна само ако има реална такава
				}
			}
		} catch (DbErrorException e) {
			throw e; // за да не се преопакова

		} catch (Exception e) {
			throw new DbErrorException("Грешка при актуализиране на настройки за регистратура!", e);
		}
	}

	/**
	 * @param codeParent
	 * @param removedParents
	 * @return
	 */
	private int findNewCodeParent(int codeParent, Map<Integer, Integer> removedParents) {
		if (removedParents.containsKey(codeParent)) {
			return findNewCodeParent(removedParents.get(codeParent), removedParents);
		}
		return codeParent;
	}

	/**
	 * @param registratura
	 * @return
	 * @throws DbErrorException
	 */
	@SuppressWarnings("unchecked")
	private List<RegistraturaSetting> selectRegistraturaSettings(Integer registratura) throws DbErrorException {
		try {
			Query query = createQuery("select s from RegistraturaSetting s where s.registraturaId = :registratura order by s.settingCode") //
				.setParameter("registratura", registratura);

			return query.getResultList();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на настройки за регистратура!", e);
		}
	}
}