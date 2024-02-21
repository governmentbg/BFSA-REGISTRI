package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS7696;
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
public class ApplicationS7696DTO extends ApplicationS769DTO {
    private List<FacilityDTO> facilities = new ArrayList<>();
    private List<VehicleDTO> vehicles = new ArrayList<>();
    private List<String> ch50VehicleCertNumbers = new ArrayList<>();

    public static ApplicationS7696DTO of(final Record source, final Language language) {
        ApplicationS7696DTO dto = new ApplicationS7696DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS7696 application = source.getApplicationS7696();
        if (application != null) {
            dto.setCommencementActivityDate(application.getCommencementActivityDate());

            if (!CollectionUtils.isEmpty(application.getApplicationS7696Facilities())) {
                dto.setFacilities(application.getApplicationS7696Facilities());
            }

            if (!CollectionUtils.isEmpty(application.getApplicationS7696Vehicles())) {
                dto.setVehicles(VehicleDTO.ofS7696Vehicle(application.getApplicationS7696Vehicles(), language));
            }
        }

        return dto;
    }

    public static List<ApplicationS7696DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS7696DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS7696DTO dto = new ApplicationS7696DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

//            Данни за подвижни обекти и автомати за храна
            dto.setFacilities(getMobileFacilities(findValue(sc, "facilityDataGrid")));
//            Използвам МПС за транспортиране на храни по чл. 52
            dto.getVehicles().addAll(getCh52Vehicles(sc.get("descriptionCH52")));
//            Използвам МПС за транспортиране на храни по чл. 50
            dto.getCh50VehicleCertNumbers().addAll(getCh50Vehicles(sc.get("descriptionCH50")));
//            Дата на започване на дейността
            dto.setCommencementActivityDate(OffsetDateTime.parse(sc.get("commencementActivityDate").toString()));
        }

        return dto;
    }

    private static List<FacilityDTO> getMobileFacilities(Object facilityDataGrid) {
        List<FacilityDTO> facilityList = new ArrayList<>();

        if (facilityDataGrid instanceof LinkedHashMap<?, ?> facilityHashMap) {
            facilityList.add(getMobileFacility(facilityHashMap));
        } else if (facilityDataGrid instanceof ArrayList<?> facilityArray) {
            facilityArray.forEach(f -> facilityList.add(getMobileFacility((LinkedHashMap<?, ?>) f)));
        }

        return facilityList;
    }

    private static FacilityDTO getMobileFacility(LinkedHashMap<?, ?> facilityDataGrid) {
        FacilityDTO facilityDTO = new FacilityDTO();
        Set<KeyValueDTO> foods = new HashSet<>();

//        Групи храни, които ще се произвеждат, преработват и/или диструбират в обекта
        foods = getFoodTypes(findValue(facilityDataGrid, "producedFood"), "foodProduced");
        facilityDTO.getFoodTypes().addAll(foods);
//        Aдрес на обекта
        AddressDTO addressDTO = getAddress(findValue(facilityDataGrid, "mobileFacilityAddress"));
        facilityDTO.setAddress(addressDTO);
//        Начин на отвеждане на отпадните води
        facilityDTO.setDisposalWasteWater(facilityDataGrid.get("facilityWasteWaterDisposalType") != null
                ? facilityDataGrid.get("facilityWasteWaterDisposalType").toString()
                : null);
//        Пояснение към групи храни
        facilityDTO.setFoodTypeDescription(facilityDataGrid.get("producedFoodListDescription") != null
                ? facilityDataGrid.get("producedFoodListDescription").toString()
                : null);
//        Вид водоснабдяване в обекта
        if (facilityDataGrid.get("facilityWaterSupplylType") instanceof LinkedHashMap<?, ?> facilityWaterSupplylType) {
            facilityDTO.setWaterSupplyTypeName(facilityWaterSupplylType.get("label") != null
                    ? facilityWaterSupplylType.get("label").toString()
                    : null);
            facilityDTO.setWaterSupplyTypeCode(facilityWaterSupplylType.get("value") != null
                    ? facilityWaterSupplylType.get("value").toString()
                    : null);
        }
        if (facilityDataGrid.get("specificContent") instanceof LinkedHashMap<?, ?> specificContent) {
//            Вид на обекта
            if (specificContent.get("mobileFacilityType") instanceof LinkedHashMap<?, ?> mobileFacilityType) {
                facilityDTO.setFacilityTypeName(mobileFacilityType.get("label") != null
                        ? mobileFacilityType.get("label").toString()
                        : null);
                facilityDTO.setFacilityTypeCode(mobileFacilityType.get("value") != null
                        ? mobileFacilityType.get("value").toString()
                        : null);
            }
//            Наименование
            facilityDTO.setName(specificContent.get("facilityName") != null
                    ? specificContent.get("facilityName").toString()
                    : null);

//            Площ на обекта в кв. метри
            facilityDTO.setArea(specificContent.get("facilityArea") != null
                    ? Double.parseDouble(specificContent.get("facilityArea").toString())
                    : null);

//            Описание на дейностите, извършвани в обекта
            facilityDTO.setActivityDescription(specificContent.get("objectActivityInfo") != null
                    ? specificContent.get("objectActivityInfo").toString()
                    : null);
        }

        //        TODO: Групи храни, които ще се произвеждат, преработват и/или дистрибутират в обекта

        return facilityDTO;
    }
}
