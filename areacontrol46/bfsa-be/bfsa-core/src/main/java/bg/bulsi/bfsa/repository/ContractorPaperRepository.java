package bg.bulsi.bfsa.repository;

import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ContractorPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractorPaperRepository extends JpaRepository<ContractorPaper, Long>, RevisionRepository<ContractorPaper, Long, Long> {

    List<ContractorPaper> findByContractorIdAndServiceTypeAndStatus(final Long contractorId, final ServiceType serviceType, final ApprovalDocumentStatus approvalDocumentStatus);

    List<ContractorPaper> findByContractorIdAndStatus(final Long contractorId, final ApprovalDocumentStatus approvalDocumentStatus);

}
