package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
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

import java.util.LinkedHashMap;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS1199DTO extends BaseApplicationDTO {

    private FacilityDTO facility;

    private String facilityRegNumber;
    private Boolean leasedWarehouseSpace;

    public static ApplicationS1199DTO of(final Record source, Language language) {
        ApplicationS1199DTO dto = new ApplicationS1199DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        if (source.getFacility() != null) {
            dto.setFacility((FacilityDTO.of(source.getFacility(), language)));
        }

//        --- Set ApplicationS1590 ---
        if (source.getApplicationS1199() != null) {
            dto.setFacilityRegNumber(source.getApplicationS1199().getFacilityRegNumber());
            dto.setLeasedWarehouseSpace(source.getApplicationS1199().getLeasedWarehouseSpace());
        }

        return dto;
    }

    public static ApplicationS1199DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS1199DTO dto = new ApplicationS1199DTO();

        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            dto.setFacilityRegNumber(sc.get("objectVetNumber").toString());
            dto.setLeasedWarehouseSpace("yes".equals(((LinkedHashMap<?, ?>)
                    sc.get("useRentedWarehouseSpace")).get("value").toString()));
        }

        return dto;
    }
}