package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS3182;
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
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS3182DTO extends BaseApplicationDTO {

    private AddressDTO address;

    // Vehicles data
    private List<VehicleDTO> vehicles = new ArrayList<>();
    private List<String> ch50VehicleCertNumbers = new ArrayList<>();
    private OffsetDateTime commencementActivityDate;
    private FacilityDTO facility;

    public static ApplicationS3182DTO of(final Record source, final Language language) {
        ApplicationS3182DTO dto = new ApplicationS3182DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS3182 application = source.getApplicationS3182();
        if (application != null) {
            dto.setCommencementActivityDate(application.getCommencementActivityDate());
            if (application.getAddress() != null) {
                dto.setAddress(AddressDTO.of(application.getAddress(), language));
            }
            if (application.getFacility() != null) {
                dto.setFacility(FacilityDTO.of(application.getFacility(), language));
            }
            if (!CollectionUtils.isEmpty(application.getApplicationS3182Vehicles())) {
                dto.setVehicles(VehicleDTO.ofS3182Vehicle(application.getApplicationS3182Vehicles(), language));
            }
        }

        return dto;
    }

    public static List<ApplicationS3182DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS3182DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS3182DTO dto = new ApplicationS3182DTO();

        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            if (sc.get("changeMeAddress") != null) {
                AddressDTO addressDTO = getAddress(sc.get("changeMeAddress"));
                addressDTO.setPhone(sc.get("phone").toString());
                addressDTO.setMail(sc.get("mail").toString());
                Object url = sc.get("url");
                if (url != null) {
                    addressDTO.setUrl(url.toString());
                }
                dto.setAddress(addressDTO);
            }

            FacilityDTO facilityDTO = new FacilityDTO();

            if (sc.get("waterSupplyType") instanceof LinkedHashMap<?, ?> waterSupplyType) {
                facilityDTO.setWaterSupplyTypeCode(waterSupplyType.get("value").toString());
            }

            List<FacilityCapacityDTO> facilityCapacityDTOS = new ArrayList<>();
            Object objectCapacities = sc.get("objectCapacity");
            if (objectCapacities instanceof LinkedHashMap<?, ?> capacity) {
                facilityCapacityDTOS.add(getFacilityCapacity(capacity));
            } else if (objectCapacities instanceof ArrayList<?> capacities) {
                capacities.forEach(capacity -> {
                    if (capacity instanceof LinkedHashMap<?, ?> c) {
                        facilityCapacityDTOS.add(getFacilityCapacity(c));
                    }
                });
            }
            facilityDTO.setFacilityCapacities(facilityCapacityDTOS);

            Object objectName = sc.get("objectName");
            if (objectName != null) {
                facilityDTO.setName(objectName.toString());
            }
            Object activityDescription = sc.get("activityDescription");
            if (activityDescription != null) {
                facilityDTO.setActivityDescription(activityDescription.toString());
            }
            if (sc.get("activityType") instanceof LinkedHashMap<?, ?> activityType) {
                facilityDTO.setActivityTypeCode(activityType.get("value").toString());
            }

            AddressDTO facilityAddress = new AddressDTO();
            Object settlementCode = sc.get("commonPresAddrSettlementCode");
            if (settlementCode != null) {
                facilityAddress.setSettlementCode(settlementCode.toString());
                facilityAddress.setAddress(sc.get("commonPresAddrLocationName") != null
                        ? sc.get("commonPresAddrLocationName").toString()
                        : null);
            }
            facilityDTO.setAddress(facilityAddress);
            facilityDTO.setDisposalWasteWater(sc.get("methodOfDisposal") != null
                    ? sc.get("methodOfDisposal").toString()
                    : null);
            facilityDTO.setPermission177(sc.get("permissionNumber") != null
                    ? sc.get("permissionNumber").toString()
                    : null);

            dto.setFacility(facilityDTO);
            dto.setCommencementActivityDate(sc.get("commencementActivityDate") != null
                    ? OffsetDateTime.parse(sc.get("commencementActivityDate").toString())
                    : OffsetDateTime.now());

            dto.getVehicles().addAll(getCh52Vehicles(sc.get("vehicleDescription")));
        }

        return dto;
    }

    private static FacilityCapacityDTO getFacilityCapacity(LinkedHashMap<?, ?> capacity) {
        FacilityCapacityDTO capacityDTO = new FacilityCapacityDTO();
        if (capacity.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
            Object product = sc.get("product");
            if (product != null) {
                capacityDTO.setProduct(product.toString());
            }
            Object productQuantity = sc.get("productQuantity");
            if (productQuantity != null) {
                capacityDTO.setQuantity(Integer.parseInt(productQuantity.toString()));
            }
            Object productMaterial = sc.get("productMaterial");
            if (productMaterial instanceof LinkedHashMap<?, ?> material) {
                capacityDTO.setMaterialCode(material.get("value").toString());
            }
            Object productUnit = sc.get("productUnit");
            if (productUnit instanceof LinkedHashMap<?, ?> unit) {
                capacityDTO.setUnitCode(unit.get("value").toString());
            }
        }
        return capacityDTO;
    }

}
