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
 * <p>Java class for contact-during-procedure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contact-during-procedure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="member-states" type="{http://www.ema.eaf/dictionary/}member-states"/>
 *         &lt;element name="contact-details" type="{http://www.ema.eaf/dictionary/}contact-details-type" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contact-during-procedure", propOrder = {
    "memberStates",
    "contactDetails"
})
public class ContactDuringProcedure {

    @XmlElement(name = "member-states", required = true)
    protected MemberStates memberStates;
    @XmlElement(name = "contact-details", required = true)
    protected List<ContactDetailsType> contactDetails;

    /**
     * Gets the value of the memberStates property.
     * 
     * @return
     *     possible object is
     *     {@link MemberStates }
     *     
     */
    public MemberStates getMemberStates() {
        return memberStates;
    }

    /**
     * Sets the value of the memberStates property.
     * 
     * @param value
     *     allowed object is
     *     {@link MemberStates }
     *     
     */
    public void setMemberStates(MemberStates value) {
        this.memberStates = value;
    }

    /**
     * Gets the value of the contactDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contactDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContactDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContactDetailsType }
     * 
     * 
     */
    public List<ContactDetailsType> getContactDetails() {
        if (contactDetails == null) {
            contactDetails = new ArrayList<ContactDetailsType>();
        }
        return this.contactDetails;
    }

}