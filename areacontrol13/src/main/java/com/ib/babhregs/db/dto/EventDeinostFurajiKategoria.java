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
 * Категория
 */
@Entity
@Table(name = "event_deinost_furaji_kategoria")
public class EventDeinostFurajiKategoria implements MultiClassifProperty {

	private static final long serialVersionUID = -4728425811662766442L;

	@SequenceGenerator(name = "EventDeinostFurajiKategoria", sequenceName = "seq_event_deinost_furaji_kategoria", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = SEQUENCE, generator = "EventDeinostFurajiKategoria")
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "event_deinost_furaji_id")
	private Integer eventDeinostFurajiId;

	@Column(name = "kategoria")
	private Integer kategoria;

	/**  */
	public EventDeinostFurajiKategoria() {
		super();
	}

	/** @param eventDeinostFurajiId */
	public EventDeinostFurajiKategoria(Integer eventDeinostFurajiId) {
		this.eventDeinostFurajiId = eventDeinostFurajiId;
	}

	@Override
	public MultiClassifProperty createNew(Integer parentId, Integer code) {
		EventDeinostFurajiKategoria x = new EventDeinostFurajiKategoria(parentId);
		x.setKategoria(code);
		return x;
	}

	@Override
	public Query createQueryDelete(Integer parentId, List<Integer> codeList) {
		String sql = "delete from event_deinost_furaji_kategoria where event_deinost_furaji_id = ?1 and kategoria in (?2)";
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

	/** @return the kategoria */
	public Integer getKategoria() {
		return this.kategoria;
	}

	@Override
	public Integer getParentId() {
		return getEventDeinostFurajiId();
	}

	/** @param eventDeinostFurajiId the eventDeinostFurajiId to set */
	public void setEventDeinostFurajiId(Integer eventDeinostFurajiId) {
		this.eventDeinostFurajiId = eventDeinostFurajiId;
	}

	/** @param id the id to set */
	public void setId(Integer id) {
		this.id = id;
	}

	/** @param kategoria the kategoria to set */
	public void setKategoria(Integer kategoria) {
		this.kategoria = kategoria;
	}
}