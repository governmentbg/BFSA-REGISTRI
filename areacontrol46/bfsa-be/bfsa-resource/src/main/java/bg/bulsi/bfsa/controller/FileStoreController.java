package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApiResponse;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.FileStoreService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FileStoreController {

	private final FileStoreService service;

	@GetMapping(path = "/{id}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> findById(@PathVariable @NotNull final Long id) {
		return ResponseEntity.ok().body(service.get(id));
	}

	@GetMapping(path = "/{id}/file")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> getFile(@PathVariable @NotNull final Long id) {
		return ResponseEntity.ok().body(service.getFile(id));
	}

	@PostMapping(path = "/{docTypeCode}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> upload(@PathVariable @NotNull final String docTypeCode,
									@Valid @RequestPart(value = "file") final MultipartFile multipartFile) {
		service.create(docTypeCode, multipartFile);
		// TODO Add the message to the message resource with translation
		return ResponseEntity.ok(new ApiResponse(true, "The file has been uploaded successfully"));
	}

	@PutMapping(path = "/{id}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
									@Valid @RequestPart(value = "file") final MultipartFile multipartFile) {
		service.update(id, multipartFile);
		// TODO Add the message to the message resource with translation
		return ResponseEntity.ok(new ApiResponse(true, "The file with id: " + id + " has been updated successfully"));
	}

	@DeleteMapping (path = "/{id}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> delete(@PathVariable @NotNull final Long id) {
		service.delete(id);
		// TODO Add the message to the message resource with translation
		return ResponseEntity.ok(new ApiResponse(true, "The file with id: " + id + " has been deleted successfully"));
	}
}
