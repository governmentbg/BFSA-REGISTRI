package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO implements UserDetails {

	@Serial
	private static final long serialVersionUID = 2684189068113021115L;

	private Long id;
	private String email;
	private String fullName;
	private String username;
	private String identifier;
	private Long branchId;
	private String directorateCode;
	@JsonIgnore
	private String password;
	@JsonIgnore
	private boolean accountNonExpired;
	@JsonIgnore
	private boolean accountNonLocked;
	@JsonIgnore
	private boolean credentialsNonExpired;
	private boolean enabled;
	@Builder.Default
	private Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

	public static UserDetailsDTO of(final Contractor source) {
		List<GrantedAuthority> roles = new ArrayList<>();
		if (!CollectionUtils.isEmpty(source.getRoles())) {
			source.getRoles().forEach(r -> roles.add(new SimpleGrantedAuthority(r.getName())));
		}

		return UserDetailsDTO.builder()
				.id(source.getId())
				.email(source.getEmail())
				.fullName(source.getFullName())
				.username(source.getUsername())
				.identifier(source.getIdentifier())
				.password(source.getPassword())
				.authorities(roles)
				.enabled(source.getEnabled() != null ? source.getEnabled() : false)
				.accountNonLocked(true)
				.accountNonExpired(true)
				.credentialsNonExpired(true)
				.build();
	}

	public static UserDetailsDTO of(final User source) {
		List<GrantedAuthority> roles = new ArrayList<>();
		if (!CollectionUtils.isEmpty(source.getRoles())) {
			source.getRoles().forEach(r -> roles.add(new SimpleGrantedAuthority(r.getName())));
		}

		return UserDetailsDTO.builder()
				.id(source.getId())
				.email(source.getEmail())
				.fullName(source.getFullName())
				.username(source.getUsername())
				.identifier(source.getIdentifier())
				.password(source.getPassword())
				.branchId(source.getBranch() != null ? source.getBranch().getId() : null)
				.directorateCode(source.getDirectorate() != null && StringUtils.hasText(source.getDirectorate().getCode()) ? source.getDirectorate().getCode() : null)
				.authorities(roles)
				.enabled(source.getEnabled() != null ? source.getEnabled() : false)
				.accountNonLocked(true)
				.accountNonExpired(true)
				.credentialsNonExpired(true)
				.build();
	}
}
