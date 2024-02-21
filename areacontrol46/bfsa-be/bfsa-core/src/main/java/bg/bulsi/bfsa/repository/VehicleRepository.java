package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, RevisionRepository<Vehicle, Long, Long> {

	boolean existsByRegistrationPlate(final String registrationPlate);

	boolean existsByRegistrationPlateAndCertificateNumberIsNotNull(final String registrationPlate);

	@Query("SELECT v FROM Vehicle v JOIN v.i18ns i18n "
			+ "WHERE i18n.vehicleI18nIdentity.languageId = :languageId "
			+ "AND (:param IS NULL"
			+ "  OR lower(v.registrationPlate) like lower(concat('%', :param, '%'))"
			+ "  OR lower(v.entryNumber) like lower(concat('%', :param, '%'))"
			+ "  OR lower(i18n.brandModel) like lower(concat('%', :param, '%'))"
			+ "  OR lower(i18n.description) like lower(concat('%', :param, '%'))"
			+ ")"
	)
	Page<Vehicle> search(
			@Param("param") final String param,
			@Param("languageId") final String languageId,
			final Pageable pageable);

	@Query("SELECT v FROM Vehicle v JOIN v.i18ns i18n "
			+ "WHERE i18n.vehicleI18nIdentity.languageId = :languageId "
			+ "AND v.registrationPlate = :registrationPlate"
	)
	Optional<Vehicle> findByRegistrationPlate(final String registrationPlate, final String languageId);

	@Query("SELECT v FROM Vehicle v JOIN v.i18ns i18n "
			+ "WHERE i18n.vehicleI18nIdentity.languageId = :languageId "
			+ "AND v.certificateNumber = :certificateNumber"
	)
	Optional<Vehicle> findByCertificateNumber(final String certificateNumber, final String languageId);
}
