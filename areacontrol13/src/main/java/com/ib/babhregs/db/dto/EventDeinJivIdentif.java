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
 * Предмет на дейности с идентификатори
 */
@Entity
@Table(name = "event_deinost_jiv_identif")
public class EventDeinJivIdentif implements Serializable {

	private static final long serialVersionUID = -3628882619186881252L;

	@SequenceGenerator(name = "EventDeinJivIdentif", sequenceName = "seq_event_deinost_jiv_identif", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinJivIdentif")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "event_deinost_jiv_id", insertable = false, updatable = false)
	private Integer eventDeinJivId;

	@Column(name = "vid_identif")
	@JournalAttr(label = "vid_identif", defaultText = "Вид идентификатор", classifID = "" + BabhConstants.CODE_CLASSIF_VID_IDENT_JIV)
	private Integer vidIdentif; // (int8)

	@Column(name = "model")
	@JournalAttr(label = "model", defaultText = "Модел")
	private String model; // (varchar)

	@Column(name = "vid_jiv")
	@JournalAttr(label = "vid_jiv", defaultText = "Вид животно", classifID = "" + BabhConstants.CODE_CLASSIF_VID_JIVOTNO)
	private Integer vidJiv; // (int8)

	@Column(name = "kod")
	@JournalAttr(label = "kod", defaultText = "Код")
	private String kod; // (varchar(100))

	/**  */
	public EventDeinJivIdentif() {
		super();
	}

	/** @return the eventDeinJivId */
	public Integer getEventDeinJivId() {
		return this.eventDeinJivId;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the kod */
	public String getKod() {
		return this.kod;
	}

	/** @return the model */
	public String getModel() {
		return this.model;
	}

	/** @return the vidIdentif */
	public Integer getVidIdentif() {
		return this.vidIdentif;
	}

	/** @return the vidJiv */
	public Integer getVidJiv() {
		return this.vidJiv;
	}

	/** @param eventDeinJivId the eventDeinJivId to set */
	public void setEventDeinJivId(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param kod the kod to set */
	public void setKod(String kod) {
		this.kod = kod;
	}

	/** @param model the model to set */
	public void setModel(String model) {
		this.model = model;
	}

	/** @param vidIdentif the vidIdentif to set */
	public void setVidIdentif(Integer vidIdentif) {
		this.vidIdentif = vidIdentif;
	}

	/** @param vidJiv the vidJiv to set */
	public void setVidJiv(Integer vidJiv) {
		this.vidJiv = vidJiv;
	}
}