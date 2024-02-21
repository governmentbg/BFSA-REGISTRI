package com.ib.babhregs.db.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ib.system.db.JPA;
import com.ib.system.db.JournalAttr;
import com.ib.system.utils.SearchUtils;

/**
 * Субстанции
 */
@Entity
@Table(name = "substances")
public class Substance implements Serializable {

	private static final long serialVersionUID = -9126602446934656422L;

	@Id
	@Column(name = "identifier", unique = true, nullable = false)
	@JournalAttr(label = "identifier", defaultText = "Системен идентификатор", isId = "true")
	private String identifier; // (varchar(25))

	@Column(name = "name")
	@JournalAttr(label = "name", defaultText = "Наименование")
	private String name; // (varchar(2000))

	@Column(name = "short_name")
	@JournalAttr(label = "short_name", defaultText = "Кратко име")
	private String shortName; // (varchar(250))

	@Column(name = "status")
	@JournalAttr(label = "status", defaultText = "Статус")
	private String status = "Current"; // (varchar(255))

	/**  */
	public Substance() {
		super();
	}

	/**
	 * @param identifier
	 * @param name
	 */
	public Substance(String identifier, String name) {
		this.identifier = identifier;
		this.name = name;
	}

	/** @return the identifier */
	public String getIdentifier() {
		return this.identifier;
	}

	/** @return the name */
	public String getName() {
		return this.name;
	}

	/** @return the shortName */
	public String getShortName() {
		return this.shortName;
	}

	/** @return the status */
	public String getStatus() {
		return this.status;
	}

	/**
	 * @return
	 */
	public Substance manage() {
		this.identifier = SearchUtils.trimToNULL(this.identifier);
		if (this.identifier == null) {
			return this; // няма как да се запише
		}

		Substance find = JPA.getUtil().getEntityManager().find(this.getClass(), this.identifier);
		if (find != null) {
			return find; // трябва да се използва този от БД
		}
		// нов запис
		JPA.getUtil().getEntityManager().persist(this);
		return this;
	}

	/** @param identifier the identifier to set */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/** @param name the name to set */
	public void setName(String name) {
		this.name = name;
	}

	/** @param shortName the shortName to set */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/** @param status the status to set */
	public void setStatus(String status) {
		this.status = status;
	}
}