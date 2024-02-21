package com.ib.babhregs.beans;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.system.SystemData;
import com.ib.babhregs.system.UserData;
import com.ib.indexui.system.IndexUIbean;


/**
 * 
 * Работен плот
 * 
 * 
 */

@Named
@ViewScoped
public class Dashboard extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 972836306549920602L;

	private static final Logger LOGGER = LoggerFactory.getLogger(Dashboard.class);
	
	private SystemData sd;
	private UserData ud;
	
	//1,2,3,4
	private Integer selectedCodeMenuObject;
 	
	private String styleLineDataResult;
	
	@PostConstruct
	public void init() {
		
		sd = (SystemData) getSystemData();
		ud = getUserData(UserData.class);			
		
		selectedCodeMenuObject = Integer.valueOf(1);
		
		styleLineDataResult = "overview-box-1";
	}
	
	public void actionSelectMenuOnject(Integer selectedCodeMenuObject) {
		this.selectedCodeMenuObject = selectedCodeMenuObject;
		
		switch (selectedCodeMenuObject.intValue()){
			case 1:
				styleLineDataResult = "overview-box-1";
			break;
			case 2:
				styleLineDataResult = "overview-box-3";
			break;
			case 3:
				styleLineDataResult = "overview-box-4";
			break;
			case 4:
				styleLineDataResult = "overview-box-2";
			break;
			
		}
		
	}
	

	public Integer getSelectedCodeMenuObject() {
		return selectedCodeMenuObject;
	}

	public void setSelectedCodeMenuObject(Integer selectedCodeMenuObject) {
		this.selectedCodeMenuObject = selectedCodeMenuObject;
	}

	public String getStyleLineDataResult() {
		return styleLineDataResult;
	}

	public void setStyleLineDataResult(String styleLineDataResult) {
		this.styleLineDataResult = styleLineDataResult;
	}


}
