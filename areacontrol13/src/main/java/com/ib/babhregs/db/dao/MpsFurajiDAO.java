package com.ib.babhregs.db.dao;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.MpsFuraji;
import com.ib.babhregs.search.MPSsearch;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.exceptions.DbErrorException;

/**
 * DAO for {@link MpsFuraji}
 */
public class MpsFurajiDAO extends AbstractDAO<MpsFuraji> {

	/**  */
	static final Logger LOGGER = LoggerFactory.getLogger(MpsFurajiDAO.class);

	/** @param user */
	public MpsFurajiDAO(ActiveUser user) {
		super(MpsFuraji.class, user);
	}

	/**
	 * + разни lazy атрибути<br>
	 */
	@Override
	public MpsFuraji findById(Object id) throws DbErrorException {
		if (id == null) {
			return null;
		}
		MpsFuraji entity = super.findById(id);
		if (entity == null) {
			return entity;
		}
		entity.getMpsVidProducts().size(); // lazy

		return entity;
	}

	/**
	 * Запис на мпс и всевъзможните му логики
	 *
	 * @param entity
	 * @return
	 * @throws DbErrorException
	 */
	@Override
	public MpsFuraji save(MpsFuraji entity) throws DbErrorException {
		if (entity.getMpsVidProducts() == null) {
			entity.setMpsVidProducts(new ArrayList<>()); // за да може да се направи повторен запис след първият
		}

		entity.setNomer(MPSsearch.convertMpsNomer(entity.getNomer()));

		MpsFuraji saved = super.save(entity);

		return saved;
	}
}
