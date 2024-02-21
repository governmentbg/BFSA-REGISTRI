package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.util.Constants;
import bg.bulsi.bfsa.dto.ContractorDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.model.VerificationToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Slf4j
class ContractorServiceTest extends BaseServiceTest {

    @Autowired
    private ContractorService contractorService;
    @Autowired
    private UserService userService;
    @MockBean
    private MailService mailService;
    @Autowired
    private ContractorBuilder contractorBuilder;
    @Autowired
    private ClassifierBuilder classifierBuilder;
    @Autowired
    private AddressBuilder addressBuilder;
    @Autowired
    private BranchBuilder branchBuilder;
    @Autowired
    private UserBuilder userBuilder;

    private static final String USERNAME = "contractor";
    private static final String USERNAME_UPDATED = "contractor-updated";
    private static final String REV_NAME = "Name for revision";
    private static final String TEST_FULL_NAME = "Contractor Full Name";
    private static final String FAKE_VALUE = "FAKE_VALUE";
    private static final String TEST_EMAIL = "contractor@domain.xyz";

    @Test
    void givenContractors_whenUpdate_thenReturnContractors() {
        Contractor contractor = contractorBuilder.saveContractor();
        Assertions.assertNotNull(contractor);

        ContractorDTO updated = contractorService.update(contractor.getId(), contractor, langBG);
        Assertions.assertNotNull(updated);

        Contractor contractor1 = contractorBuilder.saveContractor();
        Assertions.assertNotNull(contractor1);
    }

//    @Test
//    void givenContractorUsername_whenUpdateContractor_thenReturnUpdatedRecord() {
//        Contractor relation = contractorBuilder.saveContractor();
//        Contractor contractor = contractorBuilder.saveContractor();
//        Assertions.assertNotNull(contractor);
//
//        int rolesBeforeUpdate = contractor.getRoles().size();
//
//        Role roleAdmin = roleRepository.findByName(RolesConstants.ROLE_ADMIN);
//        contractor.setUsername(USERNAME_UPDATED);
//        contractor.setFullName(TEST_FULL_NAME);
//        contractor.getRoles().add(roleAdmin);
//
//        contractor.getContractorRelations().add(
//                ContractorRelation.builder()
//                .contractor(contractor)
//                .relation(relation)
//                .relationType(nomenclatureBuilder.saveNomenclature(langBG)).build()
//        );
//
//        ContractorDTO updated = contractorService.update(contractor.getId(), contractor);
//        int rolesAfterUpdate = updated.getRoles().size();
//
//        Assertions.assertEquals(TEST_FULL_NAME, updated.getFullName());
//        Assertions.assertEquals(USERNAME_UPDATED, updated.getUsername());
//        Assertions.assertEquals(rolesAfterUpdate, rolesBeforeUpdate + 1);
//        Assertions.assertNotNull(updated.getContractorRelations());
//        Assertions.assertTrue(updated.getContractorRelations().size() > 0);
//        Assertions.assertEquals(relation.getId(), updated.getContractorRelations().stream()
//                .filter(r -> updated.getId().equals(r.getContractorId()))
//                .toList().get(0).getRelationId()
//        );
//        Assertions.assertNotNull(updated.getRelatedActivityCategories());
//        List<ContractorDTO> revisions = contractorService.findRevisions(updated.getId());
//        Assertions.assertNotNull(revisions);
//        Assertions.assertTrue(revisions.size() > 0);
//    }


    @Test
    void givenContractorUsername_whenChangeEnabledStatus_thenReturnUpdatedRecord() {
        Contractor main = contractorBuilder.saveContractor();
        Contractor contractor = this.contractorService.findByUsername(main.getUsername());
        Assertions.assertNotNull(contractor);

        Contractor updated = this.contractorService.setEnabled(contractor.getId(), false);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(updated.getEnabled(), false);
    }

    @Test
    void givenUsername_whenEnabledStatusIsNull_thenThrowException() {
        Contractor main = contractorBuilder.saveContractor();
        Contractor contractor = this.contractorService.findByUsername(main.getUsername());
        Assertions.assertNotNull(contractor);

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class, () -> this.contractorService.setEnabled(contractor.getId(), null)
        );
        Assertions.assertEquals("Boolean param enabled is required", exception.getMessage());
    }

