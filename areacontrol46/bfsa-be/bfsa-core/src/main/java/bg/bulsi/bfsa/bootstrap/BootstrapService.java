package bg.bulsi.bfsa.bootstrap;

import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.exception.LibraryImportException;
import bg.bulsi.bfsa.model.ActivityGroup;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.MessageResource;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.Setting;
import bg.bulsi.bfsa.model.Settlement;
import bg.bulsi.bfsa.model.Tariff;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.ActivityGroupRepository;
import bg.bulsi.bfsa.repository.BranchRepository;
import bg.bulsi.bfsa.repository.ClassifierRepository;
import bg.bulsi.bfsa.repository.CountryRepository;
import bg.bulsi.bfsa.repository.LanguageRepository;
import bg.bulsi.bfsa.repository.MessageResourceRepository;
import bg.bulsi.bfsa.repository.NomenclatureRepository;
import bg.bulsi.bfsa.repository.RoleRepository;
import bg.bulsi.bfsa.repository.SettingRepository;
import bg.bulsi.bfsa.repository.SettlementRepository;
import bg.bulsi.bfsa.repository.TariffRepository;
import bg.bulsi.bfsa.repository.UserRepository;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.util.Constants;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BootstrapService {

    private final Environment environment;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageResourceRepository messageResourceRepository;
    private final SettlementRepository settlementRepository;
    private final CountryRepository countryRepository;
    private final BranchRepository branchRepository;
    private final NomenclatureRepository nomenclatureRepository;
    private final ActivityGroupRepository activityGroupRepository;
    private final ClassifierRepository classifierRepository;
    private final TariffRepository tariffRepository;
    private final SettingRepository settingRepository;

    @Value("${bfsa.bootstrap.message-resource-library:message-resource-library.tsv}")
    private String messageResourceLibrary;
    @Value("${bfsa.bootstrap.settlements-library:settlements-library.tsv}")
    private String settlementsLibrary;
    @Value("${bfsa.bootstrap.branches-library:branches-library.tsv}")
    private String branchesLibrary;
    @Value("${bfsa.bootstrap.countries-library:countries-library.tsv}")
    private String countryLibrary;
    @Value("${bfsa.bootstrap.nomenclatures-library:nomenclatures-library.tsv}")
    private String nomenclatureLibrary;
    @Value("${bfsa.bootstrap.activity-groups-library:activity-groups-library.tsv}")
    private String activityGroupLibrary;
    @Value("${bfsa.bootstrap.classifiers-library:classifiers-library.tsv}")
    private String classifiersLibrary;

    @PostConstruct
    public void postConstruct() {
        log.info("......... bootstrap start .......");
        final long startTime = System.currentTimeMillis();

        Role adminRole = createRoleIfNotFound(RolesConstants.ROLE_ADMIN);
        Role expertRole = createRoleIfNotFound(RolesConstants.ROLE_EXPERT);
        Role financeRole = createRoleIfNotFound(RolesConstants.ROLE_FINANCE);

        initializeDefaultSettings();

        loadClassifierLibrary();

        createUserIfNotFound("admin", Set.of(adminRole), null);
        createUserIfNotFound("expert_food", Set.of(expertRole), Constants.FOOD_DIRECTORATE_CODE);
        createUserIfNotFound("expert_ppp", Set.of(expertRole), Constants.PPP_DIRECTORATE_CODE);
        createUserIfNotFound("expert_phy", Set.of(expertRole), Constants.PHYTO_DIRECTORATE_CODE);
        createUserIfNotFound("expert_food_ho", Set.of(expertRole), Constants.FOOD_DIRECTORATE_CODE);
        createUserIfNotFound("expert_ppp_ho", Set.of(expertRole), Constants.PPP_DIRECTORATE_CODE);
        createUserIfNotFound("expert_phy_ho", Set.of(expertRole), Constants.PHYTO_DIRECTORATE_CODE);
        createUserIfNotFound("finance", Set.of(financeRole), null);
        createUserIfNotFound("finance_ho", Set.of(financeRole), null);

        createLanguagesIfNotExists();

        createTariffsIfNotFound();

        loadMessageResourceLibrary();
        loadNomenclatureLibrary();
        loadActivityGroupLibrary();

        List<String> activeProfiles = List.of(environment.getActiveProfiles());
        if (activeProfiles.contains("postgres") || activeProfiles.contains("h2")) {
            loadCountryLibrary();
            loadSettlementsLibrary();
            loadBranchesLibrary();

            // TODO Remove production condition
            // Add user branches for development only
            if (activeProfiles.contains("development") || activeProfiles.contains("production")) {
                List<User> users = userRepository.findAll();
                if (!CollectionUtils.isEmpty(users)) {
                    Branch ho = branchRepository.findByIdentifier("176040023").orElse(null);
                    Branch haskovo = branchRepository.findByIdentifier("176986657").orElse(null);
                    users.forEach(u -> {
                        if (u.getBranch() == null) {
                            u.setBranch(u.getUsername().contains("ho") ? ho : haskovo);
                            userRepository.save(u);
                        }
                    });
                }
            }
        }

        log.info("......... bootstrap FINISHED for {}ms .......", System.currentTimeMillis() - startTime);
    }

    private void createUserIfNotFound(final String username, final Set<Role> roles, final String directorateCode) {
        User user = userRepository.findByUsernameIgnoreCase(username.toLowerCase()).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setFullName(StringUtils.capitalize(username) + " " + StringUtils.capitalize(username));
            user.setEmail(username + "@domain.xyz");
            user.setPassword(passwordEncoder.encode(username));
            user.setRoles(roles);
            if (StringUtils.hasText(directorateCode)) {
                user.setDirectorate(classifierRepository.findByCode(directorateCode).orElseThrow(() -> new EntityNotFoundException(Classifier.class, directorateCode)));
            }
            user.setEnabled(true);
            user.setCreatedBy("anonymousUser");
            user.setLastModifiedBy("anonymousUser");
            userRepository.save(user);
        }
        log.info("createUserIfNotFound: [{}]", username);
    }

    private Role createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = roleRepository.save(Role.builder().name(name).build());
        }
        log.info("createRoleIfNotFound: [{}]", name);
        return role;
    }

    private void createTariffsIfNotFound() {
        if (tariffRepository.count() <= 0) {
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S3180)
                    .price(new BigDecimal("31.80"))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2701)
                    .price(new BigDecimal("40.0"))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S3181)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S3182)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S1590)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2697)
                    .price(new BigDecimal(150))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2699)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2272)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2702)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S1199)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S1811)
                    .price(new BigDecimal(34))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2869)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S769)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S16)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S3201)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2698)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2274)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2700)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2711)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2170)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S502)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S503)
                    .price(new BigDecimal(300))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S1366)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S3125)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S2695)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S7691)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S7692)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S7693)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S7694)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S7695)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S7696)
                    .price(new BigDecimal(-1))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S3362)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S3363)
                    .price(new BigDecimal(0))
                    .build());
            tariffRepository.save(Tariff.builder()
                    .serviceType(ServiceType.S3365)
                    .price(new BigDecimal(0))
                    .build());
        }
        log.info("createTariffsIfNotFound");
    }

    private void createLanguagesIfNotExists() {
        if (languageRepository.count() <= 0) {
            languageRepository.save(
                    Language.builder()
                            .languageId("bg")
                            .name("Български")
                            .description("Български")
                            .locale("bg_BG.UTF-8,BG,bulgarian")
                            .createdBy("anonymousUser")
                            .lastModifiedBy("anonymousUser")
                            .main(true).enabled(true).build()
            );
            languageRepository.save(
                    Language.builder()
                            .languageId("en")
                            .name("English")
                            .description("English")
                            .locale("en_US.UTF-8,en_US,en-gb,english")
                            .createdBy("anonymousUser")
                            .lastModifiedBy("anonymousUser")
                            .main(false).enabled(true).build()
            );
        }
    }

    private void loadMessageResourceLibrary() {
        if (messageResourceRepository.count() <= 0) {
            try (InputStream is = this.getClass().getResourceAsStream(messageResourceLibrary)) {
                final List<TsvReader.TsvMessageResource> tsvMessageResources = TsvReader.tsvMessageResourceList(BootstrapService.class, messageResourceLibrary);
                log.info("Message Resource Library upload using messageResourceLibrary={}", messageResourceLibrary);
                final List<ErrorObject> objectErrors = new ArrayList<>();
                // TODO
                // Add objectErrors to the game

                if (!CollectionUtils.isEmpty(tsvMessageResources)) {
                    List<MessageResource> records = messageResourceRepository.saveAll(TsvReader.TsvMessageResource.to(tsvMessageResources));
                    log.info("Message Resource Library upload finished ({} message resource(s) processed).", records.size());
                    if (!objectErrors.isEmpty()) {
                        log.warn("Some Message Resource(s) were not saved because of the following errors: ");
                        objectErrors.forEach(e -> log.warn(e.toString()));
                    }
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                throw new LibraryImportException(e);
            }
        }
    }

    private void loadCountryLibrary() {
        if (countryRepository.count() <= 0) {
            try (InputStream is = this.getClass().getResourceAsStream(countryLibrary)) {
                long startTime = System.nanoTime();
                final List<TsvReader.TsvCountry> tsvCountries = TsvReader.tsvCountryList(BootstrapService.class, countryLibrary);
                log.info("Countries Library upload using countryLibrary={}", countryLibrary);
                final List<ErrorObject> objectErrors = new ArrayList<>();
                // TODO
                // Add objectErrors to the game

                if (!CollectionUtils.isEmpty(tsvCountries)) {
                    List<Country> records = countryRepository.saveAll(TsvReader.TsvCountry.to(tsvCountries));

                    log.info("Country Library upload finished ({} country(ies) processed).", records.size(), (System.nanoTime() - startTime) / 1000000000);
                    if (!objectErrors.isEmpty()) {
                        log.warn("Some country(ies) were not saved because of the following errors: ");
                        objectErrors.forEach(e -> log.warn(e.toString()));
                    }
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                throw new LibraryImportException(e);
            }
        }
    }

    private void loadSettlementsLibrary() {
        if (settlementRepository.count() <= 0) {
            try (InputStream is = this.getClass().getResourceAsStream(settlementsLibrary)) {
                long startTime = System.nanoTime();
                final List<TsvReader.TsvSettlement> tsvtsvSettlementList =
                        TsvReader.tsvSettlementList(BootstrapService.class, settlementsLibrary);
                log.info("Settlements Library upload using settlementsLibrary={}", settlementsLibrary);
                final List<ErrorObject> objectErrors = new ArrayList<>();
                // TODO
                // Add objectErrors to the game

                if (!CollectionUtils.isEmpty(tsvtsvSettlementList)) {
                    List<Settlement> list = TsvReader.TsvSettlement.to(tsvtsvSettlementList);
                    if (!CollectionUtils.isEmpty(list)) {
                        list.forEach(
                                c -> c.setCountry(countryRepository.findByCode(c.getCountry().getCode()).orElseThrow(
                                        () -> new EntityNotFoundException(Country.class, c.getCountry().getCode())))
                        );
                    }

                    List<Settlement> records = settlementRepository.saveAll(list);
                    log.info("Settlement Library upload finished ({} settlement(s) processed). Time: {} sec.", records.size(), (System.nanoTime() - startTime) / 1000000000);
                    if (!objectErrors.isEmpty()) {
                        log.warn("Some Settlement(s) were not saved because of the following errors: ");
                        objectErrors.forEach(e -> log.warn(e.toString()));
                    }
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                throw new LibraryImportException(e);
            }
        }
    }

    private void loadBranchesLibrary() {
        if (branchRepository.count() <= 0) {
            try (InputStream is = this.getClass().getResourceAsStream(branchesLibrary)) {
                long startTime = System.nanoTime();
                final List<TsvReader.TsvBranch> tsvBranches =
                        TsvReader.tsvBranchList(BootstrapService.class, branchesLibrary);
                log.info("Branches Library upload using branchesLibrary={}", branchesLibrary);
                final List<ErrorObject> objectErrors = new ArrayList<>();
                // TODO
                // Add objectErrors to the game

                if (!CollectionUtils.isEmpty(tsvBranches)) {
                    List<Branch> list = TsvReader.TsvBranch.to(tsvBranches, settlementRepository);
                    List<Branch> records = branchRepository.saveAll(list);
                    log.info("Branches Library upload finished ({} branch(es) processed). Time: {} sec.", records.size(), (System.nanoTime() - startTime) / 1000000000);
                    if (!objectErrors.isEmpty()) {
                        log.warn("Some Branch(es) were not saved because of the following errors: ");
                        objectErrors.forEach(e -> log.warn(e.toString()));
                    }
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                throw new LibraryImportException(e);
            }
        }
    }

    private void loadNomenclatureLibrary() {
        if (nomenclatureRepository.count() <= 0) {
            try (InputStream is = this.getClass().getResourceAsStream(nomenclatureLibrary)) {
                long startTime = System.nanoTime();
                final List<TsvReader.TsvNomenclature> tsvNomenclatures =
                        TsvReader.tsvNomenclatureList(BootstrapService.class, nomenclatureLibrary);
                log.info("Nomenclature Library upload using nomenclatureLibrary = {}", nomenclatureLibrary);
                final List<ErrorObject> objectErrors = new ArrayList<>();
                // TODO
                // Add objectErrors to the game

                List<Nomenclature> records = new ArrayList<>();

                if (!CollectionUtils.isEmpty(tsvNomenclatures)) {
                    for (TsvReader.TsvNomenclature tsvNom : tsvNomenclatures) {
                        Nomenclature nom = TsvReader.TsvNomenclature.to(tsvNom);

                        if (StringUtils.hasText(tsvNom.getParentCode())) {
                            Nomenclature parent = nomenclatureRepository.findByCode(tsvNom.getParentCode()).get();
                            nom.setParent(parent);
                        }

                        records.add(nomenclatureRepository.save(nom));
                    }

                    log.info("Nomenclature Library upload finished ({} country(ies) processed).",
                            records.size(), (System.nanoTime() - startTime) / 1000000000);
                    if (!objectErrors.isEmpty()) {
                        log.warn("Some nomenclature(ies) were not saved because of the following errors: ");
                        objectErrors.forEach(e -> log.warn(e.toString()));
                    }
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                throw new LibraryImportException(e);
            }
        }
    }

    private void loadActivityGroupLibrary() {
        if (activityGroupRepository.count() <= 0) {
            try (InputStream is = this.getClass().getResourceAsStream(activityGroupLibrary)) {
                long startTime = System.nanoTime();
                final List<TsvReader.TsvActivityGroup> tsvActivityGroups = TsvReader.tsvActivityGroupList(BootstrapService.class, activityGroupLibrary);
                log.info("Group library upload using groupLibrary={}", activityGroupLibrary);
                final List<ErrorObject> objectErrors = new ArrayList<>();

                if (!CollectionUtils.isEmpty(tsvActivityGroups)) {
                    List<ActivityGroup> list = TsvReader.TsvActivityGroup.to(tsvActivityGroups);
                    if (!CollectionUtils.isEmpty(list)) {
                        Map<Long, ActivityGroup> savedMap = new HashMap<>();
                        for (ActivityGroup activityGroup : list) {
                            if (!CollectionUtils.isEmpty(activityGroup.getRelatedActivityCategories())) {
                                List<String> codes = activityGroup.getRelatedActivityCategories().stream().map(Nomenclature::getCode).toList();
                                activityGroup.getRelatedActivityCategories().clear();
                                for (String code : codes) {
                                    nomenclatureRepository.findByCode(code).ifPresent(nom -> activityGroup.getRelatedActivityCategories().add(nom));
                                }
                            }
                            if (!CollectionUtils.isEmpty(activityGroup.getAssociatedActivityCategories())) {
                                List<String> codes = activityGroup.getAssociatedActivityCategories()
                                        .stream().map(Nomenclature::getCode).toList();
                                activityGroup.getAssociatedActivityCategories().clear();
                                for (String code : codes) {
                                    nomenclatureRepository.findByCode(code).ifPresent(nom -> activityGroup.getAssociatedActivityCategories().add(nom));
                                }
                            }
                            if (!CollectionUtils.isEmpty(activityGroup.getAnimalSpecies())) {
                                List<String> codes = activityGroup.getAnimalSpecies().stream().map(Nomenclature::getCode).toList();
                                activityGroup.getAnimalSpecies().clear();
                                for (String code : codes) {
                                    nomenclatureRepository.findByCode(code).ifPresent(nom -> activityGroup.getAnimalSpecies().add(nom));
                                }
                            }
                            if (!CollectionUtils.isEmpty(activityGroup.getRemarks())) {
                                List<String> codes = activityGroup.getRemarks().stream().map(Nomenclature::getCode).toList();
                                activityGroup.getRemarks().clear();
                                for (String code : codes) {
                                    nomenclatureRepository.findByCode(code).ifPresent(remark -> activityGroup.getRemarks().add(remark));
                                }
                            }
                            if ("0".equals(activityGroup.getParent().getId())) {
                                activityGroup.setParent(null);
                                savedMap.put(0l, activityGroupRepository.save(activityGroup));
                            } else {
                                long level = activityGroup.getParent().getId();
                                activityGroup.setParent(savedMap.get(level - 1));
                                savedMap.put(level, activityGroupRepository.save(activityGroup));
                            }
                        }
                    }

                    log.info("Activity group library upload finished ({} activity group(s) processed). Time {} sec.",
                            tsvActivityGroups.size(), (System.nanoTime() - startTime) / 1000000000);

                    if (!objectErrors.isEmpty()) {
                        log.warn("Some activity group(s) were not saved because of following errors: ");
                        objectErrors.forEach(e -> log.warn(e.toString()));
                    }
                    if (is != null) {
                        is.close();
                    }
                }
            } catch (Exception e) {
                throw new LibraryImportException(e);
            }
        }
    }

    private void loadClassifierLibrary() {
        if (classifierRepository.count() <= 0) {
            try (InputStream is = this.getClass().getResourceAsStream(classifiersLibrary)) {
                long startTime = System.nanoTime();
                final List<TsvReader.TsvClassifier> tsvtsvClassifiers =
                        TsvReader.tsvClassifierList(BootstrapService.class, classifiersLibrary);
                log.info("Classifier Library upload using classifiersLibrary = {}", classifiersLibrary);
                final List<ErrorObject> objectErrors = new ArrayList<>();
                // TODO
                // Add objectErrors to the game

                List<Classifier> records = new ArrayList<>();

                if (!CollectionUtils.isEmpty(tsvtsvClassifiers)) {
                    for (TsvReader.TsvClassifier tsvClassifier : tsvtsvClassifiers) {
                        Classifier classifier = TsvReader.TsvClassifier.to(tsvClassifier);
                        if (StringUtils.hasText(tsvClassifier.getParentCode())) {
                            Classifier parent = classifierRepository.findByCode(tsvClassifier.getParentCode()).get();
                            classifier.setParent(parent);
                        }
                        records.add(classifierRepository.save(classifier));
                    }

                    log.info("Classifier Library upload finished ({} classifier(s) processed).",
                            records.size(), (System.nanoTime() - startTime) / 1000000000);
                    if (!objectErrors.isEmpty()) {
                        log.warn("Some classifier(s) were not saved because of the following errors: ");
                        objectErrors.forEach(e -> log.warn(e.toString()));
                    }
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                throw new LibraryImportException(e);
            }
        }
    }

    // Initialize default settings
    private void initializeDefaultSettings() {
        if (settingRepository.count() <= 0) {
            List<String> activeProfiles = List.of(environment.getActiveProfiles());

            Setting defaultSetting = new Setting();
            defaultSetting.setKey("global");
            defaultSetting.setValue("true");

            Setting globalAppSync = new Setting();
            globalAppSync.setKey("global.app-sync");
            globalAppSync.setValue(activeProfiles.contains("production") ? "false" : "true");
            globalAppSync.setParent(defaultSetting);

            defaultSetting.getSubSettings().add(globalAppSync);

            Setting globalMarkAsFinished = new Setting();
            globalMarkAsFinished.setKey("global.mark-as-finished");
            globalMarkAsFinished.setValue("false");
            globalMarkAsFinished.setParent(defaultSetting);

            defaultSetting.getSubSettings().add(globalMarkAsFinished);

            settingRepository.save(defaultSetting);
        }
    }
}