package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

/**
 * Характеристики на ОЕЗ
 */
@Entity
@Table(name = "oez_harakt")
public class OezHarakt implements Serializable {

	private static final long serialVersionUID = -6540017887145868410L;

	@SequenceGenerator(name = "OezHarakt", sequenceName = "seq_oez_harakt", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "OezHarakt")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "id_oez", insertable = false, updatable = false)
	private Integer idOez;

	@Column(name = "vid_jivotno")
	@JournalAttr(label = "vid_jivotno", defaultText = "Вид животно", classifID = "" + BabhConstants.CODE_CLASSIF_VID_JIVOTNO)
	private Integer vidJivotno;

	@Column(name = "prednaznachenie")
	@JournalAttr(label = "prednaznachenie", defaultText = "Предназначение", classifID = "" + BabhConstants.CODE_CLASSIF_PREDNAZNACHENIE_JIV)
	private Integer prednaznachenie;

	@Column(name = "tehnologia")
	@JournalAttr(label = "tehnologia", defaultText = "Технология", classifID = "" + BabhConstants.CODE_CLASSIF_TEHNOLOGIA_OTGLEJDANE)
	private Integer tehnologia;

	@Column(name = "kapacitet")
	@JournalAttr(label = "kapacitet", defaultText = "Количество - число")
	private Integer kapacitet;

	@Column(name = "kapacitet_text")
	@JournalAttr(label = "kapacitet_text", defaultText = "Количество - текст ")
	private String kapacitetText;

	/** */
	public OezHarakt() {
		super();
	}

	public Object getId() {
		return this.id;
	}

	public Integer getIdOez() {
		return this.idOez;
	}

	public Integer getKapacitet() {
		return this.kapacitet;
	}

	public String getKapacitetText() {
		return this.kapacitetText;
	}

	public Integer getPrednaznachenie() {
		return this.prednaznachenie;
	}

	public Integer getTehnologia() {
		return this.tehnologia;
	}

	public Integer getVidJivotno() {
		return this.vidJivotno;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdOez(Integer idOez) {
		this.idOez = idOez;
	}

	public void setKapacitet(Integer kapacitet) {
		this.kapacitet = kapacitet;
	}

	public void setKapacitetText(String kapacitetText) {
		this.kapacitetText = kapacitetText;
	}

	public void setPrednaznachenie(Integer prednaznachenie) {
		this.prednaznachenie = prednaznachenie;
	}

	public void setTehnologia(Integer tehnologia) {
		this.tehnologia = tehnologia;
	}

	public void setVidJivotno(Integer vidJivotno) {
		this.vidJivotno = vidJivotno;
	}

}
