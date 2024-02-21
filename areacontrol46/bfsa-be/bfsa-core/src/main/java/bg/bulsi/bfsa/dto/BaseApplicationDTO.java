package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.util.Constants;
import bg.e_gov.eform.dictionary.agent.Agent;
import bg.e_gov.eform.dictionary.authorizationdocument.AuthorizationDocument;
import bg.e_gov.eform.dictionary.communicationchannels.CommunicationChannels;
import bg.e_gov.eform.dictionary.contactpoint.ContactPoint;
import bg.e_gov.eform.dictionary.identifier.Identifier;
import bg.e_gov.eform.dictionary.legalentity.LegalEntity;
import bg.e_gov.eform.dictionary.location.Location;
import bg.e_gov.eform.dictionary.person.Person;
import bg.e_gov.eform.dictionary.personaldata.PersonalData;
import bg.e_gov.eform.dictionary.requestauthor.RequestAuthor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import generated.ServiceRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class BaseApplicationDTO {

//    private LocalDateTime requestDateTime;
    private String entryNumber;
    private LocalDate entryDate;

    private String orderNumber;
    private LocalDate orderDate;

    /**
     * Common fields.
     */
    private String branchIdentifier;

    @NotNull
    private EntityType entityType;
    private ApplicationStatus applicationStatus;
    private BigDecimal applicationPrice;

    /**
     * Base Requestor data fields (Представляващо лице).
     */
    private String requestorIdentifier;
    private String requestorFullName;
    private String requestorPhone;
    private String requestorEmail;
    private String requestorPowerAttorneyNumber;
    private String requestorPowerAttorneyNotary;
    private LocalDate requestorPowerAttorneyDate;
    private LocalDate requestorPowerAttorneyUntilDate;
    private String requestorAuthorTypeCode;
    private String requestorAuthorTypeExternalCode;
    private AddressDTO requestorCorrespondenceAddress;

    /**
     * Base Applicant data fields (Заявител).
     */
    private String applicantIdentifier;
    private String applicantFullName;
    private String applicantEmail;
    private String applicantPhone;
    private String applicantLegalForm;
    private AddressDTO applicantCorrespondenceAddress;

    private Long recordId;
    private String recordIdentifier;
    private ServiceType serviceType;
    private String regNumber;
    private LocalDate regDate;
    private BigDecimal recordPrice;
    private RecordStatus recordStatus;

    private ApprovalDocumentStatus approvalDocumentStatus;
    private String approvalDocumentNumber;
    private byte[] certificateImage;

    @Builder.Default
    private List<String> errors = new ArrayList<>();

    public static BaseApplicationDTO of(final Record source) {
        BaseApplicationDTO dto = new BaseApplicationDTO();

        dto.setEntryNumber(source.getEntryNumber());
        dto.setEntryDate(source.getEntryDate());
//        dto.setRequestDateTime(source.getRequestDateTime());
        dto.setServiceType(source.getServiceType());
        dto.setRecordId(source.getId());
        dto.setRecordIdentifier(source.getIdentifier());

        if (source.getRequestor() != null) {
            dto.setRequestorIdentifier(source.getRequestor().getIdentifier());
            dto.setRequestorFullName(source.getRequestor().getFullName());
        }

        if (source.getApplicant() != null) {
            dto.setApplicantIdentifier(source.getApplicant().getIdentifier());
            dto.setApplicantFullName(source.getApplicant().getFullName());
        }

        dto.setRecordStatus(source.getStatus());
        dto.setRecordPrice(source.getPrice());

        if (source.getContractorPaper() != null) {
            dto.setApprovalDocumentStatus(source.getContractorPaper().getStatus());
            dto.setApprovalDocumentNumber(source.getContractorPaper().getRegNumber());
        }

        if (source.getFacilityPaper() != null) {
            dto.setApprovalDocumentStatus(source.getFacilityPaper().getStatus());
            dto.setApprovalDocumentNumber(source.getFacilityPaper().getRegNumber());
        }

        if (ServiceType.S502.equals(source.getServiceType())) {
            dto.setApplicationStatus(source.getApplicationS502().getStatus());
            dto.setApplicationPrice(source.getApplicationS502().getPrice());
        }

        return dto;
    }

    public void ofRecordBase(final Record source, final Language language) {

        this.recordId = source.getId();
        this.serviceType = source.getServiceType();
        this.recordStatus = source.getStatus();
        this.recordPrice = source.getPrice();

        if (source.getBranch() != null && StringUtils.hasText(source.getBranch().getIdentifier())) {
            this.branchIdentifier = source.getBranch().getIdentifier();
        }

//        --- Set Requestor ---
        if (source.getRequestor() != null) {
            this.requestorIdentifier = source.getRequestor().getIdentifier();
            this.requestorFullName = source.getRequestor().getFullName();
            this.requestorEmail = source.getRequestor().getEmail();
            this.requestorPhone = source.getRequestor().getPhone();

            if (!CollectionUtils.isEmpty(source.getRequestor().getAddresses())) {
                source.getRequestor().getAddresses().stream()
                        .filter(a -> Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE.equals(a.getAddressType().getCode()))
                        .findAny()
                        .ifPresent(a -> this.requestorCorrespondenceAddress = AddressDTO.of(a, language));
            }
        }
        this.requestorAuthorTypeCode = source.getRequestorAuthorType().getCode();
        this.requestorPowerAttorneyNumber = source.getRequestorPowerAttorneyNumber();
        this.requestorPowerAttorneyNotary = source.getRequestorPowerAttorneyNotary();
        this.requestorPowerAttorneyDate = source.getRequestorPowerAttorneyDate();
        this.requestorPowerAttorneyUntilDate = source.getRequestorPowerAttorneyUntilDate();

//        --- Set Applicant ---
        if (source.getApplicant() != null) {
            this.applicantIdentifier = source.getApplicant().getIdentifier();
            this.applicantFullName = source.getApplicant().getFullName();
            this.applicantEmail = source.getApplicant().getEmail();
            this.applicantPhone = source.getApplicant().getPhone();
            this.entityType = source.getApplicant().getEntityType();

            if (!CollectionUtils.isEmpty(source.getApplicant().getAddresses())) {
                source.getApplicant().getAddresses().stream()
                        .filter(a -> Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE.equals(a.getAddressType().getCode()))
                        .findAny()
                        .ifPresent(a -> this.applicantCorrespondenceAddress = AddressDTO.of(a, language));
            }
        }

        if (source.getContractorPaper() != null) {
            this.approvalDocumentStatus = source.getContractorPaper().getStatus();
            this.approvalDocumentNumber = source.getContractorPaper().getRegNumber();
        }

        if (source.getFacilityPaper() != null) {
            this.approvalDocumentStatus = source.getFacilityPaper().getStatus();
            this.approvalDocumentNumber = source.getFacilityPaper().getRegNumber();
        }

        if (!CollectionUtils.isEmpty(source.getErrors())) {
            this.errors.addAll(source.getErrors());
        }
    }

    public void of(final ServiceRequest serviceRequest, DocWS docInfo) {

//        if (serviceRequest.getRequestDateTime() != null) {
//            this.requestDateTime = serviceRequest.getRequestDateTime().toGregorianCalendar().toZonedDateTime().toLocalDateTime();
//        }
        if (docInfo != null) {
            this.entryNumber = docInfo.getRnDoc();
            if (docInfo.getDatDoc() != null) {
                this.entryDate = docInfo.getDatDoc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        }

        this.branchIdentifier = serviceRequest.getServiceProvider().getAdministrativeUnits().getLegalIdentifier().getIdentifier();

        // TODO: Remove after find correct branch identifier from json
        if (this.branchIdentifier == null) {
            this.branchIdentifier = serviceRequest.getServiceProvider().getLegalIdentifier().getIdentifier();
        }

//        --- REQUESTOR INFORMATION ---
        RequestAuthor requestor = serviceRequest.getRequestAuthor();
        Person requestorPerson = requestor.getPerson();
        PersonalData requestorPersonalData = requestorPerson.getPersonalData();
        CommunicationChannels requestCommunicationChannels = requestor.getCommunicationChannels();

        Identifier requestorIdentifier = requestorPersonalData.getIdentifier();

        AuthorizationDocument authorizationDocument = requestorPerson.getAuthorizationDocument();
        if (authorizationDocument != null) {
            ZonedDateTime utcZonedDate = authorizationDocument.getDateTime()
                    .toGregorianCalendar().toZonedDateTime().withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime utcZonedUntilDate = authorizationDocument.getValidTo()
                    .toGregorianCalendar().toZonedDateTime().withZoneSameInstant(ZoneId.of("UTC"));
            this.requestorPowerAttorneyNumber = authorizationDocument.getNumber();
            this.requestorPowerAttorneyNotary = authorizationDocument.getCertifiedBy();
            this.requestorPowerAttorneyDate = utcZonedDate.toLocalDate();
            this.requestorPowerAttorneyUntilDate = utcZonedUntilDate.toLocalDate();
        }

        this.requestorIdentifier = requestorIdentifier.getIdentifier();
        this.requestorFullName = requestorPersonalData.getFullName();
        this.requestorAuthorTypeExternalCode = serviceRequest.getAuthorType().getCode();

        if (requestCommunicationChannels != null
                && !CollectionUtils.isEmpty(requestCommunicationChannels.getCommunicationChannel())) {
            for (ContactPoint p : requestCommunicationChannels.getCommunicationChannel()) {
                if ("1006-030006".equals(p.getContactType().getCode())) {
                    this.requestorEmail = p.getContact();
                }
                if ("1006-030005".equals(p.getContactType().getCode())) {
                    this.requestorPhone = p.getContact();
                }
            }

        }

//        --- APPLICANT INFORMATION ---
        Agent applicantAuthor = serviceRequest.getApplicant();
        Person applicantPerson = applicantAuthor.getPerson();
        PersonalData applicantPersonalData = applicantPerson.getPersonalData();
        LegalEntity applicantLegalData = applicantAuthor.getLegal();
        CommunicationChannels applicantCommunicationChannels = applicantAuthor.getCommunicationChannels();
        Location applicantCorrespondenceAddress = applicantPerson.getCorrAddress();

        // TODO Which one ? Legal or ApplicantPerson
        if (applicantLegalData != null && applicantLegalData.getLegalData() != null && StringUtils.hasText(applicantLegalData.getLegalData().getEik())) {
            this.applicantIdentifier = applicantLegalData.getLegalData().getEik();
            this.applicantFullName = applicantLegalData.getLegalData().getName();
            this.applicantLegalForm = applicantLegalData.getLegalData().getCompanyType();
            this.entityType = EntityType.LEGAL;
        } else if (applicantPersonalData != null && applicantPersonalData.getIdentifier() != null && StringUtils.hasText(applicantPersonalData.getIdentifier().getIdentifier())) {
            this.applicantIdentifier = applicantPersonalData.getIdentifier().getIdentifier();
            this.applicantFullName = applicantPersonalData.getFullName();
            this.entityType = EntityType.PHYSICAL;
        }

        // TODO The information is in missing contact form section
        if (applicantCommunicationChannels != null
                && !CollectionUtils.isEmpty(applicantCommunicationChannels.getCommunicationChannel())) {
            for (ContactPoint p : applicantCommunicationChannels.getCommunicationChannel()) {
                if ("1006-030006".equals(p.getContactType().getCode())) {
                    this.applicantEmail = p.getContact();
                }
                if ("1006-030005".equals(p.getContactType().getCode())) {
                    this.applicantPhone = p.getContact();
                }
            }
        }

        this.applicantCorrespondenceAddress = AddressDTO.of(applicantCorrespondenceAddress,
                Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE);
    }

    public static AddressDTO getAddress(final Object addressObj) {
        AddressDTO addressDTO = null;
        if (addressObj instanceof LinkedHashMap<?, ?> address) {

            if (address.get("SettlementCode") == null) {
                if (address.get("CountrySelect") instanceof LinkedHashMap<?, ?> country) {
                    addressDTO = new AddressDTO();
                    addressDTO.setCountryCode(country.get("countryCode").toString());
                    addressDTO.setCountryName(country.get("countryName").toString());
                    String fullAddress = address.get("FullAddress").toString();
                    addressDTO.setAddress(fullAddress);
                    addressDTO.setFullAddress(addressDTO.getCountryName() + ", " + fullAddress);
                }
            } else {
                addressDTO = new AddressDTO();
                addressDTO.setSettlementCode(address.get("SettlementCode") != null
                        ? address.get("SettlementCode").toString()
                        : null);
                addressDTO.setSettlementName(address.get("Settlement") != null
                        ? address.get("Settlement").toString()
                        : null);
                String buildingNumber = "";
                String floor = "";
                String apartment = "";
                if (address.get("BuildingNumber") != null) {
                    buildingNumber = StringUtils.hasText(address.get("BuildingNumber").toString())
                            && !address.get("BuildingNumber").toString().equals("null")
                            ? ", № " + address.get("BuildingNumber").toString()
                            : "";
                }
                String entrance = "";
                if (address.get("Entrance") != null) {
                    entrance = StringUtils.hasText(address.get("Entrance").toString())
                            && !address.get("Entrance").toString().equals("null")
                            ? ", вх. " + address.get("Entrance").toString()
                            : "";
                }
                if (address.get("Floor") != null) {
                    floor = StringUtils.hasText(address.get("Floor").toString())
                            && !address.get("Floor").toString().equals("null")
                            ? ", ет. " + address.get("Floor").toString()
                            : "";
                }
                if (address.get("Apartment") != null) {
                    apartment = StringUtils.hasText(address.get("Apartment").toString())
                            && !address.get("Apartment").toString().equals("null")
                            ? ", ап. " + address.get("Apartment").toString()
                            : "";
                }

                String location = address.get("LocationName") != null
                        ? ", " + address.get("LocationName").toString()
                        : "";
                String fullAddress = address.get("Country").toString() + ", "
                        + address.get("District").toString() + ", "
                        + address.get("Municipality").toString() + ", "
                        + address.get("Settlement").toString()
                        + location
                        + buildingNumber + entrance + floor + apartment;
                addressDTO.setFullAddress(fullAddress);
                addressDTO.setCountryCode(address.get("CountryCode").toString());
                if (address.get("PostCode") != null) {
                    addressDTO.setPostCode(address.get("PostCode").toString());
                }
                if (address.get("LocationName") != null) {
                    addressDTO.setAddress(address.get("LocationName").toString()
                            + buildingNumber + entrance + floor + apartment);
                }
            }
        }

        return addressDTO;
    }

    public static List<String> getCh50Vehicles(final Object descriptionCH50) {
        List<String> vehicleCertificateNumbers = new ArrayList<>();
        if (descriptionCH50 instanceof LinkedHashMap<?, ?> ch50Vehicle) {
            vehicleCertificateNumbers.add(getCh50Vehicle(ch50Vehicle));
        } else if (descriptionCH50 instanceof ArrayList<?> ch50Vehicle) {
            ch50Vehicle.forEach(v -> vehicleCertificateNumbers.add(getCh50Vehicle((LinkedHashMap<?, ?>) v)));
        }
        return vehicleCertificateNumbers;
    }

    private static String getCh50Vehicle(final LinkedHashMap<?, ?> descriptionCH50) {
        Object vehicleCertificateNumber = findValue(descriptionCH50, "vehicleCertificateNumber");
        if (vehicleCertificateNumber != null) {
            return vehicleCertificateNumber.toString();
        }
        return null;
    }

    public static List<VehicleDTO> getCh52Vehicles(final Object descriptionCH52) {
        List<VehicleDTO> vehicleDTOs = new ArrayList<>();

        if (descriptionCH52 instanceof LinkedHashMap<?, ?> ch52Vehicle) {
            vehicleDTOs.add(getCh52Vehicle(ch52Vehicle));
        } else if (descriptionCH52 instanceof ArrayList<?> ch52Vehicle) {
            ch52Vehicle.forEach(v -> vehicleDTOs.add(getCh52Vehicle((LinkedHashMap<?, ?>) v)));
        }
        return vehicleDTOs;
    }

    private static VehicleDTO getCh52Vehicle(final LinkedHashMap<?, ?> descriptionCH52) {
        VehicleDTO dto = null;
        if (descriptionCH52.get("specificContent") instanceof LinkedHashMap<?, ?> ch52Vehicle) {
            dto = new VehicleDTO();
            dto.setRegistrationPlate(ch52Vehicle.get("vehicleRegistrationPlate").toString());
            dto.setBrandModel(ch52Vehicle.get("vehicleBrandModel").toString());
            dto.setVehicleTypeCode(ch52Vehicle.get("vehicleTypeCode") instanceof LinkedHashMap<?, ?> vehicleType
                    ? vehicleType.get("value").toString()
                    : null);
            dto.setVehicleOwnershipTypeCode(ch52Vehicle.get("vehicleOwnershipTypeCode") instanceof LinkedHashMap<?, ?> vehicleOwnershipType
                    ? vehicleOwnershipType.get("value").toString()
                    : null);
            dto.setLoadUnitCode(ch52Vehicle.get("vehicleVolumeUnitCode") instanceof LinkedHashMap<?, ?> vehicleVolumeUnitCode
                    ? vehicleVolumeUnitCode.get("value").toString()
                    : null);
            dto.setLoad(ch52Vehicle.get("vehicleLoad") != null
                    ? Double.parseDouble(ch52Vehicle.get("vehicleLoad").toString())
                    : null);
            dto.setVolume(ch52Vehicle.get("vehicleVolume2") != null
                    ? Double.parseDouble(ch52Vehicle.get("vehicleVolume2").toString())
                    : null);
            dto.setLicenseNumber(ch52Vehicle.get("vehicleLicenseNumber") != null
                    ? ch52Vehicle.get("vehicleLicenseNumber").toString()
                    : null);

            dto.setFoodTypes(getFoodTypes(ch52Vehicle.get("producedFood"), "foodProduced"));
        }
        return dto;
    }

    protected static String getLastGroupCode(LinkedHashMap<?, ?> container, String parentCode) {
        String foodGroup = "foodGroup";

        Object object = container.get(foodGroup + parentCode);
        String result = null;
        if (object instanceof ArrayList<?>) {
            result = parentCode;
        } else if (object instanceof LinkedHashMap<?,?> map) {
            String code = map.get("value").toString();
            object = container.get(foodGroup + code);
            if (object == null) {
                result = parentCode;
            } else {
                result = getLastGroupCode(container, code);
            }
        }
        return result;
    }

    protected static Set<KeyValueDTO> getFoodTypes(final Object source) {
        Set<KeyValueDTO> foodTypes = null;
        if (source instanceof LinkedHashMap<?, ?> foodType) {
            foodTypes = new HashSet<>();
            foodTypes.add(KeyValueDTO.builder()
                    .code(foodType.get("value").toString())
                    .name(foodType.get("label").toString()).build());
        } else if (source instanceof ArrayList<?> foodType) {
            foodTypes = new HashSet<>();
            for (Object food : foodType) {
                if (food instanceof LinkedHashMap<?, ?> f) {
                    foodTypes.add(KeyValueDTO.builder()
                            .code(f.get("value").toString())
                            .name(f.get("label").toString()).build());
                }
            }
        }
        return foodTypes;
    }

    protected static Set<KeyValueDTO> getFoodTypes(final Object source, final String name) {
        Set<KeyValueDTO> foodTypes = null;
        if (source instanceof LinkedHashMap<?,?> foodType) {
            foodTypes = getFoodTypesRecursive(foodType, name);
        } else if (source instanceof ArrayList<?> foodType) {
            foodTypes = new HashSet<>();
            for (Object food : foodType) {
                Set<KeyValueDTO> subFood = getFoodTypesRecursive(food, name);
                if (subFood != null) {
                    foodTypes.addAll(subFood);
                }
            }
        }
        return foodTypes;
    }

    private static Set<KeyValueDTO> getFoodTypesRecursive(final Object source, final String name) {
        if (source instanceof LinkedHashMap<?,?> linkedHashMap) {
            if (findValue(linkedHashMap, name) instanceof LinkedHashMap<?,?> lhm) {
                if (lhm.get("value") instanceof String code) {
                    Object group = findValue(linkedHashMap, "foodGroup" + code);
                    if (group == null) {
                        return getFoodTypes(lhm);
                    } else if (group instanceof LinkedHashMap<?,?>) {
                        return getFoodTypesRecursive(source, "foodGroup" + code);
                    } else if (group instanceof ArrayList<?> arrayList) {
                        return getFoodTypes(arrayList);
                    }
                }
            } else {
                return getFoodTypes(linkedHashMap.get(name));
            }
        }
        return null;
    }

// It returns all FoodTypes in tree structure
//    protected static Set<KeyValueDTO> getFoodTypes(final Object source, final String name) {
//        return getFoodTypes(source, name, null);
//    }
//
//    protected static Set<KeyValueDTO> getFoodTypes(final Object source, final String name, KeyValueDTO parent) {
//        Set<KeyValueDTO> foodTypes = null;
//        if (source instanceof LinkedHashMap<?,?> container && findValue(container, name) instanceof LinkedHashMap<?, ?> sc) {
//            String lastCode = getLastGroupCode(container, sc.get("value").toString());
//            Object foodGroup = container.get("foodGroup" + lastCode);
//            if (foodGroup instanceof LinkedHashMap<?, ?> foodType) {
//                foodTypes = new HashSet<>();
//                foodTypes.add(KeyValueDTO.builder()
//                        .parent(parent)
//                        .code(foodType.get("value").toString())
//                        .name(foodType.get("label").toString()).build());
//            } else if (foodGroup instanceof ArrayList<?> foodType) {
//                foodTypes = new HashSet<>();
//                for (Object food : foodType) {
//                    if (food instanceof LinkedHashMap<?, ?> f) {
//                        foodTypes.add(KeyValueDTO.builder()
//                                .parent(parent)
//                                .code(f.get("value").toString())
//                                .name(f.get("label").toString()).build());
//                    }
//                }
//            }
//        }
//        return foodTypes;
//    }

    protected static Set<KeyValueDTO> getFoodTypeCodes(final LinkedHashMap<?, ?> source, final String name) {
        Set<KeyValueDTO> foodTypes = null;
        if (source.get(name) instanceof LinkedHashMap<?, ?> foodType) {
            foodTypes = new HashSet<>(getClassifierFoodTypes(foodType));
        } else if (source.get(name) instanceof ArrayList<?> foodType) {
            foodTypes = new HashSet<>();
            for (Object food : foodType) {
                if (food instanceof LinkedHashMap<?, ?> f) {
                    foodTypes.addAll(getClassifierFoodTypes(f));
                }
            }
        }
        return foodTypes;
    }

    public static Object findValue(final LinkedHashMap<?, ?> map, final String key) {
        LinkedList<LinkedHashMap<?, ?>> queue = new LinkedList<>();
        queue.add(map);

        while (!queue.isEmpty()) {
            LinkedHashMap<?, ?> currentMap = queue.poll();

            for (Map.Entry<?, ?> entry : currentMap.entrySet()) {
                if (entry.getKey().equals(key)) {
                    return entry.getValue();
                }
                if (entry.getValue() instanceof LinkedHashMap) {
                    queue.add((LinkedHashMap<?, ?>) entry.getValue());
                }
            }
        }
        return null;
    }

    protected static List<KeyValueDTO> getClassifierFoodTypes(final LinkedHashMap<?, ?> foods) {
        List<KeyValueDTO> codes = new ArrayList<>();
        foods.forEach((o, o2) -> {
            if (o2 instanceof LinkedHashMap<?, ?> food) {
                codes.add(KeyValueDTO.builder().code(food.get("value").toString()).build());
            } else if (o2 instanceof ArrayList<?> f2) {
                f2.forEach(f3 -> {
                    if (f3 instanceof LinkedHashMap<?, ?> f) {
                        codes.add(KeyValueDTO.builder().code(f.get("value").toString()).build());
                    }
                });
            }
        });
        return codes;
    }

    protected static List<KeyValueDTO> getKeyValues(final Object object) {
        List<KeyValueDTO> keyValues = null;

        if (object != null) {
            keyValues = new ArrayList<>();
            if (object instanceof LinkedHashMap<?, ?> linkedHashMap) {
                keyValues.add(getKeyValue(linkedHashMap));
            } else if (object instanceof ArrayList<?> arrayList) {
                for (Object o : arrayList) {
                    keyValues.add(getKeyValue(o));
                }
            }
        }
        return keyValues;
    }

    private static KeyValueDTO getKeyValue(final Object o) {
        if (o instanceof LinkedHashMap<?, ?> linkedHashMap) {
            return KeyValueDTO.builder()
                    .code(linkedHashMap.get("value").toString())
                    .name(linkedHashMap.get("label").toString()).build();
        }
        return null;
    }

    protected static String getKeyByPieceOfKey(LinkedHashMap<?, ?> hashMap, String pieceOfKey) {
        for (Map.Entry<?, ?> entry : hashMap.entrySet()) {
            String key = entry.getKey().toString();
            if (key.contains(pieceOfKey)) {
                return entry.getKey().toString();
            }
        }
        return null;
    }
}
