package com.ib.babhregs.migr.furaji;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.EventDeinostFurajiPrednaznJiv;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.SysConstants;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;
import com.ib.system.utils.ValidationUtils;

/**
 * Регистър на регистрираните обекти, съгласно чл. 17, ал. 3 от ЗФ.xlsx </br>
 * sheet='общо'
 *
 * @author belev
 */
public class MigrRegister32_1 {

	// TODO трявба да се направи нещо с номерата, за да се нагласят броячите след миграция
	// TODO не са направени другите sheet-ове
	// TODO какво се случва като има дублиране на номера на обекти

	private static final Logger LOGGER = LoggerFactory.getLogger(MigrRegister32_1.class);

	// Регистър на регистрираните обекти в сектор „Фуражи“ по чл. 17, ал. 3 от Закона за фуражите
	private static final int	REGISTER_CODE	= 32;
	private static final int	FILE_KEY		= 1;

	private static final int MIGR_USER_ID = -123456;

	// Заявление за регистрация на оператори по чл. 16, ал. 1 от ЗФ
	private static final int VID_ZAIAV_VPISVANE = 276;

	// Заявление за промяна на обстоятелствата по регистрацията на оператори по чл. 16, ал. 1 от ЗФ
	private static final int VID_ZAIAV_PROMIANA = 278;

	// Удостоверение за регистрация на основание чл.16
	private static final int VID_UD = 304;

	private static final Date EMPTY_DATE = DateUtils.parse("01.01.2000");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MigrRegister32_1 register = new MigrRegister32_1();

		try {
			register.transfer(JPA.getUtil());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Integer			registraturaId;
	private String			zaiavRnDoc;
	private Date			zaiavDocDate;
	private List<String>	otherZaiavRnList;
	private List<Date>		otherZaiavDateList;

	private String	refName;
	private Integer	refEkatte;
	private String	refAddress;
	private String	contactEmail;
	private String	eikEgn;

	private String	regNom;
	private Integer	obektEkatte;
	private String	obektAddress;

	private String	udRnDoc;
	private Date	udDocDate;

	private String	zapRnDoc;
	private Date	zapDocDate;

	private Integer			vpStatus;
	private StringBuilder	vpInfo;

	private Integer			tehnLinii;
	private List<Integer>	jivotniCodes;
	private String			codeDeinost;

	/**
	 * @param jpa
	 * @throws DbErrorException
	 */
	public void transfer(JPA jpa) throws DbErrorException {
		LOGGER.info("");
		LOGGER.info("Start transfer: REGISTER_CODE={}, FILE_KEY={}, MIGR_USER_ID={}" //
				, REGISTER_CODE, FILE_KEY, MIGR_USER_ID);

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Map<String, Map<String, Integer>> ekatteMap = StaticMapping.findEkatteMap(JPA.getUtil().getEntityManager());

		int id = -1;
		try {
			StringBuilder select = new StringBuilder();
			select.append(" select m.id ");
			select.append(" , m.a, m.b, m.c, m.d, m.e, m.f, m.g, m.h, m.i, m.j, m.k, m.l, m.m ");
			select.append(" , m.n, m.o, m.p, m.q, m.r, m.s, m.t, m.u, m.v, m.w, m.x, m.y, m.z ");
			select.append(" from migr_furaji m ");
			select.append(" where m.register = :registerCode and m.file_key = :fileKey ");
			select.append(" and m.header = 0 ");
//			select.append(" and m.row_num = 231 ");
			select.append(" and m.vpisvane_id is null ");
			select.append(" order by 1 ");

			@SuppressWarnings("unchecked")
			List<Object[]> rows = JPA.getUtil().getEntityManager().createNativeQuery(select.toString()) //
					.setParameter("registerCode", REGISTER_CODE).setParameter("fileKey", FILE_KEY) //
					.getResultList(); //

			int errCnt = 0;
			int loadCnt = 0;

			jpa.begin();
			for (Object[] row : rows) {
				id = ((Number) row[0]).intValue();

				boolean ok = setupData(row, ekatteMap, sdf); // за да се парснат данните
//				if (ok) {
//					MigrObject object = createMigrObject();
//					Integer vpisvaneId = object.load(JPA.getUtil().getEntityManager());
//
//					JPA.getUtil().getEntityManager().createNativeQuery( //
//							"update migr_furaji set vpisvane_id = ?1 where id = ?2") //
//							.setParameter(1, vpisvaneId).setParameter(2, id).executeUpdate();
//				}

				if (ok) {
					loadCnt++;
					if (loadCnt % 200 == 0) {
						JPA.getUtil().commit();
						JPA.getUtil().begin();
						LOGGER.info("  " + loadCnt + " -LOADED");
					}
				} else {
					errCnt++;
				}
			}
			jpa.commit();

			LOGGER.info("  " + loadCnt + " -LOADED");
			LOGGER.info("  " + errCnt + " -ERROR");
			LOGGER.info("Transfer COMPLETE.");

		} catch (Exception e) {
			jpa.rollback();

			LOGGER.error("  ! id=" + id + " ! System ERROR transfer register -> " + e.getMessage(), e);

			throw new DbErrorException(e);

		} finally {
			jpa.closeConnection();
		}
	}

	/**
	 * @return
	 */
	MigrObject createMigrObject() {
		MigrObject obj = new MigrObject(REGISTER_CODE);

		//
		// заявител
		obj.getZaiavitel().setUserReg(MIGR_USER_ID);
		obj.getZaiavitel().setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL); // дефолт
		if (this.eikEgn != null) {
			if (this.eikEgn.length() == 10 && ValidationUtils.isValidEGN(this.eikEgn)) {
				obj.getZaiavitel().setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
				obj.getZaiavitel().setFzlEgn(this.eikEgn);
			} else {
				obj.getZaiavitel().setNflEik(this.eikEgn);
			}
		}
		obj.getZaiavitel().setRefName(this.refName);
		obj.getZaiavitel().setContactEmail(this.contactEmail);
		obj.getZaiavitel().getAddressKoresp().setUserReg(MIGR_USER_ID);
		obj.getZaiavitel().getAddressKoresp().setEkatte(this.refEkatte);
		obj.getZaiavitel().getAddressKoresp().setAddrCountry(37); // винаги ли е БГ
		obj.getZaiavitel().getAddressKoresp().setAddrText(this.refAddress);

		//
		// заявление
		obj.getZaiav().setUserReg(MIGR_USER_ID);
		obj.getZaiav().setRegistraturaId(this.registraturaId);
		obj.getZaiav().setDocVid(VID_ZAIAV_VPISVANE);
		obj.getZaiav().setRnDoc(this.zaiavRnDoc);
		obj.getZaiav().setDocDate(this.zaiavDocDate);
		obj.getZaiav().setOtnosno("Миграция");
		obj.getZaiav().setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN);
		obj.getZaiav().setStatusDate(this.zaiavDocDate);

