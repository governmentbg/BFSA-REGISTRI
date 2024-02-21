package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS7695;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationS7695Repository extends JpaRepository<ApplicationS7695, Long> {
    List<ApplicationS7695> findAllByRecord_Applicant_IdAndStatus(final Long applicantId, final ApplicationStatus status);
}
