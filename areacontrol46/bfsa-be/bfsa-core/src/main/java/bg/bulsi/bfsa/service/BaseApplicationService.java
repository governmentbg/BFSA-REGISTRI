package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.BaseApplicationDTO;
import bg.bulsi.bfsa.dto.ContractorDTO;
import bg.bulsi.bfsa.dto.FishingVesselDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.PersonBO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.enums.ContractorType;
import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.BaseApprovalDocument;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.FacilityPaper;
import bg.bulsi.bfsa.model.FishingVessel;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.Tariff;
import bg.bulsi.bfsa.model.Vehicle;
import bg.bulsi.bfsa.model.VehicleI18n;
import bg.bulsi.bfsa.repository.GeneratorSequenceRepository;
import bg.bulsi.bfsa.repository.TariffRepository;
import bg.bulsi.bfsa.util.Constants;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BaseApplicationService {

    private final ContractorService contractorService;
    private final NomenclatureService nomenclatureService;
    private final SettlementService settlementService;
    private final VehicleService vehicleService;
    private final ClassifierService classifierService;
    private final GeneratorSequenceRepository generatorSequenceRepository;
    private final TariffRepository tariffRepository;
    private final FishingVesselService fishingVesselService;

    public Contractor buildRequestor(final String identifier, final String fullName, final String email,
                                     final String phone, final AddressDTO correspondenceAddress,
                                     final List<String> errors, final ServiceType serviceType, final Language language) {

        return buildContractor(identifier, EntityType.PHYSICAL, fullName, email, phone, null,
                correspondenceAddress, errors, language, ContractorType.REQUESTOR, serviceType);
    }

    public Contractor buildApplicant(final String identifier, final EntityType type,
                                     final String fullName, final String email, final String phone,
                                     final Branch branch, final AddressDTO correspondenceAddress,
                                     final Classifier register, final List<String> errors,
                                     final ServiceType serviceType, final Language language) {

        Contractor applicant = buildContractor(identifier, type, fullName,email, phone, branch,
                correspondenceAddress, errors, language, ContractorType.APPLICANT, serviceType);

        if (register != null) {
            if (applicant.getRegisters().stream().noneMatch(c -> register.getCode().equals(c.getCode()))) {
                applicant.getRegisters().add(register);
            }
        }

        return applicant;
    }

    private Contractor buildContractor(final String identifier, final EntityType type,
                                       final String fullName, final String email, final String phone,
                                       final Branch branch, final AddressDTO correspondenceAddress,
                                       final List<String> errors, final Language language,
                                       final ContractorType contractorType, final ServiceType serviceType) {

        Contractor contractor = contractorService.findByIdentifierOrNull(identifier);

        if (contractor == null) {
            contractor = Contractor.builder()
                    .identifier(identifier)
                    .branch(branch)
                    .enabled(true)
                    .build();
        }

        String errMessagePrefix = contractorType.name().substring(0, 1).toUpperCase() +
                contractorType.name().substring(1).toLowerCase() +
                " with identifier: " + contractor.getIdentifier();

        if (contractor.getEntityType() == null && type != null) {
            contractor.setEntityType(type);
        } else if (type != null && !type.equals(contractor.getEntityType())) {
            errors.add(errMessagePrefix + " has type: " + contractor.getEntityType() +
                    " which is different than the type (" + type + ") received from application");
        }
        if (!StringUtils.hasText(contractor.getFullName()) && StringUtils.hasText(fullName)) {
            contractor.setFullName(fullName);
        } else if (StringUtils.hasText(fullName) && !fullName.equals(contractor.getFullName())) {
            errors.add(errMessagePrefix + " has name: " + contractor.getFullName() +
                    " which is different than the name (" + fullName + ") received from application");
        }
        if (!StringUtils.hasText(contractor.getEmail()) && StringUtils.hasText(email)) {
            contractor.setEmail(email);
        } else if (StringUtils.hasText(email) && !email.equals(contractor.getEmail())) {
            errors.add(errMessagePrefix + " has email: " + contractor.getEmail() +
                    " which is different than the email (" + email + ") received from application");
        }
        if (!StringUtils.hasText(contractor.getPhone()) && StringUtils.hasText(phone)) {
            contractor.setPhone(phone);
        } else if (StringUtils.hasText(phone) && !phone.equals(contractor.getPhone())) {
            errors.add(errMessagePrefix + " has phone: " + contractor.getPhone() +
                    " which is different than the phone (" + phone + ") received from application");
        }
        if (contractor.getBranch() == null && branch != null) {
            contractor.setBranch(branch);
        }/* else if (branch != null && !branch.equals(contractor.getBranch())) {
            errors.add(errMessagePrefix + " has branch with identifier: " + contractor.getBranch().getIdentifier() +
                    " which is different than the branch with identifier (" + branch.getIdentifier() +
                    ") received from application");
        }*/

        processCorrespondentAddress(correspondenceAddress, contractor, errors, serviceType, language);

        return contractor;
    }

    private void processCorrespondentAddress(AddressDTO dto, Contractor contractor, final List<String> errors,
                                             final ServiceType serviceType, Language language) {
        if (dto != null) {
            Address address = contractor.getAddresses().stream()
                    .filter(a -> Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE.equals(a.getAddressType().getCode()))
                    .findAny().orElse(null);

            if (address == null) {
                address = new Address();
                address.setServiceType(serviceType);
                BeanUtils.copyProperties(dto, address);

                address.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE));
                address.setSettlement(settlementService.findByCode(dto.getSettlementCode()));
                address.setFullAddress(StringUtils.hasText(dto.getFullAddress())
                        ? dto.getFullAddress()
                        : settlementService.getInfo(dto.getSettlementCode(), language.getLanguageId())
                );

                contractor.getAddresses().add(address);
            } else {
                if (StringUtils.hasText(dto.getSettlementCode())) {
                    if (address.getSettlement() == null) {
                        address.setSettlement(settlementService.findByCode(dto.getSettlementCode()));
                    } else if (!dto.getSettlementCode().equals(address.getSettlement().getCode())) {
                        errors.add("Correspondence address has settlementCode: " + address.getSettlement().getCode() +
                                " which is different than the settlementCode (" + dto.getSettlementCode() +
                                ") received from application");
                    }
                }
                if (StringUtils.hasText(dto.getAddress())) {
                    if (!StringUtils.hasText(address.getAddress())) {
                        address.setAddress(dto.getAddress());
                    } else if (!dto.getAddress().equals(address.getAddress())) {
                        errors.add("Correspondence address has address: " + address.getAddress() +
                                " which is different than the address (" + dto.getAddress() +
                                ") received from application");
                    }
                }
                if (StringUtils.hasText(dto.getAddressLat())) {
                    if (!StringUtils.hasText(address.getAddressLat())) {
                        address.setAddressLat(dto.getAddressLat());
                    } else if (!dto.getAddressLat().equals(address.getAddressLat())) {
                        errors.add("Correspondence addressLat: " + address.getAddressLat() +
                                " is different than the addressLat (" + dto.getAddressLat() +
                                ") received from application");
                    }
                }
                if (StringUtils.hasText(dto.getPostCode())) {
                    if (!StringUtils.hasText(address.getPostCode())) {
                        address.setPostCode(dto.getPostCode());
                    } else if (!dto.getAddress().equals(address.getAddress())) {
                        errors.add("Correspondence address has postCode: " + address.getPostCode() +
                                " which is different than the postCode (" + dto.getPostCode() +
                                ") received from application");
                    }
                }
                if (StringUtils.hasText(dto.getMail())) {
                    if (!StringUtils.hasText(address.getMail())) {
                        address.setMail(dto.getMail());
                    } else if (!dto.getMail().equals(address.getMail())) {
                        errors.add("Correspondence address has mail: " + address.getMail() +
                                " which is different than the mail (" + dto.getMail() +
                                ") received from application");
                    }
                }
                if (StringUtils.hasText(dto.getUrl())) {
                    if (!StringUtils.hasText(address.getUrl())) {
                        address.setUrl(dto.getUrl());
                    } else if (!dto.getUrl().equals(address.getUrl())) {
                        errors.add("Correspondence address has url: " + address.getUrl() +
                                " which is different than the url (" + dto.getUrl() +
                                ") received from application");
                    }
                }
                if (StringUtils.hasText(dto.getPhone())) {
                    if (!StringUtils.hasText(address.getPhone())) {
                        address.setPhone(dto.getPhone());
                    } else if (!dto.getPhone().equals(address.getPhone())) {
                        errors.add("Correspondence address has phone: " + address.getPhone() +
                                " which is different than the phone (" + dto.getPhone() +
                                ") received from application");
                    }
                }
            }
            address.setEnabled(true);
        }
    }

    protected Record buildRecord(final ServiceType serviceType, final Contractor requestor, final Contractor applicant,
                                 final Branch branch, final BaseApplicationDTO dto) {
        return Record.builder()
//                .requestDateTime(dto.getRequestDateTime())
                .entryNumber(dto.getEntryNumber())
                .entryDate(dto.getEntryDate())
                .serviceType(serviceType)
                .branch(branch)
                .price(tariffRepository.findByServiceType(serviceType)
                        .orElseThrow(() -> new EntityNotFoundException(Tariff.class, serviceType))
                        .getPrice())
                .requestor(requestor)
                .requestorAuthorType(
                        StringUtils.hasText(dto.getRequestorAuthorTypeCode())
                                ? nomenclatureService.findByCode(dto.getRequestorAuthorTypeCode())
                                : nomenclatureService.findByExternalCode(dto.getRequestorAuthorTypeExternalCode())
                )
                .requestorPowerAttorneyNumber(dto.getRequestorPowerAttorneyNumber())
                .requestorPowerAttorneyNotary(dto.getRequestorPowerAttorneyNotary())
                .requestorPowerAttorneyDate(dto.getRequestorPowerAttorneyDate())
                .requestorPowerAttorneyUntilDate(dto.getRequestorPowerAttorneyUntilDate())
                .applicant(applicant)
                .status(RecordStatus.ENTERED)
                .build();
    }

    protected VehicleDTO getCh50Vehicle(final String number, final List<String> errors, final Language language) {
        Vehicle ch50Vehicle = vehicleService.findByCertificateNumberOrNull(number, language);
        if (ch50Vehicle == null) {
            errors.add("Vehicle with certificate number: " + number + " doesn't exist.");
            return VehicleDTO.builder().certificateNumber(number).build();
        } else {
            return VehicleDTO.of(ch50Vehicle, language);
        }
    }

    protected List<VehicleDTO> getCh50Vehicles(final List<String> numbers, final List<String> errors, final Language language) {
        List<VehicleDTO> vehicleDTOS = new ArrayList<>();
        for (String number : numbers) {
            vehicleDTOS.add(getCh50Vehicle(number, errors, language));
        }
        return vehicleDTOS;
    }

    protected Vehicle buildVehicle(final VehicleDTO dto, final Classifier register, final Branch branch,
                                   final List<String> errors, final Language language) {
        Vehicle vehicle = null;
        if (!StringUtils.hasText(dto.getRegistrationPlate())) {
            errors.add("Can't register vehicle without registration plate");
        } else { //--- Използвам МПС за транспортиране на храни по чл. 50 ---
            vehicle = vehicleService.findByRegistrationPlateOrNull(dto.getRegistrationPlate(), language);
            if (vehicle != null) {
                if (StringUtils.hasText(dto.getBrandModel())/* && !StringUtils.hasText(vehicle.getI18n(language).getName())*/) {
                    String brandModel = vehicle.getI18n(language).getBrandModel();
                    if (!StringUtils.hasText(brandModel)) {
                        vehicle.getI18n(language).setBrandModel(dto.getBrandModel());
                    } else if (!dto.getBrandModel().equals(brandModel)) {
                        errors.add("Vehicle with registration plate: " + vehicle.getRegistrationPlate() +
                                " has brand/model: " + brandModel + " which is different than the name (" +
                                dto.getBrandModel() + ") received from application");
                    }
                }
                if (StringUtils.hasText(dto.getVehicleTypeCode())/* && vehicle.getVehicleType() == null*/) {
                    if (vehicle.getVehicleType() == null) {
                        vehicle.setVehicleType(nomenclatureService.findByCode(dto.getVehicleTypeCode()));
                    } else if (!dto.getVehicleTypeCode().equals(vehicle.getVehicleType().getCode())) {
                        errors.add("Vehicle with registration plate: " + vehicle.getRegistrationPlate() +
                                " has type: " + vehicle.getVehicleType().getI18n(language).getName() +
                                " with code: " + vehicle.getVehicleType().getCode() +
                                " which is different than the type (" + dto.getVehicleTypeName() +
                                ") received from application");
                    }
                }
                if (StringUtils.hasText(dto.getDescription())) {
                    String desc = !StringUtils.hasText(vehicle.getI18n(language).getDescription())
                            ? dto.getDescription()
                            : vehicle.getI18n(language).getDescription() + ", " + dto.getDescription();
                    vehicle.getI18n(language).setDescription(desc);
                }
                if (vehicle.getRegisters().stream().noneMatch(r -> register.getCode().equals(r.getCode()))) {
                    vehicle.getRegisters().add(register);
                }

                if (!CollectionUtils.isEmpty(dto.getFoodTypes())) {
                    for (KeyValueDTO foodType : dto.getFoodTypes()) {
                        if (vehicle.getFoodTypes().stream().noneMatch(ft -> foodType.getCode().equals(ft.getCode()))) {
                            vehicle.getFoodTypes().add(classifierService.findByCode(foodType.getCode()));
                        }
                    }
                }
            } else {
                Double volume = dto.getVolume();
                Double load = dto.getLoad();
                if (volume != null && Constants.MEASURING_UNITS_CUBIC_METER_CODE.equals(dto.getVolumeUnitCode())) {
                    volume = volume * 1000;
                }
                if (load != null && Constants.MEASURING_UNITS_TON_CODE.equals(dto.getLoadUnitCode())) {
                    load = load * 1000;
                }

                vehicle = Vehicle.builder()
                        .registrationPlate(dto.getRegistrationPlate())
                        .branch(branch)
                        .entryDate(LocalDate.now())
                        .vehicleType(nomenclatureService.findByCode(dto.getVehicleTypeCode()))
                        .load(load)
                        .volume(volume)
                        .licenseNumber(dto.getLicenseNumber())
                        .build();
                vehicle.getRegisters().add(register);
                vehicle.getI18n(language).setDescription(dto.getDescription());

                if (!CollectionUtils.isEmpty(dto.getFoodTypes())) {
                    for (KeyValueDTO foodType : dto.getFoodTypes()) {
                        vehicle.getFoodTypes().add(classifierService.findByCode(foodType.getCode()));
                    }
                }

                if (StringUtils.hasText(dto.getBrandModel()) || StringUtils.hasText(dto.getDescription())) {
                    VehicleI18n i18n = vehicle.getI18n(language);
                    i18n.setBrandModel(dto.getBrandModel());
                    i18n.setDescription(dto.getDescription());
                }
            }
        }
        return vehicle;
    }

    public void checkPerson(final PersonBO p, final Contractor requestor, final Contractor applicant, final List<String> errors) {
        if (p.getIdentifier().equals(requestor.getIdentifier())) {
            if ((StringUtils.hasText(p.getName()) && !requestor.getFullName().contains(p.getName())) ||
                    (StringUtils.hasText(p.getSurname()) && !requestor.getFullName().contains(p.getSurname())) ||
                    (StringUtils.hasText(p.getFamilyName()) && !requestor.getFullName().contains(p.getFamilyName()))
            ) {
                errors.add("Person " + p.getName() + " " + p.getSurname() + " " + p.getFamilyName()
                        + " with identifier: " + p.getIdentifier()
                        + " has different name from requestor " + requestor.getFullName()
                        + " with same identifier.");
            } else {
                hasPaperCheck(requestor, errors, ServiceType.S2701);
            }
        } else if (p.getIdentifier().equals(applicant.getIdentifier())) {
            if ((StringUtils.hasText(p.getName()) && !requestor.getFullName().contains(p.getName())) ||
                    (StringUtils.hasText(p.getSurname()) && !requestor.getFullName().contains(p.getSurname())) ||
                    (StringUtils.hasText(p.getFamilyName()) && !requestor.getFullName().contains(p.getFamilyName()))
            ) {
                errors.add("Person " + p.getName() + " " + p.getSurname() + " " + p.getFamilyName()
                        + " with identifier: " + p.getIdentifier()
                        + " has different name from applicant " + applicant.getFullName()
                        + " with same identifier.");
            } else {
                hasPaperCheck(applicant, errors, ServiceType.S2701);
            }
        } else {
            hasPaperCheck(p.getIdentifier(), errors, ServiceType.S2701);
        }
    }

    public Contractor buildContractor(ContractorDTO dto, final List<String> errors) {
        if (!StringUtils.hasText(dto.getIdentifier())) {
            throw new RuntimeException("Person Identifier is required");
        }

        String errMessagePrefix = "Contractor with identifier: " + dto.getIdentifier();

        Contractor contractor = contractorService.findByIdentifierOrNull(dto.getIdentifier());

        if (contractor != null) {
            String fullName = dto.assembleFullName();

            if (!StringUtils.hasText(contractor.getFullName()) && StringUtils.hasText(fullName)) {
                contractor.setFullName(fullName);
            } else if (StringUtils.hasText(fullName) && !fullName.equals(contractor.getFullName())) {
                errors.add(errMessagePrefix + " has full name: " + contractor.getFullName() +
                        " which is different than the full name (" + fullName + ") received from application");
            }

            if (!StringUtils.hasText(contractor.getEmail()) && StringUtils.hasText(dto.getEmail())) {
                contractor.setEmail(dto.getEmail());
            } else if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equals(contractor.getEmail())) {
                errors.add(errMessagePrefix + " has email: " + contractor.getEmail() +
                        " which is different than the email (" + dto.getEmail() + ") received from application");
            }

            if (!StringUtils.hasText(contractor.getPhone()) && StringUtils.hasText(dto.getPhone())) {
                contractor.setPhone(dto.getPhone());
            } else if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(contractor.getPhone())) {
                errors.add(errMessagePrefix + " has phone: " + contractor.getPhone() +
                        " which is different than the phone (" + dto.getPhone() + ") received from application");
            }

            if (!StringUtils.hasText(contractor.getDegree()) && StringUtils.hasText(dto.getDegree())) {
                contractor.setDegree(dto.getDegree());
            } else if (StringUtils.hasText(dto.getDegree()) && !dto.getDegree().equals(contractor.getDegree())) {
                errors.add(errMessagePrefix + " has degree: " + contractor.getDegree() +
                        " which is different than the degree (" + dto.getDegree() + ") received from application");
            }
        } else {
            contractor = Contractor.builder()
                    .fullName(dto.assembleFullName())
                    .identifier(dto.getIdentifier())
                    .phone(dto.getPhone())
                    .email(dto.getEmail())
                    .degree(dto.getDegree())
                    .entityType(dto.getEntityType())
                    .enabled(true)
                    .build();
        }

        return contractor;
    }

    protected void hasPaperCheck(final String identifier, final List<String> errors, final ServiceType serviceType) {
        if (!StringUtils.hasText(identifier)) {
            throw new RuntimeException("Identifier is required");
        }

        Contractor person = contractorService.findByIdentifierOrNull(identifier);
        if (person == null) {
            errors.add("Person with identifier: " + identifier + " doesn't exists");
        } else {
            hasPaperCheck(person, errors, serviceType);
        }
    }

    protected void hasPaperCheck(final Contractor person, final List<String> errors, final ServiceType serviceType) {
        if (person == null) {
            throw new RuntimeException("Person is required");
        }

        if (hasNoPaper(person, serviceType)) {
            errors.add("Person person " + person.getFullName() + " with identifier: " + person.getIdentifier()
                    + " doesn't have " + serviceType + " certificate.");
        }
    }

    protected boolean hasNoPaper(final String identifier, final ServiceType serviceType) {
        return !hasPaper(contractorService.findByIdentifier(identifier), serviceType);
    }

    protected boolean hasNoPaper(final Contractor person, final ServiceType serviceType) {
        return !hasPaper(person, serviceType);
    }
    
    protected boolean hasPaper(final Contractor person, final ServiceType serviceType) {
        if (person == null) {
            throw new RuntimeException("Person is required");
        }
        return !CollectionUtils.isEmpty(person.getContractorPapers()) &&
                person.getContractorPapers().stream()
                        .anyMatch(p -> serviceType.equals(p.getServiceType())
                                && p.getEnabled()
                                && (p.getValidUntilDate() == null || LocalDate.now().isBefore(p.getValidUntilDate()))
                        );
    }

    protected void generateRegNumber(final BaseApprovalDocument paper, final String branchSequenceNumber) {
        // TODO: The generation algorithm should be changed
        // При регистъра от Неживотински произход цифрите са с една нула повече и няма символ BG:
        // "BranchSequenceNumber" + facility.getActivityGroup().getId() + "<-- externalNumber instead"

        if (paper instanceof ContractorPaper cp) {
            cp.setRegNumber(generateNumber(branchSequenceNumber));
            cp.setRegDate(LocalDate.now());
        } else if(paper instanceof FacilityPaper fp) {
            fp.getFacility().setRegNumber(generateNumber(branchSequenceNumber));
            fp.getFacility().setRegDate(LocalDate.now());
            fp.setRegNumber(generateNumber(branchSequenceNumber));
            fp.setRegDate(LocalDate.now());
        }
    }

    protected String generateNumber(final String sequenceNumber) {
        return "BG-" + sequenceNumber + "-" + generatorSequenceRepository.getNextVal();
    }

    protected FishingVessel buildFishingVessel(final FishingVesselDTO dto, final Branch branch,
                                               final List<String> errors, final Language language) {
        FishingVessel fishingVessel = null;
        if (!StringUtils.hasText(dto.getRegNumber())) {
            errors.add("Can't register fishing vessel without registration number");
        } else {
            fishingVessel = fishingVesselService.findByRegNumber(dto.getRegNumber());
            if (fishingVessel != null) {
                if (StringUtils.hasText(dto.getExternalMarking())) {
                    String externalMarking = fishingVessel.getExternalMarking();
                    if (!StringUtils.hasText(externalMarking)) {
                        fishingVessel.setExternalMarking(externalMarking);
                    } else if (!dto.getExternalMarking().equals(externalMarking)) {
                        errors.add("Fishing vessel with registration number: " + fishingVessel.getRegNumber() +
                                " has external marking: " + externalMarking + " which is different than the name (" +
                                dto.getExternalMarking() + ") received from application");
                    }
                }
                if (StringUtils.hasText(dto.getAssignmentTypeCode())) {
                    if (fishingVessel.getAssignmentType() == null) {
                        fishingVessel.setAssignmentType(nomenclatureService.findByCode(dto.getAssignmentTypeCode()));
                    } else if (!dto.getAssignmentTypeCode().equals(fishingVessel.getAssignmentType().getCode())) {
                        errors.add("Vehicle with registration number: " + fishingVessel.getRegNumber() +
                                " has assignment type: " + fishingVessel.getAssignmentType().getI18n(language).getName() +
                                " with code: " + fishingVessel.getAssignmentType().getCode() +
                                " which is different than the assignment type (" + dto.getAssignmentTypeCode() +
                                ") received from application");
                    }
                }
                if (StringUtils.hasText(dto.getTypeCode())) {
                    if (fishingVessel.getType() == null) {
                        fishingVessel.setType(nomenclatureService.findByCode(dto.getTypeCode()));
                    } else if (!dto.getTypeCode().equals(fishingVessel.getType().getCode())) {
                        errors.add("Vehicle with registration number: " + fishingVessel.getRegNumber() +
                                " has type: " + fishingVessel.getType().getI18n(language).getName() +
                                " with code: " + fishingVessel.getType().getCode() +
                                " which is different than the type (" + dto.getTypeCode() +
                                ") received from application");
                    }
                }
                if (dto.getHullLength() != null) {
                    if (fishingVessel.getHullLength() == null) {
                        fishingVessel.setHullLength(dto.getHullLength());
                    } else if (!dto.getHullLength().equals(fishingVessel.getHullLength())) {
                        errors.add("Fishing vessel with registration number: " + fishingVessel.getRegNumber() +
                                " has hull length: " + fishingVessel.getHullLength() +
                                " which is different than (" + dto.getHullLength() +
                                ") received from application");
                    }
                }
            } else {
                fishingVessel = FishingVessel.builder()
                        .regNumber(dto.getRegNumber())
                        .branch(branch)
                        .date(LocalDate.now())
                        .assignmentType(nomenclatureService.findByCode(dto.getAssignmentTypeCode()))
                        .type(nomenclatureService.findByCode(dto.getTypeCode()))
                        .externalMarking(dto.getExternalMarking())
                        .hullLength(dto.getHullLength())
                        .build();
            }
        }

        return fishingVessel;
    }
}
