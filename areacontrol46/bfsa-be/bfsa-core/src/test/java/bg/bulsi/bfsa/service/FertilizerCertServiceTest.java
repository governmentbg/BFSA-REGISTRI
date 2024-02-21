package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.FertilizerCertDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.FertilizerCert;
import bg.bulsi.bfsa.model.FertilizerCertI18n;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
class FertilizerCertServiceTest extends BaseServiceTest {
    @Autowired
    private FertilizerCertService service;
    @Autowired
    private ContractorBuilder contractorBuilder;
    @Autowired
    private NomenclatureBuilder nomenclatureBuilder;

    private static final String NEW_BG_NAME = "00100-ново-име";
    private static final String NEW_BG_DESC = "00100-ново-описание";
    private static final String FAKE_CODE = "FAKE_CODE";

    @Test
    void givenSomeFoodTypes_whenFinaAll_thenReturnAllFertilizerCerts() {
        List<FertilizerCert> saved = new ArrayList<>();
        saved.add(create());
        saved.add(create());
        saved.add(create());
        Assertions.assertNotNull(saved);

        Page<FertilizerCert> exist = service.findAll(Pageable.unpaged());
        Assertions.assertNotNull(exist);
        Assertions.assertTrue(saved.size() <= exist.getSize());
    }

    @Test
    void givenId_whenFinaById_thenReturnFertilizerCert() {
        List<FertilizerCert> saved = new ArrayList<>();
        saved.add(create());
        saved.add(create());
        saved.add(create());
        Assertions.assertNotNull(saved);

        FertilizerCert certifiedFertilizer = service.findById(saved.get(0).getId());
        Assertions.assertNotNull(certifiedFertilizer);
        Assertions.assertEquals(saved.get(0).getId(), certifiedFertilizer.getId());
    }

    @Test
    void givenFertilizerCert_whenUpdate_thenReturnUpdatedFertilizerCert() {
        FertilizerCertDTO dto = FertilizerCertDTO.of(create(), langBG);
        dto.setName(NEW_BG_NAME);
        dto.setDescription(NEW_BG_DESC);

        FertilizerCertDTO updated = service.update(dto.getId(), dto, langBG);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(dto.getName(), updated.getName());
        Assertions.assertEquals(dto.getDescription(), updated.getDescription());
    }

    @Test
    void givenFakeCode_whenUpdateFail_thenThrowsEntityNotFoundException() {
        Long fakeId = new Random().nextLong(1000);
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> service.update(
                        fakeId, FertilizerCertDTO.builder()
                                .id(fakeId).build(), langEN)
        );

        Assertions.assertEquals("Entity '"
                + FertilizerCert.class.getName() + "' with id/code='"
                + fakeId + "' not found",
                thrown.getMessage());
    }

    private FertilizerCert create() {
        FertilizerCert fertilizer = new FertilizerCert();

        fertilizer.setRegNumber(UUID.randomUUID().toString().substring(0, 5));
        fertilizer.setRegDate(LocalDate.now());
        fertilizer.setEntryDate(LocalDate.now());
        fertilizer.setValidUntilDate(LocalDate.now().plusYears(1L));
        fertilizer.setEdition(UUID.randomUUID().toString().substring(0, 5));
        fertilizer.setPh(6.5);
        fertilizer.setDose(5.7);
        fertilizer.setWaterAmount(4.6);
        fertilizer.setAppNumber(3);
        fertilizer.setOrderNumber(UUID.randomUUID().toString().substring(0, 5));
        fertilizer.setOrderDate(LocalDate.now());

        fertilizer.setFertilizerType(nomenclatureBuilder.saveNomenclature(langBG));
        fertilizer.setCertificateHolder(contractorBuilder.saveContractor());
        fertilizer.setManufacturer(contractorBuilder.saveContractor());

        fertilizer.getI18ns().add(new FertilizerCertI18n(
                UUID.randomUUID().toString().substring(0, 5),
                UUID.randomUUID().toString().substring(0, 5),
                UUID.randomUUID().toString().substring(0, 5),
                UUID.randomUUID().toString().substring(0, 5),
                UUID.randomUUID().toString().substring(0, 5),
                UUID.randomUUID().toString().substring(0, 5),
                UUID.randomUUID().toString().substring(0, 5),
                fertilizer,
                langBG));
        fertilizer.setEnabled(true);

        return service.create(fertilizer);
    }
}