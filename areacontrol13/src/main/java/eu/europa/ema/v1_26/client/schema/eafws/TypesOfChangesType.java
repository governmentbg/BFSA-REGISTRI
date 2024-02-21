
package eu.europa.ema.v1_26.client.schema.eafws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typesOfChangesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typesOfChangesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="variations" type="{http://ema.europa.eu/schema/eafws}variationsType"/&gt;
 *         &lt;element name="levels" type="{http://ema.europa.eu/schema/eafws}levelsType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typesOfChangesType", propOrder = {
    "variations",
    "levels"
})
public class TypesOfChangesType {

    @XmlElement(required = true)
    protected VariationsType variations;
    @XmlElement(required = true)
    protected LevelsType levels;

    /**
     * Gets the value of the variations property.
     * 
     * @return
     *     possible object is
     *     {@link VariationsType }
     *     
     */
    public VariationsType getVariations() {
        return variations;
    }

    /**
     * Sets the value of the variations property.
     * 
     * @param value
     *     allowed object is
     *     {@link VariationsType }
     *     
     */
    public void setVariations(VariationsType value) {
        this.variations = value;
    }

    /**
     * Gets the value of the levels property.
     * 
     * @return
     *     possible object is
     *     {@link LevelsType }
     *     
     */
    public LevelsType getLevels() {
        return levels;
    }

    /**
     * Sets the value of the levels property.
     * 
     * @param value
     *     allowed object is
     *     {@link LevelsType }
     *     
     */
    public void setLevels(LevelsType value) {
        this.levels = value;
    }

}
