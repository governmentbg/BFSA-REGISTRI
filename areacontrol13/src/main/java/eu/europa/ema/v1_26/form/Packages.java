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
 * <p>Java class for packages complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="packages">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="package" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="package-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="package-size" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="package-description" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                   &lt;element name="containers">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="container" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="shelf-life" type="{http://www.ema.eaf/dictionary/}shelf-life" maxOccurs="unbounded"/>
 *                                       &lt;element name="container-details" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="material" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="container-type" type="{http://www.ema.eaf/dictionary/}control-term"/>
 *                                                 &lt;element name="closure-material" type="{http://www.ema.eaf/dictionary/}control-term"/>
 *                                                 &lt;element name="administrative-device" type="{http://www.ema.eaf/dictionary/}control-term"/>
 *                                                 &lt;element name="container-material" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                                                 &lt;element name="closure" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                                                 &lt;element name="content-of-container" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                                                 &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *                                               &lt;/sequence>
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="attachment" type="{http://www.ema.eaf/dictionary/}attachment"/>
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
@XmlType(name = "packages", propOrder = {
    "_package"
})
public class Packages {

    @XmlElement(name = "package")
    protected List<Packages.Package> _package;

    /**
     * Gets the value of the package property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the package property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPackage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Packages.Package }
     * 
     * 
     */
    public List<Packages.Package> getPackage() {
        if (_package == null) {
            _package = new ArrayList<Packages.Package>();
        }
        return this._package;
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
     *         &lt;element name="package-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="package-size" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="package-description" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *         &lt;element name="containers">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="container" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="shelf-life" type="{http://www.ema.eaf/dictionary/}shelf-life" maxOccurs="unbounded"/>
     *                             &lt;element name="container-details" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="material" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="container-type" type="{http://www.ema.eaf/dictionary/}control-term"/>
     *                                       &lt;element name="closure-material" type="{http://www.ema.eaf/dictionary/}control-term"/>
     *                                       &lt;element name="administrative-device" type="{http://www.ema.eaf/dictionary/}control-term"/>
     *                                       &lt;element name="container-material" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                                       &lt;element name="closure" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                                       &lt;element name="content-of-container" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *                                       &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
     *                                     &lt;/sequence>
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
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
     *         &lt;element name="attachment" type="{http://www.ema.eaf/dictionary/}attachment"/>
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
        "packageName",
        "packageSize",
        "packageDescription",
        "containers",
        "attachment"
    })
    public static class Package {

        @XmlElement(name = "package-name", required = true)
        protected String packageName;
        @XmlElement(name = "package-size", required = true)
        protected List<Packages.Package.PackageSize> packageSize;
        @XmlElement(name = "package-description", required = true)
        protected String packageDescription;
        @XmlElement(required = true)
        protected Packages.Package.Containers containers;
        @XmlElement(required = true)
        protected Attachment attachment;

        /**
         * Gets the value of the packageName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPackageName() {
            return packageName;
        }

        /**
         * Sets the value of the packageName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPackageName(String value) {
            this.packageName = value;
        }

        /**
         * Gets the value of the packageSize property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the packageSize property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPackageSize().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Packages.Package.PackageSize }
         * 
         * 
         */
        public List<Packages.Package.PackageSize> getPackageSize() {
            if (packageSize == null) {
                packageSize = new ArrayList<Packages.Package.PackageSize>();
            }
            return this.packageSize;
        }

        /**
         * Gets the value of the packageDescription property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPackageDescription() {
            return packageDescription;
        }

        /**
         * Sets the value of the packageDescription property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setPackageDescription(String value) {
            this.packageDescription = value;
        }

        /**
         * Gets the value of the containers property.
         * 
         * @return
         *     possible object is
         *     {@link Packages.Package.Containers }
         *     
         */
        public Packages.Package.Containers getContainers() {
            return containers;
        }

        /**
         * Sets the value of the containers property.
         * 
         * @param value
         *     allowed object is
         *     {@link Packages.Package.Containers }
         *     
         */
        public void setContainers(Packages.Package.Containers value) {
            this.containers = value;
        }

        /**
         * Gets the value of the attachment property.
         * 
         * @return
         *     possible object is
         *     {@link Attachment }
         *     
         */
        public Attachment getAttachment() {
            return attachment;
        }

        /**
         * Sets the value of the attachment property.
         * 
         * @param value
         *     allowed object is
         *     {@link Attachment }
         *     
         */
        public void setAttachment(Attachment value) {
            this.attachment = value;
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
         *         &lt;element name="container" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="shelf-life" type="{http://www.ema.eaf/dictionary/}shelf-life" maxOccurs="unbounded"/>
         *                   &lt;element name="container-details" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="material" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="container-type" type="{http://www.ema.eaf/dictionary/}control-term"/>
         *                             &lt;element name="closure-material" type="{http://www.ema.eaf/dictionary/}control-term"/>
         *                             &lt;element name="administrative-device" type="{http://www.ema.eaf/dictionary/}control-term"/>
         *                             &lt;element name="container-material" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *                             &lt;element name="closure" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *                             &lt;element name="content-of-container" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
         *                             &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
        @XmlType(name = "", propOrder = {
            "container"
        })
        public static class Containers {

            @XmlElement(required = true)
            protected List<Packages.Package.Containers.Container> container;

            /**
             * Gets the value of the container property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the container property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getContainer().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Packages.Package.Containers.Container }
             * 
             * 
             */
            public List<Packages.Package.Containers.Container> getContainer() {
                if (container == null) {
                    container = new ArrayList<Packages.Package.Containers.Container>();
                }
                return this.container;
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
             *         &lt;element name="shelf-life" type="{http://www.ema.eaf/dictionary/}shelf-life" maxOccurs="unbounded"/>
             *         &lt;element name="container-details" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="material" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="container-type" type="{http://www.ema.eaf/dictionary/}control-term"/>
             *                   &lt;element name="closure-material" type="{http://www.ema.eaf/dictionary/}control-term"/>
             *                   &lt;element name="administrative-device" type="{http://www.ema.eaf/dictionary/}control-term"/>
             *                   &lt;element name="container-material" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
             *                   &lt;element name="closure" type="{http://www.w3.org/2001/XMLSchema}string"/>
             *                   &lt;element name="content-of-container" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
             *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
                "shelfLife",
                "containerDetails"
            })
            public static class Container {

                @XmlElement(name = "shelf-life", required = true)
                protected List<ShelfLife> shelfLife;
                @XmlElement(name = "container-details", required = true)
                protected List<Packages.Package.Containers.Container.ContainerDetails> containerDetails;

                /**
                 * Gets the value of the shelfLife property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the shelfLife property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getShelfLife().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link ShelfLife }
                 * 
                 * 
                 */
                public List<ShelfLife> getShelfLife() {
                    if (shelfLife == null) {
                        shelfLife = new ArrayList<ShelfLife>();
                    }
                    return this.shelfLife;
                }

                /**
                 * Gets the value of the containerDetails property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the containerDetails property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getContainerDetails().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Packages.Package.Containers.Container.ContainerDetails }
                 * 
                 * 
                 */
                public List<Packages.Package.Containers.Container.ContainerDetails> getContainerDetails() {
                    if (containerDetails == null) {
                        containerDetails = new ArrayList<Packages.Package.Containers.Container.ContainerDetails>();
                    }
                    return this.containerDetails;
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
                 *         &lt;element name="material" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="container-type" type="{http://www.ema.eaf/dictionary/}control-term"/>
                 *         &lt;element name="closure-material" type="{http://www.ema.eaf/dictionary/}control-term"/>
                 *         &lt;element name="administrative-device" type="{http://www.ema.eaf/dictionary/}control-term"/>
                 *         &lt;element name="container-material" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
                 *         &lt;element name="closure" type="{http://www.w3.org/2001/XMLSchema}string"/>
                 *         &lt;element name="content-of-container" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
                 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
                    "material",
                    "containerType",
                    "closureMaterial",
                    "administrativeDevice",
                    "containerMaterial",
                    "closure",
                    "contentOfContainer",
                    "description"
                })
                public static class ContainerDetails {

                    @XmlElement(required = true)
                    protected String material;
                    @XmlElement(name = "container-type", required = true)
                    protected String containerType;
                    @XmlElement(name = "closure-material", required = true)
                    protected String closureMaterial;
                    @XmlElement(name = "administrative-device", required = true)
                    protected String administrativeDevice;
                    @XmlElement(name = "container-material")
                    protected String containerMaterial;
                    @XmlElement(required = true)
                    protected String closure;
                    @XmlElement(name = "content-of-container", required = true)
                    protected List<String> contentOfContainer;
                    @XmlElement(required = true)
                    protected Object description;

                    /**
                     * Gets the value of the material property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getMaterial() {
                        return material;
                    }

                    /**
                     * Sets the value of the material property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setMaterial(String value) {
                        this.material = value;
                    }

                    /**
                     * Gets the value of the containerType property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getContainerType() {
                        return containerType;
                    }

                    /**
                     * Sets the value of the containerType property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setContainerType(String value) {
                        this.containerType = value;
                    }

                    /**
                     * Gets the value of the closureMaterial property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getClosureMaterial() {
                        return closureMaterial;
                    }

                    /**
                     * Sets the value of the closureMaterial property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setClosureMaterial(String value) {
                        this.closureMaterial = value;
                    }

                    /**
                     * Gets the value of the administrativeDevice property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getAdministrativeDevice() {
                        return administrativeDevice;
                    }

                    /**
                     * Sets the value of the administrativeDevice property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setAdministrativeDevice(String value) {
                        this.administrativeDevice = value;
                    }

                    /**
                     * Gets the value of the containerMaterial property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getContainerMaterial() {
                        return containerMaterial;
                    }

                    /**
                     * Sets the value of the containerMaterial property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setContainerMaterial(String value) {
                        this.containerMaterial = value;
                    }

                    /**
                     * Gets the value of the closure property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getClosure() {
                        return closure;
                    }

                    /**
                     * Sets the value of the closure property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setClosure(String value) {
                        this.closure = value;
                    }

                    /**
                     * Gets the value of the contentOfContainer property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the contentOfContainer property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getContentOfContainer().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link String }
                     * 
                     * 
                     */
                    public List<String> getContentOfContainer() {
                        if (contentOfContainer == null) {
                            contentOfContainer = new ArrayList<String>();
                        }
                        return this.contentOfContainer;
                    }

                    /**
                     * Gets the value of the description property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Object }
                     *     
                     */
                    public Object getDescription() {
                        return description;
                    }

                    /**
                     * Sets the value of the description property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Object }
                     *     
                     */
                    public void setDescription(Object value) {
                        this.description = value;
                    }

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
         *         &lt;element name="size" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "size"
        })
        public static class PackageSize {

            @XmlElement(required = true)
            protected String size;

            /**
             * Gets the value of the size property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSize() {
                return size;
            }

            /**
             * Sets the value of the size property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSize(String value) {
                this.size = value;
            }

        }

    }

}
