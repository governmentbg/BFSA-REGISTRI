package com.ib.babhregs.components;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@FacesComponent(value = "compSendEmail", createTag = true)
public class CompSendEmail extends UINamingContainer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompMPSsearch.class);
	
	
	
	public void initComp() {
		LOGGER.debug("compSendEmail >>> ");
	}


}
