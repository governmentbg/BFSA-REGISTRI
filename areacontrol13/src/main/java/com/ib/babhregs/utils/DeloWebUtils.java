package com.ib.babhregs.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctc.wstx.util.StringUtil;
import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.DocAccess;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.rest.DeloWebRestClient;
import com.ib.babhregs.rest.common.DocWS;
import com.ib.babhregs.rest.common.FileWS;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.system.ActiveUser;
import com.ib.system.db.JPA;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.RestClientException;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.quartz.BaseJobResult;
import com.ib.system.utils.SearchUtils;
import com.ib.system.utils.StringUtils;



import bg.egov.eforms.utils.EFormUtils;
import bg.egov.eforms.utils.EgovContainer;

public class DeloWebUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeloWebUtils.class);
	
	public BaseJobResult proccessZaiavlenia(SystemData sd) throws RestClientException {
		
		String docIdent = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		
		BaseJobResult jobResult = new BaseJobResult();
		jobResult.setStatus(BabhConstants.CODE_ZNACHENIE_JOB_STATUS_EMPTY);
		jobResult.setComment(null);
		
		ArrayList<DocWS> novi = new ArrayList<DocWS>();
		
		DeloWebRestClient dwClient = new DeloWebRestClient(sd);
		DocDAO ddao = new DocDAO(ActiveUser.DEFAULT);
		FilesDAO fdao = new FilesDAO(ActiveUser.DEFAULT);
		ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
		
		//Не прихващаме. Ако гръмне, целия процес спира
		novi = dwClient.getNewDocuments(1);
		
		
		int brAll = novi.size();
		int brOk = 0;
		int brGreshni = 0;
		boolean isNew = true;
		ArrayList<String> descrRows = new ArrayList<String>();
		
		
		
		for (DocWS doc : novi) {
			
			
			docIdent = doc.getRnDoc() + "/" + sdf.format(doc.getDatDoc());
			
			try {
				
				LOGGER.debug("Proccess document: " + doc.getRnDoc() + "/"+doc.getDatDoc());
				
				doc = dwClient.getDocumentInfo(doc.getId());
				
				LOGGER.debug("Number of files: " + doc.getFiles().size());
				if (doc.getCoresp()!= null) {
					LOGGER.debug("Number of correspondents: 1\t" + doc.getCoresp().getImeCoresp());
				}else {
					LOGGER.debug("Number of correspondents: 0");
				}
				
				
				
				
				JPA.getUtil().begin();
				
				isNew = false;
				Doc docDb = ddao.findByIdFromDeloWeb(doc.getId());
				if (docDb == null) {
					isNew = true;
					docDb = new Doc();
					docDb.setWorkOffId(doc.getId());
				}
				
				String nastr = null;
				
				nastr = sd.getSettingsValue("deloweb.defaultVidDoc");
	    		if (nastr == null || nastr.trim().isEmpty()) {
	    			throw new RestClientException("'deloweb.defaultVidDoc' is not defined in SYSTEM_OPTIONS");
	    		}				
				
//	    		Integer vid = Integer.valueOf(nastr);
//				List<SystemClassif> all = sd.getItemsByCodeExt(BabhConstants.CODE_CLASSIF_DOC_VID, ""+doc.getVidDoc(), BabhConstants.CODE_DEFAULT_LANG, new Date());
//				if (all.size() == 1) {
//					vid = all.get(0).getCode();
//				}
				
				
				nastr = sd.getSettingsValue("deloweb.defaultTipDoc");
	    		if (nastr == null || nastr.trim().isEmpty()) {
	    			throw new RestClientException("'deloweb.defaultTipDoc' is not defined in SYSTEM_OPTIONS");
	    		}	    		
				Integer tip = Integer.valueOf(nastr);
				List<SystemClassif> all = sd.getItemsByCodeExt(BabhConstants.CODE_CLASSIF_DOC_TYPE, ""+doc.getTipDoc(), BabhConstants.CODE_DEFAULT_LANG, new Date());
				if (all.size() == 1) {
					tip = all.get(0).getCode();
				}
				
				
				nastr = sd.getSettingsValue("deloweb.defaultReg");
	    		if (nastr == null || nastr.trim().isEmpty()) {
	    			throw new RestClientException("'deloweb.defaultReg' is not defined in SYSTEM_OPTIONS");
	    		}				
				Integer reg = Integer.valueOf(nastr);
				SystemClassif item = sd.decodeItemLiteGTE(BabhConstants.CODE_CLASSIF_REGISTRATURI, doc.getIdRegistratura(), BabhConstants.CODE_DEFAULT_LANG, new Date())  ;
				if (item != null) {
					reg = doc.getIdRegistratura();
				}
				
				
				
				docDb.setRnDoc(doc.getRnDoc());
				docDb.setDocDate(doc.getDatDoc());
				//docDb.setDocVid(vid);
				docDb.setDocType(tip);
				docDb.setRegistraturaId(reg);
				docDb.setOtnosno(doc.getAnot());
				docDb.setReceivedBy(doc.getIdvaOt());				
				docDb.setStatus(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_WAIT);
				docDb.setStatusDate(new Date());
				//docDb.setRegisterId(idRegister);
				
				docDb.setCodeAdmUsluga(doc.getNomAdmUsluga());
				
				
				
				
//				if (doc.getEmails() != null) {
//					String mails = "";
//					for (String em : doc.getEmails()) {
//						mails += em + ",";						
//					}
//					
//					if (mails.length() > 1) {
//						mails = mails.substring(0, mails.length()-1);
//					}
//					
//					docDb.setTaskEmails(mails);
//				}
				
				docDb.setTaskInfo(doc.getTaskInfo());
				
				
				
				
				//За сега сетва само регистъра
				try {
					docDb = new EFormUtils().setDocData(docDb, doc.getFiles(), sd);
				} catch (UnexpectedResultException e) {
					// TODO Auto-generated catch block
					LOGGER.error("Register cannot be set !!!! idRegister= " + docDb.getRegisterId());
				}
				
				//Измислени				
				docDb.setFreeAccess(0);
				docDb.setUserReg(-1);
				docDb.setDateReg(new Date());
				docDb.setGuid(doc.getIdentificatorUsluga());
				
				
				EgovContainer con = new EFormUtils().parseEgovLight(doc, docDb, sd);
				rdao = new ReferentDAO(ActiveUser.DEFAULT);
				
				if (con.ref1 != null && con.ref2 != null) {
					//Това е ако от формите сме разпознали нещо
				
					//Проврека дали двата референта са с един идентификатор - водещи са данните на заявителя (реф2) според Роси
					if (! SearchUtils.isEmpty(con.ref1.getFzlEgn()) && ! SearchUtils.isEmpty(con.ref2.getFzlEgn()) && con.ref1.getFzlEgn().trim().equals(con.ref2.getFzlEgn().trim()) ) {
						//с еднакви егн-та са
						con.ref1 = con.ref2;
					}
					if (! SearchUtils.isEmpty(con.ref1.getFzlLnc()) && ! SearchUtils.isEmpty(con.ref2.getFzlLnc()) && con.ref1.getFzlLnc().trim().equals(con.ref2.getFzlLnc().trim()) ) {
						//с еднакви лнч-та са
						con.ref1 = con.ref2;
					}
					
					if (! SearchUtils.isEmpty(con.ref1.getNflEik()) && ! SearchUtils.isEmpty(con.ref2.getNflEik()) && con.ref1.getNflEik().trim().equals(con.ref2.getNflEik().trim()) ) {
						//с еднакви eik-та са
						con.ref1 = con.ref2;
					}
					
					
					if (con.ref1 != null) {
						con.ref1 = rdao.save(con.ref1);
						docDb.setCodeRefCorresp(con.ref1.getCode());
					}
					
					if (con.ref2 != null) {
						con.ref2 = rdao.save(con.ref2);				
						docDb.setCodeRefZaiavitel(con.ref2.getCode());
					}
				}else {
					//Това е ако няма прикачени файлове или няма файл от еформи или не го е пасрнало успешно
					LOGGER.debug("ref1 or ref2 are null!!!");
				}
				
				
				
				//docDb.setCodeRefCorresp(doc.getIdCoresp());
				
				LOGGER.debug("Start saving document: " + doc.getRnDoc() + "/" + doc.getRnDoc());
				docDb = ddao.save(docDb);
				LOGGER.debug("End saving document: " + doc.getRnDoc() + "/" + doc.getRnDoc());
				
				if (! isNew) {
					List<Files> files = fdao.selectByFileObject(docDb.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
					for (Files file : files) {
						fdao.deleteFileObject(file);
					}
				}else {
					ArrayList<Object> ids = findUsersIdsByEmail(doc.getEmails());
					for (Object tek : ids) {
						Integer code = SearchUtils.asInteger(tek);
						DocAccess da = new DocAccess();
						da.setCodeRef(code);
						da.setDocId(docDb.getId());		
						JPA.getUtil().getEntityManager().persist(da);
					}
				}
				
				
				
				
				for (FileWS f :doc.getFiles()) {
					Files file = new Files();
					file.setContent(f.getFileContent());
					file.setContentType(f.getFileType());
					file.setFilename(f.getFileName());
					LOGGER.debug("Start saving file: " + file.getFilename());
					fdao.saveFileObject(file, docDb.getId(), BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC);
					LOGGER.debug("End saving file: " + file.getFilename());					
				}
				
				
				
				if (doc.getCoresp() != null && doc.getCoresp().getImeCoresp() != null && !doc.getCoresp().getImeCoresp().trim().isEmpty()) {
					
					String egn = null;
					String lnch = null;
					String bulstat = null;
					
					Referent ref = null;
					
					if (doc.getCoresp().getEgnCoresp() != null && !doc.getCoresp().getEgnCoresp().trim().isEmpty()) {
						egn = doc.getCoresp().getEgnCoresp() ;
					}else {
						if (doc.getCoresp().getLnchCoresp() != null && !doc.getCoresp().getLnchCoresp().trim().isEmpty()) {
							lnch = doc.getCoresp().getLnchCoresp();
						}else {
							if (doc.getCoresp().getBulstatCoresp() != null && !doc.getCoresp().getBulstatCoresp().trim().isEmpty()) {
								bulstat = doc.getCoresp().getBulstatCoresp();
							}
						}
					}
					
					if (ref == null && egn  != null) {
						ref = rdao.findByIdent(null, egn.trim(), null, null);
					}
					
					if (ref == null && bulstat  != null) {
						ref = rdao.findByIdent(bulstat.trim(), null, null, null);
					}
					
					if (ref == null && lnch  != null) {
						ref = rdao.findByIdent(null, null, lnch.trim(), null);
					}
					
					if (ref == null && docDb.getCodeRefCorresp() != null) {
						
						Referent coresp = rdao.findById(docDb.getCodeRefCorresp());
						if (coresp != null && doc.getCoresp().getImeCoresp().trim().equalsIgnoreCase(coresp.getRefName())) {
							ref = coresp;
						}
						
					}
					
					
					if (ref == null) {
						ref = new Referent();						
					}
					
					ref.setRefName(doc.getCoresp().getImeCoresp());
					ref.setFzlEgn(egn);
					ref.setFzlLnc(lnch);
					ref.setNflEik(bulstat);
					
					ref.setContactEmail(doc.getCoresp().getMailCoresp());
					ref.setContactPhone(doc.getCoresp().getTel());
					
					ReferentAddress address = ref.getAddress();
					if (address == null) {
						address = new ReferentAddress();
					}
					
					address.setEkatte(doc.getCoresp().getMestoCoresp());
					address.setRaion(doc.getCoresp().getRaionCoresp());
					address.setAddrText(doc.getCoresp().getAdresCoresp());
					
					ref.setAddress(address);
					
					
					nastr = sd.getSettingsValue("deloweb.defaultTipCoresp");
		    		if (nastr == null || nastr.trim().isEmpty()) {
		    			throw new RestClientException("'deloweb.defaultTipCoresp' is not defined in SYSTEM_OPTIONS");
		    		}	    		
					Integer tipC = Integer.valueOf(nastr);
					all = sd.getItemsByCodeExt(BabhConstants.CODE_CLASSIF_REF_TYPE, ""+doc.getCoresp().getTipCoresp(), BabhConstants.CODE_DEFAULT_LANG, new Date());
					if (all.size() == 1) {
						tipC = all.get(0).getCode();
					}
					
					ref.setRefType(tipC);
					
					LOGGER.debug("Start saving referent: " + ref.getRefName());
					ref=rdao.save(ref);
					LOGGER.debug("End saving referent: " + ref.getRefName());
					
					if (! ref.getId().equals(docDb.getCodeRefCorresp())) {
						docDb.setCodeRefCorresp(ref.getCode());
						docDb = ddao.save(docDb);
					}
					
					
					
				}
				
				
				
				
				JPA.getUtil().commit();
				
				dwClient.markDocAsFinished(doc.getId());
				
				brOk++;
				
				descrRows.add(docIdent + ": Обработен успешно!");
				LOGGER.debug(docIdent + " proccessed successfully ! brOk = " + brOk);
				
				
				
				
				
			} catch (Exception e) {
				LOGGER.error("Error:",e);
				if (JPA.getUtil().getEntityManager().getTransaction().isActive()) {				
					JPA.getUtil().rollback();
				}
				brGreshni++;
				descrRows.add(docIdent + ": \r\n"  + StringUtils.stack2string(e));
			
			}finally {
				//brAll++;
				JPA.getUtil().closeConnection();
			}
			
		}
		
		if  (brGreshni > 0) {
			jobResult.setStatus(BabhConstants.CODE_ZNACHENIE_JOB_STATUS_WARN);			
		}else {
			if (brAll > 0) {
				jobResult.setStatus(BabhConstants.CODE_ZNACHENIE_JOB_STATUS_OK);
			}else {
				jobResult.setStatus(BabhConstants.CODE_ZNACHENIE_JOB_STATUS_EMPTY);
			}
			
		}
		
		String comment = "";
		
		comment += "Общо обработени: " + brAll + "\r\n";
		comment += "Брой обработени с грешка : " + brGreshni + "\r\n";
		comment += "Общо обработени без грешка: " + brOk + "\r\n";
		
		
		
		jobResult.setComment(comment);
		
		
		String desc = "";
		for (String row : descrRows) {
			desc += "***** " + row + "\r\n";
		}
		
		jobResult.setDescription(desc);
		
		
		LOGGER.debug("Comment:\t"  + jobResult.getComment());	
		LOGGER.debug("Description:\t"  + jobResult.getDescription());
		return jobResult;
		
		
	}
	
	
	@SuppressWarnings("unchecked")
	public ArrayList<Object> findUsersIdsByEmail(List<String> emails) throws DbErrorException{
		
		if (emails == null || emails.size() == 0) {
			return new ArrayList<Object>();
		}
		
		ArrayList<String> bullshit = new ArrayList<String>();
		for (String s : emails) {
			bullshit.add(s.replace("\"", ""));
		}
		
		try {
			
			String sql = "select distinct code from ( "
					+ "        select user_id code from adm_users where adm_users.email  in :mails "
					+ "        union "
					+ "        select code from adm_referents where adm_referents.contact_email in :mails and adm_referents.ref_type = 2 "
					+ "    ) as tabl";
			
			Query query = JPA.getUtil().getEntityManager().createNativeQuery(sql);
			query.setParameter("mails", bullshit);
			
			return (ArrayList<Object>) query.getResultList();
			
		} catch (Exception e) {
			throw new DbErrorException("Грешка при търсене на мейли на потребители", e);
		}
		
	}

}
