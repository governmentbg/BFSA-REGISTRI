
package eu.europa.ema.v1_26.client.schema.eafws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.europa.ema.schema.eafws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TypesOfChanges_QNAME = new QName("http://ema.europa.eu/schema/eafws", "typesOfChanges");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.europa.ema.schema.eafws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TypesOfChangesType }
     * 
     */
    public TypesOfChangesType createTypesOfChangesType() {
        return new TypesOfChangesType();
    }

    /**
     * Create an instance of {@link ConditionsType }
     * 
     */
    public ConditionsType createConditionsType() {
        return new ConditionsType();
    }

    /**
     * Create an instance of {@link DocumentationsType }
     * 
     */
    public DocumentationsType createDocumentationsType() {
        return new DocumentationsType();
    }

    /**
     * Create an instance of {@link HierarchyLevelType }
     * 
     */
    public HierarchyLevelType createHierarchyLevelType() {
        return new HierarchyLevelType();
    }

    /**
     * Create an instance of {@link HierarchyType }
     * 
     */
    public HierarchyType createHierarchyType() {
        return new HierarchyType();
    }

    /**
     * Create an instance of {@link LevelType }
     * 
     */
    public LevelType createLevelType() {
        return new LevelType();
    }

    /**
     * Create an instance of {@link LevelsType }
     * 
     */
    public LevelsType createLevelsType() {
        return new LevelsType();
    }

    /**
     * Create an instance of {@link ProcedureTypesType }
     * 
     */
    public ProcedureTypesType createProcedureTypesType() {
        return new ProcedureTypesType();
    }

    /**
     * Create an instance of {@link TitleLinkType }
     * 
     */
    public TitleLinkType createTitleLinkType() {
        return new TitleLinkType();
    }

    /**
     * Create an instance of {@link VariationType }
     * 
     */
    public VariationType createVariationType() {
        return new VariationType();
    }

    /**
     * Create an instance of {@link VariationsType }
     * 
     */
    public VariationsType createVariationsType() {
        return new VariationsType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TypesOfChangesType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TypesOfChangesType }{@code >}
     */
    @XmlElementDecl(namespace = "http://ema.europa.eu/schema/eafws", name = "typesOfChanges")
    public JAXBElement<TypesOfChangesType> createTypesOfChanges(TypesOfChangesType value) {
        return new JAXBElement<TypesOfChangesType>(_TypesOfChanges_QNAME, TypesOfChangesType.class, null, value);
    }

}
