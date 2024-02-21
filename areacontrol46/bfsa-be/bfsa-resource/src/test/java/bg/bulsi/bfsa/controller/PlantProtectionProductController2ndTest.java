package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.PlantProtectionProductDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.PlantProtectionProduct;
import bg.bulsi.bfsa.model.PlantProtectionProductI18n;
import bg.bulsi.bfsa.service.PlantProtectionProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class PlantProtectionProductController2ndTest extends BaseControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private PlantProtectionProductService service;

	private static final String NAME = "Name";
	private static final String ACTIVE_SUBSTANCE = "Substance";
	private static final String PURPOSE = "Purpose";
	private static final String PEST = "Pest";
	private static final String CROP = "Crop";
	private static final String APPLICATION = "App";

	/*
	 * testFindAll()
	 */
	@Test
	void whenFindAll_thenReturnAllEntityDTOList() throws Exception {
		int mockListSize = 3;

		List<PlantProtectionProduct> mockList = mockPlantProtectionProducts(mockListSize, langBG);

		assertEquals(mockList.size(), mockListSize);

		when(service.findAll(any())).thenReturn(new PageImpl<>(mockList));

		mockMvc.perform( get("/plant-protection-products/")
						.contentType(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()) )
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.size()", is(mockListSize)))
				.andReturn();
	}

	/*
	 * testGetById()
	 */
	@Test
	void givenId_whenFindById_thenReturnEntityDTO() throws Exception {
		PlantProtectionProduct mockEntity = mockPlantProtectionProduct(99.1, langBG);
		mockEntity.setId(new Random().nextLong(1000));
		when(service.findById(any())).thenReturn(mockEntity);

		mockMvc.perform( get("/plant-protection-products/{id}", mockEntity.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()) )
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(mockEntity.getId().intValue())))
				.andExpect(jsonPath("$.quantity", is(mockEntity.getQuantity())))
				.andReturn();
	}

	/*
	 * testSearch()
	 */
	@Test
	void givenSearchString_whenSearch_thenReturnAllEntitiesDTOs() throws Exception {
		int mockListSize = 3;
		List<PlantProtectionProduct> mockList = mockPlantProtectionProducts(mockListSize, langBG);
		assertEquals(mockList.size(), mockListSize);

		when(service.search(any(), any(), any())).thenReturn(new PageImpl<>(mockList));

		mockMvc.perform(get("/plant-protection-products?q={1}", "tes")
						.contentType(MediaType.APPLICATION_JSON)
						.header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content.size()", is(mockListSize)))
				.andReturn();
	}

	/*
	 * testCreate()
	 */
	@Test
	void givenEntity_whenCreate_thenReturnCreatedEntity() throws Exception {
		PlantProtectionProduct mockEntity = mockPlantProtectionProduct(99.1, langBG);
		mockEntity.setQuantity(mockEntity.getQuantity() + 100);

		PlantProtectionProduct mockEntitySaved = mockPlantProtectionProduct(99.1, langBG);
		mockEntitySaved.setId(new Random().nextLong(1000));
		mockEntitySaved.setQuantity(mockEntity.getQuantity() + 100);
		when(service.create(any())).thenReturn(mockEntitySaved);

		String objectMapped = objectMapper.writeValueAsString(PlantProtectionProductDTO.of(mockEntity, langBG));
		log.debug("PlantProtectionProductDTO Json: {}", objectMapped);

		mockMvc.perform(post("/plant-protection-products/create")
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
				.content(objectMapped))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(mockEntitySaved.getId().intValue())))
				.andExpect(jsonPath("$.quantity", is(mockEntitySaved.getQuantity())))
				.andReturn();
	}

	/*
	 * testUpdate()
	 */
	@Test
	void givenId_whenUpdate_thenReturnUpdatedEntity() throws Exception {
		PlantProtectionProduct mockEntity = mockPlantProtectionProduct(99.1, langBG);
		mockEntity.setId(new Random().nextLong(1000));
		mockEntity.setQuantity(mockEntity.getQuantity() + 100);
		when(service.update(any(), any(), any())).thenReturn(mockEntity);

		String objectMapped = objectMapper.writeValueAsString(PlantProtectionProductDTO.of(mockEntity, langBG));
		log.debug("PlantProtectionProductDTO Json: {}", objectMapped);

		mockMvc.perform(put("/plant-protection-products/{id}", mockEntity.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId())
				.content(objectMapped))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantity", is(mockEntity.getQuantity())))
				.andReturn();
	}

	@Test
	void testHistory() {
		assertFalse(false, "Not yet implemented");
	}

	public PlantProtectionProduct mockPlantProtectionProduct(Double quantity, Language language) {
		PlantProtectionProduct prod = PlantProtectionProduct.builder()
				.quantity(quantity)
				.build();

		prod.getI18ns().add(
				new PlantProtectionProductI18n(NAME, ACTIVE_SUBSTANCE, PURPOSE, PEST, CROP, APPLICATION, prod, language)
		);
		return prod;
	}

	public List<PlantProtectionProduct> mockPlantProtectionProducts(final int size, final Language language) {
		List<PlantProtectionProduct> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			PlantProtectionProduct plantProtectionProduct = mockPlantProtectionProduct(Math.random() * 100, language);
			list.add(plantProtectionProduct);
		}
		return list;
	}
}
