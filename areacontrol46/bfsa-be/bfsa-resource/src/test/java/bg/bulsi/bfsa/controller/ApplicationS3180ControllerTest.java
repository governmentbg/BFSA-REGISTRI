package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS3180DTO;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.RecordDTO;
import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FacilityPaper;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.service.ApplicationS3180Service;
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

public class ApplicationS3180ControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ApplicationS3180Service service;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenRecordId_whenGetApplicationS3180ByRecordId_thenReturn_ApplicationS3181DTO() throws Exception {
        ApplicationS3180DTO applicationS3180DTO = mockApplicationS3180DTO();
        Record entity = RecordDTO.to(mockRecordDTO());
        Long id = entity.getId();

        when(service.findByRecordId(any(), any())).thenReturn(applicationS3180DTO);

        mockMvc.perform(get("/s3180-applications/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(applicationS3180DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestorIdentifier",
                        is(applicationS3180DTO.getRequestorIdentifier())))
                .andReturn();
    }

    @Test
    void givenRecordId_whenRefuse_thenReturnRecord() throws Exception {
        Record record = mockRecord();
        when(service.refuse(any())).thenReturn(record);
        mockMvc.perform(MockMvcRequestBuilders.put("/s3180-applications/{id}/refuse", record.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @Test
    void givenRecordId_whenApprove_thenReturnFacilityDto() throws Exception {
        Record record = mockRecord();
        when(service.approve(any(), any())).thenReturn(record);

        mockMvc.perform(MockMvcRequestBuilders.put("/s3180-applications/{id}/approve", record.getId())
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
                .facilityPaper(FacilityPaper.builder().id(new Random().nextLong(1000)).build())
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

    private ApplicationS3180DTO mockApplicationS3180DTO() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddress(UUID.randomUUID().toString());
        addressDTO.setPostCode(UUID.randomUUID().toString().substring(0,6));
        addressDTO.setSettlementCode(UUID.randomUUID().toString());

        FacilityDTO facilityDTO = new FacilityDTO();
        facilityDTO.setAddress(addressDTO);
        facilityDTO.setName(UUID.randomUUID().toString());
        facilityDTO.setPhone1(UUID.randomUUID().toString().substring(0,9));

        return ApplicationS3180DTO.builder()
                .requestorEmail(UUID.randomUUID().toString())
                .activityGroupId(new Random().nextLong(1000))
                .requestorIdentifier(UUID.randomUUID().toString())
                .applicantEmail(UUID.randomUUID().toString())
                .applicantFullName(UUID.randomUUID().toString())
                .entityType(EntityType.LEGAL)
                .applicantIdentifier(UUID.randomUUID().toString())
                .facility(facilityDTO)
                .build();
    }
}
