package com.ib.babhregs.migr.vmp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.ib.babhregs.db.dao.VlpDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.Substance;
import com.ib.babhregs.db.dto.Vlp;
import com.ib.babhregs.db.dto.VlpFarmForm;
import com.ib.babhregs.db.dto.VlpPrilagane;
import com.ib.babhregs.db.dto.VlpPrilaganeVid;
import com.ib.babhregs.db.dto.VlpVeshtva;
import com.ib.babhregs.db.dto.VlpVidJiv;
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
import com.ib.system.db.dto.SystemTranscode;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;

public class ParseVMP {

	public static int seqVal = 100000;
	
	public static boolean isTest = false;
	
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		//TreeSet<String> animalsSet = new TreeSet<String>();
		
		
		
		
		SystemData sd = new SystemData();
		
		try {
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			
			
			JPA.getUtil().begin();
			
			List<Object[]> rows = JPA.getUtil().getEntityManager().createNativeQuery("SELECT id, vanshna_opakovka, naim, nom_licens, dat_licens, vid_licens, srok_valid, status_licens, atc, nom_stan, dat_stan, subs, kolichestvo, eksp, forma, nachin_pril, opakovka, srok_godnost, rejim, vid_jiv, karenten_srok, proizvoditel, adres_proizvoditel, oop, adres_oop, pritel, adres_pritejatel, pic, subs_codes FROM temp_vmp").getResultList();
			
			List<Object[]> tempFormi = JPA.getUtil().getEntityManager().createNativeQuery("SELECT tekst, code from temp_precode_formi").getResultList();
			
			List<Object[]> tempNachini = JPA.getUtil().getEntityManager().createNativeQuery("SELECT tekst, iisr_code from temp_precode_nachin").getResultList();
			
			List<Object[]> tempOpakovka = JPA.getUtil().getEntityManager().createNativeQuery("SELECT tekst, iisr_code from temp_precode_opakovka").getResultList();
			 
			List<Substance> tempSubs = JPA.getUtil().getEntityManager().createQuery("from Substance").getResultList();
			
			
			HashMap<String, String> formiMap = new HashMap<String, String>();
			for (Object[] o : tempFormi) {
				formiMap.put(SearchUtils.asString(o[0]), SearchUtils.asString(o[1]));
			}
			
			HashMap<String, Integer> nachiniMap = new HashMap<String, Integer>();
			for (Object[] o : tempNachini) {
				nachiniMap.put(SearchUtils.asString(o[0]), SearchUtils.asInteger(o[1]));
			}
			
			HashMap<String, Integer> opakovkaMap = new HashMap<String, Integer>();
			for (Object[] o : tempOpakovka) {
				opakovkaMap.put(SearchUtils.asString(o[0]), SearchUtils.asInteger(o[1]));
			}
			
			HashMap<String, Substance> subsaMap = new HashMap<String, Substance>();
			for (Substance o : tempSubs) {
				subsaMap.put(o.getIdentifier(), o);
			}
			
			Integer cnt = 1;
			for (Object[] row : rows) {
				
				Vlp vlp = new Vlp();
				vlp.setVlpVidJiv(new ArrayList<VlpVidJiv>());
				vlp.setOpakovkaList(new ArrayList<Integer>());
				vlp.setFarmFormList(new ArrayList<Integer>());
				vlp.setVlpVeshtva(new ArrayList<VlpVeshtva>());
				vlp.setVlpPrilagane(new ArrayList<VlpPrilagane>());
				
				
				
				Integer id = SearchUtils.asInteger(row[0]);
				
//				if (id == 194 ) {
//					System.out.println("BUM!");
//				}
				
				
				///////////////////////// ИМЕ /////////////////////////////////////////////////////////
				
				String naim1= null;
				String naim2= null;
				
				String naim = tempFix(SearchUtils.asString(row[2]));
				
				//System.out.println(cnt + ". Обработка на " +naim) ;
				cnt++;
				
				String[] parts = naim.split("/");
				//System.out.println(parts.length);
				
				if (parts.length == 2) {
					//System.out.println(parts[0] + "\t" + parts[1]);
					
					naim1 = autCorrect(parts[0]);
					naim2 = autCorrect(parts[1]);
					
					boolean b1 = hasCyr(naim1);
					boolean b2 = hasCyr(naim2);
					
					
					if (!b1 && !b2) {
						System.out.println("Няма кирилица :   " + naim1 + "\t" + naim2);
						continue;
					}
					
					if (b1 && b2) {
						System.out.println("Има много кирилица :   " + naim1 + "\t" + naim1);
						continue;
					}
					
					if (b1) {
						vlp.setNaimenovanieCyr(naim1);
						vlp.setNaimenovanieLat(naim2);
					}else {
						vlp.setNaimenovanieCyr(naim2);
						vlp.setNaimenovanieLat(naim1);
					}
					
					
					
				}
				
				if (parts.length == 1) {					
					naim = autCorrect(naim);					
					vlp.setNaimenovanieCyr(naim);
					vlp.setNaimenovanieLat(naim);					
					//System.out.println(naim);
				}
				
				if (parts.length > 2) {
					
					naim = autCorrect(naim);
					System.out.println("WARINING !!!!!!!" + naim);
					continue;
				}
				
				///////////////////////// Лиценз /////////////////////////////////////////////////////////
				
				String certNom = SearchUtils.asString(row[3]);
				
				
				Date certDat = DateUtils.systemMinDate();
				
				try {
					
					String cdat = SearchUtils.asString(row[4]);
					if (cdat != null) {
						certDat = sdf.parse(cdat);
					}else {
						System.out.println("За ВМП с id " + id + " липсва дата на сертификата " );
						continue;
					}
				} catch (Exception e) {
					System.out.println("За ВМП с id " + id + " датата сертификата не е в правилен формат !!!!" + row[4]);
					continue;
				}
				
				
//				JPA.getUtil().getEntityManager().createNativeQuery("update temp_invitro set cert_nom = ?, cert_dat = ? where temp_invitro.id = ?")
//				.setParameter(1, certNom)
//				.setParameter(2, certDat)
//				.setParameter(3, id).executeUpdate();
				
				
				///////////////////////// Вид Лиценз  /////////////////////////////////////////////////////////
				
				String vidLic = SearchUtils.asString(row[5]);
				if (vidLic == null) {
					//System.out.println("За ВМП с id " + id + " вида на лиценза е " + row[5]);
					vidLic = "безсрочно";
				}
				
				
				//На 13.12 доктор Новакова потвърди, че вече няма срочни лицензи и срок на валидност. Ако е валиден, той е безсрочен !!!!!!!!!!!!!!!!!!!!!!!!!!
				
//				Date datLicTo = null;
//				if (vidLic != null && vidLic.equalsIgnoreCase("срочно")) {
//					try {
//						datLicTo = sdf.parse(SearchUtils.asString(row[6]).trim());
//					} catch (Exception e) {
//						System.out.println("За ВМП с id " + id + " датата на срочния лиценз не е в правилен формат !!!!" + row[6]);
//					}
//				}
//				
//				
//				///////////////////////// Статус  /////////////////////////////////////////////////////////
//				String stat = SearchUtils.asString(row[7]);
//				if (stat == null) {
//					System.out.println("За ВМП с id " + id + " статуса е " + row[5]);
//				}else {
//					//allStat.add(stat);
//				}
				
				///////////////////////// ATC  /////////////////////////////////////////////////////////
				String atc = SearchUtils.asString(row[8]);
				if (atc != null && atc.trim().equalsIgnoreCase("N/A")) {
					atc = null;
				}
				
				vlp.setVetMedCode(atc);
				
				
				///////////////////////// Номер на становище  /////////////////////////////////////////////////////////
								
				String nomStan = SearchUtils.asString(row[9]);
				String datStanS = SearchUtils.asString(row[10]);
				Date datStan = null;
				
				if (datStanS != null && ! datStanS.trim().equalsIgnoreCase("N/A")) {
					try {
						datStan = sdf.parse(datStanS);
					} catch (Exception e) {
						System.out.println("За ВМП с id " + id + " ***** датата на становище не е в правилен формат !!!!" + datStanS);
					}
					nomStan = nomStan + " / " + datStanS;
					
				}
				
				vlp.setProcNumber(nomStan);
				
				
				
				 
				String animalsString = SearchUtils.asString(row[19]);
				if (animalsString == null) {					 
					
					//System.out.println("За ВМП с id " + id + " статуса е " + row[18]);
				}else {
					
					String[] partsA = null;
					if (animalsString.contains(",")) {
						partsA = animalsString.split(",");
					}else {
						partsA = animalsString.split(";");
					}
					
					for (String s : partsA)	{
						if (! SearchUtils.isEmpty(s.trim())) {
							
							Integer code = null;
							
							List<SystemTranscode> codes = sd.transcodeByCodeExt(677, s.trim(), 3);
							if (codes.size() > 0) {
								code = codes.get(0).getCodeNative();								
							}else {
								System.out.println("За ВМП с id " + id + " ***** не може да бъде разпознато животно с име: " + s);
								continue;
							}
							
							VlpVidJiv jiv = new VlpVidJiv();
							jiv.setVidJiv(code);
							vlp.getVlpVidJiv().add(jiv);
						}
					}
				}
				
				
				String forma = SearchUtils.asString(row[14]);
				 
				String fCodes = null;
				if (forma != null && ! SearchUtils.isEmpty(forma)) {
					forma = forma.trim().toUpperCase();
					
					fCodes = formiMap.get(forma);
					
					if (fCodes == null) {
						System.out.println("Не се намира фармацевтичната форма " + forma);						
					}else {
						
						String[] splited = StringUtils.split(fCodes, "|");
						for (String tekCode : splited) {
							List<SystemClassif> items = sd.getItemsByCodeExt(693, tekCode, id, new Date());
							if (items.size() == 0) {
								System.out.println("Не се намира външен код на форма - " + tekCode);
								continue;
							}else {
								//formi.add(items.get(0).getCode());															
								vlp.getFarmFormList().add(items.get(0).getCode());
							}
						}
					}
				}
				
				
				
				
				ArrayList<Integer> nachini = new ArrayList<Integer>(); 
				String nachinString = SearchUtils.asString(row[15]);
				String[] splitN = nachinString.split(",");
				
				for (String temp : splitN) {
					String[] ts = temp.split(";");
					for (String s : ts) {

						Integer code = nachiniMap.get(s.trim().toUpperCase());
						if (code == null) {
							//System.out.println("За ВМП с id " + id + " ***** не може да бъде разпознато начин на приложение: " + s);
							continue;
						}else {
							nachini.add(code);
						}
					}
				}
				
				for (Integer nachin : nachini) {
					VlpPrilagane pril = new VlpPrilagane();
					pril.setCodeNachin(nachin);
					pril.setVlpPrilaganeVid(new ArrayList<VlpPrilaganeVid>());
					
					for (VlpVidJiv vidJ : vlp.getVlpVidJiv()) {
						VlpPrilaganeVid pv = new  VlpPrilaganeVid();
						pv.setVid(vidJ.getVidJiv());
						pril.getVlpPrilaganeVid().add(pv);
					}
					vlp.getVlpPrilagane().add(pril);
					
				}
				
				
				String opakovka = SearchUtils.asString(row[16]);				
				
				String[] opArr = opakovka.split(",");
				for (String temp : opArr) {
					String[] ts = temp.split(";");
					for (String s : ts) {

						Integer code = opakovkaMap.get(s.trim().toUpperCase());
						if (code == null) {
							System.out.println("За ВМП с id " + id + " ***** не може да бъде опаковка: " + s);
						}else {
							//opakovki.add(code);
							vlp.getOpakovkaList().add(code);
						}
					}
				}
				
				String srokGodnost = SearchUtils.asString(row[17]);
				vlp.setSrok(srokGodnost);
				
				
				//По лекарско предписание				
				Integer rejimCode = 2;
				String rejim = SearchUtils.asString(row[18]);
				if (rejim.equals("Без лекарско предписание")) {
					rejimCode = 1;
				}
				
				vlp.setRejimOtpuskane(rejimCode);
				
				
				
				String karentenSrok = SearchUtils.asString(row[20]);
				vlp.setKarentenSrok(karentenSrok);
				
				
				String nameProizvoditel = SearchUtils.asString(row[21]);	
				String adresProizvoditel = SearchUtils.asString(row[22]);
				
				Referent proizvoditel = createReferent(nameProizvoditel, adresProizvoditel);
				
				
				String nameОop = SearchUtils.asString(row[23]);	
				String adresOop = SearchUtils.asString(row[24]);
				
				Referent oop = createReferent(nameОop, adresOop);
				
				String namePritejatel = SearchUtils.asString(row[25]);	
				String adresPritejatel = SearchUtils.asString(row[26]);
				
				Referent pritejatel = createReferent(namePritejatel, adresPritejatel);
				
				
				String subsCodes = SearchUtils.asString(row[28]);
				if (subsCodes != null) {
					String[] sCodes = subsCodes.split("\\|");
					
					String kolichestvo = SearchUtils.asString(row[12]);
					
					if (kolichestvo != null ) {
						
						kolichestvo = kolichestvo.replace("g,", "g;");
						kolichestvo = kolichestvo.replace("ml,", "ml;");
						
						kolichestvo = kolichestvo.replace("таблетка,", "таблетка;");
						kolichestvo = kolichestvo.replace("пипета,", "пипета;");
						kolichestvo = kolichestvo.replace("IU,", "IU;");
						
						kolichestvo = kolichestvo.replace("болус,", "болус;");
						kolichestvo = kolichestvo.replace("mlq ", "ml; ");
						kolichestvo = kolichestvo.replace("EAb,", "EAb;");
						kolichestvo = kolichestvo.replace("units,", "units;");
						kolichestvo = kolichestvo.replace("доза,", "доза;");
						
						
						kolichestvo = kolichestvo.replace("dose,", "dose;");
						kolichestvo = kolichestvo.replace("ELISA Units,", "ELISA Units;");
						kolichestvo = kolichestvo.replace("log2,", "log2;");
						kolichestvo = kolichestvo.replace("таблетки,", "таблетки;");
						
						
						
						String[] kCodes = kolichestvo.split(";");
						
						if (sCodes.length == 1) {
							VlpVeshtva av = new VlpVeshtva();
							
							Substance sub = subsaMap.get(sCodes[0]);
							
							if (sub == null) {
								System.out.println("За ВМП с id " + id + " ***** не може да бъде намерен код на субстанция: " + sCodes[0]);
								continue;
							}
							
							
							av.setType(1); //активно
							av.setVidIdentifier(sub);
							av.setQuantity(kolichestvo);							
							
							vlp.getVlpVeshtva().add(av );
							
						}else {
							if (kCodes.length != sCodes.length) {								
								//System.out.println(sCodes.length + "|" + kCodes.length + "|\t" + subsCodes + "\t" + kolichestvo);
							}
							for (int i = 0; i < sCodes.length; i++) {
								String sCode = sCodes[i];
								
								String kCode = null;
								if (kCodes.length > i) {
									kCode = kCodes[i];
								}
								
								VlpVeshtva av = new VlpVeshtva();
								
								Substance sub = subsaMap.get(sCode);
								
								if (sub == null) {
									System.out.println("За ВМП с id " + id + " ***** не може да бъде намерен код на субстанция: " + sCodes[0]);
									continue;
								}
								
								
								av.setType(1); //активно
								av.setVidIdentifier(sub);
								av.setQuantity(kCode);							
								
								vlp.getVlpVeshtva().add(av );
							}
								
							
						}
					}
					
					
				}
				
				
				
				
				
				
								
				ArrayList<VpisvaneDoc> vdocList = new ArrayList<VpisvaneDoc>();
				ArrayList<VpisvaneAccess> allVAccess = new ArrayList<VpisvaneAccess>();
				
				
				

				
				
				
				
				
				
				
				Doc doc = createZaiavlene(naim, certNom, DateUtils.systemMinDate(), pritejatel);				
				Doc docU = createUdostoverenie(naim, certNom, certDat, pritejatel);
				
				Vpisvane vpisvane = createVpisvane(doc, docU, pritejatel);
				vlp.setIdVpisvane(vpisvane.getId());
				
				VpisvaneStatus vs = createVpisvaneStatus(vpisvane.getId(), docU.getDocDate());
				
				VpisvaneDoc vdoc = new VpisvaneDoc();
				vdoc.setId(InvitroMigrate.getSeq("seq_vpisvane_doc"));
				vdoc.setIdDoc(doc.getId());
				vdoc.setIdVpisvane(vpisvane.getId());				
				vdoc.setDateReg(new Date());
				vdoc.setUserReg(-1);
				vdocList.add(vdoc);
				
				VpisvaneDoc vdocU = new VpisvaneDoc();
				vdocU.setId(InvitroMigrate.getSeq("seq_vpisvane_doc"));
				vdocU.setIdDoc(docU.getId());
				vdocU.setIdVpisvane(vpisvane.getId());				
				vdocU.setDateReg(new Date());
				vdocU.setUserReg(-1);
				vdocList.add(vdocU);
				
				for (Integer codeRef : InvitroMigrate.access) {
					
					
					
					VpisvaneAccess va = new VpisvaneAccess();
					
					va.setId(InvitroMigrate.getSeq("SEQ_VPISVANE_ACCESS"));
					va.setCodeRef(codeRef);
					va.setVpisvaneId(vpisvane.getId());
					va.setUserReg(-1);
					va.setDateReg(new Date());
					allVAccess.add(va); 
					
				}
				
				
				InvitroMigrate.insertDoc(doc);
				InvitroMigrate.insertDoc(docU);
				InvitroMigrate.insertVpisvane(vpisvane);
				InvitroMigrate.insertVpisvaneStatus(vs);	
				
				//Притежател
				InvitroMigrate.insertReferent(pritejatel);
				if (pritejatel.getAddressKoresp() != null) {
					InvitroMigrate.insertRefAddress(pritejatel.getAddressKoresp());
				}
				
				//Производител
				InvitroMigrate.insertReferent(proizvoditel);
				if (proizvoditel.getAddressKoresp() != null) {
					InvitroMigrate.insertRefAddress(proizvoditel.getAddressKoresp());
				}
				
				//oop
				InvitroMigrate.insertReferent(oop);
				if (oop.getAddressKoresp() != null) {
					InvitroMigrate.insertRefAddress(oop.getAddressKoresp());
				}
				
				
				for (VpisvaneDoc vd : vdocList) {
					InvitroMigrate.insertVpisvaneDoc(vd);
				}
				
				for (VpisvaneAccess va : allVAccess ) {
					InvitroMigrate.insertVpisvaneAccess(va);
				}
				
				new VlpDAO(ActiveUser.DEFAULT).save(vlp);
				
				
			}
			
			
			
			
				
			JPA.getUtil().commit();
			
			
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JPA.getUtil().rollback();
		}finally {
			JPA.getUtil().closeConnection();
		}
		
		
//		try {
//			for (String s : animalsSet) {
//				
//				String decode = null;
//				List<SystemTranscode> codes = sd.transcodeByCodeExt(677, s, 3);
//				if (codes.size() > 0) {
//					Integer code = codes.get(0).getCodeNative();
//					decode = sd.decodeItem(677, code, 1, new Date());
//				}else {
//					//System.out.println(s + "\t" + decode);
//				}
//				
//				
//			}
//		} catch (DbErrorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		

	}
	
	
	


	private static VpisvaneStatus createVpisvaneStatus(Integer idVpisvane, Date datStatus) throws DbErrorException {
		
		VpisvaneStatus vs = new VpisvaneStatus();
				
		vs.setId(InvitroMigrate.getSeq("seq_vpisvane_status"));
		vs.setIdVpisvane(idVpisvane);
		vs.setStatus(BabhConstants.STATUS_VP_VPISAN);
		vs.setDateStatus(datStatus);
		vs.setUserReg(-1);
		vs.setDateReg(new Date());
		
		return vs;
	}





	private static Vpisvane createVpisvane(Doc doc, Doc docU, Referent pritejatel) throws DbErrorException {
		
		Vpisvane vpisvane = new Vpisvane();
		
		int idVpisvane = InvitroMigrate.getSeq("seq_vpisvane");
		
		vpisvane.setId(idVpisvane);
		vpisvane.setIdZaqavlenie(doc.getId());
		vpisvane.setStatus(BabhConstants.STATUS_VP_VPISAN);
		vpisvane.setDateStatus(docU.getDocDate());
		vpisvane.setRegistraturaId(doc.getRegistraturaId());
		vpisvane.setIdRegister(doc.getRegisterId());
		vpisvane.setLicenziantType(doc.getLicenziantType());
		vpisvane.setIdLicenziant(pritejatel.getCode());
		vpisvane.setCodeRefVpisvane(pritejatel.getCode());
		vpisvane.setLicenziant(pritejatel.getRefName());
		
		vpisvane.setRegNomZaqvlVpisvane(doc.getRnDoc());
		vpisvane.setDateZaqvlVpis(doc.getDocDate());
		vpisvane.setRegNomResult(docU.getRnDoc());
		vpisvane.setDateResult(docU.getDocDate());
		vpisvane.setIdResult(docU.getId());
		vpisvane.setUserReg(-1);
		vpisvane.setDateReg(new Date());
		
		vpisvane.setCodePage(6);
		vpisvane.setFromМigr(1);
		
		return vpisvane;
	}





	private static Doc createZaiavlene(String naim, String rnDoc, Date datDoc, Referent pritejatel) throws DbErrorException {
		Doc doc = new Doc();
		
		int idDoc = InvitroMigrate.getSeq("SEQ_DOC");
		doc.setId(idDoc);
		
		
		doc.setDocType(1); //Входящ
		doc.setDocVid(26); //Заявление за издаване на сертификат за регистрация на инвитро диагностични средства
		
		if (! SearchUtils.isEmpty(rnDoc)) {
			doc.setRnDoc("Z"+ rnDoc);
		}
		
		doc.setDocDate(DateUtils.systemMinDate());
		
		
		doc.setRegistraturaId(139);
		
		String anot = "Заявление за издаване на разрешение за търговия с ВЛП за " + naim ;
			
		doc.setOtnosno(anot);
		doc.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
		doc.setImeAdnUsluga("Издаване на разрешение за търговия с ВЛП");
		doc.setCodeAdmUsluga("704");
		doc.setRegisterId(25);  
		doc.setLicenziantType(2);
		doc.setIdLicenziant(pritejatel.getCode());
		doc.setStatus(15);
		doc.setStatusDate(new Date());
		doc.setGuid("{"+java.util.UUID.randomUUID().toString()+"}");
		doc.setFreeAccess(2);
		doc.setDocInfo("Записът е създаден от транформация!");
		doc.setUserReg(-1);
		doc.setDateReg(new Date());
		
		
		
		return doc;
	}

	private static Doc createUdostoverenie(String naim, String rnDoc, Date datDoc, Referent pritejatel) throws DbErrorException {
		Doc docU = new Doc();
		
		int idDoc = InvitroMigrate.getSeq("SEQ_DOC");
		docU.setId(idDoc);
		
		
		
		docU.setDocType(2); //Изходящ
		docU.setDocVid(246); //Разрешение за търговия с ВЛП
		
		docU.setRnDoc(rnDoc);
		
		
		docU.setDocDate(datDoc);
		
		docU.setRegistraturaId(139);
		
		String anot = "Разрешение за търговия с ВЛП за " + pritejatel.getRefName();
				
		docU.setOtnosno(anot);
		docU.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
		docU.setImeAdnUsluga("Издаване на разрешение за търговия с ВЛП");
		docU.setCodeAdmUsluga("704");
		docU.setRegisterId(4);  
		docU.setLicenziantType(2);
		docU.setIdLicenziant(pritejatel.getCode());
		docU.setStatus(15);
		
		if (datDoc == null) {
			docU.setStatusDate(DateUtils.systemMinDate());
		}else {
			docU.setStatusDate(datDoc);
		}
		
		
		docU.setGuid("{"+java.util.UUID.randomUUID().toString()+"}");
		docU.setFreeAccess(2);
		docU.setDocInfo("Записът е създаден от транформация!");
		docU.setUserReg(-1);
		docU.setDateReg(new Date());
		
		
		
		return docU;
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


	private static boolean hasCyr(String naim) {
		
		if (naim == null) {
			return false;
		}
		
		String cyr = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЧЦШЩЪЬЮЯ";
		
		for (int i = 0; i < naim.length(); i++) {
			
			if (cyr.contains(naim.substring(i, i+1).toUpperCase())) {
				return true;
			}
			
		}
		
		
		return false;
		
	}
	
	private static String autCorrect(String tekst) {
		
		if (tekst == null) {
			return null;
		}
		
		tekst = tekst.replace("PЕТPASTE", "PETPASTE");
		tekst = tekst.replace("ЕU", "EU");
		tekst = tekst.replace("ОRAL", "ORAL");
		tekst = tekst.replace("рharma", "pharma");
		tekst = tekst.replace("рharma".toUpperCase(), "pharma".toUpperCase());
		tekst = tekst.replace("MASTIQUIN А syringae ", "MASTIQUIN A syringae ");
		
		
		
		tekst = tekst.replace("|", "/");
		
		
		
		
		return tekst;
		
	}
	
	private static String tempFix(String tekst) {
		
		if (tekst == null) {
			return null;
		}
		
		//Тук махаме разделители
		
		
		tekst = tekst.replace("mg/ml/125", "mg|ml|125");
		
		tekst = tekst.replace("ADVANTIX Spot-on solution for dogs up to 4 kg / АДВАНТИКС спот-он разтвор за кучета до 4 kg / ADVATIX Spot-on solution for dogs over 4 up to 10 kg / АДВАНТИКС спот-он разтвор за кучета над 4 до 10 kg / за цялото име на ВМП виж SPC", "");
		tekst = tekst.replace("VETMEDIN 1,25 mg таблетки за дъвчене за кучета / VETMEDIN 1.25 mg chewable tablets for dogs;VETMEDIN 2,5 mg таблетки за дъвчене за кучета / VETMEDIN 2,5 mg chewable tablets for dogs; VETMEDIN 5 mg таблетки за дъвчене за кучета / VETMEDIN 5 mg chewable for", "VETMEDIN 1,25 mg таблетки за дъвчене за кучета | VETMEDIN 2,5 mg таблетки за дъвчене за кучета | VETMEDIN 5 mg таблетки за дъвчене за кучета / VETMEDIN 1.25 mg chewable tablets for dogs  | VETMEDIN 2,5 mg chewable tablets for dogs | VETMEDIN 5 mg chewable for");
		tekst = tekst.replace("ATAXXA 2000 mg / 400 mg spot-on solution for dogs over 25 kg / АТАКСА 2000 mg / 400 mg спот он разтвор за кучета с телесна маса над 25 k", "ATAXXA 2000 mg | 400 mg spot-on solution for dogs over 25 kg / АТАКСА 2000 mg | 400 mg спот он разтвор за кучета с телесна маса над 25 kg");
		tekst = tekst.replace("NEOIVEN 500 000 UI/g прах за прилагане във вода за пиене/ млекозаместител / NEOIVEN 500 000 UI/g powder for use in drinking water/milk replacer", "NEOIVEN 500 000 UI|g прах за прилагане във вода за пиене| млекозаместител / NEOIVEN 500 000 UI|g powder for use in drinking water|milk replacer");
		tekst = tekst.replace("OXYTETRACYCLIN hydrochloride-NGP 7/ 0,340 g, 11/ 0,510 g, 22/ 1,020 g comprettae spumescentes / ОКСИТЕТРАЦИКЛИН хидрохлорид-NGP 7/ 0,340 g, 11/ 0,510 g, 22/ 1,020 g пенообразуващи компрети", "OXYTETRACYCLIN hydrochloride-NGP 7| 0,340 g, 11| 0,510 g, 22| 1,020 g comprettae spumescentes / ОКСИТЕТРАЦИКЛИН хидрохлорид-NGP 7| 0,340 g, 11| 0,510 g, 22| 1,020 g пенообразуващи компрети");		
		tekst = tekst.replace("KENOCIDIN spray and dip (Кеноцидин), Chlorhexidine digluconate 5 mg/ml, разтвор за потапяне на папила на млечна жлеза на говеда (млекодайни крави) / KENOCIDIN spray and dip Chlorhexidine digluconate 5 mg/ml, teat fip/teat spray solution for cattle (dairy)", "");		
		tekst = tekst.replace("MILBEPRAZIN 2.5 mg/25.0 mg / MILBEPRAZIN 12.5 mg/125.0 mg / МИЛБЕПРАЗИН 2.5 mg/25.0 mg / МИЛБЕПРАЗИН 12.5 mg/125.0 mg", "MILBEPRAZIN 2.5 mg|25.0 mg | MILBEPRAZIN 12.5 mg|125.0 mg / МИЛБЕПРАЗИН 2.5 mg|25.0 mg | МИЛБЕПРАЗИН 12.5 mg|125.0 mg");
		tekst = tekst.replace("KILTIX за малки кучета / KILTIX for small dogs; KILTIX за средно големи кучета / KILTIX for medium-sized dogs; KILTIX за големи кучета / KILTIX for large dogs", "KILTIX за малки кучета | KILTIX за средно големи кучета | KILTIX за големи кучета / KILTIX for small dogs  | KILTIX for medium-sized dogs;  | KILTIX for large dogs");
		tekst = tekst.replace("UBROSTAR Dry Cow 100 mg / 280 mg / 100 mg, интрамамарна суспензия за крави / UBROSTAR Dry Cow 100 mg / 280 mg / 100 mg, intramammary suspension for cattle", "UBROSTAR Dry Cow 100 mg | 280 mg | 100 mg, интрамамарна суспензия за крави / UBROSTAR Dry Cow 100 mg | 280 mg | 100 mg, intramammary suspension for cattle");
		tekst = tekst.replace("MARFLOXIN 5 mg таблетки за кучета и котки / MARFLOXIN 5 mg tablets for cats and dogs; MARFLOXIN 20 mg таблетки за кучета / MARFLOXIN 20 mg tablets for dogs; MARFLOXIN 80 mg таблетки за кучета / MARFLOXIN 80 mg tablets for dogs", "MARFLOXIN 5 mg таблетки за кучета и котки | MARFLOXIN 20 mg таблетки за кучета | MARFLOXIN 80 mg таблетки за кучета / MARFLOXIN 5 mg tablets for cats and dogs  | MARFLOXIN 20 mg tablets for dogs | MARFLOXIN 80 mg tablets for dogs ");
		tekst = tekst.replace("MASTIPLAN LC, 300 mg/20 mg (Cefapirin/Prednisolone), интрамамарна суспензия за лактиращи крави / MASTIPLAN LC, 300mg/20mg (Cefapirin/Prednisolone), intramammary suspension for lactating cows", "MASTIPLAN LC, 300 mg|20 mg (Cefapirin|Prednisolone), интрамамарна суспензия за лактиращи крави / MASTIPLAN LC, 300mg|20mg (Cefapirin|Prednisolone), intramammary suspension for lactating cows");
		
		tekst = tekst.replace("PEN/STREP BG 20/20 Inj. / ПЕН/СТРЕП БГ 20/20 Инж.", "PEN|STREP BG 20|20 Inj. / ПЕН|СТРЕП БГ 20|20 Инж.");
		
		tekst = tekst.replace("teat fip/teat", "teat fip|teat");
		tekst = tekst.replace("mg/ml", "mg|ml");
		tekst = tekst.replace(" mg/g", "mg|g");
		tekst = tekst.replace("пиене/млекозаметител", "пиене|млекозаметител");
		tekst = tekst.replace("IU/ml", "IU|ml");
		tekst = tekst.replace("HIPRAGUMBORO CH / 80", "HIPRAGUMBORO CH | 80");
		tekst = tekst.replace("NOBILIS MG 6/85", "NOBILIS MG 6|85");
		
		tekst = tekst.replace("g/kg", "g|kg");
		tekst = tekst.replace("mg/", "mg|");
		tekst = tekst.replace("IU /", "IU |");
		tekst = tekst.replace("w/w", "w|w");
		tekst = tekst.replace("micrograms/", "micrograms|");
		
		tekst = tekst.replace("230/20", "230|20");
		tekst = tekst.replace("water/ milk", "water| milk");
		tekst = tekst.replace("water/milk", "water|milk");
		tekst = tekst.replace("300/16.5", "300|16.5");
		tekst = tekst.replace("пиене/мляко", "пиене|мляко");
		tekst = tekst.replace("IU/g", "IU|g");
		tekst = tekst.replace("", "");
		
		tekst = tekst.replace("20/20", "20|20");
		tekst = tekst.replace("mg / MILBEPRAZIN", "mg | MILBEPRAZIN");
		tekst = tekst.replace("Parvo/E-Amphigen", "Parvo|E-Amphigen");
		tekst = tekst.replace("Coli / C", "Coli | C");
		tekst = tekst.replace("IU/", "IU|");
		tekst = tekst.replace("MG/ML", "MG|ML");
		tekst = tekst.replace("mg/ml", "mg/ml");
		
		tekst = tekst.replace("µg/", "µg|");
		tekst = tekst.replace("мг/мл", "мг|мл");
		tekst = tekst.replace("5/10", "5|10");
		tekst = tekst.replace("1250 mg / 250 mg", "1250 mg | 250 mg");
		tekst = tekst.replace("500 mg / 100 mg", "500 mg | 100 mg");
		tekst = tekst.replace("CANIGEN DHPPi/L", "CANIGEN DHPPi|L");
		tekst = tekst.replace("330 mg / 100 mg", "330 mg | 100 mg");
		tekst = tekst.replace("mg/", "mg|");
		tekst = tekst.replace("80/20 mg", "80|20 mg");
		tekst = tekst.replace("g/100", "g|100");
		tekst = tekst.replace("150/144/50", "150/144/50");
		
		
		tekst = tekst.replace("injection/infusion", "injection|infusion");
		tekst = tekst.replace("инжекционна/инфузионна", "инжекционна|инфузионна");
		tekst = tekst.replace("µg /", "µg |");
		tekst = tekst.replace("microgram/ml", "microgram|ml");
		tekst = tekst.replace("пиене/млекозаместител", "пиене|млекозаместител");
		tekst = tekst.replace("пиене/ млекозаместител", "пиене| млекозаместител");
		tekst = tekst.replace("20/20", "20|20");
		tekst = tekst.replace("150/144/50", "150|144|50");
		
		
		
		
		
		
		
		
		
		
		
		// Тук слагаме разделители
		tekst = tekst.replace("прасета INVEMOX Premix", "прасета / INVEMOX Premix");
		tekst = tekst.replace("for dogs ЦИКЛАВАНС", "for dogs / ЦИКЛАВАНС");
		
		
		return tekst;
		
	}
	
	private static Referent createReferent(String nameRef, String addrRef) throws DbErrorException {
				
		Referent ref = null;
		
		Integer idReferent = InvitroMigrate.getSeq("SEQ_ADM_REFERENTS");
		Integer codeReferent = InvitroMigrate.getSeq("SEQ_ADM_REFERENTS_CODE");
		Integer idAdress = InvitroMigrate.getSeq("SEQ_ADM_REF_ADDRS");
		
		
		
			ref = new Referent();
			ReferentAddress ra = new ReferentAddress();
			
			ref.setId(idReferent);
			ref.setCode(codeReferent);	
			ref.setCodePrev(0);
			ref.setCodeParent(0);
			ref.setCodeClassif(0);
			ref.setRefType(3);
			
			ref.setDateOt(DateUtils.systemMinDate());
			ref.setDateDo(DateUtils.systemMaxDate());
			
			ref.setDateReg(new Date());
			ref.setUserReg(-1);
			
			ref.setRefName(nameRef);
			
			ra.setId(idAdress);
			ra.setAddrText(addrRef);
			ra.setCodeRef(codeReferent);
			ra.setAddrType(idAdress);
			ra.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
			ra.setDateReg(new Date());
			ra.setUserReg(-1);
			
			ref.setAddressKoresp(ra);
					
			return ref;
		
	}
	
	
	

}
