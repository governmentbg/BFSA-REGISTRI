package com.ib.babhregs.migr.furaji;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ib.system.db.JPA;

/**
 * @author belev
 */
public class LoadDataFromExcel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//
		// общо
//		String filePath = "C:\\Users\\gbelev\\Desktop\\babh_mig\\1 Регистрирани обекти REGISTAR_CH. 9_RABOTEN_31.12.2023.xlsx";
//		int sheetPored = 1;
//
//		int rowStart = 3;
//		int rowEnd = 3343;
//
//		int colStart = 1;
//		int colEnd = 25;
//
//		int register = 32;
//		int fileKey = 1; // това е магическото число, защото за един и същ регистър може да има няколко файла или sheet-ове

		//
		// временно отнети
//		String filePath = "C:\\Users\\gbelev\\Desktop\\babh_mig\\Регистър на регистрираните обекти, съгласно чл. 17, ал. 3 от ЗФ.xlsx";
//		int sheetPored = 2;
//
//		int rowStart = 3;
//		int rowEnd = 29;
//
//		int colStart = 1;
//		int colEnd = 24;
//
//		int register = 32;
//		int fileKey = 2; // това е магическото число, защото за един и същ регистър може да има няколко файла или sheet-ове

		//
		// заличени
//		String filePath = "C:\\Users\\gbelev\\Desktop\\babh_mig\\Регистър на регистрираните обекти, съгласно чл. 17, ал. 3 от ЗФ.xlsx";
//		int sheetPored = 3;
//
//		int rowStart = 2;
//		int rowEnd = 2150;
//
//		int colStart = 1;
//		int colEnd = 24;
//
//		int register = 32;
//		int fileKey = 3; // това е магическото число, защото за един и същ регистър може да има няколко файла или sheet-ове

//		load(filePath, sheetPored, rowStart, rowEnd, colStart, colEnd, register, fileKey);

		//
		// превозвачи
//		String filePath = "C:\\Users\\gbelev\\Desktop\\babh_mig\\данни\\Регистър на операторите, транспортиращи фуражи съгласно чл. 17е, ал. 2 от ЗФ.xlsx";
//		int sheetPored = 1;
//
//		int rowStart = 4;
//		int rowEnd = 1529;
//
//		int colStart = 1;
//		int colEnd = 22;
//
//		int register = 33;
//		int fileKey = 1; // това е магическото число, защото за един и същ регистър може да има няколко файла или sheet-ове

//		load(filePath, sheetPored, rowStart, rowEnd, colStart, colEnd, register, fileKey);
	}

	/**
	 * @param filePath
	 * @param sheetPored
	 * @param rowStart
	 * @param rowEnd
	 * @param colStart
	 * @param colEnd
	 * @param register
	 * @param fileKey
	 */
	static void load(String filePath, int sheetPored, int rowStart, int rowEnd, int colStart, int colEnd, int register, int fileKey) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		Workbook workbook = null;
		try {
			FileInputStream file = new FileInputStream(new File(filePath));
			workbook = new XSSFWorkbook(file);

			Sheet sheet = workbook.getSheetAt(sheetPored - 1);

			int header = 1;

			JPA.getUtil().begin();
			int insertCnt = 0;

			Map<Integer, List<String>> data = new HashMap<>();
			for (Row row : sheet) {
				int rowNum = row.getRowNum() + 1; // ескела ги дава от 0
				if (rowNum < rowStart) {
					continue;
				}

				StringBuilder insert = new StringBuilder(" insert into migr_furaji (id, register, file_key, row_num, header ");
				StringBuilder values = new StringBuilder(" values (nextval('seq_migr_furaji'), ?, ?, ?, ? ");

				ArrayList<String> list = new ArrayList<>();
				data.put(rowNum, list);

				for (Cell cell : row) {
					int colNum = cell.getColumnIndex() + 1; // ескела ги дава от 0

					if (colNum < colStart) {
						continue;
					}
					if (colEnd > 0 && colNum > colEnd) {
						break;
					}

					String colLetter = CellReference.convertNumToColString(cell.getColumnIndex()).toUpperCase();

					insert.append(", " + colLetter + " ");
					values.append(", ?" + " ");

					switch (cell.getCellType()) {
					case STRING:
						list.add(readString(cell));
						break;
					case NUMERIC:
						list.add(readNumeric(cell, sdf));
						break;
					case BOOLEAN:
						list.add(readBoolean(cell));
						break;
					case FORMULA:
						list.add(readFormula(cell));
						break;
					default:
						list.add("");
					}
				}

				Query query = JPA.getUtil().getEntityManager().createNativeQuery(insert + " ) " + values + " ) ");

//				printRow(list);

				query.setParameter(1, register);
				query.setParameter(2, fileKey);
				query.setParameter(3, rowNum);

				query.setParameter(4, header);
				header = 0;

				for (int i = 0; i < list.size(); i++) {
					query.setParameter(i + 5, list.get(i));
				}
				query.executeUpdate();

				insertCnt++;
				if (insertCnt % 300 == 0) {
					JPA.getUtil().commit();
					JPA.getUtil().begin();
					System.out.println(insertCnt);
				}

				if (rowEnd > 0 && rowNum >= rowEnd) {
					break;
				}
			}
			JPA.getUtil().commit();
			System.out.println(insertCnt);

		} catch (Exception e) {
			JPA.getUtil().rollback();
			e.printStackTrace();
		} finally {
			JPA.getUtil().closeConnection();

			try {
				workbook.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//	private static void printRow(List<String> list) {
//		for (String cell : list) {
//			System.out.print(cell + "\t");
//		}
//		System.out.println();
//	}

	private static String readBoolean(Cell cell) {
		System.out.println("readBoolean-" + cell.getBooleanCellValue());
		return cell.getBooleanCellValue() + "";
	}

	private static String readFormula(Cell cell) {
		System.out.println("readFormula-" + cell.getCellFormula());
		return cell.getCellFormula() + "";
	}

	private static String readNumeric(Cell cell, SimpleDateFormat sdf) {
		if (DateUtil.isCellDateFormatted(cell)) {
			return sdf.format(cell.getDateCellValue());
		}
		return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
	}

	private static String readString(Cell cell) {
		return cell.getRichStringCellValue().getString();
	}
}
