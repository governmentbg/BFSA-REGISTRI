package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.VehicleStatus;
import bg.bulsi.bfsa.model.ApplicationS2272Vehicle;
import bg.bulsi.bfsa.model.ApplicationS3180Vehicle;
import bg.bulsi.bfsa.model.ApplicationS3181Vehicle;
import bg.bulsi.bfsa.model.ApplicationS3182Vehicle;
import bg.bulsi.bfsa.model.ApplicationS7692Vehicle;
import bg.bulsi.bfsa.model.ApplicationS7693Vehicle;
import bg.bulsi.bfsa.model.ApplicationS7694Vehicle;
import bg.bulsi.bfsa.model.ApplicationS7695Vehicle;
import bg.bulsi.bfsa.model.ApplicationS7696Vehicle;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Vehicle;
import bg.bulsi.bfsa.model.VehicleI18n;
import bg.bulsi.bfsa.util.Constants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class VehicleDTO {

    private Long id;
    private String registrationPlate;
    private String entryNumber;
    private LocalDate entryDate;
    private String certificateNumber;
    private LocalDate certificateDate;
    private Double load;
    private String loadUnitCode;
    private String loadUnitName;
    private Double volume;
    private String volumeUnitCode;
    private String volumeUnitName;
    private String description;
    private String brandModel;
    private Long branchId;
    private Boolean enabled;
    private String vehicleTypeCode;
    private String vehicleTypeName;
    private String vehicleOwnershipTypeCode;
    private String vehicleOwnershipTypeName;
    private VehicleStatus status;
    private String licenseNumber;
    private List<String> registerCodes = new ArrayList<>();
    private Set<KeyValueDTO> foodTypes = new HashSet<>();
    @Builder.Default
    private List<KeyValueDTO> registers = new ArrayList<>();
    private RevisionMetadataDTO revisionMetadata;

    public static Vehicle to(final VehicleDTO source, Language language) {
        Vehicle entity = new Vehicle();
        BeanUtils.copyProperties(source, entity);

        if (source.getBranchId() != null && source.getBranchId() > 0) {
            entity.setBranch(Branch.builder().id(source.getBranchId()).build());
        }
        if (!CollectionUtils.isEmpty(source.getRegisterCodes())) {
            source.getRegisterCodes().forEach(rc -> entity.getRegisters().add(Classifier.builder().code(rc).build()));
        }
        if (StringUtils.hasText(source.getVehicleTypeCode())) {
            entity.setVehicleType(Nomenclature.builder().code(source.getVehicleTypeCode()).build());
        }
        if (!CollectionUtils.isEmpty(source.getFoodTypes())) {
            source.getFoodTypes().forEach(ft ->
                    entity.getFoodTypes().add(Classifier.builder().code(ft.getCode()).build()));
        }
        if (Constants.MEASURING_UNITS_TON_CODE.equals(source.getLoadUnitCode())) {
            entity.setLoad(source.getLoad() * 1000);
        }
        if (Constants.MEASURING_UNITS_CUBIC_METER_CODE.equals(source.getVolumeUnitCode())) {
            entity.setVolume(source.getVolume() * 1000);
        }

        entity.getI18ns().add(new VehicleI18n(null, source.getDescription(), source.getBrandModel(), entity, language));
        return entity;
    }

    public static List<Vehicle> to(final List<VehicleDTO> source, Language language) {
        return source.stream().map(d -> to(d, language)).collect(Collectors.toList());
    }
    public static VehicleDTO of(final Vehicle source, final Language language) {
        return of(source, null, language);
    }

    public static VehicleDTO of(final Vehicle source, final Nomenclature ownerType, final Language language) {
        VehicleDTO dto = new VehicleDTO();

        BeanUtils.copyProperties(source, dto);

        dto.setLoadUnitCode(Constants.MEASURING_UNITS_LOAD_CODE);
        dto.setVolumeUnitCode(Constants.MEASURING_UNITS_VOLUME_CODE);

        if (source.getBranch() != null) {
            dto.setBranchId(source.getBranch().getId());
        }
        if (!CollectionUtils.isEmpty(source.getRegisters())) {
            source.getRegisters().forEach(r -> dto.getRegisterCodes().add(r.getCode()));
        }
        if (source.getVehicleType() != null) {
            dto.setVehicleTypeCode(source.getVehicleType().getCode());
            dto.setVehicleTypeName(source.getVehicleType().getI18n(language).getName());
        }
        if (ownerType != null) {
            dto.setVehicleOwnershipTypeCode(ownerType.getCode());
            dto.setVehicleOwnershipTypeName(ownerType.getI18n(language).getName());
        }
        if (!CollectionUtils.isEmpty(source.getFoodTypes())) {
            dto.setFoodTypes(KeyValueDTO.ofClassifiers(source.getFoodTypes(), language));
        }

        if (!CollectionUtils.isEmpty(source.getRegisters())) {
            for (Classifier c : source.getRegisters()) {
                dto.getRegisters().add(KeyValueDTO.builder().code(c.getCode()).value(c.getI18n(language).getName()).build());
            }
        }

        VehicleI18n i18n = source.getI18n(language);
        if (i18n != null) {
            dto.setBrandModel(i18n.getBrandModel());
            dto.setDescription(i18n.getDescription());
        }
        return dto;
    }

    public static List<VehicleDTO> of(final List<Vehicle> sources, final Language language) {
        return sources.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }

    public static List<VehicleDTO> ofS2272Vehicle(final List<ApplicationS2272Vehicle> sources, final Language language) {
        return sources.stream().map(f -> of(f.getVehicle(), f.getOwnerType(), language)).collect(Collectors.toList());
    }

    public static List<VehicleDTO> ofS3180Vehicle(final List<ApplicationS3180Vehicle> sources, final Language language) {
        return sources.stream().map(f -> of(f.getVehicle(), f.getOwnerType(), language)).collect(Collectors.toList());
    }

    public static List<VehicleDTO> ofS3181Vehicle(final List<ApplicationS3181Vehicle> sources, final Language language) {
        return sources.stream().map(f -> of(f.getVehicle(), f.getOwnerType(), language)).collect(Collectors.toList());
    }

    public static List<VehicleDTO> ofS3182Vehicle(final List<ApplicationS3182Vehicle> sources, final Language language) {
        return sources.stream().map(f -> of(f.getVehicle(), f.getOwnerType(), language)).collect(Collectors.toList());
    }

    public static List<VehicleDTO> ofS7692Vehicle(final List<ApplicationS7692Vehicle> sources, final Language language) {
        return sources.stream().map(f -> of(f.getVehicle(), f.getOwnershipType(), language)).collect(Collectors.toList());
    }

    public static List<VehicleDTO> ofS7693Vehicle(final List<ApplicationS7693Vehicle> sources, final Language language) {
        return sources.stream().map(f -> of(f.getVehicle(), f.getOwnershipType(), language)).collect(Collectors.toList());
    }

    public static List<VehicleDTO> ofS7694Vehicle(final List<ApplicationS7694Vehicle> sources, final Language language) {
        return sources.stream().map(f -> of(f.getVehicle(), f.getOwnershipType(), language)).collect(Collectors.toList());
    }

    public static List<VehicleDTO> ofS7695Vehicle(final List<ApplicationS7695Vehicle> sources, final Language language) {
        return sources.stream().map(f -> of(f.getVehicle(), f.getOwnershipType(), language)).collect(Collectors.toList());
    }

    public static List<VehicleDTO> ofS7696Vehicle(final List<ApplicationS7696Vehicle> sources, final Language language) {
        return sources.stream().map(f -> of(f.getVehicle(), f.getOwnershipType(), language)).collect(Collectors.toList());
    }
}
