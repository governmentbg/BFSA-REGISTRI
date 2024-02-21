package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.SysConstants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

/**
 * Вписване
 */
@Entity
@Table(name = "vpisvane")
public class Vpisvane extends TrackableEntity implements AuditExt {

	private static final long serialVersionUID = 7110396951971912952L;

	@SequenceGenerator(name = "Vpisvane", sequenceName = "seq_vpisvane", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "Vpisvane")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "id_register")
	@JournalAttr(label = "id_register", defaultText = "Вид регистър", classifID = "" + BabhConstants.CODE_CLASSIF_VID_REGISTRI)
	private Integer idRegister;

	@Column(name = "id_zaqavlenie")
	@JournalAttr(label = "id_zaqavlenie", defaultText = "Заявление", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC)
	private Integer idZaqavlenie;

	@Column(name = "code_ref_vpisvane")
	@JournalAttr(label = "code_ref_vpisvane", defaultText = "Заявител", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT)
	private Integer codeRefVpisvane;

	@Column(name = "reg_nom_zaqvl_vpisvane")
	@JournalAttr(label = "reg_nom_zaqvl_vpisvane", defaultText = "Рег. номер на заявление")
	private String regNomZaqvlVpisvane;

	@Column(name = "date_zaqvl_vpis")
	@JournalAttr(label = "date_zaqvl_vpis", defaultText = "Дата на заявление", dateMask = "dd.MM.yyyy HH:mm:ss")
	private Date dateZaqvlVpis;

	@Column(name = "id_result")
	@JournalAttr(label = "id_result", defaultText = "Резултатен документ", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC)
	private Integer idResult; // (int8)

	@Column(name = "reg_nom_result")
	@JournalAttr(label = "reg_nom_result", defaultText = "Рег. номер на резултатен документ")
	private String regNomResult;

	@Column(name = "date_result")
	@JournalAttr(label = "date_result", defaultText = "Дата на резултатен документ", dateMask = "dd.MM.yyyy HH:mm:ss")
	private Date dateResult;

	@Column(name = "status")
	@JournalAttr(label = "status", defaultText = "Състояние", classifID = "" + BabhConstants.CODE_CLASSIF_STATUS_VPISVANE)
	private Integer status;

	@Column(name = "date_status")
	@JournalAttr(label = "date_status", defaultText = "Дата на състояние", dateMask = "dd.MM.yyyy HH:mm:ss")
	private Date dateStatus;

	@Column(name = "vp_locked")
	@JournalAttr(label = "vp_locked", defaultText = "Приключена работа", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer vpLocked;

	@Column(name = "info")
	@JournalAttr(label = "info", defaultText = "Доп. информация")
	private String info;

	@Column(name = "licenziant_type")
	@JournalAttr(label = "licenziant_type", defaultText = "Лицензиант Тип", classifID = "" + BabhConstants.CODE_CLASSIF_OBEKT_LICENZIRANE)
	private Integer licenziantType;

	@Column(name = "id_licenziant")
	@JournalAttr(label = "id_licenziant", defaultText = "Лицензиант ИД")
	private Integer idLicenziant;

	@Column(name = "licenziant")
	@JournalAttr(label = "licenziant", defaultText = "Лицензиант")
	private String licenziant; // Договрихме ли се да сложим текстова колона, в която да има рег номер на МПС или рег номер на ОЕЗ или ЕИК/ЕГН/ЛНЧ име на лице

	@Column(name = "code_page")
	@JournalAttr(label = "code_page", defaultText = "Страница за обработка", classifID = "" + BabhConstants.CODE_CLASSIF_TIP_OBEKT_LICENZ)
	private Integer codePage;

	@Column(name = "registratura_id")
	@JournalAttr(label = "registraturaId", defaultText = "Институция(регистратура), която работи по вписването", classifID = "" + BabhConstants.CODE_CLASSIF_REGISTRATURI)
	private Integer registraturaId;

	@Column(name = "nesaotvet")
	@JournalAttr(label = "nesaotvet", defaultText = "Установени несъответствия")
	private String nesaotvet; // (varchar)

	@Column(name = "from_migr")
	@JournalAttr(label = "fromМigr", defaultText = "Идва от миграция", classifID = "" + BabhConstants.CODE_CLASSIF_DANE)
	private Integer fromМigr = 2;
	
	 
	
//
//	ОБЕКТИ НА ВПИСВАНЕ/ДЕЙНОСТ

	

	@Transient
	private EventDeinJiv eventDeinJiv; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	@Transient
	private EventDeinostFuraji eventDeinostFuraji; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	@Transient
	private EventDeinostVlp eventDeinostVlp; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	@Transient
	private Vlp vlp; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	@Transient
	private Mps mps; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	@Transient
	private OezReg oezReg; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	@Transient
	private ObektDeinost obektDeinost; // @XmlTransient долу в гетера, защото не влиза по този начин в журнала

	/** */
	public Vpisvane() {
		super();
	}

	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE;
	}

	/** @return the codePage */
	public Integer getCodePage() {
		return this.codePage;
	}

	/** @return the codeRefVpisvane */
	public Integer getCodeRefVpisvane() {
		return this.codeRefVpisvane;
	}

	/** @return the dateResult */
	public Date getDateResult() {
		return this.dateResult;
	}

	/** @return the dateStatus */
	public Date getDateStatus() {
		return this.dateStatus;
	}

	/** @return the dateZaqvlVpis */
	public Date getDateZaqvlVpis() {
		return this.dateZaqvlVpis;
	}

	/** @return the eventDeinJiv */
	@XmlTransient
	public EventDeinJiv getEventDeinJiv() {
		return this.eventDeinJiv;
	}

	/** @return the eventDeinostFuraji */
	@XmlTransient
	public EventDeinostFuraji getEventDeinostFuraji() {
		return this.eventDeinostFuraji;
	}

	/** @return the eventDeinostVlp */
	@XmlTransient
	public EventDeinostVlp getEventDeinostVlp() {
		return this.eventDeinostVlp;
	}

	/** @return the id */
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public String getIdentInfo() throws DbErrorException {
		StringBuilder sb = new StringBuilder();

		if (this.regNomZaqvlVpisvane != null) {
			sb.append(this.regNomZaqvlVpisvane);
		}
		if (this.dateZaqvlVpis != null) {
			if (sb.length() > 0) {
				sb.append("/");
			}
			sb.append(new SimpleDateFormat("dd.MM.yyyy").format(this.dateZaqvlVpis));
		}
		if (this.licenziant != null) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(this.licenziant);
		}

		return sb.toString();
	}

	/** @return the idLicenziant */
	public Integer getIdLicenziant() {
		return this.idLicenziant;
	}

	/** @return the idRegister */
	public Integer getIdRegister() {
		return this.idRegister;
	}

	/** @return the idResult */
	public Integer getIdResult() {
		return this.idResult;
	}

	/** @return the idZaqavlenie */
	public Integer getIdZaqavlenie() {
		return this.idZaqavlenie;
	}

	/** @return the info */
	public String getInfo() {
		return this.info;
	}

	/** @return the licenziant */
	public String getLicenziant() {
		return this.licenziant;
	}

	/** @return the licenziantType */
	public Integer getLicenziantType() {
		return this.licenziantType;
	}

	/** @return the mps */
	@XmlTransient
	public Mps getMps() {
		return this.mps;
	}

	/** @return the nesaotvet */
	public String getNesaotvet() {
		return this.nesaotvet;
	}

	/** @return the obektDeinost */
	@XmlTransient
	public ObektDeinost getObektDeinost() {
		return this.obektDeinost;
	}

	/** @return the oezReg */
	@XmlTransient
	public OezReg getOezReg() {
		return this.oezReg;
	}

	/** @return the registraturaId */
	public Integer getRegistraturaId() {
		return this.registraturaId;
	}

	/** @return the regNomResult */
	public String getRegNomResult() {
		return this.regNomResult;
	}

	/** @return the regNomZaqvlVpisvane */
	public String getRegNomZaqvlVpisvane() {
		return this.regNomZaqvlVpisvane;
	}

	/** @return the status */
	public Integer getStatus() {
		return this.status;
	}

	/** @return the vlp */
	@XmlTransient
	public Vlp getVlp() {
		return this.vlp;
	}

	/** @param codePage the codePage to set */
	public void setCodePage(Integer codePage) {
		this.codePage = codePage;
	}

	/** @param codeRefVpisvane the codeRefVpisvane to set */
	public void setCodeRefVpisvane(Integer codeRefVpisvane) {
		this.codeRefVpisvane = codeRefVpisvane;
	}

	/** @param dateResult the dateResult to set */
	public void setDateResult(Date dateResult) {
		this.dateResult = dateResult;
	}

	/** @param dateStatus the dateStatus to set */
	public void setDateStatus(Date dateStatus) {
		this.dateStatus = dateStatus;
	}

	/** @param dateZaqvlVpis the dateZaqvlVpis to set */
	public void setDateZaqvlVpis(Date dateZaqvlVpis) {
		this.dateZaqvlVpis = dateZaqvlVpis;
	}

	/** @param eventDeinJiv the eventDeinJiv to set */
	public void setEventDeinJiv(EventDeinJiv eventDeinJiv) {
		this.eventDeinJiv = eventDeinJiv;
	}

	/** @param eventDeinostFuraji the eventDeinostFuraji to set */
	public void setEventDeinostFuraji(EventDeinostFuraji eventDeinostFuraji) {
		this.eventDeinostFuraji = eventDeinostFuraji;
	}

	/** @param eventDeinostVlp the eventDeinostVlp to set */
	public void setEventDeinostVlp(EventDeinostVlp eventDeinostVlp) {
		this.eventDeinostVlp = eventDeinostVlp;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param idLicenziant the idLicenziant to set */
	public void setIdLicenziant(Integer idLicenziant) {
		this.idLicenziant = idLicenziant;
	}

	/** @param idRegister the idRegister to set */
	public void setIdRegister(Integer idRegister) {
		this.idRegister = idRegister;
	}

	/** @param idResult the idResult to set */
	public void setIdResult(Integer idResult) {
		this.idResult = idResult;
	}

	/** @param idZaqavlenie the idZaqavlenie to set */
	public void setIdZaqavlenie(Integer idZaqavlenie) {
		this.idZaqavlenie = idZaqavlenie;
	}

	/** @param info the info to set */
	public void setInfo(String info) {
		this.info = info;
	}

	/** @param licenziant the licenziant to set */
	public void setLicenziant(String licenziant) {
		this.licenziant = licenziant;
	}

	/** @param licenziantType the licenziantType to set */
	public void setLicenziantType(Integer licenziantType) {
		this.licenziantType = licenziantType;
	}

	/** @param mps the mps to set */
	public void setMps(Mps mps) {
		this.mps = mps;
	}

	/** @param nesaotvet the nesaotvet to set */
	public void setNesaotvet(String nesaotvet) {
		this.nesaotvet = nesaotvet;
	}

	/** @param obektDeinost the obektDeinost to set */
	public void setObektDeinost(ObektDeinost obektDeinost) {
		this.obektDeinost = obektDeinost;
	}

	/** @param oezReg the oezReg to set */
	public void setOezReg(OezReg oezReg) {
		this.oezReg = oezReg;
	}

	/** @param registraturaId the registraturaId to set */
	public void setRegistraturaId(Integer registraturaId) {
		this.registraturaId = registraturaId;
	}

	/** @param regNomResult the regNomResult to set */
	public void setRegNomResult(String regNomResult) {
		this.regNomResult = regNomResult;
	}

	/** @param regNomZaqvlVpisvane the regNomZaqvlVpisvane to set */
	public void setRegNomZaqvlVpisvane(String regNomZaqvlVpisvane) {
		this.regNomZaqvlVpisvane = regNomZaqvlVpisvane;
	}

	/** @param status the status to set */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/** @param vlp the vlp to set */
	public void setVlp(Vlp vlp) {
		this.vlp = vlp;
	}

	
	public Integer getFromМigr() {
		return fromМigr;
	}

	public void setFromМigr(Integer fromМigr) {
		this.fromМigr = fromМigr;
	}
	
	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());

		if (this.idLicenziant != null //
				&& Objects.equals(this.licenziantType, BabhConstants.CODE_ZNACHENIE_TIP_LICENZ_VLZ)) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_VLZ);
			journal.setJoinedIdObject1(this.idLicenziant);

		} else if (this.idLicenziant != null //
				&& Objects.equals(this.licenziantType, BabhConstants.CODE_ZNACHENIE_TIP_LICENZ_MPS)) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_MPS);
			journal.setJoinedIdObject1(this.idLicenziant);

		} else if (this.idLicenziant != null //
				&& Objects.equals(this.licenziantType, BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_OEZ)) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_OEZ);
			journal.setJoinedIdObject1(this.idLicenziant);

		} else if (this.idLicenziant != null //
				&& Objects.equals(this.licenziantType, BabhConstants.CODE_ZNACHENIE_OBEKT_LICENZ_LICE)) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT);
			journal.setJoinedIdObject1(this.idLicenziant);
		}

		if (this.codeRefVpisvane != null) { // закачам го и към заявителя (реф2) като може при лицензиране на лице да съвпада със idLicenziant, но не пречи
			journal.setJoinedCodeObject2(BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT);
			journal.setJoinedIdObject2(this.codeRefVpisvane);
		}

		return journal;
	}

	/** @return the vpLocked */
	public Integer getVpLocked() {
		return this.vpLocked;
	}
	/** @param vpLocked the vpLocked to set */
	public void setVpLocked(Integer vpLocked) {
		this.vpLocked = vpLocked;
	}
}
