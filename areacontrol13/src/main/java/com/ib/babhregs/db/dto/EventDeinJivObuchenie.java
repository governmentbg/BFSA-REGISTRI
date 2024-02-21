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
 * Сфера на обучение
 */
@Entity
@Table(name = "event_deinost_jiv_obuchenie")
public class EventDeinJivObuchenie implements MultiClassifProperty {

	private static final long serialVersionUID = -5038879313862436154L;

	@SequenceGenerator(name = "EventDeinJivObuchenie", sequenceName = "seq_event_deinost_jiv_obuchenie", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinJivObuchenie")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "event_deinost_jiv_id")
	private Integer eventDeinJivId;

	@Column(name = "sfera_obucenie")
	private Integer sferaObucenie;

	/**  */
	public EventDeinJivObuchenie() {
		super();
	}

	/** @param eventDeinJivId */
	public EventDeinJivObuchenie(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		EventDeinJivObuchenie x = new EventDeinJivObuchenie(parentId);
		x.setSferaObucenie(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from event_deinost_jiv_obuchenie where event_deinost_jiv_id = ?1 and sfera_obucenie in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
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

	/** @return the sferaObucenie */
	public Integer getSferaObucenie() {
		return this.sferaObucenie;
	}

	/** @param eventDeinJivId the eventDeinJivId to set */
	public void setEventDeinJivId(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param sferaObucenie the sferaObucenie to set */
	public void setSferaObucenie(Integer sferaObucenie) {
		this.sferaObucenie = sferaObucenie;
	}
}