package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ContractorVehicleDTO;
import bg.bulsi.bfsa.dto.FoodSupplementDTO;
import bg.bulsi.bfsa.dto.reports.ContractorInfoDTO;
import bg.bulsi.bfsa.model.Language;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ReportService {

    private final ContractorService contractorService;
    private final FoodSupplementService foodSupplementService;

    @Transactional(readOnly = true)
    public ContractorInfoDTO getContractorInfo(final String identifier, final Language language) {
        return ContractorInfoDTO.of(contractorService.findByIdentifier(identifier), language);
    }

    @Transactional(readOnly = true)
    public List<ContractorVehicleDTO> getContractorVehicles(final String identifier, final Language language) {
        return ContractorVehicleDTO.of(contractorService.findByIdentifier(identifier).getContractorVehicles(), language);
    }

    @Transactional(readOnly = true)
    public Page<FoodSupplementDTO> findAllContractorFoodSupplements(final String identifier, final Language language, final Pageable pageable) {
        return foodSupplementService.findAllByApplicantIdentifier(identifier, language, pageable);
    }

}
