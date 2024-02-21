package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.FacilityPaper;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
public class DocumentServiceTest extends BaseServiceTest {
    @Autowired
    private DocumentService documentService;
    @Autowired
    private RecordBuilder recordBuilder;
    @Autowired
    private BranchBuilder branchBuilder;
    @Autowired
    private FacilityBuilder facilityBuilder;
    @Autowired
    private AddressBuilder addressBuilder;
    @Autowired
    private ClassifierBuilder classifierBuilder;
    @Autowired
    private ContractorBuilder contractorBuilder;
    @Autowired
    private NomenclatureService nomenclatureService;
    @Autowired
    private NomenclatureBuilder nomenclatureBuilder;

    private User assignee;
    private Nomenclature nomenclature;

    @BeforeAll
    public void init() {
        assignee = recordBuilder.saveUser();
        nomenclature = nomenclatureBuilder.saveNomenclature(langBG);
    }

    @Test
    void exportPaper() {
        Record record = createRecord(ServiceType.S3180, contractorBuilder.mockContractor(
                addressBuilder.mockAddress(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE)),
                branchBuilder.saveBranch(langBG),
                classifierBuilder.saveClassifier(langBG)));
        Assertions.assertNotNull(record);

        Resource result = documentService.exportPaper(record.getId(), langBG);
        Assertions.assertNotNull(result);
    }

    @Test
    void exportRefusalOrderS3180() {
        Record record = createRecord(ServiceType.S3180, contractorBuilder.mockContractor(
                addressBuilder.mockAddress(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE)),
                branchBuilder.saveBranch(langBG),
                classifierBuilder.saveClassifier(langBG)));
        Assertions.assertNotNull(record.getId());

        Resource result = documentService.exportOrder(record.getId(), langBG);
        Assertions.assertNotNull(result);
    }

//    @Test
//    void getPdfDataJson() {
//        String metadataKey = Constants.APPLICATION_XML_METADATA_KEY;
//        String name = "/home/igi/Work/BFSA/eForm/3181-918445d3-fc68-423f-a937-8d380c8ad1bc-ZVLN";
//
//        try {
//            String result = documentService.getPdfDataJson(Files.readAllBytes(Paths.get(name + ".pdf")), metadataKey);
//            log.info("\nPDF data:\n{}\n\n", result);
//
//            // Write output to local file system
//            Writer output;
//            File file = new File(name + (metadataKey.equals(Constants.APPLICATION_XML_METADATA_KEY) ? ".xml" : ".json"));
//            if(file.isDirectory()){
//                file.delete();
//            }
//            if(!file.exists()){
//                file.createNewFile();
//            }
//            output = new BufferedWriter(new FileWriter(file));
//            output.write(result);
//            output.close();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


    private Record createRecord(ServiceType serviceType, Contractor contractor) {
        Record record = null;
        switch (serviceType) {
            case S2701:
                record = recordBuilder.saveRecord(assignee, RecordStatus.ENTERED,
                        null,
                        null,
                        ContractorPaper.builder()
                                .serviceType(serviceType)
                                .regDate(LocalDate.now())
                                .regNumber(UUID.randomUUID().toString())
                                .status(ApprovalDocumentStatus.ENTERED).build(),
                        contractor,
                        serviceType,
                        assignee.getBranch());
                break;
            case S3180:
                record = recordBuilder.saveRecord(assignee, RecordStatus.ENTERED,
                        facilityBuilder.mockFacility(
                                FacilityStatus.INACTIVE,
                                addressBuilder.mockAddress(),
                                contractor,
                                branchBuilder.saveBranch(langBG),
                                serviceType,
                                ApprovalDocumentStatus.ACTIVE,
                                null,
                                nomenclature),
                        FacilityPaper.builder().serviceType(serviceType).status(ApprovalDocumentStatus.ENTERED).build(),
                        null,
                        contractor,
                        serviceType,
                        assignee.getBranch());
                break;
            default:
                System.out.println("Invalid service type");
                break;
        }
        return record;
    }
}

