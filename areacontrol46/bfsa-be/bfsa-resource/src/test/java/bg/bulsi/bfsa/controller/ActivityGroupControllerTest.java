package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ActivityGroupDTO;
import bg.bulsi.bfsa.dto.NomenclatureDTO;
import bg.bulsi.bfsa.model.ActivityGroup;
import bg.bulsi.bfsa.model.ActivityGroupI18n;
import bg.bulsi.bfsa.model.Nomenclature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class ActivityGroupControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String NAME_EN_100 = "NAME_100";
    private static final String NAME_BG_100 = "ИМЕ_100";
    private static final String DESC_EN_100 = "DESC_100";
    private static final String DESC_BG_100 = "ОПИСАНИЕ_100";
    private static final String NAME_EN_101 = "NAME_101";
    private static final String NAME_BG_101 = "ИМЕ_101";
    private static final String DESC_EN_101 = "DESC_101";
    private static final String DESC_BG_101 = "ОПИСАНИЕ_101";

    @Test
    void givenGroup_whenCreate_thenReturnNewGroup() throws Exception {
        ActivityGroup group = mockGroup();

        mockMvc.perform(post("/activity-groups/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg")
                        .content(objectMapper.writeValueAsString(ActivityGroupDTO.of(group, langBG))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(group.getI18n(langBG).getName())))
                .andExpect(jsonPath("$.description", is(group.getI18n(langBG).getDescription())))
                .andReturn();

        mockMvc.perform(post("/activity-groups/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg")
                        .content(objectMapper.writeValueAsString(ActivityGroupDTO.of(group, langEN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(group.getI18n(langEN).getName())))
                .andExpect(jsonPath("$.description", is(group.getI18n(langEN).getDescription())))
                .andReturn();
    }

    @Test
    void givenGroupWithSubGroupsDto_whenFindAll_thenReturnAllGroups() throws Exception {
        ActivityGroupDTO groupDTO = ActivityGroupDTO.of(mockGroupWithSubGroups(), langBG);
        createGroup(groupDTO);

        mockMvc.perform(get("/activity-groups")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", greaterThan(0)))
                .andReturn();
    }

    @Test
    void givenGroupWithSubGroupsDto_whenFindAllGroupTypeVO_thenReturnAllGroupTypeVO() throws Exception {
        ActivityGroupDTO groupDTO = ActivityGroupDTO.of(mockGroupWithSubGroups(), langBG);
        createGroup(groupDTO);

        mockMvc.perform(get("/activity-groups/parents")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", greaterThan(0)))
                .andReturn();
    }

    @Test
    void givenGroupDto_WhenUpdate_thenReturnUpdatedGroup() throws Exception {
        ActivityGroupDTO dto = ActivityGroupDTO.of(mockGroup(), langBG);
        MvcResult result = createGroup(dto);

        String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        final ActivityGroupDTO savedGroup = objectMapper.readValue(content, ActivityGroupDTO.class);
        Assertions.assertNotNull(savedGroup);
        Assertions.assertEquals(dto.getName(), savedGroup.getName());
        Assertions.assertEquals(dto.getDescription(), savedGroup.getDescription());

        savedGroup.setName(savedGroup.getName() + "_Updated");
        savedGroup.setDescription(savedGroup.getDescription() + "_Updated");

        mockMvc.perform(put("/activity-groups/{id}", savedGroup.getId())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedGroup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(savedGroup.getName())))
                .andExpect(jsonPath("$.description", is(savedGroup.getDescription())))
                .andReturn();
    }

    private ActivityGroup mockGroup() throws Exception {
        ActivityGroup group = ActivityGroup.builder().enabled(true).build();
        Nomenclature nom = createNom();

        group.getI18ns().add(new ActivityGroupI18n(NAME_EN_100 + UUID.randomUUID().toString().substring(0, 5),
                DESC_EN_100, group, langEN));
        group.getI18ns().add(new ActivityGroupI18n(NAME_BG_100 + UUID.randomUUID().toString().substring(0, 5),
                DESC_BG_100, group, langBG));

        List<Nomenclature> recs = new ArrayList<>();
        recs.add(createNom());
        recs.add(createNom());
        recs.add(createNom());
        group.getRelatedActivityCategories().addAll(recs);
        group.getAnimalSpecies().add(nom);
        group.getRemarks().add(nom);
        return group;
    }

    private ActivityGroup mockGroupWithSubGroups() throws Exception {
        ActivityGroup parent = mockGroup();

        ActivityGroup group = ActivityGroup.builder().parent(parent).enabled(true).build();
        group.getI18ns().add(new ActivityGroupI18n(NAME_EN_101 + UUID.randomUUID().toString().substring(0, 5),
                DESC_EN_101, group, langEN));
        group.getI18ns().add(new ActivityGroupI18n(NAME_BG_101 + UUID.randomUUID().toString().substring(0, 5),
                DESC_BG_101, group, langBG));
        parent.getSubActivityGroups().add(group);

        return parent;
    }

    private MvcResult createGroup(ActivityGroupDTO dto) throws Exception {
        return mockMvc.perform(post("/activity-groups/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", is(dto.getEnabled())))
                .andReturn();
    }

    private Nomenclature createNom() throws Exception {
        NomenclatureDTO nom = NomenclatureDTO.builder().name(UUID.randomUUID().toString().substring(0, 5))
                .description(UUID.randomUUID().toString().substring(0, 5)).build();

        MvcResult result = mockMvc.perform(post("/noms/create-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg")
                        .content(objectMapper.writeValueAsString(nom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(nom.getName())))
                .andExpect(jsonPath("$.description", is(nom.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Nomenclature type = objectMapper.readValue(content, Nomenclature.class);
        Assertions.assertNotNull(type);

        return type;
    }

}