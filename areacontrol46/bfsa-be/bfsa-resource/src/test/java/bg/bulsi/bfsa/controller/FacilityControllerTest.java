package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.FacilityVO;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.service.FacilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FacilityControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    FacilityService service;

    private FacilityDTO mockFacilityDTO() {
        AddressDTO addressDTO = AddressDTO.builder()
                .id(new Random().nextLong(1000))
                .addressTypeCode(UUID.randomUUID().toString())
                .addressLat(UUID.randomUUID().toString())
                .settlementCode(UUID.randomUUID().toString())
                .phone(UUID.randomUUID().toString())
                .postCode(UUID.randomUUID().toString())
                .build();
        return FacilityDTO.builder()
                .id(new Random().nextLong(1000))
                .mail(UUID.randomUUID().toString())
                .phone1(UUID.randomUUID().toString())
                .phone2(UUID.randomUUID().toString())
                .enabled(true)
                .address(addressDTO)
                .contractorId(new Random().nextLong(1000))
                .name(UUID.randomUUID().toString())
                .description(UUID.randomUUID().toString())
                .branchId(new Random().nextLong(1000))
                .build();
    }
    private FacilityVO mockFacilityVO() {
        FacilityVO vo = new FacilityVO();

        BeanUtils.copyProperties(mockFacilityDTO(), vo);

        return vo;
    }

//    @Test
//    void givenFacility_whenCreate_thenReturnSavedFacility() throws Exception {
//        FacilityDTO dto = mockFacilityDTO();
//
//        when(service.create(any())).thenReturn(FacilityDTO.to(dto, langBG));
//        mockMvc.perform(post("/facilities/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(dto.getId())))
//                .andExpect(jsonPath("$.mail", is(dto.getMail())))
//                .andExpect(jsonPath("$.phone1", is(dto.getPhone1())))
//                .andExpect(jsonPath("$.phone2", is(dto.getPhone2())))
//                .andExpect(jsonPath("$.enabled", is(dto.getEnabled())))
//                .andExpect(jsonPath("$.contractorId", is(dto.getContractorId())))
//                .andExpect(jsonPath("$.description", is(dto.getDescription())))
//                .andExpect(jsonPath("$.name", is(dto.getName())))
//                .andReturn();
//    }

    @Test
    void givenFacility_whenUpdate_thenReturnUpdatedFacility() throws Exception {
        FacilityDTO dto = mockFacilityDTO();
        dto.setMail(dto.getMail() + "_updated");
        dto.setPhone1(dto.getPhone1() + "_updated");
        dto.setPhone2(dto.getPhone2() + "_updated");
        dto.setEnabled(!dto.getEnabled());
        dto.getAddress().setId(new Random().nextLong(1000));
        dto.setContractorId(new Random().nextLong(1000));
        dto.setDescription(dto.getDescription() + "_updated");
        dto.setName(dto.getName() + "_updated");

        when(service.update(any(), any(), any())).thenReturn(FacilityDTO.to(dto, langBG));
        mockMvc.perform(put("/facilities/{id}", dto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.mail", is(dto.getMail())))
                .andExpect(jsonPath("$.phone1", is(dto.getPhone1())))
                .andExpect(jsonPath("$.phone2", is(dto.getPhone2())))
                .andExpect(jsonPath("$.enabled", is(dto.getEnabled())))
//                .andExpect(jsonPath("$.contractorId", is(dto.getContractorId())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andReturn();
    }

    @Test
    void givenFacility_whenFindById_thenReturnFacility() throws Exception {
        FacilityDTO dto = mockFacilityDTO();

        when(service.getById(any(), any())).thenReturn(dto);
        mockMvc.perform(get("/facilities/{id}", dto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.mail", is(dto.getMail())))
                .andExpect(jsonPath("$.phone1", is(dto.getPhone1())))
                .andExpect(jsonPath("$.phone2", is(dto.getPhone2())))
                .andExpect(jsonPath("$.enabled", is(dto.getEnabled())))
                .andExpect(jsonPath("$.contractorId", is(dto.getContractorId().intValue())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andReturn();
    }

    @Test
    void givenFacilityList_whenFindAll_thenReturnFacilityList() throws Exception {
        List<Facility> facilities = new ArrayList<>();
        facilities.add(FacilityDTO.to(mockFacilityDTO(), langBG));
        facilities.add(FacilityDTO.to(mockFacilityDTO(), langBG));
        facilities.add(FacilityDTO.to(mockFacilityDTO(), langBG));

        when(service.findAll()).thenReturn(facilities);
        mockMvc.perform(get("/facilities/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(1)))
                .andReturn();
    }

//    @Test
//    void givenFacilityAndContractor_whenChangeFacilityOwner_thenReturnChangedFacility() throws Exception {
//        Facility entity = FacilityDTO.to(mockFacilityDTO(), langBG);
//        String newOwnerId = UUID.randomUUID().toString();
//        entity.getContractor().setId(newOwnerId);
//
//        when(service.changeFacilityOwner(any(), any(), any(), any(), any())).thenReturn(FacilityDTO.of(entity, langBG));
//
//        mockMvc.perform(put("/facilities/{facilityId}/new-facility-owner/{newFacilityOwnerId}",
//                        entity.getId(), newOwnerId)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
//                )
//                .andExpect(status().isOk())
//                .andDo(print())
//               .andExpect(jsonPath("$.contractorId", is(entity.getContractor().getId())))
//                .andReturn();
//    }

//    @Test
//    void givenRegisterCodeAndBranch_whenFindByRegisterCodeAndBranch_thenReturnListFacilityDto() throws Exception {
//        List<FacilityVO> mocked = List.of(mockFacilityVO(), mockFacilityVO());
//        when(service.findByRegisterCodeAndBranchId(any(), any(), any(), any())).thenReturn(new PageImpl<>(mocked));
//
//        mockMvc.perform(get("/facilities/register-code-and-branch/{registerCode}", UUID.randomUUID().toString())
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
//                .andExpect(status().isOk())
//                .andReturn();
//    }

//    @Test
//    void givenContractorId_whenFindByContractorId_thenReturnFacilities() throws Exception {
//        Facility entity = FacilityDTO.to(mockFacilityDTO(), langBG);
//        List<FacilityDTO> facilities = new ArrayList<>();
//        facilities.add(FacilityDTO.of(entity, langBG));
//
//        when(service.findAllByContractorId(any(), any())).thenReturn(facilities);
//        mockMvc.perform(get("/facilities/contractor-id/{contractorId}", entity.getContractor().getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(jsonPath("$.length()", greaterThan(0)))
//                .andReturn();
//    }

//    @Test
//    void givenDivisionCodeAndBranch_whenFindByDivisionCodeAndBranch_thenReturnListFacilityDto() throws Exception {
//        List<FacilityVO> mocked = List.of(mockFacilityVO(), mockFacilityVO());
//        when(service.findByDivisionCodeAndBranchId(any(), any(), any(), any())).thenReturn(new PageImpl<>(mocked));
//
//        mockMvc.perform(get("/facilities/division-code-and-branch/{registerCode}", UUID.randomUUID().toString())
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
//                .andExpect(status().isOk())
//                .andReturn();
//    }
}
