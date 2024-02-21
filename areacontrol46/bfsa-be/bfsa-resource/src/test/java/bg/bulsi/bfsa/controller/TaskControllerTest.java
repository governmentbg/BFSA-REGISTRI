package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.TaskDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.Task;
import bg.bulsi.bfsa.model.TaskI18n;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.RoleRepository;
import bg.bulsi.bfsa.repository.TaskRepository;
import bg.bulsi.bfsa.repository.UserRepository;
import bg.bulsi.bfsa.security.RolesConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final String TASK_NAME_BG = "Задача";
    private static final String TASK_DESCRIPTION_BG = "Тестова задача";
    private static final String TASK_NAME_BG_UPDATED = "Задача_Променена";
    private static final String TASK_DESCRIPTION_BG_UPDATED = "Променена Тестова задача";
    private static final String TASK_NAME_EN = "Task";
    private static final String TASK_DESCRIPTION_EN = "Task for the test.";
    private static final String NEW_NAME_BG = "Нова_Задача_BG";
    private static final String NEW_DESCRIPTION_BG = "Описание на новата задача БГ";
    private static final String NEW_NAME_EN = "New_Task_EN";
    private static final String NEW_DESCRIPTION_EN = "New task description EN";
    private final Language bg = Language.builder().languageId("bg").build();
    private final Language en = Language.builder().languageId("en").build();
    private Long initTaskId;
    private Task task;
    private User user;;

    @BeforeEach
    public void init() {
        if (roleRepository.findByName("TEST") == null){
            roleRepository.save(Role.builder().name("TEST").build());
        }

        if (taskRepository.count() == 0) {
            task = Task.builder().role(roleRepository.findByName(RolesConstants.ROLE_ADMIN)).build();
            task.getI18ns().add(new TaskI18n(TASK_NAME_BG, TASK_DESCRIPTION_BG, task, bg));
            task.getI18ns().add(new TaskI18n(TASK_NAME_EN, TASK_DESCRIPTION_EN, task, en));
            task = taskRepository.save(task);
            initTaskId = task.getId();

            User user = User.builder()
                    .username("firstUser")
                    .email("firstUser@domain.zxc")
                    .enabled(true)
                    .fullName("firstUser firstUser")
                    .password(passwordEncoder.encode("firstUser")).build();
            user.getRoles().add(roleRepository.findByName(RolesConstants.ROLE_ADMIN));
            this.user = userRepository.save(user);
        }
    }

    @Test
    void givenTaskId_whenSearchById_thenReturnTask() throws Exception {
        mockMvc.perform(get("/tasks/{id}", task.getId())
                        .header(HttpHeaders.ACCEPT_LANGUAGE, langBG.getLanguageId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(task.getId().intValue())))
                .andReturn();
    }

    @Test
    void givenNoParams_whenAssertSuccessfullyFindAllTasks_thenReturnAllTasks() throws Exception {
        mockMvc.perform(get("/tasks/")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount", greaterThan(0)))
                .andExpect(jsonPath("$.results.size()", greaterThan(0)))
                .andReturn();
    }

//    @Test
//    void givenTaskDto_whenSuccessfulCreateValidTask_thenReturnCorrectResult() throws Exception {
//        TaskDTO dto = new TaskDTO();
//        dto.setRoleId("TEST");
//        dto.setName(NEW_NAME_EN);
//        dto.setDescription(NEW_DESCRIPTION_EN);
//
//        mockMvc.perform(post("/tasks/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto))
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
//                .andExpect(jsonPath("$.name", is(NEW_NAME_EN)))
//                .andExpect(jsonPath("$.description", is(NEW_DESCRIPTION_EN)))
//                .andExpect(jsonPath("$.role", is("TEST")))
//                .andReturn();
//
//        mockMvc.perform(get("/tasks/")
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.totalCount", greaterThan(1)))
//                .andExpect(jsonPath("$.results.size()", greaterThan(1)))
//                .andReturn();
//    }

//    @Test
//    void givenTaskDto_whenSuccessfulUpdateExistTask_thenReturnUpdatedTask() throws Exception {
//        TaskDTO dto = TaskDTO.of(this.task, bg);
//        dto.setName(TASK_NAME_BG_UPDATED);
//        dto.setDescription(TASK_DESCRIPTION_BG_UPDATED);
//
//        mockMvc.perform(put("/tasks/{id}", initTaskId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto))
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(TASK_NAME_BG_UPDATED)))
//                .andExpect(jsonPath("$.description", is(TASK_DESCRIPTION_BG_UPDATED)))
//                .andReturn();
//    }

    @Disabled
    @Test
    void givenTaskDto_whenTryToUpdateExistTask_thenReturnRuntimeException() throws Exception {
        TaskDTO dto = TaskDTO.of(this.task, bg);
        dto.setId(new Random().nextLong(1000));

        mockMvc.perform( put("/tasks/{id}", initTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg") )
                .andExpect(status().isInternalServerError())
                .andExpect((jsonPath("$.exception", is("java.lang.RuntimeException"))))
                .andExpect((jsonPath("$.error", is("Internal Server Error"))))
                .andExpect((jsonPath("$.message", is("Path variable id doesn't match RequestBody parameter id"))))
                .andExpect((jsonPath("$.exceptionCode", is("RuntimeException"))));
    }

//    @Test
//    void givenTaskAndUser_whenClaimTaskToUser_thenReturnUserClaimedTask_thenSuccessfullyCancelTask() throws Exception {
//
//        mockMvc.perform(put("/tasks/claim/{id}", task.getId())
//                .with(user(user.getUsername()).password(user.getPassword()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId", is(user.getId())))
//                .andReturn();
//
//        mockMvc.perform(put("/tasks/cancel/{id}", task.getId())
//                        //.with(user(user.getUsername()).password(user.getPassword()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId").doesNotExist())
//                .andReturn();
//    }

//    @Test
//    void current_user() throws Exception {
//        Task taskWithRoleTest = Task.builder().role(roleRepository.findByName("TEST")).build();
//        taskWithRoleTest.getI18ns().add(new TaskI18n(NEW_NAME_BG, NEW_DESCRIPTION_BG, taskWithRoleTest, bg));
//        this.task = taskRepository.save(taskWithRoleTest);
//
//        mockMvc.perform(get("/tasks/current-user")
//                        .with(user(user.getUsername()).password(user.getPassword()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(HttpHeaders.ACCEPT_LANGUAGE, "bg"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()", is(1)))
//                .andReturn();
//    }

}