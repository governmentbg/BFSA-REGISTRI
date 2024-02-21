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
 * Предмет на дейността
 */
@Entity
@Table(name = "event_deinost_furaji_predmet")
public class EventDeinostFurajiPredmet implements Serializable {

	private static final long serialVersionUID = 3989328138269649296L;

	@SequenceGenerator(name = "EventDeinostFurajiPredmet", sequenceName = "seq_event_deinost_furaji_predmet", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostFurajiPredmet")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "event_deinost_furaji_id", insertable = false, updatable = false)
	private Integer eventDeinostFurajiId;

	@Column(name = "vid")
	@JournalAttr(label = "vid", defaultText = "Фураж", classifID = "" + BabhConstants.CODE_CLASSIF_VIDOVE_FURAJ)
	private Integer vid;

	@Column(name = "kolichestvo")
	@JournalAttr(label = "kolichestvo", defaultText = "Количество")
	private String kolichestvo; // (varchar(255))

	@Column(name = "prednaznachenie")
	@JournalAttr(label = "prednaznachenie", defaultText = "Предназначение", classifID = "" + BabhConstants.CODE_CLASSIF_PREDNAZNACHENIE_FURAJI)
	private Integer prednaznachenie; // (int8)

	@Column(name = "dop_info")
	@JournalAttr(label = "dop_info", defaultText = "Доп. информация")
	private String dopInfo; // (varchar)

	@Column(name = "sastoianie")
	@JournalAttr(label = "sastoianie", defaultText = "Състояние на предмета")
	private Integer sastoianie; // (int4)

	/**  */
	public EventDeinostFurajiPredmet() {
		super();
	}

	/** @return the dopInfo */
	public String getDopInfo() {
		return this.dopInfo;
	}

	/** @return the eventDeinostFurajiId */
	public Integer getEventDeinostFurajiId() {
		return this.eventDeinostFurajiId;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the kolichestvo */
	public String getKolichestvo() {
		return this.kolichestvo;
	}

	/** @return the prednaznachenie */
	public Integer getPrednaznachenie() {
		return this.prednaznachenie;
	}

	/** @return the sastoianie */
	public Integer getSastoianie() {
		return this.sastoianie;
	}

	/** @return the vid */
	public Integer getVid() {
		return this.vid;
	}

	/** @param dopInfo the dopInfo to set */
	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}

	/** @param eventDeinostFurajiId the eventDeinostFurajiId to set */
	public void setEventDeinostFurajiId(Integer eventDeinostFurajiId) {
		this.eventDeinostFurajiId = eventDeinostFurajiId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param kolichestvo the kolichestvo to set */
	public void setKolichestvo(String kolichestvo) {
		this.kolichestvo = kolichestvo;
	}

	/** @param prednaznachenie the prednaznachenie to set */
	public void setPrednaznachenie(Integer prednaznachenie) {
		this.prednaznachenie = prednaznachenie;
	}

	/** @param sastoianie the sastoianie to set */
	public void setSastoianie(Integer sastoianie) {
		this.sastoianie = sastoianie;
	}

	/** @param vid the vid to set */
	public void setVid(Integer vid) {
		this.vid = vid;
	}
}