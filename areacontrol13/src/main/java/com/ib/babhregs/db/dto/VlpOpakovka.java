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
 * Първична опаковка
 */
@Entity
@Table(name = "vlp_opakovka")
public class VlpOpakovka implements MultiClassifProperty {

	private static final long serialVersionUID = 8149928181424389396L;

	@SequenceGenerator(name = "VlpOpakovka", sequenceName = "seq_vlp_opakovka", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "VlpOpakovka")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "vlp_id")
	private Integer vlpId;

	@Column(name = "opakovka")
	private Integer opakovka;

	/**  */
	public VlpOpakovka() {
		super();
	}

	/** @param vlpId */
	public VlpOpakovka(Integer vlpId) {
		this.vlpId = vlpId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		VlpOpakovka x = new VlpOpakovka(parentId);
		x.setOpakovka(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from vlp_opakovka where vlp_id = ?1 and opakovka in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
	}

	/** @return the opakovka */
	public Integer getOpakovka() {
		return this.opakovka;
	}

	/** @param opakovka the opakovka to set */
	public void setOpakovka(Integer opakovka) {
		this.opakovka = opakovka;
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

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param vlpId the vlpId to set */
	public void setVlpId(Integer vlpId) {
		this.vlpId = vlpId;
	}
}