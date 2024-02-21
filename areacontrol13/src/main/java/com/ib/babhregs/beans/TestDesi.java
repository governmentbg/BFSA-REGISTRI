package com.ib.babhregs.beans;


import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.system.BabhConstants;
import com.ib.indexui.system.IndexUIbean;


@Named
@ViewScoped
public class TestDesi extends IndexUIbean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1827667122996829015L;


	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(TestDesi.class);
	
	
	private Integer vidObektDein;	
	private Date decodeDate;
	private Integer idObDein;
	private String naimenovanie;

	
	
	/** */
	@PostConstruct
	void initData() {
		
		decodeDate = new Date();
		//vidObektDein = BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_OEZ; //1 - Обекти за дейности с ОЕЗ
		vidObektDein = BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_FURAJI; // 2 - Обекти за дейности с фуражи
		//vidObektDein = BabhConstants.CODE_ZNACHENIE_TIP_OBEKT_VLP; // 3 - Обекти за дейности с ВЛП
				
	}
	
	public void someActionAfterSelection() {
		// Тук може да се сложи някакво действие с избора на обекта на дейност.
		System.out.println("idObDein >>> " + idObDein);
		System.out.println("naimenovanie >>> " + naimenovanie);
	}	
	
	/** @return the vidObektDein */
	public Integer getVidObektDein() {
		return vidObektDein;
	}
	
	/** @param vidObektDein the vidObektDein to set */
	public void setVidObektDein(Integer vidObektDein) {
		this.vidObektDein = vidObektDein;
	}
	
	/** @return the decodeDate */
	public Date getDecodeDate() {
		return decodeDate;
	}
	
	/** @param decodeDate the decodeDate to set */
	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate;
	}
	
	/** @return the idObDein */
	public Integer getIdObDein() {
		return idObDein;
	}

	/** @param idObDein the idObDein to set */
	public void setIdObDein(Integer idObDein) {
		this.idObDein = idObDein;
	}

	/** @return the naimenovanie */
	public String getNaimenovanie() {
		return naimenovanie;
	}
	
	/** @param naimenovanie the naimenovanie to set */
	public void setNaimenovanie(String naimenovanie) {
		this.naimenovanie = naimenovanie;
	}	
	 
}