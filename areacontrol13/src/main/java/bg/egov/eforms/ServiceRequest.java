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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="requestURI" type="{http://eform.e-gov.bg/dictionary/Identifier}Identifier"/>
 *         &lt;element name="requestDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="dataProcessingAgreement" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="authorType" type="{http://eform.e-gov.bg/dictionary/Code}Code"/>
 *         &lt;element name="publicService" type="{http://eform.e-gov.bg/dictionary/PublicService}PublicService"/>
 *         &lt;element name="serviceProvider" type="{http://eform.e-gov.bg/dictionary/PublicOrganization}PublicOrganization"/>
 *         &lt;element name="requestType" type="{http://eform.e-gov.bg/dictionary/Code}Code"/>
 *         &lt;element name="resultChannel" type="{http://eform.e-gov.bg/dictionary/Channel}Channel"/>
 *         &lt;element name="processTime" type="{http://eform.e-gov.bg/dictionary/ProcessTime}ProcessTime"/>
 *         &lt;element name="tax" type="{http://eform.e-gov.bg/dictionary/Tax}Tax" minOccurs="0"/>
 *         &lt;element name="requestAuthor" type="{http://eform.e-gov.bg/dictionary/RequestAuthor}RequestAuthor"/>
 *         &lt;element name="applicant" type="{http://eform.e-gov.bg/dictionary/Agent}Agent"/>
 *         &lt;element name="specificContent" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="language" type="{http://eform.e-gov.bg/dictionary/Code}Code" maxOccurs="unbounded" minOccurs="0"/>
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
    "requestURI",
    "requestDateTime",
    "dataProcessingAgreement",
    "authorType",
    "publicService",
    "serviceProvider",
    "requestType",
    "resultChannel",
    "processTime",
    "tax",
    "requestAuthor",
    "applicant",
    "specificContent",
    "language"
})
@XmlRootElement(name = "ServiceRequest", namespace = "")
public class ServiceRequest {

    @XmlElement(required = true)
    protected Identifier requestURI;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestDateTime;
    protected boolean dataProcessingAgreement;
    @XmlElement(required = true)
    protected Code authorType;
    @XmlElement(required = true)
    protected PublicService publicService;
    @XmlElement(required = true)
    protected PublicOrganization serviceProvider;
    @XmlElement(required = true)
    protected Code requestType;
    @XmlElement(required = true)
    protected Channel resultChannel;
    @XmlElement(required = true)
    protected ProcessTime processTime;
    protected Tax tax;
    @XmlElement(required = true)
    protected RequestAuthor requestAuthor;
    @XmlElement(required = true)
    protected Agent applicant;
    protected Object specificContent;
    protected List<Code> language;

    /**
     * Gets the value of the requestURI property.
     * 
     * @return
     *     possible object is
     *     {@link Identifier }
     *     
     */
    public Identifier getRequestURI() {
        return requestURI;
    }

    /**
     * Sets the value of the requestURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link Identifier }
     *     
     */
    public void setRequestURI(Identifier value) {
        this.requestURI = value;
    }

    /**
     * Gets the value of the requestDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestDateTime() {
        return requestDateTime;
    }

    /**
     * Sets the value of the requestDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestDateTime(XMLGregorianCalendar value) {
        this.requestDateTime = value;
    }

    /**
     * Gets the value of the dataProcessingAgreement property.
     * 
     */
    public boolean isDataProcessingAgreement() {
        return dataProcessingAgreement;
    }

    /**
     * Sets the value of the dataProcessingAgreement property.
     * 
     */
    public void setDataProcessingAgreement(boolean value) {
        this.dataProcessingAgreement = value;
    }

    /**
     * Gets the value of the authorType property.
     * 
     * @return
     *     possible object is
     *     {@link Code }
     *     
     */
    public Code getAuthorType() {
        return authorType;
    }

    /**
     * Sets the value of the authorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Code }
     *     
     */
    public void setAuthorType(Code value) {
        this.authorType = value;
    }

    /**
     * Gets the value of the publicService property.
     * 
     * @return
     *     possible object is
     *     {@link PublicService }
     *     
     */
    public PublicService getPublicService() {
        return publicService;
    }

    /**
     * Sets the value of the publicService property.
     * 
     * @param value
     *     allowed object is
     *     {@link PublicService }
     *     
     */
    public void setPublicService(PublicService value) {
        this.publicService = value;
    }

    /**
     * Gets the value of the serviceProvider property.
     * 
     * @return
     *     possible object is
     *     {@link PublicOrganization }
     *     
     */
    public PublicOrganization getServiceProvider() {
        return serviceProvider;
    }

    /**
     * Sets the value of the serviceProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link PublicOrganization }
     *     
     */
    public void setServiceProvider(PublicOrganization value) {
        this.serviceProvider = value;
    }

    /**
     * Gets the value of the requestType property.
     * 
     * @return
     *     possible object is
     *     {@link Code }
     *     
     */
    public Code getRequestType() {
        return requestType;
    }

    /**
     * Sets the value of the requestType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Code }
     *     
     */
    public void setRequestType(Code value) {
        this.requestType = value;
    }

    /**
     * Gets the value of the resultChannel property.
     * 
     * @return
     *     possible object is
     *     {@link Channel }
     *     
     */
    public Channel getResultChannel() {
        return resultChannel;
    }

    /**
     * Sets the value of the resultChannel property.
     * 
     * @param value
     *     allowed object is
     *     {@link Channel }
     *     
     */
    public void setResultChannel(Channel value) {
        this.resultChannel = value;
    }

    /**
     * Gets the value of the processTime property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessTime }
     *     
     */
    public ProcessTime getProcessTime() {
        return processTime;
    }

    /**
     * Sets the value of the processTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessTime }
     *     
     */
    public void setProcessTime(ProcessTime value) {
        this.processTime = value;
    }

    /**
     * Gets the value of the tax property.
     * 
     * @return
     *     possible object is
     *     {@link Tax }
     *     
     */
    public Tax getTax() {
        return tax;
    }

    /**
     * Sets the value of the tax property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tax }
     *     
     */
    public void setTax(Tax value) {
        this.tax = value;
    }

    /**
     * Gets the value of the requestAuthor property.
     * 
     * @return
     *     possible object is
     *     {@link RequestAuthor }
     *     
     */
    public RequestAuthor getRequestAuthor() {
        return requestAuthor;
    }

    /**
     * Sets the value of the requestAuthor property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestAuthor }
     *     
     */
    public void setRequestAuthor(RequestAuthor value) {
        this.requestAuthor = value;
    }

    /**
     * Gets the value of the applicant property.
     * 
     * @return
     *     possible object is
     *     {@link Agent }
     *     
     */
    public Agent getApplicant() {
        return applicant;
    }

    /**
     * Sets the value of the applicant property.
     * 
     * @param value
     *     allowed object is
     *     {@link Agent }
     *     
     */
    public void setApplicant(Agent value) {
        this.applicant = value;
    }

    /**
     * Gets the value of the specificContent property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getSpecificContent() {
        return specificContent;
    }

    /**
     * Sets the value of the specificContent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setSpecificContent(Object value) {
        this.specificContent = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the language property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLanguage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Code }
     * 
     * 
     */
    public List<Code> getLanguage() {
        if (language == null) {
            language = new ArrayList<Code>();
        }
        return this.language;
    }

}
