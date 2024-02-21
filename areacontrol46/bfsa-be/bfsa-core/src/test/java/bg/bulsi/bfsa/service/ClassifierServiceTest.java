package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ClassifierDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.ClassifierI18n;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;

class ClassifierServiceTest extends BaseServiceTest {

    @Autowired
    private ClassifierService service;
    @Autowired
    private ClassifierBuilder builder;

    // It fills all Classifier table positions
//    @Test
//    void fillClassifierTable() {
//        for (int i = 0; i < 100000; i += 100) {
//            Classifier nomBuilder = builder.saveClassifierWithSubClassifiers(langBG);
//            if (i == 99800) {
//                System.out.println(nomBuilder.getCode());
//                System.out.println(i);
//            }
//        }
//    }

    @Test
    void givenClassifier_whenAddNext_thenReturnClassifierWithCorrectI18n() {

        Classifier classifier = builder.saveClassifier(langBG);

        ClassifierI18n classifierI18n = classifier.getI18n(langEN);

        classifierI18n.setName("xxxx");
        classifierI18n.setDescription("yyy");

        Classifier next = service.addNext(classifier.getCode(), ClassifierDTO.of(classifier, langEN), langEN);
        Assertions.assertNotNull(next);

        Assertions.assertEquals(classifier.getCode(), next.getCode());
        Assertions.assertEquals("xxxx", next.getI18n(langEN).getName());
        Assertions.assertEquals("yyy", next.getI18n(langEN).getDescription());
    }

    @Test
    void givenClassifier_whenAddNext_thenThrowExceptionClassifierCodeNotEqual() {

        Classifier Classifier = builder.saveClassifier(langBG);

        RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, () -> service.addNext("FAKE_CODE", ClassifierDTO.of(Classifier, langEN), langEN)
        );

        Assertions.assertEquals("Classifier code is not equal to dto code.", thrown.getMessage());
    }

    @Test
    void givenClassifier_whenCreate_thenReturnClassifier() {
        Classifier classifier = builder.mockClassifier(langEN);
        Assertions.assertNotNull(classifier);

        Classifier saved = service.createNext(classifier);
        Assertions.assertNotNull(saved);

        Assertions.assertEquals(classifier.getCode(), saved.getCode());
        Assertions.assertEquals(classifier.getI18n(langEN).getName(), saved.getI18n(langEN).getName());
        Assertions.assertEquals(classifier.getI18n(langEN).getDescription(), saved.getI18n(langEN).getDescription());
    }

    @Test
    void givenClassifierWithSubClassifiers_whenCreateNext_thenReturnClassifierWithSubClassifiers() {
        Classifier mockedClassifier = builder.mockClassifierWithSubClassifiers(langEN);
        Assertions.assertNotNull(mockedClassifier);

        Classifier classifier = service.createNext(mockedClassifier);
        Assertions.assertNotNull(classifier);

        Assertions.assertEquals(mockedClassifier.getCode(), classifier.getCode());
        Assertions.assertEquals(mockedClassifier.getI18n(langEN).getName(), classifier.getI18n(langEN).getName());
        Assertions.assertEquals(mockedClassifier.getI18n(langEN).getDescription(), classifier.getI18n(langEN).getDescription());
        Assertions.assertEquals(mockedClassifier.getSubClassifiers().get(0).getCode(), classifier.getSubClassifiers().get(0).getCode());
        Assertions.assertEquals(mockedClassifier.getSubClassifiers().get(0).getI18n(langEN).getName(),
                classifier.getSubClassifiers().get(0).getI18n(langEN).getName());
        Assertions.assertEquals(mockedClassifier.getSubClassifiers().get(0).getI18n(langEN).getDescription(),
                classifier.getSubClassifiers().get(0).getI18n(langEN).getDescription());
    }

    @Test
    void givenClassifier_whenFindByCode_thenReturnClassifier() {
        Classifier saved = builder.saveClassifier(langBG);
        Assertions.assertNotNull(saved);

        Classifier classifier = service.findByCode(saved.getCode());
        Assertions.assertNotNull(classifier);
        Assertions.assertEquals(saved.getCode(), classifier.getCode());
    }

    @Test
    void givenEmptyClassifiers_whenFindAllParents_thenReturnAllParentClassifiers() {
        Classifier classifier = builder.saveClassifierWithSubClassifiers(langBG);
        Assertions.assertNotNull(classifier);

        List<ClassifierDTO> classifiers = service.findAllParents(langBG);
        Assertions.assertNotNull(classifiers);

        Assertions.assertNotNull(classifiers.stream()
                .filter(Classifier -> classifier.getCode().equals(Classifier.getCode()))
                .findAny()
                .orElse(null));
    }

    @Test
    void givenClassifier_whenUpdate_thenReturnUpdatedClassifier() {
        Classifier classifier = builder.saveClassifier(langBG);
        Assertions.assertNotNull(classifier);

        Classifier updates = builder.mockClassifier("updated_name", "updated_desc", langBG);
        Assertions.assertNotNull(updates);

        ClassifierDTO updated = service.update(classifier.getCode(), ClassifierDTO.of(updates, langBG), langBG);
        Assertions.assertNotNull(updated);

        Assertions.assertEquals("updated_name", updated.getName());
        Assertions.assertEquals("updated_desc", updated.getDescription());
    }

    @Test
    void givenFakeCode_whenUpdate_thenThrowsEntityNotFoundException() {
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> service.update(
                        "FAKE_CODE", ClassifierDTO.builder().code("FAKE_CODE").build(), langEN)
        );

        Assertions.assertEquals(
                "Entity '" + Classifier.class.getName() + "' with id/code='" +
                        "FAKE_CODE" + "' not found", thrown.getMessage()
        );
    }

    @Test
    void givenEmptyClassifierObject_whenUpdate_thenThrowsNullPointerException() {
        Classifier classifier = builder.saveClassifier(langBG);

        TransactionSystemException thrown = Assertions.assertThrows(
                TransactionSystemException.class, () -> service.update(classifier.getCode(),
                        ClassifierDTO.builder().build(), langBG)
        );

        Assertions.assertEquals("Could not commit JPA transaction", thrown.getMessage());
    }
}