package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS2698;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationS2698Repository extends JpaRepository<ApplicationS2698, Long> {
    List<ApplicationS2698> findAllByRecord_Applicant_IdAndStatus(final Long applicantId, final ApplicationStatus status);
}
