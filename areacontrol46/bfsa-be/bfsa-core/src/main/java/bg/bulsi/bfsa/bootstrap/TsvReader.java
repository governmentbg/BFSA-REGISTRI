package bg.bulsi.bfsa.bootstrap;

import bg.bulsi.bfsa.enums.PlaceType;
import bg.bulsi.bfsa.enums.TSB;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.*;
import bg.bulsi.bfsa.repository.SettlementRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static bg.bulsi.bfsa.util.TextFileUtil.processFileByLine;
import static bg.bulsi.bfsa.util.TextFileUtil.valueAtIndex;

@Slf4j
public class TsvReader {

    public static final String TAB_DELIMITER = "\\t";

    private TsvReader() {
        // empty constructor, this is "utility" class with only static methods
    }

    public static List<TsvMessageResource> tsvMessageResourceList(Class clazz, final String filename) {
        final InputStream is = clazz.getResourceAsStream(filename);
        return tsvMessageResourceList(is, filename);
    }

    public static List<TsvMessageResource> tsvMessageResourceList(final InputStream inputStream, final String filename) {
        final List<TsvMessageResource> list = new ArrayList<>();

        processFileByLine(filename, inputStream, TAB_DELIMITER,
                List.of(
                        "Code",
                        "Language ID",
                        "Message"
                ),
                line -> {
                    final String code = valueAtIndex(line, 0);
                    final String languageId = valueAtIndex(line, 1);
                    final String message = valueAtIndex(line, 2);

                    list.add(TsvMessageResource.builder()
                            .code(code)
                            .languageId(languageId)
                            .message(message).build());
                });

        return list;
    }

    public static List<TsvSettlement> tsvSettlementList(Class clazz, final String filename) {
        final InputStream is = clazz.getResourceAsStream(filename);
        return tsvSettlementList(is, filename);
    }

    public static List<TsvSettlement> tsvSettlementList(final InputStream inputStream, final String filename) {
        final List<TsvSettlement> list = new ArrayList<>();

        processFileByLine(filename, inputStream, TAB_DELIMITER,
                List.of(
                        "Code",
                        "Country Code",
                        "Region Code",
                        "Region Name",
                        "Region NameLat",
                        "Enable",
                        "Municipality Code",
                        "Municipality Name",
                        "Municipality NameLat",
                        "Name",
                        "NameLat",
                        "Place type",
                        "TSB",
                        "Parent code"
                ),
                line -> {
                    final String code = valueAtIndex(line, 0);
                    final String countryCode = valueAtIndex(line, 1);
                    final String regionCode = valueAtIndex(line, 2);
                    final String regionName = valueAtIndex(line, 3);
                    final String regionNameLat = valueAtIndex(line, 4);
                    final Boolean enable = Boolean.valueOf(valueAtIndex(line, 5));
                    final String municipalityCode = valueAtIndex(line, 6);
                    final String municipalityName = valueAtIndex(line, 7);
                    final String municipalityNameLat = valueAtIndex(line, 8);
                    final String name = valueAtIndex(line, 9);
                    final String nameLat = valueAtIndex(line, 10);
                    final String placeType = valueAtIndex(line, 11);
                    final String tsb = valueAtIndex(line, 12);
                    final String parentCode = valueAtIndex(line, 13);

                    list.add(TsvSettlement.builder()
                            .code(code)
                            .countryCode(countryCode)
                            .regionCode(regionCode)
                            .regionName(regionName)
                            .regionNameLat(regionNameLat)
                            .enabled(enable)
                            .municipalityCode(municipalityCode)
                            .municipalityName(municipalityName)
                            .municipalityNameLat(municipalityNameLat)
                            .name(name)
                            .nameLat(nameLat)
                            .placeType(placeType)
                            .tsb(tsb)
                            .parentCode(StringUtils.hasText(parentCode) ? parentCode : null)
                            .build());
                });

        return list;
    }

