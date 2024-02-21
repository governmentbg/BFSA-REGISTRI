package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.RevisionMetadataDTO;
import bg.bulsi.bfsa.dto.UserDTO;
import bg.bulsi.bfsa.exception.EntityAlreadyExistException;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.exception.InvalidUserDataException;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.RoleRepository;
import bg.bulsi.bfsa.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserService {

	private final UserRepository repository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final BranchService branchService;
	private final ClassifierService classifierService;

	public User findById(final Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(User.class, id));
	}

	public Role findRoleById(final Long id) {
		return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Role.class, id));
	}

	public Role findRoleByIdOrNull(final Long id) {
		return roleRepository.findById(id).orElse(null);
	}

	public User findByIdOrNull(final Long id) {
		return repository.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	public Page<User> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public User findByUsername(final String username) {
		return repository.findByUsernameIgnoreCase(
				username.toLowerCase()).orElseThrow(() -> new EntityNotFoundException(User.class, username)
		);
	}

	public User findByUsernameOrNull(final String username) {
		return repository.findByUsernameIgnoreCase(username).orElse(null);
	}

	public User findByEmail(final String email) {
		return repository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(User.class, email));
	}

	@Transactional
	public User create(final User user) {
		if (user == null || !StringUtils.hasText(user.getUsername())) {
			throw new InvalidUserDataException("User or username is empty!");
		}

		User regUser = findByUsernameOrNull(user.getUsername());
		if (regUser != null) {
			throw new EntityAlreadyExistException(User.class, regUser.getId());
		}

		user.setBranch(user.getBranch() != null ? branchService.findById(user.getBranch().getId()) : null);

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		if (!CollectionUtils.isEmpty(user.getRoles())) {
			Set<Role> roles = new HashSet<>();
			user.getRoles().forEach(r -> roles.add(roleRepository.findByName(r.getName())));
			user.setRoles(roles);
		}

		user.setDirectorate(user.getDirectorate() != null
				? classifierService.findByCode(user.getDirectorate().getCode())
				: null);

        return repository.save(user);
	}

	@Transactional
	public User update(final Long id, final User user) {
		if (id == null && user.getId() == null) {
			throw new RuntimeException("User ID is required");
		}
		User regUser = findById(id != null && id > 0 ? id : user.getId());

		if (StringUtils.hasText(user.getFullName())) {
			regUser.setFullName(user.getFullName());
		}
		if (StringUtils.hasText(user.getIdentifier())) {
			regUser.setIdentifier(user.getIdentifier());
		}
		if (StringUtils.hasText(user.getPassword())) {
			regUser.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		regUser.setEnabled(user.getEnabled());

		regUser.getRoles().clear();
		user.getRoles().forEach(r -> regUser.getRoles().add(roleRepository.findByName(r.getName())));

		regUser.setBranch(
				user.getBranch() != null && user.getBranch().getId() != null && user.getBranch().getId() > 0
						? branchService.findById(user.getBranch().getId())
						: null
		);

		regUser.setDirectorate(
				user.getDirectorate() != null && StringUtils.hasText(user.getDirectorate().getCode())
						? classifierService.findByCode(user.getDirectorate().getCode())
						: null
		);

		return repository.save(regUser);
	}

	@Transactional
	public User updateRoles(final Long id, final List<String> roles) {
		if (CollectionUtils.isEmpty(roles)) {
			throw new RuntimeException("At least one role is required");
		}
		User user = findById(id);
		user.getRoles().clear();
		roles.forEach(r -> user.getRoles().add(roleRepository.findByName(r)));

		return repository.save(user);
	}

	@Transactional(readOnly = true)
    public Page<User> search(String param, Pageable pageable) {
		return repository.search(param, pageable);
    }

	@Transactional
	public User setEnabled(final Long id, final Boolean enabled) {
		if (enabled == null) {
			throw new RuntimeException("Boolean param enabled is required");
		}
		User user = findById(id);
		user.setEnabled(enabled);
		return repository.save(user);
	}

	@Transactional(readOnly = true)
	public List<Role> roles() {
		return roleRepository.findAll();
	}

	@Transactional
	public Role createRole(final Role role) {
		if (role == null || !StringUtils.hasText(role.getName())) {
			throw new InvalidUserDataException("Role or role name is empty!");
		}
		return roleRepository.save(role);
	}

	@Transactional(readOnly = true)
	public List<UserDTO> findRevisions(final Long id) {
		List<UserDTO> revisions = new ArrayList<>();
		repository.findRevisions(id).get().forEach(r -> {
			UserDTO rev = UserDTO.of(r.getEntity());
			rev.setRevisionMetadata(RevisionMetadataDTO.builder()
					.revisionNumber(r.getMetadata().getRevisionNumber().orElse(null))
					.revisionInstant(r.getMetadata().getRevisionInstant().orElse(null))
					.revisionType(r.getMetadata().getRevisionType().name())
					.createdBy(r.getEntity().getCreatedBy())
					.createdDate(r.getEntity().getCreatedDate())
					.lastModifiedBy(r.getEntity().getLastModifiedBy())
					.lastModifiedDate(r.getEntity().getLastModifiedDate())
					.build()
			);
			revisions.add(rev);
		});
		return revisions;
	}
}
