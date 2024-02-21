package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS7695;
import bg.bulsi.bfsa.model.Facility;
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
import org.springframework.beans.BeanUtils;
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
public class ApplicationS7695DTO extends ApplicationS769DTO {

    private String facilityRegNumber;
    private String facilityActivityDescription;
    private Double capacityUsage;
    private String unitTypeCode;
    private String unitTypeName;
    private String periodTypeCode;
    private String periodTypeName;
    private FacilityDTO facility;
    private String foodTypeDescription;
    private List<VehicleDTO> vehicles = new ArrayList<>();
    private List<String> ch50VehicleCertNumbers = new ArrayList<>();
    private Set<KeyValueDTO> foodTypes = new HashSet<>();
    private AddressDTO address;

    public static ApplicationS7695DTO of(final ApplicationS7695 source, final Language language) {
        ApplicationS7695DTO dto = new ApplicationS7695DTO();

        if (source != null) {
            BeanUtils.copyProperties(source, dto);

            Facility facility = source.getFacility();
            if (facility != null) {
                dto.setFacilityRegNumber(facility.getRegNumber());
                dto.setAddress(AddressDTO.of(facility.getAddress(), language));
                dto.setRegNumber(facility.getRegNumber());
                dto.setRegDate(facility.getRegDate());
            }

            Record record = source.getRecord();
            if (record != null && record.getContractorPaper() != null) {
                dto.setEntryNumber(record.getEntryNumber());
                dto.setEntryDate(record.getEntryDate());
            }

            dto.setApplicationStatus(source.getStatus());
        }

        return dto;
    }

    public static List<ApplicationS7695DTO> of(final List<ApplicationS7695> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS7695DTO ofRecord(final Record source, final Language language) {
        ApplicationS7695DTO dto = new ApplicationS7695DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS7695 application = source.getApplicationS7695();
        if (application != null) {
            dto.setCommencementActivityDate(application.getCommencementActivityDate());
            dto.setFoodTypeDescription(application.getFoodTypeDescription());
            dto.setAddress(AddressDTO.of(application.getAddress(), language));
            dto.setCapacityUsage(application.getCapacityUsage());
            dto.setFacilityActivityDescription(application.getFacilityActivityDescription());

            if (application.getFacility() != null) {
                dto.setFacility(FacilityDTO.of(application.getFacility(), language));
//                TODO: What is necessary to be added for MItko
//                dto.setFacility(FacilityDTO.builder()
//
//                        .build());
            }

            if (application.getPeriodType() != null) {
                dto.setPeriodTypeCode(application.getPeriodType().getCode());
                dto.setPeriodTypeName(application.getPeriodType().getI18n(language).getName());
            }

            if (application.getUnitType() != null) {
                dto.setUnitTypeCode(application.getUnitType().getCode());
                dto.setUnitTypeName(application.getUnitType().getI18n(language).getName());
            }

            if (!CollectionUtils.isEmpty(application.getApplicationS7695Vehicles())) {
                dto.setVehicles(VehicleDTO.ofS7695Vehicle(application.getApplicationS7695Vehicles(), language));
            }

            if (ApplicationStatus.ENTERED.equals(application.getStatus())) {
                dto.setFoodTypes(application.getApplicationS7695FoodTypes());
            } else if (!CollectionUtils.isEmpty(application.getFoodTypes())) {
                dto.setFoodTypes(KeyValueDTO.ofClassifiers(application.getFoodTypes(), language));
            }
        }

        return dto;
    }

    public static List<ApplicationS7695DTO> ofRecord(final List<Record> source, final Language language) {
        return source.stream().map(f -> ofRecord(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS7695DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS7695DTO dto = new ApplicationS7695DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            dto.getVehicles().addAll(getCh52Vehicles(sc.get("descriptionCH52")));
            dto.getCh50VehicleCertNumbers().addAll(getCh50Vehicles(sc.get("descriptionCH50")));
            dto.setCommencementActivityDate(OffsetDateTime.parse(sc.get("commencementActivityDate").toString()));
            dto.setFoodTypes(getFoodTypes(sc.get("producedFood"), "foodProduced"));

            AddressDTO address = getAddress(sc.get("distanceTradingAddress"));
            if (address != null) {
                address.setUrl(sc.get("distanceTradeWebPage") != null
                        ? sc.get("distanceTradeWebPage").toString()
                        : null);
                address.setPhone(sc.get("distanceTradePhone") != null
                        ? sc.get("distanceTradePhone").toString()
                        : null);
                address.setMail(sc.get("distanceTradeEmail") != null
                        ? sc.get("distanceTradeEmail").toString()
                        : null);
                dto.setAddress(address);
            }

            dto.setFacilityActivityDescription(sc.get("facilityActivityDescription") != null
                    ? sc.get("facilityActivityDescription").toString()
                    : null);
            dto.setFacilityRegNumber(sc.get("facilityRegNumber") != null
                    ? sc.get("facilityRegNumber").toString()
                    : null);
            dto.setCapacityUsage(sc.get("facilityCapacity") != null
                    ? Double.parseDouble(sc.get("facilityCapacity").toString())
                    : null);

            if (sc.get("unitType") instanceof LinkedHashMap<?, ?> unitType) {
                dto.setUnitTypeCode(unitType.get("value").toString());
            }
            if (sc.get("facilityPeriodType") instanceof LinkedHashMap<?, ?> periodType) {
                dto.setPeriodTypeCode(periodType.get("value").toString());
            }

            Object additional = sc.get("__additionalSpecificContent");
            if (additional instanceof LinkedHashMap<?, ?> ad) {
                dto.setFoodTypeDescription(ad.get("producedFoodListDescription") != null
                        ? ad.get("producedFoodListDescription").toString()
                        : null);
            }
        }

        return dto;
    }
}
