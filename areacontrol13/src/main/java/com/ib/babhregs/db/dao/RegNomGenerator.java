package com.ib.babhregs.db.dao;

import static com.ib.babhregs.system.BabhClassifAdapter.REGISTRATURI_INDEX_NOMER;
import static com.ib.babhregs.system.BabhConstants.CODE_CLASSIF_REGISTRATURI;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.EventDeinostFuraji;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.RegisterOptionsDocsOut;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.ActiveUser;
import com.ib.system.BaseSystemData;
import com.ib.system.SysConstants;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.SearchUtils;

/**
 * За генериране на номера на обекти
 *
 * @author belev
 */
public class RegNomGenerator {

	/**
	 * Това ще даде възможност да се генерира номер в отделна транзакция
	 *
	 * @author belev
	 */
	private static class GenTransact extends Thread {
		private String		key;
		private int			value;
		DbErrorException	ex;		// и като е в отделна нишка и гръмне няма как да знам и за това тука ще се пази грешката ако е има

		/** @param task */
		GenTransact(String key) {
			this.key = key;
		}

		@Override
		public void run() {
			try {
				JPA.getUtil().begin();

				StoredProcedureQuery storedProcedure = JPA.getUtil().getEntityManager().createStoredProcedureQuery("gen_nom_babh") //
						.registerStoredProcedureParameter(0, String.class, ParameterMode.IN) //
						.registerStoredProcedureParameter(1, Integer.class, ParameterMode.OUT); //

				storedProcedure.setParameter(0, this.key);

				storedProcedure.execute();

				this.value = (Integer) storedProcedure.getOutputParameterValue(1);

				JPA.getUtil().commit();

			} catch (Exception e) {
				JPA.getUtil().rollback();
				this.ex = new DbErrorException("Системна грешка(procedure) при генериране на рег.номер на обект!", e);

			} finally {
				JPA.getUtil().closeConnection(); // това си е в отделна нишка и задължително трябва да си се затвори само
			}
		}
	}

	// TODO ако обекта има номер не трябва да се замазва
	
	/**  */
	static final Logger LOGGER = LoggerFactory.getLogger(RegNomGenerator.class);

	/**  */
	private static final String	REG_ALFA_BG_PREFIX		= "αBG";
	/**  */
	private static final String	REG_VREM_LETTER			= "В";
	/**  */
	private static final String	REG_MEDIKAMENT_LETTER	= "М";

	private ActiveUser user;

	/**
	 * @param user
	 */
	RegNomGenerator(ActiveUser user) {
		this.user = user;
	}