		//
		// уд
		if (this.udDocDate != null) {
			obj.getUdost().setUserReg(MIGR_USER_ID);
			obj.getUdost().setRegistraturaId(this.registraturaId);
			obj.getUdost().setDocVid(VID_UD);
			obj.getUdost().setRnDoc(this.udRnDoc);
			obj.getUdost().setDocDate(this.udDocDate);
			obj.getUdost().setOtnosno("Миграция");
			obj.getUdost().setValid(SysConstants.CODE_ZNACHENIE_DA);
			obj.getUdost().setValidDate(this.udDocDate);
		}

		Date vpStatusDate = this.zapDocDate;
		if (vpStatusDate == null) {
			vpStatusDate = this.udDocDate;
		}
		if (vpStatusDate == null) {
			vpStatusDate = this.zaiavDocDate;
		}
		if (vpStatusDate == null) {
			vpStatusDate = EMPTY_DATE;
		}

		//
		// вписване
		obj.getVpisvane().setUserReg(MIGR_USER_ID);
		obj.getVpisvane().setCodePage(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI);
		obj.getVpisvane().setRegistraturaId(this.registraturaId);
		obj.getVpisvane().setStatus(this.vpStatus);
		obj.getVpisvane().setDateStatus(vpStatusDate);
		obj.getVpisvane().setInfo(this.vpInfo.toString());

		//
		// статус
		obj.getStatus().setUserReg(MIGR_USER_ID);
		obj.getStatus().setStatus(this.vpStatus);
		obj.getStatus().setRegNomZapoved(this.zapRnDoc);
		obj.getStatus().setDateZapoved(this.zapDocDate);
		obj.getStatus().setDateStatus(vpStatusDate);

		//
		// фуражи
		obj.getFuraji().setUserReg(MIGR_USER_ID);
		obj.getFuraji().setBroiTehnLinii(this.tehnLinii);
		obj.getFuraji().setCodeDeinost(this.codeDeinost);

