package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.InspectionDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.File;
import bg.bulsi.bfsa.model.Inspection;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.repository.InspectionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class InspectionService {

    private final InspectionRepository repository;
    private final UserService userService;
    private final FileStoreService fileStoreService;
    private final NomenclatureService nomenclatureService;

    public Inspection findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Inspection.class, id));
    }

    @Transactional(readOnly = true)
    public InspectionDTO findById(final Long id, final Language language) {
        return InspectionDTO.of(repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Inspection.class, id)), language);
    }

    public List<File> attachments(final Long id) {
        return fileStoreService.findAllByInspectionId(id);
    }

    @Transactional(readOnly = true)
    public Page<InspectionDTO> findAll(final Pageable pageable, final Language language) {
        return repository.findAllByOrderByLastModifiedDateDesc(pageable).map(i -> InspectionDTO.baseOf(i, language));
    }

    @Transactional
    public Inspection create(final Inspection inspection) {
//        if (inspection == null) {
//            throw new RuntimeException("Inspection cannot be null.");
//        }
//
//        if (!CollectionUtils.isEmpty(inspection.getUsers())) {
//            List<String> userIds = inspection.getUsers().stream().map(User::getId).toList();
//            inspection.getUsers().clear();
//            userIds.forEach(id -> inspection.getUsers().add(userService.findById(id)));
//        }
//
//        inspection.setFacility(inspection.getFacility() != null
//                ? facilityService.findById(inspection.getFacility().getId())
//                : null);
//
//        inspection.setVehicle(inspection.getVehicle() != null
//                ? vehicleService.findById(inspection.getVehicle().getId())
//                : null);
//
//        inspection.setRecord(inspection.getRecord() != null
//                ? recordService.findById(inspection.getRecord().getId())
//                : null);
//
//        if (inspection.getRecord() != null) {
//            if (ServiceType.S3180.equals(inspection.getRecord().getServiceType())) {
//                inspection.setType(InspectionType.FOR_APPROVAL);
//            }
//        }
//        if (!CollectionUtils.isEmpty(inspection.getReasons())) {
//            List<Nomenclature> reasons = new ArrayList<>();
//            inspection.getReasons().forEach(r ->
//                    reasons.add(nomenclatureService.findByCode(r.getCode())));
//
//            inspection.getReasons().clear();
//            inspection.getReasons().addAll(reasons);
//        }
//
//        inspection.setStatus(InspectionStatus.PROCESSING);

        return repository.save(inspection);
    }

    @Transactional
    public File attach(final Long id, final MultipartFile file, final String docTypeCode) {
        return fileStoreService.create(docTypeCode, id, file);
    }

    @Transactional
    public Inspection update(final Long id, final InspectionDTO dto, final Language language) {
        if (id == null || id <= 0) {
            throw new RuntimeException("id field is required");
        }

        if (!id.equals(dto.getId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
        }

        Inspection current = findById(id);
        current.getI18n(language).setDescription(dto.getDescription());
        current.setEndDate(dto.getEndDate());

        if (dto.getRiskLevel() != null) {
            current.setRiskLevel(dto.getRiskLevel());
        }

        current.getReasons().clear();
        if (!CollectionUtils.isEmpty(dto.getReasonsCodes())) {
            dto.getReasonsCodes().forEach(code -> current.getReasons().add(nomenclatureService.findByCode(code)));
        }

        current.getUsers().clear();
        if (!CollectionUtils.isEmpty(dto.getUsers())) {
            dto.getUsers().forEach(userId -> current.getUsers().add(userService.findById(userId)));
        }

        return repository.save(current);
    }
}
