
package eu.europa.ema.v1_26.client.eaf.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getTypesOfChanges complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTypesOfChanges"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="variationClassificationType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" form="qualified"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTypesOfChanges", propOrder = {
    "variationClassificationType"
})
public class GetTypesOfChanges {

    @XmlElement(namespace = "http://services.eaf.ema.europa.eu/")
    protected String variationClassificationType;

    /**
     * Gets the value of the variationClassificationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVariationClassificationType() {
        return variationClassificationType;
    }

    /**
     * Sets the value of the variationClassificationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVariationClassificationType(String value) {
        this.variationClassificationType = value;
    }

}
