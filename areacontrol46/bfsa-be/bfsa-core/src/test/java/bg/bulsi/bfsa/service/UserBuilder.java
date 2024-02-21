package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.RoleRepository;
import bg.bulsi.bfsa.security.RolesConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static bg.bulsi.bfsa.service.BaseServiceTest.langBG;

@Service
public class UserBuilder {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService service;
    @Autowired
    private BranchBuilder branchBuilder;

    public User saveUser() {
        return saveUser(null);
    }

    public User saveUser(final Branch branch) {

        return service.create(User.builder()
                .username(UUID.randomUUID().toString())
                .email(UUID.randomUUID().toString())
                .enabled(true)
                .fullName(UUID.randomUUID().toString())
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(roleRepository.findByName(RolesConstants.ROLE_ADMIN)))
                .branch(branch != null ? branch : branchBuilder.saveBranch(langBG)).build());
    }

    public User mockUser(Branch branch) {
        User entity = User.builder()
//                .id(new Random().nextLong(1000))
                .email(UUID.randomUUID() + "@domain.xyz")
                .fullName(UUID.randomUUID().toString())
                .username(UUID.randomUUID().toString())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .identifier(UUID.randomUUID().toString())
                .branch(branch)
                .enabled(true)
                .build();
        Role roleAdmin = Role.builder().name(RolesConstants.ROLE_ADMIN).build();
        roleAdmin.getUsers().add(entity);
        entity.getRoles().add(roleAdmin);
        return entity;
    }
}
