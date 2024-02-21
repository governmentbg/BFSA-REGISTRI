package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ApplicationS2700;
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
public class ApplicationS2700DTO extends BaseApplicationDTO {

    @Builder.Default
    private List<PersonBO> ch83CertifiedPersons = new ArrayList<>();

    public static ApplicationS2700DTO of(final ApplicationS2700 source, Language language) {
        ApplicationS2700DTO dto = new ApplicationS2700DTO();
        dto.setServiceType(ServiceType.S2700);
        dto.setApplicationStatus(source.getStatus());

        if (!CollectionUtils.isEmpty(source.getCh83CertifiedPersons())) {
            dto.setCh83CertifiedPersons(source.getCh83CertifiedPersons());
        }

        return dto;
    }

    public static List<ApplicationS2700DTO> of(final List<ApplicationS2700> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS2700DTO ofRecord(final Record source, final Language language) {
        ApplicationS2700DTO dto = new ApplicationS2700DTO();

        dto.ofRecordBase(source, language);

        if (source.getApplicationS2700() != null) {
            if (!CollectionUtils.isEmpty(source.getApplicationS2700().getCh83CertifiedPersons())) {
                dto.getCh83CertifiedPersons().addAll(source.getApplicationS2700().getCh83CertifiedPersons());
            }
        }

        return dto;
    }

    public static ApplicationS2700DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2700DTO dto = new ApplicationS2700DTO();

        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            Object contractorsData = findValue(sc, "contractorsData");
            if (contractorsData instanceof LinkedHashMap<?, ?> person) {
                dto.getCh83CertifiedPersons().add(getCh83CertifiedPerson(person));
            } else if (contractorsData instanceof ArrayList<?> persons) {
                persons.forEach(p -> dto.getCh83CertifiedPersons().add(getCh83CertifiedPerson(p)));
            }
        }

        return dto;
    }

    private static PersonBO getCh83CertifiedPerson(final Object personObj) {
        PersonBO ch83CertifiedPerson = null;

        if (personObj instanceof LinkedHashMap<?, ?> person) {
            ch83CertifiedPerson = new PersonBO();

            if (person.get("contractorIdentifier") != null) {
                ch83CertifiedPerson.setIdentifier(person.get("contractorIdentifier").toString());
            }
            if (person.get("contractorName") != null) {
                ch83CertifiedPerson.setName(person.get("contractorName").toString());
            }
            if (person.get("contractorSurname") != null) {
                ch83CertifiedPerson.setSurname(person.get("contractorSurname").toString());
            }
            if (person.get("contractorFamilyName") != null) {
                ch83CertifiedPerson.setFamilyName(person.get("contractorFamilyName").toString());
            }
        }

        return ch83CertifiedPerson;
    }
}
