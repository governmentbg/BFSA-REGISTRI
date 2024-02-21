package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.SettlementDTO;
import bg.bulsi.bfsa.dto.SettlementVO;
import bg.bulsi.bfsa.enums.PlaceType;
import bg.bulsi.bfsa.enums.TSB;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Settlement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
class SettlementServiceTest extends BaseServiceTest {

    private static final String NAME_BLG_BG_NEW = "Париж";
    private static final String NAME_BLG_EN_NEW = "Paris";

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private SettlementBuilder settlementBuilder;

    @Test
    void givenCode_whenFindByCode_thenReturnTheSettlement() {
        Settlement s = settlementBuilder.saveSettlement();
        SettlementDTO dto = settlementService.findByCodeDto(s.getCode());

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(s.getCode(), dto.getCode());
        Assertions.assertEquals(s.getName(), dto.getName());
        Assertions.assertEquals(s.getNameLat(), dto.getNameLat());
    }

    @Test
    void whenFindAllParents_thenReturnListWithSettlementParents() {
        List<Settlement> settlements = settlementBuilder.saveSettlements();
        List<SettlementDTO> list = settlementService.findAllParents();

        Assertions.assertNotNull(list);
        Assertions.assertTrue(settlements.size() > 0);
        Assertions.assertTrue(settlements.size() <= list.size());
    }

    @Test
    void whenFindAllSettlementVO_thenReturnListSettlementVOs() {
        List<Settlement> settlements = settlementBuilder.saveSettlements();
        List<SettlementVO> list = settlementService.findAllSettlementVO();

        Assertions.assertNotNull(list);
        Assertions.assertTrue(settlements.size() > 0);
        Assertions.assertTrue(settlements.size() <= list.size());
    }

    @Test
    void givenFakeCode_whenFindByIdOrNull_thenReturnNull() {
        Settlement settlement = settlementService
                .findByCodeOrNull(UUID.randomUUID().toString());
        Assertions.assertNull(settlement);
    }

    @Test
    void givenSettlementWithSubSettlements_whenCreate_thenReturnNewSettlementWithSubSettlements() {
        final Country country = settlementBuilder.saveCountry();
        final Settlement settlement = settlementBuilder.mockSettlementWithSubSettlements(country);
        final Settlement saved = settlementService.create(settlement);

        Assertions.assertNotNull(saved);
        Assertions.assertEquals(saved.getCode(), settlement.getCode());
        Assertions.assertTrue( 0 < saved.getSubSettlements().size());
        Assertions.assertTrue(settlement.getSubSettlements().size() <= saved.getSubSettlements().size());
    }

    @Test
    void givenSettlement_whenUpdate_thenReturnUpdatedSettlement() {
        SettlementDTO dto = SettlementDTO.of(settlementBuilder.saveSettlement());
        dto.setName(NAME_BLG_BG_NEW);
        dto.setNameLat(NAME_BLG_EN_NEW);
        dto.setEnabled(false);
        dto.setRegionCode(UUID.randomUUID().toString());
        dto.setMunicipalityCode(UUID.randomUUID().toString());
        dto.setPlaceType(PlaceType.RESORT.name());
        dto.setTsb(TSB.NW.name());

        Settlement updated = settlementService.update(dto.getCode(), dto);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(NAME_BLG_BG_NEW, updated.getName());
        Assertions.assertEquals(NAME_BLG_EN_NEW, updated.getNameLat());
        Assertions.assertEquals(dto.getRegionCode(), updated.getRegionCode());
        Assertions.assertEquals(dto.getMunicipalityCode(), updated.getMunicipalityCode());
    }

    @Test
    void givenFakeCode_whenUpdate_thenThrowsEntityNotFoundException() {
        String code = UUID.randomUUID().toString();
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> settlementService.update(
                        code, SettlementDTO.builder()
                                .code(code)
                                .municipalityCode(UUID.randomUUID().toString())
                                .placeType(PlaceType.CITY.name())
                                .tsb(TSB.NE.name())
                                .build()));

        Assertions.assertEquals("Entity '"
                + Settlement.class.getName() + "' with id/code='"
                + code + "' not found",
                thrown.getMessage());
    }

    @Test
    void givenSettlementDTOAndEN_whenGetInfo_thenReturnExpectedString(){
        SettlementDTO dto = SettlementDTO.of(settlementBuilder.saveSettlement());

        Assertions.assertEquals(
                "c. Благоевград, mun. BLG03, reg. BLG",
                settlementService.getInfo(dto.getCode(), Language.EN));
    }

    @Test
    void givenSettlementDTOAndBG_whenGetInfo_thenReturnExpectedString(){
        SettlementDTO dto = SettlementDTO.of(settlementBuilder.saveSettlement());

        Assertions.assertEquals(
                "гр. Благоевград, общ. BLG03, обл. BLG",
                settlementService.getInfo(dto.getCode(), Language.BG));
    }

    @Test
    void givenSettlementDTO_whenChangeTypeAndGetInfo_thenReturnExpectedString(){
        SettlementDTO dto = SettlementDTO.of(settlementBuilder.saveSettlement());
        dto.setPlaceType("VILLAGE");
        Settlement updated = settlementService.update(dto.getCode(), dto);

        Assertions.assertEquals(
                "с. Благоевград, общ. BLG03, обл. BLG",
                settlementService.getInfo(updated.getCode(), Language.BG));
    }

    @Test
    void givenSettlementList_whenFindAllRegionSettlements_thenReturnCorrectListOrderOfSettlements(){
        List<Settlement> settlements = new ArrayList<>();
        settlements.add(settlementBuilder.saveSettlement());
        settlements.add(settlementBuilder.saveSettlement());
        settlements.add(settlementBuilder.saveSettlement());

        settlements.get(1).setName("София");
        settlements.get(1).setRegionCode("SOF");
        settlementService.update(settlements.get(1).getCode(), SettlementDTO.of(settlements.get(1)));
        settlements.get(2).setName("Бистрица");
        settlements.get(2).setRegionCode("SOF");
        settlementService.update(settlements.get(2).getCode(), SettlementDTO.of(settlements.get(2)));


        List<SettlementVO> result = settlementService.findAllRegionSettlements(settlements.get(1).getCode());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(result.get(0).getName(), settlements.get(2).getName());
        Assertions.assertEquals(result.get(1).getName(), settlements.get(1).getName());
    }

    @Test
    void givenSettlementList_whenFindAllMunicipalitySettlements_thenReturnCorrectListOfSettlements(){
        List<Settlement> settlements = new ArrayList<>();
        settlements.add(settlementBuilder.saveSettlement());
        settlements.add(settlementBuilder.saveSettlement());
        settlements.add(settlementBuilder.saveSettlement());

        settlements.get(1).setName("Белене");
        settlements.get(1).setMunicipalityCode("PVN03");
        settlementService.update(settlements.get(1).getCode(), SettlementDTO.of(settlements.get(1)));
        settlements.get(2).setName("Аспарухово");
        settlements.get(2).setMunicipalityCode("PVN03");
        settlementService.update(settlements.get(2).getCode(), SettlementDTO.of(settlements.get(2)));

        List<SettlementVO> result = settlementService.findAllMunicipalitySettlements(settlements.get(1).getCode());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }
}