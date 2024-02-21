package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Settlement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
class AddressServiceTest extends BaseServiceTest {
    @Autowired
    private AddressService service;
    @Autowired
    private AddressBuilder addressBuilder;
    @Autowired
    private NomenclatureBuilder nomenclatureBuilder;
    @Autowired
    private SettlementBuilder settlementBuilder;
    @Autowired
    private ContractorBuilder contractorBuilder;

    private static final String NEW_BG_NAME = "00100-ново-име";
    private static final String NEW_BG_DESC = "00100-ново-описание";
    private static final String FAKE_CODE = "FAKE_CODE";


    @Test
    void givenAddress_whenCreate_thenReturnAddress() {
        Address address = addressBuilder.mockAddress();
        Address saved = service.create(address);

        Assertions.assertNotNull(address);
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(address.getAddress(), saved.getAddress());
    }

    @Test
    void givenSomeAddresses_whenFinaAll_thenReturnAllAddresses() {
        Nomenclature nom = nomenclatureBuilder.saveNomenclature(langBG);
        Settlement settlement = settlementBuilder.saveSettlement();
        Contractor contractor = contractorBuilder.saveContractor();

        Address address = addressBuilder.mockAddress();
        address.setAddressType(nom);
        address.setSettlement(settlement);
        address.getContractors().add(contractor);

        Address newAddress = service.create(address);
        Assertions.assertNotNull(newAddress);

        Page<Address> saved = service.findAll(Pageable.unpaged());
        Assertions.assertNotNull(saved.getContent());
    }

    @Test
    void givenId_whenFinaById_thenReturnAddress() {
        Address addressDb = addressBuilder.saveAddress();
        Address address = service.findById(addressDb.getId());

        Assertions.assertNotNull(address);
        Assertions.assertEquals(addressDb.getId(), address.getId());
    }

    @Test
    void givenAddress_whenUpdate_thenReturnUpdatedAddress() {
        AddressDTO dto = AddressDTO.of(addressBuilder.saveAddress(), langBG);
        dto.setAddress(NEW_BG_NAME);
        dto.setAddressLat(NEW_BG_DESC);

        Address updated = service.update(dto.getId(), dto);
        Assertions.assertNotNull(updated);
        Assertions.assertEquals(dto.getAddress(), updated.getAddress());
        Assertions.assertEquals(dto.getAddressLat(), updated.getAddressLat());
    }

    @Test
    void givenFakeCode_whenUpdate_thenThrowsEntityNotFoundException() {
        Long fakeId = -1L;
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> service.update(
                        fakeId, AddressDTO.builder().id(fakeId).build())
        );

        Assertions.assertEquals("Entity '"
                + Address.class.getName() + "' with id/code='"
                + fakeId + "' not found",
                thrown.getMessage());
    }

    @Test
    void givenFakeId_whenFindById_thenThrowsEntityNotFoundException() {
        Long fakeId = -1L;
        EntityNotFoundException thrown = Assertions.assertThrows(
                EntityNotFoundException.class, () -> service.findById(fakeId)
        );

        Assertions.assertEquals("Entity '"
                        + Address.class.getName() + "' with id/code='"
                        + fakeId + "' not found",
                thrown.getMessage());
    }

    @Test
    void givenString_whenSearch_thenReturnAddress() {
        Address saved = addressBuilder.saveAddress();
        Assertions.assertNotNull(saved);

        String searchString = AddressBuilder.TEST_NAME_BG;
        Page<Address> result = service.search(searchString, Pageable.unpaged());
        Assertions.assertNotNull(result);
    }
}