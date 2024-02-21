package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.BaseApplicationDTO;
import bg.bulsi.bfsa.dto.DaeuRegisterPaymentResponseDTO;
import bg.bulsi.bfsa.dto.InspectionDTO;
import bg.bulsi.bfsa.enums.InspectionStatus;
import bg.bulsi.bfsa.enums.InspectionType;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.File;
import bg.bulsi.bfsa.model.Inspection;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.Tariff;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.RecordRepository;
import bg.bulsi.bfsa.repository.TariffRepository;
import bg.bulsi.bfsa.security.RolesConstants;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RecordService {

    private final RecordRepository repository;
    private final MailService mailService;
    private final TariffRepository tariffRepository;
    private final EPaymentService ePaymentService;
    private final UserService userService;
    private final NomenclatureService nomenclatureService;
    private final InspectionService inspectionService;
    private final FileStoreService fileStoreService;

    @Transactional(readOnly = true)
    public Record findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Record.class, id));
    }

    public List<Record> findAllByApplicantId(final Long applicantId) {
        return repository.findAllByApplicant_Id(applicantId);
    }

    public Page<Record> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Record save(final Record record) {
        return repository.save(record);
    }

    @Transactional(readOnly = true)
    public Page<BaseApplicationDTO> findAllByBranchBaseApplicationDTO(final String username, final Long branchId, final Pageable pageable) {
        if (branchId == null || branchId <= 0) {
            throw new RuntimeException("BranchId is required");
        }

        User currentUser = userService.findByUsername(username);
        if (currentUser.getBranch() == null || !branchId.equals(currentUser.getBranch().getId())) {
            throw new RuntimeException("Path variable branchId doesn't match current user branch id");
        }

        if (currentUser.getRoles().stream().anyMatch(role -> RolesConstants.ROLE_FINANCE.equals(role.getName()))) {
            List<RecordStatus> statuses = List.of(RecordStatus.PAYMENT_CONFIRMATION, RecordStatus.PAYMENT_CONFIRMED);
            return repository.findAllByBranchAndStatuses(branchId, statuses, pageable).map(BaseApplicationDTO::of);
        } else if (currentUser.getRoles().stream().anyMatch(role -> RolesConstants.ROLE_ADMIN.equals(role.getName()))) {
            return repository.findAllByOrderByLastModifiedDateDesc(pageable).map(BaseApplicationDTO::of);
        }

        return repository.findAllByBranchAndDirectorate(branchId, currentUser.getDirectorate().getCode(), pageable).map(BaseApplicationDTO::of);
    }

    @Transactional(readOnly = true)
    public Page<BaseApplicationDTO> search(final String username,
                                           final String param,
                                           final RecordStatus RecordStatus,
                                           final LocalDate date,
                                           final Pageable pageable) {

        User currentUser = userService.findByUsername(username);
        if (currentUser.getBranch() == null || currentUser.getBranch().getId() == null ||
                currentUser.getBranch().getId() <= 0) {
            throw new RuntimeException("The current user has no branch");
        }

        return repository.search(currentUser.getBranch().getId(), currentUser.getDirectorate().getCode(),
                        param, RecordStatus, date, pageable).map(BaseApplicationDTO::of);
    }

    public List<File> attachments(final Long id) {
        return fileStoreService.findAllByRecordId(id);
    }

    @Transactional
    public Record sendForPayment(final Long recordId, final BigDecimal recordPrice, final Language language) throws Exception {
        Record record = findById(recordId);

//        TODO: Check record price If price is greater than 0 then send application notification mail.
        Tariff tariff = tariffRepository.findByServiceType(
                record.getServiceType()).orElseThrow(() -> new EntityNotFoundException(Tariff.class, record.getServiceType())
        );

        if (tariff.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("The service type: " + record.getServiceType() + " is free");
        }

        if (tariff.getPrice().compareTo(new BigDecimal(-1)) == 0 && (recordPrice == null || recordPrice.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new RuntimeException("The price is required for service type: " + record.getServiceType());
        }

        record.setPrice(tariff.getPrice().compareTo(BigDecimal.ZERO) > 0 ? tariff.getPrice() : recordPrice);

        // TODO Pass DaeuRegisterPaymentResponseDTO fields to the email template
        DaeuRegisterPaymentResponseDTO responseDTO = ePaymentService.registerEPayment(record); // TODO pass amount to register payment

        mailService.sendApplicationPaymentNotification(record.getPrice(), record, language, responseDTO.getAccessCode());

        record.setStatus(RecordStatus.PAYMENT_CONFIRMATION); // TODO: Добавяне на "За потвърждение на плащане от финансист"

        return repository.save(record);
    }

    @Transactional
    public Record confirmPayment(final Long recordId) {
        Record record = findById(recordId);

        if (!RecordStatus.PAYMENT_CONFIRMATION.equals(record.getStatus())) {
            throw new RuntimeException("Record status have to be " + RecordStatus.PAYMENT_CONFIRMATION + " to confirm payment, but it is "
                    + record.getStatus());
        }

        record.setStatus(RecordStatus.PAYMENT_CONFIRMED);
        return repository.save(record);
    }

    @Transactional
    public InspectionDTO createInspection(final Long id, final InspectionDTO dto, Language language) {
        if (dto == null) {
            throw new RuntimeException("Inspection cannot be null.");
        }
        if (dto.getRecordId() == null || dto.getRecordId() <= 0) {
            throw new RuntimeException("RecordId cannot be empty.");
        }
        if (!id.equals(dto.getRecordId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
        }

        Record record = findById(id);
        if (!record.getStatus().equals(RecordStatus.PROCESSING)) {
            throw new RuntimeException("Record payment is not processing.");
        }
        record.setStatus(RecordStatus.INSPECTION);

        Inspection inspection = InspectionDTO.to(dto, language);
        inspection.setRecord(record);
        inspection.setFacility(record.getFacility());
        inspection.setStatus(InspectionStatus.PROCESSING);
        if (!CollectionUtils.isEmpty(dto.getUsers())) {
            dto.getUsers().forEach(userId -> {
                User u = userService.findById(userId);
                u.getInspections().add(inspection);
                inspection.getUsers().add(u);
            });
        }
        if (ServiceType.S3180.equals(record.getServiceType())) {
            inspection.setType(InspectionType.FOR_APPROVAL);
        }
        record.getInspections().add(inspection);

        return InspectionDTO.of(inspectionService.create(inspection), language);
    }

    @Transactional
    public Record completeInspection(final Long recordId, final InspectionDTO dto, final Language language) {
        if (dto == null) {
            throw new RuntimeException("Inspection cannot be null.");
        }
        if (dto.getRecordId() == null) {
            throw new RuntimeException("Inspection Record cannot be null.");
        }
        if (!recordId.equals(dto.getRecordId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
        }

        Record record = findById(recordId);
        if (!RecordStatus.INSPECTION.equals(record.getStatus())) {
            throw new RuntimeException("Record is not in " + RecordStatus.INSPECTION + " status.");
        }

        Inspection inspection = record.getInspections().stream()
                .filter(i1 -> i1.getId().equals(dto.getId()))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(Inspection.class, dto.getId()));

        if (InspectionStatus.COMPLETED.equals(inspection.getStatus())) {
            throw new RuntimeException("Inspection is already completed.");
        }

        inspection.getI18n(language).setDescription(dto.getDescription());
        inspection.setEndDate(dto.getEndDate());
        inspection.setStatus(InspectionStatus.COMPLETED);
        if (dto.getRiskLevel() != null) {
            inspection.setRiskLevel(dto.getRiskLevel());
        }
        inspection.getReasons().clear();
        if (!CollectionUtils.isEmpty(dto.getReasonsCodes())) {
            dto.getReasonsCodes().forEach(code -> inspection.getReasons().add(nomenclatureService.findByCode(code)));
        }
        inspection.getUsers().clear();
        if (!CollectionUtils.isEmpty(dto.getUsers())) {
            dto.getUsers().forEach(userId -> inspection.getUsers().add(userService.findById(userId)));
        }

        record.setStatus(RecordStatus.INSPECTION_COMPLETED);

        return repository.save(record);
    }
}