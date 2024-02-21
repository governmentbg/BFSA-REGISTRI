package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ApplicationS2711;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import generated.ServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2711DTO extends BaseApplicationDTO {
    private String facilityTypeCode;
    private String facilityTypeName;
    private AddressBO facilityAddress;
    private ContractorDTO ch83CertifiedPerson;
    @Builder.Default
    private List<PersonBO> ch83CertifiedPersons = new ArrayList<>();

    public static ApplicationS2711DTO of(final ApplicationS2711 source, Language language) {
        ApplicationS2711DTO dto = new ApplicationS2711DTO();
        dto.setServiceType(ServiceType.S2711);
        dto.setApplicationStatus(source.getStatus());
        dto.setFacilityAddress(source.getFacilityAddress());

        if (source.getCh83CertifiedPerson() != null) {
            dto.setCh83CertifiedPerson(ContractorDTO.of(source.getCh83CertifiedPerson(), language));
        }
        if (!CollectionUtils.isEmpty(source.getCh83CertifiedPersons())) {
            dto.getCh83CertifiedPersons().addAll(source.getCh83CertifiedPersons());
        }

        return dto;
    }

    public static List<ApplicationS2711DTO> of(final List<ApplicationS2711> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS2711DTO ofRecord(final Record source, Language language) {
        ApplicationS2711DTO dto = new ApplicationS2711DTO();

        dto.ofRecordBase(source, language);

        ApplicationS2711 application = source.getApplicationS2711();
        if (application != null) {
            dto.setApplicationStatus(application.getStatus());
            if (application.getFacilityType() != null) {
                dto.setFacilityTypeCode(application.getFacilityType().getCode());
                dto.setFacilityTypeName(application.getFacilityType().getI18n(language).getName());
            }
            dto.setFacilityAddress(application.getFacilityAddress());
            if (application.getCh83CertifiedPerson() != null) {
                dto.setCh83CertifiedPerson(ContractorDTO.of(application
                        .getCh83CertifiedPerson(), language));
            }
            if (!CollectionUtils.isEmpty(application.getCh83CertifiedPersons())) {
                dto.getCh83CertifiedPersons().addAll(application.getCh83CertifiedPersons());
            }
        }

        return dto;
    }

    public static ApplicationS2711DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2711DTO dto = new ApplicationS2711DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            if (findValue(sc, "facilityTypeCode") instanceof LinkedHashMap<?, ?> code) {
                dto.setFacilityTypeCode(code.get("value").toString());
            }
            if (findValue(sc, "facilityAddress") instanceof LinkedHashMap<?, ?> address) {
                dto.setFacilityAddress((getFacilityAddress(address)));
            }
            if (dto.getFacilityTypeCode().equals("02007")) {
                dto.setCh83CertifiedPerson(getCh83CertifiedPerson(sc));
            }
            dto.getCh83CertifiedPersons().addAll(getActivityPersons(sc));
        }

        return dto;
    }

    private static AddressBO getFacilityAddress(final Object addressObj) {
        AddressBO facilityAddress = null;

        if (addressObj instanceof LinkedHashMap<?, ?> address) {
            facilityAddress = new AddressBO();

            if (address.get("FullAddress") != null) {
                facilityAddress.setFullAddress(address.get("FullAddress").toString());
            }
            if (address.get("SettlementSelect") instanceof LinkedHashMap<?, ?> map) {
                facilityAddress.setSettlementName(map.get("settlementName").toString());
                facilityAddress.setSettlementCode(map.get("settlementCode").toString());
            }
        }
        return facilityAddress;
    }

    private static ContractorDTO getCh83CertifiedPerson(final LinkedHashMap<?, ?> personObj) {
        ContractorDTO controlPerson = new ContractorDTO();
        AddressDTO personAddress = new AddressDTO();

        if (personObj.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> person) {
            if (person.get("responsiblePersonIdentifier") != null) {
                controlPerson.setIdentifier(person.get("responsiblePersonIdentifier").toString());
            }
            if (person.get("responsiblePersonName") != null) {
                controlPerson.setName(person.get("responsiblePersonName").toString());
            }
            if (person.get("responsiblePersonSurname") != null) {
                controlPerson.setSurname(person.get("responsiblePersonSurname").toString());
            }
            if (person.get("responsiblePersonFamilyName") != null) {
                controlPerson.setFamilyName(person.get("responsiblePersonFamilyName").toString());
            }
        }

        if (personObj.get("responsiblePersonAddress") instanceof LinkedHashMap<?, ?> address) {
            if (address.get("FullAddress") != null) {
                personAddress.setFullAddress(address.get("FullAddress").toString());
            }
            if (address.get("SettlementSelect") instanceof LinkedHashMap<?, ?> code) {
                personAddress.setSettlementCode(code.get("settlementCode").toString());
                personAddress.setSettlementName(code.get("settlementName").toString());
            }
        }


        controlPerson.setAddress(personAddress);

        return controlPerson;
    }

    private static List<PersonBO> getActivityPersons(final LinkedHashMap<?, ?> sc) {
        List<PersonBO> activityPersons = new ArrayList<>();
        Object activityPersonsData = findValue(sc, "activityPersonsData");
        if (activityPersonsData instanceof LinkedHashMap<?, ?> person) {
            activityPersons.add(getActivityPerson(person));
        } else if (activityPersonsData instanceof ArrayList<?> persons) {
            for (Object person : persons) {
                activityPersons.add(getActivityPerson(person));
            }
        }
        return activityPersons;
    }

    private static PersonBO getActivityPerson(final Object activityPersonsData) {
        PersonBO personBO = null;
        if (activityPersonsData instanceof LinkedHashMap<?, ?> person) {
            personBO = new PersonBO();
            if (person.get("activityPersonIdentifier") != null) {
                personBO.setIdentifier(person.get("activityPersonIdentifier").toString());
            }
            if (person.get("activityPersonName") != null) {
                personBO.setName(person.get("activityPersonName").toString());
            }
            if (person.get("activityPersonSurname") != null) {
                personBO.setSurname(person.get("activityPersonSurname").toString());
            }
            if (person.get("activityPersonFamilyName") != null) {
                personBO.setFamilyName(person.get("activityPersonFamilyName").toString());
            }
        }
        return personBO;
    }
}
