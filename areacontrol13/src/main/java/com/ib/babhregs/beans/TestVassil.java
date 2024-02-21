package com.ib.babhregs.beans;


import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;


import com.ib.indexui.system.IndexUIbean;


@Named
@ViewScoped
public class TestVassil extends IndexUIbean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1827667122996829015L;


//	private static final Logger LOGGER = LoggerFactory.getLogger(TestVassil.class);

	private String txtEditor;
	private String txtSimple;

	
	/** */
	@PostConstruct
	void initData() {
		txtEditor = null;
		txtSimple = null;
	}

	public void actionCopyToSimple() {
		txtSimple = txtEditor;
	}

	public void actionClear() {
		txtEditor = null;
		txtSimple = null;
	}
	
	public String getTxtEditor() {
		return txtEditor;
	}



	public void setTxtEditor(String txtEditor) {
		this.txtEditor = txtEditor;
	}



	public String getTxtSimple() {
		return txtSimple;
	}



	public void setTxtSimple(String txtSimple) {
		this.txtSimple = txtSimple;
	}

	 
}