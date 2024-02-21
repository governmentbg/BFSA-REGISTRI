package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ApplicationS2170;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
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
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2170DTO extends BaseApplicationDTO {

    //    1. Име на лицето /търговеца, пускащо продукта на пазара:
    private ContractorDTO supplier;

    //    2. Идентифициране на производителя
    private ContractorDTO manufacturer;

    //    2.2. Място на производство
    private AddressBO manufactureAddress;
    private String manufacturePlace;
//    private String manufactureAddress;

    //3.  Идентификация на продукта
    private String name;
    private String nameLat;
    private String productCategoryCode;
    private String productCategoryName;
    private String productTypeCode;
    private String productTypeName;
    private String euMarketPlacementCountryCode;
    private String euMarketPlacementCountryName;

    //    4. Суровини:
    private List<MaterialBO> materials = new ArrayList<>();

    //    5. Производствена технология
    private String processingDescription;
    private String processingDescriptionPatentNumber;

    //    6. Готов продукт
    private List<IngredientBO> ingredients = new ArrayList<>();

    //    6.2 Физични и физико-химични характеристики:
    private String physicalStateCode; //<- physicalStateCode
    private String physicalStateName; //<- physicalStateName
    private String physicalCode;
    private String physicalName;
    private Double drySubstance;
    private Double organicSubstance;
    private Double inorganicSubstance;
    private Double ph;

    //    6.3. Съдържание на тежки метали в мг/кг - количество в готовия продукт
    private Double arsen;
    private Double nickel;
    private Double cadmium;
    private Double mercury;
    private Double chrome;
    private Double lead;

    //    6.4. Биологични и/или биохимични характеристики
    private List<BioCharacteristicBO> livingOrganisms = new ArrayList<>();

    //    7. Микробиологична характеристика за липса на вредни микроорганизми
    private Double enterococci;
    private Double enterococciColi;
    private Double clostridiumPerfringens;
    private Double salmonella;
    private Double staphylococus;
    private Double aspergillus;
    private Double nematodes;

    //    8. Очакван ефект:
    private String expectedEffect;
    //    9. Култури, дози, начин на употреба
    private Set<CropBO> crops = new HashSet<>();

    //    10. Специални предпазни мерки
    private String possibleMixes;
    private String notRecommendedMixes;
    private String notRecommendedClimaticConditions;
    private String notRecommendedSoilConditions;
    private String prohibitedImportCrops;

    //    11.  Предпазни мерки при манипулация
    private String storage;
    private String transport;
    private String fire;
    private String humanAccident;
    private String spilliageAccident;
    private String handlingDeactivationOption;

    public static ApplicationS2170DTO of(final ApplicationS2170 source, final Language language) {
        ApplicationS2170DTO dto = new ApplicationS2170DTO();

        if (source != null) {
            BeanUtils.copyProperties(source, dto);

            if (source.getProductCategory() != null) {
//                dto.setProductCategoryCode(source.getProductCategory().getCode());
                dto.setProductCategoryName(source.getProductCategory().getI18n(language).getName());
            }
            if (source.getProductType() != null) {
//                dto.setProductTypeCode(source.getProductType().getCode());
                dto.setProductTypeName(source.getProductType().getI18n(language).getName());
            }
            if (source.getManufacturer() != null) {
                dto.setManufacturer(ContractorDTO.builder().fullName(source.getManufacturer().getFullName()).build());
            }
            Record record = source.getRecord();
            if (record != null && record.getContractorPaper() != null) {
                dto.setRegNumber(record.getContractorPaper().getRegNumber());
                dto.setRegDate(record.getContractorPaper().getRegDate());
            }

            dto.setApplicationStatus(source.getStatus());
        }

        return dto;
    }

    public static List<ApplicationS2170DTO> of(final List<ApplicationS2170> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS2170DTO ofRecord(final Record source, final Language language) {
        ApplicationS2170DTO dto = new ApplicationS2170DTO();
        dto.ofRecordBase(source, language);

        ApplicationS2170 application = source.getApplicationS2170();

        if (application != null) {
            BeanUtils.copyProperties(application, dto);

            if (application.getSupplier() != null) {
                dto.setSupplier(ContractorDTO.of(application.getSupplier(), language));

                application.getSupplier().getAddresses()
                        .stream().filter(a -> Constants.ADDRESS_TYPE_SUPPLIER_CODE.equals(a.getAddressType().getCode())
                                && ServiceType.S2170.equals(a.getServiceType()))
                        .findFirst()
                        .ifPresent(supplierAddress -> dto.getSupplier().setAddress(AddressDTO.of(supplierAddress, language)));
            }

            if (application.getManufacturer() != null) {
                dto.setManufacturer(ContractorDTO.of(application.getManufacturer(), language));

                application.getManufacturer().getAddresses()
                        .stream().filter(a -> Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE.equals(a.getAddressType().getCode())
                                && ServiceType.S2170.equals(a.getServiceType()))
                        .findFirst()
                        .ifPresent(manufacturerAddress -> dto.getManufacturer().setAddress(AddressDTO.of(manufacturerAddress, language)));
                if (!StringUtils.hasText(dto.getManufacturer().getAddress().getSettlementCode())) {
                    dto.getManufacturer().getAddress().setFullAddress(dto.getManufacturer().getAddress().getFullAddress());
                }
            }

            dto.setManufactureAddress(application.getManufactureAddress());

            if (application.getProductCategory() != null) {
                Nomenclature nom = application.getProductCategory();
                dto.setProductCategoryCode(nom.getCode());
                dto.setProductCategoryName(nom.getI18n(language).getName());
            }

            if (application.getProductType() != null) {
                Nomenclature nom = application.getProductType();
                dto.setProductTypeCode(nom.getCode());
                dto.setProductTypeName(nom.getI18n(language).getName());
            }

            if (application.getEuMarketPlacementCountry() != null) {
                Country country = application.getEuMarketPlacementCountry();
                dto.setEuMarketPlacementCountryCode(country.getCode());
                dto.setEuMarketPlacementCountryName(country.getI18n(language).getName());
            }

            if (application.getPhysicalStateCode() != null) {
                Nomenclature nom = application.getPhysicalStateCode();
                dto.setPhysicalStateCode(nom.getCode());
                dto.setPhysicalStateName(nom.getI18n(language).getName());
            }

            if (application.getPhysicalCode() != null) {
                Nomenclature nom = application.getPhysicalCode();
                dto.setPhysicalCode(nom.getCode());
                dto.setPhysicalName(nom.getI18n(language).getName());
            }

            if (!CollectionUtils.isEmpty(application.getCrops())) {
                dto.getCrops().addAll(application.getCrops());
            }
        }

        return dto;
    }

    public static ApplicationS2170DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2170DTO dto = new ApplicationS2170DTO();

        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setSupplier(getSupplier(sc));
            dto.setManufacturer(getManufacturer(sc));
            dto.setManufactureAddress(getManufactureAddress(sc));

            if (sc.get("manufacturerProductionPlaceName") != null) {
                dto.setManufacturePlace(sc.get("manufacturerProductionPlaceName").toString());
            }
            if (sc.get("productName") != null) {
                dto.setName(sc.get("productName").toString());
            }
            if (sc.get("productNameLat") != null) {
                dto.setNameLat(sc.get("productNameLat").toString());
            }
            if (findValue(sc, "productCategoryCode") instanceof LinkedHashMap<?, ?> category) {
                dto.setProductCategoryCode(category.get("value") != null
                        ? category.get("value").toString()
                        : null);
                dto.setProductCategoryName(category.get("label") != null
                        ? category.get("label").toString()
                        : null);

                if (findValue(sc, "productTypeCode" + dto.getProductCategoryCode()) instanceof LinkedHashMap<?, ?> type) {
                    dto.setProductTypeCode(type.get("value") != null
                            ? type.get("value").toString()
                            : null);
                    dto.setProductTypeName(type.get("label") != null
                            ? type.get("label").toString()
                            : null);
                }
            }

            Object euMarketCountry = findValue(sc, "productEuMarketCountryCode");
            if (euMarketCountry instanceof LinkedHashMap<?, ?> euCountry) {
                dto.setEuMarketPlacementCountryCode(euCountry.get("value") != null
                        ? euCountry.get("value").toString()
                        : null);
                dto.setEuMarketPlacementCountryName(euCountry.get("label") != null
                        ? euCountry.get("label").toString()
                        : null);
            }

            List<MaterialBO> materials = getMaterials(findValue(sc, "materialsGrid"));
            dto.setMaterials(!CollectionUtils.isEmpty(materials) ? materials : null);

            //    5. Производствена технология
            if (sc.get("productionTechnologyDescription") != null) {
                dto.setProcessingDescription(sc.get("productionTechnologyDescription").toString());
            }

            if (sc.get("productionTechnologyPatentNumber") != null) {
                dto.setProcessingDescriptionPatentNumber(sc.get("productionTechnologyPatentNumber").toString());
            }

            //    6. Готов продукт
            List<IngredientBO> finishedProducts = getFinishedProducts(findValue(sc, "materialsGrid1"));
            dto.setIngredients(!CollectionUtils.isEmpty(finishedProducts) ? finishedProducts : null);

            //    6.2 Физични и физико-химични характеристики:
            if (findValue(sc, "physicalConditionCode") instanceof LinkedHashMap<?, ?> condition) {
                dto.setPhysicalStateCode(condition.get("value") != null
                        ? condition.get("value").toString()
                        : null);
                dto.setPhysicalStateName(condition.get("label") != null
                        ? condition.get("label").toString()
                        : null);

                if (findValue(sc, "physicalConditionCode" + dto.getPhysicalStateCode()) instanceof LinkedHashMap<?, ?> physical) {
                    dto.setPhysicalCode(physical.get("value") != null
                            ? physical.get("value").toString()
                            : null);
                    dto.setPhysicalName(physical.get("label") != null
                            ? physical.get("label").toString()
                            : null);
                }
            }

            if (sc.get("physicalConditionpH") != null) {
                dto.setPh(Double.parseDouble(sc.get("physicalConditionpH").toString()));
            }

            if (sc.get("physicalConditionDryMatter") != null) {
                dto.setDrySubstance(Double.parseDouble(sc.get("physicalConditionDryMatter").toString()));
            }

            if (sc.get("physicalConditionInorganicMatter") != null) {
                dto.setInorganicSubstance(Double.parseDouble(sc.get("physicalConditionInorganicMatter").toString()));
            }
            if (sc.get("physicalConditionInorganicMatter") != null) {
                dto.setInorganicSubstance(Double.parseDouble(sc.get("physicalConditionInorganicMatter").toString()));
            }
            //    6.3. Съдържание на тежки метали в мг/кг - количество в готовия продукт
            if (sc.get("metalsContentAs") != null) {
                dto.setArsen(Double.parseDouble(sc.get("metalsContentAs").toString()));
            }
            if (sc.get("metalsContentNi") != null) {
                dto.setNickel(Double.parseDouble(sc.get("metalsContentNi").toString()));
            }
            if (sc.get("metalsContentCd") != null) {
                dto.setCadmium(Double.parseDouble(sc.get("metalsContentCd").toString()));
            }
            if (sc.get("metalsContentHg") != null) {
                dto.setMercury(Double.parseDouble(sc.get("metalsContentHg").toString()));
            }
            if (sc.get("metalsContentCr") != null) {
                dto.setChrome(Double.parseDouble(sc.get("metalsContentCr").toString()));
            }
            if (sc.get("metalsContentPb") != null) {
                dto.setLead(Double.parseDouble(sc.get("metalsContentPb").toString()));
            }

            //    6.4. Биологични и/или биохимични характеристики
            Object organisms = findValue(sc, "biochemicalCharacteristicLiveOrganismCodeDataGrid");
            List<BioCharacteristicBO> livingOrganismsList = getLivingOrganisms(organisms);
            dto.setLivingOrganisms(!CollectionUtils.isEmpty(materials) ? livingOrganismsList : null);

            //    7. Микробиологична характеристика за липса на вредни микроорганизми
            Object microbiologicalAbsenceSalmonella = findValue(sc, "microbiologicalAbsenceSalmonella");
            if (microbiologicalAbsenceSalmonella != null) {
                dto.setSalmonella(Double.parseDouble(microbiologicalAbsenceSalmonella.toString()));
            }
            Object microbiologicalAbsenceEscherichiaColi = findValue(sc, "microbiologicalAbsenceEscherichiaColi");
            if (microbiologicalAbsenceEscherichiaColi != null) {
                dto.setEnterococciColi(Double.parseDouble(microbiologicalAbsenceEscherichiaColi.toString()));
            }
            Object microbiologicalAbsenceNematodes = findValue(sc, "microbiologicalAbsenceNematodes");
            if (microbiologicalAbsenceNematodes != null) {
                dto.setNematodes(Double.parseDouble(microbiologicalAbsenceNematodes.toString()));
            }
            Object microbiologicalAbsenceAspergillus = findValue(sc, "microbiologicalAbsenceAspergillus");
            if (microbiologicalAbsenceAspergillus != null) {
                dto.setAspergillus(Double.parseDouble(microbiologicalAbsenceAspergillus.toString()));
            }
            Object microbiologicalAbsenceStaphylococus = findValue(sc, "microbiologicalAbsenceStaphylococus");
            if (microbiologicalAbsenceStaphylococus != null) {
                dto.setStaphylococus(Double.parseDouble(microbiologicalAbsenceStaphylococus.toString()));
            }
            Object microbiologicalAbsenceEnterococci = findValue(sc, "microbiologicalAbsenceEnterococci");
            if (microbiologicalAbsenceEnterococci != null) {
                dto.setEnterococci(Double.parseDouble(microbiologicalAbsenceEnterococci.toString()));
            }
            Object microbiologicalAbsenceClostridium = findValue(sc, "microbiologicalAbsenceClostridiumPerfringens");
            if (microbiologicalAbsenceClostridium != null) {
                dto.setClostridiumPerfringens(Double.parseDouble(microbiologicalAbsenceClostridium.toString()));
            }

            //    8. Очакван ефект:
            if (sc.get("expectedEffect") != null) {
                dto.setExpectedEffect(sc.get("expectedEffect").toString());
            }

            //    9. Култури, дози, начин на употреба
            dto.setCrops(getCrops(findValue(sc, "dataGrid1")));

            //    10. Специални предпазни мерки
            if (sc.get("precautionPossibleMixes") != null) {
                dto.setPossibleMixes(sc.get("precautionPossibleMixes").toString());
            }
            if (sc.get("precautionNotRecommendedMixes") != null) {
                dto.setNotRecommendedMixes(sc.get("precautionNotRecommendedMixes").toString());
            }
            if (sc.get("precautionNotRecommendedClimaticConditions") != null) {
                dto.setNotRecommendedClimaticConditions(sc.get("precautionNotRecommendedClimaticConditions").toString());
            }
            if (sc.get("precautionNotRecommendedSoilConditions") != null) {
                dto.setNotRecommendedSoilConditions(sc.get("precautionNotRecommendedSoilConditions").toString());
            }
            if (sc.get("precautionProhibidetImportCrops") != null) {
                dto.setProhibitedImportCrops(sc.get("precautionProhibidetImportCrops").toString());
            }

            //    11.  Предпазни мерки при манипулация
            if (sc.get("handlingPrecautionStorage") != null) {
                dto.setStorage(sc.get("handlingPrecautionStorage").toString());
            }
            if (sc.get("handlingPrecautionTransport") != null) {
                dto.setTransport(sc.get("handlingPrecautionTransport").toString());
            }
            if (sc.get("handlingPrecautionFire") != null) {
                dto.setFire(sc.get("handlingPrecautionFire").toString());
            }
            if (sc.get("handlingPrecautionHumanAccident") != null) {
                dto.setHumanAccident(sc.get("handlingPrecautionHumanAccident").toString());
            }
            if (sc.get("handlingPrecautionSplilliageAccident") != null) {
                dto.setSpilliageAccident(sc.get("handlingPrecautionSplilliageAccident").toString());
            }
            if (sc.get("handlingDeactivationOption") != null) {
                dto.setHandlingDeactivationOption(sc.get("handlingDeactivationOption").toString());
            }
        }

        return dto;
    }

    private static ContractorDTO getSupplier(LinkedHashMap<?, ?> supplierObj) {
        ContractorDTO dto = new ContractorDTO();

        if (supplierObj.get("traderName") != null) {
            dto.setFullName(supplierObj.get("traderName").toString());
        }
        if (supplierObj.get("traderIdentifier") != null) {
            dto.setIdentifier(supplierObj.get("traderIdentifier").toString());
        }
        if (supplierObj.get("traderIdentifier") != null) {
            dto.setIdentifier(supplierObj.get("traderIdentifier").toString());
        }
        if (supplierObj.get("traderEmailAddress") != null) {
            dto.setEmail(supplierObj.get("traderEmailAddress").toString());
        }

        if (supplierObj.get("traderAddress") instanceof LinkedHashMap<?, ?> changeAddress) {
            AddressDTO addressDTO = getAddress(changeAddress);
            if (supplierObj.get("phone") != null) {
                addressDTO.setPhone(supplierObj.get("phone").toString());
            }
            if (supplierObj.get("mail") != null) {
                addressDTO.setMail(supplierObj.get("mail").toString());
            }
            Object url = supplierObj.get("url");
            if (url != null) {
                addressDTO.setUrl(url.toString());
            }
            dto.setAddress(addressDTO);
        }

        return dto;
    }

    private static ContractorDTO getManufacturer(LinkedHashMap<?, ?> supplierObj) {
        ContractorDTO dto = new ContractorDTO();

        if (supplierObj.get("manufacturerName") != null) {
            dto.setFullName(supplierObj.get("manufacturerName").toString());
        }
        if (supplierObj.get("manufacturerIdentifier") != null) {
            dto.setIdentifier(supplierObj.get("manufacturerIdentifier").toString());
        }
        if (supplierObj.get("traderEmailAddress") != null) {
            dto.setEmail(supplierObj.get("traderEmailAddress").toString());
        }

        if (supplierObj.get("manufacturerLegalAddress") instanceof LinkedHashMap<?, ?> changeAddress) {
            AddressDTO addressDTO = getAddress(changeAddress);
            if (supplierObj.get("phone") != null) {
                addressDTO.setPhone(supplierObj.get("phone").toString());
            }
            if (supplierObj.get("mail") != null) {
                addressDTO.setMail(supplierObj.get("mail").toString());
            }
            Object url = supplierObj.get("url");
            if (url != null) {
                addressDTO.setUrl(url.toString());
            }
            dto.setAddress(addressDTO);
        }

        return dto;
    }

    private static AddressBO getManufactureAddress(final LinkedHashMap<?, ?> addressObj) {

        if (addressObj.get("manufacturerPlaceAddress") instanceof LinkedHashMap<?, ?> changeAddress) {
            AddressDTO addressDTO = getAddress(changeAddress);
            if (addressDTO.getAddress() != null) {
                addressDTO.setFullAddress(addressDTO.getFullAddress());
            }

            return AddressBO.of(addressDTO);
        }

        return null;
    }

    private static List<MaterialBO> getMaterials(final Object materialsObj) {
        List<MaterialBO> materials = new ArrayList<>();
        if (materialsObj instanceof LinkedHashMap<?, ?> m) {
            materials.add(getMaterial(m));
        } else if (materialsObj instanceof ArrayList<?> food) {
            food.forEach(m -> materials.add(getMaterial(m)));
        }

        return materials;
    }

    private static void processDose(Object object, Set<CropBO> crops, CropBO cropBO, LinkedHashMap<?, ?> dataGrid1Index) {
        if (object instanceof LinkedHashMap<?, ?> specificContent
                && specificContent.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
            CropDoseBO cropDose;

            if (sc.get("cropsRecommendsCropCode") instanceof LinkedHashMap<?, ?> cropsRecommendsCropCode) {
                String cropTypeCode = cropsRecommendsCropCode.get("value").toString();
                String cropCode = "cropsRecommendsCropCode" + cropTypeCode.substring(3);
                if (sc.get(cropCode) instanceof LinkedHashMap<?, ?> lhm) {
                    String name = lhm.get("label").toString();
                    String code = lhm.get("value").toString();

                    cropBO = crops.stream().filter(c -> code != null
                            && code.equals(c.getCropCode())).findFirst().orElse(null);

                    if (cropBO == null) {
                        cropBO = new CropBO();
                        cropBO.setCropCode(code);
                        cropBO.setCropName(name);
                        cropBO.setCropTypeCode(cropsRecommendsCropCode.get("value").toString());
                        cropBO.setCropTypeName(cropsRecommendsCropCode.get("label").toString());
                    }
                }
            }

            cropDose = CropDoseBO.builder()
                    .mixtureConcentrationMax(sc.get("cropsRecommendsConcentrationMixtureMax") != null
                            ? Double.parseDouble(sc.get("cropsRecommendsConcentrationMixtureMax").toString())
                            : null)
                    .mixtureConcentrationMin(sc.get("cropsRecommendsConcentrationMixtureMin") != null
                            ? Double.parseDouble(sc.get("cropsRecommendsConcentrationMixtureMin").toString())
                            : null)
                    .deadline(sc.get("cropsRecommendsDeadline") != null
                            ? sc.get("cropsRecommendsDeadline").toString()
                            : null)
                    .treatmentMax(sc.get("cropsRecommendsTreatedPerYearMax") != null
                            ? Integer.parseInt(sc.get("cropsRecommendsTreatedPerYearMax").toString())
                            : null)
                    .treatmentMin(sc.get("cropsRecommendsTreatedPerYearMin") != null
                            ? Integer.parseInt(sc.get("cropsRecommendsTreatedPerYearMin").toString())
                            : null)
                    .doseMax(sc.get("cropsRecommendsMaxDose") != null
                            ? Double.parseDouble(sc.get("cropsRecommendsMaxDose").toString())
                            : null)
                    .doseMin(sc.get("cropsRecommendsMinDose") != null
                            ? Double.parseDouble(sc.get("cropsRecommendsMinDose").toString())
                            : null)
                    .build();

            if (sc.get("cropsRecommendsUnitCode2") instanceof LinkedHashMap<?, ?> code) {
                cropDose.setConcentrationDoseUnitCode(code.get("value") != null
                        ? code.get("value").toString()
                        : null);
                cropDose.setConcentrationDoseUnitName(code.get("label") != null
                        ? code.get("label").toString()
                        : null);
            }
            if (sc.get("cropsRecommendsUnitCode") instanceof LinkedHashMap<?, ?> doseCode) {
                cropDose.setDoseUnitCode(doseCode.get("value") != null
                        ? doseCode.get("value").toString()
                        : null);
                cropDose.setDoseUnitName(doseCode.get("label") != null
                        ? doseCode.get("label").toString()
                        : null);
            }
            if (dataGrid1Index.get("specificContent") instanceof LinkedHashMap<?, ?> content) {
                if (content.get("cropsRecommendsApplicationCode") instanceof LinkedHashMap<?, ?> code) {
                    cropDose.setApplicationCode(code.get("value") != null
                            ? code.get("value").toString()
                            : null);
                    cropDose.setApplicationName(code.get("label") != null
                            ? code.get("label").toString()
                            : null);
                }
            }

            cropBO.getCropDoses().add(cropDose);
        }
        crops.add(cropBO);
    }

    private static void processCrop(final Object o, Set<CropBO> crops) {
        CropBO cropBO = null;
        if (o instanceof LinkedHashMap<?, ?> dataGrid1Index) {

            Object object = dataGrid1Index.get("cropsGrid");
            if (object instanceof LinkedHashMap<?, ?> cropsGrid) {
                processDose(cropsGrid, crops, cropBO, dataGrid1Index);
            } else if (object instanceof ArrayList<?> arrayList) {
                for (Object cropsGrid : arrayList) {
                    processDose(cropsGrid, crops, cropBO, dataGrid1Index);
                }
            }
        }
    }

    private static Set<CropBO> getCrops(final Object dataGridObj) {
        Set<CropBO> crops = null;

        if (dataGridObj != null) {
            crops = new HashSet<>();
            if (dataGridObj instanceof LinkedHashMap<?, ?> dataGrid1) {
                processCrop(dataGrid1, crops);
            } else if (dataGridObj instanceof ArrayList<?> dataGrid1) {
                for (Object cropObj : dataGrid1) {
                    processCrop(cropObj, crops);
                }
            }
        }

        return crops;
    }

    private static MaterialBO getMaterial(Object materialsObj) {
        MaterialBO bo = new MaterialBO();
        if (materialsObj instanceof LinkedHashMap<?, ?> material) {
            if (findValue(material, "materialTypeCode") instanceof LinkedHashMap<?, ?> materialTypeCode) {
                bo.setMaterialTypeCode(materialTypeCode.get("value") != null
                        ? materialTypeCode.get("value").toString()
                        : null);
                bo.setMaterialTypeName(materialTypeCode.get("label") != null
                        ? materialTypeCode.get("label").toString()
                        : null);
            }
            if (findValue(material, "specificContent") instanceof LinkedHashMap<?, ?> specificContent) {
                bo.setOrigin(specificContent.get("materialOrigin") != null
                        ? specificContent.get("materialOrigin").toString()
                        : null);
                bo.setName(specificContent.get("materialName") != null
                        ? specificContent.get("materialName").toString()
                        : null);
                bo.setAmount(specificContent.get("materialAmount") != null
                        ? Double.parseDouble(specificContent.get("materialAmount").toString())
                        : null);
            }
        }

        return bo;
    }

    private static List<IngredientBO> getFinishedProducts(final Object ingredientObj) {
        List<IngredientBO> ingredients = new ArrayList<>();
        if (ingredientObj instanceof LinkedHashMap<?, ?> i) {
            ingredients.add(getFinishedProduct(i));
        } else if (ingredientObj instanceof ArrayList<?> ingredient) {
            ingredient.forEach(i -> ingredients.add(getFinishedProduct(i)));
        }

        return ingredients;
    }

    private static IngredientBO getFinishedProduct(Object sc) {
        IngredientBO bo = new IngredientBO();
        if (sc instanceof LinkedHashMap<?, ?> materialsGrid1) {
            if (materialsGrid1.get("specificContent") instanceof LinkedHashMap<?, ?> specificContent) {
                bo.setType(specificContent.get("ingrediantType") != null
                        ? specificContent.get("ingrediantType").toString()
                        : null);
                bo.setName(specificContent.get("ingrediantName") != null
                        ? specificContent.get("ingrediantName").toString()
                        : null);
                bo.setAmount(specificContent.get("ingrediantAmount") != null
                        ? Integer.parseInt(specificContent.get("ingrediantAmount").toString())
                        : null);
            }
        }

        return bo;
    }

    private static List<BioCharacteristicBO> getLivingOrganisms(final Object bioCharacteristicObj) {
        List<BioCharacteristicBO> bioCharacteristics = new ArrayList<>();
        if (bioCharacteristicObj instanceof LinkedHashMap<?, ?> b) {
            bioCharacteristics.add(getBioCharacteristic(b));
        } else if (bioCharacteristicObj instanceof ArrayList<?> ingredient) {
            ingredient.forEach(b -> bioCharacteristics.add(getBioCharacteristic(b)));
        }

        return bioCharacteristics;
    }

    private static BioCharacteristicBO getBioCharacteristic(Object sc) {
        BioCharacteristicBO bo = new BioCharacteristicBO();
        if (sc instanceof LinkedHashMap<?, ?> biochemicalGrid) {
            if (biochemicalGrid.get("specificContent") instanceof LinkedHashMap<?, ?> specificContent) {
                if (specificContent.get("biochemicalCharacteristicLiveOrganismCode") instanceof LinkedHashMap<?, ?> code) {
                    bo.setLivingOrganismCode(code.get("value") != null
                            ? code.get("value").toString()
                            : null);
                    bo.setLivingOrganismName(code.get("label") != null
                            ? code.get("label").toString()
                            : null);
                }
            }
//            TODO: "specificConten" misspell value in  JSON, is not fixed!
            if (biochemicalGrid.get("specificConten") instanceof LinkedHashMap<?, ?> specificContent) {
                bo.setClassification(specificContent.get("biochemicalCharacteristicClass") != null
                        ? specificContent.get("biochemicalCharacteristicClass").toString()
                        : null);
                bo.setStrain(specificContent.get("biochemicalCharacteristicStrain") != null
                        ? specificContent.get("biochemicalCharacteristicStrain").toString()
                        : null);
                bo.setType(specificContent.get("biochemicalCharacteristicType") != null
                        ? specificContent.get("biochemicalCharacteristicType").toString()
                        : null);
                bo.setCfu(specificContent.get("biochemicalCharacteristicCfu") != null
                        ? specificContent.get("biochemicalCharacteristicCfu").toString()
                        : null);
            }
        }

        return bo;
    }
}
