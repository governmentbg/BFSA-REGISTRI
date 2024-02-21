package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.FishingVesselDTO;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.FishingVessel;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.repository.FishingVesselRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Slf4j
class FishingVesselServiceTest extends BaseServiceTest {

    @Autowired
    private FishingVesselService fishingVesselService;
    @Autowired
    private FishingVesselRepository fishingVesselRepository;
    @Autowired
    private NomenclatureBuilder nomenclatureBuilder;
    @Autowired
    private BranchBuilder branchBuilder;
    private static final String REG_NUMBER = "02-001";
    private static final String ENTRY_NUMBER = "001";
    private static final Double HULL_LENGTH = 8.25;
    private static final String EXTERNAL_MARKING = "Сз-303";
    private static final LocalDate DATE = LocalDate.now();
    private static Long VESSEL_ID = null;
    private static Long BRANCH_ID = null;
    private static String ASSIGMENT_TYPE_CODE = null;

    @BeforeAll
    public void init() {
        if (fishingVesselRepository.count() == 0) {
            Nomenclature type = nomenclatureBuilder.saveNomenclature(langBG);
            ASSIGMENT_TYPE_CODE = type.getCode();
            Branch branch = branchBuilder.saveBranch(langBG);
            BRANCH_ID = branch.getId();

            FishingVessel fishingVessel = FishingVessel
                    .builder()
                    .date(DATE)
                    .regNumber(REG_NUMBER)
                    .entryNumber(ENTRY_NUMBER)
                    .hullLength(HULL_LENGTH)
                    .externalMarking(EXTERNAL_MARKING)
                    .assignmentType(type)
                    .branch(branch)
                    .enabled(true)
                    .build();

            FishingVessel newVessel = fishingVesselRepository.save(fishingVessel);
            VESSEL_ID = newVessel.getId();
        }
        VESSEL_ID = fishingVesselRepository.findAll().stream().findFirst().get().getId();
    }

//    @Test
//    void given_ExistingFishingVessel_whenUpdate_thenReturnUpdatedFishingVessel() {
//        FishingVessel fishingVessel = fishingVesselService.findByRegNumber(REG_NUMBER);
//        fishingVessel.setExternalMarking(EXTERNAL_MARKING + "_updated");
//
//        FishingVesselDTO updated = fishingVesselService
//                .update(fishingVessel.getId(), FishingVesselDTO.of(fishingVessel, langBG));
//
//        Assertions.assertNotNull(updated);
//        Assertions.assertEquals(EXTERNAL_MARKING + "_updated", updated.getExternalMarking());
//    }

    @Test
    void given_NoParams_whenFindAll_thenReturnAllFishingVessels() {
        Page<FishingVesselDTO> fishingVessels = fishingVesselService.findAll(Pageable.unpaged(), langBG);
        Assertions.assertNotNull(fishingVessels);
        Assertions.assertEquals(REG_NUMBER, fishingVessels.getContent().get(0).getRegNumber());
    }

    @Test
    void givenId_whenFindById_thenReturnFishingVessel() {
        FishingVessel fishingVessel = fishingVesselService.findById(VESSEL_ID);
        Assertions.assertNotNull(fishingVessel);
        Assertions.assertEquals(REG_NUMBER, fishingVessel.getRegNumber());
    }

    @Test
    void givenRegNumber_whenFindByRegNumber_thenReturnFishingVessel() {
        FishingVessel fishingVessel = fishingVesselService.findByRegNumber(REG_NUMBER);
        Assertions.assertNotNull(fishingVessel);
    }

    @Test
    void givenExternalMarking_whenFindByExternalMarking_thenReturnFishingVessel() {
        FishingVessel fishingVessel = fishingVesselService.findByExternalMarking(EXTERNAL_MARKING);

        if (fishingVessel == null) {
            fishingVessel = fishingVesselService.findByExternalMarking(EXTERNAL_MARKING + "_updated");
        }

        Assertions.assertNotNull(fishingVessel);
        Assertions.assertEquals(REG_NUMBER, fishingVessel.getRegNumber());
    }

    @Test
    void givenHullLength_whenFindByHullLength_thenReturnFishingVessels() {
        Page<FishingVessel> fishingVessels = fishingVesselService
                .findByHullLength(HULL_LENGTH, Pageable.unpaged());
        Assertions.assertNotNull(fishingVessels);
        Assertions.assertEquals(REG_NUMBER, fishingVessels.getContent().get(0).getRegNumber());
    }

    @Test
    void givenEntryNumber_whenFindByEntryNUmber_thenReturnFishingVessel() {
        FishingVessel fishingVessel = fishingVesselService.findByEntryNumber(ENTRY_NUMBER);
        Assertions.assertNotNull(fishingVessel);
        Assertions.assertEquals(REG_NUMBER, fishingVessel.getRegNumber());
    }

    @Test
    void givenDates_whenFindByDate_thenReturnFishingVessels() {
        Page<FishingVessel> fishingVessels = fishingVesselService
                .findByDate(DATE.minusDays(10L), DATE, Pageable.unpaged());

        Assertions.assertNotNull(fishingVessels);
        Assertions.assertEquals(REG_NUMBER, fishingVessels.getContent().get(0).getRegNumber());
    }

    @Test
    void givenBranchId_whenFindByBranch_thenReturnFishingVessels() {
        Page<FishingVessel> fishingVessels = fishingVesselService
                .findByBranch(BRANCH_ID, Pageable.unpaged());
        Assertions.assertNotNull(fishingVessels);
        Assertions.assertEquals(REG_NUMBER, fishingVessels.getContent().get(0).getRegNumber());
    }

    @Test
    void givenAssignmentTypeCode_whenFindByType_thenReturnFishingVessels() {
        Page<FishingVessel> fishingVessels = fishingVesselService
                .findByAssignmentType(ASSIGMENT_TYPE_CODE, Pageable.unpaged());
        Assertions.assertNotNull(fishingVessels);
        Assertions.assertEquals(REG_NUMBER, fishingVessels.getContent().get(0).getRegNumber());
    }
}