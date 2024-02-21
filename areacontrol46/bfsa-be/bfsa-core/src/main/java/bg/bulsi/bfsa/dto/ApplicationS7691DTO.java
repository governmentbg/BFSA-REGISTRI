package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS7691;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import generated.ServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;
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
public class ApplicationS7691DTO extends ApplicationS769DTO {

    private FacilityDTO facility;

    // ---  Първични продукти ---
    @Builder.Default
    private Set<KeyValueDTO> primaryProductFoodTypes = new HashSet<>();
    private String primaryProductFoodTypeDescription;
    private String primaryProductLiveStockRegNumber;
    private String primaryProductFarmerIdentifier;
    private Double primaryProductEstimatedAnnualYield;

    // --- Произведените храни ---
    @Builder.Default
    private Set<KeyValueDTO> producedFoodTypes = new HashSet<>();
    private String producedFoodTypeDescription;
    private Double producedFoodEstimatedAnnual;
    private String producedFoodEstimatedAnnualUnitCode;
    private String producedFoodEstimatedAnnualUnitName;

    // --- Извършвам обработка на дивеч (попълва се само при Вид дейност = "....-чрез ЛОВ) ---
    private String huntingPartyBranch;
    private Double areaHuntingAreas;
    private String locationGameProcessingFacility;
    private Double gameStationCapacity;
    private String gameStationCapacityUnitCode;
    private String gameStationCapacityUnitName;
    private String gameProcessingPeriodCode;
    private String gameProcessingPeriodName;
    private String responsiblePeople;
    private String gameType;
    private Double permittedExtraction;

    // --- Данни за риболовните съдове:  (попълва се само при Вид дейност = "....-чрез УЛОВ") ---
    private List<FishingVesselDTO> fishingVessels = new ArrayList<>();

    // --- Осъществявам директни доставки на първични продукти ---
    private String deliveryLiveStockRegNumber;
    private String deliveryFarmerIdentifier;
    private String deliveryAddressLivestockFacility;
    private String deliveryPeriodCode;
    private String deliveryPeriodName;
    private Double deliveryFacilityCapacity;
    private String deliveryFacilityCapacityUnitCode;
    private String deliveryFacilityCapacityUnitName;

    // --- Средства за комуникация при търговия от разстояние: (попълва се при "Да") ---
    private AddressDTO address;

    // --- Данни за капацитет ---
    private Double foodAnnualCapacity;
    private String foodAnnualCapacityUnitCode;
    private String foodAnnualCapacityUnitName;
    private String foodPeriodCode;
    private String foodPeriodName;

    public static ApplicationS7691DTO of(final Record source, final Language language) {
        ApplicationS7691DTO dto = new ApplicationS7691DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS7691 application = source.getApplicationS7691();
        if (application != null) {
            BeanUtils.copyProperties(application, dto);

            if (application.getFacility() != null) {
                dto.setFacility(FacilityDTO.of(application.getFacility(), language));
            }
            if (application.getAddress() != null) {
                dto.setAddress(AddressDTO.of(application.getAddress(), language));
            }
            if (!CollectionUtils.isEmpty(application.getApplicationS7691FishingVessels())) {
                dto.setFishingVessels(FishingVesselDTO
                        .ofS7691FishingVessel(application.getApplicationS7691FishingVessels(), language));
            }
            if (application.getProducedFoodEstimatedAnnualUnit() != null) {
                dto.setProducedFoodEstimatedAnnualUnitCode(application.getProducedFoodEstimatedAnnualUnit().getCode());
                dto.setProducedFoodEstimatedAnnualUnitName(application.getProducedFoodEstimatedAnnualUnit().getI18n(language).getName());
            }
            if (application.getDeliveryPeriod() != null) {
                dto.setDeliveryPeriodCode(application.getDeliveryPeriod().getCode());
                dto.setDeliveryPeriodName(application.getDeliveryPeriod().getI18n(language).getName());
            }
            if (application.getDeliveryFacilityCapacityUnit() != null) {
                dto.setDeliveryFacilityCapacityUnitCode(application.getDeliveryFacilityCapacityUnit().getCode());
                dto.setDeliveryFacilityCapacityUnitName(application.getDeliveryFacilityCapacityUnit().getI18n(language).getName());
            }
            if (application.getGameStationCapacityUnit() != null) {
                dto.setGameStationCapacityUnitCode(application.getGameStationCapacityUnit().getCode());
                dto.setGameStationCapacityUnitName(application.getGameStationCapacityUnit().getI18n(language).getName());
            }
            if (application.getGameProcessingPeriod() != null) {
                dto.setGameProcessingPeriodCode(application.getGameProcessingPeriod().getCode());
                dto.setGameProcessingPeriodName(application.getGameProcessingPeriod().getI18n(language).getName());
            }
            if (application.getFoodAnnualCapacityUnit() != null) {
                dto.setFoodAnnualCapacityUnitCode(application.getFoodAnnualCapacityUnit().getCode());
                dto.setFoodAnnualCapacityUnitName(application.getFoodAnnualCapacityUnit().getI18n(language).getName());
            }
            if (application.getFoodPeriod() != null) {
                dto.setFoodPeriodCode(application.getFoodPeriod().getCode());
                dto.setFoodPeriodName(application.getFoodPeriod().getI18n(language).getName());
            }
            if (ApplicationStatus.ENTERED.equals(application.getStatus())) {
                dto.setPrimaryProductFoodTypes(application.getApplicationS7691PrimaryProductFoodTypes());
                dto.setProducedFoodTypes(application.getApplicationS7691ProducedFoodTypes());
            } else {
                if (!CollectionUtils.isEmpty(application.getPrimaryProductFoodTypes())) {
                    dto.setPrimaryProductFoodTypes(KeyValueDTO.ofClassifiers(application.getPrimaryProductFoodTypes(), language));
                }
                if (!CollectionUtils.isEmpty(application.getProducedFoodTypes())) {
                    dto.setProducedFoodTypes(KeyValueDTO.ofClassifiers(application.getProducedFoodTypes(), language));
                }
            }
        }

        return dto;
    }

