package bg.egov.eforms.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dao.MpsDAO;
import com.ib.babhregs.db.dao.ObektDeinostDAO;
import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.EventDeinJiv;
import com.ib.babhregs.db.dto.EventDeinJivIdentif;
import com.ib.babhregs.db.dto.EventDeinJivLice;
import com.ib.babhregs.db.dto.EventDeinJivPredmet;
import com.ib.babhregs.db.dto.EventDeinostFuraji;
import com.ib.babhregs.db.dto.EventDeinostFurajiPredmet;
import com.ib.babhregs.db.dto.EventDeinostFurajiPrednaznJiv;
import com.ib.babhregs.db.dto.EventDeinostFurajiSert;
import com.ib.babhregs.db.dto.EventDeinostVlp;
import com.ib.babhregs.db.dto.EventDeinostVlpLice;
import com.ib.babhregs.db.dto.EventDeinostVlpPredmet;
import com.ib.babhregs.db.dto.Mps;
import com.ib.babhregs.db.dto.MpsDeinost;
import com.ib.babhregs.db.dto.MpsLice;
import com.ib.babhregs.db.dto.ObektDeinost;
import com.ib.babhregs.db.dto.ObektDeinostDeinost;
import com.ib.babhregs.db.dto.ObektDeinostLica;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.ReferentDoc;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.db.dto.Vpisvane;
import com.ib.babhregs.rest.common.DocWS;
import com.ib.babhregs.rest.common.FileWS;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.system.ActiveUser;
import com.ib.system.db.dao.FilesDAO;
import com.ib.system.db.dto.Files;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.InvalidParameterException;
import com.ib.system.exceptions.UnexpectedResultException;
import com.ib.system.utils.DateUtils;
import com.ib.system.utils.JAXBHelper;
import com.ib.system.utils.SearchUtils;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;

import bg.egov.eforms.Address;
import bg.egov.eforms.ContactPoint;
import bg.egov.eforms.ProcessTime;
import bg.egov.eforms.ServiceRequest;






