package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.TaskDTO;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.Task;
import bg.bulsi.bfsa.model.TaskI18n;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.RoleRepository;
import bg.bulsi.bfsa.repository.TaskRepository;
import bg.bulsi.bfsa.repository.UserRepository;
import bg.bulsi.bfsa.security.RolesConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class TaskServiceTest  extends BaseServiceTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TASK_NAME_BG = "Задача";
    private static final String TASK_DESCRIPTION_BG = "Тестова задача";
    private static final String TASK_NAME_EN = "Test task";
    private static final String TASK_DESCRIPTION_EN = "Task for the test.";
    private static final String TASK_UPDATE = "_updated";
    private static final String USERNAME = "test";
    private static final String TEST_EMAIL = "test@domain.xyz";
    private static final String TEST_USER = "user";

    @BeforeAll
    public void init() {
        this.createRolesIfNotExist();
        if (this.taskRepository.count() <= 0) {
            Task task = Task.builder()
                    .role(this.roleRepository.findByName(RolesConstants.ROLE_ADMIN))
                    .build();
            task.getI18ns().add(new TaskI18n(TASK_NAME_BG, TASK_DESCRIPTION_BG, task, langBG));
            task.getI18ns().add(new TaskI18n(TASK_NAME_EN, TASK_DESCRIPTION_EN, task, langEN));
            this.taskRepository.save(task);
        }
    }

    @Test
    public void getAllTasks_whenFind_thenReturnListWithDtos() {
        log.debug("\nGet all task test.\n");

        List<TaskDTO> result = this.taskService.findAll(langBG);
        log.debug("\nAll tasks count: {}\n", result.size());

        Assertions.assertNotNull(result);
    }

    @Test
    public void givenId_whenGetById_thenReturnTaskDto() {
        log.debug("\nGet by id task test.\n");

        TaskDTO result = taskService.findById(this.findIdForTest(), langBG);
        log.debug("\nGet by id task result: {}\n", result);

        Assertions.assertNotNull(result);
    }

//    @Test
//    public void givenTaskDto_whenCreateNewTask_thenReturnTaskDto() {
//        log.info("\nCreate new task from dto test.\n");
//
//        TaskDTO dto = TaskDTO.builder()
//                .name(TASK_NAME_BG)
//                .description(TASK_DESCRIPTION_BG)
//                .roleId(RolesConstants.ROLE_ADMIN)
//                .build();
//
//        Task result = this.taskService.create(dto, this.bg);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals(RolesConstants.ROLE_ADMIN, result.getRole().getName());
//    }

//    @Test
//    public void givenDto_whenUpdate_thenReturnTaskDto() {
//        log.info("\n\nUpdate task test");
//        String id = findIdForTest();
//
//        TaskDTO dto = TaskDTO.builder()
//                .name(TASK_NAME_BG + TASK_UPDATE)
//                .description(TASK_DESCRIPTION_BG + TASK_UPDATE)
//                .roleId(RolesConstants.ROLE_USER)
//                .id(id)
//                .build();
//        log.info("\nDto: {}", dto);
//
//        TaskDTO result = TaskDTO.of(taskService.update(id, dto, this.bg), this.bg);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals(TASK_NAME_BG + TASK_UPDATE, result.getName());
//        Assertions.assertEquals(TASK_DESCRIPTION_BG + TASK_UPDATE, result.getDescription());
//        Assertions.assertEquals(RolesConstants.ROLE_USER, result.getRoleId());
//    }

    @Test
    public void givenTaskIdAndUsername_whenClaimTask_returnTaskDto() {
        log.debug("\nClaim task test.\n");

        User user = this.createUserIfNotExist();
        Assertions.assertNotNull(user);
        log.debug("\nClaim task test - user id: {}\n", user.getId());

        TaskDTO task =  this.taskService.findAll(langBG).stream().findFirst().orElse(null);
        Assertions.assertNotNull(task);
        log.debug("\nClaim task test - task: {}\nUser id: {}\nTask name: {}\n", task, task.getUserId(), task.getName());

        TaskDTO result = taskService.claimTask(task.getId(), user.getUsername(), langBG);
        log.debug("\nClaim task test - result: {}\nUser id: {}\nTask name: {}\n", result, result.getUserId(), result.getName());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUserId(), user.getId());
    }

    @Test
    public void givenTaskId_whenCancelTask_thenReturnTaskDto() {
        log.debug("\nCancel task test.");

        User user = this.createUserIfNotExist();
        Assertions.assertNotNull(user);
        log.debug("\nCancel task test: user id - {}\n", user.getId());

        TaskDTO task = this.taskService.findAll(langBG).stream().findFirst().orElse(null);
        Assertions.assertNotNull(task);
        log.debug("\nCancel task test - task: {}\nUser id: {}\nTask name: {}\n", task, task.getUserId(), task.getName());

        TaskDTO claimed = taskService.claimTask(task.getId(), user.getUsername(), langBG);
        log.debug("\nCancel task test - claimed: {}\n - Claimed user id: {}\n - Claimed task name: {}\n", claimed, claimed.getUserId(), claimed.getName());

        TaskDTO canceled = taskService.cancelTask(task.getId(), langBG);
        Assertions.assertNotNull(canceled);
        Assertions.assertNull(canceled.getUserId());
        log.debug("\n\nCancel task test: result user id - {}", canceled.getUserId());

        Assertions.assertEquals(claimed.getId(), canceled.getId());
        log.debug("\n\nCancel task test:\n - claimed id: {}\n - canceled id: {}", claimed.getId(), canceled.getId());

        Assertions.assertNotEquals(claimed.getUserId(), canceled.getUserId());
        log.debug("\n\nCancel task test:\n - claimed user id: {}\n - canceled user id: {}", claimed.getUserId(), canceled.getUserId());
    }