		if (!this.jivotniCodes.isEmpty()) {
			obj.getFuraji().setEventDeinostFurajiPrednaznJiv(new ArrayList<>());
			for (Integer vidJiv : this.jivotniCodes) {
				EventDeinostFurajiPrednaznJiv prednJiv = new EventDeinostFurajiPrednaznJiv();
				prednJiv.setVidJiv(vidJiv);
				obj.getFuraji().getEventDeinostFurajiPrednaznJiv().add(prednJiv);
			}
		}

		//
		// обект
		if (this.regNom != null || this.obektEkatte != null || this.obektAddress != null) {
			obj.getObekt().setUserReg(MIGR_USER_ID);
			obj.getObekt().setRegNom(this.regNom);
			obj.getObekt().setNasMesto(this.obektEkatte);
			obj.getObekt().setDarj(37); // винаги ли е БГ
			obj.getObekt().setAddress(this.obektAddress);
		}

		//
		// другите заявления
		for (int i = 0; i < this.otherZaiavRnList.size() && i < this.otherZaiavDateList.size(); i++) {
			Doc doc = new Doc();

			doc.setUserReg(MIGR_USER_ID);
			doc.setRegistraturaId(this.registraturaId);
			doc.setDocVid(VID_ZAIAV_PROMIANA);
			doc.setRnDoc(this.otherZaiavRnList.get(i));
			doc.setDocDate(this.otherZaiavDateList.get(i));
			doc.setOtnosno("Миграция");
			doc.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_OBRABOTEN);
			doc.setStatusDate(this.otherZaiavDateList.get(i));

