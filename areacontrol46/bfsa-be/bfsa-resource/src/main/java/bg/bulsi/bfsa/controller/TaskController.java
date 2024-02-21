package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.TaskDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.service.TaskService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/tasks")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TaskController {

    private final TaskService taskService;
    private final LanguageService languageService;

    @Secured({RolesConstants.ROLE_ADMIN})
    @GetMapping("/")
    public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        List<TaskDTO> tasks = taskService.findAll(LocaleUtil.getLanguage(locale, languageService));
        return ResponseEntity.ok()
                .body(ApiListResponse.builder()
                    .results(tasks)
                    .totalCount(tasks.size())
                    .build());
    }

    @GetMapping("/{id}")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> getById(@PathVariable final Long id,
                                     @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        return ResponseEntity.ok().body(taskService.findById(id, LocaleUtil.getLanguage(locale, languageService)));
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody final TaskDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(TaskDTO.of(taskService.create(dto, language), language));
    }

    @PutMapping("/{id}")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
                                    @RequestBody final TaskDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        if (dto.getId() != null && !id.equals(dto.getId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
        }
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(TaskDTO.of(taskService.update(id, dto, language), language));
    }

    @PutMapping("/claim/{taskId}")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> claimTask(@PathVariable final Long taskId,
                                       @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(
                taskService.claimTask(taskId, currentUser.getUsername(), language)
        );
    }

    @PutMapping("/cancel/{taskId}")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> cancelTask(@PathVariable final Long taskId,
                                        @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(taskService.cancelTask(taskId, language));
    }

    @GetMapping("/current-user")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> findAllTasksByCurrentUserRoles(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(
                TaskDTO.of(taskService.findAllByRoles(currentUser.getUsername()), LocaleUtil.getLanguage(locale, languageService))
        );
    }

    @GetMapping("/current-user/{id}")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> findAllTasksByUserId(@PathVariable final Long id,
                                                  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(taskService.findAllByUserId(id, language));
    }
}
