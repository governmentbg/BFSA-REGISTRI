package com.ib.babhregs.db.dto;


import static com.ib.system.utils.SearchUtils.trimToNULL;
import static javax.persistence.GenerationType.SEQUENCE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.system.Constants;
import com.ib.system.SysConstants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;

/**
 * Участник в процеса
 *
 * @author belev
 */
@Entity
@Table(name = "ADM_REFERENTS")
public class Referent extends TrackableEntity implements AuditExt, Cloneable {
	/**  */
	private static final long serialVersionUID = 8671296280803403457L;

	@SequenceGenerator(name = "Referent", sequenceName = "SEQ_ADM_REFERENTS", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "Referent")
	@Column(name = "REF_ID", unique = true, nullable = false)
	private Integer id;

	@JournalAttr(label = "missing.code", defaultText = "Код")
	@Column(name = "CODE", updatable = false)
	private Integer code;

//	@JournalAttr(label = "admStruct.itemBefore", defaultText = "Предходен елемент", classifID = "" + Constants.CODE_CLASSIF_ADMIN_STR)
	@Column(name = "CODE_PREV")
	private Integer codePrev;

//	@JournalAttr(label = "users.zveno", defaultText = "Звено", classifID = "" + Constants.CODE_CLASSIF_ADMIN_STR)
	@Column(name = "CODE_PARENT")
	private Integer codeParent;

	@Column(name = "CODE_CLASSIF")
	private Integer codeClassif;

	@JournalAttr(label = "missing.refType", defaultText = "Тип", classifID = "" + BabhConstants.CODE_CLASSIF_REF_TYPE)
	@Column(name = "REF_TYPE")
	private Integer refType;

	@JournalAttr(label = "regData.registratura", defaultText = "Регистратура", classifID = "" + BabhConstants.CODE_CLASSIF_REGISTRATURI)
	@Column(name = "REF_REGISTRATURA")
	private Integer refRegistratura;

	@JournalAttr(label = "refCorr.nameUL", defaultText = "Наименование")
	@Column(name = "REF_NAME")
	private String refName;
	
	@JournalAttr(label = "ime", defaultText = "Име")
	@Column(name = "IME")
	private String ime;
	
	@JournalAttr(label = "prezime", defaultText = "Презиме")
	@Column(name = "prezime")
	private String prezime;
	
	@JournalAttr(label = "familia", defaultText = "Фамилия")
	@Column(name = "familia")
	private String familia;


	@JournalAttr(label = "refCorr.nameLatinUL", defaultText = "Наименование на латиница")
	@Column(name = "REF_LATIN")
	private String refLatin;

	@JournalAttr(label = "refCorr.regCountry", defaultText = "Държава на регистрация", classifID = "" + Constants.CODE_CLASSIF_COUNTRIES)
	@Column(name = "REF_GRJ")
	private Integer refGrj;

	@JournalAttr(label = "docu.note", defaultText = "Забележка")
	@Column(name = "REF_INFO")
	private String refInfo;

	@JournalAttr(label = "refDeleg.dateFrom", defaultText = "От дата")
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_OT")
	private Date dateOt;

	@JournalAttr(label = "refDeleg.dateTo", defaultText = "До дата")
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_DO")
	private Date dateDo;

	@JournalAttr(label = "external_code", defaultText = "Външен код")
	@Column(name = "external_code")
	private String externalCode;

	@JournalAttr(label = "admStruct.telefon", defaultText = "Телефон")
	@Column(name = "CONTACT_PHONE")
	private String contactPhone;

	@JournalAttr(label = "admStruct.email", defaultText = "e-mail")
	@Column(name = "CONTACT_EMAIL")
	private String contactEmail;
	
//	@JournalAttr(label = "web_page", defaultText = "Уеб страница")
//	@Column(name = "web_page")
//	private String webPage;

	@Column(name = "MAX_UPLOAD_SIZE")
	private Integer maxUploadSize;

	@JournalAttr(label = "regGrSluj.position", defaultText = "Длъжност", classifID = "" + Constants.CODE_CLASSIF_POSITION)
	@Column(name = "EMPL_POSITION")
	private Integer emplPosition;

