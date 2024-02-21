package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlTransient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

/**
 * Ветеринарен лекарствен продукт (ВЛП)
 */
@Entity
@Table(name = "vlp")
public class Vlp extends TrackableEntity implements AuditExt {

	private static final long serialVersionUID = -3770606985829027161L;

	@SequenceGenerator(name = "Vlp", sequenceName = "seq_vlp", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "Vlp")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "id_vpisvane")
	@JournalAttr(label = "id_vpisvane", defaultText = "Вписване", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE)
	private Integer idVpisvane; // (int8)

	@Column(name = "naimenovanie_cyr")
	@JournalAttr(label = "naimenovanie_cyr", defaultText = "Търговско име на кирилица")
	private String naimenovanieCyr; // (varchar)

	@Column(name = "naimenovanie_lat")
	@JournalAttr(label = "naimenovanie_lat", defaultText = "Търговско име на латиница")
	private String naimenovanieLat; // (varchar)

	@Column(name = "rejim_otpuskane")
	@JournalAttr(label = "rejim_otpuskane", defaultText = "Начин на отпускане", classifID = "" + BabhConstants.CODE_CLASSIF_NACHIN_OTPUSK_VLP)
	private Integer rejimOtpuskane; // (int8)

	@Column(name = "vet_med_code")
	@JournalAttr(label = "vet_med_code", defaultText = "Ветеринарномедицински Анатомо-Терапевтичен Код")
	private String vetMedCode; // (varchar)

	@Column(name = "vid_proc_odobrenie")
	@JournalAttr(label = "vid_proc_odobrenie", defaultText = "Процедура за одобряване", classifID = "" + BabhConstants.CODE_CLASSIF_PROC_ODOBR)
	private Integer vidProcOdobrenie; // (int8)
	
	@Column(name = "kolichestva_opakovka")
	@JournalAttr(label = "kolichestva_opakovka", defaultText = "Количествa първична опаковка")
	private String kolichestvaOpakovka; // (varchar)
	

	// за vlp_opakovka.opakovka
	@Transient
	@JournalAttr(label = "opakovkaList", defaultText = "Първична опаковка", classifID = "" + BabhConstants.CODE_CLASSIF_PARVICHNA_OPAKOVKA)
	private List<Integer>	opakovkaList;
	@Transient
	private List<Integer>	dbOpakovkaList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	@Column(name = "srok")
	@JournalAttr(label = "srok", defaultText = "Срок на годност")
	private String srok; // (varchar(255))

	@Column(name = "karenten_srok")
	@JournalAttr(label = "karenten_srok", defaultText = "Карентен срок")
	private String karentenSrok; // (varchar(255))
	
	@Column(name = "proc_number")
	@JournalAttr(label = "proc_number", defaultText = "Процедурен номер")
	private String procNumber;
	
	

	@JournalAttr(label = "vlpLice", defaultText = "Свързани лица")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "vlp_id", referencedColumnName = "id", nullable = false)
	private List<VlpLice> vlpLice;

	@JournalAttr(label = "vlpVeshtva", defaultText = "Вещества")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "vlp_id", referencedColumnName = "id", nullable = false)
	private List<VlpVeshtva> vlpVeshtva;

	// това може да се направи да се работи само с кодовете
	@JournalAttr(label = "vlpVidJiv", defaultText = "Вид животни")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "vlp_id", referencedColumnName = "id", nullable = false)
	private List<VlpVidJiv> vlpVidJiv;

	// за vlp_farm_form.farm_form
	@Transient
	@JournalAttr(label = "farmFormList", defaultText = "Фармацевтични форми", classifID = "" + BabhConstants.CODE_CLASSIF_PHARM_FORMI)
	private List<Integer>	farmFormList;
	@Transient
	private List<Integer>	dbFarmFormList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	@JournalAttr(label = "vlpPrilagane", defaultText = "Начин на прилагане")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "vlp_id", referencedColumnName = "id", nullable = false)
	private List<VlpPrilagane> vlpPrilagane = new ArrayList<>();

	/** */
	public Vlp() {
		super();
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_VLP;
	}

	/** @return the dbFarmFormList */
	@XmlTransient
	public List<Integer> getDbFarmFormList() {
		return this.dbFarmFormList;
	}

	/** @return the dbOpakovkaList */
	public List<Integer> getDbOpakovkaList() {
		return this.dbOpakovkaList;
	}

	/** @return the farmFormList */
	public List<Integer> getFarmFormList() {
		return this.farmFormList;
	}

	/** @return the id */
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public String getIdentInfo() throws DbErrorException {
		return this.naimenovanieCyr;
	}

	/** @return the idVpisvane */
	public Integer getIdVpisvane() {
		return this.idVpisvane;
	}

	/** @return the karentenSrok */
	public String getKarentenSrok() {
		return this.karentenSrok;
	}

	/** @return the naimenovanieCyr */
	public String getNaimenovanieCyr() {
		return this.naimenovanieCyr;
	}

	/** @return the naimenovanieLat */
	public String getNaimenovanieLat() {
		return this.naimenovanieLat;
	}

	/** @return the opakovkaList */
	public List<Integer> getOpakovkaList() {
		return this.opakovkaList;
	}

	/** @return the rejimOtpuskane */
	public Integer getRejimOtpuskane() {
		return this.rejimOtpuskane;
	}

	/** @return the srok */
	public String getSrok() {
		return this.srok;
	}

	/** @return the vetMedCode */
	public String getVetMedCode() {
		return this.vetMedCode;
	}

	/** @return the vidProcOdobrenie */
	public Integer getVidProcOdobrenie() {
		return this.vidProcOdobrenie;
	}

	/** @return the vlpLice */
	public List<VlpLice> getVlpLice() {
		return this.vlpLice;
	}

	/** @return the vlpPrilagane */
	public List<VlpPrilagane> getVlpPrilagane() {
		return this.vlpPrilagane;
	}

	/** @return the vlpVeshtva */
	public List<VlpVeshtva> getVlpVeshtva() {
		return this.vlpVeshtva;
	}

	/** @return the vlpVidJiv */
	public List<VlpVidJiv> getVlpVidJiv() {
		return this.vlpVidJiv;
	}

	/** @param dbFarmFormList the dbFarmFormList to set */
	public void setDbFarmFormList(List<Integer> dbFarmFormList) {
		this.dbFarmFormList = dbFarmFormList;
	}

	/** @param dbOpakovkaList the dbOpakovkaList to set */
	public void setDbOpakovkaList(List<Integer> dbOpakovkaList) {
		this.dbOpakovkaList = dbOpakovkaList;
	}

	/** @param farmFormList the farmFormList to set */
	public void setFarmFormList(List<Integer> farmFormList) {
		this.farmFormList = farmFormList;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param idVpisvane the idVpisvane to set */
	public void setIdVpisvane(Integer idVpisvane) {
		this.idVpisvane = idVpisvane;
	}

	/** @param karentenSrok the karentenSrok to set */
	public void setKarentenSrok(String karentenSrok) {
		this.karentenSrok = karentenSrok;
	}

	/** @param naimenovanieCyr the naimenovanieCyr to set */
	public void setNaimenovanieCyr(String naimenovanieCyr) {
		this.naimenovanieCyr = naimenovanieCyr;
	}

	/** @param naimenovanieLat the naimenovanieLat to set */
	public void setNaimenovanieLat(String naimenovanieLat) {
		this.naimenovanieLat = naimenovanieLat;
	}

	/** @param opakovkaList the opakovkaList to set */
	public void setOpakovkaList(List<Integer> opakovkaList) {
		this.opakovkaList = opakovkaList;
	}

	/** @param rejimOtpuskane the rejimOtpuskane to set */
	public void setRejimOtpuskane(Integer rejimOtpuskane) {
		this.rejimOtpuskane = rejimOtpuskane;
	}

	/** @param srok the srok to set */
	public void setSrok(String srok) {
		this.srok = srok;
	}

	/** @param vetMedCode the vetMedCode to set */
	public void setVetMedCode(String vetMedCode) {
		this.vetMedCode = vetMedCode;
	}

	/** @param vidProcOdobrenie the vidProcOdobrenie to set */
	public void setVidProcOdobrenie(Integer vidProcOdobrenie) {
		this.vidProcOdobrenie = vidProcOdobrenie;
	}

	/** @param vlpLice the vlpLice to set */
	public void setVlpLice(List<VlpLice> vlpLice) {
		this.vlpLice = vlpLice;
	}

	/** @param vlpPrilagane the vlpPrilagane to set */
	public void setVlpPrilagane(List<VlpPrilagane> vlpPrilagane) {
		this.vlpPrilagane = vlpPrilagane;
	}

	/** @param vlpVeshtva the vlpVeshtva to set */
	public void setVlpVeshtva(List<VlpVeshtva> vlpVeshtva) {
		this.vlpVeshtva = vlpVeshtva;
	}

	/** @param vlpVidJiv the vlpVidJiv to set */
	public void setVlpVidJiv(List<VlpVidJiv> vlpVidJiv) {
		this.vlpVidJiv = vlpVidJiv;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());

		if (this.idVpisvane != null) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
			journal.setJoinedIdObject1(this.idVpisvane);
		}
		return journal;
	}

	public String getKolichestvaOpakovka() {
		return kolichestvaOpakovka;
	}

	public void setKolichestvaOpakovka(String kolichestvaOpakovka) {
		this.kolichestvaOpakovka = kolichestvaOpakovka;
	}

	public String getProcNumber() {
		return procNumber;
	}

	public void setProcNumber(String procNumber) {
		this.procNumber = procNumber;
	}
}
