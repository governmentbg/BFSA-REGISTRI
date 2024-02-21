package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.PlantProtectionProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantProtectionProductRepository extends JpaRepository<PlantProtectionProduct, Long>, RevisionRepository<PlantProtectionProduct, Long, Long> {

	@Query("SELECT v FROM PlantProtectionProduct v JOIN v.i18ns i18n "
			+ "WHERE i18n.plantProtectionProductI18nIdentity.languageId = :languageId "
			+ "AND (:param IS NULL"
			+ "  OR lower(i18n.name) like lower(concat('%', :param, '%'))"
			+ "  OR lower(i18n.activeSubstances) like lower(concat('%', :param, '%'))"
			+ "  OR lower(i18n.purpose) like lower(concat('%', :param, '%'))"
			+ "  OR lower(i18n.pest) like lower(concat('%', :param, '%'))"
			+ "  OR lower(i18n.crop) like lower(concat('%', :param, '%'))"
			+ "  OR lower(i18n.application) like lower(concat('%', :param, '%'))"
			+ ")"
	)
	Page<PlantProtectionProduct> search(
			@Param("param") final String param,
			@Param("languageId") final String languageId,
			final Pageable pageable);

//	List<PlantProtectionProduct> findByContractorId(final Long contractorId);
}
