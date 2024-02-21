package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ApplicationS1590;
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
public class ApplicationS1590DTO extends BaseApplicationDTO {

    private AddressDTO warehouseAddress;
    private ContractorDTO ch83CertifiedPerson;
    @Builder.Default
    private List<PersonBO> ch83CertifiedPersons = new ArrayList<>();

    public static ApplicationS1590DTO ofRecord(final Record source, final Language language) {
        ApplicationS1590DTO dto = new ApplicationS1590DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

//        --- Set ApplicationS1590 ---
        ApplicationS1590 application = source.getApplicationS1590();
        if (application != null) {
            dto.setWarehouseAddress(AddressDTO.of(application.getWarehouseAddress(), language));

            if (application.getCh83CertifiedPerson() != null) {
                dto.setCh83CertifiedPerson(ContractorDTO.of(application.getCh83CertifiedPerson(), language));
            }
            if (!CollectionUtils.isEmpty(application.getCh83CertifiedPersons())) {
                dto.setCh83CertifiedPersons(application.getCh83CertifiedPersons());
            }
        }

        return dto;
    }

    public static List<ApplicationS1590DTO> ofRecord(final List<Record> source, final Language language) {
        return source.stream().map(r -> ofRecord(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS1590DTO of(final ApplicationS1590 source, Language language) {
        ApplicationS1590DTO dto = new ApplicationS1590DTO();
        dto.setServiceType(ServiceType.S1590);
        dto.setApplicationStatus(source.getStatus());

        dto.setWarehouseAddress(AddressDTO.of(source.getWarehouseAddress(), language));

        if (source.getCh83CertifiedPerson() != null) {
            dto.setCh83CertifiedPerson(ContractorDTO.of(source.getCh83CertifiedPerson(), language));
        }
        if (!CollectionUtils.isEmpty(source.getCh83CertifiedPersons())) {
            dto.setCh83CertifiedPersons(source.getCh83CertifiedPersons());
        }

        return dto;
    }

    public static List<ApplicationS1590DTO> of(final List<ApplicationS1590> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS1590DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS1590DTO dto = new ApplicationS1590DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setWarehouseAddress(BaseApplicationDTO.getAddress(sc.get("warehouseAddress")));
            dto.setCh83CertifiedPerson(getControlPerson(sc.get("__additionalSpecificContent")));
            dto.getCh83CertifiedPersons().addAll(getPerformingPersons(sc.get("__additionalSpecificContent")));
        }

        return dto;
    }

    private static ContractorDTO getControlPerson(final Object controlPersonObj) {
        ContractorDTO controlPerson = new ContractorDTO();
        if (controlPersonObj instanceof LinkedHashMap<?, ?> person) {
            controlPerson.setIdentifier(person.get("fumigationProcessControlPersonIdentifier").toString());
            controlPerson.setName(person.get("fumigationProcessControlPersonName").toString());

            Object fumigationProcessControlPersonSurname = person.get("fumigationProcessControlPersonSurname");
            if (fumigationProcessControlPersonSurname != null) {
                controlPerson.setSurname(fumigationProcessControlPersonSurname.toString());
            }

            controlPerson.setFamilyName(person.get("fumigationProcessControlPersonFamilyName").toString());
        }
        return controlPerson;
    }

    private static List<PersonBO> getPerformingPersons(final Object performingPeopleObj) {
        List<PersonBO> people = new ArrayList<>();
        if (performingPeopleObj instanceof LinkedHashMap<?, ?> activityPeopleObj) {
            Object activityPeople = activityPeopleObj.get("fumigationPerformingPersonsData");
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
        performingPerson.setIdentifier(person.get("fumigationPerformingPersonIdentifier").toString());
        String firstName = person.get("fumigationPerformingPersonName").toString();
        performingPerson.setName(firstName);
        String familyName = person.get("fumigationPerformingPersonFamilyName").toString();
        performingPerson.setFamilyName(familyName);

        Object treatmentSeedPerformingPersonSurname = person.get("treatmentSeedPerformingPersonSurname");
        if (treatmentSeedPerformingPersonSurname != null) {
            performingPerson.setSurname(treatmentSeedPerformingPersonSurname.toString());
        }

        if (person.get("fumigationDegreeNumber") != null) {
            String degreeNumber = "№ " + person.get("fumigationDegreeNumber").toString();
            String dateString = person.get("fumigationDegreeIssuedDate").toString();
            String date = "; издадена на " + dateString.split("T")[0];
            String from = " от " + person.get("fumigationDegreeIssuer").toString();
            performingPerson.setDegree(degreeNumber + date + from);
        }

        performingPerson.setFullName(
                (StringUtils.hasText(performingPerson.getName()) ? performingPerson.getName() : "") +
                        (StringUtils.hasText(performingPerson.getSurname()) ? " " + performingPerson.getSurname() : "")  +
                        (StringUtils.hasText(performingPerson.getFamilyName()) ?  " " + performingPerson.getFamilyName() : "")
        );
        return performingPerson;
    }

}