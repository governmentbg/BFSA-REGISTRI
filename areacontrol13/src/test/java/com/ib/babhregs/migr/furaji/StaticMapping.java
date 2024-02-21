package com.ib.babhregs.migr.furaji;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

/**
 * @author belev
 */
public class StaticMapping {

	static final String	NEW_LINE_R_N	= "\r\n";
	static final String	NEW_LINE_N		= "\n";

	static final Map<String, Integer> REGISTRATURI = new HashMap<>();

	static final Map<String, List<Integer>> JIVOTNI_32_1 = new HashMap<>();

	static {
		REGISTRATURI.put("БЛАГОЕВГРАД", 140);
		REGISTRATURI.put("БУРГАС", 118);
		REGISTRATURI.put("ВАРНА", 125);
		REGISTRATURI.put("ВЕЛИКО ТЪРНОВО", 132);
		REGISTRATURI.put("ВИДИН", 124);
		REGISTRATURI.put("ВРАЦА", 112);
		REGISTRATURI.put("ГАБРОВО", 2);
		REGISTRATURI.put("ДОБРИЧ", 127);
		REGISTRATURI.put("КЪРДЖАЛИ", 121);
		REGISTRATURI.put("КЮСТЕНДИЛ", 103);
		REGISTRATURI.put("ЛОВЕЧ", 108);
		REGISTRATURI.put("МОНТАНА", 119);
		REGISTRATURI.put("ПАЗАРДЖИК", 111);
		REGISTRATURI.put("ПЕРНИК", 105);
		REGISTRATURI.put("ПЛЕВЕН", 110);
		REGISTRATURI.put("ПЛОВДИВ", 115);
		REGISTRATURI.put("РАЗГРАД", 131);
		REGISTRATURI.put("РУСЕ", 126);
		REGISTRATURI.put("СИЛИСТРА", 133);
		REGISTRATURI.put("СЛИВЕН", 106);
		REGISTRATURI.put("СМОЛЯН", 104);
		REGISTRATURI.put("СОФИИЙСКА ОБЛАСТ", 117);
		REGISTRATURI.put("СОФИЯ ОБЛАСТ", 117);
		REGISTRATURI.put("СОФИЯ", 116);
		REGISTRATURI.put("СОФИЯ/БУРГАС", 116);
		REGISTRATURI.put("СОФИЯ/ДОБРИЧ", 116);
		REGISTRATURI.put("СОФИЯ/РУСЕ", 116);
		REGISTRATURI.put("СОФИЯ/ТЪРГОВИЩЕ", 116);
		REGISTRATURI.put("СТАРА ЗАГОРА", 113);
		REGISTRATURI.put("СТАРА ЗАГОРА/БУРГАС", 113);
		REGISTRATURI.put("ТЪРГОВИЩЕ", 109);
		REGISTRATURI.put("ХАСКОВО", 120);
		REGISTRATURI.put("ШУМЕН", 122);
		REGISTRATURI.put("ЯМБОЛ", 107);

		JIVOTNI_32_1.put("ДОМАШНИ ЛЮБИМЦИ", Arrays.asList(5));
//		JIVOTNI_32_1.put("ЖИВОТНИ С ЦЕННА КОЖА", null); // TODO тука какво
		JIVOTNI_32_1.put("НПЖ", Arrays.asList(3, 4));
		JIVOTNI_32_1.put("ПЖ", Arrays.asList(1, 2));
		JIVOTNI_32_1.put("ПЖ,НПЖ", Arrays.asList(1, 2, 3, 4));
		JIVOTNI_32_1.put("ПЖ/НПЖ", Arrays.asList(1, 2, 3, 4));
		JIVOTNI_32_1.put("ПЧЕЛИ", Arrays.asList(9));
		JIVOTNI_32_1.put("РИБИ", Arrays.asList(49));
		JIVOTNI_32_1.put("РИБИ - ЗАХРАНКИ", Arrays.asList(169));
	}

	/**
	 * Дава мап за откриване на код по текст UPPER без интервали - за ЕКАТТЕ
	 *
	 * @param em
	 * @return
	 */
	static final Map<String, Map<String, Integer>> findEkatteMap(EntityManager em) {
		@SuppressWarnings("unchecked")
		Stream<Object[]> stream = em.createNativeQuery("select ekatte, tvm, ime, oblast_ime from ekatte_att").getResultStream();

		Map<String, Map<String, Integer>> map = new HashMap<>();
		Iterator<Object[]> iter = stream.iterator();
		while (iter.hasNext()) {
			Object[] row = iter.next();

			String key = ("" + row[1] + row[2]).toUpperCase().replace(" ", ""); // ГР.ВЕЛИНГРАД

			Map<String, Integer> value = map.get(key);
			if (value == null) {
				value = new HashMap<>();
				map.put(key, value); // ПАЗАРДЖИК, 23456
			}
			value.put(("" + row[3]).toUpperCase(), ((Number) row[0]).intValue());
		}
		stream.close();

		return map;
	}

	static String replaceNewLine(String s, String replace) {
		if (s.indexOf(NEW_LINE_R_N) != -1) {
			return s.replace(NEW_LINE_R_N, replace);
		}
		if (s.indexOf(NEW_LINE_N) != -1) {
			return s.replace(NEW_LINE_N, replace);
		}
		return s;
	}

	static String[] splitNewLine(String s) {
		if (s.indexOf(NEW_LINE_R_N) != -1) {
			return s.split(NEW_LINE_R_N);
		}
		if (s.indexOf(NEW_LINE_N) != -1) {
			return s.split(NEW_LINE_N);
		}
		return new String[] { s };
	}

	/**  */
	private StaticMapping() {
		super();
	}

}
