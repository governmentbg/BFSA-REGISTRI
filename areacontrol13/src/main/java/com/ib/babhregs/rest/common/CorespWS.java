package com.ib.babhregs.rest.common;

import java.io.Serializable;


/** 
 * Клас, описващ кореспондент на деловоден документ
 * @author Vasil Shulev
 * @version 1.2
 * @since  01.05.2023
 */
public class CorespWS implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3434659418282767814L;
	
	/** Системен идентификатор на кореспондент  */
	private Integer id ;
	
	/** Тип на кореспондент  */
	private Integer tipCoresp;
	
	/** Име  */
	private String imeCoresp;
	
	/** ЕГН  */
	private String egnCoresp;
	
	/** Булстат  */
	private String bulstatCoresp;
	
	/** ЛНЧ  */
	private String lnchCoresp;
	
	
	
	//Адрес на кореспондент
	
	/** Код на държава  */
	private Integer darjavaCoresp;
	
	/** EKATTE  */
	private Integer mestoCoresp;
	
	/** EKATTE Област*/
	private String oblastCoresp ;
	
	/** EKATTE Община */
	private String obstinaCoresp;
	
	/** EKATTE Район */
	private String raionCoresp;
	
	/** Пощенски код  */
	private String pkCoresp;
	
	/** Адрес - улица номер, вх, ет, ап, алабала  */
	private String adresCoresp;
	
	/** Телефон  */
	private String tel;
	
	/** Факс  */
	private String faxCoresp;
	
	/** Email  */
	private String mailCoresp;
	
	/** Допълнителна информация  */
	private String dopInfoCoresp;
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTipCoresp() {
		return tipCoresp;
	}
	public void setTipCoresp(Integer tipCoresp) {
		this.tipCoresp = tipCoresp;
	}
	public Integer getDarjavaCoresp() {
		return darjavaCoresp;
	}
	public void setDarjavaCoresp(Integer darjavaCoresp) {
		this.darjavaCoresp = darjavaCoresp;
	}
	public Integer getMestoCoresp() {
		return mestoCoresp;
	}
	public void setMestoCoresp(Integer mestoCoresp) {
		this.mestoCoresp = mestoCoresp;
	}
	public String getImeCoresp() {
		return imeCoresp;
	}
	public void setImeCoresp(String imeCoresp) {
		this.imeCoresp = imeCoresp;
	}
	public String getEgnCoresp() {
		return egnCoresp;
	}
	public void setEgnCoresp(String egnCoresp) {
		this.egnCoresp = egnCoresp;
	}
	public String getBulstatCoresp() {
		return bulstatCoresp;
	}
	public void setBulstatCoresp(String bulstatCoresp) {
		this.bulstatCoresp = bulstatCoresp;
	}
	public String getLnchCoresp() {
		return lnchCoresp;
	}
	public void setLnchCoresp(String lnchCoresp) {
		this.lnchCoresp = lnchCoresp;
	}
	public String getOblastCoresp() {
		return oblastCoresp;
	}
	public void setOblastCoresp(String oblastCoresp) {
		this.oblastCoresp = oblastCoresp;
	}
	public String getObstinaCoresp() {
		return obstinaCoresp;
	}
	public void setObstinaCoresp(String obstinaCoresp) {
		this.obstinaCoresp = obstinaCoresp;
	}
	public String getRaionCoresp() {
		return raionCoresp;
	}
	public void setRaionCoresp(String raionCoresp) {
		this.raionCoresp = raionCoresp;
	}
	public String getPkCoresp() {
		return pkCoresp;
	}
	public void setPkCoresp(String pkCoresp) {
		this.pkCoresp = pkCoresp;
	}
	public String getAdresCoresp() {
		return adresCoresp;
	}
	public void setAdresCoresp(String adresCoresp) {
		this.adresCoresp = adresCoresp;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getFaxCoresp() {
		return faxCoresp;
	}
	public void setFaxCoresp(String faxCoresp) {
		this.faxCoresp = faxCoresp;
	}
	public String getMailCoresp() {
		return mailCoresp;
	}
	public void setMailCoresp(String mailCoresp) {
		this.mailCoresp = mailCoresp;
	}
	public String getDopInfoCoresp() {
		return dopInfoCoresp;
	}
	public void setDopInfoCoresp(String dopInfoCoresp) {
		this.dopInfoCoresp = dopInfoCoresp;
	}
	
	
	

}
