package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS2701DTO;
import bg.bulsi.bfsa.dto.NomenclatureDTO;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
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
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Slf4j
class ApplicationS27010ServiceTest extends BaseServiceTest {

    @Autowired
    private ApplicationS2701Service service;
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
    private SettlementBuilder settlementBuilder;
    @MockBean
    private MailService mailService;

    private Record record;
    private Contractor contractor;

    @BeforeAll
    public void init() {
        if (record == null) {
            User assignee = recordBuilder.saveUser();

            Classifier register = classifierRepository.findByCode(Constants.CLASSIFIER_REGISTER_0002041_CODE).orElse(null);
            if (register == null) {
                register = classifierRepository.save(Classifier.builder().code(Constants.CLASSIFIER_REGISTER_0002041_CODE).build());
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
                    ServiceType.S2701);
        }
    }

    @Test
    void givenDto_whenRegister_thenReturnSavedRecord() {
        ApplicationS2701DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);
    }

    @Test
    void givenDto_whenApprove_thenReturnRecord() {
        ApplicationS2701DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);

        Record approved = service.approve(saved.getId(), langBG);
        Assertions.assertNotNull(approved);
        Assertions.assertEquals(RecordStatus.FINAL_APPROVED, approved.getStatus());
    }

    @Test
    void givenDto_whenRefuse_thenReturnRecord() {
        ApplicationS2701DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);

        Record approved = service.refuse(saved.getId());
        Assertions.assertNotNull(approved);
        Assertions.assertEquals(RecordStatus.FINAL_REJECTED, approved.getStatus());
    }

    @Test
    void givenIdAndMessage_whenForCorrection_thenReturnRecord() {
        doNothing().when(mailService).sendApplicationCorrectionNotification(any(), any(), any());

        ApplicationS2701DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);

        String massage = "Test - Send for correction!";
        Record forCorrection = service.forCorrection(saved.getId(), massage, langBG);
        Assertions.assertNotNull(forCorrection);
        Assertions.assertEquals(RecordStatus.FOR_CORRECTION, forCorrection.getStatus());
    }

    @Test
    void givenRecordIdS3180Dto_whenEducation_thenReturnRecord() {
        ApplicationS2701DTO dto = createDto();
        dto.setApplicantIdentifier(contractor.getIdentifier());

        Record saved = service.register(dto, null, langBG);
        Assertions.assertNotNull(saved);

        ApplicationS2701DTO dto1 = createDto();
        dto1.setApplicantIdentifier(contractor.getIdentifier());
        dto1.setRecordId(saved.getId());

        Record edited = service.education(saved.getId(), dto1, langBG);
        Assertions.assertNotNull(edited);
        Assertions.assertEquals(RecordStatus.PROCESSING, edited.getStatus());
    }

    private ApplicationS2701DTO createDto() {
        NomenclatureDTO correspondenceAddressType = new NomenclatureDTO();
        correspondenceAddressType.setCode(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE);
        Settlement s = settlementBuilder.saveSettlement(UUID.randomUUID().toString());

        Nomenclature type = nomenclatureBuilder.saveNomenclature(langBG);
        Branch branch = branchBuilder.saveBranch(langBG);

        ApplicationS2701DTO dto = new ApplicationS2701DTO();
        dto.setRequestorIdentifier(UUID.randomUUID().toString());
        dto.setRequestorEmail(UUID.randomUUID().toString());
        dto.setRequestorFullName(UUID.randomUUID().toString());
        dto.setRequestorAuthorTypeCode(type.getCode());
        dto.setApplicantCorrespondenceAddress(AddressDTO.builder().settlementCode(s.getCode()).addressTypeCode(correspondenceAddressType.getCode()).build());
        dto.setEducationalDocumentNumber(UUID.randomUUID().toString());
        dto.setBranchIdentifier(branch.getIdentifier());
        return dto;
    }
}