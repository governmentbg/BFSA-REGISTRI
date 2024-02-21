package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.EducationalDocumentType;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractorPaperDTO {

    private String regNumber;
    private LocalDate regDate;
    private LocalDate validUntilDate;
    private ServiceType serviceType;
    private ApprovalDocumentStatus status;
    private Boolean enabled;
    private String registerCode;
    private String registerName;

    // S503
    private String fullAddress;
    // S2701
    private EducationalDocumentType educationalDocumentType;
    private String educationalDocumentNumber;
    private LocalDate educationalDocumentDate;
    private LocalDate discrepancyUntilDate;
    private byte[] image;
    // S2702
    private String materialName;
    private String materialType;
    private Double materialTotalAmount;
    private String materialMeasuringUnitCode;
    private String materialMeasuringUnitName;

    public static ContractorPaper to(final ContractorPaperDTO source) {
        ContractorPaper entity = new ContractorPaper();
        BeanUtils.copyProperties(source, entity);

        return entity;
    }

    public static ContractorPaperDTO of(final ContractorPaper source, final Language language) {
        ContractorPaperDTO dto = new ContractorPaperDTO();
        BeanUtils.copyProperties(source, dto);

        if (ServiceType.S502.equals(source.getServiceType()) && source.getRecord().getApplicationS502() != null) {
            dto.setRegisterCode(Constants.CLASSIFIER_REGISTER_0002029_CODE);
            dto.setRegisterName(Constants.CLASSIFIER_REGISTER_0002029_NAME);
        }

        if (ServiceType.S503.equals(source.getServiceType()) && source.getRecord().getApplicationS503() != null) {
            dto.setRegisterCode(Constants.CLASSIFIER_REGISTER_0002033_CODE);
            dto.setRegisterName(Constants.CLASSIFIER_REGISTER_0002033_NAME);
//            dto.setFullAddress(source.getRecord().getApplicationS503().getFullAddress());
        }

        if (ServiceType.S2695.equals(source.getServiceType()) && source.getRecord().getApplicationS2695() != null) {
            dto.setRegisterCode(Constants.CLASSIFIER_REGISTER_0002036_CODE);
            dto.setRegisterName(Constants.CLASSIFIER_REGISTER_0002036_NAME);
        }

        if (ServiceType.S2697.equals(source.getServiceType()) && source.getRecord().getApplicationS2697() != null) {
            dto.setRegisterCode(Constants.CLASSIFIER_REGISTER_0002035_CODE);
            dto.setRegisterName(Constants.CLASSIFIER_REGISTER_0002035_NAME);
        }

        if (ServiceType.S2701.equals(source.getServiceType()) && source.getRecord().getApplicationS2701() != null) {
            dto.setRegisterCode(Constants.CLASSIFIER_REGISTER_0002041_CODE);
            dto.setRegisterName(Constants.CLASSIFIER_REGISTER_0002041_NAME);
//            dto.setEducationalDocumentType(source.getRecord().getApplicationS2701().getEducationalDocumentType());
//            dto.setEducationalDocumentNumber(source.getRecord().getApplicationS2701().getEducationalDocumentNumber());
//            dto.setEducationalDocumentDate(source.getRecord().getApplicationS2701().getEducationalDocumentDate());
//            dto.setDiscrepancyUntilDate(source.getRecord().getApplicationS2701().getDiscrepancyUntilDate());
        }

        if (ServiceType.S2702.equals(source.getServiceType()) && source.getRecord().getApplicationS2702() != null) {
            dto.setRegisterCode(Constants.CLASSIFIER_REGISTER_0002043_CODE);
            dto.setRegisterName(Constants.CLASSIFIER_REGISTER_0002043_NAME);
//            dto.setMaterialName(source.getRecord().getApplicationS2702().getMaterialName());
//            dto.setMaterialType(source.getRecord().getApplicationS2702().getMaterialType());
//            dto.setMaterialTotalAmount(source.getRecord().getApplicationS2702().getMaterialTotalAmount());
//            dto.setMaterialMeasuringUnitCode(source.getRecord().getApplicationS2702().getMaterialMeasuringUnitCode().getCode());
//            dto.setMaterialMeasuringUnitName(source.getRecord().getApplicationS2702().getMaterialMeasuringUnitCode().getI18n(language).getName());
        }

        if (ServiceType.S2711.equals(source.getServiceType()) && source.getRecord().getApplicationS2711() != null) {
            dto.setRegisterCode(Constants.CLASSIFIER_REGISTER_0002032_CODE);
            dto.setRegisterName(Constants.CLASSIFIER_REGISTER_0002032_NAME);
        }

        if (ServiceType.S2869.equals(source.getServiceType()) && source.getRecord().getApplicationS2869() != null) {
            dto.setRegisterCode(Constants.CLASSIFIER_REGISTER_0002023_CODE);
            dto.setRegisterName(Constants.CLASSIFIER_REGISTER_0002023_NAME);
        }

        return dto;
    }

    public static List<ContractorPaperDTO> of(final List<ContractorPaper> source, final Language language) {
        return source.stream().map(c -> of(c, language)).collect(Collectors.toList());
    }
}
