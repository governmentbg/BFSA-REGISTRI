package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ApplicationS2698;
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
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2698DTO extends BaseApplicationDTO {

    private String adjuvantName;
    private String adjuvantNameLat;
    private String manufacturerName;
    private String manufacturerIdentifier;
    private String effects;
    private ContractorDTO supplier;
    private String adjuvantProductFormulationTypeName;
    private String adjuvantProductFormulationTypeCode;
    private List<String> pppFunctionCodes = new ArrayList<>();
    private List<AdjuvantBO> ingredients = new ArrayList<>();
    private List<AdjuvantBO> applications = new ArrayList<>();

    public static ApplicationS2698DTO ofRecord(final Record source, final Language language) {
        ApplicationS2698DTO dto = new ApplicationS2698DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS2698 application = source.getApplicationS2698();
        if (application != null) {
            dto.setManufacturerIdentifier(application.getManufacturerIdentifier());
            dto.setManufacturerName(application.getManufacturerName());
            dto.setEffects(application.getEffects());
            dto.setAdjuvantName(application.getAdjuvantName());

            if (application.getAdjuvantProductFormulationType() != null) {
                dto.setAdjuvantProductFormulationTypeCode(
                        application.getAdjuvantProductFormulationType().getCode());
                dto.setAdjuvantProductFormulationTypeName(
                        application.getAdjuvantProductFormulationType().getI18n(language).getName());
            }
            if (application.getPppFunctions() != null) {
                application.getPppFunctions().forEach(pu -> dto.getPppFunctionCodes().add(pu.getCode()));
            }
            if (application.getIngredients() != null) {
                dto.setIngredients(application.getIngredients());
            }
            if (application.getApplications() != null) {
                dto.setApplications(application.getApplications());
            }
            if (application.getSupplier() != null) {
                dto.setSupplier(ContractorDTO.of(application.getSupplier(), language));
                application.getSupplier().getAddresses()
                        .stream().filter(a -> Constants.ADDRESS_TYPE_SUPPLIER_CODE.equals(a.getAddressType().getCode())
                                && ServiceType.S2698.equals(a.getServiceType()))
                        .findFirst()
                        .ifPresent(supplierAddress -> dto.getSupplier().setAddress(AddressDTO.of(supplierAddress, language)));
            }
        }

        return dto;
    }

    public static List<ApplicationS2698DTO> ofRecord(final List<Record> source, final Language language) {
        return source.stream().map(r -> ofRecord(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS2698DTO of(final ApplicationS2698 source, final Language language) {
        ApplicationS2698DTO dto = new ApplicationS2698DTO();

        if (source != null) {
            BeanUtils.copyProperties(source, dto);

            dto.setApplicationStatus(source.getStatus());

            if (source.getAdjuvantProductFormulationType() != null) {
                dto.setAdjuvantProductFormulationTypeName(
                        source.getAdjuvantProductFormulationType().getI18n(language).getName());
            }
            if (source.getPppFunctions() != null) {
                source.getPppFunctions().forEach(pu -> dto.getPppFunctionCodes().add(pu.getCode()));
            }

            if (source.getIngredients() != null) {
                dto.setIngredients(source.getIngredients());
            }

            if (source.getApplications() != null) {
                dto.setApplications(source.getApplications());
            }

            if (source.getSupplier() != null) {
                dto.setSupplier(ContractorDTO.of(source.getSupplier(), language));
            }
        }

        return dto;
    }

    public static List<ApplicationS2698DTO> of(final List<ApplicationS2698> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS2698DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2698DTO dto = new ApplicationS2698DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            if (sc.get("manufacturerIdentifier") != null) {
                dto.setManufacturerIdentifier(sc.get("manufacturerIdentifier").toString());
            }
            if (sc.get("manufacturerName") != null) {
                dto.setManufacturerName(sc.get("manufacturerName").toString());
            }

            Object adjuvantProductFormulationTypeCode = sc.get("adjuvantProductFormulationTypeCode");
            if (adjuvantProductFormulationTypeCode != null) {
                if (adjuvantProductFormulationTypeCode instanceof LinkedHashMap<?, ?> formulationType) {
                    dto.setAdjuvantProductFormulationTypeCode(formulationType.get("value").toString());
                }
            }
            ContractorDTO supplier = new ContractorDTO();
            // Случай с фирма
            if (sc.get("supplierFullName") != null) {
                supplier.setFullName(sc.get("supplierFullName").toString());
            }
//            TODO: FIx it when 'supplierID/supplierIdentifier' is ok!
            if (sc.get("supplierID") != null) {
                supplier.setIdentifier(sc.get("supplierID").toString());
            }
            if (sc.get("supplierIdentifier") != null) {
                supplier.setIdentifier(sc.get("supplierIdentifier").toString());
            }
            if (sc.get("supplierAddress") != null) {
                supplier.setAddress(BaseApplicationDTO.getAddress(sc.get("supplierAddress")));
            }
            if (sc.get("effects") != null) {
                dto.setEffects(sc.get("effects").toString());
            }
            String name = "";
            if (sc.get("name") != null) {
                name = sc.get("name").toString();
            }
            String nameLat = "";
            if (sc.get("nameLat") != null) {
                nameLat = sc.get("nameLat").toString();
            }
            dto.setAdjuvantName(name + " / " + nameLat);

            Object przUseCodes = sc.get("przUseCodes");
            if (przUseCodes != null) {
                if (przUseCodes instanceof LinkedHashMap<?, ?> useCode) {
                    dto.getPppFunctionCodes().add(getPrzUseCodes(useCode));
                } else if (przUseCodes instanceof ArrayList<?> useCodes) {
                    useCodes.forEach(uc -> dto.getPppFunctionCodes().add(getPrzUseCodes((LinkedHashMap<?, ?>) uc)));
                }
            }

            Object additionalSC = sc.get("__additionalSpecificContent");
            if (additionalSC != null) {
                if (additionalSC instanceof LinkedHashMap<?, ?> additional) {
                    // Случай с физическо лице
                    Object supplierName = additional.get("supplierName");
                    Object supplierSurname = sc.get("supplierSurname") != null
                            ? sc.get("supplierSurname")
                            : additional.get("supplierSurname");
                    Object supplierFamilyName = sc.get("supplierFamilyName") != null
                            ? sc.get("supplierFamilyName")
                            : additional.get("supplierFamilyName");

                    String fullName = "";
                    if (supplierName != null) {
                        supplier.setName(supplierName.toString());
                        fullName = supplierName.toString();
                    }
                    if (supplierSurname != null) {
                        supplier.setSurname(supplierSurname.toString());
                        fullName = fullName + " " + supplierSurname;
                    }
                    if (supplierFamilyName != null) {
                        supplier.setFamilyName(supplierFamilyName.toString());
                        fullName = fullName + " " + supplierFamilyName;
                    }
                    supplier.setFullName(fullName);

                    Object ingredientsDataGrid = additional.get("ingredientsDataGrid");
                    if (ingredientsDataGrid != null) {
                        if (ingredientsDataGrid instanceof LinkedHashMap<?, ?> ingredient) {
                            dto.getIngredients().add(getIngredient(ingredient));
                        } else if (ingredientsDataGrid instanceof ArrayList<?> ingredients) {
                            ingredients.forEach(i -> dto.getIngredients()
                                    .add(getIngredient((LinkedHashMap<?, ?>) i)));
                        }
                    }

                    Object applicationsDataGridObj = additional.get("applicationsDataGrid");
                    if (applicationsDataGridObj != null) {
                        if (applicationsDataGridObj instanceof LinkedHashMap<?, ?> application) {
                            dto.getApplications().add(getApplication(application));
                        } else if (applicationsDataGridObj instanceof ArrayList<?> applications) {
                            applications.forEach(a -> dto.getApplications()
                                    .add(getApplication((LinkedHashMap<?, ?>) a)));
                        }
                    }
                }
            }

            dto.setSupplier(supplier);
        }

        return dto;
    }

    private static AdjuvantBO getApplication(LinkedHashMap<?, ?> application) {
        AdjuvantBO bo = new AdjuvantBO();
        Object specificContent = application.get("specificContent");
        if (specificContent instanceof LinkedHashMap<?, ?> sc) {
            bo.setApplicationDose(Double.valueOf(sc.get("applicationDose").toString()));

            Object applicationGrainCultureCode = sc.get("applicationGrainCultureCode");
            String sub = "";
            if (applicationGrainCultureCode instanceof LinkedHashMap<?, ?> cc) {
                sub = cc.get("value").toString().substring(3);
            }

            Object applicationGrainCultureCodeSubSelect = sc.get("applicationGrainCultureCode" + sub + "select");
            if (applicationGrainCultureCodeSubSelect instanceof LinkedHashMap<?, ?> cc) {
                bo.setApplicationGrainCultureCode(cc.get("value").toString());
                bo.setApplicationGrainCultureName(cc.get("label").toString());
            }

            Object ingredientUnitCode = sc.get("ingredientUnitCode1");
            if (ingredientUnitCode instanceof LinkedHashMap<?, ?> cc) {
                bo.setApplicationUnitTypeCode(cc.get("value").toString());
                bo.setUnitTypeName(cc.get("label").toString());
            }
        }

        return bo;
    }

    private static AdjuvantBO getIngredient(LinkedHashMap<?, ?> ingredient) {
        AdjuvantBO bo = new AdjuvantBO();
        Object specificContent = ingredient.get("specificContent");
        if (specificContent instanceof LinkedHashMap<?, ?> sc) {
            Object ingredientUnitCode = sc.get("ingredientUnitCode");
            if (ingredientUnitCode instanceof LinkedHashMap<?, ?> uc) {
                bo.setIngredientUnitTypeCode(uc.get("value").toString());
                bo.setUnitTypeName(uc.get("label").toString());
            }
            bo.setIngredientContent(StringUtils.hasText(sc.get("ingredientContent").toString())
                    ? sc.get("ingredientContent").toString()
                    : null);
            bo.setIngredientCasNumber(StringUtils.hasText(sc.get("ingredientCasNumber").toString())
                    ? sc.get("ingredientCasNumber").toString()
                    : null);
            bo.setIngredientChemName(StringUtils.hasText(sc.get("ingredientChemName").toString())
                    ? sc.get("ingredientChemName").toString()
                    : null);
        }

        return bo;
    }

    private static String getPrzUseCodes(LinkedHashMap<?, ?> useCode) {
        return useCode.get("value").toString();
    }
}
