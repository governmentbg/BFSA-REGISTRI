package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.BranchDTO;
import bg.bulsi.bfsa.dto.FishingVesselDTO;
import bg.bulsi.bfsa.model.FishingVessel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FishingVesselControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenFindAll_thenReturnAllFishingVesselsDtos() throws Exception {
        FishingVesselDTO newDTO = mockFishingVesselDTO();

        MvcResult result = mockMvc.perform(post("/fishing-vessels/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regNumber", is(newDTO.getRegNumber())))
                .andExpect(jsonPath("$.externalMarking", is(newDTO.getExternalMarking())))
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        FishingVesselDTO savedDTO = objectMapper.readValue(content, FishingVesselDTO.class);
        Assertions.assertNotNull(savedDTO);
        Assertions.assertEquals(newDTO.getRegNumber(), savedDTO.getRegNumber());
        Assertions.assertEquals(newDTO.getExternalMarking(), savedDTO.getExternalMarking());

        mockMvc.perform(get("/fishing-vessels/")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", greaterThan(0)))
                .andReturn();
    }

//    @Test
//    void givenFishingVesselId_whenFindById_thenReturnFishingVesselDto() throws Exception {
//        FishingVesselDTO newDTO = mockFishingVesselDTO();
//
//        MvcResult result = mockMvc.perform(post("/fishing-vessels/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .content(objectMapper.writeValueAsString(newDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.regNumber", is(newDTO.getRegNumber())))
//                .andExpect(jsonPath("$.externalMarking", is(newDTO.getExternalMarking())))
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        FishingVesselDTO savedDTO = objectMapper.readValue(content, FishingVesselDTO.class);
//        Assertions.assertNotNull(savedDTO);
//        Assertions.assertEquals(newDTO.getRegNumber(), savedDTO.getRegNumber());
//        Assertions.assertEquals(newDTO.getExternalMarking(), savedDTO.getExternalMarking());
//
//        mockMvc.perform(get("/fishing-vessels/{id}", savedDTO.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(savedDTO.getId().intValue())))
//                .andExpect(jsonPath("$.enabled", is(savedDTO.getEnabled())))
//                .andExpect(jsonPath("$.regNumber", is(savedDTO.getRegNumber())))
//                .andExpect(jsonPath("$.entryNumber", is(savedDTO.getEntryNumber())))
//                .andExpect(jsonPath("$.externalMarking", is(savedDTO.getExternalMarking())))
//                .andReturn();
//    }

//    @Test
//    void givenFakeId_whenDoNotFindFishingVesselById_thenThrowError() throws Exception {
//        mockMvc.perform(get("/fishing-vessels/{id}", FAKE_VALUE)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.exception",
//                        is("bg.bulsi.bfsa.exception.EntityNotFoundException")))
//                .andExpect(jsonPath("$.error", is("Not Found")))
//                .andExpect(jsonPath("$.message",
//                        is("Entity '" + FishingVessel.class.getName() +"' with id/code='" + FAKE_VALUE + "' not found")))
//                .andReturn();
//    }

    @Test
    void givenRegistrationNumber_whenFindByRegNumber_thenReturnFishingVesselDto() throws Exception {
        FishingVesselDTO newDTO = mockFishingVesselDTO();

        MvcResult result = mockMvc.perform(post("/fishing-vessels/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regNumber", is(newDTO.getRegNumber())))
                .andExpect(jsonPath("$.externalMarking", is(newDTO.getExternalMarking())))
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        FishingVesselDTO savedDTO = objectMapper.readValue(content, FishingVesselDTO.class);
        Assertions.assertNotNull(savedDTO);
        Assertions.assertEquals(newDTO.getRegNumber(), savedDTO.getRegNumber());
        Assertions.assertEquals(newDTO.getExternalMarking(), savedDTO.getExternalMarking());

        mockMvc.perform(
                get("/fishing-vessels/registration-number/{regNumber}", savedDTO.getRegNumber())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedDTO.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(savedDTO.getEnabled())))
                .andExpect(jsonPath("$.regNumber", is(savedDTO.getRegNumber())))
                .andExpect(jsonPath("$.entryNumber", is(savedDTO.getEntryNumber())))
                .andExpect(jsonPath("$.externalMarking", is(savedDTO.getExternalMarking())))
                .andReturn();
    }

