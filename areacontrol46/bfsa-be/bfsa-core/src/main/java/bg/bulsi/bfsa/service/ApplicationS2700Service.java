package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS2700DTO;
import bg.bulsi.bfsa.dto.ApproveDTO;
import bg.bulsi.bfsa.dto.PersonBO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS2700;
import bg.bulsi.bfsa.model.ApplicationS503;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.ApplicationS2700Repository;
import bg.bulsi.bfsa.repository.RecordRepository;
import bg.bulsi.bfsa.util.Constants;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS2700Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final MailService mailService;
    private final ContractorService contractorService;

    private final ApplicationS2700Repository repository;

    private final ServiceType SERVICE_TYPE = ServiceType.S2700;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002039_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS2700DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS2700DTO.ofRecord(recordService.findById(recordId), language);
    }

    @Transactional(readOnly = true)
    public List<ApplicationS2700DTO> findAllByApplicantIdAndStatus(final Long applicantId, final ApplicationStatus status, final Language language) {
        return ApplicationS2700DTO.of(repository.findAllByRecord_Applicant_IdAndStatus(applicantId, status), language);
    }

    @Transactional
    public Record register(final ApplicationS2700DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        List<String> errors = new ArrayList<>();
        Classifier register = classifierService.findByCode(REGISTER_CODE);

        Contractor applicant = baseApplicationService.buildApplicant(
                dto.getApplicantIdentifier(), dto.getEntityType(),
                dto.getApplicantFullName(), dto.getApplicantEmail(),
                dto.getApplicantPhone(), branch,
                dto.getApplicantCorrespondenceAddress(),
                register, errors, SERVICE_TYPE, language);

        Contractor requestor = applicant;

        if (!Constants.REQUESTOR_AUTHOR_TYPE_HOLDER_CODE.equals(dto.getRequestorAuthorTypeCode())
                && !Constants.REQUESTOR_AUTHOR_TYPE_HOLDER_EXTERNAL_CODE.equals(dto.getRequestorAuthorTypeExternalCode())
                && !dto.getApplicantIdentifier().equals(dto.getRequestorIdentifier())) {
            requestor = baseApplicationService.buildRequestor(
                    dto.getRequestorIdentifier(), dto.getRequestorFullName(),
                    dto.getRequestorEmail(), dto.getRequestorPhone(),
                    dto.getRequestorCorrespondenceAddress(), errors, SERVICE_TYPE, language);
        }

        ApplicationS2700 application = ApplicationS2700.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED).build();

        if (!CollectionUtils.isEmpty(dto.getCh83CertifiedPersons())) {
            for (PersonBO p : dto.getCh83CertifiedPersons()) {
                baseApplicationService.checkPerson(p, requestor, applicant, errors);
                application.getCh83CertifiedPersons().add(p);
            }
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS2700(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordRepository.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final ApproveDTO dto) {
        Record record = recordService.findById(recordId);

        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }

        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS2700 application = record.getApplicationS2700();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS503.class, "null");
        }

        if (CollectionUtils.isEmpty(application.getCh83CertifiedPersons())) {
            throw new RuntimeException("There are no Ch. 83 certified persons");
        }

        for (PersonBO personBO : application.getCh83CertifiedPersons()) {
            if (baseApplicationService.hasNoPaper(personBO.getIdentifier(), ServiceType.S2701)) {
                throw new RuntimeException("Person with identifier: " + personBO.getIdentifier() + " doesn't have " +
                        ServiceType.S2701 + " certificate.");
            }
        }

        Classifier register = classifierService.findByCode(REGISTER_CODE);
        List<PersonBO> personBOS = record.getApplicationS2700().getCh83CertifiedPersons();

        for (PersonBO personBO : personBOS) {
            Contractor person = contractorService.findByIdentifierOrNull(personBO.getIdentifier());
            if (person != null) {
                if (person.getRegisters().stream().noneMatch(r -> r.getCode().equals(REGISTER_CODE))) {
                    person.getRegisters().add(register);
                }
            }
        }

        application.setOrderNumber(dto.getOrderNumber());
        application.setOrderDate(dto.getOrderDate());

//        TODO: Check should create some sort of paper?
        application.setStatus(ApplicationStatus.APPROVED);

        record.setStatus(RecordStatus.FINAL_APPROVED);
        return recordRepository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS2700().setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);
        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        record.getApplicationS2700().setStatus(ApplicationStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);

        return recordRepository.save(record);
    }
}


