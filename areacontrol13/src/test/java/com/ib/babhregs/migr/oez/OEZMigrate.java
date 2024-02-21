package com.ib.babhregs.migr.oez;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.Query;

import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.ObektDeinostLica;
import com.ib.babhregs.db.dto.OezHarakt;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.db.dto.VpisvaneAccess;
import com.ib.babhregs.db.dto.VpisvaneDoc;
import com.ib.babhregs.db.dto.VpisvaneStatus;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.system.db.JPA;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;


public class OEZMigrate {
	
	public static int seqVal = 100000;
	
	public static boolean isTest = false;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OEZMigrate.class);
	
	private static Integer[] access = {-1, 1712, 1795, 1751, 1661, 1675};
	
	public static void main(String[] args) {
		
		try {
			
			//exportClassif(12);
			
			
			transferOEZ();
//			System.out.println(getSeq("SEQ_DOC"));
//			System.out.println(getSeq("SEQ_DOC"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	private static void exportClassif(int i) {
		
		//много е тодо
		
//		String connectTo = "vetisReal";
//		String transferSQL = "select ID_VETIS from TEMP_ISSR_TRANSFER where TIP = 'OEZ' and done is null ";
//		
//		ArrayList<Object[]> ids = (ArrayList<Object[]>) JPA.getUtil(connectTo).getEntityManager().createNativeQuery(transferSQL).getResultList();
		
	}




	@SuppressWarnings("unchecked")	
	public static void transferOEZ() {
		
		
		TreeSet<Integer> notFoundVids = new  TreeSet<Integer>();
		HashMap<Integer, Integer> foundLica = new HashMap<Integer, Integer>(); 
		HashMap<Integer, Integer> tipLica = new HashMap<Integer, Integer>();
		
		
		
		SystemData sd = new SystemData();
		
		
		
		String sqlMain = "select  jo.id, \n"
				+ "        jo.reg_nomer_reglament, \n"
				+ "        jo.reg_nomer_star, \n"
				+ "        jo.reg_nomer, \n"
				+ "        jop.naim, \n"
				+ "		   EKATTE_OBLASTI.OBLAST, \n"
				+ "        EKATTE_OBSHTINI.OBSHTINA, \n"
				+ "        EKATTE_NAS_MESTA.EKNM  NAS_MESTO, \n"
				+ "        jop.adres, \n"
				+ "        jo.post_kod, \n"
				+ "        jo.tel, \n"
				+ "        jo.E_MAIL,\n"
				+ "         jo.STATUS,\n"
				+ "         jo.DATA_STATUS,\n"
				+ "         jo.vid,\n"
				+ "         jo.GPS_N,\n"
				+ "         jo.GPS_E,\n"
				+ "         jos.REG_NOMER REGNOM_S,\n"
				+ "         jos.dat_beg,\n"
				+ "         jos.ID_IZDAL,\n"
				+ "         jos.KATEGORIA\n,"
				+ "			jos.ID SERTID" 				
				+ "        from JIV_OBEKT jo\n"
				+ "        left outer join JIV_OBEKT_SERT jos  on jo.id = jos.ID_OBEKT\n"
				+ "        left outer join JIV_OBEKT_POM jop on jo.id = jop.ID_JIV_OBEKT and jop.pom = 1\n"
				+ " 	   left outer join EKATTE_OBLASTI on jo.OBL = EKATTE_OBLASTI.id \n"
				+ " 	   left outer join EKATTE_NAS_MESTA on jo.NAS_MESTO = EKATTE_NAS_MESTA.id \n"
				+ "		   left outer join EKATTE_OBSHTINI on jo.OBSHT = EKATTE_OBSHTINI.id \n"
				
				//+ "where jos.DAT_END is null and jos.KATEGORIA is not null and jos.VID_SERT = 590 and jo.id = :idoez";
				+ "where jos.DAT_END is null and jos.VID_SERT = 590 and jo.id = :idoez";
		
		
		
		
		
		HashMap<Integer, Integer> regMap = createRegistraturiMap();
		HashMap<Integer, Integer> vidOezMap = createCategoriaMap();
		HashMap<Integer, Integer> techMap = createTehnologyMap();
		HashMap<Integer, Integer> prednMap = createPrednaznachenieMap();
		
		
		
		
		Date dat = new Date();
		
		
		
		int cnt = 0;
		int cntSkipped = 0;
		int cntErrors = 0;
		
		//String connectTo = "vetis";		
		String connectTo = "vetisReal";
		
		String transferSQL = "select ID_VETIS from TEMP_ISSR_TRANSFER where TIP = 'oez' and done is null ";
		
		ArrayList<Object[]> ids = (ArrayList<Object[]>) JPA.getUtil(connectTo).getEntityManager().createNativeQuery(transferSQL).getResultList();
				

		Iterator<Object[]> it = ids.iterator();
		
		
		
		
//		JPA.getUtil().begin();
//		
//		JPA.getUtil().getEntityManager().createNativeQuery("delete from oez where oez.id > 0").executeUpdate();
		
		
		while (it.hasNext()) {
			
			Integer id = SearchUtils.asInteger(it.next());
			
			
			
			
			//id = 614522;
			
					
			try {
				cnt++;
				
				System.out.println(cnt + ". Мигриране на ОЕЗ с id = " + id);
				
				
				ArrayList<Object[]> mainData =  (ArrayList<Object[]>) JPA.getUtil(connectTo).getEntityManager().createNativeQuery(sqlMain).setParameter("idoez", id).getResultList();
				if (mainData.size() != 1) {
					System.out.println("********************************************** WARNING mainData.size = " + mainData.size());
					cntSkipped ++;
					LOGGER.info("Неочаквано размножаване на основни данни за ОЕЗ с id " + id );
					continue;
				}
				
				
				Vpisvane vpisvane = new Vpisvane();
				vpisvane.setFromМigr(1);
				Doc docU = new Doc();
				Doc doc = new Doc();
				ObektDeinost oez = new ObektDeinost();
				ArrayList<Referent> referents = new ArrayList<Referent>();
				ArrayList<ObektDeinostLica> odlList = new ArrayList<ObektDeinostLica>();
				ArrayList<VpisvaneDoc> vdocList = new ArrayList<VpisvaneDoc>();
				ArrayList<VpisvaneAccess> allVAccess = new ArrayList<VpisvaneAccess>();
				ArrayList<OezHarakt> allHaract = new ArrayList<OezHarakt>();
				ObektDeinostDeinost odd = new ObektDeinostDeinost();
				VpisvaneStatus vs = new VpisvaneStatus();
				
				
				int idOD = getSeq("seq_obekt_deinost");
				int idODD = getSeq("seq_obekt_deinost_deinost");
				int idDoc = getSeq("SEQ_DOC");
				int idDocU = getSeq("SEQ_DOC");
				//int idVpisvane = getSeq("seq_vpisvane");
				int idVpisvane = id;  //По изричното настояване на КК
				
				
				
				
				Object[] tekObekt = mainData.get(0);
				
						//		 jo.id, \n"
				String regNomRegl = SearchUtils.asString(tekObekt[1]);		//        jo.reg_nomer_reglament, \n"
				String regNomStar = SearchUtils.asString(tekObekt[2]);		//        jo.reg_nomer_star, \n"
				//String regNom = SearchUtils.asString(tekObekt[3]);		//        jo.reg_nomer, \n"
				String naim = SearchUtils.asString(tekObekt[4]);		//        jop.naim, \n"
				//String obl = SearchUtils.asString(tekObekt[5]);		//        jo.obl, \n"
				//String obsh = SearchUtils.asString(tekObekt[6]);		//        jo.OBSHT, \n"
				Integer nasMesto = SearchUtils.asInteger(tekObekt[7]);		//        jo.NAS_MESTO, \n"
				String adres = SearchUtils.asString(tekObekt[8]);		//        jop.adres, \n"
				String pkod = SearchUtils.asString(tekObekt[9]);		//        jo.post_kod, \n"
				String tel = SearchUtils.asString(tekObekt[10]);		//        jo.tel, \n"
				String mail = SearchUtils.asString(tekObekt[11]);		//        jo.E_MAIL,\n"
				Integer status = SearchUtils.asInteger(tekObekt[12]);		//         jo.STATUS,\n"
				Date datStatus = SearchUtils.asDate(tekObekt[13]);		//         jo.DATA_STATUS,\n"
				//Integer vidOez = SearchUtils.asInteger(tekObekt[14]);		//         jo.vid,\n"
				Double gpsn = SearchUtils.asDouble(tekObekt[15]);		//         jo.GPS_N,\n"
				Double gpse = SearchUtils.asDouble(tekObekt[16]);		//         jo.GPS_E,\n"
				String regNomSert = SearchUtils.asString(tekObekt[17]);		//         jos.REG_NOMER REGNOM_S,\n"
				Date datBegSert = SearchUtils.asDate(tekObekt[18]);		//         jos.dat_beg,\n"
				Integer idIzdal = SearchUtils.asInteger(tekObekt[19]);		//         jos.ID_IZDAL\n"
				Integer kategoria = SearchUtils.asInteger(tekObekt[20]);		//         jos.kategoria\n"
				Integer sertId = SearchUtils.asInteger(tekObekt[21]);		//         jos.id\n"
				
				
				
				
				
				odd.setId(idODD);				
				odd.setDateBeg(datBegSert);
				odd.setObektDeinostId(idOD);
				
				odd.setDeinostId(id);
				odd.setTablEventDeinost(id);
				
				
				
				
				//НАЧАЛО НА ОЕЗ - Обект Дейност //////////////////////////////////////////////////////////////////////////////////////////
				oez.setId(idOD);
				oez.setRegNom(regNomRegl);
				oez.setRegNomerStar(regNomStar);
				oez.setNaimenovanie(naim);
				oez.setVid(1);
				oez.setDarj(37);  //България
				oez.setNasMesto(nasMesto);
				oez.setAddress(adres);
				
				if (pkod != null) {
					oez.setPostCode(pkod.trim());
				}
				
				
				oez.setEmail(mail);
				oez.setTel(tel);
				oez.setStatus(status);
				oez.setStatusDate(datStatus);
				oez.setGpsLat(gpsn);
				oez.setGpsLon(gpse);
				oez.setUserReg(-1);
				oez.setDateReg(new Date());
				
				if (kategoria != null) {
					
					Integer vid = vidOezMap.get(kategoria);
					if (vid != null) {
						oez.setVidOez(vid);
					}else {
						cntSkipped ++;
						 LOGGER.warn("Не може да се прекодира категория на на ОЕЗ ("+kategoria+")с id " + id);
						 System.out.println("Не може да се прекодира категория на на ОЕЗ ("+kategoria+")с id " + id);
						 notFoundVids.add(kategoria);  // тук се пазят ненамерените - печат в края
						continue;
						
					}
					
				}else {
					oez.setVid(52);
				}
				
				
				
				//oez.setIdVetis(id);
				oez.setDopInfo("Записът е създаден от транформация!");
				
				
				//КРАЙ НА ОЕЗ - Обект Дейност //////////////////////////////////////////////////////////////////////////////////////////
				
				//НАЧАЛО НА ХАРАКТЕРИСТИКИ  //////////////////////////////////////////////////////////////////////////////////////////
				
				 Query queryTemp = JPA.getUtil(connectTo).getEntityManager().createNativeQuery("select VID_JIVOTNO, KOD_HARAKT, ZNACHENIE_HARAKT, DATA_BEG, KAPACITET_NUMBER from JIV_OBEKT_HARAKT where ID_JIV_OBEKT_SERT = ? and DATA_END is null");
				 queryTemp.setParameter(1, sertId);
				 
				 ArrayList<Object[]> allH = (ArrayList<Object[]>) queryTemp.getResultList();
				 if (allH.size() == 0) {
					 LOGGER.warn("Няма характеристики за ОЕЗ с id " + id + " за сертификат с id " + sertId );
				 }else {
					 TreeSet<Integer> codesJ = new TreeSet<Integer>();					 
					 
					 for (Object[] har : allH) {
						 codesJ.add(Integer.parseInt(""+har[0]));
					 }
					 
					 for (Integer jiv : codesJ) {
						 
						 ArrayList<Integer> codesP = new ArrayList<Integer>();
						 ArrayList<Integer> codesT = new ArrayList<Integer>();
						 ArrayList<Integer> kapN = new ArrayList<Integer>();
						 ArrayList<String> kapT = new ArrayList<String>();
						 
						 
						 
						 for (Object[] har : allH) {
							 Integer vidJ = Integer.parseInt(""+har[0]);
							 Integer kod = SearchUtils.asInteger(har[1]);
							 Integer znachenie = SearchUtils.asInteger(har[2]);								 
							 Integer num = SearchUtils.asInteger(har[4]);
							 
							 if (vidJ.equals(jiv)) {
								 
								 if (kod.equals(49)) {
									 Integer prekod = prednMap.get(znachenie);
									 if (prekod == null) {
										 LOGGER.warn("Внимание! не се намира преднзначение с код "  + znachenie + " за оез с ид = " + id);										 
									 }else {
										 codesP.add(prekod);
									 }
								 }
								 
								 if (kod.equals(57)) {
									 
								 }
								 
								 if (kod.equals(60)) {
									 Integer prekod = techMap.get(znachenie);
									 if (prekod == null) {
										 LOGGER.warn("Внимание! не се намира технология с код "  + znachenie + " за оез с ид = " + id);										 
									 }else {
										 codesT.add(prekod);
									 }
								 }
								 
								 if (kod.equals(62)) {
									 String decodeSQL = "select TEKST from ADM_SYSTEM_CLASSIF where CODE_CLASSIF = 62 and CODE = ? and DATE_DO is null and LANG = 1";
									 
									 Query queryDecode = JPA.getUtil(connectTo).getEntityManager().createNativeQuery(decodeSQL);
									 queryDecode.setParameter(1, znachenie);
									 
									 ArrayList<Object> allDecod = (ArrayList<Object>) queryDecode.getResultList();
									 if (allDecod.size() != 1) {
										 LOGGER.warn("Внимание! не може да се декодира капацитет с код "  + znachenie + " за оез с ид = " + id);
									 }else {
										 kapT.add("Капацитет: "+allDecod.get(0));
									 }
									 
									 
								 }
								 
								 if (kod.equals(-62)) {
									 kapN.add(num);
								 }
								 
								 
								 
							 }
						 }
						 
						 if (codesP.size() == 0) {
							 codesP.add(37);  //не е посочено
						 }
						 if (codesT.size() == 0) {
							 codesT.add(22); //не е посочено
						 }
						 if (kapT.size() == 0) {
							 kapT.add(null);
						 }
						 if (kapN.size() == 0) {
							 kapN.add(null);
						 }
						 
						 for (Integer predn : codesP) {
							 for (Integer tech : codesT) {
								 for (Integer valueN : kapN) {
									 for (String valueT : kapT) {
										 OezHarakt ohar = new OezHarakt();
										 
										 List<SystemClassif> found = sd.getItemsByCodeExt(508, ""+jiv, 1, new Date());
										 Integer jivReg = jiv;
										 if (found.size() == 0) {
											 LOGGER.warn("Не може да се прекодира вид животно с код " + jiv + " за оез с id " + id );
										 }else {
											 jivReg = found.get(0).getCode();
													 
										 }
										 
										 
										 ohar.setVidJivotno(jivReg);
										 ohar.setId(getSeq("seq_oez_harakt"));
										 ohar.setIdOez(oez.getId());
										 ohar.setPrednaznachenie(predn);
										 ohar.setKapacitet(valueN);
										 ohar.setKapacitetText(valueT);
										 ohar.setTehnologia(tech);
										 allHaract.add(ohar);
									 }
								 }
							 }
						 }
						 
						 
						 
					 }
				 }
				
				
				//КРАЙ НА ХАРАКТЕРИСТИКИ  //////////////////////////////////////////////////////////////////////////////////////////
				 
				//НАЧАЛО НА ЗАЯВЛЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
				
				doc.setId(idDoc);
				doc.setDocType(1); //Входящ
				doc.setDocVid(18); //Заявление за регистрация на животновъден обект
				if (! SearchUtils.isEmpty(regNomSert)) {
					doc.setRnDoc("Z"+ regNomSert);
				}else {
					cntSkipped ++;
					LOGGER.info("Няма номер на сертификат за ОЕЗ с id " + id );
					continue;
				}
				
				doc.setDocDate(datBegSert);
				
				Integer regId = regMap.get(idIzdal);
				if (regId == null) {
					cntSkipped ++;
					LOGGER.info("Не може да намери съоветствие за регистратура на ОЕЗ с id " + id + " и издал сертификата " + idIzdal);
					continue;
				}
				doc.setRegistraturaId(regId);
				
				String anot = "Заявление за регистрация на ОЕЗ ";
				if (! SearchUtils.isEmpty(naim)) {
					anot += naim;
				}	
				doc.setOtnosno(anot);
				doc.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
				doc.setImeAdnUsluga("Регистрация на обект за отглеждане или настаняване на животни");
				doc.setCodeAdmUsluga("273");
				doc.setRegisterId(4);  
				doc.setLicenziantType(1);
				doc.setIdLicenziant(idOD);
				doc.setStatus(15);
				doc.setStatusDate(datStatus);
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
				
				
				
				//КРАЙ НА ЗАЯВЛЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
				//НАЧАЛО НА УДОСТОВЕРЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
			
				
				
				docU.setId(idDocU);
				docU.setDocType(2); //Изходящ
				docU.setDocVid(211); //Удостоверение за регистрация на животновъден обект
				
				docU.setRnDoc(regNomSert);
				docU.setDocDate(doc.getDocDate());
				
				docU.setRegistraturaId(doc.getRegistraturaId());
				
				anot = "Удостоверение за регистрация на ОЕЗ ";
				if (! SearchUtils.isEmpty(naim)) {
					anot += naim;
				}	
				docU.setOtnosno(anot);
				docU.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
				docU.setImeAdnUsluga("Регистрация на обект за отглеждане или настаняване на животни");
				docU.setCodeAdmUsluga("273");
				docU.setRegisterId(4);  
				docU.setLicenziantType(1);
				docU.setIdLicenziant(idOD);
				docU.setStatus(15);
				docU.setStatusDate(datStatus);
				docU.setGuid("{"+java.util.UUID.randomUUID().toString()+"}");
				docU.setFreeAccess(2);
				docU.setDocInfo("Записът е създаден от транформация!");
				docU.setUserReg(-1);
				docU.setDateReg(new Date());
				
				docU.setCodeRefCorresp(-3);
				docU.setValid(1);
				docU.setValidDate(datBegSert);
				docU.setPayType(BabhConstants.CODE_ZNACHENIE_PAY_TYPE_NOPAY );
				
				
				//КРАЙ НА УДОСТОВЕРЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
				//НАЧАЛО НА ЛИЦА  //////////////////////////////////////////////////////////////////////////////////////////
				
				
				
				//ЛИЦА
				String sqlLica = "select  jol.ID_LICE_ORG, jol.DATA_BEG, jol.TIP_VRAZ, \n"
						+ "        lo.EGN_BULSTAT, lo.LNCH, EKATTE_NAS_MESTA.EKNM, lo.POST_KOD, lo.E_MAIL, TEL, lo.TIP, \n"
						+ "        lop.NAIM_ORG, lop.IME, lop.PREZIME, lop.FAMILIA, lop.ADRES, lop.ADRES_UPR \n"
						+ "from JIV_OBEKT_LICE jol\n"
						+ "        left outer join LICA_ORG lo on lo.id = jol.ID_LICE_ORG\n"
						+ "        left outer join LICA_ORG_POM lop on  jol.ID_LICE_ORG = lop.ID_LICA_ORG and pom = 1\n"	
						+ "        left outer join EKATTE_NAS_MESTA on  EKATTE_NAS_MESTA.id = lo.NAS_MESTO\n"	
						+ "where jol.TIP_VRAZ in (503, 504)  and jol.DATA_END is null and jol.ID_JIV_OBEKT = :idoez";
				
				
				queryTemp = JPA.getUtil(connectTo).getEntityManager().createNativeQuery(sqlLica);
				queryTemp.setParameter("idoez", id);
				
				ArrayList<Object[]> lica = (ArrayList<Object[]>) queryTemp.getResultList();
				
				for (Object[] lice : lica ) {
					Integer liceId = SearchUtils.asInteger(lice[0]);
					Date datBegL = SearchUtils.asDate(lice[1]);
					Integer tipVraz = SearchUtils.asInteger(lice[2]);
					String egnBulstat = SearchUtils.asString(lice[3]);
					String lnch = SearchUtils.asString(lice[4]);
					Integer nasMestoL = SearchUtils.asInteger(lice[5]);
					String pkodL = SearchUtils.asString(lice[6]);
					String mailL = SearchUtils.asString(lice[7]);
					String telL = SearchUtils.asString(lice[8]);
					Integer tipLice = SearchUtils.asInteger(lice[9]);
					String naimOrg = SearchUtils.asString(lice[10]);
					String ime = SearchUtils.asString(lice[11]);
					String prezime = SearchUtils.asString(lice[12]);
					String familia = SearchUtils.asString(lice[13]);
					String adresLice = SearchUtils.asString(lice[14]);
					String adresUpr = SearchUtils.asString(lice[15]);
					
					
					
					Integer codeReferent = foundLica.get(liceId);
					
					if ( codeReferent == null) {
						
						Integer idReferent = getSeq("SEQ_ADM_REFERENTS");
						codeReferent = getSeq("SEQ_ADM_REFERENTS_CODE");
						
					
						//Записваме референт
						Referent ref = new Referent();
						ref.setId(idReferent);
						ref.setCode(codeReferent);
						ref.setCodeClassif(0);
						ref.setCodeParent(0);
						ref.setCodePrev(0);
						ref.setContactEmail(mailL);
						ref.setContactPhone(telL);
						ref.setDateBeg(DateUtils.systemMinDate());
						ref.setUserReg(-1);
						ref.setDateReg(new Date());
						ref.setDateOt(DateUtils.systemMinDate());
						ref.setDateDo(DateUtils.systemMaxDate());
						
						
						
						
						if (! SearchUtils.isEmpty(lnch)) {
							ref.setFzlLnc(lnch);
							ref.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
						}else {
							if (tipLice != null && tipLice == 1 ) {
								ref.setFzlEgn(egnBulstat);
								ref.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
							}else {
								ref.setNflEik(egnBulstat);
								ref.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
							}
						}
						
						
						
						tipLica.put(codeReferent, ref.getRefType());
						
						
						if (ref.getRefType() == BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL){
							ref.setIme(ime);
							ref.setPrezime(prezime);
							ref.setFamilia(familia);
							
							String namesL = "";
							if (! SearchUtils.isEmpty(ime)) {
								namesL += ime.trim() + " ";
							}
							if (! SearchUtils.isEmpty(prezime)) {
								namesL += prezime.trim() + " ";
							}
							if (! SearchUtils.isEmpty(familia)) {
								namesL += familia.trim() + " ";
							}
							ref.setRefName(namesL.trim());
						}else {
							ref.setIme(naimOrg);
							ref.setRefName(naimOrg);
						}
						
						
						if (nasMestoL != null) {
							int idRA = getSeq("SEQ_ADM_REF_ADDRS");
							ReferentAddress adrRef = new ReferentAddress();
							adrRef.setId(idRA);
							adrRef.setCodeRef(ref.getCode());
							adrRef.setAddrCountry(BabhConstants.CODE_ZNACHENIE_BULGARIA);
							adrRef.setEkatte(nasMestoL);
							
							if (! SearchUtils.isEmpty(adresLice)) {
								adrRef.setAddrText(adresLice);
							}else {
								adrRef.setAddrText(adresUpr);
							}
							
							adrRef.setPostCode(pkodL);
							adrRef.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
							adrRef.setUserReg(-1);
							adrRef.setDateReg(new Date());
							
							ref.setAddress(adrRef );
						}
						
						foundLica.put(liceId, codeReferent);
						referents.add(ref);
						
						
					}
					
					int idodl = getSeq("seq_obekt_deinost_lica");
					
					ObektDeinostLica odl = new ObektDeinostLica();
					odl.setId(idodl);
					odl.setCodeRef(codeReferent);
					odl.setDateBeg(datBegL);
					odl.setObektDeinostId(oez.getId());
					
					
					if (tipVraz != null && tipVraz == 503) {
						odl.setRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_SOBSTVENIK);
						vpisvane.setCodeRefVpisvane(codeReferent);
						Integer tip = tipLica.get(codeReferent);
						if (tip != null && tip   == BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL) {
							//doc.setCodeRefCorresp(codeReferent);
						}
					}else {
						odl.setRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_OBEKT_POLZVATEL);
					}
					
					odlList.add(odl);
				}
					
				
				//КРАЙ НА ЛИЦА  //////////////////////////////////////////////////////////////////////////////////////////
				
				//НАЧАЛО НА ВПИСВАНЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
				vpisvane.setId(idVpisvane);
				vpisvane.setIdZaqavlenie(doc.getId());
				vpisvane.setStatus(BabhConstants.STATUS_VP_VPISAN);
				vpisvane.setDateStatus(docU.getDocDate());
				vpisvane.setRegistraturaId(doc.getRegistraturaId());
				vpisvane.setIdRegister(doc.getRegisterId());
				vpisvane.setLicenziantType(doc.getLicenziantType());
				vpisvane.setIdLicenziant(doc.getIdLicenziant());
				
				vpisvane.setRegNomZaqvlVpisvane(doc.getRnDoc());
				vpisvane.setDateZaqvlVpis(doc.getDocDate());
				vpisvane.setRegNomResult(docU.getRnDoc());
				vpisvane.setDateResult(docU.getDocDate());
				vpisvane.setIdResult(docU.getId());
				vpisvane.setUserReg(-1);
				vpisvane.setDateReg(new Date());
				vpisvane.setCodePage(1);
				
				
				
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
				
				
				
				
				String licensiant = "";
				if (regNomRegl != null) {
					licensiant += regNomRegl;
				}
				if (regNomStar != null) {
					licensiant += "(" + regNomStar + ")";
				}
				
				if (naim != null) {
					licensiant += " " + naim;
				}
				vpisvane.setLicenziant(licensiant);
				
				
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
				
				
				//КРАЙ НА ВПИСВАНЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
				JPA.getUtil().begin();
				
				
				
				insertDoc(doc);
				insertDoc(docU);
				insertVpisvane(vpisvane);
				insertVpisvaneStatus(vs);	
				insertObektDeinost(oez);
				insertObektDeinostDeinost(odd);
				
				for (Referent ref : referents) {
					insertReferent(ref);
					if (ref.getAddress() != null) {
						insertRefAddress(ref.getAddress());
					}
				}
				
				for (ObektDeinostLica odl : odlList) {
					insertObektDeinostLica(odl);
				}
				
				for (OezHarakt har : allHaract) {
					insertOezHarakt(har);
				}
				
				for (VpisvaneDoc vd : vdocList) {
					insertVpisvaneDoc(vd);
				}
				
				for (VpisvaneAccess va : allVAccess ) {
					insertVpisvaneAccess(va);
				}
				
				
				
				
				
				
				JPA.getUtil().commit();
				
				
				
				
				
			} catch (Exception e) {		
				e.printStackTrace();
				cntErrors++;				
				LOGGER.error("Грешка при обработка на ОЕЗ с id:  " + id, e);
				JPA.getUtil().rollback();
				
			}finally {
				
			}
			
		}
		
		LOGGER.info("Общо Обработени ОЕЗ : " + cnt );
		LOGGER.info( "Прoпуснати ОЕЗ : " + cntSkipped );
		LOGGER.info("Грешки : " + cntErrors);
		LOGGER.info(dat + " - " + new Date());
		LOGGER.info("Ненамерени категории обекти : " + notFoundVids);
		
		System.out.println("Общо Обработени ОЕЗ : " + cnt);
		System.out.println("Прoпуснати ОЕЗ : " + cntSkipped);
		System.out.println("Грешки : " + cntErrors);
		System.out.println(dat + " - " + new Date());
		System.out.println(notFoundVids);
		
		
		
		
	}
		
		
		
	




	




	




	




	




	




	public static int getSeq(String seqName) throws DbErrorException {
		
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
	
	
	public static HashMap<Integer, Integer> createRegistraturiMap() {
		
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer> ();
		
		map.put(311, 139);	//Българска агенция по безопасност на храните
		map.put(186, 139);	//НВМС-ЦУ
		map.put(309, 139);	//Национална ветеринарномедицинска служба
	
	
		map.put(312, 140);	//ОДБХ-Благоевград
		map.put(40, 140);	//РВМС-Благоевград
	
		map.put(314, 118);	//ОДБХ-Бургас
		map.put(54, 118);	//РВМС-Бургас
	
		map.put(318, 125);	//ОДБХ-Варна
	
		map.put(319, 132);	//ОДБХ-Велико Търново
		map.put(254, 132);	//РВМС-Велико Търново
	
		map.put(320, 124);	//ОДБХ-Видин
		map.put(268, 124);	//РВМС-Видин
	
		map.put(321, 112);	//ОДБХ-Враца
		map.put(279, 112);	//РВМС-Враца
	
		map.put(322, 2);	//ОДБХ-Габрово
		map.put(288, 2);	//РВМС-Габрово
	
		map.put(323, 127);	//ОДБХ-Добрич
		map.put(210, 127);	//РВМС-Добрич
	
		map.put(324, 121);	//ОДБХ-Кърджали
		map.put(223, 121);	//РВМС-Кърджали
	
		map.put(231, 103);	//ОДБХ-Кюстендил
		map.put(325, 103);	//ОДБХ-Кюстендил
	
		map.put(326, 108);	//ОДБХ-Ловеч
		map.put(239, 108);	//РВМС-Ловеч
	
	
		map.put(315, 119);	//ОДБХ-Монтана
		map.put(221, 119);	//РВМС-Монтана
	
		map.put(327, 111);	//ОДБХ-Пазарджик
	
		map.put(328, 105);	//ОДБХ-Перник
	
		map.put(313, 110);	//ОДБХ-Плевен
		map.put(99, 110);	//РВМС-Плевен
		map.put(100, 110);	//ОВМС-Плевен
	
		map.put(329, 115);	//ОДБХ-Пловдив
		map.put(111, 115);	//РВМС-Пловдив
	
		map.put(330, 131);	//ОДБХ-Разград
		map.put(126, 131);	//РВМС-Разград
	
		map.put(331, 126);	//ОДБХ-Русе
		map.put(298, 126);	//РВМС-Русе
	
		map.put(334, 104);	//ОДБХ СМОЛЯН
		map.put(159, 104);	//РВМС-Смолян
	
		map.put(332, 133);	//ОДБХ - Силистра
		map.put(141, 133);	//РВМС - Силистра
			
		map.put(333, 106);	//ОДБХ-Сливен	
		map.put(149, 106);	//ОВМС-Сливен
	
		map.put(316, 116);	//ОДБХ-София
		map.put(185, 116);	//РВМС-София
	
		map.put(317, 117);	//ОДБХ-София област
		map.put(175, 117);	//РВМС-София област
	
		map.put(335, 113);	//ОДБХ-Стара Загора
		map.put(197, 113);	//РВМС-Стара Загора
	
		map.put(336, 109);	//ОДБХ-Търговище
		map.put(204, 109);	//РВМС-Търговище
	
		map.put(337, 120);	//ОДБХ-Хасково
	
		map.put(338, 122);	//ОДБХ-Шумен
		map.put(30, 122);	//РВМС-Шумен
	
		map.put(339, 107);	//ОДБХ-Ямбол
		map.put(35, 107);	//РВМС-Ямбол
		
		
		return map;
	}
	
	
	
	public static HashMap<Integer, Integer> createTehnologyMap() {
		
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer> ();
		
//		интензивно 725
		map.put(725, 4);
		
//		екстензивно 726
		map.put(726, 3);
		
//		волиерно 8667
		map.put(8667, 5);
		
		return map;
		
	}
	
	
	public static HashMap<Integer, Integer> createPrednaznachenieMap() {
		
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer> ();
		

		
//		за репродукция 471
		map.put(471, 7);
		
//		за продукция 1325
		map.put(1325, 4);
		
//		мед 2251
		map.put(2251, 21);
		
//		приплоди 727
		map.put(727, 22);
		
//		месо 728
		map.put(728, 23);
		
//		мляко 729
		map.put(729, 24);
		
//		яйца 2269
		map.put(2269, 25);
		
//		гъши дроб 2520
		map.put(2520, 26);
		
//		вълна 730
		map.put(730, 27);
		
//		отводки 2081
		map.put(2081, 28);
		
//		слуз (охлюви) 2365
		map.put(2365, 29);
		
//		за спорт 473
		map.put(473, 8);
		
//		за компаньон 679
		map.put(679, 1);
		
//		за работа 752
		map.put(752, 9);
		
//		за oпити 681
		map.put(681, 2);
		
//		за друга стопанска дейност 8666
		map.put(8666, 14);
		
//		забавление(атракция) 5167
		map.put(5167, 10);
		
//		Производство на развъдни птици 8155
		map.put(8155, 3);
		
////		Производство на яйца за консумация 8156
//		map.put(8156, 5);
//		
////		Производство на месо птици 8157
//		map.put(8157, 5);
		
//		Възстановяване на запасите на пернат дивеч 8158
		map.put(8158, 13);
		
		return map;
		
	}
	
	
	
	
	public static HashMap<Integer, Integer> createCategoriaMap() {
		
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer> ();
		
		
		
		//Пасище за сезонно настаняване	8081
		map.put(8081, 11);
		
		//Майкопроизводен пчелин	8216
		map.put(8216, 5);
		
		//За друга стопанска дейност	8325
		map.put(8325, 14);
		
		//Рибовъдно стопанство	8406
		map.put(8406, 12);
		
		//Зоокът, други обекти за атракция	8319
		map.put(8319, 37);
		
		//Подвижен (временен) пчелин	8078
		map.put(8078, 6);
		
		//Люпилня	8256
		map.put(8256, 43);
		
		//Пансион/хотел	8317
		map.put(8317, 33);
		
		//Развъдник	8316
		map.put(8316, 32);
		
//		Ферма за ИБС	2227
		map.put(2227, 8);
		
//		Пчелин	8077
		map.put(8077, 5);
		
//		Индустриална ферма	8074
		map.put(8074, 1);
		
//		За развъждане на опитни животни	8321
		map.put(8321, 35);
		
//		Фамилна ферма	8075
		map.put(8075, 3);
				
//		заден двор(лично стопанство)	1326
		map.put(1326, 7);
		
//		Ферма	8076
		map.put(8076, 4);
		
//		Приют	8318
		map.put(8318, 31);
		
//		Зоомагазин	8225
		map.put(8225, 30);
		
//		ЖО-пасище за целогодишно отглеждане	8486
		map.put(8486, 10);
		
//		Животновъден обект за дивеч	8320
		map.put(8320, 15);
		
//		Вивариум към екип за опити	8322
		map.put(8322, 36);
		
//		Състезание с животни	8327
		map.put(8327, 21);
		
//		Животинска изложба	8326
		map.put(8326, 22);
		
//		Пазар за животни	8331
		map.put(8331, 23);
		
//		Събирателен център	8446
		map.put(8446, 38);
		
//		Обект за съхранение на зародишни продукти	8324
		map.put(8324, 42);
		
//		Обект за събиране на зародишни продукти	8323
		map.put(8323, 42);

		
//		8193	Екарисаж/инсенератор стационарен
		map.put(8323, 19);
		
				
////		8082	Кланица
//		map.put(8323, 0);
//		
////		8083	Кланичен пункт
//		map.put(8323, 0);
//		
////		8330	Млекосъбирателен център (МСЦ)
//		map.put(8323, 0);
//		
////		8376	Мобилен кланичен пункт
//		map.put(8323, 0);
//		
////		8328	Трупосъбирателна площадка
//		map.put(8323, 0);

		
		
		return map;
	}
	
	
	
	
	
	
	public static void insertDoc(Doc doc) throws DbErrorException {
		
	
		
		
		String insertSql = "INSERT INTO doc (doc_id, "
				+ "registratura_id, "
				+ "register_id, "
				+ "rn_doc, "
				+ "guid, "
				+ "doc_type, "
				+ "doc_vid, "
				+ "doc_date, "
				+ "code_usluga, "
				+ "otnosno, "
				+ "status, "
				+ "user_reg, "
				+ "date_reg, "
				+ "status_date, "
				+ "licenziant_type, "
				+ "id_licenziant, "
				+ "ime_usluga, "
				+ "plateno, "
				+ "free_access, "
				+ "doc_info, "
				+ "code_ref_corresp, "
				+ "valid, "
				+ "valid_date, "
				+ "pay_type, "
				+ "kachestvo_lice ) "
				+ "         VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, doc.getId());
			query.setParameter(2, doc.getRegistraturaId());
			query.setParameter(3, doc.getRegisterId());
			query.setParameter(4, doc.getRnDoc());
			query.setParameter(5, doc.getGuid());
			query.setParameter(6, doc.getDocType());
			query.setParameter(7, doc.getDocVid());
			query.setParameter(8, doc.getDocDate());
			query.setParameter(9, doc.getCodeAdmUsluga());
			query.setParameter(10, doc.getOtnosno());
			query.setParameter(11, doc.getStatus());
			query.setParameter(12, -1);
			query.setParameter(13, new Date());
			query.setParameter(14, doc.getStatusDate());
			query.setParameter(15, doc.getLicenziantType());
			query.setParameter(16, doc.getIdLicenziant());
			query.setParameter(17, doc.getImeAdnUsluga());
			query.setParameter(18, doc.getIndPlateno());
			query.setParameter(19, doc.getFreeAccess());
			query.setParameter(20, doc.getDocInfo());
			//query.setParameter(21, doc.getCodeRefCorresp());			
			query.setParameter(21, new TypedParameterValue(StandardBasicTypes.INTEGER, doc.getCodeRefCorresp()));
			query.setParameter(22, new TypedParameterValue(StandardBasicTypes.INTEGER, doc.getValid()));
			query.setParameter(23, new TypedParameterValue(StandardBasicTypes.DATE, doc.getValidDate()));
			query.setParameter(24, new TypedParameterValue(StandardBasicTypes.INTEGER, doc.getPayType()));
			query.setParameter(25, new TypedParameterValue(StandardBasicTypes.INTEGER, doc.getKachestvoLice()));
			
			query.executeUpdate();
		} catch (Exception e) {
			LOGGER.error("Грешка при запис на документ ", e);
			throw new DbErrorException("Грешка при запис на документ", e); 
		}
		
		
	}
	
	private static void insertVpisvaneDoc(VpisvaneDoc vdoc) throws DbErrorException {
		String insertSql = "INSERT INTO vpisvane_doc (id, id_vpisvane, id_doc, user_reg, date_reg) VALUES (?, ?, ?, ?, ?)";
		
		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, vdoc.getId());
			query.setParameter(2, vdoc.getIdVpisvane());
			query.setParameter(3, vdoc.getIdDoc());
			query.setParameter(4, vdoc.getUserReg());
			query.setParameter(5, vdoc.getDateReg());
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			LOGGER.error("Грешка при запис на връзки с документ ", e);
			throw new DbErrorException("Грешка при запис на връзки с документ", e); 
		}
	}




	private static void insertObektDeinostLica(ObektDeinostLica odl) throws DbErrorException {
		
		String insertSql = "INSERT INTO obekt_deinost_lica (id, role, date_beg, obekt_deinost_id, code_ref) VALUES (?, ?, ?, ?, ?)";
		
		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, odl.getId());
			query.setParameter(2, odl.getRole());			
			query.setParameter(3, odl.getDateBeg());
			query.setParameter(4, odl.getObektDeinostId());
			query.setParameter(5, odl.getCodeRef());
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			LOGGER.error("Грешка при запис на връзки на обект с лица ", e);
			throw new DbErrorException("Грешка при запис на обект с лица", e); 
		}
	}




	private static void insertReferent(Referent ref) throws DbErrorException {
		
		String insertSql = "INSERT INTO adm_referents (ref_id, code, code_classif, code_parent, code_prev, contact_email, contact_phone, date_reg, user_reg, fzl_lnc, ref_type, fzl_egn, nfl_eik, ime, prezime, familia, ref_name, date_ot, date_do) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, ref.getId());
			query.setParameter(2, ref.getCode());
			query.setParameter(3, ref.getCodeClassif());
			query.setParameter(4, ref.getCodeParent());
			query.setParameter(5, ref.getCodePrev());
			query.setParameter(6, ref.getContactEmail());
			query.setParameter(7, ref.getContactPhone());
			query.setParameter(8, ref.getDateReg());
			query.setParameter(9, ref.getUserReg());
			query.setParameter(10, ref.getFzlLnc());
			query.setParameter(11, ref.getRefType());
			query.setParameter(12, ref.getFzlEgn());
			query.setParameter(13, ref.getNflEik());
			query.setParameter(14, ref.getIme());
			query.setParameter(15, ref.getPrezime());
			query.setParameter(16, ref.getFamilia());
			query.setParameter(17, ref.getRefName());
			query.setParameter(18, ref.getDateOt());
			query.setParameter(19, ref.getDateDo());
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			LOGGER.error("Грешка при запис на референт ", e);
			throw new DbErrorException("Грешка при запис на референт", e); 
		}
	}
	
	private static void insertRefAddress(ReferentAddress address) throws DbErrorException {
		
		String insertSql = "INSERT INTO adm_ref_addrs (addr_id, code_ref, addr_type, addr_country, addr_text, post_code, ekatte, user_reg, date_reg) VALUES (?,?,?,?,?,?,?,?,?)";
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, address.getId());
			query.setParameter(2, address.getCodeRef());
			query.setParameter(3, address.getAddrType());
			query.setParameter(4, address.getAddrCountry());
			query.setParameter(5, address.getAddrText());
			query.setParameter(6, address.getPostCode());
			query.setParameter(7, address.getEkatte());			
			query.setParameter(8, address.getUserReg());
			query.setParameter(9, address.getDateReg());
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на адрес на референт ", e);
			throw new DbErrorException("Грешка при запис на адрес на референт", e); 
		}
		
		
	}




	private static void insertObektDeinost(ObektDeinost oez) throws DbErrorException {
		// 
		
		String insertSql = "INSERT INTO obekt_deinost (id, reg_nom, reg_nomer_star, naimenovanie, vid, vid_oez, darj,  nas_mesto, address, post_code, email, tel, status, status_date, gps_lat, gps_lon, dop_info, user_reg, date_reg) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, oez.getId());
			query.setParameter(2, oez.getRegNom());
			query.setParameter(3, oez.getRegNomerStar());
			query.setParameter(4, oez.getNaimenovanie());
			query.setParameter(5, oez.getVid());
			query.setParameter(6, new TypedParameterValue(StandardBasicTypes.INTEGER, oez.getVidOez()));
			query.setParameter(7, oez.getDarj());						
			query.setParameter(8, oez.getNasMesto());
			query.setParameter(9, oez.getAddress());
			query.setParameter(10, oez.getPostCode());
			query.setParameter(11, oez.getEmail());
			query.setParameter(12, oez.getTel());
			query.setParameter(13, oez.getStatus());
			query.setParameter(14, oez.getStatusDate());
			query.setParameter(15, new TypedParameterValue(StandardBasicTypes.DOUBLE,oez.getGpsLat()));
			query.setParameter(16, new TypedParameterValue(StandardBasicTypes.DOUBLE,oez.getGpsLon()));			
			query.setParameter(17, oez.getDopInfo());
			query.setParameter(18, oez.getUserReg());
			query.setParameter(19, oez.getDateReg());
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на обект на дейност ", e);
			throw new DbErrorException("Грешка при запис на адрес на  обект на дейност", e); 
		}
		
	}




	private static void insertVpisvane(Vpisvane vpisvane) throws DbErrorException {
		
		String insertSql = "INSERT INTO vpisvane (id, id_zaqavlenie, status, date_status, registratura_id, id_register, licenziant_type, id_licenziant, reg_nom_zaqvl_vpisvane, date_zaqvl_vpis, reg_nom_result, date_result, id_result, user_reg, date_reg, licenziant, code_page, code_ref_vpisvane) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		

		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, vpisvane.getId());
			query.setParameter(2, vpisvane.getIdZaqavlenie());
			query.setParameter(3, vpisvane.getStatus());
			query.setParameter(4, vpisvane.getDateStatus());
			query.setParameter(5, vpisvane.getRegistraturaId());
			query.setParameter(6, vpisvane.getIdRegister());
			query.setParameter(7, vpisvane.getLicenziantType());
			query.setParameter(8, vpisvane.getIdLicenziant());
			query.setParameter(9, vpisvane.getRegNomZaqvlVpisvane());
			query.setParameter(10, vpisvane.getDateZaqvlVpis());
			query.setParameter(11, vpisvane.getRegNomResult());
			query.setParameter(12, vpisvane.getDateResult());
			query.setParameter(13, vpisvane.getIdResult());
			query.setParameter(14, vpisvane.getUserReg());
			query.setParameter(15, vpisvane.getDateReg());
			query.setParameter(16, vpisvane.getLicenziant());
			query.setParameter(17, vpisvane.getCodePage());
			//query.setParameter(18, vpisvane.getCodeRefVpisvane());
			
			query.setParameter(18, new TypedParameterValue(StandardBasicTypes.INTEGER, vpisvane.getCodeRefVpisvane()));
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на вписване ", e);
			throw new DbErrorException("Грешка при запис на вписване", e); 
		}
		
	}
	
	private static void insertVpisvaneAccess(VpisvaneAccess va) throws DbErrorException {
		String insertSql = "INSERT INTO vpisvane_access (id, vpisvane_id, code_ref) VALUES (?, ?, ?)";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, va.getId());			
			query.setParameter(2, va.getVpisvaneId());
			query.setParameter(3, va.getCodeRef());
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на адрес на достъп на вписване ", e);
			throw new DbErrorException("Грешка при запис на достъп на вписване", e); 
		}
		
		
	}
	
	
	private static void insertOezHarakt(OezHarakt har) throws DbErrorException {
		String insertSql = "INSERT INTO oez_harakt (id, id_oez, vid_jivotno, prednaznachenie, tehnologia, kapacitet, kapacitet_text) VALUES (?,?,?,?,?,?,?)";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, har.getId());			
			query.setParameter(2, har.getIdOez());
			query.setParameter(3, har.getVidJivotno());
