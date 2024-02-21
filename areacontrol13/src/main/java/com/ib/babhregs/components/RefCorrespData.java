package com.ib.babhregs.components;

import static com.ib.system.utils.SearchUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.xml.datatype.DatatypeConfigurationException;

import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.ReferentDAO;
import com.ib.babhregs.db.dto.Referent;
import com.ib.babhregs.db.dto.ReferentAddress;
import com.ib.babhregs.db.dto.ReferentDoc;
import com.ib.babhregs.system.BabhConstants;
import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.babhregs.utils.RegixUtils2;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.SysClassifAdapter;
import com.ib.system.db.JPA;
import com.ib.system.exceptions.BaseException;
import com.ib.system.exceptions.DbErrorException;
import com.ib.system.exceptions.InvalidParameterException;
import com.ib.system.exceptions.ObjectInUseException;
import com.ib.system.utils.SearchUtils;
import com.ib.system.utils.ValidationUtils;

/** */
@FacesComponent(value = "refCorrespData", createTag = true)
public class RefCorrespData extends UINamingContainer {
	
	private enum PropertyKeys {
		  REF, SHOWME, EKATTE, EKATTESPEC,  REFDOC, SEEPERSONALDATA, REFKORESP, ROWDOC, REF2TMP, VALIDIDENT, SHOWADDRESS1
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RefCorrespData.class);
	public static final String	UIBEANMESSAGES = "ui_beanMessages";
	public static final String	BEANMESSAGES = "beanMessages";
	public static final String  MSGPLSINS = "general.pleaseInsert";
	public static final String  ERRDATABASEMSG = "general.errDataBaseMsg";
	public static final String  SUCCESSAVEMSG = "general.succesSaveMsg";
	public static final String	OBJECTINUSE		 = "general.objectInUse";
	public static final String	LABELS = "labels";
	private static final String CODEREF = "codeRef";
	private static final String MSGVALIDEIK = "refCorr.msgValidEik";
	private static final String MSGVALIDEGN = "refCorr.msgValidEgn"; 
	private static final String MSGVALIDLNCH = "refCorr.msgValidLnch"; 
	//private static final String REFCORRESPMSG1 = "docu.refCorrespMsg1";
	private static final String SUCCESSDELETEMSG = "general.successDeleteMsg";

	private String errMsg = null;
	private SystemData	systemData	= null;
	private UserData	userData	= null;
	private Date			dateClassif	= null;
	//private boolean loadedFromRegix=false; // за сега не се използва
	private TimeZone timeZone = TimeZone.getDefault();
//	private IndexUIbean 	indexUIbean = null;
//	private String 			modalMsg = "";
	
	private Referent tmpRef;

	private int countryBG; // ще се инициализира в getter-а през системна настройка: delo.countryBG
	
	private Integer vidDoc=null;
//	private boolean validIdent;
	/// За сега не са включени: 
	//  1. Районите в адреса
	//  2. повече от един адрес
	//  3. имената на латиница
	//  4. гражданството
	//  5. личен номер в ЕС - за физ. лица	
	//  6. данъчен номер - за юрид. лица
	
	//  7. Специфичните настройки - сеос,guid и т.н. !!!
	//  8. списък с документи
	 
	



