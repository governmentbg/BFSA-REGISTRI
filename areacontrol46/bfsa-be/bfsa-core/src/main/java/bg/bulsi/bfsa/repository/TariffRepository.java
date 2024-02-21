package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long>, RevisionRepository<Tariff, Long, Long> {
    Optional<Tariff> findByServiceType(ServiceType serviceType);
}
