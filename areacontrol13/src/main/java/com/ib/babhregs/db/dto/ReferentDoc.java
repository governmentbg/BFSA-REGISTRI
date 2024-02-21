package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;

/**
 * Документи (дипломи, удостоверения...)
 *
 * @author svilen
 */
@Entity
@Table(name = "adm_ref_doc")
public class ReferentDoc extends TrackableEntity implements Cloneable {

	private static final long serialVersionUID = 98554551046266984L;

	@SequenceGenerator(name = "ReferentDoc", sequenceName = "seq_adm_ref_doc", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "ReferentDoc")
	@Column(name = "ID", nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "code_ref", updatable = false)
	private Integer codeRef;

	@JournalAttr(label = "vid_doc", defaultText = "Вид документ", classifID = "" + BabhConstants.CODE_CLASSIF_VID_DOC_LICE)
	@Column(name = "vid_doc")
	private Integer vidDoc;

	@JournalAttr(label = "nom_doc", defaultText = "Номер на док.")
	@Column(name = "nom_doc")
	private String nomDoc;

	@JournalAttr(label = "date_issued", defaultText = "Дата на издаване")
	@Temporal(TemporalType.DATE)
	@Column(name = "date_issued")
	private Date dateIssued;

	@JournalAttr(label = "issued_by", defaultText = "Издаден от")
	@Column(name = "issued_by")
	private String issuedBy;

	/** */
	public ReferentDoc() {
		super();
	}

	@Override
	public ReferentDoc clone() {
		try {
			return (ReferentDoc) super.clone();
		} catch (CloneNotSupportedException e) {
			return new ReferentDoc();
		}
	}

	/**
	 * Метод, който трябва да направи уникален хеш код за обекти от тип {@link ReferentDoc}.
	 *
	 * @return число
	 */
	public int dbHashProps() { // codeRef не се включва, защото се проверява в контекста на един и същ референт
		return Objects.hash(this.id, this.vidDoc, this.nomDoc, this.dateIssued, this.issuedBy);
	}

	/** */
	@Override
	public Integer getCodeMainObject() {
		return null;
	}

	/** @return the codeRef */
	public Integer getCodeRef() {
		return this.codeRef;
	}

	/** @return the dateIssued */
	public Date getDateIssued() {
		return this.dateIssued;
	}

	/** */
	@Override
	public Integer getId() {
		return this.id;
	}

	/** @return the issuedBy */
	public String getIssuedBy() {
		return this.issuedBy;
	}

	/** @return the nomDoc */
	public String getNomDoc() {
		return this.nomDoc;
	}

	/** @return the vidDoc */
	public Integer getVidDoc() {
		return this.vidDoc;
	}

	/** */
	@Override
	public boolean isAuditable() {
		return false; // ще се журналира в контекста на участника в процеса
	}

	/** @param codeRef the codeRef to set */
	public void setCodeRef(Integer codeRef) {
		this.codeRef = codeRef;
	}

	/** @param dateIssued the dateIssued to set */
	public void setDateIssued(Date dateIssued) {
		this.dateIssued = dateIssued;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param issuedBy the issuedBy to set */
	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
	}

	/** @param nomDoc the nomDoc to set */
	public void setNomDoc(String nomDoc) {
		this.nomDoc = nomDoc;
	}

	/** @param vidDoc the vidDoc to set */
	public void setVidDoc(Integer vidDoc) {
		this.vidDoc = vidDoc;
	}
}