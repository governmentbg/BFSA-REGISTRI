package com.ib.babhregs.search;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ib.babhregs.system.SystemData;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;

/**
 */
public class TestSpecReportSearch {

	private static SystemData sd;

	/***/
	@BeforeClass
	public static void setUp() {
		sd = new SystemData();
	}

	/**
	 * Test method for {@link com.ib.babhregs.search.SpecReportSearch#buildQueryReport()}.
	 */
	@Test
	public void testBuildQueryReport() {
		try {
			SpecReportSearch search = new SpecReportSearch();
			search.setOblast(2);
			
//			search.setRefNameBgEn("ЙОНЧЕВ ");
//			search.setRefAddress("първа");
//			search.setPredstLice(true);
			search.setLicenziant(true);
			search.setPredstLice(true);
			search.setVlpNaimenovanie("т");
			
//			search.setFurObektEkatte(100022);
//			search.setRefEkatte(68134);
			
//			search.setVlpPrednObektList(Arrays.asList(2, 3, 4, 5, 6));
//			search.setFurObektAddress("б");
			
//			search.setVlpPomVeshtva("methyl");
//			search.setVlpAktVeshtva("Dexmedetomidine");
			
//			search.setVlpOpakovkaList(Arrays.asList(2, 3, 4, 5, 6));
//			search.setVlpPredmetPrvnosList(Arrays.asList(18, 29, 61));
//			search.setVlpPredmetFarmakolList(Arrays.asList(1, 2, 3, 4, 5));

//			search.setVlpVidDeinList(Arrays.asList(42, 58, 60, 50));
//			search.setVlpNaimenovanie("Kuku");

//			search.setFurObektRegNom("αBG26100005");
//			search.setFurNomerMps("CA1221Б");

//			search.setKachestvoPredstLiceList(Arrays.asList(4, 7));

//			search.setOezVidList(Arrays.asList(1, 2, 3, 4));
//			search.setVlzVidList(Arrays.asList(1, 2, 3, 4));

//			search.setJivNomerMps("ah2524ah");

//			search.setJivObektRegNomerStar("BG1973-297");
//			search.setJivObektAddress("Бойка");
//			search.setJivVidJivotniList(Arrays.asList(145, 146));
//			search.setJivVidDeinList(Arrays.asList(2));
//			search.setRefEkatte(100040);

//			search.setRegistraturaIdList(Arrays.asList(139));
//			search.setVpStatusList(Arrays.asList(1));
//			search.setVpStatusDateFrom(DateUtils.parse("01.01.2023"));
//			search.setVpStatusDateTo(DateUtils.parse("01.01.2024"));
//
//			search.setZaiavDocVidList(Arrays.asList(14, 53, 219));
//			search.setZaiavDateFrom(DateUtils.parse("01.01.2023"));
//			search.setZaiavDateTo(DateUtils.parse("01.01.2024"));
//			search.setZaiavStatusList(Arrays.asList(15));
//			search.setZaiavStatusDateFrom(DateUtils.parse("01.11.2023"));
//			search.setZaiavStatusDateTo(DateUtils.parse("01.01.2024"));
//			search.setZaiavRnDoc("31");
//			search.setZaiavRnDocEQ(false);
//			search.setLicenziant("ФИЛИП* 82*");
//
//			search.setUdDateFrom(DateUtils.parse("01.01.2023"));
//			search.setUdDateTo(DateUtils.parse("01.01.2024"));
//			search.setUdRnDoc("Соб-32");
//			search.setUdRnDocEQ(false);
//			search.setUdDocVidList(Arrays.asList(123));
//			search.setUdValidList(Arrays.asList(1));
//			search.setUdValidDateFrom(DateUtils.parse("01.01.2023"));
//			search.setUdValidDateTo(DateUtils.parse("01.01.2024"));

			search.buildQueryReport(sd);

			LazyDataModelSQL2Array lazy = new LazyDataModelSQL2Array(search, "a0");
			List<Object[]> result = lazy.load(0, lazy.getRowCount(), null, null);

			for (Object[] row : result) {
				System.out.println(Arrays.toString(row));
			}
			System.out.println(result.size());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