	/**
	 * Генерира номера за обекта, но само ако вече няма генериран.
	 *
	 * @param vpisvane
	 * @param opts
	 * @param systemData
	 * @return номер само в случай, че е генериран нов !
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	public String genRegNomer(Vpisvane vpisvane, RegisterOptionsDocsOut opts, BaseSystemData systemData, String migRegNomer) throws DbErrorException, ObjectInUseException {
		String newRegNomer = null;
		if (vpisvane.getOezReg() != null // за всеки случай
				&& Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ)) {

			if (vpisvane.getOezReg().getRegNom() == null //
					|| vpisvane.getOezReg().getRegNom().trim().length() == 0) { // ще се генерира

				// TODO журнал
				newRegNomer = new OezRegDAO(this.user).generateAndSaveRegNomReglament(vpisvane.getOezReg(), migRegNomer);

				vpisvane.getOezReg().setRegNom(newRegNomer);
			}

		} else if (vpisvane.getEventDeinostFuraji() != null // за всеки случай
				&& Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI)) {

			if (Objects.equals(vpisvane.getIdRegister(), BabhConstants.REGISTER_KF_31)) {
				Integer udostDocVid = opts != null ? opts.getVidDoc() : null;
				newRegNomer = genRegNomerFuraji31and34(vpisvane.getCodeRefVpisvane(), vpisvane.getEventDeinostFuraji(), vpisvane.getRegistraturaId(), udostDocVid, systemData, migRegNomer //
						, "", "REG_KF_31_");

			} else if (Objects.equals(vpisvane.getIdRegister(), BabhConstants.REGISTER_KF_32)) {
				newRegNomer = genRegNomerFuraji32and33(vpisvane.getCodeRefVpisvane(), vpisvane.getEventDeinostFuraji(), vpisvane.getRegistraturaId(), systemData, migRegNomer);

			} else if (Objects.equals(vpisvane.getIdRegister(), BabhConstants.REGISTER_KF_33)) {
				newRegNomer = genRegNomerFuraji32and33(vpisvane.getCodeRefVpisvane(), vpisvane.getEventDeinostFuraji(), vpisvane.getRegistraturaId(), systemData, migRegNomer);

			} else if (Objects.equals(vpisvane.getIdRegister(), BabhConstants.REGISTER_KF_34)) {
				Integer udostDocVid = opts != null ? opts.getVidDoc() : null;
				newRegNomer = genRegNomerFuraji31and34(vpisvane.getCodeRefVpisvane(), vpisvane.getEventDeinostFuraji(), vpisvane.getRegistraturaId(), udostDocVid, systemData, migRegNomer,
						REG_MEDIKAMENT_LETTER, "REG_KF_34_");

			} else if (Objects.equals(vpisvane.getIdRegister(), BabhConstants.REGISTER_KF_44)) {
				newRegNomer = genRegNomerFuraji44(vpisvane.getEventDeinostFuraji(), vpisvane.getRegistraturaId(), systemData, migRegNomer);
			}
		} else if (vpisvane.getObektDeinost() != null // за всеки случай
				&& Objects.equals(vpisvane.getCodePage(), CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ)) {
			newRegNomer = genRegNomerVlz(vpisvane.getObektDeinost(), vpisvane.getRegistraturaId(), systemData, migRegNomer);
		}
		return newRegNomer;
	}

	/**
	 * Дейности с фуражи регистър {@link BabhConstants#REGISTER_KF_31} и {@link BabhConstants#REGISTER_KF_34}<br>
	 */
	String genRegNomerFuraji31and34(Integer operator, EventDeinostFuraji deinost, Integer registraturaId, Integer udostDocVid, BaseSystemData systemData //
			, String migRegNomer, String m, String sidPrefix) throws DbErrorException, ObjectInUseException {
		ObektDeinost obekt = null;
		if (deinost.isSaveObektDeinost() && deinost.getObektDeinostDeinost() != null && !deinost.getObektDeinostDeinost().isEmpty()) {
			// важен е обекта, който се записва през това вписване
			obekt = deinost.getObektDeinostDeinost().get(0).getObektDeinost();
		}

		if (!SearchUtils.isEmpty(migRegNomer)) { // нововъвден от ръчна миграция

			if (migRegNomer.startsWith(RegNomGenerator.REG_ALFA_BG_PREFIX + RegNomGenerator.REG_VREM_LETTER)) {
				deinost.setRegNomVrem(migRegNomer);
			} else {
				deinost.setRegNom(migRegNomer);
			}
			if (obekt != null) {
				obekt.setRegNom(migRegNomer);
			}
			new EventDeinostFurajiDAO(this.user).save(deinost);
			return migRegNomer;
		}

		// малко по сложно е това да се разбере дали има номер, защото може да е временен, а сега да се иска постоянен

		String prefixPart = null; // префикса за съответния случай като не се съдържа XXYYYY

		boolean vrem = udostDocVid != null && systemData.matchClassifItems(BabhConstants.CODE_CLASSIF_UDOST_DOC_VREM, udostDocVid, null);
		if (vrem) {
			if (SearchUtils.isEmpty(deinost.getRegNomVrem())) { // временен и ако няма какъвто и да е
				prefixPart = REG_ALFA_BG_PREFIX + REG_VREM_LETTER + m;
			}
		} else {
			if (SearchUtils.isEmpty(deinost.getRegNom())) { // иска се постоянен, но няма или има временен
				prefixPart = REG_ALFA_BG_PREFIX + m;
			}
		}
		if (prefixPart == null) {
			return null; // има наличния номер спрамо входните данни и няма да се генерира нов
		}

		Integer nasMesto = null;
		String postCode = null;

		if (obekt != null) { // взимаме от обекта
			nasMesto = obekt.getNasMesto();
			postCode = obekt.getPostCode();

		} else { // от оператора
			try {
				@SuppressWarnings("unchecked")
				List<Object[]> rows = JPA.getUtil().getEntityManager().createNativeQuery( //
						"select ekatte, post_code from adm_ref_addrs where code_ref = ?1 and addr_type = ?2") //
						.setParameter(1, operator).setParameter(2, BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN) //
						.getResultList();
				if (!rows.isEmpty()) {
					nasMesto = SearchUtils.asInteger(rows.get(0)[0]);
					postCode = (String) rows.get(0)[1];
				}
			} catch (Exception e) {
				throw new DbErrorException("Грешка при определяне на адрес на оператор.", e);
			}
		}

		if (nasMesto == null || nasMesto.intValue() > 100000) {
			if (obekt != null) {
				throw new ObjectInUseException("Не може да се генерира рег.номер! Моля, въведете населено място на обект.");
			}
			throw new ObjectInUseException("Не може да се генерира рег.номер! Моля, въведете населено място на заявител.");
		}
		if (SearchUtils.isEmpty(postCode)) {
			if (obekt != null) {
				throw new ObjectInUseException("Не може да се генерира рег.номер! Моля, въведете пощенски код на обект.");
			}
			throw new ObjectInUseException("Не може да се генерира рег.номер! Моля, въведете пощенски код на заявител.");
		}

		String nomerRegistratuira = findRegistraturaNomer(nasMesto);
		if (nomerRegistratuira == null) { // ако от горе нещо не е ОК взимам от вписването
			nomerRegistratuira = (String) systemData.getItemSpecific(CODE_CLASSIF_REGISTRATURI, registraturaId, SysConstants.CODE_LANG_BG, null, REGISTRATURI_INDEX_NOMER);
		}

		String prefix = prefixPart //
				+ nomerRegistratuira //
				+ SearchUtils.trimToNULL(postCode);

		if (vrem) {
			sidPrefix = sidPrefix + "V_";
		}
		String sidKey = sidPrefix + nasMesto;
		Integer sidValue = genSidValue(sidKey); // генериране

		String newRegNomer = prefix + String.format("%02d", sidValue);
		if (vrem) {
			deinost.setRegNomVrem(newRegNomer);
		} else {
			deinost.setRegNom(newRegNomer);
		}
		if (obekt != null) {
			obekt.setRegNom(newRegNomer);
		}
		new EventDeinostFurajiDAO(this.user).save(deinost);

		return newRegNomer;
	}

