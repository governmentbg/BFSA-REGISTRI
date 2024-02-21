package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS502;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS502DTO extends BaseApplicationDTO {

    private List<AddressBO> testingAddresses = new ArrayList<>();
    private List<String> plantGroupTypeCodes = new ArrayList<>();
    private List<PersonBO> pppTestingPersons = new ArrayList<>();
    private List<String> easedFacilities = new ArrayList<>();
    private List<MaintenanceEquipmentBO> maintenanceEquipments = new ArrayList<>();

//    standardOperatingProcedures
//    standardOperatingProcedureFileName
//    standardOperatingProcedureDescription

    private List<TestMethodologyBO> testMethodologies = new ArrayList<>();
    private String researchPlansDescription;
    private String archivingDocDescription;


    public static ApplicationS502DTO ofRecord(final Record source, Language language) {
        ApplicationS502DTO dto = new ApplicationS502DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS502 application = source.getApplicationS502();
        if (application != null) {
            dto.setApplicationStatus(application.getStatus());
            if (!CollectionUtils.isEmpty(application.getTestingAddresses())) {
                dto.getTestingAddresses().addAll(application.getTestingAddresses());
            }
            if (!CollectionUtils.isEmpty(application.getPlantGroupTypes())) {
                for (Nomenclature n : application.getPlantGroupTypes()) {
                    dto.getPlantGroupTypeCodes().add(n.getCode());
                }
            }
            dto.setPppTestingPersons(application.getPppTestingPersons());
            dto.setEasedFacilities(application.getEasedFacilities());
            dto.setMaintenanceEquipments(application.getMaintenanceEquipments());
            dto.setTestMethodologies(application.getTestMethodologies());
            dto.setResearchPlansDescription(application.getResearchPlansDescription());
            dto.setArchivingDocDescription(application.getArchivingDocDescription());
        }

        return dto;
    }

    public static ApplicationS502DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {

        ApplicationS502DTO dto = new ApplicationS502DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            //    Mеста за изпитване
            Object testingAddresses = findValue(sc, "testingAddresses");
            if (testingAddresses instanceof LinkedHashMap<?, ?> address) {
                dto.getTestingAddresses().add(getTestingAddress(address));
            } else if (testingAddresses instanceof ArrayList<?> addresses) {
                addresses.forEach(a -> dto.getTestingAddresses().add(getTestingAddress(a)));
            }

            //    Искано одобрение по чл. 23, ал. 1 от Наредба № 19 за първоначално одобряване на база за следната/ните групи култури (избор от номенклатура с чек бокс)
            if (findValue(sc, "plantGroupType") instanceof ArrayList<?> plantGroupTypes) {
                plantGroupTypes.forEach(pt -> {
                    if (pt instanceof LinkedHashMap<?, ?> plantGroupType) {
                        dto.getPlantGroupTypeCodes().add(plantGroupType.get("key").toString());

                    }
                });
            }

            //    Информация по Приложението на Наредба № 19.
            Object testingPersons = findValue(sc, "pppTestingPersons");
            if (testingPersons instanceof LinkedHashMap<?, ?> person) {
                dto.getPppTestingPersons().add(getTestingPerson(person));
            } else if (testingPersons instanceof ArrayList<?> persons) {
                persons.forEach(p -> dto.getPppTestingPersons().add(getTestingPerson(p)));
            }

            //    Списък на притежаваните или взети под наем съоръжения /сгради, оранжерии и др.
            Object easedFacilities = findValue(sc, "easedFacilities");
            if (easedFacilities instanceof LinkedHashMap<?, ?> facility) {
                dto.getEasedFacilities().add(getEasedFacility(facility));
            } else if (easedFacilities instanceof ArrayList<?> facilities) {
                facilities.forEach(f -> dto.getEasedFacilities().add(getEasedFacility(f)));
            }

            //    Оборудване - поддръжка и калибриране.
            Object maintenanceEquipments = findValue(sc, "maintenanceEquipmentsData");
            if (maintenanceEquipments instanceof LinkedHashMap<?, ?> maintenanceEquipment) {
                dto.getMaintenanceEquipments().add(getMaintenanceEquipment(maintenanceEquipment));
            } else if (maintenanceEquipments instanceof ArrayList<?> equipments) {
                equipments.forEach(e -> dto.getMaintenanceEquipments().add(getMaintenanceEquipment(e)));
            }

            //    Методики на изпитване
            Object testMethodologies = findValue(sc, "testMethodologiesData");
            if (testMethodologies instanceof LinkedHashMap<?, ?> testMethodology) {
                dto.getTestMethodologies().add(getTestMethodologies(testMethodology));
            } else if (testMethodologies instanceof ArrayList<?> methodologies) {
                methodologies.forEach(m -> dto.getTestMethodologies().add(getTestMethodologies(m)));
            }

            //    Планове за изследване, организиране на опит, събиране на сурови данни, доклади (текст)
            if (sc.get("researchPlansDecription") != null) {
                dto.setResearchPlansDescription(sc.get("researchPlansDecription").toString());
            }

            //    Организация на архивиране на документацията от изпитването (текст)
            if (sc.get("archivingDocDescription") != null) {
                dto.setArchivingDocDescription(sc.get("archivingDocDescription").toString());
            }
        }

        return dto;
    }

    private static AddressBO getTestingAddress(final Object addressObj) {
        AddressBO testingAddress = null;

        if (addressObj instanceof LinkedHashMap<?, ?> address) {
            testingAddress = new AddressBO();

            if (address.get("address") instanceof LinkedHashMap<?, ?> adr) {

                if (adr.get("FullAddress") != null) {
                    testingAddress.setFullAddress(adr.get("FullAddress").toString());
                }
                if (adr.get("Settlement") != null) {
                    testingAddress.setSettlementName(adr.get("Settlement").toString());
                }
                if (adr.get("SettlementCode") != null) {
                    testingAddress.setSettlementCode(adr.get("SettlementCode").toString());
                }
                if (adr.get("plot") != null) {
                    testingAddress.setPlotNumber(adr.get("plot").toString());
                }
                if (adr.get("land") != null) {
                    testingAddress.setLand(adr.get("land").toString());
                }
            }
        }
        return testingAddress;
    }

    private static PersonBO getTestingPerson(final Object personObj) {
        PersonBO pppTestingPerson = null;

        if (personObj instanceof LinkedHashMap<?, ?> person) {
            pppTestingPerson = new PersonBO();

            if (person.get("specificContent") instanceof LinkedHashMap<?, ?> specificContent) {

                if (specificContent.get("pppTestingPersonEgn") != null) {
                    pppTestingPerson.setIdentifier(specificContent.get("pppTestingPersonEgn").toString());
                }
                if (specificContent.get("pppTestingPersonResponsibility") != null) {
                    pppTestingPerson.setDescription(specificContent.get("pppTestingPersonResponsibility").toString());
                }
                if (specificContent.get("pppTestingPersonFirstName") != null) {
                    pppTestingPerson.setName(specificContent.get("pppTestingPersonFirstName").toString());
                }
                if (specificContent.get("pppTestingPersonSecondName") != null) {
                    pppTestingPerson.setSurname(specificContent.get("pppTestingPersonSecondName").toString());
                }
                if (specificContent.get("pppTestingPersonLastName") != null) {
                    pppTestingPerson.setFamilyName(specificContent.get("pppTestingPersonLastName").toString());
                }
            }
        }

        return pppTestingPerson;
    }

    private static String getEasedFacility(final Object easedFacilityObj) {

        if (easedFacilityObj instanceof LinkedHashMap<?, ?> facility) {
            if (facility.get("specificContent") instanceof LinkedHashMap<?, ?> specificContent) {
                return specificContent.get("easedFacilityDescription").toString();
            }
        }

        return null;
    }

    private static MaintenanceEquipmentBO getMaintenanceEquipment(final Object equipmentObj) {
        MaintenanceEquipmentBO maintenanceEquipment = null;

        if (equipmentObj instanceof LinkedHashMap<?, ?> map && map.get("specificContent") instanceof LinkedHashMap<?, ?> equipment) {
            maintenanceEquipment = new MaintenanceEquipmentBO();

            if (equipment.get("maintenanceEquipmentKind") instanceof LinkedHashMap<?, ?> equipmentKind) {
                maintenanceEquipment.setEquipmentTypeCode(equipmentKind.get("value").toString());
                maintenanceEquipment.setEquipmentTypeName(equipmentKind.get("label").toString());
                if ("0004100".equals(maintenanceEquipment.getEquipmentTypeCode()) &&
                        equipment.get("maintenanceEquipmentType0004100") instanceof LinkedHashMap<?, ?> maintenanceEquipmentType0004100) {
                    maintenanceEquipment.setEquipmentSubTypeCode(maintenanceEquipmentType0004100.get("value").toString());
                    maintenanceEquipment.setEquipmentSubTypeName(maintenanceEquipmentType0004100.get("label").toString());
                } else if ("0004200".equals(maintenanceEquipment.getEquipmentTypeCode()) &&
                        equipment.get("maintenanceEquipmentType0004200") instanceof LinkedHashMap<?, ?> maintenanceEquipmentType0004200) {
                    maintenanceEquipment.setEquipmentSubTypeCode(maintenanceEquipmentType0004200.get("value").toString());
                    maintenanceEquipment.setEquipmentSubTypeName(maintenanceEquipmentType0004200.get("label").toString());
                }
            }
            if (equipment.get("maintenanceEquipmentDescriprion") != null) {
                maintenanceEquipment.setDescription(equipment.get("maintenanceEquipmentDescriprion").toString());
            }
        }
        return maintenanceEquipment;
    }

    private static TestMethodologyBO getTestMethodologies(final Object methodologyObj) {
        TestMethodologyBO testMethodology = null;

        if (methodologyObj instanceof LinkedHashMap<?, ?> methodologies) {
            testMethodology = new TestMethodologyBO();

            if (methodologies.get("specificContent") instanceof LinkedHashMap<?, ?> methodology) {
                if (methodology.get("testMethodologyType") instanceof LinkedHashMap<?, ?> methodologyType) {
                    testMethodology.setTypeName(methodologyType.get("label").toString());
                    testMethodology.setTypeCode(methodologyType.get("key").toString());
                }
                if (methodology.get("testMethodologyDescription") != null) {
                    testMethodology.setDescription(methodology.get("testMethodologyDescription").toString());
                }
            }
        }
        return testMethodology;
    }
}