public class EFormUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EFormUtils.class);
	
	private SystemData sd = null;
	
	
	public EgovContainer convertEformToVpisvane(Integer idDoc, SystemData systemData) throws DbErrorException, UnexpectedResultException, InvalidParameterException {
		
		LOGGER.debug("Entering convertEformToVpisvane");
		EgovContainer eCon = new EgovContainer();
		
		
		FilesDAO fdao = new FilesDAO(ActiveUser.DEFAULT);
		DocDAO ddao = new DocDAO(ActiveUser.DEFAULT);
		
		//String jsonInfo = null;
		String xmlInfo = null;
		
		this.sd = systemData;
		
		
		
		
		ServiceRequest sr;
		
		eCon.doc = ddao.findById(idDoc);
		eCon.files = fdao.selectByFileObject(idDoc, eCon.doc.getCodeMainObject());
		eCon.vpisvane = new Vpisvane();
		
		eCon.vpisvane.setIdRegister(eCon.doc.getRegisterId());
		
		LOGGER.debug("Number of attached files: " +  eCon.files.size());
		
		
		//Тръгваме да обикаляме файловете, за да намерим дали има такъв от електронни форми
		if (eCon.files != null) {
			for (Files file : eCon.files) {
				
				file = fdao.findById(file.getId());
				
				if (file.getFilename() != null && file.getFilename().trim().toUpperCase().endsWith(".PDF")) {
					LOGGER.debug("Found PDF: " + file.getFilename());
					if (file.getContent() != null) {
						
						try {
							//PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC));		
							PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(file.getContent())));						
							
							//Метаданни за pdf-a
							PdfDocumentInfo info = pdfDoc.getDocumentInfo();
							
							//Къстом добавени данни :
							//jsonInfo = info.getMoreInfo("application.json_json");
							xmlInfo = info.getMoreInfo("application.json_xml");
							
							System.out.println(xmlInfo);
							
							
							if (xmlInfo != null && !xmlInfo.isEmpty()) {
								//Трансформ към клас
								
								sr = JAXBHelper.xmlToObject(ServiceRequest.class, xmlInfo);
								
								if (sr != null ) {
									
									eCon = fillVpisvaneByServiceRequest(sr, eCon, sd, false);
									
									
								}else {
									LOGGER.error("ServiceRequest sr is null !!!!!" );
								}
								
							}else{
								LOGGER.debug("xml property is null !!!!!" );
							}
							

						} catch (IOException e) {
							LOGGER.error("Проблем при изчитането на PDF файл: " + file.getFilename());							
							throw new UnexpectedResultException("Проблем при изчитането на PDF файл: " + file.getFilename());							
						} catch (JAXBException e) {
							
							LOGGER.error("Проблем при обработката на инфомацията от еформи в PDF файл: " + file.getFilename());	
							LOGGER.error("xmlInfo=" + xmlInfo);
							throw new UnexpectedResultException("Проблем при обработката на инфомацията от еформи в PDF файл: " + file.getFilename());		
						}
					}
				}
			}
		}
		
		
		
		LOGGER.debug("Exiting convertEformToVpisvane");
		return eCon;
	}


	private EgovContainer fillVpisvaneByServiceRequest(ServiceRequest sr, EgovContainer eCon, SystemData sd, boolean referentsOnly) throws UnexpectedResultException, DbErrorException, InvalidParameterException {
		
		
		
		
		Referent refApplicant = new Referent(); // От името на кого --> отива във Vpisvane
		Referent refAuthor = new Referent();    // Фактическия подател --> отива в Doc
		
		
		
		
		
		//Aplicant
		if (sr != null && sr.getApplicant() != null) {
			LOGGER.debug("APPLICANT:");
			if (sr.getApplicant().getPerson() != null && sr.getApplicant().getPerson().getPersonalData() != null && sr.getApplicant().getPerson().getPersonalData().getFirstName() != null) {				
				LOGGER.debug("APPLICANT Лице е !");
				
				refApplicant.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
				
				if (sr.getApplicant().getPerson().getPersonalData().getIdentifier() != null) {
					
					LOGGER.debug("APPLICANT  Идентификатор  :" + sr.getApplicant().getPerson().getPersonalData().getIdentifier().getIdentifier());
					String indent = sr.getApplicant().getPerson().getPersonalData().getIdentifier().getIdentifier();
					if (sr.getApplicant().getPerson().getPersonalData().getIdentifier().getIdentifierType() != null && sr.getApplicant().getPerson().getPersonalData().getIdentifier().getIdentifierType().getCode() != null) {
						LOGGER.debug("APPLICANT  Идентификатор (тип) :" + sr.getApplicant().getPerson().getPersonalData().getIdentifier().getIdentifierType().getCode());
						if (sr.getApplicant().getPerson().getPersonalData().getIdentifier().getIdentifierType().getCode().equals("idn") ||
							sr.getApplicant().getPerson().getPersonalData().getIdentifier().getIdentifierType().getCode().equals("1"))
						{ //EGN
							refApplicant.setFzlEgn(indent);							
						}else {
							//За сега приемаме, че е ЛНЧ, защото само това има за лице във формите
							refApplicant.setFzlLnc(indent);
						}
					}else {
						//пак приемаме, че ако въобще има нещо, то то е егн
						refApplicant.setFzlEgn(indent);
					}
				}
				
				LOGGER.debug("APPLICANT  First Name :" + sr.getApplicant().getPerson().getPersonalData().getFirstName());
				refApplicant.setIme(sr.getApplicant().getPerson().getPersonalData().getFirstName());
				LOGGER.debug("APPLICANT  Middle Name :" + sr.getApplicant().getPerson().getPersonalData().getMiddleName());
				refApplicant.setPrezime(sr.getApplicant().getPerson().getPersonalData().getMiddleName());				
				LOGGER.debug("APPLICANT  Family Name :" + sr.getApplicant().getPerson().getPersonalData().getFamilyName());
				refApplicant.setFamilia(sr.getApplicant().getPerson().getPersonalData().getFamilyName());
				
				if (sr.getApplicant().getPerson().getPersonalData().getFullName() != null && ! sr.getApplicant().getPerson().getPersonalData().getFullName().trim().isEmpty()) {
					LOGGER.debug("APPLICANT  Full Name (Laitin) :" + sr.getApplicant().getPerson().getPersonalData().getFullNameLatin());
					refApplicant.setRefName(sr.getApplicant().getPerson().getPersonalData().getFullName());
				}else {
					refApplicant.setRefName(refApplicant.getIme() + " " + refApplicant.getPrezime() + " " + refApplicant.getFamilia());
				}
				
				
				if (sr.getApplicant().getPerson().getPersonalData().getFirstNameLatin() != null && ! sr.getApplicant().getPerson().getPersonalData().getFirstNameLatin().isEmpty()) {
					//Има латиница
					LOGGER.debug("APPLICANT First Name (Latim) :" + sr.getApplicant().getPerson().getPersonalData().getFirstNameLatin());
					LOGGER.debug("APPLICANT Middle Name (Latim) :" + sr.getApplicant().getPerson().getPersonalData().getMiddleNameLatin());
					LOGGER.debug("APPLICANT Family Name (Latim) :" + sr.getApplicant().getPerson().getPersonalData().getFamilyNameLatin());
					refApplicant.setRefLatin(sr.getApplicant().getPerson().getPersonalData().getFirstNameLatin() + " " + sr.getApplicant().getPerson().getPersonalData().getMiddleNameLatin() + " " + sr.getApplicant().getPerson().getPersonalData().getFamilyNameLatin());
				}
				
			}else {
				LOGGER.debug("APPLICANT  Фирма е !");
				refApplicant.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
				//TODO Още нямаме нормална услуга с юридическо лице
				
				LOGGER.debug("APPLICANT Firm Name :" + sr.getApplicant().getLegal().getLegalData().getName());
				LOGGER.debug("APPLICANT Firm Name (Latin) :" + sr.getApplicant().getLegal().getLegalData().getNameLatin());
				
				
				String eik = sr.getApplicant().getLegal().getLegalData().getEik();
				if (eik == null && sr.getApplicant().getLegal().getLegalData().getIdentifier() != null && sr.getApplicant().getLegal().getLegalData().getIdentifier().getIdentifier() != null) {
					eik = sr.getApplicant().getLegal().getLegalData().getIdentifier().getIdentifier();
				}
				LOGGER.debug("APPLICANT Firm EIK : " + eik);
				
				refApplicant.setRefName(sr.getApplicant().getLegal().getLegalData().getName());
				refApplicant.setNflEik(eik);
				
				
			}
		
			//Адрес --- !!!! ЗА СЕГА по някаква неясна за мен причина, той винаги седи в частта на физическото лице !!!!
			
			if (sr.getApplicant().getPerson().getCorrAddress() != null && sr.getApplicant().getPerson().getCorrAddress().getAddress() != null && sr.getApplicant().getPerson().getCorrAddress().getAddress().getMunicipality() != null ) {
				
				
				ReferentAddress addr = new ReferentAddress();
				if (sr.getApplicant().getPerson().getCorrAddress() != null && sr.getApplicant().getPerson().getCorrAddress().getCountry().getCode() != null) {
					
					List<SystemClassif> all = sd.getItemsByCodeExt(BabhConstants.CODE_CLASSIF_COUNTRIES, sr.getApplicant().getPerson().getCorrAddress().getCountry().getCode(), BabhConstants.CODE_DEFAULT_LANG, new Date());
					if (all != null && all.size() > 0) {
						addr.setAddrCountry(all.get(0).getCode());
					}
				}
				
				Address eformiAddr = sr.getApplicant().getPerson().getCorrAddress().getAddress();
				if (eformiAddr.getSettlement() != null) {
					
					try {
						addr.setEkatte(Integer.parseInt(eformiAddr.getSettlement().getCode()));
					} catch (NumberFormatException e) {
						LOGGER.debug("Не може да се намери/разкодира ekatte код на населено място !");
						eCon.warnings.add("Не може да се намери/разкодира ekatte код на населено място !");
						
					}
					
				}
				
				if (eformiAddr.getPostCode() != null) {
					addr.setPostCode(eformiAddr.getPostCode());
				}
				
				if (eformiAddr.getPostBox() != null) {
					addr.setPostBox(eformiAddr.getPostBox());
				}
				
				if (eformiAddr.getArea() != null) {
					addr.setRaion(eformiAddr.getArea().getCode());
				}
				
				String restA = "";
				
				if (eformiAddr.getLocationName() != null) {						
					restA += eformiAddr.getLocationName() + ","; 
				}
				
				if (eformiAddr.getBuildingNumber() != null) {						
					restA += " №/блок " + eformiAddr.getBuildingNumber() + ","; 
				}
				
				if (eformiAddr.getEntrance() != null) {						
					restA += " вход " + eformiAddr.getEntrance() + ","; 
				}
				
				if (eformiAddr.getFloor() != null) {						
					restA += " етаж " + eformiAddr.getFloor() + ","; 
				}
				
				if (eformiAddr.getApartment() != null) {						
					restA += " ап.  " + eformiAddr.getApartment() + ","; 
				}
				
				if (restA.length() > 0) {
					restA = restA.substring(0, restA.length()-1);
				}
				
				addr.setAddrText(restA);
				//addr.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
					
				LOGGER.debug("APPLICANT Addres Country:  " + addr.getAddrCountry());
				LOGGER.debug("APPLICANT Addres City/Town:  " + addr.getEkatte());
				LOGGER.debug("APPLICANT Addres Area:  " + addr.getRaion());
				LOGGER.debug("APPLICANT Addres PostBox:  " + addr.getPostBox());
				LOGGER.debug("APPLICANT Addres PostCode:  " + addr.getPostCode());
				LOGGER.debug("APPLICANT Addres Addr:  " + addr.getAddrText());
				
				refApplicant.mergeAddress(addr, BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);            //setAddressKoresp(addr);
				
			}
		}

		
		
		//RequestAuthor
		if (sr != null && sr.getRequestAuthor() != null) {
			LOGGER.debug("RequestAuthor:");
			if (sr.getRequestAuthor().getPerson() != null) {
				LOGGER.debug("RequestAuthor Лице е !");
				
				refAuthor.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
				
				if (sr.getRequestAuthor().getPerson().getPersonalData().getIdentifier() != null) {
					
					LOGGER.debug("RequestAuthor  Идентификатор  :" + sr.getRequestAuthor().getPerson().getPersonalData().getIdentifier().getIdentifier());
					String indent = sr.getRequestAuthor().getPerson().getPersonalData().getIdentifier().getIdentifier();
					if (sr.getRequestAuthor().getPerson().getPersonalData().getIdentifier().getIdentifierType() != null && sr.getRequestAuthor().getPerson().getPersonalData().getIdentifier().getIdentifierType().getCode() != null) {
						LOGGER.debug("RequestAuthor  Идентификатор (тип) :" + sr.getRequestAuthor().getPerson().getPersonalData().getIdentifier().getIdentifierType().getCode());
						if (sr.getRequestAuthor().getPerson().getPersonalData().getIdentifier().getIdentifierType().getCode().equals("idn") ||
							sr.getRequestAuthor().getPerson().getPersonalData().getIdentifier().getIdentifierType().getCode().equals("1")) { //EGN
							refAuthor.setFzlEgn(indent);							
						}else {
							//За сега приемаме, че е ЛНЧ, защото само това има за лице във формите
							refAuthor.setFzlLnc(indent);
						}
					}else {
						//пак приемаме, че ако въобще има нещо, то то е егн
						refAuthor.setFzlEgn(indent);
					}
				}
				
				LOGGER.debug("RequestAuthor  First Name :" + sr.getRequestAuthor().getPerson().getPersonalData().getFirstName());
				refAuthor.setIme(sr.getRequestAuthor().getPerson().getPersonalData().getFirstName());
				LOGGER.debug("RequestAuthor  Middle Name :" + sr.getRequestAuthor().getPerson().getPersonalData().getMiddleName());
				refAuthor.setPrezime(sr.getRequestAuthor().getPerson().getPersonalData().getMiddleName());				
				LOGGER.debug("RequestAuthor  Family Name :" + sr.getRequestAuthor().getPerson().getPersonalData().getFamilyName());
				refAuthor.setFamilia(sr.getRequestAuthor().getPerson().getPersonalData().getFamilyName());
				
				if (sr.getRequestAuthor().getPerson().getPersonalData().getFullName() != null && ! sr.getRequestAuthor().getPerson().getPersonalData().getFullName().trim().isEmpty()) {
					LOGGER.debug("RequestAuthor  Full Name (Laitin) :" + sr.getRequestAuthor().getPerson().getPersonalData().getFullNameLatin());
					refAuthor.setRefName(sr.getRequestAuthor().getPerson().getPersonalData().getFullName());
				}else {
					refAuthor.setRefName(refAuthor.getIme() + " " + refAuthor.getPrezime() + " " + refAuthor.getFamilia());
				}
				
				
				if (sr.getRequestAuthor().getPerson().getPersonalData().getFirstNameLatin() != null && ! sr.getRequestAuthor().getPerson().getPersonalData().getFirstNameLatin().isEmpty()) {
					//Има латиница
					LOGGER.debug("RequestAuthor First Name (Latim) :" + sr.getRequestAuthor().getPerson().getPersonalData().getFirstNameLatin());
					LOGGER.debug("RequestAuthor Middle Name (Latim) :" + sr.getRequestAuthor().getPerson().getPersonalData().getMiddleNameLatin());
					LOGGER.debug("RequestAuthor Family Name (Latim) :" + sr.getRequestAuthor().getPerson().getPersonalData().getFamilyNameLatin());
					refAuthor.setRefLatin(sr.getRequestAuthor().getPerson().getPersonalData().getFirstNameLatin() + " " + sr.getRequestAuthor().getPerson().getPersonalData().getMiddleNameLatin() + " " + sr.getRequestAuthor().getPerson().getPersonalData().getFamilyNameLatin());
				}
				
			}else {
				LOGGER.debug("RequestAuthor  Фирма е !");
				
				refAuthor.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
				
				//TODO Още нямаме нормална услуга с юридическо лице				
			}
			
			
			if (sr.getAuthorType() != null && sr.getAuthorType().getCode() != null && eCon.doc != null) {
				LOGGER.debug("Качество на представлявано лице (Код): " + sr.getAuthorType().getCode());
				LOGGER.debug("Качество на представлявано лице (Текст): " + sr.getAuthorType().getName());
				List<SystemClassif> items = sd.getItemsByCodeExt(BabhConstants.CODE_CLASSIF_KACHESTVO_LICE, sr.getAuthorType().getCode(), BabhConstants.CODE_DEFAULT_LANG, new Date());
				if  (items != null && items.size() > 0) {
					LOGGER.debug("Качество на представлявано лице (Декодиран текст): " + items.get(0).getTekst());
					eCon.doc.setKachestvoLice(items.get(0).getCode());
				}				
			}
		}
		
		
		//Тук една малка хитринка 
		if (! SearchUtils.isEmpty(refApplicant.getFzlEgn()) && ! SearchUtils.isEmpty(refAuthor.getFzlEgn()) && refApplicant.getFzlEgn().equals(refAuthor.getFzlEgn()) ) {
			//с еднакви егн-та са
			if (SearchUtils.isEmpty(refApplicant.getRefLatin()) && ! SearchUtils.isEmpty(refAuthor.getRefLatin())) {
				refApplicant.setRefLatin(refAuthor.getRefLatin());
			}
			if (! SearchUtils.isEmpty(refApplicant.getRefLatin()) && SearchUtils.isEmpty(refAuthor.getRefLatin())) {
				refAuthor.setRefLatin(refApplicant.getRefLatin());
			}
		}
		if (! SearchUtils.isEmpty(refApplicant.getFzlLnc()) && ! SearchUtils.isEmpty(refAuthor.getFzlLnc()) && refApplicant.getFzlLnc().equals(refAuthor.getFzlLnc()) ) {
			//с еднакви лнч-та са
			if (SearchUtils.isEmpty(refApplicant.getRefLatin()) && ! SearchUtils.isEmpty(refAuthor.getRefLatin())) {
				refApplicant.setRefLatin(refAuthor.getRefLatin());
			}
			if (! SearchUtils.isEmpty(refApplicant.getRefLatin()) && SearchUtils.isEmpty(refAuthor.getRefLatin())) {
				refAuthor.setRefLatin(refApplicant.getRefLatin());
			}
		}
		
		String phone = null;
		String email = null;
		
		if (sr != null && sr.getRequestAuthor() != null &&  sr.getRequestAuthor().getCommunicationChannels() != null &&  sr.getRequestAuthor().getCommunicationChannels().getCommunicationChannel() != null && sr.getRequestAuthor().getCommunicationChannels().getCommunicationChannel().size() > 0) {
			LOGGER.debug("Точки за контакт: ");
			
			for (ContactPoint contact : sr.getRequestAuthor().getCommunicationChannels().getCommunicationChannel()) {
				
				if (contact.getContactType() != null && contact.getContactType().getCode().equals("1006-030006")) {
					//Електронна поща					
					if (contact.getContact() != null && ! contact.getContact().trim().isEmpty()) {
						email = contact.getContact().trim();
					}
				}
				
				if (contact.getContactType() != null && contact.getContactType().getCode().equals("1006-030005")) {
					//Телефон
					if (contact.getContact() != null && ! contact.getContact().trim().isEmpty()) {
						phone = contact.getContact().trim();
					}
				}
				
				
				
//				LOGGER.debug(contact.getContactType().getName() + "(" + contact.getContactType().getCode() +"): " + contact.getContact());
//				if (contact.getContactAddress() != null) {
//					LOGGER.debug("\t* Има адрес");
//				}
			}
			
		}else {
			LOGGER.debug("НЯМА Toчка за контакт за автор");
		}
		
		
		if (sr != null && sr.getApplicant() != null &&  sr.getApplicant().getCommunicationChannels() != null &&  sr.getApplicant().getCommunicationChannels().getCommunicationChannel() != null && sr.getApplicant().getCommunicationChannels().getCommunicationChannel().size() > 0) {
			LOGGER.debug("Точки за контакт: ");
			
			for (ContactPoint contact : sr.getApplicant().getCommunicationChannels().getCommunicationChannel()) {
				
				if (contact.getContactType() != null && contact.getContactType().getCode().equals("1006-030006")) {
					//Електронна поща					
					if (contact.getContact() != null && ! contact.getContact().trim().isEmpty()) {
						email = contact.getContact().trim();
					}
				}
				
				if (contact.getContactType() != null && contact.getContactType().getCode().equals("1006-030005")) {
					//Телефон
					if (contact.getContact() != null && ! contact.getContact().trim().isEmpty()) {
						phone = contact.getContact().trim();
					}
				}
				
				
				
//				LOGGER.debug(contact.getContactType().getName() + "(" + contact.getContactType().getCode() +"): " + contact.getContact());
//				if (contact.getContactAddress() != null) {
//					LOGGER.debug("\t* Има адрес");
//				}
			}
			
		}else {
			LOGGER.debug("НЯМА Toчка за контакт за автор");
		}
		
		
		
		
		
		if (email != null) {
			refApplicant.setContactEmail(email);
		}else {
			LOGGER.debug("НЯМА email в тoчки за контакт");
		}
		
		if (phone != null) {
			refApplicant.setContactPhone(phone);
		}else {
			LOGGER.debug("НЯМА телефон в тoчки за контакт");
		}
		
		
		
		//Идентификация от базата.
		ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
		Referent refDb = null;
		if (! SearchUtils.isEmpty(refAuthor.getFzlEgn())) {
			refDb = rdao.findByIdent(null, refAuthor.getFzlEgn(), null, BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
		}else {
			if (! SearchUtils.isEmpty(refAuthor.getFzlLnc())) {
				refDb = rdao.findByIdent(null, null, refAuthor.getFzlLnc(), BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
			}else {
				if (! SearchUtils.isEmpty(refAuthor.getNflEik())) {
					refDb = rdao.findByIdent(refAuthor.getNflEik(), null, null, BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
				}
			}
		}
		if (refDb != null) {
			eCon.ref1 = (mergeReferents(refDb, refAuthor));
		}else {
			eCon.ref1 = refAuthor;
		}
		
		
		
		
		refDb = null;
		if (! SearchUtils.isEmpty(refApplicant.getFzlEgn())) {
			refDb = rdao.findByIdent(null, refApplicant.getFzlEgn(), null, BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
		}else {
			if (! SearchUtils.isEmpty(refApplicant.getFzlLnc())) {
				refDb = rdao.findByIdent(null, null, refApplicant.getFzlLnc(), BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
			}else {
				if (! SearchUtils.isEmpty(refApplicant.getNflEik())) {
					refDb = rdao.findByIdent(refApplicant.getNflEik(), null, null, BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
				}
			}
		}
		if (refDb != null) {
			eCon.ref2 = (mergeReferents(refDb, refApplicant));
		}else {
			eCon.ref2 = refApplicant;
		}
		
		
		
		
		if (referentsOnly) {
			return eCon;
		}
		
		
		
		// Същинската част ...
		String codeUsluga = eCon.doc.getCodeAdmUsluga();
		//Public service
		if (sr.getPublicService() != null) {
			
			if (sr.getPublicService().getIdentifier() != null && sr.getPublicService().getIdentifier().getIdentifier() != null && ! sr.getPublicService().getIdentifier().getIdentifier().trim().isEmpty()) {
				codeUsluga = sr.getPublicService().getIdentifier().getIdentifier();
				eCon.doc.setCodeAdmUsluga(codeUsluga);
			}
			
			if (sr.getPublicService().getName() != null && ! sr.getPublicService().getName().trim().isEmpty()) {
				eCon.doc.setImeAdnUsluga(sr.getPublicService().getName());
			}
			
			if (sr.getRequestURI() != null && sr.getRequestURI().getIdentifier() != null && ! sr.getRequestURI().getIdentifier().trim().isEmpty()) {
				eCon.doc.setGuid(sr.getRequestURI().getIdentifier().trim());
			}
			
		}
		
		
		String vResult = null; 
		if (sr != null && sr.getResultChannel() != null) {
			vResult = sr.getResultChannel().getIdentifier();
			LOGGER.debug("КАНАЛ (ЗА РЕЗУЛТАТ)");
			LOGGER.debug("Идентификатор :" + sr.getResultChannel().getIdentifier());
			if (sr.getResultChannel().getChannelType() != null) {
				LOGGER.debug("RESULT channelType :" + sr.getResultChannel().getChannelType().getCode());
				LOGGER.debug("RESULT channelType (Текст) :" + sr.getResultChannel().getChannelType().getName());
				vResult = sr.getResultChannel().getChannelType().getCode();
			}
			
			if (vResult != null && !vResult.isEmpty()) {				
				List<SystemClassif> items = sd.getItemsByCodeExt(BabhConstants.CODE_CLASSIF_NACHIN_POLUCHAVANE, vResult, BabhConstants.CODE_DEFAULT_LANG, new Date());
				if  (items != null && items.size() > 0) {
					LOGGER.debug("RESULT channelType (Декодиран текст) :" + items.get(0).getTekst());
					eCon.doc.setReceiveMethod(items.get(0).getCode());
				}
				
				if (vResult.equals("0006-000076")) {   //eл. поща
					eCon.doc.setReceivedBy(sr.getResultChannel().getContactAddress());
				}
			}
			
			if (sr.getResultChannel().getContactAddress() != null) {
				eCon.doc.setAdressRezultat(sr.getResultChannel().getContactAddress());
			}
			
			if (sr.getResultChannel().getContactAddressCountry() != null) {
				
				List<SystemClassif> all = sd.getItemsByCodeExt(BabhConstants.CODE_CLASSIF_COUNTRIES, sr.getResultChannel().getContactAddressCountry().getCode(), BabhConstants.CODE_DEFAULT_LANG, new Date());
				if (all != null && all.size() > 0) {
					eCon.doc.setAdressRezultat("Държава: " + all.get(0).getTekst() + " " + eCon.doc.getAdressRezultat()  );
				}
				
				eCon.doc.setAdressRezultat(sr.getResultChannel().getContactAddress());
				LOGGER.debug("RESULT dopInfo : " + eCon.doc.getAdressRezultat());
			}
			
			
			
			
			
			System.out.println("Opening Hours :" + sr.getResultChannel().getOpeningHours());
			if (sr.getResultChannel().getProcessTime() != null) {
				for (ProcessTime pt : sr.getResultChannel().getProcessTime()) {
					System.out.println("ProcessTime :" + pt.getProcessTime() + "\t" + pt.getProcessTimeType().getDictionaryName() + "\t" + pt.getProcessTimeUnit().getCode());
				}
			}else {
				System.out.println("НЯМА ProcessTime");
			}
			
		}else {
			System.out.println("НЯМА ResultChannel");
		}
		
		
		
		
		Node node = ((Node) sr.getSpecificContent());
		
		proccessSpecificData(eCon, node, codeUsluga, sd);
		
//		System.out.println(sr.getSpecificContent().getClass());
//		
//		try {
//			StringWriter writer = new StringWriter();
//			Transformer transformer = TransformerFactory.newInstance().newTransformer();
//			transformer.transform(new DOMSource((Element)sr.getSpecificContent()), new StreamResult(writer));
//			String xml = writer.toString();
//			System.out.println("------------------------------------------------");	
//			System.out.println(xml);
//			
//			
//			
//			
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();		
//		}
		
		
		
		return eCon;
	}
	
	
	
	


	private EgovContainer proccessSpecificData(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException, InvalidParameterException{
		LOGGER.debug("Entering proccessSpecificData with codeUsluga= " + codeUsluga  );
		switch(codeUsluga) {
		  case "1589":   //Заявление за издаване на лиценз за производство но ветеринарномедицински продукт
			  proccessUsluga1589(container, node, codeUsluga, sd);
			  break;
			  
		  case "272":   //Заявление за регистрация на търговци на животни и зародишни продукти
			  proccessUsluga272(container, node, codeUsluga, sd);
			  break;
		  case "480":   //Заявление за регистрация на търговци на животни и зародишни продукти
			  proccessUsluga480(container, node, codeUsluga, sd);
			  break;
		  case "768":   //Заявление за регистрация на търговци на животни и зародишни продукти
			  proccessUsluga768(container, node, codeUsluga, sd);
			  break;
		  case "1352":   //Заявление за регистрация на търговци на животни и зародишни продукти
			  proccessUsluga1352(container, node, codeUsluga, sd);
			  break;
		  case "354":   //Заявление за издаване на разрешение за износ на животни и зародишни продукти
			  proccessUsluga354(container, node, codeUsluga, sd);
			  break;
			  
		  case "1110":   //Заявление за издаване на разрешително за провеждане на опити с животни
			  proccessUsluga1110(container, node, codeUsluga, sd);
			  break;
			  
		  case "274":   //Заявление за изплащане на обезщетение за убити и унищожени животни
			  proccessUsluga274(container, node, codeUsluga, sd);
			  break;
		  
		  case "2704":   //Заявление за регистрация на ВЛЗ
			  proccessUsluga2704(container, node, codeUsluga, sd);
			  break;
			  
		  case "2202":   //Заявление за прилагане на подмерки 4.1, 4.2, 5.1, 6.1 и 6.3 от ПРСР
			  proccessUsluga2202(container, node, codeUsluga, sd);
			  break;
		  case "499":   //0499_Заявление за издаване на разрешение за използване на СЖП-ПП за научни и други цели по чл. 271 от ЗВД
			  proccessUsluga499(container, node, codeUsluga, sd);
			  break;
		  case "2691":   // 2691-Издаване на сертификат за добра производствена практика и сертификат за произход и свободна продажба
			  proccessUsluga2691(container, node, codeUsluga, sd);
			  break;  
			  
		  case "2867":   // 2867_Заявление за регистрация на транспортно средство за превоз на СЖП и ПП по чл. 246 от ЗВД
			  proccessUsluga2867(container, node, codeUsluga, sd);
			  break; 
		  case "3239":   //3239 - Вписване в регистър на производителите и търговците на средства за идентификация на животнит
			  proccessUsluga3239(container, node, codeUsluga, sd);
			  break; 
		  case "1810":   //1810 - Издаване на сертификат за одобряване на транспортни средства за превоз на животни
			  proccessUsluga1810(container, node, codeUsluga, sd);
			  break; 
			 
		  case "3364":   // 3364 - Одобрение на учебен план и учебна програма в сферата на хуманно отношение към животните и суровото мляко
			  proccessUsluga3364(container, node, codeUsluga, sd);
			  break;  
			  
		  case "2705":   // 
			  proccessUsluga2705(container, node, codeUsluga, sd);
			  break;  
		
		  case "570":   // 570 - Одобряване на реклама на ветеринарномедицински продукт
			  proccessUsluga570(container, node, codeUsluga, sd);
			  break;  
		  case "705":   // 705 Издаване на лиценз за търговия на едро с ветеринарномедицински продукт
			  proccessUsluga705(container, node, codeUsluga, sd);
			  break; 
		  case "1365":   // 1365 Издаване на лиценз за търговия на дребно с ветеринарномедицински продукт
			  proccessUsluga1365(container, node, codeUsluga, sd);
			  break; 
			  
		  case "2802":   // 2802 Издаване на сертификат за регистрация на инвитро диагностични средства
			  proccessUsluga2802(container, node, codeUsluga, sd);
			  break;   
			    
			  
			  
		  default:
		    //Няма за сега
		}

		LOGGER.debug("Leaving proccessSpecificData ");
		return container;
	}
	
	
	
	
	
	
	


	/** Обработване на "1589 - Издаване на лиценз за производство но ветеринарномедицински продукт
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga1589(EgovContainer container, Node rootNode, String codeUsluga, SystemData sd) throws DbErrorException{
		LOGGER.debug("Entering proccessUsluga1589");
		
		
		//За сега само го печатаме
		//printNode(node, "");
		
		
		EventDeinostVlp edv = new EventDeinostVlp();
		edv.setObektDeinostDeinost(new ArrayList<ObektDeinostDeinost>());
		
		
		ArrayList<Node> rootChildren = findNodesByName(rootNode, "doubleAll", false);
		for (Node rootChild : rootChildren) {
			
			System.out.println("\r\n\r\n\r\n\r\n");
			
		
			ObektDeinostDeinost odd = new ObektDeinostDeinost();
			ObektDeinost od = new ObektDeinost();
			od.setObektDeinostLica(new ArrayList<ObektDeinostLica>());
			od.setPrednaznachenieList(new ArrayList<Integer>());
			
			
			ArrayList<Node> nodes = findNodesByName(rootChild, "objectName", false);
			for (Node node : nodes) {
				String val =  getSimpleNodeValue(node);
				LOGGER.debug("Адрес на обект: " + val);
				od.setNaimenovanie(val);
			}
			
			
			nodes = findNodesByName(rootChild, "objectUse", false);
			for (Node node : nodes) {
				String val =  getSimpleNodeValue(node);
				LOGGER.debug("Предназначения: " + val);
				od.getPrednaznachenieList().add(Integer.valueOf(val));
			}
			
			//<contractRegNumber>22</contractRegNumber>
            //<numberOfTechnologyLines>2</numberOfTechnologyLines>
			
			
			nodes = findNodesByName(rootChild, "contractRegNumber", false);
			for (Node node : nodes) {
				String val =  getSimpleNodeValue(node);
				LOGGER.debug("Възлагателен договор: " + val);
			}
            
			nodes = findNodesByName(rootChild, "numberOfTechnologyLines", false);
			for (Node node : nodes) {
				String val =  getSimpleNodeValue(node);
				LOGGER.debug("Брой технологични линии: " + val);
				
			}
		
		
			nodes = findNodesByName(rootChild, "relatedPersons", false);
			System.out.println("********************************* " + nodes.size());
			int cnt = 0;
			for (Node liceNode : nodes) {
				cnt++;
				
				Integer rolia = null;
				String roliaTekst = null;
				ArrayList<Node> childNode = findNodesByName(liceNode, "personRole", false);
				if (childNode.size() > 0) {
					String val =  getSimpleNodeValue(childNode.get(0));
					LOGGER.debug("Роля на лице: " + val);
					
					SystemClassif roleItem = sd.decodeItemFull(BabhConstants.CODE_CLASSIF_TIP_VRAZKA_LICE_VNOS_PROIZV_VLP, Integer.parseInt(val),  BabhConstants.CODE_DEFAULT_LANG, new Date(), false) ;
					if (roleItem != null ) {
						rolia = roleItem.getCode();
						roliaTekst = roleItem.getTekst();
					}else {
						container.warnings.add("Роля на лице с код " + val + " не може да бъде намерена в системната класификация !");
					}
					
				}
				
				
				
				String[] labels = {"egnLnchType", "egnLnch", "personName", "personMiddleName", "personFamilyName"}; 
				
				Referent tekLice = convertNodeToReferentFl(liceNode, labels, "********** " + roliaTekst + " " + cnt);
				
	//			ReferentAddress liceAddr = convertNodeToRefAddress(liceNode, roliaTekst + cnt );
	//			if (liceAddr != null) {
	//				liceAddr.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN);
	//				tekLice.setAddress(liceAddr);
	//			}
				
				childNode = findNodesByName(liceNode, "educationalQualificationDegree", false);
				if (childNode.size() > 0) {
					String val =  getSimpleNodeValue(childNode.get(0));
					LOGGER.debug("Образователно-квалификационна степен: " + val);
					if (tekLice != null){
						tekLice.setObrazovStepen(val);
					}
				}
				
				childNode = findNodesByName(liceNode, "professionalExperience", false);
				if (childNode.size() > 0) {
					String val =  getSimpleNodeValue(childNode.get(0));
					LOGGER.debug("Професионален опит за заеманата длъжност " + val);
					if (tekLice != null){
						tekLice.setOpit(val);
					}
				}
				
				
				ObektDeinostLica odln = new ObektDeinostLica();
				odln.setCodeRef(tekLice.getCode());
				odln.setDateBeg(DateUtils.systemMinDate());
				odln.setReferent(tekLice);
				odln.setRole(rolia);
				od.getObektDeinostLica().add(odln );
				
				
			}
			
			
			
			
			
			odd.setObektDeinost(od);
			edv.getObektDeinostDeinost().add(odd);
			
		}
		container.vpisvane.setEventDeinostVlp(edv);
		LOGGER.debug("Leaving proccessUsluga1589");
		return container;
	}
	
	
	
	/** Обработване на "272 - Заявление за регистрация на търговци на животни и зародишни продукти
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga272(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		ArrayList<Integer> deins = new ArrayList<Integer>();
		
		
		EventDeinJiv edj = new EventDeinJiv();
		edj.setEventDeinJivPredmet(new ArrayList<EventDeinJivPredmet>());
		
		
		
		//За сега само го печатаме
		//printNode(node, "");
		
		
		
		

		
		ArrayList<Node> nodes = findNodesByName(node, "activityChoice1", false);
		if (nodes.size() > 0) {
			for (Node child : nodes) {
				String val =  getSimpleNodeValue(child);
				LOGGER.debug("Търговия с животни: " + val);
				if (val != null && val.equalsIgnoreCase("true")) {
					deins.add(1);
				}
			}
		}
		
		
		nodes = findNodesByName(node, "activityChoice2", false);
		if (nodes.size() > 0) {
			for (Node child : nodes) {
				String val =  getSimpleNodeValue(child);
				LOGGER.debug("Търговия със зародишни продукти: " + val);
				if (val != null && val.equalsIgnoreCase("true")) {
					deins.add(2);
				}
			}
		}
		
		nodes = findNodesByName(node, "activityChoice105", false);
		if (nodes.size() > 0) {
			for (Node child : nodes) {
				String val =  getSimpleNodeValue(child);
				LOGGER.debug("Търговия с пратки СЖП и производни продукти по чл. 71 от ЗВД: " + val);
				if (val != null && val.equalsIgnoreCase("true")) {
					deins.add(105);
				}
			}
		}
		
		
		
		nodes = findNodesByName(node, "epizooticObjectRegNumber", true);
		if (nodes.size() > 0) {
			ObektDeinostDAO odDao = new ObektDeinostDAO(ActiveUser.DEFAULT);
			for (Node child : nodes) {
				String regNom = getSimpleNodeValue(child);
				System.out.println("СПЕЦИФИЧНИ ДАННИ Рег. Номер на ОЕЗ: " + regNom);
				if (regNom != null && ! regNom.trim().isEmpty()) {
					
					ObektDeinost od = odDao.findByRegNomer(regNom, null, false);
					if (od == null) {
						container.warnings.add("ОЕЗ с номер " + regNom + " не може да бъде намерен в БД");
						container.vpisvane.setInfo("ОЕЗ с номер " + regNom + " не може да бъде намерен в БД\\r\\n");
					}else {
						ObektDeinostDeinost odd = new ObektDeinostDeinost();
						odd.setObektDeinost(od);
						odd.setObektDeinostId(odd.getId());						
						List<ObektDeinostDeinost> allodd = new ArrayList<ObektDeinostDeinost>();
						allodd.add(odd);
						edj.setObektDeinostDeinost(allodd);
					}
					
				}
				
				
				
				
				
			}
		}
		
		
		
		//staroto
		nodes = findNodesByName(node, "activityType", true);
		if (nodes.size() > 0) {
			for (Node child : nodes) {
				String val =  getSimpleNodeValue(child);
				LOGGER.debug("Продукти: " + val  );
				if (val != null) {
					try {
						Integer codeVal = Integer.parseInt(val);						
						String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_PREDMET_TARG_JIV, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
						LOGGER.debug("Продукти (Декодирано): " + tekst  );

						EventDeinJivPredmet predmet = new EventDeinJivPredmet();
						predmet.setPredmet(codeVal);
						predmet.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_JIVOTNO );
						edj.getEventDeinJivPredmet().add(predmet);
						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		
		
		
		
		
		//Nowoto
		nodes = findNodesByName(node, "germProductsType", true);
		if (nodes.size() > 0) {
			for (Node child : nodes) {
				String val =  getSimpleNodeValue(child);
				LOGGER.debug("Продукти: " + val  );
				if (val != null) {
					try {
						Integer codeVal = Integer.parseInt(val);						
						String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_PREDMET_TARG_JIV, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
						LOGGER.debug("Продукти (Декодирано): " + tekst  );
						
						EventDeinJivPredmet predmet = new EventDeinJivPredmet();
						predmet.setPredmet(codeVal);
						predmet.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_JIVOTNO );
						edj.getEventDeinJivPredmet().add(predmet);
						
						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		
		
		nodes = findNodesByName(node, "dataGrid", true);
		if (nodes.size() > 0) {
			for (Node dataGrid : nodes) {
				
				Integer codeParent = null;
				Integer codeChild = null;
				String poiasnenie = null;
				
				ArrayList<Node> szp = findNodesByName(dataGrid, "SZP", false);
				if (nodes.size() > 0) {
					for (Node child : szp) {
						String val =  getSimpleNodeValue(child);
						
						if (val != null && ! val.trim().isEmpty()) {							
							codeParent = Integer.parseInt(val);
							LOGGER.debug("Родител СЖП: " + codeParent  );
							String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_FURAJI_SJP_PP, codeParent, BabhConstants.CODE_DEFAULT_LANG, new Date());
							LOGGER.debug("Родител СЖП (Декодирано): " + tekst  );
						}
					}
				}
				
				szp = findNodesIncludeInName(dataGrid, "level");
				if (nodes.size() > 0) {
					for (Node child : szp) {
						String val =  getSimpleNodeValue(child);
						
						if (val != null && ! val.trim().isEmpty()) {							
							codeChild = Integer.parseInt(val);
							LOGGER.debug("Дете СЖП: " + codeChild  );
							String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_FURAJI_SJP_PP, codeChild, BabhConstants.CODE_DEFAULT_LANG, new Date());
							LOGGER.debug("Дете СЖП (Декодирано): " + tekst  );
						}
					}
				}
				
				ArrayList<Node> poiasnenia = findNodesByName(dataGrid, "explanation", true);
				if (nodes.size() > 0) {
					for (Node child : poiasnenia) {
						poiasnenie =  getSimpleNodeValue(child);
						LOGGER.debug("Пояснение: " + poiasnenie);
						
					}
				}
				
				EventDeinJivPredmet predmet = new EventDeinJivPredmet ();
				predmet.setCodeClassif(BabhConstants.CODE_CLASSIF_FURAJI_SJP_PP );
				predmet.setDopInfo(poiasnenie);
				if (codeChild == null) {
					predmet.setPredmet(codeParent);
				}else {
					predmet.setPredmet(codeChild);
				}
				edj.getEventDeinJivPredmet().add(predmet);
				
			}
		}
		
		
		
		
		
		
		nodes = findNodesByName(node, "category", true);
		if (nodes.size() > 0) {
			for (Node child : nodes) {
				String val =  getSimpleNodeValue(child);
				LOGGER.debug("Категории: " + val  );
				if (val != null) {
					try {
						Integer codeVal = Integer.parseInt(val);						
						String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_CATEGORY_SJP, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
						LOGGER.debug("Категории (Декодирано): " + tekst  );
						
						edj.setCategoria(codeVal);
						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		
		nodes = findNodesByName(node, "shipmentsStorage", true);
		if (nodes.size() > 0) {
			for (Node child : nodes) {
				String val =  getSimpleNodeValue(child);
				LOGGER.debug("Категории: " + val  );
				if (val != null) {
					try {
						Integer codeVal = Integer.parseInt(val);						
						String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_CATEGORY_SJP, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
						LOGGER.debug("Категории (Декодирано): " + tekst  );
						
						edj.setSklad(codeVal);
						
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}
		
		
		
		edj.setVidList(new ArrayList<Integer>());
		for (Integer dein : deins) {
			edj.getVidList().add(dein);
		}
		
		
		
		
		
		//Тук по номера на оез се търси в базата 
		// Ако се намери
		//edj.getObektDeinostDeinost()
		
		
		container.vpisvane.setEventDeinJiv(edj);
		
		
		return container;
	}
	
	
	/** Обработване на "480 - УВЕДОМЛЕНИЕ за провеждане на мероприятие с животни
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga480(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		List<Integer> animals = new ArrayList<Integer>();
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinJiv edj = new EventDeinJiv();
		
		ArrayList<Node> nodes = findNodesByName(node, "eventType", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);				
				edj.setMeroptiatie(Integer.parseInt(val));
				LOGGER.debug("Вид Мероприятие: " + val);
			}
		}
		
		nodes = findNodesByName(node, "eventAddress", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Адрес: " + val);
				edj.setAdres(val);
			}
		}
		
		nodes = findNodesByName(node, "startDate", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);				
				
				try {
					Date datBeg = DateUtils.convertXmlDateStringToDate(val);
					LOGGER.debug("Начална дата: " + datBeg);
					edj.setDateBegMeropriatie(datBeg);
				} catch (ParseException e) {
					container.warnings.add("Началната дата " + val + " не може да бъде конвертирана !");
				}
				
			}
		}
		
		nodes = findNodesByName(node, "endDate", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				try {
					Date datEnd = DateUtils.convertXmlDateStringToDate(val);
					LOGGER.debug("Крайна дата: " + datEnd);
					edj.setDateEndMeropriatie(datEnd);
				} catch (ParseException e) {
					container.warnings.add("Крайната дата " + val + " не може да бъде конвертирана !");
				}
			}
		}
		
		nodes = findNodesByName(node, "animals", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Животно: " + val);
				animals.add(Integer.parseInt(val));
			}
		}
		
		nodes = findNodesByName(node, "additionalInfo", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Допълнителна информация: " + val);
				edj.setDopInfo(val);
			}
		}
		
		edj.setVidList(new ArrayList<Integer>());
		//edj.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_MEROPRIATIE_JIV);
		
		edj.setEventDeinJivPredmet(new ArrayList<EventDeinJivPredmet>());
		for (Integer vid : animals) {
			EventDeinJivPredmet predmet = new EventDeinJivPredmet();
			predmet.setPredmet(vid);
			edj.getEventDeinJivPredmet().add(predmet);
		}
		
		container.vpisvane.setEventDeinJiv(edj);
		
		
		
		
		return container;
	}
	
	
	/** Обработване на 768 Заявление за издаване на удостоверение за водачи и придружители
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga768(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinJiv edj = new EventDeinJiv();
		List<EventDeinJivLice> edjlica = new ArrayList<EventDeinJivLice>();
		
		ArrayList<Node> nodes = findNodesByName(node, "certificate", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Правоспособност за: " + val);
				EventDeinJivLice lice = new EventDeinJivLice();
				lice.setReferent(container.ref2);
				lice.setCodeRef(container.ref2.getId());
				lice.setTipVraz(Integer.parseInt(val));
				edjlica.add(lice);
			}
		}
		
		
		
		edj.setVidList(new ArrayList<Integer>());
		//edj.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PRAVOSPOSOBN_JIV);
		
		
		edj.setEventDeinJivLice(edjlica );
		
		container.vpisvane.setEventDeinJiv(edj);
		
		
		
		
		return container;
	}
	
	
	/** Обработване на 1352_Заявление за издаване на разрешение за превоз на живи животни
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 * @throws InvalidParameterException 
	 */
	private EgovContainer proccessUsluga1352(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException, InvalidParameterException{
		
		
		
		ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
		
		container.ref2.setReferentDocs(new ArrayList<ReferentDoc>());
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		 
				
				
		
		
		EventDeinJiv edj = new EventDeinJiv();
		List<EventDeinJivLice> edjlica = new ArrayList<EventDeinJivLice>();
		
		ArrayList<Node> nodes = findNodesByName(node, "typeOfTrip", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Вид пътуване: " + val);	
				edj.setPatuvane(Integer.parseInt(val));
			}
		}
		
		nodes = findNodesByName(node, "methodOfTransportation", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Начин на транспортиране: " + val);	
				edj.setNachinTransp(Integer.parseInt(val));
			}
		}
		
		String internalLicNom = null;
		Date internalLicDat = null;
		nodes = findNodesByName(node, "licenseNumberInternalTransport", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Вътрешен Лизенз номер: " + val);
				internalLicNom = val;
				
			}
		}
		nodes = findNodesByName(node, "licenseDateInternalTransport", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Вътрешен Лизенз дата: " + val);
				
				try {
					internalLicDat = DateUtils.convertXmlDateStringToDate(val);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			}
		}
		
		
		if (internalLicNom != null && ! internalLicNom.trim().isEmpty()) {
			
			ReferentDoc lic = new ReferentDoc();
			lic.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_VPREVOZ  );
			lic.setNomDoc(internalLicNom);
			lic.setDateIssued(internalLicDat);
			
			container.ref2.setReferentDocs(new ArrayList<ReferentDoc>());
			
			if (container.ref2.getId() != null) {
				//лицето е го има в базата
				ReferentDoc licDb = rdao.findReferentDoc(internalLicNom, lic.getVidDoc(), container.ref2.getId());
				if (licDb != null) {					
					licDb = mergeDoc(licDb, lic);
					container.ref2.getReferentDocs().add(licDb);
				}else {
					container.ref2.getReferentDocs().add(lic);
				}
			}else {
				container.ref2.getReferentDocs().add(lic);
			}
		}
		
		
		
		
		String mejdLicNom = null;
		Date mejdLicDat = null;
		nodes = findNodesByName(node, "licenseNumber", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);	
				//System.out.println("licenseNumber: **********************" + child.getNodeName());				
				LOGGER.debug("Международен Лизенз номер: " + val);
				mejdLicNom = val;
				
			}
		}
		nodes = findNodesByName(node, "licenseDate", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {				
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Международен Лизенз дата: " + val);
				try {
					mejdLicDat = DateUtils.convertXmlDateStringToDate(val);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		
		if (mejdLicNom != null && ! mejdLicNom.trim().isEmpty()) {
			
			ReferentDoc lic = new ReferentDoc();
			lic.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_LICENZ_MPREVOZ   );
			lic.setNomDoc(mejdLicNom);
			lic.setDateIssued(mejdLicDat);
			
			
			
			if (container.ref2.getId() != null) {
				//лицето е го има в базата
				ReferentDoc licDb = rdao.findReferentDoc(internalLicNom, lic.getVidDoc(), container.ref2.getId());
				if (licDb != null) {					
					licDb = mergeDoc(licDb, lic);
					container.ref2.getReferentDocs().add(licDb);
				}else {
					container.ref2.getReferentDocs().add(lic);
				}
			}else {
				container.ref2.getReferentDocs().add(lic);
			}
		}
		
		
		
		List<MpsDeinost> allMD = new ArrayList<MpsDeinost>(); 
		nodes = findNodesByName(node, "carRegNumber", false);
		if (nodes.size() > 0) {			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Регистрационен номер: " + val);
				
				Object[] mpsInfo = new MpsDAO(ActiveUser.DEFAULT).findMpsInfoByIdOrNomer(null, val.trim());
				if (mpsInfo != null) {		
					MpsDeinost md = new MpsDeinost();
					md.setMpsId(SearchUtils.asInteger(mpsInfo[0]));
					md.setMpsInfo(mpsInfo);
					allMD.add(md);
					
				}else {
					container.warnings.add("Номер на автомобил " + val + " не е намерен !");
				}
			}
		}
		edj.setMpsDeinost(allMD);
		
		
		ArrayList<Node> animalNodes = findNodesByName(node, "carRegNumberData1", false);
		if (animalNodes.size() > 0) {		
			
			edj.setEventDeinJivPredmet(new ArrayList<EventDeinJivPredmet>());
			
			for (Node anode : animalNodes) {
				
				ArrayList<Node> typeNodes = findNodesByName(anode, "animalType", false);
				ArrayList<Node> vidNodes = findNodesByName(anode, "animalGroup", true);
				
				if (vidNodes == null || vidNodes.size() == 0) {
					//Има само първо ниво
					for (Node tnode : typeNodes) {
						String val = getSimpleNodeValue(tnode);
						LOGGER.debug("ЖивотноT: " + val);	
						EventDeinJivPredmet edjp = new EventDeinJivPredmet();
						edjp.setPredmet(Integer.parseInt(val));
						edjp.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_JIVOTNO );
						edj.getEventDeinJivPredmet().add(edjp);
						
					}
				}else {
					//Има и второ - ползваме него
					for (Node vnode : vidNodes) {
						String val = getSimpleNodeValue(vnode);
						LOGGER.debug("Животно: " + val);
						EventDeinJivPredmet edjp = new EventDeinJivPredmet();
						edjp.setPredmet(Integer.parseInt(val));
						edjp.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_JIVOTNO );
						edj.getEventDeinJivPredmet().add(edjp);
						String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_VID_JIVOTNO, edjp.getPredmet(), BabhConstants.CODE_DEFAULT_LANG, new Date());
						LOGGER.debug("Животно (Декодирано): " + tekst  );
						
					}
				}
				
				
			}
				
				
		}
		
		
		
		edj.setVidList(new ArrayList<Integer>());
		//edj.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_PREVOZ_JIV);
		
		
		edj.setEventDeinJivLice(edjlica );
		
		container.vpisvane.setEventDeinJiv(edj);
		
		
		//TODO Лицензите към референта :(((((((((((((((((((((((((((((((((((((((((
		
		
		
		
		return container;
	}
	
	
	/** Обработване на 354_Заявление за издаване на разрешение за износ на животни и зародишни продукти
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga354(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		
		
		EventDeinJiv edj = new EventDeinJiv();
		edj.setDarjList(new ArrayList<Integer>());
		
		ArrayList<Node> nodes = findNodesByName(node, "exportGoods", true);
		if (nodes.size() > 0) {			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("ИЗНАСЯНЕ на : " + val);
				edj.setIznos(val);
			}
		}
		
		nodes = findNodesByName(node, "exportCountries", true);
		if (nodes.size() > 0) {			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Държави : " + val);
				edj.getDarjList().add(Integer.parseInt(val));
			}
		}
			
			
		ObektDeinostDAO odao = new ObektDeinostDAO(ActiveUser.DEFAULT);
		nodes = findNodesByName(node, "oezRegNumber", true);
		if (nodes.size() > 0) {
			List<ObektDeinostDeinost> deinosti = new ArrayList<ObektDeinostDeinost>();	
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Рег номер на ОЕЗ : " + val);
				
				if (val != null && ! val.trim().isEmpty()) {
					List<ObektDeinost> result = odao.findByIdent(val, BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ, false);
					ObektDeinost od = null;
					if (result != null && result.size() > 0) {
						od = result.get(0);
						
											
						ObektDeinostDeinost  odd = new ObektDeinostDeinost ();
						odd.setObektDeinost(od);
						deinosti.add(odd);
						edj.setObektDeinostDeinost(deinosti);
						break; // Станло е единичен
					}else {
						container.warnings.add("ОЕЗ с номер " + val + " не може да бъде открит в БД !");
						break; // Станло е единичен
					}
					
					
				}
				
			}
		}
		
		
		nodes = findNodesByName(node, "conditions", true);
		if (nodes.size() > 0) {
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Условия на заготовка : " + val);
				edj.setNachinMqstoPridobiv(val);
			}
		}
		
		edj.setGkppList(new ArrayList<Integer>());
		nodes = findNodesByName(node, "checkPoint", true);
		if (nodes.size() > 0) {
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Изходящ ГКПП : " + val);
				edj.getGkppList().add(Integer.valueOf(val));
			}
		}
			
		edj.setVidList(new ArrayList<Integer>());
		//edj.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_IZNOS_JIV);
		
		
		
		
		container.vpisvane.setEventDeinJiv(edj);
		return container;
	}
	
	
	/** Обработване на 1110_Заявление за издаване на разрешително за провеждане на опити с животни
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga1110(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinJiv edj = new EventDeinJiv();
		
		
		ArrayList<Node> nodes = findNodesByName(node, "methodAndPlaceOfAcquisitionOfTheAnimals", true);
		if (nodes.size() > 0) {			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Начин и място на придобиване: " + val);
				edj.setNachinMqstoPridobiv(val);
			}
		}
		
		nodes = findNodesByName(node, "projectGoal", true);
		if (nodes.size() > 0) {			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Цел на проекта: " + val);
				edj.setCel(val);
			}
		}
		
		
		//"1210800016"
		ObektDeinostDAO odao = new ObektDeinostDAO(ActiveUser.DEFAULT);
		nodes = findNodesByName(node, "oezRegNumber", true);   //TODO да се смени с истинския лейбъл
		if (nodes.size() > 0) {
			List<ObektDeinostDeinost> deinosti = new ArrayList<ObektDeinostDeinost>();	
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Рег номер на ОЕЗ : " + val);
				
				if (val != null && ! val.trim().isEmpty()) {
					List<ObektDeinost> result = odao.findByIdent(val, BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ, false);
					ObektDeinost od = null;
					if (result != null && result.size() > 0) {
						od = result.get(0);
						
											
						ObektDeinostDeinost  odd = new ObektDeinostDeinost ();
						odd.setObektDeinost(od);
						deinosti.add(odd);
						edj.setObektDeinostDeinost(deinosti);
						break; // Станло е единичен
					}else {
						container.warnings.add("ОЕЗ с номер " + val + " не може да бъде открит в БД !");
						break; // Станло е единичен
					}
					
					
				}
				
			}
		}
		
			
		nodes = findNodesByName(node, "experimentAddtionalInfo", true);
		if (nodes.size() > 0) {			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Допълнителна информация: " + val);
				edj.setOpisanieCyr(val);
			}
		}
		
		
		
		
		edj.setEventDeinJivPredmet(new ArrayList<EventDeinJivPredmet>());
		nodes = findNodesByName(node, "animals", false);
		if (nodes.size() > 0) {			
			for (Node child : nodes) {
				System.out.println();
				
				EventDeinJivPredmet edjp = new EventDeinJivPredmet();
				
				ArrayList<Node> childNodes = findNodesByName(child, "animal", false);
				if (childNodes.size() > 0) {			
					for (Node animal : childNodes) {
						String val = getSimpleNodeValue(animal);
						LOGGER.debug("Животно: " + val);
						edjp.setPredmet(Integer.parseInt(val));
						edjp.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_JIVOTNO );
					}
				}
				
				childNodes = findNodesByName(child, "animalCount", false);
				if (childNodes.size() > 0) {			
					for (Node count : childNodes) {
						String val = getSimpleNodeValue(count);
						LOGGER.debug("Животно брой: " + val);
						edjp.setBroi(Integer.parseInt(val));
					}
				}
				edj.getEventDeinJivPredmet().add(edjp);
				System.out.println();
			}
		}
		
		
		List<EventDeinJivLice> lica = new ArrayList<EventDeinJivLice>();
		nodes = findNodesByName(node, "veterinaryDoctors", false);
		if (nodes.size() > 0) {			
			for (Node child : nodes) {
				System.out.println();
				
				String tipIdent = null;
				String ident = null;
				String name = null;
				String family = null;
				Integer pos = null;
				
				ArrayList<Node> childNodes = findNodesByName(child, "vetIdType", false);
				if (childNodes.size() > 0) {			
					for (Node tek : childNodes) {
						String val = getSimpleNodeValue(tek);
						LOGGER.debug("vetIdType: " + val);
						tipIdent = val;
					}
				}
				
				childNodes = findNodesByName(child, "vetId", false);
				if (childNodes.size() > 0) {			
					for (Node tek : childNodes) {
						String val = getSimpleNodeValue(tek);
						LOGGER.debug("vetId: " + val);
						ident = val;
					}
				}
				
				childNodes = findNodesByName(child, "vetName", false);
				if (childNodes.size() > 0) {			
					for (Node tek : childNodes) {
						String val = getSimpleNodeValue(tek);
						LOGGER.debug("vetName: " + val);
						name = val;
					}
				}
				
				childNodes = findNodesByName(child, "vetFamilyName", false);
				if (childNodes.size() > 0) {			
					for (Node tek : childNodes) {
						String val = getSimpleNodeValue(tek);
						LOGGER.debug("vetFamilyName: " + val);
						family = val;
					}
				}
				
				childNodes = findNodesByName(child, "teamPosition", false);
				if (childNodes.size() > 0) {			
					for (Node tek : childNodes) {
						String val = getSimpleNodeValue(tek);
						LOGGER.debug("teamPosition: " + val);
						pos = Integer.parseInt(val);
					}
				}
				
				
				if (ident != null && tipIdent != null) {
					EventDeinJivLice edjl = new EventDeinJivLice();
					edjl.setTipVraz(pos);
					
					
					ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
					Referent refDb = null;
					
					if (tipIdent.equals("idn")) {
						refDb = rdao.findByIdent(null, ident, null, BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
					}else {
						refDb = rdao.findByIdent(null, null, ident, BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);

					}
					
					if (refDb == null) {
						refDb = new Referent();
					}
					
					refDb.setIme(name);
					refDb.setFamilia(family);
					
					refDb.setRefName(name + " " + family);
					
					if (tipIdent.equals("idn")) {
						refDb.setFzlEgn(ident);
					}else {
						refDb.setFzlLnc(ident);
					}
					
					edjl.setReferent(refDb);
					edjl.setCodeRef(refDb.getCode());
					lica.add(edjl);
					
				}
				
				
				System.out.println();
			}
		}

		
		
		edj.setVidList(new ArrayList<Integer>());		
		edj.setEventDeinJivLice(lica);
		
		
		
		container.vpisvane.setEventDeinJiv(edj);
		return container;
	}
	
	
	/** Обработване на 274_Заявление за изплащане на обезщетение за убити и унищожени животни
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga274(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinJiv edj = new EventDeinJiv();
		
		edj.setEventDeinJivPredmet(new ArrayList<EventDeinJivPredmet>());
		
		
		
		
		
		
		
		ObektDeinostDAO odao = new ObektDeinostDAO(ActiveUser.DEFAULT);
		ArrayList<Node> nodes = findNodesByName(node, "oezRegNumber", false);   //TODO да се смени с истинския лейбъл
		if (nodes.size() > 0) {
			List<ObektDeinostDeinost> deinosti = new ArrayList<ObektDeinostDeinost>();	
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Рег номер на ОЕЗ : " + val);
				
				if (val != null && ! val.trim().isEmpty()) {
					List<ObektDeinost> result = odao.findByIdent(val, BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ, false);
					ObektDeinost od = null;
					if (result != null && result.size() > 0) {
						od = result.get(0);
						
											
						ObektDeinostDeinost  odd = new ObektDeinostDeinost ();
						odd.setObektDeinost(od);
						deinosti.add(odd);
						edj.setObektDeinostDeinost(deinosti);
						break; // Станло е единичен
					}else {
						container.warnings.add("ОЕЗ с номер " + val + " не може да бъде открит в БД !");
						break; // Станло е единичен
					}
					
					
				}
				
			}
		}
		
		nodes = findNodesByName(node, "additionalInfo", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Допълнителна информация: " + val);	
				edj.setDopInfo(val);
			}
		}
		
		nodes = findNodesByName(node, "caseExplanation", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Описание: " + val);	
				edj.setOpisanieCyr(val);
				
			}
		}
		
		
		ArrayList<Node> animalNodes = findNodesByName(node, "forciblyKilledAnimals", true);
		if (animalNodes.size() > 0) {
			
			for (Node animalNode : animalNodes) {
				
				Integer aType = null;
				Integer aCount = null;
				
				ArrayList<Node> animalTypes = findNodesByName(animalNode, "animalType", true);
				if (animalTypes.size() > 0) {					
					for (Node animalType : animalTypes) {
						String val = getSimpleNodeValue(animalType);
						Integer codeVal = Integer.parseInt(val);	
						LOGGER.debug("Тип животно: " + codeVal  );
						String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_VID_JIVOTNO, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
						LOGGER.debug("Тип животно: " + tekst  );	
						aType = codeVal;
					}
				}
				
				ArrayList<Node> animalCounts = findNodesByName(animalNode, "count", true);
				if (animalCounts.size() > 0) {					
					for (Node countNode : animalCounts) {
						String val = getSimpleNodeValue(countNode);
						aCount = Integer.parseInt(val);	
						LOGGER.debug("Брой: " + aCount  );
						
					}
				}
				
				EventDeinJivPredmet predmet = new EventDeinJivPredmet();
				predmet.setPredmet(aType);
				predmet.setBroi(aCount);
				predmet.setCodeClassif(BabhConstants.CODE_CLASSIF_VID_JIVOTNO);
				edj.getEventDeinJivPredmet().add(predmet);			
			}
		}
		
		
		edj.setVidList(new ArrayList<Integer>());
		//edj.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBEZSHЕТЕНИЕ_JIV);
		
		
		
		
		container.vpisvane.setEventDeinJiv(edj);
		
		
		
		
		return container;
	}
	
	
	
	
	
	/** Обработване на 2704_Заявление за регистрация на ВЛЗ
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 * @throws InvalidParameterException 
	 */
	private EgovContainer proccessUsluga2704(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException, InvalidParameterException{
		
		
		
		
		
		//За сега само го печатаме
		//printNode(node, "");	
		
		ObektDeinost vlz = new ObektDeinost();
		vlz.setVid(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLZ);
		vlz.setObektDeinostLica(new ArrayList<ObektDeinostLica>() );
		
		EventDeinJiv edj = new EventDeinJiv();
		
		
		ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
		
		
		
		
		ArrayList<Node> nodes = findNodesByName(node, "veterinaryMedicalFacilityName", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("ВЛЗ Наименование: " + val);	
				vlz.setNaimenovanie(val);
			}
		}
		
		nodes = findNodesByName(node, "veterinaryMedicalFacilityType", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("ВЛЗ Тип: " + val);	
				vlz.setVidVlz(Integer.parseInt(val));
			}
		}
		
		
		nodes = findNodesByName(node, "animalType", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Вид животно: " + val);	
				vlz.setObslujvaniJiv(Integer.parseInt(val));
				
			}
		}
		
		nodes = findNodesByName(node, "imagingDiagnosticsSector", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Наличие на сектор за образна диагностика: " + val);	
				vlz.setSektorObrDiag(Integer.parseInt(val));
			}
		}
		
		nodes = findNodesByName(node, "physiotherapySector", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Наличие на сектор за физиотерапия: " + val);		
				vlz.setSektorFizio(Integer.parseInt(val));
			}
		}
		
		nodes = findNodesByName(node, "hospital", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Наличие на стационар: " + val);	
				vlz.setStacionar(Integer.parseInt(val));
			}
		}
		
		nodes = findNodesByName(node, "researchTypeInfo", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Видове изследвания: " + val);		
				vlz.setVidIzsl(val);
			}
		}
		//vlz.setVidIzsl("test");
		
		ArrayList<Node> nodesAddr = findNodesByName(node, "changeMeAddress", false);
		if (nodesAddr.size() > 0) {
			
			for (Node child : nodesAddr) {
				ReferentAddress temp = convertNodeToRefAddress(child, "Адрес ВЛЗ " );
				
				vlz.setAddress(temp.getAddrText());
				vlz.setDarj(temp.getAddrCountry());
				vlz.setNasMesto(temp.getEkatte());
				vlz.setPostCode(temp.getPostCode());
			}
				
		}
		
		
		///////////////////////////// УПРАВИТЕЛ /////////////////////////////////////////////////
		
		
		String[] labels = {"managerIdType", "managerId", "managerName", "managerFamilyName", "managerMiddleName"}; 
		
		Referent manager = convertNodeToReferentFl(node, labels, "********** Управител на ВЛЗ");
		
		ObektDeinostLica edl = new ObektDeinostLica();
		edl.setRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_UPRAVITEL);
		edl.setReferent(manager);
		edl.setCodeRef(manager.getCode());
		vlz.getObektDeinostLica().add(edl );
		
		//Диплома на управител
		ReferentDoc diploma = new ReferentDoc();
		diploma.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA );
				
		nodes = findNodesByName(node, "managerDiplomNumber", true);
		if (nodes.size() > 0) {			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Управител на ВЛЗ Диплома/Номер на диплома : " + val);
				diploma.setNomDoc(val);
			}
		}
		
		nodes = findNodesByName(node, "managerDiplomIssueDate", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Управител на ВЛЗ Диплома/Дата на издаване : " + val);
				try {
					diploma.setDateIssued(DateUtils.convertXmlDateStringToDate(val));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		nodes = findNodesByName(node, "managerDiplomFromUnivercity", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Управител на ВЛЗ Диплома/Издадена от : " + val);
				diploma.setIssuedBy(val);
			}
		}
		
		if (diploma != null && diploma.getNomDoc() != null && ! diploma.getNomDoc().trim().isEmpty()) {
			manager.setReferentDocs(new ArrayList<ReferentDoc>());
			
			if (manager.getId() != null) {
				//лицето е го има в базата
				ReferentDoc diplomaDb = rdao.findReferentDoc(diploma.getNomDoc(), diploma.getVidDoc(), manager.getId());
				if (diplomaDb != null) {					
					diplomaDb = mergeDoc(diplomaDb, diploma);
					manager.getReferentDocs().add(diplomaDb);
				}else {
					manager.getReferentDocs().add(diploma);
				}
			}else {
				manager.getReferentDocs().add(diploma);
			}
		}
		
		
		///////////////////////////// Доктори  /////////////////////////////////////////////////
		
		
		ArrayList<Node> vDoctors = findNodesByName(node, "veterinaryDoctors", true);
		if (vDoctors.size() > 0) {
			int cnt = 0;
			for (Node vDocNode : vDoctors) {
				cnt ++;
				System.out.println();
				
				
				String[] labelsV = {"vetIdType", "vetId", "vetName", "vetFamilyName", "vetMiddleName", "vetURN"}; 				
				Referent vetDoc = convertNodeToReferentFl(vDocNode, labelsV, "********** Ветеринарен Лекар ");				
				ReferentAddress vetAddr = convertNodeToRefAddress(vDocNode, "Ветеринарен Лекар " + cnt );
				if (vetAddr != null) {					
					vetDoc.mergeAddress(vetAddr, BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN) ;              ///setAddress(vetAddr);
				}
				
				
				ObektDeinostLica edlV = new ObektDeinostLica();
				edlV.setRole(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_VET_LEKAR);
				edlV.setReferent(vetDoc);
				edlV.setCodeRef(vetDoc.getCode());
				vlz.getObektDeinostLica().add(edlV );
				
				vetDoc.setReferentDocs(new ArrayList<ReferentDoc>());
				
				
				//Удостоверение от БВС
				ReferentDoc udostoverenie = new ReferentDoc();
				udostoverenie.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_UDOST_BVS  );
						
				nodes = findNodesByName(vDocNode, "certificateNumber", true);
				if (nodes.size() > 0) {			
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Ветеринарен Лекар "+cnt+" Удостоверение от БВС/Номер на диплома : " + val);
						udostoverenie.setNomDoc(val);
					}
				}
				
				nodes = findNodesByName(vDocNode, "certificateIssueDate", true);
				if (nodes.size() > 0) {
					
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Ветеринарен Лекар "+cnt+" Удостоверение от БВС/Дата на издаване : " + val);
						try {
							udostoverenie.setDateIssued(DateUtils.convertXmlDateStringToDate(val));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				nodes = findNodesByName(vDocNode, "vetUnionName", true);
				if (nodes.size() > 0) {
					
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Ветеринарен Лекар "+cnt+" Удостоверение от БВС/Издадена от : " + val);
						udostoverenie.setIssuedBy(val);
					}
				}
				
				if (udostoverenie != null && udostoverenie.getNomDoc() != null && ! udostoverenie.getNomDoc().trim().isEmpty()) {
					
					if (vetDoc.getId() != null) {
						ReferentDoc udostoverenieDb = rdao.findReferentDoc(udostoverenie.getNomDoc(), udostoverenie.getVidDoc(), vetDoc.getId() );
						if (udostoverenieDb != null) {
							udostoverenieDb = mergeDoc(udostoverenieDb, udostoverenie);
							vetDoc.getReferentDocs().add(udostoverenieDb);
						}else {
							vetDoc.getReferentDocs().add(udostoverenie);
						}
					}else {
						vetDoc.getReferentDocs().add(udostoverenie);
					}
				}
				
				
				
				
				//Диплома
				ReferentDoc diplomaV = new ReferentDoc();
				diplomaV.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA );
						
				nodes = findNodesByName(vDocNode, "vetDiplomNumber", true);
				if (nodes.size() > 0) {			
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Ветеринарен Лекар "+cnt+" Диплома/Номер на диплома : " + val);
						diplomaV.setNomDoc(val);
					}
				}
				
				nodes = findNodesByName(vDocNode, "vetDiplomIssueDate", true);
				if (nodes.size() > 0) {
					
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Ветеринарен Лекар "+cnt+" Диплома/Дата на издаване : " + val);
						try {
							diplomaV.setDateIssued(DateUtils.convertXmlDateStringToDate(val));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				nodes = findNodesByName(vDocNode, "vetDiplomFromUnivercity", true);
				if (nodes.size() > 0) {
					
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Ветеринарен Лекар "+cnt+" Диплома/Издадена от : " + val);
						diplomaV.setIssuedBy(val);
					}
				}
				
				
				if (diplomaV != null && diplomaV.getNomDoc() != null && ! diplomaV.getNomDoc().trim().isEmpty()) {							
					
					if (vetDoc.getId() != null) {
						ReferentDoc diplomaVDb = rdao.findReferentDoc(diplomaV.getNomDoc(), diplomaV.getVidDoc(), vetDoc.getId() );
						if (diplomaVDb != null) {
							diplomaVDb = mergeDoc(diplomaVDb, diplomaV);
							vetDoc.getReferentDocs().add(diplomaVDb);
						}else {
							vetDoc.getReferentDocs().add(diplomaV);
						}
					}else {
						vetDoc.getReferentDocs().add(diplomaV);
					}
				}
				
				
				
				
				
				
				
				
				System.out.println();
			}
			
		}
		
		
		
		edj.setVidList(new ArrayList<Integer>());
		//edj.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_);   //TODO
		
		
		
		container.vpisvane.setObektDeinost(vlz );
		container.vpisvane.setEventDeinJiv(edj);
		
		
		
		
		return container;
	}
	
	
	


	/** Обработване на 2202_Заявление за прилагане на подмерки 4.1, 4.2, 5.1, 6.1 и 6.3 от ПРСР
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga2202(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		List<Integer> animals = new ArrayList<Integer>();
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinostFuraji edf = new EventDeinostFuraji();
		
		ArrayList<Node> nodes = findNodesByName(node, "subMeasure", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Мярка: " + val);
				edf.setMiarka(Integer.parseInt(val));
			}
		}
		
		nodes = findNodesByName(node, "propertyRegNumber", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Регистрационен номер: " + val);
				//edf.setNomObektBg(val);
			}
		}
		
		
		
		nodes = findNodesByName(node, "animals", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Животно: " + val);
				animals.add(Integer.valueOf(val));
			}
		}
		
		edf.setVidList(new ArrayList<Integer>());
		//edf.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_STAN_PODMERKI );
		
		edf.setEventDeinostFurajiPrednaznJiv(new ArrayList<EventDeinostFurajiPrednaznJiv>());
		for (Integer vid : animals) {
			EventDeinostFurajiPrednaznJiv  jiv = new EventDeinostFurajiPrednaznJiv ();
			jiv.setVidJiv(vid);
			edf.getEventDeinostFurajiPrednaznJiv().add(jiv);
		}
		
		container.vpisvane.setEventDeinostFuraji(edf);
		
		
		
		
		return container;
	}
	
	
	
	/** Обработване на 0499_Заявление за издаване на разрешение за използване на СЖП-ПП за научни и други цели по чл. 271 от ЗВД
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga499(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		ObektDeinostDAO odao = new ObektDeinostDAO(ActiveUser.DEFAULT);
		
		
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinostFuraji edf = new EventDeinostFuraji();
		
		edf.setEventDeinostFurajiPredmet(new ArrayList<EventDeinostFurajiPredmet>());
		
		edf.setCelList(new ArrayList<Integer>());
		
		
		
		
		ArrayList<Node> nodes = findNodesByName(node, "razreshenie", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Разрешение: " + val);
				//edfp.setka
				
			}
		}
		
		nodes = findNodesByName(node, "category", false);
		if (nodes.size() > 0) {
			edf.setKategoriaList(new ArrayList<>());
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Категория: " + val); // TODO категорията е множествена
//				EventDeinostFurajiPredmet edfp = new EventDeinostFurajiPredmet();
//				edfp.setKategoria(Integer.parseInt(val));
				edf.getKategoriaList().add(Integer.parseInt(val));
			}
		}
		
		
		
		nodes = findNodesByName(node, "activityGoals", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Цели на дейността: " + val);
				edf.getCelList().add(Integer.parseInt(val));
				
			}
		}
		
		
		
		nodes = findNodesByName(node, "activityDescription", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Описание на дейността: " + val);
				edf.setOpisanie(val);
				
			}
		}
		
		nodes = findNodesByName(node, "objectRegNumber", true);
		if (nodes.size() > 0) {
			List<ObektDeinostDeinost> deinosti = new ArrayList<ObektDeinostDeinost>();
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Номер на оез: " + val);
				
				if (val != null && ! val.trim().isEmpty()) {
					List<ObektDeinost> result = odao.findByIdent(val, BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ, false);
					ObektDeinost od = null;
					if (result != null && result.size() > 0) {
						od = result.get(0);
																
						ObektDeinostDeinost  odd = new ObektDeinostDeinost ();
						odd.setObektDeinost(od);
						deinosti.add(odd);
					}else {
						container.warnings.add("ОЕЗ с номер " + val + " не може да бъде открит в БД !");
						
					}
					
					
				}
				
			}
			edf.setObektDeinostDeinost(deinosti);
		}
		
		
		
		container.vpisvane.setEventDeinostFuraji(edf);
		
		
		
		
		return container;
	}
	
	
	
	
	/** Обработване на 2691-Издаване на сертификат за добра производствена практика и сертификат за произход и свободна продажба
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga2691(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinostFuraji edf = new EventDeinostFuraji();
		
		edf.setEventDeinostFurajiSert(new ArrayList<EventDeinostFurajiSert>());
		EventDeinostFurajiSert sert = new EventDeinostFurajiSert();
		edf.getEventDeinostFurajiSert().add(sert);
		
		ArrayList<Node> nodes = findNodesByName(node, "documentType", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Вида на документ: " + val);
				sert.setSertType(Integer.parseInt(val));				
				
			}
		}
		
		nodes = findNodesByName(node, "feedType", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Вид Фураж: " + val);
				sert.setVidFuraji(val);
			}
		}
		
		
		
		nodes = findNodesByName(node, "documentLanguage", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Език на документа: " + val);
				sert.setLanguage(val);
			}
		}
		
		nodes = findNodesByName(node, "docCountryIntendence", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Държава: " + val);
				sert.setDarj(Integer.parseInt(val));
			}
		}
		
		
		
		nodes = findNodesByName(node, "animals", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Животни: " + val);
				edf.setOpisanie(val);
				sert.setJivotni(val);
				
			}
		}
		
		nodes = findNodesByName(node, "additionalRequirements", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Допълнителни изисквания: " + val);
				edf.setDopInfo(val);
				
			}
		}
		
		
		
		container.vpisvane.setEventDeinostFuraji(edf);
		
		
		
		
		return container;
	}
	
	
	

		
		
		
	/** Обработване на 2867_Заявление за регистрация на транспортно средство за превоз на СЖП и ПП по чл. 246 от ЗВД
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga2867(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinostFuraji edf = new EventDeinostFuraji();
		
		Mps mps = new Mps();
		mps.setCategoryList(new ArrayList<Integer>());
		
		ArrayList<Node> nodes = findNodesByName(node, "vehicleType", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Вида на автомобила: " + val);
				mps.setVid(Integer.parseInt(val));
			}
		}
		
		nodes = findNodesByName(node, "vehicleRegNumber", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Номер на автомобил: " + val);
				mps.setNomer(val);
				
			}
		}
		
		
		
		nodes = findNodesByName(node, "vehicleBrand", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Марка на автомобил: " + val);
//				mps.setMarka(Integer.parseInt(val)); // TODO нямаме марка
			}
		}
		
		nodes = findNodesByName(node, "vehicleModel", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Модел на автомобил: " + val);
				mps.setModel(val);
				
			}
		}
		
		
		
		nodes = findNodesByName(node, "vehicleMaxWieght", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Допустима максимална маса " + val);
				mps.setMaxMasa(val);
				
			}
		}
		
		nodes = findNodesByName(node, "vehicleLoadCapacity", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Товароподемност: " + val);
				mps.setTovaropodemnost(val);
			}
		}
		
		
		nodes = findNodesByName(node, "vehicleVolumeCapacity", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Обем/вместимост: " + val);
				mps.setObem(val);
			}
		}
		
		nodes = findNodesByName(node, "transportedCategories", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Категории на превозваните СЖП: " + val);
				mps.getCategoryList().add(Integer.valueOf(val));
				
				
			}
		}
		
		nodes = findNodesByName(node, "additionalInformation", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Допълнителна информация за СЖП" + val);
				mps.setDopInfo(val);
			}
		}
		
		
		nodes = findNodesByName(node, "residenceAddress", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {

				ReferentAddress addr = convertNodeToRefAddress(child, "Местодомуване");
				if (addr != null) {
					mps.setNasMesto(addr.getEkatte());
					mps.setAdres(addr.getAddrText());
				}else {
					LOGGER.warn("Не може да се измъкне автоматизирано адреса на местодомуване");
				}
				
			}
		}
		
		
		if (! SearchUtils.isEmpty(mps.getNomer())) {			
			MpsDAO mpsDao = new MpsDAO(ActiveUser.DEFAULT);			
			Object[] mpsData = mpsDao.findMpsInfoByIdOrNomer(null, mps.getNomer());
			if (mpsData != null) {
				//Има го в базата
				Integer status = SearchUtils.asInteger(mpsData[8]);
				if (status != null && status.equals(BabhConstants.STATUS_VP_VPISAN)) {
					container.warnings.add("Tранспортно средство с рег. номер " + mps.getNomer() + " вече притежава валидна регистарция за превоз на СЖП/ПП");
				}else {
					Mps mpsDb = mpsDao.findById(SearchUtils.asInteger(mpsData[0]));
					mpsDb = mergeMps(mpsDb, mps);
					container.vpisvane.setMps(mpsDb);
				}
			}else {
				//Нов
				container.vpisvane.setMps(mps);
			}
		}
		
		
		
		
		container.vpisvane.setEventDeinostFuraji(edf);
		
		
		
		
		return container;
	}
		
		
		
		
		
	
	/** Обработване на 3239 - Вписване в регистър на производителите и търговците на средства за идентификация на животнит
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga3239(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinJiv edj = new EventDeinJiv();
		edj.setVidList(new ArrayList<Integer>());
		edj.setEventDeinJivIdentif(new ArrayList<EventDeinJivIdentif>());
		
		ArrayList<Node> nodes = findNodesByName(node, "activityType", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Вид дейност: " + val);
				Integer codeVal = Integer.parseInt(val);						
				String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_VID_DEIN_IDENT_JIV, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
				LOGGER.debug("Вид дейност (Декодирано): " + tekst  );	
				edj.getVidList().add(codeVal);
				
			}
		}
		
		
		//officialIdentificationDevices
		nodes = findNodesByName(node, "officialIdentificationDevices", true);
		if (nodes.size() > 0) {
			for (Node ident : nodes) {
				LOGGER.debug("---------- Идентификатор --------------------------");
				ArrayList<Node> types = findNodesByName(ident, "identificatorType", true);
				Integer typeIdent = null;
				if (types.size() > 0) {
					for (Node tNode : types) {
						String val = getSimpleNodeValue(tNode);
						LOGGER.debug("Tип на идентификатора: " + val);
						typeIdent = Integer.parseInt(val);						
						String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_VID_IDENT_JIV, typeIdent, BabhConstants.CODE_DEFAULT_LANG, new Date());
						LOGGER.debug("Tип на идентификатора: " + tekst  );	
					}
				}
				ArrayList<Node> models = findNodesByName(ident, "identificatorModel", true);	
				String modelIdent = null;
				if (models.size() > 0) {
					for (Node mNode : models) {
						modelIdent = getSimpleNodeValue(mNode);
						LOGGER.debug("Модел на идентификатора: " + modelIdent);
					}
				}
				
				
				ArrayList<Node> аtypes = findNodesByName(ident, "animalType", true);
				ArrayList<Integer> animals = new ArrayList<Integer>();
				if (аtypes.size() > 0) {
					for (Node aNode : аtypes) {
						String val = getSimpleNodeValue(aNode);
						LOGGER.debug("Животно: " + val);
						Integer codeVal = Integer.parseInt(val);						
						String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_VID_JIV_IDENT, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
						LOGGER.debug("Животно (Декод): " + tekst  );
						animals.add(codeVal);
					}
				}
				
				if (animals != null && animals.size() > 0) {
					for (Integer animal : animals) {
						EventDeinJivIdentif edji = new EventDeinJivIdentif();
						edji.setVidIdentif(typeIdent);
						edji.setVidJiv(animal);
						edji.setModel(modelIdent);
						edj.getEventDeinJivIdentif().add(edji);
					}
				}
				
				
				LOGGER.debug("---------------------------------------------------");
			}
		}
		
		nodes = findNodesByName(node, "samplesLetterRegNumber", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Рег. номер/дата на придружително писмо, с което са изпратени мострите: " + val);
				edj.setCel(val);
			}
		}
		
		nodes = findNodesByName(node, "additionalInfo", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Допълнително описание на дейността: " + val);
				edj.setOpisanieCyr(val);
			}
		}
		
		
		container.vpisvane.setEventDeinJiv(edj);
		
		
		
		
		return container;
	}
	
	
	
	
	

	/** Обработване на 1810 - Издаване на сертификат за одобряване на транспортни средства за превоз на животни
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga1810(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		EventDeinJiv edj = new EventDeinJiv();
		
		container.vpisvane.setEventDeinJiv(edj);
		
		ArrayList<Node> nodes = findNodesByName(node, "mpsData", true);
		if (nodes.size() > 0) {			
			for (Node mpsNode : nodes) {
				LOGGER.debug("---------- MPS --------------------------");
				
				Mps mps = new Mps();
				
				ArrayList<Node> regNoms = findNodesByName(mpsNode, "vehicleRegistrationPlate", true);
				for (Node tabl : regNoms) {
					String val = getSimpleNodeValue(tabl);
					LOGGER.debug("Рег. № на ТС: " + val);
					mps.setNomer(val.replaceAll(" ", ""));
				}
				
				ArrayList<Node> types = findNodesByName(mpsNode, "vehicleType", true);
				for (Node tNode : types) {
					String val = getSimpleNodeValue(tNode);
					LOGGER.debug("Вид МПС: " + val);
					Integer codeVal = Integer.parseInt(val);						
					String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_VID_MPS, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
					LOGGER.debug("Вид МПС (Декод): " + tekst  );
					mps.setVid(codeVal);
				}
				
				ArrayList<Node> marki = findNodesByName(mpsNode, "vehicleBrandModel", true);
				for (Node mm : marki) {
					String val = getSimpleNodeValue(mm);
					LOGGER.debug("Марка/Модел: " + val);
					mps.setModel(val);
				}
				
				ArrayList<Node> nss = findNodesByName(mpsNode, "navigationSystem", true);
				for (Node ns : nss) {
					String val = getSimpleNodeValue(ns);
					LOGGER.debug("Оборудване с навигационна система: " + val);
					Integer codeVal = Integer.parseInt(val);						
					String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_DANE, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
					LOGGER.debug("Оборудване с навигационна система (Декод): " + tekst  );
					mps.setNavigation(codeVal);
				}
				
				ArrayList<Node> pls = findNodesByName(mpsNode, "carSpace", true);
				for (Node pl : pls) {
					String val = getSimpleNodeValue(pl);
					LOGGER.debug("Площ: " + val);
					mps.setPlosht(val);
				}
				
				ArrayList<Node> noms = findNodesByName(mpsNode, "mpsGerPaperNumber", true);
				for (Node nom : noms) {
					String val = getSimpleNodeValue(nom);
					LOGGER.debug("№ / дата на рег. талон: " + val);
					mps.setNomDatReg(val);
				}
				
				ArrayList<Node> animals = findNodesByName(mpsNode, "animalForTransportation", true);
				for (Node aNode : animals) {
					String val = getSimpleNodeValue(aNode);
					LOGGER.debug("Животни за превоз: " + val);
					mps.setDopInfo(val);
				}
				
				
				
				
				
				Referent sobst = null;
				String tipLice = null;
				ArrayList<Node> lidents = findNodesByName(mpsNode, "ownerPhysicalIdentifierType", true);
				for (Node n : lidents) {
					String val = getSimpleNodeValue(n);
					LOGGER.debug("Тип идентификатор на физическо лице " + val);
					tipLice = val;					
					String[] labels = {"ownerPhysicalIdentifierType", "ownerPhysicalIdentifier", "ownerPhysicalFirstName", "ownerPhysicalMiddleName", "ownerPhysicalFamilyName"};
					sobst = convertNodeToReferentFl(mpsNode, labels, "Собственик на ТС ");
				}
				
				
				if (tipLice == null) {
					lidents = findNodesByName(mpsNode, "ownerType", true);
					for (Node n : lidents) {
						String val = getSimpleNodeValue(n);
						LOGGER.debug("Тип идентификатор на юридическо лице " + val);
						tipLice = val;
					}
					
					if (tipLice != null ) {
						sobst = new Referent();
						ArrayList<Node> firmNames = findNodesByName(mpsNode, "ownerLegalName", true);
						for (Node fNode : firmNames) {
							String val = getSimpleNodeValue(fNode);
							LOGGER.debug("Собственик Наименование на ЮЛ: " + val);
							sobst.setRefName(val);
						}
						
						ArrayList<Node> eiks = findNodesByName(mpsNode, "ownerLegalEik", true);
						for (Node eNode : eiks) {
							String val = getSimpleNodeValue(eNode);
							LOGGER.debug("Собственик ЕИК на ЮЛ: " + val);
							sobst.setNflEik(val);
							sobst.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
						}
						
					}
					
				}
				
				if (sobst == null) {
					sobst = container.ref2;
				}
				
				MpsDAO mpsDAO = new MpsDAO(ActiveUser.DEFAULT);
				Object[] mpsArray = mpsDAO.findMpsInfoByIdOrNomer(null, mps.getNomer());
				if (mpsArray != null) {
					Integer vpIdJiv = SearchUtils.asInteger(mpsArray[11]);
					
					Mps mpsDb = mpsDAO.findById(SearchUtils.asInteger(mpsArray[0]), true);
					
					mps = mergeMPS(mpsDb, mps, sobst, container);
					
					if (vpIdJiv != null) {
						mps.setCorrect(false);
						container.warnings.add("Транспортно средство с рег. номер " + mps.getNomer() + " вече притежава валидна регистрация за превоз на животни");
					}
					
					
					
					
				}else {
					//Ново МПС
					MpsLice mpsLice = new MpsLice();
					mpsLice.setReferent(sobst);
					mpsLice.setCodeRef(sobst.getCode());
					mpsLice.setTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK);
					
					if (mps.getMpsLice() == null) {
						mps.setMpsLice(new ArrayList<MpsLice>());
					}
					mps.getMpsLice().add(mpsLice);
				}
				
				
				container.allMps.add(mps);
				
				LOGGER.debug("-----------------------------------------");
			}
		}
		
		
		return container;
	}
	
	
	
	/** Обработване на 3364 - Одобрение на учебен план и учебна програма в сферата на хуманно отношение към животните и суровото мляко
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 */
	private EgovContainer proccessUsluga3364(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException{
		
		
		
		
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinJiv edj = new EventDeinJiv();
		edj.setObuchenieList(new ArrayList<Integer>());
		edj.setVidList(new ArrayList<Integer>());
		
		
		edj.setEventDeinJivLice(new ArrayList<EventDeinJivLice>());
		
		
		ArrayList<Node> nodes = findNodesByName(node, "activityType", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Вид дейност: " + val);
				Integer codeVal = Integer.parseInt(val);						
				String tekst = sd.decodeItem(707, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
				LOGGER.debug("Вид дейност(Декодирано): " + tekst  );				
				container.doc.setDocPodVid(codeVal);
				if (codeVal == 62) {
					edj.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_UCHEBNA_PROGR );
				}else {
					edj.getVidList().add(BabhConstants.CODE_ZNACHENIE_DEIN_ZJ_OBUCHENIE  );
				}
			}
		}


		 nodes = findNodesByName(node, "fieldOfStudy", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Сфера на обучение: " + val);
				Integer codeVal = Integer.parseInt(val);						
				String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_SFERI_OBUCHENIE, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
				LOGGER.debug("Сфера на обучение (Декодирано): " + tekst  );	
				edj.getObuchenieList().add(codeVal);
				
			}
		}
		
		//Временно, защото в момента е забито към лицата
		 nodes = findNodesByName(node, "personFieldOfStudy", true);
			if (nodes.size() > 0) {
				
				for (Node child : nodes) {
					String val = getSimpleNodeValue(child);
					LOGGER.debug("Сфера на обучение: " + val);
					Integer codeVal = Integer.parseInt(val);						
					String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_SFERI_OBUCHENIE, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
					LOGGER.debug("Сфера на обучение (Декодирано): " + tekst  );	
					edj.getObuchenieList().add(codeVal);
					
				}
			}
		
		
		
		
		
		nodes = findNodesByName(node, "courseName", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Наименование на курса: " + val);
				edj.setOpisanieCyr(val);
			}
		}
		
		nodes = findNodesByName(node, "listOfProfessions", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Описание на дейността: " + val);
				edj.setCel(val);
			}
		}
		
		nodes = findNodesByName(node, "courseStartDate", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				try {
					Date datBeg = DateUtils.convertXmlDateStringToDate(val);
					LOGGER.debug("Начална дата на курс: " + datBeg);
					edj.setDateBegMeropriatie(datBeg);
				} catch (ParseException e) {
					container.warnings.add("Началната дата " + val + " не може да бъде конвертирана !");
				}
			}
		}
		
		nodes = findNodesByName(node, "courseEndDate", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				try {
					Date datEnd = DateUtils.convertXmlDateStringToDate(val);
					LOGGER.debug("Крайна дата на курс: " + datEnd);
					edj.setDateEndMeropriatie(datEnd);
				} catch (ParseException e) {
					container.warnings.add("Крайна дата " + val + " не може да бъде конвертирана !");
				}
			}
		}
		
		
		nodes = findNodesByName(node, "teamMembersData", true);
		if (nodes.size() > 0) {
			int cnt = 0;
			for (Node child : nodes) {
				cnt++;
				String[] labels = {"personPhysicalIdentifier", "personPhysicalIdentifierType", "personFirstName", "personMiddleName", "personFamilyName" , "personEducation"}; 
				
				Referent tekLice = convertNodeToReferentFl(child, labels, "********** Участник " + cnt);
				EventDeinJivLice edjl = new EventDeinJivLice();
				edjl.setCodeRef(tekLice.getCode());
				edjl.setReferent(tekLice);
				edjl.setTipVraz(null);
				
				edj.getEventDeinJivLice().add(edjl);
				
				
			}
		}
		
		
		
		container.vpisvane.setEventDeinJiv(edj);
		
		
		
		
		return container;
	}
	
	
	
	/** Обработване на 2705 - Одобрение на екипи за добив и съхранение на ембриони и яйцеклетки
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 * @throws InvalidParameterException 
	 */
	private EgovContainer proccessUsluga2705(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException, InvalidParameterException{
		
		
		
		ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
		//За сега само го печатаме
		//printNode(node, "");		
		
		EventDeinJiv edj = new EventDeinJiv();
		edj.setObuchenieList(new ArrayList<Integer>());
		edj.setVidList(new ArrayList<Integer>());
		
		
		edj.setEventDeinJivLice(new ArrayList<EventDeinJivLice>());
		
		
		ArrayList<Node>  nodes = findNodesByName(node, "labDescription", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Описание на оборудването в лабораторията: " + val);
				edj.setOpisanieCyr(val);
			}
		}
		
		
		
		
		ArrayList<Node> allMembers = findNodesByName(node, "teamMembersData", true);
		if (nodes.size() > 0) {
			int cnt = 0;
			for (Node member : allMembers) {
				cnt++;
				String[] labels = {"memberPhysicalIdentifier", "memberPhysicalIdentifierType", "memberFirstName", "memberMiddleName", "memberFamilyName" }; 
				
				Referent tekLice = convertNodeToReferentFl(member, labels, "********** Участник " + cnt);
				
				tekLice.setReferentDocs(new ArrayList<ReferentDoc>());
				
				
				Integer pos = null;
				nodes = findNodesByName(member, "teamPosition", true);
				if (nodes.size() > 0) {
					
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Позиция в екипа: " + val);
						Integer codeVal = Integer.parseInt(val);						
						String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_VRAZ_LICE_JIV_EMBR, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
						LOGGER.debug("Позиция в екипа (Декодирано): " + tekst  );	
						pos = codeVal;
						
					}
				}
				
				
				// <ДИПЛОМА>
				
				ReferentDoc diploma = new ReferentDoc();
				diploma.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA );
						
				nodes = findNodesByName(member, "dimplomNumber", true);
				if (nodes.size() > 0) {			
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Диплома/Номер на диплома : " + val);
						diploma.setNomDoc(val);
					}
				}
				
				nodes = findNodesByName(member, "dimplomIssueDate", true);
				if (nodes.size() > 0) {
					
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Диплома/Дата на издаване : " + val);
						try {
							diploma.setDateIssued(DateUtils.convertXmlDateStringToDate(val));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				
				
				if (diploma != null && diploma.getNomDoc() != null && ! diploma.getNomDoc().trim().isEmpty()) {
					
					
					if (tekLice.getId() != null) {
						//лицето е го има в базата
						ReferentDoc diplomaDb = rdao.findReferentDoc(diploma.getNomDoc(), diploma.getVidDoc(), tekLice.getId());
						if (diplomaDb != null) {					
							diplomaDb = mergeDoc(diplomaDb, diploma);
							tekLice.getReferentDocs().add(diplomaDb);
						}else {
							tekLice.getReferentDocs().add(diploma);
						}
					}else {
						tekLice.getReferentDocs().add(diploma);
					}
				}
				
				
				// </ДИПЛОМА>
				
				
				// <УДОСТОВЕРЕНЕИ>
				
				ReferentDoc udostowerenie = new ReferentDoc();
				udostowerenie.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_UDOST_BVS );
						
				nodes = findNodesByName(member, "sertificateNumber", true);
				if (nodes.size() > 0) {			
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Диплома/Номер на диплома : " + val);
						udostowerenie.setNomDoc(val);
					}
				}
				
				nodes = findNodesByName(member, "sertificateIssueDate", true);
				if (nodes.size() > 0) {
					
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Диплома/Дата на издаване : " + val);
						try {
							udostowerenie.setDateIssued(DateUtils.convertXmlDateStringToDate(val));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				
				
				if (udostowerenie != null && udostowerenie.getNomDoc() != null && ! udostowerenie.getNomDoc().trim().isEmpty()) {
					
					
					if (tekLice.getId() != null) {
						//лицето е го има в базата
						ReferentDoc udostowerenieDb = rdao.findReferentDoc(udostowerenie.getNomDoc(), udostowerenie.getVidDoc(), tekLice.getId());
						if (udostowerenieDb != null) {					
							udostowerenieDb = mergeDoc(udostowerenieDb, udostowerenie);
							tekLice.getReferentDocs().add(udostowerenieDb);
						}else {
							tekLice.getReferentDocs().add(udostowerenie);
						}
					}else {
						tekLice.getReferentDocs().add(udostowerenie);
					}
				}
				
				
				// </ДИПЛОМА>
				
				
				EventDeinJivLice edjl = new EventDeinJivLice();
				edjl.setCodeRef(tekLice.getCode());
				edjl.setReferent(tekLice);
				edjl.setTipVraz(null);
				edjl.setTipVraz(pos);
				
				edj.getEventDeinJivLice().add(edjl);
				
				
			}
		}
		
		
		
		container.vpisvane.setEventDeinJiv(edj);
		
		
		
		
		return container;
	}
	
	
	
	
	/** Обработване на 570 - Одобряване на реклама на ветеринарномедицински продукт
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 * @throws InvalidParameterException 
	 */
	private EgovContainer proccessUsluga570(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException, InvalidParameterException{
		
		
		
		EventDeinostVlp edv = new EventDeinostVlp();
		
		ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
		Referent ref = new Referent();
		
		ArrayList<Node>  nodes = findNodesByName(node, "licenseeLegalEik", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Притежател на лиценза ЕИК: " + val);
				ref.setNflEik(val);
			}
		}
		
		nodes = findNodesByName(node, "legalName", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Притежател на лиценза (кирилица) " + val);
				ref.setRefName(val);
			}
		}
		
		nodes = findNodesByName(node, "legalName1", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Притежател на лиценза (Латиница) " + val);
				ref.setRefLatin(val);
			}
		}
		
		ArrayList<Node> nodesAddr = findNodesByName(node, "corespondationAddress", false);
		if (nodesAddr.size() > 0) {
			for (Node nodeAddr : nodesAddr) {
				ReferentAddress addr = convertNodeToRefAddress(nodeAddr, "Адрес на притежателя на лиценз");
				if (addr == null) {
					LOGGER.warn("Не може да се измъкне автоматизирано адреса на местодомуване");
				}else {
					ref.setAddressKoresp(addr);
				}
			}
		}
		
		nodes = findNodesByName(node, "addressLatin", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Адрес (латиница) на притежателя на лиценз " + val);
				ref.getAddressKoresp().setAddrTextLatin(val);
			}
		}
		
		
		Referent refDb = rdao.findByIdent(ref.getNflEik(), null, null, BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
		if (refDb != null) {
			ref = mergeReferents(refDb, ref);
		}
		
		container.ref3 = ref;
		
		
		nodes = findNodesByName(node, "vlp", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("ВЛП, което ще се рекламира " + val);
				edv.setVlpReklama(val);
			}
		}	
		
		String nomDatLicanzi = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		nodes = findNodesByName(node, "licenseNumbersData", true);
		if (nodes.size() > 0) {
			
			
			
			for (Node child : nodes) {
				
				String lic = "";
				
				ArrayList<Node> allLicN = findNodesByName(child, "licenseNumber", false);
				for (Node licN : allLicN) {
					
					String val = getSimpleNodeValue(licN);
					LOGGER.debug("Номер на лиценз " + val);
					lic += val;
				}
				
				ArrayList<Node> allLicD= findNodesByName(child, "licenseDate", false);
				for (Node licD : allLicD) {
					
					String val = getSimpleNodeValue(licD);
					LOGGER.debug("Дата на лиценз " + val);
					try {
						Date dat = DateUtils.convertXmlDateStringToDate(val);
						lic += "/"+sdf.format(dat);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (! lic.isEmpty()) {
					nomDatLicanzi += lic + "; ";
				}
				
			}
			edv.setNomDatLizenz(nomDatLicanzi);
		}	
		
		nodes = findNodesByName(node, "commercialPurpose", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Предназначение " + val);
				Integer codeVal = Integer.parseInt(val);						
				String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_PREDN_REKLAMA, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
				LOGGER.debug("Предназначение (Декодирано): " + tekst  );
				edv.setPrednaznReklama(codeVal);
			}
		}	
		
		
		
		container.vpisvane.setEventDeinostVlp(edv);
		return container;
	}
		
	
	
	
	
	/** Обработване на 705 Издаване на лиценз за търговия на едро с ветеринарномедицински продукт
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 * @throws InvalidParameterException 
	 */
	private EgovContainer proccessUsluga705(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException, InvalidParameterException{
		
		
		
		EventDeinostVlp edv = new EventDeinostVlp();
		edv.setObektDeinostDeinost(new ArrayList<ObektDeinostDeinost>());
		edv.setEventDeinostVlpPredmet(new ArrayList<EventDeinostVlpPredmet>());
		
		ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
		//Referent ref = new Referent();
		
		//String fgAll = "";
		ArrayList<String> formi = new ArrayList<String>(); 
		ArrayList<Node>  nodes = findNodesByName(node, "pharmacologicalGroups", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Фармакологични групи, с които ще се търгува: " + val);
				Integer codeVal = Integer.parseInt(val);						
				String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_PREDMET_TARGOVIA_EDRO_VLP, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
				LOGGER.debug("Фармакологични групи, с които ще се търгува: (Декодирано): " + tekst  );
				EventDeinostVlpPredmet edvp = new EventDeinostVlpPredmet();
				edvp.setPredmet(codeVal);
				edvp.setCodeClassif(BabhConstants.CODE_CLASSIF_PREDMET_TARGOVIA_EDRO_VLP);
				edv.getEventDeinostVlpPredmet().add(edvp);
				//fgAll += "Всички "  + tekst + "; ";
				formi.add(tekst);
			}
		}
		
		String ex = null;
		nodes = findNodesByName(node, "exceptions", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("С изключение на: " + val);
				//edv.setDopInfo(fgAll + " с изключение на " + val);
				ex = val;
				
			}
		}
		
		
		// формираме стринг-а
		String dopInfo = "";
		String farm = null;
		String imuno = null;
		for (String s : formi) {
			if (s != null) {
				if (s.toUpperCase().contains("ИМУНО")) {
					imuno = s;
				}
				if (s.toUpperCase().contains("ФАРМА")) {
					farm = s;
				}
				
			}
		}
		int cnt = 0;
		if (farm != null) {
			cnt++;
			dopInfo += cnt+". Всички " + farm.toLowerCase();
			if (ex != null && ! ex.trim().isEmpty()) {
				if (ex.toUpperCase().contains("С ИЗКЛЮЧЕНИЕ")) {
					dopInfo += " " + ex;
				}else {
					dopInfo += " с изключение на " + ex;
				}
				
			}
			dopInfo+="\r\n";
		}
		if (imuno != null) {
			cnt++;
			dopInfo += cnt+". Всички " + imuno.toLowerCase();
		}
		dopInfo = dopInfo.replace("влп", "ВЛП").replace("вмп", "ВМП");
		edv.setDopInfo(dopInfo);
		
		
		
		ObektDeinost od = new ObektDeinost();
		od.setObektDeinostLica(new ArrayList<ObektDeinostLica>());
		
		nodes = findNodesByName(node, "objectName", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Обект на дейност: " + val);
				od.setNaimenovanie(val);
			}
		}
		
		nodes = findNodesByName(node, "objectPhoneNumber", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Телефон на Обект на дейност: " + val);				
				od.setTel(val);
				
			}
		}
		
		nodes = findNodesByName(node, "objectEmailAddress", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Емейл на Обект на дейност: " + val);
				od.setEmail(val);
			}
		}
		
		
		
		

		ArrayList<Node> nodesAddr = findNodesByName(node, "activityObjectAddress", false);
		if (nodesAddr.size() > 0) {
			for (Node nodeAddr : nodesAddr) {
				ReferentAddress addr = convertNodeToRefAddress(nodeAddr, "Адрес на обект на дейност");
				if (addr == null) {
					LOGGER.warn("Не може да се измъкне автоматизирано адреса на Обект на дейност");
				}else {
					od.setAddress(addr.getAddrText());
					od.setDarj(addr.getAddrCountry());
					od.setNasMesto(addr.getEkatte());
					od.setPostCode(addr.getPostCode());
				}
			}
		}
		
		
		
		String nomDatAct= "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		nodes = findNodesByName(node, "commissioningActNumber", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Номер на Акт за въвеждане в експлоатация на обекта " + val);
				nomDatAct += val;
			}
		}	
		
		
		nodes= findNodesByName(node, "commissioningActDate", false);
		for (Node child : nodes) {
			
			String val = getSimpleNodeValue(child);
			LOGGER.debug("Дата на Акт за въвеждане в експлоатация на обекта " + val);
			try {
				Date dat = DateUtils.convertXmlDateStringToDate(val);
				nomDatAct += " / " + sdf.format(dat);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		od.setNomDatAkt(nomDatAct);		
			
		
		
		nodes = findNodesByName(node, "additionalInfo", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Допълнително инфо за вписването: " + val);				
				container.vpisvane.setInfo(val);
				
			}
		}
		
		Referent otgLice = new Referent();
		String[] labels = {"responsiblePersonIdentifierIdentifierType", "responsiblePersonIdentifier", "responsiblePersonFirstName", "responsiblePersonMiddleName", "responsiblePersonFamilyName"}; 
		nodes= findNodesByName(node, "__additionalSpecificContent", false);
		for (Node child : nodes) {
			otgLice= convertNodeToReferentFl(child, labels, "Отговорно лице за обекта");
		}
		
		
		
        
      //Диплома на отговорното лице
  		ReferentDoc diploma = new ReferentDoc();
  		diploma.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA );
  				
  		nodes = findNodesByName(node, "responsiblePersonDiplomNumber", true);
  		if (nodes.size() > 0) {			
  			for (Node child : nodes) {
  				String val = getSimpleNodeValue(child);
  				LOGGER.debug("Отговорно лице за обекта Диплома/Номер на диплома : " + val);
  				diploma.setNomDoc(val);
  			}
  		}
  		
  		nodes = findNodesByName(node, "responsiblePersonDiplomIssueDate", true);
  		if (nodes.size() > 0) {
  			
  			for (Node child : nodes) {
  				String val = getSimpleNodeValue(child);
  				LOGGER.debug("Отговорно лице за обекта Диплома/Дата на издаване : " + val);
  				try {
  					diploma.setDateIssued(DateUtils.convertXmlDateStringToDate(val));
  				} catch (ParseException e) {
  					// TODO Auto-generated catch block
  					e.printStackTrace();
  				}
  			}
  		}
  		
  		nodes = findNodesByName(node, "diplomIssuer", true);
  		if (nodes.size() > 0) {
  			
  			for (Node child : nodes) {
  				String val = getSimpleNodeValue(child);
  				LOGGER.debug("Отговорно лице за обекта Диплома/Издадена от : " + val);
  				diploma.setIssuedBy(val);
  			}
  		}
  		
  		if (diploma != null && diploma.getNomDoc() != null && ! diploma.getNomDoc().trim().isEmpty()) {
  			otgLice.setReferentDocs(new ArrayList<ReferentDoc>());
  			
  			if (otgLice.getId() != null) {
  				//лицето е го има в базата
  				ReferentDoc diplomaDb = rdao.findReferentDoc(diploma.getNomDoc(), diploma.getVidDoc(), otgLice.getId());
  				if (diplomaDb != null) {					
  					diplomaDb = mergeDoc(diplomaDb, diploma);
  					otgLice.getReferentDocs().add(diplomaDb);
  				}else {
  					otgLice.getReferentDocs().add(diploma);
  				}
  			}else {
  				otgLice.getReferentDocs().add(diploma);
  			}
  		}
        
  		ObektDeinostLica odl = new ObektDeinostLica();
  		odl.setCodeRef(otgLice.getCode());
  		odl.setReferent(otgLice);
  		odl.setObektDeinostId(od.getId());
  		od.getObektDeinostLica().add(odl);
  		
  		//По изрично искане на Мариана Алексиева
  		odl.setRole(6);
  		
  		
  		ObektDeinostDeinost odd = new ObektDeinostDeinost();
  		odd.setObektDeinost(od);
  		odd.setDeinostId(od.getId());
  		odd.setObektDeinostId(edv.getId());
  		odd.setTablEventDeinost(100);
  		
  		edv.getObektDeinostDeinost().add(odd);
		
		container.vpisvane.setEventDeinostVlp(edv);
		return container;
	}
	
	
	
	/** Обработване на 1365 Издаване на лиценз за търговия на дребно с ветеринарномедицински продукт
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 * @throws InvalidParameterException 
	 */
	private EgovContainer proccessUsluga1365(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException, InvalidParameterException{
		
		
		
		EventDeinostVlp edv = new EventDeinostVlp();
		edv.setObektDeinostDeinost(new ArrayList<ObektDeinostDeinost>());
		edv.setEventDeinostVlpPredmet(new ArrayList<EventDeinostVlpPredmet>());
		
		ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
		//Referent ref = new Referent();
		
		//String fgAll = "";
		ArrayList<String> formi = new ArrayList<String>(); 
		ArrayList<Node>  nodes = findNodesByName(node, "pharmacologicalGroups", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Фармакологични групи, с които ще се търгува: " + val);
				Integer codeVal = Integer.parseInt(val);						
				String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_PREDMET_TARGOVIA_EDRO_VLP, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
				LOGGER.debug("Фармакологични групи, с които ще се търгува: (Декодирано): " + tekst  );
				EventDeinostVlpPredmet edvp = new EventDeinostVlpPredmet();
				edvp.setPredmet(codeVal);
				edvp.setCodeClassif(BabhConstants.CODE_CLASSIF_PREDMET_TARGOVIA_EDRO_VLP);
				edv.getEventDeinostVlpPredmet().add(edvp);
				//fgAll += "Всички "  + tekst + "; ";
				formi.add(tekst);
			}
		}
		
		String ex = null;
		nodes = findNodesByName(node, "exceptions", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("С изключение на: " + val);
				//edv.setDopInfo(fgAll + " с изключение на " + val);
				ex = val;
				
			}
		}
		
		
		// формираме стринг-а
		String dopInfo = "";
		String farm = null;
		String imuno = null;
		for (String s : formi) {
			if (s != null) {
				if (s.toUpperCase().contains("ИМУНО")) {
					imuno = s;
				}
				if (s.toUpperCase().contains("ФАРМА")) {
					farm = s;
				}
				
			}
		}
		int cnt = 0;
		if (farm != null) {
			cnt++;
			dopInfo += cnt+". Всички " + farm.toLowerCase();
			if (ex != null && ! ex.trim().isEmpty()) {
				if (ex.toUpperCase().contains("С ИЗКЛЮЧЕНИЕ")) {
					dopInfo += " " + ex;
				}else {
					dopInfo += " с изключение на " + ex;
				}
				
			}
			dopInfo+="\r\n";
		}
		if (imuno != null) {
			cnt++;
			dopInfo += cnt+". Всички " + imuno.toLowerCase();
		}
		dopInfo = dopInfo.replace("влп", "ВЛП").replace("вмп", "ВМП");
		edv.setDopInfo(dopInfo);
		
		
		
		ObektDeinost od = new ObektDeinost();
		od.setObektDeinostLica(new ArrayList<ObektDeinostLica>());
		
		nodes = findNodesByName(node, "objectName", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Обект на дейност: " + val);
				od.setNaimenovanie(val);
			}
		}
		
		nodes = findNodesByName(node, "objectPhoneNumber", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Телефон на Обект на дейност: " + val);				
				od.setTel(val);
				
			}
		}
		
		nodes = findNodesByName(node, "objectEmailAddress", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Емейл на Обект на дейност: " + val);
				od.setEmail(val);
			}
		}
		
		
		
		

		ArrayList<Node> nodesAddr = findNodesByName(node, "objectAddress", false);
		if (nodesAddr.size() > 0) {
			for (Node nodeAddr : nodesAddr) {
				ReferentAddress addr = convertNodeToRefAddress(nodeAddr, "Адрес на обект на дейност");
				if (addr == null) {
					LOGGER.warn("Не може да се измъкне автоматизирано адреса на Обект на дейност");
				}else {
					od.setAddress(addr.getAddrText());
					od.setDarj(addr.getAddrCountry());
					od.setNasMesto(addr.getEkatte());
					od.setPostCode(addr.getPostCode());
				}
			}
		}
		
		
		
		String nomDatAct= "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		
		nodes = findNodesByName(node, "commissioningActNumber", true);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Номер на Акт за въвеждане в експлоатация на обекта " + val);
				nomDatAct += val;
			}
		}	
		
		
		nodes= findNodesByName(node, "commissioningActDate", false);
		for (Node child : nodes) {
			
			String val = getSimpleNodeValue(child);
			LOGGER.debug("Дата на Акт за въвеждане в експлоатация на обекта " + val);
			try {
				Date dat = DateUtils.convertXmlDateStringToDate(val);
				nomDatAct += " / " + sdf.format(dat);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		od.setNomDatAkt(nomDatAct);		
			
		
		
		nodes = findNodesByName(node, "additionalInfo", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Допълнително инфо за вписването: " + val);				
				container.vpisvane.setInfo(val);
				
			}
		}
		
		 
		ArrayList<Node> licaNodes = findNodesByName(node, "prsonsDataGrid", false);
		
		for (Node liceNode : licaNodes) {
			
			Integer tip = null;
			String tipS = null;
			ArrayList<Node> nodesP = findNodesByName(liceNode, "personType", false);
			if (nodesP.size() > 0) {
				
				for (Node pt : nodesP) {
					String val = getSimpleNodeValue(pt);
					LOGGER.debug("Тип на лицето : " + val);
					Integer codeVal = Integer.parseInt(val);						
					String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_TIP_VRAZKA_TARG_DREBNO_VLP, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
					tipS = tekst;
					tip = codeVal;
					LOGGER.debug("Тип на лицето (Декодирано): " + tekst  );
				}
			}
			
			Referent otgLice = new Referent();
			String[] labels = {"responsiblePersonIdentifierIdentifierType", "responsiblePersonIdentifier", "responsiblePersonFirstName", "responsiblePersonMiddleName", "responsiblePersonFamilyName"};
			otgLice= convertNodeToReferentFl(liceNode, labels, tipS);
			
			
			//Диплома на отговорното лице
	  		ReferentDoc diploma = new ReferentDoc();
	  		diploma.setVidDoc(BabhConstants.CODE_ZNACHENIE_VIDDOC_DIPLOMA );
	  				
	  		nodes = findNodesByName(liceNode, "responsiblePersonDiplomNumber", true);
	  		if (nodes.size() > 0) {			
	  			for (Node child : nodes) {
	  				String val = getSimpleNodeValue(child);
	  				LOGGER.debug(tipS +  " Диплома/Номер на диплома : " + val);
	  				diploma.setNomDoc(val);
	  			}
	  		}
	  		
	  		nodes = findNodesByName(liceNode, "responsiblePersonDiplomIssueDate", true);
	  		if (nodes.size() > 0) {
	  			
	  			for (Node child : nodes) {
	  				String val = getSimpleNodeValue(child);
	  				LOGGER.debug( tipS + " Диплома/Дата на издаване : " + val);
	  				try {
	  					diploma.setDateIssued(DateUtils.convertXmlDateStringToDate(val));
	  				} catch (ParseException e) {
	  					// TODO Auto-generated catch block
	  					e.printStackTrace();
	  				}
	  			}
	  		}
	  		
	  		nodes = findNodesByName(liceNode, "diplomIssuer", true);
	  		if (nodes.size() > 0) {
	  			
	  			for (Node child : nodes) {
	  				String val = getSimpleNodeValue(child);
	  				LOGGER.debug(tipS +  " Диплома/Издадена от : " + val);
	  				diploma.setIssuedBy(val);
	  			}
	  		}
	  		
	  		if (diploma != null && diploma.getNomDoc() != null && ! diploma.getNomDoc().trim().isEmpty()) {
	  			otgLice.setReferentDocs(new ArrayList<ReferentDoc>());
	  			
	  			if (otgLice.getId() != null) {
	  				//лицето е го има в базата
	  				ReferentDoc diplomaDb = rdao.findReferentDoc(diploma.getNomDoc(), diploma.getVidDoc(), otgLice.getId());
	  				if (diplomaDb != null) {					
	  					diplomaDb = mergeDoc(diplomaDb, diploma);
	  					otgLice.getReferentDocs().add(diplomaDb);
	  				}else {
	  					otgLice.getReferentDocs().add(diploma);
	  				}
	  			}else {
	  				otgLice.getReferentDocs().add(diploma);
	  			}
	  		}
	        
	  		ObektDeinostLica odl = new ObektDeinostLica();
	  		odl.setCodeRef(otgLice.getCode());
	  		odl.setReferent(otgLice);
	  		odl.setObektDeinostId(od.getId());
	  		odl.setRole(tip);
	  		od.getObektDeinostLica().add(odl);
			
		}
		
		
		
        
      
  		
  		
  		ObektDeinostDeinost odd = new ObektDeinostDeinost();
  		odd.setObektDeinost(od);
  		odd.setDeinostId(od.getId());
  		odd.setObektDeinostId(edv.getId());
  		odd.setTablEventDeinost(100);
  		
  		edv.getObektDeinostDeinost().add(odd);
		
		container.vpisvane.setEventDeinostVlp(edv);
		return container;
	}


	
	
	
	
	
	
	
	
	/** Обработване на 2802 Издаване на сертификат за регистрация на инвитро диагностични средства
	 * @param container
	 * @param node
	 * @param codeUsluga
	 * @return
	 * @throws DbErrorException 
	 * @throws InvalidParameterException 
	 */
	private EgovContainer proccessUsluga2802(EgovContainer container, Node node, String codeUsluga, SystemData sd) throws DbErrorException, InvalidParameterException{
		
		
		
		EventDeinostVlp edv = new EventDeinostVlp();
		edv.setEventDeinostVlpPredmet(new ArrayList<EventDeinostVlpPredmet>());
		edv.setBolestList(new ArrayList<Integer>());
		edv.setEventDeinostVlpLice(new ArrayList<EventDeinostVlpLice>());
		
		ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
		
		EventDeinostVlpPredmet predmet = new EventDeinostVlpPredmet();
		
		ArrayList<Node>  nodes = findNodesByName(node, "productName", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Продукт, Наименование на кирилица: " + val);
				predmet.setNaimenovanie(val);
			
			}
		}
		
		nodes = findNodesByName(node, "productNameLat", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Продукт, Наименование на латиница: " + val);
				predmet.setNaimenovanieLat(val);
			}
		}
		
		
		nodes = findNodesByName(node, "productQuantity", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Продукт, Количество: " + val);
				predmet.setKolichestvo(val);
				
			}
		}
		
		nodes = findNodesByName(node, "productExpirationDade", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Продукт, Срок на годност: " + val);
				predmet.setSrok(val);
			}
			
		}
		
		nodes = findNodesByName(node, "productStorage", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Продукт, Съхранение: " + val);
				predmet.setSahranenie(val);
				
			}
		}
		
		nodes = findNodesByName(node, "productDescription", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Продукт, Описание: " + val);
				predmet.setDopInfo(val);
			}
		}
		
		edv.getEventDeinostVlpPredmet().add(predmet);
		
		
		
		nodes = findNodesByName(node, "diseases", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Болест " + val);
				Integer codeVal = Integer.parseInt(val);						
				String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_BOLESTI, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
				LOGGER.debug("Болест (Декодирано): " + tekst  );
				edv.getBolestList().add(codeVal);
			}
		}
		
		
		
		
		
		nodes = findNodesByName(node, "producerInfo", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Информация на производителя и т.н.: " + val);
				edv.setOpisanie(val);
			}
		}
		
		nodes = findNodesByName(node, "lab", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Акредитирана лаборатория по чл. 410б, ал. 1, т. 4 и т.н.: " + val);
				edv.setAkredLaboratoria(val);
			}
		}
		
		nodes = findNodesByName(node, "additionalInfo", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Допълнителна информация: " + val);
				edv.setDopInfo(val);
			}
		}
		
		
		
		ReferentAddress pritejatelAdrr = null;
		nodes = findNodesByName(node, "licenseHolderAddress", false);
		if (nodes.size() > 0) {
			for (Node child : nodes) {
				
				pritejatelAdrr = convertNodeToRefAddress(child, "Адрес на притежателя на лиценза");
			}
		}
		
		nodes = findNodesByName(node, "licenseHolderAddressLatin", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				String val = getSimpleNodeValue(child);
				LOGGER.debug("Адрес на притежателя на лиценза (латиница): " + val);
				if (pritejatelAdrr != null) {
					pritejatelAdrr.setAddrTextLatin(val);
				}
				
			}
		}
		
		String tipPLic = "ul"; //default
		nodes = findNodesByName(node, "licenseHolder", false);
		if (nodes.size() > 0) {
			
			for (Node child : nodes) {
				tipPLic = getSimpleNodeValue(child);
				LOGGER.debug("Тип на притежателя на лиценза: " + tipPLic);
				break; // тава намазване - има го и по навътре
				
			}
		}
		
		Referent pritejatel = null;		
		if (tipPLic.equals("ul")) {
			pritejatel =  createEmptyRef();
			pritejatel.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
			
			String name = null;
			String nameLat  = null;
			String eik = null;
			String ident = null;
			
			nodes = findNodesByName(node, "licenseHolderLegalName", false);
			if (nodes.size() > 0) {
				
				for (Node child : nodes) {
					String val = getSimpleNodeValue(child);
					LOGGER.debug("ЮЛ Притежател на лиценза - име: " + val);
					name = val;
					
				}
			}
			
			nodes = findNodesByName(node, "licenseHolderLegalNameLatin", false);
			if (nodes.size() > 0) {
				
				for (Node child : nodes) {
					String val = getSimpleNodeValue(child);
					LOGGER.debug("ЮЛ Притежател на лиценза - име (латиница): " + val);
					nameLat = val;
					
				}
			}
			
			nodes = findNodesByName(node, "licenseHolderLegalEik", false);
			if (nodes.size() > 0) {
				
				for (Node child : nodes) {
					String val = getSimpleNodeValue(child);
					LOGGER.debug("ЮЛ Притежател на лиценза - ЕИК: " + val);
					eik = val;
					
				}
			}
			
			nodes = findNodesByName(node, "licenseHolderInternationalLegalEik", false);
			if (nodes.size() > 0) {
				
				for (Node child : nodes) {
					String val = getSimpleNodeValue(child);
					LOGGER.debug("ЮЛ Притежател на лиценза - Межд.Идент.: " + val);
					ident = val;
					
				}
			}
			
			pritejatel.setRefName(name);
			pritejatel.setRefLatin(nameLat);
			pritejatel.setNflEik(eik);
			pritejatel.setFzlLnEs(ident); ///За сега тук
			
			
			
		}else {
			String[] labels = {"licenseHolderIdentifierType", "licenseHolderIdentifier", "licenseHolderFirstName", "licenseHolderMiddleName", "licenseHolderFamilyName" , "licenseHolderFirstNameLatin", "licenseHolderMiddleNameLatin", "licenseHolderFamilyNameLatin" };
			pritejatel = convertNodeToReferentFl(node, labels, "ФЛ Притежател на лиценза" );
		}
		
		
		if (pritejatel != null) {
			
			pritejatel.setAddressKoresp(pritejatelAdrr);
		}
		container.ref3 = pritejatel;
		//container.ref2 = pritejatel;
		
		
		
		
		
		ArrayList<Node> personsNodes = findNodesByName(node, "personsGrid", false);
		if (personsNodes.size() > 0) {
			int cnt = 0;
			for (Node personNode : personsNodes) {
				cnt++;
				
				Integer tipLiceVraz = null;
				
				EventDeinostVlpLice edvl = new EventDeinostVlpLice();
				nodes = findNodesByName(personNode, "personType", false);
				if (nodes.size() > 0) {
					
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Лице "+cnt+" (тип на връзката): " + val);
						Integer codeVal = Integer.parseInt(val);						
						String tekst = sd.decodeItem(BabhConstants.CODE_CLASSIF_TIP_VRAZ_LICE_INVITRO, codeVal, BabhConstants.CODE_DEFAULT_LANG, new Date());
						LOGGER.debug("Лице "+cnt+" (тип на връзката) Декодирано: " + tekst  );
						tipLiceVraz = codeVal;
					}
				}
				
				
				ReferentAddress addr = null;
				nodes = findNodesByName(personNode, "personAddress", false);
				if (nodes.size() > 0) {
					for (Node child : nodes) {
						
						addr = convertNodeToRefAddress(child, "Адрес на лице " + cnt);
					}
				}
				
				nodes = findNodesByName(personNode, "personAddressLatin", false);
				if (nodes.size() > 0) {
					
					for (Node child : nodes) {
						String val = getSimpleNodeValue(child);
						LOGGER.debug("Адрес на лице "+cnt+" (латиница): " + val);
						if (addr != null) {
							addr.setAddrTextLatin(val);
						}
						
					}
				}
				
				
				String tipLice = "ul"; //default
				nodes = findNodesByName(personNode, "licenseHolder", false);
				if (nodes.size() > 0) {
					
					for (Node child : nodes) {
						tipLice = getSimpleNodeValue(child);
						LOGGER.debug("Тип на Лице "+cnt+": " + tipLice);
						break; 
						
					}
				}
				
				Referent lice = null;		
				if (tipLice.equals("ul")) {
					lice =  createEmptyRef();
					lice.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
					
					String name = null;
					String nameLat  = null;
					String eik = null;
					String ident = null;
					
					nodes = findNodesByName(personNode, "personLegalName", false);
					if (nodes.size() > 0) {
						
						for (Node child : nodes) {
							String val = getSimpleNodeValue(child);
							LOGGER.debug("ЮЛ Лице "+cnt+" - име: " + val);
							name = val;
							
						}
					}
					
					nodes = findNodesByName(personNode, "personLegalNameLatin", false);
					if (nodes.size() > 0) {
						
						for (Node child : nodes) {
							String val = getSimpleNodeValue(child);
							LOGGER.debug("ЮЛ Лице "+cnt+" - име (латиница): " + val);
							nameLat = val;
							
						}
					}
					
					
					nodes = findNodesByName(personNode, "personsLegalEik", false);
					if (nodes.size() > 0) {
						
						for (Node child : nodes) {
							String val = getSimpleNodeValue(child);
							LOGGER.debug("ЮЛ Лице "+cnt+" - ЕИК: " + val);
							eik = val;
							
						}
					}
					
					nodes = findNodesByName(personNode, "personLegalEik", false);
					if (nodes.size() > 0) {
						
						for (Node child : nodes) {
							String val = getSimpleNodeValue(child);
							LOGGER.debug("ЮЛ Лице "+cnt+" - Межд.Идент.: " + val);
							ident = val;
							
						}
					}
					
					lice.setRefName(name);
					lice.setRefLatin(nameLat);
					lice.setNflEik(eik);
					lice.setFzlLnEs(ident); ///За сега тук
					
					
					
				}else {
					String[] labels = {"personsPhysicalIdentifierType", "personsPhysicalIdentifier", "personFirstName", "personMiddleName", "personFamilyName" , "personFirstNameLatin", "personMiddleNameLatin", "personFamilyNameLatin" };
					lice = convertNodeToReferentFl(personNode, labels, "ФЛ Лице " + cnt );
				}
				
				
				if (lice != null) {
					lice.setAddressKoresp(addr);
				}
				
				
				edvl.setCodeRef(lice.getCode());
				edvl.setEventDeinostVlpId(edv.getId());
				edvl.setReferent(lice);
				edvl.setTipVraz(tipLiceVraz);
				edv.getEventDeinostVlpLice().add(edvl);
				
				
			}
		}
		
		
		
		
		container.vpisvane.setEventDeinostVlp(edv);
		return container;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	private Referent createEmptyRef() {

		Referent ref = new Referent();
		ref.setCodePrev(0);
		ref.setCodeParent(0);
		ref.setCodeClassif(0);		
		ref.setDateOt(DateUtils.systemMinDate());
		ref.setDateDo(DateUtils.systemMaxDate());		
		ref.setDateReg(new Date());
		ref.setUserReg(-1);
		
		return ref;
		
	}


	public Doc setDocData (Doc doc,  List<FileWS> files, SystemData sd) throws UnexpectedResultException, DbErrorException{
		
		LOGGER.debug("Entering setDocData");
		
		
		
		
		
		LOGGER.debug("Number of attached files: " +  files.size());
		
		String codeUsluga = doc.getCodeAdmUsluga();
		
		if (codeUsluga == null) {
			//Тръгваме да обикаляме файловете, за да намерим дали има такъв от електронни форми
			if (files != null) {
				for (FileWS file : files) {
					
					if (file.getFileName() != null && file.getFileName().trim().toUpperCase().endsWith(".PDF")) {
						LOGGER.debug("Found PDF: " + file.getFileName());
						if (file.getFileContent() != null) {
							
							String xmlInfo = null;
							try {
								//PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC));		
								PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(file.getFileContent())));						
								
								//Метаданни за pdf-a
								PdfDocumentInfo info = pdfDoc.getDocumentInfo();
								
								//Къстом добавени данни :
								//jsonInfo = info.getMoreInfo("application.json_json");
								xmlInfo = info.getMoreInfo("application.json_xml");
								
								if (xmlInfo != null && !xmlInfo.isEmpty()) {
									//Трансформ към клас
									
									ServiceRequest sr = JAXBHelper.xmlToObject(ServiceRequest.class, xmlInfo);
									
									if (sr != null && sr.getPublicService() != null && sr.getPublicService().getIdentifier() != null ) {										
										codeUsluga = sr.getPublicService().getIdentifier().getIdentifier();
									}else {
										LOGGER.error("ServiceRequest or Public Serveice is null !!!!!" );
									}
									
								}else{
									LOGGER.debug("xml property is null !!!!!" );
								}
								
	
							} catch (IOException e) {
								LOGGER.error("Проблем при изчитането на PDF файл: " + file.getFileName());							
								throw new UnexpectedResultException("Проблем при изчитането на PDF файл: " + file.getFileName());							
							} catch (JAXBException e) {
								
								LOGGER.error("Проблем при обработката на инфомацията от еформи в PDF файл: " + file.getFileName());	
								LOGGER.error("xmlInfo=" + xmlInfo);
								throw new UnexpectedResultException("Проблем при обработката на инфомацията от еформи в PDF файл: " + file.getFileName());		
							}
						}
					}
				}
			}
		}
		
		//Търсим дефолтния вид документ
		String nastr = null;
		Integer vidDocDef = 6;
		Integer idRegisterDef = -1;
		try {
			nastr = sd.getSettingsValue("deloweb.defaultVidDoc");
			if (nastr == null || nastr.trim().isEmpty()) {
				LOGGER.error("!!!!!!!!!!!!!  deloweb.defaultVidDoc setting not found in SYSTEM_OPTIONS !!!!!");
			}
			
			vidDocDef = Integer.parseInt(nastr);
			
		} catch (Exception e) {
			LOGGER.error("Unexpected error when seraching default vidDoc !" , e);
		}
		
		
		
		if (codeUsluga != null) {
			RegisterOptionsDocsIn docObj = sd.getRegisterByNomUsuga().get(codeUsluga);
			
			if (docObj != null) {										
				doc.setRegisterId(docObj.getIdRegister());
				doc.setDocVid(docObj.getVidDoc());

				doc.setPayType(docObj.getPayCharacteristic());  // сетваме типа на пащанетп
				doc.setPayAmount(docObj.getPayAmount());  
				
				
				
			}else {
				LOGGER.debug(codeUsluga + " not found in RegisterOptionsDocsIn. Setting default values !");
				doc.setDocVid(vidDocDef);
				doc.setRegisterId(idRegisterDef);
			}
		}else {
			LOGGER.debug("Public Serveice Code not found. Setting default values !!!!");
			doc.setDocVid(vidDocDef);
			doc.setRegisterId(idRegisterDef);
		}
		
		
		
		LOGGER.debug("Exiting setDocData");
		return doc;
	}
	

	
	////////////////////////////////////// NODES ///////////////////////////////////////////////
	
	
