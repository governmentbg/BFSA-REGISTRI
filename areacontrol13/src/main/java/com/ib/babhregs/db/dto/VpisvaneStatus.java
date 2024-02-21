package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

/**
 * Вписване - Статус
 */
@Entity
@Table(name = "vpisvane_status")
public class VpisvaneStatus extends TrackableEntity implements AuditExt {

	private static final long serialVersionUID = -6890632570204020919L;

	@SequenceGenerator(name = "VpisvaneStatus", sequenceName = "seq_vpisvane_status", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "VpisvaneStatus")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "id_vpisvane")
	@JournalAttr(label = "id_vpisvane", defaultText = "Вписване", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE)
	private Integer idVpisvane;

	@Column(name = "status")
	@JournalAttr(label = "status", defaultText = "Статус", classifID = "" + BabhConstants.CODE_CLASSIF_STATUS_VPISVANE)
	private Integer status;

	@Column(name = "date_status")
	@JournalAttr(label = "date_status", defaultText = "Статус Дата", dateMask = "dd.MM.yyyy HH:mm:ss")
	private Date dateStatus;

	@Column(name = "reason")
	@JournalAttr(label = "reason", defaultText = "Основание", classifID = "" + BabhConstants.CODE_CLASSIF_OSN_STATUS_VPISVANE)
	private Integer reason;

	@Column(name = "reason_dop")
	@JournalAttr(label = "reason_dop", defaultText = "Основание текст")
	private String reasonDop;

	@Column(name = "reg_nom_zapoved")
	@JournalAttr(label = "reg_nom_zapoved", defaultText = "Рег. номер на заповед за статуса")
	private String regNomZapoved; // (varchar(100))

	@Column(name = "date_zapoved")
	@JournalAttr(label = "date_zapoved", defaultText = "Дата на заповед за статуса")
	private Date dateZapoved; // (timestamp)

	@Column(name = "reg_nom_protokol")
	@JournalAttr(label = "reg_nom_protokol", defaultText = "Рег. номер на протокол за статуса")
	private String regNomProtokol; // (varchar(100))

	@Column(name = "date_protokol")
	@JournalAttr(label = "date_protokol", defaultText = "Дата на протокол за статуса")
	private Date dateProtokol; // (timestamp)

	/**  */
	public VpisvaneStatus() {
		super();
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE_STATUS;
	}

	/** @return the dateProtokol */
	public Date getDateProtokol() {
		return this.dateProtokol;
	}

	/** @return the dateStatus */
	public Date getDateStatus() {
		return this.dateStatus;
	}

	/** @return the dateZapoved */
	public Date getDateZapoved() {
		return this.dateZapoved;
	}

	/** @return the id */
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public String getIdentInfo() throws DbErrorException {
		return "/vp_id=" + this.idVpisvane + "/";
	}

	/** @return the idVpisvane */
	public Integer getIdVpisvane() {
		return this.idVpisvane;
	}

	/** @return the reason */
	public Integer getReason() {
		return this.reason;
	}

	/** @return the reasonDop */
	public String getReasonDop() {
		return this.reasonDop;
	}

	/** @return the regNomProtokol */
	public String getRegNomProtokol() {
		return this.regNomProtokol;
	}

	/** @return the regNomZapoved */
	public String getRegNomZapoved() {
		return this.regNomZapoved;
	}

	/** @return the status */
	public Integer getStatus() {
		return this.status;
	}

	/** @param dateProtokol the dateProtokol to set */
	public void setDateProtokol(Date dateProtokol) {
		this.dateProtokol = dateProtokol;
	}

	/** @param dateStatus the dateStatus to set */
	public void setDateStatus(Date dateStatus) {
		this.dateStatus = dateStatus;
	}

	/** @param dateZapoved the dateZapoved to set */
	public void setDateZapoved(Date dateZapoved) {
		this.dateZapoved = dateZapoved;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param idVpisvane the idVpisvane to set */
	public void setIdVpisvane(Integer idVpisvane) {
		this.idVpisvane = idVpisvane;
	}

	/** @param reason the reason to set */
	public void setReason(Integer reason) {
		this.reason = reason;
	}

	/** @param reasonDop the reasonDop to set */
	public void setReasonDop(String reasonDop) {
		this.reasonDop = reasonDop;
	}

	/** @param regNomProtokol the regNomProtokol to set */
	public void setRegNomProtokol(String regNomProtokol) {
		this.regNomProtokol = regNomProtokol;
	}

	/** @param regNomZapoved the regNomZapoved to set */
	public void setRegNomZapoved(String regNomZapoved) {
		this.regNomZapoved = regNomZapoved;
	}

	/** @param status the status to set */
	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());

		if (this.idVpisvane != null) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
			journal.setJoinedIdObject1(this.idVpisvane);
		}
		return journal;
	}
}