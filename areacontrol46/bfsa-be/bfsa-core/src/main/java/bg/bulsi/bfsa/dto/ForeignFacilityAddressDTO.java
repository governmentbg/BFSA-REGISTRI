package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Language;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ForeignFacilityAddressDTO {
    private String countryCode;
    private String countryName;
    private String address;

    public static ForeignFacilityAddressDTO of (final Address source, final Language language) {
        ForeignFacilityAddressDTO dto = new ForeignFacilityAddressDTO();

        dto.setAddress(source.getAddress());

        if (source.getCountry() != null) {
            dto.setCountryCode(source.getCountry().getCode());
            dto.setCountryName(source.getCountry().getI18n(language).getName());
        }

        return dto;
    }

    public static List<ForeignFacilityAddressDTO> of(final List<Address> source, final Language language) {
        return source.stream().map(f -> of (f, language)).collect(Collectors.toList());
    }
}
