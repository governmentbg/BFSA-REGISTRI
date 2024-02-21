package com.ib.babhregs.udostDocs;

import static com.ib.system.utils.ValidationUtils.isNotBlank;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ib.system.SysConstants;
import com.ib.system.utils.StringUtils;
import com.ib.system.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dao.RegistraturaDAO;
import com.ib.babhregs.db.dao.VpisvaneDAO;
import com.ib.babhregs.db.dto.EventDeinJivLice;
import com.ib.babhregs.db.dto.EventDeinostVlpLice;
import com.ib.babhregs.db.dto.MpsLice;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.ObektDeinostLica;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.ReferentDoc;
import com.ib.babhregs.db.dto.Registratura;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.db.dto.VpisvaneStatus;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.system.exceptions.DbErrorException;

/**
 * Помощен клас с методи, които не се разкриват публично в екрана за шаблони.
 * Тук методите могат да хвърлят грешки.
 * 
 * @author n.kanev
 */
public class Helpers {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Helpers.class);
	private static final String DB_ERROR_MSG = "Грешка при работа с базата";
	
	private VpisvaneDAO vpisvaneDao;
	private ReferentDAO referentDao;
	private RegistraturaDAO regDao;
	private final Vpisvane vpisvane;
	private final UserData userData;
	private final SystemData systemData;
	private final Date date;
	
	public Helpers(UserData userData, SystemData systemData, Vpisvane vpisvane) {
		this.userData = userData;
		this.systemData = systemData;
		this.vpisvane = vpisvane;
		this.date = new Date();
	}
	
	private ReferentDAO getReferentDao() {
		if(this.referentDao == null) {
			this.referentDao = new ReferentDAO(userData);
		}
		return this.referentDao;
	}
	
	private VpisvaneDAO getVpisvaneDao() {
		if(this.vpisvaneDao == null) {
			this.vpisvaneDao = new VpisvaneDAO(userData);
		}
		return this.vpisvaneDao;
	}
	
	private RegistraturaDAO getRegDao() {
		if(this.regDao == null) {
			this.regDao = new RegistraturaDAO(userData);
		}
		return this.regDao;
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	/**
	 * @return Регистратурата на вписването
	 * @throws DbErrorException
	 */
	public Registratura getRegistratura() throws DbErrorException {
		return getRegDao().findById(this.vpisvane.getRegistraturaId());
	}
	
	/**
	 * @return Обекта Referent от vpisvane.getIdLicenziant
	 * @throws DbErrorException
	 */
	public Referent getReferentFromVpisvane() throws DbErrorException {
		Integer licenziantId = this.vpisvane.getIdLicenziant();
		return getReferentDao().findByCode(licenziantId, this.date, true);
	}
	
	/**
	 * @param codeRef код на референт
	 * @return Обект Referent по зададен код
	 * @throws DbErrorException
	 */
	public Referent getReferentById(Integer codeRef) throws DbErrorException {
		return (codeRef != null) 
				? getReferentDao().findByCode(codeRef, this.date, true)
				: null;
	}
	
	/**
	 * Адрес на кореспондента, който се взема от референта на вписването
	 * @return масив с полета [0] държава, [1] нас. място, [2] адрес, [3] п.код, [4] п. кутия
	 * @throws DbErrorException
	 */
	public String[] getAddressFieldsByReferent(Referent r, boolean english) throws DbErrorException {
		if(r == null) return null;
		
		ReferentAddress address;
		if(r.getAddress() != null) {
			address = r.getAddress();
		}
		else if(r.getAddressKoresp() != null) {
			address = r.getAddressKoresp();
		}
		else {
			return new String[5];
		}
		
		String country = systemData.decodeItem(
				BabhConstants.CODE_CLASSIF_COUNTRIES,
				address.getAddrCountry(),
				(english) ? SysConstants.CODE_LANG_EN : SysConstants.CODE_LANG_BG,
				date);
		String ekatte = systemData.decodeItem(
				BabhConstants.CODE_CLASSIF_EKATTE,
				address.getEkatte(),
				userData.getCurrentLang(),
				date);
		if(ekatte != null && english) ekatte = StringUtils.transliterate(ekatte);

		String addrText = (english) ? address.getAddrTextLatin() : address.getAddrText();
		if(english && !isNotBlank(addrText) && isNotBlank(address.getAddrText())) {
			addrText = StringUtils.transliterate(address.getAddrText());
		}
		
		String postCode = address.getPostCode();
		String postBox = address.getPostBox();

		if(!english && postBox != null) {
			postBox = "п.к. " + postBox;
		}
		
		return new String[] { country, ekatte, addrText, postCode, postBox };
	}
	
	/**
	 * Адрес на обекта, който се взема от дейността към вписването
	 * @return масив с полета [0] държава, [1] област, [2] община, [3] нас. място, [4] адрес, [5] п. код
	 * @throws DbErrorException
	 */
	public String[] getAddressFieldsByObektDeinost(ObektDeinost deinost, boolean english) throws DbErrorException {
		String country = systemData.decodeItem(
				BabhConstants.CODE_CLASSIF_COUNTRIES,
				deinost.getDarj(),
				(english) ? SysConstants.CODE_LANG_EN : SysConstants.CODE_LANG_BG,
				date);
		String obl = systemData.decodeItem(
				BabhConstants.CODE_CLASSIF_EKATTE,
				deinost.getObl(),
				userData.getCurrentLang(),
				date);
		String obsht = systemData.decodeItem(
				BabhConstants.CODE_CLASSIF_EKATTE,
				deinost.getObsht(),
				userData.getCurrentLang(),
				date);
		String ekatte = systemData.decodeItem(
				BabhConstants.CODE_CLASSIF_EKATTE,
				deinost.getNasMesto(),
				userData.getCurrentLang(),
				date);
		if(obl != null && english) obl = StringUtils.transliterate(obl);
		if(obsht != null && english) obsht = StringUtils.transliterate(obsht);
		if(ekatte != null && english) ekatte = StringUtils.transliterate(ekatte);
		
		String adres = deinost.getAddress();
		if(adres != null && english) {
			adres = StringUtils.transliterate(deinost.getAddress());
		}
		
		String postCode = (deinost.getPostCode() == null) ? null : String.valueOf(deinost.getPostCode());
		
		return new String[] { country, obl, obsht, ekatte, adres, postCode };
	}
	
	/**
	 * Важно - Работи само с масива, който се връща от метода {@link Helpers#getAddressFieldsByReferent}
	 * @param addressFieldsFromReferent
	 * @return
	 */
	public String getAdresStringFromReferentAdresFields(String[] addressFieldsFromReferent) {
		
		if(addressFieldsFromReferent == null) return null;

		StringBuilder sb = new StringBuilder();
		if(addressFieldsFromReferent[0] != null && !addressFieldsFromReferent[0].contains("Ненамерено значение")) sb.append(addressFieldsFromReferent[0]).append(", ");
		if(addressFieldsFromReferent[1] != null && !addressFieldsFromReferent[1].contains("Ненамерено значение")) sb.append(addressFieldsFromReferent[1]).append(", ");
		if(addressFieldsFromReferent[2] != null) sb.append(addressFieldsFromReferent[2]).append(", ");
		if(addressFieldsFromReferent[3] != null) sb.append(addressFieldsFromReferent[3]).append(", ");
		if(addressFieldsFromReferent[4] != null) sb.append(addressFieldsFromReferent[4]);
		
		// премахва ", " от края на стринга
		while(sb.lastIndexOf(", ") == sb.length() - 2) {
			sb.delete(sb.lastIndexOf(", "), sb.length());
		}

		String s = sb.toString();
		s = s.replace("\r", "").replace("\n", " ");
		
		return s;
	}

	public String getAdresStringFromObektAdresFields(String[] addressFieldsFromObekt) {
		if(addressFieldsFromObekt == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		if(addressFieldsFromObekt[0] != null && !addressFieldsFromObekt[0].contains("Ненамерено значение")) sb.append(addressFieldsFromObekt[0]).append(", ");
		if(addressFieldsFromObekt[1] != null && !addressFieldsFromObekt[1].contains("Ненамерено значение")) sb.append(addressFieldsFromObekt[1]).append(", ");
		if(addressFieldsFromObekt[2] != null && !addressFieldsFromObekt[2].contains("Ненамерено значение")) sb.append(addressFieldsFromObekt[2]).append(", ");
		if(addressFieldsFromObekt[3] != null && !addressFieldsFromObekt[3].contains("Ненамерено значение")) sb.append(addressFieldsFromObekt[3]).append(", ");
		if(addressFieldsFromObekt[4] != null) sb.append(addressFieldsFromObekt[4]).append(", ");
		if(addressFieldsFromObekt[5] != null) sb.append(addressFieldsFromObekt[5]);

		// премахва ", " от края на стринга
		while(sb.lastIndexOf(", ") == sb.length() - 2) {
			sb.delete(sb.lastIndexOf(", "), sb.length());
		}

		return sb.length() == 0 ? null : sb.toString();
	}
	
	public EventDeinostVlpLice getEventDeinostVlpLiceByTipVraz(Integer tipVrazka) {
		if(vpisvane.getEventDeinostVlp() == null) return null;
		
		return vpisvane.getEventDeinostVlp().getEventDeinostVlpLice()
			.stream()
			.filter(l -> l.getTipVraz().equals(tipVrazka) && l.getDateEnd() == null)
			.findFirst()
			.orElse(null);
	}
	
	public List<EventDeinJivLice> getEventDeinostJivLiceByTipVraz(Integer tipVrazka) {
		if(vpisvane.getEventDeinJiv() == null) return null;
		
		return vpisvane.getEventDeinJiv().getEventDeinJivLice()
			.stream()
			.filter(l -> l.getTipVraz().equals(tipVrazka) && l.getDateEnd() == null)
			.collect(Collectors.toList());
	}
	
	public ObektDeinostLica getObektDeinostLiceByRole(Integer role) {
		ObektDeinost obektDeinost = this.getObektDeinost();
		
		if(obektDeinost != null) {
			return obektDeinost.getObektDeinostLica()
				.stream()
				.filter(l -> l.getRole().equals(role) && l.getDateEnd() == null)
				.findFirst()
				.orElse(null);
		}
		else return null;
	}
	
	public ObektDeinostLica getSobstvenikIliOperator() {
		List<ObektDeinostLica> lica = null;
		if(this.vpisvane.getCodePage() == BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ
			&& this.vpisvane.getOezReg() != null) {
			lica = this.vpisvane.getOezReg().getObektDeinostLica();
		}
		else {
			ObektDeinost obekt = this.getObektDeinost();
			if(obekt != null) {
				lica = obekt.getObektDeinostLica();
			}
		}

		if(lica == null || lica.isEmpty()) {
			return null;
		}
		
		ObektDeinostLica lice = null;

		for(ObektDeinostLica l : lica) {
			if(l.getDateEnd() == null) {
				if(l.getRole() == BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_SOBSTVENIK) {
					return l;
				}
				else if(l.getRole() == BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OPERATOR) {
					lice = l;
				}
			}
		}
		
		return lice;
	}
	
	/**
	 * @return Името/Наименованието на референта на кирилица
	 * @throws DbErrorException 
	 */
	public String getReferentImeOrNaimenovanieCyr() throws DbErrorException {
		Referent r = this.getReferentFromVpisvane();
		if(r != null) {
			return (isNotBlank(r.getRefName())) ? r.getRefName() : null;
		}
		else return null;
	}
	
	/**
	 * @return Името/Наименованието на референта на латиница
	 * @throws DbErrorException 
	 */
	public String getReferentImeOrNaimenovanieLat() throws DbErrorException {
		Referent r = this.getReferentFromVpisvane();
		if(r != null) {
			return (isNotBlank(r.getRefLatin())) ? r.getRefLatin() : null;
		}
		else return null;
	}
	
	/**
	 * @return Фамилията на референта на вписването на кирилица.
	 * @throws DbErrorException 
	 */
	public String getReferentFamiliaCyr() throws DbErrorException {
		String referentImena = getReferentImeOrNaimenovanieCyr();
		return this.getFamiliaFromFullName(referentImena);
	}
	
	/**
	 * @return Името и презимето на референта на вписването на кирилица.
	 * @throws DbErrorException 
	 */
	public String getReferent2NamesCyr() throws DbErrorException {
		String referentImena = getReferentImeOrNaimenovanieCyr();
		return this.getFirst2NamesFromFullName(referentImena);
	}
	
	/**
	 * @return Фамилията на референта на вписването на латиница.
	 * @throws DbErrorException 
	 */
	public String getReferentFamiliaLat() throws DbErrorException {
		String referentImena = getReferentImeOrNaimenovanieLat();
		return this.getFamiliaFromFullName(referentImena);
	}
	
	/**
	 * @return Името и презимето на референта на вписването на латиница.
	 * @throws DbErrorException 
	 */
	public String getReferent2NamesLat() throws DbErrorException {
		String referentImena = getReferentImeOrNaimenovanieLat();
		return this.getFirst2NamesFromFullName(referentImena);
	}
	
	/**
	 * Връща най-новия статус на вписването
	 * @return
	 */
	public VpisvaneStatus getVpisvanePosledenStatus() {
		try {
			List<VpisvaneStatus> statusi = getVpisvaneDao().findVpisvaneStatusList(vpisvane.getId());
			
			VpisvaneStatus posledenStatus = statusi.stream()
				.max((s1, s2) -> s1.getDateStatus().compareTo(s2.getDateStatus()))
				.orElse(null);
			
			return posledenStatus;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Връща обекта на дейност. Търси го на различно място в зависимост от codePage на вписването.
	 */
	public ObektDeinost getObektDeinost() {
		try {
			List<ObektDeinostDeinost> obektDeinost = null;
			switch(vpisvane.getCodePage()) {
				case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ : {
					obektDeinost = vpisvane.getEventDeinJiv().getObektDeinostDeinost();
					break;
				}
				case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI : {
					obektDeinost = vpisvane.getEventDeinostFuraji().getObektDeinostDeinost();
					break;
				}
				case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP : {
					obektDeinost = vpisvane.getEventDeinostVlp().getObektDeinostDeinost();
					break;
				}
				case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ : {
					ObektDeinostDeinost bezumie = new ObektDeinostDeinost();
					bezumie.setObektDeinost(vpisvane.getObektDeinost());
					obektDeinost = List.of(bezumie);
					break;
				}
			}
			
			return (obektDeinost != null && obektDeinost.size() > 0 && obektDeinost.get(0).getDateEnd() == null)
					? obektDeinost.get(0).getObektDeinost()
					: null;
		}
		catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * @return Връща обекта на дейност. Търси го на различно място в зависимост от codePage на вписването.
	 */
	public List<ObektDeinost> getObektiDeinost() {
		try {
			List<ObektDeinostDeinost> obektDeinostDeinost = null;
			switch(vpisvane.getCodePage()) {
				case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ : {
					obektDeinostDeinost = vpisvane.getEventDeinJiv().getObektDeinostDeinost();
					break;
				}
				case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI : {
					obektDeinostDeinost = vpisvane.getEventDeinostFuraji().getObektDeinostDeinost();
					break;
				}
				case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP : {
					obektDeinostDeinost = vpisvane.getEventDeinostVlp().getObektDeinostDeinost();
					break;
				}
				case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ : {
					ObektDeinostDeinost bezumie = new ObektDeinostDeinost();
					bezumie.setObektDeinost(vpisvane.getObektDeinost());
					obektDeinostDeinost = Arrays.asList(bezumie);
					break;
				}
			}

			if(obektDeinostDeinost != null && obektDeinostDeinost.size() > 0) {
				List<ObektDeinost> obektDeinost = 
					obektDeinostDeinost
						.stream()
						.filter(o -> o.getDateEnd() == null)
						.map(ObektDeinostDeinost::getObektDeinost)
						.collect(Collectors.toList());
				return obektDeinost;
			}
			else return new ArrayList<>();
		}
		catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * @return Адреса от EventDeinost
	 */
	public String getEventDeinostAdres() {
		switch(vpisvane.getCodePage()) {
			case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ : {
				return (vpisvane.getEventDeinJiv() != null) ? vpisvane.getEventDeinJiv().getAdres() : null;
			}
			case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI : {
				return (vpisvane.getEventDeinostFuraji() != null) ? vpisvane.getEventDeinostFuraji().getAddress() : null;
			}
			case BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP : {
				return (vpisvane.getEventDeinostVlp() != null) ? vpisvane.getEventDeinostVlp().getAdres() : null;
			}
			default : return null;
		}
	}
	
	/**
	 * Получава няколко имена "Трайчо Пеев Лолов" и връща последното с надеждата да е фамилното.
	 * Ако се подаде едно име, връща него.
	 * @param fullName
	 * @return
	 */
	public String getFamiliaFromFullName(String fullName) {
		try {
			if(fullName == null) return null;
			
			fullName = fullName.trim();
			int indexLastSpace = fullName.lastIndexOf(" ");
			if(indexLastSpace > 0) { // получени са поне 2 имена
				return fullName.substring(indexLastSpace + 1);
			}
			else { // получено е само 1 име
				return fullName;
			}
		}
		catch(IndexOutOfBoundsException e) {
			return fullName;
		}
	}
	
	/**
	 * Получава три имена "Трайчо Пеев Лолов" и връща първите две "Трайчо Пеев".
	 * Ако се подадат различен брой имена, ще върне всички без последното.
	 * @param fullName
	 * @return
	 */
	public String getFirst2NamesFromFullName(String fullName) {
		try {
			if(fullName == null) return null;
			
			fullName = fullName.trim();
			int indexLastSpace = fullName.lastIndexOf(" ");
			if(indexLastSpace > 0) { // получени са поне 2 имена
				return fullName.substring(0, indexLastSpace);
			}
			else { // получено е само 1 име
				return fullName;
			}
		}
		catch(IndexOutOfBoundsException e) {
			return fullName;
		}
	}
	
	/**
	 * Получава списък стрингове "А", "Б", "В", "Г" и ги връща като един стринг, разделен с delimiter
	 * @param strings
	 * @param addINakraia дали да сложи "и" между последните две; ако е false, слага delimiter
	 * @return стринг със събрания текст
	 */
	public String joinListWithSeparator(List<String> strings, String delimiter, boolean addINakraia) {
		if(strings.isEmpty()) {
			return null;
		}
		else if(strings.size() == 1) {
			return strings.get(0);
		}
		else {
			List<String> filteredStrings = strings.stream()
					.filter(ValidationUtils::isNotBlank)
					.collect(Collectors.toList());

			StringBuilder sb = new StringBuilder();

			// всички без последното
			for(int i = 0; i < filteredStrings.size() - 1; i++) {
				sb.append(filteredStrings.get(i));
				if(i < filteredStrings.size() - 2) {
					sb.append(delimiter);
				}
			}

			if(addINakraia) {
				sb.append(" и ");
			}
			else {
				sb.append(delimiter);
			}

			sb.append(filteredStrings.get(filteredStrings.size() - 1));

			return sb.toString();
		}
	}
	
	/** Връща ЕИК, ЕГН или ЛНЧ на референт
	 * @param r Референт
	 * @return Масив от 2 елемента, първият от които е "ЕИК"/"ЕГН"/"ЛНЧ", а вторият е съответната стойност
	 */
	public String[] getEgnEikByReferent(Referent r) {
		if(r == null) {
			return new String[] {null, null};
		}
		else if(isNotBlank(r.getNflEik())) {
			return new String[] {"ЕИК", r.getNflEik()};
		}
		else if(isNotBlank(r.getFzlEgn())) {
			return new String[] {"ЕГН", r.getFzlEgn()};
		}
		else if(isNotBlank(r.getFzlLnc())) {
			return new String[] {"ЛНЧ", r.getFzlLnc()};
		}
		else return new String[] {null, null};
	}

	public Object[] getLatestVpisvaneDoc(Integer docVid) throws DbErrorException {
		VpisvaneDAO dao = new VpisvaneDAO(userData);
		List<Object[]> docsList = dao.findVpisvaneDocList(vpisvane.getId());

		Object[] latestDoc =
				docsList
						.stream()
						.filter(doc -> ((BigInteger) doc[3]).intValue() == docVid)
						.max((doc1, doc2) -> ((Date) doc1[2]).compareTo((Date) doc2[2]))
						.orElse(null);
		return latestDoc;
	}
	
	public Integer getCurrZaiavlId(Integer docId) {
		try {
			return getVpisvaneDocData(docId, 9);
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public Integer getPrevUdId(Integer docId) {
		try {
			return getVpisvaneDocData(docId, 8);
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	private Integer getVpisvaneDocData(Integer docId, int arrayIndex) throws DbErrorException {
		VpisvaneDAO dao = new VpisvaneDAO(userData);
		List<Object[]> docsList = dao.findVpisvaneDocList(vpisvane.getId());
		
		Integer returnDocId = null;
		for(Object[] vpisvaneDoc : docsList) {
			if(vpisvaneDoc[0] != null) {
				Integer zaiavlDocId = ((BigInteger) vpisvaneDoc[0]).intValue();
				if(docId.equals(zaiavlDocId) && vpisvaneDoc[arrayIndex] != null) {
					returnDocId = ((BigInteger) vpisvaneDoc[arrayIndex]).intValue();
					break;
				}
			}
		}
		return returnDocId;
	}
	
	public Referent getMpsSobstvenik() {
		MpsLice lice = vpisvane.getMps().getMpsLice()
				.stream()
				.filter(l -> l.getTipVraz() == BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK)
				.findFirst()
				.orElse(null);
			
		if(lice != null) {
			try {
				return this.getReferentById(lice.getCodeRef());
			}
			catch (DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else return null;
	}
	
	public ReferentDoc getReferentDocByVid(Referent r, Integer vidDoc) {
		if(r != null && r.getReferentDocs() != null) {
			return r.getReferentDocs()
				.stream()
				.filter(d -> d.getVidDoc().equals(vidDoc))
				.findFirst()
				.orElse(null);
		}
		else return null;
	}

	public String[] getImaNemaObektSahranenie() {
		List<ObektDeinost> obektiDeinost = this.getObektiDeinost();
		if(obektiDeinost != null && !obektiDeinost.isEmpty()) {
			
			for(ObektDeinost obektDeinost : obektiDeinost) {
				if(isNotBlank(obektDeinost.getNomDatDogovor())) {
					return new String[] { "има", "has" };
				}
			}
			
			return new String[] { "няма", "has not" };
		}
		else return new String[]{ null, null };
	}

	public String[] getImaNemaLaboratoria() {
		List<ObektDeinost> obektiDeinost = this.getObektiDeinost();
		if(obektiDeinost != null && !obektiDeinost.isEmpty()) {
			
			for(ObektDeinost obektDeinost : obektiDeinost) {
				return (obektDeinost.getPrednaznachenieList()
						.stream()
						.anyMatch(p -> p == BabhConstants.CODE_ZNACHENIE_PREDNAZN_KONTRL_KACH))
							? new String[] { "има", "has" }
							: new String[] { "няма", "has not" };
			}
			
			return new String[] { "няма", "has not" };
		}
		else return new String[]{ null, null };

	}

	public String getMestonahojdenieObekti(List<ObektDeinost> obekti) {
		List<String> obektiStringove = new ArrayList<>();

		for(ObektDeinost obekt : obekti) {
			if(obekt == null) {
				continue;
			}
			
			String obektAdres = null;
			try {
				String[] adresPoleta = this.getAddressFieldsByObektDeinost(obekt, false);
				obektAdres = this.getAdresStringFromObektAdresFields(adresPoleta);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
			
			String adresString = null;
			
			if(isNotBlank(obekt.getNaimenovanie()) && isNotBlank(obektAdres)) {
				adresString = String.join(", ", obekt.getNaimenovanie(), (isNotBlank(obektAdres) ? obektAdres : ""));
			}
			else if(!isNotBlank(obekt.getNaimenovanie()) && isNotBlank(obektAdres)) {
				adresString = obektAdres;
			}
			else if(isNotBlank(obekt.getNaimenovanie()) && !isNotBlank(obektAdres)) {
				adresString = obekt.getNaimenovanie();
			}
			
			obektiStringove.add(adresString);
		}

		if(!obektiStringove.isEmpty()) {
			return this.joinListWithSeparator(obektiStringove, "\n", false);
		}
		else return null;
	}
}
