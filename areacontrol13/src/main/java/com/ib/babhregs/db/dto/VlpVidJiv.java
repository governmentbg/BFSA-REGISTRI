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
 * Вид животни, за които е предназначен ВЛП
 */
@Entity
@Table(name = "vlp_vid_jiv")
public class VlpVidJiv implements Serializable {

	private static final long serialVersionUID = 5735504346274485732L;

	@SequenceGenerator(name = "VlpVidJiv", sequenceName = "seq_vlp_vid_jiv", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "VlpVidJiv")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "vlp_id", insertable = false, updatable = false)
	private Integer vlpId;

	@Column(name = "vid_jiv")
	@JournalAttr(label = "vid_jiv", defaultText = "Вид животни, за които е предназначен ВЛП", classifID = "" + BabhConstants.CODE_CLASSIF_VID_JIVOTNO)
	private Integer vidJiv; // (int8)

	/** */
	public VlpVidJiv() {
		super();
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the vidJiv */
	public Integer getVidJiv() {
		return this.vidJiv;
	}

	/** @return the vlpId */
	public Integer getVlpId() {
		return this.vlpId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param vidJiv the vidJiv to set */
	public void setVidJiv(Integer vidJiv) {
		this.vidJiv = vidJiv;
	}

	/** @param vlpId the vlpId to set */
	public void setVlpId(Integer vlpId) {
		this.vlpId = vlpId;
	}
}
