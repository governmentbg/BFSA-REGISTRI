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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for sci-advice-details-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sci-advice-details-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="advice-given" type="{http://www.ema.eaf/dictionary/}yes-no"/>
 *         &lt;element name="advice-details" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *                   &lt;element name="reference" type="{http://www.ema.eaf/dictionary/}string30"/>
 *                   &lt;element name="member-state" type="{http://www.ema.eaf/dictionary/}control-term"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sci-advice-details-type", propOrder = {
    "adviceGiven",
    "adviceDetails"
})
@XmlSeeAlso({
    eu.europa.ema.v1_26.form.ScientificAdvice.ScientificAdviceMemberState.class
})
public class SciAdviceDetailsType {

    @XmlElement(name = "advice-given")
    protected int adviceGiven;
    @XmlElement(name = "advice-details", required = true)
    protected List<SciAdviceDetailsType.AdviceDetails> adviceDetails;

    /**
     * Gets the value of the adviceGiven property.
     * 
     */
    public int getAdviceGiven() {
        return adviceGiven;
    }

    /**
     * Sets the value of the adviceGiven property.
     * 
     */
    public void setAdviceGiven(int value) {
        this.adviceGiven = value;
    }

    /**
     * Gets the value of the adviceDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the adviceDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdviceDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SciAdviceDetailsType.AdviceDetails }
     * 
     * 
     */
    public List<SciAdviceDetailsType.AdviceDetails> getAdviceDetails() {
        if (adviceDetails == null) {
            adviceDetails = new ArrayList<SciAdviceDetailsType.AdviceDetails>();
        }
        return this.adviceDetails;
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
     *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date"/>
     *         &lt;element name="reference" type="{http://www.ema.eaf/dictionary/}string30"/>
     *         &lt;element name="member-state" type="{http://www.ema.eaf/dictionary/}control-term"/>
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
        "date",
        "reference",
        "memberState"
    })
    public static class AdviceDetails {

        @XmlElement(required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar date;
        @XmlElement(required = true)
        protected String reference;
        @XmlElement(name = "member-state", required = true)
        protected String memberState;

        /**
         * Gets the value of the date property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getDate() {
            return date;
        }

        /**
         * Sets the value of the date property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setDate(XMLGregorianCalendar value) {
            this.date = value;
        }

        /**
         * Gets the value of the reference property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getReference() {
            return reference;
        }

        /**
         * Sets the value of the reference property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setReference(String value) {
            this.reference = value;
        }

        /**
         * Gets the value of the memberState property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMemberState() {
            return memberState;
        }

        /**
         * Sets the value of the memberState property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMemberState(String value) {
            this.memberState = value;
        }

    }

}
