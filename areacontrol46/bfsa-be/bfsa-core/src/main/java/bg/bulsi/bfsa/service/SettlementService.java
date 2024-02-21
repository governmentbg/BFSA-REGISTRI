package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.RevisionMetadataDTO;
import bg.bulsi.bfsa.dto.SettlementDTO;
import bg.bulsi.bfsa.dto.SettlementVO;
import bg.bulsi.bfsa.enums.PlaceType;
import bg.bulsi.bfsa.enums.TSB;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Settlement;
import bg.bulsi.bfsa.repository.SettlementRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class SettlementService {
    private final SettlementRepository repository;
    private final CountryService countryService;

    private final static String SETTLEMENT_REQUIRED_ERROR = "Settlement is required!";

    public Settlement findByCode(final String code) {
        return repository.findByCode(code).orElseThrow(() -> new EntityNotFoundException(Settlement.class, code));
    }

    public Settlement findByCodeOrNull(final String code) {
        return repository.findByCode(code).orElse(null);
    }

    public Settlement findRegionBySettlementCode(final String code) {
        return repository.findRegionBySettlementCode(code).orElseThrow(() -> new EntityNotFoundException(Settlement.class, code));
    }

    @Transactional(readOnly = true)
    public List<SettlementVO> findAllRegionSettlements(final String code) {
        return repository.findAllRegionSettlementsVO(code);
    }

    @Transactional(readOnly = true)
    public List<SettlementVO> findAllMunicipalitySettlements(final String code) {
        return repository.findAllMunicipalitySettlementsVO(code);
    }

    @Transactional(readOnly = true)
    public SettlementDTO findByCodeDto(final String code) {
        return SettlementDTO.of(repository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(Settlement.class, code)));
    }

    @Transactional(readOnly = true)
    public List<SettlementVO> findAllByParentCodeSettlementVO(final String parentCode) {
        return repository.findAllByParentCodeSettlementVO(parentCode);
    }

    @Transactional(readOnly = true)
    public List<SettlementDTO> findAllParents() {
        return SettlementDTO.of(repository.findAllByParentCodeIsNull());
    }

    @Transactional(readOnly = true)
    public Page<SettlementDTO> findAllParentsWithoutSubs(Pageable pageable) {
        return repository.findAllSettlementDTOWithoutSubs(pageable);
    }

    @Transactional(readOnly = true)
    public SettlementVO findByCodeSettlementVO(String code) {
        return repository.findByCodeSettlementVO(code);
    }

    @Transactional(readOnly = true)
    public List<SettlementVO> findAllSettlementVO() {
        return repository.findAllSettlementVO();
    }

    @Transactional
    public Settlement create(final Settlement settlement) {

        if (settlement == null) {
            throw new RuntimeException(SETTLEMENT_REQUIRED_ERROR);
        }
        settlement.setCountry(settlement.getCountry() != null
                ? countryService.findByCode(settlement.getCountry().getCode())
                : null);

        return repository.save(settlement);
    }

    public SettlementDTO updateDTO(final String code, final SettlementDTO dto) {
        return SettlementDTO.of(update(code, dto));
    }

    @Transactional
    public Settlement update(final String code, final SettlementDTO dto) {

        if (!StringUtils.hasText(dto.getMunicipalityCode())) {
            throw new RuntimeException("Municipality is required!");
        }
        if (!StringUtils.hasText(dto.getPlaceType())) {
            throw new RuntimeException("Place type is required!");
        }
        if (!StringUtils.hasText(dto.getTsb())) {
            throw new RuntimeException("TSB is required!");
        }

        Settlement settlement = findByCode(code);
        settlement.setEnabled(dto.getEnabled());
        settlement.setName(dto.getName());
        settlement.setNameLat(dto.getNameLat());
        settlement.setRegionCode(dto.getRegionCode());
        settlement.setMunicipalityCode(dto.getMunicipalityCode());
        settlement.setPlaceType(PlaceType.valueOf(dto.getPlaceType()));
        settlement.setTsb(TSB.valueOf(dto.getTsb()));
        settlement.setCountry(StringUtils.hasText(dto.getCountryCode())
                ? countryService.findByCode(dto.getCountryCode())
                : null);

        return repository.save(settlement);
    }

    @Transactional(readOnly = true)
    public List<SettlementDTO> findRevisions(final String id) {
        List<SettlementDTO> revisions = new ArrayList<>();
        repository.findRevisions(id).get().forEach(r -> {
            SettlementDTO rev = SettlementDTO.of(r.getEntity());
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

    public String getInfo(final String code, final String languageId) {
        SettlementVO r = findByCodeSettlementVO(code);
        StringBuilder sb = new StringBuilder();

        switch (r.getPlaceType()) {
            case RESORT -> sb.append(Language.BG.equals(languageId) ? "к.с. " : "res. ");
            case MONASTERY -> sb.append(Language.BG.equals(languageId) ? "ман. " : "mon. ");
            case CITY -> sb.append(Language.BG.equals(languageId) ? "гр. " : "c. ");
            case VILLAGE -> sb.append(Language.BG.equals(languageId) ? "с. " : "vill. ");
        }
        sb.append(r.getName()).append(Language.BG.equals(languageId) ? ", общ. " : ", mun. ")
                .append(r.getMunicipalityName())
                .append(Language.BG.equals(languageId) ? ", обл. " : ", reg. ")
                .append(r.getRegionName());

        return sb.toString();
    }

    public SettlementVO getCodes(final String settlementCode) {
        SettlementVO response = repository.findSettlementVOByCode(settlementCode);

        if (!StringUtils.hasText(response.getRegionCode())){
            response.setRegionCode(response.getMunicipalityCode());
            response.setMunicipalityCode(null);
            response.setSettlementCode(null);
            response.setResortCode(null);
        }

        return  response;
    }

    public List<SettlementVO> search(final String param) {
        return repository.search(param);
    }
}
