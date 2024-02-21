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
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

/**
 * Дейности с ВЛП
 */
@Entity
@Table(name = "event_deinost_vlp")
public class EventDeinostVlp extends TrackableEntity implements AuditExt {

	private static final long serialVersionUID = -6668429839418062419L;

	@SequenceGenerator(name = "EventDeinostVlp", sequenceName = "seq_event_deinost_vlp", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostVlp")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "opisanie")
	@JournalAttr(label = "opisanie", defaultText = "Описание на дейността")
	private String opisanie; // (varchar(1000))

	@Column(name = "danni_kontragent")
	@JournalAttr(label = "danni_kontragent", defaultText = "Данни за контрагента на паралелна търговия")
	private String danniKontragent; // (varchar(1000))

	@Column(name = "dop_info")
	@JournalAttr(label = "dop_info", defaultText = "Доп. информация")
	private String dopInfo; // (varchar(1000))

	@Column(name = "id_vpisvane")
	@JournalAttr(label = "id_vpisvane", defaultText = "Вписване", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE)
	private Integer idVpisvane; // (int8)

	@Column(name = "operator_other_country")
	@JournalAttr(label = "operator_other_country", defaultText = "Номер на оператор от друга държава")
	private String operatorOtherCountry; // (varchar(255))

	@Column(name = "email")
	@JournalAttr(label = "email", defaultText = "Електронна поща")
	private String email; // (varchar(100))

	@Column(name = "tel")
	@JournalAttr(label = "tel", defaultText = "Телефон")
	private String tel; // (varchar(50))

	@Column(name = "adres")
	@JournalAttr(label = "adres", defaultText = "Пощенски адрес")
	private String adres; // (varchar(1000))

	@Column(name = "site")
	@JournalAttr(label = "site", defaultText = "Интернет страница")
	private String site; // (varchar(1000))

	@Column(name = "dat_inspekcia")
	@JournalAttr(label = "dat_inspekcia", defaultText = "Дата на инспекцията")
	private Date datInspekcia; // (timestamp)

	@Column(name = "obhvat_inspekcia")
	@JournalAttr(label = "obhvat_inspekcia", defaultText = "Обхват на инспекцията")
	private String obhvatInspekcia; // (varchar(500))

	@Column(name = "prednazn_reklama")
	@JournalAttr(label = "prednazn_reklama", defaultText = "Предназначение на реклама", classifID = "" + BabhConstants.CODE_CLASSIF_PREDN_REKLAMA)
	private Integer prednaznReklama; // (int8)

	@Column(name = "vlp_reklama")
	@JournalAttr(label = "vlp_reklama", defaultText = "ВЛП, които ще се рекламират")
	private String vlpReklama; // (varchar(1000))

	@Column(name = "nom_dat_lizenz")
	@JournalAttr(label = "nom_dat_lizenz", defaultText = "Номер и дата на лицензии за дейността")
	private String nomDatLizenz; // (varchar(255))

	@Column(name = "mesta")
	@JournalAttr(label = "mesta", defaultText = "Места за изпитване")
	private String mesta; // (varchar(1000))

	@Column(name = "laboratorii")
	@JournalAttr(label = "laboratorii", defaultText = "Лаборатории за изпитване")
	private String laboratorii; // (varchar(1000))

	@Column(name = "pom_veshtestva")
	@JournalAttr(label = "pom_veshtestva", defaultText = "Помощни вещества")
	private String pomVeshtestva; // (varchar(1000))

	@Column(name = "akred_laboratoria")
	@JournalAttr(label = "akred_laboratoria", defaultText = "Акредитирана лаборатория по чл. 410б, ал. 1, т. 4 ЗВД")
	private String akredLaboratoria; // (varchar)

	@JournalAttr(label = "eventDeinostVlpVid", defaultText = "Вид на дейността")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "id_event_deinost_vlp", referencedColumnName = "id", nullable = false)
	private List<EventDeinostVlpVid> eventDeinostVlpVid; // да се направи само с кодовете от класификацията

