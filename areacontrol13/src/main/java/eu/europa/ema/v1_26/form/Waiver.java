//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.08.09 at 12:11:17 PM EEST 
//


package eu.europa.ema.v1_26.form;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for waiver complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="waiver">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="waiver-decision-number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="waiver_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "waiver", propOrder = {
    "waiverDecisionNumber",
    "waiverType"
})
public class Waiver {

    @XmlElement(name = "waiver-decision-number", required = true)
    protected String waiverDecisionNumber;
    @XmlElement(name = "waiver_type", required = true)
    protected String waiverType;

    /**
     * Gets the value of the waiverDecisionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaiverDecisionNumber() {
        return waiverDecisionNumber;
    }

    /**
     * Sets the value of the waiverDecisionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaiverDecisionNumber(String value) {
        this.waiverDecisionNumber = value;
    }

    /**
     * Gets the value of the waiverType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaiverType() {
        return waiverType;
    }

    /**
     * Sets the value of the waiverType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaiverType(String value) {
        this.waiverType = value;
    }

}
