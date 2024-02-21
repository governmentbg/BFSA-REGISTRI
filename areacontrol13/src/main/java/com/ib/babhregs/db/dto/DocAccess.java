package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.system.Constants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.PersistentEntity;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

@Entity
@Table(name = "DOC_ACCESS")
public class DocAccess implements PersistentEntity, AuditExt {

	private static final long serialVersionUID = 3519849066687075691L;

	@SequenceGenerator(name = "DocAccess", sequenceName = "SEQ_DOC_ACCESS", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "DocAccess")
	@Column(name = "ID", unique = true, nullable = false)
	private Integer id;

	@Column(name = "DOC_ID")
	private Integer docId;

	@Column(name = "CODE_REF")
	@JournalAttr(label = "codeRef", defaultText = "Служител", classifID = "" + Constants.CODE_CLASSIF_ADMIN_STR)
	private Integer codeRef;

	@Transient
	private String identInfo;

	/** */
	public DocAccess() {
		super();
	}

	/**
	 * @param docId
	 * @param codeRef
	 * @param identInfo
	 */
	public DocAccess(Integer docId, Integer codeRef, String identInfo) {
		this.docId = docId;
		this.codeRef = codeRef;
		this.identInfo = identInfo;
	}

	/** */
	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_IZR_DOST;
	}

	/** @return the codeRef */
	public Integer getCodeRef() {
		return this.codeRef;
	}

	/** @return the docId */
	public Integer getDocId() {
		return this.docId;
	}

	/** @return the id */
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public String getIdentInfo() throws DbErrorException {
		return this.identInfo;
	}

	/** @param codeRef the codeRef to set */
	public void setCodeRef(Integer codeRef) {
		this.codeRef = codeRef;
	}

	/** @param docId the docId to set */
	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param identInfo the identInfo to set */
	public void setIdentInfo(String identInfo) {
		this.identInfo = identInfo;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());
		if (this.docId != null) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
			journal.setJoinedIdObject1(this.docId);
		}
		return journal;
	}
}