//    @Test
//    void givenUsername_whenFindHistory_thenReturnContractor() {
//        Contractor contractor = contractorBuilder.saveContractor();
//        Assertions.assertNotNull(contractor);
//
//        List<ContractorDTO> firstUsersList = this.contractorService.findRevisions(contractor.getId());
//
//        contractor.setFullName(REV_NAME);
//        this.contractorService.update(contractor.getId(), contractor);
//        List<ContractorDTO> secondUsersList = this.contractorService.findRevisions(contractor.getId());
//
//        this.contractorService.setEnabled(contractor.getId(), false);
//        List<ContractorDTO> thirdUsersList = this.contractorService.findRevisions(contractor.getId());
//
//        Assertions.assertNotNull(contractor);
//        Assertions.assertEquals(secondUsersList.size(), firstUsersList.size() + 1);
//        Assertions.assertEquals(thirdUsersList.size(), secondUsersList.size() + 1);
//    }

    @Test
    void givenUsername_whenFindByUsername_thenReturnContractor() {
        Contractor main = contractorBuilder.saveContractor();
        Contractor contractor = this.contractorService.findByUsername(main.getUsername());
        Assertions.assertNotNull(contractor);

        Assertions.assertNotNull(contractor);
        Assertions.assertEquals(main.getEmail(), contractor.getEmail());
    }

    @Test
    void givenSearchString_whenSearch_thenReturnPageResponse() {
        Contractor main = contractorBuilder.saveContractor();
        Assertions.assertNotNull(main);

        Page<ContractorDTO> page = contractorService.search(ContractorBuilder.USERNAME, Pageable.unpaged(), langBG);
        Assertions.assertNotNull(page);
    }

    @Test
    public void givenFakeUsername_whenFindByUsername_thenThrowsEntityNotFoundException() {
        List<Contractor> contractors = contractorBuilder.saveContractors();
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> this.contractorService.findByUsername(FAKE_VALUE)
        );

        Assertions.assertEquals(
                "Entity '" + Contractor.class.getName() + "' with id/code='" +
                        FAKE_VALUE + "' not found", thrown.getMessage()
        );
    }

    @Test
    public void givenEmail_whenFindByEmail_thenReturnContractor() {
        Contractor main = contractorBuilder.saveContractor();
        Contractor contractor = contractorService.findByEmail(main.getEmail());
        Assertions.assertNotNull(contractor);
        Assertions.assertEquals(main.getUsername(), contractor.getUsername());
    }

    @Test
    public void givenFakeEmail_whenFindByMail_thenThrowsEntityNotFoundException() {
        List<Contractor> contractors = contractorBuilder.saveContractors();
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> this.contractorService.findByEmail(FAKE_VALUE)
        );

        Assertions.assertEquals(
                "Entity '" + Contractor.class.getName() + "' with id/code='" +
                        FAKE_VALUE + "' not found", thrown.getMessage()
        );
    }

    @Test
    public void givenVerificationTokenWhenRegisterUser_thenReturnVerificationToken() {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        String email = TEST_EMAIL.replaceFirst("contractor", "contractor1");
        VerificationToken token = VerificationToken.builder()
                .identifier(UUID.randomUUID().toString())
                .email(email)
                .fullName(USERNAME)
                .username(USERNAME + "1")
                .password(USERNAME + "1").build();

        token = this.contractorService.registerContractor(token, Language.builder().languageId("en").build());

        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.getId());
        Assertions.assertNotNull(token.getPassword());
        Assertions.assertEquals(email, token.getEmail());
    }

    @Test
    public void givenToken_whenRegisterContractor_thenReturnResendVerificationToken() {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        VerificationToken registerUserToken = VerificationToken.builder()
                .identifier(UUID.randomUUID().toString())
                .email("vladimir.angelov@bul-si.bg")
                .fullName("resendUser Full Name")
                .username("resendUsername")
                .password("resendUserPassword")
                .build();

        registerUserToken = this.contractorService
                .registerContractor(registerUserToken, Language.builder().languageId("en").build());
        String verToken = registerUserToken.getVerificationToken();

        Assertions.assertTrue(this.contractorService.resendVerificationToken(verToken,
                Language.builder().languageId("en").build()));
        Assertions.assertFalse(this.contractorService.resendVerificationToken(UUID.randomUUID().toString(),
                Language.builder().languageId("en").build()));
    }

    @Test
    public void givenVerificationToken_whenRegisterContractorAndChangePasswordWithVerifications_thenReturnToken() {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        String email = TEST_EMAIL.replaceFirst("contractor", "contractor21");

        VerificationToken registerUserToken = VerificationToken.builder()
                .identifier(UUID.randomUUID().toString())
                .email(email)
                .fullName(USERNAME)
                .username(USERNAME + "21")
                .password(USERNAME + "21").build();
        registerUserToken = this.contractorService
                .registerContractor(registerUserToken, Language.builder().languageId("en").build());

        Assertions.assertNotNull(registerUserToken);
        Assertions.assertNotNull(registerUserToken.getId());
        Assertions.assertNotNull(registerUserToken.getPassword());
        Assertions.assertEquals(email, registerUserToken.getEmail());

        String validateUserRegistrationTokenResult = this.contractorService
                .validateRegisterContractorToken(registerUserToken.getVerificationToken());

        Assertions.assertNotNull(validateUserRegistrationTokenResult);
        Assertions.assertEquals(Constants.TOKEN_VALID, validateUserRegistrationTokenResult);

        VerificationToken forgotPasswordToken = this.contractorService
                .forgotPassword(email, Language.builder().languageId("en").build());

        Assertions.assertNotNull(forgotPasswordToken);
        Assertions.assertNotNull(forgotPasswordToken.getId());
        Assertions.assertNotNull(forgotPasswordToken.getVerificationToken());
        Assertions.assertEquals(email, registerUserToken.getEmail());

        String validateForgotPasswordTokenResult = this.contractorService
                .validateForgotPasswordToken(forgotPasswordToken.getVerificationToken(), forgotPasswordToken.getUsername());
        Assertions.assertNotNull(validateForgotPasswordTokenResult);
        Assertions.assertEquals(Constants.TOKEN_VALID, validateForgotPasswordTokenResult);
    }

    @Test
    void givenContractor_whenSave_thenReturnContractor() {
        Contractor contractor = contractorBuilder.mockContractor();
        Assertions.assertNotNull(contractor);

        Contractor saved = this.contractorService.create(contractor);
        Assertions.assertNotNull(saved.getId());
        Assertions.assertEquals(contractor.getUsername(), saved.getUsername());
    }

    @Test
    void givenSomeRoles_whenRoles_thenReturnContractorsWithAllRoles() {
        Contractor contractorDb = contractorBuilder.saveContractor();
        Role role1 = userService.createRole(Role.builder().name(UUID.randomUUID().toString()).build());
        Role role2 = userService.createRole(Role.builder().name(UUID.randomUUID().toString()).build());
        Contractor contractor = contractorService.updateRoles(contractorDb.getId(), List.of(role1.getName(), role2.getName()));
        Assertions.assertNotNull(contractor);
        Assertions.assertEquals(contractorDb.getUsername(), contractor.getUsername());
        Assertions.assertNotNull(contractor.getRoles());
        Assertions.assertTrue(contractor.getRoles().size() == 2);
    }

    @Test
    void givenTokenForExistContractor_whenResendVerificationToken_thenReceiveToken() {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        VerificationToken registerContractorToken = VerificationToken.builder()
                .identifier(UUID.randomUUID().toString())
                .email("resendTest@domain.xyz")
                .fullName("resendUser")
                .username("resendUser")
                .password("resendUser")
                .build();

        registerContractorToken = this.contractorService
                .registerContractor(registerContractorToken, Language.builder().languageId("en").build());
        String verToken = registerContractorToken.getVerificationToken();

        Assertions.assertTrue(this.contractorService.resendVerificationToken(verToken,
                Language.builder().languageId("en").build()));

        Assertions.assertFalse(this.contractorService.resendVerificationToken(UUID.randomUUID().toString(),
                Language.builder().languageId("en").build()));
    }

    @Test
    void givenContractor_whenChangePasswordWithVerifications_thenSendVerification() {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        String email = "ang.vladimir@abv.bg"; //TEST_EMAIL.replaceFirst("test", "test3");

        VerificationToken registerContractorToken = VerificationToken.builder()
                .identifier(UUID.randomUUID().toString())
                .email(email)
                .fullName(USERNAME)
                .username(USERNAME + "2")
                .password(USERNAME + "2").build();

        registerContractorToken = this.contractorService.registerContractor(registerContractorToken, langEN);

        Assertions.assertNotNull(registerContractorToken);
        Assertions.assertNotNull(registerContractorToken.getId());
        Assertions.assertNotNull(registerContractorToken.getPassword());
        Assertions.assertEquals(email, registerContractorToken.getEmail());

        String validateUserRegistrationTokenResult = this.contractorService
                .validateRegisterContractorToken(registerContractorToken.getVerificationToken());

        Assertions.assertNotNull(validateUserRegistrationTokenResult);
        Assertions.assertEquals(Constants.TOKEN_VALID, validateUserRegistrationTokenResult);

        VerificationToken forgotPasswordToken = this.contractorService.forgotPassword(email, langEN);

        Assertions.assertNotNull(forgotPasswordToken);
        Assertions.assertNotNull(forgotPasswordToken.getId());
        Assertions.assertNotNull(forgotPasswordToken.getVerificationToken());
        Assertions.assertEquals(email, registerContractorToken.getEmail());

        String validateForgotPasswordTokenResult = this.contractorService
                .validateForgotPasswordToken(forgotPasswordToken.getVerificationToken(), forgotPasswordToken.getUsername());
        Assertions.assertNotNull(validateForgotPasswordTokenResult);
        Assertions.assertEquals(Constants.TOKEN_VALID, validateForgotPasswordTokenResult);
    }

    @Test
    void givenRegisterCode_whenFindByRegisterCode_thenReturnListContractorDto() {
        Classifier register = classifierBuilder.saveClassifier(langBG);

        Contractor contractor = contractorBuilder.saveContractor(
                addressBuilder.mockAddress(),
                branchBuilder.saveBranch(langBG),
                register
        );

        List<ContractorDTO> contractorDTOS = contractorService
                .findByRegisterCode(contractor.getRegisters().get(0).getCode(), langBG);

        Assertions.assertNotNull(contractorDTOS);
        Assertions.assertEquals(register.getCode(), contractorDTOS.get(0).getRegisterCodes().get(0).getCode());
    }

    @Test
    void givenRegisterCodeAndBranch_whenFindByRegisterCodeAndBranch_thenReturnPageContractorDto() {
        Classifier register = classifierBuilder.saveClassifier(langBG);
        Branch branch = branchBuilder.saveBranch(langBG);
        User user = userBuilder.saveUser(branch);

        Contractor contractor = contractorBuilder.saveContractor(addressBuilder.mockAddress(), branch, register);

        Page<ContractorDTO> contractorDTOS = contractorService.findByRegisterCodeAndBranchId(
                user.getUsername(),
                contractor.getRegisters().get(0).getCode(),
                Pageable.unpaged(),
                langBG);

        Assertions.assertNotNull(contractorDTOS);
        Assertions.assertEquals(register.getCode(), contractorDTOS.getContent().get(0).getRegisterCodes().get(0).getCode());
        Assertions.assertEquals(branch.getId(), contractorDTOS.getContent().get(0).getBranchId());
    }

    @Test
    void givenDivisionCodeAndBranch_whenFindByDivisionCodeAndBranch_thenReturnPageContractorDto() {
        Branch branch = branchBuilder.saveBranch(langBG);
        User user = userBuilder.saveUser(branch);
        Classifier parent = classifierBuilder.saveClassifierWithSubClassifiers(langBG);
        Classifier register = parent.getSubClassifiers().get(0);
        Contractor contractor = contractorBuilder.saveContractor(addressBuilder.mockAddress(), branch, register);

        Page<ContractorDTO> contractorDTOS = contractorService.findByDivisionCodeAndBranchId(
                user.getUsername(),
                contractor.getRegisters().get(0).getParent().getCode(),
                Pageable.unpaged(),
                langBG);

        Assertions.assertNotNull(contractorDTOS);
        Assertions.assertEquals(register.getCode(), contractorDTOS.getContent().get(0).getRegisterCodes().get(0).getCode());
        Assertions.assertEquals(branch.getId(), contractorDTOS.getContent().get(0).getBranchId());
    }

    @Test
    public void givenServiceType_whenFindByServiceType_thenReturnContractorsDto() {
        Contractor main = contractorBuilder.saveContractor(ServiceType.S2701);
        List<ContractorDTO> contractors = contractorService.findAllByContractorPaperServiceType(ServiceType.S2701.name(), langBG);
        Assertions.assertNotNull(contractors);
    }
}