package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ib.system.db.JournalAttr;

/**
 * Номера кадастър на ОЕЗ
 */
@Entity
@Table(name = "oez_nomer_kadastar")
public class OezNomKadastar implements Serializable {

	private static final long serialVersionUID = -6540017887145868410L;

	@SequenceGenerator(name = "OezNomKadastar", sequenceName = "seq_oez_kadastar", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "OezNomKadastar")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "id_oez", insertable = false, updatable = false)
	private Integer idOez;

	
	@Column(name = "nomer")
	@JournalAttr(label = "nomer", defaultText = "Номер по кадастър ")
	private String nomer;


	/** */
	public OezNomKadastar() {
		super();
	}

	public Object getId() {
		return this.id;
	}

	public Integer getIdOez() {
		return this.idOez;
	}


	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdOez(Integer idOez) {
		this.idOez = idOez;
	}


	public String getNomer() {
		return nomer;
	}

	public void setNomer(String nomer) {
		this.nomer = nomer;
	}

}
