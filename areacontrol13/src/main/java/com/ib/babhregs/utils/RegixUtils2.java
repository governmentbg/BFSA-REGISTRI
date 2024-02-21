package com.ib.babhregs.utils;


import bg.egov.regix.RegixClientException;
import bg.egov.regix.requests.av.tr.ActualStateRequestType;
import bg.egov.regix.requests.av.tr.ActualStateResponseType;
import bg.egov.regix.requests.av.tr.AddressType;
import bg.egov.regix.requests.av.tr.TROperation;
import bg.egov.regix.requests.grao.GraoOperation;
import bg.egov.regix.requests.grao.nbd.PersonDataRequestType;
import bg.egov.regix.requests.grao.nbd.PersonDataResponseType;
import bg.egov.regix.requests.grao.nbd.PersonNames;
import bg.egov.regix.requests.grao.pna.PermanentAddressRequestType;
import bg.egov.regix.requests.grao.pna.PermanentAddressResponseType;
import bg.egov.regix.requests.grao.pna.TemporaryAddressRequestType;
import bg.egov.regix.requests.grao.pna.TemporaryAddressResponseType;
import bg.egov.regix.RegixClient;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.indexui.system.Constants;
import com.ib.system.BaseSystemData;
import com.ib.system.SysConstants;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Тука ще има методи, които от респонса на регикс сетват данни в нашите обекти фзл/нфл/адреси и т.н.
 *
 * @author belev
 */
