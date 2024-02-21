package eu.europa.ema.v1_26;

import eu.europa.ema.v1_26.form.AdminOffice;
import eu.europa.ema.v1_26.form.ContactDetailsType;
import eu.europa.ema.v1_26.form.ManuFacility;

public class BaseAddress {
	
		public BaseAddress() {
			
		}
		
		public BaseAddress(ManuFacility obj) {
			this.companyName = obj.getCompanyName();
			this.address1  = obj.getAddress1();	 
			this.address2  = obj.getAddress2();	 
			this.address3  = obj.getAddress3();	 
			this.address4  = obj.getAddress4();	 
			this.poBox  = obj.getPoBox();	 
			this.city  = obj.getCity();
			this.state  = obj.getState();	 
			this.county  = obj.getCounty();	 
			this.postCode  = obj.getPostCode();	 
			this.country  = obj.getCountry();	 
			this.orgID  = obj.getOrgID();	 
			this.locID  = obj.getLocID();
			this.language  = obj.getLanguage();	    
			this.phone  = obj.getPhone();	    
			this.email =  obj.getEmail();
		}
		
		public BaseAddress(AdminOffice obj) {
			this.companyName = obj.getCompanyName();
			this.address1  = obj.getAddress1();	 
			this.address2  = obj.getAddress2();	 
			this.address3  = obj.getAddress3();	 
			this.address4  = obj.getAddress4();	 
			this.poBox  = obj.getPoBox();	 
			this.city  = obj.getCity();
			this.state  = obj.getState();	 
			this.county  = obj.getCounty();	 
			this.postCode  = obj.getPostCode();	 
			this.country  = obj.getCountry();	 
			this.orgID  = obj.getOrgID();	 
			this.locID  = obj.getLocID();
			this.language  = obj.getLanguage();	    
			this.phone  = obj.getPhone();	    
			this.email =  obj.getEmail();
		}
		
		public BaseAddress(ContactDetailsType obj) {
			this.companyName = obj.getCompanyName();
			this.address1  = obj.getAddress1();	 
			this.address2  = obj.getAddress2();	 
			this.address3  = obj.getAddress3();	 
			this.address4  = obj.getAddress4();
			this.city  = obj.getCity();
			this.state  = obj.getState();	 
			this.county  = obj.getCounty();	 
			this.postCode  = obj.getPostCode();	 
			this.country  = obj.getCountry();	 
			this.orgID  = obj.getOrgID();	 
			this.locID  = obj.getLocID();
			this.language  = obj.getLanguage();	    
			this.phone  = obj.getPhone();	    
			this.email =  obj.getEmail();
			this.personalTitle =  obj.getPersonalTitle();	    
			this.givenName =  obj.getGivenName();	    
			this.familyName =  obj.getFamilyName();
		}
	 
	    private String companyName;	 
	    private String address1;	 
	    private String address2;	 
	    private String address3;	 
	    private String address4;	 
	    private String poBox;	 
	    private String city;
	    private String state;	 
	    private String county;	 
	    private String postCode;	 
	    private String country;	 
	    private String orgID;	 
	    private String locID;
	    private String language;	    
	    private String phone;	    
	    private String email;
	    
	    private String personalTitle;	    
	    private String givenName;	    
	    private String familyName;
	    
	    
	    
	    
		public String getPersonalTitle() {
			return personalTitle;
		}

		public void setPersonalTitle(String personalTitle) {
			this.personalTitle = personalTitle;
		}

		public String getGivenName() {
			return givenName;
		}

		public void setGivenName(String givenName) {
			this.givenName = givenName;
		}

		public String getFamilyName() {
			return familyName;
		}

		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}

		public String getCompanyName() {
			return companyName;
		}

		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

		public String getAddress1() {
			return address1;
		}

		public void setAddress1(String address1) {
			this.address1 = address1;
		}

		public String getAddress2() {
			return address2;
		}

		public void setAddress2(String address2) {
			this.address2 = address2;
		}

		public String getAddress3() {
			return address3;
		}

		public void setAddress3(String address3) {
			this.address3 = address3;
		}

		public String getAddress4() {
			return address4;
		}

		public void setAddress4(String address4) {
			this.address4 = address4;
		}

		public String getPoBox() {
			return poBox;
		}

		public void setPoBox(String poBox) {
			this.poBox = poBox;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getCounty() {
			return county;
		}

		public void setCounty(String county) {
			this.county = county;
		}

		public String getPostCode() {
			return postCode;
		}

		public void setPostCode(String postCode) {
			this.postCode = postCode;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getOrgID() {
			return orgID;
		}

		public void setOrgID(String orgID) {
			this.orgID = orgID;
		}

		public String getLocID() {
			return locID;
		}

		public void setLocID(String locID) {
			this.locID = locID;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

	  
}
