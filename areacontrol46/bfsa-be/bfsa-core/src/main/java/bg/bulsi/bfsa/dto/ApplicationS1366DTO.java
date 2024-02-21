package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS1366;
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
import org.springframework.util.StringUtils;

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
public class ApplicationS1366DTO extends BaseApplicationDTO {
    // Common
    private String recipientName;
    private String recipientCountryCode;
    private String recipientCountryName;
    private String recipientAddress;
    private String facilityRegNumber;
//    private FacilityDTO facility;
    // Health
    private String applicantTypeCode;
    private String applicantTypeName;
    // Veterinary
    private String borderCrossingCode;
    private String borderCrossingName;



    //TODO: have to take which certificate type is VET or HEALTH
//    private String certificateType; // == 1 , 2

    private List<ApplicationS1366ProductDTO> products = new ArrayList<>();

    public static ApplicationS1366DTO ofRecord(final Record source, final Language language) {

        ApplicationS1366DTO dto = new ApplicationS1366DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

//        --- Set ApplicationS1366 ---
        ApplicationS1366 application = source.getApplicationS1366();
        if (application != null) {
            BeanUtils.copyProperties(application, dto);

            if (application.getRecipientCountry() != null) {
                dto.setRecipientCountryCode(application.getRecipientCountry().getCode());
                dto.setRecipientCountryName(application.getRecipientCountry().getI18n(language).getName());
            }
            if (application.getApplicantType() != null) {
                dto.setApplicantTypeCode(application.getApplicantType().getCode());
                dto.setApplicantTypeName(application.getApplicantType().getI18n(language).getName());
            }
            if (application.getBorderCrossing() != null) {
                dto.setBorderCrossingCode(application.getBorderCrossing().getCode());
                dto.setBorderCrossingName(application.getBorderCrossing().getI18n(language).getName());
            }

            if (application.getFacility() != null) {
                dto.setFacilityRegNumber(application.getFacility().getRegNumber());

//                dto.setFacility(FacilityDTO.ofRecord(application.getFacility(), language));
//                TODO: What is necessary to be added for Mitko
//                dto.setFacility(FacilityDTO.builder()
//
//                        .build());
            }

            dto.setProducts(ApplicationS1366ProductDTO.of(application.getApplicationS1366Products(), application.getStatus(), language));
        }

        return dto;
    }

