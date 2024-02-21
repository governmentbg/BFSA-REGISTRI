package com.ib.babhregs.db.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.Praznici;
import com.ib.system.ActiveUser;
import com.ib.system.db.AbstractDAO;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.ObjectInUseException;

public class PrazniciDAO extends AbstractDAO<Praznici> {

	/**  */
	static final Logger LOGGER = LoggerFactory.getLogger(PrazniciDAO.class);
	
	/** @param user */
	public PrazniciDAO(ActiveUser user) {
		super(Praznici.class, user);
	}
	
	/**
	 * Load all holidays within a year saved in DB
	 */
	@SuppressWarnings("unchecked")
	public List<Date> getHolidaysInYear(int year) throws DbErrorException {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" Select den from praznici where ").append(getSQLyear("den")).append(" = ?1 order by den");
			Query query = createNativeQuery(sql.toString()).setParameter(1, year);

			List<Date> result = query.getResultList();

			return result;
		} catch (Exception e) {
			throw new DbErrorException("Грешка при взимането на всички празници в рамките на една година!", e);
		}
	}
	
	/**
	 * @param date
	 * @return extract the year from sql field for different DB vendors
	 */
	public String getSQLyear(String date) {
		String dialect = JPA.getUtil().getDbVendorName();
		if ("SQLSERVER".equals(dialect)) {
			return new StringBuilder(" YEAR (").append(date).append(" )").toString();
		}
		if ("POSTGRESQL".equals(dialect)) {
				return new StringBuilder("EXTRACT(YEAR FROM ").append(date).append(" )").toString();	
		}
		if ("ORACLE".equals(dialect)) {
			return new StringBuilder("EXTRACT(YEAR FROM ").append(date).append(" )").toString();
		}
		return "";	
	}
	
	

	/**
	 * delete all records for holiday on particular date
	 */
	@SuppressWarnings("unchecked")
	public void delete(Date d) throws DbErrorException, ObjectInUseException {
		List<Praznici> praznici=null;
		try {
			praznici = createQuery("select p from Praznici p where p.den = ?1").setParameter(1, d).getResultList();
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на празници!", e);
		}
		
		if (!praznici.isEmpty()) {
			for (Praznici p : praznici) {
				super.delete(p);
			}
		}
	}
	

}
