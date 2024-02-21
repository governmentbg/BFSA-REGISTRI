package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.FertilizerCertDTO;
import bg.bulsi.bfsa.dto.RevisionMetadataDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.FertilizerCert;
import bg.bulsi.bfsa.model.FertilizerCertI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.repository.FertilizerCertRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FertilizerCertService {

    private final FertilizerCertRepository repository;
    private final NomenclatureService nomenclatureService;
    private final ContractorService contractorService;

    private final static String FERTILIZER_REQUIRED_ERROR = "Certified Fertilizer is required!";

    @Transactional(readOnly = true)
    public FertilizerCert findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(FertilizerCert.class, id));
    }

    @Transactional(readOnly = true)
    public FertilizerCert findByIdOrNull(final Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<FertilizerCert> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public FertilizerCert create(final FertilizerCert source) {
        if (source == null) {
            throw new RuntimeException(FERTILIZER_REQUIRED_ERROR);
        }

        source.setFertilizerType(source.getFertilizerType() != null && StringUtils.hasText(source.getFertilizerType().getCode())
                ? nomenclatureService.findByCode(source.getFertilizerType().getCode())
                : null);

        source.setManufacturer(source.getManufacturer() != null && source.getManufacturer().getId() != null && source.getManufacturer().getId() > 0
                ? contractorService.findById(source.getManufacturer().getId())
                : null);

        source.setCertificateHolder(source.getCertificateHolder() != null && source.getCertificateHolder().getId() != null && source.getCertificateHolder().getId() > 0
                ? contractorService.findById(source.getCertificateHolder().getId())
                : null);

        return repository.save(source);
    }

    @Transactional
    public FertilizerCertDTO update(final Long id, final FertilizerCertDTO dto, Language language) {
        if (id == null || !id.equals(dto.getId())) {
            throw new RuntimeException(FERTILIZER_REQUIRED_ERROR);
        }

        FertilizerCert entity = findById(id);
        BeanUtils.copyProperties(dto, entity);

        FertilizerCertI18n i18n = entity.getI18n(language);
        i18n.setName(dto.getName());
        i18n.setIngredients(dto.getIngredients());
        i18n.setWording(dto.getWording());
        i18n.setCrop(dto.getCrop());
        i18n.setApplication(dto.getApplication());
        i18n.setReason(dto.getReason());
        i18n.setDescription(dto.getDescription());

        entity.setFertilizerType(StringUtils.hasText(dto.getFertilizerTypeCode())
                ? nomenclatureService.findByCode(dto.getFertilizerTypeCode())
                : null);

        entity.setManufacturer(dto.getManufacturerId() != null && dto.getManufacturerId() > 0
                ? contractorService.findById(dto.getManufacturerId())
                : null);

        entity.setCertificateHolder(dto.getCertificateHolderId() != null && dto.getCertificateHolderId() > 0
                ? contractorService.findById(dto.getCertificateHolderId())
                : null);

        return FertilizerCertDTO.of(repository.save(entity), language);
    }

    @Transactional(readOnly = true)
    public List<FertilizerCertDTO> findRevisions(final Long id, Language language) {
        List<FertilizerCertDTO> revisions = new ArrayList<>();
        repository.findRevisions(id).get().forEach(r -> {
            FertilizerCertDTO rev = FertilizerCertDTO.of(r.getEntity(), language);
            rev.setRevisionMetadata(RevisionMetadataDTO.builder()
                    .revisionNumber(r.getMetadata().getRevisionNumber().orElse(null))
                    .revisionInstant(r.getMetadata().getRevisionInstant().orElse(null))
                    .revisionType(r.getMetadata().getRevisionType().name())
                    .createdBy(r.getEntity().getCreatedBy())
                    .createdDate(r.getEntity().getCreatedDate())
                    .lastModifiedBy(r.getEntity().getLastModifiedBy())
                    .lastModifiedDate(r.getEntity().getLastModifiedDate())
                    .build()
            );
            revisions.add(rev);
        });
        return revisions;
    }
}
