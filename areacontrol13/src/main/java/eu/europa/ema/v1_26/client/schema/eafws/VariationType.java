
package eu.europa.ema.v1_26.client.schema.eafws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for variationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="variationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="identifier" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="shortName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="termName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="termDescription" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="selectable" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="article5" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="procedureTypes" type="{http://ema.europa.eu/schema/eafws}procedureTypesType"/&gt;
 *         &lt;element name="conditions" type="{http://ema.europa.eu/schema/eafws}conditionsType" minOccurs="0"/&gt;
 *         &lt;element name="documentations" type="{http://ema.europa.eu/schema/eafws}documentationsType" minOccurs="0"/&gt;
 *         &lt;element name="hierarchy" type="{http://ema.europa.eu/schema/eafws}hierarchyType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "variationType", propOrder = {
    "identifier",
    "shortName",
    "termName",
    "termDescription",
    "selectable",
    "article5",
    "procedureTypes",
    "conditions",
    "documentations",
    "hierarchy"
})
public class VariationType {

    @XmlElement(required = true)
    protected String identifier;
    @XmlElement(required = true)
    protected String shortName;
    @XmlElement(required = true)
    protected String termName;
    @XmlElement(required = true)
    protected String termDescription;
    protected boolean selectable;
    protected boolean article5;
    @XmlElement(required = true)
    protected ProcedureTypesType procedureTypes;
    protected ConditionsType conditions;
    protected DocumentationsType documentations;
    @XmlElement(required = true)
    protected HierarchyType hierarchy;

    /**
     * Gets the value of the identifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentifier(String value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the shortName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the value of the shortName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

    /**
     * Gets the value of the termName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTermName() {
        return termName;
    }

    /**
     * Sets the value of the termName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTermName(String value) {
        this.termName = value;
    }

    /**
     * Gets the value of the termDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTermDescription() {
        return termDescription;
    }

    /**
     * Sets the value of the termDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTermDescription(String value) {
        this.termDescription = value;
    }

    /**
     * Gets the value of the selectable property.
     * 
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * Sets the value of the selectable property.
     * 
     */
    public void setSelectable(boolean value) {
        this.selectable = value;
    }

    /**
     * Gets the value of the article5 property.
     * 
     */
    public boolean isArticle5() {
        return article5;
    }

    /**
     * Sets the value of the article5 property.
     * 
     */
    public void setArticle5(boolean value) {
        this.article5 = value;
    }

    /**
     * Gets the value of the procedureTypes property.
     * 
     * @return
     *     possible object is
     *     {@link ProcedureTypesType }
     *     
     */
    public ProcedureTypesType getProcedureTypes() {
        return procedureTypes;
    }

    /**
     * Sets the value of the procedureTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcedureTypesType }
     *     
     */
    public void setProcedureTypes(ProcedureTypesType value) {
        this.procedureTypes = value;
    }

    /**
     * Gets the value of the conditions property.
     * 
     * @return
     *     possible object is
     *     {@link ConditionsType }
     *     
     */
    public ConditionsType getConditions() {
        return conditions;
    }

    /**
     * Sets the value of the conditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConditionsType }
     *     
     */
    public void setConditions(ConditionsType value) {
        this.conditions = value;
    }

    /**
     * Gets the value of the documentations property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentationsType }
     *     
     */
    public DocumentationsType getDocumentations() {
        return documentations;
    }

    /**
     * Sets the value of the documentations property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentationsType }
     *     
     */
    public void setDocumentations(DocumentationsType value) {
        this.documentations = value;
    }

    /**
     * Gets the value of the hierarchy property.
     * 
     * @return
     *     possible object is
     *     {@link HierarchyType }
     *     
     */
    public HierarchyType getHierarchy() {
        return hierarchy;
    }

    /**
     * Sets the value of the hierarchy property.
     * 
     * @param value
     *     allowed object is
     *     {@link HierarchyType }
     *     
     */
    public void setHierarchy(HierarchyType value) {
        this.hierarchy = value;
    }

}
