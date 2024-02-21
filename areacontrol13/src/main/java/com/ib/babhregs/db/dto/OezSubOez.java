package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

/**
 * Подобект на оез
 */
@Entity
@Table(name = "oez_sub_oez")
public class OezSubOez implements Serializable ,Cloneable {

	private static final long serialVersionUID = -6540017887145868410L;

	@SequenceGenerator(name = "OezSubOez", sequenceName = "seq_oez_sub_oez", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "OezSubOez")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "obekt_deinost_id", insertable = false, updatable = false)
	private Integer obektDeinostId;

	@Column(name = "naimenovanie")
	@JournalAttr(label = "naimenovanie", defaultText = "Наименование на подобект")
	private String naimenovanie;

	@Column(name = "nomer")
	@JournalAttr(label = "nomer", defaultText = "Номер на подобект")
	private Integer nomer;

	@Column(name = "plosht")
	@JournalAttr(label = "plosht", defaultText = "Площт на подобект")
	private Integer plosht;

	@Column(name = "broi_cikli")
	@JournalAttr(label = "broi_cikli", defaultText = "Брой цикли на подобект")
	private Integer broiCikli;

	@Column(name = "status")
	@JournalAttr(label = "status", defaultText = "Статус на подобект", classifID = "" + BabhConstants.CODE_CLASSIF_STATUS_OEZ)
	private Integer status;

	@Column(name = "status_date")
	@JournalAttr(label = "status_date", defaultText = "Дата на статуса")
	private Date statusDate; // (timestamp)

	@Column(name = "dop_info")
	@JournalAttr(label = "dop_info", defaultText = "Доп. информация за подобект")
	private String dopInfo;

	@JournalAttr(label = "subOezHaraktList", defaultText = "Характеристики на подобект")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "id_sub_oez", referencedColumnName = "id", nullable = false)
	private List<OezSubOezHarakt> subOezHaraktList;

	/** */
	public OezSubOez() {
		super();
	}

	public Integer getBroiCikli() {
		return this.broiCikli;
	}

	public String getDopInfo() {
		return this.dopInfo;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	public String getNaimenovanie() {
		return this.naimenovanie;
	}

	public Integer getNomer() {
		return this.nomer;
	}

	public Integer getObektDeinostId() {
		return this.obektDeinostId;
	}

	public Integer getPlosht() {
		return this.plosht;
	}

	public Integer getStatus() {
		return this.status;
	}

	public Date getStatusDate() {
		return this.statusDate;
	}

	public List<OezSubOezHarakt> getSubOezHaraktList() {
		return this.subOezHaraktList;
	}

	public void setBroiCikli(Integer broiCikli) {
		this.broiCikli = broiCikli;
	}

	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	public void setNaimenovanie(String naimenovanie) {
		this.naimenovanie = naimenovanie;
	}

	public void setNomer(Integer nomer) {
		this.nomer = nomer;
	}

	public void setObektDeinostId(Integer obektDeinostId) {
		this.obektDeinostId = obektDeinostId;
	}

	public void setPlosht(Integer plosht) {
		this.plosht = plosht;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public void setSubOezHaraktList(List<OezSubOezHarakt> subOezHaraktList) {
		this.subOezHaraktList = subOezHaraktList;
	}
	
	
	@Override
	public OezSubOez clone(){
		try {
			OezSubOez cloneSubOez =  (OezSubOez)super.clone();
			List<OezSubOezHarakt> cloneSubOezHaraktList = new ArrayList<OezSubOezHarakt>();
			for(OezSubOezHarakt subHaract:cloneSubOez.getSubOezHaraktList()) {
				cloneSubOezHaraktList.add(subHaract.clone());
			}
			cloneSubOez.setSubOezHaraktList(cloneSubOezHaraktList);
			
			return cloneSubOez;
		}catch (CloneNotSupportedException e) {
			return new OezSubOez();
		}
	}
}
