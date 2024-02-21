package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS7694;
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
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS7694DTO extends ApplicationS769DTO {

    private String name;
    private String foodTypeDescription;
    private List<VehicleDTO> vehicles = new ArrayList<>();
    private List<String> ch50VehicleCertNumbers = new ArrayList<>();
    private Set<KeyValueDTO> foodTypes = new HashSet<>();

    public static ApplicationS7694DTO of(final Record source, final Language language) {
        ApplicationS7694DTO dto = new ApplicationS7694DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS7694 application = source.getApplicationS7694();
        if (application != null) {
            dto.setCommencementActivityDate(application.getCommencementActivityDate());
            dto.setName(application.getName());
            dto.setFoodTypeDescription(application.getFoodTypeDescription());

            if (!CollectionUtils.isEmpty(application.getApplicationS7694Vehicles())) {
                dto.setVehicles(VehicleDTO.ofS7694Vehicle(application.getApplicationS7694Vehicles(), language));
            }

            if (ApplicationStatus.ENTERED.equals(application.getStatus())) {
                dto.setFoodTypes(application.getApplicationS7694FoodTypes());
            } else if (!CollectionUtils.isEmpty(application.getFoodTypes())) {
                dto.setFoodTypes(KeyValueDTO.ofClassifiers(application.getFoodTypes(), language));
            }
        }

        return dto;
    }

    public static List<ApplicationS7694DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS7694DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS7694DTO dto = new ApplicationS7694DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            dto.getVehicles().addAll(getCh52Vehicles(sc.get("descriptionCH52")));
            dto.getCh50VehicleCertNumbers().addAll(getCh50Vehicles(sc.get("descriptionCH50")));
            dto.setCommencementActivityDate(OffsetDateTime.parse(sc.get("commencementActivityDate").toString()));
            dto.setFoodTypes(getFoodTypes(sc.get("producedFood"), "foodProduced"));

            Object facilityName = sc.get("facilityName");
            if (facilityName != null) {
                dto.setName(facilityName.toString());
            }
            if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> asc) {
                Object description = asc.get("producedFoodListDescription");
                if (description != null) {
                    dto.setFoodTypeDescription(description.toString());
                }
            }
        }

        return dto;
    }
}
