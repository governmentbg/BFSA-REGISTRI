package com.ib.babhregs.search;

import static com.ib.system.SysConstants.CODE_CLASSIF_EKATTE;
import static com.ib.system.utils.SearchUtils.trimToNULL_Upper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.BaseSystemData;
import com.ib.system.SysConstants;
import com.ib.system.db.SelectMetadata;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.InvalidParameterException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

/**
 * Филтър за специализирани справки
 *
 * @author belev
 */
public class SpecReportSearch extends SelectMetadata {

	private static final long serialVersionUID = -816432581752529711L;

	private static final String	AND		= " AND ";
	private static final String	WHERE	= " WHERE ";

//
//	2.1.	Общи атрибути:

	private Integer			oblast;			//  Област на контрол - задължително
	private List<Integer>	registerIdList;	//  Видове регистри (от избраната област)

//		Заявления
	private List<Integer>	zaiavDocVidList;		// • Видове заявления (от избраната област)
	private String			zaiavRnDoc;				// • Рег. номер
	private boolean			zaiavRnDocEQ	= true;	// ако е true се търси по пълно съвпадение по номер на документ
	private Date			zaiavDateFrom;			// • Период на дата на регистрация
	private Date			zaiavDateTo;			// • Период на дата на регистрация
	private List<Integer>	zaiavStatusList;		// • Статус на заявление
	private Date			zaiavStatusDateFrom;	// • Период на дата на статуса на заявление
	private Date			zaiavStatusDateTo;		// • Период на дата на статуса на заявление

//		Физически лица 	Юридически лица
	private String	fzlEgn;			// • ЕГН
	private String	fzlLnc;			// • ЛНЧ
	private String	fzlNomDoc;		// • Номер на документ
	private String	nflEik;			// • ЕИК
	private String	nflIdentNom;	// • Идент. номер на ЮЛ
	private String	refNameBgEn;	// • Имена на ФЛ • Наименование на ЮЛ
	private String	refAddress;		// • Адрес
	private Integer	refCountry;		// • Адрес държава
	private Integer	refEkatte;		// • Адрес нас.място /област /община
//	•	Свързаност - алтернативно
	private boolean			licenziant	= true;			//  Заявител / Притежател на разрешение (да/не)
	private boolean			obektLice;					//  Качество на лицето във връзка с обект на дейност (да/не)
	private boolean			predstLice;					//  Представялващо лице. (да/не)
	private List<Integer>	kachestvoPredstLiceList;	//  Класификация 512
	private List<Integer>	kachestvoObektLiceList;		//  Класификация 535

//		Вписвания
	private List<Integer>	registraturaIdList;	// • Институция, работила по вписването
	private List<Integer>	vpStatusList;		// • Статус на вписване
	private Date			vpStatusDateFrom;	// • Период на датата на статуса
	private Date			vpStatusDateTo;		// • Период на датата на статуса

//		Удостоверителни документи:
	private List<Integer>	udDocVidList;		// • Вид документ
	private String			udRnDoc;			// • Рег. номер на удостоверителен документ
	private boolean			udRnDocEQ	= true;	// ако е true се търси по пълно съвпадение по номер на документ
	private Date			udDateFrom;			// • Период на дата на регистрация на удостоверителен документ
	private Date			udDateTo;			// • Период на дата на регистрация на удостоверителен документ
	private List<Integer>	udValidList;		// • Статус на удостоверителен документ
	private Date			udValidDateFrom;	// • Период на дата на статуса на удостоверителен документ
	private Date			udValidDateTo;		// • Период на дата на статуса на удостоверителен документ

//
//	2.2.1.	Здравеопазване на животните
//		Обект на дейност:
	private List<Integer>	oezVidList;				// • Вид на обекта (ОЕЗ)
	private List<Integer>	vlzVidList;				// • Вид на обекта (ВЛЗ)
	private String			jivObektNaimenovanie;	// • Наименование
	private String			jivObektRegNom;			// • Номер по регламент
	private String			jivObektRegNomerStar;	// • Стар номер на ОЕЗ
	private String			jivObektAddress;		// • Местонахождение на обект (адрес)
	private Integer			jivObektCountry;		// • Местонахождение на обект (адрес) държава
	private Integer			jivObektEkatte;			// • Местонахождение на обект (адрес) нас.място /област /община

	private List<Integer>	jivVidDeinList;		//  Вид дейност (логическа класификация)
	private List<Integer>	jivVidIdentifList;	//  Вид на идентификатор за идентификация на животни
	private List<Integer>	jivVidJivotniList;	//  Вид животни (класификация 508)
	private List<Integer>	jivPrednJivOezList;	//  Предназначение на животните в ОЕЗ
	private List<Integer>	jivTehnologiaList;	//  Технология на отглеждане
	private String			jivNomerMps;		//  Номер на МПС

//
//	2.2.2.	Контрол на фуражи
//		Обект на дейност
	private String	furObektRegNom;		// • Номер на обект
	private String	furObektAddress;	// • Местонахождение на обект на дейност (адрес)
	private Integer	furObektCountry;	// • Местонахождение на обект на дейност (адрес) държава
	private Integer	furObektEkatte;		// • Местонахождение на обект на дейност (адрес) нас.място /област /община

	private List<Integer>	furVidDeinList;		//  Вид дейност (логическа класификация)
	private List<Integer>	furVidFurajList;	//  Вид фуражи (класификация 566)
	private String			furNomerMps;		//  Номер на МПС
	private List<Integer>	furVidJivotniList;	//  Видове животни
	private Integer			furSklad;			//  Съхранява ли фуражи на склад

//
//	2.2.3.	Контрол на ВЛП
	private List<Integer>	vlpVidDeinList;			//  Вид дейност (логическа класификация)
	private String			vlpNaimenovanie;		//  Наименование на ВЛП (на кирилица или латиница)
	private List<Integer>	vlpPredmetPrvnosList;	//  Фармацевтични форми и дейности за производство и внос (класификация 605)
	private List<Integer>	vlpFarmFormList;		//  Фармацевтични форми за ВЛП (класификация 693)
	private List<Integer>	vlpPredmetFarmakolList;	//  Фармакологични групи ВЛП (класификация 564)
	private List<Integer>	vlpOpakovkaList;		//  Първична опаковка
	private String			vlpAktVeshtva;			//  Активни вещества
	private String			vlpPomVeshtva;			//  Помощни вещества

//		Обект на дейност
	private List<Integer>	vlpPrednObektList;		// • Предназначение;
	private String			vlpObektNaimenovanie;	// • Наименование;
	private String			vlpObektAddress;		// • Местонахождение на обект на дейност (адрес)
	private Integer			vlpObektCountry;		// • Местонахождение на обект на дейност (адрес) държава
	private Integer			vlpObektEkatte;			// • Местонахождение на обект на дейност (адрес) нас.място /област /община

