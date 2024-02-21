package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Address;
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
public class ContactPersonBO {
    private String identifier;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String fullAddress;
    private String settlementCode;
    private String settlementName;
    // Nomenclature ContactPersonType 04000
    private String contactPersonTypeCode;
    private String contactPersonTypeName;

    public static Address to(final ContactPersonBO source) {
        Address entity = new Address();
        BeanUtils.copyProperties(source, entity);

        entity.setSettlement(StringUtils.hasText(source.getSettlementCode())
                ? Settlement.builder().code(source.getSettlementCode()).build()
                : null);

        return entity;
    }

    public static List<Address> to(final List<ContactPersonBO> source) {
        return source.stream().map(ContactPersonBO::to).collect(Collectors.toList());
    }

    public static ContactPersonBO of(final Address source, final Language language) {
        ContactPersonBO bo = new ContactPersonBO();
        BeanUtils.copyProperties(source, bo);

        if (source.getSettlement() != null && StringUtils.hasText(source.getSettlement().getCode())) {
            bo.setSettlementCode(source.getSettlement().getCode());
            bo.setSettlementName(source.getSettlement().getName());
        }
        if (StringUtils.hasText(source.getSettlementName())) {
            bo.setSettlementName(source.getSettlementName());
        }

        return bo;
    }

    public static List<ContactPersonBO> of(final List<Address> sources, final Language language) {
        return sources.stream().map(a -> ContactPersonBO.of(a, language)).collect(Collectors.toList());
    }

}
