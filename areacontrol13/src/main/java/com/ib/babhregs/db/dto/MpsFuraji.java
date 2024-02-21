package com.ib.babhregs.db.dto;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

/**
 * МПС за фуражи
 */
@Entity
@Table(name = "mps")
public class MpsFuraji extends MpsBase implements AuditExt {

	private static final long serialVersionUID = 930861666906660006L;

	@JournalAttr(label = "mpsVidProducts", defaultText = "Фуражи за превоз")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "mps_id", referencedColumnName = "id", nullable = false)
	private List<MpsVidProducts> mpsVidProducts;

	/**  */
	public MpsFuraji() {
		super();
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_MPS_FURAJI;
	}

	/** @return the mpsVidProducts */
	public List<MpsVidProducts> getMpsVidProducts() {
		return this.mpsVidProducts;
	}

	/** @param mpsVidProducts the mpsVidProducts to set */
	public void setMpsVidProducts(List<MpsVidProducts> mpsVidProducts) {
		this.mpsVidProducts = mpsVidProducts;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());
		return journal;
	}
}