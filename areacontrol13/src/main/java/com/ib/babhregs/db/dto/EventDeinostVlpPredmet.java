package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ib.system.db.JournalAttr;

/**
 * Предмет на дейността
 */
@Entity
@Table(name = "event_deinost_vlp_predmet")
public class EventDeinostVlpPredmet implements Serializable {

	private static final long serialVersionUID = -8793893384151669195L;

	@SequenceGenerator(name = "EventDeinostVlpPredmet", sequenceName = "seq_event_deinost_vlp_predmet", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostVlpPredmet")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "id_event_deinost_vlp", insertable = false, updatable = false)
	private Integer idEventDeinostVlp;

	@Column(name = "code_classif")
	@JournalAttr(label = "code_classif", defaultText = "Класификация на предмет", classifID = "-1")
	private Integer codeClassif;

	@Column(name = "predmet")
	@JournalAttr(label = "predmet", defaultText = "Предмет", classifField = "codeClassif")
	private Integer predmet;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "vid_identifier")
	@JournalAttr(label = "vid_identifier", defaultText = "Код вещество")
	private Substance vidIdentifier; // (varchar(25))

	@Column(name = "naimenovanie")
	@JournalAttr(label = "naimenovanie", defaultText = "Наименование")
	private String naimenovanie; // (varchar)

	@Column(name = "kolichestvo")
	@JournalAttr(label = "kolichestvo", defaultText = "Количество в една опаковка")
	private String kolichestvo; // (varchar)

	@Column(name = "srok")
	@JournalAttr(label = "srok", defaultText = "Срок на годност")
	private String srok; // (varchar)

	@Column(name = "sahranenie")
	@JournalAttr(label = "sahranenie", defaultText = "Условия за съхранение")
	private String sahranenie; // (varchar)

	@Column(name = "dop_info")
	@JournalAttr(label = "dop_info", defaultText = "Доп. информация")
	private String dopInfo; // (varchar)

	@Column(name = "naimenovanie_lat")
	@JournalAttr(label = "naimenovanie_lat", defaultText = "Наименование на латиница")
	private String naimenovanieLat; // (varchar(500))

	@Column(name = "mer_edinica")
	@JournalAttr(label = "mer_edinica", defaultText = "Мерна единица")
	private String merEdinica; // (varchar(100))

	@Column(name = "standart")
	@JournalAttr(label = "standart", defaultText = "Стандарт или монография")
	private String standart; // (varchar(255))

	/**  */
	public EventDeinostVlpPredmet() {
		super();
	}

	/** @return the type */
	public Integer getCodeClassif() {
		return this.codeClassif;
	}

	/** @return the dopInfo */
	public String getDopInfo() {
		return this.dopInfo;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the idEventDeinostVlp */
	public Integer getIdEventDeinostVlp() {
		return this.idEventDeinostVlp;
	}

	/** @return the kolichestvo */
	public String getKolichestvo() {
		return this.kolichestvo;
	}

	public String getMerEdinica() {
		return this.merEdinica;
	}

	/** @return the naimenovanie */
	public String getNaimenovanie() {
		return this.naimenovanie;
	}

	public String getNaimenovanieLat() {
		return this.naimenovanieLat;
	}

	/** @return the predmet */
	public Integer getPredmet() {
		return this.predmet;
	}

	/** @return the sahranenie */
	public String getSahranenie() {
		return this.sahranenie;
	}

	/** @return the srok */
	public String getSrok() {
		return this.srok;
	}

	public String getStandart() {
		return this.standart;
	}

	/** @return the vidIdentifier */
	public Substance getVidIdentifier() {
		return this.vidIdentifier;
	}

	/** @param codeClassif the codeClassif to set */
	public void setCodeClassif(Integer codeClassif) {
		this.codeClassif = codeClassif;
	}

	/** @param dopInfo the dopInfo to set */
	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param idEventDeinostVlp the idEventDeinostVlp to set */
	public void setIdEventDeinostVlp(Integer idEventDeinostVlp) {
		this.idEventDeinostVlp = idEventDeinostVlp;
	}

	/** @param kolichestvo the kolichestvo to set */
	public void setKolichestvo(String kolichestvo) {
		this.kolichestvo = kolichestvo;
	}

	public void setMerEdinica(String merEdinica) {
		this.merEdinica = merEdinica;
	}

	/** @param naimenovanie the naimenovanie to set */
	public void setNaimenovanie(String naimenovanie) {
		this.naimenovanie = naimenovanie;
	}

	public void setNaimenovanieLat(String naimenovanieLat) {
		this.naimenovanieLat = naimenovanieLat;
	}

	/** @param predmet the predmet to set */
	public void setPredmet(Integer predmet) {
		this.predmet = predmet;
	}

	/** @param sahranenie the sahranenie to set */
	public void setSahranenie(String sahranenie) {
		this.sahranenie = sahranenie;
	}

	/** @param srok the srok to set */
	public void setSrok(String srok) {
		this.srok = srok;
	}

	public void setStandart(String standart) {
		this.standart = standart;
	}

	/** @param vidIdentifier the vidIdentifier to set */
	public void setVidIdentifier(Substance vidIdentifier) {
		this.vidIdentifier = vidIdentifier;
	}
}