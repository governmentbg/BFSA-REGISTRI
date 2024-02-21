package com.ib.babhregs.search;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.Query;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.Constants;
import com.ib.system.db.JPA;

/**
 * Тест клас за {@link TaskSearch}
 *
 * @author belev
 */
public class TestVpisvaneSearch {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestVpisvaneSearch.class);

	private static SystemData sd;

	/** @throws Exception */
	@BeforeClass
	public static void setUp() throws Exception {
		JPA.getUtil();
		sd = new SystemData();
		
	}

	/** */
	@Test
	public void buildQuery() {
		try {
			Date dat = new Date();

			VpisvaneSearch search = new VpisvaneSearch();
			
			
			search.setCodeRefVpisvane(1);
			search.setIdLicenziant(2);
			search.setLicenziantType(3);
			search.setRegisterId(2);
			search.setRegNomResult("5");
			search.setRegNomZaiav("6");
			search.setResultDateFrom(dat);
			search.setResultDateTo(dat);
			search.setStatDateFrom(dat);
			search.setStatDateTo(dat);
			search.setStatus(7);
			search.setZaiavDateFrom(dat);
			search.setZaiavDateTo(dat);
			search.setLicenziant("Мамун Е ВЕЛИК");
			
			
			
			search.buildQueryVpisvaneList(new UserData(-1, "-1", "-1"), sd);
			
			System.out.println(search.getSql());
			Query q = JPA.getUtil().getEntityManager().createNativeQuery(search.getSql());
			Iterator<Entry<String, Object>> it = search.getSqlParameters().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				q.setParameter(entry.getKey(), entry.getValue());
			}
			
			q.getResultList();
			
			
			System.out.println(search.getSqlCount());
			q = JPA.getUtil().getEntityManager().createNativeQuery(search.getSqlCount());
			it = search.getSqlParameters().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				q.setParameter(entry.getKey(), entry.getValue());
			}
			
			q.getResultList();
			
		} catch (Exception e) {
			fail(e.getMessage());
			LOGGER.error(e.getMessage(), e);
		}
	}

}