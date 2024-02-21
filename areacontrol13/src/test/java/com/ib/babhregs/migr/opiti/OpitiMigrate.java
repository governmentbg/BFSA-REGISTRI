package com.ib.babhregs.migr.opiti;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.persistence.Query;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ib.babhregs.db.dao.OezRegDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.EventDeinJiv;
import com.ib.babhregs.db.dto.EventDeinJivLice;
import com.ib.babhregs.db.dto.EventDeinJivPredmet;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.db.dto.VpisvaneAccess;
import com.ib.babhregs.db.dto.VpisvaneDoc;
import com.ib.babhregs.db.dto.VpisvaneStatus;
import com.ib.babhregs.migr.invitro.InvitroMigrate;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

public class OpitiMigrate {
	
	private static HashMap<String, Referent> allReferents = new HashMap<String, Referent>();
	
	public static int seqVal = 8000000;
	
	public static boolean isTest = true;
	
	public static Integer REG_BABH = 139;
	
	public static Integer[] access = {-1, 1712, 1795, 1751, 1661, 1675};
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	
	
	 

	@SuppressWarnings("resource")
	public static void main(String[] args) throws DbErrorException {
		
		OezRegDAO odao = new OezRegDAO(ActiveUser.DEFAULT);
		
		TreeSet<String> aSet = new TreeSet<String>();
		
		try {
			JPA.getUtil().begin();
			InvitroMigrate.deleteDbInRange(seqVal);
			JPA.getUtil().commit();
		} catch (DbErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		List<SystemClassif> classif = new SystemData().getSysClassification(701, new Date(), 1);
		
		String fileName = "D:\\_VLP\\Регистър на разрешенията за провеждане на опити с животни.xlsx";
		
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
				
  
				
				String rowNum = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(3)));
				if (rowNum == null) {
					System.out.println("******************************** curRow " + curRow + " skipped !!!!");
					continue;
				}
				
				String nomRazr = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(3)));
				Date datRazrBeg = getDate(row.getCell(4));
				Date datRazrEnd = getDate(row.getCell(5));
				String pritejatel = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(6)));
				String dlmesto = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(7)));
				String bulstatEgn = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(8)));
				String oblast = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(9)));
				String nm = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(10)));
				String address = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(11)));
				
				String nomJO = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(12)));
				String opitiInfo = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(13)));
				String cel = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(14)));
				String rakovoditel = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(15)));
				String otgovornik = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(16)));
				String chlen = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(17)));
				Date datStan = getDateUS(row.getCell(18));
				String nomStan = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(19)));
				String dopInfo = InvitroMigrate.clearStringAndNewLine(getString(row.getCell(20)));
				
