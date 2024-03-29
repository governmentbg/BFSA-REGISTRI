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
 * <p>Java class for contact-batch-testing-site complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="contact-batch-testing-site">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.ema.eaf/dictionary/}contact-details-type">
 *       &lt;sequence>
 *         &lt;element name="control-tests" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="control-test" type="{http://www.ema.eaf/dictionary/}control-term"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="function-description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attach-GMP-complaince" type="{http://www.ema.eaf/dictionary/}yes-no"/>
 *         &lt;element name="eudraGMP-auth-reference" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attach-gmp-certificate" type="{http://www.ema.eaf/dictionary/}yes-no"/>
 *         &lt;element name="gmpc" type="{http://www.ema.eaf/dictionary/}gmpc"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contact-batch-testing-site", propOrder = {
    "controlTests",
    "functionDescription",
    "attachGMPComplaince",
    "eudraGMPAuthReference",
    "attachGmpCertificate",
    "gmpc"
})
public class ContactBatchTestingSite
    extends ContactDetailsType
{

    @XmlElement(name = "control-tests", required = true)
    protected List<ContactBatchTestingSite.ControlTests> controlTests;
    @XmlElement(name = "function-description", required = true)
    protected String functionDescription;
    @XmlElement(name = "attach-GMP-complaince")
    protected int attachGMPComplaince;
    @XmlElement(name = "eudraGMP-auth-reference", required = true)
    protected String eudraGMPAuthReference;
    @XmlElement(name = "attach-gmp-certificate")
    protected int attachGmpCertificate;
    @XmlElement(required = true)
    protected Gmpc gmpc;

    /**
     * Gets the value of the controlTests property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the controlTests property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getControlTests().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContactBatchTestingSite.ControlTests }
     * 
     * 
     */
    public List<ContactBatchTestingSite.ControlTests> getControlTests() {
        if (controlTests == null) {
            controlTests = new ArrayList<ContactBatchTestingSite.ControlTests>();
        }
        return this.controlTests;
    }

    /**
     * Gets the value of the functionDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFunctionDescription() {
        return functionDescription;
    }

    /**
     * Sets the value of the functionDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFunctionDescription(String value) {
        this.functionDescription = value;
    }

    /**
     * Gets the value of the attachGMPComplaince property.
     * 
     */
    public int getAttachGMPComplaince() {
        return attachGMPComplaince;
    }

    /**
     * Sets the value of the attachGMPComplaince property.
     * 
     */
    public void setAttachGMPComplaince(int value) {
        this.attachGMPComplaince = value;
    }

    /**
     * Gets the value of the eudraGMPAuthReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEudraGMPAuthReference() {
        return eudraGMPAuthReference;
    }

    /**
     * Sets the value of the eudraGMPAuthReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEudraGMPAuthReference(String value) {
        this.eudraGMPAuthReference = value;
    }

    /**
     * Gets the value of the attachGmpCertificate property.
     * 
     */
    public int getAttachGmpCertificate() {
        return attachGmpCertificate;
    }

    /**
     * Sets the value of the attachGmpCertificate property.
     * 
     */
    public void setAttachGmpCertificate(int value) {
        this.attachGmpCertificate = value;
    }

    /**
     * Gets the value of the gmpc property.
     * 
     * @return
     *     possible object is
     *     {@link Gmpc }
     *     
     */
    public Gmpc getGmpc() {
        return gmpc;
    }

    /**
     * Sets the value of the gmpc property.
     * 
     * @param value
     *     allowed object is
     *     {@link Gmpc }
     *     
     */
    public void setGmpc(Gmpc value) {
        this.gmpc = value;
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
     *         &lt;element name="control-test" type="{http://www.ema.eaf/dictionary/}control-term"/>
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
        "controlTest"
    })
    public static class ControlTests {

        @XmlElement(name = "control-test", required = true)
        protected String controlTest;

        /**
         * Gets the value of the controlTest property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getControlTest() {
            return controlTest;
        }

        /**
         * Sets the value of the controlTest property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setControlTest(String value) {
            this.controlTest = value;
        }

    }

}
