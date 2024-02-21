package com.ib.babhregs.components;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.primefaces.PrimeFaces;
import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.search.ObektDeinostSearch;
import com.ib.babhregs.system.SystemData;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.SysConstants;
import com.ib.system.exceptions.DbErrorException;

/** */
@FacesComponent(value = "compObektDeinostSearch", createTag = true)
public class CompObektDeinostSearch extends UINamingContainer {
	
	private enum PropertyKeys {
		ODSEARCH, 
		IDOD, 
		NAIM, 
		SHOWME, 
		OBEKTDEINOSTLIST
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CompObektDeinostSearch.class);
	
	private Integer vid;

	private SystemData	systemData	= null;
	private Date		dateClassif	= null;

	private int countryBG; // ще се инициализира в getter-а през системна настройка: delo.countryBG	
	
	/**
	 * Разширеното търсене - инциализира компонентата   <f:event type="preRenderComponent" listener="#{cc.initOdS(true)}" />
	 */	
	public void initOdS(boolean bb) {

		@SuppressWarnings("unused")
		boolean modal = (Boolean) getAttributes().get("modal"); // обработката е в модален диалог (true) или не (false)
		this.vid = (Integer) getAttributes().get("vidOd"); 
		boolean registered = (Boolean) getAttributes().get("registered"); // дали да връща само регистрираните обекти

		ObektDeinostSearch tmpOdS = new ObektDeinostSearch();
		
		tmpOdS.setVid(this.vid);
		
		tmpOdS.setDate(getDateClassif());
		
		tmpOdS.setRegistered(registered);
		
		if (bb) { // при отваряне на модалния
			setShowMe(true);		
		} 
		
		setOdSearch(tmpOdS);
		setObektDeisnostList(null);		
		
		LOGGER.debug("initOdS >>> ");
	}
	 
