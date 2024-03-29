//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.08.28 at 02:42:59 PM EEST 
//


package bg.egov.eforms;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * 
 * 		  
 * 			
 * 
 * <p>Java class for PersonalData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonalData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identifier" type="{http://eform.e-gov.bg/dictionary/Identifier}Identifier"/>
 *         &lt;element name="fullName" type="{http://eform.e-gov.bg/dictionary/CyrillicNameType}CyrillicNameType"/>
 *         &lt;element name="firstName" type="{http://eform.e-gov.bg/dictionary/CyrillicNameType}CyrillicNameType"/>
 *         &lt;element name="middleName" type="{http://eform.e-gov.bg/dictionary/CyrillicNameType}CyrillicNameType" minOccurs="0"/>
 *         &lt;element name="familyName" type="{http://eform.e-gov.bg/dictionary/CyrillicNameType}CyrillicNameType"/>
 *         &lt;element name="alternativeName" type="{http://eform.e-gov.bg/dictionary/CyrillicNameType}CyrillicNameType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="fullNameLatin" type="{http://eform.e-gov.bg/dictionary/LatinNameType}LatinNameType" minOccurs="0"/>
 *         &lt;element name="firstNameLatin" type="{http://eform.e-gov.bg/dictionary/LatinNameType}LatinNameType" minOccurs="0"/>
 *         &lt;element name="middleNameLatin" type="{http://eform.e-gov.bg/dictionary/LatinNameType}LatinNameType" minOccurs="0"/>
 *         &lt;element name="familyNameLatin" type="{http://eform.e-gov.bg/dictionary/LatinNameType}LatinNameType" minOccurs="0"/>
 *         &lt;element name="birthName" type="{http://eform.e-gov.bg/dictionary/CyrillicNameType}CyrillicNameType" minOccurs="0"/>
 *         &lt;element name="gender" type="{http://eform.e-gov.bg/dictionary/Code}Code" minOccurs="0"/>
 *         &lt;element name="countryOfBirth" type="{http://eform.e-gov.bg/dictionary/Code}Code" minOccurs="0"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="placeOfBirth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateOfDeath" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="countryOfDeath" type="{http://eform.e-gov.bg/dictionary/Code}Code" minOccurs="0"/>
 *         &lt;element name="placeOfDeath" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="citizenship" type="{http://eform.e-gov.bg/dictionary/Jurisdiction}Jurisdiction" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="residency" type="{http://eform.e-gov.bg/dictionary/Jurisdiction}Jurisdiction" minOccurs="0"/>
 *         &lt;element name="regix" type="{http://eform.e-gov.bg/dictionary/RegixSync}RegixSync" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonalData", namespace = "http://eform.e-gov.bg/dictionary/PersonalData", propOrder = {
    "identifier",
    "fullName",
    "firstName",
    "middleName",
    "familyName",
    "alternativeName",
    "fullNameLatin",
    "firstNameLatin",
    "middleNameLatin",
    "familyNameLatin",
    "birthName",
    "gender",
    "countryOfBirth",
    "dateOfBirth",
    "placeOfBirth",
    "dateOfDeath",
    "countryOfDeath",
    "placeOfDeath",
    "citizenship",
    "residency",
    "regix"
})
public class PersonalData {

