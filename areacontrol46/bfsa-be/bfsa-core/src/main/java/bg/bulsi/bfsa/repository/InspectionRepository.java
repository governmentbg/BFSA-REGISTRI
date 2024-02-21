package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.Inspection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    Page<Inspection> findAllByOrderByLastModifiedDateDesc(final Pageable pageable);

    @Query("SELECT i FROM Inspection i JOIN i.i18ns i18n " +
            "WHERE i18n.inspectionI18nIdentity.languageId = :languageId " +
            "AND i.id = :id"
    )
    Optional<Inspection> findByIdAndLanguage(final Long id, final String languageId);
}
