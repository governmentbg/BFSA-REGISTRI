package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApplicationS2701DTO;
import bg.bulsi.bfsa.dto.RecordDTO;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.service.ApplicationS2701Service;
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

public class ApplicationS2701ControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ApplicationS2701Service service;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenRecordId_whenGetApplicationS2701ByRecordId_thenReturn_ApplicationS2701DTO() throws Exception {
        ApplicationS2701DTO applicationS2701DTO = mockApplicationS2701DTO();
        Record entity = RecordDTO.to(mockRecordDTO());
        Long id = entity.getId();

        when(service.findByRecordId(any(), any())).thenReturn(applicationS2701DTO);

        mockMvc.perform(get("/s2701-applications/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(applicationS2701DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestorIdentifier",
                        is(applicationS2701DTO.getRequestorIdentifier())))
                .andDo(print())
                .andReturn();
    }

    @Test
    void givenRecordId_whenRefuse_thenReturnRecord() throws Exception {
        Record record = mockRecord();
        when(service.refuse(any())).thenReturn(record);
        mockMvc.perform(MockMvcRequestBuilders.put("/s2701-applications/{id}/refuse", record.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    void givenRecordId_whenApprove_thenReturnFacilityDto() throws Exception {
        Record record = mockRecord();
        when(service.approve(any(), any())).thenReturn(record);

        mockMvc.perform(MockMvcRequestBuilders.put("/s2701-applications/{id}/approve", record.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
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
                .contractorPaper(ContractorPaper.builder().id(new Random().nextLong(1000)).build())
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

    private ApplicationS2701DTO mockApplicationS2701DTO() {
        return ApplicationS2701DTO.builder()
                .requestorEmail(UUID.randomUUID().toString())
                .requestorIdentifier(UUID.randomUUID().toString())
                .applicantEmail(UUID.randomUUID().toString())
                .applicantFullName(UUID.randomUUID().toString())
                .applicantIdentifier(UUID.randomUUID().toString())
                .build();
    }
}
