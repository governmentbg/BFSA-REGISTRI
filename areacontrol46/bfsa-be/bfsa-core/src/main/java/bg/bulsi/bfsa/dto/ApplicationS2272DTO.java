package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS2272;
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
public class ApplicationS2272DTO extends BaseApplicationDTO {

    // Vehicles data
    private List<VehicleDTO> vehicles = new ArrayList<>();

    public static ApplicationS2272DTO of(final Record source, final Language language) {
        ApplicationS2272DTO dto = new ApplicationS2272DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS2272 application = source.getApplicationS2272();
        if (application != null) {
            if (ApplicationStatus.ENTERED.equals(application.getStatus())) {
                dto.setVehicles(application.getCh55vehicles());
            } else if (!CollectionUtils.isEmpty(application.getApplicationS2272Vehicles())) {
                dto.setVehicles(VehicleDTO.ofS2272Vehicle(source.getApplicationS2272().getApplicationS2272Vehicles(), language));
            }
        }

        return dto;
    }

    public static List<ApplicationS2272DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS2272DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2272DTO dto = new ApplicationS2272DTO();

        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            Object vehicleData = findValue(sc, "vehicleData");
            dto.getVehicles().addAll(getCh52Vehicles(vehicleData));
        }

        return dto;
    }
}
