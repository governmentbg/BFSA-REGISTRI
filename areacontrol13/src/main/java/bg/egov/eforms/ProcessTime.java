//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.08.28 at 02:42:59 PM EEST 
//


package bg.egov.eforms;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				
 * 			
 * 
 * <p>Java class for ProcessTime complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessTime">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="processTimeType" type="{http://eform.e-gov.bg/dictionary/Code}Code"/>
 *         &lt;element name="processTimeUnit" type="{http://eform.e-gov.bg/dictionary/Code}Code"/>
 *         &lt;element name="processTime" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessTime", namespace = "http://eform.e-gov.bg/dictionary/ProcessTime", propOrder = {
    "processTimeType",
    "processTimeUnit",
    "processTime"
})
public class ProcessTime {

    @XmlElement(required = true)
    protected Code processTimeType;
    @XmlElement(required = true)
    protected Code processTimeUnit;
    protected BigDecimal processTime;

    /**
     * Gets the value of the processTimeType property.
     * 
     * @return
     *     possible object is
     *     {@link Code }
     *     
     */
    public Code getProcessTimeType() {
        return processTimeType;
    }

    /**
     * Sets the value of the processTimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Code }
     *     
     */
    public void setProcessTimeType(Code value) {
        this.processTimeType = value;
    }

    /**
     * Gets the value of the processTimeUnit property.
     * 
     * @return
     *     possible object is
     *     {@link Code }
     *     
     */
    public Code getProcessTimeUnit() {
        return processTimeUnit;
    }

    /**
     * Sets the value of the processTimeUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Code }
     *     
     */
    public void setProcessTimeUnit(Code value) {
        this.processTimeUnit = value;
    }

    /**
     * Gets the value of the processTime property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getProcessTime() {
        return processTime;
    }

    /**
     * Sets the value of the processTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setProcessTime(BigDecimal value) {
        this.processTime = value;
    }

}