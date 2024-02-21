package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.UserDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService service;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String USERNAME = "test";
    private static final String USERNAME_UPDATED = "test_updated";

    @Test
    void givenSearchString_whenSearch_thenReturnAllUserDTOs() throws Exception {
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(mockUser());
        mockUsers.add(mockUser());
        when(service.search(any(), any())).thenReturn(new PageImpl<>(mockUsers));

        mockMvc.perform(get("/users?q={1}", "tes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", greaterThan(1)))
                .andExpect(jsonPath("$.content[*].email", containsInAnyOrder(mockUsers.get(0).getEmail(), mockUsers.get(1).getEmail())))
                .andReturn();
    }

    @Test
    void givenUsedId_whenFindById_thenReturnUserDTO() throws Exception {
        User mockUser = mockUser();
        when(service.findById(any())).thenReturn(mockUser);

        mockMvc.perform(get("/users/{id}", mockUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(mockUser.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(mockUser.getEnabled())))
                .andExpect(jsonPath("$.username", is(mockUser.getUsername())))
                .andExpect(jsonPath("$.identifier", is(mockUser.getIdentifier())))
                .andReturn();
    }

    @Test
    void givenFakeId_whenFindById_thenThrowEntityNotFoundException() throws Exception {
        when(service.findById(0L)).thenThrow(new EntityNotFoundException(User.class, FAKE_VALUE));
        mockMvc.perform(get("/users/{id}", FAKE_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is(EntityNotFoundException.class.getName())))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message",
                        is("Entity '" + User.class.getName() + "' with id/code='" + FAKE_VALUE + "' not found")))
                .andReturn();
    }

    @Test
    void givenUsername_whenFindByUsername_thenReturnUserDTO() throws Exception {
        User mockUser = mockUser();
        when(service.findByUsername(any())).thenReturn(mockUser);

        mockMvc.perform(get("/users/username/{username}", mockUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(mockUser.getId().intValue())))
                .andExpect(jsonPath("$.enabled", is(mockUser.getEnabled())))
                .andExpect(jsonPath("$.username", is(mockUser.getUsername())))
                .andExpect(jsonPath("$.identifier", is(mockUser.getIdentifier())))
                .andReturn();
    }

    @Test
    void givenFakeUsername_whenFindByUsername_thenThrowEntityNotFoundException() throws Exception {
        when(service.findByUsername("FAKE_VALUE")).thenThrow(new EntityNotFoundException(User.class, "FAKE_VALUE"));
        mockMvc.perform(get("/users/username/{username}", "FAKE_VALUE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is(EntityNotFoundException.class.getName())))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message",
                        is("Entity '" + User.class.getName() + "' with id/code='" + "FAKE_VALUE" + "' not found")))
                .andReturn();
    }

    @Test
    void whenFindAll_thenReturnAllUserDTOs() throws Exception {
        List<User> mockUsers = new ArrayList<>();
        mockUsers.add(mockUser());
        mockUsers.add(mockUser());
        when(service.findAll(any())).thenReturn(new PageImpl<>(mockUsers));

        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", greaterThan(1)))
                .andExpect(jsonPath("$.content[*].email", containsInAnyOrder(mockUsers.get(0).getEmail(), mockUsers.get(1).getEmail())))
                .andReturn();
    }

    @Test
    void givenRole_whenUpdateRoles_thenReturnUserWithUpdatedRoles() throws Exception {
        User mockUser = mockUser();
        mockUser.getRoles().add(Role.builder().name("DEBUG").build());
        when(service.updateRoles(any(), any())).thenReturn(mockUser);

        MvcResult result = mockMvc.perform(put("/users/{id}/roles", mockUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"DEBUG\"]"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        final UserDTO returnedUser = objectMapper.readValue(content, UserDTO.class);

        Assertions.assertNotNull(returnedUser);
        Assertions.assertEquals(2, returnedUser.getRoles().size());
        Assertions.assertTrue(returnedUser.getRoles().contains("DEBUG"));
    }

    @Test
    void givenEmptyRoleArray_whenUpdateRoles_thenThrowRuntimeException() throws Exception {
        when(service.updateRoles(any(), any())).thenThrow(new RuntimeException("At least one role is required"));

        mockMvc.perform(put("/users/{id}/roles", new Random().nextLong(1000))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]") )
                .andExpect(status().isInternalServerError())
                .andExpect((jsonPath("$.exception", is("java.lang.RuntimeException"))))
                .andExpect((jsonPath("$.error", is("Internal Server Error"))))
                .andExpect((jsonPath("$.message", is("At least one role is required"))))
                .andExpect((jsonPath("$.exceptionCode", is("RuntimeException"))));

//        NestedServletException exception = Assertions.assertThrows(NestedServletException.class,
//                () -> mockMvc.perform(put("/users/{id}/roles", UUID.randomUUID())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("[]"))
//        );
//
//        Assertions.assertEquals("Request processing failed; nested exception is java.lang.RuntimeException:" +
//                " At least one role is required", exception.getMessage());
    }

    @Test
    void givenIdAndFullName_whenUpdate_thenReturnUpdatedUser() throws Exception {
        User mockUser = mockUser();
        mockUser.setEnabled(false);
        mockUser.setFullName(USERNAME_UPDATED + " " + USERNAME_UPDATED);
        when(service.update(any(), any())).thenReturn(mockUser);

        mockMvc.perform(put("/users/{id}", mockUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserDTO.builder()
                                .id(mockUser.getId())
                                .username(mockUser.getUsername())
                                .enabled(mockUser.getEnabled())
                                .fullName(mockUser.getFullName()).build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", is(mockUser.getEnabled())))
                .andExpect(jsonPath("$.fullName", is(mockUser.getFullName())))
                .andReturn();
    }

    @Test
    void givenUserAndDifferentId_whenUpdate_thenThrowRuntimeException() throws Exception {
        User mockUser = mockUser();
        when(service.update(any(), any())).thenReturn(mockUser);

        mockMvc.perform(put("/users/{id}", mockUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserDTO.builder()
                                .id(0L)
                                .username(USERNAME + "_updated")
                                .email("test@domain.xyz")
                                .enabled(true)
                                .fullName(USERNAME + "new_full name").build())))
                .andExpect(status().isInternalServerError())
                .andExpect((jsonPath("$.exception", is("java.lang.RuntimeException"))))
                .andExpect((jsonPath("$.error", is("Internal Server Error"))))
                .andExpect((jsonPath("$.message", is("Path variable id doesn't match RequestBody parameter id"))))
                .andExpect((jsonPath("$.exceptionCode", is("RuntimeException"))));
    }

    @Test
    void whenFindAllRoles_theReturnAllRoles() throws Exception {
        List<Role> roles = List.of(
                Role.builder().name(RolesConstants.ROLE_ADMIN).build(),
                Role.builder().name(RolesConstants.ROLE_USER).build()
        );
        when(service.roles()).thenReturn(roles);

        mockMvc.perform(get("/users/roles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]", is(roles.get(0).getName())))
                .andExpect(jsonPath("$.[1]", is(roles.get(1).getName())))
                .andReturn();
    }

    @Test
    void givenUser_whenUpdate_thenReturnUpdatedHistory() throws Exception {
        User mockUser = mockUser();
        mockUser.setFullName(USERNAME + "_history " + USERNAME + "_history");
        List<UserDTO> mockUsers = List.of(UserDTO.of(mockUser()), UserDTO.of(mockUser));

        when(service.findRevisions(any())).thenReturn(mockUsers);

        mockMvc.perform(get("/users/{id}/history", mockUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(1)))
                .andReturn();
    }

    @Test
    void givenUser_whenSetEnabled_thenReturnUpdatedUser() throws Exception {
        User mockUser = mockUser();
        mockUser.setEnabled(false);
        when(service.setEnabled(any(), any())).thenReturn(mockUser);

        mockMvc.perform(put("/users/{id}/{enabled}", mockUser.getId(), false)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", is(false)))
                .andReturn();
    }

    private User mockUser() {
        User entity = User.builder()
                .id(new Random().nextLong(1000))
                .email(UUID.randomUUID() + "@domain.xyz")
                .fullName(USERNAME + " " + USERNAME)
                .username(USERNAME)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .identifier(UUID.randomUUID().toString())
                .branch(Branch.builder().id(new Random().nextLong(1000)).build())
                .enabled(true)
                .build();
        Role roleAdmin = Role.builder().name(RolesConstants.ROLE_ADMIN).build();
        roleAdmin.getUsers().add(entity);
        entity.getRoles().add(roleAdmin);
        return entity;
    }
}