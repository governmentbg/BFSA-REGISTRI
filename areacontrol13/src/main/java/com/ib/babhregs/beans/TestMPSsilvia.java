package com.ib.babhregs.beans;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.babhregs.db.dto.Mps;
import com.ib.indexui.system.IndexUIbean;

@Named
@ViewScoped
public class TestMPSsilvia extends IndexUIbean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2245845751609387302L;
	private static final Logger LOGGER = LoggerFactory.getLogger(TestMPSsilvia.class);

	private Date decodeDate;
	private Integer selectedMPSid;
	private Mps selectedMPS;

	@PostConstruct
	void init() {
		this.decodeDate = new Date();
	}

	public void actionCompleteSelection() {
		
		LOGGER.info("SELECTED MPS ID IS: " + selectedMPSid);
		LOGGER.info("SELECTED MPS IS: " + selectedMPS.getId());
		
	}

	public Date getDecodeDate() {
		return decodeDate;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate;
	}

	public Integer getSelectedMPSid() {
		return selectedMPSid;
	}

	public void setSelectedMPSid(Integer selectedMPSid) {
		this.selectedMPSid = selectedMPSid;
	}

	public Mps getSelectedMPS() {
		return selectedMPS;
	}

	public void setSelectedMPS(Mps selectedMPS) {
		this.selectedMPS = selectedMPS;
	}

}
