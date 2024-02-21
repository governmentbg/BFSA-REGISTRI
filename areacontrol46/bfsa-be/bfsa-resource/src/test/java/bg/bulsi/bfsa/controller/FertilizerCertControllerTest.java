package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.FertilizerCertDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.FertilizerCert;
import bg.bulsi.bfsa.service.FertilizerCertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FertilizerCertControllerTest extends BaseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FertilizerCertService service;

    @Test
    void whenFindAll_thenReturnAllFertilizerCertDTOs() throws Exception {
        List<FertilizerCertDTO> dtos = new ArrayList<>();
        dtos.add(mockFertilizerCertDTO());
        dtos.add(mockFertilizerCertDTO());
        List<FertilizerCert> t = FertilizerCertDTO.to(dtos, langBG);
        when(service.findAll(any())).thenReturn(new PageImpl<>((t)));

        mockMvc.perform(get("/fertilizer-certs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", greaterThan(0)))
                .andReturn();
    }

    @Test
    void givenId_whenFindById_thenReturnFertilizerCert() throws Exception {
        FertilizerCertDTO mockmockFertilizerCertDTO = mockFertilizerCertDTO();
        when(service.findById(any())).thenReturn(FertilizerCertDTO.to(mockmockFertilizerCertDTO, langBG));

        mockMvc.perform(get("/fertilizer-certs/{id}", mockmockFertilizerCertDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(mockmockFertilizerCertDTO.getId().intValue())))
                .andReturn();
    }

    @Test
    void givenFakeId_whenFindById_thenThrowEntityNotFoundException() throws Exception {
        when(service.findById(0L)).thenThrow(new EntityNotFoundException(FertilizerCert.class, FAKE_VALUE));
        mockMvc.perform(get("/fertilizer-certs/{id}", FAKE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is(EntityNotFoundException.class.getName())))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message",
                        is("Entity '" + FertilizerCert.class.getName()
                                + "' with id/code='" + FAKE_VALUE + "' not found")))
                .andReturn();
    }

    @Test
    void givenId_whenUpdate_thenReturnUpdatedFertilizerCert() throws Exception {
        FertilizerCertDTO mockFertilizerCertDTO = mockFertilizerCertDTO();

        mockFertilizerCertDTO.setName(mockFertilizerCertDTO.getName() + "_Updated");
        when(service.update(any(), any(), any())).thenReturn(mockFertilizerCertDTO);

        mockMvc.perform(put("/fertilizer-certs/{id}", mockFertilizerCertDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(mockFertilizerCertDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(mockFertilizerCertDTO.getName())))
                .andReturn();
    }

    private FertilizerCertDTO mockFertilizerCertDTO() {
        return FertilizerCertDTO.builder()
                .id(new Random().nextLong(1000))
                .entryDate(LocalDate.now().minusDays(2L))
                .orderDate(LocalDate.now())
                .regDate(LocalDate.now())
                .orderDate(LocalDate.now())
                .validUntilDate(LocalDate.now().plusYears(1L))
                .crop(UUID.randomUUID().toString())
                .application(UUID.randomUUID().toString())
                .dose(3.4)
                .fertilizerTypeCode(UUID.randomUUID().toString())
                .enabled(true)
                .manufacturerId(new Random().nextLong(1000))
                .ph(4.5)
                .appNumber(4)
                .edition(UUID.randomUUID().toString())
                .certificateHolderId(new Random().nextLong(1000))
                .ingredients(UUID.randomUUID().toString())
                .description(UUID.randomUUID().toString())
                .reason(UUID.randomUUID().toString())
                .orderNumber(UUID.randomUUID().toString())
                .waterAmount(5.6)
                .regNumber(UUID.randomUUID().toString())
                .build();
    }
}