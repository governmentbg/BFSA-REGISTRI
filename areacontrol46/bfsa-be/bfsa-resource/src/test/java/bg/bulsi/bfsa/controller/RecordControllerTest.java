package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.BaseApplicationDTO;
import bg.bulsi.bfsa.dto.RecordDTO;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.RecordRepository;
import bg.bulsi.bfsa.service.RecordService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecordControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RecordService service;
    @MockBean
    private RecordRepository repository;

//    @Test
//    void whenFindAllByBranch_thenReturnAllRecordDtosPageable() throws Exception {
//        List<Record> records = new ArrayList<>();
//        records.add(mockRecord());
//        records.add(mockRecord());
//        when(service.findAllByBranch(any(), any(), any())).thenReturn(new PageImpl<>(records));
//        when(repository.findAllByBranch(any(), any())).thenReturn(new PageImpl<>(records));
//
//        mockMvc.perform(get("/records/branch/{id}", UUID.randomUUID().toString())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content.size()", greaterThan(1)))
//                .andReturn();
//    }

    @Test
    void givenDTO_whenGetById_thenReturnEntity() throws Exception {
        Record entity = RecordDTO.to(mockRecordDTO());
        Long id = entity.getId();
        when(service.findById(any())).thenReturn(entity);

        mockMvc.perform(get("/records/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(entity.getId().intValue())))
                .andExpect(jsonPath("$.identifier", is(entity.getIdentifier())))
                .andExpect(jsonPath("$.entryDate", is(entity.getEntryDate().toString())))
                .andExpect(jsonPath("$.contractorId", is(entity.getApplicant().getId().intValue())))
                .andReturn();
    }

    @Test
    void givenRecordWithPaymentConfirmation_whenConfirmPayment_thenReturnRecordWithStatusPaid() throws Exception {
        Record record = RecordDTO.to(mockRecordDTO());
        Long id = record.getId();
        Assertions.assertEquals(RecordStatus.ENTERED, record.getStatus());

        record.setStatus(RecordStatus.PAYMENT_CONFIRMATION);
        when(service.confirmPayment(any())).thenReturn(record);

        mockMvc.perform(put("/records/{recordId}/confirm-payment", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(record.getStatus().name())));
    }

    @Test
    void givenDTO_whenSearch_thenReturnEntity() throws Exception {
        List<Record> records = new ArrayList<>();
        Record recordA = RecordDTO.to(mockRecordDTO());
        recordA.getApplicant().setFullName("Full Name");
        records.add(recordA);
        records.add(RecordDTO.to(mockRecordDTO()));

        when(service.search(any(), any(),any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(BaseApplicationDTO.of(recordA))));

        mockMvc.perform(get("/records?q={1}", "Full")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    private Record mockRecord() {
        return Record.builder()
                .id(new Random().nextLong(1000))
                .identifier(UUID.randomUUID().toString())
                .entryNumber(UUID.randomUUID().toString())
                .entryDate(LocalDate.now())
                .price(BigDecimal.valueOf((new Random()).nextDouble()))
                .status(RecordStatus.ENTERED)
                .applicant(Contractor.builder()
                        .identifier(UUID.randomUUID().toString())
                        .fullName(UUID.randomUUID().toString()).build())
                .requestor(Contractor.builder()
                        .identifier(UUID.randomUUID().toString())
                        .fullName(UUID.randomUUID().toString()).build())
                .facility(Facility.builder().build())
                .branch(Branch.builder().id(new Random().nextLong(1000)).build())
                .build();
    }

    private RecordDTO mockRecordDTO() {
        return RecordDTO.builder()
                .id(new Random().nextLong(1000))
                .identifier(UUID.randomUUID().toString())
                .entryDate(LocalDate.now())
                .contractorId(new Random().nextLong(1000))
                .assigneeId(new Random().nextLong(1000))
                .status(RecordStatus.ENTERED.name())
                .build();
    }
}
