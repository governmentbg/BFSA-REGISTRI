package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

/**
 * МПС - лица
 */
@Entity
@Table(name = "mps_lice")
public class MpsLice implements Serializable {

	private static final long serialVersionUID = 1118982439625208519L;

	@SequenceGenerator(name = "MpsLice", sequenceName = "seq_mps_lice", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "MpsLice")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "mps_id", insertable = false, updatable = false)
	private Integer mpsId;

	@Column(name = "tip_vraz")
	@JournalAttr(label = "tipVraz", defaultText = "Тип на връзката", classifID = "" + BabhConstants.CODE_CLASSIF_VRAZ_LICE_OBEKT)
	private Integer tipVraz;

	@Column(name = "code_ref")
	@JournalAttr(label = "codeRef", defaultText = "Лице", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT)
	private Integer codeRef;

	@Transient
	private Referent referent; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	/**  */
	public MpsLice() {
		super();
	}

	/** @return the codeRef */
	public Integer getCodeRef() {
		return this.codeRef;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the mpsId */
	public Integer getMpsId() {
		return this.mpsId;
	}

	/** @return the referent */
	@XmlTransient
	public Referent getReferent() {
		return this.referent;
	}

	/** @return the tipVraz */
	public Integer getTipVraz() {
		return this.tipVraz;
	}

	/** @param codeRef the codeRef to set */
	public void setCodeRef(Integer codeRef) {
		this.codeRef = codeRef;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param mpsId the mpsId to set */
	public void setMpsId(Integer mpsId) {
		this.mpsId = mpsId;
	}

	/** @param referent the referent to set */
	public void setReferent(Referent referent) {
		this.referent = referent;
	}

	/** @param tipVraz the tipVraz to set */
	public void setTipVraz(Integer tipVraz) {
		this.tipVraz = tipVraz;
	}
}