package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS3125;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.util.Constants;
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
public class ApplicationS3125DTO extends BaseApplicationDTO {
    private List<FoodSupplementDTO> foodSupplements = new ArrayList<>();
    private OffsetDateTime commencementActivityDate;
    private String applicantTypeCode;
    private String applicantTypeName;
    private AddressDTO distanceTradingAddress;

    public static ApplicationS3125DTO ofRecord(final Record source, final Language language) {
        ApplicationS3125DTO dto = new ApplicationS3125DTO();

        dto.ofRecordBase(source, language);

        ApplicationS3125 application = source.getApplicationS3125();
        if (application != null) {
            if (application.getApplicantType() != null) {
                dto.setApplicantTypeCode(application.getApplicantType().getCode());
                dto.setApplicantTypeName(application.getApplicantType().getI18n(language).getName());
            }

            dto.setDistanceTradingAddress(application.getDistanceTradingAddress());

            if (!CollectionUtils.isEmpty(application.getFoodSupplements())) {
                dto.getFoodSupplements().addAll(FoodSupplementDTO.of(application.getFoodSupplements(), language));
            }
            dto.setCommencementActivityDate(application.getCommencementActivityDate());
        }

        return dto;
    }

    public static ApplicationS3125DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS3125DTO dto = new ApplicationS3125DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            if (sc.get("productionFacilitiesData") != null) {
                dto.getFoodSupplements().addAll(getFacilities(sc.get("productionFacilitiesData")));
            }
            if (sc.get("businessOperator") instanceof LinkedHashMap<?, ?> operatorType) {
                dto.setApplicantTypeCode(operatorType.get("value").toString());
            }
            if (sc.get("companyName") != null) {
                String manufactureCompanyName = sc.get("companyName").toString();
                dto.getFoodSupplements()
                        .forEach(fs -> {
                            fs.setManufactureCompanyName(manufactureCompanyName);
                            fs.setManufactureFacilityAddress(manufactureCompanyName + "; " + fs.getManufactureFacilityAddress());
                        });
            }

            if (findValue(sc, "distanceTradingAddress") instanceof LinkedHashMap<?, ?> distanceTradingAddress) {
                AddressDTO addressDTO = BaseApplicationDTO.getAddress(distanceTradingAddress);

                Object distanceTradingEmail = findValue(sc, "remoteTradingEmail");
                if (distanceTradingEmail != null) {
                    addressDTO.setMail(distanceTradingEmail.toString());
                }
                Object distanceTradingPhone = findValue(sc, "remoteTradingPhone");
                if (distanceTradingPhone != null) {
                    addressDTO.setPhone(distanceTradingPhone.toString());
                }
                Object distanceTradingUrl = findValue(sc, "remoteTradingWebPage");
                if (distanceTradingUrl != null) {
                    addressDTO.setUrl(distanceTradingUrl.toString());
                }
                dto.setDistanceTradingAddress(addressDTO);
            }

