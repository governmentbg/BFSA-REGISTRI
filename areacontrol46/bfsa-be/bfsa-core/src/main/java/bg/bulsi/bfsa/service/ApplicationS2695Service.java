package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS2695DTO;
import bg.bulsi.bfsa.dto.ApplicationS2695FieldDTO;
import bg.bulsi.bfsa.dto.ApplicationS2695PppDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS2695;
import bg.bulsi.bfsa.model.ApplicationS2695Field;
import bg.bulsi.bfsa.model.ApplicationS2695Ppp;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.ContractorPaperI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.RecordRepository;
import bg.bulsi.bfsa.util.Constants;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS2695Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordRepository recordRepository;
    private final NomenclatureService nomenclatureService;
    private final SettlementService settlementService;
    private final RecordService recordService;
    private final MailService mailService;

    private final ServiceType SERVICE_TYPE = ServiceType.S2695;
    public static final String CLASSIFIER_REGISTER_0002036_CODE = Constants.CLASSIFIER_REGISTER_0002036_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS2695DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS2695DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS2695DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        Classifier register = classifierService.findByCode(CLASSIFIER_REGISTER_0002036_CODE);
        List<String> errors = new ArrayList<>();

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

        ApplicationS2695 application = ApplicationS2695.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED).build();
        BeanUtils.copyProperties(dto, application);

        if (StringUtils.hasText(dto.getAerialSprayAgriculturalGroupCode())) {
            application.setAerialSprayAgriculturalGroup(classifierService.findByCode(dto.getAerialSprayAgriculturalGroupCode()));
        }
        if (!CollectionUtils.isEmpty(dto.getSubAgricultures())) {
            for (KeyValueDTO sub : dto.getSubAgricultures()) {
                application.getSubAgricultures().add(classifierService.findByCode(sub.getCode()));
            }
        }
        if (StringUtils.hasText(dto.getPhenophaseCultureCode())) {
            application.setPhenophaseCulture(nomenclatureService.findByCode(dto.getPhenophaseCultureCode()));
        }

        if (!CollectionUtils.isEmpty(dto.getPlantProtectionProducts())) {
            for (ApplicationS2695PppDTO pppDTO : dto.getPlantProtectionProducts()) {
                ApplicationS2695Ppp ppp = ApplicationS2695PppDTO.to(pppDTO);

                if (StringUtils.hasText(pppDTO.getPppUnitCode())) {
                    ppp.setPppUnit(nomenclatureService.findByCode(pppDTO.getPppUnitCode()));
                }
                if (StringUtils.hasText(pppDTO.getPppFunctionCode())) {
                    ppp.setPppFunction(nomenclatureService.findByCode(pppDTO.getPppFunctionCode()));
                }
                application.getPlantProtectionProducts().add(ppp);
            }
        }

        if (dto.getField() != null) {
            ApplicationS2695Field field = ApplicationS2695FieldDTO.of(dto.getField());
            if (dto.getField().getTreatmentAddress() != null) {
                Address address = AddressDTO.to(dto.getField().getTreatmentAddress());
                address.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_TREATMENT_ADDRESS_CODE));
                address.setServiceType(SERVICE_TYPE);
                address.setSettlement(settlementService.findByCode(dto.getField().getTreatmentAddress().getSettlementCode()));
                field.setTreatmentAddress(address);
            }
            application.setApplicationS2695Field(field);
        }

        if (dto.getCh83CertifiedPerson() != null) {
            application.setCh83CertifiedPerson(dto.getCh83CertifiedPerson());
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS2695(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordRepository.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final Language language) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }
        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS2695 application = record.getApplicationS2695();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2695.class, "null");
        }

        Address treatmentAddress = application.getApplicationS2695Field().getTreatmentAddress();
        if (treatmentAddress != null) {
            treatmentAddress.setEnabled(true);
            applicant.getAddresses().add(treatmentAddress);
        }

        ContractorPaper paper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .regDate(LocalDate.now())
                .record(record)
                .validUntilDate(application.getAerialSprayEndDate().toLocalDate())
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .build();

        paper.getI18ns().add(
                new ContractorPaperI18n("ЗАЯВЛЕНИЕ за издаване на разрешение за прилагане на продукти" +
                        "за растителна защита чрез въздушно пръскане, съгласно чл. 110 от Закона за защита на растенията",
                        null,
                        paper,
                        language)
        );

        baseApplicationService.generateRegNumber(paper, record.getBranch().getSequenceNumber());

        applicant.getContractorPapers().add(paper);
        record.setContractorPaper(paper);

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);
        return recordService.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS2695().setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);
        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        mailService.sendApplicationCorrectionNotification(record, message, language);
        return recordRepository.save(record);
    }
}