package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.FoodSupplementDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.FoodSupplement;
import bg.bulsi.bfsa.model.FoodSupplementI18n;
import bg.bulsi.bfsa.service.FoodSupplementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FoodSupplementControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FoodSupplementService foodSupplementService;

    @Test
    void whenFindAll_thenReturnAllFoodSupplements() throws Exception {
        List<FoodSupplement> foodSupplements = new ArrayList<>();
        foodSupplements.add(mockFoodSupplement());
        foodSupplements.add(mockFoodSupplement());

        when(foodSupplementService.findAll(any())).thenReturn(new PageImpl<>(foodSupplements));

        mockMvc.perform(get("/food-supplements/")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", greaterThan(1)))
                .andReturn();
    }

    @Test
    void givenId_whenFindById_thenReturnFoodSupplement() throws Exception {
        FoodSupplement savedFoodSupplement = mockFoodSupplement();

        when(foodSupplementService.findById(any())).thenReturn(savedFoodSupplement);

        mockMvc.perform(get("/food-supplements/{id}", savedFoodSupplement.getId())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedFoodSupplement.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(savedFoodSupplement.getEnabled())))
                .andExpect(jsonPath("$.regNumber", is(savedFoodSupplement.getRegNumber())))
                .andReturn();
    }

    @Test
    void givenFakeId_whenFindById_thenThrowEntityNotFoundException() throws Exception {

        when(foodSupplementService.findById(0L)).thenThrow(new EntityNotFoundException(FoodSupplement.class, FAKE_VALUE));

        mockMvc.perform(get("/food-supplements/{id}", FAKE_VALUE)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception",
                        is("bg.bulsi.bfsa.exception.EntityNotFoundException")))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message",
                        is("Entity '" + FoodSupplement.class.getName() +"' with id/code='" + FAKE_VALUE + "' not found")))
                .andReturn();
    }

    @Test
    void givenFoodSupplement_whenCreate_thenReturnCreatedFoodSupplement() throws Exception {
        FoodSupplement savedFoodSupplement = mockFoodSupplement();

        when(foodSupplementService.create(any())).thenReturn(savedFoodSupplement);
        mockMvc.perform(post("/food-supplements/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FoodSupplementDTO.of(savedFoodSupplement, langBG)))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedFoodSupplement.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(savedFoodSupplement.getEnabled())))
                .andExpect(jsonPath("$.regNumber", is(savedFoodSupplement.getRegNumber())))
                .andReturn();
    }

    @Test
    void givenFoodSupplement_whenUpdate_thenReturnUpdatedFoodSupplement() throws Exception {
        FoodSupplement savedFoodSupplement = mockFoodSupplement();

        when(foodSupplementService.update(any(), any(), any())).thenReturn(savedFoodSupplement);
        savedFoodSupplement.setRegNumber(UUID.randomUUID().toString());

        mockMvc.perform(put("/food-supplements/{id}", savedFoodSupplement.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(FoodSupplementDTO.of(savedFoodSupplement, langBG)))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedFoodSupplement.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(savedFoodSupplement.getEnabled())))
                .andExpect(jsonPath("$.regNumber", is(savedFoodSupplement.getRegNumber())))
                .andReturn();
    }

    private FoodSupplement mockFoodSupplement() {
        FoodSupplement foodSupplement = FoodSupplement.builder()
                .id(new Random().nextLong(1000))
                .regNumber(UUID.randomUUID().toString())
                .regDate(LocalDate.now())
                .applicant(mockContractor())
                .enabled(true)
                .build();

        foodSupplement.getI18ns().add(
                new FoodSupplementI18n("име", "предназначение", "състав",
                        "описание", "име_компания", foodSupplement, langBG)
        );
        foodSupplement.getI18ns().add(
                new FoodSupplementI18n("name", "purpose", "ingredients",
                        "description","име_компания", foodSupplement, langEN)
        );
        return foodSupplement;
    }

    private Contractor mockContractor() {
        return Contractor.builder()
                .id(new Random().nextLong(1000))
                .fullName("Тест Тестов")
                .email("test@domain.123")
                .identifier("000000000")
                .password("000000000")
                .username("test@domain.123")
                .enabled(true)
                .build();
    }
}