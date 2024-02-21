package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.MessageResourceDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.MessageResource;
import bg.bulsi.bfsa.repository.MessageResourceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class MessageResourceControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageResourceRepository repository;

    private static final String testCode = "bfsa.test.message";
    private static final String testMsgBg = "Тестово съобщение";
    private static final String testMsgBgUpdated = "Тестово съобщение обновено";
    private static final String testMsgEn = "Test message";
    private static final String testMsgEnUpdated = "Test message Updated";
    private static final Language langEN = Language.builder().languageId("en").build();
    private static final Language langBG = Language.builder().languageId("bg").build();
    private MessageResource testMsgResBg;
    private MessageResource testMsgResEn;

    @BeforeAll
    public void init() {
        if (!repository.existsById(MessageResource.MessageResourceIdentity.builder()
                .languageId(langEN.getLanguageId()).code(testCode).build())) {
            testMsgResBg = repository.save(new MessageResource(langBG.getLanguageId(), testCode, testMsgBg));
            testMsgResEn = repository.save(new MessageResource(langEN.getLanguageId(), testCode, testMsgEn));
        }
    }

    @Test
    void givenMessageResourceCode_whenFindByCode_thenReturnMessageResource() throws Exception {
        mockMvc.perform(get("/message-resources/{code}", testCode).header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(testCode)))
                .andExpect(jsonPath("$.message", is(testMsgBg)))
                .andExpect(jsonPath("$.languageId", is(langBG.getLanguageId())))
                .andDo(print())
                .andReturn();
    }

    @Test
    void whenFindAll_thenReturnAllMessageResources() throws Exception {
        mockMvc.perform(get("/message-resources/").header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", greaterThan(0)))
                .andReturn();

        mockMvc.perform(get("/message-resources/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", greaterThan(1)))
                .andReturn();
    }

    @Test
    void givenMessageResourceWhenUpdate_thenReturnUpdatedMessageResource() throws Exception {
        mockMvc.perform(put("/message-resources/{code}", testCode).header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(
                                MessageResourceDTO.builder()
                                        .code(testCode)
                                        .message(testMsgBgUpdated).build()
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(testCode)))
                .andExpect(jsonPath("$.message", is(testMsgBgUpdated)))
                .andExpect(jsonPath("$.languageId", is(langBG.getLanguageId())))
                .andReturn();

        mockMvc.perform(put("/message-resources/{code}", testCode).header(HttpHeaders.ACCEPT_LANGUAGE, langEN.getLanguageId())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(
                                MessageResourceDTO.builder()
                                        .code(testCode)
                                        .message(testMsgEnUpdated).build()
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(testCode)))
                .andExpect(jsonPath("$.message", is(testMsgEnUpdated)))
                .andExpect(jsonPath("$.languageId", is(langEN.getLanguageId())))
                .andReturn();
    }
}