//	private void printNode(Node node, String prefix) {
//		if (node != null) {
//			System.out.println(prefix + node.getNodeName() + " = " + node.getNodeValue() + "\t\t"+ node.getNodeType());
//			if (node.getChildNodes() != null) {
//				for (int i = 0; i < node.getChildNodes().getLength(); i++) {
//					printNode((Node) node.getChildNodes().item(i), prefix + "\t");
//				}
//				
//			}
//		}
//	}
	
	
	private  ArrayList<Node> findNodesByName(Node node, String nodeName, boolean startWith){
		ArrayList<Node> result = new ArrayList<Node>();
		
		if (nodeName == null) {
			return new ArrayList<Node>();
		}
		
		if (startWith) {
			if (node.getNodeName().toUpperCase().startsWith(nodeName.trim().toUpperCase())) {
				result.add(node);
			}	
		}else {
			if (node.getNodeName().equalsIgnoreCase(nodeName.trim())) {
				result.add(node);
			}
		}
		
		if (node.getChildNodes() != null) {
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {				
				result.addAll(findNodesByName(node.getChildNodes().item(i), nodeName, startWith));
			}
		}
		
		
		
		return result;
	}
	
	private  ArrayList<Node> findNodesIncludeInName(Node node, String tekst){
		ArrayList<Node> result = new ArrayList<Node>();
		
		if (tekst == null) {
			return new ArrayList<Node>();
		}
		
		
		if (node.getNodeName().toUpperCase().toUpperCase().contains(tekst.trim().toUpperCase())) {
			result.add(node);
		}	
		
		
		if (node.getChildNodes() != null) {
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {				
				result.addAll(findNodesIncludeInName(node.getChildNodes().item(i), tekst));
			}
		}
		
		
		
		return result;
	}
	
	
	
	String getSimpleNodeValue(Node node) {
		if (node.getNodeValue() != null) {
			return node.getNodeValue();			
		}else {
			if (node.getChildNodes() != null) {
				for (int i = 0; i < node.getChildNodes().getLength(); i++) {	
					Node child = node.getChildNodes().item(i);
					if ( child.getNodeName() != null && child.getNodeName().equalsIgnoreCase( "#text")) {						
						if (child.getNodeValue() != null && ! child.getNodeValue().trim().isEmpty())
						return child.getNodeValue();
					}
				}
				
				for (int i = 0; i < node.getChildNodes().getLength(); i++) {	
					Node child = node.getChildNodes().item(i);
					if ( child.getNodeName() != null && child.getNodeName().equalsIgnoreCase( "value")) {						
						return getSimpleNodeValue(child);
					}
				}
			}
		}
		
		return null;
	}
	
	
	private Mps mergeMps(Mps mpsDb, Mps mps) {
		// TODO За сега най-лесното - реплейсвам всичко
		
		mps.setId(mpsDb.getId());
		
		return mps;
	}
	
	
	
	private Referent mergeReferents(Referent refDb, Referent newRef) {
		
		// За сега само имената, после евентуално и адреси ....
		//TODO Адрес
		
		
		String name = "";
		if (! SearchUtils.isEmpty(newRef.getIme())) {
			refDb.setIme(newRef.getIme());
			name += newRef.getIme() + " ";
		}
		
		if (! SearchUtils.isEmpty(newRef.getPrezime())) {
			refDb.setPrezime(newRef.getPrezime());
			name += newRef.getPrezime() + " ";
		}
		
		if (! SearchUtils.isEmpty(newRef.getFamilia())) {
			refDb.setFamilia(newRef.getFamilia());
			name += newRef.getFamilia() + " ";
		}
				
		if (! SearchUtils.isEmpty(name)) {
			refDb.setRefName(name);
		}else {
			if (! SearchUtils.isEmpty(newRef.getRefName())){
				refDb.setRefName(newRef.getRefName());
			}
		}
		
		//if (! SearchUtils.isEmpty(newRef.getRefLatin())) {
			refDb.setRefLatin(null);
		//}
		
		
		if (! SearchUtils.isEmpty(newRef.getContactEmail())) {
			refDb.setContactEmail(newRef.getContactEmail());
		}
		
		if (! SearchUtils.isEmpty(newRef.getContactPhone())) {
			refDb.setContactPhone(newRef.getContactPhone());
		}
		
		
		if (newRef.getAddressKoresp() != null) {
			refDb.mergeAddress(newRef.getAddressKoresp(), BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
		}
		
		
		return refDb;
		
		
	}
	
	private ReferentAddress convertNodeToRefAddress(Node nodesAddr, String logTooltip) throws DbErrorException {
		
		ReferentAddress address = new ReferentAddress();
		address.setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
		
		
		
		if (logTooltip == null) {
			logTooltip = "";
		}
		
		ArrayList<Node> nodesAddrP = findNodesByName(nodesAddr, "Country", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " Държава: " + val);				
		}
			
		String codeDarj = null;
		nodesAddrP = findNodesByName(nodesAddr, "CountryCode", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " Държава код : " + val);
			if (val.equalsIgnoreCase("BG")) {
				address.setAddrCountry(BabhConstants.CODE_ZNACHENIE_BULGARIA);
			}
			codeDarj = val;
		}
		
		Integer ekkate = null;
		nodesAddrP = findNodesByName(nodesAddr, "settlementCode", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " ekatte: " + val);
			ekkate = Integer.parseInt(val);
			address.setEkatte(ekkate);
			
		}
		
		nodesAddrP = findNodesByName(nodesAddr, "addressLatin", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " ekatte: " + val);
			address.setAddrTextLatin(val);
			
		}
		
		
		
		
		String locationName = null;
		String buildingNumber = null;
		String entrance = null;
		String floor = null;
		String apartment = null;
		
		nodesAddrP = findNodesByName(nodesAddr, "LocationName", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " Улица: " + val);
			locationName = val;
			
		}
		
		nodesAddrP = findNodesByName(nodesAddr, "buildingNumber", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " Улица Номер: " + val);	
			buildingNumber = val;
		}
		
		nodesAddrP = findNodesByName(nodesAddr, "entrance", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " Улица Вход: " + val);
			entrance = val;
		}
		
		nodesAddrP = findNodesByName(nodesAddr, "floor", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " Улица Етаж: " + val);	
			floor = val;
		}
		
		nodesAddrP = findNodesByName(nodesAddr, "apartment", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " Улица Апартамент: " + val);		
			apartment = val;
		}
		
		nodesAddrP = findNodesByName(nodesAddr, "postCode", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " Пощенски код: " + val);	
			address.setPostCode(val);
		}
		
		String fullAddr = null;
		nodesAddrP = findNodesByName(nodesAddr, "FullAddress", false);
		for (Node childA : nodesAddrP) {
			String val = getSimpleNodeValue(childA);
			LOGGER.debug(logTooltip + " Пълен адрес: " + val);	
			fullAddr = val;
		}
		
		
		String restA = "";
		
		if (locationName != null) {						
			restA += locationName + ","; 
		}
		
		if (buildingNumber != null) {						
			restA += " №/блок " + buildingNumber + ","; 
		}
		
		if (entrance != null) {						
			restA += " вход " + entrance + ","; 
		}
		
		if (floor != null) {						
			restA += " етаж " + floor + ","; 
		}
		
		if (apartment != null) {						
			restA += " ап.  " + apartment + ","; 
		}
		
		if (fullAddr != null) {						
			restA += fullAddr + ","; 
		}
		
		if (restA.length() > 0) {
			restA = restA.substring(0, restA.length()-1);
		}
		
		address.setAddrText(restA);
		LOGGER.debug(logTooltip + " Адрес: " + restA);
		
		
		if (sd != null && codeDarj != null) {
			
			List<SystemClassif> allC = sd.getItemsByCodeExt(BabhConstants.CODE_CLASSIF_DARJAVI_TRETI_STRANI, codeDarj, BabhConstants.CODE_DEFAULT_LANG, new Date());
			if (allC != null && allC.size() > 0) {
				address.setAddrCountry(allC.get(0).getCode());
			}
		}
		
		
		return address;
	}
	