    public static ApplicationS1366DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS1366DTO dto = new ApplicationS1366DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            // Common
            Object objectRegNumber = sc.get("objectRegNumber");
            if (objectRegNumber != null) {
                dto.setFacilityRegNumber(objectRegNumber.toString());
            }
            Object recipientName = sc.get("recipientName");
            if (recipientName != null) {
                dto.setRecipientName(recipientName.toString());
            }
            if (sc.get("recipientCountryCode") instanceof LinkedHashMap<?, ?> recipientCountry) {
                dto.setRecipientCountryCode(recipientCountry.get("value").toString());
                dto.setRecipientCountryName(recipientCountry.get("label").toString());
            }
            if (sc.get("recipientAddress") != null) {
                dto.setRecipientAddress(sc.get("recipientAddress").toString());
            }
            // Health
            if (sc.get("operatorTypeCode") instanceof LinkedHashMap<?, ?> operatorType) {
                dto.setApplicantTypeCode(operatorType.get("value").toString());
                dto.setApplicantTypeName(operatorType.get("label").toString());
            }
            if (sc.get("borderCrossingCode") instanceof LinkedHashMap<?, ?> borderCrossing) {
                dto.setBorderCrossingCode(borderCrossing.get("value").toString());
                dto.setBorderCrossingName(borderCrossing.get("label").toString());
            }
            dto.setProducts(getProducts(sc.get("producedFood")));
        }
        return dto;
    }

    private static List<ApplicationS1366ProductDTO> getProducts(final Object obj) {
        List<ApplicationS1366ProductDTO> products = new ArrayList<>();
        if (obj instanceof LinkedHashMap<?, ?> product) {
            products.add(getProduct(product));
        } else if (obj instanceof ArrayList<?> product) {
            for (Object o : product) {
                products.add(getProduct(o));
            }
        }
        return products;
    }

    private static ApplicationS1366ProductDTO getProduct(final Object obj) {
        ApplicationS1366ProductDTO dto = null;
        if (obj instanceof LinkedHashMap<?, ?> root) {
            String code = null;
            if (root.get("specificContent") instanceof LinkedHashMap<?, ?> product) {
                dto = new ApplicationS1366ProductDTO();

                Object foodTypeParent = product.get("foodProducedCertificate1");
                if (foodTypeParent == null) {
                    foodTypeParent = product.get("foodProducedCertificate2");
                }

                if (foodTypeParent instanceof LinkedHashMap<?, ?> foodTypeParentCode) {
                    Object value = foodTypeParentCode.get("value");
                    if (value != null) {
                        code = getLastGroupCode(root, value.toString());
                    }
                }

                if (StringUtils.hasText(code)) {
                    dto.setFoodTypes(getFoodTypes(root.get("foodGroup" + code)));
                }

                if (product.get("foodName") != null) {
                    dto.setProductName(product.get("foodName").toString());
                }
                if (product.get("foodTradeMark") != null) {
                    dto.setProductTrademark(product.get("foodTradeMark").toString());
                }
                if (product.get("foodTotalNetWeight") != null) {
                    dto.setProductTotalNetWeight(Double.parseDouble(product.get("foodTotalNetWeight").toString()));
                }
                if (product.get("productTotalNetWeightUnit") instanceof LinkedHashMap<?, ?> unit) {
                    dto.setProductTotalNetWeightUnitCode(unit.get("value").toString());
                    dto.setProductTotalNetWeightUnitName(unit.get("label").toString());
                }
                if (findValue(product, "foodCountryCode") instanceof LinkedHashMap<?, ?> lhm &&
                        lhm.get("CountrySelect") instanceof LinkedHashMap<?, ?> foodCountry) {
                    dto.setProductCountryCode(foodCountry.get("countryCode").toString());
                    dto.setProductCountryName(foodCountry.get("countryName").toString());
                }
                Object productExpiryDate = findValue(product, "foodExpirityDate");
                if (productExpiryDate != null) {
                    dto.setProductExpiryDate(OffsetDateTime.parse(productExpiryDate.toString()));
                }
                Object productManufactureDate = findValue(product, "foodManifactureDate");
                if (productManufactureDate != null) {
                    dto.setProductManufactureDate(OffsetDateTime.parse(productManufactureDate.toString()));
                }
                Object productPackageType = findValue(product, "foodPackageType");
                if (productPackageType != null) {
                    dto.setProductPackageType(productPackageType.toString());
                }

                Object batchData = product.get("partidaData");
                if (batchData != null) {
                    dto.setBatches(getBatches(batchData));
                } else {
                    batchData = product.get("partidaDataHealthSertificate");
                    dto.setBatches(List.of(getBatch(batchData)));
                }
            }

        }
        return dto;
    }

    private static List<ApplicationS1366ProductBatchDTO> getBatches(final Object obj) {
        List<ApplicationS1366ProductBatchDTO> batches = new ArrayList<>();
        if (obj instanceof LinkedHashMap<?, ?> batch) {
            batches.add(getBatch(batch.get("container")));
        } else if (obj instanceof ArrayList<?> batch) {
            for (Object o : batch) {
                batches.add(getBatch(((LinkedHashMap<?, ?>)o).get("container")));
            }
        }
        return batches;
    }

    private static ApplicationS1366ProductBatchDTO getBatch(final Object obj) {
        ApplicationS1366ProductBatchDTO dto = null;
        if (obj instanceof LinkedHashMap<?, ?> lhm && lhm.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
            dto = new ApplicationS1366ProductBatchDTO();
            Object batchNumber = sc.get("foodBatchNumber");
            if (batchNumber != null) {
                dto.setBatchNumber(batchNumber.toString());
            }
            Object perUnitNetWeight = sc.get("foodNetWeightPerUnit");
            if (perUnitNetWeight != null) {
                dto.setPerUnitNetWeight(Double.parseDouble(perUnitNetWeight.toString()));
            }
            if (sc.get("perUnitNetWeightUnit") instanceof LinkedHashMap<?, ?> unit) {
                dto.setPerUnitNetWeightUnitCode(unit.get("value").toString());
                dto.setPerUnitNetWeightUnitName(unit.get("label").toString());
            }
            Object batchNetWeight = sc.get("foodNetWeightPerBatch");
            if (batchNetWeight != null) {
                dto.setBatchNetWeight(Double.parseDouble(batchNetWeight.toString()));
            }
            if (sc.get("batchNetWeightUnit") instanceof LinkedHashMap<?, ?> unit) {
                dto.setBatchNetWeightUnitCode(unit.get("value").toString());
                dto.setBatchNetWeightUnitName(unit.get("label").toString());
            }
            Object unitsInBatch = sc.get("foodUnitsInBatch");
            if (unitsInBatch != null) {
                dto.setUnitsInBatch(Integer.parseInt(unitsInBatch.toString()));
            }
        }
        return dto;
    }
}
