package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Contractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractorRepository extends JpaRepository<Contractor, Long>, RevisionRepository<Contractor, Long, Long> {

//	@Query("SELECT c FROM Contractor c LEFT JOIN FETCH c.relations WHERE c.id = :id")
//	Optional<Contractor> findById(String id);
	boolean existsByUsernameIgnoreCase(String username);
	boolean existsByIdentifier(String identifier);
	boolean existsByEmail(String email);

//	@Query("SELECT c FROM Contractor c LEFT JOIN FETCH c.relations LEFT JOIN FETCH c.roles WHERE lower(c.username) = lower(:username)")
	Optional<Contractor> findByUsernameIgnoreCase(String username);
//	@Query("SELECT c FROM Contractor c LEFT JOIN FETCH c.relations WHERE c.email = :email")
	Optional<Contractor> findByEmail(String email);

	Optional<Contractor> findByIdentifier(String identifier);

	@Query("SELECT c FROM Contractor c "
			+ "WHERE lower(c.username) like lower(concat('%', :param, '%')) "
			+ "OR lower(c.email) like lower(concat('%', :param, '%')) "
			+ "OR lower(c.fullName) like lower(concat('%', :param, '%'))"
			+ "OR lower(c.identifier) like lower(concat('%', :param, '%'))"
	)
	Page<Contractor> search(@Param("param") final String param, final Pageable pageable);

	List<Contractor> findByRegistersCode(final String registerCode);

	Page<Contractor> findByRegistersCodeAndBranch_Id(final String registerCode, final Long branchId, final Pageable pageable);

	@Query("SELECT c FROM Contractor c " +
			"JOIN c.registers r " +
			"WHERE r.parent.code = :divisionCode " +
			"AND c.branch.id = :branchId"
	)
	Page<Contractor> findByDivisionCodeAndBranchId(final String divisionCode,  final Long branchId, final Pageable pageable);

	@Query("SELECT c FROM Contractor c JOIN c.contractorPapers cp " +
			"WHERE cp.serviceType = :serviceType")
	List<Contractor> findAllByContractorPaperServiceType(final ServiceType serviceType);

	@Query("SELECT c FROM Contractor c JOIN c.contractorFacilities cf " +
			"WHERE cf.facility.id = :facilityId AND cf.owner = true ")
	Optional<Contractor> findByFacilityId(final Long facilityId);
}
