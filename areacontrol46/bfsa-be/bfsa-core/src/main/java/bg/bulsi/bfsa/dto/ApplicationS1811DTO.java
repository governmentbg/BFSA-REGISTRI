package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS1811;
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
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS1811DTO extends BaseApplicationDTO {

    private String facilityRegNumber;
    private List<VehicleDTO> ch52Vehicles = new ArrayList<>();
    private List<String> vehicleCertificateNumbers = new ArrayList<>();
    private List<VehicleDTO> ch50Vehicles = new ArrayList<>();
    private OffsetDateTime commencementActivityDate;

    public static ApplicationS1811DTO of(final Record source, Language language) {
        ApplicationS1811DTO dto = new ApplicationS1811DTO();

        dto.ofRecordBase(source, language);

        if (source.getFacility() != null) {
            dto.setFacilityRegNumber(source.getFacility().getRegNumber());
            dto.setCommencementActivityDate(source.getFacility().getCommencementActivityDate());
        }

        ApplicationS1811 application = source.getApplicationS1811();
        if (application != null) {
            if (!CollectionUtils.isEmpty(application.getCh50vehicles())) {
                dto.getCh50Vehicles().addAll(application.getCh50vehicles());
            }

            if (!CollectionUtils.isEmpty(application.getCh52vehicles())) {
                dto.getCh52Vehicles().addAll(application.getCh52vehicles());
            }

            dto.setCommencementActivityDate(application.getCommencementActivityDate());
        }

        return dto;
    }

    public static ApplicationS1811DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS1811DTO dto = new ApplicationS1811DTO();

        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setFacilityRegNumber(sc.get("facilityRegNumber").toString());

            if (sc.get("descriptionCH52") != null) {
                dto.getCh52Vehicles().addAll(getCh52Vehicles(sc.get("descriptionCH52")));
            }

            if (sc.get("descriptionCH50") != null) {
                dto.getVehicleCertificateNumbers().addAll(getCh50Vehicles(sc.get("descriptionCH50")));
            }

            dto.setCommencementActivityDate(OffsetDateTime.parse(sc.get("commencementActivityDate").toString()));
        }

        return dto;
    }
}