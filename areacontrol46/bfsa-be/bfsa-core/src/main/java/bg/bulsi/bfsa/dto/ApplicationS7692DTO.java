package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS7692;
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
public class ApplicationS7692DTO extends ApplicationS769DTO {

    // Данни за обекта *:
    // Вид дейност (избор от номенклатура)
    // Вид на обекта (избор от номенклатура)
    // Наименование (текст)
    // Площ на обекта (число) кв. метри
    // Разрешение за ползване по чл. 177, ал. 2 от Закона за устройство на територията № (текст)
    // Описание на дейностите, извършвани в обекта (текст)
    // Вид водоснабдяване в обекта (избор от номенклатура)
    // Начин на отвеждане на отпадните води (текст)
    // Капацитет на обекта:  (число)
    // Мерна единица (избор от номенклатура)
    private FacilityDTO facility;

    // Групи храни, които ще се произвеждат, преработват и/или дистрибутират в обекта * (избор от номенклатура с чек бокс)
    private Set<KeyValueDTO> foodTypes = new HashSet<>();

    // Пояснение към групи храни (текст)
    private String foodTypeDescription;

    // Средства за комуникация при търговия от разстояние: (попълва се при "Да")
    private AddressDTO address;

    // Описание на превозните средства по чл.52, които ще се използват за транспортиране на храни:  (попълва се при "Да")
    private List<String> ch50VehicleCertNumbers = new ArrayList<>();

    // Стикер No (текст) (попълва се при "Да")
    private List<VehicleDTO> vehicles = new ArrayList<>();


    public static ApplicationS7692DTO of(final Record source, final Language language) {
        ApplicationS7692DTO dto = new ApplicationS7692DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS7692 application = source.getApplicationS7692();
        if (application != null) {
            dto.setCommencementActivityDate(application.getCommencementActivityDate());
            dto.setFacility(FacilityDTO.of(application.getFacility(), language));
            dto.setFoodTypeDescription(application.getFoodTypeDescription());
            dto.setAddress(AddressDTO.of(application.getRemoteTradingAddress(), language));

            if (!CollectionUtils.isEmpty(application.getApplicationS7692Vehicles())) {
                dto.setVehicles(VehicleDTO.ofS7692Vehicle(application.getApplicationS7692Vehicles(), language));
            }

            if (ApplicationStatus.ENTERED.equals(application.getStatus())) {
                dto.setFoodTypes(application.getApplicationS7692FoodTypes());
            } else if (!CollectionUtils.isEmpty(application.getFoodTypes())) {
                dto.setFoodTypes(KeyValueDTO.ofClassifiers(application.getFoodTypes(), language));
            }
        }

        return dto;
    }