    public static List<TsvBranch> tsvBranchList(final InputStream inputStream, final String filename) {
        final List<TsvBranch> list = new ArrayList<>();

        processFileByLine(filename, inputStream, TAB_DELIMITER,
                List.of(
                        "Settlement Code",
                        "Sequence Number",
                        "Email",
                        "Phone1",
                        "Phone2",
                        "Phone3",
                        "Name",
                        "Address",
                        "Description",
                        "Enabled",
                        "Name En",
                        "Address En",
                        "Description En",
                        "Identifier"
                ),
                line -> {
                    final String settlementCode = valueAtIndex(line, 0);
                    final String sequenceNumber = valueAtIndex(line, 1);
                    final String email = valueAtIndex(line, 2);
                    final String phone1 = valueAtIndex(line, 3);
                    final String phone2 = valueAtIndex(line, 4);
                    final String phone3 = valueAtIndex(line, 5);
                    final String name = valueAtIndex(line, 6);
                    final String address = valueAtIndex(line, 7);
                    final String description = valueAtIndex(line, 8);
                    final Boolean enabled = Boolean.valueOf(valueAtIndex(line, 9));
                    final String nameEn = valueAtIndex(line, 10);
                    final String addressEn = valueAtIndex(line, 11);
                    final String descriptionEn = valueAtIndex(line, 12);
                    final String identifier = valueAtIndex(line, 13);

                    list.add(TsvBranch.builder()
                            .settlementCode(settlementCode)
                            .sequenceNumber(sequenceNumber)
                            .email(email)
                            .phone1(phone1)
                            .phone2(phone2)
                            .phone3(phone3)
                            .name(name)
                            .address(address)
                            .description(description)
                            .enabled(enabled)
                            .nameEn(nameEn)
                            .addressEn(addressEn)
                            .descriptionEn(descriptionEn)
                            .identifier(identifier)
                            .build());
                });

        return list;
    }

    public static List<TsvBranch> tsvBranchList(Class clazz, final String filename) {
        final InputStream is = clazz.getResourceAsStream(filename);
        return tsvBranchList(is, filename);
    }

    public static List<TsvCountry> tsvCountryList(Class clazz, final String filename) {
        final InputStream is = clazz.getResourceAsStream(filename);
        return tsvCountryList(is, filename);
    }

    public static List<TsvCountry> tsvCountryList(final InputStream inputStream, final String filename) {
        final List<TsvCountry> list = new ArrayList<>();

        processFileByLine(filename, inputStream, TAB_DELIMITER,
                List.of(
                        "Code",
                        "Iso Alpha 3",
                        "Continent",
                        "Currency Code",
                        "Enabled",
                        "Name BG",
                        "Capital Name BG",
                        "Continent Name BG",
                        "Desc BG",
                        "Name EN",
                        "Capital Name EN",
                        "Continent Name EB",
                        "Desc EN",
                        "European Union Member"
                ),
                line -> {
                    final String code = valueAtIndex(line, 0);
                    final String isoAlpha3 = valueAtIndex(line, 1);
                    final String continent = valueAtIndex(line, 2);
                    final String currencyCode = valueAtIndex(line, 3);
                    final Boolean enabled = Boolean.valueOf(valueAtIndex(line, 4));
                    final String nameBg = valueAtIndex(line, 5);
                    final String capitalBg = valueAtIndex(line, 6);
                    final String continentNameBg = valueAtIndex(line, 7);
                    final String descBg = valueAtIndex(line, 8);
                    final String nameEn = valueAtIndex(line, 9);
                    final String capitalEn = valueAtIndex(line, 10);
                    final String continentNameEn = valueAtIndex(line, 11);
                    final String descEn = valueAtIndex(line, 12);
                    final Boolean europeanUnionMember = Boolean.valueOf(valueAtIndex(line, 13));

                    list.add(TsvCountry.builder()
                            .code(code)
                            .isoAlpha3(isoAlpha3)
                            .continent(continent)
                            .currencyCode(currencyCode)
                            .enabled(enabled)
                            .nameBg(nameBg)
                            .capitalBg(capitalBg)
                            .continentNameBg(continentNameBg)
                            .descBg(descBg)
                            .nameEn(nameEn)
                            .capitalEn(capitalEn)
                            .continentNameEn(continentNameEn)
                            .descEn(descEn)
                            .europeanUnionMember(europeanUnionMember)
                            .build());
                });
        return list;
    }