	@JournalAttr(label = "admStruct.grDogovor", defaultText = "Граждански договор", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	@Column(name = "EMPL_CONTRACT")
	private Integer emplContract;

	@JournalAttr(label = "admStruct.eik", defaultText = "ЕИК")
	@Column(name = "NFL_EIK")
	private String nflEik;

	@JournalAttr(label = "admStruct.egn", defaultText = "ЕГН")
	@Column(name = "FZL_EGN")
	private String fzlEgn;

	@JournalAttr(label = "missing.fzlLnc", defaultText = "ЛНЧ")
	@Column(name = "FZL_LNC")
	private String fzlLnc;

	@JournalAttr(label = "refCorr.fzlLnEs", defaultText = "Идент. код")
	@Column(name = "FZL_LN_ES")
	private String fzlLnEs;

	@JournalAttr(label = "missing.fzlBirthDate", defaultText = "Дата на раждане")
	@Temporal(TemporalType.DATE)
	@Column(name = "FZL_BIRTH_DATE")
	private Date fzlBirthDate;
	
	@JournalAttr(label = "dateSmart", defaultText = "Дата на смърт")
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_SMART")
	private Date dateSmart;

	@JournalAttr(label = "liquidation", defaultText = "Ликвидация или несъстоятелност")
	@Column(name = "LIQUIDATION")
	private String liquidation;

	@JournalAttr(label = "urn", defaultText = "УРН на ветеринарен лекар")
	@Column(name = "urn")
	private String urn;

	@JournalAttr(label = "fzl_birth_place", defaultText = "Място на раждане")
	@Column(name = "fzl_birth_place")
	private String fzlBirthPlace;

	@JournalAttr(label = "fzl_birth_place_latin", defaultText = "Място на раждане на английски")
	@Column(name = "fzl_birth_place_latin")
	private String fzlBirthPlaceLatin;


	@JournalAttr(label = "nationality", defaultText = "Националност")
	@Column(name = "nationality")
	private String nationality;
	
	@JournalAttr(label = "nationality_latin", defaultText = "Националност на английски")
	@Column(name = "nationality_Latin")
	private String nationalityLatin;
	
	@Transient // TODO да се махне като започне да се използва obrazovanie 
	private String obrazovStepen; // (varchar)

	@JournalAttr(label = "obrazovanie", defaultText = "Образователна степен", classifID = "" + BabhConstants.CODE_CLASSIF_OBRAZOBANIE)  
	@Column(name = "obrazovanie")
	private Integer obrazovanie;
	
	@JournalAttr(label = "opit", defaultText = "Опит")
	@Column(name = "opit")
	private String opit; // (varchar)

	@Column(name = "LEVEL_NUMBER")
	private Integer levelNumber; // тази колона е празна и ще се изпокзва като флаг за проверен в РЕГИКС

	@Transient
	private transient Boolean auditable; // за да може да се включва и изключва журналирането

	@JournalAttr(label = "address", defaultText = "Контакти")
	@Transient
	private ReferentAddress		address;		// адреса в момента е 1:1. Ако се появи необходимост от множествени адреси, то
												// този ще си остане и в списък ще има другите адреси. Базата позволява, защото
												// адреса е в друга таблица.
	
	@JournalAttr(label = "addressKoresp", defaultText = "Адрес за кореспонденция")
	@Transient
	private ReferentAddress		addressKoresp;		// адреса в момента е 1:1. Ако се появи необходимост от множествени адреси, то
												// този ще си остане и в списък ще има другите адреси. Базата позволява, защото
												// адреса е в друга таблица.
	@Transient
	private transient Integer	dbAddressId;	// за да се знае имало ли адрес. може и цял обект копие да се използва, за да се
												// знае имало ли е реална промяна.

	@Transient
	private transient Integer	dbAddressKorespId;	// за да се знае имало ли адрес. може и цял обект копие да се използва, за да се
												// знае имало ли е реална промяна.

	@JournalAttr(label = "referentDocs", defaultText = "Документи за лице")
	@Transient
	private List<ReferentDoc> referentDocs;
	/** key={@link ReferentDoc#getId()} , value={@link ReferentDoc#dbHashProps()} */
	@Transient
	private Map<Integer, Integer> dbMapReferentDocs; // това ще служи, за да се сравнява на запис !@XmlTransient! в гетера

	// данни за елементи на административна структура, чиято промяна прави история
	@Transient
	private transient String	dbRefName;
	@Transient
	private transient Integer	dbRefRegistratura;
	@Transient
	private transient Integer	dbEmplPosition;
	@Transient
	private transient String	dbContactEmail;
	@Transient
	private transient Integer	dbEmplContract;
	
	@Transient
	private transient Date	dateBeg;
	@Transient
	private transient Date	dateEnd;

	/**  */
	public Referent() {
		super();
	}

	@Override
	public Referent clone() throws CloneNotSupportedException {
		Referent cloned = (Referent) super.clone();
		
		if (this.address != null) {
			cloned.setAddress(this.address.clone());
		}
		if (this.addressKoresp != null) {
			cloned.setAddressKoresp(this.addressKoresp.clone());
		}
		if (this.referentDocs != null) {
			cloned.referentDocs = new ArrayList<>();
			
			for (ReferentDoc rd : this.referentDocs) {
				cloned.referentDocs.add(rd.clone());
			}
		}
		if (this.dbMapReferentDocs != null) {
			cloned.dbMapReferentDocs = new HashMap<>(this.dbMapReferentDocs);
		}
		
		return cloned;
	}

	/**
	 * Всички стрингови полета, които са празен стринг ги прави на null
	 */
	public void fixEmptyStringValues() {
		this.nflEik = trimToNULL(this.nflEik);
		this.fzlEgn = trimToNULL(this.fzlEgn);
		this.fzlLnc = trimToNULL(this.fzlLnc);
		this.fzlLnEs = trimToNULL(this.fzlLnEs);
		this.urn = trimToNULL(this.urn);
	}

	
	/**
	 * Дава <code>true</code> ако двата са еднакви с цел да не се размножават на записа.
	 * 
	 * @param other този трябва да е различен от нулл и да е записан ! ако не е такъв значи не са еднакви !
	 * @return
	 */
	public boolean isSameToOther(Referent other) {
		if (other == null || other.code == null || !Objects.equals(other.refType, this.refType)) {
			return false; // другия не е записан или имат различен тип фзл/нфл
		}
		if (other.code.equals(this.code)) {
			return true; // явно и двата са записани и имат еднакъв код
		}
		
		if (this.code == null) { // надолу са проверките по външни идентификатори
			
			String eik = SearchUtils.trimToNULL(this.nflEik);
			if (eik != null && eik.equals(SearchUtils.trimToNULL(other.nflEik))) {
				return true; // съвпадение по ЕИК
			}
			String egn = SearchUtils.trimToNULL(this.fzlEgn);
			if (egn != null && egn.equals(SearchUtils.trimToNULL(other.fzlEgn))) {
				return true; // съвпадение по ЕГН
			}
			String lnc = SearchUtils.trimToNULL(this.fzlLnc);
			if (lnc != null && lnc.equals(SearchUtils.trimToNULL(other.fzlLnc))) {
				return true; // съвпадение по ЛНЧ
			}
			String lnEs = SearchUtils.trimToNULL(this.fzlLnEs);
			if (lnEs != null && lnEs.equals(SearchUtils.trimToNULL(other.fzlLnEs))) {
				return true; // съвпадение по Идент. код
			}

			if (eik == null // не трябва да има ЕИК
					&& SearchUtils.isEmpty(other.nflEik) // и този, с който се сравнява също
					&& lnEs == null // не трябва да има Идент. код
					&& SearchUtils.isEmpty(other.fzlLnEs) // и този, с който се сравнява също
					&& Objects.equals(this.refType, BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL)) {

				String name = SearchUtils.trimToNULL(this.refName);
				if (name != null && name.equals(SearchUtils.trimToNULL(other.refName))) {
					return true; // съвпадение по име за НФЛ
				}
			}
		}
		return false;

	}
	
	/** @return the address */
	public ReferentAddress getAddress() {
		return this.address;
	}
	/** @return the address */
	public ReferentAddress getAddressKoresp() {
		return this.addressKoresp;
	}

	/** @return the code */
	public Integer getCode() {
		return this.code;
	}

	/** @return the codeClassif */
	public Integer getCodeClassif() {
		return this.codeClassif;
	}

	/** */
	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT;
	}

