package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ContractorDTO;
import bg.bulsi.bfsa.dto.ContractorFacilityDTO;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.RecordDTO;
import bg.bulsi.bfsa.dto.RevisionMetadataDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityAlreadyExistException;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.exception.UserAlreadyExistAuthenticationException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorVehicle;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FacilityI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.model.VerificationToken;
import bg.bulsi.bfsa.repository.ContractorRepository;
import bg.bulsi.bfsa.repository.FacilityRepository;
import bg.bulsi.bfsa.repository.RoleRepository;
import bg.bulsi.bfsa.repository.VerificationTokenRepository;
import bg.bulsi.bfsa.util.Constants;
import bg.bulsi.bfsa.util.GeneralUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ContractorService {

    private final ContractorRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final NomenclatureService nomenclatureService;
    private final MailService mailService;
    private final FacilityRepository facilityRepository;
    private final AddressService addressService;
    private final BranchService branchService;
    private final ClassifierService classifierService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Contractor findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Contractor.class, id));
    }

    @Transactional(readOnly = true)
    public ContractorDTO findByIdDTO(final Long id, final Language language) {
        return ContractorDTO.of(findById(id), language);
    }

    @Transactional(readOnly = true)
    public Contractor findByIdOrNull(final Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Page<Contractor> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ContractorDTO> findAllDTO(final Pageable pageable, final Language language) {
        return repository.findAll(pageable).map(c -> ContractorDTO.of(c, language));
    }

    @Transactional(readOnly = true)
    public List<RecordDTO> records(final Long id) {
        return RecordDTO.of(findById(id).getRecords());
    }

    @Transactional(readOnly = true)
    public Facility findFacilityById(final Long id) {
        return facilityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Facility.class, id));
    }

    @Transactional(readOnly = true)
    public Contractor findByUsername(final String username) {
        return repository.findByUsernameIgnoreCase(
                username.toLowerCase()).orElseThrow(() -> new EntityNotFoundException(Contractor.class, username)
        );
    }

    @Transactional(readOnly = true)
    public Contractor findByUsernameOrNull(final String username) {
        return repository.findByUsernameIgnoreCase(username).orElse(null);
    }

    @Transactional(readOnly = true)
    public Contractor findByEmail(final String email) {
        return repository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(Contractor.class, email));
    }

    @Transactional(readOnly = true)
    public Contractor findByIdentifier(final String identifier) {
        return repository.findByIdentifier(identifier).orElseThrow(() -> new EntityNotFoundException(Contractor.class, identifier));
    }

    public Contractor findByIdentifierOrNull(final String identifier) {
        return repository.findByIdentifier(identifier).orElse(null);
    }

    @Transactional
    public VerificationToken registerContractor(final VerificationToken verificationToken, final Language language) throws UserAlreadyExistAuthenticationException {
        if (verificationToken.getId() != null && repository.existsById(verificationToken.getId())) {
            throw new UserAlreadyExistAuthenticationException("Contractor with id " + verificationToken.getId() + " already exist");
        } else if (repository.existsByIdentifier(verificationToken.getIdentifier()) ||
                verificationTokenRepository.existsByIdentifier(verificationToken.getIdentifier())) {
            throw new UserAlreadyExistAuthenticationException("Contractor with identifier " + verificationToken.getIdentifier() + " already exist");
        } else if (repository.existsByEmail(verificationToken.getEmail()) ||
                verificationTokenRepository.existsByEmail(verificationToken.getEmail())) {
            throw new UserAlreadyExistAuthenticationException("Contractor with email " + verificationToken.getEmail() + " already exist");
        } else if (repository.existsByUsernameIgnoreCase(verificationToken.getUsername()) ||
                verificationTokenRepository.existsByUsernameIgnoreCase(verificationToken.getUsername())) {
            throw new UserAlreadyExistAuthenticationException("Contractor with username " + verificationToken.getUsername() + " already exist");
        }

        VerificationToken savedVerificationToken = verificationTokenRepository.save(
                VerificationToken.builder()
                        .identifier(verificationToken.getIdentifier())
                        .username(verificationToken.getUsername())
                        .password(passwordEncoder.encode(verificationToken.getPassword()))
                        .email(verificationToken.getEmail())
                        .fullName(verificationToken.getFullName())
                        .verificationToken(UUID.randomUUID().toString())
                        .expDate(GeneralUtils.calculateExpiryDate(VerificationToken.EXPIRATION))
                        .build()
        );
        mailService.sendRegisterUserVerificationToken(savedVerificationToken, language);

        return savedVerificationToken;
    }

    @Transactional
    public boolean resendVerificationToken(final String existingVerificationToken, final Language language) {
        VerificationToken vToken = verificationTokenRepository.findByVerificationToken(existingVerificationToken);
        if (vToken != null) {
            vToken.updateToken(UUID.randomUUID().toString());
            mailService.sendRegisterUserVerificationToken(verificationTokenRepository.save(vToken), language);
            return true;
        }
        return false;
    }

    @Transactional
    public String validateRegisterContractorToken(final String token) {
        final VerificationToken verificationToken = verificationTokenRepository.findByVerificationToken(token);
        if (verificationToken == null) {
            return Constants.TOKEN_INVALID;
        }

        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpDate().getTime() - cal.getTime().getTime()) <= 0) {
            return Constants.TOKEN_EXPIRED;
        }
        repository.save(
                Contractor.builder()
                        .identifier(verificationToken.getIdentifier())
                        .username(verificationToken.getUsername())
                        .password(verificationToken.getPassword())
                        .fullName(verificationToken.getFullName())
                        .email(verificationToken.getEmail())
                        .enabled(true)
                        .build()
        );
        verificationTokenRepository.delete(verificationToken);
        return Constants.TOKEN_VALID;
    }

    @Transactional
    public VerificationToken forgotPassword(final String email, final Language language) {
        if (!StringUtils.hasText(email)) {
            throw new RuntimeException("Email is required");
        }
        Contractor user = findByEmail(email);
        VerificationToken token = verificationTokenRepository.findByUsernameAndEmail(user.getUsername(), user.getEmail());
        final Calendar cal = Calendar.getInstance();
        if (token == null || (token.getExpDate().getTime() - cal.getTime().getTime()) <= 0) {
            // TOKEN_EXPIRED;
            token = verificationTokenRepository.save(
                    VerificationToken.builder()
                            .identifier(user.getIdentifier())
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .verificationToken(UUID.randomUUID().toString())
                            .expDate(GeneralUtils.calculateExpiryDate(VerificationToken.EXPIRATION))
                            .build()
            );
            mailService.sendForgotPasswordVerificationToken(token, language);
        }

        return token;
    }

    @Transactional
    public String validateForgotPasswordToken(final String token, final String password) {
        final VerificationToken verificationToken = verificationTokenRepository.findByVerificationToken(token);
        if (verificationToken == null) {
            return Constants.TOKEN_INVALID;
        }

        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpDate().getTime() - cal.getTime().getTime()) <= 0) {
            return Constants.TOKEN_EXPIRED;
        }
        Contractor user = findByUsername(verificationToken.getUsername());
        user.setPassword(passwordEncoder.encode(password));

        repository.save(user);
        verificationTokenRepository.delete(verificationToken);

        return Constants.TOKEN_VALID;
    }

    @Transactional
    public Contractor create(final Contractor contractor) {
        if (StringUtils.hasText(contractor.getUsername()) && findByUsernameOrNull(contractor.getUsername()) != null) {
            throw new EntityAlreadyExistException(Contractor.class, contractor.getUsername());
        }
        contractor.setPassword(StringUtils.hasText(contractor.getPassword())
                ? passwordEncoder.encode(contractor.getPassword())
                : null);

        if (!CollectionUtils.isEmpty(contractor.getRoles())) {
            Set<Role> roles = new HashSet<>();
            contractor.getRoles().forEach(r -> roles.add(roleRepository.findByName(r.getName())));
            contractor.setRoles(roles);
        }

        if (!CollectionUtils.isEmpty(contractor.getRegisters())) {
            List<Classifier> registers = new ArrayList<>();
            contractor.getRegisters().forEach(r -> registers.add(classifierService.findByCode(r.getCode())));
            contractor.setRegisters(registers);
        }


//		if (!CollectionUtils.isEmpty(contractor.getFacilities())) {
////			contractor.getFacilities().forEach(f -> contractor.getFacilities().add(facilityService.findById(f.getId())));
//			contractor.getFacilities().forEach(f -> contractor.getFacilities().add(findFacilityById(f.getId())));
//		}

        return repository.save(contractor);
    }

    @Transactional
    public ContractorDTO update(final Long id, final Contractor contractor, final Language language) {
        if (id == null && contractor.getId() == null) {
            throw new RuntimeException("User ID is required");
        }
        Contractor current = findById(id != null && id >= 0 ? id : contractor.getId());

        current.setFullName(contractor.getFullName());
        current.setIdentifier(contractor.getIdentifier());
        current.setUsername(contractor.getUsername());
        current.setEnabled(contractor.getEnabled());

        current.getRoles().clear();
        contractor.getRoles().forEach(r -> current.getRoles().add(roleRepository.findByName(r.getName())));

        current.getContractorRelations().clear();
        if (!CollectionUtils.isEmpty(contractor.getContractorRelations())) {
            contractor.getContractorRelations().forEach(r -> {
                r.setContractor(current);
                r.setRelation(findById(r.getRelation().getId()));
                r.setRelationType(nomenclatureService.findByCode(r.getRelationType().getCode()));
                current.getContractorRelations().add(r);
            });
        }

        if (contractor.getBranch() != null) {
            current.setBranch(branchService.findById(contractor.getBranch().getId()));
        }

        current.getRegisters().clear();
        if (!CollectionUtils.isEmpty(contractor.getRegisters())) {
            contractor.getRegisters().forEach(r -> current.getRegisters().add(classifierService.findByCode(r.getCode())));
        }

        return ContractorDTO.of(repository.save(current), language);
    }

    @Transactional
    public Contractor updateRoles(final Long id, final List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            throw new RuntimeException("At least one role is required");
        }
        Contractor contractor = findById(id);
        contractor.getRoles().clear();
        roles.forEach(r -> contractor.getRoles().add(roleRepository.findByName(r)));

        return repository.save(contractor);
    }

    @Transactional(readOnly = true)
    public List<ContractorFacilityDTO> findAllContractorFacilities(final Long id, final Language language) {
        return ContractorFacilityDTO.of(findById(id).getContractorFacilities()
                .stream().filter(cf -> FacilityStatus.ACTIVE.equals(cf.getFacility().getStatus())).collect(Collectors.toList()), language);
    }

    @Transactional(readOnly = true)
    public Page<ContractorDTO> search(final String param, final Pageable pageable, final Language language) {
        return repository.search(param, pageable).map(c -> ContractorDTO.of(c, language));
    }

    @Transactional
    public Contractor setEnabled(final Long id, final Boolean enabled) {
        if (enabled == null) {
            throw new RuntimeException("Boolean param enabled is required");
        }
        Contractor contractor = findById(id);
        contractor.setEnabled(enabled);
        return repository.save(contractor);
    }

