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
 * Обект на дейност - Дейност
 */
@Entity
@Table(name = "obekt_deinost_deinost")
public class ObektDeinostDeinost implements Serializable {

	private static final long serialVersionUID = -1513450738023803721L;

	@SequenceGenerator(name = "ObektDeinostDeinost", sequenceName = "seq_obekt_deinost_deinost", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "ObektDeinostDeinost")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	/**
	 * id от съответната таблица event_deinost_jiv или event_deinost_vlp или event_deinost_furaji
	 */
	@Column(name = "deinost_id", insertable = false, updatable = false)
	private Integer	deinostId;
	/**
	 * id на обект от таблица obekt_deinost
	 */
	@Column(name = "obekt_deinost_id")
	@JournalAttr(label = "obekt_deinost_id", defaultText = "Обект на дейност", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_OBEKT_DEINOST)
	private Integer	obektDeinostId;

	@Column(name = "date_beg")
	@JournalAttr(label = "date_beg", defaultText = "Начална дата")
	private Date dateBeg; // (timestamp)

	@Column(name = "date_end")
	@JournalAttr(label = "date_end", defaultText = "Крайна дата")
	private Date dateEnd; // (timestamp)

	/**
	 * Определя кои са таблиците за съответната дейност. Записва се кода на обекта от журнала за съответната дейност Дейности с
	 * животни CODE_ZNACHENIE_JOURNAL_event_deinost_JIV = 98; Дейности с фуражи CODE_ZNACHENIE_JOURNAL_event_deinost_FURAJI = 99;
	 * Дейности с ВЛП* CODE_ZNACHENIE_JOURNAL_event_deinost_VLP = 100;
	 */
	@Column(name = "tabl_event_deinost", updatable = false)
	private Integer tablEventDeinost; // (int8)

	@Transient
	private ObektDeinost obektDeinost; // @XmlTransient в гетера и не влиза в журнала така !?!?!

	/**  */
	public ObektDeinostDeinost() {
		super();
	}

	/** @return the dateBeg */
	public Date getDateBeg() {
		return this.dateBeg;
	}

	/** @return the dateEnd */
	public Date getDateEnd() {
		return this.dateEnd;
	}

	/** @return the deinostId */
	public Integer getDeinostId() {
		return this.deinostId;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the obektDeinost */
	@XmlTransient
	public ObektDeinost getObektDeinost() {
		return this.obektDeinost;
	}

	/** @return the obektDeinostId */
	public Integer getObektDeinostId() {
		return this.obektDeinostId;
	}

	/** @return the tablEventDeinost */
	public Integer getTablEventDeinost() {
		return this.tablEventDeinost;
	}

	/** @param dateBeg the dateBeg to set */
	public void setDateBeg(Date dateBeg) {
		this.dateBeg = dateBeg;
	}

	/** @param dateEnd the dateEnd to set */
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	/** @param deinostId the deinostId to set */
	public void setDeinostId(Integer deinostId) {
		this.deinostId = deinostId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param obektDeinost the obektDeinost to set */
	public void setObektDeinost(ObektDeinost obektDeinost) {
		this.obektDeinost = obektDeinost;
	}

	/** @param obektDeinostId the obektDeinostId to set */
	public void setObektDeinostId(Integer obektDeinostId) {
		this.obektDeinostId = obektDeinostId;
	}

	/** @param tablEventDeinost the tablEventDeinost to set */
	public void setTablEventDeinost(Integer tablEventDeinost) {
		this.tablEventDeinost = tablEventDeinost;
	}
}