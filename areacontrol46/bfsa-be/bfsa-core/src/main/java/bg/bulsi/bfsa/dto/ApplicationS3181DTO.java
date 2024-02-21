package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS3181;
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
import java.util.stream.Collectors;

/**
 * №3181 Регистрация на операторите, които извършват търговия с храни от разстояние
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS3181DTO extends BaseApplicationDTO {

    // Address
    private AddressDTO address;

    // TODO rename to facilityRegNumbers
    private List<String> facilitiesPaperNumbers = new ArrayList<>();
    private List<ForeignFacilityAddressDTO> foreignFacilityAddresses = new ArrayList<>();

    // Vehicles data
    private List<VehicleDTO> vehicles = new ArrayList<>();
    private List<String> ch50VehicleCertNumbers = new ArrayList<>();
    private OffsetDateTime commencementActivityDate;

    private List<FacilityDTO> facilities = new ArrayList<>();

    public static ApplicationS3181DTO of(final Record source, final Language language) {
        ApplicationS3181DTO dto = new ApplicationS3181DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS3181 application = source.getApplicationS3181();
        if (application != null) {
            dto.setCommencementActivityDate(application.getCommencementActivityDate());
            if (!CollectionUtils.isEmpty(application.getAddresses())) {
                application.getAddresses().forEach(a -> {
                    // We should have only one ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE Do we need check here?
                    if (Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE
                            .equals(a.getAddressType().getCode())) {
                        dto.setAddress(AddressDTO.of(a, language));
                    } else {
//                    if (Constants.ADDRESS_TYPE_FOREIGN_FACILITY_CODE.equals(a.getAddressType().getCode())) {
                        dto.getForeignFacilityAddresses().add(ForeignFacilityAddressDTO.of(a, language));
                    }
                });
            }

            if (!CollectionUtils.isEmpty(application.getFacilities())) {
                dto.setFacilities(FacilityDTO.baseOf(application.getFacilities(), language));
            }
            if (!CollectionUtils.isEmpty(application.getApplicationS3181Vehicles())) {
                dto.setVehicles(VehicleDTO.ofS3181Vehicle(application.getApplicationS3181Vehicles(), language));
            }
        }
        return dto;
    }

    public static List<ApplicationS3181DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS3181DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS3181DTO dto = new ApplicationS3181DTO();

        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }
            // TODO To use the method from base class, address should be the same in all applications
            dto.setAddress(getCommonAddress(sc));

            dto.getForeignFacilityAddresses().addAll(getForeignFacilityAddresses(sc.get("descriptionCH53")));
            dto.getFacilitiesPaperNumbers().addAll(getFacilityPaperRegNumbers(sc.get("registeredApprovedObjectInBulgariaNumber")));
            dto.getCh50VehicleCertNumbers().addAll(getCh50Vehicles(sc.get("descriptionCH50")));
            dto.getVehicles().addAll(getCh52Vehicles(sc.get("descriptionCH52")));

            if (sc.get("commencementActivityDate") != null) {
                dto.setCommencementActivityDate(OffsetDateTime.parse(sc.get("commencementActivityDate").toString()));
            }
        }

        return dto;
    }

    private static List<String> getFacilityPaperRegNumbers(Object facilityPaperRegNumberObject) {
        List<String> facilityPaperRegNumbers = new ArrayList<>();
        if (facilityPaperRegNumberObject instanceof LinkedHashMap<?, ?> regNumber) {
            facilityPaperRegNumbers.add(getFacilityPaperRegNumber(regNumber));
        } else if (facilityPaperRegNumberObject instanceof ArrayList<?> regNumbers) {
            regNumbers.forEach(v -> facilityPaperRegNumbers.add(getFacilityPaperRegNumber((LinkedHashMap<?, ?>) v)));
        }
        return facilityPaperRegNumbers;
    }

    private static String getFacilityPaperRegNumber(LinkedHashMap<?, ?> map) {
        String facilityPaperRegNumber = null;
        if (map.get("specificContent") instanceof LinkedHashMap<?, ?> specificContent) {
            facilityPaperRegNumber = specificContent.get("facilityPaperRegNumber").toString();
        }
        return facilityPaperRegNumber;
    }

    private static List<ForeignFacilityAddressDTO> getForeignFacilityAddresses(Object foreignFacilityAddressObject) {
        List<ForeignFacilityAddressDTO> foreignFacilityAddresses = new ArrayList<>();
        if (foreignFacilityAddressObject instanceof LinkedHashMap<?, ?> address) {
            foreignFacilityAddresses.add(getForeignFacilityAddress(address));
        } else if (foreignFacilityAddressObject instanceof ArrayList<?> addresses) {
            addresses.forEach(a -> foreignFacilityAddresses.add(getForeignFacilityAddress((LinkedHashMap<?, ?>) a)));
        }
        return foreignFacilityAddresses;
    }

    private static ForeignFacilityAddressDTO getForeignFacilityAddress(LinkedHashMap<?, ?> map) {
        if (map.get("specificContent") instanceof LinkedHashMap<?, ?> specificContent &&
                specificContent.get("countryCode") instanceof LinkedHashMap<?, ?> countryCode) {
            return ForeignFacilityAddressDTO.builder()
                    .countryCode(countryCode.get("countryCode").toString())
                    .address(specificContent.get("AddressOutsideBulgaria").toString())
                    .build();
        }
        return null;
    }

    private static AddressDTO getCommonAddress(final LinkedHashMap<?, ?> addressObj) {
        AddressDTO dto = new AddressDTO();

        if (findValue(addressObj, "commonPresAddrSettlementSelect") instanceof LinkedHashMap<?, ?> settlement) {
            if (settlement.get("settlementCode") != null) {
                dto.setSettlementCode(settlement.get("settlementCode").toString());
            }
        }
        if (findValue(addressObj, "commonPresAddrCountrySelect") instanceof LinkedHashMap<?, ?> country) {
            if (country.get("countryCode") != null) {
                dto.setCountryCode(country.get("countryCode").toString());
            }
        }
        Object location = findValue(addressObj, "commonPresAddrLocationName");
        if (location != null) {
            dto.setAddress(location.toString());
        }

        Object fullAddress = findValue(addressObj, "commonPresAddrFullAddress");
        if (fullAddress != null) {
            dto.setFullAddress(fullAddress.toString());
        }

        Object phone = findValue(addressObj, "phone");
        if (phone != null) {
            dto.setPhone(phone.toString());
        }

        Object url = findValue(addressObj, "url");
        if (url != null) {
            dto.setUrl(url.toString());

        }

        Object mail = findValue(addressObj, "mail");
        if (mail != null) {
            dto.setMail(mail.toString());
        }

        return dto;
    }
}
