package com.ib.babhregs.udostDocs;

import com.aspose.words.ControlChar;
import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.MpsFurajiDAO;
import com.ib.babhregs.db.dto.*;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ldap.Control;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.ib.system.utils.ValidationUtils.isNotBlank;

/**
 * Всички методи трябва да връщат String при налична стойност
 * или null при липсваща стойност и да не приемат параметри.
 * Да се прихващат всички exceptions и вместо тях да връщат null. 
 * <br/>
 * <b>Ако се преименува метод</b>,
 * трябва да се оправи в екрана за попълване на шаблони като се избере 
 * бутонът за преименуване на метод и се смени старото име с ново. 
 * Това ще оправи всички записани досега шаблони.
 * 
 * @author n.kanev
 */
@SuppressWarnings("unused")
public class UdostDokumentMethods {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UdostDokumentMethods.class);
	private static final String DB_ERROR_MSG = "Грешка при работа с базата";
	
	private static final SimpleDateFormat DATE_FORMAT_DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy");
	
	private final Helpers helpers;
	private final Vpisvane vpisvane;
	private final Doc doc;
	private final UserData userData;
	private final SystemData systemData;
	private final Date date;
	
	public UdostDokumentMethods(Vpisvane vpisvane, Doc doc, UserData userData, SystemData systemData) {
		this.vpisvane = vpisvane;
		this.doc = doc;
		this.userData = userData;
		this.systemData = systemData;
		
		this.date = new Date();
		this.helpers = new Helpers(userData, systemData, vpisvane);
	}

	/**
	 * @return Рег. номер на документа от вписването
	 */
	public String getVpisvaneRegNomResult() {
		if(isNotBlank(this.doc.getRnDoc())) {
			return this.doc.getRnDoc();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return дата на документа от вписването
	 */
	public String getVpisvaneDateResult() {
		if(this.doc.getDocDate() != null) {
			return DATE_FORMAT_DD_MM_YYYY.format(this.doc.getDocDate());
		}
		else {
			return null;
		}
	}
	
	public String getCurrZaiavlRegNom() {
		try {
			Integer currZaiavId = this.helpers.getCurrZaiavlId(this.doc.getId());
			
			if(currZaiavId != null) {
				Doc zaiavlenie = new DocDAO(userData).findById(currZaiavId);
				return ((zaiavlenie != null) && isNotBlank(zaiavlenie.getRnDoc())) ? zaiavlenie.getRnDoc() : null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getCurrZaiavlDate() {
		try {
			Integer currZaiavId = this.helpers.getCurrZaiavlId(this.doc.getId());
			
			if(currZaiavId != null) {
				Doc zaiavlenie = new DocDAO(userData).findById(currZaiavId);
				return ((zaiavlenie != null) && isNotBlank(zaiavlenie.getRnDoc())) 
						? DATE_FORMAT_DD_MM_YYYY.format(zaiavlenie.getDocDate())
						: null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getPrevUdRegNom() {
		try {
			Integer prevUdId = this.helpers.getPrevUdId(this.doc.getId());
			
			if(prevUdId != null) {
				Doc ud = new DocDAO(userData).findById(prevUdId);
				return ((ud != null) && isNotBlank(ud.getRnDoc())) ? ud.getRnDoc() : null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getPrevUdDate() {
		try {
			Integer prevUdId = this.helpers.getPrevUdId(this.doc.getId());
			
			if(prevUdId != null) {
				Doc ud = new DocDAO(userData).findById(prevUdId);
				return ((ud != null) && isNotBlank(ud.getRnDoc())) 
						? DATE_FORMAT_DD_MM_YYYY.format(ud.getDocDate())
						: null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getDocChangeCyr() {
		return (isNotBlank(this.doc.getChangeBg()))
				? this.doc.getChangeBg()
				: null;
	}
	
	public String getDocChangeLat() {
		return (isNotBlank(this.doc.getChangeEn()))
				? this.doc.getChangeEn()
				: null;
	}
	
	public String getDocVersia() {
		return (isNotBlank(this.doc.getDocVersion()))
				? this.doc.getDocVersion()
				: null;
	}
	
	/**
	 * @return Името/Наименованието на референта на кирилица
	 */
	public String getReferentImeOrNaimenovanieCyr() {
		try {
			return this.helpers.getReferentImeOrNaimenovanieCyr();
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Името/Наименованието на референта на латиница
	 */
	public String getReferentImeOrNaimenovanieLat() {
		try {
			return this.helpers.getReferentImeOrNaimenovanieLat();
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Стринг "ЕГН" или "ЕИК" в зависимост дали референтът е ФЗЛ/НФЛ
	 */
	public String getReferentEgnOrEikLiteral() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			return this.helpers.getEgnEikByReferent(r)[0];
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return ЕГН или ЕИК на референта в зависимост дали е ФЗЛ или НФЛ
	 */
	public String getReferentEgnOrEik() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			return this.helpers.getEgnEikByReferent(r)[1];
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * Адрес на референта/кореспондента, който се взема от vpisvane.getIdLicenziant
	 * @return адреса като "държава, нас. място, адрес, п.код, п. кутия"
	 */
	public String getReferentAdresCyr() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			String[] addressFields = this.helpers.getAddressFieldsByReferent(r, false);
			return this.helpers.getAdresStringFromReferentAdresFields(addressFields);
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}

	/**
	 * Адрес на референта/кореспондента, който се взема от vpisvane.getIdLicenziant
	 * @return адреса като "държава, нас. място, адрес, п.код, п. кутия"
	 */
	public String getReferentAdresLat() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			String[] addressFields = this.helpers.getAddressFieldsByReferent(r, true);
			return this.helpers.getAdresStringFromReferentAdresFields(addressFields);
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getReferentAddrTextCyr() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			String[] addressFields = this.helpers.getAddressFieldsByReferent(r, false);
			return isNotBlank(addressFields[2]) ? addressFields[2] : null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getReferentEkatteCyr() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			String[] addressFields = this.helpers.getAddressFieldsByReferent(r, false);
			return isNotBlank(addressFields[1]) ? addressFields[1] : null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getReferentPostCode() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			String[] addressFields = this.helpers.getAddressFieldsByReferent(r, false);
			return isNotBlank(addressFields[3]) ? addressFields[3] : null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getReferentPhone() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			if(r != null) {
				return isNotBlank(r.getContactPhone()) ? r.getContactPhone() : null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getReferentEmail() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			if(r != null) {
				return isNotBlank(r.getContactEmail()) ? r.getContactEmail() : null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getDocPodatelImeOrNaimenovanie() {
		try {
			Referent r = this.helpers.getReferentById(this.doc.getCodeRefCorresp());
			if(r != null) {
				return isNotBlank(r.getRefName()) ? r.getRefName() : null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Име на обекта на дейност 
	 */
	public String getObektDeinostNaimenovanie() {
		ObektDeinost obekt = this.helpers.getObektDeinost();
		if(obekt != null) {
			return (isNotBlank(obekt.getNaimenovanie())) ? obekt.getNaimenovanie() : null;
		}
		else return null;
	}
	
	/**
	 * @return Име на обекта на дейност 
	 */
	public String getNaimenovanieObektFromOezReg() {
		OezReg oezReg = this.vpisvane.getOezReg();
		if(oezReg != null) {
			String naimenovanie =  this.vpisvane.getOezReg().getNaimenovanie();
			return (isNotBlank(naimenovanie)) ? naimenovanie : null;
		}
		else return null;
	}
	
	/**
	 * Адрес на обекта, който се взема от дейността към вписването
	 * @return адреса като "държава, област, община, нас. място, адрес, п. код"
	 */
	public String getAdresObekt() {
		try {
			ObektDeinost deinost = this.helpers.getObektDeinost();

			if(deinost != null) {
				String[] addressFields = this.helpers.getAddressFieldsByObektDeinost(deinost, false);
				String adres = this.helpers.getAdresStringFromObektAdresFields(addressFields);
				return (isNotBlank(adres)) ? adres : null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}

	public String getAdresObektOezReg() {
		try {
			String country = this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_COUNTRIES,
					this.vpisvane.getOezReg().getDarj(),
					UdostDokumentMethods.this.userData.getCurrentLang(),
					UdostDokumentMethods.this.date);
			String obl = this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_EKATTE,
					this.vpisvane.getOezReg().getObl(),
					UdostDokumentMethods.this.userData.getCurrentLang(),
					UdostDokumentMethods.this.date);
			String obsht = UdostDokumentMethods.this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_EKATTE,
					this.vpisvane.getOezReg().getObsht(),
					UdostDokumentMethods.this.userData.getCurrentLang(),
					UdostDokumentMethods.this.date);
			String ekatte = UdostDokumentMethods.this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_EKATTE,
					this.vpisvane.getOezReg().getNasMesto(),
					UdostDokumentMethods.this.userData.getCurrentLang(),
					UdostDokumentMethods.this.date);
			String adres = this.vpisvane.getOezReg().getAddress();
			String postCode = (this.vpisvane.getOezReg().getPostCode() == null) ? null : String.valueOf(this.vpisvane.getOezReg().getPostCode());
			
			StringBuilder sb = new StringBuilder();
			if(country != null && !country.contains("Ненамерено значение")) sb.append(country).append(", ");
			if(obl != null && !obl.contains("Ненамерено значение")) sb.append(obl).append(", ");
			if(obsht != null && !obsht.contains("Ненамерено значение")) sb.append(obsht).append(", ");
			if(ekatte != null && !ekatte.contains("Ненамерено значение")) sb.append(ekatte).append(", ");
			if(adres != null) sb.append(adres).append(", ");
			if(postCode != null) sb.append(postCode);
			
			if(sb.lastIndexOf(", ") == sb.length() - 2) {
				sb.delete(sb.lastIndexOf(", "), sb.length());
			}
			
			return sb.length() == 0 ? null : sb.toString();
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Името на лицето "отговорно лице" от ObektDeinost
	 */
	public String getObektDeinostOtgLice() {
		ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_OTG);
		if(lice != null && lice.getReferent() != null && isNotBlank(lice.getReferent().getRefName())) {
			return lice.getReferent().getRefName();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Името на лицето "управител" от ObektDeinost
	 */
	public String getObektDeinostUpravitelIme() {
		ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
		if(lice != null && isNotBlank(lice.getReferent().getRefName())) {
			return lice.getReferent().getRefName();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Стринг "ЕГН" или "ЕИК" в зависимост дали лицето управител е ФЗЛ/НФЛ
	 */
	public String getUpravitelEgnOrEikLiteral() {
		try {	
			ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
			if(lice != null) {
				Referent r = this.helpers.getReferentById(lice.getCodeRef());
				return this.helpers.getEgnEikByReferent(r)[0];
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return ЕГН или ЕИК на лицето управител в зависимост дали е ФЗЛ/НФЛ
	 */
	public String getUpravitelEgnOrEik() {
		try {	
			ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
			if(lice != null) {
				Referent r = this.helpers.getReferentById(lice.getCodeRef());
				return this.helpers.getEgnEikByReferent(r)[1];
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Номера на дипломата на управителя
	 */
	public String getUpravitelDiplomaNomer() {
		ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
		if(lice != null && lice.getReferent() != null) {
			ReferentDoc diploma = this.helpers.getReferentDocByVid(lice.getReferent(), BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA);
			if(diploma != null) {
				return isNotBlank(diploma.getNomDoc()) ? diploma.getNomDoc() : null;
			}
			else return null;
		}
		else return null;
	}
	
	/**
	 * @return Дата на издаване на дипломата на управителя
	 */
	public String getUpravitelDiplomaDate() {
		ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
		if(lice != null && lice.getReferent() != null) {
			ReferentDoc diploma = this.helpers.getReferentDocByVid(lice.getReferent(), BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA);
			if(diploma != null) {
				return (diploma.getDateIssued() == null) ? null : DATE_FORMAT_DD_MM_YYYY.format(diploma.getDateIssued());
			}
			else return null;
		}
		else return null;
	}
	
	/**
	 * @return issuedBy на дипломата на управителя
	 */
	public String gerUpravitelDiplomaIssuedBy() {
		ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
		if(lice != null && lice.getReferent() != null) {
			ReferentDoc diploma = this.helpers.getReferentDocByVid(lice.getReferent(), BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA);
			if(diploma != null) {
				return isNotBlank(diploma.getIssuedBy()) ? diploma.getIssuedBy() : null;
			}
			else return null;
		}
		else return null;
	}
	
	public String gerUpravitelUdostNomDoc() {
		ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
		if(lice != null && lice.getReferent() != null) {
			ReferentDoc udost = this.helpers.getReferentDocByVid(lice.getReferent(), BabhConstants.CODE_ZNACHENIE_VIDDOC_UDOST_BVS);
			if(udost != null) {
				return isNotBlank(udost.getNomDoc()) ? udost.getNomDoc() : null;
			}
			else return null;
		}
		else return null;
	}
	
	public String gerUpravitelUdostDate() {
		ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
		if(lice != null && lice.getReferent() != null) {
			ReferentDoc udost = this.helpers.getReferentDocByVid(lice.getReferent(), BabhConstants.CODE_ZNACHENIE_VIDDOC_UDOST_BVS);
			if(udost != null) {
				return (udost.getDateIssued() == null) ? null : DATE_FORMAT_DD_MM_YYYY.format(udost.getDateIssued());
			}
			else return null;
		}
		else return null;
	}
	
	public String getUpravitelUrn() {
		ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
		if(lice != null && lice.getReferent() != null) {
			return isNotBlank(lice.getReferent().getUrn()) ? lice.getReferent().getUrn() : null;
		}
		else return null;
	}
	
	public String getObektDeinostUpravitelAdres() {
		ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
		if(lice != null && lice.getReferent() != null) {
			try {
				String[] address = this.helpers.getAddressFieldsByReferent(lice.getReferent(), false);
				return this.helpers.getAdresStringFromReferentAdresFields(address);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	public String getVeterinari240() {
		String pattern = "%s, %s %s с адрес: %s, притежаващ диплома №%s, издадена на %s г. от ВУЗ %s,"
				+ " с номер %s на издадено удостоверение за членство в БВС от дата %s г."
				+ " Уникален регистрационен номер %s.";
		
		try {
			List<String> veterinari = new ArrayList<>();
			ObektDeinost obektDeinost = this.helpers.getObektDeinost();
			if(obektDeinost == null) return null;
			
			List<ObektDeinostLica> lica = obektDeinost.getObektDeinostLica()
					.stream()
					.filter(l -> l.getRole().equals(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_VET_LEKAR) && l.getDateEnd() == null)
					.collect(Collectors.toList());
 
			for(var lice : lica) {
				Referent r = lice.getReferent();
				if(r == null) continue;
				
				ReferentDoc diploma = this.helpers.getReferentDocByVid(lice.getReferent(), BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA);
				ReferentDoc udost = this.helpers.getReferentDocByVid(lice.getReferent(), BabhConstants.CODE_ZNACHENIE_VIDDOC_UDOST_BVS);
				
				String imena = r.getRefName();
				String egnEikLiteral = isNotBlank(this.helpers.getEgnEikByReferent(r)[0]) ? this.helpers.getEgnEikByReferent(r)[0] : "";
				String egnEik = isNotBlank(this.helpers.getEgnEikByReferent(r)[1]) ? this.helpers.getEgnEikByReferent(r)[1] : "";
				String adres = this.helpers.getAdresStringFromReferentAdresFields(this.helpers.getAddressFieldsByReferent(r, false));
				String diplomaNom = (diploma != null && isNotBlank(diploma.getNomDoc())) ? diploma.getNomDoc() : "";
				String diplomaDate = (diploma == null || diploma.getDateIssued() == null) ? "" : DATE_FORMAT_DD_MM_YYYY.format(diploma.getDateIssued());
				String diplomaIssuedBy = (diploma != null && isNotBlank(diploma.getIssuedBy())) ? diploma.getIssuedBy() : "";
				String udostNomer = (udost != null && isNotBlank(udost.getNomDoc())) ? udost.getNomDoc() : "";
				String udostDate = (udost == null || udost.getDateIssued() == null) ? "" : DATE_FORMAT_DD_MM_YYYY.format(udost.getDateIssued());
				String urn = isNotBlank(r.getUrn()) ? r.getUrn() : "";
				
				veterinari.add(String.format(pattern, imena, egnEikLiteral, egnEik, adres, diplomaNom, diplomaDate, diplomaIssuedBy, udostNomer, udostDate, urn));
			}
			return (veterinari.isEmpty()) ? null : String.join(UdostDocumentCreator.LIST_DELIMITER, veterinari);
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * Списък с фармакологичните групи
	 * @return Стринг с групите, разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getFarmakGrupi() {
		if(this.vpisvane.getEventDeinostVlp() == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		// ако имаме dopInfo, слага него; преди всеки ред слагам таб, за да е малко навътре
		if(isNotBlank(this.vpisvane.getEventDeinostVlp().getDopInfo())) {
			for(String line : this.vpisvane.getEventDeinostVlp().getDopInfo().split("\n")) {
				sb.append(ControlChar.TAB).append(line).append(ControlChar.LINE_BREAK);
			}
			return sb.toString();
		}

		// ако няма доп инфо, изкарвам списък с DeinostVlpPredmet
		try {
			for(int i = 0; i < this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().size(); i++) {
				String predmet = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_PREDMET_TARGOVIA_EDRO_VLP,
						this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().get(i).getPredmet(),
						this.userData.getCurrentLang(),
						this.date);
				sb.append(ControlChar.TAB).append(i + 1).append( ". ").append(predmet).append(ControlChar.LINE_BREAK);
			}
			return sb.toString();
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * Връща пълното име на регистратурата, за да се сложи в хедъра на документите.
	 * Тъй като във всеки хедър пише БАБХ, ако регистратурата също е БАБХ, връща null
	 * @return orgName на регистратура на вписването ( ОДБХ Пловдив...) или празно, ако е БАБХ
	 */
	public String getRegistraturaOrgNameOrEmpty() {
		if(this.vpisvane.getRegistraturaId() == BabhConstants.CODE_REGISTRATURA_BABH) {
			return null;
		}
		try {
			Registratura reg = this.helpers.getRegistratura();
			if(reg != null && isNotBlank(reg.getOrgName())) {
				return reg.getOrgName();
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return orgName на регистратурата на кирилица
	 */
	public String getRegistraturaOrgNameCyr() {
		try {
			Registratura reg = this.helpers.getRegistratura();
			if(reg != null) {
				return isNotBlank(reg.getOrgName())
						? reg.getOrgName()
						: null;
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Името на регистратурата на кирилица
	 */
	public String getRegistraturaShortCyr() {
		try {
			Registratura reg = this.helpers.getRegistratura();
			if(reg != null) {
				return isNotBlank(reg.getRegistratura())
						? reg.getRegistratura()
						: null;
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Името на регистратурата на латиница
	 */
	public String getRegistraturaShortLat() {
		try {
			Registratura reg = this.helpers.getRegistratura();
			if(reg != null) {
				return isNotBlank(reg.getRegistraturaLat())
						? reg.getRegistraturaLat()
						: null;
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * Адреса на регистратурата на кирилица
	 * @return orgName, "Република България", postCode, ekatte, adres
	 */
	public String getRegistraturaAdresCyr() {
		try {
			Registratura reg = this.helpers.getRegistratura();
			if(reg != null) {
				String orgName = this.getRegistraturaOrgNameCyr();
				String postCode = reg.getPostCode();
				String ekatte =  
						this.systemData.decodeItem(
							BabhConstants.CODE_CLASSIF_EKATTE,
							reg.getEkatte(),
							UdostDokumentMethods.this.userData.getCurrentLang(),
							UdostDokumentMethods.this.date);
				String adres = reg.getAddress();
				
				StringBuilder sb = new StringBuilder();
				if(isNotBlank(orgName)) sb.append(orgName).append(", ");
				sb.append("Република България, ");
				if(isNotBlank(postCode)) sb.append(postCode).append(", ");
				if(ekatte != null && !ekatte.contains("Ненамерено значение")) sb.append(ekatte).append(", ");
				if(isNotBlank(adres)) sb.append(adres).append(", ");
				
				if(sb.lastIndexOf(", ") == sb.length() - 2) {
					sb.delete(sb.lastIndexOf(", "), sb.length());
				}
				
				return sb.length() == 0 ? null : sb.toString();
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * Адреса на регистратурата на латиница
	 * @return orgName, "Republic of Bulgaria", postCode, ekatte (транслитериран), adres (транслитериран)
	 */
	public String getRegistraturaAdresLat() {
		try {
			Registratura reg = this.helpers.getRegistratura();
			if(reg != null) {

				String orgName = reg.getOrgNameLat();
				String postCode = reg.getPostCode();
				String ekatte =  
						this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_EKATTE,
						reg.getEkatte(),
						BabhConstants.CODE_LANG_EN,
						UdostDokumentMethods.this.date);
				String adres = reg.getAddress();
				
				StringBuilder sb = new StringBuilder();
				if(isNotBlank(orgName)) sb.append(orgName).append(", ");
				sb.append("Republic of Bulgaria, ");
				if(isNotBlank(postCode)) sb.append(postCode).append(", ");
				if(ekatte != null && !ekatte.contains("Ненамерено значение")) sb.append(StringUtils.transliterate(ekatte)).append(", ");
				if(isNotBlank(adres)) sb.append(StringUtils.transliterate(adres)).append(", ");
				
				if(sb.lastIndexOf(", ") == sb.length() - 2) {
					sb.delete(sb.lastIndexOf(", "), sb.length());
				}
				
				return sb.length() == 0 ? null : sb.toString();
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}

	/**
	 * @return Връща стринг с вида дейност с животни: "животни", "зародишни продукти" или "животни/зародишни продукти"
	 */
	public String getVidDeinostSJivotniShort() {
		if(this.vpisvane.getEventDeinJiv() == null) {
			return null;
		}
			
		List<Integer> deinosti = this.vpisvane.getEventDeinJiv().getVidList();
		
		if(deinosti == null || deinosti.size() == 0) {
			return null;
		}
		else if(deinosti.size() == 2) {
			return "животни/зародишни продукти";
		}
		else if(deinosti.get(0) == BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_JIV) {
			return "животни";
		}
		else if(deinosti.get(0) == BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD) {
			return "зародишни продукти";
		}
		else return null;
	}
	
	/**
	 * @return Връща стринг с вида дейност с животни: "животни", "зародишни продукти" или "животни и зародишни продукти - ... и ..."
	 */
	public String getVidDeinostSJivotniDetails() {
		if(this.vpisvane.getEventDeinJiv() == null) return null;
		List<Integer> deinosti = this.vpisvane.getEventDeinJiv().getVidList();
		
		StringBuilder sb = new StringBuilder();
		
		if(!deinosti.isEmpty()) {
			
			if(deinosti.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_JIV)) {
				sb.append("животни");
				if(deinosti.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD)) {
					sb.append(" и ");
				}
			}
			if(deinosti.contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_ZARPROD)) {
				sb.append("зародишни продукти");
				List<String> predmeti = this.vpisvane.getEventDeinJiv().getEventDeinJivPredmet()
					.stream()
					.map(p -> {
						try {
							return this.systemData.decodeItem(
										BabhConstants.CODE_CLASSIF_VID_JIVOTNO,
										p.getPredmet(),
										UdostDokumentMethods.this.userData.getCurrentLang(),
										UdostDokumentMethods.this.date);
						} catch (DbErrorException e) {
							return null;
						}
					})
					.collect(Collectors.toList());
				if(!predmeti.isEmpty()) {
					StringBuilder predmetiString = new StringBuilder(String.join(", ", predmeti));
					if(predmetiString.lastIndexOf(", ") > 0) {
						predmetiString.replace(predmetiString.lastIndexOf(", "), predmetiString.lastIndexOf(", ") + 2, " и ");
					}
					sb.append(" - ").append(predmetiString);
				}
			}
		}
		
		if(sb.length() > 0) {
			if(sb.charAt(0) == 'с' || sb.charAt(0) == 'С' || sb.charAt(0) == 'з' || sb.charAt(0) == 'З') {
				sb.insert(0, "със ");
			}
			else {
				sb.insert(0, "с ");
			}
			
			return sb.toString();
		}
		else return null;
	}
	
	/**
	 * @return Регистранионния номер на заявлението за вписване
	 */
	public String getZaiavlVpisvaneRegNom() {
		if(isNotBlank(this.vpisvane.getRegNomZaqvlVpisvane())) {
			return this.vpisvane.getRegNomZaqvlVpisvane();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Датата на регистранионния номер на заявлението за вписване
	 */
	public String getZaiavlVpisvaneDate() {
		if(this.vpisvane.getDateZaqvlVpis() != null) {
			return DATE_FORMAT_DD_MM_YYYY.format(this.vpisvane.getDateZaqvlVpis());
		}
		else {
			return null;
		}
	}
	
	/**
	 * Списък с видовете дейности с ВЛП
	 * @return Стринг с видовете дейности, разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getDeinostiVlp() {
		try {
			List<String> vidove = new ArrayList<>();
			if(this.vpisvane.getEventDeinostVlp() != null) {
				for(EventDeinostVlpVid vid : this.vpisvane.getEventDeinostVlp().getEventDeinostVlpVid()) {
					vidove.add(
							this.systemData.decodeItem(
								BabhConstants.CODE_CLASSIF_VID_DEINOST,
								vid.getVid(),
								this.userData.getCurrentLang(),
								this.date));
				}
				
				return (vidove.isEmpty()) ? null : String.join(UdostDocumentCreator.LIST_DELIMITER, vidove);
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * Списък с видовете дейности с фуражи
	 * @return Стринг с видовете дейности, разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getDeinostiFurajiList() {
		try {
			List<String> vidove = new ArrayList<>();
			if(this.vpisvane.getEventDeinostFuraji() != null) {
				for(Integer vid : this.vpisvane.getEventDeinostFuraji().getVidList()) {
					vidove.add(
							this.systemData.decodeItem(
								BabhConstants.CODE_CLASSIF_VID_DEINOST,
								vid,
								this.userData.getCurrentLang(),
								this.date));
				}
				return (vidove.isEmpty()) ? null : String.join(UdostDocumentCreator.LIST_DELIMITER, vidove);
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}

	/**
	 * Списък с видовете дейности с фуражи
	 * @return Стринг с видовете дейности, разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getDeinostiFurajiWithComma() {
		try {
			List<String> vidove = new ArrayList<>();
			if(this.vpisvane.getEventDeinostFuraji() != null) {
				for(Integer vid : this.vpisvane.getEventDeinostFuraji().getVidList()) {
					vidove.add(
							this.systemData.decodeItem(
									BabhConstants.CODE_CLASSIF_VID_DEINOST,
									vid,
									this.userData.getCurrentLang(),
									this.date));
				}
				return (vidove.isEmpty()) ? null : this.helpers.joinListWithSeparator(vidove, ", ", true);
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}

	public String getEventDeinostVlpOpisanie() {
		if(this.vpisvane.getEventDeinostVlp() != null) {
			return isNotBlank(this.vpisvane.getEventDeinostVlp().getOpisanie())
					? this.vpisvane.getEventDeinostVlp().getOpisanie()
					: null;
		}
		else return null;
	}
	
	public String getEventDeinostFurajiOpisanie() {
		if(this.vpisvane.getEventDeinostFuraji() != null) {
			return isNotBlank(this.vpisvane.getEventDeinostFuraji().getOpisanie())
					? this.vpisvane.getEventDeinostFuraji().getOpisanie()
					: null;
		}
		else return null;
	}
	
	/**
	 * @return Стринг с болестите от дейността (ВЛП) на бг и англ, разделени със запетайка
	 */
	public String getDeinostVlpBolesti() {
		try {
			List<String> bolesti = new ArrayList<>();
			for(Integer bolezCode : this.vpisvane.getEventDeinostVlp().getBolestList()) {
				SystemClassif bolez = this.systemData.decodeItemLite(
						BabhConstants.CODE_CLASSIF_BOLESTI,
						bolezCode,
						BabhConstants.CODE_LANG_BG,
						new Date(), false);
				
				if(bolez != null) {
					bolesti.add(String.format("%s/%s", bolez.getTekst(), bolez.getDopInfo()));
				}
			}
			return (bolesti.isEmpty()) ? null : this.helpers.joinListWithSeparator(bolesti, ", ", false);
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Наименованието на предмета на дейност (ВЛП) на кирилица - event_deinost_vlp_predmet.naimenovanie
	 */
	public String getDeinostVlpPredmetNaimenovanieCyr() {
		if(!this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().isEmpty()) {
			if(isNotBlank(this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().get(0).getNaimenovanie())) {
				return this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().get(0).getNaimenovanie();
			}
			else {
				return null;
			}
		}
		else return null;
	}
	
	/**
	 * @return Наименованието на предмета на дейност (ВЛП) на латиница - event_deinost_vlp_predmet.naimenovanie_lat
	 */
	public String getDeinostVlpPredmetNaimenovanieLat() {
		if(!this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().isEmpty()) {
			if(isNotBlank(this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().get(0).getNaimenovanieLat())) {
				return this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().get(0).getNaimenovanieLat();
			}
			else {
				return null;
			}
		}
		else return null;
	}
	
	/**
	 * @return Срока на предмета на дейност (ВЛП) - event_deinost_vlp_predmet.srok
	 */
	public String getDeinostVlpPredmetSrok() {
		if(!this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().isEmpty()) {
			if(isNotBlank(this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().get(0).getSrok())) {
				return this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet().get(0).getSrok();
			}
			else {
				return null;
			}
		}
		else return null;
	}
	
	/**
	 * @return Името на лицето "производител" от дейността (ВЛП) 
	 */
	public String getEventDeinostVlpProizvoditelImeCyr() {
		EventDeinostVlpLice lice = this.helpers.getEventDeinostVlpLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_PROIZVODITEL);
		
		if(lice != null && isNotBlank(lice.getReferent().getRefName())) {
			return lice.getReferent().getRefName();
		}
		else {
			return null;
		}
	}

	public String getEventDeinostVlpProizvoditelImeLat() {
		EventDeinostVlpLice lice = this.helpers.getEventDeinostVlpLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_PROIZVODITEL);

		if(lice != null && isNotBlank(lice.getReferent().getRefLatin())) {
			return lice.getReferent().getRefLatin();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Адреса на лицето "производител" от дейността (ВЛП) 
	 */
	public String getEventDeinostVlpProizvoditelAdresCyr() {
		EventDeinostVlpLice lice = this.helpers.getEventDeinostVlpLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_PROIZVODITEL);
		
		if(lice != null) {
			try {
				String[] referentAddress = this.helpers.getAddressFieldsByReferent(lice.getReferent(), false);
				return this.helpers.getAdresStringFromReferentAdresFields(referentAddress);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else {
			return null;
		}
	}

	public String getEventDeinostVlpProizvoditelAdresLat() {
		EventDeinostVlpLice lice = this.helpers.getEventDeinostVlpLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_PROIZVODITEL);

		if(lice != null) {
			try {
				String[] referentAddress = this.helpers.getAddressFieldsByReferent(lice.getReferent(), true);
				return this.helpers.getAdresStringFromReferentAdresFields(referentAddress);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Името на лицето "Отговорно лице по чл. 35 от ЗКНВП" (14) от дейността (ВЛП)
	 */
	public String getEventDeinostVlpOtgLiceChlen35() {
		EventDeinostVlpLice lice = this.helpers.getEventDeinostVlpLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_OTG_LICE);
		if(lice != null && lice.getReferent() != null && isNotBlank(lice.getReferent().getRefName())) {
			return lice.getReferent().getRefName();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Лицето за ObektDeinost ВЛП с роля "Производител, отговорен за освобождаване на партидата" (20)
	 */
	public String getObektDeinostVlpProizvoditelIme() {

		VlpLice lice = this.vpisvane.getVlp().getVlpLice()
			.stream()
			.filter(l -> l.getDateEnd() == null && l.getTip() == BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_PROIZVODITEL_OTG_RELEASE)
			.findFirst()
			.orElse(null);
		
		
		if(lice != null && lice.getReferent() != null && isNotBlank(lice.getReferent().getRefName())) {
			return lice.getReferent().getRefName();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Адреса на лицето "Производител, отговорен за освобождаване на партидата" от ObektDeinost (ВЛП)
	 */
	public String getObektDeinostVlpProizvoditelAdres() {
		VlpLice lice = this.vpisvane.getVlp().getVlpLice()
				.stream()
				.filter(l -> l.getDateEnd() == null && l.getTip() == BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_PROIZVODITEL_OTG_RELEASE)
				.findFirst()
				.orElse(null);
		
		if(lice != null) {
			try {
				String[] referentAddress = this.helpers.getAddressFieldsByReferent(lice.getReferent(), false);
				return this.helpers.getAdresStringFromReferentAdresFields(referentAddress);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Процедурен номер на лекарствения продукт
	 */
	public String getVlpProcNomer() {
		if(this.vpisvane.getVlp() != null && isNotBlank(this.vpisvane.getVlp().getNaimenovanieCyr())) {
			return this.vpisvane.getVlp().getProcNumber();
		}
		else return null;
	}
	
	/**
	 * @return наименование на лекарствения продукт на кирилица
	 */
	public String getVlpNaimenovanieCyr() {
		if(this.vpisvane.getVlp() != null && isNotBlank(this.vpisvane.getVlp().getNaimenovanieCyr())) {
			return this.vpisvane.getVlp().getNaimenovanieCyr();
		}
		else return null;
	}
	
	/**
	 * @return наименование на лекарствения продукт на латиница
	 */
	public String getVlpNaimenovanieLat() {
		if(this.vpisvane.getVlp() != null && isNotBlank(this.vpisvane.getVlp().getNaimenovanieLat())) {
			return this.vpisvane.getVlp().getNaimenovanieLat();
		}
		else return null;
	}
	
	/**
	 * @return стринг с активни вещества от ВЛП на бг и англ, разделени със запетайка
	 */
	public String getAktivnoVeshtestvo() {
		List<String> aktSubstancii = null;
		if(this.vpisvane.getVlp() != null) {
			aktSubstancii = this.vpisvane.getVlp().getVlpVeshtva()
				.stream()
				.filter(v -> v.getType().equals(BabhConstants.CODE_ZNACHENIE_VID_VESHTESTVO_AKTIVNO) && v.getVidIdentifier() != null)
				.sorted((v1, v2) -> v1.getId().compareTo(v2.getId()))
				.map(v -> v.getVidIdentifier().getName().trim())
				.collect(Collectors.toList());
		}
		
		return (aktSubstancii == null || aktSubstancii.isEmpty())
				? null
				: this.helpers.joinListWithSeparator(aktSubstancii, "; ", false);
	}
	
	/**
	 * @return стринг с "quantity measurement" на веществата на ВЛП, заразделени с ";" 
	 */
	public String getVeshtestvaKoncentracia() {
		List<String> koncentracii = null;
		if(this.vpisvane.getVlp() != null) {
			koncentracii = this.vpisvane.getVlp().getVlpVeshtva()
				.stream()
				.filter(v -> v.getType().equals(BabhConstants.CODE_ZNACHENIE_VID_VESHTESTVO_AKTIVNO) && v.getVidIdentifier() != null)
				.sorted((v1, v2) -> v1.getId().compareTo(v2.getId()))
				.map(v -> v.getQuantity() + " " + v.getMeasurement())
				.collect(Collectors.toList());
		}
		
		return (koncentracii == null || koncentracii.isEmpty())
				? null
				: this.helpers.joinListWithSeparator(koncentracii, "; ", false);
	}
	
	/**
	 * @return фармацевтичната форма на ВЛП
	 */
	public String getFarmacevtForma() {
		try {
			if(this.vpisvane.getVlp() != null && this.vpisvane.getVlp().getFarmFormList() != null) {
				
				List<String> farmFormi = new ArrayList<>();
				for(Integer farmFormaCode : this.vpisvane.getVlp().getFarmFormList()) {
					farmFormi.add(
							this.systemData.decodeItem(
								BabhConstants.CODE_CLASSIF_PHARM_FORMI,
								farmFormaCode,
								this.userData.getCurrentLang(),
								this.date));
				}
				return (farmFormi.isEmpty()) ? null : this.helpers.joinListWithSeparator(farmFormi, ", ", false);
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return режима на отпускане на ВЛП
	 */
	public String getRejimPredpisvane() {
		if(this.vpisvane.getVlp() == null || this.vpisvane.getVlp().getRejimOtpuskane() == null) {
			return null;
		}

		try {
			SystemClassif rejim = this.systemData.decodeItemLite(
					BabhConstants.CODE_CLASSIF_NACHIN_OTPUSK_VLP,
					this.vpisvane.getVlp().getRejimOtpuskane(),
					this.userData.getCurrentLang(),
					new Date(), false);
			if(rejim != null) {
				int codeToDecode = (rejim.getCodeParent() == 0)
						? rejim.getCode()
						: rejim.getCodeParent();

				return this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_NACHIN_OTPUSK_VLP,
						codeToDecode,
						this.userData.getCurrentLang(),
						this.date);
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Връща стринг "произвежда", ако в дейностите с животни има {@link BabhConstants#CODE_ZNACHENIE_DEIN_ZJ_PROIZV_IDENT},
	 * или стринг "произвежда и търгува", ако в дейностите има И {@link BabhConstants#CODE_ZNACHENIE_DEIN_ZJ_TARG_IDENT}
	 */
	public String getProizvejdaTarguvaLiteral() {
		
		if(this.vpisvane.getEventDeinJiv().getVidList().contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PROIZV_IDENT)
				&& this.vpisvane.getEventDeinJiv().getVidList().contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_TARG_IDENT)) {
			return "произвежда и търгува със";
		}
		else if(this.vpisvane.getEventDeinJiv().getVidList().contains(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PROIZV_IDENT)) {
			return "произвежда";
		}
		else return null;
	}
	
	/**
	 * @return Списък с видовете животни в дейността Животни, разделени със запетайка
	 */
	public String getVidoveJivotni() {
		if(this.vpisvane.getEventDeinJiv() != null && this.vpisvane.getEventDeinJiv().getEventDeinJivIdentif() != null) {
			List<String> vidoveJivotni = new ArrayList<>();
			this.vpisvane.getEventDeinJiv().getEventDeinJivIdentif()
				.forEach(j -> {
					try {
						vidoveJivotni.add(
							this.systemData.decodeItem(
								BabhConstants.CODE_CLASSIF_VID_JIVOTNO,
								j.getVidJiv(),
								this.userData.getCurrentLang(),
								this.date));
					}
					catch(DbErrorException e) {
						LOGGER.error(DB_ERROR_MSG, e);
					}
				});
			
			return (vidoveJivotni.isEmpty()) ? null : this.helpers.joinListWithSeparator(vidoveJivotni, ", ", true);
		}
		else return null;
	}
	
	public String getEventDeinJivotniPredmet() {
		if(this.vpisvane.getEventDeinJiv() != null && this.vpisvane.getEventDeinJiv().getEventDeinJivPredmet() != null) {
			List<String> vidoveJivotni = new ArrayList<>();
			this.vpisvane.getEventDeinJiv().getEventDeinJivPredmet()
				.forEach(p -> {
					try {
						vidoveJivotni.add(
							this.systemData.decodeItem(
								BabhConstants.CODE_CLASSIF_VID_JIVOTNO,
								p.getPredmet(),
								this.userData.getCurrentLang(),
								this.date));
					}
					catch(DbErrorException e) {
						LOGGER.error(DB_ERROR_MSG, e);
					}
				});
			
			return (vidoveJivotni.isEmpty()) ? null : this.helpers.joinListWithSeparator(vidoveJivotni, ", ", false);
		}
		else return null;
	}
	
	public String getJivotniNachinTransport() {
		if(this.vpisvane.getEventDeinJiv() != null) {
			try {
				return this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_NACHIN_TRANSP,
					this.vpisvane.getEventDeinJiv().getNachinTransp(),
					this.userData.getCurrentLang(),
					this.date);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else return null;
	}
	
	/**
	 * @return Полето iznos на дейността с Животни
	 */
	public String getJivotniIznos() {
		if(this.vpisvane.getEventDeinJiv() != null) {
			return (isNotBlank(this.vpisvane.getEventDeinJiv().getIznos())) 
					? this.vpisvane.getEventDeinJiv().getIznos()
					: null;
		}
		else return null;
	}
	
	/**
	 * @return Списък с номерата на обекта на дейност, разделени със запетайка
	 */
	public String getObektDeinostRegNomera() {
		List<String> nomera = new ArrayList<String>();
		ObektDeinost obektDein = this.helpers.getObektDeinost();
		
		if(obektDein != null) {
			if(obektDein.getVid() == BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ) {
				if(obektDein.getRegNom() != null) {
					nomera.add("рег. номер по регламент " + obektDein.getRegNom());
				}
				if(obektDein.getRegNomerStar() != null) {
					nomera.add("рег. номер (стар) " + obektDein.getRegNomerStar());
				}
			}
			else {
				if(obektDein.getRegNom() != null) {
					nomera.add("рег. номер " + obektDein.getRegNom());
				}
			}
		}
		
		return nomera.isEmpty() ? null : this.helpers.joinListWithSeparator(nomera, ", ", false);
	}
		
	/**
	 * @return Списък с номерата на всичките обекти на дейност и имената на обектите, разделени с ;
	 */
	public String getObektiDeinostRegNomeraINaimenovania() {
		List<String> nomera = new ArrayList<String>();
		List<ObektDeinost> obektiDein = this.helpers.getObektiDeinost();
		
		for(ObektDeinost obektDein : obektiDein) {
			if(obektDein != null) {
				List<String> currentObektNomera = new ArrayList<>(2);
				if(obektDein.getVid() == BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ) {
					if(obektDein.getRegNom() != null) {
						currentObektNomera.add("рег. номер по регламент " + obektDein.getRegNom());
					}
					if(obektDein.getRegNomerStar() != null) {
						currentObektNomera.add("рег. номер (стар) " + obektDein.getRegNomerStar());
					}
				}
				else {
					if(obektDein.getRegNom() != null) {
						currentObektNomera.add("рег. номер " + obektDein.getRegNom());
					}
				}
				if(obektDein.getNaimenovanie() != null && isNotBlank(obektDein.getNaimenovanie())) {
					currentObektNomera.add(obektDein.getNaimenovanie());
				}
				
				if(!currentObektNomera.isEmpty()) {
					nomera.add(this.helpers.joinListWithSeparator(currentObektNomera, ", ", false));
				}
			}
		}
		
		return nomera.isEmpty() ? null : this.helpers.joinListWithSeparator(nomera, "; ", false);
	}
	
	/**
	 * @return Рег. номер / УРН на обекта на дейност
	 */
	public String getObektDeinostRegNom() {
		ObektDeinost obektDein = this.helpers.getObektDeinost();
		
		if(obektDein != null) {
			return (isNotBlank(obektDein.getRegNom())) ? obektDein.getRegNom() : null;
		}
		else return null;
	}
	
	/**
	 * @return Списък с държавите на дейността с Животни, разделени със запетайка. Между последните 2 има "и"
	 */
	public String getEventDeinostJivDarjavi() {
		
		List<String> darjavi = new ArrayList<>();

		this.vpisvane.getEventDeinJiv().getDarjList()
			.forEach(d -> {
				try {
					darjavi.add(
						this.systemData.decodeItem(
							BabhConstants.CODE_CLASSIF_COUNTRIES,
							d,
							this.userData.getCurrentLang(),
							this.date));
				}
				catch(DbErrorException e) {
					LOGGER.error(DB_ERROR_MSG, e);
				}
			});
	
		return darjavi.isEmpty() ? null : this.helpers.joinListWithSeparator(darjavi, ", ", true);
	}
	
	/**
	 * @return Списък с ГКПП на дейността с Животни, разделени със запетайка. Между последните 2 има "и"
	 */
	public String getEventDeinostJivGkpp() {
		
		List<String> gkpp = new ArrayList<>();

		this.vpisvane.getEventDeinJiv().getGkppList()
			.forEach(g -> {
				try {
					gkpp.add(
						this.systemData.decodeItem(
							BabhConstants.CODE_CLASSIF_GKPP,
							g,
							this.userData.getCurrentLang(),
							this.date));
				}
				catch(DbErrorException e) {
					LOGGER.error(DB_ERROR_MSG, e);
				}
			});
	
		return gkpp.isEmpty() ? null : this.helpers.joinListWithSeparator(gkpp, ", ", true);
	}
	
	/**
	 * @return Датата DateValidAkt на документа
	 */
	public String getDocDateValidAkt() {
		if(this.doc.getDateValidAkt() != null) {
			return DATE_FORMAT_DD_MM_YYYY.format(this.doc.getDateValidAkt());
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Описанието на дейността с Животни на кирилица
	 */
	public String getEventDeinostJivOpisanieCyr() {
		if(isNotBlank(this.vpisvane.getEventDeinJiv().getOpisanieCyr())) {
			return this.vpisvane.getEventDeinJiv().getOpisanieCyr();
		}
		else return null;
	}
	
	/**
	 * @return Името на лицето "ръководител на екип" от EventDeinostJiv
	 */
	public String getEventDeinostJivLiceRakovoditel() {
		List<EventDeinJivLice> lica = this.helpers.getEventDeinostJivLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_RAKOVODITEL_EKIP);
		if(lica.size() == 0) {
			return null;
		}
		
		EventDeinJivLice lice = lica.get(0);
		
		if(lice != null && lice.getReferent() != null && isNotBlank(lice.getReferent().getRefName())) {
			return lice.getReferent().getRefName();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Стринг "ЕГН" или "ЕИК" в зависимост дали лицето ръководител е ФЗЛ/НФЛ
	 */
	public String getRakovoditelEgnOrEikLiteral() {
		try {
			List<EventDeinJivLice> lica = this.helpers.getEventDeinostJivLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_RAKOVODITEL_EKIP);
			if(lica.size() == 0) {
				return null;
			}
			
			EventDeinJivLice lice = lica.get(0);

			Referent r = this.helpers.getReferentById(lice.getCodeRef());
			return this.helpers.getEgnEikByReferent(r)[0];
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return ЕГН или ЕИК на лицето ръководител в зависимост дали е ФЗЛ или НФЛ
	 */
	public String getRakovoditelEgnOrEik() {
		try {
			List<EventDeinJivLice> lica = this.helpers.getEventDeinostJivLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_RAKOVODITEL_EKIP);
			if(lica.size() == 0) {
				return null;
			}
			
			EventDeinJivLice lice = lica.get(0);
			Referent r = this.helpers.getReferentById(lice.getCodeRef());
			return this.helpers.getEgnEikByReferent(r)[1];
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Името на контролиращия в дейността с животни
	 */
	public String getEventDeinostJivLiceKontrolirasht() {
		List<EventDeinJivLice> lica = this.helpers.getEventDeinostJivLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_CTRL_LICE);
		if(lica.size() == 0) {
			return null;
		}
		
		EventDeinJivLice lice = lica.get(0);
		
		if(lice != null && lice.getReferent() != null && isNotBlank(lice.getReferent().getRefName())) {
			return lice.getReferent().getRefName();
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Имената на лицата "следящ за... хуманно отношение към животните" 
	 * от EventDeinostJiv, разделени със запетайка, ако са повече от едно.
	 */
	public String getEventDeinostJivLiceSlediashtHumannotoOtnoshenie() {
		List<String> lica = this.vpisvane.getEventDeinJiv().getEventDeinJivLice()
			.stream()
			.filter(l -> l.getTipVraz() == BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_HUMANNO_OTN && l.getDateEnd() == null)
			.filter(l -> isNotBlank(l.getReferent().getRefName()))
			.map(l -> l.getReferent().getRefName())
			.collect(Collectors.toList());
		
		return lica.isEmpty() ? null : helpers.joinListWithSeparator(lica, ", ", true);
	}
	
	/**
	 * @return GPS LATitude полето на обекта на дейност
	 */
	public String getObektGpsLat() {
		if(this.vpisvane.getOezReg() != null) {
			return (this.vpisvane.getOezReg().getGpsLat() == null)
					? null
					: String.valueOf(this.vpisvane.getOezReg().getGpsLat());
		}
		else return null;
	}
	
	/**
	 * @return GPS LONgtitude полето на обекта на дейност
	 */
	public String getObektGpsLon() {
		if(this.vpisvane.getOezReg() != null) {
			return (this.vpisvane.getOezReg().getGpsLon() == null)
					? null
					: String.valueOf(this.vpisvane.getOezReg().getGpsLon());
		}
		else return null;
	}
	
	/**`
	 * @return Вида на ОЕЗ от обект на дейност (obekt_deinost.vid_oez)
	 */
	public String getObektDeinostVidOez() {
		if(vpisvane.getOezReg() != null) {
			try {
				return this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VID_OEZ,
						vpisvane.getOezReg().getVidOez(), // vpisvane.getObektDeinost().getVidOez() - da ama ne! TODO za6to
						this.userData.getCurrentLang(),
						this.date);
			}
			catch (DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else return null;
	}
	
	/**
	 * @return ВЛЗ от обекта на дейност
	 */
	public String getVidVlz() {
		ObektDeinost obekt = this.helpers.getObektDeinost();
		if(obekt != null && obekt.getVidVlz() != null) {
			try {
				return this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VID_VLZ,
						obekt.getVidVlz(),
						this.userData.getCurrentLang(),
						this.date);
			}
			catch (DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else return null;
	}
	
	public String getSobstvenikIme() {
		ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_SOBSTVENIK);
		if(lice != null && lice.getReferent() != null) {
			return (isNotBlank(lice.getReferent().getRefName())) ? lice.getReferent().getRefName() : null;
		}
		else return null;
	}
	
	/**
	 * @return Стринг "ЕГН" или "ЕИК" в зависимост дали лицето собственик е ФЗЛ/НФЛ
	 */
	public String getSobstvenikEgnOrEikLiteral() {
		try {	
			ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_SOBSTVENIK);
			if(lice != null) {
				Referent r = this.helpers.getReferentById(lice.getCodeRef());
				return this.helpers.getEgnEikByReferent(r)[0];
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return ЕГН или ЕИК на лицето собственик в зависимост дали е ФЗЛ/НФЛ
	 */
	public String getSobstvenikEgnOrEik() {
		try {	
			ObektDeinostLica lice = this.helpers.getObektDeinostLiceByRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_SOBSTVENIK);
			if(lice != null) {
				Referent r = this.helpers.getReferentById(lice.getCodeRef());
				return this.helpers.getEgnEikByReferent(r)[1];
			}
			else return null;
		}
		catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getObektDeinostAdres() {
		try {
			ObektDeinost obektDeinost = this.helpers.getObektDeinost();
			if(obektDeinost == null) return null;
			
			String country = this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_COUNTRIES,
					obektDeinost.getDarj(),
					this.userData.getCurrentLang(),
					this.date);
			String obl = this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_EKATTE,
					obektDeinost.getObl(),
					this.userData.getCurrentLang(),
					this.date);
			String obsht = this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_EKATTE,
					obektDeinost.getObsht(),
					this.userData.getCurrentLang(),
					this.date);
			String ekatte = this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_EKATTE,
					obektDeinost.getNasMesto(),
					this.userData.getCurrentLang(),
					this.date);
			String adres = obektDeinost.getAddress();
			String postCode = (obektDeinost.getPostCode() == null) ? null : String.valueOf(obektDeinost.getPostCode());
			
			StringBuilder sb = new StringBuilder();
			if(country != null && !country.contains("Ненамерено значение")) sb.append(country).append(", ");
			if(obl != null && !obl.contains("Ненамерено значение")) sb.append(obl).append(", ");
			if(obsht != null && !obsht.contains("Ненамерено значение")) sb.append(obsht).append(", ");
			if(ekatte != null && !ekatte.contains("Ненамерено значение")) sb.append(ekatte).append(", ");
			if(adres != null) sb.append(adres).append(", ");
			if(postCode != null) sb.append(postCode);
			
			if(sb.lastIndexOf(", ") == sb.length() - 2) {
				sb.delete(sb.lastIndexOf(", "), sb.length());
			}
			
			return sb.length() == 0 ? null : sb.toString();
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getObektiDeinostAdresi() {
		List<ObektDeinost> obekti = this.helpers.getObektiDeinost();
		List<String> adresi = new ArrayList<>();
		
		if(obekti != null && !obekti.isEmpty() ) {
			for(ObektDeinost obekt : obekti) {
				if(obekt != null) {
					try {
						String[] adresFields = this.helpers.getAddressFieldsByObektDeinost(obekt, false);
						String adres = this.helpers.getAdresStringFromObektAdresFields(adresFields);
						if(isNotBlank(adres)) {
							adresi.add(adres);
						}
					}
					catch(DbErrorException e) {
						LOGGER.error(DB_ERROR_MSG, e);
						return null;
					}
				}
			}
			if(!adresi.isEmpty() ) {
				return String.join(UdostDocumentCreator.LIST_DELIMITER, adresi);
			}
			else return null;
		}
		else return null;
		
	}
	
	public String getObektDeinostObslujvaniJiv() {
		ObektDeinost obektDeinost = this.helpers.getObektDeinost();
		
		if(obektDeinost != null) {
			try {
				return this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_OBSL_JIV_VLZ,
					obektDeinost.getObslujvaniJiv(),
					this.userData.getCurrentLang(),
					this.date);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else return null;
	}
	
	public String getObektDeinostObrazDiagn() {
		ObektDeinost obektDeinost = this.helpers.getObektDeinost();
		
		if(obektDeinost != null) {
			if(obektDeinost.getSektorObrDiag() == null) {
				return null;
			}
			else if(obektDeinost.getSektorObrDiag().equals(BabhConstants.CODE_ZNACHENIE_DA)) {
				return "със сектор за образна диагностика";
			}
			else if(obektDeinost.getSektorObrDiag().equals(BabhConstants.CODE_ZNACHENIE_NE)) {
				return "без сектор за образна диагностика";
			}
			else return null;
		}
		else return null;
	}
	
	public String getObektDeinostImaNemaStacionar() {
		ObektDeinost obektDeinost = this.helpers.getObektDeinost();
		
		if(obektDeinost != null) {
			if(obektDeinost.getStacionar() == null) {
				return null;
			}
			else if(obektDeinost.getStacionar().equals(BabhConstants.CODE_ZNACHENIE_DA)) {
				return "има";
			}
			else if(obektDeinost.getStacionar().equals(BabhConstants.CODE_ZNACHENIE_NE)) {
				return "няма";
			}
			else return null;
		}
		else return null;
	}
	
	public String getObektDeinostVidIzsledvania() {
		ObektDeinost obektDeinost = this.helpers.getObektDeinost();
		
		if(obektDeinost != null) {
			return isNotBlank(obektDeinost.getVidIzsl()) ? obektDeinost.getVidIzsl() : null;
		}
		else return null;
	}	
	
	/**
	 * Създава данните за попълване на таблицата "Характеристики на обекта" в документ 211
	 * @return данните за всеки ред са разделени помежду си с {@link UdostDocumentCreator#TABLE_ROW_DELIMITER},
	 * а във всеки ред отделните полета са разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getVidJivotnoTableData() {
		List<String> tableData = new ArrayList<>();
		
		try {
			for(OezHarakt harakt : this.vpisvane.getOezReg().getOezHarakt()) {
				List<String> currentHaraktList = new ArrayList<>();
				
				String vidJivotno = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VID_JIVOTNO,
						harakt.getVidJivotno(),
						this.userData.getCurrentLang(),
						this.date);
				
				String prednaznachenie = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_PREDNAZNACHENIE_JIV,
						harakt.getPrednaznachenie(),
						this.userData.getCurrentLang(),
						this.date);
				
				String kapacitet = (harakt.getKapacitet() == null) ? null : String.valueOf(harakt.getKapacitet());
				String kapacitetText = (harakt.getKapacitetText() == null) ? null : String.valueOf(harakt.getKapacitetText());
				
				String tehnologia = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_TEHNOLOGIA_OTGLEJDANE,
						harakt.getTehnologia(),
						this.userData.getCurrentLang(),
						this.date);
				
				currentHaraktList.add(isNotBlank(vidJivotno) ? vidJivotno : "");
				currentHaraktList.add(isNotBlank(prednaznachenie) ? prednaznachenie : "");
				currentHaraktList.add(isNotBlank(kapacitet) ? kapacitet : "");
				currentHaraktList.add(isNotBlank(kapacitetText) ? kapacitetText : "");
				currentHaraktList.add(isNotBlank(tehnologia) ? tehnologia : "");
				
				tableData.add(String.join(UdostDocumentCreator.LIST_DELIMITER, currentHaraktList));
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
		
		return tableData.isEmpty() ? null : String.join(UdostDocumentCreator.TABLE_ROW_DELIMITER, tableData);
	}
	
	/**
	 * Проверява дали лицето от обекта на дейност е собственик или ползвател
	 * @return "собственик" или "оператор" 
	 */
	public String getSobstvenikIliOperatorLiteral() {
		
		ObektDeinostLica lice = this.helpers.getSobstvenikIliOperator();
		
		if(lice != null) {
			try {
				return this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_VRAZ_LICE_OBEKT,
					lice.getRole(),
					this.userData.getCurrentLang(),
					this.date);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else return null;
	}
	
	/**
	 * @return "с"/"със" в зависимост от резултата от метода {@link #getSobstvenikIliOperatorLiteral}
	 */
	public String getSIliSus1() {
		String s = this.getSobstvenikIliOperatorLiteral();
		
		if(s != null) {
			return (s.toUpperCase().startsWith("С") || s.toUpperCase().startsWith("З")) ? "със" : "с";
		}
		else return "с";
	}
	
	public String getSobstvIliPolzvEgnEikLiteral() {
		ObektDeinostLica lice = this.helpers.getSobstvenikIliOperator();
		if(lice != null) {
			return this.helpers.getEgnEikByReferent(lice.getReferent())[0];
		}
		else return null;
	}
	
	public String getSobstvIliPolzvEgnEikValue() {
		ObektDeinostLica lice = this.helpers.getSobstvenikIliOperator();
		if(lice != null) {
			return this.helpers.getEgnEikByReferent(lice.getReferent())[1];
		}
		else return null;
	}
	
	public String getFurajiKategoria() {
		if(this.vpisvane.getEventDeinostFuraji() == null || this.vpisvane.getEventDeinostFuraji().getKategoriaList() == null) {
			return null;
		}

		List<String> kategorii = new ArrayList<>();
		this.vpisvane.getEventDeinostFuraji().getKategoriaList()
			.forEach(j -> {
				try {
					kategorii.add(
						this.systemData.decodeItem(
							BabhConstants.CODE_CLASSIF_CATEGORY_SJP,
							j,
							this.userData.getCurrentLang(),
							this.date));
				}
				catch(DbErrorException e) {
					LOGGER.error(DB_ERROR_MSG, e);
				}
			});
		
		return (kategorii.isEmpty()) ? null : this.helpers.joinListWithSeparator(kategorii, ", ", false);
	}
	
	public String getObektDeinostKapacitet() {
		if(this.helpers.getObektDeinost() != null) {
			return(isNotBlank(this.helpers.getObektDeinost().getKapacitet()))
					? this.helpers.getObektDeinost().getKapacitet()
					: null;
		}
		else return null;
	}

	public String getSobstvIliPolzvImeNaimenovanie() {
		ObektDeinostLica lice = this.helpers.getSobstvenikIliOperator();
		if(lice != null) {
			return lice.getReferent().getRefName();
		}
		else return null;
	}
	
	public String getSobstvIliPolzvAdres() {
		ObektDeinostLica lice = this.helpers.getSobstvenikIliOperator();
		if(lice != null) {
			try {
				String[] addressFields = this.helpers.getAddressFieldsByReferent(lice.getReferent(), false);
				return this.helpers.getAdresStringFromReferentAdresFields(addressFields);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else return null;
	}
	
	public String getObektDeinostNomerParcel() {
		if(this.vpisvane.getOezReg() != null) {
			return(isNotBlank(this.vpisvane.getOezReg().getNumParcel()))
					? this.vpisvane.getOezReg().getNumParcel()
					: null;
			}
		else return null;
	}
	
	public String getObektDeinostZemlishte() {
		if(this.vpisvane.getOezReg() != null) {
			return(isNotBlank(this.vpisvane.getOezReg().getZemliste()))
					? this.vpisvane.getOezReg().getZemliste()
					: null;
			}
		else return null;
	}
	
	public String getObektDeinostNomerUpi() {
		if(this.vpisvane.getOezReg() != null) {
			return(isNotBlank(this.vpisvane.getOezReg().getUpi()))
					? this.vpisvane.getOezReg().getUpi()
					: null;
			}
		else return null;
	}
	
	/**
	 * @return Номера на протокол от последния статус на вписването
	 */
	public String getVpisvaneStatusNomProtokol() {

		VpisvaneStatus posledenStatus = this.helpers.getVpisvanePosledenStatus();
		
		if(posledenStatus != null) {
			return isNotBlank(posledenStatus.getRegNomProtokol())
					? posledenStatus.getRegNomProtokol()
					: null;
		}
		else return null;
	}
	
	/**
	 * @return Датата на протокол от последния статус на вписването
	 */
	public String getVpisvaneStatusDateProtokol() {

		VpisvaneStatus posledenStatus = this.helpers.getVpisvanePosledenStatus();
		
		if(posledenStatus != null && posledenStatus.getDateProtokol() != null) {
			return DATE_FORMAT_DD_MM_YYYY.format(posledenStatus.getDateProtokol());
		}
		else return null;
	}
	
	/**
	 * @return Номера на заповед от последния статус на вписването
	 */
	public String getVpisvaneStatusNomZapoved() {

		VpisvaneStatus posledenStatus = this.helpers.getVpisvanePosledenStatus();
		
		if(posledenStatus != null) {
			return isNotBlank(posledenStatus.getRegNomZapoved())
					? posledenStatus.getRegNomZapoved()
					: null;
		}
		else return null;
	}
	
	/**
	 * @return Датата на заповед от последния статус на вписването
	 */
	public String getVpisvaneStatusDateZapoved() {

		VpisvaneStatus posledenStatus = this.helpers.getVpisvanePosledenStatus();
		
		if(posledenStatus != null && posledenStatus.getDateZapoved() != null) {
			return DATE_FORMAT_DD_MM_YYYY.format(posledenStatus.getDateZapoved());
		}
		else return null;
	}
	
	/**
	 * @return Рег. номер от обекта на дейност
	 */
	public String getRegNomerOezReg() {
		if(this.vpisvane.getOezReg() != null) {
			return isNotBlank(this.vpisvane.getOezReg().getRegNom())
					? this.vpisvane.getOezReg().getRegNom()
					: null;
			}
		else return null;
	}
	
	/**
	 * @return Стария рег. номер от ОЕЗ
	 */
	public String getRegNomerStarOezReg() {
		if(this.vpisvane.getOezReg() != null) {
			return isNotBlank(this.vpisvane.getOezReg().getRegNomerStar())
					? this.vpisvane.getOezReg().getRegNomerStar()
					: null;
		}
		else return null;
	}
	
	/**
	 * @return Стария рег. номер от обекта на дейност
	 */
	public String getObektDeinostRegNomerStar() {
		if(this.helpers.getObektDeinost() != null) {
			return isNotBlank(this.helpers.getObektDeinost().getRegNomerStar())
					? this.helpers.getObektDeinost().getRegNomerStar()
					: null;
		}
		else return null;
	}
	
	/**
	 * @return Целта от събитие с животни
	 */
	public String getEventDeinostJivCel() {
		if(this.vpisvane.getEventDeinJiv() != null) {
			return isNotBlank(this.vpisvane.getEventDeinJiv().getCel())
					? this.vpisvane.getEventDeinJiv().getCel()
					: null;
			}
		else return null;
	}
	
	/**
	 * @return Целта от събитие с фуражи
	 */
	public String getEventDeinostFurajiCel() {
		if(this.vpisvane.getEventDeinostFuraji() != null) {
			List<String> celi = new ArrayList<>();

			this.vpisvane.getEventDeinostFuraji().getCelList()
					.forEach(c -> {
						try {
							celi.add(
									this.systemData.decodeItem(
											BabhConstants.CODE_CLASSIF_CEL_SGP,
											c,
											this.userData.getCurrentLang(),
											this.date));
						}
						catch(DbErrorException e) {
							LOGGER.error(DB_ERROR_MSG, e);
						}
					});

			return celi.isEmpty() ? null : this.helpers.joinListWithSeparator(celi, ", ", true);
		}
		else return null;
	}
	
	/**
	 * Създава данните за попълване на таблицата в документ 242
	 * @return данните за всеки ред са разделени помежду си с {@link UdostDocumentCreator#TABLE_ROW_DELIMITER},
	 * а във всеки ред отделните полета са разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getVidBroiJivotnoTableData() {
		List<String> tableData = new ArrayList<>();
		
		try {
			for(EventDeinJivPredmet predmet : this.vpisvane.getEventDeinJiv().getEventDeinJivPredmet()) {
				List<String> currentPredmetList = new ArrayList<>();
				
				String vidJivotno = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VID_JIV_OPIT,
						predmet.getPredmet(),
						this.userData.getCurrentLang(),
						this.date);
				
				Integer broi = (predmet.getBroi() == null) ? 0 : predmet.getBroi();
				
				currentPredmetList.add(isNotBlank(vidJivotno) ? vidJivotno : "");
				currentPredmetList.add(String.valueOf(broi));
				
				tableData.add(String.join(UdostDocumentCreator.LIST_DELIMITER, currentPredmetList));
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
		
		return tableData.isEmpty() ? null : String.join(UdostDocumentCreator.TABLE_ROW_DELIMITER, tableData);
	}
	
	
	public String getEventDeinJivLiceKachestvo() {
		if(this.vpisvane.getEventDeinJiv() != null) {
			try {
				return this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_OTN_LICE_PREVOZ,
					this.vpisvane.getEventDeinJiv().getLiceKachestvo(),
					this.userData.getCurrentLang(),
					this.date);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		else return null;
	}
	
	/**
	 * @return Фамилията на референта на вписването на кирилица и латиница, разделени с /
	 */
	public String getReferentFamiliaCyrLat() {
		try {
			if(this.helpers.getReferentFamiliaCyr() == null) {
				return this.helpers.getReferentFamiliaLat();
			}
			else if(this.helpers.getReferentFamiliaLat() == null) {
				return this.helpers.getReferentFamiliaCyr();
			}
			else {
				return String.format("%s/%s", this.helpers.getReferentFamiliaCyr(), this.helpers.getReferentFamiliaLat());
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Име и презиме на референта на вписването на кирилица и латиница, разделени с /
	 */
	public String getReferent2NamesCyrLat() {
		try {
			if(this.helpers.getReferent2NamesCyr() == null) {
				return this.helpers.getReferent2NamesLat();
			}
			else if(this.helpers.getReferent2NamesLat() == null) {
				return this.helpers.getReferent2NamesCyr();
			}
			else {
				return String.format("%s/%s", this.helpers.getReferent2NamesCyr(), this.helpers.getReferent2NamesLat());
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Датата на раждане на референта
	 */
	public String getReferentBirthDate() {
		try {
			Referent referent = this.helpers.getReferentFromVpisvane();
			if(referent != null) {
				return (referent.getFzlBirthDate() == null)
						? null
						: DATE_FORMAT_DD_MM_YYYY.format(referent.getFzlBirthDate());
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Място на раждане на референта
	 */
	public String getReferentBirthPlace() {
		try {
			Referent referent = this.helpers.getReferentFromVpisvane();
			if(referent != null) {
				return (isNotBlank(referent.getFzlBirthPlace()))
						? referent.getFzlBirthPlace()
						: null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Националност на раждане на референта
	 */
	public String getReferentNationality() {
		try {
			Referent referent = this.helpers.getReferentFromVpisvane();
			if(referent != null) {
				return (isNotBlank(referent.getNationality()))
						? referent.getNationality()
						: null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Телефон на регистратурата
	 */
	public String getRegistraturaPhone() {
		try {
			Registratura reg = this.helpers.getRegistratura();
			if(reg != null) {
				return isNotBlank(reg.getContactPhone())
						? reg.getContactPhone()
						: null;
			}
			else return null;
		} catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Факс на регистратурата
	 */
	public String getRegistraturaFax() {
		try {
			Registratura reg = this.helpers.getRegistratura();
			if(reg != null) {
				return isNotBlank(reg.getContactFax())
						? reg.getContactFax()
						: null;
			}
			else return null;
		} catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}

	/**
	 * @return Имейл на регистратурата
	 */
	public String getRegistraturaEmail() {
		try {
			Registratura reg = this.helpers.getRegistratura();
			if(reg != null) {
				return isNotBlank(reg.getContactEmail())
						? reg.getContactEmail()
						: null;
			}
			else return null;
		} catch (DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Вида мероприятие от дейността с животни
	 */
	public String getEventDeinJivMeropr() {
		try {
			if(this.vpisvane.getEventDeinJiv() != null) {
				return this.systemData.decodeItem(
					BabhConstants.CODE_CLASSIF_VID_MEROPRIATIE,
					this.vpisvane.getEventDeinJiv().getMeroptiatie(),
					this.userData.getCurrentLang(),
					this.date);
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Началната дата на мероприятието от дейността с животни
	 */
	public String getEventDeinJivMeroprBegin() {
		
		if(this.vpisvane.getEventDeinJiv() != null && this.vpisvane.getEventDeinJiv().getDateBegMeropriatie() != null) {
			return DATE_FORMAT_DD_MM_YYYY.format(this.vpisvane.getEventDeinJiv().getDateBegMeropriatie());
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Крайната дата на мероприятието от дейността с животни
	 */
	public String getEventDeinJivMeroprEnd() {
		if(this.vpisvane.getEventDeinJiv() != null && this.vpisvane.getEventDeinJiv().getDateEndMeropriatie() != null) {
			return DATE_FORMAT_DD_MM_YYYY.format(this.vpisvane.getEventDeinJiv().getDateEndMeropriatie());
		}
		else {
			return null;
		}
	}
	
	/**
	 * @return Адреса от дейността с животни
	 */
	public String getEventDeinJivAdres() {
		return (isNotBlank(this.vpisvane.getEventDeinJiv().getAdres()))
				? this.vpisvane.getEventDeinJiv().getAdres()
				: null;
	}
	
	/**
	 * @return Видове животни, които ще участват в мероприятието
	 */
	public String getMeropriatieJivotni() {
		if(this.vpisvane.getEventDeinJiv() == null) return null;
		
		List<String> vidoveJivotni = new ArrayList<>();
		this.vpisvane.getEventDeinJiv().getEventDeinJivPredmet()
			.forEach(j -> {
				try {
					vidoveJivotni.add(
						this.systemData.decodeItem(
							BabhConstants.CODE_CLASSIF_VID_JIVOTNO,
							j.getPredmet(),
							this.userData.getCurrentLang(),
							this.date));
				}
				catch(DbErrorException e) {
					LOGGER.error(DB_ERROR_MSG, e);
				}
			});
		
		return (vidoveJivotni.isEmpty()) ? null : this.helpers.joinListWithSeparator(vidoveJivotni, ", ", true);
	}
	
	/**
	 * @return Има ли МПС навигационна система
	 */
	public String getMpsNavSystem() {
		try {
			return this.systemData.decodeItem(
				BabhConstants.CODE_CLASSIF_DANE,
				this.vpisvane.getMps().getNavigation(),
				this.userData.getCurrentLang(),
				this.date);
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Вида и марката на МПС
	 */
	public String getMpsVidMarka() {
		List<String> vidIMarka = new ArrayList<>();
		
		try {
			if(this.vpisvane.getMps() != null) {
				vidIMarka.add(this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VID_MPS,
						this.vpisvane.getMps().getVid(),
						this.userData.getCurrentLang(),
						this.date));

				vidIMarka.add(isNotBlank(this.vpisvane.getMps().getModel()) ? this.vpisvane.getMps().getModel() : null);
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
		
		return (vidIMarka.isEmpty()) ? null : this.helpers.joinListWithSeparator(vidIMarka, ", ", false);
	}
	
	/**
	 * @return Регистрационния номер на МПС
	 */
	public String getMpsNomer() {
		return (isNotBlank(this.vpisvane.getMps().getNomer()))
				? this.vpisvane.getMps().getNomer()
				: null;
	}
	
	/**
	 * @return Площта на МПС
	 */
	public String getMpsPlosht() {
		return (isNotBlank(this.vpisvane.getMps().getPlosht()))
				? this.vpisvane.getMps().getPlosht()
				: null;
	}
	
	/**
	 * @return Модела на МПС
	 */
	public String getMpsModel() {
		return (isNotBlank(this.vpisvane.getMps().getModel()))
				? this.vpisvane.getMps().getModel()
				: null;
	}
	
	/**
	 * @return Вида на МПС
	 */
	public String getMpsVid() {
		try {
			return this.systemData.decodeItem(
				BabhConstants.CODE_CLASSIF_VID_MPS,
				this.vpisvane.getMps().getVid(),
				this.userData.getCurrentLang(),
				this.date);
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Товароподемостта на МПС
	 */
	public String getMpsTovaropodemost() {
		return (isNotBlank(this.vpisvane.getMps().getTovaropodemnost()))
				? this.vpisvane.getMps().getTovaropodemnost()
				: null;
	}
	
	/**
	 * @return Обема на МПС
	 */
	public String getMpsObem() {
		return (isNotBlank(this.vpisvane.getMps().getObem())) 
				? this.vpisvane.getMps().getObem() 
				: null;
	}
	
	/**
	 * @return Категориите на МПС
	 */
	public String getMpsKategorii() {
		try {
			List<String> kategorii = new ArrayList<>();
			for(Integer cat : this.vpisvane.getMps().getCategoryList()) {
				kategorii.add(this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_CATEGORY_SJP,
						cat,
						this.userData.getCurrentLang(),
						this.date));
			}
			return (kategorii.isEmpty()) ? null : this.helpers.joinListWithSeparator(kategorii, ", ", false);
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Доп. инфо на МПС
	 */
	public String getMpsDopInfo() {
		return isNotBlank(this.vpisvane.getMps().getDopInfo())
				? this.vpisvane.getMps().getDopInfo()
				: null;
	}
	
	/**
	 * @return Името на собственика на МПС
	 */
	public String getMpsSobstvenikName() {
		Referent sobstvenik = this.helpers.getMpsSobstvenik();
		if(sobstvenik != null) {
			return (isNotBlank(sobstvenik.getRefName())) ? sobstvenik.getRefName() : null;
		}
		else return null;
	}
	
	/**
	 * @return Адрес на собственика на МПС
	 */
	public String getMpsSobstvenikAdres() {
		try {
			Referent sobstvenik = this.helpers.getMpsSobstvenik();
			if(sobstvenik != null) {
				String[] addressFields = this.helpers.getAddressFieldsByReferent(sobstvenik, false);
				return this.helpers.getAdresStringFromReferentAdresFields(addressFields);
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return ЕИК на собственика на МПС
	 */
	public String getMpsSobstvenikEik() { // TODO da se gleda dali e nfl ili fzl i egn/eik
		Referent sobstvenik = this.helpers.getMpsSobstvenik();
		if(sobstvenik != null) {
			return (isNotBlank(sobstvenik.getNflEik())) ? sobstvenik.getNflEik() : null;
		}
		else return null;
	}
	
	/**
	 * Създава данните за попълване на таблицата разрешени животни за пренасяне в документ 212
	 * @return данните за всеки ред са разделени помежду си с {@link UdostDocumentCreator#TABLE_ROW_DELIMITER},
	 * а във всеки ред отделните полета са разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getMpsVidJivotniTableData() {
		List<String> tableData = new ArrayList<>();
		
		try {
			for(MpsKapacitetJiv kapacitet : this.vpisvane.getMps().getMpsKapacitetJiv()) {
				List<String> currentKapacitetList = new ArrayList<>();
				
				String vidJivotno = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VID_JIVOTNO_PREVOZ,
						kapacitet.getVidJiv(),
						this.userData.getCurrentLang(),
						this.date);
				
				Integer broi = (kapacitet.getBroiJiv() == null) ? 0 : kapacitet.getBroiJiv();
				
				currentKapacitetList.add(isNotBlank(vidJivotno) ? vidJivotno : "");
				currentKapacitetList.add(String.valueOf(broi));
				
				tableData.add(String.join(UdostDocumentCreator.LIST_DELIMITER, currentKapacitetList));
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
		
		return tableData.isEmpty() ? null : String.join(UdostDocumentCreator.TABLE_ROW_DELIMITER, tableData);
	}
	
	/**
	 * @return Рег. номер на най-новото становище към вписването
	 */
	public String getLatestStanovishteRnDoc() {
		try {
			Object[] latestStanovishte = this.helpers.getLatestVpisvaneDoc(BabhConstants.CODE_ZNACHENIE_STANOVISHTE);
			return (latestStanovishte == null) ? null : (String) latestStanovishte[1];
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Датата на най-новото становище към вписването
	 */
	public String getLatestStanovishteDate() {
		try {
			Object[] latestStanovishte = this.helpers.getLatestVpisvaneDoc(BabhConstants.CODE_ZNACHENIE_STANOVISHTE);
			return (latestStanovishte == null) ? null : DATE_FORMAT_DD_MM_YYYY.format((Date) latestStanovishte[2]);
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Регистрационния номер на дейността с фуражи
	 */
	public String getEventDeinFurajRegNom() {
		if(this.vpisvane.getEventDeinostFuraji() != null) {
			return (isNotBlank(this.vpisvane.getEventDeinostFuraji().getRegNom())) 
					? this.vpisvane.getEventDeinostFuraji().getRegNom() 
					: null; 
		}
		else return null;
	}
	
	/**
	 * @return Временния регистрационния номер на дейността с фуражи
	 */
	public String getEventDeinFurajRegNomVrem() {
		if(this.vpisvane.getEventDeinostFuraji() != null) {
			return (isNotBlank(this.vpisvane.getEventDeinostFuraji().getRegNomVrem())) 
					? this.vpisvane.getEventDeinostFuraji().getRegNomVrem() 
					: null; 
		}
		else return null;
	}
	
	/**
	 * Създава данните за попълване на таблицата с видове фуражи в документ 255
	 * @return данните за всеки ред са разделени помежду си с {@link UdostDocumentCreator#TABLE_ROW_DELIMITER},
	 * а във всеки ред отделните полета са разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getFurajiSastioanieTableData() {
		List<String> tableData = new ArrayList<>();
		
		if(this.vpisvane.getEventDeinostFuraji() == null) {
			return null;
		}
		
		try {
			for(EventDeinostFurajiPredmet furajPredmet : this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet()) {
				List<String> currentPredmetList = new ArrayList<>();
				
				String vidFuraj = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ,
						furajPredmet.getVid(),
						this.userData.getCurrentLang(),
						this.date);
				
				String sastFuraj = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_SAST_FURAJ_PREVOZ,
						furajPredmet.getSastoianie(),
						this.userData.getCurrentLang(),
						this.date);
				
				currentPredmetList.add(isNotBlank(vidFuraj) ? vidFuraj : "");
				currentPredmetList.add(isNotBlank(sastFuraj) ? sastFuraj : "");
				
				tableData.add(String.join(UdostDocumentCreator.LIST_DELIMITER, currentPredmetList));
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
		
		return tableData.isEmpty() ? null : String.join(UdostDocumentCreator.TABLE_ROW_DELIMITER, tableData);
	}
	
	/**
	 * Създава данните за попълване на таблицата с транспортни средства в документ 255
	 * @return данните за всеки ред са разделени помежду си с {@link UdostDocumentCreator#TABLE_ROW_DELIMITER},
	 * а във всеки ред отделните полета са разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getFurajiTransportTableData() {
		List<String> tableData = new ArrayList<>();

		/*
		 * Нямам представа тази магия каква е и защо се прави. 
		 * Копирах го от екрана за вписването, защото иначе не ще да работи. 
		 * Бях шокиран.
		 * справка -> RegTargovtsiFuraj.loadMpsFurajiList()
		 */
		try {
			for (MpsDeinost mpsDein : this.vpisvane.getEventDeinostFuraji().getMpsDeinost()) {
				MpsFurajiDAO mpsFurajDAO = new MpsFurajiDAO(this.userData);
				MpsFuraji mpsFuraj = mpsFurajDAO.findById(mpsDein.getMpsId());
				mpsDein.setMpsFuraji(mpsFuraj);
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
				
		try {
			for(int i = 0; i < this.vpisvane.getEventDeinostFuraji().getMpsDeinost().size(); i++) {
				MpsDeinost mpsDeinost = this.vpisvane.getEventDeinostFuraji().getMpsDeinost().get(i);
			
				List<String> currentMpsList = new ArrayList<>();
				
				String mpsVid = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VID_MPS,
						mpsDeinost.getMpsFuraji().getVid(),
						this.userData.getCurrentLang(),
						this.date);
				
				String mpsNomer = isNotBlank(mpsDeinost.getMpsFuraji().getNomer()) ? mpsDeinost.getMpsFuraji().getNomer() : null;
				
				List<String> sastoiania = new ArrayList<String>();
				for(MpsVidProducts vidProdukt : mpsDeinost.getMpsFuraji().getMpsVidProducts()) {
					sastoiania.add(this.systemData.decodeItem(
									BabhConstants.CODE_CLASSIF_SAST_FURAJ_PREVOZ,
									vidProdukt.getSastoianie(),
									this.userData.getCurrentLang(),
									this.date));
				}
				
				currentMpsList.add(String.valueOf(i + 1));
				currentMpsList.add(isNotBlank(mpsVid) ? mpsVid : "");
				currentMpsList.add(isNotBlank(mpsNomer) ? mpsNomer : "");
				currentMpsList.add(sastoiania.isEmpty() ? "" : String.join(", ", sastoiania));
				
				tableData.add(String.join(UdostDocumentCreator.LIST_DELIMITER, currentMpsList));
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
		
		return tableData.isEmpty() ? null : String.join(UdostDocumentCreator.TABLE_ROW_DELIMITER, tableData);
	}
	
	
	/**
	 * Създава данните за попълване на таблицата със СЖП в документ 259
	 * @return данните за всеки ред са разделени помежду си с {@link UdostDocumentCreator#TABLE_ROW_DELIMITER},
	 * а във всеки ред отделните полета са разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getSjpTableData() {
		List<String> tableData = new ArrayList<>();
		
		try {
			for(EventDeinostFurajiPredmet furajPredmet : this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet()) {
				List<String> currentSjpList = new ArrayList<>();
				
				String vidFuraj = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ,
						furajPredmet.getVid(),
						this.userData.getCurrentLang(),
						this.date);

				String poiasnenie = isNotBlank(furajPredmet.getDopInfo()) ? furajPredmet.getDopInfo() : "";
				
				String kategoria = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_CATEGORY_SJP,
						//furajPredmet.getKategoria(), TODO kakvo se slu4va tuk?
						null,						
						this.userData.getCurrentLang(),
						this.date);
				
				currentSjpList.add(vidFuraj);
				currentSjpList.add(poiasnenie);
				currentSjpList.add(kategoria);
				
				tableData.add(String.join(UdostDocumentCreator.LIST_DELIMITER, currentSjpList));
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
		
		return tableData.isEmpty() ? null : String.join(UdostDocumentCreator.TABLE_ROW_DELIMITER, tableData);
	}
	
	/**
	 * @return Дървото с фуражи от класификация 531 в документ 24
	 */
	public String getVidoveFurajiTreeData() {
		
		if(this.vpisvane.getEventDeinostFuraji() == null) {
			return null;
		}
		
		List<Integer> vidoveFuraji = this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet()
			.stream()
			.map(EventDeinostFurajiPredmet::getVid)
			.collect(Collectors.toList());
		
		if(vidoveFuraji.isEmpty()) {
			return null;
		}

		TreeBuilder builder;
		try {
			builder = new TreeBuilder(this.userData, this.systemData, BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ, vidoveFuraji);
			builder.buildTree(false);
		}
		catch(Exception e) {
			LOGGER.error("Грешка при генериране на дърво с видове фуражи", e);
			return null;
		}
		
		builder.getResultList(); // тук са подредени значенията от класификацията, в реда в който трябва да се изпишат
		builder.getLevels(); // тук са съответните нива на табулиране, които трябва да се приложат на горния списък

		List<String> treeData = new ArrayList<>();

		for(int i = 0; i < builder.getResultList().size(); i++) {
			List<String> currentTreeRowData = new ArrayList<>();

			String text = "";
			try {
				text = this.systemData.decodeItem(
						builder.getResultList().get(i).getCodeClassif(),
						builder.getResultList().get(i).getCode(),
						this.userData.getCurrentLang(),
						this.date);
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}

			currentTreeRowData.add(text);
			currentTreeRowData.add(String.valueOf(builder.getLevels().get(i)));
			currentTreeRowData.add(vidoveFuraji.contains(builder.getResultList().get(i).getCode()) 
					? String.valueOf(BabhConstants.CODE_ZNACHENIE_DA)
					: String.valueOf(BabhConstants.CODE_ZNACHENIE_NE));
			
			treeData.add(String.join(UdostDocumentCreator.TREE_ATTRIBUTES_DELIMITER, currentTreeRowData));
		}
		
		String s = String.join(UdostDocumentCreator.LIST_DELIMITER, treeData);
		
		return (treeData.isEmpty()) ? null : s;
	}

	/**
	 * @return Дървото с фармацевтични форми от класификация 605 в документ 272
	 */
	public String getFarmFormiTreeData() {
		if(this.vpisvane.getEventDeinostVlp() == null) {
			return null;
		}
		
		List<Integer> vidoveFormi = this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPredmet()
				.stream()
				.map(EventDeinostVlpPredmet::getPredmet)
				.collect(Collectors.toList());

		if(vidoveFormi.isEmpty()) {
			return null;
		}

		TreeBuilder builder;
		try {
			builder = new TreeBuilder(this.userData, this.systemData, BabhConstants.CODE_CLASSIF_VID_DEIN_PROIZV_VNOS_VLP, vidoveFormi);
			builder.buildTree(false);
		}
		catch(Exception e) {
			LOGGER.error("Грешка при генериране на дърво с видове ВЛП", e);
			return null;
		}

		builder.getResultList(); // тук са подредени значенията от класификацията, в реда в който трябва да се изпишат
		builder.getLevels(); // тук са съответните нива на табулиране, които трябва да се приложат на горния списък

		List<String> treeData = new ArrayList<>();

		for(int i = 0; i < builder.getResultList().size(); i++) {
			List<String> currentTreeRowData = new ArrayList<>();
			SystemClassif currentClassif = builder.getResultList().get(i);
			String text = "";
			try {
				text = this.systemData.decodeItem(
						currentClassif.getCodeClassif(),
						currentClassif.getCode(),
						this.userData.getCurrentLang(),
						this.date);


				if(vidoveFormi.contains(currentClassif.getCode()) && isNotBlank(currentClassif.getDopInfo())) {
					text += " - " + currentClassif.getDopInfo();
				}
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}

			currentTreeRowData.add(text);
			currentTreeRowData.add(String.valueOf(builder.getLevels().get(i)));
			currentTreeRowData.add(vidoveFormi.contains(currentClassif.getCode())
					? String.valueOf(BabhConstants.CODE_ZNACHENIE_DA)
					: String.valueOf(BabhConstants.CODE_ZNACHENIE_NE));

			treeData.add(String.join(UdostDocumentCreator.TREE_ATTRIBUTES_DELIMITER, currentTreeRowData));
		}

		String s = String.join(UdostDocumentCreator.LIST_DELIMITER, treeData);

		return (treeData.isEmpty()) ? null : s;
	}
	
	public String getBgDiplomaWrapper248() {
		// Това е специфичен случай и трябва да се добави по-специална логика и в 
		// getReferentDiplomaNomer, getReferentDiplomaDate и getReferentDiplomaIssuedBy
		
		List<EventDeinJivLice> lica = this.helpers.getEventDeinostJivLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_RAKOVODITEL_EKIP);
		if(lica.size() == 0) {
			return null;
		}
		
		EventDeinJivLice lice = lica.get(0);
		Referent r = lice.getReferent();
		if(r != null) {
			if(isNotBlank(r.getFzlEgn())) {
				return UdostDocumentCreator.DONT_CHANGE_BOOKMARK_FLAG;
			}
			else if(isNotBlank(r.getFzlLnc())) {
				List<String> killedBookmarks = Arrays.asList("diplomaNomer", "diplomaDate", "diplomaIssuer");
				return String.join(UdostDocumentCreator.LIST_DELIMITER, killedBookmarks);
			}
			else return UdostDocumentCreator.DONT_CHANGE_BOOKMARK_FLAG;
		}
		else return UdostDocumentCreator.DONT_CHANGE_BOOKMARK_FLAG;
	}
	
	public String getChujdDiplomaWrapper248() {
		// Това е специфичен случай и трябва да се добави по-специална логика и в 
		// getReferentDiplomaNomer, getReferentDiplomaDate и getReferentDiplomaIssuedBy
		
		List<EventDeinJivLice> lica = this.helpers.getEventDeinostJivLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_RAKOVODITEL_EKIP);
		if(lica.size() == 0) {
			return null;
		}
		
		EventDeinJivLice lice = lica.get(0);
		Referent r = lice.getReferent();
		if(r != null) {
			if(isNotBlank(r.getFzlEgn())) {
				List<String> killedBookmarks = List.of("chujdGrajdaninNomer");
				return String.join(UdostDocumentCreator.LIST_DELIMITER, killedBookmarks);
			}
			else if(isNotBlank(r.getFzlLnc())) {
				return UdostDocumentCreator.DONT_CHANGE_BOOKMARK_FLAG;
			}
			else return UdostDocumentCreator.DONT_CHANGE_BOOKMARK_FLAG;
		}
		else return UdostDocumentCreator.DONT_CHANGE_BOOKMARK_FLAG;
	}
	
	/**
	 * @return Номера на дипломата на референта
	 */
	public String getRakovoditelDiplomaNomer() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			ReferentDoc diploma = this.helpers.getReferentDocByVid(r, BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA);
			if(diploma != null) {
				return isNotBlank(diploma.getNomDoc()) ? diploma.getNomDoc() : null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return Дата на издаване на дипломата на референта
	 */
	public String getRakovoditelDiplomaDate() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			ReferentDoc diploma = this.helpers.getReferentDocByVid(r, BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA);
			if(diploma != null) {
				return (diploma.getDateIssued() == null) ? null : DATE_FORMAT_DD_MM_YYYY.format(diploma.getDateIssued());
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * @return issuedBy на дипломата на референта
	 */
	public String getRakovoditelDiplomaIssuedBy() {
		try {
			Referent r = this.helpers.getReferentFromVpisvane();
			ReferentDoc diploma = this.helpers.getReferentDocByVid(r, BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA);
			if(diploma != null) {
				return isNotBlank(diploma.getIssuedBy()) ? diploma.getIssuedBy() : null;
			}
			else return null;
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	public String getChlenoveEkip248() {
		List<EventDeinJivLice> chlenove = this.helpers.getEventDeinostJivLiceByTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_SYBITIE_OBEKT_CHLEN_NA_EKIP);
		
		try {
			List<String> result = new ArrayList<>();
			
			for(EventDeinJivLice chlen : chlenove) {
				Referent r = this.helpers.getReferentById(chlen.getCodeRef());
				if(r == null) continue;
				
				ReferentDoc diploma = this.helpers.getReferentDocByVid(r, BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA);
				
				String imena = r.getRefName();
				String egnLncLiteral = this.helpers.getEgnEikByReferent(r)[0];
				String egnLncValue = this.helpers.getEgnEikByReferent(r)[1];
				String diplomaNomer = (diploma != null && isNotBlank(diploma.getNomDoc())) ? diploma.getNomDoc() : "";
				String diplomaData = (diploma != null && diploma.getDateIssued() != null) ? DATE_FORMAT_DD_MM_YYYY.format(diploma.getDateIssued()) : "";
				String vuz = (diploma != null && isNotBlank(diploma.getIssuedBy())) ? diploma.getIssuedBy() : "";
				
				String chlenString = 
						String.format("%s с %s %s, диплома за висше или средно животновъдно или ветеринарномедицинско образование с №%s и дата %s г. от ВУЗ %s",
								imena, egnLncLiteral, egnLncValue, diplomaNomer, diplomaData, vuz);
				result.add(chlenString);
			}
			
			return (result.isEmpty()) ? null : String.join(UdostDocumentCreator.LIST_DELIMITER, result);
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
	}
	
	/**
	 * Създава данните за попълване на таблицата с идентификатори в документ 215
	 * @return данните за всеки ред са разделени помежду си с {@link UdostDocumentCreator#TABLE_ROW_DELIMITER},
	 * а във всеки ред отделните полета са разделени с {@link UdostDocumentCreator#LIST_DELIMITER}
	 */
	public String getIdentifikatoriTableData() {
		List<String> tableData = new ArrayList<>();
		
		try {
			
			for(EventDeinJivIdentif identif : this.vpisvane.getEventDeinJiv().getEventDeinJivIdentif()) {
				List<String> currentRow = new ArrayList<>();
				
				String vidIdentif = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VID_IDENT_JIV,
						identif.getVidIdentif(),
						this.userData.getCurrentLang(),
						this.date);
				
				String vidJiv = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VID_JIVOTNO,
						identif.getVidJiv(),					
						this.userData.getCurrentLang(),
						this.date);
				
				String model = isNotBlank(identif.getModel()) ? identif.getModel() : "";
				String kodProizv = isNotBlank(identif.getKod()) ? identif.getKod() : "";
				
				currentRow.add(isNotBlank(vidIdentif) ? vidIdentif : "");
				currentRow.add(isNotBlank(vidJiv) ? vidJiv : "");
				currentRow.add(model);
				currentRow.add(kodProizv);
				
				tableData.add(String.join(UdostDocumentCreator.LIST_DELIMITER, currentRow));
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}
		
		return tableData.isEmpty() ? null : String.join(UdostDocumentCreator.TABLE_ROW_DELIMITER, tableData);
	}
	
	public String getObektDeinostImeIAdres() {
		ObektDeinost obekt = this.helpers.getObektDeinost();
		return this.helpers.getMestonahojdenieObekti(Arrays.asList(obekt));
	}

	public String getMestonahojdenieSobstvObekti() {
		List<ObektDeinost> obekti = this.helpers.getObektiDeinost()
				.stream()
				.filter(o -> o != null && !isNotBlank(o.getNomDatDogovor()))
				.collect(Collectors.toList());
		
		List<String> adresiList = new ArrayList<>();
		
		for(ObektDeinost obekt : obekti) {
			if(obekt == null) {
				continue;
			}
			
			try {
				List<String> adresiCyrLat = new ArrayList<>();
				String[] adresPoleta = this.helpers.getAddressFieldsByObektDeinost(obekt, false);
				adresiCyrLat.add(this.helpers.getAdresStringFromObektAdresFields(adresPoleta));
				adresPoleta = this.helpers.getAddressFieldsByObektDeinost(obekt, true);
				adresiCyrLat.add(this.helpers.getAdresStringFromObektAdresFields(adresPoleta));
				
				adresiList.add(this.helpers.joinListWithSeparator(adresiCyrLat, " / ", false));
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		
		if(!adresiList.isEmpty()) {
			return this.helpers.joinListWithSeparator(adresiList, "\n", false);
		}
		else return null;
	}

	public String getMestonahojdenieObektKontrol() {
		List<ObektDeinost> obekti = this.helpers.getObektiDeinost()
				.stream()
				.filter(o -> o != null && isNotBlank(o.getNomDatDogovor()))
				.collect(Collectors.toList());
		return this.helpers.getMestonahojdenieObekti(obekti);
	}

	public String getMestonahojdenieLaboratiria() {
		List<ObektDeinost> obekti = new ArrayList<>();
		for(ObektDeinost obekt : this.helpers.getObektiDeinost()) {
			if(obekt != null 
					&& obekt.getPrednaznachenieList() != null
					&& obekt.getPrednaznachenieList().contains(BabhConstants.CODE_ZNACHENIE_PREDNAZN_KONTRL_KACH)) {
				obekti.add(obekt);
			}
		}

		return this.helpers.getMestonahojdenieObekti(obekti);
	}

	public String getImaNemaObektSahranenie() {
		return this.helpers.getImaNemaObektSahranenie()[0];
	}

	public String getHasHasNotObektSahranenie() {
		return this.helpers.getImaNemaObektSahranenie()[1];
	}

	public String getImaNemaLaboratoria() {
		return this.helpers.getImaNemaLaboratoria()[0];
	}

	public String getHasHasNotLaboratoria() {
		return this.helpers.getImaNemaLaboratoria()[1];
	}

	public String getObektDeinostKvalifLica() { // TODO da go pravi na LIST!
		List<ObektDeinost> obektiDeinost = this.helpers.getObektiDeinost();
		List<String> lica = new ArrayList<>();
		for(ObektDeinost obekt : obektiDeinost) {
			if(obekt != null) {
				for(ObektDeinostLica lice : obekt.getObektDeinostLica()) {
					if(lice != null && lice.getRole() == BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_KVALIF_LICE) {
						try {
							Referent r = this.helpers.getReferentById(lice.getCodeRef());
							if(r != null) {
								lica.add(r.getRefName());
							}
						}
						catch(DbErrorException e) {
							LOGGER.error(DB_ERROR_MSG, e);
							return null;
						}
					}
				}
			}
		}

		return (lica.isEmpty()) ? null : this.helpers.joinListWithSeparator(lica, ", ", false);
	}

	public String getEventVlpDataInspekcia() {
		if(this.vpisvane.getEventDeinostVlp() != null) {
			Date data = this.vpisvane.getEventDeinostVlp().getDatInspekcia();

			if(data != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				return sdf.format(data);
			}
			else return null;
		}
		else return null;
	}

	public String getEventVlpObhvatInspekcia() {
		if(this.vpisvane.getEventDeinostVlp() != null) {
			String obhvat = this.vpisvane.getEventDeinostVlp().getObhvatInspekcia();
			return (isNotBlank(obhvat)) ? obhvat : null;
		}
		else return null;
	}

	public String getVlpVnosTableData() {
		if(this.vpisvane.getEventDeinostVlp() == null) {
			return null;
		}
		
		List<String> tableData = new ArrayList<>();

		try {
			for(EventDeinostVlpPrvnos prVnos : this.vpisvane.getEventDeinostVlp().getEventDeinostVlpPrvnos()) {
				List<String> currentPrvnosList = new ArrayList<>();

				String naimenovanie = prVnos.getNaimenovanie();

				String farmForma = this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_PHARM_FORMI,
						prVnos.getForma(),
						this.userData.getCurrentLang(),
						this.date);

				List<String> aktVeshtestva = new ArrayList<>();
				prVnos.getEventDeinostVlpPrvnosSubst()
						.forEach(s -> {
							if(s != null && s.getVidIdentifier() != null && isNotBlank(s.getVidIdentifier().getName())) {
								aktVeshtestva.add(s.getVidIdentifier().getName());
							}
						});

				List<String> opakovki = new ArrayList<>();
				prVnos.getEventDeinostVlpPrvnosOpakovka()
						.forEach(o -> {
							try {
								String opakovka = this.systemData.decodeItem(
										BabhConstants.CODE_CLASSIF_PARVICHNA_OPAKOVKA,
										o.getOpakovka(),
										this.userData.getCurrentLang(),
										this.date);
								opakovki.add(opakovka);
							}
							catch(DbErrorException e) {
								LOGGER.error(DB_ERROR_MSG, e);
							}
						});

				String prilojenie = prVnos.getPrilozenie();

				currentPrvnosList.add(isNotBlank(naimenovanie) ? naimenovanie : "");
				currentPrvnosList.add(isNotBlank(farmForma) ? farmForma : "");
				currentPrvnosList.add((aktVeshtestva.isEmpty()) ? "" : this.helpers.joinListWithSeparator(aktVeshtestva, "; ", false));
				currentPrvnosList.add((opakovki.isEmpty()) ? "" : this.helpers.joinListWithSeparator(opakovki, "; ", false));
				currentPrvnosList.add(isNotBlank(prilojenie) ? prilojenie : "");

				tableData.add(String.join(UdostDocumentCreator.LIST_DELIMITER, currentPrvnosList));
			}
		}
		catch(DbErrorException e) {
			LOGGER.error(DB_ERROR_MSG, e);
			return null;
		}

		return tableData.isEmpty() ? null : String.join(UdostDocumentCreator.TABLE_ROW_DELIMITER, tableData);
	}
	
	public String getFurajniProdukti() {
		
		if(this.vpisvane.getEventDeinostFuraji() == null) {
			return null;
		}
		
		List<Integer> vidoveFuraji = this.vpisvane.getEventDeinostFuraji().getEventDeinostFurajiPredmet()
				.stream()
				.map(EventDeinostFurajiPredmet::getVid)
				.collect(Collectors.toList());
			
		if(vidoveFuraji.isEmpty()) {
			return null;
		}
		
		List<String> vidoveFurajiDecoded = new ArrayList<>();
		
		for(Integer vid : vidoveFuraji) {
			try {
				vidoveFurajiDecoded.add(this.systemData.decodeItem(
						BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ,
						vid,
						this.userData.getCurrentLang(),
						this.date));
			}
			catch(DbErrorException e) {
				LOGGER.error(DB_ERROR_MSG, e);
				return null;
			}
		}
		
		if(!vidoveFurajiDecoded.isEmpty()) {
			return this.helpers.joinListWithSeparator(vidoveFurajiDecoded, ", ", true);
		}
		else return null;
	}

	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	
	/**
	 * Връща празен стринг; за тестване
	 * @return ""
	 */
	public String getEmpty() {
		return "";
	}

}