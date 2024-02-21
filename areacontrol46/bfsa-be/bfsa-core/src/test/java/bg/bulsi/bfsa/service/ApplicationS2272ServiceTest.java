package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS2272DTO;
import bg.bulsi.bfsa.dto.ApproveFoodTypesDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.ClassifierRepository;
import bg.bulsi.bfsa.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Slf4j
class ApplicationS2272ServiceTest extends BaseServiceTest {

    @Autowired
    private ApplicationS2272Service service;
    @Autowired
    private NomenclatureBuilder nomenclatureBuilder;
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
    @MockBean
    private MailService mailService;

    private Record record;
    private Contractor contractor;

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
        }
    }

    @Test
    void givenDto_whenRegister_thenReturnSavedRecord() {
        ApplicationS2272DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getApplicationS2272());
        Assertions.assertNotNull(saved.getApplicationS2272().getApplicationS2272Vehicles());
    }

    @Test
    void givenDto_whenApprove_thenReturnRecord() {
        String approvedFoodTypeCode = "0001002";
        String declinedFoodTypeCode = "0001003";
        Set<KeyValueDTO> foodTypes = Set.of(KeyValueDTO.builder().code(approvedFoodTypeCode).build(),
                        KeyValueDTO.builder().code(declinedFoodTypeCode).build());
        ApplicationS2272DTO dto = createDto(foodTypes);
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getApplicationS2272());

        List<ApproveFoodTypesDTO> approvedFoodTypes = new ArrayList<>();
        for (VehicleDTO vehicleDTO : dto.getVehicles()) {
            vehicleDTO.getFoodTypes().removeIf(ft -> ft.getCode().equals(declinedFoodTypeCode));
            approvedFoodTypes.add(
                    ApproveFoodTypesDTO.builder()
                            .identifier(vehicleDTO.getRegistrationPlate())
                            .foodTypes(vehicleDTO.getFoodTypes()).build());
        }

        Record approved = service.approve(saved.getId(), approvedFoodTypes, langBG);

        // TODO Check that the only approved foodTypes are in the application vehicles

        Assertions.assertNotNull(approved);
        Assertions.assertEquals(RecordStatus.FINAL_APPROVED, approved.getStatus());
    }

    @Test
    void givenDto_whenRefuse_thenReturnRecord() {
        ApplicationS2272DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getApplicationS2272());

        Record approved = service.refuse(saved.getId());
        Assertions.assertNotNull(approved);
        Assertions.assertEquals(RecordStatus.FINAL_REJECTED, approved.getStatus());
    }

    @Test
    void givenIdAndMessage_whenReturnForCorrection_thenReturnRecord() {
        doNothing().when(mailService).sendApplicationCorrectionNotification(any(), any(), any());

        ApplicationS2272DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);
        Assertions.assertNotNull(saved.getApplicationS2272());

        String massage = "Test - Send for correction!";
        Record forCorrection = service.forCorrection(saved.getId(), massage, langBG);
        Assertions.assertNotNull(forCorrection);
        Assertions.assertEquals(RecordStatus.FOR_CORRECTION, forCorrection.getStatus());
    }

    private ApplicationS2272DTO createDto() {
        return createDto(null);
    }

    private ApplicationS2272DTO createDto(Set<KeyValueDTO> foodTypes) {
        Branch branch = branchBuilder.saveBranch(langBG);
        Nomenclature type = nomenclatureBuilder.saveNomenclature(langBG);

        ApplicationS2272DTO dto = new ApplicationS2272DTO();
        dto.setRequestorIdentifier(UUID.randomUUID().toString());
        dto.setRequestorEmail(UUID.randomUUID().toString());
        dto.setRequestorFullName(UUID.randomUUID().toString());
        dto.setRequestorAuthorTypeCode(type.getCode());
        dto.setBranchIdentifier(branch.getIdentifier());
        dto.setVehicles(mockVehicles(type, foodTypes));

        return dto;
    }

    private List<VehicleDTO> mockVehicles(final Nomenclature type, final Set<KeyValueDTO> foodTypes) {
        List<VehicleDTO> vehicles = new ArrayList<>();
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setRegistrationPlate(UUID.randomUUID().toString().substring(0,6));
        vehicle.setVehicleTypeCode(type.getCode());
        vehicle.setVehicleOwnershipTypeCode(type.getCode());
        vehicle.setBrandModel(UUID.randomUUID().toString());
        if (!CollectionUtils.isEmpty(foodTypes)) {
            vehicle.setFoodTypes(foodTypes);
        }
        vehicles.add(vehicle);
        return vehicles;
    }
}