	/**
	 * Дейности с фуражи регистър {@link BabhConstants#REGISTER_KF_32} и {@link BabhConstants#REGISTER_KF_33} <br>
	 */
	String genRegNomerFuraji32and33(Integer operator, EventDeinostFuraji deinost, Integer registraturaId, BaseSystemData systemData, String migRegNomer) throws DbErrorException, ObjectInUseException {
		ObektDeinost obekt = null;
		if (deinost.isSaveObektDeinost() && deinost.getObektDeinostDeinost() != null && !deinost.getObektDeinostDeinost().isEmpty()) {
			// важен е обекта, който се записва през това вписване
			obekt = deinost.getObektDeinostDeinost().get(0).getObektDeinost();
		}

		if (!SearchUtils.isEmpty(migRegNomer)) { // нововъвден от ръчна миграция
			if (obekt != null) {
				obekt.setRegNom(migRegNomer);
			}
			deinost.setRegNom(migRegNomer);
			new EventDeinostFurajiDAO(this.user).save(deinost);
			return migRegNomer;
		}

		if (!SearchUtils.isEmpty(deinost.getRegNom())) {
			return null; // има обект с номер и приключваме
		}

		Integer nasMesto = null;
		String postCode = null;

		if (obekt != null) { // взимаме от обекта
			nasMesto = obekt.getNasMesto();
			postCode = obekt.getPostCode();

		} else { // от оператора
			try {
				@SuppressWarnings("unchecked")
				List<Object[]> rows = JPA.getUtil().getEntityManager().createNativeQuery( //
						"select ekatte, post_code from adm_ref_addrs where code_ref = ?1 and addr_type = ?2") //
						.setParameter(1, operator).setParameter(2, BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN) //
						.getResultList();
				if (!rows.isEmpty()) {
					nasMesto = SearchUtils.asInteger(rows.get(0)[0]);
					postCode = (String) rows.get(0)[1];
				}
			} catch (Exception e) {
				throw new DbErrorException("Грешка при определяне на адрес на оператор.", e);
			}
		}

		if (nasMesto == null || nasMesto.intValue() > 100000) {
			if (obekt != null) {
				throw new ObjectInUseException("Не може да се генерира рег.номер! Моля, въведете населено място на обект.");
			}
			throw new ObjectInUseException("Не може да се генерира рег.номер! Моля, въведете населено място на заявител.");
		}
		if (SearchUtils.isEmpty(postCode)) {
			if (obekt != null) {
				throw new ObjectInUseException("Не може да се генерира рег.номер! Моля, въведете пощенски код на обект.");
			}
			throw new ObjectInUseException("Не може да се генерира рег.номер! Моля, въведете пощенски код на заявител.");
		}

		String nomerRegistratuira = findRegistraturaNomer(nasMesto);
		if (nomerRegistratuira == null) { // ако от горе нещо не е ОК взимам от вписването
			nomerRegistratuira = (String) systemData.getItemSpecific(CODE_CLASSIF_REGISTRATURI, registraturaId, SysConstants.CODE_LANG_BG, null, REGISTRATURI_INDEX_NOMER);
		}

		String prefix = nomerRegistratuira //
				+ SearchUtils.trimToNULL(postCode);

		String sidKey = "REG_KF_32_33_" + nasMesto;
		Integer sidValue = genSidValue(sidKey);

		String newRegNomer = prefix + String.format("%03d", sidValue);
		if (obekt != null) {
			obekt.setRegNom(newRegNomer);
		}
		deinost.setRegNom(newRegNomer);
		new EventDeinostFurajiDAO(this.user).save(deinost);

		return newRegNomer;
	}

