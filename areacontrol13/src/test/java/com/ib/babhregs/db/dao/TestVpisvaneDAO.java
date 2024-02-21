package com.ib.babhregs.db.dao;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.ProcExe;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.Constants;
import com.ib.system.SysConstants;
import com.ib.system.db.JPA;
import com.ib.system.db.SelectMetadata;
import com.ib.system.exceptions.DbErrorException;

/**
 */
public class TestVpisvaneDAO {
	private static SystemData	sd;
	private static VpisvaneDAO	dao;
	private static ProcExeDAO	procExeDao;

	/**  */
	@BeforeClass
	public static void setUp() {
		dao = new VpisvaneDAO(new UserData(-1, "zxc", "zxc"));
		procExeDao = new ProcExeDAO(new UserData(-1, "zxc", "zxc"));

		sd = new SystemData();
		try { // да кешира и долу да не бави
			sd.decodeItem(SysConstants.CODE_CLASSIF_EKATTE, 123, 1, null);
			sd.decodeItem(Constants.CODE_CLASSIF_COUNTRIES, 123, 1, null);
		} catch (DbErrorException e) {
			e.printStackTrace();
		}
	}

	/** */
	@Test
	public void testCreateSelectReportLice() {
		try {

			SelectMetadata search = dao.createSelectReportLice(2207, 564);

			LazyDataModelSQL2Array lazy = new LazyDataModelSQL2Array(search, "a0");
			List<Object[]> result = lazy.load(0, lazy.getRowCount(), null, null);

			for (Object[] row : result) {
				System.out.print(row[0]); // • ИД Вписване
				System.out.print("\t");

				System.out.print(row[2]); // • Наименование на регистър - през класифиакция CODE_CLASSIF_VID_REGISTRI
				System.out.print("\t");

				System.out.print(row[5]); // • Рег. номер на удостоверителен документ
				System.out.print("\t");

				System.out.print(row[6]); // • Дата на издаване на документа
				System.out.print("\t");

				// • Видове дейности по вписването - специфичен медод decodeItems !!!
				System.out.print(sd.decodeItems(BabhConstants.CODE_CLASSIF_VID_DEINOST, (String) row[7], 1, null));
				System.out.print("\t");

				// • Предмет на дейността - специфичен медод decodePredmet !!!
				System.out.print(sd.decodePredmet((String) row[8], 1, null));
				System.out.print("\t");

				// • Обекти на дейностите - специфичен медод decodeObekt !!!
				System.out.print(sd.decodeObekt((String) row[9], 1, null));

				System.out.println();
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	// @Test
	public void testFindProcExeIdent() {
		try {
			Object[] data = dao.findProcExeIdent(45);
			if (data != null) {
				System.out.println(data[0]);
				System.out.println(data[1]);
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			JPA.getUtil().closeConnection();
		}
	}

//	@Test
	public void testFindVpisvaneDocList() {
		try {
			Doc doc = new Doc();
			doc.setId(80356);

			JPA.getUtil().begin();
//			new DocDAO(ActiveUser.DEFAULT).deleteDelovoden(doc, 66, sd);
			JPA.getUtil().commit();

		} catch (Exception e) {
			JPA.getUtil().rollback();

			e.printStackTrace();
		} finally {
			JPA.getUtil().closeConnection();
		}
	}

//	@Test
	public void testStartProc() {
		try {
			Integer docId = 80206;
			Integer procDefId = 21;

			JPA.getUtil().begin();
			ProcExe procExe = procExeDao.startProc(procDefId, docId, sd);
			JPA.getUtil().commit();

			System.out.println(procExe.getId());

		} catch (Exception e) {
			JPA.getUtil().rollback();

			e.printStackTrace();
		} finally {
			JPA.getUtil().closeConnection();
		}
	}
}
