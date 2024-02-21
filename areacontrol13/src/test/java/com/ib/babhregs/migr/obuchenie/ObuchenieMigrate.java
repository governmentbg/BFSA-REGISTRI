package com.ib.babhregs.migr.obuchenie;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Query;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.EventDeinJiv;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.db.dto.VpisvaneAccess;
import com.ib.babhregs.db.dto.VpisvaneDoc;
import com.ib.babhregs.db.dto.VpisvaneStatus;
import com.ib.babhregs.migr.invitro.InvitroMigrate;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

public class ObuchenieMigrate {
	
	private static HashMap<String, Referent> allReferents = new HashMap<String, Referent>();
	
	public static int seqVal = 7000000;
	
	public static boolean isTest = true;
	
	public static Integer REG_BABH = 139;
	
	public static Integer[] access = {-1, 1712, 1795, 1751, 1661, 1675};
	
	 

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		try {
			JPA.getUtil().begin();
			InvitroMigrate.deleteDbInRange(seqVal);
			JPA.getUtil().commit();
		} catch (DbErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		
		
		String fileName = "D:\\_VLP\\Регистър на фирмите и центровете, предоставящи обучение за защита и хуманно отношение към животните.xlsx";
		
		XSSFWorkbook workbook = null;
		
		FileInputStream fis;
		try {
			
			fis = new FileInputStream(fileName);
			workbook = new XSSFWorkbook (fis);
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		for (int curRow = 4; curRow < sheet.getLastRowNum(); curRow++ ) {
		    try {
				Row row = sheet.getRow(curRow);
				
  
				
				String nameOrg = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(1)));
				if (nameOrg == null) {
					System.out.println("******************************** curRow " + curRow + " skipped !!!!");
					continue;
				}
				
				String bulstat = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(2)));
				String sfera = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(3)));
				String kurs = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(4)));
				String nomSt = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(5)));
				Date datStBeg = getDate(row.getCell(6));
				Date datStEnd = getDate(row.getCell(7));
				
				String lektori = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(8)));
				String oblast = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(9)));
				String mesto = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(10)));
				String adres = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(11)));
				String mail = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(12)));
				
				System.out.println(curRow + "----------------------------------------------------------------------------------------");
				System.out.println("Организация: " + nameOrg);
				System.out.println("Булстат: " + bulstat);
				System.out.println("Сфера: " + sfera);
				System.out.println("Курс: " + kurs);
				System.out.println("Номер на становище: " + nomSt);
				System.out.println("Дата от: " + datStBeg);
				System.out.println("Дата до: " + datStEnd);
				System.out.println("Лектори: " + lektori);
				System.out.println("Област: " + oblast);
				System.out.println("Населено Място: " + mesto);
				System.out.println("Адрес: " + adres);
				System.out.println("email: " + mail);
				System.out.println("----------------------------------------------------------------------------------------");
				
				boolean isNewRef = false;
				Referent ref = allReferents.get(bulstat);
				if (ref == null) {
					
					isNewRef = true;
					
					ref = new Referent();
					ref.setContactEmail(mail);
					ref.setNflEik(bulstat);
					ref.setRefType(3);
					ref.setRefName(nameOrg);
					
					Integer idAdress = getSeq("SEQ_ADM_REF_ADDRS");
					
					ref.setId(getSeq("SEQ_ADM_REFERENTS"));
					ref.setCode(getSeq("SEQ_ADM_REFERENTS_CODE"));	
					ref.setCodePrev(0);
					ref.setCodeParent(0);
					ref.setCodeClassif(0);					
					ref.setDateOt(DateUtils.systemMinDate());
					ref.setDateDo(DateUtils.systemMaxDate());
					
					ref.setDateReg(new Date());
					ref.setUserReg(-1);
					
					ReferentAddress ra = new ReferentAddress();
					ra.setId(idAdress);
					ra.setCodeRef(ref.getCode());
					ra.setAddrText(adres);					
					ra.setAddrCountry(37);	
					ra.setEkatte(Integer.parseInt(mesto));
//					
					ra.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
					ra.setDateReg(new Date());
					ra.setUserReg(-1);
					ref.setAddressKoresp(ra);
					
					allReferents.put(bulstat, ref);
					
				}
				
				Doc docU = new Doc();
				Doc doc = new Doc();
				
				
				doc.setId(getSeq("SEQ_DOC"));
				doc.setDocType(1); //Входящ
				doc.setDocVid(297); 
				doc.setRnDoc("Z"+ nomSt);
				
				doc.setDocDate(DateUtils.systemMinDate());
				
				
				doc.setRegistraturaId(REG_BABH);
				
				String anot = "Заявление за издаване на становище за учебна програма на " + nameOrg ;
					
				doc.setOtnosno(anot);
				doc.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
				doc.setImeAdnUsluga("Издаване на становище за учебна програма или документ за преминато обучение");
				doc.setCodeAdmUsluga("3364");
				doc.setRegisterId(16);  
				doc.setLicenziantType(2);
				doc.setIdLicenziant(ref.getCode());
				doc.setCodeRefZaiavitel(-3);
				doc.setStatus(15);
				doc.setStatusDate(new Date());
				doc.setGuid("{"+java.util.UUID.randomUUID().toString()+"}");
				doc.setFreeAccess(2);
				doc.setDocInfo("Записът е създаден от транформация!");
				doc.setUserReg(-1);
				doc.setDateReg(new Date());
				doc.setCodeRefCorresp(-3);
				doc.setKachestvoLice(BabhConstants.CODE_ZNACHENIE_PALNOM);
				//doc.setValid(1);
				//doc.setValidDate(datBegSert);
				doc.setPayType(BabhConstants.CODE_ZNACHENIE_PAY_TYPE_NOPAY );
				
				doc.setDocPodVid(62);
				
				
				//251
				
				docU.setId(getSeq("SEQ_DOC"));
				docU.setDocType(2); //Изходящ
				docU.setDocVid(251); 
				
				docU.setRnDoc(nomSt);
				docU.setDocDate(datStBeg);
				docU.setRegistraturaId(doc.getRegistraturaId());
				
				anot = "Становище за одобрение на учебна програма и учебен план на " + nameOrg ;
					
				docU.setOtnosno(anot);
				docU.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
				docU.setImeAdnUsluga(doc.getImeAdnUsluga());
				docU.setCodeAdmUsluga(doc.getCodeAdmUsluga());
				docU.setRegisterId(16);  
				docU.setLicenziantType(2);
				docU.setIdLicenziant(ref.getCode());
				docU.setStatus(15);
				
				if (datStBeg == null) {
					docU.setStatusDate(DateUtils.systemMinDate());
				}else {
					docU.setStatusDate(datStBeg);
				}
				
				if (datStEnd != null) {
					docU.setValidDate(datStEnd);
					
					if (new Date().getTime() > datStEnd.getTime()) {
						docU.setValid(2);
					}else {
						docU.setValid(1);
					}
					
				}
				
				docU.setGuid("{"+java.util.UUID.randomUUID().toString()+"}");
				docU.setFreeAccess(2);
				docU.setDocInfo("Записът е създаден от транформация!");
				docU.setUserReg(-1);
				docU.setDateReg(new Date());
				
				Vpisvane vpisvane = new Vpisvane();
				VpisvaneStatus vs = new VpisvaneStatus();
				ArrayList<VpisvaneAccess> allVAccess = new ArrayList<VpisvaneAccess>();
				ArrayList<VpisvaneDoc> vdocList = new ArrayList<VpisvaneDoc>();
				
				int idVpisvane = getSeq("seq_vpisvane");
				
				vpisvane.setId(idVpisvane);
				vpisvane.setIdZaqavlenie(doc.getId());
				vpisvane.setStatus(BabhConstants.STATUS_VP_VPISAN);
				vpisvane.setDateStatus(docU.getDocDate());
				vpisvane.setRegistraturaId(doc.getRegistraturaId());
				vpisvane.setIdRegister(doc.getRegisterId());
				vpisvane.setLicenziantType(doc.getLicenziantType());
				vpisvane.setIdLicenziant(ref.getCode());
				vpisvane.setCodeRefVpisvane(ref.getCode());
				
				vpisvane.setRegNomZaqvlVpisvane(doc.getRnDoc());
				vpisvane.setDateZaqvlVpis(doc.getDocDate());
				vpisvane.setRegNomResult(docU.getRnDoc());
				vpisvane.setDateResult(docU.getDocDate());
				vpisvane.setIdResult(docU.getId());
				vpisvane.setUserReg(-1);
				vpisvane.setDateReg(new Date());
				vpisvane.setCodePage(7);
				vpisvane.setFromМigr(1);
				vpisvane.setLicenziant(ref.getRefName());
				
				
				
				vs.setId(getSeq("seq_vpisvane_status"));
				vs.setIdVpisvane(vpisvane.getId());
				vs.setStatus(BabhConstants.STATUS_VP_VPISAN);
				vs.setDateStatus(docU.getDocDate());
				vs.setUserReg(-1);
				vs.setDateReg(new Date());
				
				
				for (Integer codeRef : access) {
					
					
					
					VpisvaneAccess va = new VpisvaneAccess();
					
					va.setId(getSeq("SEQ_VPISVANE_ACCESS"));
					va.setCodeRef(codeRef);
					va.setVpisvaneId(vpisvane.getId());
					va.setUserReg(-1);
					va.setDateReg(new Date());
					allVAccess.add(va); 
					
				}
				
				VpisvaneDoc vdoc = new VpisvaneDoc();
				vdoc.setId(getSeq("seq_vpisvane_doc"));
				vdoc.setIdDoc(doc.getId());
				vdoc.setIdVpisvane(vpisvane.getId());				
				vdoc.setDateReg(new Date());
				vdoc.setUserReg(-1);
				vdocList.add(vdoc);
				
				VpisvaneDoc vdocU = new VpisvaneDoc();
				vdocU.setId(getSeq("seq_vpisvane_doc"));
				vdocU.setIdDoc(docU.getId());
				vdocU.setIdVpisvane(vpisvane.getId());				
				vdocU.setDateReg(new Date());
				vdocU.setUserReg(-1);
				vdocList.add(vdocU);
				
				
				EventDeinJiv edj = new EventDeinJiv();
				edj.setId(getSeq("seq_event_deinost_jiv"));
				edj.setIdVpisvane(vpisvane.getId());
				
				
				edj.setVidList(new ArrayList<Integer>());
				edj.setObuchenieList(new ArrayList<Integer>());
				
				edj.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_UCHEBNA_PROGR );
				edj.getObuchenieList().add(2);
				edj.setOpisanieCyr(kurs);
				edj.setCel("Лектори: " + lektori);
				
				edj.setUserReg(-1);
				edj.setDateReg(new Date());
				
				
				JPA.getUtil().begin();
				
				
				
				InvitroMigrate.insertDoc(doc);
				InvitroMigrate.insertDoc(docU);
				
				InvitroMigrate.insertVpisvane(vpisvane);
				InvitroMigrate.insertVpisvaneStatus(vs);
				
				for (VpisvaneDoc vd : vdocList) {
					InvitroMigrate.insertVpisvaneDoc(vd);
				}
				
				for (VpisvaneAccess va : allVAccess ) {
					InvitroMigrate.insertVpisvaneAccess(va);
				}
				
				if (isNewRef) {
					InvitroMigrate.insertReferent(ref);
					if (ref.getAddressKoresp() != null) {
						InvitroMigrate.insertRefAddress(ref.getAddressKoresp());
					}
				}
				
				InvitroMigrate.insertEventDeinostJiv(edj);
				
				for (Integer ob : edj.getObuchenieList()) {
					InvitroMigrate.insertEventDeinostJivObuchenie(getSeq("seq_event_deinost_jiv_obuchenie"), edj.getId(), ob);
				}
				
				for (Integer vid : edj.getVidList()) {
					InvitroMigrate.insertEventDeinostJivVid(getSeq("seq_event_deinost_jiv_vid"), edj.getId(), vid);
				}
				
				
				JPA.getUtil().commit();
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    		
		    
		}
		
		
	}
	
	
	public static String getString(Cell cell) {
		if (cell != null && cell.getCellType() != CellType.BLANK) {
//			if (cell.getCellType() == CellType.NUMERIC) {
//				double d = cell.getNumericCellValue();
//				
//				return "" + (int)d;
//			}
//			
//			if (cell.getCellType() == CellType.BOOLEAN) {
//				return "" + cell.getBooleanCellValue();
//			}
//			
////			if (cell.getCellType() == CellType.) {
////				return "" + cell.getBooleanCellValue();
////			}
//			
//			return cell.getStringCellValue();
			
			
			
			
			
			DataFormatter dataFormatter = new DataFormatter();
			return dataFormatter.formatCellValue(cell);
			
		}else {
			return null;
		}
	}
	
	public static Date getDate(Cell cell) {
		if (cell != null && cell.getCellType() != CellType.BLANK) {
//			
			
			if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
	            return cell.getDateCellValue();
			}else {
				DataFormatter dataFormatter = new DataFormatter();
				String dat =  dataFormatter.formatCellValue(cell);
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
				try {
					return sdf.parse(dat);
				} catch (ParseException e) {					
					e.printStackTrace();
					return null;
				}
			}
			
			
			
			
			
		}else {
			return null;
		}
	}
	
	private static int getSeq(String seqName) throws DbErrorException {
		
		if (isTest) {
			seqVal ++;
			return seqVal;
		}else {
			String sql = "SELECT nextval(:seqName)";
			try {
				Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
				query.setParameter("seqName", seqName);
				
				return SearchUtils.asInteger(query.getSingleResult());
			}catch (Exception e) {				
				e.printStackTrace();
				throw new DbErrorException("Грешка при взимане на sequence " + seqName, e);
			}
		}
			
		
	}
	

}
