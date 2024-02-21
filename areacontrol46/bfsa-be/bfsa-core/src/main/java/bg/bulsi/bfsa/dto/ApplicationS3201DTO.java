package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS3201;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import generated.ServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS3201DTO extends BaseApplicationDTO {

    private List<AddressBO> activityAddresses = new ArrayList<>();
    private List<KeyValueDTO> activityTypes = new ArrayList<>();
    private List<PlantProductBO> plantProducts = new ArrayList<>();
    private Boolean plantPassportIssue;
    private Boolean markingIssue;
    private List<ContactPersonBO> contactPersons;

    public static ApplicationS3201DTO ofRecord(final Record source, final Language language) {
        ApplicationS3201DTO dto = new ApplicationS3201DTO();

        dto.ofRecordBase(source, language);

        ApplicationS3201 application = source.getApplicationS3201();
        if (application != null) {
            dto.setActivityAddresses(application.getActivityAddresses());
            dto.setActivityTypes(application.getActivityTypes());
            dto.setPlantProducts(application.getPlantProducts());
            dto.setContactPersons(application.getContactPersons());
            dto.setPlantPassportIssue(application.getPlantPassportIssue());
            dto.setMarkingIssue(application.getMarkingIssue());
        }

        return dto;
    }

    public static ApplicationS3201DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS3201DTO dto = new ApplicationS3201DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setActivityTypes(getKeyValues(sc.get("activityTypeCodes")));
            if (sc.get("hasPlantPassport") instanceof LinkedHashMap<?,?> hasPlantPassport) {
                dto.setPlantPassportIssue("yes".equals(hasPlantPassport.get("value").toString()));
            }
            if (sc.get("hasPlantMarking") instanceof LinkedHashMap<?,?> hasPlantMarking) {
                dto.setMarkingIssue("yes".equals(hasPlantMarking.get("value").toString()));
            }
            dto.setContactPersons(getContactPersons(sc.get("contractorsData")));

            if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> additionalSpecificContent) {
                dto.setActivityAddresses(getActivityAddresses(additionalSpecificContent.get("activityFacilitiesdataGrid")));
                dto.setPlantProducts(getPlantProducts(additionalSpecificContent.get("applicationsDataGrid")));
            }
        }

        return dto;
    }

    private static List<ContactPersonBO> getContactPersons(Object object) {
        List<ContactPersonBO> contactPerson = null;
        if (object != null) {
            contactPerson = new ArrayList<>();
            if (object instanceof LinkedHashMap<?, ?> linkedHashMap) {
                contactPerson.add(getContactPerson(linkedHashMap));
            } else if (object instanceof ArrayList<?> arrayList) {
                for (Object o : arrayList) {
                    contactPerson.add(getContactPerson(o));
                }
            }
        }
        return contactPerson;
    }

    private static ContactPersonBO getContactPerson(Object object) {
        ContactPersonBO bo = null;
        if (object instanceof LinkedHashMap<?, ?> map) {
            bo = new ContactPersonBO();

            Object contractorIdentifier = map.get("contractorIdentifier");
            if (contractorIdentifier != null) {
                bo.setIdentifier(contractorIdentifier.toString());
            }
            Object contractorName = map.get("contractorName");
            if (contractorName != null) {
                bo.setFullName(contractorName.toString());
            }
            Object contractorFamilyName = map.get("contractorFamilyName");
            if (contractorFamilyName != null) {
                bo.setFullName(bo.getFullName() + " " + contractorFamilyName);
            }
            Object contractorPhoneNumber = map.get("contractorPhoneNumber");
            if (contractorPhoneNumber != null) {
                bo.setPhone(contractorPhoneNumber.toString());
            }
            Object applicantEmailAddress = map.get("applicantEmailAddress");
            if(applicantEmailAddress != null) {
                bo.setEmail(applicantEmailAddress.toString());
            }

            if (findValue(map, "contractorAddress") instanceof LinkedHashMap<?, ?> lhm) {
                Object fullAddress = lhm.get("FullAddress");
                if (fullAddress != null) {
                    bo.setFullAddress(fullAddress.toString());
                }
                Object locationName = lhm.get("LocationName");
                if (locationName != null) {
                    bo.setAddress(locationName.toString());
                }
                if (lhm.get("SettlementSelect") instanceof LinkedHashMap<?, ?> settlementSelect) {
                    Object settlementCode = settlementSelect.get("settlementCode");
                    if (settlementCode != null) {
                        bo.setSettlementCode(settlementCode.toString());
                    }
                    Object settlementName = settlementSelect.get("settlementName");
                    if (settlementName != null) {
                        bo.setSettlementName(settlementName.toString());
                    }
                }
            }
            if (findValue(map, "contactPersonType") instanceof LinkedHashMap<?, ?> contactPersonType) {
                Object code = contactPersonType.get("value");
                if (code != null) {
                    bo.setContactPersonTypeCode(code.toString());
                }
                Object name = contactPersonType.get("label");
                if (name != null) {
                    bo.setContactPersonTypeName(name.toString());
                }
            }
        }
        return bo;
    }

    private static List<PlantProductBO> getPlantProducts(Object object) {
        List<PlantProductBO> plantProducts = null;
        if (object != null) {
            plantProducts = new ArrayList<>();
            if (object instanceof LinkedHashMap<?, ?> linkedHashMap) {
                plantProducts.add(getPlantProduct(linkedHashMap));
            } else if (object instanceof ArrayList<?> arrayList) {
                for (Object o : arrayList) {
                    plantProducts.add(getPlantProduct(o));
                }
            }
        }
        return plantProducts;
    }

    private static PlantProductBO getPlantProduct(Object object) {
        PlantProductBO bo = null;
        if (object instanceof LinkedHashMap<?, ?> map && map.get("specificContent") instanceof LinkedHashMap<?,?> sc) {
            bo = new PlantProductBO();

            if (sc.get("test05300") instanceof LinkedHashMap<?, ?> lhm) {
                bo.setCultureGroupCode(lhm.get("value").toString());
                bo.setCultureGroupName(lhm.get("label").toString());
            }
            if (sc.get("cultureGroupOrigin") instanceof LinkedHashMap<?, ?> lhm) {
                bo.setOriginCode(lhm.get("value").toString());
                bo.setOriginName(lhm.get("label").toString());
            }
            if (sc.get("applicationGrainCultureCode") instanceof LinkedHashMap<?, ?> grainCultureCode) {
                String code = grainCultureCode.get("value").toString();//.substring(3);
                if (sc.get("applicationGrainCultureCode" + code + "select") instanceof LinkedHashMap<?, ?> lhm) {
                    bo.setProductCode(lhm.get("value").toString());
                    bo.setProductName(lhm.get("label").toString());
                }
            }
            Object cultureGroupPassportNumber = sc.get("cultureGroupPassportNumber");
            if (cultureGroupPassportNumber != null) {
                bo.setProductPassport(cultureGroupPassportNumber.toString());
            }
        }
        return bo;
    }

    private static List<AddressBO> getActivityAddresses(Object object) {
        List<AddressBO> addresses = null;
        if (object != null) {
            addresses = new ArrayList<>();
            if (object instanceof LinkedHashMap<?, ?> linkedHashMap) {
                addresses.add(getAddressBO(linkedHashMap));
            } else if (object instanceof ArrayList<?> arrayList) {
                for (Object o : arrayList) {
                    addresses.add(getAddressBO(o));
                }
            }
        }
        return addresses;
    }

    private static AddressBO getAddressBO(Object object) {
        AddressBO bo = null;
        if (object instanceof LinkedHashMap<?, ?> linkedHashMap
                && linkedHashMap.get("activityFacilitiesData") instanceof LinkedHashMap<?,?> activityFacilitiesData
                && activityFacilitiesData.get("facilityAddress") instanceof LinkedHashMap<?, ?> facilityAddress) {
            bo = new AddressBO();
            Object facilityDescription = findValue(activityFacilitiesData, "facilityDescription");
            if (facilityDescription != null) {
                bo.setDescription(facilityDescription.toString());
            }
            Object fullAddress = facilityAddress.get("FullAddress");
            if (fullAddress != null) {
                bo.setFullAddress(fullAddress.toString());
            }
            Object postCode = facilityAddress.get("PostCode");
            if (postCode != null) {
                bo.setPostCode(postCode.toString());
            }
            Object address = facilityAddress.get("LocationName");
            if (address != null) {
                bo.setAddress(address.toString());
            }
            if (facilityAddress.get("SettlementSelect") instanceof LinkedHashMap<?, ?> settlementSelect) {
                Object settlementCode = settlementSelect.get("settlementCode");
                if (settlementCode != null) {
                    bo.setSettlementCode(settlementCode.toString());
                }
                Object settlementName = settlementSelect.get("settlementName");
                if (settlementName != null) {
                    bo.setSettlementName(settlementName.toString());
                }
            }
            if (activityFacilitiesData.get("CountrySelect") instanceof LinkedHashMap<?, ?> countrySelect) {
                Object countryCode = countrySelect.get("countryCode");
                if (countryCode != null) {
                    bo.setCountryCode(countryCode.toString());
                }
                Object countryName = countrySelect.get("countryName");
                if (countryName != null) {
                    bo.setCountryName(countryName.toString());
                }
            }
        }
        return bo;
    }

}