//    @Test
//    public void givenUsername_whenFindAllByRole_thenReturnListOfTaskDto() {
//        log.debug("\nFind by role test.\n");
//
//        User user = this.createUserIfNotExist();
//        List<TaskDTO> tasks = TaskDTO.of(this.taskService.findAllByRoles(user.getUsername()), langBG);
//
//        if (tasks.size() == 0) {
//            user = this.createUserWithUserRole();
//            this.createUserTask();
//            tasks = TaskDTO.of(this.taskService.findAllByRoles(user.getUsername()), langBG);
//            log.debug("\n\nUser's tasks count: {}", tasks.size());
//            Assertions.assertNotNull(tasks);
//            Assertions.assertEquals(1, tasks.size());
//            return;
//        }
//
//        Assertions.assertNotNull(tasks);
//        Assertions.assertEquals(1, tasks.size());
//    }

    @Test
    public void givenUserId_whenFindTasksByUserId_thenReturnListOfTask() {
        log.debug("\n\nFind tasks by user id test.\n");
        User user = this.createUserIfNotExist();
        Assertions.assertNotNull(user);
        log.debug("\n\nUser id - {}\n", user.getId());

        TaskDTO task = this.taskService.findAll(langBG).stream().findFirst().orElse(null);
        Assertions.assertNotNull(task);
        Assertions.assertNotEquals(user.getId(), task.getUserId());
        log.debug("\n\nTask user id: {} - should be null\n", task.getUserId());
        log.debug("\n\nTask id: {}\n", task.getId());

        TaskDTO claimed = taskService.claimTask(task.getId(), user.getUsername(), langBG);
        Assertions.assertNotNull(claimed);
        Assertions.assertEquals(task.getId(), claimed.getId());
        log.debug("\n\nTask id: {}\nClaimed id: {}\n", task.getId(), claimed.getId());
        Assertions.assertEquals(user.getId(), claimed.getUserId());
        log.debug("\n\nClaimed task user id: {}\n", claimed.getUserId());

        List<TaskDTO> tasks = taskService.findAllByUserId(user.getId(), langBG);
        Assertions.assertNotNull(task);

        for (TaskDTO t : tasks) {
            log.debug("\n\nTask in list user id: {}", t.getUserId());

            Assertions.assertEquals(user.getId(), t.getUserId());
            log.debug("\nUser id: {}\nTask user id: {}", user.getId(), t.getUserId());
        }
    }

    private Long findIdForTest() {
        log.debug("\n\nFind id for test");
        return taskService.findAll(langBG).stream().findFirst().orElse(null).getId();
    }

    private void createRolesIfNotExist() {
        Role admin = roleRepository.findByName(RolesConstants.ROLE_ADMIN);
        if (admin == null) {
            admin = new Role();
            admin.setName(RolesConstants.ROLE_ADMIN);
            Role na = roleRepository.save(admin);
            log.debug("\n\nNew admin role: {}\n", na);
            System.out.println("\n\nNew admin role: " + na);

            User adminUser = createUserIfNotExist();
            List<User> users = new ArrayList<>();
            users.add(adminUser);
            admin.setUsers(users);

            Role ua = roleRepository.save(admin);
            log.debug("\n\nNew admin: {}\n", ua);
            System.out.println("\n\nNew admin: " + ua);
        }

        Role user = roleRepository.findByName(RolesConstants.ROLE_USER);
        if (user == null) {
            user = new Role();
            user.setName(RolesConstants.ROLE_USER);
            roleRepository.save(user);
        }
    }

    private User createUserIfNotExist() {
        User user = userRepository.findByUsernameIgnoreCase(USERNAME).orElse(null);
        if (user == null) {
            user = User.builder()
                    .username(USERNAME)
                    .email(TEST_EMAIL)
                    .fullName(USERNAME + " " + USERNAME)
                    .password(passwordEncoder.encode(USERNAME)).build();
            user.getRoles().add(roleRepository.findByName(RolesConstants.ROLE_ADMIN));
            return userRepository.save(user);
        }
        return user;
    }

    private User createUserWithUserRole() {
       User user = User.builder()
                .username(USERNAME + TEST_USER)
                .email(TEST_USER + TEST_EMAIL)
                .fullName(USERNAME + TEST_USER + " " + USERNAME)
                .password(passwordEncoder.encode(USERNAME)).build();
        user.getRoles().add(roleRepository.findByName(RolesConstants.ROLE_USER));
        return userRepository.save(user);
    }

//    private void createUserTask () {
//        // Added because when are started all test in update method change the role of the task, so in any case.
//
//        Task task = Task.builder()
//                .role(this.roleRepository.findByName(RolesConstants.ROLE_USER))
//                .build();
//        task.getI18ns().add(new TaskI18n(TASK_NAME_BG + TASK_NAME_BG, TASK_DESCRIPTION_BG + " " + TASK_DESCRIPTION_BG, task, langBG));
//        task.getI18ns().add(new TaskI18n(TASK_NAME_EN + TASK_NAME_EN, TASK_DESCRIPTION_EN + " " + TASK_DESCRIPTION_EN, task, langEN));
//        taskRepository.save(task);
//    }
}
