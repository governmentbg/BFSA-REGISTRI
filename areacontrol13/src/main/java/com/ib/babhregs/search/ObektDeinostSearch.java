package com.ib.babhregs.search;

import static com.ib.system.SysConstants.CODE_CLASSIF_EKATTE;
import static com.ib.system.SysConstants.CODE_DEFAULT_LANG;
import static com.ib.system.utils.SearchUtils.trimToNULL_Upper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.BaseSystemData;
import com.ib.system.db.SelectMetadata;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;

/**
 * Търсене на обект на дейност.
 *
 * @see #buildQuery()
 * @author dessy
 */
public class ObektDeinostSearch extends SelectMetadata {

	/**  */
	private static final long serialVersionUID = 5798543069896050423L;

	/**  */
	private static final Logger LOGGER = LoggerFactory.getLogger(ObektDeinostSearch.class);

	/** вид обект на дейност - задължително*/
	private Integer	vid;
	
	/** Рег.номер */
	private String regNom; 
	
	/** Рег.номер стар */
	private String regNomerStar; 
	
	/** наименование */
	private String	naimenovanie;

	private Integer	country;
	
	private Integer	ekatte;			// местоположение - код от класификация
	
	/** има смисъл само ако елемента е област или община */
	private String	ekatteExtCode;	// местоположение – външен код

	/** датата към която се търси. ако не се въведе се работи с днешна дата */
	private Date date;

	private Boolean registered;
	
	/**  */
	public ObektDeinostSearch() {
		super();
	}

	/**
	 * На база входните параметри подготвя селект за получаване на резултат от вида: <br>
	 * [0]-ID<br>
	 * [1]-VID<br>
	 * [2]-REG_NOM<br>
	 * [3] NAIMENOVANIE<br>		 
	 * [4]-DARJ<br>
	 * [5]-TVM<br>
	 * [6]-MIASTO_IME<br>
	 * [7]-OBSTINA_IME<br>
	 * [8]-OBLAST_IME<br>
	 * [9]-ADDRESS<br>
	 * [10]-TEL<br>
	 * [11]-EMAIL<br>
	 * [12]-vpisvane.status - като може и да е NULL</br>	   
	 * [13]-REG_NOMER_STAR<br>
	 *
	 * @see #calcEkatte(BaseSystemData)
	 */
	@Override
	public void buildQuery() {		
		if (this.vid == null) {
			throw new IllegalArgumentException("Argument vid is required!");
		}
		
		Map<String, Object> params = new HashMap<>();

		StringBuilder select = new StringBuilder();
		select.append(" select distinct od.ID 			a0 ");
		select.append(" , od.VID 			    a1 ");		
		select.append(", od.REG_NOM 			a2 ");
		select.append(", od.NAIMENOVANIE 		a3 ");	
		select.append(", od.darj 		 		a4 ");		
		select.append(", att.TVM 				a5 ");
		select.append(", att.IME 				a6 ");
		select.append(", att.OBSTINA_IME 		a7 ");
		select.append(", att.OBLAST_IME 		a8 ");
		select.append(", od.ADDRESS				a9 ");
		select.append(", od.TEL					a10 ");
		select.append(", od.EMAIL	  			a11 ");
		select.append(", v.status	  			a12 ");
		select.append(", od.REG_NOMER_STAR		a13 ");
		

		StringBuilder from = new StringBuilder(" from OBEKT_DEINOST od ");
		
		from.append(" left outer join EKATTE_ATT att on att.EKATTE = od.NAS_MESTO and att.DATE_OT <= :dateArg and att.DATE_DO > :dateArg ");

		from.append(" left outer join vpisvane v on v.id_licenziant = od.id and v.licenziant_type = :licType ");
		int licType = -1;
		if (this.vid.intValue() == BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ) {
			licType = BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_OEZ;
			
		} else if (this.vid.intValue() == BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLZ) {
			licType = BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ;
			
		} else { // така няма да се върне вписване и все едно обекта няма вписване и статуса ще е NULL
			LOGGER.error("!!! required attribute vid={} is unknown !!! ", this.vid);
		}
		params.put("licType", licType);

		Date dateArg = DateUtils.startDate(this.date == null ? new Date() : this.date);
		params.put("dateArg", dateArg);

		StringBuilder where = new StringBuilder(" where od.VID = :vid ");
		params.put("vid", this.vid);

		
		String regNo = trimToNULL_Upper(this.regNom); 
		if (regNo != null) {			
			where.append(" and upper(od.REG_NOM) like :regNom ");
			params.put("regNom", "%" + regNo + "%");
			
		}
		
		String regNomStar = trimToNULL_Upper(this.regNomerStar); 
		if (regNomStar != null) {			
			where.append(" and upper(od.REG_NOMER_STAR) like :regNomerStar ");
			params.put("regNomerStar", "%" + regNomStar + "%");
			
		}
		
		String naim = trimToNULL_Upper(this.naimenovanie); 
		if (naim != null) {			
			where.append(" and upper(od.NAIMENOVANIE) like :naimenovanie ");
			params.put("naimenovanie", "%" + naim + "%");
			
		}
		
		if (this.ekatte != null) {
			if (this.ekatte.intValue() < 100000) { // търсене по населено място
				where.append(" and od.NAS_MESTO = :ekatte ");
				params.put("ekatte", this.ekatte);

			} else if (this.ekatteExtCode != null) { // търсене по област или община

				String col = null;
				if (this.ekatteExtCode.length() == 3) { // област
					col = "OBLAST";
				} else if (this.ekatteExtCode.length() == 5) { // община
					col = "OBSTINA";
				}

				if (col != null) {
					where.append(" and att." + col + " = :codeExt ");
					params.put("codeExt", this.ekatteExtCode);
				}
			}
		}
		
		if (this.country != null) {
			where.append(" and od.darj = :country ");
			params.put("country", this.country);
		}
		
		if (this.registered != null) {
			if (this.registered.booleanValue()) { // само регистрирани
				where.append(" and od.REG_NOM is not null and od.REG_NOM != '' ");
			} else { // само нерегистрирани
				where.append(" and (od.REG_NOM is null or od.REG_NOM = '') ");
			}
		}
		
		setSql("" + select + from + where);
		setSqlCount(" select count(distinct od.ID) " + from + where);
		setSqlParameters(params);
	}