    public static List<TsvNomenclature> tsvNomenclatureList(Class clazz, final String filename) {
        final InputStream is = clazz.getResourceAsStream(filename);
        return tsvNomenclatureList(is, filename);
    }

    public static List<TsvNomenclature> tsvNomenclatureList(final InputStream inputStream, final String filename) {
        final List<TsvNomenclature> list = new ArrayList<>();

        processFileByLine(filename, inputStream, TAB_DELIMITER,
                List.of(
                        "Code",
                        "Enabled",
                        "Name BG",
                        "Desc BG",
                        "Name EN",
                        "Desc EN",
                        "Parent Code",
                        "Symbol",
                        "External Code"
                ),
                line -> {
                    final String code = valueAtIndex(line, 0);
                    final Boolean enabled = Boolean.valueOf(valueAtIndex(line, 1));
                    final String nameBg = valueAtIndex(line, 2);
                    final String descBg = valueAtIndex(line, 3);
                    final String nameEn = valueAtIndex(line, 4);
                    final String descEn = valueAtIndex(line, 5);
                    final String parentCode = valueAtIndex(line, 6);
                    final String symbol = valueAtIndex(line, 7);
                    final String externalCode = valueAtIndex(line, 8);

                    list.add(TsvNomenclature.builder()
                            .code(code)
                            .enabled(enabled)
                            .nameBg(nameBg)
                            .descBg(descBg)
                            .nameEn(nameEn)
                            .parentCode(parentCode)
                            .descEn(descEn)
                            .symbol(symbol)
                            .externalCode(externalCode)
                            .build());
                });
        return list;
    }

    public static List<TsvActivityGroup> tsvActivityGroupList(final InputStream inputStream, final String filename) {
        final List<TsvActivityGroup> list = new ArrayList<>();

        processFileByLine(filename, inputStream, TAB_DELIMITER,
                List.of(
                        "Enabled",
                        "NameBg",
                        "DescriptionBg",
                        "NameEn",
                        "DescriptionEn",
                        "Level",
                        "GroupRelatedActivityCategories",
                        "AnimalSpecies",
                        "Remarks",
                        "AssociatedActivityCategories"
                ),
                line -> {
                    final Boolean enabled = Boolean.valueOf(valueAtIndex(line, 0));
                    final String nameBg = valueAtIndex(line, 1);
                    final String descriptionBg = valueAtIndex(line, 2);
                    final String nameEn = valueAtIndex(line, 3);
                    final String descriptionEn = valueAtIndex(line, 4);
                    final Long level = Long.parseLong(valueAtIndex(line, 5));
                    final String groupRelatedActivityCategories = valueAtIndex(line, 6);
                    final String groupAnimalSpecies = valueAtIndex(line, 7);
                    final String remarks = valueAtIndex(line, 8);
                    final String associatedActivityCategories = valueAtIndex(line, 9);

                    list.add(TsvActivityGroup
                            .builder()
                            .enabled(enabled)
                            .nameBg(nameBg)
                            .descriptionBg(descriptionBg)
                            .nameEn(nameEn)
                            .descriptionEn(descriptionEn)
                            .level(level)
                            .relatedActivityCategories(groupRelatedActivityCategories)
                            .animalSpecies(groupAnimalSpecies)
                            .remarks(remarks)
                            .associatedActivityCategories(associatedActivityCategories)
                            .build());
                });

        return list;
    }

