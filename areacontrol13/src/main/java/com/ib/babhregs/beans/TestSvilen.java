package com.ib.babhregs.beans;


import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.indexui.system.IndexUIbean;


@Named
@ViewScoped
public class TestSvilen extends IndexUIbean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1827667122996829015L;


	private static final Logger LOGGER = LoggerFactory.getLogger(TestSvilen.class);
	
	
	private Integer idObject;	
	private Date decodeDate;
	private String txtRef;

	
	public void someActionAfterSelection() {
		// Тук може да се сложи дали по този референт имаме обект в базата за да го заредим на екран.
		System.out.println("someActionAfterSelection");
	}	
	/** */
	@PostConstruct
	void initData() {
		decodeDate=new Date();
		
	}

	public Integer getIdObject() {
		return idObject;
	}

	public void setIdObject(Integer idObject) {
		this.idObject = idObject;
	}

	public Date getDecodeDate() {
		return decodeDate;
	}

	public void setDecodeDate(Date decodeDate) {
		this.decodeDate = decodeDate;
	}
	 
	public String getTxtRef() {
		return txtRef;
	}
	public void setTxtRef(String txtRef) {
		this.txtRef = txtRef;
	}
	
	 
}