//	private ReferentAddress convertNodeToObektAddress(Node nodesAddr, String logTooltip) {
//		
//		ReferentAddress address = new ReferentAddress();
//		
//		
//		
//		
//		if (logTooltip == null) {
//			logTooltip = "";
//		}
//		
//		ArrayList<Node> nodesAddrP = findNodesByName(nodesAddr, "Country", false);
//		for (Node childA : nodesAddrP) {
//			String val = getSimpleNodeValue(childA);
//			LOGGER.debug(logTooltip + " Държава: " + val);				
//		}
//			
//		nodesAddrP = findNodesByName(nodesAddr, "CountryCode", false);
//		for (Node childA : nodesAddrP) {
//			String val = getSimpleNodeValue(childA);
//			LOGGER.debug(logTooltip + " Държава код : " + val);
//			if (val.equalsIgnoreCase("BG")) {
//				address.setAddrCountry(BabhConstants.CODE_ZNACHENIE_BULGARIA);
//			}
//			
//		}
//		
//		nodesAddrP = findNodesByName(nodesAddr, "settlementCode", false);
//		for (Node childA : nodesAddrP) {
//			String val = getSimpleNodeValue(childA);
//			LOGGER.debug(logTooltip + " ekatte: " + val);
//			address.setEkatte(Integer.parseInt(val));
//			
//		}
//		
//		String locationName = null;
//		String buildingNumber = null;
//		String entrance = null;
//		String floor = null;
//		String apartment = null;
//		
//		nodesAddrP = findNodesByName(nodesAddr, "LocationName", false);
//		for (Node childA : nodesAddrP) {
//			String val = getSimpleNodeValue(childA);
//			LOGGER.debug(logTooltip + " Улица: " + val);
//			locationName = val;
//			
//		}
//		
//		nodesAddrP = findNodesByName(nodesAddr, "buildingNumber", false);
//		for (Node childA : nodesAddrP) {
//			String val = getSimpleNodeValue(childA);
//			LOGGER.debug(logTooltip + " Улица Номер: " + val);	
//			buildingNumber = val;
//		}
//		
//		nodesAddrP = findNodesByName(nodesAddr, "entrance", false);
//		for (Node childA : nodesAddrP) {
//			String val = getSimpleNodeValue(childA);
//			LOGGER.debug(logTooltip + " Улица Вход: " + val);
//			entrance = val;
//		}
//		
//		nodesAddrP = findNodesByName(nodesAddr, "floor", false);
//		for (Node childA : nodesAddrP) {
//			String val = getSimpleNodeValue(childA);
//			LOGGER.debug(logTooltip + " Улица Етаж: " + val);	
//			floor = val;
//		}
//		
//		nodesAddrP = findNodesByName(nodesAddr, "apartment", false);
//		for (Node childA : nodesAddrP) {
//			String val = getSimpleNodeValue(childA);
//			LOGGER.debug(logTooltip + " Улица Апартамент: " + val);		
//			apartment = val;
//		}
//		
//		nodesAddrP = findNodesByName(nodesAddr, "postCode", false);
//		for (Node childA : nodesAddrP) {
//			String val = getSimpleNodeValue(childA);
//			LOGGER.debug(logTooltip + " Пощенски код: " + val);	
//			address.setPostCode(val);
//		}
//		
//		String restA = "";
//		
//		if (locationName != null) {						
//			restA += locationName + ","; 
//		}
//		
//		if (buildingNumber != null) {						
//			restA += " №/блок " + buildingNumber + ","; 
//		}
//		
//		if (entrance != null) {						
//			restA += " вход " + entrance + ","; 
//		}
//		
//		if (floor != null) {						
//			restA += " етаж " + floor + ","; 
//		}
//		
//		if (apartment != null) {						
//			restA += " ап.  " + apartment + ","; 
//		}
//		
//		if (restA.length() > 0) {
//			restA = restA.substring(0, restA.length()-1);
//		}
//		
//		address.setAddrText(restA);
//		LOGGER.debug(logTooltip + " Адрес: " + restA);
//		
//		return address;
//	}
	
	
	private Referent convertNodeToReferentFl(Node node, String[] labels, String logTooltip) throws DbErrorException {
		
		Referent ref = createEmptyRef();
		ref.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
		
		String ident = null;
		String identType = null;
		String imeLatin = null;
		String prezimeLatin = null;
		String familiaLatin = null;;
		
		//String[] labels = {"managerIdType", "managerId", "managerIdType", "managerName", "managerFamilyName"}; 
		
		
		if (logTooltip == null) {
			logTooltip = "";
		}
		
		for (String label : labels) {
			ArrayList<Node> children = findNodesByName(node, label, false);
			for (Node child : children) {
				String val = getSimpleNodeValue(child);
				
				if (val == null || val.equalsIgnoreCase("null")) {
					continue;
				}
				
				//LOGGER.debug(logTooltip + " " + label + ": " + val);		
				
				if (label.toUpperCase().endsWith("ID") || label.equalsIgnoreCase("egnLnch") || label.toUpperCase().endsWith("Identifier".toUpperCase())) {
					ident = val;
					LOGGER.debug(logTooltip + " Идентификатор: " + val);
					break;
				}
				
				if (label.toUpperCase().endsWith("IDTYPE") || label.toUpperCase().endsWith("IdentifierType".toUpperCase()) ) {
					identType = val;
					LOGGER.debug(logTooltip + " Тип Идентификатор: " + val);
					break;
				}
				
				if (label.equalsIgnoreCase("egnLnchType")) {					
					identType = val;
					LOGGER.debug(logTooltip + " Тип Идентификатор: " + val);
					
				}
				
				if (label.toUpperCase().contains("LATIN")) {
					
					//Първо обработваме латиницата
					if (label.toUpperCase().contains("MIDDLE")) {						
						LOGGER.debug(logTooltip + " Презиме (Латиница): " + val);
						prezimeLatin = val;
						break;
					}
					
					if (label.toUpperCase().contains("NAME")) {
						
						if (label.toUpperCase().contains("FAMILYNAME")) {							
							LOGGER.debug(logTooltip + " Фамилия (Латиница): " + val);
							familiaLatin = val;
							break;
						}else {							
							LOGGER.debug(logTooltip + " Име (латиница): " + val);
							imeLatin = val;
							break;
						}
					}
					
				}
				
				
				
				
				
				
				
				if (label.toUpperCase().contains("MIDDLE")) {
					ref.setPrezime(val);
					LOGGER.debug(logTooltip + " Презиме: " + val);
					break;
				}
				
				if (label.toUpperCase().contains("URN")) {
					ref.setUrn(val);
					LOGGER.debug(logTooltip + " УРН: " + val);
					break;
				}
				
				
				
				if (label.toUpperCase().endsWith("NAME")) {
					
					if (label.toUpperCase().endsWith("FAMILYNAME")) {
						ref.setFamilia(val);
						LOGGER.debug(logTooltip + " Фамилия: " + val);
						break;
					}else {
						ref.setIme(val);
						LOGGER.debug(logTooltip + " Име: " + val);
						break;
					}
				}
				
				if (label.toUpperCase().contains("EDUCATION")) {					
					identType = val;
					LOGGER.debug(logTooltip + " Образование: " + val);
					ref.setObrazovanie(Integer.parseInt(val));
					
				}
				
				
			}
			
		}
		
		if (ident != null) {
			
			ReferentDAO rdao = new ReferentDAO(ActiveUser.DEFAULT);
			Referent refDb = null;
			
			if (identType.equalsIgnoreCase("idn") || identType.equalsIgnoreCase("1")  || identType.equalsIgnoreCase("EGN")) {
				ref.setFzlEgn(ident);
				refDb = rdao.findByIdent(null, ref.getFzlEgn(), null, BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
			}else {
				ref.setFzlLnc(ident);
				refDb = rdao.findByIdent(null, null, ref.getFzlLnc(), BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
			}
			
			
			if (refDb != null) {
				ref = mergeReferents(refDb, ref);
			}
			
			
		}
		
		String name = "";
		if (ref.getIme() != null && ! ref.getIme().trim().isEmpty()) {
			name += ref.getIme().trim() + " "; 
		}
		
		if (ref.getPrezime() != null && ! ref.getPrezime().trim().isEmpty()) {
			name += ref.getPrezime().trim() + " "; 
		}
		
		if (ref.getFamilia() != null && ! ref.getFamilia().trim().isEmpty()) {
			name += ref.getFamilia().trim() + " "; 
		}
		
		
		String nameLat = "";
		if (imeLatin != null && ! imeLatin.trim().isEmpty()) {
			nameLat += imeLatin.trim() + " "; 
		}
		
		if (prezimeLatin != null && ! prezimeLatin.trim().isEmpty()) {
			nameLat += prezimeLatin.trim() + " "; 
		}
		
		if (familiaLatin != null && ! familiaLatin.trim().isEmpty()) {
			nameLat += familiaLatin.trim() + " "; 
		}
		
		
		
		name = name.trim();
		nameLat = nameLat.trim();
		ref.setRefName(name);
		ref.setRefLatin(nameLat);
		return ref;
	}
	
	
	private ReferentDoc mergeDoc(ReferentDoc docDb, ReferentDoc newDoc) {
		// TODO Merge
		return docDb;
	}
	
	
	
	public EgovContainer parseEgovLight(DocWS docWS, Doc doc, SystemData sd) throws DbErrorException, UnexpectedResultException, InvalidParameterException {
		
		LOGGER.debug("Entering convertEformToVpisvane");
		EgovContainer eCon = new EgovContainer();
		eCon.doc = doc;
		
		
		//Тръгваме да обикаляме файловете, за да намерим дали има такъв от електронни форми
		if (docWS.getFiles() != null && docWS.getFiles().size() > 0) {
			
			LOGGER.debug("Number of attached files: " +  docWS.getFiles().size());
			
			for (FileWS file : docWS.getFiles()) {
				
				
				
				if (file.getFileName() != null && file.getFileName().trim().toUpperCase().endsWith(".PDF")) {
					LOGGER.debug("Found PDF: " + file.getFileName());
					if (file.getFileContent() != null) {
						
						try {
							//PdfDocument pdfDoc = new PdfDocument(new PdfReader(SRC));		
							PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(file.getFileContent())));						
							
							//Метаданни за pdf-a
							PdfDocumentInfo info = pdfDoc.getDocumentInfo();
							
							//Къстом добавени данни :
							//jsonInfo = info.getMoreInfo("application.json_json");
							String xmlInfo = info.getMoreInfo("application.json_xml");
							
							System.out.println(xmlInfo);
							
							
							if (xmlInfo != null && !xmlInfo.isEmpty()) {
								//Трансформ към клас
								
								ServiceRequest sr = JAXBHelper.xmlToObject(ServiceRequest.class, xmlInfo);
								
								if (sr != null ) {
									
									eCon = fillVpisvaneByServiceRequest(sr, eCon, sd, true);
																		
								}else {
									LOGGER.error("ServiceRequest sr is null !!!!!" );
								}
								
							}else{
								LOGGER.debug("xml property is null !!!!!" );
							}
							

						} catch (IOException e) {
							LOGGER.error("Проблем при изчитането на PDF файл: " + file.getFileName());							
							throw new UnexpectedResultException("Проблем при изчитането на PDF файл: " + file.getFileName());							
						} catch (JAXBException e) {
							
							LOGGER.error("Проблем при обработката на инфомацията от еформи в PDF файл: " + file.getFileName());							
							throw new UnexpectedResultException("Проблем при обработката на инфомацията от еформи в PDF файл: " + file.getFileName());		
						}
					}
				}
			}
		}else {
			LOGGER.debug("No files attached !!! ");
		}
		
		
		
		LOGGER.debug("Exiting convertEformToVpisvane");
		return eCon;
	}
	
	
	private Mps mergeMPS(Mps mpsDb, Mps mps, Referent sobst, EgovContainer container) {
		if (mps == null) {
			return mpsDb;
		}
		
		if (mpsDb == null) {
			return mps;
		}
		
		if (mps.getModel() != null && ! mps.getModel().isEmpty()) {
			
			if (mpsDb.getModel() != null && ! mps.getModel().trim().equalsIgnoreCase(mpsDb.getModel())) {
				container.warnings.add("Марка/модел на съществуващо в базата транспортно средство с номер "+mps.getNomer()+" не съвпада с подаденото в заявлението !" + mpsDb.getModel() + " <> " + mps.getModel() );
			}
			
			mpsDb.setModel(mps.getModel());
		}
		
		if (mps.getVid() != null) {
			
			if (mpsDb.getVid() != null && ! mps.getVid().equals(mpsDb.getVid())) {
				container.warnings.add("Видът на съществуващо в базата транспортно средство не съвпада с подаденото в заявлението !" + mpsDb.getVid() + " <> " + mps.getVid() );
			}
			
			mpsDb.setVid(mps.getVid());
		}
		
		if (mps.getPlosht() != null && ! mps.getPlosht().isEmpty()) {
			
			if (mpsDb.getPlosht() != null && ! mps.getPlosht().trim().equalsIgnoreCase(mpsDb.getPlosht())) {
				container.warnings.add("Площта на съществуващо в базата транспортно средство с номер "+mps.getNomer()+" не съвпада с подаденото в заявлението !" + mpsDb.getPlosht() + " <> " + mps.getPlosht() );
			}
			
			mpsDb.setPlosht(mps.getPlosht());
		}
		
		if (mps.getNavigation() != null) {
			
			if (mpsDb.getNavigation() != null && ! mps.getNavigation().equals(mpsDb.getNavigation())) {
				container.warnings.add("Данните за наличие на навигационна система в базата за транспортно средство с номер "+mps.getNomer()+ " не съвпада с подаденото в заявлението !" + mpsDb.getNavigation() + " <> " + mps.getNavigation() );
			}
			
			mpsDb.setNavigation(mps.getNavigation());
		}
		
		
		if (mps.getNomDatReg() != null) {
			
			if (mpsDb.getNomDatReg() != null && ! mps.getNomDatReg().equals(mpsDb.getNomDatReg())) {
				container.warnings.add("Данните за No/дата на рег. талон в базата за транспортно средство с номер "+mps.getNomer()+ " не съвпада с подаденото в заявлението !" + mpsDb.getNomDatReg() + " <> " + mps.getNomDatReg() );
			}
			
			mpsDb.setNomDatReg(mps.getNomDatReg());
		}
		
		if (mps.getDopInfo() != null) {
			
//			if (mpsDb.getDopInfo() != null && ! mps.getDopInfo().equals(mpsDb.getDopInfo())) {
//				container.warnings.add("Данните за No/дата на рег. талон в базата за транспортно средство с номер "+mps.getNomer()+ " не съвпада с подаденото в заявлението !" + mpsDb.getDopInfo() + " <> " + mps.getDopInfo() );
//			}
			
			mpsDb.setDopInfo(mps.getDopInfo());
		}
		
		
		boolean hasSobst = false;
		if (mpsDb.getMpsLice() != null) {
			for (MpsLice mpsLice : mpsDb.getMpsLice()) {
				if (mpsLice.getTipVraz() != null && mpsLice.getTipVraz().equals(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK)) {
					Referent sobstDb = mpsLice.getReferent();
					hasSobst = true;
					boolean hasSobErr = false;
					if (sobstDb != null) {
						if (!hasSobErr && sobstDb.getRefType() != null && sobst.getRefType() != null && ! sobstDb.getRefType().equals(sobst.getRefType())) {
							container.warnings.add("Данните за собственик в базата за транспортно средство с номер "+mps.getNomer()+ " не съвпада с подаденото в заявлението ! ");
							mpsLice.setReferent(sobst);
							mpsLice.setCodeRef(sobst.getCode());
							hasSobErr = true;
						}
						
						if (!hasSobErr && sobstDb.getNflEik() != null && sobst.getNflEik() != null && ! sobstDb.getNflEik().equals(sobst.getNflEik())) {
							container.warnings.add("Данните за собственик в базата за транспортно средство с номер "+mps.getNomer()+ " не съвпада с подаденото в заявлението !"  );
							mpsLice.setReferent(sobst);
							mpsLice.setCodeRef(sobst.getCode());
							hasSobErr = true;
						}
						
						if (!hasSobErr && sobstDb.getFzlEgn() != null && sobst.getFzlEgn() != null && ! sobstDb.getFzlEgn().equals(sobst.getFzlEgn())) {
							container.warnings.add("Данните за собственик в базата за транспортно средство с номер "+mps.getNomer()+ " не съвпада с подаденото в заявлението !"  );
							mpsLice.setReferent(sobst);
							mpsLice.setCodeRef(sobst.getCode());
							hasSobErr = true;
						}
						
						if (!hasSobErr && sobstDb.getFzlLnc() != null && sobst.getFzlLnc() != null && ! sobstDb.getFzlLnc().equals(sobst.getFzlLnc())) {
							container.warnings.add("Данните за собственик в базата за транспортно средство с номер "+mps.getNomer()+ " не съвпада с подаденото в заявлението !"  );
							mpsLice.setReferent(sobst);
							mpsLice.setCodeRef(sobst.getCode());
							hasSobErr = true;
						}
						
						if (!hasSobErr && sobstDb.getNflEik() != null && sobst.getFzlEgn() != null ) {
							container.warnings.add("Данните за собственик в базата за транспортно средство с номер "+mps.getNomer()+ " не съвпада с подаденото в заявлението !"  );
							mpsLice.setReferent(sobst);
							mpsLice.setCodeRef(sobst.getCode());
							hasSobErr = true;
						}
						
						if (!hasSobErr && sobstDb.getFzlEgn() != null && sobst.getNflEik() != null ) {
							container.warnings.add("Данните за собственик в базата за транспортно средство с номер "+mps.getNomer()+ " не съвпада с подаденото в заявлението !"  );
							mpsLice.setReferent(sobst);
							mpsLice.setCodeRef(sobst.getCode());
							hasSobErr = true;
						}
						
						//TODO тук не знам какво ще става
					}
					
				}
			}
		}
		
		if (! hasSobst) {
			MpsLice mpsLice = new MpsLice();
			mpsLice.setReferent(sobst);
			mpsLice.setCodeRef(sobst.getCode());
			mpsLice.setTipVraz(BabhConstants.CODE_ZNACHENIE_VRAZ_LICE_MPS_SOBSTVENIK);
			
			if (mpsDb.getMpsLice() == null) {
				mpsDb.setMpsLice(new ArrayList<MpsLice>());
			}
			mpsDb.getMpsLice().add(mpsLice);
			
		}
		
		return mpsDb;
	}
	
	
}
