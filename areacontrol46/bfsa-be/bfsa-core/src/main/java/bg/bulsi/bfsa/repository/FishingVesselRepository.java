package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.FishingVessel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface FishingVesselRepository extends JpaRepository<FishingVessel, Long>, RevisionRepository<FishingVessel, Long, Long> {
    Page<FishingVessel> findAll(Pageable pageable);
    FishingVessel findByRegNumber(String regNumber);
    FishingVessel findByExternalMarking(String externalMarking);
    Page<FishingVessel> findByAssignmentType_Code(String assignmentTypeCode, Pageable pageable);
    Page<FishingVessel> findByHullLength(Double hullLength, Pageable pageable);
    FishingVessel findByEntryNumber(String entryNumber);

    @Query("SELECT fv FROM FishingVessel fv WHERE fv.date >= :from AND fv.date <= :to")
    Page<FishingVessel> findByDate(LocalDate from, LocalDate to, Pageable pageable);
    Page<FishingVessel> findByBranch_Id(Long branchId, Pageable pageable);
}