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
 * Дейност – физически лица
 */
@Entity
@Table(name = "event_deinost_jiv_lice")
public class EventDeinJivLice implements Serializable {

	private static final long serialVersionUID = 8682701811940479239L;

	@SequenceGenerator(name = "EventDeinJivLice", sequenceName = "seq_event_deinost_jiv_lice", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinJivLice")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "event_deinost_jiv_id", insertable = false, updatable = false)
	private Integer eventDeinJivId;

	@Column(name = "tip_vraz")
	@JournalAttr(label = "tip_vraz", defaultText = "Тип на връзка", classifID = "" + BabhConstants.CODE_CLASSIF_VRAZ_SYBITIE_OBEKT)
	private Integer tipVraz; // (int8)

	@Column(name = "code_ref")
	@JournalAttr(label = "code_ref", defaultText = "Лице", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT)
	private Integer codeRef; // (int8)

	@Column(name = "date_beg")
	@JournalAttr(label = "date_beg", defaultText = "Начална дата")
	private Date dateBeg; // (timestamp)

	@Column(name = "date_end")
	@JournalAttr(label = "date_end", defaultText = "Крайна дата")
	private Date dateEnd; // (timestamp)

	@Transient
	private Referent referent; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	/**  */
	public EventDeinJivLice() {
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

	/** @return the eventDeinJivId */
	public Integer getEventDeinJivId() {
		return this.eventDeinJivId;
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

	/** @return the tipVraz */
	public Integer getTipVraz() {
		return this.tipVraz;
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

	/** @param eventDeinJivId the eventDeinJivId to set */
	public void setEventDeinJivId(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
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