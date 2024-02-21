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
@Table(name = "event_deinost_furaji_vid")
public class EventDeinostFurajiVid implements MultiClassifProperty {

	private static final long serialVersionUID = -4728425811662766442L;

	@SequenceGenerator(name = "EventDeinostFurajiVid", sequenceName = "seq_event_deinost_furaji_vid", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostFurajiVid")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "event_deinost_furaji_id")
	private Integer eventDeinostFurajiId;

	@Column(name = "vid")
	private Integer vid;

	/**  */
	public EventDeinostFurajiVid() {
		super();
	}

	/** @param eventDeinostFurajiId */
	public EventDeinostFurajiVid(Integer eventDeinostFurajiId) {
		this.eventDeinostFurajiId = eventDeinostFurajiId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		EventDeinostFurajiVid x = new EventDeinostFurajiVid(parentId);
		x.setVid(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from event_deinost_furaji_vid where event_deinost_furaji_id = ?1 and vid in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
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

	/** @return the vid */
	public Integer getVid() {
		return this.vid;
	}

	/** @param eventDeinostFurajiId the eventDeinostFurajiId to set */
	public void setEventDeinostFurajiId(Integer eventDeinostFurajiId) {
		this.eventDeinostFurajiId = eventDeinostFurajiId;
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