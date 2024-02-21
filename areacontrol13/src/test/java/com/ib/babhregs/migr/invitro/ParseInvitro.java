package com.ib.babhregs.migr.invitro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import com.ib.babhregs.system.SystemData;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

public class ParseInvitro {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		try {
			
			SystemData sd = new SystemData();
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			
			
			List<SystemClassif> bolestiClassif = sd.getSysClassification(672, new Date(), 1);
			int cntNotFoundBolesti = 0;
			TreeSet<String> notFoundB = new TreeSet<String>(); 
			
			
			//System.out.println("Number of rows : " + rows.getCount());
			JPA.getUtil().begin();
			
			List<Object[]> rows = JPA.getUtil().getEntityManager().createNativeQuery("select id, naim_s, cert_s, bolesti_s, firmi_s, notes_s from temp_invitro order by id").getResultList();
			
			for (Object[] row : rows) {
				
				Integer id = SearchUtils.asInteger(row[0]);
				
				if (id == 194 ) {
					System.out.println("BUM!");
				}
				
				//Сертификат
				String certNom = null;
				Date certDat = DateUtils.systemMinDate();
				
				String certS = SearchUtils.asString(row[2]);

				certS = certS.replace("№", "");
				String[] parts = certS.split("/");
				
				certNom = parts[0];
				certNom = certNom.trim();
				
				if (parts.length != 2) {
					System.out.println("!!!!!!! За id= " + row[0] + ", сертификата " + certS + " не може да се раздели на части !!!! ");					
				}else {
					
					
					//Проверка дали номерът е число
					try {
						Integer t = Integer.parseInt(certNom);
					} catch (Exception e) {
						System.out.println("!!!!!!! За id= " + row[0] + ", номерът на сертификата " + certNom + " не е число !!!! ");
					}
					
					String datS = parts[1].trim();
					datS = datS.replace(" ", "");
					datS = datS.replace("..", ".");
					
					try {
						certDat  = sdf.parse(datS);
					} catch (Exception e) {
						System.out.println("!!!!!!! За id= " + row[0] + ", датата на сертификата не може да се форматира:  " + datS + " !!!! ");
						certDat = DateUtils.systemMinDate();
					}
					
				}
				
				JPA.getUtil().getEntityManager().createNativeQuery("update temp_invitro set cert_nom = ?, cert_dat = ? where temp_invitro.id = ?")
				.setParameter(1, certNom)
				.setParameter(2, certDat)
				.setParameter(3, id).executeUpdate();
				
					
				//Наименование
				String name1 = null;
				String name2 = null;
				
				String naimS = SearchUtils.asString(row[1]);
				
				ArrayList<String> naimList = doParts(naimS);
				if (naimList.size() > 2) {
					System.out.println("!!!!!!! За id= " + row[0] + ", наименованието не може да се раздели на 2:  ");
					System.out.println(naimS);
					System.out.println();
				}else {
					name1 = naimList.get(0).trim();
					if (name1.endsWith("/")) {
						name1 = name1.substring(0, name1.length()-1);
					}
					
					if (naimList.size() == 2) {
						
						name2 = naimList.get(1).trim();
					}
				}
				
				JPA.getUtil().getEntityManager().createNativeQuery("update temp_invitro set naim1 = ?, naim2 = ? where temp_invitro.id = ?")
				.setParameter(1, name1)
				.setParameter(2, name2)
				.setParameter(3, id).executeUpdate();
				
				
//					System.out.println(name1);
//					System.out.println(name2);
//					System.out.println("******************************************");
				
				
				
				//Болести
				name1 = null;
				name2 = null;
				//System.out.println(row[0]);
				
//					if (SearchUtils.asInteger(row[0]) == 263) {
//						System.out.println();
//					}
				
				String bolestiS = SearchUtils.asString(row[3]);
				String bolestiCoded = "";
				
				if (!SearchUtils.isEmpty(bolestiS)) {
					ArrayList<String> bolestiList = doParts(bolestiS);
					if (bolestiList.size() > 2) {
						System.out.println("!!!!!!! За id= " + row[0] + ", болестите не може да се разделят на 2:  ");
						System.out.println(bolestiS);
						System.out.println();
					}else {
						name1 = bolestiList.get(0).trim();
						if (name1.endsWith("/")) {
							name1 = name1.substring(0, name1.length()-1);
						}
						
						if (bolestiList.size() == 2) {
							
							name2 = bolestiList.get(1).trim();
						}
					}
					

					if (name1 == null) {
						System.out.println("!!!!!!! За id= " + row[0] + ", болестите не може да се разбият  ");
					}else {

						String codes = doCodeBolest(bolestiClassif, name1);
						if (codes == null) {
							notFoundB.add(name1.trim().toUpperCase());
							cntNotFoundBolesti++;
						}else {
							bolestiCoded += codes + "|";
						}
						
					}
					
					
				}
				
				JPA.getUtil().getEntityManager().createNativeQuery("update temp_invitro set bolesti_codes = ? where temp_invitro.id = ?")
				.setParameter(1, bolestiCoded)					
				.setParameter(2, id).executeUpdate();
				

				
				
			}
				
			JPA.getUtil().commit();
			
