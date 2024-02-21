package com.ib.babhregs.db.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "temp_vmp")
public class TempVMP {
    
    @Id    
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "vanshna_opakovka")
    private String vanshnaOpakovka;
    
    @Column(name = "naim")
    private String naim;
    
    @Column(name = "nom_licens")
    private String nomLicens;
    
    @Column(name = "dat_licens")
    private String datLicens;
    
    @Column(name = "vid_licens")
    private String vidLicens;
    
    @Column(name = "srok_valid")
    private String srokValid;
    
    @Column(name = "status_licens")
    private String statusLicens;
    
    @Column(name = "atc")
    private String atc;
    
    @Column(name = "nom_stan")
    private String nomStan;
    
    @Column(name = "dat_stan")
    private String datStan;
    
    @Column(name = "subs")
    private String subs;
    
    @Column(name = "kolichestvo")
    private String kolichestvo;
    
    @Column(name = "eksp")
    private String eksp;
    
    @Column(name = "forma")
    private String forma;
    
    @Column(name = "nachin_pril")
    private String nachinPril;
    
    @Column(name = "opakovka")
    private String opakovka;
    
    @Column(name = "srok_godnost")
    private String srokGodnost;
    
    @Column(name = "rejim")
    private String rejim;
    
    @Column(name = "vid_jiv")
    private String vidJiv;
    
    @Column(name = "karenten_srok")
    private String karentenSrok;
    
    @Column(name = "proizvoditel")
    private String proizvoditel;
    
    @Column(name = "adres_proizvoditel")
    private String adresProizvoditel;
    
    @Column(name = "oop")
    private String oop;
    
    @Column(name = "adres_oop")
    private String adresOop;
    
    @Column(name = "pritel")
    private String pritel;
    
    @Column(name = "adres_pritejatel")
    private String adresPritejatel;
    
    @Column(name = "pic")
    private String pic;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVanshnaOpakovka() {
		return vanshnaOpakovka;
	}

	public void setVanshnaOpakovka(String vanshnaOpakovka) {
		this.vanshnaOpakovka = vanshnaOpakovka;
	}

	public String getNaim() {
		return naim;
	}

	public void setNaim(String naim) {
		this.naim = naim;
	}

	public String getNomLicens() {
		return nomLicens;
	}

	public void setNomLicens(String nomLicens) {
		this.nomLicens = nomLicens;
	}

	public String getDatLicens() {
		return datLicens;
	}

	public void setDatLicens(String datLicens) {
		this.datLicens = datLicens;
	}

	public String getVidLicens() {
		return vidLicens;
	}

	public void setVidLicens(String vidLicens) {
		this.vidLicens = vidLicens;
	}

	public String getSrokValid() {
		return srokValid;
	}

	public void setSrokValid(String srokValid) {
		this.srokValid = srokValid;
	}

	public String getStatusLicens() {
		return statusLicens;
	}

	public void setStatusLicens(String statusLicens) {
		this.statusLicens = statusLicens;
	}

	public String getAtc() {
		return atc;
	}

	public void setAtc(String atc) {
		this.atc = atc;
	}

	public String getNomStan() {
		return nomStan;
	}

	public void setNomStan(String nomStan) {
		this.nomStan = nomStan;
	}

	public String getDatStan() {
		return datStan;
	}

	public void setDatStan(String datStan) {
		this.datStan = datStan;
	}

	public String getSubs() {
		return subs;
	}

	public void setSubs(String subs) {
		this.subs = subs;
	}

	public String getKolichestvo() {
		return kolichestvo;
	}

	public void setKolichestvo(String kolichestvo) {
		this.kolichestvo = kolichestvo;
	}

	public String getEksp() {
		return eksp;
	}

	public void setEksp(String eksp) {
		this.eksp = eksp;
	}

	public String getForma() {
		return forma;
	}

	public void setForma(String forma) {
		this.forma = forma;
	}

	public String getNachinPril() {
		return nachinPril;
	}

	public void setNachinPril(String nachinPril) {
		this.nachinPril = nachinPril;
	}

	public String getOpakovka() {
		return opakovka;
	}

	public void setOpakovka(String opakovka) {
		this.opakovka = opakovka;
	}

	public String getSrokGodnost() {
		return srokGodnost;
	}

	public void setSrokGodnost(String srokGodnost) {
		this.srokGodnost = srokGodnost;
	}

	public String getRejim() {
		return rejim;
	}

	public void setRejim(String rejim) {
		this.rejim = rejim;
	}

	public String getVidJiv() {
		return vidJiv;
	}

	public void setVidJiv(String vidJiv) {
		this.vidJiv = vidJiv;
	}

	public String getKarentenSrok() {
		return karentenSrok;
	}

	public void setKarentenSrok(String karentenSrok) {
		this.karentenSrok = karentenSrok;
	}

	public String getProizvoditel() {
		return proizvoditel;
	}

	public void setProizvoditel(String proizvoditel) {
		this.proizvoditel = proizvoditel;
	}

	public String getAdresProizvoditel() {
		return adresProizvoditel;
	}

	public void setAdresProizvoditel(String adresProizvoditel) {
		this.adresProizvoditel = adresProizvoditel;
	}

	public String getOop() {
		return oop;
	}

	public void setOop(String oop) {
		this.oop = oop;
	}

	public String getAdresOop() {
		return adresOop;
	}

	public void setAdresOop(String adresOop) {
		this.adresOop = adresOop;
	}

	public String getPritel() {
		return pritel;
	}

	public void setPritel(String pritel) {
		this.pritel = pritel;
	}

	public String getAdresPritejatel() {
		return adresPritejatel;
	}

	public void setAdresPritejatel(String adresPritejatel) {
		this.adresPritejatel = adresPritejatel;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

    
}