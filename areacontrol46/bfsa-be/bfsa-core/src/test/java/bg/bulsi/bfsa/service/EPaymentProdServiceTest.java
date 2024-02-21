package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.DaeuRegisterPaymentResponseDTO;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class EPaymentProdServiceTest extends BaseServiceTest {

    @Autowired
    private EPaymentService ePaymentProdService;
    @Autowired
    private RecordBuilder recordBuilder;
    @Autowired
    private FacilityBuilder facilityBuilder;
    @Autowired
    private ContractorBuilder contractorBuilder;
    @Autowired
    private NomenclatureBuilder nomenclatureBuilder;
    @Autowired
    private AddressBuilder addressBuilder;
    @Autowired
    private BranchBuilder branchBuilder;

    private Nomenclature nomenclature;

    @BeforeAll
    public void init() {
        nomenclature = nomenclatureBuilder.saveNomenclature(langBG);
    }

    @Test
    void register() throws Exception {
        Contractor contractor = contractorBuilder.mockContractor();
        Facility facility = facilityBuilder.mockFacility(
                FacilityStatus.ACTIVE,
                addressBuilder.mockAddress(),contractor,
                branchBuilder.saveBranch(langBG),
                ServiceType.S2701,
                ApprovalDocumentStatus.ACTIVE, null, nomenclature);
        contractor.setIdentifier("1407048430");
        contractor.setFullName("Драган Цанков");

        User user = recordBuilder.saveUser();

        Record record = recordBuilder.saveRecord(
                user,
                RecordStatus.ENTERED,
                facility,
                null,
                null,
                contractor,
                ServiceType.S2701,
                user.getBranch());

        record.setRequestor(contractor);
        DaeuRegisterPaymentResponseDTO result = ePaymentProdService.registerEPayment(record);

        log.info("\n\nResult of: EPaymentProdServiceTest, register(): \n--->>> {}\n\n", result);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getAccessCode());
    }

}
