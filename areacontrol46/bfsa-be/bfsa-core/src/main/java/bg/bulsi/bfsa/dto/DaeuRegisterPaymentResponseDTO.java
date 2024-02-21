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
public class DaeuRegisterPaymentResponseDTO {

    @JsonProperty("paymentId")
    private String paymentId;

    @JsonProperty("registrationTime")
    private String registrationTime; // LocalDateTime

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
//    private Date createdDate;

    @JsonProperty("accessCode")
    private String accessCode;
}