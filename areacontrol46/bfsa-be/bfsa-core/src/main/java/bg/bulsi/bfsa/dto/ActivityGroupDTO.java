package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.ActivityGroup;
import bg.bulsi.bfsa.model.ActivityGroupI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

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
public class ActivityGroupDTO {

    private Long id;

    private String name;

    private String description;

    private Boolean enabled;

    private Long parentCode;

    private RevisionMetadataDTO revisionMetadata;

    @Builder.Default
    private List<KeyValueDTO> relatedActivityCategories = new ArrayList<>();

    @Builder.Default
    private List<KeyValueDTO> associatedActivityCategories = new ArrayList<>();

    @Builder.Default
    private List<KeyValueDTO> animalSpecies = new ArrayList<>();

    @Builder.Default
    private List<KeyValueDTO> remarks = new ArrayList<>();

    @Builder.Default
    private Set<ActivityGroupDTO> subActivityGroups = new HashSet<>();

    public static ActivityGroup to(final ActivityGroupDTO source, final Language language) {
        return to(source, language, null);
    }

    public static ActivityGroup to(final ActivityGroupDTO source, final Language language, ActivityGroup parent) {
        ActivityGroup entity = ActivityGroup.builder().parent(parent).build();
        BeanUtils.copyProperties(source, entity);

        entity.getI18ns().add(new ActivityGroupI18n(source.getName(), source.getDescription(), entity, language));

        if (!CollectionUtils.isEmpty(source.getRelatedActivityCategories())) {
            for (KeyValueDTO dto : source.getRelatedActivityCategories()) {
                entity.getRelatedActivityCategories().add(Nomenclature.builder().code(dto.getCode()).build());
            }
        }
        if (!CollectionUtils.isEmpty(source.getAssociatedActivityCategories())) {
            for (KeyValueDTO dto : source.getAssociatedActivityCategories()) {
                entity.getAssociatedActivityCategories().add(Nomenclature.builder().code(dto.getCode()).build());
            }
        }
        if (!CollectionUtils.isEmpty(source.getAnimalSpecies())) {
            for (KeyValueDTO dto : source.getAnimalSpecies()) {
                entity.getAnimalSpecies().add(Nomenclature.builder().code(dto.getCode()).build());
            }
        }
        if (!CollectionUtils.isEmpty(source.getRemarks())) {
            for (KeyValueDTO dto : source.getRemarks()) {
                entity.getRemarks().add(Nomenclature.builder().code(dto.getCode()).build());
            }
        }
        if (!CollectionUtils.isEmpty(source.getSubActivityGroups())) {
            entity.getSubActivityGroups().addAll(to(source.getSubActivityGroups(), language, entity));
        }

        return entity;
    }

    public static Set<ActivityGroup> to(final Set<ActivityGroupDTO> source, Language language, ActivityGroup parent) {
        return source.stream().map(d -> to(d, language, parent)).collect(Collectors.toSet());
    }

    // TODO Return main languages translations if translations form chosen languages do not exist
    public static ActivityGroupDTO of(final ActivityGroup source, final Language language) {
        ActivityGroupDTO dto = new ActivityGroupDTO();
        BeanUtils.copyProperties(source, dto);

        dto.setParentCode(source.getParent() != null && source.getParent().getId() != null && source.getParent().getId() > 0
                ? source.getParent().getId()
                : null
        );

        ActivityGroupI18n i18n = source.getI18n(language);
        if(i18n != null) {
            dto.setName(i18n.getName());
            dto.setDescription(i18n.getDescription());
        }
        if (!CollectionUtils.isEmpty(source.getRelatedActivityCategories())) {
            for (Nomenclature nom : source.getRelatedActivityCategories()) {
                dto.getRelatedActivityCategories().add(KeyValueDTO.builder().code(nom.getCode()).name(nom.getI18n(language).getName()).build());
            }
        }
        if (!CollectionUtils.isEmpty(source.getAssociatedActivityCategories())) {
            for (Nomenclature nom : source.getAssociatedActivityCategories()) {
                dto.getAssociatedActivityCategories().add(KeyValueDTO.builder().code(nom.getCode()).name(nom.getI18n(language).getName()).build());
            }
        }
        if (!CollectionUtils.isEmpty(source.getAnimalSpecies())) {
            for (Nomenclature nom : source.getAnimalSpecies()) {
                dto.getAnimalSpecies().add(KeyValueDTO.builder().code(nom.getCode()).name(nom.getI18n(language).getName()).build());
            }
        }
        if (!CollectionUtils.isEmpty(source.getRemarks())) {
            for (Nomenclature nom : source.getRemarks()) {
                dto.getRemarks().add(KeyValueDTO.builder().code(nom.getCode()).name(nom.getI18n(language).getName()).build());
            }
        }
        if (!CollectionUtils.isEmpty(source.getSubActivityGroups())) {
            dto.getSubActivityGroups().addAll(of(source.getSubActivityGroups(), language));
        }

        return dto;
    }

    public static Set<ActivityGroupDTO> of(final Set<ActivityGroup> source, final Language language) {
        return source.stream().map(e -> of(e, language)).collect(Collectors.toSet());
    }
}
