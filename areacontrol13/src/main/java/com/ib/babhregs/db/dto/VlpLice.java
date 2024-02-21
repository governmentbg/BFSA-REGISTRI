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
 * ВЛП – Лице
 */
@Entity
@Table(name = "vlp_lice")
public class VlpLice implements Serializable {

	private static final long serialVersionUID = 2680709264096340475L;

	@SequenceGenerator(name = "VlpLice", sequenceName = "seq_vlp_lice", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "VlpLice")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "vlp_id", insertable = false, updatable = false)
	private Integer vlpId;

	@Column(name = "tip")
	@JournalAttr(label = "tip", defaultText = "Тип на връзката", classifID = "" + BabhConstants.CODE_CLASSIF_VRAZ_LICE_OBEKT)
	private Integer tip; // (int8)

	@Column(name = "code_ref")
	@JournalAttr(label = "code_ref", defaultText = "Лице", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT)
	private Integer codeRef; // (int8)

	@Column(name = "date_beg")
	@JournalAttr(label = "date_beg", defaultText = "Начална дата")
	private Date dateBeg; // (timestamp)

	@Column(name = "date_end")
	@JournalAttr(label = "date_end", defaultText = "Крайна дата")
	private Date dateEnd; // (timestamp)
	
	
	@Column(name = "dop_info")
	@JournalAttr(label = "dopInfo", defaultText = "Допълнителна информация")
	private String dopInfo; 
	

	@Transient
	private Referent referent = new Referent(); // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	/** */
	public VlpLice() {
		super();
	}

	/** @return the codeRef */
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

	/** @return the referent */
	@XmlTransient
	public Referent getReferent() {
		return this.referent;
	}

	/** @return the tip */
	public Integer getTip() {
		return this.tip;
	}

	/** @return the vlpId */
	public Integer getVlpId() {
		return this.vlpId;
	}

	/** @param codeRef the codeRef to set */
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

	/** @param referent the referent to set */
	public void setReferent(Referent referent) {
		this.referent = referent;
	}

	/** @param tip the tip to set */
	public void setTip(Integer tip) {
		this.tip = tip;
	}

	/** @param vlpId the vlpId to set */
	public void setVlpId(Integer vlpId) {
		this.vlpId = vlpId;
	}

	public String getDopInfo() {
		return dopInfo;
	}

	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}
	
	
}
