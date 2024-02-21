package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS2869;
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
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2869DTO extends BaseApplicationDTO {

    private List<FacilityDTO> facilities = new ArrayList<>();
    private List<String> vehicleCertificateNumbers = new ArrayList<>();
    private List<VehicleDTO> ch52Vehicles = new ArrayList<>();
    private List<VehicleDTO> ch50Vehicles = new ArrayList<>();

    private OffsetDateTime commencementActivityDate;

    public static ApplicationS2869DTO of(final Record source, final Language language) {
        ApplicationS2869DTO dto = new ApplicationS2869DTO();

        dto.ofRecordBase(source, language);

        ApplicationS2869 application = source.getApplicationS2869();
        if (application != null) {
            dto.setCommencementActivityDate(application.getCommencementActivityDate());

            if (!CollectionUtils.isEmpty(application.getFacilities())) {
                dto.getFacilities().addAll(application.getFacilities());
            }

            if (!CollectionUtils.isEmpty(source.getApplicationS2869().getCh50vehicles())) {
                dto.getCh50Vehicles().addAll(source.getApplicationS2869().getCh50vehicles());
            }

            if (!CollectionUtils.isEmpty(source.getApplicationS2869().getCh52vehicles())) {
                dto.getCh52Vehicles().addAll(source.getApplicationS2869().getCh52vehicles());
            }
        }

        if (source.getContractorPaper() != null) {
            dto.setApprovalDocumentStatus(source.getContractorPaper().getStatus());
        }

        return dto;
    }

    public static ApplicationS2869DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2869DTO dto = new ApplicationS2869DTO();
        dto.of(serviceRequest, docInfo);
        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            // Обект и дейностите му
            Object activitiesObject = sc.get("listActivity");
            if (activitiesObject != null) {
                if (activitiesObject instanceof LinkedHashMap<?, ?> map) {
                    Object specificContentObj = map.get("specificContent");
                    if (specificContentObj instanceof LinkedHashMap<?, ?> specContent) {
                        dto.getFacilities().add(FacilityDTO.builder()
                                .regNumber(specContent.get("objectRegisterNumber").toString())
                                .activityTypes(getActivities(specContent.get("typeOfActivity")))
                                .build());
                    }
                } else if (activitiesObject instanceof ArrayList<?> maps) {
                    for (var mObj : maps) {
                        if (mObj instanceof LinkedHashMap<?, ?> m) {
                            Object mSpec = m.get("specificContent");
                            if (mSpec instanceof LinkedHashMap<?, ?> sp) {
                                dto.getFacilities().add(FacilityDTO.builder()
                                        .regNumber(sp.get("objectRegisterNumber").toString())
                                        .activityTypes(getActivities(sp.get("typeOfActivity")))
                                        .build());
                            }
                        }
                    }
                }
            }

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

    private static List<KeyValueDTO> getActivities(Object activitiesObj) {
        List<KeyValueDTO> activities = new ArrayList<>();
        if (activitiesObj instanceof LinkedHashMap<?, ?> activity) {
            activities.add(KeyValueDTO.builder().code(activity.get("value").toString()).build());
        } else if (activitiesObj instanceof ArrayList<?> activitiesData) {
            activitiesData.forEach(a -> {
                if (a instanceof LinkedHashMap<?, ?> activity) {
                    activities.add(KeyValueDTO.builder().code(activity.get("value").toString()).build());
                }
            });
        }
        return activities;
    }
}