			obj.getOtherZaiavList().add(doc);
		}
		return obj;
	}

	/**
	 * @param row
	 * @param ekatteMap
	 * @param sdf
	 * @return ОК или НЕ
	 * @throws ParseException
	 */
	boolean setupData(Object[] row, Map<String, Map<String, Integer>> ekatteMap, SimpleDateFormat sdf) throws ParseException {
		boolean ok = true;
		String tmp;

		this.registraturaId = null;
		this.zaiavRnDoc = null;
		this.zaiavDocDate = null;
		this.otherZaiavRnList = new ArrayList<>();
		this.otherZaiavDateList = new ArrayList<>();

		this.refName = null;
		this.refEkatte = null;
		this.refAddress = null;
		this.contactEmail = null;
		this.eikEgn = null;

		this.regNom = null;
		this.obektEkatte = null;
		this.obektAddress = null;

		this.udRnDoc = null;
		this.udDocDate = null;

		this.zapRnDoc = null;
		this.zapDocDate = null;

		this.vpStatus = BabhConstants.STATUS_VP_VPISAN;
		this.vpInfo = new StringBuilder();

		this.tehnLinii = null;
		this.jivotniCodes = new ArrayList<>();
		this.codeDeinost = null;

		tmp = SearchUtils.trimToNULL_Upper((String) row[1]); // A
		if (tmp != null) {
			this.registraturaId = StaticMapping.REGISTRATURI.get(tmp);
			if (this.registraturaId == null) {
				ok = false;
				LOGGER.error("  ! id=" + row[0] + " ! error NOT_FOUND a={}", row[1]);
			}
		} else {
			ok = false;
			LOGGER.error("  ! id=" + row[0] + " ! error EMPTY a={}", row[1]);
		}

		tmp = SearchUtils.trimToNULL((String) row[2]); // B
		if (tmp != null) {
			if (tmp.indexOf(StaticMapping.NEW_LINE_R_N) != -1) {
				tmp = tmp.replace(StaticMapping.NEW_LINE_R_N, StaticMapping.NEW_LINE_N);
			}
			if (tmp.indexOf(";") != -1) {
				tmp = tmp.replace(";", StaticMapping.NEW_LINE_N);
			}
			if (tmp.indexOf(",") != -1) {
				tmp = tmp.replace(",", StaticMapping.NEW_LINE_N);
			}

			String[] rnDocArr = StaticMapping.splitNewLine(tmp);
			this.zaiavRnDoc = rnDocArr[0].trim(); // за първоначално вписване
			if (rnDocArr.length > 1) {
				for (int i = 1; i < rnDocArr.length; i++) {
					if (rnDocArr[i].trim().length() == 0) {
						continue;
					}
					this.otherZaiavRnList.add(rnDocArr[i].trim());
				}
			}
		} else {
			this.zaiavRnDoc = "МИГ-" + row[0];
		}

		tmp = SearchUtils.trimToNULL((String) row[3]); // C
		if (tmp != null) {
			if (tmp.indexOf(StaticMapping.NEW_LINE_R_N) != -1) {
				tmp = tmp.replace(StaticMapping.NEW_LINE_R_N, StaticMapping.NEW_LINE_N);
			}
			if (tmp.indexOf(";") != -1) {
				tmp = tmp.replace(";", StaticMapping.NEW_LINE_N);
			}
			String[] docDateArr = StaticMapping.splitNewLine(tmp);
			try {
				this.zaiavDocDate = sdf.parse(docDateArr[0].trim().replace(",", ".").replace("/", "."));
			} catch (Exception ex) {
				this.zaiavDocDate = EMPTY_DATE; // TODO КК: Ще питам! Може за тях да вземаш датата на разрешението!
//				ok = false;
//				LOGGER.error("  ! id=" + row[0] + " ! error EXCEPTION c={}, ex={}", row[3], ex.getMessage());
			}
			if (docDateArr.length > 1) {
				for (int i = 1; i < docDateArr.length; i++) {
					if (docDateArr[i].trim().length() == 0) {
						continue;
					}
					try {
						this.otherZaiavDateList.add(sdf.parse(docDateArr[i].trim().replace(",", ".").replace("/", ".")));
					} catch (Exception ex) {
						ok = false;
						LOGGER.error("  ! id=" + row[0] + " ! error EXCEPTION c={}, ex={}", row[3], ex.getMessage());
					}
				}
			}
		} else {
			this.zaiavDocDate = EMPTY_DATE;
		}

		if (ok) {
			if (this.otherZaiavRnList.size() != this.otherZaiavDateList.size()) {
//				ok = false;
//				LOGGER.error("  ! id=" + row[0] + " ! error otherZaiav b={} ||| c={}", row[2], row[3]);

				// това няма да спира записа, но като се разминава бройката не се знае кое на коя дата е
				this.otherZaiavRnList.clear();
				this.otherZaiavDateList.clear();
			}
		}

		tmp = SearchUtils.trimToNULL((String) row[4]); // D
		if (tmp != null) {
			this.refName = tmp;
		} else {
			this.refName = "Неизвестно";
		}

		tmp = SearchUtils.trimToNULL((String) row[5]); // E
		if (tmp != null) {
			this.refEkatte = extractEkatte(0, row, ekatteMap, 5, "e", tmp);
			this.refAddress = StaticMapping.replaceNewLine(tmp, " ").replaceAll(" +", " ");
		}

		this.contactEmail = SearchUtils.trimToNULL((String) row[6]); // F

		tmp = SearchUtils.trimToNULL((String) row[7]); // G
		if (tmp != null) {
			StringBuilder sb = new StringBuilder();
			for (int ind = 0; ind < tmp.length(); ind++) {
				char ch = tmp.charAt(ind);
				if (Character.isDigit(ch)) {
					sb.append(ch);
				}
			}
			this.eikEgn = sb.length() == 0 ? null : sb.toString();
		}

		tmp = SearchUtils.trimToNULL((String) row[8]); // H
		if (tmp != null) {
			this.obektEkatte = extractEkatte(0, row, ekatteMap, 8, "h", tmp);
			this.obektAddress = StaticMapping.replaceNewLine(tmp, " ").replaceAll(" +", " ");
		}

		tmp = SearchUtils.trimToNULL((String) row[9]); // I
		String deinostText = tmp; // TODO КК: Ще направя анализ на данните, за да видим може ли да скалъпим нещо!

		tmp = SearchUtils.trimToNULL((String) row[10]); // J
		if (tmp != null) {
			try {
				this.tehnLinii = Integer.parseInt(tmp);
			} catch (Exception ex) { // ако не мине няма грижи
//				ok = false;
//				LOGGER.error("  ! id=" + row[0] + " ! error EXCEPTION j={}, ex={}", row[10], ex.getMessage());
			}
		}

		tmp = SearchUtils.trimToNULL((String) row[13]); // M
		if (tmp != null) {
			try {
				String[] zapArr = tmp.replace("№", "").split("/");
				this.zapRnDoc = zapArr[0].trim();
				this.zapDocDate = sdf.parse(StaticMapping.replaceNewLine(zapArr[1], "").replace(" ", "")); // остатъка ако е дата

			} catch (Exception ex) { // всичко отива в номера и няма дата
				this.zapRnDoc = StaticMapping.replaceNewLine(tmp, " ").replaceAll(" +", " ");
			}
		}

		tmp = SearchUtils.trimToNULL((String) row[11]); // K
		if (tmp != null) {
			// TODO KK: Предлагам да със същия вид, както е основното вписване, само че само последното да е със статус ‚валидно“, а другите
			// да са невалидни!
			try { // взимам един общ голям номер
				tmp = StaticMapping.replaceNewLine(tmp, " ").replace("№", "").trim();

				int ind = tmp.lastIndexOf('/');

				if (ind == -1) { // не може да се разбере кое е номера и приемам всичко за номер
					this.udRnDoc = tmp;

					if (this.zapDocDate != null) {
						this.udDocDate = this.zapDocDate;
					} else if (this.zaiavDocDate != null) {
						this.udDocDate = this.zaiavDocDate;
					} else {
						this.udDocDate = EMPTY_DATE;
					}

				} else {
					this.udRnDoc = tmp.substring(0, ind).trim();

					String rest = tmp.substring(ind + 1, tmp.length()).trim(); // остатъка

					rest = rest.replaceAll(" +", " ");

					if (rest.indexOf('/') != -1) {
						rest = rest.replace("/", "");
					}
					if (rest.indexOf("..") != -1) {
						rest = rest.replace("..", ".");
					}
					if (rest.indexOf(".  ") != -1) {
						rest = rest.replace(".  ", ".");
					}

					if (rest.length() > 10) {
						rest = rest.substring(0, 10).trim();
					}
					if (rest.indexOf(',') != -1) {
						rest = rest.replace(",", ".");
					}
					if (rest.indexOf('г') != -1) {
						rest = rest.replace("г", "").trim();
					}
					if (rest.indexOf(" .") != -1) {
						rest = rest.replace(" .", ".");
					}
					if (rest.indexOf(". ") != -1) {
						rest = rest.replace(". ", ".");
					}
					if (rest.indexOf(' ') != -1) {
						rest = rest.replace(" ", ".");
					}
					this.udDocDate = sdf.parse(rest);
				}

			} catch (Exception ex) {
				ok = false;
				LOGGER.error("  ! id=" + row[0] + " ! error EXCEPTION k={}, ex={}", row[11], ex.getMessage());
			}
		}
		if (ok && SearchUtils.isEmpty(this.udRnDoc)) {
			this.vpStatus = BabhConstants.STATUS_VP_WAITING;
		}

		this.regNom = SearchUtils.trimToNULL((String) row[12]); // L

		tmp = SearchUtils.trimToNULL((String) row[14]); // N
		if (tmp != null) {
			if (this.vpInfo.length() > 0) {
				this.vpInfo.append(StaticMapping.NEW_LINE_R_N);
			}
			this.vpInfo.append(tmp);
		}

		tmp = SearchUtils.trimToNULL((String) row[15]); // O
		if (tmp != null) {
			if (this.vpInfo.length() > 0) {
				this.vpInfo.append(StaticMapping.NEW_LINE_R_N);
			}
			this.vpInfo.append(tmp);
		}

		tmp = SearchUtils.trimToNULL((String) row[16]); // P
		if (tmp != null) {
			if (this.vpInfo.length() > 0) {
				this.vpInfo.append(StaticMapping.NEW_LINE_R_N);
			}
			this.vpInfo.append(tmp);
		}

		// TODO надолу със статусите не е много ясно какво и как

		tmp = SearchUtils.trimToNULL((String) row[18]); // R
		if (tmp != null) {
			if (tmp.toUpperCase().indexOf("НЕ Е ИЗДАДЕНО УДОСТОВЕРЕНИЕ ЗА РЕГИСТРАЦИЯ") != -1) {
				this.vpStatus = BabhConstants.STATUS_VP_OTKAZ;
			}

			if (this.vpInfo.length() > 0) {
				this.vpInfo.append(StaticMapping.NEW_LINE_R_N);
			}
			this.vpInfo.append(tmp);
		}

		this.codeDeinost = SearchUtils.trimToNULL((String) row[19]); // S

		tmp = SearchUtils.trimToNULL_Upper((String) row[20]); // T
		if (tmp != null) {
			List<Integer> list = StaticMapping.JIVOTNI_32_1.get(tmp);
			if (list == null) {
//				ok = false;
				LOGGER.warn("  ! id=" + row[0] + " ! error NOT_FOUND t={}", row[20]);
			} else {
				this.jivotniCodes.addAll(list);
			}
		}

		tmp = SearchUtils.trimToNULL((String) row[22]); // V
		if (tmp != null) {
			this.vpStatus = BabhConstants.STATUS_VP_VREM_PREUST;

			if (this.vpInfo.length() > 0) {
				this.vpInfo.append(StaticMapping.NEW_LINE_R_N);
			}
			this.vpInfo.append(tmp);
		}

		tmp = SearchUtils.trimToNULL((String) row[23]); // W
		if (tmp != null) {
			this.vpStatus = BabhConstants.STATUS_VP_ZALICHEN;

			if (this.vpInfo.length() > 0) {
				this.vpInfo.append(StaticMapping.NEW_LINE_R_N);
			}
			this.vpInfo.append(tmp);
		}

		tmp = SearchUtils.trimToNULL((String) row[25]); // Y
		if (tmp != null) {
			if (SearchUtils.isEmpty((String) row[14])) { // N
//				this.vpStatus = BabhConstants.STATUS_VP_VREM_PREUST; // TODO не може така да се направи
			}

			if (this.vpInfo.length() > 0) {
				this.vpInfo.append(StaticMapping.NEW_LINE_R_N);
			}
			this.vpInfo.append(tmp);
		}

		return ok;
	}

	private Integer extractEkatte(int checkIndex, Object[] row, Map<String, Map<String, Integer>> ekatteMap //
			, int rowDataIndex, String excelCol, String tmp) {
		if (tmp.contains("задържа") || tmp.contains("складиране") //
				|| tmp.contains("съхранение") || tmp.contains("използва")) {
			return null;
		}
		if (tmp.contains("гр. Нова Загора")) {
			return 51809;
		}
		if (tmp.contains("гр. Ловеч")) {
			return 43952;
		}
		if (tmp.contains("гр. Чирпан")) {
			return 81414;
		}
		if (tmp.contains("с. Игнатиево")) {
			return 32278;
		}

		String[] arr = StaticMapping.splitNewLine(tmp);
		if (arr.length == 1) { // ще сплитваме по ","
			arr = tmp.split(",");
		}
		if (arr.length == 1) { // ще сплитваме по " ул."
			arr = tmp.split(" ул.");
		}
		if (arr.length == 1) { // ще сплитваме по " бул."
			arr = tmp.split(" бул.");
		}
		if (arr.length == 1) { // ще сплитваме по " бл."
			arr = tmp.split(" бл.");
		}
		if (arr.length == 1) { // ще сплитваме по " обл."
			arr = tmp.split(" обл.");
		}
		if (arr.length == 1) { // ще сплитваме по " общ."
			arr = tmp.split(" общ.");
		}
		if (checkIndex >= arr.length) { // гледа се за всеки ред
			LOGGER.warn("  ! id=" + row[0] + " ! warn NOT_FOUND {}={}", excelCol, row[rowDataIndex]);
			return null;
		}

		String mesto = arr[checkIndex].toUpperCase(); // тука го търся

		int x;
		if ((x = mesto.indexOf(",")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf("УЛ.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf("БУЛ.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf("Ж.К.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf("КВ.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(" УПИ ")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf("-")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(";")) != -1) {
			mesto = mesto.substring(0, x);
		}
		boolean digit = false;
		for (x = 0; x < mesto.length(); x++) {
			if (Character.isDigit(mesto.charAt(x))) {
				digit = true;
				break;
			}
		}
		if (digit) {
			mesto = mesto.substring(0, x);
		}
		mesto = mesto.replace(" ", "").toUpperCase();

		Integer ekatte = null;

		Map<String, Integer> map = ekatteMap.get(mesto);
		if (map == null) {
			return extractEkatte(checkIndex + 1, row, ekatteMap, rowDataIndex, excelCol, tmp);

		} else if (map.size() > 1) {
			String check = tmp;

			if (row[1] != null) { // A
				check = check + row[1];
			}
			if (row[17] != null) { // Q
				check = check + row[17];
			}
			check = check.toUpperCase();

			if (check.contains("СОФИЯ")) {
				check = check + "СОФИЯ (СТОЛИЦА)";
			}
			if (check.contains("СОФИИЙСКА")) {
				check = check + "СОФИЯ";
			}

			for (Entry<String, Integer> entry : map.entrySet()) {
				if (check.contains(entry.getKey())) {
					ekatte = entry.getValue();
					break;
				}
			}
			if (ekatte == null) {
				LOGGER.warn("  ! id=" + row[0] + " ! warn DUPLICATE {}={}", excelCol, row[rowDataIndex]);
			}
		} else {
			ekatte = map.values().stream().findAny().get();
		}
		return ekatte;
	}
}