	/**
	 * [0]-vpisvane.id<br>
	 * [1]-id_register<br>
	 * [2]-code_ref_vpisvane<br>
	 * [3]-zaiavitel.fzl_egn<br>
	 * [4]-zaiavitel.fzl_ln_es<br>
	 * [5]-zaiavitel.fzl_lnc<br>
	 * [6]-zaiavitel.ref_name<br>
	 * [7]-zaiavitel.nfl_eik<br>
	 * [8]-status<br>
	 * [9]-date_status<br>
	 * [10]-reg_nom_result<br>
	 * [11]-date_result<br>
	 * [12]-licenziant<br>
	 * [13]-licenziant_type<br>
	 * [14]-id_licenziant<br>
	 * [15]-id_zaqavlenie<br>
	 * [16]-code_page<br>
	 * [17]-reg_nom_zaqvl_vpisvane<br>
	 * [18]-date_zaqvl_vpis<br>
	 * [19]-registratura_id<br>
	 * [20]-doc.doc_vid<br>
	 *
	 * @throws InvalidParameterException
	 * @throws DbErrorException
	 */
	public void buildQueryReport(BaseSystemData systemData) throws InvalidParameterException, DbErrorException {
		if (this.oblast == null) {
			throw new InvalidParameterException("Моля, изберете област на контрол.");
		}

		StringBuilder select = new StringBuilder();
		StringBuilder from = new StringBuilder();
		StringBuilder where = new StringBuilder();

		Map<String, Object> params = new HashMap<>();

		select.append(" select distinct v.id a0, v.id_register a1, v.code_ref_vpisvane a2 ");
		select.append(" , zaiavitel.fzl_egn a3, zaiavitel.fzl_ln_es a4, zaiavitel.fzl_lnc a5, zaiavitel.ref_name a6, zaiavitel.nfl_eik a7 ");
		select.append(" , v.status a8, v.date_status a9, v.reg_nom_result a10, v.date_result a11 ");
		select.append(" , v.licenziant a12, v.licenziant_type a13, v.id_licenziant a14 ");
		select.append(" , v.id_zaqavlenie a15, v.code_page a16, v.reg_nom_zaqvl_vpisvane a17, v.date_zaqvl_vpis a18, v.registratura_id a19, d.doc_vid a20");

		from.append(" from vpisvane v ");
		from.append(" left outer join doc d on v.id_zaqavlenie = d.doc_id ");

		//
		// Критерии по вписване
		List<Integer> registerIdParam;
		if (this.registerIdList == null || this.registerIdList.isEmpty()) { // иска се за цялата област
			registerIdParam = systemData.getCodesOnNextLevel(BabhConstants.CODE_CLASSIF_VIDOVE_REGISTRI, this.oblast, null, SysConstants.CODE_LANG_BG);
		} else { // само избраните
			registerIdParam = this.registerIdList;
		}
		where.append((where.length() == 0 ? WHERE : AND) + " v.id_register in (:registerIdParam) ");
		params.put("registerIdParam", registerIdParam);

		if (this.registraturaIdList != null && !this.registraturaIdList.isEmpty()) {
			where.append((where.length() == 0 ? WHERE : AND) + " v.registratura_id in (:registraturaIdList) ");
			params.put("registraturaIdList", this.registraturaIdList);
		}

		if (this.vpStatusList != null && !this.vpStatusList.isEmpty()) {
			where.append((where.length() == 0 ? WHERE : AND) + " v.status in (:vpStatusList) ");
			params.put("vpStatusList", this.vpStatusList);
		}
		if (this.vpStatusDateFrom != null) {
			where.append((where.length() == 0 ? WHERE : AND) + " v.date_status >= :vpStatusDateFrom ");
			params.put("vpStatusDateFrom", DateUtils.startDate(this.vpStatusDateFrom));
		}
		if (this.vpStatusDateTo != null) {
			where.append((where.length() == 0 ? WHERE : AND) + " v.date_status <= :vpStatusDateTo ");
			params.put("vpStatusDateTo", DateUtils.endDate(this.vpStatusDateTo));
		}

		//
		// Критерии по заявления
		boolean joinZaiav = false; // само ако има търсене по данни за заявленията

		if (this.zaiavDocVidList != null && !this.zaiavDocVidList.isEmpty()) {
			joinZaiav = true;
			where.append((where.length() == 0 ? WHERE : AND) + " ( zaiav.doc_vid in (:zaiavDocVidList) or zaiav.doc_pod_vid in (:zaiavDocVidList) ) ");
			params.put("zaiavDocVidList", this.zaiavDocVidList);
		}

		String t = trimToNULL_Upper(this.zaiavRnDoc);
		if (t != null) {
			joinZaiav = true;
			if (this.zaiavRnDocEQ) { // пълно съвпадение case insensitive
				where.append((where.length() == 0 ? WHERE : AND) + " upper(zaiav.rn_doc) = :zaiavRnDoc ");
				params.put("zaiavRnDoc", t);

			} else {
				where.append((where.length() == 0 ? WHERE : AND) + " upper(zaiav.rn_doc) like :zaiavRnDoc ");
				params.put("zaiavRnDoc", "%" + t + "%");
			}
		}

		if (this.zaiavDateFrom != null) {
			joinZaiav = true;
			where.append((where.length() == 0 ? WHERE : AND) + " zaiav.doc_date >= :zaiavDateFrom ");
			params.put("zaiavDateFrom", DateUtils.startDate(this.zaiavDateFrom));
		}
		if (this.zaiavDateTo != null) {
			joinZaiav = true;
			where.append((where.length() == 0 ? WHERE : AND) + " zaiav.doc_date <= :zaiavDateTo ");
			params.put("zaiavDateTo", DateUtils.endDate(this.zaiavDateTo));
		}

		if (this.zaiavStatusList != null && !this.zaiavStatusList.isEmpty()) {
			joinZaiav = true;
			where.append((where.length() == 0 ? WHERE : AND) + " zaiav.status in (:zaiavStatusList) ");
			params.put("zaiavStatusList", this.zaiavStatusList);
		}
		if (this.zaiavStatusDateFrom != null) {
			joinZaiav = true;
			where.append((where.length() == 0 ? WHERE : AND) + " zaiav.status_date >= :zaiavStatusDateFrom ");
			params.put("zaiavStatusDateFrom", DateUtils.startDate(this.zaiavStatusDateFrom));
		}
		if (this.zaiavStatusDateTo != null) {
			joinZaiav = true;
			where.append((where.length() == 0 ? WHERE : AND) + " zaiav.status_date <= :zaiavStatusDateTo ");
			params.put("zaiavStatusDateTo", DateUtils.endDate(this.zaiavStatusDateTo));
		}

		if (joinZaiav) {
			from.append(" left outer join vpisvane_doc vz on vz.id_vpisvane = v.id ");
			from.append(" left outer join doc zaiav on zaiav.doc_id = vz.id_doc and zaiav.doc_type = :docTypeIn ");
			params.put("docTypeIn", BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN);
		}

//		//
//		// Критерии по Лицензиант - ТЕКСТ - за сега няма да се използва
//		t = trimToNULL(this.licenziant);
//		if (t != null) {
//			StringBuilder sb = new StringBuilder();
//			String[] words = t.split(" ");
//
//			for (String word : words) {
//				word = word.trim();
//
//				if (word.endsWith("*")) {
//					word = word.substring(0, word.length() - 1) + ":*";
//				}
//				if (word.startsWith("*")) {
////					word = word.substring(0, word.length() - 1) + ":*";
//				}
//
//				if (sb.length() > 0) {
//					sb.append(" & ");
//				}
//				sb.append(word);
//			}
//			where.append((where.length() == 0 ? WHERE : AND) + " to_tsvector('bulgarian', v.licenziant) @@ to_tsquery('bulgarian','" + sb + "') ");
//		}

		//
		// Критерии по УД
		boolean joinUd = false; // само ако има търсене по данни за УД

		if (this.udDocVidList != null && !this.udDocVidList.isEmpty()) {
			joinUd = true;
			where.append((where.length() == 0 ? WHERE : AND) + " ud.doc_vid in (:udDocVidList) ");
			params.put("udDocVidList", this.udDocVidList);
		}

		t = trimToNULL_Upper(this.udRnDoc);
		if (t != null) {
			joinUd = true;
			if (this.udRnDocEQ) { // пълно съвпадение case insensitive
				where.append((where.length() == 0 ? WHERE : AND) + " upper(ud.rn_doc) = :udRnDoc ");
				params.put("udRnDoc", t);

			} else {
				where.append((where.length() == 0 ? WHERE : AND) + " upper(ud.rn_doc) like :udRnDoc ");
				params.put("udRnDoc", "%" + t + "%");
			}
		}

		if (this.udDateFrom != null) {
			joinUd = true;
			where.append((where.length() == 0 ? WHERE : AND) + " ud.doc_date >= :udDateFrom ");
			params.put("udDateFrom", DateUtils.startDate(this.udDateFrom));
		}
		if (this.udDateTo != null) {
			joinUd = true;
			where.append((where.length() == 0 ? WHERE : AND) + " ud.doc_date <= :udDateTo ");
			params.put("udDateTo", DateUtils.endDate(this.udDateTo));
		}

		if (this.udValidList != null && !this.udValidList.isEmpty()) {
			joinUd = true;
			where.append((where.length() == 0 ? WHERE : AND) + " ud.valid in (:udValidList) ");
			params.put("udValidList", this.udValidList);
		}
		if (this.udValidDateFrom != null) {
			joinUd = true;
			where.append((where.length() == 0 ? WHERE : AND) + " ud.valid_date >= :udValidDateFrom ");
			params.put("udValidDateFrom", DateUtils.startDate(this.udValidDateFrom));
		}
		if (this.udValidDateTo != null) {
			joinUd = true;
			where.append((where.length() == 0 ? WHERE : AND) + " ud.valid_date <= :udValidDateTo ");
			params.put("udValidDateTo", DateUtils.endDate(this.udValidDateTo));
		}

		if (joinUd) {
			from.append(" left outer join vpisvane_doc vud on vud.id_vpisvane = v.id ");
			from.append(" left outer join doc ud on ud.doc_id = vud.id_doc and ud.doc_type = :docTypeOwn ");
			params.put("docTypeOwn", BabhConstants.CODE_ZNACHENIE_DOC_TYPE_OWN);
		}

		// иска се щом няма избрана роля, да се търси във всички роли, което прави много криви нещата !!!
		boolean liceNoRoleSelected = !this.licenziant && !this.predstLice && !this.obektLice;

		//
		// Критерии в зависимост от областа на контрол

		if (this.oblast.equals(BabhConstants.REGISTER_ZJ)) {
			buildJivotni(from, where, params, this.obektLice || liceNoRoleSelected, systemData);

		} else if (this.oblast.equals(BabhConstants.REGISTER_VLP)) {
			buildVlp(from, where, params, this.obektLice || liceNoRoleSelected, systemData);

		} else if (this.oblast.equals(BabhConstants.REGISTER_KF)) {
			buildFuraji(from, where, params, this.obektLice || liceNoRoleSelected, systemData);

		} else {
			throw new InvalidParameterException("Непозната област на контрол: " + this.oblast + ".");
		}

		//
		// Критерии по Лица
		boolean joinLice = false;

		if (this.licenziant) {
			joinLice = true;
			where.append((where.length() == 0 ? WHERE : AND) + " ( lice.code = v.code_ref_vpisvane or ( lice.code = v.id_licenziant and v.licenziant_type = :licTypeLice ) ) ");
			params.put("licTypeLice", BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_LICE);
		}
		if (this.predstLice) {
			joinLice = true;
			where.append((where.length() == 0 ? WHERE : AND) + " lice.code = d.code_ref_corresp ");

			if (this.kachestvoPredstLiceList != null && !this.kachestvoPredstLiceList.isEmpty()) {
				where.append((where.length() == 0 ? WHERE : AND) + " d.kachestvo_lice in (:kachestvoPredstLiceList) ");
				params.put("kachestvoPredstLiceList", this.kachestvoPredstLiceList);
			}
		}
		if (this.obektLice) {
			joinLice = true;
			where.append((where.length() == 0 ? WHERE : AND) + " lice.code = odlica.code_ref ");

			if (this.kachestvoObektLiceList != null && !this.kachestvoObektLiceList.isEmpty()) {
				where.append((where.length() == 0 ? WHERE : AND) + " odlica.role in (:kachestvoObektLiceList) ");
				params.put("kachestvoObektLiceList", this.kachestvoObektLiceList);
			}
		}

		if (liceNoRoleSelected) { // ако никое не е цъкнато
			joinLice = true;
			where.append((where.length() == 0 ? WHERE : AND) + " ( ");
			where.append(" ( lice.code = v.code_ref_vpisvane or ( lice.code = v.id_licenziant and v.licenziant_type = :licTypeLice ) ) ");
			where.append(" or lice.code = d.code_ref_corresp ");
			where.append(" or lice.code = odlica.code_ref ");
			where.append(" ) ");
			params.put("licTypeLice", BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_LICE);
		}

		if (joinLice) {
			from.append(" left outer join adm_referents lice on 1=1 "); // в околните части на кодя се гарантира, че няма да има мега размножаване

			if (this.obektLice || liceNoRoleSelected) { // obekt.id идва от Критерии за област на контрол
				from.append(" left outer join obekt_deinost_lica odlica on odlica.obekt_deinost_id = obekt.id ");
			}

			t = SearchUtils.trimToNULL(this.fzlEgn);
			if (t != null) {
				where.append((where.length() == 0 ? WHERE : AND) + " lice.fzl_egn = :fzlEgn ");
				params.put("fzlEgn", t);
			}
			t = SearchUtils.trimToNULL(this.fzlLnc);
			if (t != null) {
				where.append((where.length() == 0 ? WHERE : AND) + " lice.fzl_lnc = :fzlLnc ");
				params.put("fzlLnc", t);
			}
			t = SearchUtils.trimToNULL(this.nflEik);
			if (t != null) {
				where.append((where.length() == 0 ? WHERE : AND) + " lice.nfl_eik = :nflEik ");
				params.put("nflEik", t);
			}
			t = SearchUtils.trimToNULL_Upper(this.nflIdentNom);
			if (t != null) {
				where.append((where.length() == 0 ? WHERE : AND) + " upper(lice.fzl_ln_es) = :nflIdentNom ");
				params.put("nflIdentNom", t);
			}

			t = SearchUtils.trimToNULL_Upper(this.refNameBgEn);
			if (t != null) {
				where.append((where.length() == 0 ? WHERE : AND) + " ( upper(lice.ref_name) like :refNameBgEn or upper(lice.ref_latin) like :refNameBgEn ) ");
				params.put("refNameBgEn", "%" + t + "%");
			}

			t = SearchUtils.trimToNULL_Upper(this.fzlNomDoc);
			if (t != null) {
				from.append(" left outer join adm_ref_doc refdoc on refdoc.code_ref = lice.code ");
				where.append((where.length() == 0 ? WHERE : AND) + " upper(refdoc.nom_doc) = :fzlNomDoc ");
				params.put("fzlNomDoc", t);
			}

			t = SearchUtils.trimToNULL_Upper(this.refAddress);
			if (t != null || this.refCountry != null || this.refEkatte != null) {
				from.append(" left outer join adm_ref_addrs refaddr on refaddr.code_ref = lice.code ");

				if (t != null) {
					where.append((where.length() == 0 ? WHERE : AND) + " upper(refaddr.addr_text) like :refAddress ");
					params.put("refAddress", "%" + t + "%");
				}
				if (this.refCountry != null) {
					where.append((where.length() == 0 ? WHERE : AND) + " refaddr.addr_country = :refCountry ");
					params.put("refCountry", this.refCountry);
				}
				if (this.refEkatte != null) {
					if (this.refEkatte.intValue() < 100000) { // търсене по населено място
						where.append(" and refaddr.ekatte = :refEkatte ");
						params.put("refEkatte", this.refEkatte);

					} else { // търсене по област или община
						SystemClassif item = systemData.decodeItemLite(CODE_CLASSIF_EKATTE, this.refEkatte, SysConstants.CODE_LANG_BG, null, false);
						String ekatteExtCode = item != null ? item.getCodeExt() : null; // ще се търси по външния код ако се открие

						String col = null;
						if (ekatteExtCode != null && ekatteExtCode.length() == 3) { // област
							col = "oblast";

						} else if (ekatteExtCode != null && ekatteExtCode.length() == 5) { // община
							col = "obstina";
						}

						if (col != null) {
							from.append(" left outer join ekatte_att refatt on refatt.ekatte = refaddr.ekatte and refatt.date_ot <= refaddr.date_reg and refatt.date_do > refaddr.date_reg ");
							where.append(" and refatt." + col + " = :refattCodeExt ");
							params.put("refattCodeExt", ekatteExtCode);
						}
					}
				}
			}
		}

		setSqlCount(" select count(distinct v.id) " + from + where);

		// това не влияе на count-а и за това е тук
		from.append(" left outer join adm_referents zaiavitel on v.code_ref_vpisvane = zaiavitel.code ");

		setSql("" + select + from + where);
		setSqlParameters(params);
	}