	/**
	 * Данни на лице - актуализация и разглеждане
	 * @return
	 * @throws DbErrorException
	 */
	public void initRefCorresp() {		
	
		try {
			setRefDoc(new ReferentDoc());
			setRowDoc(-1);
			//boolean modal = (Boolean) getAttributes().get("modal"); // обработката е в модален диалог (true) или не (false)
			if ((Integer) getAttributes().get("vidDoc")  !=  null) {
				this.setVidDoc((Integer) getAttributes().get("vidDoc"));
			}
			Integer idR = (Integer) getAttributes().get(CODEREF); 
			if( (Referent) getAttributes().get("refObj") != null ) {
				tmpRef=(Referent) getAttributes().get("refObj");
				boolean newAddr = true;
				boolean newAddrK = true;
				if (tmpRef == null) {
					tmpRef = new Referent();
				} else {
					newAddr = tmpRef.getAddress() == null || (tmpRef.getAddress().getId()==null && tmpRef.getAddress().getAddrCountry() == null);
					newAddrK =  tmpRef.getAddressKoresp() == null || (tmpRef.getAddressKoresp().getId()==null && tmpRef.getAddressKoresp().getAddrCountry() == null) ;
				}
				
				defCountryBG(newAddr, newAddrK);
				
				defTypeRef();
				setRef(tmpRef);
			}else {
				if(idR != null) {
					loadRefCorr(idR, null, null, null, null, false);
				}  else { // ново лице					
					
					
					String srchTxt = (String) getAttributes().get("searchTxt"); 
					clearRefCorresp(srchTxt, null);
					

					ValueExpression expr2 = getValueExpression("searchTxt"); //зачиствам текста - искам да се изпозлва само при първото отваряне - това има смсиъл само, ако се изивква като модален.....
					ELContext ctx2 = getFacesContext().getELContext();
					if (expr2 != null) {
						expr2.setValue(ctx2, null);
					}
					
					defCountryBG(true, true);
					
					setRef(tmpRef);
									
				}
				
			}
		} catch (NumberFormatException e) {
			LOGGER.error("Грешка при сетване на държава! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,"Грешка при обработка на данни! ", e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Грешка!", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,"Грешка! ", e.getMessage());
		}
				
		setSeePersonalData(true);//TODO това нужно ли е?
		if (getRef()!=null) {
			if (!isSeePersonalData() && getRef().getRefType()!=null && getRef().getRefType().equals(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL)) {
				getAttributes().put("readonly", true);
			}	
			setShowMe(true);
			setErrMsg(null);
		}else {
			setShowMe(false);
		}
		
		LOGGER.debug("initRefCorresp");
	}

	
	private void defCountryBG(boolean newAddr, boolean newAddrK) {
		if(((Boolean)getAttributes().get("showPostAdress") || (Boolean)getAttributes().get("btnPostAdress"))
				&& newAddr) {
			if (tmpRef.getAddress()==null || tmpRef.getRefName()==null) {
				tmpRef.setAddress(new ReferentAddress());
				tmpRef.getAddress().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN);
				tmpRef.getAddress().setAddrCountry(getCountryBG());	
			}			
		}
		if((Boolean)getAttributes().get("showKorespAdress") && newAddrK) {
			if (tmpRef.getAddressKoresp()==null || tmpRef.getRefName()==null) {
				tmpRef.setAddressKoresp(new ReferentAddress());
				tmpRef.getAddressKoresp().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
				tmpRef.getAddressKoresp().setAddrCountry(getCountryBG());
			}
		}
	}
	
	
	/**
	 * да зареди тип на лицето по подразбиране
	 */
	private void defTypeRef() {
		if (tmpRef != null && tmpRef.getRefType() == null) {
			Integer refType = (Integer) getAttributes().get("refType");
			if (refType != null) {
				tmpRef.setRefType(refType);
			}else if (vidDoc != null) {
				tmpRef.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);		
				if (!getSystemData().isElementVisible(vidDoc, BabhConstants.EKRAN_ATTR_UL) && getSystemData().isElementVisible(vidDoc, BabhConstants.EKRAN_ATTR_FL)) {
					tmpRef.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL);
				} 
			} else {
				tmpRef.setRefType(BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL);
			} 
		}
	}
	
	
	
	/**
	 *  зарежда данни за корепондент по зададени критерии
	 */
	private boolean loadRefCorr(Integer idR, String eik, String egn, String lnc, String fzlLnEs , boolean regix) {
		boolean bb = true; // true - ako е заредено от базата
		Integer tmpRefType = (getRef() == null) ? null: getRef().getRefType(); 
	    boolean identNom =  SearchUtils.isEmpty(fzlLnEs);  
		try {
			setErrMsg(null);
			if(idR != null) {
				// подадено е ид на лице
				JPA.getUtil().runWithClose(() -> tmpRef = new ReferentDAO(getUserData()).findByCodeRef(idR));
				if (tmpRef == null) {
					setErrMsg("Грешка при зареждане на данни за лице!");
					bb = false;
				}
			} else if (!(SearchUtils.isEmpty(eik) && SearchUtils.isEmpty(egn) && SearchUtils.isEmpty(lnc) && identNom)){	
				if(identNom) {
					// 1. търси лице по ЕГН/ЛНЧ/ЕИК в базата 
					JPA.getUtil().runWithClose(() -> tmpRef = new ReferentDAO(getUserData()).findByIdent(eik, egn, lnc, getRef().getRefType()));
				} else {
					// търси по идентификационен номер
					JPA.getUtil().runWithClose(() -> tmpRef = new ReferentDAO(getUserData()).findByIdentNomNfl(fzlLnEs));
				}
				if(tmpRef != null && !tmpRef.getId().equals(getRef().getId())) {
					// 	намерено лице в базата 
				    String str1 = (SearchUtils.isEmpty(eik) ? " ЕГН/ЛНЧ" : " ЕИК" );
					setErrMsg(IndexUIbean.getMessageResourceString(BEANMESSAGES, "refCorr.loadByEikEgn", str1));//Намерен е кореспондент с посочения {0}. 
				} else if(tmpRef == null ) {
					tmpRef = getRef(); // КОРЕКЦИЯ на данни за лице!!!
					// За лице, записано в базата, Не се позволява да се ввъеждат идентификаторите.
					// позволено е за ново лице или за лице от базата, което няма идентификатор
					if(regix){
						// 2. да се опита да зареди през regix
						// Само по ЕГН или ЕИК. За ЛНЧ нямаме услуга!!!
						String identifikator = null;
						if(!SearchUtils.isEmpty(eik)) {
							identifikator = eik;
						} else if(!SearchUtils.isEmpty(egn)) {
							identifikator = egn;
						}
						loadFromRegix(identifikator);  // връща true, ако е променено
						bb = false;
					} else if(lnc != null){
						tmpRef.setFzlLnc(lnc);
						tmpRef.setFzlEgn(null);
					}
				}	
			}			
			if (tmpRef == null) {
				tmpRef = new Referent(); // за всеки случай, ако по някава причина е останало null, а го е имало преди...
				tmpRef.setRefType(tmpRefType);
			}
			if(bb) {
				// ако  не е заредено от Regix - да сетне по подразбирне държавата
				boolean newAddr = true;
				boolean newAddrK = true;
				if (tmpRef == null) {
					tmpRef = new Referent();
				} else {
					newAddr = tmpRef.getAddress() == null || (tmpRef.getAddress().getId()==null && tmpRef.getAddress().getAddrCountry() == null);
					newAddrK =  tmpRef.getAddressKoresp() == null || (tmpRef.getAddressKoresp().getId()==null && tmpRef.getAddressKoresp().getAddrCountry() == null) ;
				}
				defCountryBG(newAddr, newAddrK);
			}
			
			setRef(tmpRef);
			
			tmpRef = null;
			
			LOGGER.debug("load initRefCorresp");
		} catch (BaseException e) {
			LOGGER.error("Грешка при зареждане на данни за лице! ", e);
		}
		return bb;
	}
   


	/**
	 * Търсене в regix
	 * САМО по ЕГН и ЕИК. 
	 * За ЛНЧ - нямаме услуга!
	 * @param identifikator
	 */
	public boolean  loadFromRegix(String identifikator) {
		boolean bb = false;
		try {
			
			//REGIX опит за зареждане на данни. Валидацията за ЕГН/ЛНЧ/ЕИК - вече трябва да е минала
			if(Objects.equals(getRef().getRefType(),BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL )&&
				Objects.equals(getSystemData().getSettingsValue("regix.GRAO.PersonDataSearch"), "1")) {
				// постоянен адрес за ФЛ
				boolean regixPermAddress =  false;
				if((Boolean) getAttributes().get("regixPermAddress") != null) {
					regixPermAddress = (Boolean) getAttributes().get("regixPermAddress");
				}
				boolean loadPermanentAddress = false;			
				if(regixPermAddress && Objects.equals(getSystemData().getSettingsValue("regix.GRAO.PermanentAddressSearch"), "1")) {
					loadPermanentAddress = true;
				}
				// настоящ адрес за ФЛ
				boolean regixCurrentAddress = false;
				if((Boolean) getAttributes().get("regixCurrentAddress") != null) {
					regixCurrentAddress = (Boolean) getAttributes().get("regixCurrentAddress");
				}
				boolean loadCurrentAddress  = false;
				if(regixCurrentAddress && Objects.equals(getSystemData().getSettingsValue("regix.GRAO.TemporaryAddressSearch"), "1")) {
					loadCurrentAddress = true;
				}
//				bb = RegixUtils.loadFizLiceByEgn(tmpRef, identifikator, true, loadPermanentAddress, loadCurrentAddress, systemData);
				bb = RegixUtils2.loadFizLiceByEgn(tmpRef, identifikator, true, loadPermanentAddress, loadCurrentAddress, systemData);
				if(loadPermanentAddress) {
					// Постоянния адрес да стане и адрес за кореспонденция!
					tmpRef.setAddressKoresp(tmpRef.getAddress());
				}
				
				tmpRef.setFzlLnc(null);
			
			} else if(Objects.equals(getRef().getRefType(),BabhConstants.CODE_ZNACHENIE_REF_TYPE_NFL )&&
					  Objects.equals(getSystemData().getSettingsValue("regix.AVTR.GetActualState"), "1")){
//				bb = RegixUtils.loadUridLiceByEik(tmpRef, identifikator, true, true, true, systemData);
				bb = RegixUtils2.loadUridLiceByEik(tmpRef, identifikator, true, true, true, systemData);
		
			}

		} catch (DbErrorException e) {
			LOGGER.error("Грешка при работа с базата данни! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,"Грешка при работа с базата данни! ", e.getMessage());
//		} catch (RegixClientException e) {
//			LOGGER.error("Грешка при извличане на данни от Regix! ", e);
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,"Проблем при извличане на данни от Regix!", e.getMessage());
		} catch (DatatypeConfigurationException e) {
			LOGGER.error("Грешка при извличане на данни от Regix! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,"Проблем при извличане на данни от Regix!", e.getMessage());
		} catch (bg.egov.regix.RegixClientException e) {
			LOGGER.error("Грешка при извличане на данни от Regix! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_WARN,"Проблем при извличане на данни от Regix!", e.getMessage());
        }
        return bb;
	}
	
	
	
	
	
	
	
	
	
   /**
    * Зачиства данните на лице - бутон "нов"
    * 
    */
   public void clearRefCorresp(String srchTxt, Integer typeRef) {
	   
	   	setRef(new Referent());
	    tmpRef = new Referent();
	    if(typeRef != null) {
	    	tmpRef.setRefType(typeRef); // юридическо лице
	    }else {
	    	defTypeRef();
	    }
		tmpRef.setDateOt(getDateClassif());
		defCountryBG(true, true);
		tmpRef.setRefName(srchTxt);	
		tmpRef.setRefGrj(getCountryBG());
		setRef(tmpRef);
   }
   
   /**
    * Зачиства данните на лице - бутон "Изчисти" - бутона до панела "Данни за лицето:
    * ако е подаден параметър на компонентата #{cc.attrs.showBtnClear}
    * ако комп. се изпозлва без да се записват данните при затварянето и 
    * Да запази  типа на лицето! 
    */
   public void clearRefCorresp2(Integer typeRef) {
	    setRef(new Referent());
	    tmpRef = new Referent();
	    tmpRef.setRefType(typeRef); // запомня типа на кореспондента
		tmpRef.setDateOt(getDateClassif());
		tmpRef.setAddress(new ReferentAddress());
		tmpRef.getAddress().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN);
		tmpRef.setAddressKoresp(new ReferentAddress());
		tmpRef.getAddressKoresp().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
		setRef(tmpRef);
		tmpRef.getAddress().setAddrCountry(getCountryBG());
		tmpRef.getAddressKoresp().setAddrCountry(getCountryBG());
		tmpRef.setRefGrj(getCountryBG());
   }
	/**
    * смяна на лице - физическо/юридическо  
    */
   public void actionChTypRef() { 
	   // това дали изобщо е нужно?
	   // целта е при смяна с радиобутоните да се запомни временно първия ЗАПИСАН референт и ако се върнат обратно например от физ. към юрид. лице - да се зареди той
	   if(getRef2Tmp() == null) {
		  setRef2Tmp(getRef());
		  clearRefCorresp(null, null);
	   } else {
		  setRef(getRef2Tmp());
		  setRef2Tmp(null);
	   }
	   
	   // извиква remoteCommnad - ако има такава....
		String remoteCommnad = (String) getAttributes().get("onChTypRef");
		if (remoteCommnad != null && !"".equals(remoteCommnad)) {
			PrimeFaces.current().executeScript(remoteCommnad);
		}
   }
     
   /**
    * При смяна на държава - да се нулира полето за ЕКАТЕ
    */
   public void  actionChangeCountry() {
	   if(getRef().getAddress() == null) { // не би трябвало да се случва....
		   getRef().setAddress(new ReferentAddress());
		   getRef().getAddress().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_POSTOQNEN);
	   }
	   getRef().getAddress().setPostBox(null);
	   getRef().getAddress().setPostCode(null);
	   getRef().getAddress().setEkatte(null);
   }
   
   /**
    * При смяна на държава - да се нулира полето за ЕКАТЕ
    */
   public void  actionChangeCountryKoresp() {
	   if (getRef().getAddressKoresp() == null) {
		   getRef().setAddressKoresp(new ReferentAddress());
		   getRef().getAddressKoresp().setAddrType(BabhConstants.CODE_ZNACHENIE_ADDR_TYPE_CORRESP);
	   }
	   getRef().getAddressKoresp().setPostBox(null);
	   getRef().getAddressKoresp().setPostCode(null);
	   getRef().getAddressKoresp().setEkatte(null);
   }
   
   
   /**
    * зарежда лице по зададен еик
    */
	 public void actionLoadByEIK() { 
		//системна настройка - Допуска се дублиране на ЕИК при въвеждане на нефизическо лице  (1- да / 0 - не); по подразбиране - не
//		try {
			setValidIdent(true);
			//String	setting = getSystemData().getSettingsValue("delo.allowEikDuplicate"); 	
			//if (setting == null || !Objects.equals(Integer.valueOf(setting), SysConstants.CODE_ZNACHENIE_DA)) {
			if (!SearchUtils.isEmpty(getRef().getNflEik()) && !ValidationUtils.isValidBULSTAT(getRef().getNflEik())) {

				JSFUtils.addMessage(this.getClientId(FacesContext.getCurrentInstance()) + ":eik",
						FacesMessage.SEVERITY_ERROR, IndexUIbean.getMessageResourceString(BEANMESSAGES, MSGVALIDEIK));
				
				setErrMsg(IndexUIbean.getMessageResourceString(BEANMESSAGES, MSGVALIDEIK)); 
				setValidIdent(false);
			} else {

				loadRefCorr(null, getRef().getNflEik(), null, null, null, true);
			}
			//}
		
//		} catch (DbErrorException e) {
//			LOGGER.error(e.getMessage(), e);
//			JSFUtils.addErrorMessage(e.getMessage(), e);
//		}  

		LOGGER.debug("actionLoadByEIK");
	 }
	 
	 /**
	  * зарежада юридическо лице по идентификационен номер
	  */
	 public void actionLoadByIdentNomNfl() { 
		
		setValidIdent(true);
		loadRefCorr(null, null, null, null,getRef().getFzlLnEs(), false);

		LOGGER.debug("actionLoadByIdentNomNfl");
	 }
	
	 
	 /**
    * зарежда лице по зададен егн
	 * @throws DbErrorException 
	 * @throws InvalidParameterException 
    */
	 public void actionLoadByEGN() throws InvalidParameterException, DbErrorException {
		// Винаги да се прави проверка за дублирано ЕГН, ако се въвежда физическо лице
		 setValidIdent(true);
		 if (!SearchUtils.isEmpty(getRef().getFzlEgn()) && !ValidationUtils.isValidEGN(getRef().getFzlEgn())) {
			
			JSFUtils.addMessage(this.getClientId(FacesContext.getCurrentInstance()) + ":egn",
					FacesMessage.SEVERITY_ERROR, IndexUIbean.getMessageResourceString(BEANMESSAGES, MSGVALIDEGN));
			
			setErrMsg(IndexUIbean.getMessageResourceString(BEANMESSAGES, MSGVALIDEGN));
			setValidIdent(false);
		} else {
			loadRefCorr(null, null, getRef().getFzlEgn(), null, null, true);
		}
		getRef().setFzlLnc(null);
		LOGGER.debug("actionLoadByEGN");
	 }
   
	/**
	 * зарежда лице по зададен ЛНЧ
	 * @throws DbErrorException 
	 * @throws InvalidParameterException 
	 */
	public void actionLoadByLNCH() throws InvalidParameterException, DbErrorException {
		// Винаги да се прави проверка за дублирано ЕГН, ако се въвежда физическо лице
		setValidIdent(true);
		if (!SearchUtils.isEmpty(getRef().getFzlLnc()) && !ValidationUtils.isValidLNCH(getRef().getFzlLnc())) {

			JSFUtils.addMessage(this.getClientId(FacesContext.getCurrentInstance()) + ":lnch",
					FacesMessage.SEVERITY_ERROR, IndexUIbean.getMessageResourceString(BEANMESSAGES, MSGVALIDLNCH));

			setErrMsg(IndexUIbean.getMessageResourceString(BEANMESSAGES, MSGVALIDLNCH));
			 setValidIdent(false);
		} else {
			loadRefCorr(null, null, null, getRef().getFzlLnc(), null, false);
		}
		getRef().setFzlEgn(null);
		LOGGER.debug("actionLoadByLNCH");
	}
	
	public void actionCheckEmail()  {
			
		if (!isEmpty(getRef().getContactEmail()) && !ValidationUtils.isEmailValid(getRef().getContactEmail())) {
			JSFUtils.addMessage(this.getClientId(FacesContext.getCurrentInstance()) + ":contactEmail", FacesMessage.SEVERITY_ERROR, IndexUIbean.getMessageResourceString(BEANMESSAGES, "general.mail"));

			setErrMsg(IndexUIbean.getMessageResourceString(BEANMESSAGES, "general.mail"));
		}
		
	}

   /** 
    * Запис на лице 
    */
   public void actionSave(){ 
	    errMsg = " Моля, въведете задължителната информация!";
	    try { 
			if(checkData()) {
				errMsg = null;
				
					LOGGER.debug("actionSave>>>> ");
					getRef().setLevelNumber(null); // за да може процеса на регикс после да мине през този
					if ((Boolean) getAttributes().get("saveInComponent")) {
						JPA.getUtil().runInTransaction(() -> this.tmpRef = new ReferentDAO(getUserData()).save(getRef()));
						getSystemData().mergeReferentsClassif(tmpRef, false );
					}
					 	
									
				   if( tmpRef != null && tmpRef.getCode() != null) {
					   //връща id на избрания лице
					    ValueExpression expr2 = getValueExpression(CODEREF);
						ELContext ctx2 = getFacesContext().getELContext();
						if (expr2 != null) {
							expr2.setValue(ctx2, tmpRef.getCode());
						}
						
						ValueExpression expr3 = getValueExpression("refObj");
						ELContext ctx3 = getFacesContext().getELContext();
						if (expr3 != null) {
							expr3.setValue(ctx3, tmpRef);
						}
						
				   }
				   
				   
				    // извиква remoteCommnad - ако има такава....
					String remoteCommnad = (String) getAttributes().get("onComplete");
					if (remoteCommnad != null && !"".equals(remoteCommnad)) {
						PrimeFaces.current().executeScript(remoteCommnad);
					}
					
	//				if(tmpRef != null && tmpRef.getAddress() == null) {
	//					tmpRef.setAddress(new ReferentAddress());
	//				}
	//				setRef(tmpRef); // излишно е, ако веднага при запис се затваря модалния, иначе не е....
					
					JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO, IndexUIbean.getMessageResourceString(UIBEANMESSAGES, SUCCESSAVEMSG) );
				
			} 
	    } catch (BaseException e) {			
			LOGGER.error("Грешка при запис на лице ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,IndexUIbean.getMessageResourceString(UIBEANMESSAGES, ERRDATABASEMSG));
		}
   }
   /** 
    * Връша целия обект референт без да записва
    */
   public void actionReturn(){ 
	   errMsg = " Моля, въведете задължителната информация!";
	   try { 
		   if(checkData()) {
			   errMsg = null;
			   
			   LOGGER.debug("actionSave>>>> ");
			   getRef().setLevelNumber(null); // за да може процеса на регикс после да мине през този
			   
			   if( tmpRef != null) {
				   //връща id на избрания лице
				   ValueExpression expr2 = getValueExpression(CODEREF);
				   ELContext ctx2 = getFacesContext().getELContext();
				   if (expr2 != null) {
					   expr2.setValue(ctx2, tmpRef.getCode());
				   }
				   
				   ValueExpression expr3 = getValueExpression("refObj");
				   ELContext ctx3 = getFacesContext().getELContext();
				   if (expr3 != null) {
					   expr3.setValue(ctx3, tmpRef);
				   }
				   
			   }
			   
			   // извиква remoteCommnad - ако има такава....
			   String remoteCommnad = (String) getAttributes().get("onComplete");
			   if (remoteCommnad != null && !"".equals(remoteCommnad)) {
				   PrimeFaces.current().executeScript(remoteCommnad);
			   }
			  			   
		   } 
	   } catch (BaseException e) {			
		   LOGGER.error("Грешка при запис на лице ", e);
		   JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,IndexUIbean.getMessageResourceString(UIBEANMESSAGES, ERRDATABASEMSG));
	   }
   }
   
   
 

   /**
    * Проверка за валидни данни
    * @return 
 * @throws DbErrorException 
 * @throws InvalidParameterException 
    */
	public boolean checkData() throws InvalidParameterException, DbErrorException {
		boolean flagSave = true;	
		FacesContext context = FacesContext.getCurrentInstance();
	    String clientId = null;	  
	    tmpRef = getRef();
	    
	    
	    if (context != null && tmpRef != null ) {
		   clientId =  this.getClientId(context);		
		   
		   String tmp = tmpRef.getNflEik() == null ? null : tmpRef.getNflEik().trim();  
		   tmpRef.setNflEik(tmp);
		   tmp = tmpRef.getFzlEgn() == null ? null : tmpRef.getFzlEgn().trim();  
		   tmpRef.setFzlEgn(tmp);
		   if(SearchUtils.isEmpty(tmpRef.getRefName())) {
				JSFUtils.addMessage(clientId+":nameCorr",FacesMessage.SEVERITY_ERROR,
						IndexUIbean.getMessageResourceString(BEANMESSAGES,"general.msgRefCorrName"));
				flagSave = false;	
		   }
	        
		  
		   if (tmpRef.getRefType()==BabhConstants.CODE_ZNACHENIE_REF_TYPE_FZL) {
				
				if (!SearchUtils.isEmpty(tmpRef.getFzlEgn()) && 
					!ValidationUtils.isValidEGN(tmpRef.getFzlEgn())) {			   
				   JSFUtils.addMessage(clientId+":egn",FacesMessage.SEVERITY_ERROR, IndexUIbean.getMessageResourceString(BEANMESSAGES, MSGVALIDEGN));
				   flagSave = false;
				}
				if (!SearchUtils.isEmpty(tmpRef.getFzlLnc()) &&
					!ValidationUtils.isValidLNCH(tmpRef.getFzlLnc())) {
					   
					JSFUtils.addMessage(clientId+":lnch",FacesMessage.SEVERITY_ERROR,IndexUIbean.getMessageResourceString(BEANMESSAGES, MSGVALIDLNCH));
					flagSave = false;
				}
 
				
//				// Това дали е нужно?
//				if (!isEmpty(tmpRef.getRefLatin())) {
//					if (!ValidationUtils.isLat(tmpRef.getRefLatin())) {
//						   JSFUtils.addMessage(clientId+":nameLatinCorr",FacesMessage.SEVERITY_ERROR, "Невалидни имена на латиница!");
//						   flagSave = false;
//					}
//				}
			}else {
				
				
				//urid lice
 
				if (!SearchUtils.isEmpty(tmpRef.getNflEik()) &&
					!ValidationUtils.isValidBULSTAT(tmpRef.getNflEik())) {
				   JSFUtils.addMessage(clientId+":eik",FacesMessage.SEVERITY_ERROR,IndexUIbean.getMessageResourceString(BEANMESSAGES, MSGVALIDEIK));
				   flagSave = false;
				}
 
				
//				if (!isEmpty(tmpRef.getRefLatin())) {
//					if (!ValidationUtils.isLat(tmpRef.getRefLatin())) {
//						   JSFUtils.addMessage(clientId+":nameLatinCorr",FacesMessage.SEVERITY_ERROR, "Невалидно Наименование на латиница!");
//						   flagSave = false;
//					}
//				}
 
			}
		    

//		   if ((Boolean)getAttributes().get("showPostAdress") &&
//			   tmpRef.getAddress() != null && tmpRef.getAddress().getAddrCountry() != null &&  
//			   tmpRef.getAddress().getAddrCountry() == getCountryBG() &&
//			   tmpRef.getAddress().getEkatte() == null ) {
//			   JSFUtils.addMessage(clientId+":mestoC:аutoCompl",FacesMessage.SEVERITY_ERROR, "Въведете населено място!");
//			   flagSave = false;
//		   }
		   // Коментирам проверката за сега
//		   if ((Boolean)getAttributes().get("showKorespAdress") && 
//			   tmpRef.getAddressKoresp() != null && tmpRef.getAddressKoresp().getAddrCountry() != null &&  
//			   tmpRef.getAddressKoresp().getAddrCountry() == getCountryBG() &&
//			   tmpRef.getAddressKoresp().getEkatte() == null ) {
//			   JSFUtils.addMessage(clientId+":mestoC2:аutoCompl",FacesMessage.SEVERITY_ERROR, "Въведете населено място!");
//			   flagSave = false;
//		   }
		   if(!SearchUtils.isEmpty(tmpRef.getContactEmail()) &&
		      !ValidationUtils.isEmailValid(tmpRef.getContactEmail()) ) {
			   JSFUtils.addMessage(clientId+":contactEmail",FacesMessage.SEVERITY_ERROR,IndexUIbean.getMessageResourceString(UIBEANMESSAGES, "general.validE-mail"));
			   flagSave = false;
		   }
 
	     
	    } else {
		   flagSave = false;
	    }		
		return flagSave;
	}

   
   /** 
    * Изтриване на лице 
    */
   public void actionDelete(){ 
		try {
			LOGGER.debug("actionDelete>>>> ");
						
			JPA.getUtil().runInTransaction(() -> new ReferentDAO(getUserData()).delete(getRef()));
		   
			getSystemData().mergeReferentsClassif(getRef(), true );	
			
		
			ValueExpression expr2 = getValueExpression(CODEREF);
			ELContext ctx2 = getFacesContext().getELContext();
			if (expr2 != null) {
				expr2.setValue(ctx2, null);
			}	
			
		    // извиква remoteCommnad - ако има такава....
			String remoteCommnad = (String) getAttributes().get("onComplete");
			if (remoteCommnad != null && !"".equals(remoteCommnad)) {
				PrimeFaces.current().executeScript(remoteCommnad);
			}
						
		
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,  IndexUIbean.getMessageResourceString(UIBEANMESSAGES, SUCCESSDELETEMSG) );
		} catch (ObjectInUseException e) {			
			LOGGER.error("Грешка при изтриване на лице! ObjectInUseException = {}", e.getMessage()); 
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
		}catch (BaseException e) {			
			LOGGER.error("Грешка при изтриване на лице ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR,IndexUIbean.getMessageResourceString(UIBEANMESSAGES, ERRDATABASEMSG));
		}
		
   }
 
   /** 
    * коригиране данни на лице - изп. се само, ако е в модален прозорец
    * изивква се при затваряне на модалния прозореца (onhide) 
    * 
    */
   public void actionHideModal() {		
	   // за сега няма да се ползва
	   setRef(null);
	   setShowMe(false);
	   LOGGER.debug("actionHideModal>>>> ");
	}
   
   public void actionLoadDocsList() {
	   
//	 	 TODO
   }
   
   public void actionEditDoc(Integer row) {
	   setRowDoc(row);
	   
//	   ArrayList<ReferentDoc> tmp=getDocsList();
	   setRefDoc(getRef().getReferentDocs().get(row));
   }
 
   public void actionAddDoc() {
	   if (validateDoc()) {
		   ArrayList<ReferentDoc> tmp=(ArrayList<ReferentDoc>) getRef().getReferentDocs();
		   if (tmp==null) {
			tmp=new ArrayList<ReferentDoc>();
		   }
		   if (getRowDoc()==-1) {
			   tmp.add(getRefDoc());
		   }else {
			   tmp.set(getRowDoc(), getRefDoc());
		   }
		   getRef().setReferentDocs(tmp);
		   setRowDoc(-1);
//		   setDocsList(tmp);
		   setRefDoc(new ReferentDoc());
	   }
	   
   }
   
	public boolean validateDoc() {
		FacesContext context = FacesContext.getCurrentInstance();
		String clientId = null;
		boolean save = true;

		if (context != null) {
			clientId = this.getClientId(context);

			if (getRefDoc().getVidDoc() == null) {
				save = false;
				JSFUtils.addMessage(clientId + ":vidDocInput", FacesMessage.SEVERITY_ERROR, "Моля, въведете вид на документа!");
			}
			if (getRefDoc().getNomDoc() == null || getRefDoc().getNomDoc().isEmpty()) {
				save = false;
				JSFUtils.addMessage(clientId + ":nomDiplomDocInput", FacesMessage.SEVERITY_ERROR, "Моля, въведете номер на документа!");
			}
		}

		return save;
	}
		    

    
    /**
     * Метод за изтриване на документи, свързани с лице при заличаване на лице
     */
    public void actionDeleteDocs() {
    	//TODO
//    	try {
//    	
//			for (Object[] doc : getDocSelectedAllM()) {
//				
//				Integer docId = SearchUtils.asInteger(doc[0]);
//
//				JPA.getUtil().runInTransaction(() -> {					
//
//					new DocDAO(getUserData()).deleteById(docId);
//					
//					FilesDAO filesDao = new FilesDAO(getUserData());		
//					List<Files> filesList = filesDao.selectByFileObject(docId, BabhConstants.CODE_ZNACHENIE_JOURNAL_DOC); 
//					
//					if (filesList != null && !filesList.isEmpty()) {
//						for (Files f : filesList) {
//							filesDao.deleteFileObject(f);	
//						}
//					}					
//				});
//				
//				JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_INFO,  IndexUIbean.getMessageResourceString(UIBEANMESSAGES, SUCCESSDELETEMSG) );				
//			} 
//			
//			actionLoadDocsList();			
//    	
//    	} catch (ObjectInUseException e) {			
//			LOGGER.error("Грешка при изтриване на документа - обекта се използва!", e); 
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, IndexUIbean.getMessageResourceString(UIBEANMESSAGES, OBJECTINUSE), e.getMessage());
//		
//    	} catch (BaseException e) {			
//			LOGGER.error("Грешка при изтриване на документа - грешка при работа с базата данни!", e);			
//			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, IndexUIbean.getMessageResourceString(UIBEANMESSAGES, ERRDATABASEMSG), e.getMessage());
//		
//    	} finally {
//			PrimeFaces.current().executeScript("scrollToErrors()");
//		}
    	
    }
    
    

	/** @return */
	public boolean isShowMe() {
		return (Boolean) getStateHelper().eval(PropertyKeys.SHOWME, false);
	}
	
	/** @param showMe */
	public void setShowMe(boolean showMe) {
		getStateHelper().put(PropertyKeys.SHOWME, showMe);
	}
	
	/** @return */
	public boolean isSeePersonalData() {
		return (Boolean) getStateHelper().eval(PropertyKeys.SEEPERSONALDATA, false);
	}
	/** @param seePersonalData */
	public void setSeePersonalData(boolean seePersonalData) {
		getStateHelper().put(PropertyKeys.SEEPERSONALDATA, seePersonalData);
	}

	/** @return the dateClassif */
	private Date getDateClassif() {
		if (this.dateClassif == null) {
			this.dateClassif = (Date) getAttributes().get("dateClassif");
			if (this.dateClassif == null) {
				this.dateClassif = new Date();
			}
		}
		return this.dateClassif;
	}
	
	private SystemData getSystemData() {
		if (this.systemData == null) {
			this.systemData =  (SystemData) JSFUtils.getManagedBean("systemData");
		}
		return this.systemData;
	}

	/** @return the userData */
	private UserData getUserData() {
		if (this.userData == null) {
			this.userData = (UserData) JSFUtils.getManagedBean("userData");
		}
		return this.userData;
	}
	
	/** 
	 *   1 само области; 2 - само общини; 3 - само населени места; без специфики - всикчи
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public Map<Integer, Object> getSpecificsEKATTE() {
		Map<Integer, Object> eval = (Map<Integer, Object>) getStateHelper().eval(PropertyKeys.EKATTESPEC, null);
		return eval != null ? eval : Collections.singletonMap(SysClassifAdapter.EKATTE_INDEX_TIP, 3);
	}

	/** @return */
	public Integer getLang() {
		return getUserData().getCurrentLang();
	}
	
	/** @return */
	public Date getCurrentDate() {
		return getDateClassif();
	}

