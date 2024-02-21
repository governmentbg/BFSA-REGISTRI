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
 * Изходен ГКПП
 */
@Entity
@Table(name = "event_deinost_jiv_gkpp")
public class EventDeinJivGkpp implements MultiClassifProperty {

	private static final long serialVersionUID = 3819616739905455888L;

	@SequenceGenerator(name = "EventDeinJivGkpp", sequenceName = "seq_event_deinost_jiv_gkpp", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinJivGkpp")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "event_deinost_jiv_id")
	private Integer eventDeinJivId;

	@Column(name = "gkpp")
	private Integer gkpp;

	/**  */
	public EventDeinJivGkpp() {
		super();
	}

	/** @param eventDeinJivId */
	public EventDeinJivGkpp(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		EventDeinJivGkpp x = new EventDeinJivGkpp(parentId);
		x.setGkpp(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from event_deinost_jiv_gkpp where event_deinost_jiv_id = ?1 and gkpp in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
	}

	/** @return the eventDeinJivId */
	public Integer getEventDeinJivId() {
		return this.eventDeinJivId;
	}

	/** @return the gkpp */
	public Integer getGkpp() {
		return this.gkpp;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	@Override
	public Integer getParentId() {
		return getEventDeinJivId();
	}

	/** @param eventDeinJivId the eventDeinJivId to set */
	public void setEventDeinJivId(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	/** @param gkpp the gkpp to set */
	public void setGkpp(Integer gkpp) {
		this.gkpp = gkpp;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}
}