//    @Test
//    void givenExternalMarking_whenFindByExternalMarking_thenReturnFishingVesselDto() throws Exception {
//        FishingVesselDTO newDTO = mockFishingVesselDTO();
//
//        MvcResult result = mockMvc.perform(post("/fishing-vessels/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .content(objectMapper.writeValueAsString(newDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.regNumber", is(newDTO.getRegNumber())))
//                .andExpect(jsonPath("$.externalMarking", is(newDTO.getExternalMarking())))
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        FishingVesselDTO savedDTO = objectMapper.readValue(content, FishingVesselDTO.class);
//        Assertions.assertNotNull(savedDTO);
//        Assertions.assertEquals(newDTO.getRegNumber(), savedDTO.getRegNumber());
//        Assertions.assertEquals(newDTO.getExternalMarking(), savedDTO.getExternalMarking());
//
//        mockMvc.perform(get("/fishing-vessels/external-marking/{externalMarking}", savedDTO.getExternalMarking()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(savedDTO.getId().intValue())))
//                .andExpect(jsonPath("$.enabled", is(savedDTO.getEnabled())))
//                .andExpect(jsonPath("$.regNumber", is(savedDTO.getRegNumber())))
//                .andExpect(jsonPath("$.entryNumber", is(savedDTO.getEntryNumber())))
//                .andExpect(jsonPath("$.externalMarking", is(savedDTO.getExternalMarking())))
//                .andReturn();
//    }

//    @Test
//    void givenHullLength_whenFindByHullLength_thenReturnFishingVesselDtos() throws Exception {
//        FishingVesselDTO newDTO = mockFishingVesselDTO();
//
//        MvcResult result = mockMvc.perform(post("/fishing-vessels/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .content(objectMapper.writeValueAsString(newDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.regNumber", is(newDTO.getRegNumber())))
//                .andExpect(jsonPath("$.externalMarking", is(newDTO.getExternalMarking())))
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        FishingVesselDTO savedDTO = objectMapper.readValue(content, FishingVesselDTO.class);
//        Assertions.assertNotNull(savedDTO);
//        Assertions.assertEquals(newDTO.getRegNumber(), savedDTO.getRegNumber());
//        Assertions.assertEquals(newDTO.getExternalMarking(), savedDTO.getExternalMarking());
//
//        mockMvc.perform(get("/fishing-vessels/hull-length/{hullLength}", savedDTO.getHullLength()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content.size()", greaterThan(0)))
//                .andReturn();;
//    }

//    @Test
//    void givenEntryNumber_whenFindByEntryNumber_thenReturnFishingVesselDto() throws Exception {
//        FishingVesselDTO newDTO = mockFishingVesselDTO();
//
//        MvcResult result = mockMvc.perform(post("/fishing-vessels/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .content(objectMapper.writeValueAsString(newDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.regNumber", is(newDTO.getRegNumber())))
//                .andExpect(jsonPath("$.externalMarking", is(newDTO.getExternalMarking())))
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        FishingVesselDTO savedDTO = objectMapper.readValue(content, FishingVesselDTO.class);
//        Assertions.assertNotNull(savedDTO);
//        Assertions.assertEquals(newDTO.getRegNumber(), savedDTO.getRegNumber());
//        Assertions.assertEquals(newDTO.getExternalMarking(), savedDTO.getExternalMarking());
//
//        mockMvc.perform(get("/fishing-vessels/entry-number/{entryNumber}", savedDTO.getEntryNumber()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(savedDTO.getId().intValue())))
//                .andExpect(jsonPath("$.enabled", is(savedDTO.getEnabled())))
//                .andExpect(jsonPath("$.regNumber", is(savedDTO.getRegNumber())))
//                .andExpect(jsonPath("$.entryNumber", is(savedDTO.getEntryNumber())))
//                .andExpect(jsonPath("$.externalMarking", is(savedDTO.getExternalMarking())))
//                .andReturn();
//    }

//    @Test
//    void givenDatesFromAndTo_whenFindByDate_thenReturnFishingVesselDtos() throws Exception {
//        FishingVesselDTO newDTO = mockFishingVesselDTO();
//
//        MvcResult result = mockMvc.perform(post("/fishing-vessels/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .content(objectMapper.writeValueAsString(newDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.regNumber", is(newDTO.getRegNumber())))
//                .andExpect(jsonPath("$.externalMarking", is(newDTO.getExternalMarking())))
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        FishingVesselDTO savedDTO = objectMapper.readValue(content, FishingVesselDTO.class);
//        Assertions.assertNotNull(savedDTO);
//        Assertions.assertEquals(newDTO.getRegNumber(), savedDTO.getRegNumber());
//        Assertions.assertEquals(newDTO.getExternalMarking(), savedDTO.getExternalMarking());
//
//        mockMvc.perform(get("/fishing-vessels/date/")
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .queryParam("start", String.valueOf(savedDTO.getDate().minusDays(50L)))
//                        .queryParam("end", String.valueOf(savedDTO.getDate().plusDays(50L))))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content.size()", greaterThan(0)))
//                .andReturn();
//    }

