package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.enums.EducationalDocumentType;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ApplicationS2701;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FacilityPaper;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.RecordRepository;
import bg.bulsi.bfsa.repository.RoleRepository;
import bg.bulsi.bfsa.repository.UserRepository;
import bg.bulsi.bfsa.security.RolesConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static bg.bulsi.bfsa.service.BaseServiceTest.langBG;

@Service
public class RecordBuilder {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RecordRepository repository;
    @Autowired
    private BranchBuilder branchBuilder;
    @Autowired
    private ContractorBuilder contractorBuilder;
    @Autowired
    private UserBuilder userBuilder;

    public User saveUser() {
        String username = UUID.randomUUID().toString().substring(0, 10);

        User user = User.builder()
                .username(username)
                .email(UUID.randomUUID().toString().substring(0, 10))
                .enabled(true)
                .branch(branchBuilder.saveBranch(langBG))
                .fullName(username + " " + username)
                .password(passwordEncoder.encode(username)).build();
        user.getRoles().add(roleRepository.findByName(RolesConstants.ROLE_ADMIN));

        return userRepository.save(user);
    }
    public Record saveRecord() {
        return saveRecord(null, null);
    }

    public Record saveRecord(final RecordStatus recordStatus) {
        return saveRecord(recordStatus, null);
    }

    public Record saveRecord(final RecordStatus recordStatus, final ServiceType serviceType) {
        Record record = new Record();
        record.setStatus(recordStatus != null ? recordStatus : RecordStatus.ENTERED);
        record.setApplicant(contractorBuilder.mockContractor());
        record.setEntryDate(LocalDate.now());
        record.setAssignee(userBuilder.saveUser());
        record.setIdentifier(UUID.randomUUID().toString().substring(0, 5));
        record.setEntryNumber(UUID.randomUUID().toString().substring(0, 5));
        record.setFacility(null);
        record.setServiceType(serviceType!= null ? serviceType : ServiceType.S2701);
        record.setRequestor(contractorBuilder.mockContractor());
        record.setBranch(branchBuilder.saveBranch(langBG));
        record.setPrice(new BigDecimal("25.6"));

        return repository.save(record);
    }


    public Record saveRecord(final User assignee,
                             final RecordStatus recordStatus,
                             final Facility facility,
                             final FacilityPaper facilityPaper,
                             final ContractorPaper contractorPaper,
                             final Contractor applicant,
                             final ServiceType serviceType,
                             final Branch branch) {
        Record record = new Record();
        record.setStatus(recordStatus != null ? recordStatus : RecordStatus.ENTERED);
        record.setApplicant(applicant);
        record.setEntryDate(LocalDate.now());
        record.setAssignee(assignee);
        record.setIdentifier(UUID.randomUUID().toString().substring(0, 5));
        record.setEntryNumber(UUID.randomUUID().toString().substring(0, 5));
        record.setFacility(facility);
        record.setServiceType(serviceType);
        record.setRequestor(applicant);
        record.setBranch(branch);
        record.setPrice(new BigDecimal("25.6"));

        if (ServiceType.S2701.equals(serviceType)) {
            record.setApplicationS2701(ApplicationS2701.builder()
                    .educationalDocumentType(EducationalDocumentType.TRAINING)
                    .educationalDocumentNumber(UUID.randomUUID().toString())
                    .educationalDocumentDate(LocalDate.now())
                    .educationalInstitution(UUID.randomUUID().toString())
                    .discrepancyUntilDate(LocalDate.now())
                    .certificateImage(mockImageBytes())
                    .build());
        }

        if (facilityPaper != null) {
            record.setFacilityPaper(facilityPaper);
            facilityPaper.setRecord(record);
        }
        if (contractorPaper != null) {
            record.setContractorPaper(contractorPaper);
            contractorPaper.setRecord(record);
        }

        return repository.save(record);
    }

    public Record saveRecord(final User assignee,
                             final RecordStatus recordStatus,
                             final Contractor contractor,
                             final Branch branch,
                             final ServiceType serviceType) {
        Record record = new Record();
        record.setStatus(recordStatus != null ? recordStatus : RecordStatus.ENTERED);
        record.setApplicant(contractor);
        record.setEntryDate(LocalDate.now());
        record.setAssignee(assignee);
        record.setIdentifier(UUID.randomUUID().toString().substring(0, 5));
        record.setEntryNumber(UUID.randomUUID().toString().substring(0, 5));
        record.setServiceType(serviceType);
        record.setRequestor(contractor);
        record.setBranch(branch);
        record.setPrice(new BigDecimal("25.6"));

        return repository.save(record);
    }

    public Record mockRecord(final RecordStatus recordStatus, ServiceType serviceType) {
        Record record = new Record();
        record.setStatus(recordStatus != null ? recordStatus : RecordStatus.ENTERED);
        record.setApplicant(contractorBuilder.mockContractor());
        record.setEntryDate(LocalDate.now());
        record.setAssignee(userBuilder.saveUser());
        record.setIdentifier(UUID.randomUUID().toString().substring(0, 5));
        record.setEntryNumber(UUID.randomUUID().toString().substring(0, 5));
        record.setFacility(null);
        record.setServiceType(serviceType != null ? serviceType : ServiceType.S2701);
        record.setRequestor(contractorBuilder.mockContractor());
        record.setBranch(branchBuilder.saveBranch(langBG));
        record.setPrice(new BigDecimal("25.6"));

        return record;
    }

    private byte[] mockImageBytes() {
        // Create a BufferedImage with a specified width and height
        int width = 100;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Fill the image with a color (e.g., red)
        int fillColor = 0xFFFF0000; // Red in ARGB format
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, fillColor);
            }
        }

        // Convert the BufferedImage to a byte array
        try {
            return bufferedImageToByteArray(image, "png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] bufferedImageToByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
