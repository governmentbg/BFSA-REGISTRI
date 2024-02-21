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
import com.ib.system.db.JournalAttr;

/**
 * Фуражи за превоз
 */
@Entity
@Table(name = "mps_vid_products")
public class MpsVidProducts implements Serializable {

	private static final long serialVersionUID = 5891339953764700883L;

	@SequenceGenerator(name = "MpsVidProducts", sequenceName = "seq_mps_vid_products", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "MpsVidProducts")
	@Column(name = "id", unique = true, nullable = false)
	@JournalAttr(label = "id", defaultText = "Системен идентификатор", isId = "true")
	private Integer id;

	@Column(name = "mps_id", insertable = false, updatable = false)
	private Integer mpsId;

	@Column(name = "products")
	@JournalAttr(label = "products", defaultText = "Видове продукти за превоз", classifID = "" + BabhConstants.CODE_CLASSIF_VID_PRODUCT_PREVOZ)
	private Integer products;

	@Column(name = "sastoianie")
	@JournalAttr(label = "sastoianie", defaultText = "Състояние на фуражите за пренос", classifID = "" + BabhConstants.CODE_CLASSIF_SAST_FURAJ_PREVOZ)
	private Integer sastoianie;

	/**  */
	public MpsVidProducts() {
		super();
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the mpsId */
	public Integer getMpsId() {
		return this.mpsId;
	}

	/** @return the products */
	public Integer getProducts() {
		return this.products;
	}

	/** @return the sastoianie */
	public Integer getSastoianie() {
		return this.sastoianie;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param mpsId the mpsId to set */
	public void setMpsId(Integer mpsId) {
		this.mpsId = mpsId;
	}

	/** @param products the products to set */
	public void setProducts(Integer products) {
		this.products = products;
	}

	/** @param sastoianie the sastoianie to set */
	public void setSastoianie(Integer sastoianie) {
		this.sastoianie = sastoianie;
	}
}