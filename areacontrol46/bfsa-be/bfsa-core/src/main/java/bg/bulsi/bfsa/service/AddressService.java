package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.RevisionMetadataDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.repository.AddressRepository;
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
public class AddressService {

    private final AddressRepository repository;
    private final NomenclatureService nomenclatureService;
    private final SettlementService settlementService;

    public Address findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Address.class, id));
    }

    public Address findByIdOrNull(final Long id) {
        return repository.findById(id).orElse(null);
    }

    public Page<Address> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    public String getAddressInfo(final Long id, final String languageId) {
        Address address = findById(id);
        String info = settlementService.getInfo(address.getSettlement().getCode(), languageId);
        return info + ", " + address.getAddress();
    }

    @Transactional
    public Address create(final Address address) {
        if (address == null) {
            throw new RuntimeException("Address is required!");
        }
        address.setAddressType(address.getAddressType() != null && StringUtils.hasText(address.getAddressType().getCode())
                ? nomenclatureService.findByCode(address.getAddressType().getCode())
                : null);

        address.setSettlement(address.getSettlement() != null && StringUtils.hasText(address.getSettlement().getCode())
                ? settlementService.findByCode(address.getSettlement().getCode())
                : null);

        return repository.save(address);
    }

    @Transactional
    public Address update(final Long id, final AddressDTO dto) {
        if (id == null || id <= 0) {
            throw new EntityNotFoundException(Address.class, id);
        }
        if (!id.equals(dto.getId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
        }

        Address current = findById(id);
        current.setAddress(dto.getAddress());
        current.setAddressLat(dto.getAddressLat());
        current.setEnabled(dto.getEnabled());

        current.setAddressType(StringUtils.hasText(dto.getAddressTypeCode())
                ? nomenclatureService.findByCode(dto.getAddressTypeCode())
                : null);

        current.setSettlement(StringUtils.hasText(dto.getSettlementCode())
                ? settlementService.findByCode(dto.getSettlementCode())
                : null);

        return repository.save(current);
    }

    @Transactional(readOnly = true)
    public Page<Address> search(final String param, final Pageable pageable) {
        return repository.search(param, pageable);
    }

    @Transactional(readOnly = true)
    public List<AddressDTO> findRevisions(final Long id, final Language language) {
        List<AddressDTO> revisions = new ArrayList<>();
        repository.findRevisions(id).get().forEach(r -> {
            AddressDTO rev = AddressDTO.of(r.getEntity(), language);
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
