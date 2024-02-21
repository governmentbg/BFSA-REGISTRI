package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.model.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long>, RevisionRepository<Record, Long, Long> {

    Page<Record> findAllByOrderByLastModifiedDateDesc(final Pageable pageable);

    List<Record> findAllByApplicant_Id(@Param("applicantId") final Long applicantId);

    @Query("SELECT r FROM Record r " +
            "WHERE r.branch.id = :branchId " +
            "AND r.directorate.code = :directorateCode " +
            "ORDER BY r.lastModifiedDate DESC")
    Page<Record> findAllByBranchAndDirectorate(
            @Param("branchId") final Long branchId,
            final String directorateCode,
            final Pageable pageable
    );

    @Query("SELECT r FROM Record r " +
            "LEFT JOIN r.applicationS502 s502 " +
            "WHERE r.branch.id = :branchId " +
            "AND (r.status IN :statuses " +
            "  OR s502.status IN :statuses" +
            ") " +
            "ORDER BY r.lastModifiedDate DESC")
    Page<Record> findAllByBranchAndStatuses(
            @Param("branchId") final Long branchId,
            @Param("statuses") final List<RecordStatus> statuses,
            final Pageable pageable
    );

    @Query("SELECT r FROM Record r " +
            "WHERE r.branch.id = :branchId " +
            "AND r.directorate.code = :directorateCode " +
            "  AND (:recordStatus IS NULL OR r.status = :recordStatus) " +
            "  AND (cast(:date as timestamp) IS NULL OR r.entryDate >= :date AND r.entryDate <= :date) " +
            "  AND (:param IS NULL " +
            "    OR lower(r.requestor.identifier) like lower(concat('%', :param, '%')) " +
            "    OR lower(r.requestor.fullName) like lower(concat('%', :param, '%')) " +
            "    OR lower(r.applicant.identifier) like lower(concat('%', :param, '%')) " +
            "    OR lower(r.applicant.fullName) like lower(concat('%', :param, '%')) " +
            "    OR lower(r.entryNumber) like lower(concat('%', :param, '%'))" +
            "    OR lower(r.serviceType) like lower(concat('%', :param, '%'))" +
            "  )"
    )
    Page<Record> search(
            @Param("branchId") final Long branchId,
            @Param("directorateCode") final String directorateCode,
            @Param("param") final String param,
            @Param("recordStatus") final RecordStatus recordStatus,
            @Param("date") final LocalDate date,
            final Pageable pageable
    );
}