	/** @return the codeParent */
	public Integer getCodeParent() {
		return this.codeParent;
	}

	/** @return the codePrev */
	public Integer getCodePrev() {
		return this.codePrev;
	}

	/** @return the contactEmail */
	public String getContactEmail() {
		return this.contactEmail;
	}

	/** @return the contactPhone */
	public String getContactPhone() {
		return this.contactPhone;
	}

	/** @return the dateDo */
	public Date getDateDo() {
		return this.dateDo;
	}

	/** @return the dateOt */
	public Date getDateOt() {
		return this.dateOt;
	}

	/** @return the dbAddressId */
	public Integer getDbAddressId() {
		return this.dbAddressId;
	}

	/** @return the dbContactEmail */
	public String getDbContactEmail() {
		return this.dbContactEmail;
	}

	/** @return the dbEmplContract */
	public Integer getDbEmplContract() {
		return this.dbEmplContract;
	}

	/** @return the dbEmplPosition */
	public Integer getDbEmplPosition() {
		return this.dbEmplPosition;
	}

	/** @return the dbRefName */
	public String getDbRefName() {
		return this.dbRefName;
	}

	/** @return the dbRefRegistratura */
	public Integer getDbRefRegistratura() {
		return this.dbRefRegistratura;
	}

	/** @return the emplContract */
	public Integer getEmplContract() {
		return this.emplContract;
	}

