package com.ib.babhregs.migr.furaji;

import static com.ib.system.utils.SearchUtils.trimToNULL;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.MpsFuraji;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

/**
 * Регистър на операторите, транспортиращи фуражи съгласно чл. 17е, ал. 2 от ЗФ.xlsx </br>
 * sheet='превозвачи'
 *
 * @author belev
 */
public class MigrRegister33_1 {

	private static final Logger LOGGER = LoggerFactory.getLogger(MigrRegister33_1.class);

	// Регистър на операторите, транспортиращи фуражи съгласно чл. 17б, ал. 2 от Закона за фуражите
	private static final int	REGISTER_CODE	= 33;
	private static final int	FILE_KEY		= 1;

	private static final int MIGR_USER_ID = -123457;

	// Заявление за регистрация на превозвачи, транспортиращи фуражи по чл. 17б, ал. 2 от ЗФ
	static final int VID_ZAIAV_VPISVANE = 148;

	// Заявление за промяна на обстоятелствата на регистрация на превозвачи, транспортиращи фуражи по чл. 17б, ал. 2 от ЗФ
	static final int VID_ZAIAV_PROMIANA = 149;

	// Заявление за заличаване на регистрация на превозвачи, транспортиращи фуражи по чл. 17б, ал. 2 от ЗФ
	static final int VID_ZAIAV_ZALICH = 150;

	// Удостоверение за регистрация на основание чл.17б,ал.6 от ЗФ-транспорт
	static final int VID_UD = 255;

	static final Date EMPTY_DATE = DateUtils.parse("01.01.2000");

