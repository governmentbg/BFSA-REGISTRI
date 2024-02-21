package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ClassifierDTO;
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
class ClassifierControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenClassifierWhenCreateNext_thenReturnClassifier() throws Exception {
        ClassifierDTO classifierDTO = ClassifierDTO.builder().name("Months").description("Desc Months").build();
        ClassifierDTO subClassifier = ClassifierDTO.builder().name("January").description("Desc January").build();

        MvcResult result = mockMvc.perform(post("/classifiers/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(classifierDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(classifierDTO.getName())))
                .andExpect(jsonPath("$.description", is(classifierDTO.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ClassifierDTO dto = objectMapper.readValue(content, ClassifierDTO.class);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(classifierDTO.getName(), dto.getName());
        Assertions.assertEquals(classifierDTO.getDescription(), dto.getDescription());

        subClassifier.setParentCode(dto.getParentCode());

        mockMvc.perform(post("/classifiers/{parentCode}/create-next", dto.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(subClassifier)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(subClassifier.getName())))
                .andExpect(jsonPath("$.description", is(subClassifier.getDescription())))
                .andReturn();
    }

    @Test
    void givenClassifierWhenCreateNext_thenThrowEntityAlreadyExistException() throws Exception {
        ClassifierDTO nom = ClassifierDTO.builder().name("Months").description("Desc Months").build();

        MvcResult result = mockMvc.perform(post("/classifiers/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ClassifierDTO dto = objectMapper.readValue(content, ClassifierDTO.class);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(nom.getName(), dto.getName());
        Assertions.assertEquals(nom.getDescription(), dto.getDescription());

        String message = "Entity 'bg.bulsi.bfsa.model.Classifier' with identifier='" + dto.getCode() + "' already exist";
        String exception = "bg.bulsi.bfsa.exception.EntityAlreadyExistException";

        mockMvc.perform(post("/classifiers/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", is(message)))
                .andExpect(jsonPath("$.exception", is(exception)))
                .andReturn();
    }

    @Test
    void givenClassifierWithSubClassifierWhenCreateNext_thenReturnCreatedClassifierWithSubClassifier() throws Exception {
        ClassifierDTO nom = ClassifierDTO.builder().name("Zodiac").description("Desc Zodiac").build();
        nom.getSubClassifiers().add(ClassifierDTO.builder().name("Aries").description("Desc Aries").build());
        nom.getSubClassifiers().add(ClassifierDTO.builder().name("Taurus").description("Desc Taurus").build());

        mockMvc.perform(post("/classifiers/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andExpect(jsonPath("$.subClassifiers", hasSize(2)))
                .andExpect(jsonPath("$.subClassifiers[0].name", is("Aries")))
                .andExpect(jsonPath("$.subClassifiers[0].description", is("Desc Aries")))
                .andExpect(jsonPath("$.subClassifiers[1].name", is("Taurus")))
                .andExpect(jsonPath("$.subClassifiers[1].description", is("Desc Taurus")))
                .andReturn();
    }

    @Test
    void givenClassifierWhenAddNext_thenReturnClassifierDTOWithAdditionalTranslation() throws Exception {
        ClassifierDTO nom = ClassifierDTO.builder().name("Months").description("Desc Months").build();

        MvcResult result = mockMvc.perform(post("/classifiers/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ClassifierDTO dto = objectMapper.readValue(content, ClassifierDTO.class);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(nom.getName(), dto.getName());
        Assertions.assertEquals(nom.getDescription(), dto.getDescription());
        dto.setName("Месеци");
        dto.setDescription("Описание");

        mockMvc.perform(post("/classifiers/{code}/add-next", dto.getCode())
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
    void givenClassifierCode_whenFindByCode_thenReturnClassifier() throws Exception {
        ClassifierDTO nom = ClassifierDTO.builder().name("Months").description("Desc Months").build();

        MvcResult result = mockMvc.perform(post("/classifiers/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ClassifierDTO dto = objectMapper.readValue(content, ClassifierDTO.class);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(nom.getName(), dto.getName());
        Assertions.assertEquals(nom.getDescription(), dto.getDescription());

        mockMvc.perform(get("/classifiers/{code}", dto.getCode())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(dto.getCode())))
                .andDo(print())
                .andReturn();
    }

    @Test
    void whenFindAllParents_thenReturnAllParentClassifiers() throws Exception {
        ClassifierDTO nom = ClassifierDTO.builder().name("Months").description("Desc Months").build();

        mockMvc.perform(post("/classifiers/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        mockMvc.perform(get("/classifiers/").header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", greaterThan(0)))
                .andReturn();
    }

    @Test
    void givenClassifierWhenUpdate_thenReturnUpdatedClassifier() throws Exception {
        ClassifierDTO nom = ClassifierDTO.builder().name("Months").description("Desc Months").build();

        MvcResult result = mockMvc.perform(post("/classifiers/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ClassifierDTO dto = objectMapper.readValue(content, ClassifierDTO.class);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(nom.getName(), dto.getName());
        Assertions.assertEquals(nom.getDescription(), dto.getDescription());

        mockMvc.perform(put("/classifiers/{parentCode}", dto.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .content(objectMapper.writeValueAsString(
                                ClassifierDTO.builder()
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