	/** @return the emplPosition */
	public Integer getEmplPosition() {
		return this.emplPosition;
	}

	/** @return the fzlBirthDate */
	public Date getFzlBirthDate() {
		return this.fzlBirthDate;
	}

	/** @return the fzlEgn */
	public String getFzlEgn() {
		return this.fzlEgn;
	}

	/** @return the fzlLnc */
	public String getFzlLnc() {
		return this.fzlLnc;
	}

	/** @return the fzlLnEs */
	public String getFzlLnEs() {
		return this.fzlLnEs;
	}

	/** */
	@Override
	public Integer getId() {
		return this.id;
	}

	/** */
	@Override
	public String getIdentInfo() throws DbErrorException {
		StringBuilder ident = new StringBuilder();
		ident.append(this.refName);

		boolean isNfl = this.refType != null && this.refType.equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);

		String s;
		if (isNfl) {
			s = this.nflEik;
		} else {
			s = this.fzlEgn;
			if (s == null) {
				s = this.fzlLnc;
			}
		}
		if (s != null) {
			ident.append(" " + s);
		}
		return ident.toString();
	}

	/** @return the maxUploadSize */
	public Integer getMaxUploadSize() {
		return this.maxUploadSize;
	}

	/** @return the nflEik */
	public String getNflEik() {
		return this.nflEik;
	}

	/** @return the refGrj */
	public Integer getRefGrj() {
		return this.refGrj;
	}

	/** @return the refInfo */
	public String getRefInfo() {
		return this.refInfo;
	}

	/** @return the refLatin */
	public String getRefLatin() {
		return this.refLatin;
	}

	/** @return the refName */
	public String getRefName() {
//		if (ime!=null && !ime.isEmpty()) {
//			refName=ime;
//		}
//		if (prezime!=null && !prezime.isEmpty()) {
//			if (ime!=null) {
//				refName+=" "+prezime;
//			}else {
//				refName=prezime;
//			}
//		}
//		if (familia!=null && !familia.isEmpty()) {
//			if (ime!=null) {
//				refName+=" "+familia;
//			}else {
//				refName=familia;
//			}
//		}
		return this.refName;
	}

	/** @return the refRegistratura */
	public Integer getRefRegistratura() {
		return this.refRegistratura;
	}

	/** @return the refType */
	public Integer getRefType() {
		return this.refType;
	}

	/** @return the auditable */
	@Override
	public boolean isAuditable() {
		return this.auditable == null ? super.isAuditable() : this.auditable.booleanValue();
	}

	/** @param address the address to set */
	public void setAddress(ReferentAddress address) {
		this.address = address;
	}
	/** @param address the address to set */
	public void setAddressKoresp(ReferentAddress addressKoresp) {
		this.addressKoresp = addressKoresp;
	}

	/** @param auditable the auditable to set */
	public void setAuditable(Boolean auditable) {
		this.auditable = auditable;
	}

	/** @param code the code to set */
	public void setCode(Integer code) {
		this.code = code;
	}

	/** @param codeClassif the codeClassif to set */
	public void setCodeClassif(Integer codeClassif) {
		this.codeClassif = codeClassif;
	}

	/** @param codeParent the codeParent to set */
	public void setCodeParent(Integer codeParent) {
		this.codeParent = codeParent;
	}

	/** @param codePrev the codePrev to set */
	public void setCodePrev(Integer codePrev) {
		this.codePrev = codePrev;
	}

	/** @param contactEmail the contactEmail to set */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	/** @param contactPhone the contactPhone to set */
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	/** @param dateDo the dateDo to set */
	public void setDateDo(Date dateDo) {
		this.dateDo = dateDo;
	}

	/** @param dateOt the dateOt to set */
	public void setDateOt(Date dateOt) {
		this.dateOt = dateOt;
	}

	/** @param dbAddressId the dbAddressId to set */
	public void setDbAddressId(Integer dbAddressId) {
		this.dbAddressId = dbAddressId;
	}

	/** @param dbContactEmail the dbContactEmail to set */
	public void setDbContactEmail(String dbContactEmail) {
		this.dbContactEmail = dbContactEmail;
	}

	/** @param dbEmplContract the dbEmplContract to set */
	public void setDbEmplContract(Integer dbEmplContract) {
		this.dbEmplContract = dbEmplContract;
	}

	/** @param dbEmplPosition the dbEmplPosition to set */
	public void setDbEmplPosition(Integer dbEmplPosition) {
		this.dbEmplPosition = dbEmplPosition;
	}

	/** @param dbRefName the dbRefName to set */
	public void setDbRefName(String dbRefName) {
		this.dbRefName = dbRefName;
	}

	/** @param dbRefRegistratura the dbRefRegistratura to set */
	public void setDbRefRegistratura(Integer dbRefRegistratura) {
		this.dbRefRegistratura = dbRefRegistratura;
	}

	/** @param emplContract the emplContract to set */
	public void setEmplContract(Integer emplContract) {
		this.emplContract = emplContract;
	}

	/** @param emplPosition the emplPosition to set */
	public void setEmplPosition(Integer emplPosition) {
		this.emplPosition = emplPosition;
	}

	/** @param fzlBirthDate the fzlBirthDate to set */
	public void setFzlBirthDate(Date fzlBirthDate) {
		this.fzlBirthDate = fzlBirthDate;
	}

	/** @param fzlEgn the fzlEgn to set */
	public void setFzlEgn(String fzlEgn) {
		this.fzlEgn = fzlEgn;
	}

	/** @param fzlLnc the fzlLnc to set */
	public void setFzlLnc(String fzlLnc) {
		this.fzlLnc = fzlLnc;
	}

	/** @param fzlLnEs the fzlLnEs to set */
	public void setFzlLnEs(String fzlLnEs) {
		this.fzlLnEs = fzlLnEs;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param maxUploadSize the maxUploadSize to set */
	public void setMaxUploadSize(Integer maxUploadSize) {
		this.maxUploadSize = maxUploadSize;
	}

	/** @param nflEik the nflEik to set */
	public void setNflEik(String nflEik) {
		this.nflEik = nflEik;
	}

	/** @param refGrj the refGrj to set */
	public void setRefGrj(Integer refGrj) {
		this.refGrj = refGrj;
	}

	/** @param refInfo the refInfo to set */
	public void setRefInfo(String refInfo) {
		this.refInfo = refInfo;
	}

	/** @param refLatin the refLatin to set */
	public void setRefLatin(String refLatin) {
		this.refLatin = refLatin;
	}

	/** @param refName the refName to set */
	public void setRefName(String refName) {
		this.refName = refName;
	}

	/** @param refRegistratura the refRegistratura to set */
	public void setRefRegistratura(Integer refRegistratura) {
		this.refRegistratura = refRegistratura;
	}

	/** @param refType the refType to set */
	public void setRefType(Integer refType) {
		this.refType = refType;
	}

	/** @return the externalCode */
	public String getExternalCode() {
		return this.externalCode;
	}
	/** @param externalCode the externalCode to set */
	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

	/** */
	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getCode(), getIdentInfo());

		return journal;
	}

