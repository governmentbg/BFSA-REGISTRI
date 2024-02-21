package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS3363;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationS3363Repository extends JpaRepository<ApplicationS3363, Long> {
    List<ApplicationS3363> findAllByRecord_Applicant_IdAndStatus(final Long applicantId, final ApplicationStatus status);
}
