package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.ContractorRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractorRelationRepository extends JpaRepository<ContractorRelation, Long> {

}