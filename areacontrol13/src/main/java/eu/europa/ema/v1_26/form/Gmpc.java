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
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for gmpc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gmpc">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="gmpc-number" type="{http://www.ema.eaf/dictionary/}string100"/>
 *         &lt;element name="gmpc-category" type="{http://www.ema.eaf/dictionary/}control-term"/>
 *         &lt;element name="authorisation-number" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="inspection-date" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="gmpc-detail">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="gmpc-activity" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
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
@XmlType(name = "gmpc", propOrder = {
    "gmpcNumber",
    "gmpcCategory",
    "authorisationNumber",
    "inspectionDate",
    "gmpcDetail"
})
public class Gmpc {

    @XmlElement(name = "gmpc-number", required = true)
    protected String gmpcNumber;
    @XmlElement(name = "gmpc-category", required = true)
    protected String gmpcCategory;
    @XmlElement(name = "authorisation-number", required = true)
    protected String authorisationNumber;
    @XmlElement(name = "inspection-date", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar inspectionDate;
    @XmlElement(name = "gmpc-detail", required = true)
    protected Gmpc.GmpcDetail gmpcDetail;

    /**
     * Gets the value of the gmpcNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGmpcNumber() {
        return gmpcNumber;
    }

    /**
     * Sets the value of the gmpcNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGmpcNumber(String value) {
        this.gmpcNumber = value;
    }

    /**
     * Gets the value of the gmpcCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGmpcCategory() {
        return gmpcCategory;
    }

    /**
     * Sets the value of the gmpcCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGmpcCategory(String value) {
        this.gmpcCategory = value;
    }

    /**
     * Gets the value of the authorisationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorisationNumber() {
        return authorisationNumber;
    }

    /**
     * Sets the value of the authorisationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorisationNumber(String value) {
        this.authorisationNumber = value;
    }

    /**
     * Gets the value of the inspectionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInspectionDate() {
        return inspectionDate;
    }

    /**
     * Sets the value of the inspectionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInspectionDate(XMLGregorianCalendar value) {
        this.inspectionDate = value;
    }

    /**
     * Gets the value of the gmpcDetail property.
     * 
     * @return
     *     possible object is
     *     {@link Gmpc.GmpcDetail }
     *     
     */
    public Gmpc.GmpcDetail getGmpcDetail() {
        return gmpcDetail;
    }

    /**
     * Sets the value of the gmpcDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gmpc.GmpcDetail }
     *     
     */
    public void setGmpcDetail(Gmpc.GmpcDetail value) {
        this.gmpcDetail = value;
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
     *         &lt;element name="gmpc-activity" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
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
        "gmpcActivity"
    })
    public static class GmpcDetail {

        @XmlElement(name = "gmpc-activity", required = true)
        protected List<String> gmpcActivity;

        /**
         * Gets the value of the gmpcActivity property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the gmpcActivity property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGmpcActivity().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getGmpcActivity() {
            if (gmpcActivity == null) {
                gmpcActivity = new ArrayList<String>();
            }
            return this.gmpcActivity;
        }

    }

}