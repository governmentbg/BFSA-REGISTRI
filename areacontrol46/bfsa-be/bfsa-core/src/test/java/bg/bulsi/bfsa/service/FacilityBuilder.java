package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FacilityCapacity;
import bg.bulsi.bfsa.model.FacilityI18n;
import bg.bulsi.bfsa.model.FacilityPaper;
import bg.bulsi.bfsa.model.Nomenclature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FacilityBuilder {

    @Autowired
    private AddressBuilder addressBuilder;
    @Autowired
    private ContractorBuilder contractorBuilder;
    @Autowired
    private FacilityService service;
    @Autowired
    private BranchBuilder branchBuilder;

    public static final Boolean ENABLED = true;
    public static final String TEST_NAME_BG = "Адрес";
    public static final String TEST_NAME_EN = "Address";

    public Facility mockFacility(final FacilityStatus facilityStatus,
                                 final Address address,
                                 final Contractor contractor,
                                 final Branch branch,
                                 final ServiceType serviceType,
                                 final ApprovalDocumentStatus status,
                                 final Classifier register,
                                 final Nomenclature nomenclature) {
        List<FacilityCapacity> facilityCapacities = new ArrayList<>();
        facilityCapacities.add(mockFacilityCapacity(nomenclature));

        Facility facility = Facility.builder()
                .address(address)
//                .contractor(contractor)
                .branch(branch)
                .mail(UUID.randomUUID().toString().substring(0, 5))
                .phone1(UUID.randomUUID().toString().substring(0, 5))
                .phone2(UUID.randomUUID().toString().substring(0, 5))
                .status(facilityStatus != null ? facilityStatus : FacilityStatus.INACTIVE)
                .enabled(ENABLED)
                .facilityCapacities(facilityCapacities)
                .activityType(nomenclature)
                .waterSupplyType(nomenclature)
                .build();

        facility.getRegisters().add(register);

        facility.getFacilityPapers()
                .add(mockFacilityPaper(facility, serviceType != null ? serviceType : ServiceType.S3180, status));

        facility.getI18ns().add(new FacilityI18n(
                TEST_NAME_BG + UUID.randomUUID().toString().substring(0, 3),
                TEST_NAME_BG + UUID.randomUUID().toString().substring(0, 3),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                facility,
                BaseServiceTest.langBG));
        facility.getI18ns().add(new FacilityI18n(
                TEST_NAME_EN + UUID.randomUUID().toString().substring(0, 3),
                TEST_NAME_EN + UUID.randomUUID().toString().substring(0, 3),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                facility,
                BaseServiceTest.langEN));

        return facility;
    }

    private static FacilityPaper mockFacilityPaper(
            final Facility facility,
            final ServiceType serviceType,
            final ApprovalDocumentStatus status
    ) {
        return FacilityPaper.builder()
                .serviceType(serviceType)
                .status(status)
                .facility(facility)
                .build();
    }

//    public Facility save(
//            final FacilityStatus facilityStatus,
//            final Language language,
//            final ServiceType serviceType,
//            final ApprovalDocumentStatus status,
//            final Classifier register,
//            final Nomenclature nomenclature
//    ) {
//        return service.create(
//                mockFacility(facilityStatus,
//                    addressBuilder.saveAddress(),
//                    contractorBuilder.saveContractor(),
//                    branchBuilder.saveBranch(language),
//                    serviceType,
//                    status,
//                    register,
//                    nomenclature)
//        );
//    }

    private FacilityCapacity mockFacilityCapacity(Nomenclature nomenclature) {
        FacilityCapacity facilityCapacity = new FacilityCapacity();
        facilityCapacity.setMaterial(nomenclature);
        facilityCapacity.setUnit(nomenclature);
        facilityCapacity.setProduct(UUID.randomUUID().toString());
        facilityCapacity.setQuantity(2);
        return facilityCapacity;
    }
}