//				System.out.println(curRow + "----------------------------------------------------------------------------------------");
//				System.out.println("Номер на разрешителното: " + nomRazr);
//				System.out.println("Дата на издаване на рарешителното: " + datRazrBeg);
//				System.out.println("Дата на валидност на рарешителното: " + datRazrEnd);
//				System.out.println("Наименование на притежателя на разрешителното: " + pritejatel);
//				System.out.println("Длъжност, Месторабота на лицензианта: " + dlmesto);
//				System.out.println("БУЛСТАТ/ЕГН/ЛНЧ: " + bulstatEgn);
//				System.out.println("Област : " + oblast);
//				System.out.println("Населено място: " + nm);
//				System.out.println("Адрес (седалище) на притежателя на разрешителното:  " + address);
//				
//				System.out.println("рег. № на ж.о.  : " + nomJO);
//				System.out.println("Вид  на опитните животни: " + opitiInfo);
//				System.out.println("Цел: " + cel);
//				
//				System.out.println("ръководителя на научния проект: " + rakovoditel);
//				System.out.println("отговорника по ХО: " + otgovornik);
//				System.out.println("членовете на екипа: " + chlen);
//				System.out.println("Дата на издаване на становище: " + datStan);
//				System.out.println("№ на становището: " + nomStan);
//				System.out.println("доп. информация: " + dopInfo);
//				
//				System.out.println("----------------------------------------------------------------------------------------");
//				
				Integer nmCode = null;
				if (nm != null && ! nm.trim().isEmpty()) {
					nmCode  = decodeNm(nm);
				}
				
				Integer oezId = null;
				ObektDeinostDeinost odd = new ObektDeinostDeinost();
				if (nomJO != null && ! nomJO.trim().isEmpty()) {
					oezId = SearchUtils.asInteger(odao.findOezByRegNomStar(nomJO, null));
					if (oezId != null) {
						odd.setId(getSeq("seq_obekt_deinost_deinost"));						
						odd.setObektDeinostId(oezId);
						odd.setTablEventDeinost(BabhConstants.CODE_ZNACHENIE_JOURNAL_EVENT_DEINOST_JIV);
						//System.out.println("Намерено оез за " + nomRazr);
					}
				}
				if (oezId == null) {
					//System.out.println("****************** Не се намира ОЕЗ с номер " + nomJO + " ***********************************************************************");
				}
				
				ArrayList<EventDeinJivPredmet> jivotni = new ArrayList<EventDeinJivPredmet>();
				String[] animals = opitiInfo.split(";");
				for (String a : animals) {
					if (a.trim().isEmpty()) {
						continue;
					}
					
					String[] splitted = a.split("-");
					if (splitted.length != 3) {
						System.out.println("************************ Неправилно описване ******************************************** " + opitiInfo);
						
					}else {
						//Bug fix
						for (int i = 0; i < splitted.length; i++) {
							splitted[i] = splitted[i].replace("+++", ";");
							splitted[i] = splitted[i].replace("++", "-");
							
						}
						
						
						
						String animalText  = fixAnimals(splitted[0].trim().toUpperCase());
						Integer animalCode = null;
						
				    	for (SystemClassif item : classif) {
				    		if (item.getTekst().equalsIgnoreCase(animalText.trim())) {
				    			animalCode = item.getCode();
				    			break;
				    		}
				    	}
				    	if (animalCode == null) {
				    		System.out.println("************* Животно " + animalText + " не може да бъде прекодирано !!! *************************");
				    	}
				    	
				    	EventDeinJivPredmet tek = new EventDeinJivPredmet();
				    	tek.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_JIV_OPIT);
				    	tek.setPredmet(animalCode);
				    	
				    	if (splitted[2] != null && ! splitted[2].trim().isEmpty() )
				    	try {
							tek.setBroi(Integer.parseInt(splitted[2].trim().replace(" ", "")));							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    	tek.setDopInfo(splitted[1]);
				    	jivotni.add(tek);
				    	
				    	
						
					}
					
					
					
				}
				
				