	/**
	 * Дейности с фуражи регистър {@link BabhConstants#REGISTER_KF_44}<br>
	 */
	String genRegNomerFuraji44(EventDeinostFuraji deinost, Integer registraturaId, BaseSystemData systemData, String migRegNomer) throws DbErrorException {
		ObektDeinost obekt = null;
		if (deinost.isSaveObektDeinost() && deinost.getObektDeinostDeinost() != null && !deinost.getObektDeinostDeinost().isEmpty()) {
			// важен е обекта, който се записва през това вписване
			obekt = deinost.getObektDeinostDeinost().get(0).getObektDeinost();
		}

		if (!SearchUtils.isEmpty(migRegNomer)) { // нововъвден от ръчна миграция
			if (obekt != null) {
				obekt.setRegNom(migRegNomer);
			}
			deinost.setRegNom(migRegNomer);
			new EventDeinostFurajiDAO(this.user).save(deinost);
			return migRegNomer;
		}

		if (!SearchUtils.isEmpty(deinost.getRegNom())) {
			return null; // има обект с номер и приключваме
		}

		String nomerRegistratuira = null;
		if (obekt != null) {
			nomerRegistratuira = findRegistraturaNomer(obekt.getNasMesto());
		}
		if (nomerRegistratuira == null) { // ако от горе нещо не е ОК взимам от вписването
			nomerRegistratuira = (String) systemData.getItemSpecific(CODE_CLASSIF_REGISTRATURI, registraturaId, SysConstants.CODE_LANG_BG, null, REGISTRATURI_INDEX_NOMER);
		}

		String prefix = "BG" //
				+ nomerRegistratuira //
				+ "17";

		String sidKey = "REG_KF_44";
		Integer sidValue = genSidValue(sidKey);

		String newRegNomer = prefix + String.format("%03d", sidValue);
		if (obekt != null) {
			obekt.setRegNom(newRegNomer);
		}

		deinost.setRegNom(newRegNomer);
		new EventDeinostFurajiDAO(this.user).save(deinost);

		return newRegNomer;
	}

