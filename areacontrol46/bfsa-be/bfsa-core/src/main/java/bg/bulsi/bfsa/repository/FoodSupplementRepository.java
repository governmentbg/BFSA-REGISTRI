package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.FoodSupplement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodSupplementRepository extends JpaRepository<FoodSupplement, Long>, RevisionRepository<FoodSupplement, Long, Long> {

	Page<FoodSupplement> findAllByApplicant_IdAndEnabledIsTrueAndI18ns_foodSupplementI18nIdentity_languageId(final Long applicantId, final String languageId, final Pageable pageable);

	Page<FoodSupplement> findAllByApplicant_IdentifierAndI18ns_foodSupplementI18nIdentity_languageId(final String applicantIdentifier, final String languageId, final Pageable pageable);

	@Query("SELECT c FROM FoodSupplement c JOIN c.i18ns i18n "
			+ "WHERE i18n.foodSupplementI18nIdentity.languageId = :languageId "
			+ "OR lower(i18n.name) like lower(concat('%', :param, '%')) "
			+ "OR lower(c.regNumber) like lower(concat('%', :param, '%')) "
			+ "OR lower(c.applicant.fullName) like lower(concat('%', :param, '%'))"
	)
	Page<FoodSupplement> search(@Param("param") final String param, String languageId, final Pageable pageable);
}