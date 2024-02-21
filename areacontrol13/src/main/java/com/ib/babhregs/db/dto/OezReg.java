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
 * DOCME
 *
 * @author belev
 */
@Entity
@Table(name = "obekt_deinost")
public class OezReg extends ObektDeinostBase implements AuditExt {

	private static final long serialVersionUID = -9131737734744034991L;

	@JournalAttr(label = "OezHarakt", defaultText = "Характеристики")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "id_oez", referencedColumnName = "id", nullable = false)
	private List<OezHarakt> oezHarakt;

	@JournalAttr(label = "OezSubOez", defaultText = "Подобекти")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "obekt_deinost_id", referencedColumnName = "id", nullable = false)
	private List<OezSubOez> oezSubOez;
	
	@JournalAttr(label = "oezNomKadastar", defaultText = "Номера по кадастър")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "id_oez", referencedColumnName = "id", nullable = false)
	private List<OezNomKadastar> oezNomKadastar;
		
	
	/** */
	public OezReg() {
		super();
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_OEZ;
	}

	/** @return the oezHarakt */
	public List<OezHarakt> getOezHarakt() {
		return this.oezHarakt;
	}

	/** @return the oezSubOez */
	public List<OezSubOez> getOezSubOez() {
		return this.oezSubOez;
	}

	/** @param oezHarakt the oezHarakt to set */
	public void setOezHarakt(List<OezHarakt> oezHarakt) {
		this.oezHarakt = oezHarakt;
	}

	/** @param oezSubOez the oezSubOez to set */
	public void setOezSubOez(List<OezSubOez> oezSubOez) {
		this.oezSubOez = oezSubOez;
	}
	

	public List<OezNomKadastar> getOezNomKadastar() {
		return oezNomKadastar;
	}

	public void setOezNomKadastar(List<OezNomKadastar> oezNomKadastar) {
		this.oezNomKadastar = oezNomKadastar;
	}

	
	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());
		return journal;
	}
}
