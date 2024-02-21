package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.ApiResponse;
import bg.bulsi.bfsa.dto.BuildInfoDTO;
import bg.bulsi.bfsa.dto.ForgotPasswordConfirmRequest;
import bg.bulsi.bfsa.dto.ForgotPasswordRequest;
import bg.bulsi.bfsa.dto.JwtResponse;
import bg.bulsi.bfsa.dto.LanguageDTO;
import bg.bulsi.bfsa.dto.RefreshTokenResponse;
import bg.bulsi.bfsa.dto.RegisterUserConfirmRequest;
import bg.bulsi.bfsa.dto.SignUpRequest;
import bg.bulsi.bfsa.dto.UserDetailsDTO;
import bg.bulsi.bfsa.exception.UserAlreadyExistAuthenticationException;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.RefreshToken;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.model.VerificationToken;
import bg.bulsi.bfsa.security.JwtUtils;
import bg.bulsi.bfsa.service.ContractorService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.service.MessageResourceService;
import bg.bulsi.bfsa.service.RefreshTokenService;
import bg.bulsi.bfsa.util.Constants;
import bg.bulsi.bfsa.util.LocaleUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final RefreshTokenService refreshTokenService;
	private final ContractorService contractorService;
	private final BuildProperties buildProperties;
	private final LanguageService languageService;
	private final MessageResourceService msgRes;

	@GetMapping("/build-info")
	public ResponseEntity<?> buildInfo() {
		return ResponseEntity.ok(BuildInfoDTO.of(buildProperties));
	}

	@PostMapping(path = "/login", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
	public ResponseEntity<?> authenticateUser(final String username, final String password) {
		return authenticate(username + " ", password, "0");
	}

	@PostMapping(path = "/signin", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
	public ResponseEntity<?> authenticateContractor(final String username, final String password) {
		return authenticate(username, password, "1");
	}

	private ResponseEntity<?> authenticate(final String username, final String password, final String type) {
		if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
			return ResponseEntity.badRequest().body("Incorrect credentials!");
		}
		Authentication auth;

		try {
			auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (BadCredentialsException e) {
			return ResponseEntity.badRequest().body("Incorrect credentials!");
		}

		UserDetailsDTO userDetailsDTO = (UserDetailsDTO) auth.getPrincipal();
		final String jwt = jwtUtils.generateToken(userDetailsDTO);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetailsDTO.getId());
		List<String> roles = userDetailsDTO.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		return ResponseEntity.ok(JwtResponse.builder()
				.type(type)
				.token(jwt)
				.refreshToken(refreshToken.getRefreshToken())
				.userId(userDetailsDTO.getId())
				.branchId(userDetailsDTO.getBranchId())
				.directorateCode(userDetailsDTO.getDirectorateCode())
				.email(userDetailsDTO.getEmail())
				.username(userDetailsDTO.getUsername())
				.fullName(userDetailsDTO.getFullName())
				.roles(roles).build()
		);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerContractor(@Valid @RequestBody final SignUpRequest request,
												@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		try {
			contractorService.registerContractor(SignUpRequest.buildVerificationToken(request), language);
		} catch (UserAlreadyExistAuthenticationException e) {
			log.error("Exception Occurred", e);
			return new ResponseEntity<>(new ApiResponse(false, msgRes.get("forgot.password.email.in.use", language)), HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok().body(new ApiResponse(true,
				MessageFormat.format(msgRes.get("forgot.password.success", language),
						request.getEmail(), VerificationToken.EXPIRATION / 60)));
	}

	@PutMapping("/signup-confirm")
	public ResponseEntity<?> registerContractorConfirm(@NotEmpty @RequestBody final RegisterUserConfirmRequest request) {
		final String result = contractorService.validateRegisterContractorToken(request.getToken());
		return ResponseEntity.ok().body(new ApiResponse(true, result));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@NotEmpty @RequestBody final ForgotPasswordRequest request,
											@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
			Language language = LocaleUtil.getLanguage(locale, languageService);
		try {
			contractorService.forgotPassword(request.getEmail(), language);
		} catch (UserAlreadyExistAuthenticationException e) {
			log.error("Exception Occurred", e);
			return new ResponseEntity<>(new ApiResponse(false, msgRes.get("global.something.went.wrong", language)), HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.ok().body(new ApiResponse(true,
				MessageFormat.format(msgRes.get("forgot.password.success", language),
						request.getEmail(), VerificationToken.EXPIRATION / 60)));
	}

	@PutMapping("/forgot-password-confirm")
	public ResponseEntity<?> forgotPasswordConfirm(@Valid @RequestBody final ForgotPasswordConfirmRequest request) {
		final String result = contractorService.validateForgotPasswordToken(request.getToken(), request.getPassword());
		return ResponseEntity.ok().body(new ApiResponse(true, result));
	}

	@ResponseBody
	@PostMapping("/token/resend")
	public ResponseEntity<?> resendRegistrationToken(@NotEmpty @RequestBody final Map<String, String> request,
													 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		if (!contractorService.resendVerificationToken(request.get("token"), language)) {
			return new ResponseEntity<>(new ApiResponse(false, msgRes.get("global.token.not.found", language)), HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok().body(new ApiResponse(true, Constants.SUCCESS));
	}
	
	@PostMapping("/token/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody final Map<String, String> request) {
		RefreshToken token = refreshTokenService.findByRefreshToken(request.get("token"));

		if(token != null && refreshTokenService.verifyExpiration(token) != null) {
			User user = token.getUser();
			Map<String, Object> claims = new HashMap<>();
			claims.put("ROLES", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
			String jwt = jwtUtils.createToken(claims, user.getUsername());

			return ResponseEntity.ok(new RefreshTokenResponse("Bearer", jwt, request.get("token")));
		}

		return ResponseEntity.badRequest().body("The token has expired");
	}

	@GetMapping(path = "/langs")
	public ResponseEntity<?> findAllLanguages() {
		List<Language> languages = languageService.findAll();
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(LanguageDTO.of(languages))
						.totalCount(languages.size()).build()
		);
	}

}
