package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.BaseApplicationDTO;
import bg.bulsi.bfsa.dto.InspectionDTO;
import bg.bulsi.bfsa.enums.InspectionStatus;
import bg.bulsi.bfsa.enums.InspectionType;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Inspection;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.InspectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Random;

@Slf4j
public class RecordServiceTest extends BaseServiceTest {
    @Autowired
    private RecordService service;
    @Autowired
    private InspectionService inspectionService;
    @Autowired
    private RecordBuilder recordBuilder;
    @Autowired
    private InspectionRepository inspectionRepository;

    @Test
    void whenFindAll_thenReturnAllRecords() {
        Record created = recordBuilder.saveRecord();
        Assertions.assertNotNull(created);

        Page<Record> result = service.findAll(Pageable.unpaged());
        Assertions.assertNotNull(result.getContent());
        Assertions.assertNotEquals(0, result.getContent().size());
    }

    @Test
    void givenRecord_whenFindById_thenReturnRecord() {
        Record created = recordBuilder.saveRecord();
        Assertions.assertNotNull(created);

        Record foundedRecord = service.findById(created.getId());

        Assertions.assertNotNull(foundedRecord);
        Assertions.assertEquals(created.getId(), foundedRecord.getId());
    }

    @Test
    void givenFakeId_whenFindBy_thenReturnEntityNotFoundException() {
        Long fakeId = 0L;
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> service.findById(fakeId)
        );

        Assertions.assertEquals("Entity '"
                        + Record.class.getName() + "' with id/code='"
                        + fakeId + "' not found",
                thrown.getMessage());
    }

//    @Test
    void givenRecord_whenSearchByAssigneeFullName_thenReturnRecord() {
        Record created = recordBuilder.saveRecord();
        Assertions.assertNotNull(created);

        Page<BaseApplicationDTO> records = service.search(
                created.getAssignee().getUsername(),
                created.getAssignee().getFullName(),
                null,
                null,
                Pageable.unpaged());

        Assertions.assertNotNull(records);
    }

    @Test
    void givenRecordWithPaymentConfirmation_whenConfirmPayment_thenReturnRecordWithStatusPaid() {
        Record created = recordBuilder.saveRecord(RecordStatus.PAYMENT_CONFIRMATION);
        Assertions.assertNotNull(created);

        Record recordResponse = service.confirmPayment(created.getId());
        Assertions.assertNotNull(recordResponse);
        Assertions.assertEquals(RecordStatus.PAYMENT_CONFIRMED, recordResponse.getStatus());
    }

    @Test
    void givenRecordWithNoPaymentConfirmation_whenConfirmPayment_thenReturnError() {
        Record created = recordBuilder.saveRecord();
        Assertions.assertNotNull(created);

        RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, () -> service.confirmPayment(created.getId())
        );

        Assertions.assertEquals("Record status have to be " + RecordStatus.PAYMENT_CONFIRMATION + " to confirm payment, but it is "
                + created.getStatus(), thrown.getMessage());
    }

    @Test
    void givenInspectionDTO_whenCompleteInspection_thenReturnCompletedRecordInspection() {
        Record created = recordBuilder.mockRecord(RecordStatus.INSPECTION, null);
        Assertions.assertNotNull(created);

        Inspection inspection = saveInspection(created, InspectionStatus.PROCESSING);

        Assertions.assertNotNull(inspection);

        InspectionDTO dto = InspectionDTO.builder()
                .id(inspection.getId())
                .recordId(created.getId())
                .description("Edited Desc")
                .endDate(LocalDate.now().plusDays(2))
                .build();

        Record record = service.completeInspection(created.getId(), dto, langBG);
        Assertions.assertNotNull(record);
        Assertions.assertEquals(RecordStatus.INSPECTION_COMPLETED, record.getStatus());

        Inspection completedInspection = inspectionService.findById(inspection.getId());
        Assertions.assertNotNull(completedInspection);
        Assertions.assertEquals(dto.getDescription(), completedInspection.getI18n(langBG).getDescription());
        Assertions.assertEquals(dto.getEndDate(), completedInspection.getEndDate());
        Assertions.assertEquals(InspectionStatus.COMPLETED, completedInspection.getStatus());
    }

    @Test
    void givenNullInspectionDTO_whenCompleteInspection_thenReturnInspectionCannotBeNull() {
        Record created = recordBuilder.saveRecord();
        Assertions.assertNotNull(created);

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
                () -> service.completeInspection(created.getId(), null, langBG));

        Assertions.assertEquals("Inspection cannot be null.", thrown.getMessage());
    }

    @Test
    void givenInspectionDTOWithNullRecord_whenCompleteInspection_thenReturnInspectionRecordCannotBeNull() {

        InspectionDTO dto = InspectionDTO.builder().recordId(null).build();

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
                () -> service.completeInspection(new Random().nextLong(1000), dto, langBG));

        Assertions.assertEquals("Inspection Record cannot be null.", thrown.getMessage());
    }

    @Test
    void givenRecordWithDifferentRecordStatus_whenCompleteInspection_thenReturnNotFacilityCheckingStatus() {
        Record created = recordBuilder.mockRecord(RecordStatus.INSPECTION_COMPLETED, null);
        Assertions.assertNotNull(created);

        Inspection inspection = saveInspection(created);

        InspectionDTO dto = InspectionDTO.builder()
                .id(inspection.getId())
                .recordId(created.getId())
                .build();

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
                () -> service.completeInspection(created.getId(), dto, langBG));

        Assertions.assertEquals("Record is not in " + RecordStatus.INSPECTION + " status.", thrown.getMessage());

    }

    @Test
    void givenCompletedInspectionDTO_whenCompleteInspection_thenReturnInspectionIsAlreadyCompletedError() {
        Record created = recordBuilder.mockRecord(RecordStatus.INSPECTION, null);
        Assertions.assertNotNull(created);

        Inspection inspection = saveInspection(created);

        InspectionDTO dto = InspectionDTO.builder()
                .id(inspection.getId())
                .recordId(created.getId())
                .description("Edited Desc")
                .endDate(LocalDate.now().plusDays(2))
                .build();

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
                () -> service.completeInspection(created.getId(), dto, langBG));

        Assertions.assertEquals("Inspection is already completed.", thrown.getMessage());
    }

    @Test
    void givenRecord_whenCreateInspectionWithServiceTypeS3180_thenReturnCorrectRecord() {
        Record created = recordBuilder.saveRecord(RecordStatus.PROCESSING, ServiceType.S3180);
        Assertions.assertNotNull(created);

        InspectionDTO inspection = mockInspection(created.getId(), null);

        inspection.getUsers().add(created.getAssignee().getId());

        InspectionDTO createdInspectionDto = service.createInspection(created.getId(), inspection, langBG);

        Assertions.assertNotNull(createdInspectionDto);
        Assertions.assertEquals(InspectionStatus.PROCESSING, createdInspectionDto.getStatus());
        Assertions.assertEquals(1, createdInspectionDto.getUsers().size());
        Assertions.assertEquals(InspectionType.FOR_APPROVAL, createdInspectionDto.getInspectionType());
    }

    @Test
    void givenRecordNotPaymentConfirmed_whenCreateInspection_thenThrowError() {
        Record created = recordBuilder.mockRecord(RecordStatus.ENTERED, ServiceType.S3180);
        Assertions.assertNotNull(created);

        Inspection inspection = saveInspection(created);

        created.getAssignee().getInspections().add(inspection);
        inspection.getUsers().add(created.getAssignee());

        RuntimeException thrown = Assertions.assertThrows(RuntimeException.class,
                () -> service.createInspection(created.getId(), InspectionDTO.of(inspection, langBG), langBG));

        Assertions.assertEquals("Record payment is not processing.", thrown.getMessage());
    }

