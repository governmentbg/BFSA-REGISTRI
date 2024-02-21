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
 * Начин на прилагане - Видове животни
 */
@Entity
@Table(name = "vlp_prilagane_vid")
public class VlpPrilaganeVid implements Serializable {

	private static final long serialVersionUID = 140476264573993061L;

	@SequenceGenerator(name = "VlpPrilaganeVid", sequenceName = "seq_vlp_prilagane_vid", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "VlpPrilaganeVid")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "vlp_prilagane_id", insertable = false, updatable = false)
	private Integer vlpPrilaganeId;

	@Column(name = "vid")
	@JournalAttr(label = "vid", defaultText = "Видове животни", classifID = "" + BabhConstants.CODE_CLASSIF_VID_JIV_ES)
	private Integer vid; // (int8)

	/** */
	public VlpPrilaganeVid() {
		super();
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the vid */
	public Integer getVid() {
		return this.vid;
	}

	/** @return the vlpPrilaganeId */
	public Integer getVlpPrilaganeId() {
		return this.vlpPrilaganeId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param vid the vid to set */
	public void setVid(Integer vid) {
		this.vid = vid;
	}

	/** @param vlpPrilaganeId the vlpPrilaganeId to set */
	public void setVlpPrilaganeId(Integer vlpPrilaganeId) {
		this.vlpPrilaganeId = vlpPrilaganeId;
	}
}
