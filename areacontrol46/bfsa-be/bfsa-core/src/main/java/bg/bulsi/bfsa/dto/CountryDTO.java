package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.CountryI18n;
import bg.bulsi.bfsa.model.Language;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO {

    @NotNull
    private String code;
    private String isoAlpha3;
    private String continent;
    private String currencyCode;
    private String name;
    private String capital;
    private String continentName;
    private String description;
    private Boolean enabled;
    private Boolean europeanUnionMember;

    public static Country to(final CountryDTO source, final Language language) {
        Country entity = new Country();
        BeanUtils.copyProperties(source, entity);

        entity.getI18ns().add(new CountryI18n(
                source.getName(),
                source.getCapital(),
                source.getContinentName(),
                source.getDescription(),
                entity, language)
        );

        return entity;
    }

    public static Set<Country> to(final List<CountryDTO> countries, final Language language){
        return countries.stream().map(c -> to(c, language)).collect(Collectors.toSet());
    }

    public static CountryDTO of(final Country source, Language language) {

        CountryDTO dto = new CountryDTO();
        BeanUtils.copyProperties(source, dto);

        CountryI18n i18n = source.getI18n(language);
        dto.setName(i18n.getName());
        dto.setCapital(i18n.getCapital());
        dto.setContinentName(i18n.getContinentName());
        dto.setDescription(i18n.getDescription());

        return dto;
    }

    public static Set<CountryDTO> of(final Set<Country> countries, final Language language){
        return countries.stream().map(f-> of(f, language)).collect(Collectors.toSet());
    }
}
