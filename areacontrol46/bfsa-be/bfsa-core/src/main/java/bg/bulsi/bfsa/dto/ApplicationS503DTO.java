package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ApplicationS503;
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
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS503DTO extends BaseApplicationDTO {

    private String settlementCode;
    private String address;
    private String fullAddress; // = info from settlementCode + " " + address
    private ContractorDTO ch83CertifiedPerson;
    @Builder.Default
    private List<PersonBO> ch83CertifiedPersons = new ArrayList<>();

    public static ApplicationS503DTO ofRecord(final Record source, Language language) {
        ApplicationS503DTO dto = new ApplicationS503DTO();

        dto.ofRecordBase(source, language);

        ApplicationS503 application = source.getApplicationS503();
        if (application != null) {
            dto.setApplicationStatus(application.getStatus());
            dto.setFullAddress(application.getFullAddress());
            if (application.getCh83CertifiedPerson() != null) {
                dto.setCh83CertifiedPerson(ContractorDTO.of(application
                        .getCh83CertifiedPerson(), language));
            }
            if (!CollectionUtils.isEmpty(application.getCh83CertifiedPersons())) {
                dto.setCh83CertifiedPersons(application.getCh83CertifiedPersons());
            }
        }

        return dto;
    }

    public static List<ApplicationS503DTO> ofRecord(final List<Record> source, final Language language) {
        return source.stream().map(r -> ofRecord(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS503DTO of(final ApplicationS503 source, Language language) {
        ApplicationS503DTO dto = new ApplicationS503DTO();
        dto.setServiceType(ServiceType.S503);
        dto.setApplicationStatus(source.getStatus());
        dto.setFullAddress(source.getFullAddress());

        if (source.getCh83CertifiedPerson() != null) {
            dto.setCh83CertifiedPerson(ContractorDTO.of(source.getCh83CertifiedPerson(), language));
        }
        if (!CollectionUtils.isEmpty(source.getCh83CertifiedPersons())) {
            dto.setCh83CertifiedPersons(source.getCh83CertifiedPersons());
        }

        return dto;
    }

    public static List<ApplicationS503DTO> of(final List<ApplicationS503> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS503DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS503DTO dto = new ApplicationS503DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            dto.setCh83CertifiedPerson(getResponsiblePerson(sc));
            dto.getCh83CertifiedPersons().addAll(getPerformingPersons(sc));
            Object code = sc.get("pppRepackagingFacility");
            if (code instanceof LinkedHashMap<?, ?> address) {
                dto.setSettlementCode(address.get("SettlementCode").toString());
                dto.setAddress(address.get("LocationName").toString());
                dto.setFullAddress(address.get("SettlementCode").toString()
                        + ", " + address.get("LocationName").toString());
            }
        }
        return dto;
    }

    private static ContractorDTO getResponsiblePerson(final LinkedHashMap<?, ?> person) {
        ContractorDTO controlPerson = new ContractorDTO();
        LinkedHashMap<?, ?> responsiblePerson = person;
        if (person.get("repackagingFacilityControlPersonIdentifier") == null) {
            if (person.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> p) {
                responsiblePerson = p;
            }
        }
        if (responsiblePerson.get("repackagingFacilityControlPersonIdentifier") != null) {
            controlPerson.setIdentifier(responsiblePerson.get("repackagingFacilityControlPersonIdentifier").toString());
        }
        if (responsiblePerson.get("repackagingFacilityControlPersonName") != null) {
            controlPerson.setName(responsiblePerson.get("repackagingFacilityControlPersonName").toString());
        }
        if (responsiblePerson.get("repackagingFacilityControlPersonSurname") != null) {
            controlPerson.setSurname(responsiblePerson.get("repackagingFacilityControlPersonSurname").toString());
        }
        if (responsiblePerson.get("repackagingFacilityControlPersonFamilyName") != null) {
            controlPerson.setFamilyName(responsiblePerson.get("repackagingFacilityControlPersonFamilyName").toString());
        }
        return controlPerson;
    }

    private static List<PersonBO> getPerformingPersons(final LinkedHashMap<?, ?> sc) {
        List<PersonBO> people = new ArrayList<>();
        Object contractorsData = findValue(sc, "pppRepackagingFacilityPerformingPersons");
        if (contractorsData instanceof LinkedHashMap<?, ?> person) {
            people.add(getPerformingPerson(person.get("specificContent")));
        } else if (contractorsData instanceof ArrayList<?> persons) {
            for (Object p : persons) {
                if (p instanceof LinkedHashMap<?, ?> person) {
                    people.add(getPerformingPerson(person.get("specificContent")));
                }
            }
        }

        return people;
    }

    private static PersonBO getPerformingPerson(Object personObject) {
        PersonBO performingPerson = null;

        if (personObject instanceof LinkedHashMap<?, ?> person) {
            performingPerson = new PersonBO();
            if (person.get("pppRepackagingFacilityPerformingPersonIdentifier") != null) {
                performingPerson.setIdentifier(person.get("pppRepackagingFacilityPerformingPersonIdentifier").toString());
            }
            if (person.get("pppRepackagingFacilityPerformingPersonName") != null) {
                performingPerson.setName(person.get("pppRepackagingFacilityPerformingPersonName").toString());
            }
            if (person.get("pppRepackagingFacilityPerformingPersonSurname") != null) {
                performingPerson.setSurname(person.get("pppRepackagingFacilityPerformingPersonSurname").toString());
            }
            if (person.get("pppRepackagingFacilityPerformingPersonFamilyName") != null) {
                performingPerson.setFamilyName(person.get("pppRepackagingFacilityPerformingPersonFamilyName").toString());
            }
        }
        return performingPerson;
    }

}