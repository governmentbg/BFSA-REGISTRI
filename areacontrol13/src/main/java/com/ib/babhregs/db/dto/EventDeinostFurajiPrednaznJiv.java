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
 * Предназначени за животни
 */
@Entity
@Table(name = "event_deinost_furaji_prednazn_jiv")
public class EventDeinostFurajiPrednaznJiv implements Serializable {

	private static final long serialVersionUID = -3312468017245834793L;

	@SequenceGenerator(name = "EventDeinostFurajiPrednaznJiv", sequenceName = "seq_event_deinost_furaji_prednazn_jiv", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostFurajiPrednaznJiv")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "event_deinost_furaji_id", insertable = false, updatable = false)
	private Integer eventDeinostFurajiId;

	@Column(name = "vid_jiv")
	@JournalAttr(label = "vid_jiv", defaultText = "Вид животно", classifID = "" + BabhConstants.CODE_CLASSIF_VID_JIVOTNO)
	private Integer vidJiv; // (int8)

	@Column(name = "kolichestvo")
	@JournalAttr(label = "kolichestvo", defaultText = "Количество")
	private String kolichestvo; // (varchar(255))

	/**  */
	public EventDeinostFurajiPrednaznJiv() {
		super();
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

	/** @return the vidJiv */
	public Integer getVidJiv() {
		return this.vidJiv;
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

	/** @param vidJiv the vidJiv to set */
	public void setVidJiv(Integer vidJiv) {
		this.vidJiv = vidJiv;
	}
}