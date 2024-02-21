package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.model.ContractorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractorFacilityRepository extends JpaRepository<ContractorFacility, Long>, RevisionRepository<ContractorFacility, Long, Long> {
    List<ContractorFacility> findAllByContractorIdAndFacilityStatus(final Long contractorId, final FacilityStatus facilityStatus);

//    @Query("SELECT cf.facility " +
//            "FROM ContractorFacility cf JOIN cf.facility.i18ns i18n " +
//            "WHERE i18n.facilityI18nIdentity.languageId = :languageId " +
//            "AND cf.contractor.id = :contractorId " +
//            "AND cf.serviceType = :serviceType " +
//            "AND cf.facility.status = :status"
//    )
//    List<Facility> findAllByContractorIdAndServiceTypeAndStatus(final Long contractorId, final ServiceType serviceType, final FacilityStatus status, final String languageId);
}