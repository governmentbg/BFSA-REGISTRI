package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS2695;
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
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
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
public class ApplicationS2695DTO extends BaseApplicationDTO {

    private String aviationOperatorName;
    private String aviationOperatorIdentifier;
    private String aviationCh64CertNumber;
    private OffsetDateTime aviationCh64CertDate;
    private OffsetDateTime aerialSprayStartDate;
    private OffsetDateTime aerialSprayEndDate;
    private PersonBO ch83CertifiedPerson;
    private String aerialSprayAgriculturalGroupCode;
    private String aerialSprayAgriculturalGroupName;
    private String phenophaseCultureName;
    private String phenophaseCultureCode;
    private String worksite;
    private String workLand;
    private ApplicationS2695FieldDTO field;
    private List<ApplicationS2695PppDTO> plantProtectionProducts = new ArrayList<>();
    private List<KeyValueDTO> subAgricultures = new ArrayList<>();

    public static ApplicationS2695DTO of(final Record source, final Language language) {
        ApplicationS2695DTO dto = new ApplicationS2695DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS2695 application = source.getApplicationS2695();
        if (application != null) {
            BeanUtils.copyProperties(application, dto);

            if (!CollectionUtils.isEmpty(application.getPlantProtectionProducts())) {
                dto.getPlantProtectionProducts().addAll(ApplicationS2695PppDTO.of(application.getPlantProtectionProducts(), language));
            }
            if (application.getApplicationS2695Field() != null) {
                dto.setField(ApplicationS2695FieldDTO.of(application.getApplicationS2695Field(), language));
            }
            if (!CollectionUtils.isEmpty(application.getSubAgricultures())) {
                application.getSubAgricultures().forEach(sa -> dto.getSubAgricultures().add(
                        KeyValueDTO.builder()
                                .code(sa.getCode())
                                .name(sa.getI18n(language).getName())
                                .build()));
            }
            if (application.getAerialSprayAgriculturalGroup() != null) {
                dto.setAerialSprayAgriculturalGroupCode(application.getAerialSprayAgriculturalGroup().getCode());
                dto.setAerialSprayAgriculturalGroupName(
                        application.getAerialSprayAgriculturalGroup().getI18n(language).getName());
            }
            if (application.getPhenophaseCulture() != null) {
                dto.setPhenophaseCultureCode(application.getPhenophaseCulture().getCode());
                dto.setPhenophaseCultureName(application.getPhenophaseCulture().getI18n(language).getName());
            }
        }

        return dto;
    }

