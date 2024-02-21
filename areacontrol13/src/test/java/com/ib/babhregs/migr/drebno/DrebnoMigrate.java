package com.ib.babhregs.migr.drebno;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Query;

import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.ObektDeinostLica;
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


public class DrebnoMigrate {
	
	public static int seqVal = 4000000;
	
	public static boolean isTest = true;
	
	
	public static Integer REG_BABH = 139;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DrebnoMigrate.class);
	
	public static Integer[] access = {-1, 1712, 1795, 1751, 1661, 1675};
	
	public static ArrayList<Object[]> ekattes = new ArrayList<Object[]>();
	
	
	
	public static void main(String[] args) {	
		
		
		
		try {
			
			
			JPA.getUtil().begin();
			InvitroMigrate.deleteDbInRange(seqVal);
			JPA.getUtil().commit();
			
			ekattes = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery("select ekatte_att.ekatte,  upper(ekatte_att.tvm || ' ' || ekatte_att.ime) , ekatte_att.oblast_ime, ekatte_att.obstina_ime  from babhregs_migr.ekatte_att").getResultList();
			
			transferDrebno();

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	



	@SuppressWarnings("unchecked")	
	public static void transferDrebno() {
		
		
		
		
		
		
		 
		
		
		
		
		//SystemData sd = new SystemData();
		
		
		
		String sqlMain = "SELECT nom, razreshenie, firm, addr, upr, dop_info, active, ime, notes FROM temp_drebno order by nom";
		
		
		
		
		
		
		
		
		
		
		Date dat = new Date();
		
		
		
		int cnt = 0;
		int cntSkipped = 0;
		int cntErrors = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		
		
		
		
		
		ArrayList<Object[]> allDrebno = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery(sqlMain).getResultList();
		
		
		
		for (Object[] row : allDrebno) {
				
			String nom = SearchUtils.asString(row[0]);
					
			try {
				cnt++;
				
				
				
				//System.out.println(cnt + ". Мигриране на drebno с nom = " + nom);
				
				
				
//				if (nom == 194 ) {
//					System.out.println("BUM!");
//				}
				
			
				
				
				
				String certS = InvitroMigrate.clearString(SearchUtils.asString(row[1]));
				String firmAddr = InvitroMigrate.clearString(SearchUtils.asString(row[2]));
				String addr = InvitroMigrate.clearString(SearchUtils.asString(row[3]));
				String upr = InvitroMigrate.clearString(SearchUtils.asString(row[4]));
				String dopInfo = InvitroMigrate.clearString(SearchUtils.asString(row[5]));
				String active = InvitroMigrate.clearString(SearchUtils.asString(row[6]));
				String firmIme = InvitroMigrate.clearString(SearchUtils.asString(row[7]));
				String notes = InvitroMigrate.clearString(SearchUtils.asString(row[8]));
				
				String izmS = null;
				
				String certNom = null;
				Date certDat = DateUtils.systemMinDate();
				String izmNom = null;
				Date izmDat = DateUtils.systemMinDate();
				
				
				if (certS != null) {
					int izmIndex = certS.toUpperCase().indexOf("ИЗМЕНЕНИЕ");
					if (izmIndex != -1) {
						izmS = certS.substring(izmIndex+9);
						certS = certS.substring(0, izmIndex);
						
					}
				}
				
				
				
				Object[] result = InvitroMigrate.parseRnDoc(certS);
				if (result[0] != null) {
					certNom = (String)result[0]; 
				}else {
					System.out.println(nom + "Нe може да се извлече номер от: " + certS );
				}
				
				if (result[1] != null) {
					certDat = (Date)result[1]; 
				}else {
					System.out.println(nom + "Нe може да се извлече дата от: " + certS );
				}
				
				System.out.println("Обработка на "+certNom+" от " + certDat);
				
				
				if (izmS != null ) {
					result = InvitroMigrate.parseRnDoc(izmS);
					if (result[0] != null) {
						izmNom = (String)result[0]; 
					}else {
						System.out.println(nom + "Нe може да се извлече номер от: " + izmS );
					}
					
					if (result[1] != null) {
						izmDat = (Date)result[1]; 
					}else {
						System.out.println(nom + "Нe може да се извлече дата от: " + izmS );
					}
					
					
				}	
				
				
				
				Vpisvane vpisvane = new Vpisvane();
				Doc docU = new Doc();
				Doc doc = new Doc();
				Doc docIzm = null;
								
				ArrayList<VpisvaneDoc> vdocList = new ArrayList<VpisvaneDoc>();
				ArrayList<VpisvaneAccess> allVAccess = new ArrayList<VpisvaneAccess>();
				VpisvaneStatus vs = new VpisvaneStatus();
				VpisvaneStatus vsZ = null;
				
				Referent ref = new Referent(); //Лицензиант
				Referent upravitel = new Referent();	 //Управител

				int idDoc = getSeq("SEQ_DOC");
				int idDocU = getSeq("SEQ_DOC");
				int idVpisvane = getSeq("seq_vpisvane");
				int idEvent = getSeq("seq_event_deinost_vlp");
				
				int idOD = getSeq("seq_obekt_deinost");
				int idODD = getSeq("seq_obekt_deinost_deinost");
				int idODLice = getSeq("seq_obekt_deinost_lica");
				
				
				
				
				
//				System.out.println("******************************** " + idDoc);
//				System.out.println("******************************** " + idDocU);
				
				
				//Лицензиянт
				
				ReferentAddress ra = new ReferentAddress();
				
				Integer idReferent = getSeq("SEQ_ADM_REFERENTS");
				Integer codeReferent = getSeq("SEQ_ADM_REFERENTS_CODE");
				Integer idAdress = getSeq("SEQ_ADM_REF_ADDRS");
				
				
				
				
				
				ref.setId(idReferent);
				ref.setCode(codeReferent);	
				ref.setCodePrev(0);
				ref.setCodeParent(0);
				ref.setCodeClassif(0);
				ref.setRefType(3);
				ref.setRefName(SearchUtils.asString(firmIme));
				ref.setDateOt(DateUtils.systemMinDate());
				ref.setDateDo(DateUtils.systemMaxDate());
				
				ref.setDateReg(new Date());
				ref.setUserReg(-1);
				
				
				ra.setId(idAdress);
				ra.setCodeRef(codeReferent);
				ra.setAddrText(firmAddr);
				ra.setAddrTextLatin(firmAddr);
				ra.setAddrCountry(37);
//				ra.setEkatte(SearchUtils.asInteger(tek[7]));
//				ra.setPostCode(SearchUtils.asString(tek[4]));
				ra.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
				ra.setDateReg(new Date());
				ra.setUserReg(-1);
				
				InvitroMigrate.recognizeAddres(ra, ekattes);
				
				ref.setAddressKoresp(ra);
				
				
				//Управител
							
				
				upravitel.setId(getSeq("SEQ_ADM_REFERENTS"));
				upravitel.setCode(getSeq("SEQ_ADM_REFERENTS_CODE"));	
				upravitel.setCodePrev(0);
				upravitel.setCodeParent(0);
				upravitel.setCodeClassif(0);
				upravitel.setRefType(4);
				upravitel.setRefName(upr);
				upravitel.setDateOt(DateUtils.systemMinDate());
				upravitel.setDateDo(DateUtils.systemMaxDate());
				
				upravitel.setDateReg(new Date());
				upravitel.setUserReg(-1);
				
				
				
				
				
				//Apteka
				ObektDeinost od = new ObektDeinost();
				od.setDarj(37);
				od.setAddress(addr);
				od.setId(idOD);
				od.setVid(3);
				od.setDateReg(dat);
				od.setUserReg(-1);
				
				InvitroMigrate.recognizeAddres(od, ekattes);
				
				ObektDeinostDeinost odd = new ObektDeinostDeinost();
				odd.setObektDeinost(od);
				odd.setObektDeinostId(od.getId());
				odd.setId(idODD);
				odd.setTablEventDeinost(100);
				odd.setDeinostId(idEvent);
				odd.setDateBeg(dat);
				
				ObektDeinostLica uLice = new ObektDeinostLica();
				uLice.setId(idODLice);
				uLice.setObektDeinostId(od.getId());
				uLice.setRole(12);  //Управител
				uLice.setCodeRef(upravitel.getCode());
				uLice.setReferent(upravitel);
				//uLice.setDateBeg(DateUtils.systemMinDate());
				
				 
				//НАЧАЛО НА ЗАЯВЛЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
				
				doc.setId(idDoc);
				doc.setDocType(1); //Входящ
				doc.setDocVid(75); 
				if (! SearchUtils.isEmpty(certNom)) {
					doc.setRnDoc("Z"+ certNom);
				}else {
					//cntSkipped ++;
					LOGGER.info("Няма номер на сертификат за drebno с nom " + nom );
					//continue;
					certNom = "N/A";
					doc.setRnDoc(certNom);
				}
				
				doc.setDocDate(DateUtils.systemMinDate());
				
				
				doc.setRegistraturaId(REG_BABH);
				
				String anot = "Заявление за издаване на разрешение за търговия на дребно с ВЛП на " + firmIme ;
					
				doc.setOtnosno(anot);
				doc.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
				doc.setImeAdnUsluga("Издаване на разрешение за търговия на дребно с ВЛП");
				doc.setCodeAdmUsluga("1365");
				doc.setRegisterId(24);  
				doc.setLicenziantType(2);
				doc.setIdLicenziant(codeReferent);
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
				
				
				
				//КРАЙ НА ЗАЯВЛЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
				//НАЧАЛО НА УДОСТОВЕРЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
			
				
				
				docU.setId(idDocU);
				docU.setDocType(2); //Изходящ
				docU.setDocVid(245); 
				
				docU.setRnDoc(certNom);
				
				
				docU.setDocDate(certDat);
				
				docU.setRegistraturaId(doc.getRegistraturaId());
				
				anot = "Разрешение за търговия на дребно с ВЛП на" + firmIme ;
					
				docU.setOtnosno(anot);
				docU.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
				docU.setImeAdnUsluga("Издаване на разрешение за търговия на дребно с ВЛП");
				docU.setCodeAdmUsluga("1365");
				docU.setRegisterId(24);  
				docU.setLicenziantType(2);
				docU.setIdLicenziant(codeReferent);
				docU.setStatus(15);
				
				if (certDat == null) {
					docU.setStatusDate(DateUtils.systemMinDate());
				}else {
					docU.setStatusDate(certDat);
				}
				
				
				docU.setGuid("{"+java.util.UUID.randomUUID().toString()+"}");
				docU.setFreeAccess(2);
				docU.setDocInfo("Записът е създаден от транформация!");
				docU.setUserReg(-1);
				docU.setDateReg(new Date());
				
				
				if (izmDat != null & izmNom != null) {
					docIzm = new Doc();
					docIzm.setId(getSeq("SEQ_DOC"));
					
					docIzm.setDocType(2); //Изходящ
					docIzm.setDocVid(298); 
					
					docIzm.setRnDoc(izmNom);
					
					
					docIzm.setDocDate(izmDat);
					
					docIzm.setRegistraturaId(doc.getRegistraturaId());
					
					anot = "Изменение на разрешение за търговия на дребно с ВЛП на" + firmIme ;
						
					docIzm.setOtnosno(anot);
					docIzm.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
					docIzm.setImeAdnUsluga("Издаване на разрешение за търговия на дребно с ВЛП");
					docIzm.setCodeAdmUsluga("1365");
					docIzm.setRegisterId(24);  
					docIzm.setLicenziantType(2);
					docIzm.setIdLicenziant(codeReferent);
					docIzm.setStatus(15);
					
					if (izmDat == null) {
						docIzm.setStatusDate(DateUtils.systemMinDate());
					}else {
						docIzm.setStatusDate(izmDat);
					}
					
					
					docIzm.setGuid("{"+java.util.UUID.randomUUID().toString()+"}");
					docIzm.setFreeAccess(2);
					docIzm.setDocInfo("Записът е създаден от транформация!");
					docIzm.setUserReg(-1);
					docIzm.setDateReg(new Date());
					
					
				}
				
				
				//КРАЙ НА УДОСТОВЕРЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
							
				vpisvane.setId(idVpisvane);
				vpisvane.setIdZaqavlenie(doc.getId());
				vpisvane.setStatus(BabhConstants.STATUS_VP_VPISAN);
				vpisvane.setDateStatus(docU.getDocDate());
				vpisvane.setRegistraturaId(doc.getRegistraturaId());
				vpisvane.setIdRegister(doc.getRegisterId());
				vpisvane.setLicenziantType(doc.getLicenziantType());
				vpisvane.setIdLicenziant(codeReferent);
				vpisvane.setCodeRefVpisvane(codeReferent);
				
				
				vpisvane.setRegNomZaqvlVpisvane(doc.getRnDoc());
				vpisvane.setDateZaqvlVpis(doc.getDocDate());
				vpisvane.setRegNomResult(docU.getRnDoc());
				vpisvane.setDateResult(docU.getDocDate());
				vpisvane.setIdResult(docU.getId());
				vpisvane.setUserReg(-1);
				vpisvane.setDateReg(new Date());
				vpisvane.setCodePage(9);
				vpisvane.setFromМigr(1);
				vpisvane.setLicenziant(firmIme);
				vpisvane.setInfo(notes);
				
				
				
				vs.setId(getSeq("seq_vpisvane_status"));
				vs.setIdVpisvane(vpisvane.getId());
				vs.setStatus(BabhConstants.STATUS_VP_VPISAN);
				vs.setDateStatus(docU.getDocDate());
				vs.setUserReg(-1);
				vs.setDateReg(new Date());
				
				if (dopInfo != null && ! SearchUtils.isEmpty(dopInfo)) {
					
					vsZ = new VpisvaneStatus();
					vsZ.setId(getSeq("seq_vpisvane_status"));
					vsZ.setReasonDop(dopInfo);
					
					
					vpisvane.setStatus(BabhConstants.STATUS_VP_PREKRATEN);
					
					
					vsZ.setStatus(BabhConstants.STATUS_VP_PREKRATEN);
					vsZ.setDateStatus(DateUtils.systemMinDate());
					vpisvane.setDateStatus(DateUtils.systemMinDate());
					
					vsZ.setIdVpisvane(vpisvane.getId());
					vsZ.setUserReg(-1);
					vsZ.setDateReg(new Date());
					
					
					String zapoved = dopInfo.toUpperCase().replace("прекратен със Заповед № ".toUpperCase(), "").replace("ОТНЕТ СЪС ЗАПОВЕД №".toUpperCase(), "").replace("прекратен със Заповед ".toUpperCase(), "");
					
					
					
					String[] partsZ = zapoved.split("/");
					
					if (partsZ.length == 1) {
						partsZ = zapoved.split("ОТ");
					}
					
					if (partsZ.length >= 2) {
						
						partsZ[0] = partsZ[0].trim();
						partsZ[1] = partsZ[1].trim();
						
						vsZ.setRegNomZapoved(partsZ[0]);
						vsZ.setDateZapoved(sdf.parse(partsZ[1]));
						vsZ.setDateStatus(vsZ.getDateZapoved());
						vpisvane.setDateStatus(vsZ.getDateZapoved());
					}else {
						vsZ.setRegNomZapoved(partsZ[0]);
					}
							
				}
				
				
				
				for (Integer codeRef : access) {
					
					
					
					VpisvaneAccess va = new VpisvaneAccess();
					
					va.setId(getSeq("SEQ_VPISVANE_ACCESS"));
					va.setCodeRef(codeRef);
					va.setVpisvaneId(vpisvane.getId());
					va.setUserReg(-1);
					va.setDateReg(new Date());
					allVAccess.add(va); 
					
				}
				
				vpisvane.setLicenziant(firmIme);
				
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
				
				if (docIzm != null) {
					VpisvaneDoc vdocIzm = new VpisvaneDoc();
					vdocIzm.setId(getSeq("seq_vpisvane_doc"));
					vdocIzm.setIdDoc(docIzm.getId());
					vdocIzm.setIdVpisvane(vpisvane.getId());				
					vdocIzm.setDateReg(new Date());
					vdocIzm.setUserReg(-1);
					vdocList.add(vdocIzm);
				}
				
				
				//КРАЙ НА ВПИСВАНЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
				
				
				
				
				
				
				
				
				JPA.getUtil().begin();
				
				
				
				InvitroMigrate.insertDoc(doc);
				InvitroMigrate.insertDoc(docU);
				InvitroMigrate.insertVpisvane(vpisvane);
				InvitroMigrate.insertVpisvaneStatus(vs);	
				
				if (vsZ != null) {
					InvitroMigrate.insertVpisvaneStatus(vsZ);	
				}
				
				insertEventVlp(idVpisvane, idEvent);	
				insertEventVlpVid(idEvent, BabhConstants.CODE_ZNACHENIE_DEIN_VLP_DREBNO);
				
				insertObektDeinost(od);
				
				insertObektDeinostDeinost(odd);
				
				
				insertObektLice(uLice);
				
				
				
				
				
				//Притежател
				insertReferent(ref);
				if (ref.getAddressKoresp() != null) {
					insertRefAddress(ref.getAddressKoresp());
				}
				
				//Управител
				insertReferent(upravitel);
				if (upravitel.getAddressKoresp() != null) {
					insertRefAddress(upravitel.getAddressKoresp());
				}
				
				
				for (VpisvaneDoc vd : vdocList) {
					insertVpisvaneDoc(vd);
				}
				
				for (VpisvaneAccess va : allVAccess ) {
					InvitroMigrate.insertVpisvaneAccess(va);
				}
				
				
				
				
				
				
				JPA.getUtil().commit();
				
				
				
				
				
			} catch (Exception e) {		
				e.printStackTrace();
				cntErrors++;				
				LOGGER.error("Грешка при обработка на drebno с nom:  " + nom, e);
				JPA.getUtil().rollback();
				
			}finally {
				
			}
			
		}
		
		LOGGER.info("Общо Обработени Drebno : " + cnt );
		LOGGER.info( "Прoпуснати Drebno : " + cntSkipped );
		LOGGER.info("Грешки : " + cntErrors);
		LOGGER.info(dat + " - " + new Date());
		
		
		System.out.println("Общо Обработени Drebno : " + cnt);
		System.out.println("Прoпуснати Drebno : " + cntSkipped);
		System.out.println("Грешки : " + cntErrors);
		System.out.println(dat + " - " + new Date());
		
		
		
		
		
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




	




	private static void insertReferent(Referent ref) throws DbErrorException {
		
		if (ref.getDbAddressId() != null) {
			return;
		}
		
		
		String insertSql = "INSERT INTO adm_referents (ref_id, code, code_classif, code_parent, code_prev, contact_email, contact_phone, date_reg, user_reg, fzl_lnc, ref_type, fzl_egn, nfl_eik, ime, prezime, familia, ref_name, date_ot, date_do, ref_latin) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
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
			query.setParameter(20, ref.getRefLatin());
			
			
			query.executeUpdate();
			
			ref.setDbAddressId(ref.getId());
			
			
		} catch (Exception e) {
			LOGGER.error("Грешка при запис на референт ", e);
			throw new DbErrorException("Грешка при запис на референт", e); 
		}
	}
	
	private static void insertRefAddress(ReferentAddress address) throws DbErrorException {
		
		if (address.getRaion() != null && ! SearchUtils.isEmpty(address.getRaion())) {
			//Флаг - виж по долу
			return;
		}
		
		String insertSql = "INSERT INTO adm_ref_addrs (addr_id, code_ref, addr_type, addr_country, addr_text, post_code, ekatte, user_reg, date_reg) VALUES (?,?,?,?,?,?,?,?,?)";
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, address.getId());
			query.setParameter(2, address.getCodeRef());
			query.setParameter(3, address.getAddrType());
			query.setParameter(4, new TypedParameterValue(StandardBasicTypes.INTEGER, address.getAddrCountry()));
			query.setParameter(5, new TypedParameterValue(StandardBasicTypes.STRING, address.getAddrText()));
			query.setParameter(6, new TypedParameterValue(StandardBasicTypes.STRING, address.getPostCode()));
			
			query.setParameter(7,new TypedParameterValue(StandardBasicTypes.INTEGER, address.getEkatte()));
			
			
			
			
			
			
			
			query.setParameter(8, address.getUserReg());
			query.setParameter(9, address.getDateReg());
			
			query.executeUpdate();
			
			//Използвам го като флаг за това дали е записан вече
			address.setRaion(""+address.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на адрес на референт ", e);
			throw new DbErrorException("Грешка при запис на адрес на референт", e); 
		}
		
		
	}




	




	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static void insertEventVlp(Integer idVpis, Integer idEvent) throws DbErrorException {
		String insertSql = "INSERT INTO event_deinost_vlp (id, id_vpisvane, user_reg, date_reg) VALUES (?,?,?,?);";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, idEvent);			
			query.setParameter(2, idVpis);			
			query.setParameter(3, -1);
			query.setParameter(4, new Date());

			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на ивент ", e);
			throw new DbErrorException("Грешка при запис на ивент", e); 
		}
		
	}
	
	private static void insertEventVlpVid(Integer idEvent, Integer vid) throws DbErrorException {
		
		String insertSql = "INSERT INTO event_deinost_vlp_vid (id, id_event_deinost_vlp, vid) VALUES (?, ?, ?);";
		
		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, getSeq("seq_event_deinost_vlp_vid"));			
			query.setParameter(2, idEvent);			
			query.setParameter(3, vid);
			

			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на ивент вид ", e);
			throw new DbErrorException("Грешка при запис на ивент вид", e); 
		}
		
	}

	
	
	
	
	
	
	
	private static void insertObektLice(ObektDeinostLica pLice) throws DbErrorException {
		
		String insertSql = "INSERT INTO obekt_deinost_lica (id, role, obekt_deinost_id, code_ref) VALUES (?,?,?,?);";
		

		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, pLice.getId());			
			query.setParameter(2, pLice.getRole());			
			query.setParameter(3, pLice.getObektDeinostId());
			query.setParameter(4, pLice.getCodeRef());

			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на обект лице към обект", e);
			throw new DbErrorException("Грешка при запис на връзка с лице към обект", e); 
		}
		
	}
	
	private static void insertObektDeinost(ObektDeinost od) throws DbErrorException {
		// 
		
		String insertSql = "INSERT INTO obekt_deinost (id, vid, darj,  nas_mesto, address, post_code, user_reg, date_reg) VALUES (?,?,?,?,?,?,?,?)";

		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, od.getId());			
			query.setParameter(2, od.getVid());			
			query.setParameter(3, od.getDarj());						
			query.setParameter(4, new TypedParameterValue(StandardBasicTypes.INTEGER,od.getNasMesto()));
			query.setParameter(5, od.getAddress());
			query.setParameter(6, new TypedParameterValue(StandardBasicTypes.STRING,od.getPostCode()));
			query.setParameter(7, od.getUserReg());
			query.setParameter(8, od.getDateReg());
			
			
			
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на обект на дейност ", e);
			throw new DbErrorException("Грешка при запис на адрес на  обект на дейност", e); 
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

			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на ообект дейност дейност ", e);
			throw new DbErrorException("Грешка при запис на ообект дейност дейност", e); 
		}
		
	}

	
}


