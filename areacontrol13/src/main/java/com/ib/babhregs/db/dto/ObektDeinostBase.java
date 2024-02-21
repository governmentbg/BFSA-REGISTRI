package com.ib.babhregs.db.dto;

import static com.ib.system.utils.SearchUtils.trimToNULL;
import static javax.persistence.GenerationType.SEQUENCE;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.system.Constants;
import com.ib.system.SysConstants;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;
import com.ib.system.exceptions.DbErrorException;

/**
 * Общото за всички вариянти на обекти в тази таблица
 *
 * @author belev
 */
@MappedSuperclass
public abstract class ObektDeinostBase extends TrackableEntity {

	private static final long serialVersionUID = -1055288804160943025L;

	@SequenceGenerator(name = "ObektDeinost", sequenceName = "seq_obekt_deinost", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "ObektDeinost")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "vid")
	@JournalAttr(label = "vid", defaultText = "Вид обект", classifID = "" + BabhConstants.CODE_CLASSIF_TIP_OBEKT)
	private Integer vid; // (int8)

	@Column(name = "vid_oez")
	@JournalAttr(label = "vid_oez", defaultText = "Вид на ОЕЗ", classifID = "" + BabhConstants.CODE_CLASSIF_VID_OEZ)
	private Integer vidOez; // (int8)

	@Column(name = "vid_vlz")
	@JournalAttr(label = "vid_vlz", defaultText = "Вид на ВЛЗ", classifID = "" + BabhConstants.CODE_CLASSIF_VID_VLZ)
	private Integer vidVlz; // (int8)

	@Column(name = "reg_nom")
	@JournalAttr(label = "reg_nom", defaultText = "Рег.номер")
	private String regNom; // (varchar(100))

	@Column(name = "reg_nomer_star")
	@JournalAttr(label = "reg_nomer_star", defaultText = "Рег.номер стар")
	private String regNomerStar; // (varchar)

	@Column(name = "naimenovanie")
	@JournalAttr(label = "naimenovanie", defaultText = "Наименование")
	private String naimenovanie; // (varchar)

	@Column(name = "status")
	@JournalAttr(label = "status", defaultText = "Статус", classifID = "" + BabhConstants.CODE_CLASSIF_STATUS_OEZ)
	private Integer status; // (int8)

	@Column(name = "status_date")
	@JournalAttr(label = "status_date", defaultText = "Дата на статуса")
	private Date statusDate; // (timestamp)

	@Column(name = "tel")
	@JournalAttr(label = "tel", defaultText = "Телефон")
	private String tel; // (varchar)

	@Column(name = "email")
	@JournalAttr(label = "email", defaultText = "Електронна поща")
	private String email; // (varchar)

	@Column(name = "nom_dat_akt")
	@JournalAttr(label = "nom_dat_akt", defaultText = "Номер и дата на издаване на акта за въвеждане в експлоатация")
	private String nomDatAkt; // (varchar)

	@Column(name = "kapacitet")
	@JournalAttr(label = "kapacitet", defaultText = "Капацитет на обекта")
	private String kapacitet; // (varchar)

	@Column(name = "dop_info")
	@JournalAttr(label = "dop_info", defaultText = "Доп. информация")
	private String dopInfo; // (varchar)

	@Column(name = "darj")
	@JournalAttr(label = "darj", defaultText = "Държава", classifID = "" + Constants.CODE_CLASSIF_COUNTRIES)
	private Integer darj; // (int8)

	@Column(name = "obl")
//	@JournalAttr(label = "obl", defaultText = "Област")
	private Integer obl; // (int4)

	@Column(name = "obsht")