	/** @return the furNomerMps */
	public String getFurNomerMps() {
		return this.furNomerMps;
	}

	/** @return the furObektAddress */
	public String getFurObektAddress() {
		return this.furObektAddress;
	}

	/** @return the furObektCountry */
	public Integer getFurObektCountry() {
		return this.furObektCountry;
	}

	/** @return the furObektEkatte */
	public Integer getFurObektEkatte() {
		return this.furObektEkatte;
	}

	/** @return the furObektRegNom */
	public String getFurObektRegNom() {
		return this.furObektRegNom;
	}

	/** @return the furSklad */
	public Integer getFurSklad() {
		return this.furSklad;
	}

	/** @return the furVidDeinList */
	public List<Integer> getFurVidDeinList() {
		return this.furVidDeinList;
	}

	/** @return the furVidFurajList */
	public List<Integer> getFurVidFurajList() {
		return this.furVidFurajList;
	}

	/** @return the furVidJivotniList */
	public List<Integer> getFurVidJivotniList() {
		return this.furVidJivotniList;
	}

	/** @return the fzlEgn */
	public String getFzlEgn() {
		return this.fzlEgn;
	}

	/** @return the fzlLnc */
	public String getFzlLnc() {
		return this.fzlLnc;
	}

	/** @return the fzlNomDoc */
	public String getFzlNomDoc() {
		return this.fzlNomDoc;
	}

