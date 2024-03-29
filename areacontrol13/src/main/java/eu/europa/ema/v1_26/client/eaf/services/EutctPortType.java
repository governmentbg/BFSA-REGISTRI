package eu.europa.ema.v1_26.client.eaf.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * eAF Web App ver. 1.7.3.2 
 * 
 * Set of Web services to retrieve valid terms from controlled term lists managed by SPOR:
 * 
 * RMS (http://spor.ema.europa.eu/v1/lists) and 
 * 
 * OMS (http://spor.ema.europa.eu/v1/locations) and 
 * 
 * SMS (http://spor.emea.eu.int/eutct/)
 *
 * This class was generated by Apache CXF 3.3.1
 * 2023-08-09T12:38:40.030+03:00
 * Generated source version: 3.3.1
 *
 */
@WebService(targetNamespace = "http://services.eaf.ema.europa.eu/", name = "EutctPortType")
@XmlSeeAlso({ObjectFactory.class, eu.europa.ema.v1_26.client.schema.eafws.ObjectFactory.class})
public interface EutctPortType {

    /**
     * Returns the list of route of administration terms that do match the search string.
     * 
     * Search can be for either  Veterinary or Human terms
     */
    @WebMethod
    @RequestWrapper(localName = "searchATCcode", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchATCcode")
    @ResponseWrapper(localName = "searchATCcodeResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchATCcodeResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> searchATCcode(
        @WebParam(name = "searchText", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String searchText,
        @WebParam(name = "type", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String type
    );

    /**
     * Returns the list of manufactureActivities subset Function.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getManufacturerFunctions", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetManufacturerFunctions")
    @ResponseWrapper(localName = "getManufacturerFunctionsResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetManufacturerFunctionsResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getManufacturerFunctions();

    /**
     * Return the last Modified Date of RMS lists
     */
    @WebMethod
    @RequestWrapper(localName = "getListModDate", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetListModDate")
    @ResponseWrapper(localName = "getListModDateResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetListModDateResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getListModDate(
        @WebParam(name = "listXml", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String listXml
    );

    /**
     * According to the current legislation "residues of pharmacologically active substances" means all pharmacologically active substances expressed in mg/kg or &mu;g/kg on a fresh weight basis, whether active substances, excipients or degradation products and their metabolites which remains in food obtained from animals.
     * 
     * This list of provisions follows the results of Maximum Residue Limit (MRL) establishment and is mentioned in the Agency's opinion on the classification of substances prepared by the Committee for Veterinary Medicinal Products (CVMP).
     * 
     * This list reflects the current provisions published in the Annex of Commission Regulation (EU) No 37/2010.
     * 
     * This Controlled Term List is for Veterinary use only.
     */
    @WebMethod
    @RequestWrapper(localName = "getMRLProvision", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetMRLProvision")
    @ResponseWrapper(localName = "getMRLProvisionResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetMRLProvisionResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getMRLProvision();

    /**
     * Returns the list of manufactureActivities subset ControlTests.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getManufacturerControlTests", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetManufacturerControlTests")
    @ResponseWrapper(localName = "getManufacturerControlTestsResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetManufacturerControlTestsResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getManufacturerControlTests();

    /**
     * This Controlled Term List covers the tissue terms that are connected with centralised establishment of Maximum Residue Limits (MRL) (Community procedure) for pharmacologically active substances used in food producing  animals and with following determination of withdrawal periods for foodstuff of animal origin (edible Tissue) during marketing authorisations of products containing these substances.
     * This Controlled Term List is for Veterinary use only.
     */
    @WebMethod
    @RequestWrapper(localName = "getTissueMRL", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetTissueMRL")
    @ResponseWrapper(localName = "getTissueMRLResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetTissueMRLResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getTissueMRL();

    /**
     * Return the list of MRL Species. 
     * 
     * This Controlled Term List is for Veterinary use only.
     */
    @WebMethod
    @RequestWrapper(localName = "getMRLSpecies", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetMRLSpecies")
    @ResponseWrapper(localName = "getMRLSpeciesResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetMRLSpeciesResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getMRLSpecies(
        @WebParam(name = "type", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String type
    );

    /**
     * Return the list of Quantity Operators. 
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getStrengthOperators", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetStrengthOperators")
    @ResponseWrapper(localName = "getStrengthOperatorsResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetStrengthOperatorsResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getStrengthOperators();

    /**
     * Returns the list of substances that do match the search string.
     * 
     * The Substance Controlled Terms List is used in the EVMPD as a practical collection of examples as submitted inICSRs (Individual Case Safety Reports) and is neither an extensive scientific collection of substance nor comprehensive.
     * 
     * This Controlled Term List is for Human and Veterinary use. Value for argument 'type' should be one of 'human' or 'vet'.
     */
    @WebMethod
    @RequestWrapper(localName = "searchSubstances", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchSubstances")
    @ResponseWrapper(localName = "searchSubstancesResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchSubstancesResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> searchSubstances(
        @WebParam(name = "searchText", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String searchText,
        @WebParam(name = "type", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String type
    );

    /**
     * Returns the list of manufactureActivities subset Steps.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getManufacturerSteps", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetManufacturerSteps")
    @ResponseWrapper(localName = "getManufacturerStepsResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetManufacturerStepsResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getManufacturerSteps();

    /**
     * Return the symbol attribute in the Unit of Measurement list.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getStrengthUnits", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetStrengthUnits")
    @ResponseWrapper(localName = "getStrengthUnitsResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetStrengthUnitsResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getStrengthUnits();

    /**
     * Returns the list of EU countries excluding UK(NI) and EFTA countries.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getEuAndEftaCountriesExcludingUkNi", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEuAndEftaCountriesExcludingUkNi")
    @ResponseWrapper(localName = "getEuAndEftaCountriesExcludingUkNiResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEuAndEftaCountriesExcludingUkNiResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getEuAndEftaCountriesExcludingUkNi();

    /**
     * Returns the list of EEA countries.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getEEACountries", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEEACountries")
    @ResponseWrapper(localName = "getEEACountriesResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEEACountriesResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getEEACountries();

    /**
     * Return the terms from Substance Type list.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getSubstanceType", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetSubstanceType")
    @ResponseWrapper(localName = "getSubstanceTypeResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetSubstanceTypeResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getSubstanceType();

    /**
     * Returns the list of closures.
     * 
     * A closure is a means to close an immediate container for the purpose of the correct storage and use of the product.
     * 
     * This Controlled Term List is for Human and Veterinary use. Value for argument 'type' should be one of 'human' or 'vet'.
     */
    @WebMethod
    @RequestWrapper(localName = "getClosures", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetClosures")
    @ResponseWrapper(localName = "getClosuresResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetClosuresResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getClosures(
        @WebParam(name = "type", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String type
    );

    /**
     * Returns the list of administration devices.In some cases a special administration device is needed for the correct administration of the product. The administration device may be an integral part of the immediate container or closure.
     * 
     * This Controlled Term List is for Human and Veterinary use. Value for argument 'type' should be one of 'human' or 'vet'.
     */
    @WebMethod
    @RequestWrapper(localName = "getAdministrationDevices", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetAdministrationDevices")
    @ResponseWrapper(localName = "getAdministrationDevicesResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetAdministrationDevicesResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getAdministrationDevices(
        @WebParam(name = "type", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String type
    );

    /**
     * Return XML containing the location(s) of Organisation where the Organisation name + 
     * 
     *  match the search parameter and for a specific Country.
     */
    @WebMethod
    @RequestWrapper(localName = "searchOMSOrganisation", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSOrganisation")
    @ResponseWrapper(localName = "searchOMSOrganisationResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSOrganisationResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.lang.String searchOMSOrganisation(
        @WebParam(name = "searchText", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String searchText,
        @WebParam(name = "countryIdentifier", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String countryIdentifier
    );

    /**
     * Returns the list of Target species.
     * 
     * This list describes the species for which the veterinary medicinal products are intended for.
     * 
     * This Controlled Term List is for Veterinary use only.
     */
    @WebMethod
    @RequestWrapper(localName = "getTargetSpecies", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetTargetSpecies")
    @ResponseWrapper(localName = "getTargetSpeciesResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetTargetSpeciesResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getTargetSpecies(
        @WebParam(name = "application", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.util.List<java.lang.String> application
    );

    /**
     * Returns the list of EEA countries excluding UK(NI).
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getEeaCountriesExcludingUkNi", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEeaCountriesExcludingUkNi")
    @ResponseWrapper(localName = "getEeaCountriesExcludingUkNiResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEeaCountriesExcludingUkNiResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getEeaCountriesExcludingUkNi();

    /**
     * Returns the list of non EEA countries.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getNonEEACountries", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetNonEEACountries")
    @ResponseWrapper(localName = "getNonEEACountriesResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetNonEEACountriesResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getNonEEACountries();

    /**
     * Returns the list of pharmaceutical forms.
     * 
     * The Dosage Form consists of a combination of the form in which a medicinal product is presented (form of presentation) and the form in which it is administered, including the physical form (form of administration). In special cases (e.g. identical products which may be distinguished only by reference to the container), the information about the immediate container can be included in the pharmaceutical form, e.g. "solution for injection in pre-filled syringes". 
     * Moreover, due to the specificity of a medicinal product the complete characterisation of a pharmaceutical form may be constructed by using a combination of existing Standard Terms, e.g. "powder for solution for injection or infusion".
     * 
     * This Controlled Term List is for Human and Veterinary use. Value for argument 'type' should be one of 'human' or 'vet'.
     */
    @WebMethod
    @RequestWrapper(localName = "getPharmaForms", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetPharmaForms")
    @ResponseWrapper(localName = "getPharmaFormsResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetPharmaFormsResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getPharmaForms(
        @WebParam(name = "type", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String type
    );

    /**
     * This Controlled Term List covers the tissue terms that are connected with centralised establishment of Maximum Residue Limits (MRL) (Community procedure) for pharmacologically active substances used in food producing  animals and with following determination of withdrawal periods for foodstuff of animal origin (edible Tissue) during marketing authorisations of products containing these substances.
     * This Controlled Term List is for Veterinary use only.
     */
    @WebMethod
    @RequestWrapper(localName = "getEdibleTissue", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEdibleTissue")
    @ResponseWrapper(localName = "getEdibleTissueResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEdibleTissueResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getEdibleTissue();

    /**
     * Return XML containing the location(s) that match the OrganisationID. 
     * 
     * Organisation id format passed as parameter ORG-XXXXXXXXX (where X is numeric
     */
    @WebMethod
    @RequestWrapper(localName = "searchOMSOrganisationById", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSOrganisationById")
    @ResponseWrapper(localName = "searchOMSOrganisationByIdResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSOrganisationByIdResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.lang.String searchOMSOrganisationById(
        @WebParam(name = "searchText", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String searchText
    );

    /**
     * Returns the list of countries.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getCountries", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetCountries")
    @ResponseWrapper(localName = "getCountriesResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetCountriesResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getCountries();

    /**
     * Returns the list of route of administration terms that do match the search string.
     * 
     * Search can be for either  Veterinary or Human terms
     */
    @WebMethod
    @RequestWrapper(localName = "searchRoutesOfAdministration", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchRoutesOfAdministration")
    @ResponseWrapper(localName = "searchRoutesOfAdministrationResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchRoutesOfAdministrationResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> searchRoutesOfAdministration(
        @WebParam(name = "searchText", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String searchText,
        @WebParam(name = "type", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String type
    );

    /**
     * Returns the list of non EU & FreeTrade countries.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getEuAndFreeTradeCountries", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEuAndFreeTradeCountries")
    @ResponseWrapper(localName = "getEuAndFreeTradeCountriesResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEuAndFreeTradeCountriesResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getEuAndFreeTradeCountries();

    /**
     * Returns types of changes (variation scopes) based on the RMS Variation Classification list (https://spor.ema.europa.eu/v1/lists/100000152091)
     */
    @WebMethod
    @RequestWrapper(localName = "getTypesOfChanges", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetTypesOfChanges")
    @ResponseWrapper(localName = "getTypesOfChangesResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetTypesOfChangesResponse")
    @WebResult(name = "typesOfChanges", targetNamespace = "")
    public eu.europa.ema.v1_26.client.schema.eafws.TypesOfChangesType getTypesOfChanges(
        @WebParam(name = "variationClassificationType", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String variationClassificationType
    );

    /**
     * Return XML containing the location(s) of Organisation where the Organisation name + 
     * 
     *  match the search parameter and for a specific Country.The location instance(s) is the historic version
     */
    @WebMethod
    @RequestWrapper(localName = "searchOMSOrganisationHistory", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSOrganisationHistory")
    @ResponseWrapper(localName = "searchOMSOrganisationHistoryResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSOrganisationHistoryResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.lang.String searchOMSOrganisationHistory(
        @WebParam(name = "searchText", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String searchText,
        @WebParam(name = "countryIdentifier", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String countryIdentifier
    );

    /**
     * Returns the list of EEA countries including UK.
     * 
     * This Controlled Term List is for Human and Veterinary use.
     */
    @WebMethod
    @RequestWrapper(localName = "getEeaCountriesAndUkGb", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEeaCountriesAndUkGb")
    @ResponseWrapper(localName = "getEeaCountriesAndUkGbResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetEeaCountriesAndUkGbResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getEeaCountriesAndUkGb();

    /**
     * This list contains the description of the possible storage and/or transport conditions for a medicinal product under which it can be kept during its shelf life.
     * These are based on evaluation of the stability studies undertaken on the finished product. Details of the conditions recommended for these stability studies are included in the relevant CHMP/ICH Guidelines. The storage conditions must be possible for the user to follow and the statements are therefore restricted to those achievable in practice.
     * 
     * This Controlled Term List is for Human and Veterinary use. Value for argument 'type' should be one of 'human' or 'vet'.
     */
    @WebMethod
    @RequestWrapper(localName = "getSpecialPrecautionForStorage", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetSpecialPrecautionForStorage")
    @ResponseWrapper(localName = "getSpecialPrecautionForStorageResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetSpecialPrecautionForStorageResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getSpecialPrecautionForStorage(
        @WebParam(name = "type", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String type
    );

    /**
     * Returns the list of containers.
     * 
     * The pharmaceutical form is supplied in an immediate packaging, which is the container or other form of packaging immediately in contact with the product.
     * 
     * This Controlled Term List is for Human and Veterinary use. Value for argument 'type' should be one of 'human' or 'vet'.
     */
    @WebMethod
    @RequestWrapper(localName = "getContainers", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetContainers")
    @ResponseWrapper(localName = "getContainersResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetContainersResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.util.List<eu.europa.ema.v1_26.client.eaf.services.EafTermVo> getContainers(
        @WebParam(name = "type", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String type
    );

    /**
     * Return the version of the Form as configured.
     */
    @WebMethod
    @RequestWrapper(localName = "getVersion", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetVersion")
    @ResponseWrapper(localName = "getVersionResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetVersionResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.lang.String getVersion(
        @WebParam(name = "formType", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String formType
    );

    /**
     * Return XML containing the location(s) that match the OrganisationID. 
     * 
     * Organisation id format passed as parameter ORG-XXXXXXXXX (where X is numeric)The location instance(s) is the historic version
     */
    @WebMethod
    @RequestWrapper(localName = "searchOMSOrganisationHistoryById", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSOrganisationHistoryById")
    @ResponseWrapper(localName = "searchOMSOrganisationHistoryByIdResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSOrganisationHistoryByIdResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.lang.String searchOMSOrganisationHistoryById(
        @WebParam(name = "searchText", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String searchText
    );

    /**
     * Return XML containing the Single location that match the LocationID. 
     * 
     * Location id format passed as parameter LOC-XXXXXXXXX (where X is numeric
     */
    @WebMethod
    @RequestWrapper(localName = "searchOMSLocationById", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSLocationById")
    @ResponseWrapper(localName = "searchOMSLocationByIdResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSLocationByIdResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.lang.String searchOMSLocationById(
        @WebParam(name = "searchText", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String searchText
    );

    /**
     * Returns the version number of the latest pdf form of type 'formType' that has been deployed on the esubmissions web site
     */
    @WebMethod
    @RequestWrapper(localName = "getFormVersion", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetFormVersion")
    @ResponseWrapper(localName = "getFormVersionResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.GetFormVersionResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.lang.String getFormVersion(
        @WebParam(name = "formType", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String formType
    );

    /**
     * Return XML containing the Single location that match the LocationID. 
     * 
     * Location id format passed as parameter LOC-XXXXXXXXX (where X is numeric)The location instance(s) is the historic version
     */
    @WebMethod
    @RequestWrapper(localName = "searchOMSLocationHistoryById", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSLocationHistoryById")
    @ResponseWrapper(localName = "searchOMSLocationHistoryByIdResponse", targetNamespace = "http://services.eaf.ema.europa.eu/", className = "eu.europa.ema.v1_26.client.eaf.services.SearchOMSLocationHistoryByIdResponse")
    @WebResult(name = "return", targetNamespace = "")
    public java.lang.String searchOMSLocationHistoryById(
        @WebParam(name = "searchText", targetNamespace = "http://services.eaf.ema.europa.eu/")
        java.lang.String searchText
    );
}