			for (String s : notFoundB) {
				System.out.println(s);
			}
			
			
			System.out.println("cnt: " + cntNotFoundBolesti);
			
			
			
//			for (String s : firmi) {
//				System.out.println(s);
//			}
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JPA.getUtil().rollback();
		}finally {
			JPA.getUtil().closeConnection();
		}

	}
	
	
	private static ArrayList<String> doParts(String naimS) {
		
		ArrayList<String> rows = new ArrayList<String>();
		
		
		
		naimS = naimS.replace("\n", " ");
		if (naimS.endsWith("\r")) {
			naimS = naimS.substring(0, naimS.length()-1);
		}
		if (naimS.startsWith("\r")) {
			naimS = naimS.substring(1);
		}
		
		
		String[] parts = naimS.split("\r");
		
		
		if (parts.length == 1) {
			parts = naimS.split("/");
		}
		
		
		for (String s : parts) {
			s = s.trim();
			if (SearchUtils.isEmpty(s)) {
				continue;
			}
			rows.add(s);
		}
		
		
		return rows;
	}


	private static String clearStringRN(String value) {
		value = value.replace("", "");
		value = value.replace("\r\n", "|");
		value = value.replace("\n", "|");
		value = value.replace("\r", "|");
		value = value.replace("   ", " ");
		value = value.replace("  ", " ");
		
		return value;
	}
	
	private static String clearString(String value) {
		value = value.replace("", "");
		value = value.replace("  ", " ");
		
		return value;
	}
	
	private static String doCodeBolest(List<SystemClassif> bolesti, String bolest) {
		
		String bolestCode = null;
		
		bolest = bolest.toUpperCase().trim();
		if (bolest.equals("ЗАПАДНО НИЛСКА ТРЕСКА")) {
			return "302";
		}
		
		if (bolest.equals("ТРЕСКА ОТ ЗАПАДЕН НИЛ")) {
			return "302";
		}
		
		if (bolest.equals("ЗАПАДНОНИЛСКА ТРЕСКА")) {
			return "302";
		}
		
		if (bolest.equals("АФРИКАНСКА ЧУМА СВИНЕ")) {
			return "439";
		}
		
		if (bolest.equals("БРУЦЕЛОЗА ПО КУЧЕТАТА")) {
			return "881";
		}
		
		if (bolest.equals("ВИРУСНА ДИАРИЯ ПО ГОВЕДАТА")) {
			return "407";
		}
		
		if (bolest.equals("ВИРУСНА ДИАРИЯ ПО ГОВЕДАТА (BVDV)")) {
			return "407";
		}
		
		if (bolest.equals("МУКОЗНА БОЛЕСТ ВИРУСНА ДИАРИЯ ПО ГОВЕДАТА")) {
			return "407";
		}
		
		if (bolest.equals("МУКОЗНА БОЛЕСТ- ВИРУСНА ДИАРИЯ ПО ГОВЕДАТА")) {
			return "407";
		}
		
		if (bolest.equals("МУКОЗНА БОЛЕСТ-ВИРУСНА ДИАРИЯ ПО ГОВЕДАТА")) {
			return "407";
		}
		
		
		if (bolest.equals("ИНФЕКЦИОЗЕН ГОВЕЖДИ РИНОТРАХЕИТ")) {
			return "680";
		}
		
		if (bolest.equals("ИНФЕКЦИОЗЕН РИНОТРАХЕИТ ПО ГОВЕДА")) {
			return "680";
		}
		
		if (bolest.equals("ИНФЕКЦИОЗЕН РИНОТРАХИЕТ ПО ГОВЕДАТА")) {
			return "680";
		}
		
		if (bolest.equals("СИН ЕЗИК ПО ПРЕЖИВНИ ЖИВОТНИ")) {
			return "436";
		}
		
		if (bolest.equals("СИН ЕЗИК")) {
			return "436";
		}
		
		if (bolest.equals("ЛЕВКОЗА ПО ГОВЕДАТА")) {
			return "332";
		}
		
		if (bolest.equals("ЕНЗООТИЧНА ЛЕВКОЗА ПО ГОВЕДАТА")) {
			return "332";
		}
		
		
		if (bolest.equals("КУ ТРЕСКА ПРИ ЖИВОТНИТЕ")) {
			return "322";
		}
		
		if (bolest.equals("КУ ТРЕСКА")) {
			return "322";
		}
		
		if (bolest.equals("КУ ТРЕСКА ПО ПРЕЖИВНИТЕ ЖИВОТНИ")) {
			return "322";
		}
		
		if (bolest.equals("КЛАСИЧЕСКА ЧУМА ПО СВИНЕ")) {
			return "441";
		}
		
		
		
		
		
		
		
		
		if (bolest.equals("НЮКЯСЪЛСКА БОЛЕСТ ПО ПТИЦИТЕ")) {
			return "448";
		}
		
		if (bolest.equals("ПСЕВДОЧУМА /НЮКЯСЪЛСКА БОЛЕСТ")) {
			return "448";
		}
		 
		
		if (bolest.equals("ТУЛАРЕМИЯ")) {
			return "317";
		}
		
		if (bolest.equals("ТУЛАРЕМИЯ ПО ЖИВОТНИТЕ")) {
			return "317";
		}

		
		if (bolest.equals("БРУЦЕЛОИДОЗА (ЗАРАЗЕН ЕПИДИДИМИТ) ПО КОЧОВЕТЕ")) {
			return "340";
		}
		
		if (bolest.equals("САП ПО ЕДНОКОПИТНИТЕ")) {
			return "443";
		}
		 
		if (bolest.equals("СПОНГИФОРМНА ЕНЦЕФАЛОПАТИЯ ПО ГОВЕДАТА")) {
			return "428";
		}
		
		if (bolest.equals("")) {
			return "";
		}
		 
		 
		 
		
		
		
		
		
		for (SystemClassif item  : bolesti) {
			if (item.getTekst().trim().equalsIgnoreCase(bolest.trim())) {
				bolestCode = ""+item.getCode();
				break;
			}
		}
		
		return bolestCode;
	}

}
