package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.util.ArrayList;
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
 * Дейности с фуражи
 */
@Entity
@Table(name = "event_deinost_furaji")
public class EventDeinostFuraji extends TrackableEntity implements AuditExt {

	private static final long serialVersionUID = -524374567444198806L;

	@SequenceGenerator(name = "EventDeinostFuraji", sequenceName = "seq_event_deinost_furaji", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostFuraji")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "opisanie")
	@JournalAttr(label = "opisanie", defaultText = "Описание на дейността")
	private String opisanie; // (varchar(1000))

	@Column(name = "code_deinost")
	@JournalAttr(label = "code_deinost", defaultText = "Код на дейността")
	private String codeDeinost; // (varchar(50))

	@Column(name = "broi_tehn_linii")
	@JournalAttr(label = "broi_tehn_linii", defaultText = "Брой технологични линии")
	private Integer broiTehnLinii; // (int8)

	@Column(name = "broi_fur_surovini")
	@JournalAttr(label = "broi_fur_surovini", defaultText = "Брой фуражни суровини", classifID = "" + BabhConstants.CODE_CLASSIF_FURAJI_SUROVINI)
	private Integer broiFurSurovini; // (int8)

	@Column(name = "proizhod_surovini")
	@JournalAttr(label = "proizhod_surovini", defaultText = "Произход на суровини", classifID = "" + BabhConstants.CODE_CLASSIF_PROIZHOD_SUROVINI)
	private Integer proizhodSurovini; // (int8)

	@Column(name = "dop_info")
	@JournalAttr(label = "dop_info", defaultText = "Доп. информация")
	private String dopInfo; // (varchar(1000))

	@Column(name = "id_vpisvane")
	@JournalAttr(label = "id_vpisvane", defaultText = "Вписване", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE)
	private Integer idVpisvane; // (int8)

	@Column(name = "side")
	@JournalAttr(label = "side", defaultText = "Електронен адрес")
	private String side; // (varchar(1000))

	@Column(name = "url")
	@JournalAttr(label = "url", defaultText = "Интернет страница")
	private String url; // (varchar(1000))

	@Column(name = "email")
	@JournalAttr(label = "email", defaultText = "Имейл адрес")
	private String email; // (varchar(200))

	@Column(name = "tel")
	@JournalAttr(label = "tel", defaultText = "Телефон")
	private String tel; // (varchar(50))

	@Column(name = "installation_type")
	@JournalAttr(label = "installation_type", defaultText = "Вид инсталация", classifID = "" + BabhConstants.CODE_CLASSIF_VID_INSTALACIA)
	private Integer installationType; // (int8)

	@Column(name = "polzvane_mps")
	@JournalAttr(label = "polzvane_mps", defaultText = "Ползване на МПС", classifID = "" + BabhConstants.CODE_CLASSIF_POLZANE_MPS)
	private Integer polzvaneMps; // (int8)

	@Column(name = "darj")
	@JournalAttr(label = "darj", defaultText = "Държава", classifID = "" + Constants.CODE_CLASSIF_COUNTRIES)
	private Integer darj; // (int8)

	@Column(name = "naimenovanie")
	@JournalAttr(label = "naimenovanie", defaultText = "Наименование на контрагента")
	private String naimenovanie; // (varchar)

	@Column(name = "address")
	@JournalAttr(label = "address", defaultText = "Адрес на контрагента")
	private String address; // (varchar)

	@Column(name = "nom_obekt_drug")
	@JournalAttr(label = "nom_obekt_drug", defaultText = "Номер на оператор от друга държава")
	private String nomObektDrug; // (varchar)

	@Column(name = "sklad")
	@JournalAttr(label = "sklad", defaultText = "Съхранение на склад", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer sklad; // (int8)

	@Column(name = "cel")
	@JournalAttr(label = "cel", defaultText = "Цел на дейността")
	private String cel; // (varchar)

	@Column(name = "kontakt")
	@JournalAttr(label = "kontakt", defaultText = "Данни за контакт")
	private String kontakt; // (varchar)

	@Column(name = "miarka")
	@JournalAttr(label = "miarka", defaultText = "Вид мярка", classifID = "" + BabhConstants.CODE_CLASSIF_VID_MIARKA)
	private Integer miarka; // (int8)

	@Column(name = "nom_partida")
	@JournalAttr(label = "nom_partida", defaultText = "Номер на партида")
	private String nomPartida; // (varchar(100))

	@Column(name = "reg_nom")
	@JournalAttr(label = "reg_nom", defaultText = "Регистрационен номер")
	private String regNom; // (varchar(100))

	@Column(name = "reg_nom_vrem")
	@JournalAttr(label = "reg_nom_vrem", defaultText = "Временен регистрационен номер")
	private String regNomVrem; // (varchar(100))

	// за event_deinost_furaji_vid.vid
	@Transient
	@JournalAttr(label = "vidList", defaultText = "Вид на дейността", classifID = "" + BabhConstants.CODE_CLASSIF_VID_DEINOST)
	private List<Integer>	vidList;
	@Transient
	private List<Integer>	dbVidList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	@JournalAttr(label = "eventDeinostFurajiPredmet", defaultText = "Предмет на дейността")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "event_deinost_furaji_id", referencedColumnName = "id", nullable = false)
	private List<EventDeinostFurajiPredmet> eventDeinostFurajiPredmet;

	@JournalAttr(label = "eventDeinostFurajiPrednaznJiv", defaultText = "Предназначени за животни")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "event_deinost_furaji_id", referencedColumnName = "id", nullable = false)
	private List<EventDeinostFurajiPrednaznJiv> eventDeinostFurajiPrednaznJiv;

