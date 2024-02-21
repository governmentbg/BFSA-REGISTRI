package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.BranchDTO;
import bg.bulsi.bfsa.model.Branch;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BranchControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenFindAll_thenReturnAllBranchDTOs() throws Exception {
        BranchDTO newBranchDTO = mockBranchDTO();
        newBranchDTO.setSettlementCode(createSettlement().getCode());

        mockMvc.perform(post("/branches/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(newBranchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(newBranchDTO.getName())))
                .andExpect(jsonPath("$.description", is(newBranchDTO.getDescription())))
                .andReturn();

        mockMvc.perform(get("/branches/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.size()", greaterThan(0)))
                .andReturn();
    }

    @Test
    void givenBranchId_whenFindById_thenReturnBranchDto() throws Exception {
        BranchDTO newBranchDTO = mockBranchDTO();
        newBranchDTO.setSettlementCode(createSettlement().getCode());

        MvcResult result = mockMvc.perform(post("/branches/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(newBranchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(newBranchDTO.getName())))
                .andExpect(jsonPath("$.description", is(newBranchDTO.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        BranchDTO savedBranchDTO = objectMapper.readValue(content, BranchDTO.class);
        Assertions.assertNotNull(savedBranchDTO);
        Assertions.assertEquals(newBranchDTO.getName(), savedBranchDTO.getName());
        Assertions.assertEquals(newBranchDTO.getDescription(), savedBranchDTO.getDescription());

        mockMvc.perform(get("/branches/{id}", savedBranchDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedBranchDTO.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(savedBranchDTO.getEnabled())))
                .andExpect(jsonPath("$.name", is(savedBranchDTO.getName())))
                .andExpect(jsonPath("$.description", is(savedBranchDTO.getDescription())))
                .andExpect(jsonPath("$.email", is(savedBranchDTO.getEmail())))
                .andReturn();
    }

    @Test
    void givenFakeId_whenDoNotFindFishingVesselById_thenThrowError() throws Exception {
        mockMvc.perform(get("/branches/{id}", FAKE_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception",
                        is("bg.bulsi.bfsa.exception.EntityNotFoundException")))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message",
                        is("Entity '" + Branch.class.getName() + "' with id/code='" + FAKE_VALUE + "' not found")))
                .andReturn();
    }

    @Test
    void givenBranch_whenUpdate_thenReturnUpdatedBranch() throws Exception {
        BranchDTO newBranchDTO = mockBranchDTO();
        newBranchDTO.setSettlementCode(createSettlement().getCode());

        MvcResult result = mockMvc.perform(post("/branches/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(newBranchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(newBranchDTO.getName())))
                .andExpect(jsonPath("$.description", is(newBranchDTO.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        BranchDTO savedBranchDTO = objectMapper.readValue(content, BranchDTO.class);
        Assertions.assertNotNull(savedBranchDTO);
        Assertions.assertEquals(newBranchDTO.getName(), savedBranchDTO.getName());
        Assertions.assertEquals(newBranchDTO.getDescription(), savedBranchDTO.getDescription());

        savedBranchDTO.setName(newBranchDTO.getName() + "_Updated");
        savedBranchDTO.setDescription(newBranchDTO.getDescription() + "_Updated");

        mockMvc.perform(put("/branches/{id}", savedBranchDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(savedBranchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(newBranchDTO.getName() + "_Updated")))
                .andExpect(jsonPath("$.description", is(newBranchDTO.getDescription() + "_Updated")))
                .andReturn();
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