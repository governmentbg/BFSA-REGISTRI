package com.ib.babhregs.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.ib.babhregs.db.dto.ProcDefEtap;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;

/**
 * DAO for {@link ProcDefEtap}
 *
 * @author belev
 */
public class ProcDefEtapDAO extends AbstractDAO<ProcDefEtap> {

	/** @param user */
	public ProcDefEtapDAO(ActiveUser user) {
		super(ProcDefEtap.class, user);
	}

	/** */
	@Override
	public ProcDefEtap findById(Object id) throws DbErrorException {
		ProcDefEtap entity = super.findById(id);
		if (entity == null) {
			return entity;
		}

		// на запис ми трябва да знам дали са променяни
		entity.setDbNextOk(entity.getNextOk());
		entity.setDbNextNot(entity.getNextNot());
		entity.setDbNextOptional(entity.getNextOptional());

		return entity;
	}

	/**
	 * @param id
	 * @param byDefinition при <code>true</code> се зареждат данни, който ще се покажат на Бутон <Дефиниция>
	 * @return
	 * @throws DbErrorException
	 */
	public ProcDefEtap findById(Object id, boolean byDefinition) throws DbErrorException {
		ProcDefEtap entity = findById(id);
		if (entity == null) {
			return entity;
		}

		if (byDefinition) { // иска се да се заредят данни, който ще се покажат на Бутон <Дефиниция>
			entity.setNextOkList(findNextList(entity.getDefId(), entity.getNextOk()));
			entity.setNextNotList(findNextList(entity.getDefId(), entity.getNextNot()));
			entity.setNextOptionalList(findNextList(entity.getDefId(), entity.getNextOptional()));
		}
		return entity;
	}

	/**
	 * Проверка за номер
	 *
	 * @param defId
	 * @param nomer
	 * @return
	 * @throws DbErrorException
	 */
	public boolean isEtapNomerExist(Integer defId, Integer nomer) throws DbErrorException {
		try {
			List<?> list = createNativeQuery("select DEF_ETAP_ID from PROC_DEF_ETAP where DEF_ID = ?1 and NOMER = ?2") //
					.setParameter(1, defId).setParameter(2, nomer).getResultList();

			return !list.isEmpty();

		} catch (Exception e) {
			throw new DbErrorException("Грешка при проверка номер на етап!", e);
		}
	}

	/**
	 *
	 */
	@Override
	public ProcDefEtap save(ProcDefEtap entity) throws DbErrorException {
		ProcDefEtap saved = super.save(entity);

		saved.setDbNextOk(saved.getNextOk());
		saved.setDbNextNot(saved.getNextNot());
		saved.setDbNextOptional(saved.getNextOptional());

		return saved;
	}

	/**
	 * Връща списък от следващи етапи зарадени като [0]-id; [1]-nomer; [2]-etapName
	 *
	 * @param defId
	 * @param next
	 * @return
	 * @throws DbErrorException
	 */
	private List<Object[]> findNextList(Integer defId, String next) throws DbErrorException {
		next = SearchUtils.trimToNULL(next);
		if (next == null) {
			return new ArrayList<>();
		}
		try {
			@SuppressWarnings("unchecked")
			List<Object[]> nextList = createNativeQuery( //
					"select DEF_ETAP_ID, NOMER, ETAP_NAME from PROC_DEF_ETAP where DEF_ID = ?1 and NOMER in (" + next + ")") //
					.setParameter(1, defId).getResultList();

			return nextList;
		} catch (Exception e) {
			throw new DbErrorException("Грека при търсене на следващи етапи!", e);
		}
	}
}