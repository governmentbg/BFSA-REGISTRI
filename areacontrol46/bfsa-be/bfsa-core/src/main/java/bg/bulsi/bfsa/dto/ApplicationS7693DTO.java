package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS7693;
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
import org.springframework.util.StringUtils;

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
public class ApplicationS7693DTO extends ApplicationS769DTO {

    private String facilityRegNumber;
    private FacilityDTO facility;
    private String foodTypeDescription;
    private List<VehicleDTO> vehicles = new ArrayList<>();
    private List<String> ch50VehicleCertNumbers = new ArrayList<>();
    private Set<KeyValueDTO> foodTypes = new HashSet<>();
    private AddressDTO address;

    public static ApplicationS7693DTO of(final Record source, final Language language) {
        ApplicationS7693DTO dto = new ApplicationS7693DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS7693 application = source.getApplicationS7693();
        if (application != null) {
            dto.setCommencementActivityDate(application.getCommencementActivityDate());
            dto.setFacility(FacilityDTO.of(application.getFacility(), language));
            dto.setFoodTypeDescription(application.getFoodTypeDescription());
            dto.setAddress(AddressDTO.of(application.getAddress(), language));

            if (!CollectionUtils.isEmpty(application.getApplicationS7693Vehicles())) {
                dto.setVehicles(VehicleDTO.ofS7693Vehicle(application.getApplicationS7693Vehicles(), language));
            }

            if (ApplicationStatus.ENTERED.equals(application.getStatus())) {
                dto.setFoodTypes(application.getApplicationS7693FoodTypes());
            } else if (!CollectionUtils.isEmpty(application.getFoodTypes())) {
                dto.setFoodTypes(KeyValueDTO.ofClassifiers(application.getFoodTypes(), language));
            }
        }

        return dto;
    }

    public static List<ApplicationS7693DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS7693DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS7693DTO dto = new ApplicationS7693DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            dto.getVehicles().addAll(getCh52Vehicles(sc.get("descriptionCH52")));
            dto.getCh50VehicleCertNumbers().addAll(getCh50Vehicles(sc.get("descriptionCH50")));
            dto.setCommencementActivityDate(OffsetDateTime.parse(sc.get("commencementActivityDate").toString()));
            dto.setFoodTypes(getFoodTypes(sc.get("producedFood"), "foodProduced"));

            Object remoteTradingAddress = sc.get("remoteTradingAddress");
            if (remoteTradingAddress != null) {
                AddressDTO addressDTO = getAddress(remoteTradingAddress);
                if (sc.get("remoteTradingCommunicationWebPage") != null) {
                    addressDTO.setUrl(sc.get("remoteTradingCommunicationWebPage").toString());
                }
                if (sc.get("remoteTradingCommunicationEmail") != null) {
                    addressDTO.setMail(sc.get("remoteTradingCommunicationEmail").toString());
                }
                if (sc.get("remoteTradingCommunicationPhone") != null) {
                    addressDTO.setPhone(sc.get("remoteTradingCommunicationPhone").toString());
                }

                dto.setAddress(addressDTO);
            }

            FacilityDTO facilityDTO = new FacilityDTO();
            facilityDTO.setAddress(getFacilityAddress(sc));
            facilityDTO.setName(sc.get("facilityName") != null
                    ? sc.get("facilityName").toString()
                    : null);
            if (sc.get("activityType") instanceof LinkedHashMap<?, ?> activityType) {
                facilityDTO.setActivityTypeCode(activityType.get("value").toString());
            }

            Object additionalSpecificContent = sc.get("__additionalSpecificContent");
            if (additionalSpecificContent instanceof LinkedHashMap<?, ?> asc) {
                getFacilityCapacities(facilityDTO, asc);
            }

