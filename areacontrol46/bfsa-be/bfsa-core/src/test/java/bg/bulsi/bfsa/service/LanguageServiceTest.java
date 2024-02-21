package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.LanguageDTO;
import bg.bulsi.bfsa.model.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LanguageServiceTest extends BaseServiceTest {

    @Autowired
    private LanguageService languageService;

    @Test
    void givenLanguage_whenGetLanguageById_thenReturnLanguage() {
        Assertions.assertNotNull(languageService.findById("en"));
    }

    @Test
    void givenLanguage_whenGetLanguageByNotExistId_thenReturnNotFound() {
        RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, () -> languageService.findById(FAKE_VALUE));

        Assertions.assertEquals("Entity 'bg.bulsi.bfsa.model.User' with id/code='"
                + FAKE_VALUE + "' not found", thrown.getMessage());
    }

    @Test
    void givenLanguage_whenGetLanguageByNotExistId_thenReturnNull() {
        Assertions.assertNull(languageService.findByIdOrNull(FAKE_VALUE));
    }

    @Test
    void givenLanguageDto_whenCreateNewLanguage_thenFindItById() {
        LanguageDTO dto = LanguageDTO.builder().languageId("de").name("German").enabled(true).main(false).build();
        Language lang = languageService.create(LanguageDTO.to(dto));

        Assertions.assertNotNull(lang);
    }

    @Test
    void givenLanguageDtoWithoutId_whenTryToCreateNewLanguage_thenThrownIdIsRequired() {
        RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, () -> languageService.create(LanguageDTO
                        .to(LanguageDTO.builder().main(false).build()))
        );

        Assertions.assertEquals("Language id is required", thrown.getMessage());
    }

    @Test
    void givenLanguageDto_whenCreateNewLanguage_thenThrowError() {
        RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, () -> languageService.create(languageService.findById("en")));

        Assertions.assertEquals("Language with id en already exists", thrown.getMessage());
    }

    @Test
    void giveLanguageDtoWithEnabledMain_whenCreateNewLanguage_thenReplaceMainLanguage() {
        Language lang = languageService.create(LanguageDTO.to(LanguageDTO.builder()
                .languageId("uk")
                .main(true)
                .name("UK English")
                .build()));

        Assertions.assertNotNull(lang);
        Assertions.assertTrue(lang.getMain());
        Assertions.assertNotNull(languageService.findById(lang.getLanguageId()));
    }

    @Test
    void givenLanguageDto_whenUpdateLanguageWithFakeId_thenReturnErrorNotEqualId() {
        RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, () -> languageService.update(FAKE_VALUE,
                        LanguageDTO.builder().languageId("en").build())
        );

        Assertions.assertEquals("Language id is not equal to dto language id", thrown.getMessage());
    }

    @Test
    void givenLanguageDto_whenUpdateLanguage_thenReturnErrorChooseMainLanguage() {

        LanguageDTO updateInfo = LanguageDTO.builder()
                .languageId("bg")
                .main(false)
                .description("Updated Language")
                .enabled(false).build();

        RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, () -> languageService.update(languageService.findById("bg").getLanguageId(), updateInfo)
        );
        Assertions.assertEquals("Choose another language as main language before updating this one",
                thrown.getMessage());
    }

    @AfterEach
    void resetLanguages() {
        LanguageDTO dto = LanguageDTO.builder()
                .languageId("bg")
                .enabled(true)
                .main(true).build();
        languageService.update("bg", dto);
    }

}