            dto.setCommencementActivityDate(OffsetDateTime.parse(sc.get("commencementActivityDate").toString()));
        }

        return dto;
    }

    private static List<FoodSupplementDTO> getFacilities(final Object facilitiesDataObj) {
        List<FoodSupplementDTO> list = new ArrayList<>();
        if (facilitiesDataObj instanceof LinkedHashMap<?, ?> facilityData) {
            list.addAll(getFoodSupplements(facilityData));
        } else if (facilitiesDataObj instanceof ArrayList<?> facilitiesData) {
            facilitiesData.forEach(p -> list.addAll(getFoodSupplements((LinkedHashMap<?, ?>) p)));
        }
        return list;
    }

    private static List<FoodSupplementDTO> getFoodSupplements(LinkedHashMap<?, ?> facilityData) {
        String addressProduction = "";
        String addressDistribution = "";
        String facilityTypeCode = "";

        if (facilityData.get("productionObjectAddress") != null) { // productionObjectAddress
            addressProduction = BaseApplicationDTO.getAddress(facilityData.get("productionObjectAddress")).getFullAddress();
        }
        if (facilityData.get("distributionObjectAddress") != null) { // distributionObjectAddress
            addressDistribution = BaseApplicationDTO.getAddress(facilityData.get("distributionObjectAddress")).getFullAddress();
        }

        List<FoodSupplementDTO> foodSupplementDTOS = new ArrayList<>();

        Object facilitySpecificObj = facilityData.get("specificContent");
        if (facilitySpecificObj instanceof LinkedHashMap<?, ?> facilitySpecific) {
            Object mobileFacilityTypeObj = facilitySpecific.get("mobileFacilityType");
            if (mobileFacilityTypeObj == null) {
                facilityTypeCode = Constants.FACILITY_TYPE_PRODUCTION_CODE;
            } else if (mobileFacilityTypeObj instanceof LinkedHashMap<?, ?> mobileFacilityType) {
                facilityTypeCode = mobileFacilityType.get("value").toString();
            }
            Object descriptionFoodObj = facilitySpecific.get("descriptionFood");
            if (descriptionFoodObj instanceof LinkedHashMap<?, ?> descriptionFood) {
                FoodSupplementDTO foodSupplementDTO = getFoodSupplement(descriptionFood);
                foodSupplementDTO.setFacilityTypeCode(facilityTypeCode);
                foodSupplementDTO.setManufactureFacilityAddress(addressProduction);
                foodSupplementDTO.setDistributionFacilityAddress(addressDistribution);
                foodSupplementDTOS.add(foodSupplementDTO);
            } else if (descriptionFoodObj instanceof ArrayList<?> descriptionFoods) {
                for (Object p : descriptionFoods) {
                    FoodSupplementDTO foodSupplementDTO = getFoodSupplement((LinkedHashMap<?, ?>) p);
                    foodSupplementDTO.setFacilityTypeCode(facilityTypeCode);
                    foodSupplementDTO.setManufactureFacilityAddress(addressProduction);
                    foodSupplementDTO.setDistributionFacilityAddress(addressDistribution);
                    foodSupplementDTOS.add(foodSupplementDTO);
                }
            }
        }

        return foodSupplementDTOS;
    }

    private static FoodSupplementDTO getFoodSupplement(LinkedHashMap<?, ?> descriptionFood) {
        Object decrFoodSpec = descriptionFood.get("specificContent");
        FoodSupplementDTO supplementDTO = new FoodSupplementDTO();
        if (decrFoodSpec instanceof LinkedHashMap<?, ?> foodSpec) {
            if (foodSpec.get("foodSupplementIngredients") != null) {
                supplementDTO.setIngredients(foodSpec.get("foodSupplementIngredients").toString());
            }
            if (foodSpec.get("foodSupplementUnit") != null) {
                Object fsu = foodSpec.get("foodSupplementUnit");
                if (fsu instanceof LinkedHashMap<?, ?> unit) {
                    if (unit.get("value") != null) {
                        supplementDTO.setMeasuringUnitCode(unit.get("value").toString());
                    }
                }
            }
            if (foodSpec.get("foodSupplementUnit2") != null) {
                Object fsu = foodSpec.get("foodSupplementUnit2");
                if (fsu instanceof LinkedHashMap<?, ?> unit) {
                    if (unit.get("value") != null) {
                        supplementDTO.setMeasuringUnitCode(unit.get("value").toString());
                    }
                }
            }
            if (foodSpec.get("foodType") != null) {
                Object fsu = foodSpec.get("foodType");
                if (fsu instanceof LinkedHashMap<?, ?> unit) {
                    if (unit.get("value") != null) {
                        supplementDTO.setFoodSupplementTypeCode(unit.get("value").toString());
                    }
                }
            }
            Object countriesObj = foodSpec.get("country");
            if (countriesObj instanceof LinkedHashMap<?, ?> country) {
                supplementDTO.getCountries().add(KeyValueDTO.builder().code(country.get("value").toString()).build());
            } else if (countriesObj instanceof ArrayList<?> countries) {
                for (var country : countries) {
                    if (country instanceof LinkedHashMap<?, ?> c) {
                        supplementDTO.getCountries().add(KeyValueDTO.builder().code(c.get("value").toString()).build());
                    }
                }
            }
            if (foodSpec.get("foodSupplementName") != null) {
                supplementDTO.setName(foodSpec.get("foodSupplementName").toString());
            }
            if (foodSpec.get("foodSupplementUse") != null) {
                supplementDTO.setPurpose(foodSpec.get("foodSupplementUse").toString());
            }
            if (foodSpec.get("foodSupplementDoseQuantity") != null) {
                supplementDTO.setQuantity(Double.valueOf(
                        foodSpec.get("foodSupplementDoseQuantity").toString()));
            }
        }

        return supplementDTO;
    }
}
