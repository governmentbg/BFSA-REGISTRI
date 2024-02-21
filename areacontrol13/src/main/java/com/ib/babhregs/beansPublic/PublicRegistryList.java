package com.ib.babhregs.beansPublic;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;

import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.dto.SystemClassif;
import com.ib.system.exceptions.DbErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@RequestScoped
public class PublicRegistryList extends IndexUIbean {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6109650274586597094L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PublicRegistryList.class);

	private List<Integer> registerIdList;
	private List<SystemClassif> registerSC;
	private String			vlpObektAddress;		// • Местонахождение на обект на дейност (адрес)
	private Integer			vlpObektCountry;		// • Местонахождение на обект на дейност (адрес) държава
	private Integer			vlpObektEkatte;			// • Местонахождение на обект на дейност (адрес) нас.място /област /община
	private int countryBG; // ще се инициализира в инита през системна настройка: delo.countryBG


	@PostConstruct
	public void init(){
		try {
			this.countryBG = Integer.parseInt(getSystemData().getSettingsValue("delo.countryBG"));
		} catch (DbErrorException e) {
			LOGGER.error("error na countryBG w init na PublicRegistryList! ", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(beanMessages, "general.exception"), e.getMessage());
		}
	}

	/**
	 * занулява полетата при смяна на държава
	 */
	public void  actionChangeCountry() {
		setVlpObektEkatte(null);
	}

	public List<Integer> getRegisterIdList() {
		return registerIdList;
	}

	public void setRegisterIdList(List<Integer> registerIdList) {
		this.registerIdList = registerIdList;
	}

	public List<SystemClassif> getRegisterSC() {
		return registerSC;
	}

	public void setRegisterSC(List<SystemClassif> registerSC) {
		this.registerSC = registerSC;
	}


	public String getVlpObektAddress() {
		return vlpObektAddress;
	}

	public void setVlpObektAddress(String vlpObektAddress) {
		this.vlpObektAddress = vlpObektAddress;
	}

	public Integer getVlpObektCountry() {
		return vlpObektCountry;
	}

	public void setVlpObektCountry(Integer vlpObektCountry) {
		this.vlpObektCountry = vlpObektCountry;
	}

	public Integer getVlpObektEkatte() {
		return vlpObektEkatte;
	}

	public void setVlpObektEkatte(Integer vlpObektEkatte) {
		this.vlpObektEkatte = vlpObektEkatte;
	}

	public int getCountryBG() {
		return countryBG;
	}

	public void setCountryBG(int countryBG) {
		this.countryBG = countryBG;
	}
}
