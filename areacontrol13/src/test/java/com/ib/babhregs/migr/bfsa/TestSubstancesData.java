package com.ib.babhregs.migr.bfsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;

public class TestSubstancesData {
	
	
	private static HashMap<String, String> parsed = new HashMap<String, String> ();
	
	private static int cntFound = 0;
	private static TreeMap<String, Long> nosubs = new TreeMap<String, Long>();

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
	
		
		TreeSet<String> nerazpoznati = new TreeSet<String>();
		
		
		
		Integer idVmp = null;
		String sql = "select id, subs, naim from temp_vmp where subs_codes is null";
		if (idVmp != null) {
			sql += " and id = "+ idVmp;
		}
		
		
		try {
			ArrayList<Object[]> data = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery(sql).getResultList();
			ArrayList<Object[]> substances = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery("select identifier, name from substances order by status").getResultList();
			
			
			
			for (Object[] o : data) {
				
				Integer id = SearchUtils.asInteger(o[0]);
				String tekSubstances = SearchUtils.asString(o[1]).toUpperCase().trim();
				String naim = SearchUtils.asString(o[2]).toUpperCase().trim();
				
				//System.out.println(naim.split("/").length + "\t" + naim);
				
				
				String subsCodes = parsed.get(tekSubstances);
				if (subsCodes != null) {
					updateVMP(id, subsCodes, tekSubstances);					
					continue;
				}
				
				
				subsCodes = "";
				
				tekSubstances = fixErrors(tekSubstances);
				
				
				
				boolean found = false;
				
				//Първо по пълно съвпадание
				for (Object[] s : substances) {
					String nameSub = SearchUtils.asString(s[1]).trim().toUpperCase();
					String codeSub = SearchUtils.asString(s[0]).trim();
					
					//if (nameSub.trim().equalsIgnoreCase(tekSubstances.trim())) {
					if (compare(nameSub,tekSubstances)) {
						found = true;						
						subsCodes = codeSub;						
						break;
					}
				}
				
				if (found) {
					updateVMP(id, subsCodes, tekSubstances);
					continue;					
				}
				
				
				//Пробваме да го разделим с ;
				subsCodes = "";
				String test = tekSubstances.replace("  ", " ");
				String[] splitted = test.split(";");
				if (splitted.length > 1) {
					boolean foundParts = true;
					int i = 0;
					while (i < splitted.length && foundParts) {
						foundParts = false;
						String part = splitted[i].trim();
						if (part.length()< 3) {
							i++;
							continue;
						}
						
						for (Object[] s : substances) {
							String sub = SearchUtils.asString(s[1]).trim().toUpperCase();
							String codeSub = SearchUtils.asString(s[0]).trim();
							
							//if (sub.trim().equalsIgnoreCase(part.trim())) {
							if (compare(sub,part)) {
								foundParts = true;
								subsCodes += codeSub + "|";								
								break;
							}
						}
						
						
						i++;
						if (foundParts) {
							//System.out.println(part + " --> OK" );
						}else {
							//System.out.println(part + " --> NOT OK" );	
							putSubs(part);
						}
					}
					
					if (foundParts) {						
						found = true;						
					}
				}	
			
				
				if (found) {
					updateVMP(id, subsCodes, tekSubstances);
					continue;					
				}
				
				
				//Пробваме да го разделим с ,
				subsCodes = "";
				test = tekSubstances.replace(";", ",");
				test = test.replace("  ", " ");
				splitted = test.split(",");
				if (splitted.length > 1) {
					boolean foundParts = true;
					int i = 0;
					while (i < splitted.length && foundParts) {
						foundParts = false;
						String part = splitted[i].trim();
						if (part.length()< 3) {
							i++;
							continue;
						}
						
						for (Object[] s : substances) {
							String nameSub = SearchUtils.asString(s[1]).trim().toUpperCase();
							String codeSub = SearchUtils.asString(s[0]).trim();
							
							if (nameSub.trim().equalsIgnoreCase(part.trim())) {
								foundParts = true;
								subsCodes += codeSub + "|";
								break;
							}
						}
						
						i++;
						if (foundParts) {
							//System.out.println(part + " --> OK" );
						}else {
							//System.out.println(part + " --> NOT OK" );
							
						}
					}
					
					if (foundParts) {						
						found = true;						
					}
				}				
				
				if (found) {
					updateVMP(id, subsCodes, tekSubstances);
					continue;					
				}
				
				//System.out.println(tekSubstances);
				nerazpoznati.add(tekSubstances);
			}
			
			
			System.out.println("Разпознати " + cntFound + " от " + data.size());
			
			printSubs();
			
//			for (String s : nerazpoznati) {
//				System.out.println(s);
//			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	private static void updateVMP(Integer id, String subsCodes, String name) throws DbErrorException {
		parsed.put(name, subsCodes);
		try {
			
			if (subsCodes.trim().isEmpty()) {
				System.out.println("aaaa");
			}
			
			cntFound++;
			
			JPA.getUtil().begin();
			
			JPA.getUtil().getEntityManager().createNativeQuery("update temp_vmp set subs_codes = :codes where id = :id").setParameter("codes", subsCodes).setParameter("id", id).executeUpdate();
			
			JPA.getUtil().commit();
		} catch (DbErrorException e) {
			JPA.getUtil().rollback();
			throw e;
		}catch (Exception e) {
			JPA.getUtil().rollback();
			throw new DbErrorException("Грешка при ъпдейт на код на субстанции", e);
		}finally {
			JPA.getUtil().closeConnection();
		}
		
	}
	
	
	private static String fixErrors(String name) {
			
		
		
		name = name.replace("AMIXICILLIN TRIHYDRATE".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());
		name = name.replace("Amoxicillin (trihydrate)".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());
		name = name.replace("Amoxicillin (as trihydrate)".toUpperCase(), "Amoxicillin trihydrate ".toUpperCase());
		name = name.replace("AMOXYCILLIN (AS TRIHYDRATE)".toUpperCase(), "Amoxicillin trihydrate ".toUpperCase());
		name = name.replace("AMOXICILLIN (AS AMOXICILLIN TRIHYDRATE)".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());				
		name = name.replace("AMOXACILLIN (AS AMOXACILLIN TRIHYDRATE)".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());		
		name = name.replace("AMOXICILLIN (AS AMOXICILLIN TRIHYDREATE )".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());	
		name = name.replace("AMOXICILLIN (AS AMOXICILLIN TRIHYDREATE)".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());
		name = name.replace("AMOXICILLIN BASE (AS AMOXICILLIN TRIHYDRATE)".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());		
		name = name.replace("AMOXICILLIN (AS SODIUM)".toUpperCase(), "Amoxicillin sodium ".toUpperCase());
		
		name = name.replace("AMOXICILLIN ( TRIHYDRATE)".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());
		name = name.replace("AMOXICILLIN (КАТО AMOXICILLIN TRIHYDRATE)".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());
		name = name.replace("AMOXICILLIN (КАТО TRIHYDRATE)".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());
		name = name.replace("AMOXICILLIN TRIHYDTARE".toUpperCase(), "Amoxicillin trihydrate".toUpperCase());
		
		
				
		name = name.replace("AMPICILLIN (AS AMPICILLIN SODIUM)".toUpperCase(), "Ampicillin sodium ".toUpperCase());
		name = name.replace("AMPICILLIN (AS AMPICILLIN TRIHYDRATE)".toUpperCase(), "Ampicillin trihydrate ".toUpperCase());
		name = name.replace("AMPICILLIN (AS TRIHYDRATE)".toUpperCase(), "Ampicillin trihydrate ".toUpperCase());
				
		name = name.replace("BENZYLPENICILLIN (AS BENZATHINE))".toUpperCase(), "Benzathine benzylpenicillin ".toUpperCase());		
		name = name.replace("Benzylpenicillin ( as procaine monohydrate)".toUpperCase(), "Benzylpenicillin procaine monohydrate ".toUpperCase());
		name = name.replace("Benzylpenicillin (as procaine monohydrate)".toUpperCase(), "Benzylpenicillin procaine monohydrate ".toUpperCase());
		name = name.replace("BENZYLPENICILLIN (AS PROCAINE MONOHYDRATE)".toUpperCase(), "Benzylpenicillin procaine monohydrate ".toUpperCase());
				
		name = name.replace("BUTORPHANOL (AS TARTRATE)".toUpperCase(), "Butorphanol tartrate".toUpperCase());
		
		name = name.replace("CEFALEXIN (AS MONOHYDRATE)".toUpperCase(), "Cefalexin monohydrate ".toUpperCase());
		
		name = name.replace("CEFAPIRIN (AS BENZATHINE)".toUpperCase(), "Cefapirin benzathine ".toUpperCase());
		name = name.replace("CEFAPIRIN (AS SODIUM SALT)".toUpperCase(), "Cefapirin sodium ".toUpperCase());
				
		name = name.replace("CEFQUINOME (AS CEFQUINOME SULFATE)".toUpperCase(), "Cefquinome sulfate ".toUpperCase());
				
		name = name.replace("CEFTIOFUR (AS SODIUM)".toUpperCase(), "Ceftiofur sodium ".toUpperCase());
		name = name.replace("CEFTIOFUR (AS HYDROCHLORIDE)".toUpperCase(), "Ceftiofur hydrochloride ".toUpperCase());
		name = name.replace("CEFTIUFUR (AS HYDROCHLORIDE)".toUpperCase(), "Ceftiofur hydrochloride ".toUpperCase());
		
		name = name.replace("CHLORTETRACYCLINE (AS CHLORTETRACYCLINE HYDROCHLORIDE)".toUpperCase(), "CHLORTETRACYCLINE HYDROCHLORIDE ".toUpperCase());
		name = name.replace("CHLORTETRACYCLINE (AS HYDROCHLORIDE)".toUpperCase(), "CHLORTETRACYCLINE HYDROCHLORIDE ".toUpperCase());
		
		name = name.replace("CLAVULANIC ACID (AS POTASSIUM CLAVULANATE)".toUpperCase(), "Potassium Clavulanate ".toUpperCase());
		name = name.replace("CLAVULONIC ACID (AS POTASSIUM CLAVULANATE)".toUpperCase(), "Potassium Clavulanate".toUpperCase());
		
		name = name.replace("Clavulanic acid (as Potassium salt)".toUpperCase(), "CLAVULANIC ACID (POTASSIUM)".toUpperCase());
		name = name.replace("CLAVULANIC ACID (AS POTASSIUM CLAVULANATE)".toUpperCase(), "CLAVULANIC ACID (POTASSIUM)".toUpperCase());
				
		name = name.replace("CLINDAMYCIN BASE (AS HYDROCHLORIDE)".toUpperCase(), "Clindamycin hydrochloride ".toUpperCase());
		name = name.replace("CLOPROSTENOL (AS CLOPROSTENOL SODIUM)".toUpperCase(), "Cloprostenol sodium ".toUpperCase());
		
		name = name.replace("CLOXACILLIN (AS CLOXACILLIN BENZATHINE)".toUpperCase(), "Cloxacillin benzathine ".toUpperCase());
		name = name.replace("CLOXACILLIN (AS CLOXACILLIN SODIUM)".toUpperCase(), "Cloxacillin sodium ".toUpperCase());
		name = name.replace("CLOXACILLIN (AS SODIUM)".toUpperCase(), "Cloxacillin sodium ".toUpperCase());
		
		name = name.replace("CLOXACILLIN (BENZATHINE)".toUpperCase(), "Cloxacillin benzathine ".toUpperCase());
		
		name = name.replace("COLISTIN (AS COLISTIN SULFATE)".toUpperCase(), "COLISTIN SULFATE ".toUpperCase());
		
		name = name.replace("Colistin (as sulfate)".toUpperCase(), "COLISTIN SULFATE ".toUpperCase());
		name = name.replace("Colistin (sulfate)".toUpperCase(), "COLISTIN SULFATE ".toUpperCase());
		name = name.replace("Cefquinome (as sulfate)".toUpperCase(), "Cefquinome sulfate ".toUpperCase());
		
		name = name.replace("D - CLOPROSTENOL (AS D - CLOPROSTENOLSODIUM)".toUpperCase(), "D-CLOPROSTENOL SODIUM SALT ".toUpperCase());
				
		name = name.replace("DEXAMETHAZONE (AS DEXAMETHAZONE ACETATE)".toUpperCase(), "Dexamethasone acetate ".toUpperCase());
		
		name = name.replace("DINOPROST (AS DINOPROST TROMETHAMINE)".toUpperCase(), "DINOPROST TROMETHAMINE ".toUpperCase());
		
		name = name.replace("Dihydrostreptomycin (as sulfate)".toUpperCase(), "Dihydrostreptomycin sulfate ".toUpperCase());
		name = name.replace("Doxycycline (hyclate)".toUpperCase(), "Doxycycline hyclate ".toUpperCase());		
		name = name.replace("Doxycycline (as hyclate)".toUpperCase(), "Doxycycline hyclate ".toUpperCase());
		name = name.replace("Doxycycline ( as hyclate)".toUpperCase(), "Doxycycline hyclate ".toUpperCase());
		name = name.replace("Doxycycline ( as Doxycycline hyclate)".toUpperCase(), "Doxycycline hyclate ".toUpperCase());
		name = name.replace("DOXYCYCLINE (AS DOXYCYCLINE HYCLATE)".toUpperCase(), "Doxycycline hyclate ".toUpperCase());
		
		name = name.replace("FLUNIXIN (AS FLUNIXIN MEGLOMINE)".toUpperCase(), "Flunixin meglumine ".toUpperCase());
		
		name = name.replace("GENTAMYCIN (AS GENTAMYCIN SULFATE)".toUpperCase(), "Gentamicin sulfate ".toUpperCase());
		
		name = name.replace("GONADORELIN (AS ACETATE)".toUpperCase(), "Gonadorelin acetate ".toUpperCase());
		name = name.replace("GONADORELIN (AS DIACETATE)".toUpperCase(), "Gonadorelin diacetate ".toUpperCase());
		
		name = name.replace("IRON (AS DEXTRAN COMPLEX)".toUpperCase(), "IRON(III)-HYDROXIDE DEXTRAN COMPLEX ".toUpperCase());
		
		name = name.replace("LINCOMYCIN (AS HYDROCHLORIDE)".toUpperCase(), "Gonadorelin diacetate ".toUpperCase());
				
		name = name.replace("Lincomycin (as Lincomycin hydrochloride)".toUpperCase(), "Lincomycin hydrochloride ".toUpperCase());
		
		name = name.replace("NAFCILLIN (AS SODIUM SALT)".toUpperCase(), "Nafcillin sodium ".toUpperCase());
		
		name = name.replace("NEOMYCIN (AS SULFATE)".toUpperCase(), "NEOMYCIN SULFATE ".toUpperCase());
		
		name = name.replace("Oxytetracycline (as dihydrate)".toUpperCase(), "Oxytetracycline dihydrate".toUpperCase());
		name = name.replace("OXYTETRACYCLINE (AS HYDROCHLORIDE)".toUpperCase(), "Oxytetracycline hydrochloride ".toUpperCase());
		name = name.replace("OXYTETRACYCLINE (AS OXYTETRACYCLINE DIHYDRATE)".toUpperCase(), "Oxytetracycline dihydrate".toUpperCase());
		
		name = name.replace("Pyriproxifen".toUpperCase(), "Pyriproxyfen".toUpperCase());
		
		name = name.replace("PYRANTEL (AS PYRANTEL EMBONATE)".toUpperCase(), "Pyrantel embonate ".toUpperCase());
		
		name = name.replace("Spectinomycin (as Spectinomycin sulfate)".toUpperCase(), "Spectinomycin sulfate".toUpperCase());
		
		name = name.replace("SPECTINOMYCIN (AS DIHYDROCHLORIDE PENTAHYDRATE)".toUpperCase(), "Spectinomycin dihydrochloride pentahydrate ".toUpperCase());
		name = name.replace("SPECTINOMYCIN (AS HYDROCHLORIDE)".toUpperCase(), "SPECTINOMYCIN HYDROCHLORIDE ".toUpperCase());
		name = name.replace("SPECTINOMYCIN (AS SULFATE)".toUpperCase(), "Spectinomycin sulfate".toUpperCase());
		
		name = name.replace("SULFADIMETHOXINE (AS SODIUM)".toUpperCase(), "Sulfadimethoxine sodium ".toUpperCase());
		
		name = name.replace("S - Methoprene".toUpperCase(), "(S)-Methoprene".toUpperCase());
		
		name = name.replace("Tilmicosin (as phosphate)".toUpperCase(), "Tilmicosin phosphate".toUpperCase());
		name = name.replace("Tylosin (as phosphate)".toUpperCase(), "Tylosin phosphate".toUpperCase());
		
		name = name.replace("VITAMIN A (AS PALMITATE)".toUpperCase(), "VITAMIN A PALMITATE ".toUpperCase());
		name = name.replace("VITAMIN E (AS ACETATE)".toUpperCase(), "VITAMIN E ACETATE ".toUpperCase());
	
		name = name.replace("ZOLAZEPAM (AS HYDROCHLORIDE)".toUpperCase(), "Zolazepam hydrochloride ".toUpperCase());
		
		name = name.replace("TIAMULIN BASE (AS TIAMULIN HYDROGEN FUMARATE )".toUpperCase(), "Tiamulin hydrogen fumarate ".toUpperCase());
		name = name.replace("TILETAMINE (AS HYDROCHLORIDE)".toUpperCase(), "Tiletamine hydrochloride ".toUpperCase());
		name = name.replace("TOBRAMYCIN (AS SULFATE)".toUpperCase(), "TOBRAMYCIN SULFATE ".toUpperCase());
		name = name.replace("TYLOSIN (AS TARTARATE)".toUpperCase(), "Tylosin tartrate ".toUpperCase());
		name = name.replace("TYLOSIN ACTIVITY (AS TYLOSIN PHOSPHATE)".toUpperCase(), "Tylosin phosphate ".toUpperCase());
		
		
		name = name.replace("NEWCASTLE DISEASE VIRUS (NDV), INACTIVATED, STRAIN ULSTER 2C".toUpperCase(), "Newcastle disease virus, strain Ulster 2C, Inactivated".toUpperCase());
		name = name.replace("NEWCASTLE DISEASE VIRUS (NDV), LIVE, STRAIN 13-1".toUpperCase(), "Newcastle disease virus, strain Clone 13-1, Live ".toUpperCase());
		name = name.replace("NEWCASTLE DISEASE VIRUS (NDV), LIVE, STRAIN CLONE 30".toUpperCase(), "Newcastle disease virus, strain Clone 30, Live ".toUpperCase());
		
		name = name.replace("NEWCASTLE DISEASE VIRUS (NDV), LIVE, STRAIN CLONE 30".toUpperCase(), "Newcastle disease virus, strain La Sota ".toUpperCase());
		name = name.replace("NEWCASTLE DISEASE VIRUS (NDV), STRAIN LA SOTA".toUpperCase(), "Newcastle disease virus, strain La Sota ".toUpperCase());
		
		name = name.replace("NEWCASTLE DISEASE VIRUS (NDV), STRAIN PHY.LMY".toUpperCase(), "Newcastle disease virus, strain PHY.LMV.42, Live ".toUpperCase());
		name = name.replace("NEWCASTLE DISEASE VIRUS (NDV), STRAIN ULSTER 2C".toUpperCase(), "Newcastle disease virus, strain Ulster 2C, Live ".toUpperCase());
		name = name.replace("NEWCASTLE DISEASE VIRUS (NDV),LIVE, STRAIN C2".toUpperCase(), "Newcastle disease virus, strain C2, Live ".toUpperCase());
		
		name = name.replace("GENTAMICIN (SULFATE)".toUpperCase(), "Gentamicin sulfate ".toUpperCase());
		name = name.replace("AMOXACILLIN".toUpperCase(), "Amoxicillin ".toUpperCase());
		
		
		
		//name = name.replace("TYLOSINE (AS TARTRATE)".toUpperCase(), "Zolazepam hydrochloride ".toUpperCase());
		//name = name.replace("XYLAZINE (AS HCI)".toUpperCase(), "VITAMIN A PALMITATE ".toUpperCase());
		//name = name.replace("NITROXINIL (AS N-ETHYLGLUCAMINE)".toUpperCase(), "Oxytetracycline dihydrate".toUpperCase());
		//name = name.replace("NITROXYNIL (AS N-ETHYLGLUCAMINESALT)".toUpperCase(), "Oxytetracycline dihydrate".toUpperCase());
		//name = name.replace("DIHYDROSTREPTOMYCIN (AS DIHYDROSTREPTOMYCIN SULFATE)".toUpperCase(), "Cefquinome sulfate ".toUpperCase());		
		//name = name.replace("DIHYDROSTREPTOMYCIN (AS SULFATE)".toUpperCase(), "Cefquinome sulfate ".toUpperCase());
		//name = name.replace("DIHYDROSTREPTOMYCIN (AS SULPHATE)".toUpperCase(), "Cefquinome sulfate ".toUpperCase());
		//name = name.replace("DEXAMETHASONE (AS 21 SODIUM PHOSPHATE)".toUpperCase(), "COLISTIN SULFATE ".toUpperCase());
		//name = name.replace("DEXAMETHASONE BASE (AS SODIUM PHOSPHATE)".toUpperCase(), "COLISTIN SULFATE ".toUpperCase());
						
		
		
		return name;
	}

	
	
	
	private static void putSubs(String sub) {
		
		if (sub == null) {
			return;
		}
		
		sub = sub.trim().toUpperCase();
				
		
		Long cnt = nosubs.get(sub);
		if (cnt == null) {
			cnt = 0L;
		}
		
		cnt++;
		nosubs.put(sub, cnt);
	}
	
	
	private static void printSubs() {
		
		Iterator<Entry<String, Long>> it = nosubs.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Long> entry = it.next();
			System.out.println(entry.getValue() + " "  + entry.getKey());
		}
	}
	
	public static boolean compare(String str1, String str2) {
	    
    	if (str2 == null || str1 == null) {
    		return false;
    	}
    	
    	str1 = str1.toUpperCase().trim().replace(" ", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "").replace("+", "").replace("–", "").replace("-", "");
    	str2 = str2.toUpperCase().trim().replace(" ", "").replace(",", "").replace(".", "").replace("(", "").replace(")", "").replace("+", "").replace("–", "").replace("-", "");
    	
    	return (str1.equalsIgnoreCase(str2));
    }
}

	