//	public String getWebPage() {
//		return webPage;
//	}
//
//	public void setWebPage(String webPage) {
//		this.webPage = webPage;
//	}

//	public Integer getPolza() {
//		return polza;
//	}
//
//	public void setPolza(Integer polza) {
//		this.polza = polza;
//	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public String getFamilia() {
		return familia;
	}

	public void setFamilia(String familia) {
		this.familia = familia;
	}

	public Date getDateSmart() {
		return dateSmart;
	}

	public void setDateSmart(Date dateSmart) {
		this.dateSmart = dateSmart;
	}

	public Integer getDbAddressKorespId() {
		return this.dbAddressKorespId;
	}

	public void setDbAddressKorespId(Integer dbAddressKorespId) {
		this.dbAddressKorespId = dbAddressKorespId;
	}

	public String getLiquidation() {
		return this.liquidation;
	}
	public void setLiquidation(String liquidation) {
		this.liquidation = liquidation;
	}

	public Integer getLevelNumber() {
		return this.levelNumber;
	}
	public void setLevelNumber(Integer levelNumber) {
		this.levelNumber = levelNumber;
	}

	public Date getDateBeg() {
		return dateBeg;
	}

	public void setDateBeg(Date dateBeg) {
		this.dateBeg = dateBeg;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	/** @return the urn */
	public String getUrn() {
		return this.urn;
	}

	/** @param urn the urn to set */
	public void setUrn(String urn) {
		this.urn = urn;
	}
	
	/** @return the fzlBirthPlace */
	public String getFzlBirthPlace() {
		return this.fzlBirthPlace;
	}
	/** @param fzlBirthPlace the fzlBirthPlace to set */
	public void setFzlBirthPlace(String fzlBirthPlace) {
		this.fzlBirthPlace = fzlBirthPlace;
	}
	/** @return the nationality */
	public String getNationality() {
		return this.nationality;
	}
	/** @param nationality the nationality to set */
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	/** @return the referentDocs */
	public List<ReferentDoc> getReferentDocs() {
		return this.referentDocs;
	}
	/** @param referentDocs the referentDocs to set */
	public void setReferentDocs(List<ReferentDoc> referentDocs) {
		this.referentDocs = referentDocs;
	}

	/** @return the dbMapReferentDocs */
	@XmlTransient
	public Map<Integer, Integer> getDbMapReferentDocs() {
		return this.dbMapReferentDocs;
	}
	/** @param dbMapReferentDocs the dbMapReferentDocs to set */
	public void setDbMapReferentDocs(Map<Integer, Integer> dbMapReferentDocs) {
		this.dbMapReferentDocs = dbMapReferentDocs;
	}

	/** @return the obrazovStepen */
	public String getObrazovStepen() {
		return this.obrazovStepen;
	}
	/** @param obrazovStepen the obrazovStepen to set */
	public void setObrazovStepen(String obrazovStepen) {
		this.obrazovStepen = obrazovStepen;
	}


	/** @return the opit */
	public String getOpit() {
		return this.opit;
	}
	/** @param opit the opit to set */
	public void setOpit(String opit) {
		this.opit = opit;
	}
	
	/** Качество на представляващото лице - записва се в заявлението*/
	@Transient
	private Integer	kachestvoLice;		
	
	public Integer getKachestvoLice() {
		return kachestvoLice;
	}

	public void setKachestvoLice(Integer kachestvoLice) {
		this.kachestvoLice = kachestvoLice;
	}
	
	public String getFzlBirthPlaceLatin() {
		return fzlBirthPlaceLatin;
	}

	public void setFzlBirthPlaceLatin(String fzlBirthPlaceLatin) {
		this.fzlBirthPlaceLatin = fzlBirthPlaceLatin;
	}
	
	public String getNationalityLatin() {
		return nationalityLatin;
	}

	public void setNationalityLatin(String nationalityLatin) {
		this.nationalityLatin = nationalityLatin;
	}

	/** @return the obrazovanie */
	public Integer getObrazovanie() {
		return this.obrazovanie;
	}
	/** @param obrazovanie the obrazovanie to set */
	public void setObrazovanie(Integer obrazovanie) {
		this.obrazovanie = obrazovanie;
	}

	/** Прави сливане на стар и нов адрес
	 * @param newAddress - нов адрес
	 * @param type - тип на новия адресс
	 */
	public void mergeAddress(ReferentAddress newAddress, Integer type) {
		if (newAddress == null || type == null) {
			return;
		}
		
		newAddress.setAddrType(type);
		newAddress.setCodeRef(code);
		
		
		if (type.equals(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN)) {
			
			if (address == null) {
				setAddress(newAddress);
			}else {
				mergeAddresses(address, newAddress);
			}
			
		}else {
			if (addressKoresp == null) {
				setAddressKoresp(newAddress);
			}else {
				mergeAddresses(addressKoresp, newAddress);
			}
		}
		
		
		
		
	}

	/** "Заплясква информационните данни от новия адрес върху стария"
	 * @param address - стар адрес
	 * @param newAddress - мов адрес
	 */
	private void mergeAddresses(ReferentAddress address, ReferentAddress newAddress) {
		
		address.setAddrCountry(newAddress.getAddrCountry());
		address.setAddrText(newAddress.getAddrText());
		address.setAddrType(newAddress.getAddrType());
		address.setCodeRef(newAddress.getCodeRef());
		address.setCountryName(newAddress.getCountryName());
		address.setPostBox(newAddress.getPostBox());
		address.setPostCode(newAddress.getPostCode());  
		address.setRaion(newAddress.getRaion());
		address.setEkatte(newAddress.getEkatte());
		address.setAddrTextLatin(newAddress.getAddrTextLatin());
	}
	
	
	
}
