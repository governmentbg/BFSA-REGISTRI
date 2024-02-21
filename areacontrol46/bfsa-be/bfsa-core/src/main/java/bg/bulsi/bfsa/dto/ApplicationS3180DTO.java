package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.FacilityPaper;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
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

/**
 * Заявление Образец КХ №1
 * Заявление за регистрация/одобрение на обект за производство,
 * преработка и/или дистрибуция с храни.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS3180DTO extends BaseApplicationDTO {

    private FacilityDTO facility;

    // Описание на дейността
    private Long sectionGroupId;
    private Long activityGroupId;
    private List<String> relatedActivityCategories = new ArrayList<>();
    private List<String> associatedActivityCategories = new ArrayList<>();
    private List<String> animalSpecies = new ArrayList<>();
    private List<String> remarks = new ArrayList<>();
    private List<String> pictograms = new ArrayList<>();
    private Set<KeyValueDTO> foodTypes = new HashSet<>();

    // Vehicles data
    private List<VehicleDTO> vehicles = new ArrayList<>();
    private List<String> ch50VehicleCertNumbers = new ArrayList<>();
    private OffsetDateTime commencementActivityDate;

    // Осъществяване търговия от разстояние
    private AddressDTO address;

    public static ApplicationS3180DTO of(final Record source, final Language language) {
        ApplicationS3180DTO dto = new ApplicationS3180DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        dto.setCommencementActivityDate(source.getFacility().getCommencementActivityDate());

        if (source.getFacility() != null) {
            dto.setFacility((FacilityDTO.of(source.getFacility(), language)));

            if (source.getFacility().getActivityGroup() != null) {
                dto.setSectionGroupId(source.getFacility().getActivityGroup().getParent().getId());
                dto.setActivityGroupId(source.getFacility().getActivityGroup().getId());
            }
            if (!CollectionUtils.isEmpty(source.getFacility().getRelatedActivityCategories())) {
                for (Nomenclature nom : source.getFacility().getRelatedActivityCategories()) {
                    dto.getRelatedActivityCategories().add(nom.getCode());
                }
            }
            if (!CollectionUtils.isEmpty(source.getFacility().getAssociatedActivityCategories())) {
                for (Nomenclature nom : source.getFacility().getAssociatedActivityCategories()) {
                    dto.getAssociatedActivityCategories().add(nom.getCode());
                }
            }
            if (!CollectionUtils.isEmpty(source.getFacility().getAnimalSpecies())) {
                for (Nomenclature nom : source.getFacility().getAnimalSpecies()) {
                    dto.getAnimalSpecies().add(nom.getCode());
                }
            }
            if (!CollectionUtils.isEmpty(source.getFacility().getRemarks())) {
                for (Nomenclature nom : source.getFacility().getRemarks()) {
                    dto.getRemarks().add(nom.getCode());
                }
            }
            if (!CollectionUtils.isEmpty(source.getFacility().getPictograms())) {
                for (Nomenclature nom : source.getFacility().getPictograms()) {
                    dto.getPictograms().add(nom.getCode());
                }
            }
            if (!CollectionUtils.isEmpty(source.getFacility().getFoodTypes())) {
                dto.setFoodTypes(KeyValueDTO.ofClassifiers(source.getFacility().getFoodTypes(), language));
            }
        }

        if (source.getApplicationS3180() != null) {
            if (source.getApplicationS3180().getAddress() != null) {
                dto.setAddress(AddressDTO.of(source.getApplicationS3180().getAddress(), language));
            }

            if (!CollectionUtils.isEmpty(source.getApplicationS3180().getApplicationS3180Vehicles())) {
                dto.setVehicles(VehicleDTO.ofS3180Vehicle(source.getApplicationS3180().getApplicationS3180Vehicles(), language));
            }
        }

        FacilityPaper paper = source.getFacilityPaper();
        if (paper != null) {
            dto.setApprovalDocumentStatus(paper.getStatus());
        }

        return dto;
    }

    public static ApplicationS3180DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS3180DTO dto = new ApplicationS3180DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setCommencementActivityDate(OffsetDateTime.parse(sc.get("commencementActivityDate").toString()));

            FacilityDTO facilityDTO = new FacilityDTO();

            facilityDTO.setAddress(BaseApplicationDTO.getAddress(sc.get("facilityAddress")));

            Object facilityDisposalWasteWater = sc.get("facilityDisposalWasteWater");
            if (facilityDisposalWasteWater != null) {
                facilityDTO.setDisposalWasteWater(facilityDisposalWasteWater.toString());
            }

            Object facilityPermission177 = sc.get("facilityPermission177");
            if (facilityPermission177 != null) {
                facilityDTO.setPermission177(facilityPermission177.toString());
            }

            Object facilityActivityDescription = sc.get("facilityActivityDescription");
            if (facilityActivityDescription != null) {
                facilityDTO.setActivityDescription(facilityActivityDescription.toString());
            }

            Object facilityCapacity = sc.get("facilityCapacity");
            if (facilityCapacity != null) {
                facilityDTO.setCapacity(Double.valueOf(facilityCapacity.toString()));
            }

            if (sc.get("facilityActivityTypeCode") instanceof LinkedHashMap<?, ?> facilityActivityTypeCode) {
                // Nomenclature-library 01800: Activity Type
                Object code = facilityActivityTypeCode.get("value");
                if (code != null) {
                    facilityDTO.setActivityTypeCode(code.toString());
                }

                if (sc.get("facilitySubActivityTypeCode") instanceof LinkedHashMap<?, ?> facilitySubActivityTypeCode) {
                    // Nomenclature-library 01100: Sub Activity Type
                    Object subCode = facilitySubActivityTypeCode.get("value");
                    if (subCode != null) {
                        facilityDTO.setSubActivityTypeCode(subCode.toString());
                    }
                }
            }
            if (sc.get("facilityWaterSupplyTypeCode") instanceof LinkedHashMap<?, ?> waterSupplyType) {
                facilityDTO.setWaterSupplyTypeCode(waterSupplyType.get("value").toString());
            }
            if (sc.get("facilityPeriodCode") instanceof LinkedHashMap<?, ?> periodCode) {
                facilityDTO.setPeriodCode(periodCode.get("value").toString());
            }
            if (sc.get("facilityMeasuringUnitCode") instanceof LinkedHashMap<?, ?> unitCode) {
                facilityDTO.setMeasuringUnitCode(unitCode.get("value").toString());
            }

            facilityDTO.setFoodTypes(getFoodTypes(sc.get("producedFood"), "foodProduced"));

            Object additionalSpecificContent = sc.get("__additionalSpecificContent");
            if (additionalSpecificContent instanceof LinkedHashMap<?, ?> adsc) {
                Object specific = adsc.get("specificcontent");
                if (specific instanceof LinkedHashMap<?, ?> s) {
                    List<FacilityCapacityDTO> capacityDTOS = new ArrayList<>();
                    Object facilitiesCapacities = s.get("facilityCapacities");
                    if (facilitiesCapacities instanceof LinkedHashMap<?, ?> capacity) {
                        capacityDTOS.add(getCapacity(capacity));
                    } else if (facilitiesCapacities instanceof ArrayList<?> capacities) {
                        capacities.forEach(c -> {
                            if (c instanceof LinkedHashMap<?, ?> capacity) {
                                capacityDTOS.add(getCapacity(capacity));
                            }
                        });
                    }
                    facilityDTO.getFacilityCapacities().addAll(capacityDTOS);
                }
            }
            dto.setFacility(facilityDTO);

            Object vehicles52Obj = sc.get("descriptionCH52");
            if (vehicles52Obj instanceof LinkedHashMap<?, ?> vehicle) {
                dto.getVehicles().addAll(getCh52Vehicles(vehicle));
            } else if (vehicles52Obj instanceof ArrayList<?> vehiclesObjs) {
                vehiclesObjs.forEach(vehicle -> {
                    if (vehicle instanceof LinkedHashMap<?, ?> v) {
                        dto.getVehicles().addAll(getCh52Vehicles(v));
                    }
                });
            }

            Object vehicles50Obj = sc.get("descriptionCH50");
            if (vehicles50Obj instanceof LinkedHashMap<?, ?> vehicle) {
                dto.getCh50VehicleCertNumbers().addAll(getCh50Vehicles(vehicle));
            } else if (vehicles50Obj instanceof ArrayList<?> vehiclesObjs) {
                vehiclesObjs.forEach(vehicle -> {
                    if (vehicle instanceof LinkedHashMap<?, ?> v) {
                        dto.getCh50VehicleCertNumbers().addAll(getCh50Vehicles(v));
                    }
                });
            }

            if (sc.get("changeMeAddress") instanceof LinkedHashMap<?,?> changeAddress) {
                AddressDTO addressDTO = getAddress(changeAddress);
                Object phone = sc.get("phone");
                if (phone != null) {
                    addressDTO.setPhone(phone.toString());
                }
                Object email = sc.get("mail");
                if (email != null) {
                    addressDTO.setMail(email.toString());
                }
                Object url = sc.get("url");
                if (url != null) {
                    addressDTO.setUrl(url.toString());
                }
                dto.setAddress(addressDTO);
            }
        }

        return dto;
    }

    private static FacilityCapacityDTO getCapacity(LinkedHashMap<?, ?> capacity) {
        FacilityCapacityDTO dto = new FacilityCapacityDTO();
        Object specificContent = capacity.get("specificContent");
        if (specificContent instanceof LinkedHashMap<?, ?> content) {
            if (content.get("facilityCapacityFacilityFridgeCapacity") != null) {
                dto.setFridgeCapacity(
                        Double.valueOf(content.get("facilityCapacityFacilityFridgeCapacity").toString()));
            }
            if (content.get("facilityCapacityRawMilkTypeCode") != null) {
                if (content.get("facilityCapacityRawMilkTypeCode") instanceof LinkedHashMap<?, ?> milkType) {
                    dto.setRawMilkTypeCode(milkType.get("value").toString());
                }
            }
        }
        return dto;
    }
}
