package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.NomenclatureDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.NomenclatureI18n;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;

class NomenclatureServiceTest extends BaseServiceTest {

    @Autowired
    private NomenclatureService nomenclatureService;
    @Autowired
    private NomenclatureBuilder builder;

    // It fills all nomenclature table positions
//    @Test
//    void fillNomenclatureTable() {
//        for (int i = 0; i < 100000; i += 100) {
//            Nomenclature nomBuilder = builder.saveNomenclatureWithSubNomenclatures(langBG);
//            if (i == 99800) {
//                System.out.println(nomBuilder.getCode());
//                System.out.println(i);
//            }
//        }
//    }

    @Test
    void givenNomenclature_whenAddNext_thenReturnNomenclatureWithCorrectI18n() {

        Nomenclature nom = builder.saveNomenclature(langBG);
        Assertions.assertNotNull(nom);

        NomenclatureI18n nomenclatureI18n = nom.getI18n(langEN);
        nomenclatureI18n.setName("xxxx");
        nomenclatureI18n.setDescription("yyy");

        Nomenclature nextNom = nomenclatureService.addNext(nom.getCode(), NomenclatureDTO.of(nom, langEN), langEN);
        Assertions.assertNotNull(nextNom);

        Assertions.assertEquals(nom.getCode(), nextNom.getCode());
        Assertions.assertEquals("xxxx", nextNom.getI18n(langEN).getName());
        Assertions.assertEquals("yyy", nextNom.getI18n(langEN).getDescription());
    }

    @Test
    void givenNomenclature_whenAddNext_thenThrowExceptionNomenclatureCodeNotEqual() {

        Nomenclature nomenclature = builder.saveNomenclature(langBG);

        RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, () -> nomenclatureService.addNext("FAKE_CODE", NomenclatureDTO.of(nomenclature, langEN), langEN)
        );

        Assertions.assertEquals("Nomenclature code is not equal to dto code.", thrown.getMessage());
    }

    @Test
    void givenNomenclature_whenCreate_thenReturnNomenclature() {
        Nomenclature nom = builder.mockNomenclature(langEN);
        Nomenclature nomenclature = nomenclatureService.createNext(nom);

        Assertions.assertNotNull(nomenclature);
        Assertions.assertEquals(nom.getCode(), nomenclature.getCode());
        Assertions.assertEquals(nom.getI18n(langEN).getName(), nomenclature.getI18n(langEN).getName());
        Assertions.assertEquals(nom.getI18n(langEN).getDescription(), nomenclature.getI18n(langEN).getDescription());
    }

    @Test
    void givenNomenclatureWithSubNomenclatures_whenCreateNext_thenReturnNomenclatureWithSubNomenclatures() {
        Nomenclature mockNomenclature = builder.mockNomenclatureWithSubNomenclatures(langEN);
        Nomenclature nom = nomenclatureService.createNext(mockNomenclature);

        Assertions.assertNotNull(nom);
        Assertions.assertEquals(mockNomenclature.getCode(), nom.getCode());
        Assertions.assertEquals(mockNomenclature.getI18n(langEN).getName(), nom.getI18n(langEN).getName());
        Assertions.assertEquals(mockNomenclature.getI18n(langEN).getDescription(), nom.getI18n(langEN).getDescription());
        Assertions.assertEquals(mockNomenclature.getSubNomenclatures().get(0).getCode(), nom.getSubNomenclatures().get(0).getCode());
        Assertions.assertEquals(mockNomenclature.getSubNomenclatures().get(0).getI18n(langEN).getName(),
                nom.getSubNomenclatures().get(0).getI18n(langEN).getName());
        Assertions.assertEquals(mockNomenclature.getSubNomenclatures().get(0).getI18n(langEN).getDescription(),
                nom.getSubNomenclatures().get(0).getI18n(langEN).getDescription());
    }

    @Test
    void givenNomenclature_whenFindByCode_thenReturnNomenclature() {
        Nomenclature nomBuilder = builder.saveNomenclature(langBG);
        Nomenclature nom = nomenclatureService.findByCode(nomBuilder.getCode());

        Assertions.assertNotNull(nom);
        Assertions.assertEquals(nomBuilder.getCode(), nom.getCode());
    }

    @Test
    void givenEmptyNomenclatures_whenFindAllParents_thenReturnAllParentNomenclatures() {
        Nomenclature nom = builder.saveNomenclatureWithSubNomenclatures(langBG);
        Assertions.assertNotNull(nom);

        List<NomenclatureDTO> nomenclatures = nomenclatureService.findAllParents(langBG);
        Assertions.assertNotNull(nomenclatures);

        Assertions.assertNotNull(nomenclatures.stream()
                .filter(nomenclature -> nom.getCode().equals(nomenclature.getCode()))
                .findAny()
                .orElse(null));
    }

    @Test
    void givenNomenclature_whenUpdate_thenReturnUpdatedNomenclature() {
        Nomenclature nomenclature = builder.saveNomenclature(langBG);
        Nomenclature updates = builder.mockNomenclature("updated_name", "updated_desc", langBG);

        NomenclatureDTO updated = nomenclatureService.update(nomenclature.getCode(), NomenclatureDTO.of(updates, langBG), langBG);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals("updated_name", updated.getName());
        Assertions.assertEquals("updated_desc", updated.getDescription());
    }

    @Test
    void givenFakeCode_whenUpdate_thenThrowsEntityNotFoundException() {
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> nomenclatureService.update(
                        "FAKE_CODE", NomenclatureDTO.builder().code("FAKE_CODE").build(), langEN)
        );

        Assertions.assertEquals(
                "Entity '" + Nomenclature.class.getName() + "' with id/code='" +
                        "FAKE_CODE" + "' not found", thrown.getMessage()
        );
    }

    @Test
    void givenEmptyNomenclatureObject_whenUpdate_thenThrowsNullPointerException() {
        Nomenclature nomenclature = builder.saveNomenclature(langBG);

        TransactionSystemException thrown = Assertions.assertThrows(
                TransactionSystemException.class, () -> nomenclatureService.update(nomenclature.getCode(),
                        NomenclatureDTO.builder().build(), langBG)
        );

        Assertions.assertEquals("Could not commit JPA transaction", thrown.getMessage());
    }
}