package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;
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

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

/**
 * ВЛП за производство/ внос
 */
@Entity
@Table(name = "event_deinost_vlp_prvnos")
public class EventDeinostVlpPrvnos implements Serializable {

	private static final long serialVersionUID = -3417417027777140141L;

	@SequenceGenerator(name = "EventDeinostPrvnos", sequenceName = "seq_event_deinost_vlp_prvnos", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostPrvnos")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "event_deinost_vlp_id", insertable = false, updatable = false)
	private Integer eventDeinostVlpId;

	@Column(name = "naimenovanie")
	@JournalAttr(label = "naimenovanie", defaultText = "Търговско име")
	private String naimenovanie; // (varchar)

	@Column(name = "forma")
	@JournalAttr(label = "forma", defaultText = "Фармацевтична форма", classifID = "" + BabhConstants.CODE_CLASSIF_PHARM_FORMI)
	private Integer forma; // (int8)

	@Column(name = "prilozenie")
	@JournalAttr(label = "prilozenie", defaultText = "Начин на приложение")
	private String prilozenie; // (varchar)

	// това долното се явява на трето ниво
	@JournalAttr(label = "eventDeinostVlpPrvnosSubst", defaultText = "Активни субстанции")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "event_deinost_vlp_prvnos_id", referencedColumnName = "id", nullable = false)
	private List<EventDeinostVlpPrvnosSubst> eventDeinostVlpPrvnosSubst;

	// това долното се явява на трето ниво
	@JournalAttr(label = "eventDeinostVlpPrvnosOpakovka", defaultText = "Първични опаковки")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "event_deinost_vlp_prvnos_id", referencedColumnName = "id", nullable = false)
	private List<EventDeinostVlpPrvnosOpakovka> eventDeinostVlpPrvnosOpakovka;

	/**  */
	public EventDeinostVlpPrvnos() {
		super();
	}

	/** @return the eventDeinostVlpId */
	public Integer getEventDeinostVlpId() {
		return this.eventDeinostVlpId;
	}

	/** @return the eventDeinostVlpPrvnosOpakovka */
	public List<EventDeinostVlpPrvnosOpakovka> getEventDeinostVlpPrvnosOpakovka() {
		return this.eventDeinostVlpPrvnosOpakovka;
	}

	/** @return the eventDeinostVlpPrvnosSubst */
	public List<EventDeinostVlpPrvnosSubst> getEventDeinostVlpPrvnosSubst() {
		return this.eventDeinostVlpPrvnosSubst;
	}

	/** @return the forma */
	public Integer getForma() {
		return this.forma;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the naimenovanie */
	public String getNaimenovanie() {
		return this.naimenovanie;
	}

	/** @return the prilozenie */
	public String getPrilozenie() {
		return this.prilozenie;
	}

	/** @param eventDeinostVlpId the eventDeinostVlpId to set */
	public void setEventDeinostVlpId(Integer eventDeinostVlpId) {
		this.eventDeinostVlpId = eventDeinostVlpId;
	}

	/** @param eventDeinostVlpPrvnosOpakovka the eventDeinostVlpPrvnosOpakovka to set */
	public void setEventDeinostVlpPrvnosOpakovka(List<EventDeinostVlpPrvnosOpakovka> eventDeinostVlpPrvnosOpakovka) {
		this.eventDeinostVlpPrvnosOpakovka = eventDeinostVlpPrvnosOpakovka;
	}

	/** @param eventDeinostVlpPrvnosSubst the eventDeinostVlpPrvnosSubst to set */
	public void setEventDeinostVlpPrvnosSubst(List<EventDeinostVlpPrvnosSubst> eventDeinostVlpPrvnosSubst) {
		this.eventDeinostVlpPrvnosSubst = eventDeinostVlpPrvnosSubst;
	}

	/** @param forma the forma to set */
	public void setForma(Integer forma) {
		this.forma = forma;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param naimenovanie the naimenovanie to set */
	public void setNaimenovanie(String naimenovanie) {
		this.naimenovanie = naimenovanie;
	}

	/** @param prilozenie the prilozenie to set */
	public void setPrilozenie(String prilozenie) {
		this.prilozenie = prilozenie;
	}
}