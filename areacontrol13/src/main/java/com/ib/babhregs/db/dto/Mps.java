package com.ib.babhregs.db.dto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.system.Constants;
import com.ib.system.SysConstants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

/**
 * МПС
 */
@Entity
@Table(name = "mps")
public class Mps extends MpsBase implements AuditExt {

	private static final long serialVersionUID = 930861666906660006L;

	@Column(name = "max_masa")
	@JournalAttr(label = "maxMasa", defaultText = "Допустима макс. маса")
	private String maxMasa;

	@Column(name = "masa")
	@JournalAttr(label = "masa", defaultText = "Маса")
	private String masa;

	@Column(name = "tovaropodemnost")
	@JournalAttr(label = "tovaropodemnost", defaultText = "Товароподемност")
	private String tovaropodemnost;

	@Column(name = "obem")
	@JournalAttr(label = "obem", defaultText = "Обем/вместимост")
	private String obem;

	@Column(name = "plosht")
	@JournalAttr(label = "plosht", defaultText = "Площ")
	private String plosht;

	@Column(name = "dop_info")
	@JournalAttr(label = "dopInfo", defaultText = "Доп. информация")
	private String dopInfo;

	@Column(name = "nom_dat_reg")
	@JournalAttr(label = "nomDatReg", defaultText = "Номер и дата на издаване на рег. талон")
	private String nomDatReg;

	@Column(name = "nom_dat_sert")
	@JournalAttr(label = "nomDatSert", defaultText = "Номер и дата на издаване на сертификата за одобрение")
	private String nomDatSert;

	@Column(name = "navigation")
	@JournalAttr(label = "navigation", defaultText = "Има ли навигационна система", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer navigation;

	@Column(name = "darj")
	@JournalAttr(label = "darj", defaultText = "Държава", classifID = "" + Constants.CODE_CLASSIF_COUNTRIES)
	private Integer darj;

	@Column(name = "obl")
	private Integer obl;

	@Column(name = "obsht")
	private Integer obsht;

	@Column(name = "nas_mesto")
	@JournalAttr(label = "nas_mesto", defaultText = "Населено място", classifID = "" + SysConstants.CODE_CLASSIF_EKATTE)
	private Integer nasMesto;

	@Column(name = "adres")
	@JournalAttr(label = "adres", defaultText = "Улица/сграда")
	private String adres;

	@Column(name = "post_code")
	@JournalAttr(label = "postCode", defaultText = "Пощенски код")
	private String postCode;


	@Column(name = "patuvane")
	@JournalAttr(label = "patuvane", defaultText = "Вид пътуване", classifID = "" + BabhConstants.CODE_CLASSIF_VID_PATUVANE)
	private Integer patuvane; // (int8)

	
	// за mps_category.category
	@Transient
	@JournalAttr(label = "categoryList", defaultText = "Категория", classifID = "" + BabhConstants.CODE_CLASSIF_CATEGORY_SJP)
	private List<Integer>	categoryList;
	@Transient
	private List<Integer>	dbCategoryList;	// @XmlTransient долу в гетера, зашото не е нужно в журнала

	@JournalAttr(label = "mpsKapacitetJiv", defaultText = "Капацитет за превоз на животни")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "mps_id", referencedColumnName = "id", nullable = false)
	private List<MpsKapacitetJiv> mpsKapacitetJiv;

