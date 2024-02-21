package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.RoleRepository;
import bg.bulsi.bfsa.security.RolesConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class UserServiceTest extends BaseServiceTest {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserBuilder userBuilder;

    public static final String ADMIN_EMAIL = "admin@domain.xyz";

    @Test
    void given_ExistingUser_whenUpdate_thenReturnUpdatedUser() {

        User created = userBuilder.saveUser();
        Assertions.assertNotNull(created);

        Role roleMock = roleRepository.save(Role.builder().name("MOCK").build());
        Assertions.assertNotNull(roleMock);

        User user = userService.findByUsername(created.getUsername());
        Assertions.assertNotNull(user);

        int roleSizeBeforeUpdate = user.getRoles().size();

        user.setFullName("updated full name");
        user.getRoles().add(roleMock);
        User updatedUser = userService.update(user.getId(), user);
        Assertions.assertNotNull(updatedUser);

        int roleSizeAfterUpdate = updatedUser.getRoles().size();

        Assertions.assertEquals("updated full name", updatedUser.getFullName());
        Assertions.assertEquals(roleSizeAfterUpdate, roleSizeBeforeUpdate + 1);
        Assertions.assertTrue(updatedUser.getRoles()
                .stream()
                .map(Role::getName)
                .toList().contains(roleMock.getName()));
    }

    @Test
    void assertThatCanChangeUserEnabled() {
        User created = userBuilder.saveUser();
        Assertions.assertNotNull(created);

        User updatedUser = userService.setEnabled(created.getId(), false);

        Assertions.assertEquals(updatedUser.getEnabled(), false);
    }

    @Test
    void assertThrowExceptionWhenEnabledParamIsNull() {
        User created = userBuilder.saveUser();
        Assertions.assertNotNull(created);

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class, () -> userService.setEnabled(created.getId(), null)
        );

        Assertions.assertEquals("Boolean param enabled is required", exception.getMessage());
    }

    @Test
    void givenUsername_whenFindByUsername_thenReturnUser() {
        User created = userBuilder.saveUser();
        Assertions.assertNotNull(created);

        User user = userService.findByUsername(created.getUsername());
        Assertions.assertNotNull(user);
        Assertions.assertEquals(created.getEmail(), user.getEmail());
    }

    @Test
    void givenSearchString_whenSearch_thenReturnUserDetailsPageResponse() {
        Page<User> page = userService.search("ad", Pageable.unpaged());
        Assertions.assertNotNull(page);
        Assertions.assertTrue(page.getTotalElements() > 0);
        Assertions.assertNotNull(page.getContent().stream().filter(u -> ADMIN_EMAIL.equals(u.getEmail())).findFirst().orElse(null));
    }

    @Test
    void givenFakeUsername_whenFindByUsername_thenThrowsEntityNotFoundException() {
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> userService.findByUsername(FAKE_VALUE)
        );

        Assertions.assertEquals(
                "Entity '" + User.class.getName() + "' with id/code='" +
                        FAKE_VALUE + "' not found", thrown.getMessage()
        );
    }

    @Test
    void givenEmail_whenfindByMail_thenReturnUser() {
        User created = userBuilder.saveUser();
        Assertions.assertNotNull(created);

        User user = userService.findByEmail(created.getEmail());
        Assertions.assertNotNull(user);
        Assertions.assertEquals(created.getUsername(), user.getUsername());
    }

    @Test
    void givenFakeEmail_whenFindByEmail_thenThrowsEntityNotFoundException() {
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> userService.findByEmail(FAKE_VALUE)
        );

        Assertions.assertEquals(
                "Entity '" + User.class.getName() + "' with id/code='" +
                        FAKE_VALUE + "' not found", thrown.getMessage()
        );
    }

    @Test
    void givenUser_whenSaveUser_thenReturnUser() {
        User created = userBuilder.saveUser();
        Assertions.assertNotNull(created);

        Assertions.assertNotNull(created);
        Assertions.assertNotNull(created.getId());
    }

    @Test
    void givenSomeRoles_whenRoles_thenReturnUserWithAllRoles() {
        User created = userBuilder.saveUser();
        Assertions.assertNotNull(created);

        Role roleUser = roleRepository.findByName(RolesConstants.ROLE_USER);
        if (roleUser == null) {
            roleRepository.save(Role.builder().name(RolesConstants.ROLE_USER).build());
        }

        User user = userService.updateRoles(created.getId(), List.of(RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_USER));
        Assertions.assertNotNull(user);
        Assertions.assertEquals(created.getUsername(), user.getUsername());
        Assertions.assertNotNull(user.getRoles());
        Assertions.assertEquals(2, user.getRoles().size());
        assertThat(user.getRoles().stream().map(Role::getName)).contains(RolesConstants.ROLE_USER);
        assertThat(user.getRoles().stream().map(Role::getName)).contains(RolesConstants.ROLE_ADMIN);
    }
}