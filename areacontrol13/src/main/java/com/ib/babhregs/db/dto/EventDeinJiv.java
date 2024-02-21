package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Where;

import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.system.Constants;
import com.ib.system.SysConstants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

/**
 * Дейности с животни
 */
@Entity
@Table(name = "event_deinost_jiv")
public class EventDeinJiv extends TrackableEntity implements AuditExt {

	private static final long serialVersionUID = -8349576289986779283L;

	@SequenceGenerator(name = "EventDeinJiv", sequenceName = "seq_event_deinost_jiv", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinJiv")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "opisanie_cyr")
	@JournalAttr(label = "opisanie_cyr", defaultText = "Описание на дейността")
	private String opisanieCyr; // (varchar)

	@Column(name = "dop_info")
	@JournalAttr(label = "dop_info", defaultText = "Доп. информация/ Становище")
	private String dopInfo; // (varchar)

	@Column(name = "date_beg_licence")
	@JournalAttr(label = "date_beg_licence", defaultText = "Начална дата на лицензиране")
	private Date dateBegLicence; // (timestamp)

	@Column(name = "date_end_licence")
	@JournalAttr(label = "date_end_licence", defaultText = "Крайна дата на лицензиране")
	private Date dateEndLicence; // (timestamp)

	@Column(name = "id_vpisvane")
	@JournalAttr(label = "id_vpisvane", defaultText = "Вписване", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE)
	private Integer idVpisvane; // (int8)

	@Column(name = "patuvane")
	@JournalAttr(label = "patuvane", defaultText = "Вид пътуване", classifID = "" + BabhConstants.CODE_CLASSIF_VID_PATUVANE)
	private Integer patuvane; // (int8)

	@Column(name = "lice_kachestvo")
	@JournalAttr(label = "lice_kachestvo", defaultText = "Качество на лице за превоз на животни", classifID = "" + BabhConstants.CODE_CLASSIF_OTN_LICE_PREVOZ)
	private Integer liceKachestvo; // (int8)

	@Column(name = "meroptiatie")
	@JournalAttr(label = "meroptiatie", defaultText = "Вид мероприятие", classifID = "" + BabhConstants.CODE_CLASSIF_VID_MEROPRIATIE)
	private Integer meroptiatie; // (int8)

	@Column(name = "adres")
	@JournalAttr(label = "adres", defaultText = "Адрес/ Месторабота")
	private String adres; // (varchar)

	@Column(name = "date_beg_meropriatie")
	@JournalAttr(label = "date_beg_meropriatie", defaultText = "Начална дата на мероприятие")
	private Date dateBegMeropriatie; // (timestamp)

	@Column(name = "date_end_meropriatie")
	@JournalAttr(label = "date_end_meropriatie", defaultText = "Крайна дата на мероприятие")
	private Date dateEndMeropriatie; // (timestamp)

	@Column(name = "opisanie_lat")
	@JournalAttr(label = "opisanie_lat", defaultText = "Описание на дейността (латиница)")
	private String opisanieLat; // (varchar)

	@Column(name = "nachin_mqsto_pridobiv")
	@JournalAttr(label = "nachin_mqsto_pridobiv", defaultText = "Начин и място на придобиване")
	private String nachinMqstoPridobiv; // (varchar)

	@Column(name = "iznos")
	@JournalAttr(label = "iznos", defaultText = "Предмет на износа")
	private String iznos; // (varchar)

	@Column(name = "cel")
	@JournalAttr(label = "cel", defaultText = "Цел на дейността")
	private String cel; // (varchar)

	@Column(name = "miarka")
	@JournalAttr(label = "miarka", defaultText = "Вид мярка", classifID = "" + BabhConstants.CODE_CLASSIF_VID_MIARKA)
	private Integer miarka; // (int8)

	@Column(name = "zemliste")
	@JournalAttr(label = "zemliste", defaultText = "Землище")
	private String zemliste; // (varchar)

	@Column(name = "nachin_transp")
	@JournalAttr(label = "nachin_transp", defaultText = "Начин на транспортиране", classifID = "" + BabhConstants.CODE_CLASSIF_NACHIN_TRANSP)
	private Integer nachinTransp; // (int8)

	@Column(name = "categoria")
	@JournalAttr(label = "categoria", defaultText = "Категория", classifID = "" + BabhConstants.CODE_CLASSIF_CATEGORY_SJP)
	private Integer categoria; // (int8)

	@Column(name = "sklad")
	@JournalAttr(label = "sklad", defaultText = "Съхраняват ли се пратките СЖП на склад", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer sklad; // (int8)

	// за event_deinost_jiv_vid.vid
	@Transient
	@JournalAttr(label = "vidList", defaultText = "Вид на дейността", classifID = "" + BabhConstants.CODE_CLASSIF_VID_DEINOST)
	private List<Integer>	vidList;
	@Transient
	private List<Integer>	dbVidList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	@JournalAttr(label = "eventDeinJivPredmet", defaultText = "Предмет на дейността")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "event_deinost_jiv_id", referencedColumnName = "id", nullable = false)
	private List<EventDeinJivPredmet> eventDeinJivPredmet;

	@JournalAttr(label = "eventDeinJivIdentif", defaultText = "Предмет на дейността с идентификатори")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "event_deinost_jiv_id", referencedColumnName = "id", nullable = false)
	private List<EventDeinJivIdentif> eventDeinJivIdentif;