//    @Transactional
//    public ContractorDTO createFacility(final String id, final Facility facility, final Language language) {
//        if (!StringUtils.hasText(id)) {
//            throw new RuntimeException("ID field is required");
//        }
//        if (facility == null) {
//            throw new RuntimeException("Facility is required");
//        }
//        if (facility.getContractor() == null || !id.equals(facility.getContractor().getId())) {
//            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
//        }
//
//        Contractor current = findById(id);
//
//        facility.setContractor(current);
//
//        facility.setAddress(facility.getAddress() != null && StringUtils.hasText(facility.getAddress().getId())
//                ? addressService.findById(facility.getAddress().getId())
//                : null);
//
//        current.addContractorFacility(facility, null);
//
//        return ContractorDTO.of(repository.save(current) ,language);
//    }

    @Transactional
    public ContractorDTO updateFacility(final Long id, final FacilityDTO dto, Language language) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID field is required");
        }
        if (dto == null) {
            throw new RuntimeException("Facility is required");
        }
//		if (!id.equals(dto.getContractorId())) {
//			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
//		}

        Contractor current = findById(id);

        Facility facility = current.getContractorFacilities().stream()
                .filter(f -> id.equals(f.getFacility().getId()))
                .findFirst().orElseThrow(() -> new EntityNotFoundException(Facility.class, id)).getFacility();

        BeanUtils.copyProperties(dto, facility);

        facility.setAddress(dto.getAddress().getId() != null && dto.getAddress().getId() > 0
                ? addressService.findById(dto.getAddress().getId())
                : null);

        FacilityI18n i18n = facility.getI18n(language);
        i18n.setName(dto.getName());
        i18n.setDescription(dto.getDescription());

        return ContractorDTO.of(repository.save(current), language);
    }

    @Transactional(readOnly = true)
    public List<ContractorDTO> findRevisions(final Long id, final Language language) {
        List<ContractorDTO> revisions = new ArrayList<>();
        repository.findRevisions(id).get().forEach(r -> {
            ContractorDTO rev = ContractorDTO.of(r.getEntity(), language);
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
//		Collections.reverse(revisions);
        return revisions;
    }

    @Transactional(readOnly = true)
    public List<FacilityDTO> findRevisionsFacility(final Long id, final Language language) {
        List<FacilityDTO> revisions = new ArrayList<>();
        facilityRepository.findRevisions(id).get().forEach(r -> {
            FacilityDTO rev = FacilityDTO.of(r.getEntity(), language);
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
//		Collections.reverse(revisions);
        return revisions;
    }

    @Transactional(readOnly = true)
    public List<ContractorDTO> findByRegisterCode(final String registerCode, final Language language) {
        return ContractorDTO.of(repository.findByRegistersCode(registerCode), language);
    }

    @Transactional(readOnly = true)
    public Page<ContractorDTO> findByRegisterCodeAndBranchId(final String username, final String registerCode,
                                                             final Pageable pageable, final Language language) {
        User currentUser = userService.findByUsername(username);
        if (currentUser.getBranch() == null || currentUser.getBranch().getId() == null || currentUser.getBranch().getId() <= 0) {
            throw new RuntimeException("The current user has no branch");
        }
        return repository.findByRegistersCodeAndBranch_Id(registerCode, currentUser.getBranch().getId(), pageable)
                .map(c -> ContractorDTO.of(c, language));
    }

    @Transactional(readOnly = true)
    public Page<ContractorDTO> findByDivisionCodeAndBranchId(final String username, final String registerCode,
                                                             final Pageable pageable, final Language language) {
        User currentUser = userService.findByUsername(username);
        if (currentUser.getBranch() == null || currentUser.getBranch().getId() == null) {
            throw new RuntimeException("The current user has no branch");
        }

        return repository.findByDivisionCodeAndBranchId(registerCode, currentUser.getBranch().getId(), pageable)
                .map(c -> ContractorDTO.of(c, language));
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> findAllVehicles(Long id, Language language) {
        return VehicleDTO.of(findById(id).getContractorVehicles().stream()
                .map(ContractorVehicle::getVehicle).toList(), language);
    }

    @Transactional(readOnly = true)
    public List<ContractorDTO> findAllByContractorPaperServiceType(final String serviceType, final Language language) {
        return ContractorDTO.of(repository.findAllByContractorPaperServiceType(ServiceType.valueOf(serviceType)), language);
    }

    @Transactional
    public List<AddressDTO> findDistanceTradingByContractorId(final Long contractorId, final Language language) {

        List<Address> addresses = findById(contractorId).getAddresses().stream()
                .filter(a -> Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE.equals(a.getAddressType().getCode()))
                .toList();

        return AddressDTO.of(addresses, language);
    }

    @Transactional(readOnly = true)
    public Contractor findByFacilityId(final Long facilityId) {
        return repository.findByFacilityId(facilityId).orElseThrow(() ->
                new EntityNotFoundException(Contractor.class, "Not found owner of facility with id: " + facilityId));
    }
}