    public static List<ApplicationS2695DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS2695DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2695DTO dto = new ApplicationS2695DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            if (sc.get("aerialSprayAgriculturalGroupCode") instanceof LinkedHashMap<?, ?> agriculturalGroup) {
                dto.setAerialSprayAgriculturalGroupCode(agriculturalGroup.get("value").toString());
                dto.setAerialSprayAgriculturalGroupName(agriculturalGroup.get("label").toString());
            }
            if (sc.get("aerialSprayAgriculturalFenofaza") instanceof LinkedHashMap<?, ?> phenophase) {
                dto.setPhenophaseCultureCode(phenophase.get("value").toString());
                dto.setPhenophaseCultureName(phenophase.get("label").toString());
            }
            if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> asc) {
                dto.getSubAgricultures().addAll(getClassifiers(asc));
            }

            dto.setAerialSprayStartDate(StringUtils.hasText(sc.get("aerialSprayStartDate").toString())
                    ? OffsetDateTime.parse(sc.get("aerialSprayStartDate").toString())
                    : null);
            dto.setAerialSprayEndDate(StringUtils.hasText(sc.get("aerialSprayEndDate").toString())
                    ? OffsetDateTime.parse(sc.get("aerialSprayEndDate").toString())
                    : null);
            dto.setAviationOperatorName(StringUtils.hasText(sc.get("operatorName").toString())
                    ? sc.get("operatorName").toString()
                    : null);
            dto.setAviationOperatorIdentifier(StringUtils.hasText(sc.get("operatorIdentifier").toString())
                    ? sc.get("operatorIdentifier").toString()
                    : null);

            Object operatorCh64CertNumber = sc.get("operatorCh64CertNumber");
            if (operatorCh64CertNumber != null) {
                dto.setAviationCh64CertNumber(operatorCh64CertNumber.toString());
            }

            Object operatorCh64CertDate = sc.get("operatorCh64CertDate");
            if (operatorCh64CertDate != null) {
                dto.setAviationCh64CertDate(OffsetDateTime.parse(operatorCh64CertDate.toString()));
            }

            PersonBO personBO = new PersonBO();
            personBO.setName(StringUtils.hasText(sc.get("operatorPersonName").toString())
                    ? sc.get("operatorPersonName").toString()
                    : "");
            personBO.setSurname(StringUtils.hasText(sc.get("operatorPersonSurname").toString())
                    ? sc.get("operatorPersonSurname").toString()
                    : "");
            personBO.setFamilyName(StringUtils.hasText(sc.get("operatorPersonFamilyName").toString())
                    ? sc.get("operatorPersonFamilyName").toString()
                    : "");
            personBO.setIdentifier(StringUtils.hasText(sc.get("operatorPersonIdentifier").toString())
                    ? sc.get("operatorPersonIdentifier").toString()
                    : null);
            dto.setCh83CertifiedPerson(personBO);

            dto.setWorksite(StringUtils.hasText(sc.get("operatorWorkSite").toString())
                    ? sc.get("operatorWorkSite").toString()
                    : null);

            dto.setWorkLand(StringUtils.hasText(sc.get("operatorWorkLand").toString())
                    ? sc.get("operatorWorkLand").toString()
                    : null);

            dto.setPlantProtectionProducts(getPlantProducts(sc.get("przGrid")));

            dto.setField(getField(sc));
        }

        return dto;
    }

    private static ApplicationS2695FieldDTO getField(LinkedHashMap<?, ?> sc) {
        ApplicationS2695FieldDTO field = new ApplicationS2695FieldDTO();

        field.setTreatmentStartHour(StringUtils.hasText(sc.get("treatmentStartHour").toString())
                ? OffsetDateTime.parse(sc.get("treatmentStartHour").toString())
                : null);
        field.setTreatmentEndHour(StringUtils.hasText(sc.get("treatmentEndHour").toString())
                ? OffsetDateTime.parse(sc.get("treatmentEndHour").toString())
                : null);
        field.setTreatmentDate(StringUtils.hasText(sc.get("treatmentDate").toString())
                ? OffsetDateTime.parse(sc.get("treatmentDate").toString())
                : null);

        if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> asc) {
            if (asc.get("treatmentSettlementsData") instanceof LinkedHashMap<?, ?> treatmentSettlement) {
                field.getDistantNeighborSettlements().add(getDistantNeighborSettlement(treatmentSettlement));
            } else if (asc.get("treatmentSettlementsData") instanceof ArrayList<?> treatmentSettlements) {
                for (Object ts : treatmentSettlements) {
                    field.getDistantNeighborSettlements().add(getDistantNeighborSettlement((LinkedHashMap<?, ?>) ts));
                }
            }
            if (asc.get("trearmentAddressData") instanceof LinkedHashMap<?, ?> addressData) {
                AddressDTO address = getAddress(addressData.get("facilityAddress"));
                if (addressData.get("facilityAddress") instanceof LinkedHashMap<?, ?> facilityAddress) {
                    Object treatmentArea = facilityAddress.get("treatmentArea");
                    if (treatmentArea != null) {
                        field.setTreatmentArea(Double.parseDouble(treatmentArea.toString()));
                    }
                    Object treatmentDistance = facilityAddress.get("treatmentDistance");
                    if (treatmentDistance != null) {
                        field.setTreatmentDistance(Double.parseDouble(treatmentDistance.toString()));
                    }
                    Object treatmentLand = facilityAddress.get("treatmentLand");
                    if (treatmentLand != null) {
                        address.setLand(treatmentLand.toString());
                    }
                }

                field.setTreatmentAddress(address);
            }
        }

        return field;
    }

    private static ApplicationS2695PppDTO getPppDto(LinkedHashMap<?, ?> sc) {
        ApplicationS2695PppDTO dto = null;
        if (sc.get("przFunctionCode") != null) {
            dto = new ApplicationS2695PppDTO();
//            getPlantProduct(dto, sc);

            if (sc.get("__additionalSpecificContent") instanceof LinkedHashMap<?, ?> asc) {
                Object przDataGrid = asc.get("przDataGrid");
                dto.getPppPests().addAll(getPests(przDataGrid));
            }
        } else {
            if (sc.get("przGrid") instanceof LinkedHashMap<?, ?> grid) {
                dto = new ApplicationS2695PppDTO();
                getPpp(dto, grid);
            } else if (sc.get("przGrid") instanceof ArrayList<?> pppGrid) {
                dto = new ApplicationS2695PppDTO();
                for (Object grid : pppGrid) {
                    dto = new ApplicationS2695PppDTO();
                    if (grid instanceof LinkedHashMap<?, ?> g) {
                        getPpp(dto, g);
                    }
                }
            }
        }
        return dto;
    }

    private static void getPpp(ApplicationS2695PppDTO pppDTO, LinkedHashMap<?, ?> grid) {
        if (grid.get("container") instanceof LinkedHashMap<?, ?> container) {
            if (container.get("specificContent") instanceof LinkedHashMap<?, ?> specificContent) {
//                getPlantProduct(pppDTO, specificContent);
            }
            List<PppPestBO> pests = getPests(container.get("przDataGrid"));
            pppDTO.setPppPests(pests);
        }
    }

    private static List<ApplicationS2695PppDTO> getPlantProducts(Object object) {
        List<ApplicationS2695PppDTO> plantProducts = null;
        if (object != null) {
            plantProducts = new ArrayList<>();
            if (object instanceof LinkedHashMap<?, ?> linkedHashMap) {
                plantProducts.add(getPlantProduct(linkedHashMap));
            } else if (object instanceof ArrayList<?> arrayList) {
                for (Object o : arrayList) {
                    plantProducts.add(getPlantProduct(o));
                }
            }
        }
        return plantProducts;
    }

    private static ApplicationS2695PppDTO getPlantProduct(Object object) {
        ApplicationS2695PppDTO dto = null;
        if (object instanceof LinkedHashMap<?, ?> sc0 && sc0.get("container") instanceof LinkedHashMap<?, ?> container) {
            if (container.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
                dto = new ApplicationS2695PppDTO();

                Object przPurchase = sc.get("przPurchase");
                if (przPurchase != null) {
                    dto.setPppPurchase(przPurchase.toString());
                }

                Object przName = sc.get("przName");
                if (przName != null) {
                    dto.setPppName(przName.toString());
                }

                Object przDose = sc.get("przDose");
                if (przDose != null) {
                    dto.setPppDose(Double.parseDouble(przDose.toString()));
                }

                Object przQuarantinePeriod = sc.get("przQuarantinePeriod");
                if (przQuarantinePeriod != null) {
                    dto.setPppQuarantinePeriod(Integer.parseInt(przQuarantinePeriod.toString()));
                }

                if (sc.get("przAerialSpray") instanceof LinkedHashMap<?, ?> spray) {
                    Object value = spray.get("value");
                    if (value != null) {
                        dto.setPppAerialSpray("yes".equals(value.toString()));
                    }
                }

                if (sc.get("foodMeasuringUnitCode2") instanceof LinkedHashMap<?, ?> unit) {
                    dto.setPppUnitCode(unit.get("value").toString());
                    dto.setPppUnitName(unit.get("label").toString());
                }

                if (sc.get("przFunctionCode") instanceof LinkedHashMap<?, ?> f) {
                    dto.setPppFunctionCode(f.get("value").toString());
                    dto.setPppFunctionName(f.get("label").toString());
                }

                dto.setPppPests(getPests(container.get("przDataGrid")));
            }
        }
        return dto;
    }

    private static List<PppPestBO> getPests(Object pppDataGrid) {
        List<PppPestBO> list = null;
        if (pppDataGrid instanceof LinkedHashMap<?, ?> ppp) {
            list = new ArrayList<>();
            list.add(getPest(ppp));
        } else if (pppDataGrid instanceof ArrayList<?> ppps) {
            list = new ArrayList<>();
            for (var ppp : ppps) {
                list.add(getPest((LinkedHashMap<?, ?>) ppp));
            }
        }
        return list;
    }

    private static PppPestBO getPest(LinkedHashMap<?, ?> ppp) {
        PppPestBO pestDTO = null;
        if (ppp.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
            pestDTO = new PppPestBO();
            pestDTO.setPest(sc.get("przPest").toString());
            if (sc.get("przPestGroupCode") instanceof LinkedHashMap<?, ?> group) {
                pestDTO.setPestGroupCode(group.get("value").toString());
                pestDTO.setPestGroupName(group.get("label").toString());
            }
        }
        return pestDTO;
    }

    private static List<KeyValueDTO> getClassifiers(LinkedHashMap<?, ?> asc) {
        List<KeyValueDTO> codes = new ArrayList<>();
        asc.forEach((o, o2) -> {
            if (o2 instanceof LinkedHashMap<?, ?> type) {
                if (type.get("value") != null) {
                    codes.add(KeyValueDTO.builder()
                            .code(type.get("value").toString())
                            .name(type.get("label").toString()).build());
                }
            } else if (o2 instanceof ArrayList<?> types) {
                types.forEach(type -> {
                    if (type instanceof LinkedHashMap<?, ?> t) {
                        if (t.get("value") != null) {
                            codes.add(KeyValueDTO.builder()
                                    .code(t.get("value").toString())
                                    .name(t.get("label").toString()).build());
                        }
                    }
                });
            }
        });
        return codes;
    }

    private static DistantNeighborSettlementBO getDistantNeighborSettlement(LinkedHashMap<?, ?> ts) {
        if (ts.get("specificContent") instanceof LinkedHashMap<?, ?> sc) {
            DistantNeighborSettlementBO dto = new DistantNeighborSettlementBO();
            dto.setTreatmentSettlements(StringUtils.hasText(sc.get("treatmentSettlements").toString())
                    ? sc.get("treatmentSettlements").toString()
                    : null);
            dto.setTreatmentDistances(StringUtils.hasText(sc.get("treatmentDistances").toString())
                    ? Double.parseDouble(sc.get("treatmentDistances").toString())
                    : null);
            return dto;
        }
        return null;
    }
}
