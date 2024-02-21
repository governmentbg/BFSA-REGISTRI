package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.BranchDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.BranchI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.repository.BranchRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BranchService {

    private final BranchRepository repository;
    private final SettlementService settlementService;

    @Transactional(readOnly = true)
    public Branch findById(final Long id) {
        return repository.get(id).orElseThrow(() -> new EntityNotFoundException(Branch.class, id));
    }

    @Transactional(readOnly = true)
    public Branch findByIdOrNull(final Long id) {
        return repository.get(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Branch> findAll() {
        return repository.getAll();
    }

    public Branch findBySequenceNumber(final String sequenceNumber) {
        return repository.findBySequenceNumber(sequenceNumber).orElseThrow(() -> new EntityNotFoundException(Branch.class, sequenceNumber));
    }

    public Branch findByIdentifier(final String identifier) {
        return repository.findByIdentifier(identifier).orElseThrow(() -> new EntityNotFoundException(Branch.class, identifier));
    }

    public Branch findBySettlementCode(final String settlementCode) {
        return repository.findBySettlement_Code(settlementCode).orElseThrow(() -> new EntityNotFoundException(Branch.class, settlementCode));
    }

    @Transactional
    public Branch create(final Branch entity) {
        if (entity.getSettlement() != null && StringUtils.hasText(entity.getSettlement().getCode())) {
            entity.setSettlement(settlementService.findByCode(entity.getSettlement().getCode()));
        }
        if (Boolean.TRUE.equals(entity.getMain())) {
            removeMain();
        }
        return repository.save(entity);
    }

    @Transactional
    public Branch update(final Long id, final BranchDTO dto, final Language language) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID field is required");
        }
        if (!id.equals(dto.getId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
        }
        Branch entity = findById(id);
        if (Boolean.TRUE.equals(entity.getMain())) {
            if (!dto.getMain() || !dto.getEnabled()) {
                throw new RuntimeException("Choose another branch as main before updating this one");
            }
            removeMain();
        } else if (Boolean.TRUE.equals(dto.getMain()) && dto.getEnabled()) {
            removeMain();
        }

        entity.setMain(dto.getMain());
        entity.setEmail(dto.getEmail());
        entity.setPhone1(dto.getPhone1());
        entity.setPhone2(dto.getPhone2());
        entity.setPhone3(dto.getPhone3());
        entity.setEnabled(dto.getEnabled());
        entity.setIdentifier(dto.getIdentifier());

        if (StringUtils.hasText(dto.getSettlementCode())) {
            if (entity.getSettlement() == null || !dto.getSettlementCode().equals(entity.getSettlement().getCode())) {
                entity.setSettlement(settlementService.findByCode(dto.getSettlementCode()));
            }
        } else {
            entity.setSettlement(null);
        }

        BranchI18n i18n = entity.getI18n(language);
        i18n.setName(dto.getName());
        i18n.setAddress(dto.getAddress());
        i18n.setDescription(dto.getDescription());

        return repository.save(entity);
    }

    private void removeMain() {
        List<Branch> mains = repository.findAllByMainIsTrue();
        if (!CollectionUtils.isEmpty(mains)) {
            mains.forEach(m -> m.setMain(false));
        }
        repository.saveAll(mains);
    }
}
