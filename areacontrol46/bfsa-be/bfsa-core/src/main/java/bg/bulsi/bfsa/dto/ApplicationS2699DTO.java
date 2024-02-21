package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ApplicationS2699;
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
import org.springframework.util.StringUtils;

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
public class ApplicationS2699DTO extends BaseApplicationDTO {

    private AddressDTO seedTreatmentFacilityAddress;
    private ContractorDTO ch83CertifiedPerson;
    @Builder.Default
    private List<PersonBO> ch83CertifiedPersons = new ArrayList<>();

    public static ApplicationS2699DTO ofRecord(final Record source, final Language language) {
        ApplicationS2699DTO dto = new ApplicationS2699DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

//        --- Set ApplicationS2699 ---
        ApplicationS2699 application = source.getApplicationS2699();
        if (application != null) {
            dto.setSeedTreatmentFacilityAddress(AddressDTO.of(application
                    .getSeedTreatmentFacilityAddress(), language));
            if (application.getCh83CertifiedPerson() != null) {
                dto.setCh83CertifiedPerson(ContractorDTO.of(application.getCh83CertifiedPerson(), language));
            }
            if (!CollectionUtils.isEmpty(application.getCh83CertifiedPersons())) {
                dto.setCh83CertifiedPersons(application.getCh83CertifiedPersons());
            }
        }

        return dto;
    }

    public static List<ApplicationS2699DTO> ofRecord(final List<Record> source, final Language language) {
        return source.stream().map(r -> ofRecord(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS2699DTO of(final ApplicationS2699 source, Language language) {
        ApplicationS2699DTO dto = new ApplicationS2699DTO();
        dto.setServiceType(ServiceType.S2699);
        dto.setApplicationStatus(source.getStatus());

//        --- Set Requestor, Applicant and base fields ---
//        dto.ofRecordBase(source);

//        --- Set ApplicationS2699 ---
        dto.setSeedTreatmentFacilityAddress(AddressDTO.of(source.getSeedTreatmentFacilityAddress(), language));
        if (source.getCh83CertifiedPerson() != null) {
            dto.setCh83CertifiedPerson(ContractorDTO.of(source.getCh83CertifiedPerson(), language));
        }
        if (!CollectionUtils.isEmpty(source.getCh83CertifiedPersons())) {
            dto.setCh83CertifiedPersons(source.getCh83CertifiedPersons());
        }

        return dto;
    }

    public static List<ApplicationS2699DTO> of(final List<ApplicationS2699> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS2699DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2699DTO dto = new ApplicationS2699DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            dto.setSeedTreatmentFacilityAddress(BaseApplicationDTO.getAddress(sc.get("facilityAddress")));
            dto.setCh83CertifiedPerson(getControlPerson(sc.get("__additionalSpecificContent")));
            dto.getCh83CertifiedPersons().addAll(getPerformingPersons(sc.get("__additionalSpecificContent")));
        }
        return dto;
    }

    private static ContractorDTO getControlPerson(final Object contractorObj) {
        ContractorDTO controlPerson = new ContractorDTO();
        if (contractorObj instanceof LinkedHashMap<?, ?> person) {
            controlPerson.setIdentifier(person.get("treatmentSeedControlPersonIdentifier").toString());
            controlPerson.setName(person.get("treatmentSeedControlPersonName").toString());

            Object treatmentSeedControlPersonSurname = person.get("treatmentSeedControlPersonSurname");
            if (treatmentSeedControlPersonSurname != null) {
                controlPerson.setSurname(treatmentSeedControlPersonSurname.toString());
            }

            controlPerson.setFamilyName(person.get("treatmentSeedControlPersonFamilyName").toString());
        }
        return controlPerson;
    }

    private static List<PersonBO> getPerformingPersons(final Object performingPeopleObj) {
        List<PersonBO> people = new ArrayList<>();
        if (performingPeopleObj instanceof LinkedHashMap<?, ?> activityPeopleObj) {
            Object activityPeople = activityPeopleObj.get("treatmentSeedPerformingPersons");
            if (activityPeople instanceof LinkedHashMap<?, ?> performingPerson) {
                people.add(getPerformingPerson(performingPerson));
            } else if (activityPeople instanceof ArrayList<?> performingPeople) {
                performingPeople.forEach(p -> people.add(getPerformingPerson((LinkedHashMap<?, ?>) p)));
            }
        }
        return people;
    }

    private static PersonBO getPerformingPerson(final LinkedHashMap<?, ?> person) {
        PersonBO performingPerson = new PersonBO();
        performingPerson.setIdentifier(person.get("treatmentSeedPerformingPersonIdentifier").toString());
        String name = person.get("treatmentSeedPerformingPersonName").toString();
        performingPerson.setName(name);
        String familyName = person.get("treatmentSeedPerformingPersonFamilyName").toString();
        performingPerson.setFamilyName(familyName);

        Object treatmentSeedPerformingPersonSurname = person.get("treatmentSeedPerformingPersonSurname");
        if (treatmentSeedPerformingPersonSurname != null) {
            performingPerson.setSurname(treatmentSeedPerformingPersonSurname.toString());
        }

        performingPerson.setFullName(
                (StringUtils.hasText(performingPerson.getName()) ? performingPerson.getName() : "") + " " +
                (StringUtils.hasText(performingPerson.getSurname()) ? performingPerson.getSurname() : "")  + " " +
                (StringUtils.hasText(performingPerson.getFamilyName()) ? performingPerson.getFamilyName() : "")
        );
        return performingPerson;
    }
}
