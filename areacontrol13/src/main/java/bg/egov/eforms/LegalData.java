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
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				
 * 			
 * 
 * <p>Java class for LegalData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LegalData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identifier" type="{http://eform.e-gov.bg/dictionary/Identifier}Identifier" minOccurs="0"/>
 *         &lt;element name="eik" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nameLatin" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="companyType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isLegal" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
@XmlType(name = "LegalData", namespace = "http://eform.e-gov.bg/dictionary/LegalEntity", propOrder = {
    "identifier",
    "eik",
    "name",
    "nameLatin",
    "companyType",
    "isLegal",
    "regix"
})
public class LegalData {

    protected Identifier identifier;
    protected String eik;
    protected String name;
    protected List<String> nameLatin;
    protected String companyType;
    protected Boolean isLegal;
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
     * Gets the value of the eik property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEik() {
        return eik;
    }

    /**
     * Sets the value of the eik property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEik(String value) {
        this.eik = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the nameLatin property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nameLatin property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNameLatin().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNameLatin() {
        if (nameLatin == null) {
            nameLatin = new ArrayList<String>();
        }
        return this.nameLatin;
    }

    /**
     * Gets the value of the companyType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyType() {
        return companyType;
    }

    /**
     * Sets the value of the companyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyType(String value) {
        this.companyType = value;
    }

    /**
     * Gets the value of the isLegal property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsLegal() {
        return isLegal;
    }

    /**
     * Sets the value of the isLegal property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsLegal(Boolean value) {
        this.isLegal = value;
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
