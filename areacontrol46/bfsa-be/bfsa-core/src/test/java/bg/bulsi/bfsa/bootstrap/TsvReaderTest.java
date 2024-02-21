package bg.bulsi.bfsa.bootstrap;

import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.MessageResource;
import bg.bulsi.bfsa.model.Settlement;
import bg.bulsi.bfsa.repository.SettlementRepository;
import bg.bulsi.bfsa.service.BaseServiceTest;
import bg.bulsi.bfsa.service.SettlementBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TsvReaderTest extends BaseServiceTest {
    @Autowired
    private SettlementRepository settlementRepository;
    @Autowired
    private SettlementBuilder settlementBuilder;

    @BeforeAll
    public void init() {
        settlementBuilder.saveSettlement();
    }

    @Test
    public void givenMessageResourceFile_whenCallTsvMessageResourceListTo_thenCreateCorrectMessageResourceList() {
        final List<TsvReader.TsvMessageResource> result =
                TsvReader.tsvMessageResourceList(TsvReaderTest.class, "message-resource-library.tsv");

        List<MessageResource> messageResources = TsvReader.TsvMessageResource.to(result);

        assertThat(messageResources.get(0).getMessage()).isEqualTo(result.get(0).getMessage());
        assertThat(messageResources.get(0).getMessageResourceIdentity().getCode()).isEqualTo(result.get(0).getCode());
        assertThat(messageResources.get(0).getMessageResourceIdentity().getLanguageId())
                .isEqualTo(result.get(0).getLanguageId());
    }

    @Test
    public void givenMessageResourceFile_whenCallTsvMessageResourceListTo_thenCreateCorrectMessageResource() {
        final List<TsvReader.TsvMessageResource> result =
                TsvReader.tsvMessageResourceList(TsvReaderTest.class, "message-resource-library.tsv");

        MessageResource messageResource = TsvReader.TsvMessageResource.to(result.get(0));

        assertThat(messageResource).isNotNull();
        assertThat(messageResource.getMessage()).isEqualTo(result.get(0).getMessage());
        assertThat(messageResource.getMessageResourceIdentity().getLanguageId())
                .isEqualTo(result.get(0).getLanguageId());
        assertThat(messageResource.getMessageResourceIdentity().getCode()).isEqualTo(result.get(0).getCode());
    }

    @Test
    public void givenTsvSettlement_whenCallTsvSettlementTo_thenReturnCorrectSettlementObject() {
        final List<TsvReader.TsvSettlement> result =
                TsvReader.tsvSettlementList(TsvReaderTest.class, "settlements-library.tsv");

        Settlement settlement = TsvReader.TsvSettlement.to(result.get(1));

        assertThat(settlement).isNotNull();
        assertThat(settlement.getCode()).isEqualTo(result.get(1).getCode());
        assertThat(settlement.getRegionCode()).isEqualTo(result.get(1).getRegionCode());
        assertThat(settlement.getRegionName()).isEqualTo(result.get(1).getRegionName());
        assertThat(settlement.getRegionNameLat()).isEqualTo(result.get(1).getRegionNameLat());
        assertThat(settlement.getEnabled()).isEqualTo(result.get(1).getEnabled());
        assertThat(settlement.getMunicipalityCode()).isEqualTo(result.get(1).getMunicipalityCode());
        assertThat(settlement.getMunicipalityName()).isEqualTo(result.get(1).getMunicipalityName());
        assertThat(settlement.getMunicipalityNameLat()).isEqualTo(result.get(1).getMunicipalityNameLat());
        assertThat(settlement.getName()).isEqualTo(result.get(1).getName());
        assertThat(settlement.getNameLat()).isEqualTo(result.get(1).getNameLat());
        assertThat(settlement.getPlaceType().toString()).isEqualTo(result.get(1).getPlaceType());
        assertThat(settlement.getTsb().toString()).isEqualTo(result.get(1).getTsb());
        assertThat(settlement.getParentCode()).isEqualTo(result.get(1).getParentCode());
    }

    @Test
    public void givenTsvSettlementList_whenTsvSettlementTo_thenReturnCorrectSettlementList() {
        final List<TsvReader.TsvSettlement> result =
                TsvReader.tsvSettlementList(TsvReaderTest.class, "settlements-library.tsv");

        List<Settlement> settlements = TsvReader.TsvSettlement.to(result);

        assertThat(settlements).isNotNull();
        assertThat(settlements.get(1).getCode()).isEqualTo(result.get(1).getCode());
        assertThat(settlements.get(1).getRegionCode()).isEqualTo(result.get(1).getRegionCode());
        assertThat(settlements.get(1).getRegionName()).isEqualTo(result.get(1).getRegionName());
        assertThat(settlements.get(1).getRegionNameLat()).isEqualTo(result.get(1).getRegionNameLat());
        assertThat(settlements.get(1).getEnabled()).isEqualTo(result.get(1).getEnabled());
        assertThat(settlements.get(1).getMunicipalityCode()).isEqualTo(result.get(1).getMunicipalityCode());
        assertThat(settlements.get(1).getMunicipalityName()).isEqualTo(result.get(1).getMunicipalityName());
        assertThat(settlements.get(1).getMunicipalityNameLat()).isEqualTo(result.get(1).getMunicipalityNameLat());
        assertThat(settlements.get(1).getName()).isEqualTo(result.get(1).getName());
        assertThat(settlements.get(1).getNameLat()).isEqualTo(result.get(1).getNameLat());
        assertThat(settlements.get(1).getPlaceType().toString()).isEqualTo(result.get(1).getPlaceType());
        assertThat(settlements.get(1).getTsb().toString()).isEqualTo(result.get(1).getTsb());
        assertThat(settlements.get(1).getParentCode()).isEqualTo(result.get(1).getParentCode());
    }

    @Test
    void givenTsvBranch_whenCallTsvBranchTo_thenReturnCorrectBranchObject() {
        Language langBG = Language.builder().languageId("bg").build();

        final List<TsvReader.TsvBranch> result =
                TsvReader.tsvBranchList(TsvReaderTest.class, "branches-library.tsv");

        Branch branch = TsvReader.TsvBranch.to(result.get(0), settlementRepository);

        assertThat(branch).isNotNull();
        assertThat(branch.getEmail()).isEqualTo(result.get(0).email);
        assertThat(branch.getPhone1()).isEqualTo(result.get(0).phone1);
        assertThat(branch.getPhone2()).isEqualTo(result.get(0).phone2);
        assertThat(branch.getI18n(langBG).getName()).isEqualTo(result.get(0).name);
        assertThat(branch.getI18n(langBG).getDescription()).isEqualTo(result.get(0).description);
    }

    @Test
    void givenTsvBranchList_whenTsvBranchTo_thenReturnCorrectBranchList() {
        Language langBG = Language.builder().languageId("bg").build();

        final List<TsvReader.TsvBranch> result =
                TsvReader.tsvBranchList(TsvReaderTest.class, "branches-library.tsv");

        List<Branch> branch = TsvReader.TsvBranch.to(result, settlementRepository);

        assertThat(branch).isNotNull();
        assertThat(branch.get(0).getEmail()).isEqualTo(result.get(0).email);
        assertThat(branch.get(0).getPhone1()).isEqualTo(result.get(0).phone1);
        assertThat(branch.get(0).getPhone2()).isEqualTo(result.get(0).phone2);
        assertThat(branch.get(0).getI18n(langBG).getName()).isEqualTo(result.get(0).name);
        assertThat(branch.get(0).getI18n(langBG).getDescription()).isEqualTo(result.get(0).description);
    }
}