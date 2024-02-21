package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;
import java.util.Date;

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
 * Обект на дейност – Лице
 */
@Entity
@Table(name = "obekt_deinost_lica")
public class ObektDeinostLica implements Serializable  ,Cloneable {

	private static final long serialVersionUID = -6540017887145868410L;

	@SequenceGenerator(name = "ObektDeinostLica", sequenceName = "seq_obekt_deinost_lica", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "ObektDeinostLica")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "obekt_deinost_id", insertable = false, updatable = false)
	private Integer obektDeinostId; // (int8)

	@Column(name = "role")
	@JournalAttr(label = "role", defaultText = "Тип на връзка", classifID = "" + BabhConstants.CODE_CLASSIF_VRAZ_LICE_OBEKT)
	private Integer role; // (int8)

	@Column(name = "date_beg")
	@JournalAttr(label = "date_beg", defaultText = "Начална дата")
	private Date dateBeg; // (timestamp)

	@Column(name = "date_end")
	@JournalAttr(label = "date_end", defaultText = "Крайна дата")
	private Date dateEnd; // (timestamp)

	@Column(name = "code_ref")
	@JournalAttr(label = "code_ref", defaultText = "Лице", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT)
	private Integer codeRef;

	@Transient
	private Referent referent; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	/** */
	public ObektDeinostLica() {
		super();
	}

	public Integer getCodeRef() {
		return this.codeRef;
	}

	/** @return the dateBeg */
	public Date getDateBeg() {
		return this.dateBeg;
	}

	/** @return the dateEnd */
	public Date getDateEnd() {
		return this.dateEnd;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the obektDeinostId */
	public Integer getObektDeinostId() {
		return this.obektDeinostId;
	}

	/** @return the referent */
	@XmlTransient
	public Referent getReferent() {
		return this.referent;
	}

	/** @return the role */
	public Integer getRole() {
		return this.role;
	}

	public void setCodeRef(Integer codeRef) {
		this.codeRef = codeRef;
	}

	/** @param dateBeg the dateBeg to set */
	public void setDateBeg(Date dateBeg) {
		this.dateBeg = dateBeg;
	}

	/** @param dateEnd the dateEnd to set */
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param obektDeinostId the obektDeinostId to set */
	public void setObektDeinostId(Integer obektDeinostId) {
		this.obektDeinostId = obektDeinostId;
	}

	/** @param referent the referent to set */
	public void setReferent(Referent referent) {
		this.referent = referent;
	}

	/** @param role the role to set */
	public void setRole(Integer role) {
		this.role = role;
	}
	
	@Override
	public ObektDeinostLica clone(){
		try {
			ObektDeinostLica cloneL =  (ObektDeinostLica)super.clone();
			
			cloneL.setReferent(referent.clone());
			
			return cloneL;
		}catch (CloneNotSupportedException e) {
			return new ObektDeinostLica();
		}
	}
}
