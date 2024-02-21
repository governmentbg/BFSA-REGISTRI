
package eu.europa.ema.v1_26.client.schema.eafws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for procedureTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="procedureTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="typeIA"/&gt;
 *     &lt;enumeration value="typeIAIN"/&gt;
 *     &lt;enumeration value="typeIB"/&gt;
 *     &lt;enumeration value="typeIBun"/&gt;
 *     &lt;enumeration value="typeII"/&gt;
 *     &lt;enumeration value="typeIIart29"/&gt;
 *     &lt;enumeration value="E"/&gt;
 *     &lt;enumeration value="S"/&gt;
 *     &lt;enumeration value="R"/&gt;
 *     &lt;enumeration value="VNRA"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "procedureTypeEnum")
@XmlEnum
public enum ProcedureTypeEnum {

    @XmlEnumValue("typeIA")
    TYPE_IA("typeIA"),
    @XmlEnumValue("typeIAIN")
    TYPE_IAIN("typeIAIN"),
    @XmlEnumValue("typeIB")
    TYPE_IB("typeIB"),
    @XmlEnumValue("typeIBun")
    TYPE_I_BUN("typeIBun"),
    @XmlEnumValue("typeII")
    TYPE_II("typeII"),
    @XmlEnumValue("typeIIart29")
    TYPE_I_IART_29("typeIIart29"),
    E("E"),
    S("S"),
    R("R"),
    VNRA("VNRA");
    private final String value;

    ProcedureTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProcedureTypeEnum fromValue(String v) {
        for (ProcedureTypeEnum c: ProcedureTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
