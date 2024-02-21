package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.RefreshToken;
import bg.bulsi.bfsa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	RefreshToken findByRefreshToken(String token);

	@Modifying
	int deleteByUser(User user);

	@Modifying
	int deleteByContractor(Contractor contractor);

}
