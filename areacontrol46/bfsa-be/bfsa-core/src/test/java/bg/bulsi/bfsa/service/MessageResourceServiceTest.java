package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.MessageResourceDTO;
import bg.bulsi.bfsa.model.MessageResource;
import bg.bulsi.bfsa.repository.MessageResourceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertAll;

class MessageResourceServiceTest extends BaseServiceTest {
    @Autowired
    private MessageResourceRepository messageResourceRepository;
    @Autowired
    MessageResourceService service;
    private static final String TEST_CODE = "bfsa.test.message";
    private static final String TEST_MSG_BG = "Тестово съобщение";
    private static final String TEST_MSG_EN = "Test message";
    public static final String FAKE_VALUE = "fake";
    private MessageResource testMsgResBg;
    private MessageResource testMsgResEn;
    private MessageResource.MessageResourceIdentity messageIdentityBg;
    private MessageResource.MessageResourceIdentity messageIdentityEn;


    @BeforeEach
    public void init() {
        if (!messageResourceRepository.existsById(MessageResource.MessageResourceIdentity.builder()
                .languageId(langEN.getLanguageId()).code(TEST_CODE).build())) {

            messageIdentityBg = MessageResource.MessageResourceIdentity.builder().code(TEST_CODE).languageId("bg").build();
            messageIdentityEn = MessageResource.MessageResourceIdentity.builder().code(TEST_CODE).languageId("en").build();

            testMsgResBg = messageResourceRepository.save(new MessageResource(messageIdentityBg, TEST_MSG_BG));
            testMsgResEn = messageResourceRepository.save(new MessageResource(messageIdentityEn, TEST_MSG_EN));
        }
    }

    @Test
    void givenMessageResourceIdentity_whenFindById_thenReturnMessageResource() {
        MessageResource messageResource = service.findById(messageIdentityBg);
        assertAll(
                () -> Assertions.assertEquals(testMsgResBg.getMessage(), messageResource.getMessage()),

                () -> Assertions.assertEquals(testMsgResBg.getMessageResourceIdentity().getCode()
                        , messageResource.getMessageResourceIdentity().getCode()),

                () -> Assertions.assertEquals(testMsgResBg.getMessageResourceIdentity().getLanguageId(),
                        messageResource.getMessageResourceIdentity().getLanguageId())
        );
    }

    @Test
    void givenMessageCodeAndLanguage_whenGet_thenReturnMessage() {
        Assertions.assertEquals(testMsgResBg.getMessage(), service.get(TEST_CODE, langBG));
    }

    @Test
    void givenMessageCodeAndLanguageId_whenGet_thenReturnMessage() {
        Assertions.assertEquals(testMsgResBg.getMessage(), service.get(TEST_CODE, langBG.getLanguageId()));
    }

    @Test
    void givenMessageResourceIdentity_whenFindByExistId_thenReturnMessageResource() {
        MessageResource messageResource = service.findByIdOrNull(messageIdentityBg);
        assertAll(
                () -> Assertions.assertEquals(testMsgResBg.getMessage(), messageResource.getMessage()),

                () -> Assertions.assertEquals(testMsgResBg.getMessageResourceIdentity().getCode()
                        , messageResource.getMessageResourceIdentity().getCode()),

                () -> Assertions.assertEquals(testMsgResBg.getMessageResourceIdentity().getLanguageId(),
                        messageResource.getMessageResourceIdentity().getLanguageId())
        );
    }

    @Test
    void givenMessageResourceIdentity_whenFindByNotExistId_thenReturnNull() {
        MessageResource.MessageResourceIdentity messageIdentityFAKE =
                MessageResource.MessageResourceIdentity.builder().code(FAKE_VALUE).languageId(langBG.getLanguageId()).build();

        Assertions.assertNull(service.findByIdOrNull(messageIdentityFAKE));
    }

    @Test
    void givenMessageResources_whenFindAll_thenReturnMessageResourceList() {
        Assertions.assertTrue(service.findAll()
                .stream()
                .map(MessageResource::getMessage)
                .toList().contains(testMsgResBg.getMessage()));
    }

    @Test
    void givenMessageResources_whenFindAllBySpecificLangId_thenReturnAllMessageResourcesWithThisLangId() {
        Assertions.assertFalse(service.findAll(langEN.getLanguageId(), Pageable.unpaged())
                .stream()
                .map(MessageResource::getMessage)
                .toList().contains(testMsgResBg.getMessage()));

    }

    @Test
    void givenMessageResourceDto_whenUpdateMessageResource_ThenReturnUpdatedMessageResource() {

        MessageResourceDTO dto = MessageResourceDTO.builder()
                .message("Updated Test Message")
                .languageId(langEN.getLanguageId())
                .code(TEST_CODE).build();

        Assertions.assertEquals("Updated Test Message",
                service.update(messageIdentityEn, dto).getMessage());
    }

    @Test
    void givenMessageResourceDto_whenTryToUpdateMessageResourceWithWrongLangId_ThenReturnError() {

        MessageResourceDTO dto = MessageResourceDTO.builder()
                .message("Updated Test Message")
                .languageId(FAKE_VALUE)
                .code(FAKE_VALUE).build();

        RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, () -> service.update(messageIdentityEn, dto));

        Assertions.assertEquals("Language id is not equal to dto language id", thrown.getMessage());
    }
}