package com.ib.babhregs.db.dto;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

/**
 * Обект на дейност
 */
@Entity
@Table(name = "obekt_deinost")
public class ObektDeinost extends ObektDeinostBase implements AuditExt {

	private static final long serialVersionUID = 160482707815083271L;

	// за obekt_deinost_prednaznachenie.prednaznachenie
	@Transient
	@JournalAttr(label = "prednaznachenieList", defaultText = "Предназначение", classifID = "" + BabhConstants.CODE_CLASSIF_PREDN_OBEKT)
	private List<Integer>	prednaznachenieList;
	@Transient
	private List<Integer>	dbPrednaznachenieList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	/**  */
	public ObektDeinost() {
		super();
	}

	@Override
	public Integer getCodeMainObject() {
		if (getVid() != null && getVid().intValue() == BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLZ) {
			return BabhConstants.CODE_ZNACHENIE_JOURNAL_VLZ; // са сега само тези + ОЕЗ (OezReg) са с разпознаваеми кодове
		}
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_OBEKT_DEINOST;
	}

	/** @return the dbPrednaznachenieList */
	@XmlTransient
	public List<Integer> getDbPrednaznachenieList() {
		return this.dbPrednaznachenieList;
	}

	/** @return the prednaznachenieList */
	public List<Integer> getPrednaznachenieList() {
		return this.prednaznachenieList;
	}

	/** @param dbPrednaznachenieList the dbPrednaznachenieList to set */
	public void setDbPrednaznachenieList(List<Integer> dbPrednaznachenieList) {
		this.dbPrednaznachenieList = dbPrednaznachenieList;
	}

	/** @param prednaznachenieList the prednaznachenieList to set */
	public void setPrednaznachenieList(List<Integer> prednaznachenieList) {
		this.prednaznachenieList = prednaznachenieList;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());
		return journal;
	}
}