    public static List<ApplicationS7692DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS7692DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS7692DTO dto = new ApplicationS7692DTO();

        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

//            Информация за обекта
            dto.setFacility(getFacilityDetails(sc));

//            Групи храни, които ще се произвеждат, преработват и/или дистрибутират в обекта
            dto.setFoodTypes(getFoodTypes(sc.get("producedFood"), "foodProduced"));

//            Средства за комуникация при търговия от разстояние
            AddressDTO address = getAddress(sc.get("distanceTradingAddress"));
            if (address != null) {
                address.setUrl(sc.get("remoteTradingWebPage") != null
                        ? sc.get("remoteTradingWebPage").toString()
                        : null);
                address.setPhone(sc.get("remoteTradingPhone") != null
                        ? sc.get("remoteTradingPhone").toString()
                        : null);
                address.setMail(sc.get("remoteTradingEmail") != null
                        ? sc.get("remoteTradingEmail").toString()
                        : null);
                dto.setAddress(address);
            }

//            Използвам МПС за транспортиране на храни по чл. 52
            dto.getVehicles().addAll(getCh52Vehicles(sc.get("descriptionCH52")));

//            Използвам МПС за транспортиране на храни по чл. 50
            dto.getCh50VehicleCertNumbers().addAll(getCh50Vehicles(sc.get("descriptionCH50")));

//            Дата на започване на дейността
            dto.setCommencementActivityDate(OffsetDateTime.parse(sc.get("commencementActivityDate").toString()));
        }

        return dto;
    }


    private static FacilityDTO getFacilityDetails(LinkedHashMap<?, ?> sc) {
        FacilityDTO dto = new FacilityDTO();
        if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> additionalContent) {
            if (additionalContent.get("facilityWaterSupplyType") instanceof LinkedHashMap<?, ?> supplyType) {
                dto.setWaterSupplyTypeCode(supplyType.get("value") != null ?
                        supplyType.get("value").toString()
                        : null);
                dto.setWaterSupplyTypeName(supplyType.get("label") != null ?
                        supplyType.get("label").toString()
                        : null);
            }
            dto.setDisposalWasteWater(additionalContent.get("facilityWasteWaterDisposalType") != null
                    ? additionalContent.get("facilityWasteWaterDisposalType").toString()
                    : null);
            dto.setFoodTypeDescription(additionalContent.get("producedFoodListDescription") != null
                    ? additionalContent.get("producedFoodListDescription").toString()
                    : null);
            dto.setPermission177(additionalContent.get("facilityArt177p2") != null
                    ? additionalContent.get("facilityArt177p2").toString()
                    : null);
            dto.setCapacity(additionalContent.get("facilityCapacity") != null
                    ? Double.parseDouble(additionalContent.get("facilityCapacity").toString())
                    : null);
            if (additionalContent.get("unitType") instanceof LinkedHashMap<?, ?> unit) {
                dto.setMeasuringUnitName(unit.get("label") != null
                        ? unit.get("label").toString()
                        : null);
                dto.setMeasuringUnitCode(unit.get("value") != null
                        ? unit.get("value").toString()
                        : null);
            }

        }
        dto.setName(sc.get("facilityName") != null
                ? sc.get("facilityName").toString()
                : null);
        dto.setActivityDescription(sc.get("objectActivityInfo") != null
                ? sc.get("objectActivityInfo").toString()
                : null);
        dto.setArea(sc.get("facilityArea") != null
                ? Double.parseDouble(sc.get("facilityArea").toString())
                : null);

        if (sc.get("activityType") instanceof LinkedHashMap<?, ?> activityCode) {
            dto.setActivityTypeCode(activityCode.get("value") != null
                    ? activityCode.get("value").toString()
                    : null);
            dto.setActivityTypeName(activityCode.get("label") != null
                    ? activityCode.get("label").toString()
                    : null);
        }
        if (sc.get("objectType" + dto.getActivityTypeCode()) instanceof LinkedHashMap<?, ?> facilityType) {
            dto.setFacilityTypeCode(facilityType.get("value") != null
                    ? facilityType.get("value").toString()
                    : null);
            dto.setFacilityTypeName(facilityType.get("label") != null
                    ? facilityType.get("label").toString()
                    : null);
        }
//        TODO: Ако избера долните две - адреса не излиза, но излиза capacity и unitType
        if ("01816".equals(dto.getActivityTypeCode()) || "01817".equals(dto.getActivityTypeCode())) {
            dto.setAddress(getFacilityAddress(sc));
        }
        return dto;
    }

    private static AddressDTO getFacilityAddress(LinkedHashMap<?, ?> sc) {
        AddressDTO facilityAddress = new AddressDTO();
//        facilityAddress.setMail(sc.get("remoteTradingCommunicationEmail") != null
//                ? sc.get("remoteTradingCommunicationEmail").toString()
//                : null);
//        facilityAddress.setPhone(sc.get("remoteTradingCommunicationPhone") != null
//                ? sc.get("remoteTradingCommunicationPhone").toString()
//                : null);
//        facilityAddress.setUrl(sc.get("remoteTradingCommunicationWebPage") != null
//                ? sc.get("remoteTradingCommunicationWebPage").toString()
//                : null);
        facilityAddress.setSettlementCode(sc.get("commonPresAddrSettlementCode") != null
                ? sc.get("commonPresAddrSettlementCode").toString()
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
        facilityAddress.setCountryCode(sc.get("commonPresAddrCountryCode") != null ?
                sc.get("commonPresAddrCountryCode").toString()
                : null);
        facilityAddress.setCountryName(sc.get("commonPresAddrCountry") != null
                ? sc.get("commonPresAddrCountry").toString()
                : null);

        return facilityAddress;
    }
}
