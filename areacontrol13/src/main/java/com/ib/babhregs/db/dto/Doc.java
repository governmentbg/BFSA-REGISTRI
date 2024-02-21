package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.system.Constants;
import com.ib.system.SysConstants;
import com.ib.system.db.AuditExt;
import com.ib.system.db.JournalAttr;
import com.ib.system.db.TrackableEntity;
import com.ib.system.db.dto.SystemJournal;
import com.ib.system.exceptions.DbErrorException;

/**
 * ОБЕКТ ДОКУМЕНТ (ПОСТЪПИЛИ ЗАЯВЛЕНИЯ ЗА РЕГИСТРИ) В РАМКИТЕ НА БАБХ
 *
 * @author s.arnaudova
 */

/**
 * 
 */
@Entity
@Table(name = "DOC")
public class Doc extends TrackableEntity implements AuditExt {


	private static final long serialVersionUID = -411435325531593423L;
	
	
	@SequenceGenerator(name = "Doc", sequenceName = "SEQ_DOC", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "Doc")
	@Column(name = "DOC_ID", unique = true, nullable = false)
	private Integer id;
	
	
	/**
	 * ТИП НА ДОКУМЕНТА (входящ, изходящ)
	 */
	@Column(name = "DOC_TYPE")
	@JournalAttr(label="docType",defaultText = "Тип на документа",classifID="129")
	private Integer docType;

	
	/**
	 * ВИД НА ДОКУМЕНТА (заявление, лиценз, заповед, разрешение, удостоверение за регистрация, отказ на вписване и др.)
	 */
	@Column(name = "DOC_VID")
	@JournalAttr(label="docVid",defaultText = "Вид на документа", classifID = "104")
	private Integer docVid;
	
	/**
	 * Под ВИД НА заявление (група заявления са обединени в едно осново при подаване)
	 */
	@Column(name = "DOC_POD_VID")
	@JournalAttr(label="docPodVid",defaultText = "Под вид на заявление", classifID = "104")
	private Integer docPodVid;
	
	/**
	 * ID НА РЕГИСТРАТУРА (къде е регистриран документът – ЦУ на БАБХ или ОДБХ)
	 */
	@Column(name = "REGISTRATURA_ID")
	@JournalAttr(label="registraturaId", defaultText = "Регистратура", classifID = "" + BabhConstants.CODE_CLASSIF_REGISTRATURI)
	private Integer registraturaId;

	
	/**
	 * ДЕЛОВОДЕН НОМЕР (регистрационен номер на документ)
	 */
	@Column(name = "RN_DOC")
	@JournalAttr(label="rnDoc", defaultText = "Деловоден номер номер")
	private String rnDoc;
	
	
	/**
	 * Код на електронната услуга
	 */
	@Column(name = "code_usluga")
	@JournalAttr(label="code_usluga", defaultText = "Код на административната услуга")
	private String codeAdmUsluga;
	
	
	/**
	 * Наименование на електронната услуга
	 */
	@Column(name = "ime_usluga")
	@JournalAttr(label="ime_usluga", defaultText = "Наименование на административната услуга")
	private String imeAdnUsluga;
	
	
	/**
	 * ДАТА НА ДОКУМЕНТА
	 */
	@Column(name = "DOC_DATE")
	@JournalAttr(label="docDate", defaultText = "Дата на документ", dateMask = "dd.MM.yyyy HH:mm:ss")
	private Date docDate;
	
	/**
	 * ПОДАТЕЛ НА ДОКУМЕНТА 
	 */
	@Column(name = "CODE_REF_CORRESP")
	@JournalAttr(label="codeRefCorresp", defaultText = "Подател", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT)
	private Integer codeRefCorresp;
	
	/**
	 * Заявител
	 */
	@Column(name = "code_ref_zaiavitel")
	@JournalAttr(label="code_ref_zaiavitel", defaultText = "Заявител", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_REFERENT)
	private Integer codeRefZaiavitel;

	/**
	 * КАЧЕСТВО НА представляващото лице (управител, собственик, лице за контакт, упълномощено лице)
	 */
	@Column(name = "KACHESTVO_LICE")
	@JournalAttr(label="kachestvoLice", defaultText = "Качество на представляващото лице", classifID = "" + BabhConstants.CODE_CLASSIF_KACHESTVO_LICE)
	private Integer kachestvoLice;
	
	

