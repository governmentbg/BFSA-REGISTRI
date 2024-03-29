
package eu.europa.ema.v1_26.client.schema.eafws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for procedureTypesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="procedureTypesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="procedureType" type="{http://ema.europa.eu/schema/eafws}procedureTypeEnum" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "procedureTypesType", propOrder = {
    "procedureType"
})
public class ProcedureTypesType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected List<ProcedureTypeEnum> procedureType;

    /**
     * Gets the value of the procedureType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the procedureType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcedureType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcedureTypeEnum }
     * 
     * 
     */
    public List<ProcedureTypeEnum> getProcedureType() {
        if (procedureType == null) {
            procedureType = new ArrayList<ProcedureTypeEnum>();
        }
        return this.procedureType;
    }

}
