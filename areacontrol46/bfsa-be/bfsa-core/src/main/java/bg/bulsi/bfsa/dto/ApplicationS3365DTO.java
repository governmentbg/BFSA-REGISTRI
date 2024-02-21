package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS3365;
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
public class ApplicationS3365DTO extends BaseApplicationDTO {
    private CropBO cropType;
    private List<ProductionLandBO> productionLocations = new ArrayList<>();

    public static ApplicationS3365DTO ofRecord(Record source, Language language) {
        ApplicationS3365DTO dto = new ApplicationS3365DTO();
        dto.ofRecordBase(source, language);

        ApplicationS3365 application = source.getApplicationS3365();

        if (application != null) {
            dto.setCropType(application.getCropType());

            if (!CollectionUtils.isEmpty(application.getProductionLocations())) {
                dto.getProductionLocations().addAll(application.getProductionLocations());
            }
        }

        return dto;
    }

    public static ApplicationS3365DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS3365DTO dto = new ApplicationS3365DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setCropType(getCropType(sc));

            Object productionLocationObj = findValue(sc, "placesDataGrid");
            List<ProductionLandBO> productionLands = getProductions(productionLocationObj);
            dto.getProductionLocations().addAll(productionLands);
        }

        return dto;
    }

    private static List<ProductionLandBO> getProductions(final Object placesDataGridObj) {
        List<ProductionLandBO> productionLands = new ArrayList<>();

        if (placesDataGridObj instanceof LinkedHashMap<?, ?> land) {
            productionLands.add(getProduction(land));
        } else if (placesDataGridObj instanceof ArrayList<?> t) {
            t.forEach(land -> productionLands.add(getProduction(land)));
        }

        return productionLands;
    }

    private static ProductionLandBO getProduction(Object placesDataGridObj) {
        ProductionLandBO bo = new ProductionLandBO();
        List<ProductionBO> lands = new ArrayList<>();

        if (placesDataGridObj instanceof LinkedHashMap<?, ?> placesDataGrid) {
            if (placesDataGrid.get("address") instanceof LinkedHashMap<?, ?> address) {
                String land = findValue(placesDataGrid, "land").toString();
                if (land != null) {
                    bo.setLand(Double.parseDouble(land));
                }

                Object cadastreNumber = findValue(placesDataGrid, "cadastreNumber");
                if (cadastreNumber != null) {
                    bo.setCadastreNumber(cadastreNumber.toString());
                }

                if (address.get("SettlementSelect") instanceof LinkedHashMap<?, ?> settlementSelect) {
                    bo.setSettlementCode(settlementSelect.get("settlementCode") != null
                            ? settlementSelect.get("settlementCode").toString()
                            : null);
                    bo.setSettlementName(settlementSelect.get("settlementName") != null
                            ? settlementSelect.get("settlementName").toString()
                            : null);
                }

                String district = address.get("District") != null
                        ? address.get("District").toString()
                        : null;

                String municipality = address.get("Municipality") != null
                        ? address.get("Municipality").toString()
                        : null;
                bo.setFullAddress("Област " + district + ", община " + municipality + ", " + bo.getSettlementName());

                if (address.get("specificContent") instanceof LinkedHashMap<?, ?> specificContent) {
                    //                    Year One
                    ProductionBO production = new ProductionBO();
                    production.setYear(specificContent.get("yearOne") != null
                            ? Integer.parseInt(specificContent.get("yearOne").toString().substring(6))
                            : null);
                    if (specificContent.get("plantTypeCode1") instanceof LinkedHashMap<?, ?> plantType) {
                        production.setPlantTypeCode(plantType.get("value") != null
                                ? plantType.get("value").toString()
                                : null);
                        production.setPlantTypeName(plantType.get("label") != null
                                ? plantType.get("label").toString()
                                : null);
                    }
                    lands.add(production);

                    //                    Year Two
                    production = new ProductionBO();
                    ;
                    production.setYear(specificContent.get("yearTwo2") != null
                            ? Integer.parseInt(specificContent.get("yearTwo2").toString().substring(6))
                            : null);
                    if (specificContent.get("plantTypeCode2") instanceof LinkedHashMap<?, ?> plantType) {
                        production.setPlantTypeCode(plantType.get("value") != null
                                ? plantType.get("value").toString()
                                : null);
                        production.setPlantTypeName(plantType.get("label") != null
                                ? plantType.get("label").toString()
                                : null);
                    }
                    lands.add(production);

                    //                    Year Three
                    production = new ProductionBO();
                    ;
                    production.setYear(specificContent.get("yearThree") != null
                            ? Integer.parseInt(specificContent.get("yearThree").toString().substring(6))
                            : null);
                    if (specificContent.get("plantTypeCode3") instanceof LinkedHashMap<?, ?> plantType) {
                        production.setPlantTypeCode(plantType.get("value") != null
                                ? plantType.get("value").toString()
                                : null);
                        production.setPlantTypeName(plantType.get("label") != null
                                ? plantType.get("label").toString()
                                : null);
                    }
                    lands.add(production);

                    //                    Year Four
                    production = new ProductionBO();
                    ;
                    production.setYear(specificContent.get("yearFour") != null
                            ? Integer.parseInt(specificContent.get("yearFour").toString().substring(6))
                            : null);
                    if (specificContent.get("plantTypeCode4") instanceof LinkedHashMap<?, ?> plantType) {
                        production.setPlantTypeCode(plantType.get("value") != null
                                ? plantType.get("value").toString()
                                : null);
                        production.setPlantTypeName(plantType.get("label") != null
                                ? plantType.get("label").toString()
                                : null);
                    }
                    lands.add(production);

                    //                    Year Five
                    production = new ProductionBO();
                    ;
                    production.setYear(specificContent.get("yearFive") != null
                            ? Integer.parseInt(specificContent.get("yearFive").toString().substring(6))
                            : null);
                    if (specificContent.get("plantTypeCode5") instanceof LinkedHashMap<?, ?> plantType) {
                        production.setPlantTypeCode(plantType.get("value") != null
                                ? plantType.get("value").toString()
                                : null);
                        production.setPlantTypeName(plantType.get("label") != null
                                ? plantType.get("label").toString()
                                : null);
                    }
                    lands.add(production);

                }
            }
        }
        bo.getProductions().addAll(lands);

        return bo;

    }

    private static CropBO getCropType(final LinkedHashMap<?, ?> sc) {
        CropBO bo = new CropBO();

        if (sc.get("cultureCode") instanceof LinkedHashMap<?, ?> cultureTypeCode) {
            bo.setCropTypeCode(cultureTypeCode.get("value") != null
                    ? cultureTypeCode.get("value").toString()
                    : null);
            bo.setCropTypeName(cultureTypeCode.get("label") != null
                    ? cultureTypeCode.get("label").toString()
                    : null);
        }

        if (sc.get("cultureCode" + bo.getCropTypeCode()) instanceof LinkedHashMap<?, ?> cultureCode) {
            bo.setCropCode(cultureCode.get("value") != null
                    ? cultureCode.get("value").toString()
                    : null);
            bo.setCropName(cultureCode.get("label") != null
                    ? cultureCode.get("label").toString()
                    : null);
        }

        return bo;
    }


}
