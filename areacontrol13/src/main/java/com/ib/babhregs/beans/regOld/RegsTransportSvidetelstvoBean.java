package com.ib.babhregs.beans.regOld;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.ib.babhregs.system.BabhConstants;
import com.ib.system.BaseUserData;
import com.ib.system.SysConstants;
import org.primefaces.component.export.PDFOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.PublicRegisterDAO;
import com.ib.indexui.customexporter.CustomExpPreProcess;
import com.ib.indexui.pagination.LazyDataModelSQL2Array;
import com.ib.indexui.system.IndexUIbean;


@Named
@ViewScoped

/**
 * Регистър на издадените удостоверения за правоспособност на водачи на транспортни средства, в които се превозват животни,
 * и на придружители на животни при транспортиране
 */
public class RegsTransportSvidetelstvoBean extends IndexUIbean   {



	private static final Logger LOGGER = LoggerFactory.getLogger(RegsTransportSvidetelstvoBean.class);
	private static final long serialVersionUID = -8301866853361231376L;

	private LazyDataModelSQL2Array regsList;
	PublicRegisterDAO publicRegisterDAO = new PublicRegisterDAO();

	//Това е за да сетвам езика през xhtml-to с o:viewParama и да правя разлика кога е външно и вътрешно приложение
	int currentLang=-100;
	@PostConstruct
	void initData() {

		try {
			regsList=new LazyDataModelSQL2Array(publicRegisterDAO.getRegisterAsList(18),"date_licenz desc");

		} catch (Exception e) {
			LOGGER.error("Грешка при инициализиране на списък! ", e);
		}
	}

	/**
	 * за експорт в excel - добавя заглавие и дата на изготвяне на справката и др.
	 */
	public void postProcessXLS(Object document) {

		String title = getMessageResourceString(LABELS, "regsTransportSvidetelstvo.title");
    	new CustomExpPreProcess().postProcessXLS(document, title, null, null, null);

	}


	/**
	 * за експорт в pdf - добавя заглавие и дата на изготвяне на справката
	 */
	public void preProcessPDF(Object document)  {
		try{

			String title = getMessageResourceString(LABELS, "regsTransportSvidetelstvo.title");
			new CustomExpPreProcess().preProcessPDF(document, title,  null, null, null);
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
