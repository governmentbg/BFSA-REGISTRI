package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.NomenclatureDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class NomenclatureControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenNomenclatureWhenCreateNext_thenReturnNomenclature() throws Exception {
        NomenclatureDTO nom = NomenclatureDTO.builder().name("Months").description("Desc Months").build();
        NomenclatureDTO subNom = NomenclatureDTO.builder().name("January").description("Desc January").build();

        MvcResult result = mockMvc.perform(post("/noms/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        NomenclatureDTO dto = objectMapper.readValue(content, NomenclatureDTO.class);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(nom.getName(), dto.getName());
        Assertions.assertEquals(nom.getDescription(), dto.getDescription());

        subNom.setParentCode(dto.getParentCode());

        mockMvc.perform(post("/noms/{parentCode}/create-next", dto.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(subNom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(subNom.getName())))
                .andExpect(jsonPath("$.description", is(subNom.getDescription())))
                .andReturn();
    }

    @Test
    void givenNomenclatureWhenCreateNext_thenThrowEntityAlreadyExistException() throws Exception {
        NomenclatureDTO nom = NomenclatureDTO.builder().name("Months").description("Desc Months").build();

        MvcResult result = mockMvc.perform(post("/noms/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        NomenclatureDTO dto = objectMapper.readValue(content, NomenclatureDTO.class);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(nom.getName(), dto.getName());
        Assertions.assertEquals(nom.getDescription(), dto.getDescription());

        String message = "Entity 'bg.bulsi.bfsa.model.Nomenclature' with identifier='" + dto.getCode() + "' already exist";
        String exception = "bg.bulsi.bfsa.exception.EntityAlreadyExistException";

        mockMvc.perform(post("/noms/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is(message)))
                .andExpect(jsonPath("$.exception", is(exception)))
                .andReturn();
    }

    @Test
    void givenNomenclatureWithSubNomenclatureWhenCreateNext_thenReturnCreatedNomenclatureWithSubNomenclature() throws Exception {
        NomenclatureDTO nom = NomenclatureDTO.builder().name("Zodiac").description("Desc Zodiac").build();
        nom.getSubNomenclatures().add(NomenclatureDTO.builder().name("Aries").description("Desc Aries").build());
        nom.getSubNomenclatures().add(NomenclatureDTO.builder().name("Taurus").description("Desc Taurus").build());

        mockMvc.perform(post("/noms/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andExpect(jsonPath("$.subNomenclatures", hasSize(2)))
                .andExpect(jsonPath("$.subNomenclatures[0].name", is("Aries")))
                .andExpect(jsonPath("$.subNomenclatures[0].description", is("Desc Aries")))
                .andExpect(jsonPath("$.subNomenclatures[1].name", is("Taurus")))
                .andExpect(jsonPath("$.subNomenclatures[1].description", is("Desc Taurus")))
                .andReturn();
    }

    @Test
    void givenNomenclatureWhenAddNext_thenReturnNomenclatureDTOWithAdditionalTranslation() throws Exception {
        NomenclatureDTO nom = NomenclatureDTO.builder().name("Months").description("Desc Months").build();

        MvcResult result = mockMvc.perform(post("/noms/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        NomenclatureDTO dto = objectMapper.readValue(content, NomenclatureDTO.class);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(nom.getName(), dto.getName());
        Assertions.assertEquals(nom.getDescription(), dto.getDescription());
        dto.setName("Месеци");
        dto.setDescription("Описание");

        mockMvc.perform(post("/noms/{code}/add-next", dto.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(dto.getCode())))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andReturn();
    }

    @Test
    void givenNomenclatureCode_whenFindByCode_thenReturnNomenclature() throws Exception {
        NomenclatureDTO nom = NomenclatureDTO.builder().name("Months").description("Desc Months").build();

        MvcResult result = mockMvc.perform(post("/noms/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        NomenclatureDTO dto = objectMapper.readValue(content, NomenclatureDTO.class);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(nom.getName(), dto.getName());
        Assertions.assertEquals(nom.getDescription(), dto.getDescription());

        mockMvc.perform(get("/noms/{code}", dto.getCode())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(dto.getCode())))
                .andDo(print())
                .andReturn();
    }

    @Test
    void whenFindAllParents_thenReturnAllParentNomenclatures() throws Exception {
        NomenclatureDTO nom = NomenclatureDTO.builder().name("Months").description("Desc Months").build();

        MvcResult result = mockMvc.perform(post("/noms/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        mockMvc.perform(get("/noms/").header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", greaterThan(0)))
                .andReturn();
    }

    @Test
    void givenNomenclatureWhenUpdate_thenReturnUpdatedNomenclature() throws Exception {
        NomenclatureDTO nom = NomenclatureDTO.builder().name("Months").description("Desc Months").build();

        MvcResult result = mockMvc.perform(post("/noms/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        NomenclatureDTO dto = objectMapper.readValue(content, NomenclatureDTO.class);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(nom.getName(), dto.getName());
        Assertions.assertEquals(nom.getDescription(), dto.getDescription());

        mockMvc.perform(put("/noms/{parentCode}", dto.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(
                                NomenclatureDTO.builder()
                                        .name(dto.getName() + "Updated")
                                        .description(dto.getDescription() + "Updated").build()
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(dto.getCode())))
                .andExpect(jsonPath("$.name", is(dto.getName() + "Updated")))
                .andExpect(jsonPath("$.description", is(dto.getDescription() + "Updated")))
                .andReturn();
    }
}