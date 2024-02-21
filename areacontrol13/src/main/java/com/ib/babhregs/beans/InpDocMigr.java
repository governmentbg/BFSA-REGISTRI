package com.ib.babhregs.beans;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.DocDAO;
import com.ib.babhregs.db.dto.Doc;
import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.babhregs.db.dto.RegisterOptionsDocsIn;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.utils.SearchUtils;
import  com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dao.ReferentDAO;

@Named
@ViewScoped
public class InpDocMigr extends IndexUIbean {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = -2099028078361201777L;
	private static final Logger LOGGER = LoggerFactory.getLogger(InpDocMigr.class);
	
	private static final String FORM = "formInpZaiavl";
	
	private SystemData sd;
	private UserData ud;

	private Date decodeDate;

	/** Документ, в рамките на БАБХ */
	private Doc doc;
	private transient DocDAO dao;
	
	private Integer registrId = null;
	private Integer vidZaiavl = null;
	private String rnDoc = null;
	private Date dateDoc = null;
	private Integer codeZaiavitel = null;
	private boolean visGOTO = false;
	
	private Integer registerId;                   // Регистър от настройките по вид документ
	private RegisterOptions regOptions;	 // Всички настройки за регистъра
	private RegisterOptionsDocsIn docOpt;   // 
	
	private Integer objType = null;          // Тип на бекта
	

	
	@PostConstruct
	public void init() {

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("BabhZaiavEditBean init!!!!!!");
		}
	
		this.sd = (SystemData) getSystemData();
		this.ud = getUserData(UserData.class);
		this.setDecodeDate(new Date());
		this.dao = new DocDAO(getUserData());
		
