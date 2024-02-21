package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.UserDetailsDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserDetailsApiService implements UserDetailsService {

    private final UserService userService;
	private final ContractorService contractorService;

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		if (!StringUtils.hasText(username)) {
			throw new UsernameNotFoundException("User Not Found");
		}
		if (username.endsWith(" ")) {
			return UserDetailsDTO.of(userService.findByUsername(username.trim().toLowerCase()));
		}
		return UserDetailsDTO.of(contractorService.findByUsername(username.trim().toLowerCase()));
	}
}