	/** @return the jivNomerMps */
	public String getJivNomerMps() {
		return this.jivNomerMps;
	}

	/** @return the jivObektAddress */
	public String getJivObektAddress() {
		return this.jivObektAddress;
	}

	/** @return the jivObektCountry */
	public Integer getJivObektCountry() {
		return this.jivObektCountry;
	}

	/** @return the jivObektEkatte */
	public Integer getJivObektEkatte() {
		return this.jivObektEkatte;
	}

	/** @return the jivObektNaimenovanie */
	public String getJivObektNaimenovanie() {
		return this.jivObektNaimenovanie;
	}

	/** @return the jivObektRegNom */
	public String getJivObektRegNom() {
		return this.jivObektRegNom;
	}

	/** @return the jivObektRegNomerStar */
	public String getJivObektRegNomerStar() {
		return this.jivObektRegNomerStar;
	}

	/** @return the jivPrednJivOezList */
	public List<Integer> getJivPrednJivOezList() {
		return this.jivPrednJivOezList;
	}

	/** @return the jivTehnologiaList */
	public List<Integer> getJivTehnologiaList() {
		return this.jivTehnologiaList;
	}

	/** @return the jivVidDeinList */
	public List<Integer> getJivVidDeinList() {
		return this.jivVidDeinList;
	}

	/** @return the jivVidIdentifList */
	public List<Integer> getJivVidIdentifList() {
		return this.jivVidIdentifList;
	}

	/** @return the jivVidJivotniList */
	public List<Integer> getJivVidJivotniList() {
		return this.jivVidJivotniList;
	}

	/** @return the kachestvoObektLiceList */
	public List<Integer> getKachestvoObektLiceList() {
		return this.kachestvoObektLiceList;
	}

	/** @return the kachestvoPredstLiceList */
	public List<Integer> getKachestvoPredstLiceList() {
		return this.kachestvoPredstLiceList;
	}

	/** @return the nflEik */
	public String getNflEik() {
		return this.nflEik;
	}

	/** @return the nflIdentNom */
	public String getNflIdentNom() {
		return this.nflIdentNom;
	}

	/** @return the oblast */
	public Integer getOblast() {
		return this.oblast;
	}

	/** @return the oezVidList */
	public List<Integer> getOezVidList() {
		return this.oezVidList;
	}

	/** @return the refAddress */
	public String getRefAddress() {
		return this.refAddress;
	}

	/** @return the refCountry */
	public Integer getRefCountry() {
		return this.refCountry;
	}

	/** @return the refEkatte */
	public Integer getRefEkatte() {
		return this.refEkatte;
	}

	/** @return the refNameBgEn */
	public String getRefNameBgEn() {
		return this.refNameBgEn;
	}

	/** @return the registerIdList */
	public List<Integer> getRegisterIdList() {
		return this.registerIdList;
	}

	/** @return the registraturaIdList */
	public List<Integer> getRegistraturaIdList() {
		return this.registraturaIdList;
	}

	/** @return the udDateFrom */
	public Date getUdDateFrom() {
		return this.udDateFrom;
	}

	/** @return the udDateTo */
	public Date getUdDateTo() {
		return this.udDateTo;
	}

	/** @return the udDocVidList */
	public List<Integer> getUdDocVidList() {
		return this.udDocVidList;
	}

	/** @return the udRnDoc */
	public String getUdRnDoc() {
		return this.udRnDoc;
	}

	/** @return the udValidDateFrom */
	public Date getUdValidDateFrom() {
		return this.udValidDateFrom;
	}

	/** @return the udValidDateTo */
	public Date getUdValidDateTo() {
		return this.udValidDateTo;
	}

	/** @return the udValidList */
	public List<Integer> getUdValidList() {
		return this.udValidList;
	}

	/** @return the vlpAktVeshtva */
	public String getVlpAktVeshtva() {
		return this.vlpAktVeshtva;
	}

	/** @return the vlpFarmFormList */
	public List<Integer> getVlpFarmFormList() {
		return this.vlpFarmFormList;
	}

	/** @return the vlpNaimenovanie */
	public String getVlpNaimenovanie() {
		return this.vlpNaimenovanie;
	}

	/** @return the vlpObektAddress */
	public String getVlpObektAddress() {
		return this.vlpObektAddress;
	}

	/** @return the vlpObektCountry */
	public Integer getVlpObektCountry() {
		return this.vlpObektCountry;
	}

	/** @return the vlpObektEkatte */
	public Integer getVlpObektEkatte() {
		return this.vlpObektEkatte;
	}

	/** @return the vlpObektNaimenovanie */
	public String getVlpObektNaimenovanie() {
		return this.vlpObektNaimenovanie;
	}

	/** @return the vlpOpakovkaList */
	public List<Integer> getVlpOpakovkaList() {
		return this.vlpOpakovkaList;
	}

	/** @return the vlpPomVeshtva */
	public String getVlpPomVeshtva() {
		return this.vlpPomVeshtva;
	}

	/** @return the vlpPredmetFarmakolList */
	public List<Integer> getVlpPredmetFarmakolList() {
		return this.vlpPredmetFarmakolList;
	}

	/** @return the vlpPredmetPrvnosList */
	public List<Integer> getVlpPredmetPrvnosList() {
		return this.vlpPredmetPrvnosList;
	}

