package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

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
 * Вписване - Документи
 */
@Entity
@Table(name = "vpisvane_doc")
public class VpisvaneDoc extends TrackableEntity implements AuditExt {

	private static final long serialVersionUID = -6936927121797742783L;

	@SequenceGenerator(name = "VpisvaneDoc", sequenceName = "seq_vpisvane_doc", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "VpisvaneDoc")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "id_vpisvane")
	@JournalAttr(label = "id_vpisvane", defaultText = "Вписване", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE)
	private Integer idVpisvane;

	@Column(name = "id_doc")
	@JournalAttr(label = "id_doc", defaultText = "Документ", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC)
	private Integer idDoc;

	@Column(name = "prev_ud_id")
	@JournalAttr(label = "prev_ud_id", defaultText = "Предишен Удост.Док.", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC)
	private Integer prevUdId;

	@Column(name = "curr_zaiav_id")
	@JournalAttr(label = "curr_zaiav_id", defaultText = "Текущо заявление", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC)
	private Integer currZaiavId;

	/**  */
	public VpisvaneDoc() {
		super();
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE_DOC;
	}

	/** @return the currZaiavId */
	public Integer getCurrZaiavId() {
		return this.currZaiavId;
	}

	/** @return the id */
	@Override
	public Integer getId() {
		return this.id;
	}

	/** @return the idDoc */
	public Integer getIdDoc() {
		return this.idDoc;
	}

	@Override
	public String getIdentInfo() throws DbErrorException {
		return "/vp_id=" + this.idVpisvane + "/";
	}

	/** @return the idVpisvane */
	public Integer getIdVpisvane() {
		return this.idVpisvane;
	}

	/** @return the prevUdId */
	public Integer getPrevUdId() {
		return this.prevUdId;
	}

	/** @param currZaiavId the currZaiavId to set */
	public void setCurrZaiavId(Integer currZaiavId) {
		this.currZaiavId = currZaiavId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param idDoc the idDoc to set */
	public void setIdDoc(Integer idDoc) {
		this.idDoc = idDoc;
	}

	/** @param idVpisvane the idVpisvane to set */
	public void setIdVpisvane(Integer idVpisvane) {
		this.idVpisvane = idVpisvane;
	}

	/** @param prevUdId the prevUdId to set */
	public void setPrevUdId(Integer prevUdId) {
		this.prevUdId = prevUdId;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());

		if (this.idVpisvane != null) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
			journal.setJoinedIdObject1(this.idVpisvane);
		}
		if (this.idDoc != null) {
			journal.setJoinedCodeObject2(BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
			journal.setJoinedIdObject2(this.idDoc);
		}

		return journal;
	}
}