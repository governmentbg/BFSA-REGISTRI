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
import com.ib.indexui.system.Constants;
import com.ib.system.SysConstants;
import com.ib.system.db.JournalAttr;

/**
 * Данни за искан сертификат
 */
@Entity
@Table(name = "event_deinost_furaji_sert")
public class EventDeinostFurajiSert implements Serializable {

	private static final long serialVersionUID = -4728425811662766442L;

	@SequenceGenerator(name = "EventDeinostFurajiSert", sequenceName = "seq_event_deinost_furaji_sert", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostFurajiSert")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "event_deinost_furaji_id", insertable = false, updatable = false)
	private Integer eventDeinostFurajiId;

	@Column(name = "sert_type")
	@JournalAttr(label = "sert_type", defaultText = "Вид на сертификата", classifID = "" + BabhConstants.CODE_CLASSIF_SERT_TYPE)
	private Integer sertType; // (int8)

	@Column(name = "language")
	@JournalAttr(label = "language", defaultText = "Език, на който да бъде издаден документа")
	private String language; // (varchar)

	@Column(name = "reglament")
	@JournalAttr(label = "reglament", defaultText = "Номер на Регламент")
	private String reglament; // (varchar)

	@Column(name = "darj")
	@JournalAttr(label = "darj", defaultText = "Държава, за която е предназначен документът", classifID = "" + Constants.CODE_CLASSIF_COUNTRIES)
	private Integer darj; // (int8)

	@Column(name = "jivotni")
	@JournalAttr(label = "jivotni", defaultText = "Животни")
	private String jivotni; // (varchar)

	@Column(name = "vid_furaji")
	@JournalAttr(label = "vid_furaji", defaultText = "Вид на фуража")
	private String vidFuraji; // (varchar)

	@Column(name = "targovia_eu")
	@JournalAttr(label = "targovia_eu", defaultText = "Фуражът е разрешен за търговия на територията на ЕС", classifID = "" + SysConstants.CODE_CLASSIF_DANE)
	private Integer targoviaEu; // (int8)

	/**  */
	public EventDeinostFurajiSert() {
		super();
	}

	/** @return the darj */
	public Integer getDarj() {
		return this.darj;
	}

	/** @return the eventDeinostFurajiId */
	public Integer getEventDeinostFurajiId() {
		return this.eventDeinostFurajiId;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the jivotni */
	public String getJivotni() {
		return this.jivotni;
	}

	/** @return the language */
	public String getLanguage() {
		return this.language;
	}

	/** @return the reglament */
	public String getReglament() {
		return this.reglament;
	}

	/** @return the sertType */
	public Integer getSertType() {
		return this.sertType;
	}

	/** @return the targoviaEu */
	public Integer getTargoviaEu() {
		return this.targoviaEu;
	}

	/** @return the vidFuraji */
	public String getVidFuraji() {
		return this.vidFuraji;
	}

	/** @param darj the darj to set */
	public void setDarj(Integer darj) {
		this.darj = darj;
	}

	/** @param eventDeinostFurajiId the eventDeinostFurajiId to set */
	public void setEventDeinostFurajiId(Integer eventDeinostFurajiId) {
		this.eventDeinostFurajiId = eventDeinostFurajiId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param jivotni the jivotni to set */
	public void setJivotni(String jivotni) {
		this.jivotni = jivotni;
	}

	/** @param language the language to set */
	public void setLanguage(String language) {
		this.language = language;
	}

	/** @param reglament the reglament to set */
	public void setReglament(String reglament) {
		this.reglament = reglament;
	}

	/** @param sertType the sertType to set */
	public void setSertType(Integer sertType) {
		this.sertType = sertType;
	}

	/** @param targoviaEu the targoviaEu to set */
	public void setTargoviaEu(Integer targoviaEu) {
		this.targoviaEu = targoviaEu;
	}

	/** @param vidFuraji the vidFuraji to set */
	public void setVidFuraji(String vidFuraji) {
		this.vidFuraji = vidFuraji;
	}
}