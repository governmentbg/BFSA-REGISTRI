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
 * Болести, за които е предназначено средството
 */
@Entity
@Table(name = "event_deinost_vlp_bolesti")
public class EventDeinostVlpBolesti implements MultiClassifProperty {

	private static final long serialVersionUID = -4728425811662766442L;

	@SequenceGenerator(name = "EventDeinostVlpBolesti", sequenceName = "seq_event_deinost_vlp_bolesti", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostVlpBolesti")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "event_dejnost_vlp_id")
	private Integer eventDejnostVlpId;

	@Column(name = "bolest")
	private Integer bolest;

	/**  */
	public EventDeinostVlpBolesti() {
		super();
	}

	/** @param eventDejnostVlpId */
	public EventDeinostVlpBolesti(Integer eventDejnostVlpId) {
		this.eventDejnostVlpId = eventDejnostVlpId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		EventDeinostVlpBolesti x = new EventDeinostVlpBolesti(parentId);
		x.setBolest(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from event_deinost_vlp_bolesti where event_dejnost_vlp_id = ?1 and bolest in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
	}

	/** @return the bolest */
	public Integer getBolest() {
		return this.bolest;
	}

	/** @return the eventDejnostVlpId */
	public Integer getEventDejnostVlpId() {
		return this.eventDejnostVlpId;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	@Override
	public Integer getParentId() {
		return getEventDejnostVlpId();
	}

	/** @param bolest the bolest to set */
	public void setBolest(Integer bolest) {
		this.bolest = bolest;
	}

	/** @param eventDejnostVlpId the eventDejnostVlpId to set */
	public void setEventDejnostVlpId(Integer eventDejnostVlpId) {
		this.eventDejnostVlpId = eventDejnostVlpId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}
}