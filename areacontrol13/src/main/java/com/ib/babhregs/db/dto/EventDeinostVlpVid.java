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
 * Вид на дейността
 */
@Entity
@Table(name = "event_deinost_vlp_vid")
public class EventDeinostVlpVid implements Serializable {

	private static final long serialVersionUID = -1218366361421794811L;

	@SequenceGenerator(name = "EventDeinostVlpVid", sequenceName = "seq_event_deinost_vlp_vid", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostVlpVid")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "id_event_deinost_vlp", insertable = false, updatable = false)
	private Integer idEventDeinostVlp;

	@Column(name = "vid")
	@JournalAttr(label = "vid", defaultText = "Вид на дейността", classifID = "" + BabhConstants.CODE_CLASSIF_VID_DEINOST)
	private Integer vid;

	/**  */
	public EventDeinostVlpVid() {
		super();
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the idEventDeinostVlp */
	public Integer getIdEventDeinostVlp() {
		return this.idEventDeinostVlp;
	}

	/** @return the vid */
	public Integer getVid() {
		return this.vid;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param idEventDeinostVlp the idEventDeinostVlp to set */
	public void setIdEventDeinostVlp(Integer idEventDeinostVlp) {
		this.idEventDeinostVlp = idEventDeinostVlp;
	}

	/** @param vid the vid to set */
	public void setVid(Integer vid) {
		this.vid = vid;
	}
}