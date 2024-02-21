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
 * <p>Java class for decision complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="decision">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="yes-no" type="{http://www.ema.eaf/dictionary/}yes-no"/>
 *         &lt;element name="class-waiver" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="class-waiver-number" type="{http://www.ema.eaf/dictionary/}string50"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="product-specific-decisions" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="product-specific-decision" type="{http://www.ema.eaf/dictionary/}string50"/>
 *                   &lt;element name="selected" type="{http://www.ema.eaf/dictionary/}yes-no"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="pip-decision-nums">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="decision-number" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="number" type="{http://www.ema.eaf/dictionary/}string50"/>
 *                             &lt;element name="selected" type="{http://www.ema.eaf/dictionary/}yes-no"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
@XmlType(name = "decision", propOrder = {
    "yesNo",
    "classWaiver",
    "productSpecificDecisions",
    "pipDecisionNums"
})
public class Decision {

    @XmlElement(name = "yes-no")
    protected int yesNo;
    @XmlElement(name = "class-waiver", required = true)
    protected List<Decision.ClassWaiver> classWaiver;
    @XmlElement(name = "product-specific-decisions", required = true)
    protected List<Decision.ProductSpecificDecisions> productSpecificDecisions;
    @XmlElement(name = "pip-decision-nums", required = true)
    protected Decision.PipDecisionNums pipDecisionNums;

    /**
     * Gets the value of the yesNo property.
     * 
     */
    public int getYesNo() {
        return yesNo;
    }

    /**
     * Sets the value of the yesNo property.
     * 
     */
    public void setYesNo(int value) {
        this.yesNo = value;
    }

    /**
     * Gets the value of the classWaiver property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classWaiver property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassWaiver().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Decision.ClassWaiver }
     * 
     * 
     */
    public List<Decision.ClassWaiver> getClassWaiver() {
        if (classWaiver == null) {
            classWaiver = new ArrayList<Decision.ClassWaiver>();
        }
        return this.classWaiver;
    }

    /**
     * Gets the value of the productSpecificDecisions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productSpecificDecisions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductSpecificDecisions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Decision.ProductSpecificDecisions }
     * 
     * 
     */
    public List<Decision.ProductSpecificDecisions> getProductSpecificDecisions() {
        if (productSpecificDecisions == null) {
            productSpecificDecisions = new ArrayList<Decision.ProductSpecificDecisions>();
        }
        return this.productSpecificDecisions;
    }

    /**
     * Gets the value of the pipDecisionNums property.
     * 
     * @return
     *     possible object is
     *     {@link Decision.PipDecisionNums }
     *     
     */
    public Decision.PipDecisionNums getPipDecisionNums() {
        return pipDecisionNums;
    }

    /**
     * Sets the value of the pipDecisionNums property.
     * 
     * @param value
     *     allowed object is
     *     {@link Decision.PipDecisionNums }
     *     
     */
    public void setPipDecisionNums(Decision.PipDecisionNums value) {
        this.pipDecisionNums = value;
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
     *         &lt;element name="class-waiver-number" type="{http://www.ema.eaf/dictionary/}string50"/>
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
        "classWaiverNumber"
    })
    public static class ClassWaiver {

        @XmlElement(name = "class-waiver-number", required = true)
        protected String classWaiverNumber;

        /**
         * Gets the value of the classWaiverNumber property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getClassWaiverNumber() {
            return classWaiverNumber;
        }

        /**
         * Sets the value of the classWaiverNumber property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setClassWaiverNumber(String value) {
            this.classWaiverNumber = value;
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
     *         &lt;element name="decision-number" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="number" type="{http://www.ema.eaf/dictionary/}string50"/>
     *                   &lt;element name="selected" type="{http://www.ema.eaf/dictionary/}yes-no"/>
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
    @XmlType(name = "", propOrder = {
        "decisionNumber"
    })
    public static class PipDecisionNums {

        @XmlElement(name = "decision-number", required = true)
        protected List<Decision.PipDecisionNums.DecisionNumber> decisionNumber;

        /**
         * Gets the value of the decisionNumber property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the decisionNumber property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDecisionNumber().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Decision.PipDecisionNums.DecisionNumber }
         * 
         * 
         */
        public List<Decision.PipDecisionNums.DecisionNumber> getDecisionNumber() {
            if (decisionNumber == null) {
                decisionNumber = new ArrayList<Decision.PipDecisionNums.DecisionNumber>();
            }
            return this.decisionNumber;
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
         *         &lt;element name="number" type="{http://www.ema.eaf/dictionary/}string50"/>
         *         &lt;element name="selected" type="{http://www.ema.eaf/dictionary/}yes-no"/>
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
            "number",
            "selected"
        })
        public static class DecisionNumber {

            @XmlElement(required = true)
            protected String number;
            protected int selected;

            /**
             * Gets the value of the number property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNumber() {
                return number;
            }

            /**
             * Sets the value of the number property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNumber(String value) {
                this.number = value;
            }

            /**
             * Gets the value of the selected property.
             * 
             */
            public int getSelected() {
                return selected;
            }

            /**
             * Sets the value of the selected property.
             * 
             */
            public void setSelected(int value) {
                this.selected = value;
            }

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
     *         &lt;element name="product-specific-decision" type="{http://www.ema.eaf/dictionary/}string50"/>
     *         &lt;element name="selected" type="{http://www.ema.eaf/dictionary/}yes-no"/>
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
        "productSpecificDecision",
        "selected"
    })
    public static class ProductSpecificDecisions {

        @XmlElement(name = "product-specific-decision", required = true)
        protected String productSpecificDecision;
        protected int selected;

        /**
         * Gets the value of the productSpecificDecision property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getProductSpecificDecision() {
            return productSpecificDecision;
        }

        /**
         * Sets the value of the productSpecificDecision property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setProductSpecificDecision(String value) {
            this.productSpecificDecision = value;
        }

        /**
         * Gets the value of the selected property.
         * 
         */
        public int getSelected() {
            return selected;
        }

        /**
         * Sets the value of the selected property.
         * 
         */
        public void setSelected(int value) {
            this.selected = value;
        }

    }

}