	@JournalAttr(label = "eventDeinJivLice", defaultText = "Лица за дейността")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "event_deinost_jiv_id", referencedColumnName = "id", nullable = false)
	private List<EventDeinJivLice> eventDeinJivLice;

	@JournalAttr(label = "obektDeinostDeinost", defaultText = "Обект на дейност - Дейност")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "deinost_id", referencedColumnName = "id")
	@Where(clause = "tabl_event_deinost = " + BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_JIV + " ")
	private List<ObektDeinostDeinost> obektDeinostDeinost; // филтъра е с hibernate

	@JournalAttr(label = "mpsDeinost", defaultText = "Дейност – МПС")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "deinost_id", referencedColumnName = "id")
	@Where(clause = "tabl_event_deinost = " + BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_JIV + " ")
	private List<MpsDeinost> mpsDeinost; // филтъра е с hibernate

	// за event_deinost_jiv_darj.darj
	@Transient
	@JournalAttr(label = "darjList", defaultText = "Страни, за които се изнася", classifID = "" + Constants.CODE_CLASSIF_COUNTRIES)
	private List<Integer>	darjList;
	@Transient
	private List<Integer>	dbDarjList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	// за event_deinost_jiv_gkpp.gkpp
	@Transient
	@JournalAttr(label = "gkppList", defaultText = "Изходен ГКПП", classifID = "" + BabhConstants.CODE_CLASSIF_GKPP)
	private List<Integer>	gkppList;
	@Transient
	private List<Integer>	dbGkppList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	// за event_deinost_jiv_obuchenie.sfera_obucenie
	@Transient
	@JournalAttr(label = "obuchenieList", defaultText = "Сфери на обучение", classifID = "" + BabhConstants.CODE_CLASSIF_SFERI_OBUCHENIE)
	private List<Integer>	obuchenieList;
	@Transient
	private List<Integer>	dbObuchenieList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	/** */
	public EventDeinJiv() {
		super();
	}

	/** @return the adres */
	public String getAdres() {
		return this.adres;
	}

	/** @return the categoria */
	public Integer getCategoria() {
		return this.categoria;
	}

	/** @return the cel */
	public String getCel() {
		return this.cel;
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_JIV;
	}

	/** @return the darjList */
	public List<Integer> getDarjList() {
		return this.darjList;
	}

	/** @return the dateBegLicence */
	public Date getDateBegLicence() {
		return this.dateBegLicence;
	}

	/** @return the dateBegMeropriatie */
	public Date getDateBegMeropriatie() {
		return this.dateBegMeropriatie;
	}

	/** @return the dateEndLicence */
	public Date getDateEndLicence() {
		return this.dateEndLicence;
	}

	/** @return the dateEndMeropriatie */
	public Date getDateEndMeropriatie() {
		return this.dateEndMeropriatie;
	}

	/** @return the dbDarjList */
	@XmlTransient
	public List<Integer> getDbDarjList() {
		return this.dbDarjList;
	}

	/** @return the dbGkppList */
	@XmlTransient
	public List<Integer> getDbGkppList() {
		return this.dbGkppList;
	}

	/** @return the dbObuchenieList */
	@XmlTransient
	public List<Integer> getDbObuchenieList() {
		return this.dbObuchenieList;
	}

	/** @return the dbVidList */
	@XmlTransient
	public List<Integer> getDbVidList() {
		return this.dbVidList;
	}

	/** @return the dopInfo */
	public String getDopInfo() {
		return this.dopInfo;
	}

	/** @return the eventDeinJivIdentif */
	public List<EventDeinJivIdentif> getEventDeinJivIdentif() {
		return this.eventDeinJivIdentif;
	}

	/** @return the eventDeinJivLice */
	public List<EventDeinJivLice> getEventDeinJivLice() {
		return this.eventDeinJivLice;
	}

	/** @return the eventDeinJivPredmet */
	public List<EventDeinJivPredmet> getEventDeinJivPredmet() {
		return this.eventDeinJivPredmet;
	}

	/** @return the gkppList */
	public List<Integer> getGkppList() {
		return this.gkppList;
	}

	/** @return the id */
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public String getIdentInfo() throws DbErrorException {
		return "/vp_id=" + this.idVpisvane + "/";
	}

	/** @return the idVpisvane */
	public Integer getIdVpisvane() {
		return this.idVpisvane;
	}

	/** @return the iznos */
	public String getIznos() {
		return this.iznos;
	}

	/** @return the liceKachestvo */
	public Integer getLiceKachestvo() {
		return this.liceKachestvo;
	}

	/** @return the meroptiatie */
	public Integer getMeroptiatie() {
		return this.meroptiatie;
	}

	/** @return the miarka */
	public Integer getMiarka() {
		return this.miarka;
	}

	/** @return the mpsDeinost */
	public List<MpsDeinost> getMpsDeinost() {
		return this.mpsDeinost;
	}

	/** @return the nachinMqstoPridobiv */
	public String getNachinMqstoPridobiv() {
		return this.nachinMqstoPridobiv;
	}

	/** @return the nachinTransp */
	public Integer getNachinTransp() {
		return this.nachinTransp;
	}

	/** @return the obektDeinostDeinost */
	public List<ObektDeinostDeinost> getObektDeinostDeinost() {
		return this.obektDeinostDeinost;
	}

	/** @return the obuchenieList */
	public List<Integer> getObuchenieList() {
		return this.obuchenieList;
	}

	/** @return the opisanieCyr */
	public String getOpisanieCyr() {
		return this.opisanieCyr;
	}

	/** @return the opisanieLat */
	public String getOpisanieLat() {
		return this.opisanieLat;
	}

	/** @return the patuvane */
	public Integer getPatuvane() {
		return this.patuvane;
	}

	/** @return the vidList */
	public List<Integer> getVidList() {
		return this.vidList;
	}

	/** @return the zemliste */
	public String getZemliste() {
		return this.zemliste;
	}

	/** @param adres the adres to set */
	public void setAdres(String adres) {
		this.adres = adres;
	}

	/** @param categoria the categoria to set */
	public void setCategoria(Integer categoria) {
		this.categoria = categoria;
	}

	/** @param cel the cel to set */
	public void setCel(String cel) {
		this.cel = cel;
	}

	/** @param darjList the darjList to set */
	public void setDarjList(List<Integer> darjList) {
		this.darjList = darjList;
	}

	/** @param dateBegLicence the dateBegLicence to set */
	public void setDateBegLicence(Date dateBegLicence) {
		this.dateBegLicence = dateBegLicence;
	}

	/** @param dateBegMeropriatie the dateBegMeropriatie to set */
	public void setDateBegMeropriatie(Date dateBegMeropriatie) {
		this.dateBegMeropriatie = dateBegMeropriatie;
	}

	/** @param dateEndLicence the dateEndLicence to set */
	public void setDateEndLicence(Date dateEndLicence) {
		this.dateEndLicence = dateEndLicence;
	}

	/** @param dateEndMeropriatie the dateEndMeropriatie to set */
	public void setDateEndMeropriatie(Date dateEndMeropriatie) {
		this.dateEndMeropriatie = dateEndMeropriatie;
	}

	/** @param dbDarjList the dbDarjList to set */
	public void setDbDarjList(List<Integer> dbDarjList) {
		this.dbDarjList = dbDarjList;
	}

	/** @param dbGkppList the dbGkppList to set */
	public void setDbGkppList(List<Integer> dbGkppList) {
		this.dbGkppList = dbGkppList;
	}

	/** @param dbObuchenieList the dbObuchenieList to set */
	public void setDbObuchenieList(List<Integer> dbObuchenieList) {
		this.dbObuchenieList = dbObuchenieList;
	}

	/** @param dbVidList the dbVidList to set */
	public void setDbVidList(List<Integer> dbVidList) {
		this.dbVidList = dbVidList;
	}

	/** @param dopInfo the dopInfo to set */
	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}

	/** @param eventDeinJivIdentif the eventDeinJivIdentif to set */
	public void setEventDeinJivIdentif(List<EventDeinJivIdentif> eventDeinJivIdentif) {
		this.eventDeinJivIdentif = eventDeinJivIdentif;
	}

	/** @param eventDeinJivLice the eventDeinJivLice to set */
	public void setEventDeinJivLice(List<EventDeinJivLice> eventDeinJivLice) {
		this.eventDeinJivLice = eventDeinJivLice;
	}

	/** @param eventDeinJivPredmet the eventDeinJivPredmet to set */
	public void setEventDeinJivPredmet(List<EventDeinJivPredmet> eventDeinJivPredmet) {
		this.eventDeinJivPredmet = eventDeinJivPredmet;
	}

	/** @param gkppList the gkppList to set */
	public void setGkppList(List<Integer> gkppList) {
		this.gkppList = gkppList;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param idVpisvane the idVpisvane to set */
	public void setIdVpisvane(Integer idVpisvane) {
		this.idVpisvane = idVpisvane;
	}

	/** @param iznos the iznos to set */
	public void setIznos(String iznos) {
		this.iznos = iznos;
	}

	/** @param liceKachestvo the liceKachestvo to set */
	public void setLiceKachestvo(Integer liceKachestvo) {
		this.liceKachestvo = liceKachestvo;
	}

	/** @param meroptiatie the meroptiatie to set */
	public void setMeroptiatie(Integer meroptiatie) {
		this.meroptiatie = meroptiatie;
	}

	/** @param miarka the miarka to set */
	public void setMiarka(Integer miarka) {
		this.miarka = miarka;
	}

	/** @param mpsDeinost the mpsDeinost to set */
	public void setMpsDeinost(List<MpsDeinost> mpsDeinost) {
		this.mpsDeinost = mpsDeinost;
	}

	/** @param nachinMqstoPridobiv the nachinMqstoPridobiv to set */
	public void setNachinMqstoPridobiv(String nachinMqstoPridobiv) {
		this.nachinMqstoPridobiv = nachinMqstoPridobiv;
	}

	/** @param nachinTransp the nachinTransp to set */
	public void setNachinTransp(Integer nachinTransp) {
		this.nachinTransp = nachinTransp;
	}

	/** @param obektDeinostDeinost the obektDeinostDeinost to set */
	public void setObektDeinostDeinost(List<ObektDeinostDeinost> obektDeinostDeinost) {
		this.obektDeinostDeinost = obektDeinostDeinost;
	}

	/** @param obuchenieList the obuchenieList to set */
	public void setObuchenieList(List<Integer> obuchenieList) {
		this.obuchenieList = obuchenieList;
	}

	/** @param opisanieCyr the opisanieCyr to set */
	public void setOpisanieCyr(String opisanieCyr) {
		this.opisanieCyr = opisanieCyr;
	}

	/** @param opisanieLat the opisanieLat to set */
	public void setOpisanieLat(String opisanieLat) {
		this.opisanieLat = opisanieLat;
	}

	/** @param patuvane the patuvane to set */
	public void setPatuvane(Integer patuvane) {
		this.patuvane = patuvane;
	}

	/** @param vidList the vidList to set */
	public void setVidList(List<Integer> vidList) {
		this.vidList = vidList;
	}

	/** @param zemliste the zemliste to set */
	public void setZemliste(String zemliste) {
		this.zemliste = zemliste;
	}

	/** @return the sklad */
	public Integer getSklad() {
		return this.sklad;
	}
	/** @param sklad the sklad to set */
	public void setSklad(Integer sklad) {
		this.sklad = sklad;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());

		if (this.idVpisvane != null) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
			journal.setJoinedIdObject1(this.idVpisvane);
		}
		return journal;
	}
}
