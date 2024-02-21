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
 * Обект на дейност – Предназначение
 */
@Entity
@Table(name = "obekt_deinost_prednaznachenie")
public class ObektDeinostPrednaznachenie implements MultiClassifProperty {

	private static final long serialVersionUID = -6540017887145868410L;

	@SequenceGenerator(name = "ObektDeinostPrednaznachenie", sequenceName = "seq_obekt_deinost_prednaznachenie", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "ObektDeinostPrednaznachenie")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "obekt_deinost_id")
	private Integer obektDeinostId; // (int8)

	@Column(name = "prednaznachenie")
	private Integer prednaznachenie; // (int8)

	/** */
	public ObektDeinostPrednaznachenie() {
		super();
	}

	/** @param obektDeinostId */
	public ObektDeinostPrednaznachenie(Integer obektDeinostId) {
		this.obektDeinostId = obektDeinostId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		ObektDeinostPrednaznachenie x = new ObektDeinostPrednaznachenie(parentId);
		x.setPrednaznachenie(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from obekt_deinost_prednaznachenie where obekt_deinost_id = ?1 and prednaznachenie in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	/** @return the obektDeinostId */
	public Integer getObektDeinostId() {
		return this.obektDeinostId;
	}

	@Override
	public Integer getParentId() {
		return getObektDeinostId();
	}

	/** @return the prednaznachenie */
	public Integer getPrednaznachenie() {
		return this.prednaznachenie;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param obektDeinostId the obektDeinostId to set */
	public void setObektDeinostId(Integer obektDeinostId) {
		this.obektDeinostId = obektDeinostId;
	}

	/** @param prednaznachenie the prednaznachenie to set */
	public void setPrednaznachenie(Integer prednaznachenie) {
		this.prednaznachenie = prednaznachenie;
	}
}