    @XmlElement(required = true)
    protected Identifier identifier;
    @XmlElement(required = true)
    protected String fullName;
    @XmlElement(required = true)
    protected String firstName;
    protected String middleName;
    @XmlElement(required = true)
    protected String familyName;
    protected List<String> alternativeName;
    protected String fullNameLatin;
    protected String firstNameLatin;
    protected String middleNameLatin;
    protected String familyNameLatin;
    protected String birthName;
    protected Code gender;
    protected Code countryOfBirth;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    protected String placeOfBirth;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfDeath;
    protected Code countryOfDeath;
    protected String placeOfDeath;
    protected List<Jurisdiction> citizenship;
    protected Jurisdiction residency;
    protected RegixSync regix;

    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link Identifier }
     *     
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link Identifier }
     *     
     */
    public void setIdentifier(Identifier value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the fullName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the value of the fullName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullName(String value) {
        this.fullName = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the familyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Sets the value of the familyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyName(String value) {
        this.familyName = value;
    }

    /**
     * Gets the value of the alternativeName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alternativeName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlternativeName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAlternativeName() {
        if (alternativeName == null) {
            alternativeName = new ArrayList<String>();
        }
        return this.alternativeName;
    }

    /**
     * Gets the value of the fullNameLatin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullNameLatin() {
        return fullNameLatin;
    }

    /**
     * Sets the value of the fullNameLatin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullNameLatin(String value) {
        this.fullNameLatin = value;
    }

    /**
     * Gets the value of the firstNameLatin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstNameLatin() {
        return firstNameLatin;
    }

    /**
     * Sets the value of the firstNameLatin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstNameLatin(String value) {
        this.firstNameLatin = value;
    }

    /**
     * Gets the value of the middleNameLatin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleNameLatin() {
        return middleNameLatin;
    }

    /**
     * Sets the value of the middleNameLatin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleNameLatin(String value) {
        this.middleNameLatin = value;
    }

    /**
     * Gets the value of the familyNameLatin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyNameLatin() {
        return familyNameLatin;
    }

    /**
     * Sets the value of the familyNameLatin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyNameLatin(String value) {
        this.familyNameLatin = value;
    }

    /**
     * Gets the value of the birthName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthName() {
        return birthName;
    }

    /**
     * Sets the value of the birthName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthName(String value) {
        this.birthName = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link Code }
     *     
     */
    public Code getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link Code }
     *     
     */
    public void setGender(Code value) {
        this.gender = value;
    }

    /**
     * Gets the value of the countryOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link Code }
     *     
     */
    public Code getCountryOfBirth() {
        return countryOfBirth;
    }

    /**
     * Sets the value of the countryOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Code }
     *     
     */
    public void setCountryOfBirth(Code value) {
        this.countryOfBirth = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the placeOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    /**
     * Sets the value of the placeOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlaceOfBirth(String value) {
        this.placeOfBirth = value;
    }

    /**
     * Gets the value of the dateOfDeath property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfDeath() {
        return dateOfDeath;
    }

    /**
     * Sets the value of the dateOfDeath property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfDeath(XMLGregorianCalendar value) {
        this.dateOfDeath = value;
    }

    /**
     * Gets the value of the countryOfDeath property.
     * 
     * @return
     *     possible object is
     *     {@link Code }
     *     
     */
    public Code getCountryOfDeath() {
        return countryOfDeath;
    }

    /**
     * Sets the value of the countryOfDeath property.
     * 
     * @param value
     *     allowed object is
     *     {@link Code }
     *     
     */
    public void setCountryOfDeath(Code value) {
        this.countryOfDeath = value;
    }

    /**
     * Gets the value of the placeOfDeath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlaceOfDeath() {
        return placeOfDeath;
    }

    /**
     * Sets the value of the placeOfDeath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlaceOfDeath(String value) {
        this.placeOfDeath = value;
    }

    /**
     * Gets the value of the citizenship property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the citizenship property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCitizenship().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Jurisdiction }
     * 
     * 
     */
    public List<Jurisdiction> getCitizenship() {
        if (citizenship == null) {
            citizenship = new ArrayList<Jurisdiction>();
        }
        return this.citizenship;
    }

    /**
     * Gets the value of the residency property.
     * 
     * @return
     *     possible object is
     *     {@link Jurisdiction }
     *     
     */
    public Jurisdiction getResidency() {
        return residency;
    }

    /**
     * Sets the value of the residency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Jurisdiction }
     *     
     */
    public void setResidency(Jurisdiction value) {
        this.residency = value;
    }

    /**
     * Gets the value of the regix property.
     * 
     * @return
     *     possible object is
     *     {@link RegixSync }
     *     
     */
    public RegixSync getRegix() {
        return regix;
    }

    /**
     * Sets the value of the regix property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegixSync }
     *     
     */
    public void setRegix(RegixSync value) {
        this.regix = value;
    }

}
