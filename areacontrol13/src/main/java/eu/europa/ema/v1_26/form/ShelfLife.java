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
 * <p>Java class for shelf-life complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="shelf-life">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shelf-life-after-open" type="{http://www.ema.eaf/dictionary/}string30"/>
 *         &lt;element name="shelf-life-after-open-units" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="shelf-life-after-dilution" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="shelf-life-after-dilution-units" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="shelf-life-before-open" type="{http://www.ema.eaf/dictionary/}string30"/>
 *         &lt;element name="shelf-life-before-open-units" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="spfs" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="special-precaution-for-storage" type="{http://www.ema.eaf/dictionary/}control-term"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="spfsao" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="special-precaution-for-storage-after-open" type="{http://www.ema.eaf/dictionary/}control-term"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="spfsadr" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="special-precaution-for-storage-after-dilution-reconstitution" type="{http://www.ema.eaf/dictionary/}control-term"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="shelf-life-duration" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "shelf-life", propOrder = {
    "shelfLifeAfterOpen",
    "shelfLifeAfterOpenUnits",
    "shelfLifeAfterDilution",
    "shelfLifeAfterDilutionUnits",
    "shelfLifeBeforeOpen",
    "shelfLifeBeforeOpenUnits",
    "spfs",
    "spfsao",
    "spfsadr",
    "shelfLifeDuration"
})
public class ShelfLife {

    @XmlElement(name = "shelf-life-after-open", required = true)
    protected String shelfLifeAfterOpen;
    @XmlElement(name = "shelf-life-after-open-units", required = true)
    protected String shelfLifeAfterOpenUnits;
    @XmlElement(name = "shelf-life-after-dilution", required = true)
    protected String shelfLifeAfterDilution;
    @XmlElement(name = "shelf-life-after-dilution-units", required = true)
    protected String shelfLifeAfterDilutionUnits;
    @XmlElement(name = "shelf-life-before-open", required = true)
    protected String shelfLifeBeforeOpen;
    @XmlElement(name = "shelf-life-before-open-units", required = true)
    protected String shelfLifeBeforeOpenUnits;
    @XmlElement(required = true)
    protected List<ShelfLife.Spfs> spfs;
    @XmlElement(required = true)
    protected List<ShelfLife.Spfsao> spfsao;
    @XmlElement(required = true)
    protected List<ShelfLife.Spfsadr> spfsadr;
    @XmlElement(name = "shelf-life-duration")
    protected String shelfLifeDuration;

    /**
     * Gets the value of the shelfLifeAfterOpen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShelfLifeAfterOpen() {
        return shelfLifeAfterOpen;
    }

    /**
     * Sets the value of the shelfLifeAfterOpen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShelfLifeAfterOpen(String value) {
        this.shelfLifeAfterOpen = value;
    }

    /**
     * Gets the value of the shelfLifeAfterOpenUnits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShelfLifeAfterOpenUnits() {
        return shelfLifeAfterOpenUnits;
    }

    /**
     * Sets the value of the shelfLifeAfterOpenUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShelfLifeAfterOpenUnits(String value) {
        this.shelfLifeAfterOpenUnits = value;
    }

    /**
     * Gets the value of the shelfLifeAfterDilution property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShelfLifeAfterDilution() {
        return shelfLifeAfterDilution;
    }

    /**
     * Sets the value of the shelfLifeAfterDilution property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShelfLifeAfterDilution(String value) {
        this.shelfLifeAfterDilution = value;
    }

    /**
     * Gets the value of the shelfLifeAfterDilutionUnits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShelfLifeAfterDilutionUnits() {
        return shelfLifeAfterDilutionUnits;
    }

    /**
     * Sets the value of the shelfLifeAfterDilutionUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShelfLifeAfterDilutionUnits(String value) {
        this.shelfLifeAfterDilutionUnits = value;
    }

    /**
     * Gets the value of the shelfLifeBeforeOpen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShelfLifeBeforeOpen() {
        return shelfLifeBeforeOpen;
    }

    /**
     * Sets the value of the shelfLifeBeforeOpen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShelfLifeBeforeOpen(String value) {
        this.shelfLifeBeforeOpen = value;
    }

    /**
     * Gets the value of the shelfLifeBeforeOpenUnits property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShelfLifeBeforeOpenUnits() {
        return shelfLifeBeforeOpenUnits;
    }

    /**
     * Sets the value of the shelfLifeBeforeOpenUnits property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShelfLifeBeforeOpenUnits(String value) {
        this.shelfLifeBeforeOpenUnits = value;
    }

    /**
     * Gets the value of the spfs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spfs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpfs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShelfLife.Spfs }
     * 
     * 
     */
    public List<ShelfLife.Spfs> getSpfs() {
        if (spfs == null) {
            spfs = new ArrayList<ShelfLife.Spfs>();
        }
        return this.spfs;
    }