	/** @return the vlpPrednObektList */
	public List<Integer> getVlpPrednObektList() {
		return this.vlpPrednObektList;
	}

	/** @return the vlpVidDeinList */
	public List<Integer> getVlpVidDeinList() {
		return this.vlpVidDeinList;
	}

	/** @return the vlzVidList */
	public List<Integer> getVlzVidList() {
		return this.vlzVidList;
	}

	/** @return the vpStatusDateFrom */
	public Date getVpStatusDateFrom() {
		return this.vpStatusDateFrom;
	}

	/** @return the vpStatusDateTo */
	public Date getVpStatusDateTo() {
		return this.vpStatusDateTo;
	}

	/** @return the vpStatusList */
	public List<Integer> getVpStatusList() {
		return this.vpStatusList;
	}

	/** @return the zaiavDateFrom */
	public Date getZaiavDateFrom() {
		return this.zaiavDateFrom;
	}

	/** @return the zaiavDateTo */
	public Date getZaiavDateTo() {
		return this.zaiavDateTo;
	}

	/** @return the zaiavDocVidList */
	public List<Integer> getZaiavDocVidList() {
		return this.zaiavDocVidList;
	}

	/** @return the zaiavRnDoc */
	public String getZaiavRnDoc() {
		return this.zaiavRnDoc;
	}

	/** @return the zaiavStatusDateFrom */
	public Date getZaiavStatusDateFrom() {
		return this.zaiavStatusDateFrom;
	}

	/** @return the zaiavStatusDateTo */
	public Date getZaiavStatusDateTo() {
		return this.zaiavStatusDateTo;
	}

	/** @return the zaiavStatusList */
	public List<Integer> getZaiavStatusList() {
		return this.zaiavStatusList;
	}

	/** @return the licenziant */
	public boolean isLicenziant() {
		return this.licenziant;
	}

	/** @return the obektLice */
	public boolean isObektLice() {
		return this.obektLice;
	}

	/** @return the predstLice */
	public boolean isPredstLice() {
		return this.predstLice;
	}

	/** @return the udRnDocEQ */
	public boolean isUdRnDocEQ() {
		return this.udRnDocEQ;
	}

	/** @return the zaiavRnDocEQ */
	public boolean isZaiavRnDocEQ() {
		return this.zaiavRnDocEQ;
	}

	/** @param furNomerMps the furNomerMps to set */
	public void setFurNomerMps(String furNomerMps) {
		this.furNomerMps = furNomerMps;
	}

	/** @param furObektAddress the furObektAddress to set */
	public void setFurObektAddress(String furObektAddress) {
		this.furObektAddress = furObektAddress;
	}

	/** @param furObektCountry the furObektCountry to set */
	public void setFurObektCountry(Integer furObektCountry) {
		this.furObektCountry = furObektCountry;
	}

	/** @param furObektEkatte the furObektEkatte to set */
	public void setFurObektEkatte(Integer furObektEkatte) {
		this.furObektEkatte = furObektEkatte;
	}

	/** @param furObektRegNom the furObektRegNom to set */
	public void setFurObektRegNom(String furObektRegNom) {
		this.furObektRegNom = furObektRegNom;
	}

	/** @param furSklad the furSklad to set */
	public void setFurSklad(Integer furSklad) {
		this.furSklad = furSklad;
	}

	/** @param furVidDeinList the furVidDeinList to set */
	public void setFurVidDeinList(List<Integer> furVidDeinList) {
		this.furVidDeinList = furVidDeinList;
	}

	/** @param furVidFurajList the furVidFurajList to set */
	public void setFurVidFurajList(List<Integer> furVidFurajList) {
		this.furVidFurajList = furVidFurajList;
	}

	/** @param furVidJivotniList the furVidJivotniList to set */
	public void setFurVidJivotniList(List<Integer> furVidJivotniList) {
		this.furVidJivotniList = furVidJivotniList;
	}

	/** @param fzlEgn the fzlEgn to set */
	public void setFzlEgn(String fzlEgn) {
		this.fzlEgn = fzlEgn;
	}

	/** @param fzlLnc the fzlLnc to set */
	public void setFzlLnc(String fzlLnc) {
		this.fzlLnc = fzlLnc;
	}

	/** @param fzlNomDoc the fzlNomDoc to set */
	public void setFzlNomDoc(String fzlNomDoc) {
		this.fzlNomDoc = fzlNomDoc;
	}

	/** @param jivNomerMps the jivNomerMps to set */
	public void setJivNomerMps(String jivNomerMps) {
		this.jivNomerMps = jivNomerMps;
	}

	/** @param jivObektAddress the jivObektAddress to set */
	public void setJivObektAddress(String jivObektAddress) {
		this.jivObektAddress = jivObektAddress;
	}

	/** @param jivObektCountry the jivObektCountry to set */
	public void setJivObektCountry(Integer jivObektCountry) {
		this.jivObektCountry = jivObektCountry;
	}

	/** @param jivObektEkatte the jivObektEkatte to set */
	public void setJivObektEkatte(Integer jivObektEkatte) {
		this.jivObektEkatte = jivObektEkatte;
	}

	/** @param jivObektNaimenovanie the jivObektNaimenovanie to set */
	public void setJivObektNaimenovanie(String jivObektNaimenovanie) {
		this.jivObektNaimenovanie = jivObektNaimenovanie;
	}

	/** @param jivObektRegNom the jivObektRegNom to set */
	public void setJivObektRegNom(String jivObektRegNom) {
		this.jivObektRegNom = jivObektRegNom;
	}

	/** @param jivObektRegNomerStar the jivObektRegNomerStar to set */
	public void setJivObektRegNomerStar(String jivObektRegNomerStar) {
		this.jivObektRegNomerStar = jivObektRegNomerStar;
	}

	/** @param jivPrednJivOezList the jivPrednJivOezList to set */
	public void setJivPrednJivOezList(List<Integer> jivPrednJivOezList) {
		this.jivPrednJivOezList = jivPrednJivOezList;
	}

	/** @param jivTehnologiaList the jivTehnologiaList to set */
	public void setJivTehnologiaList(List<Integer> jivTehnologiaList) {
		this.jivTehnologiaList = jivTehnologiaList;
	}

	/** @param jivVidDeinList the jivVidDeinList to set */
	public void setJivVidDeinList(List<Integer> jivVidDeinList) {
		this.jivVidDeinList = jivVidDeinList;
	}

	/** @param jivVidIdentifList the jivVidIdentifList to set */
	public void setJivVidIdentifList(List<Integer> jivVidIdentifList) {
		this.jivVidIdentifList = jivVidIdentifList;
	}

	/** @param jivVidJivotniList the jivVidJivotniList to set */
	public void setJivVidJivotniList(List<Integer> jivVidJivotniList) {
		this.jivVidJivotniList = jivVidJivotniList;
	}

	/** @param kachestvoObektLiceList the kachestvoObektLiceList to set */
	public void setKachestvoObektLiceList(List<Integer> kachestvoObektLiceList) {
		this.kachestvoObektLiceList = kachestvoObektLiceList;
	}

	/** @param kachestvoPredstLiceList the kachestvoPredstLiceList to set */
	public void setKachestvoPredstLiceList(List<Integer> kachestvoPredstLiceList) {
		this.kachestvoPredstLiceList = kachestvoPredstLiceList;
	}

	/** @param licenziant the licenziant to set */
	public void setLicenziant(boolean licenziant) {
		this.licenziant = licenziant;
	}

	/** @param nflEik the nflEik to set */
	public void setNflEik(String nflEik) {
		this.nflEik = nflEik;
	}

	/** @param nflIdentNom the nflIdentNom to set */
	public void setNflIdentNom(String nflIdentNom) {
		this.nflIdentNom = nflIdentNom;
	}