//				if (bulstatEgn != null && ! bulstatEgn.trim().isEmpty()) {
//					System.out.println("sus");
//				}
				
				Referent refPr = createReferent(pritejatel.trim(), bulstatEgn, nmCode, address);
				if (refPr != null && dlmesto != null && refPr.getAddressKoresp() != null && dlmesto.contains("Великобритания")) {
					refPr.getAddressKoresp().setAddrCountry(40);
				}
				
				
				ArrayList<EventDeinJivLice> edjLica = new ArrayList<EventDeinJivLice>();
				
				ArrayList<Referent> allRakovoditel = new ArrayList<Referent>(); 
				if (rakovoditel != null && ! rakovoditel.trim().isEmpty()) {
					String[] people = rakovoditel.split(";");
					for (String s : people) {
						
						Referent tekRef = createReferent(s, null, null, null);						
						if (tekRef != null) {
							allRakovoditel.add(tekRef);
							EventDeinJivLice edjl = new EventDeinJivLice();
							edjl.setCodeRef(tekRef.getCode());
							edjl.setReferent(tekRef);
							edjl.setTipVraz(4);
							edjLica.add(edjl);
						}
					}
				}
				
				
				
				
				ArrayList<Referent> allChlen = new ArrayList<Referent>(); 
				if (chlen != null && ! chlen.trim().isEmpty()) {
					String[] people = chlen.split(";");
					for (String s : people) {
						Referent tekRef = createReferent(s, null, null, null);						
						if (tekRef != null) {
							allChlen.add(tekRef);
							EventDeinJivLice edjl = new EventDeinJivLice();
							edjl.setCodeRef(tekRef.getCode());
							edjl.setReferent(tekRef);
							edjl.setTipVraz(5);
							edjLica.add(edjl);
						}
					}
				}
				
				ArrayList<Referent> allOtg = new ArrayList<Referent>(); 
				if (otgovornik != null && ! otgovornik.trim().isEmpty()) {
					String[] people = otgovornik.split(";");
					for (String s : people) {
						Referent tekRef = createReferent(s, null, null, null);						
						if (tekRef != null) {
							allOtg.add(tekRef);
							EventDeinJivLice edjl = new EventDeinJivLice();
							edjl.setCodeRef(tekRef.getCode());
							edjl.setReferent(tekRef);
							edjl.setTipVraz(28);
							edjLica.add(edjl);
						};
					}
				}
				
				
				
				


				
				Doc docU = new Doc();
				Doc doc = new Doc();
				
				
				doc.setId(getSeq("SEQ_DOC"));
				doc.setDocType(1); //Входящ
				doc.setDocVid(53); 
				doc.setRnDoc("Z"+ nomRazr);
				
				doc.setDocDate(DateUtils.systemMinDate());
				
				
				doc.setRegistraturaId(REG_BABH);
				
				String anot = "Заявление за издаване на разрешително за провеждане на опити с животни на " + pritejatel ;
					
				doc.setOtnosno(anot);
				doc.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
				doc.setImeAdnUsluga("Издаване на разрешение за извършване на опити с животни");
				doc.setCodeAdmUsluga("1110");
				doc.setRegisterId(11);  
				doc.setLicenziantType(2);
				doc.setIdLicenziant(refPr.getCode());
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
				
				//doc.setDocPodVid(62);
				
				
				//251
				
				docU.setId(getSeq("SEQ_DOC"));
				docU.setDocType(2); //Изходящ
				docU.setDocVid(242); 
				
				docU.setRnDoc(nomRazr);
				docU.setDocDate(datRazrBeg);
				docU.setRegistraturaId(doc.getRegistraturaId());
				
				anot = "Разрешително за използване на животни в опити на " + pritejatel ;
					
				docU.setOtnosno(anot);
				docU.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
				docU.setImeAdnUsluga(doc.getImeAdnUsluga());
				docU.setCodeAdmUsluga(doc.getCodeAdmUsluga());
				docU.setRegisterId(16);  
				docU.setLicenziantType(2);
				docU.setIdLicenziant(refPr.getCode());
				docU.setStatus(15);
				
				if (datRazrBeg == null) {
					docU.setStatusDate(DateUtils.systemMinDate());
				}else {
					docU.setStatusDate(datRazrBeg);
				}
				
				if (datRazrEnd != null) {
					docU.setValidDate(datRazrEnd);
					docU.setDateValidAkt(datRazrEnd);
					
					if (new Date().getTime() > datRazrEnd.getTime()) {
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
				vpisvane.setIdLicenziant(refPr.getCode());
				vpisvane.setCodeRefVpisvane(refPr.getCode());
				
				vpisvane.setRegNomZaqvlVpisvane(doc.getRnDoc());
				vpisvane.setDateZaqvlVpis(doc.getDocDate());
				vpisvane.setRegNomResult(docU.getRnDoc());
				vpisvane.setDateResult(docU.getDocDate());
				vpisvane.setIdResult(docU.getId());
				vpisvane.setUserReg(-1);
				vpisvane.setDateReg(new Date());
				vpisvane.setCodePage(7);
				vpisvane.setFromМigr(1);
				vpisvane.setLicenziant(refPr.getRefName());
				
				
				
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
				
				edj.setCel(cel);
				edj.setAdres(dlmesto);
				
				
				edj.setVidList(new ArrayList<Integer>());
	
				edj.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OPITI_JIV );
				
				edj.setUserReg(-1);
				edj.setDateReg(new Date());
				
				String stan = "";
				if (nomStan != null && ! nomStan.isEmpty()) {
					stan += "№ " + nomStan;
				}
				
				if (datStan != null ) {
					stan += " от " + sdf.format(datStan);
				}
				edj.setDopInfo(stan);
				
				
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
				
				Iterator<Entry<String, Referent>> it = allReferents.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Referent> entry = it.next();
					InvitroMigrate.insertReferent(entry.getValue());
					if (entry.getValue().getAddressKoresp() != null) {
						InvitroMigrate.insertRefAddress(entry.getValue().getAddressKoresp() );
					}
				}
				
				InvitroMigrate.insertEventDeinostJiv(edj);
				
				for (EventDeinJivPredmet edjp : jivotni) {
					
					edjp.setId(getSeq("seq_event_deinost_jiv_predmet"));
					edjp.setEventDeinJivId(edj.getId());
					InvitroMigrate.insertEventDeinostJivPredmet(edjp);
				}
				
				for (EventDeinJivLice lice : edjLica) {
					lice.setEventDeinJivId(edj.getId());
					lice.setId(getSeq("seq_event_deinost_jiv_lice"));
					InvitroMigrate.insertEventDeinostJivLica(lice);
				}
								
				
				for (Integer vid : edj.getVidList()) {
					InvitroMigrate.insertEventDeinostJivVid(getSeq("seq_event_deinost_jiv_vid"), edj.getId(), vid);
				}
				
				if (odd.getObektDeinostId() != null) {
					odd.setDeinostId(edj.getId());
					InvitroMigrate.insertObektDeinostDeinost(odd);
				}
				
				
				JPA.getUtil().commit();
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    		
		    
			
		    
		}
		
