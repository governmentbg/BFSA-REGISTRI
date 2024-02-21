
package eu.europa.ema.v1_26.client.eaf.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import eu.europa.ema.v1_26.client.schema.eafws.TypesOfChangesType;


/**
 * <p>Java class for getTypesOfChangesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTypesOfChangesResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="typesOfChanges" type="{http://ema.europa.eu/schema/eafws}typesOfChangesType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTypesOfChangesResponse", propOrder = {
    "typesOfChanges"
})
public class GetTypesOfChangesResponse {

    protected TypesOfChangesType typesOfChanges;

    /**
     * Gets the value of the typesOfChanges property.
     * 
     * @return
     *     possible object is
     *     {@link TypesOfChangesType }
     *     
     */
    public TypesOfChangesType getTypesOfChanges() {
        return typesOfChanges;
    }

    /**
     * Sets the value of the typesOfChanges property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypesOfChangesType }
     *     
     */
    public void setTypesOfChanges(TypesOfChangesType value) {
        this.typesOfChanges = value;
    }

}
