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
 * Категория МПС
 */
@Entity
@Table(name = "mps_category")
public class MpsCategory implements MultiClassifProperty {

	private static final long serialVersionUID = -6936927121797742783L;

	@SequenceGenerator(name = "MpsCategory", sequenceName = "seq_mps_category", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "MpsCategory")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "mps_id")
	private Integer mpsId;

	@Column(name = "category")
	private Integer category;

	/**  */
	public MpsCategory() {
		super();
	}

	/** @param mpsId */
	public MpsCategory(Integer mpsId) {
		this.mpsId = mpsId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		MpsCategory x = new MpsCategory(parentId);
		x.setCategory(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from mps_category where mps_id = ?1 and category in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
	}

	/** @return the category */
	public Integer getCategory() {
		return this.category;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the mpsId */
	public Integer getMpsId() {
		return this.mpsId;
	}

	@Override
	public Integer getParentId() {
		return getMpsId();
	}

	/** @param category the category to set */
	public void setCategory(Integer category) {
		this.category = category;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param mpsId the mpsId to set */
	public void setMpsId(Integer mpsId) {
		this.mpsId = mpsId;
	}

}