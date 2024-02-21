//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.08.09 at 12:11:17 PM EEST 
//


package eu.europa.ema.v1_26.form;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for generic-procedure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="generic-procedure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="selected" type="{http://www.ema.eaf/dictionary/}yes-no"/>
 *         &lt;element name="selected_scope" type="{http://www.ema.eaf/dictionary/}radiobutton"/>
 *         &lt;element name="procedure-number" type="{http://www.ema.eaf/dictionary/}string30"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "generic-procedure", propOrder = {
    "selected",
    "selectedScope",
    "procedureNumber"
})
@XmlSeeAlso({
    DecentralisedProcedure.class,
    CentralisedProcedure.class,
    NationalProcedure.class,
    MutualRecognitionProcedure.class
})
public class GenericProcedure {

    protected int selected;
    @XmlElement(name = "selected_scope", required = true)
    protected BigInteger selectedScope;
    @XmlElement(name = "procedure-number", required = true)
    protected String procedureNumber;

    /**
     * Gets the value of the selected property.
     * 
     */
    public int getSelected() {
        return selected;
    }

    /**
     * Sets the value of the selected property.
     * 
     */
    public void setSelected(int value) {
        this.selected = value;
    }

    /**
     * Gets the value of the selectedScope property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSelectedScope() {
        return selectedScope;
    }

    /**
     * Sets the value of the selectedScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSelectedScope(BigInteger value) {
        this.selectedScope = value;
    }

    /**
     * Gets the value of the procedureNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedureNumber() {
        return procedureNumber;
    }

    /**
     * Sets the value of the procedureNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedureNumber(String value) {
        this.procedureNumber = value;
    }

}
