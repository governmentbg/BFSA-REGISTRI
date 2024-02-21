package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ContractorDTO;
import bg.bulsi.bfsa.dto.UserDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.ContractorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class ContractorControllerTest extends BaseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ContractorService service;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String USERNAME = "contractor_test";
    private static final String USERNAME_UPDATED = "contractor_test_updated";

    @Test
    public void givenSearchParameter_whenSearch_thenReturnPageableResponse() throws Exception {
        List<Contractor> mockContractors = new ArrayList<>();
        mockContractors.add(mockContractor());
        mockContractors.add(mockContractor());
        when(service.search(any(), any(), any())).thenReturn(new PageImpl<>(ContractorDTO.of(mockContractors, langBG)));

        mockMvc.perform(get("/contractors?q={1}", "tes")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(1)))
                .andExpect(jsonPath("$.content[*].email",
                        containsInAnyOrder(mockContractors.get(0).getEmail(), mockContractors.get(1).getEmail())))
                .andReturn();
    }

    @Test
    public void givenId_whenFindByUsername_thenReturnContractorDto() throws Exception {
        Contractor mockContractor = mockContractor();
        when(service.findByUsername(any())).thenReturn(mockContractor);

        mockMvc.perform(get("/contractors/username/{username}", mockContractor.getUsername())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(mockContractor.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(mockContractor.getEnabled())))
                .andExpect(jsonPath("$.username", is(mockContractor.getUsername())))
                .andExpect(jsonPath("$.identifier", is(mockContractor.getIdentifier())))
                .andReturn();
    }

    @Test
    public void givenFakeUsername_whenFindByUsername_thenThrowEntityNotFoundException() throws Exception {
        when(service.findByUsername("FAKE_VALUE")).thenThrow(new EntityNotFoundException(Contractor.class, "FAKE_VALUE"));
        mockMvc.perform(get("/contractors/username/{username}", "FAKE_VALUE")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is(EntityNotFoundException.class.getName())))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message",
                        is("Entity '" + Contractor.class.getName() + "' with id/code='" + "FAKE_VALUE" + "' not found")))
                .andReturn();
    }

    @Test
    public void givenId_whenFindById_thenReturnContractorDto() throws Exception {
        ContractorDTO mockContractor = ContractorDTO.of(mockContractor(), langBG);
        when(service.findByIdDTO(any(), any())).thenReturn(mockContractor);

        mockMvc.perform(get("/contractors/{id}", mockContractor.getId())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(mockContractor.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(mockContractor.getEnabled())))
                .andExpect(jsonPath("$.username", is(mockContractor.getUsername())))
                .andExpect(jsonPath("$.identifier", is(mockContractor.getIdentifier())))
                .andReturn();
    }

    @Test
    public void givenFakeId_whenFindById_thenThrowEntityNotFoundException() throws Exception {
        when(service.findByIdDTO(any(), any())).thenThrow(new EntityNotFoundException(Contractor.class, FAKE_VALUE));
        mockMvc.perform(get("/contractors/{id}", FAKE_VALUE)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is(EntityNotFoundException.class.getName())))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message",
                        is("Entity '" + Contractor.class.getName() + "' with id/code='" + FAKE_VALUE + "' not found")))
                .andReturn();
    }

    @Test
    public void whenFindAll_thenReturnPageableResponse() throws Exception {
        List<Contractor> mockContractors = new ArrayList<>();
        mockContractors.add(mockContractor());
        mockContractors.add(mockContractor());
        when(service.findAllDTO(any(), any())).thenReturn(new PageImpl<>(ContractorDTO.of(mockContractors, langBG)));

        mockMvc.perform(get("/contractors/")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", greaterThan(1)))
                .andExpect(jsonPath("$.content[*].email", containsInAnyOrder(mockContractors.get(0).getEmail(), mockContractors.get(1).getEmail())))
                .andReturn();
    }

    @Test
    void givenRole_whenUpdateRoles_thenReturnContractorWithUpdatedRoles() throws Exception {
        Contractor mockContractor = mockContractor();
        mockContractor.getRoles().add(Role.builder().name("DEBUG").build());
        when(service.updateRoles(any(), any())).thenReturn(mockContractor);

        MvcResult result = mockMvc.perform(put("/contractors/{id}/roles", mockContractor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content("[\"DEBUG\"]"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        final ContractorDTO dto = objectMapper.readValue(content, ContractorDTO.class);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(2, dto.getRoles().size());
        Assertions.assertTrue(dto.getRoles().contains("DEBUG"));
    }

    @Test
    void givenEmptyRoleArray_whenUpdateRoles_thenThrowRuntimeException() throws Exception {
        when(service.updateRoles(any(), any())).thenThrow(new RuntimeException("At least one role is required"));

        mockMvc.perform(put("/contractors/{id}/roles", new Random().nextLong(1000))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content("[]"))
                .andExpect(status().isInternalServerError())
                .andExpect((jsonPath("$.exception", is("java.lang.RuntimeException"))))
                .andExpect((jsonPath("$.error", is("Internal Server Error"))))
                .andExpect((jsonPath("$.message", is("At least one role is required"))))
                .andExpect((jsonPath("$.exceptionCode", is("RuntimeException"))));
    }

    @Test
    void givenIdAndFullName_whenUpdate_thenReturnUpdatedContractor() throws Exception {
        Contractor mockContractor = mockContractor();
        mockContractor.setEnabled(false);
        mockContractor.setFullName(USERNAME_UPDATED + " " + USERNAME_UPDATED);
        when(service.update(any(), any(), any())).thenReturn(ContractorDTO.of(mockContractor, langBG));

        mockMvc.perform(put("/contractors/{id}", mockContractor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(UserDTO.builder()
                                .id(mockContractor.getId())
                                .username(mockContractor.getUsername())
                                .enabled(mockContractor.getEnabled())
                                .fullName(mockContractor.getFullName()).build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", is(mockContractor.getEnabled())))
                .andExpect(jsonPath("$.fullName", is(mockContractor.getFullName())))
                .andReturn();
    }

    @Disabled
    @Test
    void givenContractorAndDifferentId_whenUpdate_thenThrowRuntimeException() throws Exception {
        ContractorDTO mockContractor = ContractorDTO.of(mockContractor(), langBG);
        when(service.update(any(), any(), any())).thenReturn(mockContractor);

        mockMvc.perform(put("/contractors/{id}", mockContractor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserDTO.builder()
                                .id(0L)
                                .username(USERNAME + "_updated")
                                .email("test@domain.xyz")
                                .enabled(true)
                                .fullName(USERNAME + "new_full name").build())))
                .andExpect(status().isInternalServerError())
                .andExpect((jsonPath("$.exception", is("java.lang.RuntimeException"))))
                .andExpect((jsonPath("$.error", is("Internal Server Error"))))
                .andExpect((jsonPath("$.message", is("Path variable id doesn't match RequestBody parameter id"))))
                .andExpect((jsonPath("$.exceptionCode", is("RuntimeException"))));
    }

    @Test
    void givenContractor_whenUpdate_thenReturnUpdatedHistory() throws Exception {
        Contractor mockContractor = mockContractor();
        mockContractor.setFullName(USERNAME + "_history " + USERNAME + "_history");
        List<Contractor> mockContractors = List.of(mockContractor(), mockContractor);

        when(service.findRevisions(any(), any())).thenReturn(ContractorDTO.of(mockContractors, langBG));

        mockMvc.perform(get("/contractors/{id}/history", mockContractor.getId())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(1)))
                .andReturn();
    }

    @Test
    void givenContractor_whenSetEnabled_thenReturnUpdatedContractor() throws Exception {
        Contractor mockContractor = mockContractor();
        mockContractor.setEnabled(false);
        when(service.setEnabled(any(), any())).thenReturn(mockContractor);

        mockMvc.perform(put("/contractors/{id}/{enabled}", mockContractor.getId(), false)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", is(false)))
                .andReturn();
    }

    @Test
    public void givenRegisterCode_whenFindByRegisterCode_thenReturnListContractorDto() throws Exception {
        List<Contractor> mockContractors = List.of(mockContractor(), mockContractor());
        when(service.findByRegisterCode(any(), any())).thenReturn(ContractorDTO.of(mockContractors, langBG));

        mockMvc.perform(get("/contractors/register-code/{registerCode}", UUID.randomUUID().toString())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void givenRegisterCodeAndBranch_whenFindByRegisterCodeAndBranch_thenReturnPageContractorDto() throws Exception {
        List<Contractor> mockContractors = new ArrayList<>();
        mockContractors.add(mockContractor());
        mockContractors.add(mockContractor());
        when(service.findByRegisterCodeAndBranchId(any(), any(), any(),any()))
                .thenReturn(new PageImpl<>(ContractorDTO.of(mockContractors, langBG)));

        mockMvc.perform(get("/contractors/register-code-and-branch/{registerCode}", UUID.randomUUID().toString())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)))
                .andExpect(jsonPath("$.content[*].email",
                        containsInAnyOrder(mockContractors.get(0).getEmail(), mockContractors.get(1).getEmail())))
                .andReturn();
    }

    @Test
    public void givenDivisionCodeAndBranch_whenFindByDivisionCodeAndBranch_thenReturnPageContractorDto() throws Exception {
        List<Contractor> mockContractors = new ArrayList<>();
        mockContractors.add(mockContractor());
        mockContractors.add(mockContractor());
        when(service.findByDivisionCodeAndBranchId(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(ContractorDTO.of(mockContractors, langBG)));

        mockMvc.perform(get("/contractors/division-code-and-branch/{divisionCode}", UUID.randomUUID().toString())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(0)))
                .andExpect(jsonPath("$.content[*].email",
                        containsInAnyOrder(mockContractors.get(0).getEmail(), mockContractors.get(1).getEmail())))
                .andReturn();
    }

    @Test
    public void givenContractorPaperServiceType_whenFindByContractorPaperServiceType_thenReturnListContractorDto() throws Exception {
        List<Contractor> mockContractors = new ArrayList<>();
        mockContractors.add(mockContractor());
        mockContractors.add(mockContractor());
        when(service.findAllByContractorPaperServiceType(any(), any()))
                .thenReturn(new ArrayList<>(ContractorDTO.of(mockContractors, langBG)));

        mockMvc.perform(get("/contractors/contractor-paper-service-type/{serviceType}", UUID.randomUUID().toString())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", greaterThan(0)))
                .andReturn();
    }

    private Contractor mockContractor() {
        Contractor entity = Contractor.builder()
                .id(new Random().nextLong(1000))
                .email(UUID.randomUUID() + "@domain.xyz")
                .fullName(USERNAME + " " + USERNAME)
                .username(USERNAME)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .identifier(UUID.randomUUID().toString())
                .enabled(true)
                .build();
        Role roleAdmin = Role.builder().name(RolesConstants.ROLE_ADMIN).build();
        roleAdmin.getContractors().add(entity);
        entity.getRoles().add(roleAdmin);

        return entity;
    }
}