//		for (String s : aSet) {
//			System.out.println("|"+s+"|");
//		}
		
		
	}
	
	
	private static String fixAnimals(String animal) {
		if (animal == null) {
			return null;
		}
		
		switch(animal) {
		
		  case "БРОЯ МОРСКИ СВИНЧЕТА":
			  return "МОРСКИ СВИНЧЕТА";
			  
		  case "БРОЯ ПРАСЕТА":
			  return "ПРАСЕТА";
			  
		  case "ЯРЕ":
			  return "ЯРЕТА";
			  
		  case "КОЧ":
			  return "КОЧОВЕ";
			  
		  case "ПЛЪХА":
			  return "ПЛЪХОВЕ";
			  
		  case "ПРЪЧ":
			  return "ПРЪЧОВЕ";
			  
		  case "НЕРЕЗ":
			  return "НЕРЕЗИ";
			  
		  default:
			  return animal;
		}
	}
	
	private static Integer decodeNm(String nm) {
		
		nm = nm.trim().toUpperCase();
		
		switch(nm) {
		  case "БЛАГОЕВГРАД":
		    return 4279;
		  case "БУРГАС":
		   return 7079;
		  case "ВАРНА":
		    return 10135;
		  case "КОСТИНБРОД":
		    return 38978;
		  case "ПЕЩЕРА":
		   return 56277;
		  case "ПЛЕВЕН":
		   return 56722;
		  case "ПЛОВДИВ":
		    return 56784;
		  case "РАЗГРАД":
		   return 61707;
		  case "СОФИЯ":
		    return 68134;
		  case "СТАРА ЗАГОРА":
		   return 68850;
		  default:
			  System.out.println("************************************ Населено място с име " + nm + "не може да бъде декодирано !!!!! ************************************");
		    return null;
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
				
				dat = dat.replace("\r", "");
				dat = dat.replace("\n", "");
				dat = dat.replace("\t", "");
				dat = dat.replace(" ", "");
				
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
	
	public static Date getDateUS(Cell cell) {
		if (cell != null && cell.getCellType() != CellType.BLANK) {
//			
			
			if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
	            return cell.getDateCellValue();
			}else {
				DataFormatter dataFormatter = new DataFormatter();
				String dat =  dataFormatter.formatCellValue(cell);
				
				dat = dat.replace("\r", "");
				dat = dat.replace("\n", "");
				dat = dat.replace("\t", "");
				dat = dat.replace(" ", "");
				
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
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
	
	
	
	
	
	
	public static Referent createReferent(String name, String egnBulstat,  Integer ekatte, String address) throws DbErrorException {
		
		if (name == null || name.trim().isEmpty()) {
			return null;
		}
		
		
		
		Referent ref = allReferents.get(name);
		if (ref == null) {
			ref = new Referent();
			ref.setId(getSeq("SEQ_ADM_REFERENTS"));
			ref.setCode(getSeq("SEQ_ADM_REFERENTS_CODE"));
			ref.setCodeParent(0);
			ref.setCodePrev(0);
			ref.setCodeClassif(0);
		}
		
		if (name.toUpperCase().contains("ДИРЕКЦИЯ") || name.toUpperCase().contains("КЛИНИКА")) {
			ref.setRefType(3);			
			if (egnBulstat != null && ! egnBulstat.trim().isEmpty()) {
				ref.setNflEik(egnBulstat);
			}
			
		}else {
			ref.setRefType(4);
			if (egnBulstat != null && ! egnBulstat.trim().isEmpty()) {
				ref.setFzlEgn(egnBulstat);
			}
		}
		
		
		ref.setRefName(name.trim());
		ref.setDateOt(DateUtils.systemMinDate());
		ref.setDateDo(DateUtils.systemMaxDate());
		
		ref.setDateReg(new Date());
		ref.setUserReg(-1);
		
		if (address != null && ! address.trim().isEmpty() ) {
			
			ReferentAddress refAddr = ref.getAddressKoresp();
			if (refAddr == null) {
				refAddr = new ReferentAddress();
				refAddr.setId(getSeq("SEQ_ADM_REF_ADDRS"));
				refAddr.setCodeRef(ref.getCode());
			}
			
			refAddr.setAddrText(address);
			refAddr.setEkatte(ekatte);
			refAddr.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
			refAddr.setDateReg(new Date());
			refAddr.setUserReg(-1);
				
			if (ekatte != null) {
				refAddr.setAddrCountry(37);
			}
				
			ref.setAddressKoresp(refAddr);
			
		}
		
		allReferents.put(name, ref);
		return ref;
	}
	
	
	
	
	
	
	
	

}