    public static List<TsvActivityGroup> tsvActivityGroupList(Class clazz, final String filename) {
        final InputStream is = clazz.getResourceAsStream(filename);
        return tsvActivityGroupList(is, filename);
    }


    public static List<TsvClassifier> tsvClassifierList(Class clazz, final String filename) {
        final InputStream is = clazz.getResourceAsStream(filename);
        return tsvClassifierList(is, filename);
    }

    public static List<TsvClassifier> tsvClassifierList(final InputStream inputStream, final String filename) {
        final List<TsvClassifier> list = new ArrayList<>();

        processFileByLine(filename, inputStream, TAB_DELIMITER,
                List.of(
                        "Code",
                        "Enabled",
                        "Name BG",
                        "Desc BG",
                        "Name EN",
                        "Desc EN",
                        "Parent Code",
                        "External Code"
                ),
                line -> {
                    final String code = valueAtIndex(line, 0);
                    final Boolean enabled = Boolean.valueOf(valueAtIndex(line, 1));
                    final String nameBg = valueAtIndex(line, 2);
                    final String descBg = valueAtIndex(line, 3);
                    final String nameEn = valueAtIndex(line, 4);
                    final String descEn = valueAtIndex(line, 5);
                    final String parentCode = valueAtIndex(line, 6);
                    final String externalCode = valueAtIndex(line, 7);

                    list.add(TsvClassifier.builder()
                            .code(code)
                            .enabled(enabled)
                            .nameBg(nameBg)
                            .descBg(descBg)
                            .nameEn(nameEn)
                            .parentCode(parentCode)
                            .descEn(descEn)
                            .externalCode(externalCode)
                            .build());
                });
        return list;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    public static class TsvMessageResource {
        private final String code, languageId, message;

        public static MessageResource to(TsvMessageResource tsv) {
            return MessageResource.builder()
                    .messageResourceIdentity(new MessageResource.MessageResourceIdentity(tsv.getCode(), tsv.getLanguageId()))
                    .message(tsv.getMessage()).build();
        }

        public static List<MessageResource> to(final List<TsvMessageResource> list) {
            return list.stream().map(TsvMessageResource::to).collect(Collectors.toList());
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TsvSettlement {
        private final String code;
        private final String countryCode;
        private final String name;
        private final String nameLat;
        private final Boolean enabled;
        private final String regionCode;
        private final String regionName;
        private final String regionNameLat;
        private final String municipalityCode;
        private final String municipalityName;
        private final String municipalityNameLat;
        private final String placeType;
        private final String tsb;
        private final String parentCode;

        public static Settlement to(TsvSettlement tsv) {
            Settlement settlement = new Settlement();
            settlement.setCode(tsv.code);
            settlement.setName(tsv.name);
            settlement.setNameLat(tsv.nameLat);
            settlement.setEnabled(tsv.enabled);
            settlement.setRegionCode(tsv.regionCode);
            settlement.setRegionName(tsv.regionName);
            settlement.setRegionNameLat(tsv.regionNameLat);
            settlement.setMunicipalityCode(tsv.municipalityCode);
            settlement.setMunicipalityName(tsv.municipalityName);
            settlement.setMunicipalityNameLat(tsv.municipalityNameLat);
            settlement.setTsb(TSB.valueOf(tsv.tsb));
            settlement.setPlaceType(PlaceType.valueOf(tsv.placeType));
            settlement.setParentCode(tsv.parentCode);

            settlement.setCountry(Country.builder().code(tsv.getCountryCode()).build());

            return settlement;
        }

        public static List<Settlement> to(final List<TsvSettlement> list) {
            return list.stream().map(TsvSettlement::to).collect(Collectors.toList());
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TsvBranch {
        final String sequenceNumber;
        final String settlementCode;
        final String email;
        final String phone1;
        final String phone2;
        final String phone3;
        final String name;
        final String address;
        final String description;
        final boolean enabled;
        final String nameEn;
        final String addressEn;
        final String descriptionEn;
        final String identifier;

        public static Branch to(final TsvBranch tsv, final SettlementRepository settlementRepository) {
            Language languageBg = Language.builder().languageId("bg").build();
            Language languageEn = Language.builder().languageId("en").build();
            Branch branch = new Branch();

            branch.setSettlement(settlementRepository.findByCode(tsv.getSettlementCode())
                    .orElseThrow(() -> new EntityNotFoundException(Settlement.class, tsv.getSettlementCode()))
            );
            branch.setIdentifier(tsv.identifier);
            branch.setSequenceNumber(tsv.sequenceNumber);
            branch.setEmail(tsv.email);
            branch.setPhone1(tsv.phone1);
            branch.setPhone2(tsv.phone2);
            branch.setPhone3(tsv.phone3);

            branch.getI18ns().add(new BranchI18n(tsv.getName(), tsv.getAddress(), tsv.getDescription(), branch, languageBg));
            branch.getI18ns().add(new BranchI18n(tsv.getNameEn(), tsv.getAddressEn(), tsv.getDescriptionEn(), branch, languageEn));

            branch.getI18n(languageBg).setName(tsv.name);
            branch.getI18n(languageBg).setAddress(tsv.address);
            branch.getI18n(languageBg).setDescription(tsv.description);
            branch.setEnabled(tsv.isEnabled());
            return branch;
        }

        public static List<Branch> to(final List<TsvBranch> list, final SettlementRepository settlementRepository) {
            return list.stream().map(t -> to(t, settlementRepository)).collect(Collectors.toList());
        }

    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TsvCountry {
        private String code;
        private String isoAlpha3;
        private String continent;
        private String currencyCode;
        private Boolean enabled;
        private String nameBg;
        private String capitalBg;
        private String continentNameBg;
        private String descBg;
        private String nameEn;
        private String capitalEn;
        private String continentNameEn;
        private String descEn;
        private Boolean europeanUnionMember;

        public static Country to(final TsvCountry tsv) {
            Country country = new Country();

            country.setCode(tsv.getCode());
            country.setIsoAlpha3(tsv.getIsoAlpha3());
            country.setContinent(tsv.getContinent());
            country.setCurrencyCode(tsv.getCurrencyCode());
            country.setEnabled(tsv.getEnabled());

            country.getI18ns().add(new CountryI18n(tsv.getNameBg(), tsv.getCapitalBg(), tsv.getContinentNameBg(),
                    tsv.getDescBg(), country, Language.builder().languageId("bg").build()));

            country.getI18ns().add(new CountryI18n(tsv.getNameEn(), tsv.getCapitalEn(), tsv.getContinentNameEn(),
                    tsv.getDescEn(), country, Language.builder().languageId("en").build()));

            country.setEuropeanUnionMember(tsv.getEuropeanUnionMember());

            return country;
        }

        public static List<Country> to(final List<TsvCountry> list) {
            return list.stream().map(c -> to(c)).collect(Collectors.toList());
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TsvNomenclature {
        private String code;
        private Boolean enabled;
        private String nameBg;
        private String descBg;
        private String nameEn;
        private String descEn;
        private String parentCode;
        private String symbol;
        private String externalCode;

        public static Nomenclature to(final TsvNomenclature tsv) {
            Nomenclature nomenclature = new Nomenclature();
            nomenclature.setCode(tsv.getCode());
            nomenclature.setSymbol(tsv.getSymbol());
            nomenclature.setExternalCode(tsv.getExternalCode());
            nomenclature.setEnabled(tsv.getEnabled());
            nomenclature.setParent(StringUtils.hasText(tsv.getParentCode()) ? Nomenclature.builder().code(tsv.getParentCode()).build() : null);

            nomenclature.getI18ns().add(new NomenclatureI18n(tsv.getNameBg(), tsv.getDescBg(),
                    nomenclature, Language.builder().languageId("bg").build()));

            nomenclature.getI18ns().add(new NomenclatureI18n(tsv.getNameEn(), tsv.getDescEn(),
                    nomenclature, Language.builder().languageId("en").build()));

            return nomenclature;
        }

        public static List<Nomenclature> to(final List<TsvNomenclature> list) {
            return list.stream().map(n -> to(n)).collect(Collectors.toList());
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TsvActivityGroup {
        private String nameBg;
        private String descriptionBg;
        private String nameEn;
        private String descriptionEn;
        private String parentId;
        private Boolean enabled;
        private Long level;
        private String relatedActivityCategories;
        private String animalSpecies;
        private String remarks;
        private String associatedActivityCategories;

        public static ActivityGroup to(final TsvActivityGroup tsv) {
            Language languageBg = Language.builder().languageId("bg").build();
            Language languageEn = Language.builder().languageId("en").build();
            ActivityGroup group = new ActivityGroup();

            group.getI18ns().add(new ActivityGroupI18n(tsv.getNameBg(), tsv.getDescriptionBg(), group, languageBg));
            group.getI18ns().add(new ActivityGroupI18n(tsv.getNameEn(), tsv.getDescriptionEn(), group, languageEn));

            group.setEnabled(tsv.enabled);
            group.setParent(ActivityGroup.builder().id(tsv.getLevel()).build());

            if (StringUtils.hasText(tsv.getRelatedActivityCategories())) {
                String[] split = tsv.getRelatedActivityCategories().split("\\|");
                for (String rac : split) {
                    group.getRelatedActivityCategories().add(Nomenclature.builder().code(rac).build());
                }
            }
            if (StringUtils.hasText(tsv.getAnimalSpecies())) {
                String[] split = tsv.getAnimalSpecies().split("\\|");
                for (String code : split) {
                    group.getAnimalSpecies().add(Nomenclature.builder().code(code).build());
                }
            }
            if (StringUtils.hasText(tsv.getRemarks())) {
                String[] split = tsv.getRemarks().split("\\|");
                for (String code : split) {
                    group.getRemarks().add(Nomenclature.builder().code(code).build());
                }
            }
            if (StringUtils.hasText(tsv.getAssociatedActivityCategories())) {
                String[] split = tsv.getAssociatedActivityCategories().split("\\|");
                for (String code : split) {
                    group.getAssociatedActivityCategories().add(Nomenclature.builder().code(code).build());
                }
            }

            return group;
        }

        public static List<ActivityGroup> to(final List<TsvActivityGroup> list) {
            return list.stream().map(TsvActivityGroup::to).collect(Collectors.toList());
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TsvClassifier {
        private String code;
        private Boolean enabled;
        private String nameBg;
        private String descBg;
        private String nameEn;
        private String descEn;
        private String parentCode;
        private String externalCode;

        public static Classifier to(final TsvClassifier tsv) {
            Classifier classifier = new Classifier();
            classifier.setCode(tsv.getCode());
            classifier.setExternalCode(tsv.getExternalCode());
            classifier.setEnabled(tsv.getEnabled());
            classifier.setParent(StringUtils.hasText(tsv.getParentCode())
                    ? Classifier.builder().code(tsv.getParentCode()).build()
                    : null);

            classifier.getI18ns().add(new ClassifierI18n(tsv.getNameBg(), tsv.getDescBg(),
                    classifier, Language.builder().languageId("bg").build()));

            classifier.getI18ns().add(new ClassifierI18n(tsv.getNameEn(), tsv.getDescEn(),
                    classifier, Language.builder().languageId("en").build()));

            return classifier;
        }

        public static List<Classifier> to(final List<TsvClassifier> list) {
            return list.stream().map(TsvClassifier::to).collect(Collectors.toList());
        }
    }

}
