package com.ib.babhregs.db.dao;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.system.SystemData;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;

/**
 */
public class TestMpsDAO {
	private static SystemData sd;

	private static MpsDAO dao;

	/**  */
	@BeforeClass
	public static void setUp() {
		dao = new MpsDAO(ActiveUser.DEFAULT);

		sd = new SystemData();
	}

	/** */
	@Test
	public void testFindById() {
		try {
			Mps mps = dao.findById(1);

			System.out.println(mps);

		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
}
