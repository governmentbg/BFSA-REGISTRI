//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.08.28 at 02:42:59 PM EEST 
//


package bg.egov.eforms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 			
 * 			
 * 
 * <p>Java class for RegixSync complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegixSync">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="isSynchronized" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="dateTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegixSync", namespace = "http://eform.e-gov.bg/dictionary/RegixSync", propOrder = {
    "isSynchronized",
    "dateTime"
})
public class RegixSync {

    protected boolean isSynchronized;
    protected String dateTime;

    /**
     * Gets the value of the isSynchronized property.
     * 
     */
    public boolean isIsSynchronized() {
        return isSynchronized;
    }

    /**
     * Sets the value of the isSynchronized property.
     * 
     */
    public void setIsSynchronized(boolean value) {
        this.isSynchronized = value;
    }

    /**
     * Gets the value of the dateTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Sets the value of the dateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateTime(String value) {
        this.dateTime = value;
    }

}