            dto.setFacility(facilityDTO);
        }

        return dto;
    }

    private static void getFacilityCapacities(FacilityDTO facilityDTO, LinkedHashMap<?, ?> asc) {
        if (asc.get("unitType") instanceof LinkedHashMap<?, ?> unitType) {
            facilityDTO.setMeasuringUnitCode(unitType.get("value").toString());
        }
        if (asc.get("facilityWaterSupplyType") instanceof LinkedHashMap<?, ?> supplyType) {
            facilityDTO.setWaterSupplyTypeCode(supplyType.get("value").toString());
        }
        if (asc.get("facilityPeriodType") instanceof LinkedHashMap<?, ?> periodType) {
            facilityDTO.setPeriodCode(periodType.get("value").toString());
        }

        facilityDTO.setActivityDescription(asc.get("facilityActivity") != null
                ? asc.get("facilityActivity").toString()
                : null);
        facilityDTO.setDisposalWasteWater(asc.get("facilityWasteWaterDisposalType") != null
                ? asc.get("facilityWasteWaterDisposalType").toString()
                : null);
        facilityDTO.setCapacity(asc.get("facilityCapacity") != null
                ? Double.parseDouble(asc.get("facilityCapacity").toString())
                : null);
        facilityDTO.setFoodTypeDescription(asc.get("producedFoodListDescription") != null
                ? asc.get("producedFoodListDescription").toString()
                : null);
        facilityDTO.setPermission177(asc.get("facilityArt177p2") != null
                ? asc.get("facilityArt177p2").toString()
                : null);
    }

    private static AddressDTO getFacilityAddress(LinkedHashMap<?, ?> sc) {
        AddressDTO facilityAddress = new AddressDTO();
        facilityAddress.setMail(sc.get("remoteTradingCommunicationEmail") != null
                ? sc.get("remoteTradingCommunicationEmail").toString()
                : null);
        facilityAddress.setPhone(sc.get("remoteTradingCommunicationPhone") != null
                ? sc.get("remoteTradingCommunicationPhone").toString()
                : null);
        facilityAddress.setSettlementCode(sc.get("commonPresAddrSettlementCode") != null
                ? sc.get("commonPresAddrSettlementCode").toString()
                : null);
        facilityAddress.setUrl(sc.get("remoteTradingCommunicationWebPage") != null
                ? sc.get("remoteTradingCommunicationWebPage").toString()
                : null);
        facilityAddress.setPostCode(sc.get("commonPresAddrPostCode") != null
                ? sc.get("commonPresAddrPostCode").toString()
                : null);

        String buildingNumber = "";
        String floor = "";
        String apartment = "";
        String entrance = "";
        if (sc.get("commonPresAddrBuildingNumber") != null) {
            buildingNumber = StringUtils.hasText(sc.get("commonPresAddrBuildingNumber").toString())
                    && !sc.get("commonPresAddrBuildingNumber").toString().equals("null")
                    ? ", № " + sc.get("commonPresAddrBuildingNumber").toString()
                    : "";
        }
        if (sc.get("commonPresAddrEntrance") != null) {
            entrance = StringUtils.hasText(sc.get("commonPresAddrEntrance").toString())
                    && !sc.get("commonPresAddrEntrance").toString().equals("null")
                    ? ", вх. " + sc.get("commonPresAddrEntrance").toString()
                    : "";
        }
        if (sc.get("commonPresAddrFloor") != null) {
            floor = StringUtils.hasText(sc.get("commonPresAddrFloor").toString())
                    && !sc.get("commonPresAddrFloor").toString().equals("null")
                    ? ", ет. " + sc.get("commonPresAddrFloor").toString()
                    : "";
        }
        if (sc.get("commonPresAddrApartment") != null) {
            apartment = StringUtils.hasText(sc.get("commonPresAddrApartment").toString())
                    && !sc.get("commonPresAddrApartment").toString().equals("null")
                    ? ", ап. " + sc.get("commonPresAddrApartment").toString()
                    : "";
        }

        String address = sc.get("commonPresAddrLocationName") != null
                ? sc.get("commonPresAddrLocationName").toString()
                + buildingNumber + entrance + floor + apartment
                : "";
        facilityAddress.setAddress(address);

        String fullAddress = sc.get("commonPresAddrCountry").toString() + ", "
                + sc.get("commonPresAddrDistrict").toString() + ", "
                + sc.get("commonPresAddrMunicipality").toString() + ", "
                + address;

        facilityAddress.setFullAddress(fullAddress);
        facilityAddress.setCountryCode(sc.get("commonPresAddrCountryCode").toString());

        return facilityAddress;
    }
}
