package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ContractorPaperDTO;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.repository.ContractorPaperRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ContractorPaperService {

    private final ContractorPaperRepository repository;

    @Transactional(readOnly = true)
    public List<ContractorPaper> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ContractorPaperDTO> findAllDTOs(final Language language) {
        return ContractorPaperDTO.of(repository.findAll(), language);
    }

    @Transactional(readOnly = true)
    public ContractorPaper findById(final Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ContractorPaper.class, id));
    }

    @Transactional(readOnly = true)
    public ContractorPaperDTO getById(final Long id, final Language language) {
        return ContractorPaperDTO.of(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ContractorPaper.class, id)), language);
    }

    @Transactional(readOnly = true)
    public List<ContractorPaperDTO> getByContractorIdAndServiceTypeAndApprovalDocumentStatusIsActive(final Long contractorId, final ServiceType serviceType, final Language language) {
        if (contractorId == null || contractorId <= 0) {
            throw new RuntimeException("contractorID is required");
        }
        if (serviceType == null) {
            throw new RuntimeException("serviceType is required");
        }
        List<ContractorPaper> contractorPapers = repository.findByContractorIdAndServiceTypeAndStatus(contractorId, serviceType, ApprovalDocumentStatus.ACTIVE);
        if (ServiceType.S2701.equals(serviceType) && !CollectionUtils.isEmpty(contractorPapers) && contractorPapers.size() > 1) {
            throw new RuntimeException("Contractor with ID: " + contractorId +
                    " has more than one contractor paper of type " + serviceType + " with ApprovalDocumentStatus ACTIVE");
        }
        return ContractorPaperDTO.of(contractorPapers, language);
    }

    @Transactional(readOnly = true)
    public List<ContractorPaperDTO> getByContractorIdAndApprovalDocumentStatusIsActive(final Long contractorId, final Language language) {
        if (contractorId == null || contractorId <= 0) {
            throw new RuntimeException("contractorID is required");
        }
        List<ContractorPaper> contractorPapers = repository.findByContractorIdAndStatus(contractorId, ApprovalDocumentStatus.ACTIVE);
        return ContractorPaperDTO.of(contractorPapers, language);
    }
}
