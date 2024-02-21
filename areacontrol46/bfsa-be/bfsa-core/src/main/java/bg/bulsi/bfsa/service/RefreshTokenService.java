package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.RefreshToken;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.ContractorRepository;
import bg.bulsi.bfsa.repository.RefreshTokenRepository;
import bg.bulsi.bfsa.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RefreshTokenService {

	@Value("${jwt.refreshExp}")
	private Long refreshTokenDurationMs;

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final ContractorRepository contractorRepository;
	private final ContractorService contractorService;

	public RefreshToken findByRefreshToken(final String token) {
		return refreshTokenRepository.findByRefreshToken(token);
	}

	public RefreshToken createRefreshToken(final Long userId) {
		RefreshToken refreshToken = new RefreshToken();

		User user = userService.findByIdOrNull(userId);
		if(user != null) {
			refreshToken.setUser(user);
		}

		Contractor contractor = contractorService.findByIdOrNull(userId);
		if (contractor != null) {
			refreshToken.setContractor(contractor);
		}

		refreshToken.setExpDate(Instant.now().plusMillis(refreshTokenDurationMs));
		refreshToken.setRefreshToken(UUID.randomUUID().toString());

		return refreshTokenRepository.save(refreshToken);
	}

	@Transactional
	public RefreshToken verifyExpiration(final RefreshToken token) {
		if (token.getExpDate().compareTo(Instant.now()) < 0) {
			refreshTokenRepository.delete(token);
			return null;
		}

		return token;
	}

	@Transactional
	public int deleteByUserId(final Long id, final int type) {
		return type == 0
				? refreshTokenRepository.deleteByUser(userRepository.findById(id).get())
				: refreshTokenRepository.deleteByContractor(contractorRepository.findById(id).get());
	}
	
}