//			query.setParameter(4, har.getPrednaznachenie());
//			query.setParameter(5, har.getTehnologia());
//			query.setParameter(6, har.getKapacitet());
//			query.setParameter(7, har.getKapacitetText());
			
			query.setParameter(4, new TypedParameterValue(StandardBasicTypes.INTEGER, har.getPrednaznachenie()));
			query.setParameter(5, new TypedParameterValue(StandardBasicTypes.INTEGER, har.getTehnologia()));
			query.setParameter(6, new TypedParameterValue(StandardBasicTypes.INTEGER, har.getKapacitet()));
			query.setParameter(7, new TypedParameterValue(StandardBasicTypes.STRING, har.getKapacitetText()));
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на характеристики на оез ", e);
			throw new DbErrorException("Грешка при запис на характеристики на оез", e); 
		}
		
	}
	
	
	private static void insertObektDeinostDeinost(ObektDeinostDeinost odd) throws DbErrorException {
		String insertSql = "INSERT INTO obekt_deinost_deinost (id, obekt_deinost_id, tabl_event_deinost, deinost_id, date_beg) VALUES (?, ?, ?, ?, ?)";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, odd.getId());			
			query.setParameter(2, odd.getObektDeinostId());
			query.setParameter(3, odd.getTablEventDeinost());
			query.setParameter(4, odd.getDeinostId());
			query.setParameter(5, odd.getDateBeg());

			
			
			//query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на ообект дейност дейност ", e);
			throw new DbErrorException("Грешка при запис на ообект дейност дейност", e); 
		}
		
	}
	
	
	
	private static void insertVpisvaneStatus(VpisvaneStatus vs) throws DbErrorException {
		String insertSql = "INSERT INTO vpisvane_status (id, id_vpisvane, status, date_status, user_reg, date_reg) VALUES (?,?,?,?,?,?);";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, vs.getId());			
			query.setParameter(2, vs.getIdVpisvane());
			query.setParameter(3, vs.getStatus());
			query.setParameter(4, vs.getDateStatus());
			query.setParameter(5, vs.getUserReg());
			query.setParameter(6, vs.getDateReg());

			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на обект статус на вписване ", e);
			throw new DbErrorException("Грешка при запис на обект статус на вписване", e); 
		}
		
	}

}


