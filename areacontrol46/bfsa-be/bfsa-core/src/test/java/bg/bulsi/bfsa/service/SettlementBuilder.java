package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.enums.PlaceType;
import bg.bulsi.bfsa.enums.TSB;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.CountryI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Settlement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SettlementBuilder {
    @Autowired
    private SettlementService settlementService;

    @Autowired
    private CountryService countryService;

    public static final String NAME_BLG_BG = "Благоевград";
    public static final String NAME_BLG_LAT = "Blagoevgrad";
    public static final String DISTRICT_BLG = "BLG";
    public static final String MUNICIPALITY_BLG = "BLG03";
    public static final PlaceType PLACE_TYPE_CITY = PlaceType.CITY;
    public static final TSB TSB_SW = TSB.SW;

    public Settlement saveSettlement() {
        return saveSettlement(null);
    }

    public Settlement saveSettlement(final String code) {
        Settlement s = mockSettlement(saveCountry());
        if (StringUtils.hasText(code)) {
            s.setCode(code);
        }
        return settlementService.create(s);
    }

    public List<Settlement> saveSettlements() {
        List<Settlement> settlements = new ArrayList<>();
        settlements.add(saveSettlement());
        settlements.add(saveSettlement());
        settlements.add(saveSettlement());
        settlements.add(saveSettlement());

        return settlements;
    }

    public Settlement mockSettlement(final Country country) {
        Settlement settlement =
                Settlement.builder()
                        .code(UUID.randomUUID().toString())
                        .regionCode(DISTRICT_BLG)
                        .regionName(DISTRICT_BLG)
                        .regionNameLat(DISTRICT_BLG)
                        .name(NAME_BLG_BG)
                        .nameLat(NAME_BLG_LAT)
                        .municipalityCode(MUNICIPALITY_BLG)
                        .municipalityName(MUNICIPALITY_BLG)
                        .municipalityNameLat(MUNICIPALITY_BLG)
                        .placeType(PLACE_TYPE_CITY)
                        .tsb(TSB_SW)
                        .country(country)
                        .enabled(true)
                        .build();

        return settlement;
    }

    public Settlement mockSettlementWithSubSettlements() {
        return mockSettlementWithSubSettlements(null);
    }

    public Settlement mockSettlementWithSubSettlements(Country country) {
        Settlement settlement = mockSettlement(country != null ? country : mockCountry());
        Settlement sub0 = mockSettlement(country != null ? country : mockCountry());
        Settlement sub1 = mockSettlement(country != null ? country : mockCountry());

        sub0.setParentCode(settlement.getCode());
        settlement.getSubSettlements().add(sub0);
        sub1.setParentCode(settlement.getCode());
        settlement.getSubSettlements().add(sub1);

        return settlement;
    }

    Country mockCountry() {
        Country country = new Country();
        country.setCode(UUID.randomUUID().toString().substring(0, 10));
        country.setIsoAlpha3(UUID.randomUUID().toString().substring(0, 10));
        country.setContinent(UUID.randomUUID().toString().substring(0, 10));
        country.setCurrencyCode(UUID.randomUUID().toString().substring(0, 10));
        country.setEnabled(true);

        CountryI18n i18n = country.getI18n(Language.builder().languageId("BG").build());
        i18n.setName(UUID.randomUUID().toString().substring(0, 10));
        i18n.setCapital(UUID.randomUUID().toString().substring(0, 10));
        i18n.setContinentName(UUID.randomUUID().toString().substring(0, 10));
        i18n.setDescription(UUID.randomUUID().toString().substring(0, 10));

        return country;
    }
    Country saveCountry() {
        return countryService.create(mockCountry());
    }
}
