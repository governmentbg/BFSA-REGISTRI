package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, RevisionRepository<Address, Long, Long> {
	@Query("SELECT a FROM Address a  WHERE LOWER(a.address) like LOWER(CONCAT('%', :param, '%')) "
			+ "OR LOWER(a.addressLat) like LOWER(CONCAT('%', :param, '%')) "
			+ "OR LOWER(a.addressType.code) like LOWER(CONCAT('%', :param, '%'))"
	)
	Page<Address> search(@Param("param") final String param, final Pageable pageable);
}