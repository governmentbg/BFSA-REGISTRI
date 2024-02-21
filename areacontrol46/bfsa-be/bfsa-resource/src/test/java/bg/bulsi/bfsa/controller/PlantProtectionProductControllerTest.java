package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.PlantProtectionProductDTO;
import bg.bulsi.bfsa.model.PlantProtectionProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlantProtectionProductControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenSomeEntities_testFindByIdFindAllUpdate() throws Exception {
    	MvcResult result;
    	String content;
    	
    	PlantProtectionProductDTO mockEntityDTO = mockEntityDTO();
        result = mockMvc.perform(post("/plant-protection-products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(mockEntityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(mockEntityDTO.getName())))
                .andReturn();

        content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        
        PlantProtectionProductDTO savedEntityDTO = objectMapper.readValue(content, PlantProtectionProductDTO.class);
        Assertions.assertNotNull(savedEntityDTO);


        mockMvc.perform(get("/plant-protection-products/")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", greaterThan(0)))
                .andReturn();

        mockMvc.perform(get("/plant-protection-products/{id}", savedEntityDTO.getId())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedEntityDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(savedEntityDTO.getName())))
                .andReturn();

        savedEntityDTO.setName(savedEntityDTO.getName() + "_Updated");
       
        mockMvc.perform(put("/plant-protection-products/{id}", savedEntityDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(savedEntityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(savedEntityDTO.getName())))
                .andReturn();
    }

    @Test
    void givenFakeId_whenDoNotFindById_thenThrowEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/plant-protection-products/{id}", FAKE_VALUE)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception",
                        is("bg.bulsi.bfsa.exception.EntityNotFoundException")))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message",
                        is("Entity '" + PlantProtectionProduct.class.getName() +"' with id/code='" + FAKE_VALUE + "' not found")))
                .andReturn();
    }

    private PlantProtectionProductDTO mockEntityDTO() {
        return PlantProtectionProductDTO.builder()
        		.quantity(Math.random()*100)
                .name(UUID.randomUUID().toString())
                .build();
    }


}