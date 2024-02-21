package com.ib.babhregs.beans;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.RegisterOptions;
import com.ib.indexui.system.IndexUIbean;

@Named
@ViewScoped
public class SystemDataBrowserBean extends IndexUIbean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1554426573534400597L;
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemDataBrowserBean.class);
	
	private RegisterOptions regOption;
	
	public void init() {
		LOGGER.info("SystemDataBrowserBean init!!!!!!");
		
		this.regOption = new RegisterOptions();
	}
		
	
	
	
	/********************************************************************/
	public RegisterOptions getRegOption() {
		return regOption;
	}

	public void setRegOption(RegisterOptions regOption) {
		this.regOption = regOption;
	}

}