    /**
     * Gets the value of the spfsao property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spfsao property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpfsao().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShelfLife.Spfsao }
     * 
     * 
     */
    public List<ShelfLife.Spfsao> getSpfsao() {
        if (spfsao == null) {
            spfsao = new ArrayList<ShelfLife.Spfsao>();
        }
        return this.spfsao;
    }

    /**
     * Gets the value of the spfsadr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spfsadr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpfsadr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShelfLife.Spfsadr }
     * 
     * 
     */
    public List<ShelfLife.Spfsadr> getSpfsadr() {
        if (spfsadr == null) {
            spfsadr = new ArrayList<ShelfLife.Spfsadr>();
        }
        return this.spfsadr;
    }

    /**
     * Gets the value of the shelfLifeDuration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShelfLifeDuration() {
        return shelfLifeDuration;
    }

    /**
     * Sets the value of the shelfLifeDuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShelfLifeDuration(String value) {
        this.shelfLifeDuration = value;
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
     *         &lt;element name="special-precaution-for-storage" type="{http://www.ema.eaf/dictionary/}control-term"/>
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
        "specialPrecautionForStorage"
    })
    public static class Spfs {

        @XmlElement(name = "special-precaution-for-storage", required = true)
        protected String specialPrecautionForStorage;

        /**
         * Gets the value of the specialPrecautionForStorage property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSpecialPrecautionForStorage() {
            return specialPrecautionForStorage;
        }

        /**
         * Sets the value of the specialPrecautionForStorage property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSpecialPrecautionForStorage(String value) {
            this.specialPrecautionForStorage = value;
        }

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
     *         &lt;element name="special-precaution-for-storage-after-dilution-reconstitution" type="{http://www.ema.eaf/dictionary/}control-term"/>
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
        "specialPrecautionForStorageAfterDilutionReconstitution"
    })
    public static class Spfsadr {

        @XmlElement(name = "special-precaution-for-storage-after-dilution-reconstitution", required = true)
        protected String specialPrecautionForStorageAfterDilutionReconstitution;

        /**
         * Gets the value of the specialPrecautionForStorageAfterDilutionReconstitution property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSpecialPrecautionForStorageAfterDilutionReconstitution() {
            return specialPrecautionForStorageAfterDilutionReconstitution;
        }

        /**
         * Sets the value of the specialPrecautionForStorageAfterDilutionReconstitution property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSpecialPrecautionForStorageAfterDilutionReconstitution(String value) {
            this.specialPrecautionForStorageAfterDilutionReconstitution = value;
        }

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
     *         &lt;element name="special-precaution-for-storage-after-open" type="{http://www.ema.eaf/dictionary/}control-term"/>
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
        "specialPrecautionForStorageAfterOpen"
    })
    public static class Spfsao {

        @XmlElement(name = "special-precaution-for-storage-after-open", required = true)
        protected String specialPrecautionForStorageAfterOpen;

        /**
         * Gets the value of the specialPrecautionForStorageAfterOpen property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSpecialPrecautionForStorageAfterOpen() {
            return specialPrecautionForStorageAfterOpen;
        }

        /**
         * Sets the value of the specialPrecautionForStorageAfterOpen property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSpecialPrecautionForStorageAfterOpen(String value) {
            this.specialPrecautionForStorageAfterOpen = value;
        }

    }

}