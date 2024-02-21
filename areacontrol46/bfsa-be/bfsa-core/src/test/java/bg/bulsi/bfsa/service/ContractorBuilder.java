package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static bg.bulsi.bfsa.service.BaseServiceTest.langBG;

@Service
public class ContractorBuilder {
    @Autowired
    private ContractorService service;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BranchBuilder branchBuilder;

    public static final Boolean ENABLED = true;
    public static final String USERNAME = "contractor ";
    private static final String TEST_FULL_NAME = " Contractor Full Name";
    private static final String TEST_EMAIL = "@domain.xyz";

    public Contractor saveContractor() {
        return saveContractor(null);
    }

    public Contractor saveContractor(ServiceType serviceType) {
        Contractor mockContractor = mockContractor();
        mockContractor.setRoles(Set.of(userService.createRole(Role.builder().name(UUID.randomUUID().toString()).build())));
        mockContractor.setBranch(branchBuilder.saveBranch(langBG));
        mockContractor.getContractorPapers().get(0).setServiceType(serviceType);
        return service.create(mockContractor);
    }

    public Contractor mockContractor(final Address address, final Branch branch, final Classifier register) {
        Contractor mockContractor = mockContractor();
//        mockContractor.setRoles(Set.of(userService.createRole(Role.builder().name(UUID.randomUUID().toString()).build())));
        mockContractor.getAddresses().add(address);
        mockContractor.setBranch(branch);
        mockContractor.getRegisters().add(register);

        return mockContractor;
    }

    public Contractor saveContractor(final Address address, final Branch branch, final Classifier register) {
        return service.create(mockContractor(address, branch, register));
    }

    public List<Contractor> saveContractors() {
        List<Contractor> saved = new ArrayList<>();
        saved.add(saveContractor());
        saved.add(saveContractor());
        saved.add(saveContractor());
        saved.add(saveContractor());
        return saved;
    }

    public Contractor mockContractor() {
        Contractor contractor = Contractor.builder()
                .email(UUID.randomUUID().toString().substring(5) + TEST_EMAIL)
                .fullName(UUID.randomUUID().toString().substring(5) + TEST_FULL_NAME)
                .username(USERNAME + UUID.randomUUID().toString().substring(5))
                .password(passwordEncoder.encode(USERNAME))
                .identifier(UUID.randomUUID().toString().substring(5) + USERNAME)
                .entityType(EntityType.PHYSICAL)
//                .roles(Set.of((Role.builder().name(UUID.randomUUID().toString()).build())))
                .enabled(ENABLED)
                .build();

        contractor.getContractorPapers().add(mockContractorPaper(contractor));
        contractor.getContractorPapers().add(mockContractorPaper(contractor));
        contractor.getContractorPapers().add(mockContractorPaper(contractor));

        return contractor;
    }

    private ContractorPaper mockContractorPaper(final Contractor contractor) {
        Random rand = new Random();
        int random = rand.nextInt(10);
        String regNum = random + LocalDate.now().minusDays(10L).toString();

        return ContractorPaper.builder()
                .contractor(contractor)
                .regDate(LocalDate.now())
                .serviceType(ServiceType.S2701)
                .regNumber(regNum)
                .build();
    }
}
