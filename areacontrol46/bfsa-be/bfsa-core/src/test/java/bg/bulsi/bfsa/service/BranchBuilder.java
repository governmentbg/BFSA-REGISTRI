package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.BranchI18n;
import bg.bulsi.bfsa.model.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BranchBuilder {
    @Autowired
    private BranchService service;
    @Autowired
    private SettlementBuilder settlementBuilder;

    public static final String PHONE_1 = "111 111 111";
    public static final String PHONE_2 = "222 222 222";
    public static final String PHONE_3 = "333 333 333";
    public static final Boolean ENABLE = true;
    private static final String TEST_NAME_BG = "Тест име";
    private static final String TEST_ADDRESS_BG = "Тест адрес";
    private static final String TEST_DESC_BG = "Тест описание";

    public Branch saveBranch(Language language) {
        return service.create(mockBranch(language));
    }

    public List<Branch> saveBranches(Language language) {
        List<Branch> branches = new ArrayList<>();

        branches.add(saveBranch(language));
        branches.add(saveBranch(language));
        branches.add(saveBranch(language));
        branches.add(saveBranch(language));

        return branches;
    }

    public Branch mockBranch(Language language) {
        Branch branch = Branch.builder()
                .email(UUID.randomUUID() + "@domain.zxc")
                .phone1(PHONE_1)
                .phone2(PHONE_2)
                .phone3(PHONE_3)
                .enabled(ENABLE)
                .identifier(UUID.randomUUID().toString())
                .settlement(settlementBuilder.saveSettlement())
                .sequenceNumber(UUID.randomUUID().toString().substring(0,5))
                .build();
        branch.getI18ns().add(new BranchI18n(TEST_NAME_BG, TEST_ADDRESS_BG, TEST_DESC_BG, branch, language));

        return branch;
    }
}
