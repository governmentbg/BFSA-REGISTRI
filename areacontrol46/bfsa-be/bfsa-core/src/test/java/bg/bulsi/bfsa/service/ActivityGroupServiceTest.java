package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ActivityGroupDTO;
import bg.bulsi.bfsa.dto.BaseVO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ActivityGroup;
import bg.bulsi.bfsa.model.Nomenclature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
class ActivityGroupServiceTest extends BaseServiceTest {
    @Autowired
    private ActivityGroupService service;
    @Autowired
    private ActivityGroupBuilder builder;
    @Autowired
    private NomenclatureBuilder nomenclatureBuilder;

    private static final String NEW_BG_NAME = "00100-ново-име";
    private static final String NEW_BG_DESC = "00100-ново-описание";
    private static final String FAKE_CODE = "FAKE_CODE";

    @Test
    void whenFinaAll_thenReturnAllGroups() {
        List<ActivityGroup> groups = builder.buildSome();
        Assertions.assertNotNull(groups);

        for(ActivityGroup group : groups) {
            setNoms(group);
            group = service.create(group);
            Assertions.assertNotNull(group.getId());
        }

        Set<ActivityGroupDTO> dtos = service.findAllParents(langBG);
        Assertions.assertNotNull(dtos);
        Assertions.assertTrue(groups.size() <= dtos.size());
    }

    @Test
    void givenId_whenFinaById_thenReturnGroup() {
        List<ActivityGroup> groups = builder.buildSome();
        Assertions.assertNotNull(groups);

        List<ActivityGroup> saved = new ArrayList<>();

        for (ActivityGroup group : groups) {
            setNoms(group);
            ActivityGroup newGroup = service.create(group);
            Assertions.assertNotNull(newGroup);
            saved.add(newGroup);
        }

        ActivityGroup group = service.findById(saved.get(0).getId());
        Assertions.assertNotNull(group);
        Assertions.assertEquals(saved.get(0).getId(), group.getId());
    }

    @Test
    void givenGroupWithSubGroups_whenCreate_thenReturnGroup() {
        ActivityGroup group = builder.buildWithSubs();
        Assertions.assertNotNull(group);

        setNoms(group);

        final ActivityGroup saved = service.create(group);
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(group.getI18n(langBG).getName(),
                saved.getI18n(langBG).getName());
        Assertions.assertEquals(group.getI18n(langBG).getDescription(),
                saved.getI18n(langBG).getDescription());
    }

    @Test
    void whenFinaAllVOs_thenReturnAllGroupTypeVOs() {
        List<ActivityGroup> groups = builder.buildSome();
        Assertions.assertNotNull(groups);

        for (ActivityGroup group : groups) {
            setNoms(group);
            group = service.create(group);
            Assertions.assertNotNull(group.getId());
        }

        List<BaseVO> vos = service.findAllParentsVO(langBG);
        Assertions.assertNotNull(vos);
        Assertions.assertTrue(groups.size() <= vos.size());
    }

    @Test
    void givenGroupDto_whenUpdate_thenReturnUpdatedGroupDto() {
        ActivityGroup group = builder.build();
        Assertions.assertNotNull(group);

        setNoms(group);

        ActivityGroup saved = service.create(group);
        Assertions.assertNotNull(saved);

        ActivityGroupDTO dto = ActivityGroupDTO.of(saved, langBG);
        dto.setName(NEW_BG_NAME);
        dto.setDescription(NEW_BG_DESC);

        ActivityGroupDTO updated = service.update(dto.getId(), ActivityGroupDTO.to(dto, langBG), langBG);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(dto.getName(), updated.getName());
        Assertions.assertEquals(dto.getDescription(), updated.getDescription());
    }

    @Test
    void givenFakeId_whenFindById_thenThrowsEntityNotFoundException() {
        Long fakeId = -1L;
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> service.findById(fakeId)
        );

        Assertions.assertEquals("Entity '"
                        + ActivityGroup.class.getName() + "' with id/code='"
                        + fakeId + "' not found",
                thrown.getMessage());
    }

    @Test
    void givenFakeCode_whenUpdate_thenThrowsEntityNotFoundException() {
        Long fakeId = -1L;
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> service.update(
                        fakeId, ActivityGroup.builder().id(fakeId).build(), langEN)
        );

        Assertions.assertEquals("Entity '"
                + ActivityGroup.class.getName() + "' with id/code='"
                + fakeId + "' not found",
                thrown.getMessage());
    }

    private void setNoms(ActivityGroup group) {
        List<Nomenclature> racs = new ArrayList<>();
        racs.add(nomenclatureBuilder.saveNomenclature(langBG));
        racs.add(nomenclatureBuilder.saveNomenclature(langBG));
        racs.add(nomenclatureBuilder.saveNomenclature(langBG));
        group.getRelatedActivityCategories().addAll(racs);
        group.getAnimalSpecies().add(nomenclatureBuilder.saveNomenclature(langBG));
        group.getRemarks().add(nomenclatureBuilder.saveNomenclature(langBG));
    }
}