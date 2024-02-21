package bg.bulsi.bfsa.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DaeuGetTokenResponseDTO {

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("consented_on")
    private Long consentedOn;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("grant_type")
    private String grantType;
}