	/**
	 * Изчислява стойността на екатте за да може да се търси по област/община/нас.място
	 *
	 * @param sd
	 * @throws DbErrorException
	 */
	public void calcEkatte(BaseSystemData sd) throws DbErrorException {
		if (this.ekatte != null) { // има нещо

			if (this.ekatte.intValue() < 100000) { // населено място
				this.ekatteExtCode = null; // ще се търси по кода от класификацията

			} else { // обл/общ
				SystemClassif item = sd.decodeItemLite(CODE_CLASSIF_EKATTE, this.ekatte, CODE_DEFAULT_LANG, this.date, false);
				this.ekatteExtCode = item != null ? item.getCodeExt() : null; // ще се търси по външния код ако се открие
			}

		} else { // изчистено е и няма как да се търси
			this.ekatteExtCode = null;
		}
	}

	/** @return the vid */
	public Integer getVid() {
		return vid;
	}

	/** @param vid the vid to set */
	public void setVid(Integer vid) {
		this.vid = vid;
	}

	/** @return the regNom */
	public String getRegNom() {
		return regNom;
	}

	/** @param regNom the regNom to set */
	public void setRegNom(String regNom) {
		this.regNom = regNom;
	}

	/** @return the regNomerStar */
	public String getRegNomerStar() {
		return regNomerStar;
	}

	/** @param regNomerStar the regNomerStar to set */
	public void setRegNomerStar(String regNomerStar) {
		this.regNomerStar = regNomerStar;
	}

	/** @return the naimenovanie */
	public String getNaimenovanie() {
		return naimenovanie;
	}

	/** @param naimenovanie the naimenovanie to set */
	public void setNaimenovanie(String naimenovanie) {
		this.naimenovanie = naimenovanie;
	}

	/** @return the country */
	public Integer getCountry() {
		return country;
	}

	/** @param country the country to set */
	public void setCountry(Integer country) {
		this.country = country;
	}

	/** @return the ekatte */
	public Integer getEkatte() {
		return ekatte;
	}

	/** @param ekatte the ekatte to set */
	public void setEkatte(Integer ekatte) {
		this.ekatte = ekatte;
	}	
	
	/** @return the ekatteExtCode */
	public String getEkatteExtCode() {
		return ekatteExtCode;
	}

	/** @param ekatteExtCode the ekatteExtCode to set */
	public void setEkatteExtCode(String ekatteExtCode) {
		this.ekatteExtCode = ekatteExtCode;
	}

	/** @return the date */
	public Date getDate() {
		return date;
	}

	/** @param date the date to set */
	public void setDate(Date date) {
		this.date = date;
	}

	/** @return the registered */
	public Boolean getRegistered() {
		return this.registered;
	}
	/** @param registered the registered to set */
	public void setRegistered(Boolean registered) {
		this.registered = registered;
	}
}