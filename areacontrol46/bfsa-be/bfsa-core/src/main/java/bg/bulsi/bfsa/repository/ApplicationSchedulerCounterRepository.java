package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.ApplicationSchedulerCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationSchedulerCounterRepository extends JpaRepository<ApplicationSchedulerCounter, Long> {

    ApplicationSchedulerCounter findByDocumentId(final Long documentId);
    List<ApplicationSchedulerCounter> findAllByCounter(final Integer counter);

}
