package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.model.ApplicationS2702;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2702DTO extends BaseApplicationDTO {

    // Данни на лицето/лицата, отговарящо/и за дейността
    private List<ContractorDTO> activityResponsiblePersons = new ArrayList<>();

    // Данни за материала, за който се заявява временно разрешение
    private String materialType;
    private String materialName;
    private Double materialTotalAmount;
    private String materialMeasuringUnitCode;
    private String materialMeasuringUnitName;
    private List<Double> materialMovements = new ArrayList<>();
    private String materialOriginCountryCode;
    private String materialExportCountryCode;
    private String materialPackagingCondition;

    // Данни на износител/изпращач/доставчик
    private ContractorDTO supplier;
    private AddressDTO supplierAddress;

    // Данни за карантинния пункт/съоръжението за задържане, одобрено съгласно изискванията на чл. 61 от Регламент (ЕС) 2016/2031
    private String quarantineStationName;
    private String quarantineStationDescription;
    // Данни на лицето, отговарящо за карантинният пункт или съоръжението за задържане
    private ContractorDTO quarantineStationPerson;
    private AddressDTO quarantineStationAddress;
    private String quarantineStationMaterialStorageMeasure;

    // Резюме на естеството и целите на заявяваната дейност
    private String requestedActivitySummary;

    // Продължителност на заявяваната дейност
    private OffsetDateTime firstEntryDate;
    private OffsetDateTime expectedCompletionDate;
    private String materialEndUseCode;
    private String materialDestructionMethod;
    private String materialSafeMeasure;

    // Допълнителна информация
    private String description;


//    public static ApplicationS2702DTO ofRecord(final ApplicationS2702 source, Language language) {
//        ApplicationS2702DTO dto = new ApplicationS2702DTO();
//        BeanUtils.copyProperties(source, dto);
//
////        --- Set Requestor, Applicant and base fields ---
//        dto.ofRecordBase(source.getRecord());
//
//        return dto;
//    }
//
//    public static List<ApplicationS2702DTO> ofRecord(final List<ApplicationS2702> source, final Language language) {
//        return source.stream().map(r -> ofRecord(r, language)).collect(Collectors.toList());
//    }


    public static ApplicationS2702DTO ofRecord(final Record source, Language language) {
        ApplicationS2702DTO dto = new ApplicationS2702DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

//        --- Set ApplicationS2702 ---
        ApplicationS2702 application = source.getApplicationS2702();
        if (application != null) {

            dto.setMaterialType(application.getMaterialType());
            dto.setMaterialName(application.getMaterialName());
            dto.setMaterialTotalAmount(application.getMaterialTotalAmount());
            if (application.getMaterialMeasuringUnitCode() != null
                    && StringUtils.hasText(application.getMaterialMeasuringUnitCode().getCode())) {
                Nomenclature nom = application.getMaterialMeasuringUnitCode();
                dto.setMaterialMeasuringUnitCode(nom.getCode());
                dto.setMaterialMeasuringUnitName(nom.getI18n(language).getName());
            }

            if (!CollectionUtils.isEmpty(application.getMaterialMovements())) {
                dto.getMaterialMovements().addAll(application.getMaterialMovements());
            }
            if (application.getMaterialOriginCountry() != null
                    && StringUtils.hasText(application.getMaterialOriginCountry().getCode())) {
                dto.setMaterialOriginCountryCode(application.getMaterialOriginCountry().getCode());
            }

            if (application.getMaterialExportCountry() != null
                    && StringUtils.hasText(application.getMaterialExportCountry().getCode())) {
                dto.setMaterialExportCountryCode(application.getMaterialExportCountry().getCode());
            }

            dto.setMaterialPackagingCondition(application.getMaterialPackagingCondition());

            if (application.getSupplier() != null) {
                dto.setSupplier(ContractorDTO.of(application.getSupplier(), language));
            }

            if (application.getSupplierAddress() != null) {
                dto.setSupplierAddress(AddressDTO.of(application.getSupplierAddress(), language));
            }

            dto.setQuarantineStationName(application.getQuarantineStationName());
            dto.setQuarantineStationDescription(application.getQuarantineStationDescription());

            if (StringUtils.hasText(application.getQuarantineStationAddress())) {
                dto.setQuarantineStationAddress(AddressDTO.builder()
                        .fullAddress(application.getQuarantineStationAddress())
                        .build());
            }

            if (application.getQuarantineStationPerson() != null) {
                dto.setQuarantineStationPerson(ContractorDTO.of(
                        application.getQuarantineStationPerson(), language));
            }

            if (!CollectionUtils.isEmpty(application.getActivityResponsiblePersons())) {
                application.getActivityResponsiblePersons()
                        .forEach(arp -> dto.getActivityResponsiblePersons().add(ContractorDTO.of(arp, language)));
            }

            dto.setQuarantineStationMaterialStorageMeasure(source
                    .getApplicationS2702().getQuarantineStationMaterialStorageMeasure());
            dto.setRequestedActivitySummary(application.getRequestedActivitySummary());
            dto.setFirstEntryDate(application.getFirstEntryDate());
            dto.setExpectedCompletionDate(application.getExpectedCompletionDate());
            if (application.getMaterialEndUse() != null && StringUtils.hasText(application.getMaterialEndUse().getCode())) {
                dto.setMaterialEndUseCode(application.getMaterialEndUse().getCode());
            }
            dto.setMaterialDestructionMethod(application.getMaterialDestructionMethod());
            dto.setMaterialSafeMeasure(application.getMaterialSafeMeasure());
            dto.setDescription(application.getDescription());
        }

        return dto;
    }

    public static List<ApplicationS2702DTO> ofRecord(final List<Record> source, final Language language) {
        return source.stream().map(r -> ofRecord(r, language)).collect(Collectors.toList());
    }

    public static ApplicationS2702DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2702DTO dto = new ApplicationS2702DTO();

        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setMaterialType(sc.get("materialType") != null
                    ? sc.get("materialType").toString()
                    : null);
            dto.setMaterialName(sc.get("materialName") != null
                    ? sc.get("materialName").toString()
                    : null);
            dto.setMaterialTotalAmount(sc.get("materialTotalAmount") != null
                    ? ((Number) sc.get("materialTotalAmount")).doubleValue()
                    : null);

            if (sc.get("materialMeasuringUnitCode") instanceof LinkedHashMap<?, ?> code) {
                dto.setMaterialMeasuringUnitCode(code.get("value").toString());
                dto.setMaterialMeasuringUnitName(code.get("label").toString());
            }
            dto.setMaterialPackagingCondition(sc.get("materialPackingCondition") != null
                    ? sc.get("materialPackingCondition").toString()
                    : null);

            if (sc.get("materialEndUseCode") instanceof LinkedHashMap<?, ?> code) {
                dto.setMaterialEndUseCode(code.get("value").toString());
            }

            if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> addSpecContent) {
                if (addSpecContent.get("materialExportCountryCode") instanceof LinkedHashMap<?, ?> exportCountryCode) {
                    dto.setMaterialExportCountryCode(exportCountryCode.get("value").toString());
                }
                if (addSpecContent.get("materialOriginCountryCode") instanceof LinkedHashMap<?, ?> originCountryCode) {
                    dto.setMaterialOriginCountryCode(originCountryCode.get("value").toString());
                }
                if (addSpecContent.get("materialDestructionMethod") != null) {
                    dto.setMaterialDestructionMethod(addSpecContent.get("materialDestructionMethod").toString());
                }
                if (addSpecContent.get("quarantineStationName") != null) {
                    dto.setQuarantineStationName(addSpecContent.get("quarantineStationName").toString());
                }
                if (addSpecContent.get("quarantineStationDescription") != null) {
                    dto.setQuarantineStationDescription(addSpecContent.get("quarantineStationDescription").toString());
                }
                if (addSpecContent.get("quarantineStationMaterialStorageMeasure") != null) {
                    dto.setQuarantineStationMaterialStorageMeasure(addSpecContent
                            .get("quarantineStationMaterialStorageMeasure").toString());
                }
                if (addSpecContent.get("requestedActivitySummary") != null) {
                    dto.setRequestedActivitySummary(addSpecContent.get("requestedActivitySummary").toString());
                }
                if (addSpecContent.get("firstEntryDate") != null) {
                    dto.setFirstEntryDate(OffsetDateTime.parse(addSpecContent.get("firstEntryDate").toString()));
                }
                if (addSpecContent.get("expectedCompletionDate") != null) {
                    dto.setExpectedCompletionDate(OffsetDateTime
                            .parse(addSpecContent.get("expectedCompletionDate").toString()));
                }
                if (addSpecContent.get("materialSafeMeasure") != null) {
                    dto.setMaterialSafeMeasure(addSpecContent.get("materialSafeMeasure").toString());
                }
                if (addSpecContent.get("description") != null) {
                    dto.setDescription(addSpecContent.get("description").toString());
                }

            }

            dto.getMaterialMovements().addAll(getMaterialMovements(sc));
            dto.setQuarantineStationPerson(getQuarantineStationPerson(sc));
            dto.setQuarantineStationAddress(getQuarantineStationAddress(sc));
            dto.setSupplierAddress(getSupplierAddress(sc));
            dto.setSupplier(getSupplier(sc));
            dto.getActivityResponsiblePersons().addAll(getActivityPersons(sc));
        }

        return dto;
    }

    private static AddressDTO getSupplierAddress(final LinkedHashMap<?, ?> sc) {
        AddressDTO dto = new AddressDTO();

        if (findValue(sc, "supplierAddress") instanceof LinkedHashMap<?, ?> address) {
            if (address.get("FullAddress") != null) {
                dto.setFullAddress(address.get("FullAddress").toString());
            }
            if (address.get("SettlementSelect") instanceof LinkedHashMap<?, ?> code) {
                if (code.get("settlementCode") != null) {
                    dto.setSettlementCode(code.get("settlementCode").toString());
                }
            }
            if (address.get("CountryCode") != null) {
                dto.setCountryCode(address.get("CountryCode").toString());
            }
        }

        return dto;
    }

    private static AddressDTO getQuarantineStationAddress(final LinkedHashMap<?, ?> sc) {
        AddressDTO dto = new AddressDTO();

        if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> address) {
            if (address.get("quarantineStationDataAddress") instanceof LinkedHashMap<?, ?> quarantineAddress) {
                if (quarantineAddress.get("FullAddress") != null) {
                    dto.setFullAddress(quarantineAddress.get("FullAddress").toString());
                }
                if (quarantineAddress.get("SettlementSelect") instanceof LinkedHashMap<?, ?> code) {
                    if (code.get("settlementCode") != null) {
                        dto.setSettlementCode(code.get("settlementCode").toString());
                    }
                }
            }
        }

        return dto;
    }

    private static ContractorDTO getQuarantineStationPerson(final LinkedHashMap<?, ?> sc) {
        ContractorDTO dto = new ContractorDTO();

        if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> person) {
            if (person.get("quarantineStationPersonIdentification") != null) {
                dto.setIdentifier(person.get("quarantineStationPersonIdentification").toString());
            }
            if (person.get("quarantineStationPersonName") != null) {
                dto.setName(person.get("quarantineStationPersonName").toString());
            }
            if (person.get("quarantineStationPersonSurname") != null) {
                dto.setSurname(person.get("quarantineStationPersonSurname").toString());
            }
            if (person.get("quarantineStationPersonFamilyname") != null) {
                dto.setFamilyName(person.get("quarantineStationPersonFamilyname").toString());
            }
            if (person.get("quarantineStationPersonDegree") != null) {
                dto.setDegree(person.get("quarantineStationPersonDegree").toString());
            }
            if (person.get("quarantineStationPersonEmailAddress") != null) {
                dto.setEmail(person.get("quarantineStationPersonEmailAddress").toString());
            }
            if (person.get("quarantineStationPersonPhoneNumber") != null) {
                dto.setPhone(person.get("quarantineStationPersonPhoneNumber").toString());
            }
        }

        return dto;
    }

    private static List<ContractorDTO> getActivityPersons(final LinkedHashMap<?, ?> sc) {
        List<ContractorDTO> activityPersons = new ArrayList<>();
        Object activityPersonsData = findValue(sc, "activityResponsiblePersons");
        if (activityPersonsData instanceof LinkedHashMap<?, ?> person) {
            activityPersons.add(getActivityPerson(person));
        } else if (activityPersonsData instanceof ArrayList<?> persons) {
            for (Object person : persons) {
                activityPersons.add(getActivityPerson(person));
            }
        }
        return activityPersons;
    }

    private static ContractorDTO getActivityPerson(final Object activityPersonsData) {
        ContractorDTO dto = null;
        if (activityPersonsData instanceof LinkedHashMap<?, ?> person) {
            dto = ContractorDTO.builder()
                    .name(person.get("activityResponsiblePersonName") != null
                            ? person.get("activityResponsiblePersonName").toString()
                            : null)
                    .surname(person.get("activityResponsiblePersonSurname") != null
                            ? person.get("activityResponsiblePersonSurname").toString()
                            : null)
                    .familyName(person.get("activityResponsiblePersonFamilyname") != null
                            ? person.get("activityResponsiblePersonFamilyname").toString()
                            : null)
                    .degree(person.get("activityResponsiblePersonDegree") != null
                            ? person.get("activityResponsiblePersonDegree").toString()
                            : null)
                    .email(person.get("activityResponsiblePersonEmailAddress") != null
                            ? person.get("activityResponsiblePersonEmailAddress").toString()
                            : null)
                    .phone(person.get("activityResponsiblePersonPhoneNumber") != null
                            ? person.get("activityResponsiblePersonPhoneNumber").toString()
                            : null)
                    .identifier(person.get("activityResponsiblePersonIdentification") != null
                            ? person.get("activityResponsiblePersonIdentification").toString()
                            : null)
                    .build();
        }
        return dto;
    }

    private static ContractorDTO getSupplier(final LinkedHashMap<?, ?> sc) {
        ContractorDTO dto = new ContractorDTO();

        if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> supplier) {

            if (supplier.get("supplierIdentification") != null) { // PHYSICAL
                dto.setEntityType(EntityType.PHYSICAL);
                dto.setIdentifier(supplier.get("supplierIdentification").toString());

                if (supplier.get("supplierName") != null) {
                    dto.setName(supplier.get("supplierName").toString());
                }

                if (supplier.get("supplierSurname") != null) {
                    dto.setSurname(supplier.get("supplierSurname").toString());
                }

                if (supplier.get("supplierFamilyName") != null) {
                    dto.setFamilyName(supplier.get("supplierFamilyName").toString());
                }
            } else if (supplier.get("supplierVatNumber") != null) { // LEGAL
                dto.setEntityType(EntityType.LEGAL);
                dto.setIdentifier(supplier.get("supplierVatNumber").toString());
                dto.setFullName(supplier.get("companyName").toString());
            }

            if (supplier.get("supplierActivityTypeCode") instanceof LinkedHashMap<?, ?> code) {
                dto.setContractorActivityTypeCode(code.get("value").toString());
            }

            if (supplier.get("supplierEmailAddress") != null) {
                dto.setEmail(supplier.get("supplierEmailAddress").toString());
            }
            if (supplier.get("suppllierPhoneNumber") != null) {
                dto.setPhone(supplier.get("suppllierPhoneNumber").toString());
            }
        }

        return dto;
    }

    private static List<Double> getMaterialMovements(final LinkedHashMap<?, ?> sc) {
        List<Double> movements = new ArrayList<>();

        if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> additionalSpecificContent) {
            Object o = additionalSpecificContent.get("materialMovements");
            if (o instanceof LinkedHashMap<?, ?> materialMovement) {
                movements.add(((Number) materialMovement.get("materialMovementsQuantity")).doubleValue());
            } else if (o instanceof ArrayList<?> materialMovements) {
                materialMovements.forEach(m -> {
                    if (m instanceof LinkedHashMap<?, ?> movement) {
                        movements.add(((Number) movement.get("materialMovementsQuantity")).doubleValue());
                    }
                });
            }
        }

        return movements;
    }

}
