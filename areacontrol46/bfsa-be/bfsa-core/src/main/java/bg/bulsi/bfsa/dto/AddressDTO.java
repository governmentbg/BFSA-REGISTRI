package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Settlement;
import bg.bulsi.bfsa.util.Constants;
import bg.e_gov.eform.dictionary.location.Location;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class AddressDTO {

    private Long id;
    private String address;
    private String addressLat;
    private String fullAddress;
    private String postCode;
    private String countryCode;
    private String countryName;
    private String addressTypeCode;
    private String addressTypeName;
    private String settlementCode;
    private String settlementName;
    private String settlementNameLat;
    private String land;
    private String mail;
    private String url;
    private String phone;
    private Boolean enabled;

    private RevisionMetadataDTO revisionMetadata;

    public static Address to(final AddressDTO source) {
        Address entity = new Address();

        BeanUtils.copyProperties(source, entity);

        entity.setAddressType(StringUtils.hasText(source.getAddressTypeCode())
                ? Nomenclature.builder().code(source.getAddressTypeCode()).build()
                : null);

        entity.setSettlement(StringUtils.hasText(source.getSettlementCode())
                ? Settlement.builder().code(source.getSettlementCode()).build()
                : null);

        return entity;
    }

    public static List<Address> to(final List<AddressDTO> source) {
        return source.stream().map(AddressDTO::to).collect(Collectors.toList());
    }

    public static AddressDTO of(final Address source, final Language language) {
        AddressDTO dto = null;
        if (source != null) {
            dto = new AddressDTO();
            BeanUtils.copyProperties(source, dto);

            if (source.getSettlement() != null && StringUtils.hasText(source.getSettlement().getCode())) {
                dto.setSettlementCode(source.getSettlement().getCode());
                dto.setSettlementName(source.getSettlement().getName());
                dto.setSettlementNameLat(source.getSettlement().getNameLat());
            }
            if (source.getAddressType() != null && StringUtils.hasText(source.getAddressType().getCode())) {
                dto.setAddressTypeCode(source.getAddressType().getCode());
                dto.setAddressTypeName(source.getAddressType().getI18n(language).getName());
            }
            if (source.getCountry() != null && StringUtils.hasText(source.getCountry().getCode())) {
                dto.setCountryCode(source.getCountry().getCode());
                String countryName = source.getCountry().getI18n(language).getName();
                dto.setCountryName(countryName);
                if (!Constants.BG_CODE.equals(source.getCountry().getCode())) {
                    dto.setFullAddress(countryName + ", " + dto.getFullAddress());
                }
            }
        }
        return dto;
    }

    public static AddressDTO of(final Location source, final String addressTypeCode) {
        AddressDTO dto = null;
        if (source != null && source.getAddress() != null) {
            dto = new AddressDTO();

            if (source.getCountry() != null && StringUtils.hasText(source.getCountry().getCode())) {
                dto.setCountryCode(source.getCountry().getCode());
            }
            if (source.getAddress().getSettlement() != null
                    && StringUtils.hasText(source.getAddress().getSettlement().getCode())) {
                dto.setSettlementCode(source.getAddress().getSettlement().getCode());
            }
            if (StringUtils.hasText(source.getAddress().getPostCode())) {
                dto.setPostCode(source.getAddress().getPostCode());
            }
            if (StringUtils.hasText(source.getAddress().getLocationName())) {
                dto.setAddress(source.getAddress().getLocationName());
            }
            if (StringUtils.hasText(source.getAddress().getBuildingNumber())) {
                dto.setAddress(dto.getAddress() + ", " + source.getAddress().getBuildingNumber());
            }
            if (StringUtils.hasText(source.getAddress().getApartment())) {
                dto.setAddress(dto.getAddress() + ", " + source.getAddress().getApartment());
            }
            if (StringUtils.hasText(source.getAddress().getFullAddress())) {
                dto.setFullAddress(source.getAddress().getFullAddress());
            }
            dto.setAddressTypeCode(addressTypeCode);
        }

        return dto;
    }

    public static List<AddressDTO> of(final List<Address> sources, final Language language) {
        return sources.stream().map(a -> of(a, language)).collect(Collectors.toList());
    }
}