	static final Set<String> TEMP_DEINOST = new HashSet<>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MigrRegister33_1.transfer(JPA.getUtil());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param jpa
	 * @throws DbErrorException
	 */
	public static void transfer(JPA jpa) throws DbErrorException {
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
			select.append(" , m.n, m.o, m.p, m.q, m.r, m.s, m.t, m.u, m.v ");
			select.append(" from migr_furaji m ");
			select.append(" where m.register = :registerCode and m.file_key = :fileKey ");
			select.append(" and m.header = 0 ");
			select.append(" and m.vpisvane_id is null ");
			select.append(" order by 1 ");

			@SuppressWarnings("unchecked")
			List<Object[]> rows = JPA.getUtil().getEntityManager().createNativeQuery(select.toString()) //
					.setParameter("registerCode", REGISTER_CODE).setParameter("fileKey", FILE_KEY) //
					.getResultList(); //

			int loadCnt = 0;
			int errCnt = 0;

			// key=име на лице UPPER
			Map<String, MigrRegister33_1> vpMap = new HashMap<>(); // не всеки ред е ново вписване

			for (Object[] row : rows) {
				id = ((Number) row[0]).intValue();

//				D - 2. Име/наименование на превозвача
				String key = SearchUtils.trimToNULL_Upper((String) row[4]);
				if (key == null) {
					LOGGER.error("  ! id=" + row[0] + " ! error EMPTY D={}", row[4]);
					errCnt++;
					continue; // не се знае какво е и не може да се запише този ред
				}
				boolean newVpisvane = false; // не всеки ред е ново вписване

				MigrRegister33_1 migr = vpMap.get(key);
				if (migr == null) {
					newVpisvane = true;
					migr = new MigrRegister33_1();
					vpMap.put(key, migr);
				}
				migr.migrTableIdList.add(id); // по това ще се знае как да се маркира за обаботено накрая
				migr.setupData(newVpisvane, row, ekatteMap, sdf); // за да се парснат данните
			}

			jpa.begin();
			for (MigrRegister33_1 migr : vpMap.values()) {
				if (migr.toLoad) {
					loadCnt++;
					if (loadCnt % 100 == 0) {
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

//			System.out.println(vpMap.size());

//			for (String s : TEMP_DEINOST) {
//				System.out.println(s);
//			}
//			System.out.println(TEMP_DEINOST.size());

		} catch (Exception e) {
			jpa.rollback();

			LOGGER.error("  ! id=" + id + " ! System ERROR transfer register -> " + e.getMessage(), e);

			throw new DbErrorException(e);

		} finally {
			jpa.closeConnection();
		}
	}

	private boolean toLoad = true;

	private Integer	registraturaId;
	private Integer	vpStatus	= BabhConstants.STATUS_VP_VPISAN;

	private List<String>	zaiavRnDocList		= new ArrayList<>();
	private List<Date>		zaiavDocDateList	= new ArrayList<>();

	private String	refName;
	private Integer	refEkatte;
	private String	refAddress;
	private String	contactEmail;
	private String	eikEgn;

	private List<Integer> deinostList = new ArrayList<>();

	private List<Doc>	udostDocList	= new ArrayList<>();
	private List<Doc>	zapDocList		= new ArrayList<>();

	private String	regNom;
	private String	codeDeinost;

	private StringBuilder dopInfo = new StringBuilder();

	private LinkedHashMap<Integer, MpsFuraji> mpsMap = new LinkedHashMap<>();

	private List<Integer> migrTableIdList = new ArrayList<>();

	/**
	 * потготвя данните
	 */
	public void setupData(boolean newVpisvane, Object[] row //
			, Map<String, Map<String, Integer>> ekatteMap //
			, SimpleDateFormat sdf) throws ParseException {

		if (newVpisvane) {
//			A - Област
			String colA = trimToNULL((String) row[1]);
			if (colA != null) {
				this.registraturaId = StaticMapping.REGISTRATURI.get(colA.toUpperCase());
				if (this.registraturaId == null) {
					this.toLoad = false;
					LOGGER.error("  ! id=" + row[0] + " ! error NOT_FOUND a={}", row[1]);
				}
			} else {
				this.toLoad = false;
				LOGGER.error("  ! id=" + row[0] + " ! error EMPTY A={}", row[1]);
			}

//			D - 2. Име/наименование на превозвача
			this.refName = trimToNULL((String) row[4]);
			if (this.refName == null) {
				this.toLoad = false;
				LOGGER.error("  ! id=" + row[0] + " ! error EMPTY D={}", row[4]);
			}

//			B - № на заявлението
			String colB = trimToNULL((String) row[2]);
			if (colB != null) {
				this.zaiavRnDocList = parseZaiavRnDoc(row, "B", 2, colB);
			} else {
				this.toLoad = false;
				LOGGER.error("  ! id=" + row[0] + " ! error EMPTY B={}", row[2]);
			}

//			C - Дата на подаване на заявлението
			String colC = trimToNULL((String) row[3]);
			if (colC != null) {
				this.zaiavDocDateList = parseZaiavDocDate(row, "C", 3, colC, sdf);
			} else {
				this.toLoad = false;
				LOGGER.error("  ! id=" + row[0] + " ! error EMPTY C={}", row[3]);
			}

			if (this.zaiavRnDocList.size() != this.zaiavDocDateList.size()) {
				this.toLoad = false;
				LOGGER.error("  ! id=" + row[0] + " ! error DIFF SIZE B={}, C={}", row[2], row[3]);
			}

//			E - 3. Адрес/седалище на превозвача
			this.refAddress = trimToNULL((String) row[5]);
			if (this.refAddress != null) {
//				Q - Област по местонахождение на превозвача
				String colQ = trimToNULL((String) row[17]);
				if (colQ == null) {
					colQ = "";
				}
				this.refEkatte = parseEkatte(row, "E", 5, this.refAddress, 0, colQ.toUpperCase(), ekatteMap);
			}

//			F - e - mail
			this.contactEmail = trimToNULL((String) row[6]);

//			G - Код по БУЛСТАТ/ЕИК
			this.eikEgn = trimToNULL((String) row[7]);

//			H - Декларирана дейност
			String colH = trimToNULL((String) row[8]);
			if (colH != null) {
				this.deinostList = parseDeinost(row, "H", 8, colH);
			} else {
				this.toLoad = false;
				LOGGER.error("  ! id=" + row[0] + " ! error EMPTY H={}", row[8]);
			}

//			K - № и дата на издаденото удостоверение за регистрация
			String colK = trimToNULL((String) row[11]);
			if (colK != null) {
				this.udostDocList = parseRnDocDocDate(row, "K", 11, colK, sdf);
			} else {
				this.vpStatus = BabhConstants.STATUS_VP_WAITING;
			}

//			L - 1. Регистрационен номер
			this.regNom = trimToNULL((String) row[12]);

//			M - № на Заповед за вписване на превозвача
			String colM = trimToNULL((String) row[13]);
			if (colM != null) {
				this.zapDocList = parseRnDocDocDate(row, "M", 13, colM, sdf);
			}

//			N - № на Заповед за възстановяване на временно заличена регистрация
			String colN = trimToNULL((String) row[14]);
			if (colN != null) {
				if (this.dopInfo.length() > 0) {
					this.dopInfo.append(StaticMapping.NEW_LINE_R_N);
				}
				this.dopInfo.append(colN);
			}

//			O - № на Заповед и дата за промяна на обстоятелствата
			String colO = trimToNULL((String) row[15]);
			if (colO != null) {
				if (this.dopInfo.length() > 0) {
					this.dopInfo.append(StaticMapping.NEW_LINE_R_N);
				}
				this.dopInfo.append(colO);
			}

//			P - Забележки по вписаните обстоятелства
			String colP = trimToNULL((String) row[16]);
			if (colP != null) {
				if (this.dopInfo.length() > 0) {
					this.dopInfo.append(StaticMapping.NEW_LINE_R_N);
				}
				this.dopInfo.append(colP);
			}

//			R - Код за вид дейност
			this.codeDeinost = trimToNULL((String) row[18]);

//			S - № на Заповед и дата за временно заличаване на регистрацията
			String colS = trimToNULL((String) row[19]);

//			T - № на Заповед и дата за заличаване на регистрацията
			String colT = trimToNULL((String) row[20]);

//			U - Месец на вписване в ЦУ
//			String colU = trimToNULL((String) row[21]); // това по ското не трябва

//			V - чл. 14, промяна в обстоятелствата и допълване на превозни средства
//			String colV = trimToNULL((String) row[22]); // това по скоро не трябва
		}

//		I - 4. Вид и регистрационен номер на транспортното средство
		String colI = trimToNULL((String) row[9]);
//		if (colI != null) {
//
//			String[] mpsArr = StaticMapping.splitNewLine(colI);
//			for (int i = 0; i < mpsArr.length; i++) {
//				String mps = mpsArr[i].replaceAll(" +", " ").trim();
//
//				if (mps.indexOf("№") == -1) {
//					if (i + 1 < mpsArr.length) { // ще пробвам със следващия ред
//						String next = mpsArr[i + 1].replaceAll(" +", " ").trim();
//						if (next.indexOf("№") != -1) {
//							mps = mps + next;
//							i++;
//						}
//					}
//				}
//
//				if (mps.indexOf("№") == -1) {
//					System.out.println(mps);
//				}
//				if (mps.indexOf(".") == -1) {
//					System.out.println(mps);
//				}
//			}
//		}

//		J - 5. Вид на фуражите, които ще се транспортиратнасипни/течни или опаковани
		String colJ = trimToNULL((String) row[10]);
	}

	List<Integer> parseDeinost(Object[] row, String col, int ind, String value) {
		List<Integer> list = new ArrayList<>();
		String[] arr = StaticMapping.splitNewLine(value);
		for (String a : arr) {
			String t = SearchUtils.trimToNULL(a);
			if (t == null) {
				continue;
			}
			StringBuilder sb = new StringBuilder();
			for (char ch : t.toCharArray()) {
				if (ch == ' ' || Character.isAlphabetic(ch)) {
					sb.append(ch);
				}
			}
			t = sb.toString().toUpperCase();

			t = t.replaceAll(" +", " ");
			t = t.replace("ТРАНСПОРТ НА", "");
			t = t.replace("TРАНСПОРТ НА", "");
			t = t.replace("ТРАНСПОРТ", "");
			t = t.replace("ТЪРГОВИЯ С", "");
			t = t.trim();

			if (t.length() > 0) { // TODO как ще се разбере какво точно има тука, за да се хванат кодовете не ми е ясно
				TEMP_DEINOST.add(t);
			}
		}
		return list;
	}

	Integer parseEkatte(Object[] row, String col, int ind //
			, String value, int valueArrIndex, String oblast //
			, Map<String, Map<String, Integer>> ekatteMap) {
		value = value.toUpperCase();

		if (value.contains("ГР. НОВА ЗАГОРА")) {
			return 51809;
		}
		if (value.contains("ГР. ЛОВЕЧ")) {
			return 43952;
		}
		if (value.contains("ГР. ЧИРПАН")) {
			return 81414;
		}

		String[] arr = StaticMapping.splitNewLine(value);
		if (arr.length == 1) {
			arr = value.split(",");
		}
		if (arr.length == 1) {
			arr = value.split(" БУЛ.");
		}
		if (arr.length == 1) {
			arr = value.split(" УЛ.");
		}
		if (arr.length == 1) {
			arr = value.split(" Ж.К.");
		}
		if (arr.length == 1) {
			arr = value.split(" КВ.");
		}
		if (arr.length == 1) {
			arr = value.split(" БЛ.");
		}
		if (arr.length == 1) {
			arr = value.split(" ОБЛ.");
		}
		if (arr.length == 1) {
			arr = value.split(" ОБЩ.");
		}
		if (arr.length == 1) {
			arr = value.split(" УПИ ");
		}
		if (valueArrIndex >= arr.length) { // гледа се за всеки ред
//			LOGGER.warn("  ! id=" + row[0] + " ! warn NOT_FOUND {}={}", col, row[ind]);
			return null;
		}

		String mesto = arr[valueArrIndex].trim(); // тука го търся

		int x;
		if ((x = mesto.indexOf(",")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf("-")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(";")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(" БУЛ.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(" УЛ.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(" Ж.К.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(" КВ.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(" БЛ.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(" ОБЛ.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(" ОБЩ.")) != -1) {
			mesto = mesto.substring(0, x);
		}
		if ((x = mesto.indexOf(" УПИ ")) != -1) {
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
		mesto = mesto.replace(" ", "");

		Integer ekatte = null;

		Map<String, Integer> map = ekatteMap.get(mesto);
		if (map == null) { // така ще се случи за всеки ред
			return parseEkatte(row, col, ind, value, valueArrIndex + 1, oblast, ekatteMap);

		} else if (map.size() > 1) {
			String check = value + oblast;

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
//				LOGGER.warn("  ! id=" + row[0] + " ! warn DUPLICATE {}={}", col, row[ind]);
			}
		} else {
			ekatte = map.values().stream().findAny().get();
		}
		return ekatte;
	}

	List<Doc> parseRnDocDocDate(Object[] row, String col, int ind, String value, SimpleDateFormat sdf) {
		List<Doc> list = new ArrayList<>();

		String value2 = value.replace("г.", "").replace("год.", "");

		value2 = StaticMapping.replaceNewLine(value2, "|");

		if (value2.contains(".|")) {
			value2 = value2.replace(".|", ".");
		}
		if (value2.contains(" от ")) {
			value2 = value2.replace(" от ", "/");
		}

		String[] arr = value2.split("\\|");
		for (String a : arr) {
			String t = SearchUtils.trimToNULL(a);
			if (t == null) {
				continue;
			}
			int slashInd = t.lastIndexOf("/");

			String rnDoc = null;
			Date docDate = null;

			if (slashInd == -1) {
				try { // за всяко пробвам да не би да е дата
					if (t.length() <= 10) {
						docDate = parseDateString(t, sdf);
					} else {
						rnDoc = t;
					}
				} catch (Exception e) {
					rnDoc = t; // остава да е само номер
				}
			} else {
				rnDoc = t.substring(0, slashInd);
				try {
					docDate = parseDateString(t.substring(slashInd + 1, t.length()), sdf);
				} catch (Exception e) {
					this.toLoad = false;
					LOGGER.error("  ! id=" + row[0] + " ! {} -> {}={}", e.getMessage(), col, row[ind]);
					return list; // няма причина да показваме още грешки, защото има ли една реда не е ок
				}
			}
			if (rnDoc != null) {
				rnDoc = rnDoc.replace("№", "").trim();
			}

			Doc udostDoc = new Doc();

			udostDoc.setRnDoc(rnDoc);
			udostDoc.setDocDate(docDate);

			list.add(udostDoc);
		}
		return list;
	}

	List<Date> parseZaiavDocDate(Object[] row, String col, int ind, String value, SimpleDateFormat sdf) {
		List<Date> list = new ArrayList<>();
		String[] arr = StaticMapping.splitNewLine(value);
		for (String a : arr) {
			String t = SearchUtils.trimToNULL(a);
			if (t == null || ".".equals(t)) {
				continue;
			}
			try {
				list.add(parseDateString(t, sdf));
			} catch (Exception e) {
				this.toLoad = false;
				LOGGER.error("  ! id=" + row[0] + " ! {} -> {}={}", e.getMessage(), col, row[ind]);
				return list; // няма причина да показваме още грешки, защото има ли една реда не е ок
			}
		}
		return list;
	}

	List<String> parseZaiavRnDoc(Object[] row, String col, int ind, String value) {
		List<String> list = new ArrayList<>();
		String[] arr = StaticMapping.splitNewLine(value);
		for (String a : arr) {
			String t = SearchUtils.trimToNULL(a);
			if (t == null) {
				continue;
			}
			list.add(t);
		}
		return list;
	}

	private Date parseDateString(String dateStr, SimpleDateFormat sdf) throws ParseException {
		if (dateStr == null || dateStr.trim().length() == 0) {
			return null;
		}
		dateStr = dateStr.toUpperCase();

		if (dateStr.indexOf("/") != -1) {
			dateStr = dateStr.replace("/", ".");
		}
		if (dateStr.indexOf(",") != -1) {
			dateStr = dateStr.replace(",", ".");
		}
		if (dateStr.indexOf("..") != -1) {
			dateStr = dateStr.replace("..", ".");
		}
		if (dateStr.indexOf("Г.") != -1) {
			dateStr = dateStr.replace("Г.", "");
		}
		if (dateStr.indexOf("Г") != -1) {
			dateStr = dateStr.replace("Г", "");
		}
		if (dateStr.indexOf(";") != -1) {
			dateStr = dateStr.replace(";", "");
		}
		if (dateStr.indexOf(" .") != -1) {
			dateStr = dateStr.replace(" .", ".");
		}
		return sdf.parse(dateStr.trim());
	}
}
