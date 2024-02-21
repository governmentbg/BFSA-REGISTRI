package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;
import com.ib.system.exceptions.DbErrorException;

/**
 * Общото за всички вариянти на мпс в тази таблица
 *
 * @author belev
 */
@MappedSuperclass
public abstract class MpsBase extends TrackableEntity {

	private static final long serialVersionUID = 930861666906660006L;

	@SequenceGenerator(name = "Mps", sequenceName = "seq_mps", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "Mps")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "vid")
	@JournalAttr(label = "vid", defaultText = "Вид", classifID = "" + BabhConstants.CODE_CLASSIF_VID_MPS)
	private Integer vid;

	@Column(name = "model")
	@JournalAttr(label = "model", defaultText = "Марка и модел")
	private String model;

	@Column(name = "nomer")
	@JournalAttr(label = "nomer", defaultText = "Номер")
	private String nomer;

	/**  */
	protected MpsBase() {
		super();
	}

	/** @return the id */
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public String getIdentInfo() throws DbErrorException {
		return this.nomer;
	}

	/** @return the model */
	public String getModel() {
		return this.model;
	}

	/** @return the nomer */
	public String getNomer() {
		return this.nomer;
	}

	/** @return the vid */
	public Integer getVid() {
		return this.vid;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param model the model to set */
	public void setModel(String model) {
		this.model = model;
	}

	/** @param nomer the nomer to set */
	public void setNomer(String nomer) {
		this.nomer = nomer;
	}

	/** @param vid the vid to set */
	public void setVid(Integer vid) {
		this.vid = vid;
	}
}