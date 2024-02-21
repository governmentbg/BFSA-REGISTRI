package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ib.system.db.JournalAttr;

/**
 * Предмет на дейността
 */
@Entity
@Table(name = "event_deinost_jiv_predmet")
public class EventDeinJivPredmet implements Serializable {

	private static final long serialVersionUID = -3628882619186881252L;

	@SequenceGenerator(name = "EventDeinJivPredmet", sequenceName = "seq_event_deinost_jiv_predmet", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinJivPredmet")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "event_deinost_jiv_id", insertable = false, updatable = false)
	private Integer eventDeinJivId;

	@Column(name = "code_classif")
	@JournalAttr(label = "code_classif", defaultText = "Класификация на предмет", classifID = "-1")
	private Integer codeClassif;

	@Column(name = "predmet")
	@JournalAttr(label = "predmet", defaultText = "Предмет", classifField = "codeClassif")
	private Integer predmet;

	@Column(name = "broi")
	@JournalAttr(label = "broi", defaultText = "Брой")
	private Integer broi;

	@Column(name = "dop_info")
	@JournalAttr(label = "dop_info", defaultText = "Доп. информация")
	private String dopInfo; // (varchar)

	/**  */
	public EventDeinJivPredmet() {
		super();
	}

	/** @return the broi */
	public Integer getBroi() {
		return this.broi;
	}

	/** @return the codeClassif */
	public Integer getCodeClassif() {
		return this.codeClassif;
	}

	/** @return the dopInfo */
	public String getDopInfo() {
		return this.dopInfo;
	}

	/** @return the eventDeinJivId */
	public Integer getEventDeinJivId() {
		return this.eventDeinJivId;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the predmet */
	public Integer getPredmet() {
		return this.predmet;
	}

	/** @param broi the broi to set */
	public void setBroi(Integer broi) {
		this.broi = broi;
	}

	/** @param codeClassif the codeClassif to set */
	public void setCodeClassif(Integer codeClassif) {
		this.codeClassif = codeClassif;
	}

	/** @param dopInfo the dopInfo to set */
	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}

	/** @param eventDeinJivId the eventDeinJivId to set */
	public void setEventDeinJivId(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param predmet the predmet to set */
	public void setPredmet(Integer predmet) {
		this.predmet = predmet;
	}
}