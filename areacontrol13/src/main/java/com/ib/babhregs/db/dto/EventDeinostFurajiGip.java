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
 * ГИП, през който се изнася
 */
@Entity
@Table(name = "event_deinost_furaji_gip")
public class EventDeinostFurajiGip implements MultiClassifProperty {

	private static final long serialVersionUID = 3712709754795765558L;

	@SequenceGenerator(name = "EventDeinostFurajiGip", sequenceName = "seq_event_deinost_furaji_gip", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostFurajiGip")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "event_deinost_furaji_id")
	private Integer eventDeinostFurajiId;

	@Column(name = "gip")
	private Integer gip;

	/**  */
	public EventDeinostFurajiGip() {
		super();
	}

	/** @param eventDeinostFurajiId */
	public EventDeinostFurajiGip(Integer eventDeinostFurajiId) {
		this.eventDeinostFurajiId = eventDeinostFurajiId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		EventDeinostFurajiGip x = new EventDeinostFurajiGip(parentId);
		x.setGip(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from event_deinost_furaji_gip where event_deinost_furaji_id = ?1 and gip in (?2)";
		return JPA.getUtil().getEntityManager().createNativeQuery(sql).setParameter(1, parentId).setParameter(2, codeList);
	}

	/** @return the eventDeinostFurajiId */
	public Integer getEventDeinostFurajiId() {
		return this.eventDeinostFurajiId;
	}

	/** @return the gip */
	public Integer getGip() {
		return this.gip;
	}

	/** @return the id */
	public Integer getId() {
		return this.id;
	}

	@Override
	public Integer getParentId() {
		return getEventDeinostFurajiId();
	}

	/** @param eventDeinostFurajiId the eventDeinostFurajiId to set */
	public void setEventDeinostFurajiId(Integer eventDeinostFurajiId) {
		this.eventDeinostFurajiId = eventDeinostFurajiId;
	}

	/** @param gip the gip to set */
	public void setGip(Integer gip) {
		this.gip = gip;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}
}