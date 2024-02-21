package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long>, RevisionRepository<Facility, Long, Long> {
    Optional<Facility> findByRegNumber(final String regNumber);

//    @Query("SELECT new bg.bulsi.bfsa.dto.FacilityVO(f.id, i18n.name, i18n.description, f.mail, f.phone1, f.phone2, f.enabled, f.contractor.id, f.register.code, f.branch.id) " +
//            "FROM Facility f JOIN f.i18ns i18n WHERE i18n.facilityI18nIdentity.languageId = :languageId " +
//            " AND f.register.code = :registerCode AND f.branch.id = :branchId"
//            )
//    Page<FacilityVO> findByRegister_CodeAndBranch_Id(String registerCode, String branchId, String languageId, Pageable pageable);

    @Query("SELECT cf.facility " +
            "FROM ContractorFacility cf JOIN cf.facility.i18ns i18n " +
            "WHERE i18n.facilityI18nIdentity.languageId = :languageId " +
            "AND cf.contractor.id = :contractorId " +
            "AND cf.serviceType = :serviceType " +
            "AND cf.facility.status = :status"
    )
    List<Facility> findAllByContractorIdAndServiceTypeAndStatus(final Long contractorId, final ServiceType serviceType, final FacilityStatus status, final String languageId);

//    @Query("SELECT new bg.bulsi.bfsa.dto.FacilityVO(f.id, i18n.name, i18n.description, f.mail, f.phone1, f.phone2, f.enabled, f.contractor.id, f.register.code, f.branch.id) " +
//            "FROM Facility f JOIN f.i18ns i18n JOIN f.register r " +
//            "WHERE i18n.facilityI18nIdentity.languageId = :languageId " +
//            "AND r.parent.code = :divisionCode " +
//            "AND f.branch.id = :branchId"
//    )
//    Page<FacilityVO> findByDivisionCodeAndBranch(String divisionCode, String branchId, String languageId, Pageable pageable);


    @Query("SELECT f " +
            "FROM Facility f JOIN f.i18ns i18n JOIN f.facilityPapers fp " +
            "WHERE i18n.facilityI18nIdentity.languageId = :languageId " +
            "AND fp.regNumber = :facilityPaperNumber"
    )
    Optional<Facility> findByFacilityPaperNumber(final String facilityPaperNumber, final String languageId);
}