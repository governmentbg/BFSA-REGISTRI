package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS1590;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationS1590Repository extends JpaRepository<ApplicationS1590, Long> {
    List<ApplicationS1590> findAllByRecord_Applicant_IdAndStatus(final Long applicantId, final ApplicationStatus status);
}
