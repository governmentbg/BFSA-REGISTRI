package com.ib.babhregs.rest.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/** 
 * Клас, описващ заявление, дошло от деловодната система на БАБХ
 * @author Vasil Shulev
 * @version 1.2
 * @since  01.05.2023
 */
public class DocWS implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7350205498036949763L;
	
	
	/** Системен идентификатор на заявление в деловодната система  */
	private Integer id ;
	
	/** Тип на документа  */
	private Integer tipDoc;
	
	/** Тип на документа (като текст) */
	private String tipDocAsText;
	
	
	/** Вид на документа  */
	private Integer vidDoc;
	
	/** Вид на документа (като текст)  */
	private String vidDocAsText;
	
	/** Системен идентификатор на кореспондент  */
	private Integer idCoresp;
	
	/** Системен идентификатор на регистратура  */
	private Integer idRegistratura;	
	
	/** Регистратура (като текст)  */
	private String registraturaAsText;
	
	/** Относно/анотация  */
	private String anot;
	
	/** Идва от  */
	private String idvaOt;
	
	/** Регистрационен номер на документ  */
	private String rnDoc;
	
	/** Дата на документ  */
	private Date datDoc;
	
	@JsonProperty("coresp")
	/** Кореспондент  */
	private CorespWS coresp;
	
	
	@JsonProperty("files")
	/** Списък на файлове (без заредено съдържание)  */
	private List<FileWS> files = new ArrayList<FileWS>();

	/** Поставена задача  */
	private String taskInfo;
	
	/** Системен идентификатор на задача  */
	private Integer taskId;
	
	
	/** email адреси на служители, отговарящи за задачата  */
	private List<String> emails = new ArrayList<String>(); 
	
	/** Номер на администратиена услуга  */
	 private String nomAdmUsluga;
	 
	 
	 /** Идентификатор на административна услуга  */
	 private String identificatorUsluga;
	
	//Getter & Setters

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTipDoc() {
		return tipDoc;
	}

	public void setTipDoc(Integer tipDoc) {
		this.tipDoc = tipDoc;
	}

	public Integer getVidDoc() {
		return vidDoc;
	}

	public void setVidDoc(Integer vidDoc) {
		this.vidDoc = vidDoc;
	}

	public Integer getIdCoresp() {
		return idCoresp;
	}

	public void setIdCoresp(Integer idCoresp) {
		this.idCoresp = idCoresp;
	}

	public Integer getIdRegistratura() {
		return idRegistratura;
	}

	public void setIdRegistratura(Integer idRegistratura) {
		this.idRegistratura = idRegistratura;
	}

	public String getAnot() {
		return anot;
	}

	public void setAnot(String anot) {
		this.anot = anot;
	}

	public String getIdvaOt() {
		return idvaOt;
	}

	public void setIdvaOt(String idvaOt) {
		this.idvaOt = idvaOt;
	}

	public String getRnDoc() {
		return rnDoc;
	}

	public void setRnDoc(String rnDoc) {
		this.rnDoc = rnDoc;
	}

	public Date getDatDoc() {
		return datDoc;
	}

	public void setDatDoc(Date datDoc) {
		this.datDoc = datDoc;
	}

	public CorespWS getCoresp() {
		return coresp;
	}

	public void setCoresp(CorespWS coresp) {
		this.coresp = coresp;
	}

	public List<FileWS> getFiles() {
		return files;
	}

	public void setFiles(List<FileWS> files) {
		this.files = files;
	}

	public String getTipDocAsText() {
		return tipDocAsText;
	}

	public void setTipDocAsText(String tipDocAsText) {
		this.tipDocAsText = tipDocAsText;
	}

	public String getVidDocAsText() {
		return vidDocAsText;
	}

	public void setVidDocAsText(String vidDocAsText) {
		this.vidDocAsText = vidDocAsText;
	}

	public String getRegistraturaAsText() {
		return registraturaAsText;
	}

	public void setRegistraturaAsText(String registraturaAsText) {
		this.registraturaAsText = registraturaAsText;
	}

	public String getTaskInfo() {
		return taskInfo;
	}

	public void setTaskInfo(String taskInfo) {
		this.taskInfo = taskInfo;
	}

	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getNomAdmUsluga() {
		return nomAdmUsluga;
	}

	public void setNomAdmUsluga(String nomAdmUsluga) {
		this.nomAdmUsluga = nomAdmUsluga;
	}

	public String getIdentificatorUsluga() {
		return identificatorUsluga;
	}

	public void setIdentificatorUsluga(String identificatorUsluga) {
		this.identificatorUsluga = identificatorUsluga;
	}
	
	
	

}
