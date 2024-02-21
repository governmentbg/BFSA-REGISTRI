package com.ib.babhregs.beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;

import org.omnifaces.cdi.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dao.EventDeinostFurajiDAO;

import com.ib.babhregs.system.UserData;
import com.ib.indexui.system.IndexUIbean;
import com.ib.indexui.utils.JSFUtils;
import com.ib.system.db.JPA;
import com.ib.system.utils.SearchUtils;





@Named
@ViewScoped
public class MigrationView extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2808602661284249334L;
	private static final Logger LOGGER = LoggerFactory.getLogger(MigrationView.class);
	
	private UserData ud;
	
	/**
	 * Данни за миграция - тестово!
	 */
	private List<Object[]> migrInfoList;
	
	
	@PostConstruct
	public void init() {
		LOGGER.info("MigrationView init!!!!!!");
		
		this.ud = getUserData(UserData.class);
		String idVpisvane = JSFUtils.getRequestParameter("idVpis");
		
		try {
			
			if (idVpisvane != null && !SearchUtils.isEmpty(idVpisvane)) {				
				this.migrInfoList = new ArrayList<>();
				JPA.getUtil().runWithClose(() -> this.migrInfoList =  new EventDeinostFurajiDAO(ud).findMigrInfo(Integer.valueOf(idVpisvane)));
			}
			
		} catch (Exception e) {
			LOGGER.error("Грешка при зареждане на данните от миграция", e);
			JSFUtils.addGlobalMessage(FacesMessage.SEVERITY_ERROR, "Грешка при зареждане на данните от миграция!");
		}

		
	}

	public List<Object[]> getMigrInfoList() {
		return migrInfoList;
	}

	public void setMigrInfoList(List<Object[]> migrInfoList) {
		this.migrInfoList = migrInfoList;
	}

}
