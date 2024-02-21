package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Nomenclature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AddressBuilder {
    @Autowired
    private AddressService service;
    @Autowired
    private SettlementBuilder settlementBuilder;

    public static final Boolean ENABLED = true;
    public static final String TEST_NAME_BG = "Адрес";
    public static final String TEST_NAME_EN = "Address";

    public Address saveAddress() {
        return saveAddress(null);
    }

    public Address saveAddress(Nomenclature addressType) {
        return service.create(mockAddress(addressType));
    }

    public Address mockAddress() {
        return mockAddress(null);
    }

    public Address mockAddress(Nomenclature addressType) {
        return Address.builder()
                .address(TEST_NAME_BG + UUID.randomUUID())
                .settlement(settlementBuilder.saveSettlement())
                .addressLat(TEST_NAME_EN + UUID.randomUUID())
                .enabled(ENABLED)
                .addressType(addressType)
                .url(UUID.randomUUID().toString())
                .mail(UUID.randomUUID().toString())
                .postCode(UUID.randomUUID().toString())
                .phone(UUID.randomUUID().toString())
                .build();
    }
}
