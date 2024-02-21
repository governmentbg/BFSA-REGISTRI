package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

/**
 * Начин на прилагане
 */
@Entity
@Table(name = "vlp_prilagane")
public class VlpPrilagane implements Serializable {

	private static final long serialVersionUID = 3270957691636002209L;

	@SequenceGenerator(name = "VlpPrilagane", sequenceName = "seq_vlp_prilagane", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "VlpPrilagane")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "vlp_id", insertable = false, updatable = false)
	private Integer vlpId;

	@Transient // TODO като вземе да се използва code_nachin, да се махне текстовото
	private String nachin; // (varchar(1000))

	@Column(name = "code_nachin")
	@JournalAttr(label = "code_nachin", defaultText = "Начин на прилагане", classifID = "" + BabhConstants.CODE_CLASSIF_NACHIN_PRILAGANE)
	private Integer codeNachin; // (int8)

	@Transient
	private String vidoveJivotniForNachin;

	@JournalAttr(label = "vlpPrilagane", defaultText = "Видове животни")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "vlp_prilagane_id", referencedColumnName = "id", nullable = false)
	private List<VlpPrilaganeVid> vlpPrilaganeVid;

	/** */
	public VlpPrilagane() {
		super();
	}

	/** @return the codeNachin */
	public Integer getCodeNachin() {
		return this.codeNachin;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the nachin */
	public String getNachin() {
		return this.nachin;
	}

	public String getVidoveJivotniForNachin() {
		return this.vidoveJivotniForNachin;
	}

	/** @return the vlpId */
	public Integer getVlpId() {
		return this.vlpId;
	}

	public List<VlpPrilaganeVid> getVlpPrilaganeVid() {
		return this.vlpPrilaganeVid;
	}

	/** @param codeNachin the codeNachin to set */
	public void setCodeNachin(Integer codeNachin) {
		this.codeNachin = codeNachin;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param nachin the nachin to set */
	public void setNachin(String nachin) {
		this.nachin = nachin;
	}

	public void setVidoveJivotniForNachin(String vidoveJivotniForNachin) {
		this.vidoveJivotniForNachin = vidoveJivotniForNachin;
	}

	/** @param vlpId the vlpId to set */
	public void setVlpId(Integer vlpId) {
		this.vlpId = vlpId;
	}

	public void setVlpPrilaganeVid(List<VlpPrilaganeVid> vlpPrilaganeVid) {
		this.vlpPrilaganeVid = vlpPrilaganeVid;
	}
}
