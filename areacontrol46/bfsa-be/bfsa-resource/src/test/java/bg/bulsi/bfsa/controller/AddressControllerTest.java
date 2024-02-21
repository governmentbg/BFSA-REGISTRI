package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddressControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AddressService service;

    @Test
    void whenFindAll_thenReturnAllAddressesDTOs() throws Exception {
        List<AddressDTO> entities = new ArrayList<>();
        entities.add(mockAddressDTO());
        entities.add(mockAddressDTO());
        List<Address> addresses = AddressDTO.to(entities);

        when(service.findAll(any())).thenReturn(new PageImpl<>(addresses));
        mockMvc.perform(get("/addresses/")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", greaterThan(1)))
                .andReturn();
    }

    @Test
    void givenAddressId_whenFindById_thenReturnAddressDto() throws Exception {
        AddressDTO addressDTO = mockAddressDTO();

        when(service.findById(any())).thenReturn(AddressDTO.to(addressDTO));
        mockMvc.perform(get("/addresses/{id}", addressDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(addressDTO.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(addressDTO.getEnabled())))
                .andExpect(jsonPath("$.address", is(addressDTO.getAddress())))
                .andExpect(jsonPath("$.addressLat", is(addressDTO.getAddressLat())))
                .andReturn();
    }

    @Test
    void givenFakeId_whenDoNotFindFishingVesselById_thenThrowError() throws Exception {
        when(service.findById(0L)).thenThrow(new EntityNotFoundException(Address.class, 0));
        mockMvc.perform(get("/addresses/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception",
                        is("bg.bulsi.bfsa.exception.EntityNotFoundException")))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message",
                        is("Entity '" + Address.class.getName() +
                                "' with id/code='" + 0 + "' not found")))
                .andReturn();
    }

    @Test
    void givenListOfAddresses_whenHistory_thenReturnListOfAddresses() throws Exception {
        List<AddressDTO> entities = new ArrayList<>();
        entities.add(mockAddressDTO());
        entities.add(mockAddressDTO());

        when(service.findRevisions(any(), any())).thenReturn(entities);
        mockMvc.perform(get("/addresses/{id}/history", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(1)))
                .andReturn();
    }

    private AddressDTO mockAddressDTO() {
        return AddressDTO.builder()
                .id(new Random().nextLong(1000))
                .settlementCode(UUID.randomUUID().toString())
                .addressTypeCode(UUID.randomUUID().toString())
                .address(UUID.randomUUID().toString())
                .addressLat(UUID.randomUUID().toString())
                .enabled(true)
                .build();
    }
}