	/**
	 * Уникален регистрационен номер на ВЛЗ
	 *
	 * @throws DbErrorException
	 * @throws ObjectInUseException
	 */
	String genRegNomerVlz(ObektDeinost vlz, Integer registraturaId, BaseSystemData systemData, String migRegNomer) throws DbErrorException, ObjectInUseException {
		if (!SearchUtils.isEmpty(migRegNomer)) { // нововъвден от ръчна миграция
			vlz.setRegNom(migRegNomer);
			new ObektDeinostDAO(this.user).save(vlz, false, new ArrayList<>());
			return migRegNomer;
		}

		if (!SearchUtils.isEmpty(vlz.getRegNom())) {
			return null; // има вече
		}

		if (vlz.getNasMesto() == null || vlz.getNasMesto().intValue() > 100000) {
			throw new ObjectInUseException("Не може да се генерира рег.номер на обект! Моля, въведете населено място.");
		}

		String nomerRegistratuira = findRegistraturaNomer(vlz.getNasMesto());
		if (nomerRegistratuira == null) { // ако от горе нещо не е ОК взимам от вписването
			nomerRegistratuira = (String) systemData.getItemSpecific(CODE_CLASSIF_REGISTRATURI, registraturaId, SysConstants.CODE_LANG_BG, null, REGISTRATURI_INDEX_NOMER);
		}

		String vidVlz;
		if (vlz.getVidVlz() == null) {
			vidVlz = "99";
		} else {
			vidVlz = "9" + vlz.getVidVlz().toString();
		}

		String sidKey = "REG_VLZ_" + nomerRegistratuira;
		Integer sidValue = genSidValue(sidKey);

		String newRegNomer = nomerRegistratuira + "-" + vidVlz + "-" + String.format("%03d", sidValue);
		vlz.setRegNom(newRegNomer);

		new ObektDeinostDAO(this.user).save(vlz, false, new ArrayList<>());

		return newRegNomer;
	}

	/**
	 * Намира регистратурата, която обслужва този код по ЕКАТТЕ.<br>
	 * Ако се подаде за код на ЕКАТТЕ={@link BabhConstants#CODE_EKATTE_SOFIA} ще върне null<br>
	 * Ако се определи, че регистратурата е с код на ЕКАТТЕ={@link BabhConstants#CODE_EKATTE_SOFIA} ще върне null<br>
	 */
	private String findRegistraturaNomer(Integer ekatte) {
		if (ekatte == null || ekatte == BabhConstants.CODE_EKATTE_SOFIA) {
			return null;
		}
		String nomerRegistratuira = null;
		try {
			//  Първите 2 цифри съответстват на административния областен център, на територията на която се намира (01-Благоевград;
			// 02-Бургас; 03-Варна; и т.н.)
			StringBuilder sql = new StringBuilder();
			sql.append(" select obl.oblast, r.registratura_id, r.nomer, r.ekatte ");
			sql.append(" from ekatte_att att ");
			sql.append(" inner join ekatte_oblasti obl on obl.oblast = att.oblast ");
			sql.append(" inner join registraturi r on r.ekatte = obl.ekatte ");
			sql.append(" where att.ekatte = ?1 ");

			@SuppressWarnings("unchecked")
			List<Object[]> rows = JPA.getUtil().getEntityManager().createNativeQuery(sql.toString()).setParameter(1, ekatte).getResultList();
			if (!rows.isEmpty()) {
				Integer oblastEkatte = SearchUtils.asInteger(rows.get(0)[3]);

				if (Objects.equals(oblastEkatte, BabhConstants.CODE_EKATTE_SOFIA)) {
					// тука ще си остане НУЛЛ за резултата

				} else if (rows.get(0)[2] != null) {
					int nomer = ((Number) rows.get(0)[2]).intValue();
					if (nomer < 10) {
						nomerRegistratuira = "0" + nomer;
					} else {
						nomerRegistratuira = "" + nomer;
					}
				}
			}
		} catch (Exception e) { // няма да се определи
			LOGGER.error("Грешка при търсене на номер на регистратура за ЕКАТТЕ=" + ekatte, e);
		}
		return nomerRegistratuira;
	}

	/**
	 * Реалното генериране с извикване на функцията от БД. Извиква се в отделна транзакция с помощта на клас {@link GenTransact}
	 */
	private Integer genSidValue(String sidKey) throws DbErrorException {
		GenTransact gt;
		try {
			gt = new GenTransact(sidKey);
			gt.start();
			gt.join();

		} catch (Exception e) {
			throw new DbErrorException("Системна грешка(thread/start/join) при генериране на рег.номер на обект!", e);
		}
		if (gt.ex != null) {
			throw gt.ex; // запазвам реалната грешка
		}
		return gt.value;
	}
}
