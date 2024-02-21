package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.NomenclatureI18n;
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
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NomenclatureDTO {

    private String code;
    private String parentCode;
    private String name;
    private Boolean enabled;
    private String description;
    private String symbol;
    private String externalCode;

    @Builder.Default
    private List<NomenclatureDTO> subNomenclatures = new ArrayList<>();

    public static Nomenclature to(final NomenclatureDTO source, final Language language) {
        Nomenclature entity = new Nomenclature();
        BeanUtils.copyProperties(source, entity);

        entity.getI18ns().add(new NomenclatureI18n(source.getName(), source.getDescription(), entity, language));

        if (!CollectionUtils.isEmpty(source.getSubNomenclatures())) {
            entity.getSubNomenclatures().addAll(to(source.getSubNomenclatures(), language));
        }

        return entity;
    }

    public static List<Nomenclature> to(final List<NomenclatureDTO> sources, final Language language) {
        return sources.stream().map(s -> to(s, language)).collect(Collectors.toList());
    }

    public static NomenclatureDTO of(final Nomenclature source, final Language language) {
        NomenclatureDTO dto = new NomenclatureDTO();
        BeanUtils.copyProperties(source, dto);

        dto.setParentCode(source.getParent() != null ? source.getParent().getCode() : null);

        NomenclatureI18n i18n = source.getI18n(language);
        if(i18n != null) {
            dto.setName(i18n.getName());
            dto.setDescription(i18n.getDescription());
        }

        if (!CollectionUtils.isEmpty(source.getSubNomenclatures())) {
            dto.getSubNomenclatures().addAll(of(source.getSubNomenclatures(), language));
        }

//        if (!CollectionUtils.isEmpty(source.getSubNomenclatures())) {
//            source.getSubNomenclatures().forEach(s -> {
//                NomenclatureDTO subDTO = new NomenclatureDTO();
//                subDTO.setCode(s.getCode());
//                subDTO.setEnabled(s.getEnabled());
//                subDTO.setParentCode(s.getParent().getCode());
//
//                NomenclatureI18n subI18n = s.getI18n(languages);
//                if(subI18n != null) {
//                    subDTO.setName(subI18n.getName());
//                    subDTO.setDescription(subI18n.getDescription());
//                }
//
//                subDTO.setSubNomenclatures(null);
//                dto.getSubNomenclatures().add(subDTO);
//            });
//        }
        return dto;
    }

    public static List<NomenclatureDTO> of(final List<Nomenclature> sources, final Language language) {
        return sources.stream().map(s -> of(s, language)).collect(Collectors.toList());
    }
}
