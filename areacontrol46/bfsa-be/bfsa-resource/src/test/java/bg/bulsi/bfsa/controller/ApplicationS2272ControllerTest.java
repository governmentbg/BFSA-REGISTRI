package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApplicationS2272DTO;
import bg.bulsi.bfsa.dto.ApproveFoodTypesDTO;
import bg.bulsi.bfsa.dto.RecordDTO;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.service.ApplicationS2272Service;
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
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationS2272ControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ApplicationS2272Service applicationS2272Service;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenRecordId_whenGetApplicationS2272ByRecordId_thenReturn_ApplicationS2272DTO() throws Exception {
        ApplicationS2272DTO applicationS2272DTO = mockApplicationS2272DTO();
        Record entity = RecordDTO.to(mockRecordDTO());
        Long id = entity.getId();

        when(applicationS2272Service.findByRecordId(any(), any())).thenReturn(applicationS2272DTO);
        mockMvc.perform(get("/s2272-applications/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(applicationS2272DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestorIdentifier",
                        is(applicationS2272DTO.getRequestorIdentifier())))
                .andReturn();
    }

    @Test
    void givenRecordId_whenRefuse_thenReturnRecord() throws Exception {
        Record record = mockRecord();
        when(applicationS2272Service.refuse(any())).thenReturn(record);
        mockMvc.perform(MockMvcRequestBuilders.put("/s2272-applications/{id}/refuse", record.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    void givenRecordId_whenApprove_thenReturnFacilityDto() throws Exception {
        Record record = mockRecord();
        when(applicationS2272Service.approve(any(), any(), any())).thenReturn(record);

        mockMvc.perform(MockMvcRequestBuilders.put("/s2272-applications/{id}/approve", record.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                .content(objectMapper.writeValueAsString(List.of(ApproveFoodTypesDTO.builder().build()))))
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

    private ApplicationS2272DTO mockApplicationS2272DTO() {
        return ApplicationS2272DTO.builder()
                .requestorEmail(UUID.randomUUID().toString())
                .requestorIdentifier(UUID.randomUUID().toString())
                .applicantEmail(UUID.randomUUID().toString())
                .applicantFullName(UUID.randomUUID().toString())
                .applicantIdentifier(UUID.randomUUID().toString())
                .branchIdentifier(UUID.randomUUID().toString())
                .build();
    }
}
