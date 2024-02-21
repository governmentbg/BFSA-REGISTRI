package bg.bulsi.bfsa.service;


import bg.bulsi.bfsa.dto.PlantProtectionProductDTO;
import bg.bulsi.bfsa.model.PlantProtectionProduct;
import bg.bulsi.bfsa.model.PlantProtectionProductI18n;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
class PlantProtectionProductServiceTest extends BaseServiceTest {
	
    @Autowired
    private PlantProtectionProductBuilder builder;
    
    @Autowired
    private PlantProtectionProductService service;


	/*
	 * testFindById() {
	 */
    @Test
    void givenId_whenFinaById_thenReturnPlantProtectionProduct() {
    	PlantProtectionProduct mockProd = builder.mockPlantProtectionProduct(Math.random() * 100);
        Assertions.assertNotNull(mockProd);

        PlantProtectionProductDTO saved = PlantProtectionProductDTO.of(service.create(mockProd), langBG);
        Assertions.assertNotNull(saved);

        PlantProtectionProductDTO found = PlantProtectionProductDTO.of(service.findById(saved.getId()), langBG);

        Assertions.assertNotNull(found);
        Assertions.assertEquals(saved.getId(), found.getId());
        Assertions.assertEquals(saved.getQuantity(), found.getQuantity());
        Assertions.assertEquals(saved.getName(), found.getName());
    }
    
    
	@Test
	void testFindByIdOrNull() {
		assertFalse(false, "Not yet implemented");
	}


	/*
	 * testFindAll() {
	 */
    @Test
    void givenSomeEntities_whenFindAll_thenReturnPageOfAllEntities() {
        List<PlantProtectionProduct> list = builder.getList(3);
        Assertions.assertNotEquals(0,list.size());
        
        for (PlantProtectionProduct plantProtectionProduct: list) {
			PlantProtectionProduct saved = service.create(plantProtectionProduct);
			Assertions.assertNotNull(saved);
		}
        
        Page<PlantProtectionProduct> savedList = service.findAll(Pageable.unpaged());

        Assertions.assertNotNull(savedList);
        Assertions.assertNotNull(savedList.getContent());
        Assertions.assertTrue(savedList.getContent().size() > 1);
    }

	/*
	 * testCreate() {
	 */
    @Test
    void givenEntity_whenCreate_thenReturnEntity() {
    	PlantProtectionProduct entity = builder.mockPlantProtectionProduct(100.5);
        Assertions.assertNotNull(entity);

        PlantProtectionProduct saved = service.create(entity);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(entity.getQuantity(), saved.getQuantity());
        Assertions.assertEquals(entity.getI18n(langBG).getName(), saved.getI18n(langBG).getName());
        Assertions.assertEquals(entity.getI18n(langBG).getActiveSubstances(), saved.getI18n(langBG).getActiveSubstances());
        Assertions.assertEquals(entity.getI18n(langBG).getPurpose(), saved.getI18n(langBG).getPurpose());
        Assertions.assertEquals(entity.getI18n(langBG).getPest(), saved.getI18n(langBG).getPest());
        Assertions.assertEquals(entity.getI18n(langBG).getCrop(), saved.getI18n(langBG).getCrop());
        Assertions.assertEquals(entity.getI18n(langBG).getApplication(), saved.getI18n(langBG).getApplication());
    }

	@Test
	void testUpdate() {
		assertFalse(false, "Not yet implemented");
	}


	/*
	 * testSearch()
	 */
	@Test
	void givenString_whenSearch_thenReturnEntity() {
		PlantProtectionProduct prod1 = builder.mockPlantProtectionProduct(100.0);
		PlantProtectionProduct prod1saved = service.create(prod1);
		PlantProtectionProduct prod2 = builder.mockPlantProtectionProduct(50.0);
		PlantProtectionProduct prod2saved = service.create(prod2);
		PlantProtectionProductDTO dto1 = PlantProtectionProductDTO.of(prod1saved, langBG);
		PlantProtectionProductDTO dto2 = PlantProtectionProductDTO.of(prod2saved, langBG);
		
        Page<PlantProtectionProduct> found = service.search("Name", langBG, Pageable.unpaged());

        Assertions.assertNotNull(found);
        Assertions.assertEquals(dto1.getName(), 
        		((PlantProtectionProductI18n) found.getContent().get(0).getI18ns().toArray()[0]).getName());
	}


	@Test
	void testFindRevisions() {
		assertFalse(false, "Not yet implemented");
	}


	@Test
	void testPlantProtectionProductService() {
		assertFalse(false, "Not yet implemented");
	}
    
}