package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.FertilizerCert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FertilizerCertRepository extends JpaRepository<FertilizerCert, Long>, RevisionRepository<FertilizerCert, Long, Long> {
    Page<FertilizerCert> findAll(Pageable pageable);
}