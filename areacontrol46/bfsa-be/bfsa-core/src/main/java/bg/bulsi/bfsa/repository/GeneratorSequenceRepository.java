package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.GeneratorSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratorSequenceRepository extends JpaRepository<GeneratorSequence, Long> {

    @Query(value = "SELECT nextval('fph.gen_sequence')", nativeQuery = true)
    Long getNextVal();
}