	/**
	 * ПОДПИСАЛ ДОКУМЕНТА
	 */
	@Column(name = "PODPISAL_DOC")
	@JournalAttr(label="podpisalDoc", defaultText = "Подписал документа", classifID = "" + Constants.CODE_CLASSIF_ADMIN_STR)
	private Integer podpisal;
	
	
	/**
	 * ОТНОСНО (анотация – кратко съдържание на документа) 
	 */
	@Column(name = "OTNOSNO")
	@JournalAttr(label="otnosno", defaultText = "Относно")
	private String otnosno;
	
	
	/**
	 * СУМА ЗА ПЛАЩАНЕ (такса за съответното заявление според настройките на регистъра)
	 */
	@Column(name = "PAY_AMOUNT")
	@JournalAttr(label="payAmount", defaultText = "Сума за плащане")
	private Float payAmount = 0F;
	
	/**
	 * ПЛАТЕНО (колко е платено)
	 */
	@Column(name = "PAY_AMOUNT_REAL")
	@JournalAttr(label="payAmountReal", defaultText = "Платена сума")
	private Float payAmountReal = 0F;
	
	@Column(name = "PAY_DATE")
	@JournalAttr(label="payDate", defaultText = "Дата на плащане", dateMask = "dd.MM.yyyy HH:mm:ss")
	private Date payDate;
	
	@Column(name = "PAY_TYPE")
	@JournalAttr(label="payType", defaultText = "Тип на плащане",  classifID = "" + BabhConstants.CODE_CLASSIF_METOD_PLASHTANE)
	private Integer payType;
	
