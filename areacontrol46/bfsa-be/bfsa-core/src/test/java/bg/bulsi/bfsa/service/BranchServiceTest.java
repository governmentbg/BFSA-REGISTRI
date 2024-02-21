package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.BranchDTO;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Settlement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class BranchServiceTest extends BaseServiceTest {
    @Autowired
    private BranchService service;
    @Autowired
    private BranchBuilder branchBuilder;
    @Autowired
    private SettlementBuilder settlementBuilder;

    private static final String TEST_NAME_BG = "Тест име";
    private static final String TEST_ADDRESS_BG = "Тест адрес";
    private static final String TEST_DESC_BG = "Тест описание";
    private static final String UPDATE = "_updated";

//    @Test
//    public void givenPredefinedBranches_whenFindAll_thenReturnListOfAllBranchesDto() {
//        List<Branch> predefinedBranches = branchBuilder.saveBranches(langBG);
//
//        List<BranchDTO> branches = BranchDTO.of(service.findAll(), langBG);
//        Assertions.assertNotNull(branches);
//        Assertions.assertTrue(predefinedBranches.size() <= branches.size());
//    }

    @Test
    public void givenId_whenFindById_thenReturnDto() {
        Branch branch = branchBuilder.saveBranch(langBG);
        BranchDTO result = BranchDTO.of(service.findById(branch.getId()), langBG);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(branch.getId(), result.getId());
    }

    @Test
    public void givenDto_whenCreate_thenReturnCreatedDto() {
        Settlement settlement = settlementBuilder.saveSettlement();
        BranchDTO dto = new BranchDTO(
                null,
                "176986803",
                settlement.getCode(),
                "email_test@email.zzz",
                "111 111 111",
                "222 222 222",
                "333 333 333",
                TEST_NAME_BG,
                TEST_ADDRESS_BG,
                TEST_DESC_BG,
                false,
                true,
                null);

        BranchDTO result = BranchDTO.of(service.create(BranchDTO.to(dto, langBG)), langBG);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TEST_DESC_BG, result.getDescription());
        Assertions.assertEquals(TEST_NAME_BG, result.getName());
        Assertions.assertEquals(settlement.getCode(), dto.getSettlementCode());
    }

    @Test
    public void givenDto_whenUpdate_thenReturnUpdatedDto() {
        BranchDTO dto = BranchDTO.of(branchBuilder.saveBranch(langBG), langBG);
        dto.setEmail("email_test_update@email.xxx");
        dto.setPhone1("444 444 444");
        dto.setPhone2("777 777 777");
        dto.setPhone3("000 000 000");
        dto.setDescription(TEST_DESC_BG + UPDATE);
        dto.setName(TEST_NAME_BG + UPDATE);
        dto.setAddress(TEST_ADDRESS_BG);
        dto.setEnabled(true);

        BranchDTO result = BranchDTO.of(service.update(dto.getId(), dto, langBG), langBG);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(dto.getName(), result.getName());
        Assertions.assertEquals(dto.getDescription(), result.getDescription());
        Assertions.assertEquals(dto.getEmail(), result.getEmail());
    }
}