//	public String getModalMsg() {
//		return modalMsg;
//	}
//
//
//	public void setModalMsg(String modalMsg) {
//		this.modalMsg = modalMsg;
//	}

	public Referent getRef() {
		return (Referent) getStateHelper().eval(PropertyKeys.REF, null);
	}

	public void setRef(Referent ref) {
		getStateHelper().put(PropertyKeys.REF, ref);
	}
	
	public Referent getRefKoresp() {
		return (Referent) getStateHelper().eval(PropertyKeys.REFKORESP, null);
	}
	
	public void setRefKoresp(Referent refKoresp) {
		getStateHelper().put(PropertyKeys.REFKORESP, refKoresp);
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	

	public ReferentDoc getRefDoc() {
		return (ReferentDoc) getStateHelper().eval(PropertyKeys.REFDOC, null);
	}
	
	public void setRefDoc(ReferentDoc refDoc) {
		getStateHelper().put(PropertyKeys.REFDOC, refDoc);
	}
	
//	public ArrayList<ReferentDoc> getDocsList() {
//		return (ArrayList<ReferentDoc>) getStateHelper().eval(PropertyKeys.DOCSLIST, null);
//	}
//
//	public void setDocsList(ArrayList<ReferentDoc> docsList) {
//		getStateHelper().put(PropertyKeys.DOCSLIST, docsList);
//	}
 

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public int getCountryBG() {
		if (this.countryBG == 0) {
			try {
				this.countryBG = Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG"));
			} catch (Exception e) {
				LOGGER.error("Грешка при определяне на код на държава България от настройка: delo.countryBG", e);
			}
		}
		return this.countryBG;
	}

//	public boolean isLoadedFromRegix() {
//		return loadedFromRegix;
//	}
//
//	public void setLoadedFromRegix(boolean loadedFromRegix) {
//		this.loadedFromRegix = loadedFromRegix;
//	}

	public Integer getVidDoc() {
		return vidDoc;
	}

	public void setVidDoc(Integer vidDoc) {
		this.vidDoc = vidDoc;
	}

	public Integer getRowDoc() {
		return (Integer) getStateHelper().eval(PropertyKeys.ROWDOC, false);
	}

	public void setRowDoc(Integer rowDoc) {
		getStateHelper().put(PropertyKeys.ROWDOC, rowDoc);
	}
	

	public Referent getRef2Tmp() {
		return (Referent) getStateHelper().eval(PropertyKeys.REF2TMP, null);
	}

	public void setRef2Tmp(Referent ref2Tmp) {
		getStateHelper().put(PropertyKeys.REF2TMP, ref2Tmp);
	}
	
	
	public boolean isValidIdent() {
		return (boolean) getStateHelper().eval(PropertyKeys.VALIDIDENT, true);
	}


	public void setValidIdent(boolean validIdent) {
		getStateHelper().put(PropertyKeys.VALIDIDENT, validIdent);
	}

	public boolean isShowAddress1() {
		return (boolean) getStateHelper().eval(PropertyKeys.SHOWADDRESS1, (Boolean)getAttributes().get("showPostAdress"));
	}


	public void setShowAddress1(boolean showAddress1) {
		getStateHelper().put(PropertyKeys.SHOWADDRESS1, showAddress1);
	}
	 
}