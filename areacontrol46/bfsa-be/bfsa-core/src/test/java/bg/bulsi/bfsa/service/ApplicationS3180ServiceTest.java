package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS3180DTO;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.Settlement;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.ClassifierRepository;
import bg.bulsi.bfsa.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Slf4j
class ApplicationS3180ServiceTest extends BaseServiceTest {

    @Autowired
    private ApplicationS3180Service service;
    @Autowired
    private NomenclatureBuilder nomenclatureBuilder;
    @Autowired
    private ClassifierBuilder classifierBuilder;
    @Autowired
    private BranchBuilder branchBuilder;
    @Autowired
    private SettlementBuilder settlementBuilder;
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

    private Record record;
    private Contractor contractor;

    @BeforeAll
    public void init() {
        if (record == null) {
            User assignee = recordBuilder.saveUser();

            Classifier register = classifierRepository.findByCode(Constants.CLASSIFIER_REGISTER_0002004_CODE).orElse(null);
            if (register == null) {
                register = classifierRepository.save(Classifier.builder().code(Constants.CLASSIFIER_REGISTER_0002004_CODE).build());
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
                    ServiceType.S3180);
        }
    }

    @Test
    void givenDto_whenRegister_thenReturnSavedRecord() {
        ApplicationS3180DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);
    }

    @Test
    void givenDto_whenApprove_thenReturnRecord() {
        ApplicationS3180DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);

        Record approved = service.approve(saved.getId(), langBG);
        Assertions.assertNotNull(approved);
        Assertions.assertEquals(RecordStatus.FINAL_APPROVED, approved.getStatus());
    }

    @Test
    void givenDto_whenRefuse_thenReturnRecord() {
        ApplicationS3180DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);

        Record approved = service.refuse(saved.getId());
        Assertions.assertNotNull(approved);
        Assertions.assertEquals(RecordStatus.FINAL_REJECTED, approved.getStatus());
    }

    @Test
    void givenDto_whenDirectRegistration_thenReturnRecord() {
        ApplicationS3180DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);

        Record registered = service.directRegistration(saved.getId(), langBG);
        Assertions.assertNotNull(registered);
        Assertions.assertEquals(RecordStatus.FINAL_APPROVED, registered.getStatus());
    }

    @Test
    void givenRecordIdS3180Dto_whenActivityDescription_thenReturnRecord() {
        ApplicationS3180DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);

        ApplicationS3180DTO dto1 = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());
        dto1.setRecordId(saved.getId());

        Record edited = service.activityDescription(saved.getId(), dto1, langBG);
        Assertions.assertNotNull(edited);
    }

    private ApplicationS3180DTO createDto() {
        Settlement settlement = settlementBuilder.saveSettlement();
        Address address = addressBuilder.saveAddress();
        Nomenclature type = nomenclatureBuilder.saveNomenclature(langBG);
        Branch branch = branchBuilder.saveBranch(langBG);
        FacilityDTO facilityDTO = new FacilityDTO();
        AddressDTO addressDTO = new AddressDTO();
        facilityDTO.setName(UUID.randomUUID().toString());
        addressDTO.setAddress(address.getAddress());
        addressDTO.setPostCode(UUID.randomUUID().toString().substring(0,6));
        addressDTO.setPhone(UUID.randomUUID().toString().substring(0,9));
        addressDTO.setSettlementCode(settlement.getCode());
        facilityDTO.setAddress(addressDTO);

        ApplicationS3180DTO dto = new ApplicationS3180DTO();
        dto.setFacility(facilityDTO);
        dto.setRequestorIdentifier(UUID.randomUUID().toString());
        dto.setRequestorEmail(UUID.randomUUID().toString());
        dto.setRequestorFullName(UUID.randomUUID().toString());
        dto.setRequestorAuthorTypeCode(type.getCode());
        dto.setBranchIdentifier(branch.getIdentifier());
        dto.setEntityType(EntityType.PHYSICAL);
        dto.setAddress(AddressDTO.of(address, langBG));

        return dto;
    }
}