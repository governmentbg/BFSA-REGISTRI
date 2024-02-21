package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.PlantProtectionProduct;
import bg.bulsi.bfsa.model.PlantProtectionProductI18n;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantProtectionProductDTO {

    private Long id;
	private Double quantity;
	private String name;
	private String activeSubstances;
	private String purpose;
	private String pest;
	private String crop;
	private String application;
    private RevisionMetadataDTO revisionMetadata;

    public static PlantProtectionProduct to(final PlantProtectionProductDTO source, Language language) {
    	PlantProtectionProduct entity = new PlantProtectionProduct();
        BeanUtils.copyProperties(source, entity);

        entity.getI18ns().add(new PlantProtectionProductI18n(source.getName(), source.getActiveSubstances(), 
        		source.getPurpose(), source.getPest(), source.getCrop(), source.getApplication(), entity, language));
        return entity;
    }

    public static PlantProtectionProductDTO of(final PlantProtectionProduct source, final Language language) {
        PlantProtectionProductDTO dto = new PlantProtectionProductDTO();
        BeanUtils.copyProperties(source, dto);

        PlantProtectionProductI18n i18n = source.getI18n(language);
        dto.setName(i18n.getName());
        return dto;
    }

    public static List<PlantProtectionProductDTO> of(final List<PlantProtectionProduct> sources, final Language language) {
        return sources.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }
}
