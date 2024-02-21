package com.ib.babhregs.beansPublic.registers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.PublicRegisterDAO;
import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
/**
 * Регистър на издадените разрешителни за провеждане на опити с животни
 */

@Named
@ViewScoped
public class RegisterAnimalDefault extends IndexUIbean   {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterAnimalDefault.class);
	private static final long serialVersionUID = -3228566399229446153L;

	private LazyDataModelSQL2Array regsList;

	PublicRegisterDAO publicRegisterDAO = new PublicRegisterDAO();
	private String title="";
	private String pageTitle;
	private String []pars= {null,null,null,null,null};
	
	private String [] titles= {"regsChickenCages.title",
			"regsChickenFlock.title",
			"regsChickenWater.title",
			"regsChickenFarms.title",
			"regsChickenOthers.title",
			"regsIncubators.title",
			"regsBees.title",
			"regsAquaCultures.title",
			"regsHostelsAnimals.title",
			"regsDogFarms.title",
			"regsPetHostels.title",
			"regsFurryDeer.title",
			"regsAnimalObjectsBig.title",
			"regsAnimalObjectsSmall.title",
			"regsAnimalObjectsHoof.title",
			"regsPigObjects.title",
			"regsAnimalObjectsOthers.title",
			"regsPrivateFarms.title",
			"regsLiveAnimalMarkets.title",
			"regsCollectingAnimals.title",
			"regsAnimalTransportBreaks.title",
			"regsEmbrionsInstall.title",
			"regsZooObjects.title",
			"regsAnimalTraders.title",
			"regsEmbrioTraders.title"};
	
	private String [] [] params=  {
			//01 idRegister,typeObjectEpizodichnoZnch,508(видЖ), 502(предн.) 518(техн.)
			{"47","2,4","37","30","19,20,1,21"},
			//02
			{"48","2,4","6,39,157,74","31,5,6,3",null},
			//03
			{"49","2,4","43,73,75,74",null,null},
			//04
			{"50","2,4","36",null,null},
			//05
			{"51","2,4","38,39,40,67,68,71,72,42,147,37","34",null},
			//06
			{"52","43",null,null,null},
			//07
			{"53","5,6",null,null,null},
			//08
			{"54","12,13",null,null,null},
			//09
			{"55","31",null,null,null},
			//10
			{"56","32","32",null,null},
			//11
			{"57","33",null,null,null},
			//12
			{"58","15",null,null,null},
			//13
			{"59",null,"1,17,18,19",null,null},
			//14
			{"60",null,"2,20,21,22",null,null},
			//15
			{"61",null,"4,24,25,26,27",null,null},
			//16
			{"62","1,3,8",null,null,null},
			//17
			{"63","18,35,47,48,49,50,46,41,51,20",null,null,null},
			//18
			{"64","7",null,null,null},
			//19
			{"65","23",null,null,null},
			//20
			{"66","40",null,null,null},
			//21
			{"67","39",null,null,null},
			//22
			{"68","42",null,"35,36",null},
			//23
			{"69","37,34,29,20",null,null,null},
			//24
			//{"6",null,null,null,null},
			//25
			//{"6",null,null,null,null}
			};
 	
	@PostConstruct
	void initData() {

		try {
			String par=JSFUtils.getRequestParameter("idReg");
			System.out.println("idReg: "+par);
			if(par!=null && !par.isEmpty() ) {
				Integer idReg=Integer.valueOf(par);
				setTitle(titles[idReg-47]);
				setPageTitle(getMessageResourceString(LABELS, getTitle()));		
				//regsList= new LazyDataModelSQL2Array(null, "");
				regsList=new LazyDataModelSQL2Array(publicRegisterDAO.getRegisterAsList(idReg),null);
			}
			

		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на списък! ", e);
		}
	}
	
	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др.
	 */
	public void postProcessXLS(Object document) {
		
		String title1 = getMessageResourceString(LABELS, getTitle());		  
    	new CustomExpPreProcess().postProcessXLS(document, title1, null, null, null);		
     
	}
	

	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката
	 */
	public void preProcessPDF(Object document)  {
		try{
			
			String title1 = getMessageResourceString(LABELS, getTitle());		
			new CustomExpPreProcess().preProcessPDF(document, title1,  null, null, null);		
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
		
	public LazyDataModelSQL2Array getRegsList() {
		return regsList;
	}

	public void setRegsList(LazyDataModelSQL2Array regsList) {
		this.regsList = regsList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	
	//Това е за да сетвам езика през xhtml-to с o:viewParama и да правя разлика кога е външно и вътрешно приложение
	int currentLang=-100;
	
	/**Ako ne e podaden kato parametyr, opitwame da go wzemem ot \serData-tata
	 * @return
	 */
	@Override
	public int getCurrentLang() {
		LOGGER.info("getCurrentLang");
		if (currentLang==-100) {
			try {
				currentLang = super.getCurrentLang();
			} catch (Exception e) {
				currentLang = BabhConstants.CODE_DEFAULT_LANG;
			}
		}

		return currentLang ;

	}

	public void setCurrentLang(int lang){
		LOGGER.info("setCurrentLang(lange={})",lang);
		currentLang=lang;
	}
}