	@JournalAttr(label = "obektDeinostDeinost", defaultText = "Обект на дейност - Дейност")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "deinost_id", referencedColumnName = "id")
	@Where(clause = "tabl_event_deinost = " + BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI + " ")
	private List<ObektDeinostDeinost> obektDeinostDeinost; // филтъра е с hibernate

	@Transient
	private boolean saveObektDeinost = true; // в някои заявления се записва в други не !!!

	@JournalAttr(label = "mpsDeinost", defaultText = "Дейност – МПС")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "deinost_id", referencedColumnName = "id")
	@Where(clause = "tabl_event_deinost = " + BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI + " ")
	private List<MpsDeinost> mpsDeinost; // филтъра е с hibernate

	@JournalAttr(label = "eventDeinostFurajiSert", defaultText = "Данни за искан сертификат")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "event_deinost_furaji_id", referencedColumnName = "id", nullable = false)
	private List<EventDeinostFurajiSert> eventDeinostFurajiSert;

	// за event_deinost_furaji_sgp.cel
	@Transient
	@JournalAttr(label = "celList", defaultText = "Цели за СЖП", classifID = "" + BabhConstants.CODE_CLASSIF_CEL_SGP)
	private List<Integer>	celList;
	@Transient
	private List<Integer>	dbCelList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	// за event_deinost_furaji_gip.gip
	@Transient
	@JournalAttr(label = "gipList", defaultText = "ГИП, през който се изнася", classifID = "" + BabhConstants.CODE_CLASSIF_GKPP)
	private List<Integer>	gipList;
	@Transient
	private List<Integer>	dbGipList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	// за event_deinost_furaji_kategoria.kategoria
	@Transient
	@JournalAttr(label = "kategoriaList", defaultText = "Категория", classifID = "" + BabhConstants.CODE_CLASSIF_CATEGORY_SJP)
	private List<Integer>	kategoriaList;
	@Transient
	private List<Integer>	dbKategoriaList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	/** */
	public EventDeinostFuraji() {
		super();
	}

	/** @return the address */
	public String getAddress() {
		return this.address;
	}

	/** @return the broiFurSurovini */
	public Integer getBroiFurSurovini() {
		return this.broiFurSurovini;
	}

	/** @return the broiTehnLinii */
	public Integer getBroiTehnLinii() {
		return this.broiTehnLinii;
	}

	/** @return the cel */
	public String getCel() {
		return this.cel;
	}

	/** @return the celList */
	public List<Integer> getCelList() {
		return this.celList;
	}

	/** @return the codeDeinost */
	public String getCodeDeinost() {
		return this.codeDeinost;
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_FURAJI;
	}

	/** @return the darj */
	public Integer getDarj() {
		return this.darj;
	}

	/** @return the dbCelList */
	@XmlTransient
	public List<Integer> getDbCelList() {
		return this.dbCelList;
	}

	/** @return the dbGipList */
	@XmlTransient
	public List<Integer> getDbGipList() {
		return this.dbGipList;
	}

	/** @return the dbKategoriaList */
	@XmlTransient
	public List<Integer> getDbKategoriaList() {
		return this.dbKategoriaList;
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

	/** @return the email */
	public String getEmail() {
		return this.email;
	}

	/** @return the eventDeinostFurajiPredmet */
	public List<EventDeinostFurajiPredmet> getEventDeinostFurajiPredmet() {
		return this.eventDeinostFurajiPredmet;
	}

	/** @return the eventDeinostFurajiPrednaznJiv */
	public List<EventDeinostFurajiPrednaznJiv> getEventDeinostFurajiPrednaznJiv() {
		return this.eventDeinostFurajiPrednaznJiv;
	}

	/** @return the eventDeinostFurajiSert */
	public List<EventDeinostFurajiSert> getEventDeinostFurajiSert() {
		return this.eventDeinostFurajiSert;
	}

	/** @return the gipList */
	public List<Integer> getGipList() {
		return this.gipList;
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

	/** @return the installationType */
	public Integer getInstallationType() {
		return this.installationType;
	}

	/** @return the kategoriaList */
	public List<Integer> getKategoriaList() {
		return this.kategoriaList;
	}

	/** @return the kontakt */
	public String getKontakt() {
		return this.kontakt;
	}

	/** @return the miarka */
	public Integer getMiarka() {
		return this.miarka;
	}

	/** @return the mpsDeinost */
	public List<MpsDeinost> getMpsDeinost() {
		return this.mpsDeinost;
	}

	/** @return the naimenovanie */
	public String getNaimenovanie() {
		return this.naimenovanie;
	}

	/** @return the nomObektDrug */
	public String getNomObektDrug() {
		return this.nomObektDrug;
	}

	/** @return the nomPartida */
	public String getNomPartida() {
		return this.nomPartida;
	}

	/** @return the obektDeinostDeinost */
	public List<ObektDeinostDeinost> getObektDeinostDeinost() {
		return this.obektDeinostDeinost;
	}

	/** @return the opisanie */
	public String getOpisanie() {
		return this.opisanie;
	}

	/** @return the polzvaneMps */
	public Integer getPolzvaneMps() {
		return this.polzvaneMps;
	}

	/** @return the proizhodSurovini */
	public Integer getProizhodSurovini() {
		return this.proizhodSurovini;
	}

	/** @return the regNom */
	public String getRegNom() {
		return this.regNom;
	}

	/** @return the regNomVrem */
	public String getRegNomVrem() {
		return this.regNomVrem;
	}

	/** @return the sert за случаите на единичен избор */
	@XmlTransient
	public EventDeinostFurajiSert getSert() {

		if (this.eventDeinostFurajiSert == null) {
			this.eventDeinostFurajiSert = new ArrayList<>();
			this.eventDeinostFurajiSert.add(new EventDeinostFurajiSert());

		} else if (this.eventDeinostFurajiSert.isEmpty()) {
			this.eventDeinostFurajiSert.add(new EventDeinostFurajiSert());
		}
		return this.eventDeinostFurajiSert.get(0);
	}

	/** @return the side */
	public String getSide() {
		return this.side;
	}

	/** @return the sklad */
	public Integer getSklad() {
		return this.sklad;
	}

	/** @return the tel */
	public String getTel() {
		return this.tel;
	}

	/** @return the url */
	public String getUrl() {
		return this.url;
	}

	/** @return the vidList */
	public List<Integer> getVidList() {
		return this.vidList;
	}

	/** @return the saveObektDeinost */
	public boolean isSaveObektDeinost() {
		return this.saveObektDeinost;
	}

	/** @param address the address to set */
	public void setAddress(String address) {
		this.address = address;
	}

	/** @param broiFurSurovini the broiFurSurovini to set */
	public void setBroiFurSurovini(Integer broiFurSurovini) {
		this.broiFurSurovini = broiFurSurovini;
	}

	/** @param broiTehnLinii the broiTehnLinii to set */
	public void setBroiTehnLinii(Integer broiTehnLinii) {
		this.broiTehnLinii = broiTehnLinii;
	}

	/** @param cel the cel to set */
	public void setCel(String cel) {
		this.cel = cel;
	}

	/** @param celList the celList to set */
	public void setCelList(List<Integer> celList) {
		this.celList = celList;
	}

	/** @param codeDeinost the codeDeinost to set */
	public void setCodeDeinost(String codeDeinost) {
		this.codeDeinost = codeDeinost;
	}

	/** @param darj the darj to set */
	public void setDarj(Integer darj) {
		this.darj = darj;
	}

	/** @param dbCelList the dbCelList to set */
	public void setDbCelList(List<Integer> dbCelList) {
		this.dbCelList = dbCelList;
	}

	/** @param dbGipList the dbGipList to set */
	public void setDbGipList(List<Integer> dbGipList) {
		this.dbGipList = dbGipList;
	}

	/** @param dbKategoriaList the dbKategoriaList to set */
	public void setDbKategoriaList(List<Integer> dbKategoriaList) {
		this.dbKategoriaList = dbKategoriaList;
	}

	/** @param dbVidList the dbVidList to set */
	public void setDbVidList(List<Integer> dbVidList) {
		this.dbVidList = dbVidList;
	}

	/** @param dopInfo the dopInfo to set */
	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}

	/** @param email the email to set */
	public void setEmail(String email) {
		this.email = email;
	}

	/** @param eventDeinostFurajiPredmet the eventDeinostFurajiPredmet to set */
	public void setEventDeinostFurajiPredmet(List<EventDeinostFurajiPredmet> eventDeinostFurajiPredmet) {
		this.eventDeinostFurajiPredmet = eventDeinostFurajiPredmet;
	}

	/** @param eventDeinostFurajiPrednaznJiv the eventDeinostFurajiPrednaznJiv to set */
	public void setEventDeinostFurajiPrednaznJiv(List<EventDeinostFurajiPrednaznJiv> eventDeinostFurajiPrednaznJiv) {
		this.eventDeinostFurajiPrednaznJiv = eventDeinostFurajiPrednaznJiv;
	}

	/** @param eventDeinostFurajiSert the eventDeinostFurajiSert to set */
	public void setEventDeinostFurajiSert(List<EventDeinostFurajiSert> eventDeinostFurajiSert) {
		this.eventDeinostFurajiSert = eventDeinostFurajiSert;
	}

	/** @param gipList the gipList to set */
	public void setGipList(List<Integer> gipList) {
		this.gipList = gipList;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param idVpisvane the idVpisvane to set */
	public void setIdVpisvane(Integer idVpisvane) {
		this.idVpisvane = idVpisvane;
	}

	/** @param installationType the installationType to set */
	public void setInstallationType(Integer installationType) {
		this.installationType = installationType;
	}

	/** @param kategoriaList the kategoriaList to set */
	public void setKategoriaList(List<Integer> kategoriaList) {
		this.kategoriaList = kategoriaList;
	}

	/** @param kontakt the kontakt to set */
	public void setKontakt(String kontakt) {
		this.kontakt = kontakt;
	}

	/** @param miarka the miarka to set */
	public void setMiarka(Integer miarka) {
		this.miarka = miarka;
	}

	/** @param mpsDeinost the mpsDeinost to set */
	public void setMpsDeinost(List<MpsDeinost> mpsDeinost) {
		this.mpsDeinost = mpsDeinost;
	}

	/** @param naimenovanie the naimenovanie to set */
	public void setNaimenovanie(String naimenovanie) {
		this.naimenovanie = naimenovanie;
	}

	/** @param nomObektDrug the nomObektDrug to set */
	public void setNomObektDrug(String nomObektDrug) {
		this.nomObektDrug = nomObektDrug;
	}

	/** @param nomPartida the nomPartida to set */
	public void setNomPartida(String nomPartida) {
		this.nomPartida = nomPartida;
	}

	/** @param obektDeinostDeinost the obektDeinostDeinost to set */
	public void setObektDeinostDeinost(List<ObektDeinostDeinost> obektDeinostDeinost) {
		this.obektDeinostDeinost = obektDeinostDeinost;
	}

	/** @param opisanie the opisanie to set */
	public void setOpisanie(String opisanie) {
		this.opisanie = opisanie;
	}

	/** @param polzvaneMps the polzvaneMps to set */
	public void setPolzvaneMps(Integer polzvaneMps) {
		this.polzvaneMps = polzvaneMps;
	}

	/** @param proizhodSurovini the proizhodSurovini to set */
	public void setProizhodSurovini(Integer proizhodSurovini) {
		this.proizhodSurovini = proizhodSurovini;
	}

	/** @param regNom the regNom to set */
	public void setRegNom(String regNom) {
		this.regNom = regNom;
	}

	/** @param regNomVrem the regNomVrem to set */
	public void setRegNomVrem(String regNomVrem) {
		this.regNomVrem = regNomVrem;
	}

	/** @param saveObektDeinost the saveObektDeinost to set */
	public void setSaveObektDeinost(boolean saveObektDeinost) {
		this.saveObektDeinost = saveObektDeinost;
	}

	/** @param side the side to set */
	public void setSide(String side) {
		this.side = side;
	}

	/** @param sklad the sklad to set */
	public void setSklad(Integer sklad) {
		this.sklad = sklad;
	}

	/** @param tel the tel to set */
	public void setTel(String tel) {
		this.tel = tel;
	}

	/** @param url the url to set */
	public void setUrl(String url) {
		this.url = url;
	}

	/** @param vidList the vidList to set */
	public void setVidList(List<Integer> vidList) {
		this.vidList = vidList;
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