	@Column(name = "PLATENO")
	@JournalAttr(label="plateno", defaultText = "Платено (да/не)", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer indPlateno;
	
	@Column(name = "VALID")
	@JournalAttr(label="valid", defaultText = "Валидност (да/не)", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer valid;

	@Column(name = "VALID_DATE")
	@JournalAttr(label="validDate", defaultText = "Дата на вид на валидност", dateMask = "dd.MM.yyyy HH:mm:ss")
	private Date validDate;

	@Column(name = "DATE_VALID_AKT")
	@JournalAttr(label="dateValidAkt", defaultText = "Дата на валидност на акт", dateMask = "dd.MM.yyyy")
	private Date dateValidAkt;
	
	@Column(name = "DOC_INFO")
	@JournalAttr(label="docInfo",defaultText = "Допълнителна информация")
	private String docInfo;
	
	@Column(name = "RECEIVE_METHOD")
	@JournalAttr(label="receiveMethod", defaultText = "Начин на получаване на резултат", classifID = "" + BabhConstants.CODE_CLASSIF_NACHIN_POLUCHAVANE)
	private Integer receiveMethod;
	
	@Column(name = "RECEIVED_BY")
	@JournalAttr(label="receiveBy",defaultText = "Получен доп.информация")
	private String receivedBy;
	
	@Column(name = "ADDRESS_POLUCHAVANE")
	@JournalAttr(label="",defaultText = "Адрес за получаване на резултат")
	private String adressRezultat;
	
	@Column(name = "PROC_DEF")
	@JournalAttr(label="proceduraDef", defaultText = "Процедура", classifID = "" + BabhConstants.CODE_CLASSIF_PROCEDURI)
	private Integer procedura;
	
	@Column(name = "STATUS")
	@JournalAttr(label="status", defaultText = "Статус на обработка на заявление", classifID = "" + BabhConstants.CODE_CLASSIF_DOC_STATUS)
	private Integer status;

	@Column(name = "STATUS_DATE")
	@JournalAttr(label="statusDate", defaultText = "Дата на статус", dateMask = "dd.MM.yyyy HH:mm:ss")
	private Date statusDate;
	
	@Column(name = "STATUS_REASON")
	@JournalAttr(label="statusReason", defaultText = "Причина за статуса", classifID = "" + BabhConstants.CODE_CLASSIF_DOC_IRREGULAR)
	private Integer statusReason;
	
	@Column(name = "STATUS_REASON_DOP")
	@JournalAttr(label="statusReasonDop", defaultText = "Причина за статуса - допълнителна информация")
	private String dopInfoStatus;
	
	
	
	
	
	
	@Column(name = "task_info")
	@JournalAttr(label="taskInfo", defaultText = "Текст на задача")
	private String taskInfo ;
	
	
	
	/**
	 * ТИП НА ЛИЦЕНЗИАНТА (ОЕЗ, МПС, ВЛ, ВЛП и др.)
	 */
	@Column(name = "LICENZIANT_TYPE")
	@JournalAttr(label="licenziantType", defaultText = "Лицензиант Тип", classifID = "" + BabhConstants.CODE_CLASSIF_OBEKT_LICENZIRANE)
	private Integer licenziantType;
	
	/**
	 * Ид на регистър от класификация Видове регистри БАБХ
	 */
	@Column(name = "REGISTER_ID")
//	@JournalAttr(label="registerId",defaultText = "Регистър",classifID = ""+BabhConstants.CODE_CLASSIF_VID_REGISTRI)
	private Integer registerId;

	
	/**
	 * ID НА ЛИЦЕНЗИАНТА
	 */
	@Column(name = "ID_LICENZIANT")
	@JournalAttr(label="idLicenziant", defaultText = "Лицензиант ИД")
	private Integer idLicenziant;
	
	/*****************************************************************/
	
	
	@Column(name = "change_bg")
	@JournalAttr(label="change_bg", defaultText = "Описание на промяната на Български")
	private String changeBg;

	@Column(name = "change_en")
	@JournalAttr(label="change_en", defaultText = "Описание на промяната на Английски")
	private String changeEn;

	@Column(name = "doc_version")
	@JournalAttr(label="doc_version", defaultText = "Версия на документа")
	private String docVersion;
	
	@Override
	public Integer getCodeMainObject() {
		return BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC;
	}

	@Transient
	private transient Integer dbStatus; // стария статус

	// с това ще може да се върже в журнала към вписването, за да се вижда през преглед на журнала за вписване
	@Transient
	private Integer vpisvaneId;

	@Override
	public SystemJournal toSystemJournal() throws DbErrorException {
		SystemJournal journal = new SystemJournal(getCodeMainObject(), getId(), getIdentInfo());

		if (this.vpisvaneId != null) {
			journal.setJoinedCodeObject1(BabhConstants.CODE_ZNACHENIE_JOURNAL_VPISVANE);
			journal.setJoinedIdObject1(this.vpisvaneId);
		}
		return journal;
	}
	
	/**********************************************************************************************************************************/
	// ТУК СА ТАКИВА, КОИТО ГИ НЯМА СЕ ВЗИМАТ ОТ ОСНОВНИЯ КЛАС НА ДОКУТО, ЗАЩОТО СЕ ИЗПОЛЗВАТ ПО ЗНАЙНИ И НЕЗНАЙНИ МЕСТА
	
	@Column(name = "COUNT_FILES")
	@JournalAttr(label="countFiles",defaultText = "Брой файлове")
	private Integer countFiles;

	@Column(name = "RN_PREFIX")
	@JournalAttr(label="rnPrefix",defaultText = "Префикс на номера")
	private String rnPrefix;

	@Column(name = "RN_PORED")
	@JournalAttr(label="rnPored",defaultText = "Пореден номер от номера")
	private Integer rnPored;

	@Column(name = "FREE_ACCESS")
	@JournalAttr(label="freeAccess",defaultText = "Свободен достъп", classifID = ""+ SysConstants.CODE_CLASSIF_DANE)
	private Integer freeAccess;

	@Column(name = "RECEIVE_DATE")
	@JournalAttr(label="receiveDate",defaultText = "Дата на получаване")
	private Date receiveDate;

	@Column(name = "WORK_OFF_ID")
	@JournalAttr(label="workOffId",defaultText = "Работен/официален документ", codeObject = BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC)
	private Integer workOffId;

	@Column(name = "GUID")
	@JournalAttr(label="guid",defaultText = "GUID")
	private String guid;

	@JournalAttr(label="payment_id",defaultText = "Идентификатор плащане")
	@Column(name = "payment_id")
	private String paymentId;
	
	@JournalAttr(label="tlp_level",defaultText = "Ниво на класификация")
	@Column(name = "tlp_level")
	private Integer tlpLevel;
	
	
	//0 - Миграция, 1-Еформи, 2-Ръчно
	@Column(name = "method_reg")
	@JournalAttr(label="methodReg",defaultText = "Метод на регистрация")
	private Integer methodReg = BabhConstants.METHOD_REG_OTHER;
	
	

	public Integer getCountFiles() {
		return this.countFiles;
	}
	public void setCountFiles(Integer countFiles) {
		this.countFiles = countFiles;
	}
	public String getRnPrefix() {
		return this.rnPrefix;
	}
	public void setRnPrefix(String rnPrefix) {
		this.rnPrefix = rnPrefix;
	}
	public Integer getRnPored() {
		return this.rnPored;
	}
	public void setRnPored(Integer rnPored) {
		this.rnPored = rnPored;
	}
	public Integer getFreeAccess() {
		return this.freeAccess;
	}
	public void setFreeAccess(Integer freeAccess) {
		this.freeAccess = freeAccess;
	}
	public Integer getRegisterId() {
		return this.registerId;
	}
	public void setRegisterId(Integer registerId) {
		this.registerId = registerId;
	}
	public Date getReceiveDate() {
		return this.receiveDate;
	}
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	public Integer getWorkOffId() {
		return this.workOffId;
	}
	public void setWorkOffId(Integer workOffId) {
		this.workOffId = workOffId;
	}
	public String getGuid() {
		return this.guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	/**********************************************************************************************************************************/

	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDocType() {
		return docType;
	}

	public void setDocType(Integer docType) {
		this.docType = docType;
	}

	public Integer getDocVid() {
		return docVid;
	}

	public void setDocVid(Integer docVid) {
		this.docVid = docVid;
	}
	
	public Integer getDocPodVid() {
		return docPodVid;
	}

	public void setDocPodVid(Integer docPodVid) {
		this.docPodVid = docPodVid;
	}

	public Integer getRegistraturaId() {
		return registraturaId;
	}

	public void setRegistraturaId(Integer registraturaId) {
		this.registraturaId = registraturaId;
	}

	public String getRnDoc() {
		return rnDoc;
	}

	public void setRnDoc(String rnDoc) {
		this.rnDoc = rnDoc;
	}

	public Date getDocDate() {
		return docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}



	public Integer getPodpisal() {
		return podpisal;
	}

	public void setPodpisal(Integer podpisal) {
		this.podpisal = podpisal;
	}

	public String getOtnosno() {
		return otnosno;
	}

	public void setOtnosno(String otnosno) {
		this.otnosno = otnosno;
	}

	public Float getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(Float payAmount) {
		this.payAmount = payAmount;
	}


	public Integer getIndPlateno() {
		return indPlateno;
	}

	public void setIndPlateno(Integer indPlateno) {
		this.indPlateno = indPlateno;
	}

	/** @return the dateValidAkt */
	public Date getDateValidAkt() {
		return this.dateValidAkt;
	}
	/** @param dateValidAkt the dateValidAkt to set */
	public void setDateValidAkt(Date dateValidAkt) {
		this.dateValidAkt = dateValidAkt;
	}

	public String getDocInfo() {
		return docInfo;
	}

	public void setDocInfo(String docInfo) {
		this.docInfo = docInfo;
	}

	public String getAdressRezultat() {
		return adressRezultat;
	}

	public void setAdressRezultat(String adressRezultat) {
		this.adressRezultat = adressRezultat;
	}

	public Integer getProcedura() {
		return procedura;
	}

	public void setProcedura(Integer procedura) {
		this.procedura = procedura;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Integer getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(Integer statusReason) {
		this.statusReason = statusReason;
	}

	public String getDopInfoStatus() {
		return dopInfoStatus;
	}

	public void setDopInfoStatus(String dopInfoStatus) {
		this.dopInfoStatus = dopInfoStatus;
	}

	/** @return the codeRefCorresp */
	public Integer getCodeRefCorresp() {
		return this.codeRefCorresp;
	}
	/** @param codeRefCorresp the codeRefCorresp to set */
	public void setCodeRefCorresp(Integer codeRefCorresp) {
		this.codeRefCorresp = codeRefCorresp;
	}

	public Integer getLicenziantType() {
		return licenziantType;
	}

	public void setLicenziantType(Integer licenziantType) {
		this.licenziantType = licenziantType;
	}

	public Integer getIdLicenziant() {
		return idLicenziant;
	}

	public void setIdLicenziant(Integer idLicenziant) {
		this.idLicenziant = idLicenziant;
	}

	public String getReceivedBy() {
		return receivedBy;
	}

	public void setReceivedBy(String receivedBy) {
		this.receivedBy = receivedBy;
	}

	public Integer getReceiveMethod() {
		return receiveMethod;
	}

	public void setReceiveMethod(Integer receiveMethod) {
		this.receiveMethod = receiveMethod;
	}

	public Integer getDbStatus() {
		return this.dbStatus;
	}
	public void setDbStatus(Integer dbStatus) {
		this.dbStatus = dbStatus;
	}

	@Override
	public String getIdentInfo() throws DbErrorException {
		StringBuilder sb = new StringBuilder();

		if (this.rnDoc != null) {
			sb.append(this.rnDoc);
		}
		if (this.docDate != null) {
			if (sb.length() > 0) {
				sb.append("/");
			}
			sb.append(new SimpleDateFormat("dd.MM.yyyy").format(this.docDate));
		}

		return sb.toString();
	}


	public Float getPayAmountReal() {
		return payAmountReal;
	}


	public void setPayAmountReal(Float payAmountReal) {
		this.payAmountReal = payAmountReal;
	}
	

	public Integer getKachestvoLice() {
		return kachestvoLice;
	}
	
	public void setKachestvoLice(Integer kachestvoLice) {
		this.kachestvoLice = kachestvoLice;
	}
	
	public Integer getPayType() {
		return payType;
	}
	
	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Date getPayDate() {
		return payDate;
	}
	
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	/** @return the vpisvaneId */
	public Integer getVpisvaneId() {
		return this.vpisvaneId;
	}
	/** @param vpisvaneId the vpisvaneId to set */
	public void setVpisvaneId(Integer vpisvaneId) {
		this.vpisvaneId = vpisvaneId;
	}
	public String getCodeAdmUsluga() {
		return codeAdmUsluga;
	}
	public void setCodeAdmUsluga(String codeAdmUsluga) {
		this.codeAdmUsluga = codeAdmUsluga;
	}
	
	/** @return the valid */
	public Integer getValid() {
		return this.valid;
	}
	/** @param valid the valid to set */
	public void setValid(Integer valid) {
		this.valid = valid;
	}
	/** @return the validDate */
	public Date getValidDate() {
		return this.validDate;
	}
	/** @param validDate the validDate to set */
	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}
	public String getImeAdnUsluga() {
		return imeAdnUsluga;
	}
	public void setImeAdnUsluga(String imeAdnUsluga) {
		this.imeAdnUsluga = imeAdnUsluga;
	}
	public String getTaskInfo() {
		return taskInfo;
	}
	public void setTaskInfo(String taskInfo) {
		this.taskInfo = taskInfo;
	}
	
	/** @return the paymentId */
	public String getPaymentId() {
		return this.paymentId;
	}
	/** @param paymentId the paymentId to set */
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	/** @return the changeBg */
	public String getChangeBg() {
		return this.changeBg;
	}
	/** @param changeBg the changeBg to set */
	public void setChangeBg(String changeBg) {
		this.changeBg = changeBg;
	}
	/** @return the changeEn */
	public String getChangeEn() {
		return this.changeEn;
	}
	/** @param changeEn the changeEn to set */
	public void setChangeEn(String changeEn) {
		this.changeEn = changeEn;
	}
	/** @return the docVersion */
	public String getDocVersion() {
		return this.docVersion;
	}
	/** @param docVersion the docVersion to set */
	public void setDocVersion(String docVersion) {
		this.docVersion = docVersion;
	}
	
	/** @return the codeRefZaiavitel */
	public Integer getCodeRefZaiavitel() {
		return this.codeRefZaiavitel;
	}
	/** @param codeRefZaiavitel the codeRefZaiavitel to set */
	public void setCodeRefZaiavitel(Integer codeRefZaiavitel) {
		this.codeRefZaiavitel = codeRefZaiavitel;
	}

	/** @return the methodReg */
	public Integer getMethodReg() {
		return this.methodReg;
	}
	/** @param methodReg the methodReg to set */
	public void setMethodReg(Integer methodReg) {
		this.methodReg = methodReg;
	}
	
	/** @return the tlpLevel */
	public Integer getTlpLevel() {
		return this.tlpLevel;
	}
	/** @param tlpLevel the tlpLevel to set */
	public void setTlpLevel(Integer tlpLevel) {
		this.tlpLevel = tlpLevel;
	}
}
