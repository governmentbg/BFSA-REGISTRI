package com.ib.babhregs.migr.invitro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Query;

import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.EventDeinJiv;
import com.ib.babhregs.db.dto.EventDeinJivLice;
import com.ib.babhregs.db.dto.EventDeinJivPredmet;
import com.ib.babhregs.db.dto.EventDeinostVlpBolesti;
import com.ib.babhregs.db.dto.EventDeinostVlpLice;
import com.ib.babhregs.db.dto.EventDeinostVlpPredmet;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.db.dto.VpisvaneAccess;
import com.ib.babhregs.db.dto.VpisvaneDoc;
import com.ib.babhregs.db.dto.VpisvaneStatus;
import com.ib.babhregs.search.ObektDeinostSearch;
import com.ib.babhregs.system.BabhConstants;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.SearchUtils;


public class InvitroMigrate {
	
	public static int seqVal = 5000000;
	
	public static boolean isTest = true;
	
	
	public static Integer REG_BABH = 139;
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvitroMigrate.class);
	
	public static Integer[] access = {-1, 1712, 1795, 1751, 1661, 1675};
	
	
	private static ArrayList<Object[]> allReferents = new ArrayList<Object[]>();
	
	
	public static void main(String[] args) {	
		
		
		
		try {
			
			
			try {
				JPA.getUtil().begin();
				deleteDbInRange(seqVal);
				JPA.getUtil().commit();
			} catch (DbErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			
			//exportClassif(12);
			
			
			transferInvitro();
//			System.out.println(getSeq("SEQ_DOC"));
//			System.out.println(getSeq("SEQ_DOC"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	



	@SuppressWarnings("unchecked")	
	public static void transferInvitro() {
		
		
		
		
		
		
		HashMap<String, Referent> referents = new HashMap<String, Referent>(); 
		
		
		
		
		//SystemData sd = new SystemData();
		
		
		
		String sqlMain = "SELECT "
				+ "    id,"
				+ "    naim_s,"
				+ "    cert_s, "
				+ "    bolesti_s,"
				+ "    firmi_s, "
				+ "    notes_s, "
				+ "    naim1, "
				+ "    naim2, "
				+ "    cert_nom, "
				+ "    cert_dat, "
				+ "    bolesti_codes, "
				+ "    proizvoditel, "
				+ "    pritejatel "
				+ "FROM "
				+ "    temp_invitro "				
				+ " order by id";
		
		
		
		String sqlFirm = "SELECT "
				+ "    id, "
				+ "    name_cyr, "
				+ "    name_lat, "
				+ "    country, "
				+ "    pkod, "
				+ "    adres_bg, "
				+ "    adres_lat, "
				+ "    еkatte, "
				+ "    country_code "
				+ "FROM "
				+ "    temp_invitro_firm_new "				
				+ " order by id";
		
		
		
		
		
		
		Date dat = new Date();
		
		
		
		int cnt = 0;
		int cntSkipped = 0;
		int cntErrors = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		
		
		
		
		
		ArrayList<Object[]> allInvitro = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery(sqlMain).getResultList();
		allReferents = (ArrayList<Object[]>) JPA.getUtil().getEntityManager().createNativeQuery(sqlFirm).getResultList();
				

		Integer id = null;
		
		for (Object[] invitro : allInvitro) {
				
					
			try {
				cnt++;
				
				id = SearchUtils.asInteger(invitro[0]);
				
				System.out.println(cnt + ". Мигриране на invitro с id = " + id);
				
				if (id == 57) {
					System.out.println("BUM!");
				}
				
				
				Vpisvane vpisvane = new Vpisvane();
				Doc docU = new Doc();
				Doc doc = new Doc();
				
								
				ArrayList<VpisvaneDoc> vdocList = new ArrayList<VpisvaneDoc>();
				ArrayList<VpisvaneAccess> allVAccess = new ArrayList<VpisvaneAccess>();
				VpisvaneStatus vs = new VpisvaneStatus();
				VpisvaneStatus vsZ = null;
				

				int idDoc = getSeq("SEQ_DOC");
				int idDocU = getSeq("SEQ_DOC");
				int idVpisvane = getSeq("seq_vpisvane");
				int idEvent = getSeq("seq_event_deinost_vlp");
				int idEventPredmet = getSeq("seq_event_deinost_vlp_predmet");
				
				
//				System.out.println("******************************** " + idDoc);
//				System.out.println("******************************** " + idDocU);
				
				String kitOpis = SearchUtils.asString(invitro[3]);
				String notes = SearchUtils.asString(invitro[5]);
				String naim1 = SearchUtils.asString(invitro[6]);
				String naim2 = SearchUtils.asString(invitro[7]);
				String certNomer = SearchUtils.asString(invitro[8]);
				Date certDat = SearchUtils.asDate(invitro[9]);
				
				if (certDat == null) {
					certDat = DateUtils.systemMinDate();
				}
				
				String bolesti = SearchUtils.asString(invitro[10]);
				String prozivCode = SearchUtils.asString(invitro[11]);
				String pretejatelCode = SearchUtils.asString(invitro[12]);
				
				
				Referent proizvoditel = referents.get(prozivCode.replace("\r", "").replace("\n", "").trim());
				
				if (proizvoditel == null) {
					proizvoditel = createReferent(prozivCode);
					referents.put(prozivCode, proizvoditel)	;	
				}
				
				
				Referent pritejatel = referents.get(pretejatelCode.replace("\r", "").replace("\n", "").trim());
				if (pritejatel == null) {
					pritejatel = createReferent(pretejatelCode);
					referents.put(pretejatelCode, pritejatel)	;	
				}
				
				
				 
				//НАЧАЛО НА ЗАЯВЛЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
				
				doc.setId(idDoc);
				doc.setDocType(1); //Входящ
				doc.setDocVid(90); //Заявление за издаване на сертификат за регистрация на инвитро диагностични средства
				if (! SearchUtils.isEmpty(certNomer)) {
					doc.setRnDoc("Z"+ certNomer);
				}else {
					cntSkipped ++;
					LOGGER.info("Няма номер на сертификат за invitro с id " + id );
					continue;
				}
				
				doc.setDocDate(DateUtils.systemMinDate());
				
				
				doc.setRegistraturaId(REG_BABH);
				
				String anot = "Заявление за издаване на сертификат за регистрация на " + naim1 ;
				if (! SearchUtils.isEmpty(naim2)) {
					anot +=  " / " + naim2;
				}	
				doc.setOtnosno(anot);
				doc.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
				doc.setImeAdnUsluga("Издаване на сертификат за регистрация на инвитро диагностични средства");
				doc.setCodeAdmUsluga("2802");
				doc.setRegisterId(27);  
				doc.setLicenziantType(2);
				doc.setIdLicenziant(pritejatel.getCode());
				doc.setStatus(15);
				doc.setStatusDate(new Date());
				doc.setGuid("{"+java.util.UUID.randomUUID().toString()+"}");
				doc.setFreeAccess(2);
				doc.setDocInfo("Записът е създаден от транформация!");
				doc.setUserReg(-1);
				doc.setDateReg(new Date());
				doc.setCodeRefZaiavitel(-3);
				
				
				//КРАЙ НА ЗАЯВЛЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
				//НАЧАЛО НА УДОСТОВЕРЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
			
				
				
				docU.setId(idDocU);
				docU.setDocType(2); //Изходящ
				docU.setDocVid(249); //Сертификат за регистрация на инвитро диагностично средство
				
				docU.setRnDoc(certNomer);
				
				
				docU.setDocDate(certDat);
				
				docU.setRegistraturaId(doc.getRegistraturaId());
				
				anot = "Сертификат за регистрация на инвитро диагностично средство " + naim1 ;
				if (! SearchUtils.isEmpty(naim2)) {
					anot +=  " / " + naim2;
				}		
				docU.setOtnosno(anot);
				docU.setIndPlateno(BabhConstants.CODE_ZNACHENIE_DA);
				docU.setImeAdnUsluga("Издаване на сертификат за регистрация на инвитро диагностични средства");
				docU.setCodeAdmUsluga("2802");
				docU.setRegisterId(4);  
				docU.setLicenziantType(2);
				docU.setIdLicenziant(pritejatel.getCode());
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
				
				
				//КРАЙ НА УДОСТОВЕРЕНИЕ  //////////////////////////////////////////////////////////////////////////////////////////
				
							
				vpisvane.setId(idVpisvane);
				vpisvane.setIdZaqavlenie(doc.getId());
				vpisvane.setStatus(BabhConstants.STATUS_VP_VPISAN);
				vpisvane.setDateStatus(docU.getDocDate());
				vpisvane.setRegistraturaId(doc.getRegistraturaId());
				vpisvane.setIdRegister(doc.getRegisterId());
				vpisvane.setLicenziantType(doc.getLicenziantType());
				vpisvane.setIdLicenziant(pritejatel.getCode());
				vpisvane.setCodeRefVpisvane(pritejatel.getCode());
				
				vpisvane.setRegNomZaqvlVpisvane(doc.getRnDoc());
				vpisvane.setDateZaqvlVpis(doc.getDocDate());
				vpisvane.setRegNomResult(docU.getRnDoc());
				vpisvane.setDateResult(docU.getDocDate());
				vpisvane.setIdResult(docU.getId());
				vpisvane.setUserReg(-1);
				vpisvane.setDateReg(new Date());
				vpisvane.setCodePage(9);
				vpisvane.setFromМigr(1);
				vpisvane.setLicenziant(pritejatel.getRefName());
				
				
				
				vs.setId(getSeq("seq_vpisvane_status"));
				vs.setIdVpisvane(vpisvane.getId());
				vs.setStatus(BabhConstants.STATUS_VP_VPISAN);
				vs.setDateStatus(docU.getDocDate());
				vs.setUserReg(-1);
				vs.setDateReg(new Date());
				
				if (notes != null && ! SearchUtils.isEmpty(notes)) {
					
					vsZ = new VpisvaneStatus();
					vsZ.setId(getSeq("seq_vpisvane_status"));
					vsZ.setReasonDop(notes);
					
					
					vpisvane.setStatus(BabhConstants.STATUS_VP_PREKRATEN);
					vpisvane.setStatus(BabhConstants.STATUS_VP_PREKRATEN);
					
					vsZ.setStatus(BabhConstants.STATUS_VP_PREKRATEN);
					vsZ.setDateStatus(DateUtils.systemMinDate());
					vpisvane.setDateStatus(DateUtils.systemMinDate());
					
					vsZ.setIdVpisvane(vpisvane.getId());
					vsZ.setUserReg(-1);
					vsZ.setDateReg(new Date());
					
					
					String zapoved = notes = notes.replace("Прекратен със заповед  ", "");
					String[] partsZ = zapoved.split("/");
					if (partsZ.length == 2) {
						vsZ.setRegNomZapoved(partsZ[0]);
						vsZ.setDateZapoved(sdf.parse(partsZ[1]));
						vsZ.setDateStatus(vsZ.getDateZapoved());
						vpisvane.setDateStatus(vsZ.getDateZapoved());
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
				
				vpisvane.setLicenziant(pritejatel.getRefName());
				
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
				
				
				//НАЧАЛО на ПРЕДМЕТ  //////////////////////////////////////////////////////////////////////////////////////////
				EventDeinostVlpPredmet predmet = new EventDeinostVlpPredmet();
				
				predmet.setNaimenovanie(naim1);
				if (naim2 == null || SearchUtils.isEmpty(naim2)) {
					predmet.setNaimenovanieLat(naim1);
				}else {
					predmet.setNaimenovanieLat(naim2);
				}
				predmet.setIdEventDeinostVlp(idEvent);
				predmet.setId(idEventPredmet);
				predmet.setDopInfo(kitOpis);
				
				//КРАЙ НА ПРЕДМЕТ  //////////////////////////////////////////////////////////////////////////////////////////
				
				ArrayList<EventDeinostVlpBolesti> bolestiList = new ArrayList<EventDeinostVlpBolesti>();
				if (bolesti != null && ! SearchUtils.isEmpty(bolesti)) {
					String[] bolArr = bolesti.split("\\|");
					for (String tekB : bolArr) {
						if (! SearchUtils.isEmpty(tekB.trim())) {
							Integer codeB = Integer.parseInt(tekB.trim());
							EventDeinostVlpBolesti b = new EventDeinostVlpBolesti();
							b.setBolest(codeB);
							b.setEventDejnostVlpId(idEvent);
							b.setId(getSeq("seq_event_deinost_vlp_bolesti"));
							bolestiList.add(b);
						}
					}
					
				}else {
					//тодо ако пак се наложи predmet.setDopInfo(kitOpis);
				}
				
				EventDeinostVlpLice pLice = new EventDeinostVlpLice();
				pLice.setId(getSeq("seq_event_deinost_vlp_lice"));
				pLice.setEventDeinostVlpId(idEvent);
				pLice.setTipVraz(13);  //Прозиводител
				pLice.setCodeRef(proizvoditel.getCode());
				pLice.setReferent(proizvoditel);
				
				
				
				JPA.getUtil().begin();
				
				
				
				insertDoc(doc);
				insertDoc(docU);
				insertVpisvane(vpisvane);
				insertVpisvaneStatus(vs);	
				
				if (vsZ != null) {
					insertVpisvaneStatus(vsZ);	
				}
				
				insertEventVlp(idVpisvane, idEvent);
				insertEventVlpVid(idEvent, BabhConstants.CODE_ZNACHENIE_DEIN_VLP_INVITRO);
				
				insertEventVlpPredmet(predmet);
				insertEventBolesti(bolestiList);
				insertEventLice(pLice);
				
				
				//Притежател
				insertReferent(pritejatel);
				if (pritejatel.getAddressKoresp() != null) {
					insertRefAddress(pritejatel.getAddressKoresp());
				}
				
				//Производител
				insertReferent(proizvoditel);
				if (proizvoditel.getAddressKoresp() != null) {
					insertRefAddress(proizvoditel.getAddressKoresp());
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
				LOGGER.error("Грешка при обработка на invitro с id:  " + id, e);
				JPA.getUtil().rollback();
				
			}finally {
				
			}
			
		}
		
		LOGGER.info("Общо Обработени Invitro : " + cnt );
		LOGGER.info( "Прoпуснати Invitro : " + cntSkipped );
		LOGGER.info("Грешки : " + cntErrors);
		LOGGER.info(dat + " - " + new Date());
		
		
		System.out.println("Общо Обработени Invitro : " + cnt);
		System.out.println("Прoпуснати Invitro : " + cntSkipped);
		System.out.println("Грешки : " + cntErrors);
		System.out.println(dat + " - " + new Date());
		
		
		
		
		
	}
		
		
		
	




	




	




	




	




	




	














	







	private static Referent createReferent(String codeString) throws DbErrorException {
		

		
		Integer code = Integer.parseInt(codeString.replace("\r", "").replace("\n", "").trim());
		
		Referent ref = null;
		
		Integer idReferent = getSeq("SEQ_ADM_REFERENTS");
		Integer codeReferent = getSeq("SEQ_ADM_REFERENTS_CODE");
		Integer idAdress = getSeq("SEQ_ADM_REF_ADDRS");
		
		
		for (Object[] tek : allReferents) {
			Integer tekId = SearchUtils.asInteger(tek[0]);
			if (tekId.equals(code)) {
				
				ref = new Referent();
				ReferentAddress ra = new ReferentAddress();
				
				ref.setId(idReferent);
				ref.setCode(codeReferent);	
				ref.setCodePrev(0);
				ref.setCodeParent(0);
				ref.setCodeClassif(0);
				ref.setRefType(3);
				
				String imeBG = SearchUtils.asString(tek[1]).trim();
				String imeEN = SearchUtils.asString(tek[2]).trim();
				
				String addrBG = SearchUtils.asString(tek[5]).trim();
				String addrEN = SearchUtils.asString(tek[6]).trim();
				
				
				if (imeBG != null && ! imeBG.trim().isEmpty()) {
					ref.setRefName(imeBG);
				}
				
				if (imeEN != null && ! imeEN.trim().isEmpty()) {
					ref.setRefLatin(imeEN);
				}
				
				if (addrBG != null && ! addrBG.trim().isEmpty()) {
					ra.setAddrText(addrBG);
				}
				
				if (addrEN != null && ! addrEN.trim().isEmpty()) {
					ra.setAddrTextLatin(addrEN);
				}
				
				if (ref.getRefName() == null || ref.getRefName().trim().isEmpty()) {
					ref.setRefName(ref.getRefLatin());
				}
				
				
				
				ref.setDateOt(DateUtils.systemMinDate());
				ref.setDateDo(DateUtils.systemMaxDate());
				
				ref.setDateReg(new Date());
				ref.setUserReg(-1);
				
				ra.setId(idAdress);
				ra.setCodeRef(codeReferent);
				
				ra.setAddrCountry(SearchUtils.asInteger(tek[8]));
				ra.setEkatte(SearchUtils.asInteger(tek[7]));
				ra.setPostCode(SearchUtils.asString(tek[4]));
				ra.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
				ra.setDateReg(new Date());
				ra.setUserReg(-1);
				
				ref.setAddressKoresp(ra);
				
				
				
				
				
			}
			
		}
		
		
		return ref;
		
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void insertDoc(Doc doc) throws DbErrorException {
		
		String insertSql = "INSERT INTO doc (doc_id, registratura_id, register_id, rn_doc, guid, doc_type, doc_vid, doc_date, code_usluga, otnosno, status, user_reg, date_reg, status_date, licenziant_type, id_licenziant, ime_usluga, plateno, free_access, doc_info, code_ref_corresp, kachestvo_lice, doc_pod_vid) "
				+ "         VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
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
			query.setParameter(22, new TypedParameterValue(StandardBasicTypes.INTEGER, doc.getKachestvoLice()));
			
			query.setParameter(23, new TypedParameterValue(StandardBasicTypes.INTEGER, doc.getDocPodVid()));
			
			query.executeUpdate();
		} catch (Exception e) {
			LOGGER.error("Грешка при запис на документ ", e);
			throw new DbErrorException("Грешка при запис на документ", e); 
		}
		
		
	}
	
	public static void insertVpisvaneDoc(VpisvaneDoc vdoc) throws DbErrorException {
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




	




	public static void insertReferent(Referent ref) throws DbErrorException {
		
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
	
	public static void insertRefAddress(ReferentAddress address) throws DbErrorException {
		
		if (address.getRaion() != null && ! SearchUtils.isEmpty(address.getRaion())) {
			//Флаг - виж по долу
			return;
		}
		
		String insertSql = "INSERT INTO adm_ref_addrs (addr_id, code_ref, addr_type, addr_country, addr_text, post_code, ekatte, user_reg, date_reg, addr_text_latin) VALUES (?,?,?,?,?,?,?,?,?,?)";
		


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
			
			query.setParameter(10,new TypedParameterValue(StandardBasicTypes.STRING, address.getAddrTextLatin()));
			
			query.executeUpdate();
			
			//Използвам го като флаг за това дали е записан вече
			address.setRaion(""+address.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на адрес на референт ", e);
			throw new DbErrorException("Грешка при запис на адрес на референт", e); 
		}
		
		
	}




	




	public static void insertVpisvane(Vpisvane vpisvane) throws DbErrorException {
		
		String insertSql = "INSERT INTO vpisvane (id, id_zaqavlenie, status, date_status, registratura_id, id_register, licenziant_type, id_licenziant, reg_nom_zaqvl_vpisvane, date_zaqvl_vpis, reg_nom_result, date_result, id_result, user_reg, date_reg, licenziant, code_page, code_ref_vpisvane, info) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		

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
			query.setParameter(19, new TypedParameterValue(StandardBasicTypes.STRING,  vpisvane.getInfo()));
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на вписване ", e);
			throw new DbErrorException("Грешка при запис на вписване", e); 
		}
		
	}
	
	public static void insertVpisvaneAccess(VpisvaneAccess va) throws DbErrorException {
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
	
	
	
	
	
	
	
	
	
	public static void insertVpisvaneStatus(VpisvaneStatus vs) throws DbErrorException {
		String insertSql = "INSERT INTO vpisvane_status (id, id_vpisvane, status, date_status, user_reg, date_reg, reason_dop, reg_nom_zapoved, date_zapoved) VALUES (?,?,?,?,?,?,?,?,?);";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, vs.getId());			
			query.setParameter(2, vs.getIdVpisvane());
			query.setParameter(3, vs.getStatus());
			query.setParameter(4, vs.getDateStatus());
			query.setParameter(5, vs.getUserReg());
			query.setParameter(6, vs.getDateReg());
			
			
			

			query.setParameter(7, new TypedParameterValue(StandardBasicTypes.STRING, vs.getReasonDop()));
			query.setParameter(8, new TypedParameterValue(StandardBasicTypes.STRING, vs.getRegNomZapoved()));
			query.setParameter(9, new TypedParameterValue(StandardBasicTypes.DATE, vs.getDateZapoved()));
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на обект статус на вписване ", e);
			throw new DbErrorException("Грешка при запис на обект статус на вписване", e); 
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

	
	public static void insertEventVlpPredmet(EventDeinostVlpPredmet predmet) throws DbErrorException {
		
		String insertSql = "INSERT INTO event_deinost_vlp_predmet (id, id_event_deinost_vlp, naimenovanie, naimenovanie_lat, dop_info, predmet, code_classif) VALUES (?,?,?,?,?,?,?)";
		

		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, predmet.getId());			
			query.setParameter(2, predmet.getIdEventDeinostVlp());			
			query.setParameter(3, predmet.getNaimenovanie());
			query.setParameter(4, predmet.getNaimenovanieLat());
			query.setParameter(5, new TypedParameterValue(StandardBasicTypes.STRING, predmet.getDopInfo()));
			query.setParameter(6, new TypedParameterValue(StandardBasicTypes.INTEGER, predmet.getPredmet()));
			query.setParameter(7, new TypedParameterValue(StandardBasicTypes.INTEGER, predmet.getCodeClassif()));

			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на обект предмет ", e);
			throw new DbErrorException("Грешка при запис на предмет", e); 
		}
		
	}
	
	
	private static void insertEventBolesti(ArrayList<EventDeinostVlpBolesti> bolestiList) throws DbErrorException {
		
		String insertSql = "INSERT INTO event_deinost_vlp_bolesti (id, event_dejnost_vlp_id, bolest) VALUES (?, ?, ?)";
				
		

		try {
			
			for (EventDeinostVlpBolesti b: bolestiList) {
			
				Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
				query.setParameter(1, b.getId());			
				query.setParameter(2, b.getEventDejnostVlpId());			
				query.setParameter(3, b.getBolest());
				
	
				
				
				query.executeUpdate();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на  болести ", e);
			throw new DbErrorException("Грешка при запис на болести", e); 
		}
	}
	
	private static void insertEventLice(EventDeinostVlpLice pLice) throws DbErrorException {
		
		String insertSql = "INSERT INTO event_deinost_vlp_lice (id, event_deinost_vlp_id, tip_vraz,  code_ref) VALUES (?,?,?,?)";
		

		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, pLice.getId());			
			query.setParameter(2, pLice.getEventDeinostVlpId());			
			query.setParameter(3, pLice.getTipVraz());
			query.setParameter(4, pLice.getCodeRef());

			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на обект лице ", e);
			throw new DbErrorException("Грешка при запис на връзка с лице", e); 
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

	
	public static void insertEventDeinostJiv(EventDeinJiv edj ) throws DbErrorException {
		String insertSql = "INSERT INTO event_deinost_jiv (id, opisanie_cyr, dop_info, date_beg_licence, date_end_licence, id_vpisvane, patuvane, lice_kachestvo, meroptiatie, adres, date_beg_meropriatie, date_end_meropriatie, "
				+ "opisanie_lat, nachin_mqsto_pridobiv, user_reg, date_reg, user_last_mod, date_last_mod, iznos, cel, miarka, zemliste, nachin_transp, categoria, sklad) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			query.setParameter(1, edj.getId());			
			
			query.setParameter(2, new TypedParameterValue(StandardBasicTypes.STRING, edj.getOpisanieCyr()));
			query.setParameter(3, new TypedParameterValue(StandardBasicTypes.STRING, edj.getDopInfo()));
			query.setParameter(4, new TypedParameterValue(StandardBasicTypes.TIMESTAMP, edj.getDateBegLicence()));
			query.setParameter(5, new TypedParameterValue(StandardBasicTypes.TIMESTAMP, edj.getDateEndLicence()));
			query.setParameter(6, new TypedParameterValue(StandardBasicTypes.INTEGER, edj.getIdVpisvane()));
			query.setParameter(7, new TypedParameterValue(StandardBasicTypes.INTEGER, edj.getPatuvane()));
			query.setParameter(8, new TypedParameterValue(StandardBasicTypes.INTEGER, edj.getLiceKachestvo()));
			query.setParameter(9, new TypedParameterValue(StandardBasicTypes.INTEGER, edj.getMeroptiatie()));
			query.setParameter(10, new TypedParameterValue(StandardBasicTypes.STRING, edj.getAdres()));
			query.setParameter(11, new TypedParameterValue(StandardBasicTypes.TIMESTAMP, edj.getDateBegMeropriatie()));
			query.setParameter(12, new TypedParameterValue(StandardBasicTypes.TIMESTAMP, edj.getDateEndMeropriatie()));
			query.setParameter(13, new TypedParameterValue(StandardBasicTypes.STRING, edj.getOpisanieLat()));
			query.setParameter(14, new TypedParameterValue(StandardBasicTypes.STRING, edj.getNachinMqstoPridobiv()));
			query.setParameter(15, new TypedParameterValue(StandardBasicTypes.INTEGER, edj.getUserReg()));
			query.setParameter(16, new TypedParameterValue(StandardBasicTypes.TIMESTAMP, edj.getDateReg()));
			query.setParameter(17, new TypedParameterValue(StandardBasicTypes.INTEGER, edj.getUserLastMod()));
			query.setParameter(18, new TypedParameterValue(StandardBasicTypes.TIMESTAMP, edj.getDateLastMod()));
			query.setParameter(19, new TypedParameterValue(StandardBasicTypes.STRING, edj.getIznos()));
			query.setParameter(20, new TypedParameterValue(StandardBasicTypes.STRING, edj.getCel()));
			query.setParameter(21, new TypedParameterValue(StandardBasicTypes.INTEGER, edj.getMiarka()));
			query.setParameter(22, new TypedParameterValue(StandardBasicTypes.STRING, edj.getZemliste()));
			query.setParameter(23, new TypedParameterValue(StandardBasicTypes.INTEGER, edj.getNachinTransp()));
			query.setParameter(24, new TypedParameterValue(StandardBasicTypes.INTEGER, edj.getCategoria()));
			query.setParameter(25, new TypedParameterValue(StandardBasicTypes.INTEGER, edj.getSklad()));
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на EventDeinostJiv ", e);
			throw new DbErrorException("Грешка при запис на EventDeinostJiv", e); 
		}
		
	}
	
	
	public static void insertEventDeinostJivObuchenie(Integer id, Integer idEdj, Integer ob) throws DbErrorException {
		String insertSql = "INSERT INTO event_deinost_jiv_obuchenie (id, event_deinost_jiv_id, sfera_obucenie) VALUES (?, ?, ?)";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			
			query.setParameter(1, id );			
			
			query.setParameter(2, idEdj);
			query.setParameter(3, ob);
			
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на EventDeinostJiv ", e);
			throw new DbErrorException("Грешка при запис на EventDeinostJiv", e); 
		}
	}
	
	
	
	
	public static void insertEventDeinostJivPredmet(EventDeinJivPredmet edjp) throws DbErrorException {
		
		String insertSql = "INSERT INTO event_deinost_jiv_predmet (id, event_deinost_jiv_id, predmet, broi, dop_info, code_classif) VALUES (?, ?, ?, ?, ?, ?)";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			
			query.setParameter(1, new TypedParameterValue(StandardBasicTypes.INTEGER, edjp.getId()));
			query.setParameter(2, new TypedParameterValue(StandardBasicTypes.INTEGER, edjp.getEventDeinJivId()));
			query.setParameter(3, new TypedParameterValue(StandardBasicTypes.INTEGER, edjp.getPredmet()));
			query.setParameter(4, new TypedParameterValue(StandardBasicTypes.INTEGER, edjp.getBroi()));
			query.setParameter(5, new TypedParameterValue(StandardBasicTypes.STRING, edjp.getDopInfo()));
			query.setParameter(6, new TypedParameterValue(StandardBasicTypes.INTEGER, edjp.getCodeClassif()));
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на EventDeinostJiv ", e);
			throw new DbErrorException("Грешка при запис на EventDeinostJiv", e); 
		}
	}
	
	
	public static void insertEventDeinostJivLica(EventDeinJivLice edjl) throws DbErrorException {
		
		String insertSql = "INSERT INTO event_deinost_jiv_lice (id, event_deinost_jiv_id, tip_vraz, code_ref) VALUES (?, ?, ?, ?)";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			
			query.setParameter(1, new TypedParameterValue(StandardBasicTypes.INTEGER, edjl.getId()));
			query.setParameter(2, new TypedParameterValue(StandardBasicTypes.INTEGER, edjl.getEventDeinJivId()));
			query.setParameter(3, new TypedParameterValue(StandardBasicTypes.INTEGER, edjl.getTipVraz()));
			query.setParameter(4, new TypedParameterValue(StandardBasicTypes.INTEGER, edjl.getCodeRef()));
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на EventDeinostJiv ", e);
			throw new DbErrorException("Грешка при запис на EventDeinostJiv", e); 
		}
	}
	
	
	
	
	
	
	public static void insertEventDeinostJivVid(Integer id, Integer idEdj, Integer vid) throws DbErrorException {
		String insertSql = "INSERT INTO event_deinost_jiv_vid (id, event_deinost_jiv_id, vid) VALUES (?, ?, ?)";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			
			query.setParameter(1, id );			
			
			query.setParameter(2, idEdj);
			query.setParameter(3, vid);
			
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на EventDeinostJiv ", e);
			throw new DbErrorException("Грешка при запис на EventDeinostJiv", e); 
		}
	}
	
	
	public static void insertObektDeinostDeinost(ObektDeinostDeinost odd) throws DbErrorException {
		String insertSql = "INSERT INTO obekt_deinost_deinost (id, obekt_deinost_id, tabl_event_deinost, deinost_id) VALUES (?, ?, ?, ?);";
		
		


		try {
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(insertSql);
			
			query.setParameter(1, odd.getId() );
			query.setParameter(2, odd.getObektDeinostId());
			query.setParameter(3, odd.getTablEventDeinost());
			query.setParameter(4, odd.getDeinostId());
			
			
			
			query.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Грешка при запис на ObektDeinostDeinost ", e);
			throw new DbErrorException("Грешка при запис на ObektDeinostDeinost", e); 
		}
		
	}
	
	
	
	public static void deleteDbInRange(Integer startRange) {
		
		JPA.getUtil().getEntityManager().createNativeQuery("delete from adm_ref_doc where code_ref >= ? and code_ref <= ?").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		

		JPA.getUtil().getEntityManager().createNativeQuery("delete from adm_ref_addrs where addr_id >= ? and addr_id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();


		JPA.getUtil().getEntityManager().createNativeQuery("delete from adm_ref_ref  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		
		
		JPA.getUtil().getEntityManager().createNativeQuery("delete from adm_referents where adm_referents.ref_type in (3,4) and  ref_id >= ? and ref_id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from doc_access  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		//JPA.getUtil().getEntityManager().createNativeQuery("delete from proc_exe_etap  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		//JPA.getUtil().getEntityManager().createNativeQuery("delete from proc_exe  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from doc  where doc_id >= ? and doc_id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from oez_sub_oez_harakt  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from oez_harakt  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from oez_sub_oez  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from obekt_deinost_prednaznachenie  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from obekt_deinost_lica  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		
		JPA.getUtil().getEntityManager().createNativeQuery("delete from obekt_deinost_deinost  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from oez_nomer_kadastar  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from obekt_deinost  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_furaji_vid  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_furaji_sgp  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_furaji_sert  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_furaji_prednazn_jiv  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_furaji_predmet  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_furaji_maznini  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_furaji_gip  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_furaji  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_vlp_vid  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_vlp_prvnos_subst  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_vlp_prvnos  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_vlp_predmet  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_vlp_lice  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_vlp_bolesti  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_vlp  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_jiv_vid  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_jiv_predmet  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_jiv_obuchenie  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();

		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_jiv_lice  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_jiv_identif  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_jiv_gkpp  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_jiv_darj  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from event_deinost_jiv  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from mps_vid_products  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from mps_lice  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from mps_kapacitet_jiv  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from mps_deinost  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from mps_category  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from mps  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vlp_vid_jiv  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vlp_veshtva  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vlp_prilagane_vid  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vlp_prilagane  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vlp_lice  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vlp_farm_form  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vlp_opakovka  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vlp  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vpisvane_status  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vpisvane_doc  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vpisvane_access  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();
		JPA.getUtil().getEntityManager().createNativeQuery("delete from vpisvane  where id >= ? and id <= ? ").setParameter(1, startRange).setParameter(2, startRange+999999).executeUpdate();


		
	}
	
	
	public static String clearString(String value) {
		
		if (value == null) {
			return null;
		}
		
		value = value.replace("", "");		
		value = value.replace("«", "\"");
		value = value.replace("»", "\"");
		value = value.replace("„", "\"");
		value = value.replace("''", "\"");
		value = value.replace(",,", "\"");
		value = value.replaceAll("  ", " ");
		value = value.replaceAll("  ", " ");
		value = value.replaceAll("  ", " ");
		
		
		
		return value;
	}
	
	public static String clearStringAndNewLine(String value) {
		
		if (value == null) {
			return null;
		}
		
		value = value.replace("", "");		
		value = value.replace("«", "\"");
		value = value.replace("»", "\"");
		value = value.replace("„", "\"");
		value = value.replace("''", "\"");
		value = value.replace(",,", "\"");
		value = value.replaceAll("  ", " ");
		value = value.replaceAll("  ", " ");
		value = value.replaceAll("  ", " ");
		value = value.replaceAll("\r", "");
		value = value.replaceAll("\n", "");
		
		
		
		return value;
	}
	
	
	 public static  ArrayList<String> getPostCodeFromString(String tekst){
 		String numbers = "0123456789";
 		boolean hasQuote = false;
 		
 		ArrayList<String> foundStrings = new ArrayList<String>();
 		
 		if (tekst == null) {
 			return foundStrings;
 		}else {
 			//Това е когато завършва на цифра да не се налага след цикъла да правя анализ ;)
 			tekst += ";"; 
 		}
 		
 		
 		String num = "";
 		for(int i = 0; i < tekst.length(); i++) {
 			
 			if ("\"".equals(""+tekst.charAt(i))) {
 				hasQuote = !hasQuote;
 				num = "";
 				continue;
 				
 			}
 			
 			if (!hasQuote) {
 				String tek = ""+tekst.charAt(i);
 			    if (numbers.contains(tek)) {
 			    	num += tek; 
 			    }else {
 			    	if (num.length() == 4) {
 			    		//System.out.println("Adding " + num);
 			    		foundStrings.add(num);
 			    		num = "";				    		
 			    	}else {
 			    		num = "";
 			    	}
 			    }
 			}
 		}
 		
 		return foundStrings;
     }
	 
	 
	 
	 public static  String clearParazites (String tekst){
	 		
		 String pz = " ,;";
	 	 String newString = "";	
	 	 boolean toClear = true;
	 		
	 	 for(int i = 0; i < tekst.length(); i++) {
	 		 String tek = ""+tekst.charAt(i);
	 		 
	 		 if (pz.contains(tek) && toClear) {
	 			 continue;
	 		 }
	 		 
	 		if (pz.contains(tek) && !toClear) {
	 			 if (",".equals(tek)) {
	 				 newString = newString+ ", ";
	 			 }else {
	 				newString = newString + tek;
	 			 }
	 			toClear = !toClear;
	 			continue;
	 		 }
	 		 
	 		newString = newString + tek;
	 		toClear = false;
	 	 }
 			
 			
	 	return newString;
	 }
	 
	 
	 public static void recognizeAddres(ObektDeinost od, ArrayList<Object[]> ekattes) {
		 
		 ReferentAddress ra = new ReferentAddress();
		 
		 ra.setAddrText(od.getAddress());
		 
		 recognizeAddres(ra, ekattes);
		 
		 od.setAddress(ra.getAddrText());
		 od.setNasMesto(ra.getEkatte());
		 od.setPostCode(ra.getPostCode());
		 
		 
	 }
	 
	 
	 
	 public static void recognizeAddres(ReferentAddress ra, ArrayList<Object[]> ekattes) {
			if (ra.getAddrText() != null) {
				
				int cnt = 0;
				Integer ekatte = null;
				String ekatteStr = null;
				String oblast = null;
				String obshtina = null;
				
				
				
				
				
				//post code			
				ArrayList<String> all = InvitroMigrate.getPostCodeFromString(ra.getAddrText());
				if (all != null && all.size() == 1) {
					ra.setPostCode(all.get(0));
					//ra.setAddrText(ra.getAddrText().replace(all.get(0), ""));				
					ra.setAddrText(clearPK(ra.getAddrText(), all.get(0)));
					
					
				}
				
				String upperAdr = ra.getAddrText().toUpperCase().trim();
				
				
				
				for (Object[] row : ekattes) {
					if (upperAdr.startsWith(SearchUtils.asString(row[1]))) {
						
						ekatte = SearchUtils.asInteger(row[0]);
						ekatteStr = SearchUtils.asString(row[1]);
						oblast = SearchUtils.asString(row[2]);
						obshtina = SearchUtils.asString(row[3]);
						
						cnt++;
						if (cnt > 1) {
							System.out.println(cnt + " *****************************************" + upperAdr);
							return;
						}
					}
				}
				
				if (cnt == 0) {
					System.out.println(cnt + " *****************************************" + upperAdr);
				}else {
					ra.setEkatte(ekatte);
					
					upperAdr = ra.getAddrText().substring(ekatteStr.length()).trim();
					upperAdr = upperAdr.replace("\r", " ");
					upperAdr = upperAdr.replace("\n", " ");
					upperAdr = upperAdr.replaceAll("  ", " ");
					
					
					if (oblast != null) {
						upperAdr = clearOblasr(upperAdr, oblast);
					}
					
					if (obshtina != null) {
						upperAdr = clearObshtina(upperAdr, obshtina);
					}
					
					
					
					
					
					
					
					upperAdr = InvitroMigrate.clearParazites(upperAdr);
					
					
					ra.setAddrText(upperAdr);
					
					
				}
				
			}
			
		}








		private static String clearOblasr(String addr, String oblast) {
			
			addr = replaceIgnoreCase(addr, "област " + oblast, "") ;
			addr = replaceIgnoreCase(addr, "обл. "   + oblast, "") ;
			addr = replaceIgnoreCase(addr, "обл "    + oblast, "") ;
			addr = replaceIgnoreCase(addr, "обл."    + oblast, "") ;
			
			return addr;
			
		}



		private static String clearObshtina(String addr, String obshtina) {
			addr = replaceIgnoreCase(addr, "община " + obshtina, "") ;
			addr = replaceIgnoreCase(addr, "общ. "   + obshtina, "") ;
			addr = replaceIgnoreCase(addr, "общ "    + obshtina, "") ;
			addr = replaceIgnoreCase(addr, "общ."    + obshtina, "") ;
			
			return addr;
		}
		
		private static String clearPK(String addr, String pk) {
			addr = replaceIgnoreCase(addr, "п.к. " + pk, "") ;
			addr = replaceIgnoreCase(addr, "п.к."   + pk, "") ;
			addr = replaceIgnoreCase(addr, "п. к. "   + pk, "") ;
			addr = replaceIgnoreCase(addr, "п. к."   + pk, "") ;
			addr = replaceIgnoreCase(addr, "пк"    + pk, "") ;
			addr = replaceIgnoreCase(addr, "пк "    + pk, "") ;
			addr = replaceIgnoreCase(addr, pk, "") ;
			
			
			
			return addr;
		}



		public static String replaceIgnoreCase(String original, String target, String replacement) {
	        // Use the case-insensitive flag "(?i)" in the regular expression
	        String regex = "(?i)" + target;
	        
	        // Use the replaceAll method with the case-insensitive regular expression
	        return original.replaceAll(regex, replacement);
	    }
		
		public static Object[] parseRnDoc(String certS) {
			
			Object[] result = new Object[2];
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			
			if (certS != null) {
				
				certS = certS.toUpperCase();
				certS = certS.replace("РАЗРЕШЕНИЕ", "");
				certS = certS.replace("ИЗМЕНЕНИЕ", "");
				certS = certS.replace("Г.", "");
				certS = certS.replace(" Г", "");
				certS = certS.replace("№", "");
				certS = certS.replace("\r", "");
				certS = certS.replace("\n", "");
				certS = certS.replace("РАЗРЕ-ШЕНИЕ", "");
				certS = certS.replace("РАЗРЕ-ШЕНИЕ", "");
				certS = certS.replaceAll("  ", " ");
				certS = certS.replaceAll(" *", "");
				certS = certS.trim();
				
				String[] parts = certS.split("/");
				if (parts.length != 2) {
					//System.out.println(nom + " -> " + certS);
				}else {
					String datS = parts[1];
					
					try {
						result[1] = sdf.parse(datS.trim());
					} catch (Exception e) {
						//System.out.println("Грешна дата !!!! + |" + datS + "|");
						
					}
					
					result[0] = parts[0];	
				}
				
			}
			
			return result;
		}








		








		
	 
		
		
		
	
	
}


