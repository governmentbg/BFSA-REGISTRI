
package eu.europa.ema.v1_26.client.eaf.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getListModDate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getListModDate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="listXml" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getListModDate", propOrder = {
    "listXml"
})
public class GetListModDate {

    @XmlElement(namespace = "http://services.eaf.ema.europa.eu/")
    protected String listXml;

    /**
     * Gets the value of the listXml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListXml() {
        return listXml;
    }

    /**
     * Sets the value of the listXml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListXml(String value) {
        this.listXml = value;
    }

}
