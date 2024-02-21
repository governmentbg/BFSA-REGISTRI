package com.ib.babhregs.search;

import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS;
import static com.ib.babhregs.system.BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ;
import static com.ib.system.utils.SearchUtils.trimToNULL_Upper;

import java.util.HashMap;
import java.util.Map;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.SelectMetadata;

/***
 * ТЪРСЕНЕ НА МПС
 *
 * @author s.arnaudova
 */
public class MPSsearch extends SelectMetadata {

	private static final long serialVersionUID = 4907674260235694671L;

	private static final String	AND		= " AND ";
	private static final String	WHERE	= " WHERE ";

	/** ще се използва за конвертиране на номер ако е въведен с кирилица. в БД са само на латиница */
	private static Map<Character, Character> map = new HashMap<>();
	static {
		map.put('А', 'A');
		map.put('В', 'B');
		map.put('Е', 'E');
		map.put('К', 'K');
		map.put('М', 'M');
		map.put('Н', 'H');
		map.put('О', 'O');
		map.put('Р', 'P');
		map.put('С', 'C');
		map.put('Т', 'T');
		map.put('У', 'Y');
		map.put('Х', 'X');
	}

	/**
	 * Заменя САМО валидни кирилица бувки с правилните им латница букви, спрямо правилата за рег.номер.</br>
	 * <b>Метода НЕ валидира дали номера е коректен.</b>
	 *
	 * @param nomer
	 * @return
	 */
	public static String convertMpsNomer(String nomer) {
		if (nomer == null) {
			return nomer;
		}
		nomer = nomer.toUpperCase();

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < nomer.length(); i++) {
			char orig = nomer.charAt(i);

			if (Character.isDigit(orig)) {
				sb.append(orig); // число без промяна

			} else if (map.containsKey(orig)) {
				sb.append(map.get(orig)); // за този в мапа е казано, че се конвертира

			} else {
				sb.append(orig);
			}
		}
		return sb.toString();
	}

	/** Вид на МПС ***/
	private Integer vidMPS;

	/** Модел ***/
	private String model;

	/** Рег. Номер ***/
	private String nomer;

	/** Тип на купето ***/
	private Integer typeCoupe;

	/**
	 * NULL-не се ограничава, но се извжда статус през вписване (ако има)</br>
	 * TRUE-гледа се да имат вписване и се извежда и статусът</br>
	 * FALSE-гледа се да нямат вписване и за статус се дава NULL</br>
	 */
	private Boolean registered;

	/** използва се при {@link #registered}=NULL или {@link #registered}=TRUE и се ограничава през vpisvane.code_page */
	private Integer codePage;

	/**
	 * [0]-ID</br>
	 * [1]-VID</br>
	 * [2]-null MARKA-няма вече</br>
	 * [3]-MODEL</br>
	 * [4]-NOMER</br>
	 * [5]-vpisvane.status Рег.Животни - като може и да е NULL</br>
	 * [6]-vpisvane.id_register Рег.Животни - като може и да е NULL</br>
	 * [7]-vpisvane.status Рег.Фуражи - като може и да е NULL</br>
	 * [8]-vpisvane.id_register Рег.Фуражи - като може и да е NULL</br>
	 */
	public void buildQueryMPSList() {
		Map<String, Object> params = new HashMap<>();

		StringBuilder from = new StringBuilder();
		StringBuilder select = new StringBuilder();
		StringBuilder where = new StringBuilder();

		select.append(" select distinct m.ID a0, m.VID a1, null a2, m.MODEL a3, m.NOMER a4, v.status a5, v.id_register a6 ");
		select.append(" , v2.status a7, v2.id_register a8 ");
		from.append(" from MPS m ");
		from.append(" left outer join vpisvane v on v.id_licenziant = m.id and v.licenziant_type = :licType and v.code_page = :jivPage ");
		from.append(" left outer join vpisvane v2 on v2.id_licenziant = m.id and v2.licenziant_type = :licType and v2.code_page = :furPage ");

		params.put("licType", BabhConstants.CODE_ZNACHENIE_TIP_LICENZ_MPS);
		params.put("jivPage", CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ);
		params.put("furPage", CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS);

		/** ВИД НА МПС */
		if (this.vidMPS != null) {
			where.append((where.length() == 0 ? WHERE : AND) + " m.VID = :vid ");
			params.put("vid", this.vidMPS);
		}

		/** МОДЕЛ */
		String t = trimToNULL_Upper(this.model);
		if (t != null) {
			where.append((where.length() == 0 ? WHERE : AND) + " upper(m.MODEL) like :model ");
			params.put("model", "%" + t + "%");
		}

		/** НОМЕР */
		t = trimToNULL_Upper(this.nomer);
		if (t != null) {
			where.append((where.length() == 0 ? WHERE : AND) + " upper(m.NOMER) like :nomer ");
			params.put("nomer", "%" + convertMpsNomer(t) + "%");
		}

		if (this.registered != null) {
			if (this.registered.booleanValue()) {
				where.append((where.length() == 0 ? WHERE : AND) + " (v.id is not null or v2.id is not null) "); // да има вписване
			} else {
				where.append((where.length() == 0 ? WHERE : AND) + " v.id is null and v2.id is null"); // да няма вписване
			}
		}

		if (this.codePage != null) { // за точния регистър
			if (this.codePage.equals(CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ)) {
				where.append((where.length() == 0 ? WHERE : AND) + " v.id is not null and v2.id is null ");

			} else if (this.codePage.equals(CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS)) {
				where.append((where.length() == 0 ? WHERE : AND) + " v2.id is not null and v.id is null ");

			} else {
				// няма какво тука да му измисля
			}
		}

		setSqlCount(" select count(distinct m.ID) " + from.toString() + where.toString());
		setSql(select.toString() + from.toString() + where.toString());
		setSqlParameters(params);
	}

	/** @return the codePage */
	public Integer getCodePage() {
		return this.codePage;
	}

	/** @return the model */
	public String getModel() {
		return this.model;
	}

	/** @return the nomer */
	public String getNomer() {
		return this.nomer;
	}

	/** @return the registered */
	public Boolean getRegistered() {
		return this.registered;
	}

	/** @return the typeCoupe */
	public Integer getTypeCoupe() {
		return this.typeCoupe;
	}

	/** @return the vidMPS */
	public Integer getVidMPS() {
		return this.vidMPS;
	}

	/** @param codePage the codePage to set */
	public void setCodePage(Integer codePage) {
		this.codePage = codePage;
	}

	/** @param model the model to set */
	public void setModel(String model) {
		this.model = model;
	}

	/** @param nomer the nomer to set */
	public void setNomer(String nomer) {
		this.nomer = nomer;
	}

	/** @param registered the registered to set */
	public void setRegistered(Boolean registered) {
		this.registered = registered;
	}

	/** @param typeCoupe the typeCoupe to set */
	public void setTypeCoupe(Integer typeCoupe) {
		this.typeCoupe = typeCoupe;
	}

	/** @param vidMPS the vidMPS to set */
	public void setVidMPS(Integer vidMPS) {
		this.vidMPS = vidMPS;
	}
}
