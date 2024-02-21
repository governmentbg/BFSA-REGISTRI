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
 * Дейност – МПС
 */
@Entity
@Table(name = "mps_deinost")
public class MpsDeinost implements Serializable {

	private static final long serialVersionUID = -5484027724121590385L;

	@SequenceGenerator(name = "MpsDeinost", sequenceName = "seq_mps_deinost", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "MpsDeinost")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	/** id от съответната таблица event_deinost_jiv или event_deinost_vlp или event_deinost_furaji */
	@Column(name = "deinost_id", insertable = false, updatable = false)
	private Integer deinostId;

	@Column(name = "tip_vraz")
	@JournalAttr(label = "tip_vraz", defaultText = "Тип на връзката", classifID = "" + BabhConstants.CODE_CLASSIF_VRAZ_SYBITIE_OBEKT)
	private Integer tipVraz; // (int8)

	@Column(name = "mps_id")
	@JournalAttr(label = "mps_id", defaultText = "МПС", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_MPS)
	private Integer mpsId; // (int8)

	@Column(name = "date_beg")
	@JournalAttr(label = "date_beg", defaultText = "Начална дата")
	private Date dateBeg; // (timestamp)

	@Column(name = "date_end")
	@JournalAttr(label = "date_end", defaultText = "Крайна дата")
	private Date dateEnd; // (timestamp)

	/**
	 * Определя кои са таблиците за съответната дейност.<br>
	 * Записва се кода на обекта от журнала за съответната дейност<br>
	 * Дейности с животни CODE_ZNACHENIE_JOURNAL_event_deinost_JIV = 98;<br>
	 * Дейности с фуражи CODE_ZNACHENIE_JOURNAL_event_deinost_FURAJI = 99;<br>
	 * Дейности с ВЛП* CODE_ZNACHENIE_JOURNAL_event_deinost_VLP = 100;
	 */
	@Column(name = "tabl_event_deinost", updatable = false)
	private Integer tablEventDeinost; // (int8)

	/**
	 * [0]-id</br>
	 * [1]-vid</br>
	 * [2]-null marka-няма вече</br>
	 * [3]-model</br>
	 * [4]-nomer</br>
	 * [5]-mps_lice.codeRef (собственик)</br>
	 * [6]-vpisvane.status Рег.Животни - като може и да е NULL</br>
	 * [7]-vpisvane.id_register Рег.Животни - като може и да е NULL</br>
	 * [8]-vpisvane.status Рег.Фуражи - като може и да е NULL</br>
	 * [9]-vpisvane.id_register Рег.Фуражи - като може и да е NULL</br>
	 * [10]-plosht</br>
	 * [11]-vpisvane.id Рег.Животни - като може и да е NULL</br>
	 * [12]-vpisvane.id Рег.Фуражи - като може и да е NULL</br>
	 */
	@Transient
	private Object[] mpsInfo; // това е за дейности с животни, защото няма корекция на мпс

	@Transient // за дейности с фуражи, защото тука си се записва/коригира и т.н.
	private MpsFuraji mpsFuraji; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	/**  */
	public MpsDeinost() {
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

	/** @return the mpsFuraji */
	@XmlTransient
	public MpsFuraji getMpsFuraji() {
		return this.mpsFuraji;
	}

	/** @return the mpsId */
	public Integer getMpsId() {
		return this.mpsId;
	}

	/** @return the mpsInfo */
	public Object[] getMpsInfo() {
		return this.mpsInfo;
	}

	/** @return the tablEventDeinost */
	public Integer getTablEventDeinost() {
		return this.tablEventDeinost;
	}

	/** @return the tipVraz */
	public Integer getTipVraz() {
		return this.tipVraz;
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

	/** @param mpsFuraji the mpsFuraji to set */
	public void setMpsFuraji(MpsFuraji mpsFuraji) {
		this.mpsFuraji = mpsFuraji;
	}

	/** @param mpsId the mpsId to set */
	public void setMpsId(Integer mpsId) {
		this.mpsId = mpsId;
	}

	/** @param mpsInfo the mpsInfo to set */
	public void setMpsInfo(Object[] mpsInfo) {
		this.mpsInfo = mpsInfo;
	}

	/** @param tablEventDeinost the tablEventDeinost to set */
	public void setTablEventDeinost(Integer tablEventDeinost) {
		this.tablEventDeinost = tablEventDeinost;
	}

	/** @param tipVraz the tipVraz to set */
	public void setTipVraz(Integer tipVraz) {
		this.tipVraz = tipVraz;
	}
}