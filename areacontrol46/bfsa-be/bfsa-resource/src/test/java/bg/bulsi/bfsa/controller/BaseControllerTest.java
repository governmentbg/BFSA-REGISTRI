package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.ResourceApplication;
import bg.bulsi.bfsa.dto.CountryDTO;
import bg.bulsi.bfsa.dto.SettlementDTO;
import bg.bulsi.bfsa.enums.PlaceType;
import bg.bulsi.bfsa.enums.TSB;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles({"test", "h2", "h2-unit-test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ComponentScan(basePackages = {"bg.bulsi.bfsa"})
@WithMockUser(authorities = {RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_FINANCE, RolesConstants.ROLE_EXPERT})
@SpringBootTest(classes = ResourceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseControllerTest {
    // base controller test class

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    protected static final Long FAKE_VALUE = 0L;

    protected static final Language langBG = Language.builder().languageId(Language.BG).build();
    protected static final Language langEN = Language.builder().languageId(Language.EN).build();

    public SettlementDTO createSettlement() throws Exception {
        CountryDTO mockCountryDTO = mockCountryDTO();

        MvcResult result = mockMvc.perform(post("/countries/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                        .content(objectMapper.writeValueAsString(mockCountryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(mockCountryDTO.getName())))
                .andExpect(jsonPath("$.description", is(mockCountryDTO.getDescription())))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        CountryDTO savedCountryDTO = objectMapper.readValue(content, CountryDTO.class);
        Assertions.assertNotNull(savedCountryDTO);
        Assertions.assertEquals(savedCountryDTO.getName(), savedCountryDTO.getName());
        Assertions.assertEquals(savedCountryDTO.getDescription(), savedCountryDTO.getDescription());

        SettlementDTO newSettlementDTO = mockSettlementDTO(savedCountryDTO.getCode());

        result = mockMvc.perform(post("/settlements/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSettlementDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(newSettlementDTO.getName())))
                .andExpect(jsonPath("$.nameLat", is(newSettlementDTO.getNameLat())))
                .andReturn();

        content = result.getResponse().getContentAsString();
        SettlementDTO savedSettlementDTO = objectMapper.readValue(content, SettlementDTO.class);
        Assertions.assertNotNull(savedSettlementDTO);
        Assertions.assertEquals(newSettlementDTO.getName(), savedSettlementDTO.getName());
        Assertions.assertEquals(newSettlementDTO.getNameLat(), savedSettlementDTO.getNameLat());

        return savedSettlementDTO;

    }

    public CountryDTO mockCountryDTO() {
        return CountryDTO.builder()
                .code(UUID.randomUUID().toString().substring(0, 10))
                .isoAlpha3(UUID.randomUUID().toString().substring(0, 10))
                .continent(UUID.randomUUID().toString().substring(0, 10))
                .currencyCode(UUID.randomUUID().toString().substring(0, 10))
                .name(UUID.randomUUID().toString().substring(0,10))
                .description(UUID.randomUUID().toString())
                .enabled(true)
                .build();
    }

    public SettlementDTO mockSettlementDTO(final String countryCode) {
        return SettlementDTO.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString())
                .nameLat(UUID.randomUUID().toString())
                .enabled(true)
                .regionCode(UUID.randomUUID().toString())
                .municipalityCode(UUID.randomUUID().toString())
                .placeType(PlaceType.CITY.name())
                .tsb(TSB.SE.name())
                .countryCode(countryCode)
                .build();
    }

    public SettlementDTO mockSettlementDTO() {
        return SettlementDTO.builder()
                .code(UUID.randomUUID().toString())
                .name(UUID.randomUUID().toString().substring(0, 10))
                .nameLat(UUID.randomUUID().toString().substring(0, 10))
                .regionCode(UUID.randomUUID().toString().substring(0, 10))
                .regionName(UUID.randomUUID().toString().substring(0, 10))
                .regionNameLat(UUID.randomUUID().toString().substring(0, 10))
                .municipalityName(UUID.randomUUID().toString().substring(0, 10))
                .municipalityNameLat(UUID.randomUUID().toString().substring(0, 10))
                .placeType(PlaceType.CITY.name())
                .tsb(TSB.SE.name())
                .parentCode(UUID.randomUUID().toString().substring(0, 10))
                .enabled(true)
                .countryCode(UUID.randomUUID().toString().substring(0, 10))
                .build();
    }
}
