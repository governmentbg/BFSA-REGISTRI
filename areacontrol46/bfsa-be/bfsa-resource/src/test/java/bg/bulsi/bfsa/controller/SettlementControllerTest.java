package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.SettlementDTO;
import bg.bulsi.bfsa.dto.SettlementVO;
import bg.bulsi.bfsa.service.SettlementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class SettlementControllerTest extends BaseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SettlementService settlementService;

    @Test
    void givenSettlementDTO_whenCreateSettlement_thenReturnNewSettlement() throws Exception {
        SettlementDTO dto = mockSettlementDTO();

        when(settlementService.create(any())).thenReturn(SettlementDTO.to(dto));
        mockMvc.perform(post("/settlements/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(dto.getCode())))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andReturn();
    }

    @Test
    void givenSomeSettlements_whenFindAllParentsWithoutSubs_thenReturnAllSettlements() throws Exception {
        List<SettlementDTO> dtoList = new ArrayList<>();
        dtoList.add(mockSettlementDTO());
        dtoList.add(mockSettlementDTO());
        when(settlementService.findAllParentsWithoutSubs(any())).thenReturn(new PageImpl<>(dtoList));
        mockMvc.perform(get("/settlements/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements", is(2)))
                .andExpect(jsonPath("$.content[0].name", is(dtoList.get(0).getName())))
                .andExpect(jsonPath("$.content[1].name", is(dtoList.get(1).getName())))
                .andReturn();
    }

    @Test
    void givenSomeSettlements_whenFindAllSettlementVO_thenReturnAllSettlementVO() throws Exception {
        List<SettlementVO> responseListVO =
                Arrays.asList(new SettlementVO(UUID.randomUUID().toString(),
                                UUID.randomUUID().toString().substring(0, 10),
                                UUID.randomUUID().toString().substring(0, 10)),
                        new SettlementVO(UUID.randomUUID().toString(),
                                UUID.randomUUID().toString().substring(0, 10),
                                UUID.randomUUID().toString().substring(0, 10)));

        when(settlementService.findAllSettlementVO()).thenReturn(responseListVO);
        mockMvc.perform(get("/settlements/parents"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalCount", greaterThan(0)))
                .andExpect(jsonPath("$.results[0].id", is(responseListVO.get(0).getId())))
                .andExpect(jsonPath("$.results[0].name", is(responseListVO.get(0).getName())))
                .andExpect(jsonPath("$.results[0].nameLat", is(responseListVO.get(0).getNameLat())))
                .andReturn();
    }

    @Test
    void givenSettlementDto_WhenUpdate_thenReturnUpdatedSettlement() throws Exception {
        SettlementDTO responseDTO = mockSettlementDTO();
        String name = responseDTO.getName();
        String nameLat = responseDTO.getNameLat();

        responseDTO.setName(responseDTO.getName() + "_Updated");
        responseDTO.setNameLat(responseDTO.getNameLat() + "_Updated");

        when(settlementService.updateDTO(any(), any())).thenReturn(responseDTO);
        mockMvc.perform(put("/settlements/{code}", responseDTO.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", not(name)))
                .andExpect(jsonPath("$.nameLat", not(nameLat)))
                .andExpect(jsonPath("$.name", is(responseDTO.getName())))
                .andExpect(jsonPath("$.nameLat", is(responseDTO.getNameLat())))
                .andReturn();
    }

    @Test
    void givenSettlementVOList_whenFindAllByRegionSettlementVO_thenReturnCorrectResult() throws Exception {
        List<SettlementVO> settlements = new ArrayList<>();
        settlements.add(new SettlementVO(UUID.randomUUID().toString(), "a", "a"));
        settlements.add(new SettlementVO(UUID.randomUUID().toString(), "b", "b"));
        settlements.add(new SettlementVO(UUID.randomUUID().toString(), "c", "c"));
        settlements.add(new SettlementVO(UUID.randomUUID().toString(), "d", "d"));

        when(settlementService.findAllRegionSettlements(any())).thenReturn(settlements);
        mockMvc.perform(get("/settlements/{code}/municipality-settlements", settlements.get(0).getId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalCount", is(settlements.size())))
                .andExpect(jsonPath("$.results[0].id", is(settlements.get(0).getId())))
                .andExpect(jsonPath("$.results[0].name", is(settlements.get(0).getName())))
                .andExpect(jsonPath("$.results[0].nameLat", is(settlements.get(0).getNameLat())))
                .andReturn();
    }

    @Test
    void givenSettlementVOList_whenFindAllByMunicipalitySettlementVO_thenReturnCorrectResult() throws Exception {
        List<SettlementVO> settlements = new ArrayList<>();
        settlements.add(new SettlementVO(UUID.randomUUID().toString(), "a", "a"));
        settlements.add(new SettlementVO(UUID.randomUUID().toString(), "b", "b"));
        settlements.add(new SettlementVO(UUID.randomUUID().toString(), "c", "c"));
        settlements.add(new SettlementVO(UUID.randomUUID().toString(), "d", "d"));

        when(settlementService.findAllMunicipalitySettlements(any())).thenReturn(settlements);
        mockMvc.perform(get("/settlements/{code}/region-settlements", settlements.get(0).getId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalCount", is(settlements.size())))
                .andExpect(jsonPath("$.results[0].id", is(settlements.get(0).getId())))
                .andExpect(jsonPath("$.results[0].name", is(settlements.get(0).getName())))
                .andExpect(jsonPath("$.results[0].nameLat", is(settlements.get(0).getNameLat())))
                .andReturn();
    }

}