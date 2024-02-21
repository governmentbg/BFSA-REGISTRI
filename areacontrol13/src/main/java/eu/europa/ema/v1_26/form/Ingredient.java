//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.08.09 at 12:11:17 PM EEST 
//


package eu.europa.ema.v1_26.form;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ingredient complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ingredient">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ingredient-role" type="{http://www.ema.eaf/dictionary/}control-term"/>
 *         &lt;element name="substance-name" type="{http://www.ema.eaf/dictionary/}substance"/>
 *         &lt;element name="ingredient-strength" type="{http://www.ema.eaf/dictionary/}ingredient-strength" minOccurs="0"/>
 *         &lt;element name="moiety-substance-name" type="{http://www.ema.eaf/dictionary/}substance"/>
 *         &lt;element name="moiety-ingredient-strength" type="{http://www.ema.eaf/dictionary/}ingredient-strength" minOccurs="0"/>
 *         &lt;element name="reference-monograph-standard" type="{http://www.ema.eaf/dictionary/}string250" minOccurs="0"/>
 *         &lt;element name="reference-monograph-standard-base" type="{http://www.ema.eaf/dictionary/}string250" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ingredient", propOrder = {
    "ingredientRole",
    "substanceName",
    "ingredientStrength",
    "moietySubstanceName",
    "moietyIngredientStrength",
    "referenceMonographStandard",
    "referenceMonographStandardBase"
})
public class Ingredient {

    @XmlElement(name = "ingredient-role", required = true)
    protected String ingredientRole;
    @XmlElement(name = "substance-name", required = true)
    protected Substance substanceName;
    @XmlElement(name = "ingredient-strength")
    protected IngredientStrength ingredientStrength;
    @XmlElement(name = "moiety-substance-name", required = true)
    protected Substance moietySubstanceName;
    @XmlElement(name = "moiety-ingredient-strength")
    protected IngredientStrength moietyIngredientStrength;
    @XmlElement(name = "reference-monograph-standard")
    protected Object referenceMonographStandard;
    @XmlElement(name = "reference-monograph-standard-base")
    protected String referenceMonographStandardBase;

    /**
     * Gets the value of the ingredientRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIngredientRole() {
        return ingredientRole;
    }

    /**
     * Sets the value of the ingredientRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIngredientRole(String value) {
        this.ingredientRole = value;
    }

    /**
     * Gets the value of the substanceName property.
     * 
     * @return
     *     possible object is
     *     {@link Substance }
     *     
     */
    public Substance getSubstanceName() {
        return substanceName;
    }

    /**
     * Sets the value of the substanceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link Substance }
     *     
     */
    public void setSubstanceName(Substance value) {
        this.substanceName = value;
    }

    /**
     * Gets the value of the ingredientStrength property.
     * 
     * @return
     *     possible object is
     *     {@link IngredientStrength }
     *     
     */
    public IngredientStrength getIngredientStrength() {
        return ingredientStrength;
    }

    /**
     * Sets the value of the ingredientStrength property.
     * 
     * @param value
     *     allowed object is
     *     {@link IngredientStrength }
     *     
     */
    public void setIngredientStrength(IngredientStrength value) {
        this.ingredientStrength = value;
    }

    /**
     * Gets the value of the moietySubstanceName property.
     * 
     * @return
     *     possible object is
     *     {@link Substance }
     *     
     */
    public Substance getMoietySubstanceName() {
        return moietySubstanceName;
    }

    /**
     * Sets the value of the moietySubstanceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link Substance }
     *     
     */
    public void setMoietySubstanceName(Substance value) {
        this.moietySubstanceName = value;
    }

    /**
     * Gets the value of the moietyIngredientStrength property.
     * 
     * @return
     *     possible object is
     *     {@link IngredientStrength }
     *     
     */
    public IngredientStrength getMoietyIngredientStrength() {
        return moietyIngredientStrength;
    }

    /**
     * Sets the value of the moietyIngredientStrength property.
     * 
     * @param value
     *     allowed object is
     *     {@link IngredientStrength }
     *     
     */
    public void setMoietyIngredientStrength(IngredientStrength value) {
        this.moietyIngredientStrength = value;
    }

    /**
     * Gets the value of the referenceMonographStandard property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Object getReferenceMonographStandard() {
        return referenceMonographStandard;
    }

    /**
     * Sets the value of the referenceMonographStandard property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceMonographStandard(Object value) {
        this.referenceMonographStandard = value;
    }

    /**
     * Gets the value of the referenceMonographStandardBase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceMonographStandardBase() {
        return referenceMonographStandardBase;
    }

    /**
     * Sets the value of the referenceMonographStandardBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceMonographStandardBase(String value) {
        this.referenceMonographStandardBase = value;
    }

}
