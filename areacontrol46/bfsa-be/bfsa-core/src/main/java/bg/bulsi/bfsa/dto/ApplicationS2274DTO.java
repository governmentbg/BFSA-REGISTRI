package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS2274;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2274DTO extends BaseApplicationDTO {
    // 1. Притежател на разрешението за пускане на пазара и употреба на ПРЗ:
    private String permitHolderIdentifier;
    private String permitHolderName;
    private String permitHolderNameLat;
    private String permitHolderHeadquarter;
    private String permitHolderHeadquarterLat;
    private String permitHolderAddress;
    private String permitHolderAddressLat;
    private String permitHolderEmail;

    // 2. Идентификация на продукта
    private String pppName;
    private String pppCode;
    private String pppFormulationTypeCode;
    private String pppFormulationTypeName;
    private Map<String, String> pppManufacturers = new HashMap<>();
    private List<KeyValueDTO> pppFunctions = new ArrayList<>();
    private List<KeyValueDTO> pppActions = new ArrayList<>();
    private String pppCategoryCode;
    private String pppCategoryName;

    // 2.7. Идентификация на активните вещества
    private List<ApplicationS2274ActiveSubstanceDTO> activeSubstances = new ArrayList<>();
    private List<KeyValueDTO> pppActiveSubstanceTypes = new ArrayList<>();
    private String pppDescription;

    // 4. Характеристики на опаковката
    private List<ApplicationS2274PackageDTO> packages = new ArrayList<>();

    // 5. Допълнителна информация
    private List<ApplicationS2274ReferenceProductDTO> referenceProducts = new ArrayList<>();

    public static ApplicationS2274DTO of(final Record source, final Language language) {
        ApplicationS2274DTO dto = new ApplicationS2274DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS2274 application = source.getApplicationS2274();
        if (application != null) {
            BeanUtils.copyProperties(application, dto);
            if (application.getFormulationType() != null) {
                dto.setPppFormulationTypeCode(application.getFormulationType().getCode());
                dto.setPppFormulationTypeName(application.getFormulationType().getI18n(language).getName());
            }
            if (!CollectionUtils.isEmpty(application.getPlantProtectionProductFunctions())) {
                application.getPlantProtectionProductFunctions().forEach(f -> dto.getPppFunctions().add(KeyValueDTO.builder().name(f.getI18n(language).getName()).code(f.getCode()).build()));
            }
            if (!CollectionUtils.isEmpty(application.getPlantProtectionProductActions())) {
                application.getPlantProtectionProductActions().forEach(ac -> dto.getPppActions().add(KeyValueDTO.builder().name(ac.getI18n(language).getName()).code(ac.getCode()).build()));
            }
            if (application.getPlantProtectionProductCategory() != null) {
                dto.setPppCategoryCode(application.getPlantProtectionProductCategory().getCode());
                dto.setPppCategoryName(application.getPlantProtectionProductCategory().getI18n(language).getName());
            }

            List<ApplicationS2274ActiveSubstanceDTO> activeSubstances = application.getActiveSubstances();
            if (!CollectionUtils.isEmpty(activeSubstances)) {
                for (ApplicationS2274ActiveSubstanceDTO substance : activeSubstances) {
                    if (!CollectionUtils.isEmpty(substance.getPppActiveSubstanceTypes())) {
                        for (KeyValueDTO ast : substance.getPppActiveSubstanceTypes()) {
                            dto.getPppActiveSubstanceTypes().add(KeyValueDTO.builder()
                                    .code(ast.getCode())
                                    .name(ast.getName())
                                    .build());
                        }
                    }
                }
            }
        }

        return dto;
    }

    public static List<ApplicationS2274DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS2274DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2274DTO dto = new ApplicationS2274DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> specificContent) {
            if (specificContent.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
                dto.getPppActions().addAll(getActions(sc));
                dto.getPppFunctions().addAll(getFunctions(sc));
                dto.getActiveSubstances().addAll(getSubstances(sc, dto));

                List<ApplicationS2274ReferenceProductDTO> products = getRefProducts(sc);
                if (!CollectionUtils.isEmpty(products)) {
                    dto.getReferenceProducts().addAll(products);
                }

                dto.setPermitHolderHeadquarter(sc.get("permitHolderHeadquarter") != null
                        ? sc.get("permitHolderHeadquarter").toString()
                        : null);
                dto.setPermitHolderHeadquarterLat(sc.get("permitHolderHeadquarterLat") != null
                        ? sc.get("permitHolderHeadquarterLat").toString()
                        : null);
                dto.setPermitHolderName(sc.get("permitHolderName") != null
                        ? sc.get("permitHolderName").toString()
                        : null);
                dto.setPermitHolderNameLat(sc.get("permitHolderNameLat") != null
                        ? sc.get("permitHolderNameLat").toString()
                        : null);
                dto.setPermitHolderIdentifier(sc.get("permitHolderIdentifier") != null
                        ? sc.get("permitHolderIdentifier").toString()
                        : null);
                dto.setPermitHolderAddressLat(sc.get("permitHolderAddressLat") != null
                        ? sc.get("permitHolderAddressLat").toString()
                        : null);
                dto.setPermitHolderAddress(sc.get("permitHolderAddress") != null
                        ? sc.get("permitHolderAddress").toString()
                        : null);
                dto.setPermitHolderEmail(sc.get("permitHolderEmail") != null
                        ? sc.get("permitHolderEmail").toString()
                        : null);

                dto.setPppName(sc.get("pppName") != null
                        ? sc.get("pppName").toString()
                        : null);
                dto.setPppCode(sc.get("pppCode") != null
                        ? sc.get("pppCode").toString()
                        : null);
                dto.setPppDescription(sc.get("pppDescription") != null
                        ? sc.get("pppDescription").toString()
                        : null);

                if (sc.get("pppFormulationType") instanceof LinkedHashMap<?, ?> formulationType) {
                    dto.setPppFormulationTypeCode(formulationType.get("value").toString());
                }
                if (sc.get("pppCategory") instanceof LinkedHashMap<?, ?> category) {
                    dto.setPppCategoryCode(category.get("value").toString());
                }
                if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> additional) {
                    dto.getPackages().addAll(getPackagesFromAdditional(additional));
                    if (additional.get("pppManufacturers") != null) {
                        dto.setPppManufacturers(getManufactures(additional.get("pppManufacturers")));
                    }
                }
            }
        }
        return dto;
    }

    private static Map<String, String> getManufactures(Object manufacturesObj) {
        Map<String, String> map = null;
        if (manufacturesObj instanceof LinkedHashMap<?, ?> manufacture) {
            map = new HashMap<>(getManufacture(manufacture));
        } else if (manufacturesObj instanceof ArrayList<?> manufactures) {
            map = new HashMap<>();
            for (var manufacture : manufactures) {
                if (manufacture instanceof LinkedHashMap<?, ?> man) {
                    map.putAll(getManufacture(man));
                }
            }
        }
        return map;
    }

    private static Map<String, String> getManufacture(LinkedHashMap<?, ?> manufacture) {
        Map<String, String> map = null;
        if (manufacture.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
            map = new HashMap<>();
            String name = sc.get("pppManufacturerName") != null
                    ? sc.get("pppManufacturerName").toString()
                    : null;
            String nameLat = sc.get("pppManufacturerNameLat") != null
                    ? sc.get("pppManufacturerNameLat").toString()
                    : null;
            map.put(name, nameLat);
        }
        return map;
    }

    private static List<ApplicationS2274PackageDTO> getPackagesFromAdditional(LinkedHashMap<?, ?> additional) {
        List<ApplicationS2274PackageDTO> list = null;
        if (additional.get("packageDataGrid") instanceof LinkedHashMap<?, ?> grid) {
            list = new ArrayList<>();
            list.add(getPackage(grid));
        } else if (additional.get("packageDataGrid") instanceof ArrayList<?> grids) {
            list = new ArrayList<>();
            for (var grid : grids) {
                if (grid instanceof LinkedHashMap<?, ?> gr) {
                    list.add(getPackage(gr));
                }
            }
        }
        return list;
    }

    private static ApplicationS2274PackageDTO getPackage(LinkedHashMap<?, ?> grid) {
        ApplicationS2274PackageDTO dto = new ApplicationS2274PackageDTO();

        if (grid.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
            dto.setDescription(sc.get("packageNotes") != null
                    ? sc.get("packageNotes").toString()
                    : null);
            dto.setMaterial(sc.get("packageMaterial") != null
                    ? sc.get("packageMaterial").toString()
                    : null);
            dto.setType(sc.get("packageType") != null
                    ? sc.get("packageType").toString()
                    : null);
            dto.setSize(sc.get("packageSizes") != null
                    ? sc.get("packageSizes").toString()
                    : null);
        }
        if (grid.get("quantityDataGrid") instanceof LinkedHashMap<?, ?> quantityDataGrid) {
            dto.getQuantities().addAll(getQuantityMap(quantityDataGrid));
        } else if (grid.get("quantityDataGrid") instanceof ArrayList<?> quantityDataGrids) {
            for (Object quantity : quantityDataGrids) {
                if (quantity instanceof LinkedHashMap<?, ?> q) {
                    dto.getQuantities().addAll(getQuantityMap(q));
                }
            }
        }
        return dto;
    }

    private static List<KeyValueDTO> getQuantityMap(LinkedHashMap<?, ?> quantity) {
        List<KeyValueDTO> list = null;
        if (quantity.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
            list = new ArrayList<>();
            Double q = sc.get("packageQuantity") != null
                    ? Double.parseDouble(sc.get("packageQuantity").toString())
                    : null;
            String unitCode = null;
            if (sc.get("packageUnit") instanceof LinkedHashMap<?, ?> unit) {
                unitCode = unit.get("value").toString();
            }
            KeyValueDTO keyValue = KeyValueDTO.builder().code(unitCode).number(q).build();
            list.add(keyValue);
        }
        return list;
    }

    private static List<ApplicationS2274ActiveSubstanceDTO> getSubstances(LinkedHashMap<?, ?> sc, ApplicationS2274DTO dto) {
        List<ApplicationS2274ActiveSubstanceDTO> list = null;
        if (sc.get("substanceGrid") instanceof LinkedHashMap<?, ?> substanceGrid) {
            list = new ArrayList<>();
            if (substanceGrid.get("specificContent") instanceof LinkedHashMap<?, ?> specific) {
                getSubstance(specific, dto);
            }
        } else if (sc.get("substanceGrid") instanceof ArrayList<?> substanceGrid) {
            list = new ArrayList<>();
            for (var substance : substanceGrid) {
                if (substance instanceof LinkedHashMap<?, ?> sub) {
                    if (sub.get("specificContent") instanceof LinkedHashMap<?, ?> specific) {
                        getSubstance(specific, dto);
                    }
                }
            }
        }
        return list;
    }

    private static void getSubstance(LinkedHashMap<?, ?> sc, ApplicationS2274DTO dto) {
        ApplicationS2274ActiveSubstanceDTO substanceDTO = new ApplicationS2274ActiveSubstanceDTO();
        substanceDTO.setManufacturer(sc.get("substanceManufacturer") != null
                ? sc.get("substanceManufacturer").toString()
                : null);
        substanceDTO.setManufacturerLat(sc.get("substanceManufacturerLat") != null
                ? sc.get("substanceManufacturerLat").toString()
                : null);
        substanceDTO.setSubstanceQuantity(sc.get("substanceQuantity") != null
                ? Double.parseDouble(sc.get("substanceQuantity").toString())
                : null);

        if (sc.get("packageUnit") instanceof LinkedHashMap<?, ?> unit) {
            substanceDTO.setSubstanceQuantityUnitCode(unit.get("value").toString());
        }
        if (sc.get("activeSubstance") instanceof LinkedHashMap<?, ?> activeSubstance) {
            substanceDTO.setActiveSubstanceCode(activeSubstance.get("value").toString());
            substanceDTO.setActiveSubstanceName(activeSubstance.get("label").toString());
        }
        if (sc.get("substanceManufacturerCountry") instanceof LinkedHashMap<?, ?> country) {
            substanceDTO.setManufacturerCountryCode(country.get("value").toString());
        }
        if (sc.get("pppActiveSubstanceTypes") != null) {
            dto.getPppActiveSubstanceTypes().addAll(getKeyValues(sc.get("pppActiveSubstanceTypes")));
        }
        dto.getActiveSubstances().add(substanceDTO);
    }

    private static List<ApplicationS2274ReferenceProductDTO> getRefProducts(LinkedHashMap<?, ?> sc) {
        List<ApplicationS2274ReferenceProductDTO> list = new ArrayList<>();;
        if (sc.get("bgReferenceProducts") instanceof LinkedHashMap<?, ?> referenceProduct) {
            list.add(getProduct(referenceProduct));
        } else if (sc.get("bgReferenceProducts") instanceof ArrayList<?> referenceProducts) {
            for (Object product : referenceProducts) {
                if (product instanceof LinkedHashMap<?, ?> prod) {
                    list.add(getProduct(prod));
                }
            }
        }
        if (sc.get("euReferenceProducts") instanceof LinkedHashMap<?, ?> referenceProduct) {
            list.add(getProduct(referenceProduct));
        } else if (sc.get("euReferenceProducts") instanceof ArrayList<?> referenceProducts) {
            for (Object product : referenceProducts) {
                if (product instanceof LinkedHashMap<?, ?> prod) {
                    list.add(getProduct(prod));
                }
            }
        }
        return !list.isEmpty() ? list : null;
    }

    private static ApplicationS2274ReferenceProductDTO getProduct(LinkedHashMap<?, ?> referenceProduct) {
        ApplicationS2274ReferenceProductDTO dto = new ApplicationS2274ReferenceProductDTO();
        if (referenceProduct.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
            dto.setPermitNumber(sc.get("euPermitNumber") != null
                    ? sc.get("euPermitNumber").toString()
                    : null);
            dto.setPermitDate(sc.get("euPermitDate") != null
                    ? OffsetDateTime.parse(sc.get("euPermitDate").toString())
                    : null);
            dto.setPermitValidUntilDate(sc.get("euPermitValidUntilDate") != null
                    ? OffsetDateTime.parse(sc.get("euPermitValidUntilDate").toString())
                    : null);
            dto.setTradeName(sc.get("bgTradeName") != null
                    ? sc.get("bgTradeName").toString()
                    : null);
            if (sc.get("euCountry") instanceof LinkedHashMap<?, ?> country) {
                dto.setCountryCode(country.get("value").toString());
                dto.setTradeName(sc.get("euTradeName") != null
                        ? sc.get("euTradeName").toString()
                        : null);
            }
            if (sc.get("bgReferenceProduct") != null) {
                dto.setBgReferenceProduct(sc.get("bgReferenceProduct").toString());
                dto.setCountryCode("BG");
            }
        }
        return dto;
    }

    private static List<KeyValueDTO> getActions(LinkedHashMap<?, ?> sc) {
        List<KeyValueDTO> list = new ArrayList<>();
        if (sc.get("pppActions") instanceof LinkedHashMap<?, ?> action) {
            list.add(KeyValueDTO.builder().code(action.get("value").toString()).build());
        } else if (sc.get("pppActions") instanceof ArrayList<?> actions) {
            for (Object action : actions) {
                if (action instanceof LinkedHashMap<?, ?> act) {
                    list.add(KeyValueDTO.builder().code(act.get("value").toString()).build());
                }
            }
        }
        return list;
    }

    private static List<KeyValueDTO> getFunctions(LinkedHashMap<?, ?> sc) {
        List<KeyValueDTO> list = new ArrayList<>();
        if (sc.get("pppFunctions") instanceof LinkedHashMap<?, ?> function) {
            list.add(KeyValueDTO.builder().code(function.get("value").toString()).build());
        } else if (sc.get("pppFunctions") instanceof ArrayList<?> functions) {
            for (Object function : functions) {
                if (function instanceof LinkedHashMap<?, ?> fun) {
                    list.add(KeyValueDTO.builder().code(fun.get("value").toString()).build());
                }
            }
        }
        return list;
    }
}