	/** @param obektLice the obektLice to set */
	public void setObektLice(boolean obektLice) {
		this.obektLice = obektLice;
	}

	/** @param oblast the oblast to set */
	public void setOblast(Integer oblast) {
		this.oblast = oblast;
	}

	/** @param oezVidList the oezVidList to set */
	public void setOezVidList(List<Integer> oezVidList) {
		this.oezVidList = oezVidList;
	}

	/** @param predstLice the predstLice to set */
	public void setPredstLice(boolean predstLice) {
		this.predstLice = predstLice;
	}

	/** @param refAddress the refAddress to set */
	public void setRefAddress(String refAddress) {
		this.refAddress = refAddress;
	}

	/** @param refCountry the refCountry to set */
	public void setRefCountry(Integer refCountry) {
		this.refCountry = refCountry;
	}

	/** @param refEkatte the refEkatte to set */
	public void setRefEkatte(Integer refEkatte) {
		this.refEkatte = refEkatte;
	}

	/** @param refNameBgEn the refNameBgEn to set */
	public void setRefNameBgEn(String refNameBgEn) {
		this.refNameBgEn = refNameBgEn;
	}

	/** @param registerIdList the registerIdList to set */
	public void setRegisterIdList(List<Integer> registerIdList) {
		this.registerIdList = registerIdList;
	}

	/** @param registraturaIdList the registraturaIdList to set */
	public void setRegistraturaIdList(List<Integer> registraturaIdList) {
		this.registraturaIdList = registraturaIdList;
	}

	/** @param udDateFrom the udDateFrom to set */
	public void setUdDateFrom(Date udDateFrom) {
		this.udDateFrom = udDateFrom;
	}

	/** @param udDateTo the udDateTo to set */
	public void setUdDateTo(Date udDateTo) {
		this.udDateTo = udDateTo;
	}

	/** @param udDocVidList the udDocVidList to set */
	public void setUdDocVidList(List<Integer> udDocVidList) {
		this.udDocVidList = udDocVidList;
	}

	/** @param udRnDoc the udRnDoc to set */
	public void setUdRnDoc(String udRnDoc) {
		this.udRnDoc = udRnDoc;
	}

	/** @param udRnDocEQ the udRnDocEQ to set */
	public void setUdRnDocEQ(boolean udRnDocEQ) {
		this.udRnDocEQ = udRnDocEQ;
	}

	/** @param udValidDateFrom the udValidDateFrom to set */
	public void setUdValidDateFrom(Date udValidDateFrom) {
		this.udValidDateFrom = udValidDateFrom;
	}

	/** @param udValidDateTo the udValidDateTo to set */
	public void setUdValidDateTo(Date udValidDateTo) {
		this.udValidDateTo = udValidDateTo;
	}

	/** @param udValidList the udValidList to set */
	public void setUdValidList(List<Integer> udValidList) {
		this.udValidList = udValidList;
	}

	/** @param vlpAktVeshtva the vlpAktVeshtva to set */
	public void setVlpAktVeshtva(String vlpAktVeshtva) {
		this.vlpAktVeshtva = vlpAktVeshtva;
	}

	/** @param vlpFarmFormList the vlpFarmFormList to set */
	public void setVlpFarmFormList(List<Integer> vlpFarmFormList) {
		this.vlpFarmFormList = vlpFarmFormList;
	}

	/** @param vlpNaimenovanie the vlpNaimenovanie to set */
	public void setVlpNaimenovanie(String vlpNaimenovanie) {
		this.vlpNaimenovanie = vlpNaimenovanie;
	}

	/** @param vlpObektAddress the vlpObektAddress to set */
	public void setVlpObektAddress(String vlpObektAddress) {
		this.vlpObektAddress = vlpObektAddress;
	}

	/** @param vlpObektCountry the vlpObektCountry to set */
	public void setVlpObektCountry(Integer vlpObektCountry) {
		this.vlpObektCountry = vlpObektCountry;
	}

	/** @param vlpObektEkatte the vlpObektEkatte to set */
	public void setVlpObektEkatte(Integer vlpObektEkatte) {
		this.vlpObektEkatte = vlpObektEkatte;
	}

	/** @param vlpObektNaimenovanie the vlpObektNaimenovanie to set */
	public void setVlpObektNaimenovanie(String vlpObektNaimenovanie) {
		this.vlpObektNaimenovanie = vlpObektNaimenovanie;
	}

	/** @param vlpOpakovkaList the vlpOpakovkaList to set */
	public void setVlpOpakovkaList(List<Integer> vlpOpakovkaList) {
		this.vlpOpakovkaList = vlpOpakovkaList;
	}

	/** @param vlpPomVeshtva the vlpPomVeshtva to set */
	public void setVlpPomVeshtva(String vlpPomVeshtva) {
		this.vlpPomVeshtva = vlpPomVeshtva;
	}

	/** @param vlpPredmetFarmakolList the vlpPredmetFarmakolList to set */
	public void setVlpPredmetFarmakolList(List<Integer> vlpPredmetFarmakolList) {
		this.vlpPredmetFarmakolList = vlpPredmetFarmakolList;
	}

	/** @param vlpPredmetPrvnosList the vlpPredmetPrvnosList to set */
	public void setVlpPredmetPrvnosList(List<Integer> vlpPredmetPrvnosList) {
		this.vlpPredmetPrvnosList = vlpPredmetPrvnosList;
	}

	/** @param vlpPrednObektList the vlpPrednObektList to set */
	public void setVlpPrednObektList(List<Integer> vlpPrednObektList) {
		this.vlpPrednObektList = vlpPrednObektList;
	}

	/** @param vlpVidDeinList the vlpVidDeinList to set */
	public void setVlpVidDeinList(List<Integer> vlpVidDeinList) {
		this.vlpVidDeinList = vlpVidDeinList;
	}

	/** @param vlzVidList the vlzVidList to set */
	public void setVlzVidList(List<Integer> vlzVidList) {
		this.vlzVidList = vlzVidList;
	}

	/** @param vpStatusDateFrom the vpStatusDateFrom to set */
	public void setVpStatusDateFrom(Date vpStatusDateFrom) {
		this.vpStatusDateFrom = vpStatusDateFrom;
	}

	/** @param vpStatusDateTo the vpStatusDateTo to set */
	public void setVpStatusDateTo(Date vpStatusDateTo) {
		this.vpStatusDateTo = vpStatusDateTo;
	}

	/** @param vpStatusList the vpStatusList to set */
	public void setVpStatusList(List<Integer> vpStatusList) {
		this.vpStatusList = vpStatusList;
	}

	/** @param zaiavDateFrom the zaiavDateFrom to set */
	public void setZaiavDateFrom(Date zaiavDateFrom) {
		this.zaiavDateFrom = zaiavDateFrom;
	}

	/** @param zaiavDateTo the zaiavDateTo to set */
	public void setZaiavDateTo(Date zaiavDateTo) {
		this.zaiavDateTo = zaiavDateTo;
	}

	/** @param zaiavDocVidList the zaiavDocVidList to set */
	public void setZaiavDocVidList(List<Integer> zaiavDocVidList) {
		this.zaiavDocVidList = zaiavDocVidList;
	}

	/** @param zaiavRnDoc the zaiavRnDoc to set */
	public void setZaiavRnDoc(String zaiavRnDoc) {
		this.zaiavRnDoc = zaiavRnDoc;
	}

	/** @param zaiavRnDocEQ the zaiavRnDocEQ to set */
	public void setZaiavRnDocEQ(boolean zaiavRnDocEQ) {
		this.zaiavRnDocEQ = zaiavRnDocEQ;
	}

