package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.BranchDTO;
import bg.bulsi.bfsa.dto.ContractorDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.model.Vehicle;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.ContractorService;
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
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VehicleControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ContractorService contractorService;

    @Test
    void givenSomeVehicles_testFindByIdFindAllUpdate() throws Exception {

        BranchDTO mockBranchDTO = mockBranchDTO(createSettlement().getCode());

        MvcResult result = mockMvc.perform(post("/branches/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(mockBranchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(mockBranchDTO.getName())))
                .andExpect(jsonPath("$.description", is(mockBranchDTO.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        BranchDTO savedBranchDTO = objectMapper.readValue(content, BranchDTO.class);
        Assertions.assertNotNull(savedBranchDTO);

        ContractorDTO mockOwnerDTO = mockContractorDTO(List.of(RolesConstants.ROLE_ADMIN));
        ContractorDTO savedOwnerDTO = ContractorDTO.of(contractorService.create(ContractorDTO.to(mockOwnerDTO)), langBG);

        VehicleDTO mockVehicleDTO1 = mockVehicleDTO(savedBranchDTO.getId());
        result = mockMvc.perform(post("/vehicles/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(mockVehicleDTO1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brandModel", is(mockVehicleDTO1.getBrandModel())))
                .andExpect(jsonPath("$.description", is(mockVehicleDTO1.getDescription())))
                .andReturn();

        content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        VehicleDTO savedVehicleDTO1 = objectMapper.readValue(content, VehicleDTO.class);
        Assertions.assertNotNull(savedVehicleDTO1);

        VehicleDTO mockVehicleDTO2 = mockVehicleDTO(savedBranchDTO.getId());
        result = mockMvc.perform(post("/vehicles/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(mockVehicleDTO2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brandModel", is(mockVehicleDTO2.getBrandModel())))
                .andExpect(jsonPath("$.description", is(mockVehicleDTO2.getDescription())))
                .andReturn();

        content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        VehicleDTO savedVehicleDTO2 = objectMapper.readValue(content, VehicleDTO.class);
        Assertions.assertNotNull(savedVehicleDTO2);

        mockMvc.perform(get("/vehicles/")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", greaterThan(0)))
                .andReturn();

        mockMvc.perform(get("/vehicles/{id}", savedVehicleDTO1.getId())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedVehicleDTO1.getId().intValue())))
                .andExpect(jsonPath("$.brandModel", is(savedVehicleDTO1.getBrandModel())))
                .andReturn();

        savedVehicleDTO1.setBrandModel(savedVehicleDTO1.getBrandModel() + "_Updated");
        savedVehicleDTO1.setDescription(savedVehicleDTO1.getDescription() + "_Updated");
        mockMvc.perform(put("/vehicles/{id}", savedVehicleDTO1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(savedVehicleDTO1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brandModel", is(savedVehicleDTO1.getBrandModel())))
                .andExpect(jsonPath("$.description", is(savedVehicleDTO1.getDescription())))
                .andReturn();
    }

    @Test
    void givenFakeId_whenDoNotFindById_thenThrowEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/vehicles/{id}", FAKE_VALUE)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception",
                        is("bg.bulsi.bfsa.exception.EntityNotFoundException")))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message",
                        is("Entity '" + Vehicle.class.getName() +"' with id/code='" + FAKE_VALUE + "' not found")))
                .andReturn();
    }

    private VehicleDTO mockVehicleDTO(final Long branchId) {
        return VehicleDTO.builder()
                .registrationPlate(UUID.randomUUID().toString())
                .entryNumber(UUID.randomUUID().toString())
                .entryDate(LocalDate.now())
                .certificateNumber(UUID.randomUUID().toString())
                .certificateDate(LocalDate.now())
                .load(1055D)
                .brandModel(UUID.randomUUID().toString())
                .description(UUID.randomUUID().toString())
                .branchId(branchId != null && branchId > 0 ? branchId : null)
                .enabled(true)
                .build();
    }

    private ContractorDTO mockContractorDTO(List<String> roles) {
        return ContractorDTO.builder()
                .email(UUID.randomUUID() + "@domain.xyz")
                .fullName(UUID.randomUUID().toString())
                .username(UUID.randomUUID().toString())
                .password(UUID.randomUUID().toString())
                .identifier(UUID.randomUUID().toString())
                .type(EntityType.PHYSICAL.name())
                .enabled(true)
                .roles(roles)
                .build();
    }

    private BranchDTO mockBranchDTO(final String settlementCode) {
        return BranchDTO.builder()
                .email(UUID.randomUUID().toString())
                .phone1(UUID.randomUUID().toString())
                .phone2(UUID.randomUUID().toString())
                .phone3(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .address(UUID.randomUUID().toString())
                .description(UUID.randomUUID().toString())
                .settlementCode(settlementCode)
                .main(false)
                .enabled(true)
                .build();
    }
}