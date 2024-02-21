package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.FoodSupplementDTO;
import bg.bulsi.bfsa.model.FoodSupplement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Slf4j
class FoodSupplementServiceTest extends BaseServiceTest {
    @Autowired
    private FoodSupplementService service;
    @Autowired
    private FoodSupplementBuilder foodSupplementBuilder;
    @Autowired
    private NomenclatureBuilder nomenclatureBuilder;

    private static final String NEW_BG_NAME = "00100-ново-име";
    private static final String NEW_BG_DESC = "00100-ново-описание";
    private static final String FAKE_CODE = "FAKE_CODE";

    @Test
    void givenFoodSupplement_whenCreate_thenReturnFoodSupplement() {
        FoodSupplement supplement = foodSupplementBuilder.getFoodSupplement(nomenclatureBuilder.saveNomenclature(langBG));
        FoodSupplement saved = service.create(supplement);

        Assertions.assertNotNull(supplement);
        Assertions.assertNotNull(saved);
    }

    @Test
    void givenSomeFoodSupplements_whenFinaAll_thenReturnPageOfAllFoodSupplements() {
        List<FoodSupplement> foodSupplements = foodSupplementBuilder.saveFoodSupplements(nomenclatureBuilder.saveNomenclature(langBG));
        Page<FoodSupplement> saved = service.findAll(Pageable.unpaged());

        Assertions.assertNotNull(saved.getContent());
        Assertions.assertTrue(foodSupplements.size() <= saved.getSize());
    }

    @Test
    void givenId_whenFinaById_thenReturnFoodSupplement() {
        List<FoodSupplement> foodSupplements = foodSupplementBuilder.saveFoodSupplements(nomenclatureBuilder.saveNomenclature(langBG));
        FoodSupplement supplement = service.findById(foodSupplements.get(0).getId());

        Assertions.assertNotNull(supplement);
        Assertions.assertEquals(foodSupplements.get(0).getId(), supplement.getId());
    }

    @Test
    void givenFoodSupplement_whenUpdate_thenReturnUpdatedFoodSupplement() {
        FoodSupplementDTO dto = FoodSupplementDTO.of(foodSupplementBuilder.saveFoodSupplement(nomenclatureBuilder.saveNomenclature(langBG)), langBG);
        dto.setName(NEW_BG_NAME);
        dto.setDescription(NEW_BG_DESC);

        FoodSupplement updated = service.update(dto.getId(), dto, langBG);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(dto.getName(), updated.getI18n(langBG).getName());
        Assertions.assertEquals(dto.getPurpose(), updated.getI18n(langBG).getPurpose());
    }

//    @Test
//    void givenFakeCode_whenUpdate_thenThrowsEntityNotFoundException() {
//        Long fakeId = new Random().nextLong(1000);
//        EntityNotFoundException thrown = Assertions.assertThrows(
//                EntityNotFoundException.class, () -> service.update(
//                        fakeId, FoodSupplementDTO.builder().id(fakeId).build(), langEN)
//        );
//
//        Assertions.assertEquals("Entity '"
//                + FoodSupplement.class.getName() + "' with id/code='"
//                + fakeId + "' not found",
//                thrown.getMessage());
//    }

    @Test
    void givenString_whenSearch_thenReturnFoodSupplement() {
        var saved = foodSupplementBuilder.saveFoodSupplements(nomenclatureBuilder.saveNomenclature(langBG));
        String searchString = FoodSupplementBuilder.TEST_NAME_BG;
        Page<FoodSupplement> result = service.search(searchString, langBG, Pageable.unpaged());

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.stream().count() >= saved.size());
    }
}