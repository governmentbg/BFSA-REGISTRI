package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

/**
 * Първична опаковка
 */
@Entity
@Table(name = "event_deinost_vlp_prvnos_opakovka")
public class EventDeinostVlpPrvnosOpakovka implements Serializable {

	private static final long serialVersionUID = 6199623751666450561L;

	@SequenceGenerator(name = "EventDeinostVlpPrvnosOpakovka", sequenceName = "seq_event_deinost_vlp_prvnos_opakovka", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostVlpPrvnosOpakovka")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "event_deinost_vlp_prvnos_id", insertable = false, updatable = false)
	private Integer eventDeinostVlpPrvnosId;

	@Column(name = "opakovka")
	@JournalAttr(label = "opakovka", defaultText = "Първична опаковка", classifID = "" + BabhConstants.CODE_CLASSIF_PARVICHNA_OPAKOVKA)
	private Integer opakovka; // (int8)

	@Column(name = "kolichestvo")
	@JournalAttr(label = "kolichestvo", defaultText = "Количество в една опаковка")
	private String kolichestvo; // (varchar)

	/**  */
	public EventDeinostVlpPrvnosOpakovka() {
		super();
	}

	/** @return the eventDeinostVlpPrvnosId */
	public Integer getEventDeinostVlpPrvnosId() {
		return this.eventDeinostVlpPrvnosId;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the kolichestvo */
	public String getKolichestvo() {
		return this.kolichestvo;
	}

	/** @return the opakovka */
	public Integer getOpakovka() {
		return this.opakovka;
	}

	/** @param eventDeinostVlpPrvnosId the eventDeinostVlpPrvnosId to set */
	public void setEventDeinostVlpPrvnosId(Integer eventDeinostVlpPrvnosId) {
		this.eventDeinostVlpPrvnosId = eventDeinostVlpPrvnosId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param kolichestvo the kolichestvo to set */
	public void setKolichestvo(String kolichestvo) {
		this.kolichestvo = kolichestvo;
	}

	/** @param opakovka the opakovka to set */
	public void setOpakovka(Integer opakovka) {
		this.opakovka = opakovka;
	}
}