	/** @param zaiavStatusDateFrom the zaiavStatusDateFrom to set */
	public void setZaiavStatusDateFrom(Date zaiavStatusDateFrom) {
		this.zaiavStatusDateFrom = zaiavStatusDateFrom;
	}

	/** @param zaiavStatusDateTo the zaiavStatusDateTo to set */
	public void setZaiavStatusDateTo(Date zaiavStatusDateTo) {
		this.zaiavStatusDateTo = zaiavStatusDateTo;
	}

	/** @param zaiavStatusList the zaiavStatusList to set */
	public void setZaiavStatusList(List<Integer> zaiavStatusList) {
		this.zaiavStatusList = zaiavStatusList;
	}

	/**
	 * Тука са специфичните условия и заявки за Контрол на фуражи
	 */
	void buildFuraji(StringBuilder from, StringBuilder where, Map<String, Object> params, boolean joinObekt, BaseSystemData systemData) throws DbErrorException {
		boolean joinView = false;
		boolean joinMps = false;
		boolean joinPredmet = false;
		boolean joinPrednJiv = false;

		String t = trimToNULL_Upper(this.furObektRegNom);
		if (t != null) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " upper(v_fur.reg_nom) = :furObektRegNom ");
			params.put("furObektRegNom", t);
		}
		if (this.furSklad != null) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " v_fur.sklad = :furSklad ");
			params.put("furSklad", this.furSklad);
		}

		t = SearchUtils.trimToNULL_Upper(this.furObektAddress);
		if (t != null) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " upper(obekt.address) like :furObektAddress ");
			params.put("furObektAddress", "%" + t + "%");
		}
		if (this.furObektCountry != null) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " obekt.darj = :furObektCountry ");
			params.put("furObektCountry", this.furObektCountry);
		}
		if (this.furObektEkatte != null) {
			joinObekt = true; // само да се каже, че трябва обекта
		}

		if (this.furVidDeinList != null && !this.furVidDeinList.isEmpty()) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " v_fur.dein_vid in (:furVidDeinList) ");
			params.put("furVidDeinList", this.furVidDeinList);
		}

		if (this.furVidFurajList != null && !this.furVidFurajList.isEmpty()) {
			joinPredmet = true;
			where.append((where.length() == 0 ? WHERE : AND) + " predmet.vid in (:furVidFurajList) ");
			params.put("furVidFurajList", this.furVidFurajList);
		}
		if (this.furVidJivotniList != null && !this.furVidJivotniList.isEmpty()) {
			joinPrednJiv = true;
			where.append((where.length() == 0 ? WHERE : AND) + " prednjiv.vid_jiv in (:furVidJivotniList) ");
			params.put("furVidJivotniList", this.furVidJivotniList);
		}

		t = SearchUtils.trimToNULL_Upper(this.furNomerMps);
		if (t != null) {
			joinMps = true;
			where.append((where.length() == 0 ? WHERE : AND) + " upper(mps.nomer) = :furNomerMps ");
			params.put("furNomerMps", t);
		}

		if (joinView || joinObekt || joinMps || joinPredmet || joinPrednJiv) {
			from.append(" left outer join v_spec_report_fur v_fur on v_fur.vp_id = v.id ");

			if (joinObekt) {
				from.append(" left outer join obekt_deinost obekt on obekt.id = v_fur.od_id ");
			}
			if (joinMps) {
				from.append(" left outer join mps mps on mps.id = v_fur.mps_id ");
			}
			if (joinPredmet) {
				from.append(" left outer join event_deinost_furaji_predmet predmet on predmet.event_deinost_furaji_id = v_fur.dein_id ");
			}
			if (joinPrednJiv) {
				from.append(" left outer join event_deinost_furaji_prednazn_jiv prednjiv on prednjiv.event_deinost_furaji_id = v_fur.dein_id ");
			}
		}
		if (this.furObektEkatte != null) { // и накрая слагаме каквото трябва за екатте-то
			addObektEkatteCriteria(this.furObektEkatte, from, where, params, systemData);
		}
	}

	/**
	 * Тука са специфичните условия и заявки за Здравеопазване на животните
	 */
	void buildJivotni(StringBuilder from, StringBuilder where, Map<String, Object> params, boolean joinObekt, BaseSystemData systemData) throws DbErrorException {
		boolean joinView = false;
		boolean joinMps = false;

		if (this.oezVidList != null && !this.oezVidList.isEmpty()) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " obekt.vid_oez in (:oezVidList) ");
			params.put("oezVidList", this.oezVidList);
		}
		if (this.vlzVidList != null && !this.vlzVidList.isEmpty()) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " obekt.vid_vlz in (:vlzVidList) ");
			params.put("vlzVidList", this.vlzVidList);
		}

		String t = trimToNULL_Upper(this.jivObektNaimenovanie);
		if (t != null) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " upper(obekt.naimenovanie) like :jivObektNaimenovanie ");
			params.put("jivObektNaimenovanie", "%" + t + "%");
		}
		t = trimToNULL_Upper(this.jivObektRegNom);
		if (t != null) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " upper(obekt.reg_nom) = :jivObektRegNom ");
			params.put("jivObektRegNom", t);
		}
		t = trimToNULL_Upper(this.jivObektRegNomerStar);
		if (t != null) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " upper(obekt.reg_nomer_star) = :jivObektRegNomerStar ");
			params.put("jivObektRegNomerStar", t);
		}

		t = SearchUtils.trimToNULL_Upper(this.jivObektAddress);
		if (t != null) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " upper(obekt.address) like :jivObektAddress ");
			params.put("jivObektAddress", "%" + t + "%");
		}
		if (this.jivObektCountry != null) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " obekt.darj = :jivObektCountry ");
			params.put("jivObektCountry", this.jivObektCountry);
		}
		if (this.jivObektEkatte != null) {
			joinObekt = true; // само да се каже, че трябва обекта
		}

		if (this.jivVidDeinList != null && !this.jivVidDeinList.isEmpty()) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " v_jiv.dein_vid in (:jivVidDeinList) ");
			params.put("jivVidDeinList", this.jivVidDeinList);
		}
		if (this.jivVidIdentifList != null && !this.jivVidIdentifList.isEmpty()) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " v_jiv.vid_identif in (:jivVidIdentifList) ");
			params.put("jivVidIdentifList", this.jivVidIdentifList);
		}
		if (this.jivVidJivotniList != null && !this.jivVidJivotniList.isEmpty()) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " v_jiv.vid_jiv in (:jivVidJivotniList) ");
			params.put("jivVidJivotniList", this.jivVidJivotniList);
		}
		if (this.jivPrednJivOezList != null && !this.jivPrednJivOezList.isEmpty()) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " v_jiv.prednaznachenie in (:jivPrednJivOezList) ");
			params.put("jivPrednJivOezList", this.jivPrednJivOezList);
		}
		if (this.jivTehnologiaList != null && !this.jivTehnologiaList.isEmpty()) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " v_jiv.tehnologia in (:jivTehnologiaList) ");
			params.put("jivTehnologiaList", this.jivTehnologiaList);
		}

		t = SearchUtils.trimToNULL_Upper(this.jivNomerMps);
		if (t != null) {
			joinMps = true;
			where.append((where.length() == 0 ? WHERE : AND) + " upper(mps.nomer) = :jivNomerMps ");
			params.put("jivNomerMps", t);
		}

		if (joinView || joinObekt || joinMps) {
			from.append(" left outer join v_spec_report_jiv v_jiv on v_jiv.vp_id = v.id ");

			if (joinObekt) {
				from.append(" left outer join obekt_deinost obekt on obekt.id = v_jiv.od_id ");
			}
			if (joinMps) {
				from.append(" left outer join mps mps on mps.id = v_jiv.mps_id ");
			}
		}
		if (this.jivObektEkatte != null) { // и накрая слагаме каквото трябва за екатте-то
			addObektEkatteCriteria(this.jivObektEkatte, from, where, params, systemData);
		}
	}

	/**
	 * Тука са специфичните условия и заявки за Контрол на ВЛП
	 */
	void buildVlp(StringBuilder from, StringBuilder where, Map<String, Object> params, boolean joinObekt, BaseSystemData systemData) throws DbErrorException {
		boolean joinView = false;
		boolean joinObektPredn = false;
		boolean joinPredmet605 = false;
		boolean joinPredmet564 = false;
		boolean joinSubst = false;

		if (this.vlpVidDeinList != null && !this.vlpVidDeinList.isEmpty()) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " v_vlp.dein_vid in (:vlpVidDeinList) ");
			params.put("vlpVidDeinList", this.vlpVidDeinList);
		}

		String t = trimToNULL_Upper(this.vlpNaimenovanie);
		if (t != null) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " ( upper(v_vlp.naimenovanie_cyr) like :vlpNaimenovanie or upper(v_vlp.naimenovanie_lat) like :vlpNaimenovanie ) ");
			params.put("vlpNaimenovanie", "%" + t + "%");
		}

		if (this.vlpPredmetPrvnosList != null && !this.vlpPredmetPrvnosList.isEmpty()) {
			joinPredmet605 = true;
			where.append((where.length() == 0 ? WHERE : AND) + " predmet605.predmet in (:vlpPredmetPrvnosList) ");
			params.put("vlpPredmetPrvnosList", this.vlpPredmetPrvnosList);
		}
		if (this.vlpFarmFormList != null && !this.vlpFarmFormList.isEmpty()) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " v_vlp.farm_form in (:vlpFarmFormList) ");
			params.put("vlpFarmFormList", this.vlpFarmFormList);
		}
		if (this.vlpPredmetFarmakolList != null && !this.vlpPredmetFarmakolList.isEmpty()) {
			joinPredmet564 = true;
			where.append((where.length() == 0 ? WHERE : AND) + " predmet564.predmet in (:vlpPredmetFarmakolList) ");
			params.put("vlpPredmetFarmakolList", this.vlpPredmetFarmakolList);
		}
		if (this.vlpOpakovkaList != null && !this.vlpOpakovkaList.isEmpty()) {
			joinView = true;
			where.append((where.length() == 0 ? WHERE : AND) + " v_vlp.opakovka in (:vlpOpakovkaList) ");
			params.put("vlpOpakovkaList", this.vlpOpakovkaList);
		}

		StringBuilder substances = new StringBuilder();
		t = SearchUtils.trimToNULL_Upper(this.vlpAktVeshtva);
		if (t != null) {
			joinSubst = true;
			substances.append(" ( v_vlp.subst_type = :substTypeAkt and upper(subst.name) like :vlpAktVeshtva ) ");
			params.put("substTypeAkt", BabhConstants.CODE_ZNACHENIE_VID_VESHTESTVO_AKTIVNO);
			params.put("vlpAktVeshtva", "%" + t + "%");
		}
		t = SearchUtils.trimToNULL_Upper(this.vlpPomVeshtva);
		if (t != null) {
			joinSubst = true;
			if (substances.length() > 0) {
				substances.append(" or ");
			}
			substances.append(" ( v_vlp.subst_type = :substTypePom and upper(subst.name) like :vlpPomVeshtva ) ");
			params.put("substTypePom", BabhConstants.CODE_ZNACHENIE_VID_VESHTESTVO_POM);
			params.put("vlpPomVeshtva", "%" + t + "%");
		}
		if (substances.length() > 0) {
			where.append((where.length() == 0 ? WHERE : AND) + " ( " + substances + " ) ");
		}

		if (this.vlpPrednObektList != null && !this.vlpPrednObektList.isEmpty()) {
			joinObektPredn = true;
			where.append((where.length() == 0 ? WHERE : AND) + " obektpredn.prednaznachenie in (:vlpPrednObektList) ");
			params.put("vlpPrednObektList", this.vlpPrednObektList);
		}
		t = trimToNULL_Upper(this.vlpObektNaimenovanie);
		if (t != null) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " upper(obekt.naimenovanie) like :vlpObektNaimenovanie ");
			params.put("vlpObektNaimenovanie", "%" + t + "%");
		}

		t = SearchUtils.trimToNULL_Upper(this.vlpObektAddress);
		if (t != null) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " upper(obekt.address) like :vlpObektAddress ");
			params.put("vlpObektAddress", "%" + t + "%");
		}
		if (this.vlpObektCountry != null) {
			joinObekt = true;
			where.append((where.length() == 0 ? WHERE : AND) + " obekt.darj = :vlpObektCountry ");
			params.put("vlpObektCountry", this.vlpObektCountry);
		}
		if (this.vlpObektEkatte != null) {
			joinObekt = true; // само да се каже, че трябва обекта
		}

		if (joinView || joinObekt || joinObektPredn || joinPredmet605 || joinPredmet564 || joinSubst) {
			from.append(" left outer join v_spec_report_vlp v_vlp on v_vlp.vp_id = v.id ");

			if (joinObekt) {
				from.append(" left outer join obekt_deinost obekt on obekt.id = v_vlp.od_id ");
			}
			if (joinObektPredn) {
				from.append(" left outer join obekt_deinost_prednaznachenie obektpredn on obektpredn.obekt_deinost_id = v_vlp.od_id ");
			}

			if (joinPredmet605) {
				from.append(" left outer join event_deinost_vlp_predmet predmet605 on predmet605.id_event_deinost_vlp = v_vlp.dein_id ");
				from.append(" and predmet605.code_classif = :classif605 ");
				params.put("classif605", BabhConstants.CODE_CLASSIF_VID_DEIN_PROIZV_VNOS_VLP);
			}
			if (joinPredmet564) {
				from.append(" left outer join event_deinost_vlp_predmet predmet564 on predmet564.id_event_deinost_vlp = v_vlp.dein_id ");
				from.append(" and predmet564.code_classif = :classif564 ");
				params.put("classif564", BabhConstants.CODE_CLASSIF_PREDMET_TARGOVIA_EDRO_VLP);
			}

			if (joinSubst) {
				from.append(" left outer join substances subst on subst.identifier = v_vlp.vid_identifier ");
			}
		}
		if (this.vlpObektEkatte != null) { // и накрая слагаме каквото трябва за екатте-то
			addObektEkatteCriteria(this.vlpObektEkatte, from, where, params, systemData);
		}
	}

	private void addObektEkatteCriteria(int ekatte, StringBuilder from, StringBuilder where, Map<String, Object> params, BaseSystemData systemData) throws DbErrorException {
		if (ekatte < 100000) { // търсене по населено място
			where.append(" and obekt.nas_mesto = :obektEkatte ");
			params.put("obektEkatte", ekatte);

		} else { // търсене по област или община
			SystemClassif item = systemData.decodeItemLite(CODE_CLASSIF_EKATTE, ekatte, SysConstants.CODE_LANG_BG, null, false);
			String ekatteExtCode = item != null ? item.getCodeExt() : null; // ще се търси по външния код ако се открие

			String col = null;
			if (ekatteExtCode != null && ekatteExtCode.length() == 3) { // област
				col = "oblast";

			} else if (ekatteExtCode != null && ekatteExtCode.length() == 5) { // община
				col = "obstina";
			}

			if (col != null) {
				from.append(" left outer join ekatte_att obektatt on obektatt.ekatte = obekt.nas_mesto and obektatt.date_ot <= obekt.date_reg and obektatt.date_do > obekt.date_reg ");
				where.append(" and obektatt." + col + " = :obektattCodeExt ");
				params.put("obektattCodeExt", ekatteExtCode);
			}
		}
	}
}
