//package bg.bulsi.bfsa.service;
//
//import bg.bulsi.bfsa.dto.FacilityDTO;
//import bg.bulsi.bfsa.dto.FacilityVO;
//import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
//import bg.bulsi.bfsa.enums.FacilityStatus;
//import bg.bulsi.bfsa.model.Branch;
//import bg.bulsi.bfsa.model.Classifier;
//import bg.bulsi.bfsa.model.Contractor;
//import bg.bulsi.bfsa.model.Facility;
//import bg.bulsi.bfsa.model.User;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//public class FacilityServiceTest extends BaseServiceTest {
//    @Autowired
//    FacilityService service;
//    @Autowired
//    private FacilityBuilder builder;
//    @Autowired
//    private AddressBuilder addressBuilder;
//    @Autowired
//    private ContractorBuilder contractorBuilder;
//    @Autowired
//    private BranchBuilder branchBuilder;
//    @Autowired
//    private ClassifierBuilder classifierBuilder;
//    @Autowired
//    private UserBuilder userBuilder;
//    @Autowired
//    private NomenclatureBuilder nomenclatureBuilder;
//
//    private final ApprovalDocumentStatus STATUS_ENTERED = ApprovalDocumentStatus.ENTERED;
//    private final ApprovalDocumentStatus STATUS_ACTIVE = ApprovalDocumentStatus.ACTIVE;
//
//    @Test
//    public void givenFacility_whenFindById_thenReturnFacility() {
//        Facility entity = service.create(
//                builder.mockFacility(
//                        FacilityStatus.ACTIVE,
//                        addressBuilder.saveAddress(),
//                        contractorBuilder.saveContractor(),
//                        branchBuilder.saveBranch(langBG),null,
//                        STATUS_ENTERED, null,
//                        nomenclatureBuilder.saveNomenclature(langBG)
//                )
//        );
//
//        Facility facility = service.findById(entity.getId());
//
//        Assertions.assertNotNull(facility);
//        Assertions.assertEquals(facility.getMail(), entity.getMail());
//        Assertions.assertEquals(facility.getPhone1(), entity.getPhone1());
//        Assertions.assertEquals(facility.getPhone2(), entity.getPhone2());
//        Assertions.assertEquals(facility.getEnabled(), entity.getEnabled());
//        Assertions.assertEquals(facility.getContractor().getId(), entity.getContractor().getId());
//        Assertions.assertEquals(facility.getI18n(langBG).getName(), entity.getI18n(langBG).getName());
//        Assertions.assertEquals(facility.getI18n(langBG).getDescription(), entity.getI18n(langBG).getDescription());
//        Assertions.assertEquals(facility.getI18n(langEN).getName(), entity.getI18n(langEN).getName());
//        Assertions.assertEquals(facility.getI18n(langEN).getDescription(), entity.getI18n(langEN).getDescription());
//    }
//
//    @Test
//    public void givenListOfFacilities_whenFindAll_thenReturnListOfSavedFacilities() {
//        List<Facility> facilities = new ArrayList<>();
//        facilities.add(service.create(builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG), null,
//                STATUS_ENTERED, null,
//                nomenclatureBuilder.saveNomenclature(langBG))));
//        facilities.add(service.create(builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG), null,
//                STATUS_ENTERED, null,
//                nomenclatureBuilder.saveNomenclature(langBG))));
//        facilities.add(service.create(builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG), null,
//                STATUS_ENTERED, null,
//                nomenclatureBuilder.saveNomenclature(langBG))));
//
//        List<Facility> foundedFacilities = service.findAll();
//
//        Assertions.assertNotNull(foundedFacilities);
//        Assertions.assertTrue(foundedFacilities.size() > 2);
//    }
//
//    @Test
//    public void givenFacility_whenCreate_thenReturnFacility() {
//        Facility entity = builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG), null,
//                STATUS_ENTERED, null,
//                nomenclatureBuilder.saveNomenclature(langBG));
//
//        Facility facility = service.create(entity);
//
//        Assertions.assertNotNull(facility);
//        Assertions.assertEquals(facility.getMail(), entity.getMail());
//        Assertions.assertEquals(facility.getPhone1(), entity.getPhone1());
//        Assertions.assertEquals(facility.getPhone2(), entity.getPhone2());
//        Assertions.assertEquals(facility.getEnabled(), entity.getEnabled());
//        Assertions.assertEquals(facility.getAddress().getAddressType(), entity.getAddress().getAddressType());
//        Assertions.assertEquals(facility.getContractor().getId(), entity.getContractor().getId());
//        Assertions.assertEquals(facility.getI18n(langBG).getName(), entity.getI18n(langBG).getName());
//        Assertions.assertEquals(facility.getI18n(langBG).getDescription(), entity.getI18n(langBG).getDescription());
//        Assertions.assertEquals(facility.getI18n(langEN).getName(), entity.getI18n(langEN).getName());
//        Assertions.assertEquals(facility.getI18n(langEN).getDescription(), entity.getI18n(langEN).getDescription());
//    }
//
//    @Test
//    public void givenFacility_whenUpdate_thenReturnUpdatedFacility() {
//        FacilityDTO facility = FacilityDTO.of(service.create(builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG), null,
//                STATUS_ENTERED, null,
//                nomenclatureBuilder.saveNomenclature(langBG))),
//                langBG);
//        facility.setMail(facility.getAddress().getMail() + "_Updated");
//        facility.setPhone1(facility.getPhone1() + "_Updated");
//        facility.setPhone2(facility.getPhone2() + "_Updated");
//        facility.setEnabled(!facility.getEnabled());
//        facility.getAddress().setId(addressBuilder.saveAddress().getId());
//        facility.setContractorId(contractorBuilder.saveContractor().getId());
//
//        Facility updated = service.update(facility.getId(), facility, langBG);
//
//        Assertions.assertNotNull(updated);
//        Assertions.assertEquals(facility.getMail(), updated.getMail());
//        Assertions.assertEquals(facility.getPhone1(), updated.getPhone1());
//        Assertions.assertEquals(facility.getPhone2(), updated.getPhone2());
//        Assertions.assertEquals(facility.getEnabled(), updated.getEnabled());
//        Assertions.assertEquals(facility.getAddress().getId(), updated.getAddress().getId());
//        Assertions.assertEquals(facility.getContractorId(), updated.getContractor().getId());
//    }
//
//    @Test
//    public void givenNullFacility_whenCreate_thenReturnRuntimeExceptionFacilityIsRequired() {
//        RuntimeException thrown = Assertions.assertThrows(
//                RuntimeException.class, () -> service.create(null)
//        );
//
//        Assertions.assertEquals("Facility is required!", thrown.getMessage());
//    }
//
//    @Test
//    public void givenFacilityWithNullAddress_whenCreate_thenReturnFacilityWithNullAddress() {
//        Facility entity = builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG),
//                null,
//                STATUS_ENTERED,
//                null,
//                nomenclatureBuilder.saveNomenclature(langBG));
//        entity.setAddress(null);
//
//        Facility created = service.create(entity);
//
//        Assertions.assertNotNull(created);
//        Assertions.assertEquals(created.getMail(), entity.getMail());
//        Assertions.assertEquals(created.getPhone1(), entity.getPhone1());
//        Assertions.assertEquals(created.getPhone2(), entity.getPhone2());
//        Assertions.assertEquals(created.getEnabled(), entity.getEnabled());
//        Assertions.assertNull(created.getAddress());
//        Assertions.assertEquals(created.getContractor().getId(), entity.getContractor().getId());
//        Assertions.assertEquals(created.getI18n(langBG).getName(), entity.getI18n(langBG).getName());
//        Assertions.assertEquals(created.getI18n(langBG).getDescription(), entity.getI18n(langBG).getDescription());
//        Assertions.assertEquals(created.getI18n(langEN).getName(), entity.getI18n(langEN).getName());
//        Assertions.assertEquals(created.getI18n(langEN).getDescription(), entity.getI18n(langEN).getDescription());
//    }
//
//    @Test
//    public void givenFacilityWithNullContractor_whenCreate_thenReturnFacilityWithNullContractor() {
//        Facility entity = builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG), null,
//                STATUS_ENTERED, null,
//                nomenclatureBuilder.saveNomenclature(langBG));
//        entity.setContractor(null);
//
//        Facility created = service.create(entity);
//
//        Assertions.assertNotNull(created);
//        Assertions.assertEquals(created.getMail(), entity.getMail());
//        Assertions.assertEquals(created.getPhone1(), entity.getPhone1());
//        Assertions.assertEquals(created.getPhone2(), entity.getPhone2());
//        Assertions.assertEquals(created.getEnabled(), entity.getEnabled());
//        Assertions.assertNull(created.getContractor());
//        Assertions.assertEquals(created.getAddress().getId(), entity.getAddress().getId());
//        Assertions.assertEquals(created.getI18n(langBG).getName(), entity.getI18n(langBG).getName());
//        Assertions.assertEquals(created.getI18n(langBG).getDescription(), entity.getI18n(langBG).getDescription());
//        Assertions.assertEquals(created.getI18n(langEN).getName(), entity.getI18n(langEN).getName());
//        Assertions.assertEquals(created.getI18n(langEN).getDescription(), entity.getI18n(langEN).getDescription());
//    }
//
//    @Test
//    public void givenFacilityAndContractor_whenChangeFacilityOwner_thenReturnFacilityWithChangedOwner() {
//        Facility entity = builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG), null,
//                STATUS_ACTIVE, null,
//                nomenclatureBuilder.saveNomenclature(langBG));
//        Facility created = service.create(entity);
//        Contractor newContractor = contractorBuilder.saveContractor();
//
//        FacilityDTO changedFacilityOwner = service.changeFacilityOwner(created.getId(), newContractor.getId(), langBG);
//
//        Assertions.assertNotNull(changedFacilityOwner);
//        Assertions.assertEquals(changedFacilityOwner.getAddress().getPhone(), entity.getAddress().getPhone());
//        Assertions.assertEquals(changedFacilityOwner.getEnabled(), entity.getEnabled());
//        Assertions.assertEquals(changedFacilityOwner.getContractorId(), newContractor.getId());
//        Assertions.assertEquals(changedFacilityOwner.getAddress().getId(), entity.getAddress().getId());
//        Assertions.assertEquals(changedFacilityOwner.getName(), entity.getI18n(langBG).getName());
//        Assertions.assertEquals(changedFacilityOwner.getDescription(),
//                entity.getI18n(langBG).getDescription());
//    }
//
//    @Test
//    public void givenFacilityAndContractor_whenChangeFacilityOwner_thenReturnRuntimeExceptionEqualOwnerssId() {
//        Facility created = service.create(builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG), null,
//                STATUS_ACTIVE, null,
//                nomenclatureBuilder.saveNomenclature(langBG)));
//        Contractor newContractor = created.getContractor();
//
//        RuntimeException thrown = Assertions.assertThrows(
//                RuntimeException.class, () -> service.changeFacilityOwner(created.getId(), newContractor.getId(), langBG)
//        );
//
//        Assertions.assertEquals("Current facility owner id is equal to new facility owner id.",
//                thrown.getMessage());
//
//    }
//
//    @Test
//    public void givenFacilityAndContractor_whenChangeFacilityOwner_thenReturnRuntimeExceptionFacilityIdRequired() {
//        Facility created = service.create(builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG), null,
//                STATUS_ENTERED, null,
//                nomenclatureBuilder.saveNomenclature(langBG)));
//        created.setId(0l);
//        Contractor contractor = contractorBuilder.saveContractor();
//
//        RuntimeException thrown = Assertions.assertThrows(
//                RuntimeException.class, () -> service.changeFacilityOwner(created.getId(), contractor.getId(), langBG)
//        );
//
//        Assertions.assertEquals("Facility ID field is required", thrown.getMessage());
//    }
//
//    @Test
//    public void givenFacilityAndContractor_whenChangeFacilityOwner_thenReturnRuntimeExceptionContractorIdRequired() {
//        Facility created = service.create(builder.mockFacility(
//                FacilityStatus.ACTIVE,
//                addressBuilder.saveAddress(),
//                contractorBuilder.saveContractor(),
//                branchBuilder.saveBranch(langBG), null,
//                STATUS_ENTERED, null,
//                nomenclatureBuilder.saveNomenclature(langBG)));
//
//        Contractor contractor = contractorBuilder.saveContractor();
//        contractor.setId(0l);
//        RuntimeException thrown = Assertions.assertThrows(
//                RuntimeException.class, () -> service.changeFacilityOwner(created.getId(), contractor.getId(), langBG)
//        );
//
//        Assertions.assertEquals("New Facility Owner ID is required", thrown.getMessage());
//    }
//
//    @Test
//    public void givenRegisterCode_whenFindByRegisterCode_thenReturnListFacilityDto() {
//        Classifier register = classifierBuilder.saveClassifier(langBG);
//
//        service.create(
//                builder.mockFacility(
//                        FacilityStatus.ACTIVE,
//                        addressBuilder.saveAddress(),
//                        contractorBuilder.saveContractor(),
//                        branchBuilder.saveBranch(langBG),
//                        null,
//                        STATUS_ENTERED,
//                        register,
//                        nomenclatureBuilder.saveNomenclature(langBG)
//                )
//        );
//
//        List<FacilityDTO> facilities = service.findByRegisterCode(register.getCode(), langBG);
//
//        Assertions.assertNotNull(facilities);
//        Assertions.assertEquals(register.getCode(), facilities.get(0).getRegisterCode());
//    }
//
//    @Test
//    public void givenRegisterCodeAndBranch_whenFindByRegisterCodeAndBranch_thenReturnListFacilityVO() {
//        Classifier register = classifierBuilder.saveClassifier(langBG);
//        Branch branch = branchBuilder.saveBranch(langBG);
//        User user = userBuilder.saveUser(branch);
//        Facility facility = service.create(
//                builder.mockFacility(
//                        FacilityStatus.ACTIVE,
//                        addressBuilder.saveAddress(),
//                        contractorBuilder.saveContractor(),
//                        branch,
//                        null,
//                        STATUS_ENTERED,
//                        register,
//                        nomenclatureBuilder.saveNomenclature(langBG)
//                )
//        );
//
//        Page<FacilityVO> facilities = service
//                .findByRegisterCodeAndBranchId(
//                        user.getUsername(), register.getCode(),
//                        langBG, Pageable.unpaged());
//
//        Assertions.assertNotNull(facilities);
//        Assertions.assertEquals(register.getCode(), facilities.getContent().get(0).getRegisterCode());
//        Assertions.assertEquals(facility.getRegister().getCode(), facilities.getContent().get(0).getRegisterCode());
//        Assertions.assertEquals(branch.getId(), facilities.getContent().get(0).getBranchId());
//    }
//
//    @Test
//    public void givenDivisionCodeAndBranch_whenFindByDivisionCodeAndBranch_thenReturnListFacilityVO() {
//        Classifier parent = classifierBuilder.saveClassifierWithSubClassifiers(langBG);
//        Classifier register = parent.getSubClassifiers().get(0);
//
//        Branch branch = branchBuilder.saveBranch(langBG);
//        User user = userBuilder.saveUser(branch);
//        Facility facility = service.create(
//                builder.mockFacility(
//                        FacilityStatus.ACTIVE,
//                        addressBuilder.saveAddress(),
//                        contractorBuilder.saveContractor(),
//                        branch,
//                        null,
//                        STATUS_ENTERED,
//                        register,
//                        nomenclatureBuilder.saveNomenclature(langBG)
//                )
//        );
//
//        Page<FacilityVO> facilities = service
//                .findByDivisionCodeAndBranchId(
//                        user.getUsername(),
//                        register.getParent().getCode(),
//                        langBG, Pageable.unpaged());
//
//        Assertions.assertNotNull(facilities);
//        Assertions.assertEquals(register.getCode(), facilities.getContent().get(0).getRegisterCode());
//        Assertions.assertEquals(facility.getRegister().getCode(), facilities.getContent().get(0).getRegisterCode());
//        Assertions.assertEquals(branch.getId(), facilities.getContent().get(0).getBranchId());
//    }
//
//}