	@JournalAttr(label = "mpsLice", defaultText = "МПС - лица")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "mps_id", referencedColumnName = "id", nullable = false)
	private List<MpsLice> mpsLice = new ArrayList<>();

	/** mps_kapacitet_jiv.vid_jiv mps_kapacitet_jiv.broi_jiv; (множествено с разделител) */
	@Transient
	private String kapacitet;
	
	@Transient
	private boolean correct = true;



	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	/**  */
	public Mps() {
		super();
	}

	/** @return the adres */
	public String getAdres() {
		return this.adres;
	}

	/** @return the categoryList */
	public List<Integer> getCategoryList() {
		return this.categoryList;
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_MPS;
	}

	/** @return the darj */
	public Integer getDarj() {
		return this.darj;
	}

	/** @return the dbCategoryList */
	@XmlTransient
	public List<Integer> getDbCategoryList() {
		return this.dbCategoryList;
	}

	/** @return the dopInfo */
	public String getDopInfo() {
		return this.dopInfo;
	}

	/** @return the kapacitet */
	public String getKapacitet() {
		return this.kapacitet;
	}

	/** @return the masa */
	public String getMasa() {
		return this.masa;
	}

	/** @return the maxMasa */
	public String getMaxMasa() {
		return this.maxMasa;
	}

	/** @return the mpsKapacitetJiv */
	public List<MpsKapacitetJiv> getMpsKapacitetJiv() {
		return this.mpsKapacitetJiv;
	}

	/** @return the mpsLice */
	public List<MpsLice> getMpsLice() {
		return this.mpsLice;
	}

	/** @return the nasMesto */
	public Integer getNasMesto() {
		return this.nasMesto;
	}

	/** @return the navigation */
	public Integer getNavigation() {
		return this.navigation;
	}

	/** @return the nomDatReg */
	public String getNomDatReg() {
		return this.nomDatReg;
	}

	/** @return the nomDatSert */
	public String getNomDatSert() {
		return this.nomDatSert;
	}

	/** @return the obem */
	public String getObem() {
		return this.obem;
	}

	/** @return the obl */
	public Integer getObl() {
		return this.obl;
	}

	/** @return the obsht */
	public Integer getObsht() {
		return this.obsht;
	}

	/** @return the plosht */
	public String getPlosht() {
		return this.plosht;
	}

	/** @return the postCode */
	public String getPostCode() {
		return this.postCode;
	}

	/** @return the tovaropodemnost */
	public String getTovaropodemnost() {
		return this.tovaropodemnost;
	}

	/** @param adres the adres to set */
	public void setAdres(String adres) {
		this.adres = adres;
	}

	/** @param categoryList the categoryList to set */
	public void setCategoryList(List<Integer> categoryList) {
		this.categoryList = categoryList;
	}

	/** @param darj the darj to set */
	public void setDarj(Integer darj) {
		this.darj = darj;
	}

	/** @param dbCategoryList the dbCategoryList to set */
	public void setDbCategoryList(List<Integer> dbCategoryList) {
		this.dbCategoryList = dbCategoryList;
	}

	/** @param dopInfo the dopInfo to set */
	public void setDopInfo(String dopInfo) {
		this.dopInfo = dopInfo;
	}

	/** @param kapacitet the kapacitet to set */
	public void setKapacitet(String kapacitet) {
		this.kapacitet = kapacitet;
	}

	/** @param masa the masa to set */
	public void setMasa(String masa) {
		this.masa = masa;
	}

	/** @param maxMasa the maxMasa to set */
	public void setMaxMasa(String maxMasa) {
		this.maxMasa = maxMasa;
	}

	/** @param mpsKapacitetJiv the mpsKapacitetJiv to set */
	public void setMpsKapacitetJiv(List<MpsKapacitetJiv> mpsKapacitetJiv) {
		this.mpsKapacitetJiv = mpsKapacitetJiv;
	}

	/** @param mpsLice the mpsLice to set */
	public void setMpsLice(List<MpsLice> mpsLice) {
		this.mpsLice = mpsLice;
	}

	/** @param nasMesto the nasMesto to set */
	public void setNasMesto(Integer nasMesto) {
		this.nasMesto = nasMesto;
	}

	/** @param navigation the navigation to set */
	public void setNavigation(Integer navigation) {
		this.navigation = navigation;
	}

	/** @param nomDatReg the nomDatReg to set */
	public void setNomDatReg(String nomDatReg) {
		this.nomDatReg = nomDatReg;
	}

	/** @param nomDatSert the nomDatSert to set */
	public void setNomDatSert(String nomDatSert) {
		this.nomDatSert = nomDatSert;
	}

	/** @param obem the obem to set */
	public void setObem(String obem) {
		this.obem = obem;
	}

	/** @param obl the obl to set */
	public void setObl(Integer obl) {
		this.obl = obl;
	}

	/** @param obsht the obsht to set */
	public void setObsht(Integer obsht) {
		this.obsht = obsht;
	}

	/** @param plosht the plosht to set */
	public void setPlosht(String plosht) {
		this.plosht = plosht;
	}

	/** @param postCode the postCode to set */
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	/** @param tovaropodemnost the tovaropodemnost to set */
	public void setTovaropodemnost(String tovaropodemnost) {
		this.tovaropodemnost = tovaropodemnost;
	}

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());
		return journal;
	}
	
	
	public Integer getPatuvane() {
		return patuvane;
	}

	public void setPatuvane(Integer patuvane) {
		this.patuvane = patuvane;
	}
}