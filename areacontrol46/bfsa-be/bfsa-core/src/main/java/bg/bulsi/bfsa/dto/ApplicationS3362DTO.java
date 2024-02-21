package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.model.ApplicationS3362;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Record;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import generated.ServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS3362DTO extends BaseApplicationDTO {
    private OffsetDateTime registrationDate;
    private String fullName;
    private OffsetDateTime contractStartDate;
    private OffsetDateTime contractEndDate;
    private String pppName;
    private String pppManufacturerName;
    private Double pppPackageVolume;
    private String pppPackageUnitCode;
    private String pppPackageUnitName;
    private String pppPackageMaterial;
    private Double packageVolume;
    private String packageUnitCode;
    private String packageUnitName;
    private String packageMaterial;

    public static ApplicationS3362DTO of(final Record source, final Language language) {
        ApplicationS3362DTO dto = new ApplicationS3362DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        ApplicationS3362 application = source.getApplicationS3362();
        if (application != null) {
            BeanUtils.copyProperties(application, dto);
        }

        return dto;
    }

    public static List<ApplicationS3362DTO> of(final List<Record> source, final Language language) {
        return source.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static ApplicationS3362DTO ofRecord(final Record source, final Language language) {
        ApplicationS3362DTO dto = new ApplicationS3362DTO();
        dto.ofRecordBase(source, language);

        ApplicationS3362 application = source.getApplicationS3362();

        if (application != null) {
            BeanUtils.copyProperties(application, dto);

            if (application.getPppPackageUnitCode() != null) {
                Nomenclature nom = application.getPppPackageUnitCode();
                dto.setPppPackageUnitCode(nom.getCode());
                dto.setPppPackageUnitName(nom.getI18n(language).getName());
            }

            if (application.getPackageUnitCode() != null) {
                Nomenclature nom = application.getPackageUnitCode();
                dto.setPackageUnitCode(nom.getCode());
                dto.setPackageUnitName(nom.getI18n(language).getName());
            }
        }

        return dto;
    }

    public static ApplicationS3362DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS3362DTO dto = new ApplicationS3362DTO();
        dto.of(serviceRequest, docInfo);

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setRegistrationDate(sc.get("regDate") != null
                    ? OffsetDateTime.parse(sc.get("regDate").toString())
                    : null);

            dto.setFullName(sc.get("fullName") != null
                    ? sc.get("fullName").toString()
                    : null);

            dto.setContractStartDate(sc.get("contractStartDate") != null
                    ? OffsetDateTime.parse(sc.get("contractStartDate").toString())
                    : null);

            dto.setContractEndDate(sc.get("contractEndDate") != null
                    ? OffsetDateTime.parse(sc.get("contractEndDate").toString())
                    : null);

            dto.setPppName(sc.get("pppName") != null
                    ? sc.get("pppName").toString()
                    : null);

            dto.setPppManufacturerName(sc.get("pppManufacturerName") != null
                    ? sc.get("pppManufacturerName").toString()
                    : null);

            dto.setPppPackageVolume(sc.get("pppPackageVolume") != null
                    ? Double.parseDouble(sc.get("pppPackageVolume").toString())
                    : null);

            if (sc.get("pppPackageUnitCode") instanceof LinkedHashMap<?, ?> code) {
                dto.setPppPackageUnitCode(code.get("value").toString());
                dto.setPppPackageUnitName(code.get("label").toString());
            }

            dto.setPppPackageMaterial(sc.get("pppPackageMaterial") != null
                    ? sc.get("pppPackageMaterial").toString()
                    : null);

            dto.setPackageVolume(sc.get("packageVolume") != null
                    ? Double.parseDouble(sc.get("packageVolume").toString())
                    : null);

            if (sc.get("PackageUnitCode") instanceof LinkedHashMap<?, ?> code) {
                dto.setPackageUnitCode(code.get("value").toString());
                dto.setPackageUnitName(code.get("label").toString());
            }

            dto.setPackageMaterial(sc.get("PackageMaterial") != null
                    ? sc.get("PackageMaterial").toString()
                    : null);
        }

        return dto;
    }
}
