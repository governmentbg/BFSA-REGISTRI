package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.LanguageDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.repository.LanguageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class LanguageControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LanguageRepository repository;
    private Language langDE;

    @BeforeAll
    public void init() {
        if (!repository.existsById("de")) {
            langDE = repository.save(Language.builder()
                    .languageId("de")
                    .locale("de_DE.UTF-8,de_DE,de.utf,de_DE.utf")
                    .name("Deutsch")
                    .description("Deutsch")
                    .enabled(true).build());
        }
    }

    @Test
    void givenLanguageCode_whenFindById_thenReturnLanguageDTO() throws Exception {
        mockMvc.perform(get("/langs/{id}", langDE.getLanguageId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.languageId", is(langDE.getLanguageId())))
                .andExpect(jsonPath("$.name", is(langDE.getName())))
                .andExpect(jsonPath("$.description", is(langDE.getDescription())))
                .andExpect(jsonPath("$.enabled", is(langDE.getEnabled())))
                .andDo(print())
                .andReturn();
    }

    @Test
    void whenFindAll_thenReturnAllLanguageDTOs() throws Exception {
        mockMvc.perform(get("/langs/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", greaterThan(0)))
                .andReturn();
    }

    @Test
    void givenLanguageWhenCreate_thenReturnCreatedLanguageDTO() throws Exception {
        mockMvc.perform(post("/langs/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                LanguageDTO.builder()
                                        .languageId("fr")
                                        .name("Français")
                                        .description("Français")
                                        .locale("fr_FR")
                                        .main(false)
                                        .enabled(true).build()
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.languageId", is("fr")))
                .andExpect(jsonPath("$.name", is("Français")))
                .andExpect(jsonPath("$.description", is("Français")))
                .andReturn();
    }

    @Test
    void givenLanguageWhenUpdate_thenReturnUpdatedLanguageDTO() throws Exception {
        mockMvc.perform(put("/langs/{id}", langDE.getLanguageId())
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(
                                LanguageDTO.builder()
                                        .languageId(langDE.getLanguageId())
                                        .name(langDE.getName() + "-Updated")
                                        .description(langDE.getDescription() + "-Updated")
                                        .locale(langDE.getLocale() + "-Updated")
                                        .main(false)
                                        .enabled(true).build()
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.languageId", is(langDE.getLanguageId())))
                .andExpect(jsonPath("$.name", is(langDE.getName() + "-Updated")))
                .andExpect(jsonPath("$.description", is(langDE.getDescription() + "-Updated")))
                .andReturn();
    }
}