   /** 
    * разширено търсене на обект на дейност - изп. се само, ако е в модален прозорец
    * изивква се при затваряне на модалния прозореца (onhide) 
    * 
    */
   public void actionHideModal() {		
	   setOdSearch(null);
	   setObektDeisnostList(null);
	   setShowMe(false);
	   LOGGER.debug("actionHideModal >>> ");
	}
   
   
   /** 
    * разширено търсене на обект на дейност - бутон "Търсене"
    */
   public void actionSearchObektDein() {
	  
	   try {
		  
		   getOdSearch().calcEkatte(getSystemData());		   

		   getOdSearch().buildQuery();
		   setObektDeisnostList(new LazyDataModelSQL2Array(getOdSearch(), "a0 desc")); 
		   LOGGER.debug("actionSearchObektDein >>> {}", getObektDeisnostList().getRowCount());
	   
	   } catch (DbErrorException e) {
		   LOGGER.error("Грешка при определяне на област/община", e);
	  
	   } catch (Exception e) {
			LOGGER.error("Грешка при зареждане данните при търсене - няма подаден вид на обект на дейност на компонентата! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, IndexUIbean.getMessageResourceString("beanMessages", "compObektDeinostSearch.noVidObektDeinost"));
	   }
	  
	}
   
   /** 
    * разширено търсене на обект на дейност - бутон "Изчисти"
    */
   public void actionClearObektDein() {		
	   initOdS(false);
	   LOGGER.debug("actionClearCoresp >>> ");
	}  
  
   
   /**
    * Разширено търсене - избор на кореспондент
    */
   public void actionModalSelectOd(Object[] row) {	  
	   
		if (row != null && row[0] != null) {
			LOGGER.debug("actionModalSelectOd >>> {}", row[0]);
			// връща id и naimenovanie на избрания обект на дейност

			ValueExpression expr1 = getValueExpression("selIdObDein");
			ValueExpression expr2 = getValueExpression("selNaimObDein");

			ELContext ctx1 = getFacesContext().getELContext();
			if (expr1 != null) {
				expr1.setValue(ctx1, Integer.valueOf(row[0].toString()));
			}

			ELContext ctx2 = getFacesContext().getELContext();
			if (expr2 != null) {
				expr2.setValue(ctx2, String.valueOf(row[3].toString()));
			}
		}
	
		// извиква remoteCommnad - ако има такава....
		String remoteCommnad = (String) getAttributes().get("onComplete");
		if (remoteCommnad != null && !"".equals(remoteCommnad)) {
			PrimeFaces.current().executeScript(remoteCommnad);
		}		
   }

	/**
	 * Подсказка за ред от таблицата с резултати
	 * @param adrTxt
	 * @param phone
	 * @param email
	 * @return
	 */
   public String  titleContancts(String obshtina, String oblast, String adrTxt, String phone, String email) {
	   
	   StringBuilder title = new StringBuilder();
	   
	   if(obshtina != null && !obshtina.trim().isEmpty()) {
		   title.append("общ.: " + obshtina + ", ");
	   } 
	   
	   if(oblast != null && !oblast.trim().isEmpty()) {
		   title.append("обл.: " + oblast + ", ");
	   } 
	  
	   if(adrTxt != null && !adrTxt.trim().isEmpty()) {
		   title.append(adrTxt + ", ");
	   }
	  
	   if(phone != null && !phone.trim().isEmpty()) {
		   title.append("тел.: " + phone + ", ");
	   } 
	   
	   if(email != null && !email.trim().isEmpty()) {
		   title.append("e-mail: " + email+ ", ");
	   }
	   
	   return title.toString();
   }
   
   public ObektDeinostSearch getOdSearch() {
		ObektDeinostSearch eval = (ObektDeinostSearch) getStateHelper().eval(PropertyKeys.ODSEARCH, null);
		return eval != null ? eval : new ObektDeinostSearch();
	}
	
	public void setOdSearch(ObektDeinostSearch odSearch) {
		getStateHelper().put(PropertyKeys.ODSEARCH, odSearch);
	}
	
	public Integer getIdOd() {
		return (Integer) getStateHelper().eval(PropertyKeys.IDOD, null);
	}

	public void setIdOd(Integer idOd) {
		getStateHelper().put(PropertyKeys.IDOD, idOd);
	}
	
	public String getNaim() {
		return (String) getStateHelper().eval(PropertyKeys.NAIM, null);
	}

	public void setNaim(String naim) {
		getStateHelper().put(PropertyKeys.NAIM, naim);
	}

	public boolean isShowMe() {
		return (Boolean) getStateHelper().eval(PropertyKeys.SHOWME, false);
	}
	
	public void setShowMe(boolean showMe) {
		getStateHelper().put(PropertyKeys.SHOWME, showMe);
	}
	
	public LazyDataModelSQL2Array getObektDeisnostList() {
		return (LazyDataModelSQL2Array) getStateHelper().eval(PropertyKeys.OBEKTDEINOSTLIST, null);
	}

	public void setObektDeisnostList(LazyDataModelSQL2Array obektDeisnostList) {
		getStateHelper().put(PropertyKeys.OBEKTDEINOSTLIST, obektDeisnostList);
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

	/** @return */
	public Integer getLang() {
		return SysConstants.CODE_DEFAULT_LANG;
	}
	
	/** @return */
	public Date getCurrentDate() {
		return getDateClassif();
	}	
	
	/** @return the vid */
	public Integer getVid() {
		return vid;
	}

	/** @param vid the vid to set */
	public void setVid(Integer vid) {
		this.vid = vid;
	}

	private SystemData getSystemData() {
		if (this.systemData == null) {
			this.systemData =  (SystemData) JSFUtils.getManagedBean("systemData");
		}
		return this.systemData;
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
	
	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др.
	 */
	public void postProcessXLS(Object od) {
		new CustomExpPreProcess().postProcessXLS(od, "Списък на обекти на дейности", null , null, null);		
    }
	
	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката
	 */
	public void preProcessPDF(Object od)  {
		
		try {
			
			new CustomExpPreProcess().preProcessPDF(od, "Списък на обекти на дейности", null , null, null);		
						
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(),e);			
		
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);			
		} 
	}
	
	/**
	 * за експорт в pdf 
	 * @return
	 */
	public PDFOptions pdfOptions() {
		PDFOptions pdfOpt = new CustomExpPreProcess().pdfOptions(null, null, null);
        return pdfOpt;
	}
}