//	@JournalAttr(label = "obsht", defaultText = "Община")
	private Integer obsht; // (int8)

	@Column(name = "nas_mesto")
	@JournalAttr(label = "nas_mesto", defaultText = "Населено място", classifID = "" + SysConstants.CODE_CLASSIF_EKATTE)
	private Integer nasMesto; // (int8)

	@Column(name = "address")
	@JournalAttr(label = "address", defaultText = "Улица/Сграда")
	private String address; // (varchar)

	@Column(name = "post_code")
	@JournalAttr(label = "post_code", defaultText = "Пощенски код")
	private String postCode; // (varchar (100))

	@Column(name = "upi")
	@JournalAttr(label = "upi", defaultText = "УПИ")
	private String upi; // (varchar)

	@Column(name = "num_parcel")
	@JournalAttr(label = "num_parcel", defaultText = "Номер на парцел")
	private String numParcel; // (varchar)

	@Column(name = "obslujvani_jiv")
	@JournalAttr(label = "obslujvani_jiv", defaultText = "Обслужвани животни", classifID = "" + BabhConstants.CODE_CLASSIF_OBSL_JIV_VLZ)
	private Integer obslujvaniJiv; // (int8)

	@Column(name = "sektor_obr_diag")
	@JournalAttr(label = "sektor_obr_diag", defaultText = "Наличие на сектор за образна диагностика", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer sektorObrDiag; // (int8)

	@Column(name = "sektor_fizio")
	@JournalAttr(label = "sektor_fizio", defaultText = "Наличие на сектор за физиотерапия", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer sektorFizio; // (int8)

	@Column(name = "stacionar")
	@JournalAttr(label = "stacionar", defaultText = "Наличие на стационар", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer stacionar; // (int8)

	@Column(name = "vid_izsl")
	@JournalAttr(label = "vid_izsl", defaultText = "Видове изследвания")
	private String vidIzsl; // (varchar(500))

	@Column(name = "mobilnost")
	@JournalAttr(label = "mobilnost", defaultText = "Мобилност на обекта", classifID = "" + BabhConstants.CODE_CLASSIF_OBEKT_MOBILNOST)
	private Integer mobilnost; // (int8)

	@Column(name = "dkn")
	@JournalAttr(label = "dkn", defaultText = "ДКН на мобилна инсталация")
	private String dkn; // (varchar)

	@Column(name = "zemliste")
	@JournalAttr(label = "zemliste", defaultText = "Землище")
	private String zemliste; // (varchar)

	@Column(name = "nom_dat_dogovor")
	@JournalAttr(label = "nom_dat_dogovor", defaultText = "Номер и дата на възлагателния договор")
	private String nomDatDogovor; // (varchar)

	@Column(name = "gps_lat")
	@JournalAttr(label = "gps_lat", defaultText = "Географска ширина")
	private Double gpsLat; // (numeric(15,12))

	@Column(name = "gps_lon")
	@JournalAttr(label = "gps_lon", defaultText = "Географска дължина")
	private Double gpsLon; // (numeric(15,12))

	@Column(name = "nom_kadast")
	@JournalAttr(label = "nom_kadast", defaultText = "Номер по кадастър")
	private String nomKadast; // (varchar(100))

	@Column(name = "bio_sigurnost_oez")
	@JournalAttr(label = "biosgurnostOez", defaultText = "Био сигурност", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer biosgurnostOez; // (int8)
	
	@JournalAttr(label = "obektDeinostLica", defaultText = "Лица свързани с обекта")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "obekt_deinost_id", referencedColumnName = "id")
	private List<ObektDeinostLica> obektDeinostLica;

	/**  */
	protected ObektDeinostBase() {
		super();
	}

	/**
	 * Всички стрингови полета, които са предполага, че са номера и са празен стринг ги прави на null
	 */
	public void fixEmptyStringValues() {
		this.regNom = trimToNULL(this.regNom);
		this.regNomerStar = trimToNULL(this.regNomerStar);
		this.nomKadast = trimToNULL(this.nomKadast);
	}

	/** @return the address */
	public String getAddress() {
		return this.address;
	}

	/** @return the darj */
	public Integer getDarj() {
		return this.darj;
	}

	/** @return the dkn */
	public String getDkn() {
		return this.dkn;
	}

	/** @return the dopInfo */
	public String getDopInfo() {
		return this.dopInfo;
	}

	/** @return the email */
	public String getEmail() {
		return this.email;
	}

	/** @return the gpsLat */
	public Double getGpsLat() {
		return this.gpsLat;
	}

	/** @return the gpsLon */
	public Double getGpsLon() {
		return this.gpsLon;
	}

	/** @return the id */
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public String getIdentInfo() throws DbErrorException {
		StringBuilder sb = new StringBuilder();

		if (this.vid != null) {
			if (this.vid.intValue() == BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ) {
				sb.append("ОЕЗ");
			} else if (this.vid.intValue() == BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_FURAJI) {
				sb.append("Обект за дейности с фуражи");
			} else if (this.vid.intValue() == BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLP) {
				sb.append("Обект за дейности с ВЛП");
			} else if (this.vid.intValue() == BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLZ) {
				sb.append("ВЛЗ");
			}
		}
		if (this.regNom != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(this.regNom);
		}
		if (this.naimenovanie != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(this.naimenovanie);
		}
		return sb.toString();
	}

	/** @return the kapacitet */
	public String getKapacitet() {
		return this.kapacitet;
	}

	/** @return the mobilnost */
	public Integer getMobilnost() {
		return this.mobilnost;
	}

	/** @return the naimenovanie */
	public String getNaimenovanie() {
		return this.naimenovanie;
	}

	/** @return the nasMesto */
	public Integer getNasMesto() {
		return this.nasMesto;
	}

	/** @return the nomDatAkt */
	public String getNomDatAkt() {
		return this.nomDatAkt;
	}

	/** @return the nomDatDogovor */
	public String getNomDatDogovor() {
		return this.nomDatDogovor;
	}

	/** @return the nomKadast */
	public String getNomKadast() {
		return this.nomKadast;
	}

	/** @return the numParcel */
	public String getNumParcel() {
		return this.numParcel;
	}

	/** @return the obektDeinostLica */
	public List<ObektDeinostLica> getObektDeinostLica() {
		return this.obektDeinostLica;
	}

	/** @return the obl */
	public Integer getObl() {
		return this.obl;
	}

	/** @return the obsht */
	public Integer getObsht() {
		return this.obsht;
	}

	/** @return the obslujvaniJiv */
	public Integer getObslujvaniJiv() {
		return this.obslujvaniJiv;
	}

	/** @return the postCode */
	public String getPostCode() {
		return this.postCode;
	}

	/** @return the regNom */
	public String getRegNom() {
		return this.regNom;
	}

	/** @return the regNomerStar */
	public String getRegNomerStar() {
		return this.regNomerStar;
	}

	/** @return the sektorFizio */
	public Integer getSektorFizio() {
		return this.sektorFizio;
	}

	/** @return the sektorObrDiag */
	public Integer getSektorObrDiag() {
		return this.sektorObrDiag;
	}

	/** @return the stacionar */
	public Integer getStacionar() {
		return this.stacionar;
	}

	/** @return the status */
	public Integer getStatus() {
		return this.status;
	}

	/** @return the statusDate */
	public Date getStatusDate() {
		return this.statusDate;
	}

	/** @return the tel */
	public String getTel() {
		return this.tel;
	}

	/** @return the upi */
	public String getUpi() {
		return this.upi;
	}

	/** @return the vid */
	public Integer getVid() {
		return this.vid;
	}

	/** @return the vidIzsl */
	public String getVidIzsl() {
		return this.vidIzsl;
	}

	/** @return the vidOez */
	public Integer getVidOez() {
		return this.vidOez;
	}

	/** @return the vidVlz */
	public Integer getVidVlz() {
		return this.vidVlz;
	}

	/** @return the zemliste */
	public String getZemliste() {
		return this.zemliste;
	}

	/** @param address the address to set */
	public void setAddress(String address) {
		this.address = address;
	}

	/** @param darj the darj to set */
	public void setDarj(Integer darj) {
		this.darj = darj;
	}

	/** @param dkn the dkn to set */
	public void setDkn(String dkn) {
		this.dkn = dkn;
	}

	/** @param dopInfo the dopInfo to set */
	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}

	/** @param email the email to set */
	public void setEmail(String email) {
		this.email = email;
	}

	/** @param gpsLat the gpsLat to set */
	public void setGpsLat(Double gpsLat) {
		this.gpsLat = gpsLat;
	}

	/** @param gpsLon the gpsLon to set */
	public void setGpsLon(Double gpsLon) {
		this.gpsLon = gpsLon;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param kapacitet the kapacitet to set */
	public void setKapacitet(String kapacitet) {
		this.kapacitet = kapacitet;
	}

	/** @param mobilnost the mobilnost to set */
	public void setMobilnost(Integer mobilnost) {
		this.mobilnost = mobilnost;
	}

	/** @param naimenovanie the naimenovanie to set */
	public void setNaimenovanie(String naimenovanie) {
		this.naimenovanie = naimenovanie;
	}

	/** @param nasMesto the nasMesto to set */
	public void setNasMesto(Integer nasMesto) {
		this.nasMesto = nasMesto;
	}

	/** @param nomDatAkt the nomDatAkt to set */
	public void setNomDatAkt(String nomDatAkt) {
		this.nomDatAkt = nomDatAkt;
	}

	/** @param nomDatDogovor the nomDatDogovor to set */
	public void setNomDatDogovor(String nomDatDogovor) {
		this.nomDatDogovor = nomDatDogovor;
	}

	/** @param nomKadast the nomKadast to set */
	public void setNomKadast(String nomKadast) {
		this.nomKadast = nomKadast;
	}

	/** @param numParcel the numParcel to set */
	public void setNumParcel(String numParcel) {
		this.numParcel = numParcel;
	}

	/** @param obektDeinostLica the obektDeinostLica to set */
	public void setObektDeinostLica(List<ObektDeinostLica> obektDeinostLica) {
		this.obektDeinostLica = obektDeinostLica;
	}

	/** @param obl the obl to set */
	public void setObl(Integer obl) {
		this.obl = obl;
	}

	/** @param obsht the obsht to set */
	public void setObsht(Integer obsht) {
		this.obsht = obsht;
	}

	/** @param obslujvaniJiv the obslujvaniJiv to set */
	public void setObslujvaniJiv(Integer obslujvaniJiv) {
		this.obslujvaniJiv = obslujvaniJiv;
	}

	/** @param postCode the postCode to set */
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	/** @param regNom the regNom to set */
	public void setRegNom(String regNom) {
		this.regNom = regNom;
	}

	/** @param regNomerStar the regNomerStar to set */
	public void setRegNomerStar(String regNomerStar) {
		this.regNomerStar = regNomerStar;
	}

	/** @param sektorFizio the sektorFizio to set */
	public void setSektorFizio(Integer sektorFizio) {
		this.sektorFizio = sektorFizio;
	}

	/** @param sektorObrDiag the sektorObrDiag to set */
	public void setSektorObrDiag(Integer sektorObrDiag) {
		this.sektorObrDiag = sektorObrDiag;
	}

	/** @param stacionar the stacionar to set */
	public void setStacionar(Integer stacionar) {
		this.stacionar = stacionar;
	}

	/** @param status the status to set */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/** @param statusDate the statusDate to set */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/** @param tel the tel to set */
	public void setTel(String tel) {
		this.tel = tel;
	}

	/** @param upi the upi to set */
	public void setUpi(String upi) {
		this.upi = upi;
	}

	/** @param vid the vid to set */
	public void setVid(Integer vid) {
		this.vid = vid;
	}

	/** @param vidIzsl the vidIzsl to set */
	public void setVidIzsl(String vidIzsl) {
		this.vidIzsl = vidIzsl;
	}

	/** @param vidOez the vidOez to set */
	public void setVidOez(Integer vidOez) {
		this.vidOez = vidOez;
	}

	/** @param vidVlz the vidVlz to set */
	public void setVidVlz(Integer vidVlz) {
		this.vidVlz = vidVlz;
	}

	/** @param zemliste the zemliste to set */
	public void setZemliste(String zemliste) {
		this.zemliste = zemliste;
	}
	

	public Integer getBiosgurnostOez() {
		return biosgurnostOez;
	}

	public void setBiosgurnostOez(Integer biosgurnostOez) {
		this.biosgurnostOez = biosgurnostOez;
	}
}
