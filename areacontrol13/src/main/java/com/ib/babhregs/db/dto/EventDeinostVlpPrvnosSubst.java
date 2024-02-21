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
import javax.persistence.Transient;

import com.ib.system.db.JournalAttr;

/**
 * Активни субстанции
 */
@Entity
@Table(name = "event_deinost_vlp_prvnos_subst")
public class EventDeinostVlpPrvnosSubst implements Serializable {

	private static final long serialVersionUID = -1218366361421794811L;

	@SequenceGenerator(name = "EventDeinostVlpPrvnosSubst", sequenceName = "seq_event_deinost_vlp_prvnos_subst", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostVlpPrvnosSubst")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "event_deinost_vlp_prvnos_id", insertable = false, updatable = false)
	private Integer eventDeinostVlpPrvnosId;

	@Transient // TODO като се смени да се използва vidIdentifier да се махне
	private Integer vid; // (int8)

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "vid_identifier")
	@JournalAttr(label = "vid_identifier", defaultText = "Код вещество")
	private Substance vidIdentifier; // (varchar(25))

	@Column(name = "dop_info")
	@JournalAttr(label = "dopInfo", defaultText = "Пояснение")
	private String dopInfo; // (varchar)

	@Column(name = "doza")
	@JournalAttr(label = "doza", defaultText = "Съдържание в дозова единица")
	private String doza; // (varchar)

	/**  */
	public EventDeinostVlpPrvnosSubst() {
		super();
	}

	/** @return the dopInfo */
	public String getDopInfo() {
		return this.dopInfo;
	}

	/** @return the doza */
	public String getDoza() {
		return this.doza;
	}

	/** @return the eventDeinostVlpPrvnosId */
	public Integer getEventDeinostVlpPrvnosId() {
		return this.eventDeinostVlpPrvnosId;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the vid */
	public Integer getVid() {
		return this.vid;
	}

	/** @return the vidIdentifier */
	public Substance getVidIdentifier() {
		return this.vidIdentifier;
	}

	/** @param dopInfo the dopInfo to set */
	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}

	/** @param doza the doza to set */
	public void setDoza(String doza) {
		this.doza = doza;
	}

	/** @param eventDeinostVlpPrvnosId the eventDeinostVlpPrvnosId to set */
	public void setEventDeinostVlpPrvnosId(Integer eventDeinostVlpPrvnosId) {
		this.eventDeinostVlpPrvnosId = eventDeinostVlpPrvnosId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param vid the vid to set */
	public void setVid(Integer vid) {
		this.vid = vid;
	}

	/** @param vidIdentifier the vidIdentifier to set */
	public void setVidIdentifier(Substance vidIdentifier) {
		this.vidIdentifier = vidIdentifier;
	}
}