    public static List<ApplicationS7691DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }


    // --- Parsing the JSON file to DTO ---
    public static ApplicationS7691DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS7691DTO dto = new ApplicationS7691DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setCommencementActivityDate(OffsetDateTime.parse(sc.get("commencementActivityDate").toString()));

            Object estimatedAnnualYield = sc.get("estimatedAnnualYield");
            if (estimatedAnnualYield != null) {
                dto.setPrimaryProductEstimatedAnnualYield(Double.parseDouble(estimatedAnnualYield.toString()));
            }

            Object liveStockVatNumber = sc.get("liveStockVatNumber");
            if ((liveStockVatNumber != null)) {
                dto.setPrimaryProductFarmerIdentifier(liveStockVatNumber.toString());
            }

            Object farmerVatNumber = sc.get("farmerVatNumber");
            if (farmerVatNumber != null) {
                dto.setDeliveryFarmerIdentifier(farmerVatNumber.toString());
            }

            Object annualCapacity = sc.get("annualCapacity");
            if (annualCapacity != null) {
                dto.setFoodAnnualCapacity(Double.parseDouble(annualCapacity.toString()));
            }
            if (sc.get("unit") instanceof LinkedHashMap<?, ?> capacityUnit) {
                dto.setFoodAnnualCapacityUnitCode(capacityUnit.get("value").toString());
            }
            if (sc.get("period") instanceof LinkedHashMap<?, ?> capacityPeriod) {
                dto.setFoodPeriodCode(capacityPeriod.get("value").toString());
            }

            String facilityName = sc.get("name").toString();
            if (StringUtils.hasText(facilityName)) {
                FacilityDTO facilityDTO = new FacilityDTO();
                facilityDTO.setName(facilityName);
//                if (sc.get("liveStockFacilityRegNumber") != null) {
//                    facilityDTO.setRegNumber(sc.get("liveStockFacilityRegNumber").toString());
//                    // TODO: Този номер ли трябва да се попълни
//                }
                if (sc.get("activityType") instanceof LinkedHashMap<?, ?> activityType) {
                    facilityDTO.setActivityTypeCode(activityType.get("value").toString());
                }
                if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> additional) {
                    if (additional.get("permissionForUseNumber") != null) {
                        facilityDTO.setPermission177(additional.get("permissionForUseNumber").toString());
                    }
                }

                AddressDTO addressDTO = new AddressDTO();
                if (sc.get("commonPresAddrCountrySelect") instanceof LinkedHashMap<?, ?> country) {
                    addressDTO.setCountryCode(country.get("countryCode").toString());
                }
                if (sc.get("commonPresAddrSettlementSelect") instanceof LinkedHashMap<?, ?> settlement) {
                    addressDTO.setSettlementCode(settlement.get("settlementCode").toString());
                }
                String address = "";
                if (sc.get("commonPresAddrLocationName") != null) {
                    address = sc.get("commonPresAddrLocationName").toString();
                }
                if (sc.get("commonPresAddrBuildingNumber2") != null) {
                    address = address + ", №/блок " + sc.get("commonPresAddrBuildingNumber2").toString();
                }
                if (sc.get("commonPresAddrEntrance") != null) {
                    address = address + ", вх. " + sc.get("commonPresAddrEntrance").toString();
                }
                if (sc.get("commonPresAddrFloor") != null) {
                    address = address + ", ет. " + sc.get("commonPresAddrFloor").toString();
                }
                if (sc.get("commonPresAddrApartment") != null) {
                    address = address + ", ап. " + sc.get("commonPresAddrApartment").toString();
                }
                Object fullAddressObject = sc.get("commonPresAddrFullAddress");
                if (fullAddressObject != null) {
                    String fullAddress = fullAddressObject.toString();
                    Object countryName = sc.get("commonPresAddrCountry");
                    if (countryName != null) {
                        fullAddress = countryName + ", " + fullAddress;
                    }
                    addressDTO.setFullAddress(fullAddress);
                }

                addressDTO.setAddress(address);
                facilityDTO.setAddress(addressDTO);

                dto.setFacility(facilityDTO);
            }

            Object food2 = sc.get("food2");
            if (food2 != null) {
                dto.setPrimaryProductFoodTypes(getFoodTypes(sc.get("food2"), "foodGroupsNonAnimal"));
            } else {
                dto.setPrimaryProductFoodTypes(getFoodTypes(findValue(sc, "dataGridfoods"), "foodGroups"));
            }

            dto.setProducedFoodTypes(getFoodTypes(findValue(sc, "producedFood"), "foodProduced"));

            Object foodGroupsInfo = findValue(sc, "foodGroupsInfoNonAnimal");
            if (foodGroupsInfo != null) {
                dto.setPrimaryProductFoodTypeDescription(foodGroupsInfo.toString());
            } else {
                foodGroupsInfo = findValue(sc, "foodGroupsInfo2");
                if (foodGroupsInfo != null) {
                    dto.setPrimaryProductFoodTypeDescription(foodGroupsInfo.toString());
                }
            }

            Object foodInfo = findValue(sc, "foodInfo");
            if (foodInfo != null) {
                dto.setProducedFoodTypeDescription(foodInfo.toString());
            }

            // --- Осъществявам директни доставки на първични продукти ---
            if (sc.get("farmerObjectAddress") != null) {
                dto.setDeliveryAddressLivestockFacility(sc.get("farmerObjectAddress").toString());
            }
            if (sc.get("animalFarmRegNumber") != null) {
                dto.setDeliveryLiveStockRegNumber(sc.get("animalFarmRegNumber").toString());
            }
            if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> additional) {
                if (additional.get("farmCapacity") != null) {
                    dto.setDeliveryFacilityCapacity(Double.parseDouble(additional.get("farmCapacity").toString()));
                }
                if (additional.get("farmCapacityPeriod") instanceof LinkedHashMap<?, ?> farmCapacityPeriod) {
                    dto.setDeliveryPeriodCode(farmCapacityPeriod.get("value").toString());
                }
                if (additional.get("farmCapacityUnit") instanceof LinkedHashMap<?, ?> farmCapacityUnit) {
                    dto.setDeliveryFacilityCapacityUnitCode(farmCapacityUnit.get("value").toString());
                }
                if (sc.get("farmerObjectAddress") != null) {
                    dto.setDeliveryAddressLivestockFacility(sc.get("farmerObjectAddress").toString());
                }
            }


            // --- Средства за комуникация при търговия от разстояние ---
            if (sc.get("distanceTradingFacilities").toString().equals("true")) {
                AddressDTO addressDTO = new AddressDTO();
                if (sc.get("distanceTradePhone") != null) {
                    addressDTO.setPhone(sc.get("distanceTradePhone").toString());
                }
                if (sc.get("distanceTradeWebPage") != null) {
                    addressDTO.setUrl(sc.get("distanceTradeWebPage").toString());
                }
                if (sc.get("distanceTradeEmail") != null) {
                    addressDTO.setMail(sc.get("distanceTradeEmail").toString());
                }
                Object addressOutsideBulgariaObject = sc.get("AddressOutsideBulgaria");
                if (addressOutsideBulgariaObject != null) {
                    String addressOutsideBulgaria = addressOutsideBulgariaObject.toString();
                    if (sc.get("countryCode") instanceof LinkedHashMap<?, ?> country) {
                        addressOutsideBulgaria = country.get("countryName") + ", " + addressOutsideBulgaria;
                    }

                    addressDTO.setAddress(addressOutsideBulgaria);
                    addressDTO.setFullAddress(addressOutsideBulgaria);
                }

                dto.setAddress(addressDTO);
            }


            // --- Данни за риболовните съдове:  (попълва се само при Вид дейност = "....-чрез УЛОВ") ---
            if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> additional) {
                if (additional.get("fishingBoatInfo") instanceof LinkedHashMap<?, ?> fishingVessel) {
                    dto.getFishingVessels().add(getFishingVesselsInfo(fishingVessel));
                } else if (additional.get("fishingBoatInfo") instanceof ArrayList<?> fishingVessels) {
                    for (Object fishingVessel : fishingVessels) {
                        if (fishingVessel instanceof LinkedHashMap<?, ?> vessel) {
                            dto.getFishingVessels().add(getFishingVesselsInfo(vessel));
                        }
                    }
                }
            }

            // --- Добив на първични продукти от животински произход - чрез ЛОВ ---
            if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> additional) {
                dto.setPermittedExtraction(additional.get("permittedExtraction") != null
                        ? Double.parseDouble(additional.get("permittedExtraction").toString())
                        : null);

                dto.setHuntingPartyBranch(additional.get("huntingAssociationName") != null
                        ? additional.get("huntingAssociationName").toString()
                        : null);

                dto.setResponsiblePeople(additional.get("hygieneResponsiblePersons") != null
                        ? additional.get("hygieneResponsiblePersons").toString()
                        : null);
                dto.setGameStationCapacity(additional.get("gameProcessingStationCapacity") != null
                        ? Double.parseDouble(additional.get("gameProcessingStationCapacity").toString())
                        : null);
                dto.setLocationGameProcessingFacility(additional.get("gameProcessingFacilityLocation") != null
                        ? additional.get("gameProcessingFacilityLocation").toString()
                        : null);
                dto.setAreaHuntingAreas(additional.get("huntingArea") != null
                        ? Double.parseDouble(additional.get("huntingArea").toString())
                        : null);

                if (additional.get("gameProcessingStationCapacityUnit") instanceof LinkedHashMap<?, ?> unit) {
                    dto.setGameStationCapacityUnitCode(unit.get("value").toString());
                }
                if (additional.get("gameProcessingStationCapacityPeriod") instanceof LinkedHashMap<?, ?> period) {
                    dto.setGameProcessingPeriodCode(period.get("value").toString());
                }
            }
        }

        return dto;
    }

    private static FishingVesselDTO getFishingVesselsInfo(LinkedHashMap<?, ?> fishingVessel) {
        FishingVesselDTO fishingVesselDTO = new FishingVesselDTO();

        fishingVesselDTO.setExternalMarking(fishingVessel.get("externalMarking") != null
                ? fishingVessel.get("externalMarking").toString()
                : null);
        fishingVesselDTO.setHullLength(fishingVessel.get("bodyLength") != null
                ? Double.parseDouble(fishingVessel.get("bodyLength").toString())
                : null);
        fishingVesselDTO.setRegNumber(fishingVessel.get("boatRegNumber") != null
                ? fishingVessel.get("boatRegNumber").toString()
                : null);

        if (fishingVessel.get("fishingBoatType") instanceof LinkedHashMap<?, ?> type) {
            fishingVesselDTO.setTypeCode(type.get("value").toString());
        }
        if (fishingVessel.get("fishingBoatPurpose") instanceof LinkedHashMap<?, ?> assignmentType) {
            fishingVesselDTO.setAssignmentTypeCode(assignmentType.get("value").toString());
        }

        return fishingVesselDTO;
    }

//    private static List<KeyValueDTO> getContainerInfo(Object containerObj) {
//        List<KeyValueDTO> list = new ArrayList<>();
//        String parentCode = "";
//        if (containerObj instanceof LinkedHashMap<?, ?> container) {
//            if (container.get("container") instanceof LinkedHashMap<?, ?> con) {
//                if (con.get("foodGroups") instanceof LinkedHashMap<?, ?> group) {
//                    parentCode = group.get("value").toString();
//                    String lastGroupCode = getLastGroupCode(con, parentCode);
//                    list.addAll(getKeyValues(con.get("foodGroup" + lastGroupCode)));
//                }
//            }
//        }
//        return list;
//    }

}
