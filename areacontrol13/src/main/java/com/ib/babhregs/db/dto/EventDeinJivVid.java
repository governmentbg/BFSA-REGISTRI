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
 * Вид на дейността
 */
@Entity
@Table(name = "event_deinost_jiv_vid")
public class EventDeinJivVid implements MultiClassifProperty {

	private static final long serialVersionUID = -345986210488971698L;

	@SequenceGenerator(name = "EventDeinJivVid", sequenceName = "seq_event_deinost_jiv_vid", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinJivVid")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "event_deinost_jiv_id")
	private Integer eventDeinJivId;

	@Column(name = "vid")
	private Integer vid;

	/**  */
	public EventDeinJivVid() {
		super();
	}

	/** @param eventDeinJivId */
	public EventDeinJivVid(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		EventDeinJivVid x = new EventDeinJivVid(parentId);
		x.setVid(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from event_deinost_jiv_vid where event_deinost_jiv_id = ?1 and vid in (?2)";
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

	/** @return the vid */
	public Integer getVid() {
		return this.vid;
	}

	/** @param eventDeinJivId the eventDeinJivId to set */
	public void setEventDeinJivId(Integer eventDeinJivId) {
		this.eventDeinJivId = eventDeinJivId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param vid the vid to set */
	public void setVid(Integer vid) {
		this.vid = vid;
	}
}