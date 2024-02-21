package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS3181DTO;
import bg.bulsi.bfsa.dto.RecordDTO;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.service.ApplicationS3181Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationS3181ControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ApplicationS3181Service applicationS3181Service;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenRecordId_whenGetApplicationS3181ByRecordId_thenReturn_ApplicationS3181DTO() throws Exception {
        ApplicationS3181DTO applicationS3181DTO = mockApplicationS3181DTO();
        Record entity = RecordDTO.to(mockRecordDTO());
        Long id = entity.getId();

        when(applicationS3181Service.findByRecordId(any(), any())).thenReturn(applicationS3181DTO);
        mockMvc.perform(get("/s3181-applications/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(applicationS3181DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestorIdentifier",
                        is(applicationS3181DTO.getRequestorIdentifier())))
                .andReturn();
    }

    @Test
    void givenRecordId_whenRefuse_thenReturnRecord() throws Exception {
        Record record = mockRecord();
        when(applicationS3181Service.refuse(any())).thenReturn(record);
        mockMvc.perform(MockMvcRequestBuilders.put("/s3181-applications/{id}/refuse", record.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    void givenRecordId_whenApprove_thenReturnFacilityDto() throws Exception {
        Record record = mockRecord();
        when(applicationS3181Service.approve(any())).thenReturn(record);

        mockMvc.perform(MockMvcRequestBuilders.put("/s3181-applications/{id}/approve", record.getId())
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

    private ApplicationS3181DTO mockApplicationS3181DTO() {
        return ApplicationS3181DTO.builder()
                .requestorEmail(UUID.randomUUID().toString())
                .requestorIdentifier(UUID.randomUUID().toString())
                .applicantEmail(UUID.randomUUID().toString())
                .applicantFullName(UUID.randomUUID().toString())
                .applicantIdentifier(UUID.randomUUID().toString())
                .address(AddressDTO.builder()
                        .settlementCode(UUID.randomUUID().toString())
                        .phone(UUID.randomUUID().toString())
                        .mail(UUID.randomUUID().toString())
                        .url(UUID.randomUUID().toString())
                        .build())
                .branchIdentifier(UUID.randomUUID().toString())
                .build();
    }
}