		clearAll ();
	}

	public void clearAll () {
		this.registrId = null;       // id регистратура (инстанция)
		this.vidZaiavl = null;      // Вид заявление
		// Тези данни се изчистват винаги за нов документ
		clearDanniNewDoc ();	
	}
	
	public void clearDanniNewDoc () {
		this.doc = null;
		this.rnDoc = null;
		this.dateDoc = null;
		this.codeZaiavitel = null;
		this.registerId = null;
		this.regOptions = null;
		this.docOpt = null;
		this.objType = null;
		this.visGOTO = false;
	}
	
	public void  newDoc () {
		this.visGOTO = false;
	}
	public void clearZaiavitel () {
		this.codeZaiavitel = null;
		this.visGOTO = false;
	}
	public void changeDateDoc () {
		this.visGOTO = false;
	}
	
	
	public String getNameZaiavitel () {
		if (this.codeZaiavitel == null)  return null;
            
		Referent ref = null;
             try {
				 ref = new ReferentDAO (null).findByCode(this.codeZaiavitel,  new Date(), false);
			} catch (DbErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		String name = "";
		if (ref.getRefType() != null && ref.getRefType().intValue() == BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL ) {
			if (ref.getIme()  != null && !ref.getIme().trim().isEmpty()) name += ref.getIme();
			if (ref.getPrezime() != null && !ref.getPrezime().trim().isEmpty()) name += " " + ref.getPrezime();
			if (ref.getFamilia() != null && !ref.getFamilia().trim().isEmpty())  name += " " + ref.getFamilia ();
			name = name.trim();
			if (name.isEmpty()) {
			   if (ref.getRefName() != null && !ref.getRefName().trim().isEmpty())  name =  ref.getRefName();
			}		
			if (ref.getFzlEgn() != null)  name += " (ЕГН=" + ref.getFzlEgn() + ") ";
			else if (ref.getFzlLnc() != null)  name += " (ЛНЧ=" + ref.getFzlLnc() + ") ";
			else if (ref.getFzlLnEs () != null) name +=  " (ЛНЕС=" + ref.getFzlLnEs() + ") ";
		} else {
			if (ref.getRefName() != null && !ref.getRefName().trim().isEmpty())  name += ref.getRefName();
			else {
				if (ref.getIme()  != null && !ref.getIme().trim().isEmpty()) name += ref.getIme();
				if (ref.getPrezime() != null && !ref.getPrezime().trim().isEmpty()) name += " " + ref.getPrezime();
				if (ref.getFamilia() != null && !ref.getFamilia().trim().isEmpty())  name += " " + ref.getFamilia ();
				name = name.trim();
			}
			
			if (ref.getNflEik() != null) name += " (ЕИК=" + ref.getNflEik() + ") ";
		} 
		
		return name;
		
	}
	
	public String actionSave() {
		try {
            if (!(checkData()))  return null;
            if (!setDocProperties())  return null;
			
				JPA.getUtil().runInTransaction(() -> this.doc = this.dao.save(doc) );
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,
						getMessageResourceString(UI_beanMessages, SUCCESSAVEMSG));
			

		} catch (Exception e) {
			LOGGER.error("Грешка при запис на документа!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					getMessageResourceString(UI_beanMessages, ERRDATABASEMSG, e) + " - " + e.getLocalizedMessage());
			return null;
		}
		 
        this.visGOTO = true;  		
		return "";
	}
	
	public boolean checkData() {
		boolean ret = true;
		if (this.registrId == null || this.registrId.intValue() <= 0 ) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Не е зададена инстанция, работила по вписването!");
			ret = false;
		}
		
		if (this.rnDoc == null || this.rnDoc.trim().isEmpty()) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Не е зададен рег. номер за заявлението! ");
			ret = false;
		}
		if (this.dateDoc == null ) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Не е зададена дата зя  рег. номер ! ");
			ret = false;
		}	
		
		if (this.vidZaiavl == null || this.vidZaiavl.intValue() <= 0 ) {
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,
					"Не е зададен вид на заявлението! ");
			ret = false;
		}
	
		return ret;
	}
	
	public boolean setDocProperties () {
		  this.doc = new Doc ();
		  
		  this.doc.setRegistraturaId(this.registrId);
		  this.doc.setRnDoc(this.rnDoc.trim());
		  this.doc.setDocDate(this.dateDoc);
		  this.doc.setDocVid(this.vidZaiavl);
		  this.doc.setFreeAccess(Integer.valueOf(0));
		  
		  // Подател на документа
		  this.doc.setCodeRefCorresp(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_REF_SLUJEBEN));
		  this.doc.setKachestvoLice(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_PALNOM));   // Пълномощник
		  // Заявител
		  if (this.codeZaiavitel != null)
			  this.doc.setCodeRefZaiavitel(this.codeZaiavitel);
		  
		  try {
			  this.doc.setOtnosno(this.sd.decodeItem(BabhConstants.CODE_CLASSIF_VIDOVE_ZAIAVLENIA , this.vidZaiavl, getCurrentLang(), new Date()));
			  
		  } catch (Exception e) {
			  this.doc.setOtnosno(null);
		  }
		  
		  this.doc.setDocType(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_DOC_TYPE_IN ));    // Тип на документ - входен
		  this.doc.setStatus(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_DOC_STATUS_WAIT) );    // Статус на документа
		  this.doc.setStatusDate(new Date());    // Дата статус
		  this.doc.setPayType(Integer.valueOf(BabhConstants.CODE_ZNACHENIE_PAY_TYPE_NOPAY));  // Тип за плащането
		  
		  // От настройките - по вид документ се взима регистър
		  //  и всички настройки за регистъра
		           
		  this.registerId = this.sd.getRegisterByVidDoc().get(this.doc.getDocVid());
		  if (this.registerId == null) {
			  JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "За зададения вид заявление няма въведена настройка за  ID на регистър!");
			  return false;
		  }
		  this.doc.setRegisterId(this.registerId);       //   id на регистър за лид документ  
		  
		  this.regOptions = this.sd.getRoptions().get(this.registerId);   // всички настройки за регистъра
		  if (this.regOptions == null ) {
			  JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "За зададения вид заявление няма въведени настройки за регистър !");
			  return false;
		  }
		  
		  this.objType = this.regOptions.getObjectType();                  // Тип на обект
		  if ( this.objType == null ) {
			  JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "За зададения вид заявление във въведени настройки за регистър няма тип на обект!");
			  return false;
		  }		  
		    
		 // Проверка за предназначение на заявлението
			
			List<RegisterOptionsDocsIn> lstDocsIn = this.regOptions.getDocsIn(); 	
			  if (lstDocsIn == null || lstDocsIn.isEmpty()) {
				  JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "За зададения вид заявление във въведени настройки за регистър няма въведени опции за входни документи, за да се определи предназначение и код на услуга!");
				  return false;
			  }
		

				this.docOpt = new RegisterOptionsDocsIn();
				 boolean pr = false;
			for (RegisterOptionsDocsIn item : lstDocsIn) {
				if ((item.getVidDoc()).equals(this.vidZaiavl)) {
					this.docOpt = item;
					pr = true;
					break;
				}
			}	
			  
			if (!pr ) {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Във въведени настройки за регистър и въведени опции за входни документи няма опция за въведения вид заявление, за да се определи предназначение и код на услуга!");
                this.docOpt = null;
                  return false;
			}
			
			// Заявлението трябва да е за първоначално въвеждане..
			if  (this.docOpt.getPurpose() == null || !(this.docOpt.getPurpose().intValue() == BabhConstants.CODE_ZNACHENIE_ZAIAV_PARVONACHALNO)) {
				  JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Заявлението трябва да бъде за  първоначално въвеждане! Изберете нов вид заявление!");
				return false;
			}
		  		    
		  this.doc.setCodeAdmUsluga(this.docOpt.getMeuNimber());      // Код на услуга
		  
		 
		  return true;
	}
	
	
	/**
	 * ПРЕНАСОЧВАНЕ КЪМ ВПИСВАНЕ
	 * 
	 */
	public String actionGoToVpisv(Integer idZ,  Integer vidDoc) {
		String outcome = "index.xhtml?faces-redirect=true";

		
		if (this.regOptions != null) {
				
//			Integer docPurpose = null; // предназначението на документа ми е необходимо за да преценя на къде да пренасочвам странницата			
//			List<RegisterOptionsDocsIn> lstDocsIn = this.regOptions.getDocsIn(); 			
//			for (RegisterOptionsDocsIn item : lstDocsIn) {
//				if ((item.getVidDoc()).equals(vidDoc)) {
//					docPurpose = item.getPurpose();
//					break;
//				}
//			}	
			
			//  ако заявлението е за промяна и за НОВО вписване..
//			if ( (docPurpose.equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_PROMIANA) | (docPurpose.equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_ZALICHAVANE)) ) ) {
//				
//			} else {
				
			   boolean isObjType = false;
			   
				// Лице - дейности фуражи
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_FURAJI)) {
				
					isObjType = true;
						outcome = "regTargovtsiFuraj.xhtml?faces-redirect=true&m=1&idZ=" + idZ;
					
				}
  
				// Лице - дейности ЗЖ
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_ZJ)) {
					isObjType = true;
						outcome = "regLicaDeinZJ.xhtml?faces-redirect=true&m=1&idZ=" + idZ;
					
				}
				
				//Лице - дейност ВЛП
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_LICE_VLP)) {
					isObjType = true;
						outcome = "regVLPEdit.xhtml?faces-redirect=true&m=1&idZ=" + idZ;
					
				}
				
				//рег. на МПС - животни
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS_ZJ)) {
					isObjType = true;
						outcome = "regMpsZJ.xhtml?faces-redirect=true&m=1&idZ=" + idZ;
					
				}
				
				//рег. на МПС - фуражи
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_REG_MPS)) {
					isObjType = true;
						outcome = "regMpsFuraj.xhtml?faces-redirect=true&m=1&idZ=" + idZ;
					
				}
				
				//рег. на ОЕЗ
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_OEZ)) {
					isObjType = true;
						outcome = "regOEZ.xhtml?faces-redirect=true&m=1&idZ=" + idZ;
					
				}
				//рег. на Заявление за търговия с ВЛП
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLP)) {
					isObjType = true;
						outcome = "regObektVLPEdit.xhtml?faces-redirect=true&m=1&idZ=" + idZ;
					
				}
				//рег. на ВЛЗ
				if (objType.equals(BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_LICENZ_VLZ)) {
					isObjType = true;
						outcome = "regVLZ.xhtml?faces-redirect=true&m=1&idZ=" + idZ;
					
				}
				
				if (!isObjType)   {
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "От настройките за  вида заявление е определен тип на обект ( " + this.objType.toString() + " ), за който не е открита и не може да  се  зареди правилната страница! ");
					return null;
				}
				
	//		}
		
		} else {
			
			if ( vidDoc.equals(BabhConstants.CODE_ZNACHENIE_ZAIAV_ODOB_OPERATORI_18)) {
				outcome = "regTargovtsiFuraj.xhtml?faces-redirect=true&m=1&idZ=" + idZ;				
			} else {
				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN, "Няма открити настройки за  вида заявление  и не може да се зареди правилната страница! ");
				return null;
			}
		}
         
		clearDanniNewDoc();
		return outcome;
	}
	
	/**
	 * ЗАРЕЖДА НАСТРОЙКИТЕ НА РЕГИСТЪРА СПОРЕД ВИДА НА ДОКУМЕНТА.. ОТ НЕГО ИЗВЛИЧАМЕ
	 * ТИПА НА ОБЕКТА ЗА ЛИЦЕНЗИРАНЕ И СЕ ОПРЕДЕЛЯ КЪМ КОЯ СТРАННИЦА ДА  СЕ НАСОЧВА
	 */
	private Integer loadRegOptionsType(Integer vidDoc) {
		   Integer obType = null;    // тип на обекта от настройката

			Integer registerId = sd.getRegisterByVidDoc().get(vidDoc);
			this.regOptions = sd.getRoptions().get(registerId); // всички настройки за регистъра
			if (this.regOptions != null) {
				obType = this.regOptions.getObjectType();
			}
	
		return  obType;
	}

	public Date getDecodeDate() {
		return decodeDate;
	}


	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate;
	}


	public Doc getDoc() {
		return doc;
	}


	public void setDoc(Doc doc) {
		this.doc = doc;
	}


	public Integer getRegistrId() {
		return registrId;
	}


	public void setRegistrId(Integer registrId) {
		this.registrId = registrId;
	}


	public Integer getVidZaiavl() {
		return vidZaiavl;
	}


	public void setVidZaiavl(Integer vidZaiavl) {
		this.vidZaiavl = vidZaiavl;
	}


	public String getRnDoc() {
		return rnDoc;
	}


	public void setRnDoc(String rnDoc) {
		this.rnDoc = rnDoc;
	}


	public Date getDateDoc() {
		return dateDoc;
	}


	public void setDateDoc(Date dateDoc) {
		this.dateDoc = dateDoc;
	}

	public boolean isVisGOTO() {
		return visGOTO;
	}

	public void setVisGOTO(boolean visGOTO) {
		this.visGOTO = visGOTO;
	}

	public Integer getCodeZaiavitel() {
		return codeZaiavitel;
	}

	public void setCodeZaiavitel(Integer codeZaiavitel) {
		this.codeZaiavitel = codeZaiavitel;
	}


	
}
