package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	boolean existsByUsernameIgnoreCase(String username);
	boolean existsByIdentifier(String identifier);
	boolean existsByEmail(String email);

	VerificationToken findByVerificationToken(String token);
	VerificationToken findByUsernameAndEmail(String Username, String email);

	@Modifying
	String deleteByEmail(String email);

}
