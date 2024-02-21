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
@Table(name = "VPISVANE_ACCESS")
public class VpisvaneAccess implements PersistentEntity, AuditExt {

	private static final long serialVersionUID = -8349564033219206676L;

	@SequenceGenerator(name = "VpisvaneAccess", sequenceName = "SEQ_VPISVANE_ACCESS", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "VpisvaneAccess")
	@Column(name = "ID", unique = true, nullable = false)
	private Integer id;

	@Column(name = "VPISVANE_ID")
	private Integer vpisvaneId;

	@Column(name = "CODE_REF")
	@JournalAttr(label = "codeRef", defaultText = "Служител", classifID = "" + Constants.CODE_CLASSIF_ADMIN_STR)
	private Integer codeRef;

	@Transient
	private String identInfo;

	/** */
	public VpisvaneAccess() {
		super();
	}

	/**
	 * @param vpisvaneId
	 * @param codeRef
	 * @param identInfo
	 */
	public VpisvaneAccess(Integer vpisvaneId, Integer codeRef, String identInfo) {
		this.vpisvaneId = vpisvaneId;
		this.codeRef = codeRef;
		this.identInfo = identInfo;
	}

	/** */
	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_IZR_DOST_VP;
	}

	/** @return the codeRef */
	public Integer getCodeRef() {
		return this.codeRef;
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

	/** @return the vpisvaneId */
	public Integer getVpisvaneId() {
		return this.vpisvaneId;
	}

	/** @param vpisvaneId the vpisvaneId to set */
	public void setVpisvaneId(Integer vpisvaneId) {
		this.vpisvaneId = vpisvaneId;
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
		if (this.vpisvaneId != null) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
			journal.setJoinedIdObject1(this.vpisvaneId);
		}
		return journal;
	}
}