	@JournalAttr(label = "eventDeinostVlpPredmet", defaultText = "Предмет на дейността")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "id_event_deinost_vlp", referencedColumnName = "id", nullable = false)
	private List<EventDeinostVlpPredmet> eventDeinostVlpPredmet;

	@JournalAttr(label = "eventDeinostVlpPrvnos", defaultText = "ВЛП за производство/ внос")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "event_deinost_vlp_id", referencedColumnName = "id", nullable = false)
	private List<EventDeinostVlpPrvnos> eventDeinostVlpPrvnos;

	@JournalAttr(label = "eventDeinostVlpLice", defaultText = "Свързани лица")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "event_deinost_vlp_id", referencedColumnName = "id", nullable = false)
	private List<EventDeinostVlpLice> eventDeinostVlpLice;

	@JournalAttr(label = "obektDeinostDeinost", defaultText = "Обект на дейност - Дейност")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "deinost_id", referencedColumnName = "id")
	@Where(clause = "tabl_event_deinost = " + BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP + " ")
	private List<ObektDeinostDeinost> obektDeinostDeinost; // филтъра е с hibernate

	// за event_deinost_vlp_bolesti.bolest
	@Transient
	@JournalAttr(label = "bolestList", defaultText = "Болести, за които е предназначено средството", classifID = "" + BabhConstants.CODE_CLASSIF_BOLESTI)
	private List<Integer>	bolestList;
	@Transient
	private List<Integer>	dbBolestList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	/** */
	public EventDeinostVlp() {
		super();
	}

	/** @return the adres */
	public String getAdres() {
		return this.adres;
	}

	/** @return the akredLaboratoria */
	public String getAkredLaboratoria() {
		return this.akredLaboratoria;
	}

	/** @return the bolestList */
	public List<Integer> getBolestList() {
		return this.bolestList;
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_VLP;
	}

	/** @return the danniKontragent */
	public String getDanniKontragent() {
		return this.danniKontragent;
	}

	/** @return the datInspekcia */
	public Date getDatInspekcia() {
		return this.datInspekcia;
	}

	/** @return the dbBolestList */
	@XmlTransient
	public List<Integer> getDbBolestList() {
		return this.dbBolestList;
	}

	/** @return the dopInfo */
	public String getDopInfo() {
		return this.dopInfo;
	}

	/** @return the email */
	public String getEmail() {
		return this.email;
	}

	/** @return the eventDeinostVlpLice */
	public List<EventDeinostVlpLice> getEventDeinostVlpLice() {
		return this.eventDeinostVlpLice;
	}

	/** @return the eventDeinostVlpPredmet */
	public List<EventDeinostVlpPredmet> getEventDeinostVlpPredmet() {
		return this.eventDeinostVlpPredmet;
	}

	/** @return the eventDeinostVlpPrvnos */
	public List<EventDeinostVlpPrvnos> getEventDeinostVlpPrvnos() {
		return this.eventDeinostVlpPrvnos;
	}

	/** @return the eventDeinostVlpVid */
	public List<EventDeinostVlpVid> getEventDeinostVlpVid() {
		return this.eventDeinostVlpVid;
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

	/** @return the laboratorii */
	public String getLaboratorii() {
		return this.laboratorii;
	}

	/** @return the mesta */
	public String getMesta() {
		return this.mesta;
	}

	/** @return the nomDatLizenz */
	public String getNomDatLizenz() {
		return this.nomDatLizenz;
	}

	/** @return the obektDeinostDeinost */
	public List<ObektDeinostDeinost> getObektDeinostDeinost() {
		return this.obektDeinostDeinost;
	}

	/** @return the obhvatInspekcia */
	public String getObhvatInspekcia() {
		return this.obhvatInspekcia;
	}

	/** @return the operatorOtherCountry */
	public String getOperatorOtherCountry() {
		return this.operatorOtherCountry;
	}

	/** @return the opisanie */
	public String getOpisanie() {
		return this.opisanie;
	}

	/** @return the pomVeshtestva */
	public String getPomVeshtestva() {
		return this.pomVeshtestva;
	}

	/** @return the prednaznReklama */
	public Integer getPrednaznReklama() {
		return this.prednaznReklama;
	}

	/** @return the site */
	public String getSite() {
		return this.site;
	}

	/** @return the tel */
	public String getTel() {
		return this.tel;
	}

	/** @return the vlpReklama */
	public String getVlpReklama() {
		return this.vlpReklama;
	}

	/** @param adres the adres to set */
	public void setAdres(String adres) {
		this.adres = adres;
	}

	/** @param akredLaboratoria the akredLaboratoria to set */
	public void setAkredLaboratoria(String akredLaboratoria) {
		this.akredLaboratoria = akredLaboratoria;
	}

	/** @param bolestList the bolestList to set */
	public void setBolestList(List<Integer> bolestList) {
		this.bolestList = bolestList;
	}

	/** @param danniKontragent the danniKontragent to set */
	public void setDanniKontragent(String danniKontragent) {
		this.danniKontragent = danniKontragent;
	}

	/** @param datInspekcia the datInspekcia to set */
	public void setDatInspekcia(Date datInspekcia) {
		this.datInspekcia = datInspekcia;
	}

	/** @param dbBolestList the dbBolestList to set */
	public void setDbBolestList(List<Integer> dbBolestList) {
		this.dbBolestList = dbBolestList;
	}

	/** @param dopInfo the dopInfo to set */
	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}

	/** @param email the email to set */
	public void setEmail(String email) {
		this.email = email;
	}

	/** @param eventDeinostVlpLice the eventDeinostVlpLice to set */
	public void setEventDeinostVlpLice(List<EventDeinostVlpLice> eventDeinostVlpLice) {
		this.eventDeinostVlpLice = eventDeinostVlpLice;
	}

	/** @param eventDeinostVlpPredmet the eventDeinostVlpPredmet to set */
	public void setEventDeinostVlpPredmet(List<EventDeinostVlpPredmet> eventDeinostVlpPredmet) {
		this.eventDeinostVlpPredmet = eventDeinostVlpPredmet;
	}

	/** @param eventDeinostVlpPrvnos the eventDeinostVlpPrvnos to set */
	public void setEventDeinostVlpPrvnos(List<EventDeinostVlpPrvnos> eventDeinostVlpPrvnos) {
		this.eventDeinostVlpPrvnos = eventDeinostVlpPrvnos;
	}

	/** @param eventDeinostVlpVid the eventDeinostVlpVid to set */
	public void setEventDeinostVlpVid(List<EventDeinostVlpVid> eventDeinostVlpVid) {
		this.eventDeinostVlpVid = eventDeinostVlpVid;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param idVpisvane the idVpisvane to set */
	public void setIdVpisvane(Integer idVpisvane) {
		this.idVpisvane = idVpisvane;
	}

	/** @param laboratorii the laboratorii to set */
	public void setLaboratorii(String laboratorii) {
		this.laboratorii = laboratorii;
	}

	/** @param mesta the mesta to set */
	public void setMesta(String mesta) {
		this.mesta = mesta;
	}

	/** @param nomDatLizenz the nomDatLizenz to set */
	public void setNomDatLizenz(String nomDatLizenz) {
		this.nomDatLizenz = nomDatLizenz;
	}

	/** @param obektDeinostDeinost the obektDeinostDeinost to set */
	public void setObektDeinostDeinost(List<ObektDeinostDeinost> obektDeinostDeinost) {
		this.obektDeinostDeinost = obektDeinostDeinost;
	}

	/** @param obhvatInspekcia the obhvatInspekcia to set */
	public void setObhvatInspekcia(String obhvatInspekcia) {
		this.obhvatInspekcia = obhvatInspekcia;
	}

	/** @param operatorOtherCountry the operatorOtherCountry to set */
	public void setOperatorOtherCountry(String operatorOtherCountry) {
		this.operatorOtherCountry = operatorOtherCountry;
	}

	/** @param opisanie the opisanie to set */
	public void setOpisanie(String opisanie) {
		this.opisanie = opisanie;
	}

	/** @param pomVeshtestva the pomVeshtestva to set */
	public void setPomVeshtestva(String pomVeshtestva) {
		this.pomVeshtestva = pomVeshtestva;
	}

	/** @param prednaznReklama the prednaznReklama to set */
	public void setPrednaznReklama(Integer prednaznReklama) {
		this.prednaznReklama = prednaznReklama;
	}

	/** @param site the site to set */
	public void setSite(String site) {
		this.site = site;
	}

	/** @param tel the tel to set */
	public void setTel(String tel) {
		this.tel = tel;
	}

	/** @param vlpReklama the vlpReklama to set */
	public void setVlpReklama(String vlpReklama) {
		this.vlpReklama = vlpReklama;
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
