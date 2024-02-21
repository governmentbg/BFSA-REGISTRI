package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ForgotPasswordConfirmRequest;
import bg.bulsi.bfsa.dto.SignUpRequest;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.RefreshToken;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.model.VerificationToken;
import bg.bulsi.bfsa.repository.ContractorRepository;
import bg.bulsi.bfsa.repository.RefreshTokenRepository;
import bg.bulsi.bfsa.repository.RoleRepository;
import bg.bulsi.bfsa.repository.UserRepository;
import bg.bulsi.bfsa.repository.VerificationTokenRepository;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.MailService;
import bg.bulsi.bfsa.util.Constants;
import bg.bulsi.bfsa.util.GeneralUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static bg.bulsi.bfsa.security.RolesConstants.ROLE_CONTRACTOR;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContractorRepository contractorRepository;
    @MockBean
    private VerificationTokenRepository mockVerificationTokenRepository;
    @MockBean
    private RefreshTokenRepository refreshTokenRepository;
    @MockBean
    private MailService mailService;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@domain.xyz";
    private static final String TEST_USERNAME = "authName";
    private static final String TEST_EMAIL = "auth@domain.xcb";
    private static final String ANOTHER_USERNAME = "another";
    private static final String CONTRACTOR_USERNAME = "contractor";
    private static final String ANOTHER_EMAIL = "another@domain.xyz";
    private static final String CONTRACTOR_EMAIL = "contractor@domain.xyz";

    @BeforeAll
    void init() {
        if (!userRepository.existsByUsernameIgnoreCase(ANOTHER_USERNAME)) {
            User user = User.builder()
                    .identifier(UUID.randomUUID().toString())
                    .username(ANOTHER_USERNAME)
                    .email(ANOTHER_EMAIL)
                    .enabled(true)
                    .fullName(ANOTHER_USERNAME)
                    .password(passwordEncoder.encode(ANOTHER_USERNAME)).build();
            user.getRoles().add(roleRepository.findByName(RolesConstants.ROLE_ADMIN));
            userRepository.save(user);
        }

        if (!contractorRepository.existsByUsernameIgnoreCase(CONTRACTOR_USERNAME)) {
            Role r = roleRepository.save(Role.builder().name(ROLE_CONTRACTOR).build());
            Contractor contractor = Contractor.builder()
                    .identifier(UUID.randomUUID().toString())
                    .username(CONTRACTOR_USERNAME)
                    .email(CONTRACTOR_EMAIL)
                    .enabled(true)
                    .fullName(CONTRACTOR_USERNAME)
                    .password(passwordEncoder.encode(CONTRACTOR_USERNAME)).build();
            r.getContractors().add(contractor);
            contractor.getRoles().add(r);
            contractorRepository.save(contractor);
        }
    }

    @Test
    void givenUsernameAndPassword_whenAuthenticateUser_thenReturnJwtResponse() throws Exception {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        Mockito.when(refreshTokenRepository.save(any())).thenReturn(
                RefreshToken.builder()
                        .refreshToken(UUID.randomUUID().toString())
                        .user(User.builder()
                                .identifier(UUID.randomUUID().toString())
                                .username(ANOTHER_USERNAME)
                                .password(ANOTHER_USERNAME)
                                .email(ANOTHER_EMAIL)
                                .fullName(ANOTHER_USERNAME).build())
                        .expDate(Instant.now().plus(24, ChronoUnit.HOURS))
                        .build()
        );
        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", ANOTHER_USERNAME + " ")
                        .param("password", ANOTHER_USERNAME)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("1")))
                .andExpect(jsonPath("$.username", is(ANOTHER_USERNAME)))
                .andExpect(jsonPath("$.email", is(ANOTHER_EMAIL)))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.roles[0]", is(RolesConstants.ROLE_ADMIN)))
                .andReturn();
    }

    @Test
    void givenUsernameAndPassword_whenTryToAuthenticateUserWithWrongPass_thenReturnIncorrectCredentials() throws Exception {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        Mockito.when(refreshTokenRepository.save(any())).thenReturn(
                RefreshToken.builder()
                        .refreshToken(UUID.randomUUID().toString())
                        .user(User.builder()
                                .identifier(UUID.randomUUID().toString())
                                .username(ANOTHER_USERNAME)
                                .password(ANOTHER_USERNAME)
                                .email(ANOTHER_EMAIL)
                                .fullName(ANOTHER_USERNAME).build())
                        .expDate(Instant.now().plus(24, ChronoUnit.HOURS))
                        .build()
        );
        MvcResult result = mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", ANOTHER_USERNAME + " ")
                        .param("password", "FAKE_VALUE")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("Incorrect credentials!"));
    }

    @Test
    void givenSignUpRequest_whenRegisterUser_thenReturnApiResponse() throws Exception {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        final SignUpRequest request = SignUpRequest.builder()
                .identifier(UUID.randomUUID().toString())
                .username(TEST_USERNAME)
                .password(TEST_USERNAME)
                .matchingPassword(TEST_USERNAME)
                .email(TEST_EMAIL)
                .fullName(TEST_USERNAME).build();

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString(TEST_EMAIL)))
                .andReturn();
    }

    @Test
    void givenSignUpRequest_whenTryToRegisterExistingUser_thenErrorMessage() throws Exception {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        final SignUpRequest request = SignUpRequest.builder()
                .identifier(UUID.randomUUID().toString())
                .username(CONTRACTOR_USERNAME)
                .password(CONTRACTOR_USERNAME)
                .matchingPassword(CONTRACTOR_USERNAME)
                .email(CONTRACTOR_EMAIL)
                .fullName(CONTRACTOR_EMAIL).build();

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message",
                        is("Имейл адресът вече се използва!")))
                .andReturn();
    }

    @Test
    void givenTokenString_whenRegisterUserConfirm_thenReturnApiResponse() throws Exception {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        Mockito.when(mockVerificationTokenRepository.findByVerificationToken(Mockito.anyString())).thenReturn(
                VerificationToken.builder()
                        .identifier(UUID.randomUUID().toString())
                        .username(TEST_USERNAME)
                        .password(TEST_USERNAME)
                        .email(TEST_EMAIL)
                        .fullName(TEST_USERNAME)
                        .verificationToken(UUID.randomUUID().toString())
                        .expDate(GeneralUtils.calculateExpiryDate(VerificationToken.EXPIRATION))
                        .build()
        );

        final Map<String, String> request = new HashMap<>();
        request.put("token", "token");
        mockMvc.perform(put("/auth/signup-confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(Constants.TOKEN_VALID)))
                .andReturn();
    }

    @Test
    void givenEmail_whenForgotPassword_thenReturnApiResponse() throws Exception {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());
//
        final Map<String, String> request = new HashMap<>();
        request.put("email", CONTRACTOR_EMAIL);

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString(CONTRACTOR_EMAIL)))
                .andReturn();
    }

    @Test
    void givenForgotPasswordConfirmRequest_whenForgotPasswordConfirm_thenReturnApiResponse() throws Exception {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        Mockito.when(mockVerificationTokenRepository.findByVerificationToken(Mockito.anyString())).thenReturn(
                VerificationToken.builder()
                        .identifier(UUID.randomUUID().toString())
                        .username(CONTRACTOR_USERNAME)
                        .password(CONTRACTOR_USERNAME)
                        .email(CONTRACTOR_USERNAME)
                        .fullName(CONTRACTOR_USERNAME)
                        .verificationToken(UUID.randomUUID().toString())
                        .expDate(GeneralUtils.calculateExpiryDate(VerificationToken.EXPIRATION))
                        .build()
        );
        ForgotPasswordConfirmRequest request = ForgotPasswordConfirmRequest.builder()
                .token("token")
                .password("password12")
                .matchingPassword("password12").build();
//
        mockMvc.perform(put("/auth/forgot-password-confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(Constants.TOKEN_VALID)))
                .andReturn();
    }

    @Test
    void testResendRegistrationToken() throws Exception {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        Mockito.when(mockVerificationTokenRepository.findByVerificationToken(Mockito.anyString())).thenReturn(
                VerificationToken.builder()
                        .identifier(UUID.randomUUID().toString())
                        .username(TEST_USERNAME)
                        .password(TEST_USERNAME)
                        .email(TEST_EMAIL)
                        .fullName(TEST_USERNAME)
                        .verificationToken(UUID.randomUUID().toString())
                        .expDate(GeneralUtils.calculateExpiryDate(VerificationToken.EXPIRATION))
                        .build()
        );
        final Map<String, String> request = new HashMap<>();
        request.put("token", "token");

        mockMvc.perform(post("/auth/token/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(Constants.SUCCESS)))
                .andReturn();
    }

    @Test
    void testResendRegistrationTokenThrowException() throws Exception {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        Mockito.when(mockVerificationTokenRepository.findByVerificationToken(Mockito.anyString())).thenReturn(
                VerificationToken.builder()
                        .identifier(UUID.randomUUID().toString())
                        .username(TEST_USERNAME)
                        .password(TEST_USERNAME)
                        .email(TEST_EMAIL)
                        .fullName(TEST_USERNAME)
                        .verificationToken(UUID.randomUUID().toString())
                        .expDate(GeneralUtils.calculateExpiryDate(VerificationToken.EXPIRATION))
                        .build()
        );
        final Map<String, String> request = new HashMap<>();
        request.put("token", "token");

        mockMvc.perform(post("/auth/token/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(Constants.SUCCESS)))
                .andReturn();
    }

    @Test
    void testRefreshToken() throws Exception {
        doNothing().when(mailService).sendRegisterUserVerificationToken(any(), any());

        Mockito.when(refreshTokenRepository.findByRefreshToken(Mockito.anyString())).thenReturn(
                RefreshToken.builder()
                        .refreshToken(UUID.randomUUID().toString())
                        .user(User.builder()
                                .identifier(UUID.randomUUID().toString())
                                .username(ADMIN_USERNAME)
                                .password(ADMIN_USERNAME)
                                .email(ADMIN_EMAIL)
                                .fullName(ADMIN_USERNAME).build())
                        .expDate(Instant.now().plus(24, ChronoUnit.HOURS))
                        .build()
        );

        final Map<String, String> request = new HashMap<>();
        request.put("token", "token");

        mockMvc.perform(post("/auth/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();
    }
}