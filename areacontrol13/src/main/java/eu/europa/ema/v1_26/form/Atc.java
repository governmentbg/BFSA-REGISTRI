//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.08.09 at 12:11:17 PM EEST 
//


package eu.europa.ema.v1_26.form;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for atc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="atc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="atc-code" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="code" type="{http://www.ema.eaf/dictionary/}string100"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="atc-name" type="{http://www.ema.eaf/dictionary/}string100"/>
 *         &lt;element name="atc-version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="atc-status" type="{http://www.ema.eaf/dictionary/}yes-no"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "atc", propOrder = {
    "atcCode",
    "atcName",
    "atcVersion",
    "atcStatus"
})
public class Atc {

    @XmlElement(name = "atc-code", required = true)
    protected List<Atc.AtcCode> atcCode;
    @XmlElement(name = "atc-name", required = true)
    protected String atcName;
    @XmlElement(name = "atc-version")
    protected String atcVersion;
    @XmlElement(name = "atc-status")
    protected int atcStatus;

    /**
     * Gets the value of the atcCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the atcCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAtcCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Atc.AtcCode }
     * 
     * 
     */
    public List<Atc.AtcCode> getAtcCode() {
        if (atcCode == null) {
            atcCode = new ArrayList<Atc.AtcCode>();
        }
        return this.atcCode;
    }

    /**
     * Gets the value of the atcName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtcName() {
        return atcName;
    }

    /**
     * Sets the value of the atcName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtcName(String value) {
        this.atcName = value;
    }

    /**
     * Gets the value of the atcVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtcVersion() {
        return atcVersion;
    }

    /**
     * Sets the value of the atcVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtcVersion(String value) {
        this.atcVersion = value;
    }

    /**
     * Gets the value of the atcStatus property.
     * 
     */
    public int getAtcStatus() {
        return atcStatus;
    }

    /**
     * Sets the value of the atcStatus property.
     * 
     */
    public void setAtcStatus(int value) {
        this.atcStatus = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="code" type="{http://www.ema.eaf/dictionary/}string100"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "code"
    })
    public static class AtcCode {

        @XmlElement(required = true)
        protected String code;

        /**
         * Gets the value of the code property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCode() {
            return code;
        }

        /**
         * Sets the value of the code property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCode(String value) {
            this.code = value;
        }

    }

}
