package com.ib.babhregs.db.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "temp_vmp_eu")
public class TempVMPEU {
    
    @Id    
    @Column(name = "code")
    private String code;
    
    @Column(name = "naims")
    private String naims;
    
    @Column(name = "subs")
    private String subs;
    

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNaims() {
		return naims;
	}

	public void setNaims(String naims) {
		this.naims = naims;
	}

	public String getSubs() {
		return subs;
	}

	public void setSubs(String subs) {
		this.subs = subs;
	}
    
    

	

    
}