package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS3181DTO;
import bg.bulsi.bfsa.dto.ForeignFacilityAddressDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.ClassifierRepository;
import bg.bulsi.bfsa.repository.CountryRepository;
import bg.bulsi.bfsa.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Slf4j
class ApplicationS3181ServiceTest extends BaseServiceTest {

    @Autowired
    private ApplicationS3181Service service;
    @Autowired
    private NomenclatureBuilder nomenclatureBuilder;
    @Autowired
    private ClassifierBuilder classifierBuilder;
    @Autowired
    private BranchBuilder branchBuilder;
    @Autowired
    private AddressBuilder addressBuilder;
    @Autowired
    private RecordBuilder recordBuilder;
    @Autowired
    private ContractorBuilder contractorBuilder;
    @Autowired
    private NomenclatureService nomenclatureService;
    @Autowired
    private ClassifierRepository classifierRepository;
    @Autowired
    private CountryRepository countryRepository;
    @MockBean
    private MailService mailService;

    private Record record;
    private Contractor contractor;
    private Country country;

    @BeforeAll
    public void init() {
        if (record == null) {
            User assignee = recordBuilder.saveUser();

            Classifier register = classifierRepository.findByCode(Constants.CLASSIFIER_REGISTER_0002019_CODE).orElse(null);
            if (register == null) {
                register = classifierRepository.save(Classifier.builder().code(Constants.CLASSIFIER_REGISTER_0002019_CODE).build());
            }

            contractor = contractorBuilder.mockContractor(
                    addressBuilder.mockAddress(
                            nomenclatureService.findByCode(Constants.ADDRESS_TYPE_HEAD_OFFICE_CODE)),
                    branchBuilder.saveBranch(langBG),
                    register);

            record = recordBuilder.saveRecord(
                    assignee,
                    RecordStatus.ENTERED,
                    contractor,
                    assignee.getBranch(),
                    ServiceType.S3181);

            country = countryRepository.findByCode("AD").orElse(null);
            if (country == null) {
                country = countryRepository.save(Country.builder().code("AD").build());
            }
        }
    }

    @Test
    void givenDto_whenRegister_thenReturnSavedRecord() {
        ApplicationS3181DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getApplicationS3181());
    }

    @Test
    void givenDto_whenApprove_thenReturnRecord() {
        ApplicationS3181DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getApplicationS3181());

        Record approved = service.approve(saved.getId());
        Assertions.assertNotNull(approved);
        Assertions.assertEquals(RecordStatus.FINAL_APPROVED, approved.getStatus());
    }

    @Test
    void givenDto_whenRefuse_thenReturnRecord() {
        ApplicationS3181DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getApplicationS3181());

        Record approved = service.refuse(saved.getId());
        Assertions.assertNotNull(approved);
        Assertions.assertEquals(RecordStatus.FINAL_REJECTED, approved.getStatus());
    }

    @Test
    void givenIdAndMessage_whenReturnForCorrection_thenReturnRecord() {
        doNothing().when(mailService).sendApplicationCorrectionNotification(any(), any(), any());

        ApplicationS3181DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getApplicationS3181());

        String massage = "Test - Send for correction!";
        Record forCorrection = service.forCorrection(saved.getId(), massage, langBG);
        Assertions.assertNotNull(forCorrection);
        Assertions.assertEquals(RecordStatus.FOR_CORRECTION, forCorrection.getStatus());
    }

    private ApplicationS3181DTO createDto() {
        Address address = addressBuilder.saveAddress();
        Branch branch = branchBuilder.saveBranch(langBG);
        Nomenclature type = nomenclatureBuilder.saveNomenclature(langBG);

        ForeignFacilityAddressDTO foreignFacilityAddressDTO = new ForeignFacilityAddressDTO();
        foreignFacilityAddressDTO.setCountryCode(country.getCode());
        foreignFacilityAddressDTO.setAddress(UUID.randomUUID().toString().substring(0,9));

        List<ForeignFacilityAddressDTO> facilityAddressDTOS = new ArrayList<>();
        facilityAddressDTOS.add(foreignFacilityAddressDTO);

        ApplicationS3181DTO dto = new ApplicationS3181DTO();

        dto.setRequestorIdentifier(UUID.randomUUID().toString());
        dto.setRequestorEmail(UUID.randomUUID().toString());
        dto.setRequestorFullName(UUID.randomUUID().toString());
        dto.setRequestorAuthorTypeCode(type.getCode());
        dto.setAddress(AddressDTO.of(address, langBG));
        dto.setForeignFacilityAddresses(facilityAddressDTOS);
        dto.setBranchIdentifier(branch.getIdentifier());
        dto.setVehicles(mockVehicles(type));

        return dto;
    }

    private List<VehicleDTO> mockVehicles(Nomenclature type) {
        List<VehicleDTO> vehicles = new ArrayList<>();
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setRegistrationPlate(UUID.randomUUID().toString().substring(0,6));
        vehicleDTO.setVehicleTypeCode(type.getCode());
        vehicleDTO.setVehicleOwnershipTypeCode(type.getCode());
        vehicleDTO.setBrandModel(UUID.randomUUID().toString());
        vehicles.add(vehicleDTO);
        return vehicles;
    }
}