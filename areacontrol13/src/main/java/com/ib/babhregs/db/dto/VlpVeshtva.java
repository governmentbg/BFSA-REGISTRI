package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JournalAttr;

/**
 * Вещества
 */
@Entity
@Table(name = "vlp_veshtva")
public class VlpVeshtva implements Serializable {

	private static final long serialVersionUID = -8757466372742098212L;

	@SequenceGenerator(name = "VlpVeshtva", sequenceName = "seq_vlp_veshtva", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "VlpVeshtva")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "vlp_id", insertable = false, updatable = false)
	private Integer vlpId;

	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "vid_identifier")
	@JournalAttr(label = "vid_identifier", defaultText = "Код вещество")
	private Substance vidIdentifier = new Substance(); // (varchar(25))

	@Column(name = "quantity")
	@JournalAttr(label = "quantity", defaultText = "Количество")
	private String quantity; // (varchar(255))

	@Column(name = "measurement")
	@JournalAttr(label = "measurement", defaultText = "Мерна единица")
	private String measurement; // (varchar(255))

	@Column(name = "standart")
	@JournalAttr(label = "standart", defaultText = "Стандарт или монография")
	private String standart; // (varchar(255))



	@Column(name = "opis_etapi")
	@JournalAttr(label = "opis_etapi", defaultText = "Описание на производствени етапи")
	private String opisEtapi; // (varchar(500))

	@Column(name = "type")
	@JournalAttr(label = "type", defaultText = "Тип вещества", classifID = "" + BabhConstants.CODE_CLASSIF_VID_VESHT_ZA_VLP)
	private Integer type; // (int8)

	@Column(name = "vid_text")
	@JournalAttr(label = "vid_text", defaultText = "Вид на помощното вещество")
	private String vidText; // (varchar(500))

	@Column(name = "proizvoditel")
	@JournalAttr(label = "proizvoditel", defaultText = "Производител")
	private String proizvoditel = ""; // (varchar(500))


	@Transient
	private int cntProizvoditeli = 0;
	
	
	
	/** */
	public VlpVeshtva() {
		super();
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the measurement */
	public String getMeasurement() {
		return this.measurement;
	}

	

	/** @return the opisEtapi */
	public String getOpisEtapi() {
		return this.opisEtapi;
	}

	/** @return the proizvoditel */
	public String getProizvoditel() {
		return this.proizvoditel;
	}

	

	/** @return the quantity */
	public String getQuantity() {
		return this.quantity;
	}

	

	/** @return the standart */
	public String getStandart() {
		return this.standart;
	}

	/** @return the type */
	public Integer getType() {
		return this.type;
	}

	/** @return the vidIdentifier */
	public Substance getVidIdentifier() {
		return this.vidIdentifier;
	}

	/** @return the vidText */
	public String getVidText() {
		return this.vidText;
	}

	/** @return the vlpId */
	public Integer getVlpId() {
		return this.vlpId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param measurement the measurement to set */
	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	

	/** @param opisEtapi the opisEtapi to set */
	public void setOpisEtapi(String opisEtapi) {
		this.opisEtapi = opisEtapi;
	}

	/** @param proizvoditel the proizvoditel to set */
	public void setProizvoditel(String proizvoditel) {
		this.proizvoditel = proizvoditel;
	}

	

	/** @param quantity the quantity to set */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	

	/** @param standart the standart to set */
	public void setStandart(String standart) {
		this.standart = standart;
	}

	/** @param type the type to set */
	public void setType(Integer type) {
		this.type = type;
	}

	/** @param vidIdentifier the vidIdentifier to set */
	public void setVidIdentifier(Substance vidIdentifier) {
		this.vidIdentifier = vidIdentifier;
	}

	/** @param vidText the vidText to set */
	public void setVidText(String vidText) {
		this.vidText = vidText;
	}

	/** @param vlpId the vlpId to set */
	public void setVlpId(Integer vlpId) {
		this.vlpId = vlpId;
	}

	public int getCntProizvoditeli() {
		return cntProizvoditeli;
	}

	public void setCntProizvoditeli(int cntProizvoditeli) {
		this.cntProizvoditeli = cntProizvoditeli;
	}

	
	
}
