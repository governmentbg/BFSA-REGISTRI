package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.LogoutRequest;
import bg.bulsi.bfsa.dto.UserDTO;
import bg.bulsi.bfsa.exception.InvalidUserDataException;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.RefreshTokenService;
import bg.bulsi.bfsa.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserController {

	private final UserService userService;
	private final RefreshTokenService refreshTokenService;

	@GetMapping("/")
//	@RolesAllowed({"ADMIN"})
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public Page<UserDTO> findAll(final Pageable pageable) {
		return userService.findAll(pageable).map(UserDTO::of);
	}

	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser() {
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return ResponseEntity.ok(UserDTO.of(userService.findByUsername(currentUser.getUsername())));
	}

	@GetMapping("/username/{username}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> findByUsername(@PathVariable final String username) {
		return ResponseEntity.ok(UserDTO.of(userService.findByUsername(username)));
	}

	@GetMapping("/{id}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> findById(@PathVariable final Long id) {
		return ResponseEntity.ok(UserDTO.of(userService.findById(id)));
	}

	@GetMapping(params = {"q"})
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public Page<UserDTO> search(@RequestParam(name = "q", required = false) final String param, Pageable pageable) {
		return userService.search(param, pageable).map(UserDTO::of);
	}

	@PostMapping("/signout")
	public ResponseEntity<?> signout(@RequestBody final LogoutRequest request) {
		refreshTokenService.deleteByUserId(request.getId(), 0);
		return ResponseEntity.ok().body("User signed out");
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@GetMapping("/roles")
	public ResponseEntity<?> findAllRoles() {
		return ResponseEntity.ok().body(userService.roles().stream().map(r -> r.getName()));
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@PutMapping("/{id}/roles")
	public ResponseEntity<?> roles(@PathVariable @NotNull final Long id,
										 @RequestBody @NotEmpty final List<String> roles) {
		return ResponseEntity.ok().body(UserDTO.of(userService.updateRoles(id, roles)));
	}

	@PutMapping("/{id}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> update(@PathVariable @NotNull final Long id, @RequestBody final UserDTO dto) {
		if (dto.getId() != null && !id.equals(dto.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		return ResponseEntity.ok().body(UserDTO.of(userService.update(id, UserDTO.to(dto))));
	}

	@PutMapping("/{id}/{enabled}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> setEnabled(@PathVariable @NotNull final Long id, @PathVariable @NotNull final Boolean enabled) {
		return ResponseEntity.ok().body(UserDTO.of(userService.setEnabled(id, enabled)));
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@GetMapping(path = "/{id}/history")
	public ResponseEntity<?> history(@PathVariable final Long id) {
		return ResponseEntity.ok(userService.findRevisions(id));
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@PostMapping(path = "/")
	public ResponseEntity<?> create( @RequestBody @Valid final UserDTO dto) {
		if (!StringUtils.hasText(dto.getUsername())) {
			throw new InvalidUserDataException("Username is empty!");
		}
		return ResponseEntity.ok(UserDTO.of(userService.create(UserDTO.to(dto))));
	}
}
