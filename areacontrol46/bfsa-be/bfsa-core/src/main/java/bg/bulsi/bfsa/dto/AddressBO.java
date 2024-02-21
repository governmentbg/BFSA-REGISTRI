package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Settlement;
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
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class AddressBO {
    private String address;
    private String fullAddress;
    private String settlementCode;
    private String settlementName;
    private String land;
    private String plotNumber;
    private String countryCode;
    private String countryName;
    private String description;
    private String postCode;
    private String regNumber;

    public static Address to(final AddressBO source) {
        Address entity = new Address();
        BeanUtils.copyProperties(source, entity);

        entity.setSettlement(StringUtils.hasText(source.getSettlementCode())
                ? Settlement.builder().code(source.getSettlementCode()).build()
                : null);

        entity.setCountry(StringUtils.hasText(source.getCountryCode())
                ? Country.builder().code(source.getCountryCode()).build()
                : null);

        return entity;
    }


    public static List<Address> to(final List<AddressBO> source) {
        return source.stream().map(AddressBO::to).collect(Collectors.toList());
    }

    public static AddressBO of(final Address source, final Language language) {
        AddressBO bo = new AddressBO();
        BeanUtils.copyProperties(source, bo);

        if (source.getSettlement() != null && StringUtils.hasText(source.getSettlement().getCode())) {
            bo.setSettlementCode(source.getSettlement().getCode());
            bo.setSettlementName(source.getSettlement().getName());
        }
        if (source.getSettlement() != null && StringUtils.hasText(source.getCountry().getCode())) {
            bo.setCountryCode(source.getCountry().getCode());
            bo.setCountryName(source.getCountry().getI18n(language).getName());
        }
        if (StringUtils.hasText(source.getSettlementName())) {
            bo.setSettlementName(source.getSettlementName());
        }

        return bo;
    }

    public static List<AddressBO> of(final List<Address> sources, final Language language) {
        return sources.stream().map(a -> AddressBO.of(a, language)).collect(Collectors.toList());
    }

    public static AddressBO of(final AddressDTO source) {
        AddressBO entity = new AddressBO();
        BeanUtils.copyProperties(source, entity);

        return entity;
    }

}
