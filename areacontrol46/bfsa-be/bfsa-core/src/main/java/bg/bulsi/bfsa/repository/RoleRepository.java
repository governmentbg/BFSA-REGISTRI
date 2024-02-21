package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, RevisionRepository<Role, Long, Long> {

	Role findByName(String role);

	boolean existsByName(String name);
}
