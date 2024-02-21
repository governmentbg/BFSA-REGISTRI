package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.TaskDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Task;
import bg.bulsi.bfsa.model.TaskI18n;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.TaskRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TaskService {

    private final TaskRepository repository;
    private final UserService userService;

    public Task findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Task.class, id));
    }

    @Transactional(readOnly = true)
    public TaskDTO findById(final Long id, final Language language) {
        return TaskDTO.of(repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Task.class, id)), language);
    }

    @Transactional
    public Task create(final TaskDTO dto, final Language language) {
        Task task = TaskDTO.to(dto, language);
        task.setRole(dto.getRoleId() != null && dto.getRoleId() > 0 ? userService.findRoleById(dto.getRoleId()) : null);
        task.setUser(dto.getUserId() != null && dto.getUserId() > 0 ? userService.findById(dto.getUserId()) : null);
        return repository.save(task);
    }

    @Transactional
    public Task update(final Long id, final TaskDTO dto, final Language language) {
        if (id == null || id <= 0 || dto == null || dto.getId() == null || dto.getId() <= 0
                || !id.equals(dto.getId())) {
            throw new EntityNotFoundException(Task.class, id + " / dto." + dto.getId());
        }
        Task task = findById(id);

        if (dto.getRoleId() != null && dto.getRoleId() <= 0) {
            task.setRole(userService.findRoleById(dto.getRoleId()));
        }

        TaskI18n i18n = task.getI18n(language);
        // TODO Return new i18n with selected language null instead
        if (i18n != null) {
            i18n.setName(dto.getName());
            i18n.setDescription(dto.getDescription());
            i18n.setTask(task);
        }
        return repository.save(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findAll(final Language language) {
        return TaskDTO.of(repository.findAll(), language);
    }

    @Transactional
    public TaskDTO claimTask(final Long taskId, final String username, final Language language) {
        log.debug("\nClaim task test.");

        Task task = findById(taskId);
        task.setUser(userService.findByUsername(username));

        return TaskDTO.of(repository.save(task), language);
    }

    @Transactional
    public TaskDTO cancelTask(final Long taskId, final Language language) {
        Task task = findById(taskId);
        task.setUser(null);
        return TaskDTO.of(repository.save(task), language);
    }

    public List<Task> findAllByRoles(final String username) {
        User user = userService.findByUsername(username);
        return repository.findAllByRoleInAndUserIsNull(user.getRoles());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findAllByUserId(final Long userId, final Language language) {
        return TaskDTO.of(repository.findAllByUserId(userId), language);
    }
}
