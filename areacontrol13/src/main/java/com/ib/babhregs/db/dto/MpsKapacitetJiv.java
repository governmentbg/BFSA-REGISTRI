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
 * Капацитет за превоз на животни в МПС
 */
@Entity
@Table(name = "mps_kapacitet_jiv")
public class MpsKapacitetJiv implements Serializable {

	private static final long serialVersionUID = 4310589623970520894L;

	@SequenceGenerator(name = "MpsKapacitetJiv", sequenceName = "seq_mps_kapacitet_jiv", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "MpsKapacitetJiv")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "mps_id", insertable = false, updatable = false)
	private Integer mpsId;

	@Column(name = "vid_jiv")
	@JournalAttr(label = "vidJiv", defaultText = "Вид животно за превоз", classifID = "" + BabhConstants.CODE_CLASSIF_VID_JIVOTNO_PREVOZ)
	private Integer vidJiv;

	@Column(name = "broi_jiv")
	@JournalAttr(label = "broi_jiv", defaultText = "Брой животни")
	private Integer broiJiv;

	/**  */
	public MpsKapacitetJiv() {
		super();
	}

	/** @return the broiJiv */
	public Integer getBroiJiv() {
		return this.broiJiv;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the mpsId */
	public Integer getMpsId() {
		return this.mpsId;
	}

	/** @return the vidJiv */
	public Integer getVidJiv() {
		return this.vidJiv;
	}

	/** @param broiJiv the broiJiv to set */
	public void setBroiJiv(Integer broiJiv) {
		this.broiJiv = broiJiv;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param mpsId the mpsId to set */
	public void setMpsId(Integer mpsId) {
		this.mpsId = mpsId;
	}

	/** @param vidJiv the vidJiv to set */
	public void setVidJiv(Integer vidJiv) {
		this.vidJiv = vidJiv;
	}
}