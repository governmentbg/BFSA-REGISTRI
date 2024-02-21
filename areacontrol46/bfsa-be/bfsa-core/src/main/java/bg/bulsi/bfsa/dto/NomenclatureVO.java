package bg.bulsi.bfsa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NomenclatureVO {
    private String code;
    private String symbol;
    private String name;
    private String description;

    public NomenclatureVO(final String code, final String symbol, final String name, final String description) {
        this.code = code;
        this.symbol = symbol;
        this.name = name;
        this.description = description;
    }
}