//    @Test
//    void givenRecordS3180_whenSendForPayment_thenReturnRecordWithValidPriceAndStatus() throws Exception {
//        Record created = recordBuilder.saveRecord(RecordStatus.ENTERED, ServiceType.S3180);
//        Assertions.assertNotNull(created);
//
//        Record responceRecord = service.sendForPayment(created.getId(), 23.3, langBG);
//        Assertions.assertEquals(RecordStatus.PAYMENT_CONFIRMATION, responceRecord.getStatus());
//        Assertions.assertEquals(ServiceType.S3180, responceRecord.getServiceType());
//        Assertions.assertEquals(31.8, responceRecord.getPrice());
//    }

    private Inspection saveInspection(Record record) {
        return saveInspection(record, null);
    }
    private Inspection saveInspection(Record record, InspectionStatus inspectionStatus) {
        Inspection inspection = new Inspection();
        inspection.getI18n(langBG).setDescription("Init description");
        inspection.setFacility(record.getFacility());
        inspection.setRecord(record);
        inspection.setEndDate(LocalDate.now().plusDays(2));
        inspection.setType(InspectionType.PLANNED);
        inspection.setStatus(inspectionStatus != null ? inspectionStatus : InspectionStatus.COMPLETED);
        inspectionRepository.save(inspection);
        return inspection;
    }
    private InspectionDTO mockInspection(Long recordId, InspectionStatus inspectionStatus) {
        InspectionDTO inspection = new InspectionDTO();
        inspection.setDescription("Init description");
        inspection.setRecordId(recordId);
        inspection.setEndDate(LocalDate.now().plusDays(2));
        inspection.setInspectionType(InspectionType.PLANNED);
        inspection.setStatus(inspectionStatus != null ? inspectionStatus : InspectionStatus.COMPLETED);
        return inspection;
    }
}