public class RegixUtils2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegixUtils2.class);
	private static RegixClient regixClient;

	private static RegixClient getRegixClient(SystemData sd) throws RegixClientException {
		try {
			if (regixClient == null) { // иницализация трябва
				LOGGER.info("START RegixClient initialization ...");

				String keystore = sd.getSettingsValue("regix.keystore.location");
				if (keystore == null) {
					throw new bg.egov.regix.RegixClientException("Missing Setting regix.keystore.location!");
				}
				String password = sd.getSettingsValue("regix.keystore.password");
				if (password == null) {
					throw new bg.egov.regix.RegixClientException("Missing Setting regix.keystore.password!");
				}
				String type = sd.getSettingsValue("regix.keystore.type");
				if (type == null) {
					throw new bg.egov.regix.RegixClientException("Missing Setting regix.keystore.type!");
				}
				String wsdl = sd.getSettingsValue("regix.wsdl.url");
				if (wsdl == null) {
					throw new bg.egov.regix.RegixClientException("Missing Setting regix.wsdl.url!");
				}
				LOGGER.info("RegixClient wsdl={}", wsdl);

				try (InputStream input = new FileInputStream(keystore)) {

					regixClient = RegixClient.create(new URL(wsdl), input, password.toCharArray(), type);

					LOGGER.info("END RegixClient initialization ... success");
				} catch (Exception e) {
					LOGGER.error("RegixClient init ERROR", e);
					throw new RegixClientException(e);
				}
			}
		}catch(Exception e){
			LOGGER.error("RegixClient init ERROR", e);
			throw new RegixClientException(e);
		}
		return regixClient;
	}

	/**
	 * Зареждане на данни за физическо лице от REGIX по подадено ЕГН.
	 *
	 * @param egn
	 * @param loadReferent         - дали искаме да се зареждат основни данни за лицето
	 * @param loadPermanentAddress - дали искаме да се зарежда постоянния адрес
	 * @param loadCurrentAddress   - дали искаме да се зарежда настоящ/кореспонденция адрес
	 * @return <code>true</code> ако има разлики от RegIX спрямо обекта, иначе <code>false</code>
	 * @throws RegixClientException
	 * @throws DbErrorException
	 * @throws DatatypeConfigurationException
	 */
	public static boolean loadFizLiceByEgn(Referent referent, String egn //
			, boolean loadReferent //
			, boolean loadPermanentAddress, boolean loadCurrentAddress //
			, SystemData sd) throws DbErrorException, RegixClientException, DatatypeConfigurationException {
		boolean changed = false;

		RegixClient client = getRegixClient(sd);

		// зареждаме основните данни
		if (loadReferent) {
			PersonDataRequestType requestMainData = new PersonDataRequestType();
			requestMainData.setEGN(egn);
			PersonDataResponseType responseMainData = (PersonDataResponseType) client.executeOperation(GraoOperation.PERSON_DATA_SEARCH, requestMainData);

			if (responseMainData != null) {
				responseMainData.setEGN(egn); // заради тестовият регикс, защото го подменя
				changed |= setFzlReferent(referent, responseMainData);
			}
		}

		// зареждане на постоянен адрес
		if (loadPermanentAddress) {
			PermanentAddressRequestType requestPermAddress = new PermanentAddressRequestType();
			requestPermAddress.setEGN(egn);
			requestPermAddress.setSearchDate(DateUtils.toGregorianCalendar(new Date()));

			PermanentAddressResponseType responsePermAddress = (PermanentAddressResponseType) client.executeOperation(GraoOperation.PERMANENT_ADDRESS_SEARCH, requestPermAddress);
			if (responsePermAddress != null) {
				if (referent.getAddress() == null) {
					referent.setAddress(new ReferentAddress());
				}
				changed |= setFzlReferentAddress(referent.getAddress(), responsePermAddress);
			}
		}

		// зареждане на настоящ адрес (адрес за кореспонденция)
		if (loadCurrentAddress) {
			TemporaryAddressRequestType requestCurrentAddress = new TemporaryAddressRequestType();
			requestCurrentAddress.setEGN(egn);
			requestCurrentAddress.setSearchDate(DateUtils.toGregorianCalendar(new Date()));

			TemporaryAddressResponseType responseCurrentAddress = (TemporaryAddressResponseType) client.executeOperation(GraoOperation.TEMPORARY_ADDRESS_SEARCH, requestCurrentAddress);
			if (responseCurrentAddress != null) {
				if (referent.getAddressKoresp() == null) {
					referent.setAddressKoresp(new ReferentAddress());
				}
				changed |= setFzlReferentAddress(referent.getAddressKoresp(), responseCurrentAddress, sd);
			}
		}

		return changed;
	}



	/**
	 * Зареждане на данни за юридическо лице от REGIX по подадено ЕИК.
	 *
	 * @param referent
	 * @param eik
	 * @param loadReferent         - дали искаме да се зареждат основни данни за лицето
	 * @param loadPermanentAddress - дали искаме да се зарежда постоянния адрес
	 * @param loadCurrentAddress   - дали искаме да се зарежда настоящ/кореспонденция адрес
	 * @param sd
	 * @return <code>true</code> ако има разлики от RegIX спрямо обекта, иначе <code>false</code>
	 * @throws DbErrorException
	 * @throws RegixClientException
	 * @throws DatatypeConfigurationException
	 */
	public static boolean loadUridLiceByEik(Referent referent, String eik //
			, boolean loadReferent //
			, boolean loadPermanentAddress, boolean loadCurrentAddress //
			, SystemData sd) throws DbErrorException, RegixClientException {
		boolean changed = false;

		//RegixClient client = sd.getRegixClient();
		RegixClient client = getRegixClient(sd);

		ActualStateRequestType request = new ActualStateRequestType();
		request.setUIC(eik);

		// тука извикването е едно и винаги трябва и ще се направи еднократно
		ActualStateResponseType response = (ActualStateResponseType) client.executeOperation(TROperation.GET_ACTUAL_STATE, request);
		if (response != null) {
			response.setUIC(eik); // заради тестовият регикс, защото го подменя

			if (loadReferent) { // основни данни
				changed |= setNflReferent(referent, response);
			}

			if (loadPermanentAddress) {
				if (referent.getAddress() == null) {
					referent.setAddress(new ReferentAddress());
				}
				referent.getAddress().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN);

//				Код на поле:00050 - Седалище и адрес на управление
				AddressType seatAddress = null;
				if (response.getSeat() != null && response.getSeat().getAddress() != null) {
					seatAddress = response.getSeat().getAddress();
				}
				changed |= setNflReferentAddress(referent.getAddress(), seatAddress, sd);
			}

			if (loadCurrentAddress) {
				if (referent.getAddressKoresp() == null) {
					referent.setAddressKoresp(new ReferentAddress());
				}
				referent.getAddressKoresp().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);

//				Код на поле:00051 - Адрес за кореспонденция с НАП на територията на страната
				AddressType seatForCorrespondence = response.getSeatForCorrespondence();
				changed |= setNflReferentAddress(referent.getAddressKoresp(), seatForCorrespondence, sd);
			}
		}

		return changed;
	}

	/**
	 * Сетва в обекта Referent данните от RegIX. В данните от RegIX няма адреси и за това трябват да се ползва отделните методи за
	 * адреси
	 *
	 * @param referent
	 * @param response
	 * @return <code>true</code> ако има разлики от RegIX спрямо обекта, иначе <code>false</code>
	 */
	static boolean setFzlReferent(Referent referent, PersonDataResponseType response) {
		if (response == null || response.getPersonNames() == null) {
			return false;
		}
		referent.setFzlEgn(response.getEGN()); // това е безусловно защото за нов запис трябва, а за корекция е еднакво
		referent.setRefGrj(BabhConstants.CODE_ZNACHENIE_BULGARIA); // щом е търсене по ЕГН все пак трябва да е БГ);

		boolean changed = false;

		//
		if (setPersonNames(referent, response.getPersonNames(), false)) {
			changed = true;
		}

		//
		if (setPersonNames(referent, response.getLatinNames(), true)) {
			changed = true;
		}

		//
		Date fzlBirthDate = DateUtils.toDate(response.getBirthDate());
		if (fzlBirthDate != null && !Objects.equals(referent.getFzlBirthDate(), fzlBirthDate)) {
			changed = true;
			referent.setFzlBirthDate(fzlBirthDate);
		}

		//
		if (!isStringEq(referent.getFzlBirthPlace(), response.getPlaceBirth())) {
			changed = true;
			referent.setFzlBirthPlace(response.getPlaceBirth());
		}

		//
		Date deathDate = DateUtils.toDate(response.getDeathDate());
		if (deathDate != null && !Objects.equals(referent.getDateSmart(), deathDate)) {
			changed = true;
			referent.setDateSmart(deathDate);
		}

		//
		String nationality = null;
		if (response.getNationality() != null) {
			nationality = response.getNationality().getNationalityName();
		}
		if (!isStringEq(referent.getNationality(), nationality)) {
			changed = true;
			referent.setNationality(nationality);
		}

		return changed;
	}

	/**
	 * Сетва в обекта ReferentAddress данните от RegIX за постоянен адрес.
	 *
	 * @param address
	 * @param response
	 * @return <code>true</code> ако има разлики от RegIX спрямо обекта, иначе <code>false</code>
	 */
	static boolean setFzlReferentAddress(ReferentAddress address, PermanentAddressResponseType response) {
		if (response == null) {
			return false;
		}
		address.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN); // това е безусловно защото за нов запис трябва, а
																				// за корекция е еднакво
		address.setAddrCountry(BabhConstants.CODE_ZNACHENIE_BULGARIA); // за постоянния не връщат изобщо държава и остава БГ

		boolean changed = false;

		//
		StringBuilder addrText = new StringBuilder();
		String t = SearchUtils.trimToNULL(response.getCityArea());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(", ");
			}
			addrText.append(t);
		}
		t = SearchUtils.trimToNULL(response.getLocationName());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(", ");
			}
			String tUp = t.toUpperCase();
			if (tUp.indexOf("БУЛ") == -1 && tUp.indexOf("УЛ") == -1 && tUp.indexOf("Ж.К") == -1 && tUp.indexOf("ЖК") == -1 && tUp.indexOf("ПЛ") == -1) {
				addrText.append("ул. "); // ако няма изрично булевар или улица добавям улица
			}
			addrText.append(t);
		}
		t = SearchUtils.trimToNULL(response.getBuildingNumber());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(" ");
			}
			addrText.append("№ " + t);
		}
		t = SearchUtils.trimToNULL(response.getEntrance());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(", ");
			}
			addrText.append("вх. " + t);
		}
		t = SearchUtils.trimToNULL(response.getFloor());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(", ");
			}
			addrText.append("ет. " + t);
		}
		t = SearchUtils.trimToNULL(response.getApartment());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(", ");
			}
			addrText.append("ап. " + t);
		}
		if (!isStringEq(address.getAddrText(), addrText.toString())) {
			changed = true;
			address.setAddrText(addrText.toString());
		}

		//
		Integer ekatte = null;
		String ekatteCode = SearchUtils.trimToNULL(response.getSettlementCode());
		if (ekatteCode != null) {
			try {
				ekatte = Integer.parseInt(ekatteCode);
			} catch (Exception e) { // при нас е число
			}
		}
		if (ekatte != null && !Objects.equals(address.getEkatte(), ekatte)) {
			changed = true;
			address.setEkatte(ekatte);
		}

		return changed;
	}

	/**
	 * Сетва в обекта ReferentAddress данните от RegIX за настоящ адрес.
	 *
	 * @param address
	 * @param response
	 * @param sd
	 * @return <code>true</code> ако има разлики от RegIX спрямо обекта, иначе <code>false</code>
	 * @throws DbErrorException
	 */
	static boolean setFzlReferentAddress(ReferentAddress address, TemporaryAddressResponseType response, BaseSystemData sd) throws DbErrorException {
		if (response == null) {
			return false;
		}
		address.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP); // това е безусловно защото за нов запис трябва, а за
																				// корекция е еднакво
		boolean changed = false;

		//
		StringBuilder addrText = new StringBuilder();
		String t = SearchUtils.trimToNULL(response.getCityArea());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(", ");
			}
			addrText.append(t);
		}
		t = SearchUtils.trimToNULL(response.getLocationName());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(", ");
			}
			String tUp = t.toUpperCase();
			if (tUp.indexOf("БУЛ") == -1 && tUp.indexOf("УЛ") == -1 && tUp.indexOf("Ж.К") == -1 && tUp.indexOf("ЖК") == -1 && tUp.indexOf("ПЛ") == -1) {
				addrText.append("ул. "); // ако няма изрично булевар или улица добавям улица
			}
			addrText.append(t);
		}
		t = SearchUtils.trimToNULL(response.getBuildingNumber());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(" ");
			}
			addrText.append("№ " + t);
		}
		t = SearchUtils.trimToNULL(response.getEntrance());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(", ");
			}
			addrText.append("вх. " + t);
		}
		t = SearchUtils.trimToNULL(response.getFloor());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(", ");
			}
			addrText.append("ет. " + t);
		}
		t = SearchUtils.trimToNULL(response.getApartment());
		if (t != null) {
			if (addrText.length() > 0) {
				addrText.append(", ");
			}
			addrText.append("ап. " + t);
		}
		if (!isStringEq(address.getAddrText(), addrText.toString())) {
			changed = true;
			address.setAddrText(addrText.toString());
		}

		//
		Integer addrCountry = null;
		Integer ekatte = null;
		String ekatteCode = SearchUtils.trimToNULL(response.getSettlementCode());
		if (ekatteCode != null) {
			try {
				ekatte = Integer.parseInt(ekatteCode);
				addrCountry = BabhConstants.CODE_ZNACHENIE_BULGARIA; // щом има ЕКАТТЕ е БГ
			} catch (Exception e) { // при нас е число
			}
		}
		if (ekatte != null && !Objects.equals(address.getEkatte(), ekatte)) {
			changed = true;
			address.setEkatte(ekatte);
		}

		//
		if (addrCountry == null) { // ако от горе е решено, че е БГ няма какво да се пробва повече
			String countryName = SearchUtils.trimToNULL(response.getCountryName());
			if (countryName != null) {
				List<SystemClassif> items = sd.getItemsByTekst(Constants.CODE_CLASSIF_COUNTRIES, countryName, SysConstants.CODE_LANG_BG, null);
				if (!items.isEmpty()) {
					addrCountry = items.get(0).getCode();
				}
			}
		}
		if (addrCountry != null && !Objects.equals(address.getAddrCountry(), addrCountry)) {
			changed = true;
			address.setAddrCountry(addrCountry);
		}

		return changed;
	}

	/**
	 * Сетва в обекта Referent данните от RegIX. В данните от RegIX има всичко +адреса.
	 *
	 * @param referent
	 * @param response
	 * @return <code>true</code> ако има разлики от RegIX спрямо обекта, иначе <code>false</code>
	 */
	static boolean setNflReferent(Referent referent, ActualStateResponseType response) {
		if (response == null || response.getCompany() == null) {
			return false;
		}
		referent.setNflEik(response.getUIC()); // това е безусловно защото за нов запис трябва, а за корекция е еднакво
		referent.setRefGrj(BabhConstants.CODE_ZNACHENIE_BULGARIA); // за НФЛ май няма значение но по булстат трябва да е БГ

		boolean changed = false;

		//
		if (!isStringEq(referent.getRefName(), response.getCompany())) {
			changed = true;
			referent.setRefName(response.getCompany());
		}

		//
		if (!isStringEq(referent.getRefLatin(), response.getTransliteration())) {
			changed = true;
			referent.setRefLatin(response.getTransliteration());
		}

		String liquidation = null;
		if (response.getLiquidationOrInsolvency() != null) {
			liquidation = response.getLiquidationOrInsolvency().value();

		} else if (response.getStatus() != null && response.getStatus().value() != null && response.getStatus().value().toUpperCase().indexOf("ЗАКРИТА") != -1) {
			liquidation = response.getStatus().value();
		}
		if (!isStringEq(referent.getLiquidation(), liquidation)) {
			changed = true;
			referent.setLiquidation(liquidation);
		}

		//
		String contactEmail = null;
		String contactPhone = null;
		if (response.getSeat() != null && response.getSeat().getContacts() != null) {
			contactEmail = response.getSeat().getContacts().getEMail();
			contactPhone = response.getSeat().getContacts().getPhone();
		}
		if (!isStringEq(referent.getContactEmail(), contactEmail)) {
			changed = true;
			referent.setContactEmail(contactEmail);
		}
		if (!isStringEq(referent.getContactPhone(), contactPhone)) {
			changed = true;
			referent.setContactPhone(contactPhone);
		}

		return changed;
	}

	/**
	 * Обработва данните за адрес на нефизическо лице
	 *
	 * @param address
	 * @param response
	 * @param sd
	 * @return
	 * @throws DbErrorException
	 */
	static boolean setNflReferentAddress(ReferentAddress address, AddressType response, BaseSystemData sd) throws DbErrorException {
		if (response == null) {
			return false;
		}
		Integer addrCountry = null;
		Integer ekatte = null;
		String postCode = null;
		StringBuilder addrText = new StringBuilder();

		if (response != null) {
			//
			String countryName = SearchUtils.trimToNULL(response.getCountry());
			if (countryName != null) {
				List<SystemClassif> items = sd.getItemsByTekst(Constants.CODE_CLASSIF_COUNTRIES, countryName, SysConstants.CODE_LANG_BG, null);
				if (!items.isEmpty()) {
					addrCountry = items.get(0).getCode();
				}
			}

			//
			String ekatteCode = SearchUtils.trimToNULL(response.getSettlementEKATTE());
			if (ekatteCode != null) {
				try {
					ekatte = Integer.parseInt(ekatteCode);
					addrCountry = BabhConstants.CODE_ZNACHENIE_BULGARIA; // щом има ЕКАТТЕ е БГ
				} catch (Exception e) { // при нас е число
				}
			}

			//
			postCode = response.getPostCode();

			//
			String t = SearchUtils.trimToNULL(response.getHousingEstate());
			if (t != null) {
				if (addrText.length() > 0) {
					addrText.append(", ");
				}
				String tUp = t.toUpperCase();
				if (tUp.indexOf("Ж.") == -1) {
					addrText.append("ж.к. "); // ако няма изрично го добавям
				}
				addrText.append(t);
			}
			t = SearchUtils.trimToNULL(response.getStreet());
			if (t != null) {
				if (addrText.length() > 0) {
					addrText.append(", ");
				}
				String tUp = t.toUpperCase();
				if (tUp.indexOf("БУЛ") == -1 && tUp.indexOf("УЛ") == -1 && tUp.indexOf("Ж.К") == -1 && tUp.indexOf("ЖК") == -1 && tUp.indexOf("ПЛ") == -1) {
					addrText.append("ул. "); // ако няма изрично булевар или улица добавям улица
				}
				addrText.append(t);
			}
			t = SearchUtils.trimToNULL(response.getStreetNumber());
			if (t != null) {
				if (addrText.length() > 0) {
					addrText.append(" ");
				}
				addrText.append("№ " + t);
			}
			t = SearchUtils.trimToNULL(response.getBlock());
			if (t != null) {
				if (addrText.length() > 0) {
					addrText.append(", ");
				}
				addrText.append("бл. " + t);
			}
			t = SearchUtils.trimToNULL(response.getEntrance());
			if (t != null) {
				if (addrText.length() > 0) {
					addrText.append(", ");
				}
				addrText.append("вх. " + t);
			}
			t = SearchUtils.trimToNULL(response.getFloor());
			if (t != null) {
				if (addrText.length() > 0) {
					addrText.append(", ");
				}
				addrText.append("ет. " + t);
			}
			t = SearchUtils.trimToNULL(response.getApartment());
			if (t != null) {
				if (addrText.length() > 0) {
					addrText.append(", ");
				}
				addrText.append("ап. " + t);
			}
			t = SearchUtils.trimToNULL(response.getForeignPlace());
			if (t != null) {
				if (addrText.length() > 0) {
					addrText.append(", ");
				}
				addrText.append(" " + t);
			}
		}

		boolean changed = false;
		if (addrCountry != null && !Objects.equals(address.getAddrCountry(), addrCountry)) {
			changed = true;
			address.setAddrCountry(addrCountry);
		}
		if (ekatte != null && !Objects.equals(address.getEkatte(), ekatte)) {
			changed = true;
			address.setEkatte(ekatte);
		}
		if (!isStringEq(address.getPostCode(), postCode)) {
			changed = true;
			address.setPostCode(postCode);
		}
		if (!isStringEq(address.getAddrText(), addrText.toString())) {
			changed = true;
			address.setAddrText(addrText.toString());
		}
		return changed;
	}

	/**
	 * Иска се ако от RegIX дойде празно да се смята, че няма разлика и не се пипа текста при нас !!!
	 *
	 * @return true ако са еднакви, като за еднакви се смятат "" и " " еднаково на НУЛЛ !!!
	 */
	private static boolean isStringEq(String babh, String regix) {
		babh = SearchUtils.trimToNULL(babh);
		regix = SearchUtils.trimToNULL(regix);
		return regix == null || "-".equals(regix) || Objects.equals(babh, regix);
	}

	/**
	 * сглобява ги с разделител " ". Ако не са на латиница ги разпределя и по новите колони
	 */
	private static boolean setPersonNames(Referent referent, PersonNames personNames, boolean latin) {
		if (personNames == null) {
			personNames = new PersonNames(); // за да не се бърка в логиката ако няма данни ще си сработи правилно
		}

		boolean changed = false;

		String ime = null;
		String prezime = null;
		String familia = null;

		StringBuilder names = new StringBuilder();
		if (personNames.getFirstName() != null) {
			if (names.length() > 0) {
				names.append(" ");
			}
			names.append(personNames.getFirstName());
			ime = String.valueOf(personNames.getFirstName());
		}
		if (personNames.getSurName() != null) {
			if (names.length() > 0) {
				names.append(" ");
			}
			names.append(personNames.getSurName());
			prezime = String.valueOf(personNames.getSurName());
		}
		if (personNames.getFamilyName() != null) {
			if (names.length() > 0) {
				names.append(" ");
			}
			names.append(personNames.getFamilyName());
			familia = String.valueOf(personNames.getFamilyName());
		}

		if (latin) {
			if (!isStringEq(referent.getRefLatin(), names.toString())) {
				changed = true;
				referent.setRefLatin(names.toString());
			}
		} else {
			if (!isStringEq(referent.getRefName(), names.toString())) {
				changed = true;
				referent.setRefName(names.toString());
			}

			if (!isStringEq(referent.getIme(), ime)) {
				changed = true;
				referent.setIme(ime);
			}
			if (!isStringEq(referent.getPrezime(), prezime)) {
				changed = true;
				referent.setPrezime(prezime);
			}
			if (!isStringEq(referent.getFamilia(), familia)) {
				changed = true;
				referent.setFamilia(familia);
			}
		}

		return changed;
	}

	/** */
//	private RegixUtils2(String wsdl,String keystore,String password) throws RegixClientException {
//		super();
//		LOGGER.info("START RegixClient initialization ...");
//
//		if (keystore == null) {
//			throw new RegixClientException("Missing Setting regix.keystore.location!");
//		}
//
//		if (password == null) {
//			throw new RegixClientException("Missing Setting regix.keystore.password!");
//		}
//
////		if (type == null) {
////			throw new bg.government.regixclient.RegixClientException("Missing Setting regix.keystore.type!");
////		}
//
//		if (wsdl == null) {
//			throw new RegixClientException("Missing Setting regix.wsdl.url!");
//		}
//		LOGGER.info("RegixClient wsdl={}", wsdl);
//
//		try (InputStream input = new FileInputStream(keystore)) {
//
//			this.regixClient = RegixClient.create(new URL(wsdl), input, password.toCharArray(), "PKCS12");
//
//			LOGGER.info("END RegixClient initialization ... success");
//		} catch (Exception e) {
//			LOGGER.error("RegixClient init ERROR", e);
//			throw new RegixClientException(e);
//		}
//
//	}

}
