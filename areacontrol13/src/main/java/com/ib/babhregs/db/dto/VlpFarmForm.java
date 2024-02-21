package com.ib.babhregs.db.dto;

import static javax.persistence.GenerationType.SEQUENCE;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ib.babhregs.db.MultiClassifProperty;
import com.ib.system.db.JPA;

/**
 * Фармацевтична форма
 */
@Entity
@Table(name = "vlp_farm_form")
public class VlpFarmForm implements MultiClassifProperty {

	private static final long serialVersionUID = 3740975305532719978L;

	@SequenceGenerator(name = "VlpFarmForm", sequenceName = "seq_vlp_farm_form", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "VlpFarmForm")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "vlp_id")
	private Integer vlpId;

	@Column(name = "farm_form")
	private Integer farmForm;

	/**  */
	public VlpFarmForm() {
		super();
	}

	/** @param vlpId */
	public VlpFarmForm(Integer vlpId) {
		this.vlpId = vlpId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		VlpFarmForm x = new VlpFarmForm(parentId);
		x.setFarmForm(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from vlp_farm_form where vlp_id = ?1 and farm_form in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
	}

	/** @return the farmForm */
	public Integer getFarmForm() {
		return this.farmForm;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	@Override
	public Integer getParentId() {
		return getVlpId();
	}

	/** @return the vlpId */
	public Integer getVlpId() {
		return this.vlpId;
	}

	/** @param farmForm the farmForm to set */
	public void setFarmForm(Integer farmForm) {
		this.farmForm = farmForm;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param vlpId the vlpId to set */
	public void setVlpId(Integer vlpId) {
		this.vlpId = vlpId;
	}
}