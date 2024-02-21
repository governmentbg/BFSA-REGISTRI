package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByRoleInAndUserIsNull(Set<Role> roles);

    List<Task> findAllByUserId(Long userId);

}
