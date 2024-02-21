package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.Country;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByCode(String code);

    @Query("SELECT c FROM Country c JOIN c.i18ns i18n "
            + "WHERE i18n.countryI18nIdentity.languageId = :languageId "
            + "AND (:param IS NULL"
            + "  OR lower(i18n.name) like lower(concat('%', :param, '%'))"
            + "  OR lower(i18n.capital) like lower(concat('%', :param, '%'))"
            + "  OR lower(i18n.continentName) like lower(concat('%', :param, '%'))"
            + "  OR lower(i18n.description) like lower(concat('%', :param, '%'))"
            + ")"
    )
    List<Country> search(
            @Param("param") final String param,
            @Param("languageId") final String languageId,
            final Pageable pageable
    );

    List<Country> findAllByEuropeanUnionMemberIsTrue();
}