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
 * Цели СЖП
 */
@Entity
@Table(name = "event_deinost_furaji_sgp")
public class EventDeinostFurajiSgp implements MultiClassifProperty {

	private static final long serialVersionUID = -1697183041608759039L;

	@SequenceGenerator(name = "EventDeinostFurajiSgp", sequenceName = "seq_event_deinost_furaji_sgp", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostFurajiSgp")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "event_deinost_furaji_id")
	private Integer eventDeinostFurajiId;

	@Column(name = "cel")
	private Integer cel;

	/**  */
	public EventDeinostFurajiSgp() {
		super();
	}

	/** @param eventDeinostFurajiId */
	public EventDeinostFurajiSgp(Integer eventDeinostFurajiId) {
		this.eventDeinostFurajiId = eventDeinostFurajiId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		EventDeinostFurajiSgp x = new EventDeinostFurajiSgp(parentId);
		x.setCel(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from event_deinost_furaji_sgp where event_deinost_furaji_id = ?1 and cel in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
	}

	/** @return the cel */
	public Integer getCel() {
		return this.cel;
	}

	/** @return the eventDeinostFurajiId */
	public Integer getEventDeinostFurajiId() {
		return this.eventDeinostFurajiId;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	@Override
	public Integer getParentId() {
		return getEventDeinostFurajiId();
	}

	/** @param cel the cel to set */
	public void setCel(Integer cel) {
		this.cel = cel;
	}

	/** @param eventDeinostFurajiId the eventDeinostFurajiId to set */
	public void setEventDeinostFurajiId(Integer eventDeinostFurajiId) {
		this.eventDeinostFurajiId = eventDeinostFurajiId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}
}