//    @Test
//    void givenBranchId_whenFindBy_thenReturnAllBranchFishingVesselDtos() throws Exception {
//
//        BranchDTO newBranchDTO = mockBranchDTO();
//        newBranchDTO.setSettlementCode(createSettlement().getCode());
//
//        MvcResult result = mockMvc.perform(post("/branches/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .content(objectMapper.writeValueAsString(newBranchDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(newBranchDTO.getName())))
//                .andExpect(jsonPath("$.description", is(newBranchDTO.getDescription())))
//                .andReturn();
//
//        String content = result.getResponse().getContentAsString();
//        BranchDTO savedBranchDTO = objectMapper.readValue(content, BranchDTO.class);
//        Assertions.assertNotNull(savedBranchDTO);
//        Assertions.assertEquals(newBranchDTO.getName(), savedBranchDTO.getName());
//        Assertions.assertEquals(newBranchDTO.getDescription(), savedBranchDTO.getDescription());
//
//        FishingVesselDTO newDTO = mockFishingVesselDTO();
//        newDTO.setBranchId(savedBranchDTO.getId());
//
//        result = mockMvc.perform(post("/fishing-vessels/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .content(objectMapper.writeValueAsString(newDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.regNumber", is(newDTO.getRegNumber())))
//                .andExpect(jsonPath("$.externalMarking", is(newDTO.getExternalMarking())))
//                .andReturn();
//
//        content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        FishingVesselDTO savedDTO = objectMapper.readValue(content, FishingVesselDTO.class);
//        Assertions.assertNotNull(savedDTO);
//        Assertions.assertEquals(newDTO.getRegNumber(), savedDTO.getRegNumber());
//        Assertions.assertEquals(newDTO.getExternalMarking(), savedDTO.getExternalMarking());
//
//        mockMvc.perform(get("/fishing-vessels/branch/{branchId}", savedBranchDTO.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content.size()", greaterThan(0)))
//                .andReturn();
//    }

    @Test
    void givenFishingVessel_whenUpdate_thenReturnUpdateHistory() throws Exception {
        FishingVesselDTO newDTO = mockFishingVesselDTO();

        MvcResult result = mockMvc.perform(post("/fishing-vessels/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(newDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regNumber", is(newDTO.getRegNumber())))
                .andExpect(jsonPath("$.externalMarking", is(newDTO.getExternalMarking())))
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        FishingVesselDTO savedDTO = objectMapper.readValue(content, FishingVesselDTO.class);
        Assertions.assertNotNull(savedDTO);
        Assertions.assertEquals(newDTO.getRegNumber(), savedDTO.getRegNumber());
        Assertions.assertEquals(newDTO.getExternalMarking(), savedDTO.getExternalMarking());

        savedDTO.setExternalMarking(savedDTO.getExternalMarking() + "_updated");

        mockMvc.perform(put("/fishing-vessels/{id}", savedDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(savedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedDTO.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(savedDTO.getEnabled())))
                .andExpect(jsonPath("$.regNumber", is(savedDTO.getRegNumber())))
                .andExpect(jsonPath("$.entryNumber", is(savedDTO.getEntryNumber())))
                .andExpect(jsonPath("$.externalMarking", is(savedDTO.getExternalMarking())))
                .andReturn();
    }

    private FishingVesselDTO mockFishingVesselDTO() {
        return FishingVesselDTO.builder()
                .date(LocalDate.now())
                .regNumber(UUID.randomUUID().toString())
                .externalMarking(UUID.randomUUID().toString())
                .enabled(true)
                .entryNumber(UUID.randomUUID().toString())
                .hullLength(Math.random())
                .build();
    }

    private BranchDTO mockBranchDTO() {
        return BranchDTO.builder()
                .email(UUID.randomUUID().toString())
                .phone1(UUID.randomUUID().toString())
                .phone2(UUID.randomUUID().toString())
                .phone3(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .address(UUID.randomUUID().toString())
                .description(UUID.randomUUID().toString())
                .main(false)
                .enabled(true)
                .build();
    }

}