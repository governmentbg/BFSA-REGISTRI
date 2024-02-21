package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findBySequenceNumber(final String sequenceNumber);
    Optional<Branch> findByIdentifier(final String identifier);
    Optional<Branch> findBySettlement_Code(final String settlementCode);

    // Avoid the LazyInitializationException JOIN FETCH
    @Query("SELECT b FROM Branch b LEFT JOIN FETCH b.users WHERE b.id = :id")
    Optional<Branch> get(Long id);

    // Avoid the LazyInitializationException JOIN FETCH
    @Query("SELECT DISTINCT b FROM Branch b LEFT JOIN FETCH b.users ORDER BY b.sequenceNumber")
    List<Branch> getAll();

    List<Branch> findAllByMainIsTrue();
}
