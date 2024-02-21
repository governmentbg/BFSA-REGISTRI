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
 * Страни, за които се изнася
 */
@Entity
@Table(name = "event_deinost_jiv_darj")
public class EventDeinJivDarj implements MultiClassifProperty {

	private static final long serialVersionUID = 732542368352254046L;

	@SequenceGenerator(name = "EventDeinJivDarj", sequenceName = "seq_event_deinost_jiv_darj", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinJivDarj")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "event_deinost_jiv_id")
	private Integer eventDeinJivId;

	@Column(name = "darj")
	private Integer darj;

	/**  */
	public EventDeinJivDarj() {
		super();
	}

	/** @param eventDeinJivId */
	public EventDeinJivDarj(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		EventDeinJivDarj x = new EventDeinJivDarj(parentId);
		x.setDarj(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from event_deinost_jiv_darj where event_deinost_jiv_id = ?1 and darj in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
	}

	/** @return the darj */
	public Integer getDarj() {
		return this.darj;
	}

	/** @return the eventDeinJivId */
	public Integer getEventDeinJivId() {
		return this.eventDeinJivId;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	@Override
	public Integer getParentId() {
		return getEventDeinJivId();
	}

	/** @param darj the darj to set */
	public void setDarj(Integer darj) {
		this.darj = darj;
	}

	/** @param eventDeinJivId the eventDeinJivId to set */
	public void setEventDeinJivId(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}
}